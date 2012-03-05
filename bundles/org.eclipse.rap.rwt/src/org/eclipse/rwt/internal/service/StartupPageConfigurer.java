/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.RWTMessages;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.branding.BrandingUtil;
import org.eclipse.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rwt.internal.resources.ResourceRegistry;
import org.eclipse.rwt.internal.service.StartupPage.IStartupPageConfigurer;
import org.eclipse.rwt.internal.service.StartupPageTemplateHolder.Variable;
import org.eclipse.rwt.internal.textsize.MeasurementUtil;
import org.eclipse.rwt.internal.theme.Theme;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.rwt.internal.theme.ThemeUtil;
import org.eclipse.rwt.internal.util.HTTP;
import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.resources.IResource;


final class StartupPageConfigurer implements IStartupPageConfigurer {
  private static final String PACKAGE_NAME = StartupPageConfigurer.class.getPackage().getName();
  private final static String FOLDER = PACKAGE_NAME.replace( '.', '/' );
  private final static String INDEX_TEMPLATE = FOLDER + "/rwt-index.html";

  private static final String DISPLAY_TYPE = "rwt.Display";
  private static final String PROPERTY_FONTS = "fonts";
  private static final String METHOD_PROBE = "probe";
  private static final String PROPERTY_URL = "url";
  private static final String PROPERTY_ROOT_ID = "rootId";
  private static final String METHOD_INIT = "init";

  private final List<AbstractBranding> registeredBrandings;
  private final ResourceRegistry resourceRegistry;
  private final List<String> jsLibraries;
  private final List<String> themeDefinitions;
  // TODO [fappel]: think about clusters cache control variables
  private int probeCount;
  private long lastModified;
  private StartupPageTemplateHolder template;

  StartupPageConfigurer( ResourceRegistry resourceRegistry ) {
    this.resourceRegistry = resourceRegistry;
    lastModified = System.currentTimeMillis();
    registeredBrandings = new LinkedList<AbstractBranding>();
    jsLibraries = new ArrayList<String>();
    themeDefinitions = new ArrayList<String>();
  }

  ////////////////////////////////////////////////////
  // ILifeCycleServiceHandlerConfigurer implementation

  public StartupPageTemplateHolder getTemplate() throws IOException {
    readContent();
    template.reset();
    applyBranding();
    addThemeDefinitions();
    template.replace( StartupPageTemplateHolder.VAR_LIBRARIES, getJsLibraries() );
    template.replace( StartupPageTemplateHolder.VAR_APPSCRIPT, getAppScript() );
    return template;
  }

  public synchronized boolean isModifiedSince() {
    boolean result;

    int currentProbeCount = MeasurementUtil.getProbeCount();
    if( probeCount != currentProbeCount ) {
      lastModified = System.currentTimeMillis();
      probeCount = currentProbeCount;
    }

    HttpServletRequest request = ContextProvider.getRequest();
    HttpServletResponse response = ContextProvider.getResponse();
    // TODO [rh] this is a preliminary fix for a behavior that was easily
    //      reproducible in IE but also happened in FF: when restarting a
    //      web app (hit return in location bar), the browser used a cached
    //      version of the index.html *without* sending a request to ask
    //      whether the cached page can be used.
    //      fix for bug 220733: append no-store to the Cache-Control header
    response.addHeader( "Cache-Control", "max-age=0, no-cache, must-revalidate, no-store" );
    long dateHeader = request.getDateHeader( "If-Modified-Since" );
    // Because browser store the date in format with seconds as smallest unit
    // add one second to avoid rounding problems...
    if( dateHeader + 1000 < lastModified ) {
      result = true;
      response.addDateHeader( "Last-Modified", lastModified );
      // TODO [fappel]: Think about "expires"-header for proxy usage.
      // TODO [fappel]: Seems as if Safari doesn't react to last-modified.
    } else {
      result = false;
      response.setStatus( HttpServletResponse.SC_NOT_MODIFIED );
    }
    return result;
  }

  public void addJsLibrary( String location ) {
    ParamCheck.notNull( location, "resource" );
    jsLibraries.add( location );
  }

  ///////////////////////////////////////
  // Helping methods to load startup page

  private void readContent() throws IOException {
    if( template == null ) {
      InputStream stream = loadTemplateFile();
      InputStreamReader streamReader = new InputStreamReader( stream, HTTP.CHARSET_UTF_8 );
      BufferedReader reader = new BufferedReader( streamReader );
      try {
        String line = reader.readLine();
        StringBuilder buffer = new StringBuilder();
        while( line != null ) {
          buffer.append( line );
          buffer.append( '\n' );
          line = reader.readLine();
        }
        template = new StartupPageTemplateHolder( buffer.toString() );
      } finally {
        reader.close();
      }
    }
  }

  private static InputStream loadTemplateFile() throws IOException {
    ClassLoader classLoader = StartupPageConfigurer.class.getClassLoader();
    InputStream result = classLoader.getResourceAsStream( INDEX_TEMPLATE );
    if ( result == null ) {
      throw new IOException( "Failed to startup page: " + INDEX_TEMPLATE );
    }
    return result;
  }

  /////////////////////////////////////////
  // Helping methods to adjust startup page

  private static String getAppScript() {
    StringBuilder code = new StringBuilder();
    code.append( "org.eclipse.rwt.protocol.Processor.processMessage( " );
    code.append( getStartupProtocolMessage( "w1" ) );
    // TODO: The /*EOM*/ is needed for the native clients to determine where is the boot message in
    // the initial start HTML page. Remove it when custom native clients boot process is implemented
    code.append( " );/*EOM*/" );
    return code.toString();
  }

  private static String getStartupProtocolMessage( String id ) {
    ProtocolMessageWriter writer = new ProtocolMessageWriter();
    appendCreateDisplay( id, writer );
    appendStartupTextSizeProbe( id, writer );
    appendInitDisplay( id, writer );
    return writer.createMessage();
  }

  private static void appendCreateDisplay( String id, ProtocolMessageWriter writer ) {
    writer.appendCreate( id, DISPLAY_TYPE );
  }

  private static void appendStartupTextSizeProbe( String id, ProtocolMessageWriter writer ) {
    Object startupTextSizeProbeObject = getStartupTextSizeProbeObject();
    if( startupTextSizeProbeObject != null ) {
      Map<String, Object> args = new HashMap<String, Object>();
      args.put( PROPERTY_FONTS, startupTextSizeProbeObject );
      writer.appendCall( id, METHOD_PROBE, args );
    }
  }

  private static void appendInitDisplay( String id, ProtocolMessageWriter writer ) {
    Map<String, Object> args = new HashMap<String, Object>();
    args.put( PROPERTY_URL, getUrl() );
    args.put( PROPERTY_ROOT_ID, id );  // TODO [tb] : refactor client to remove this line
    writer.appendCall( id, METHOD_INIT, args );
  }

  private static Object getStartupTextSizeProbeObject() {
    return MeasurementUtil.getStartupProbeObject();
  }

  private static String getUrl() {
    HttpServletRequest request = ContextProvider.getRequest();
    String url = request.getServletPath().substring( 1 );
    return ContextProvider.getResponse().encodeURL( url );
  }

  //////////////////////////
  // Branding helper methods

  private void applyBranding() throws IOException {
    AbstractBranding branding = BrandingUtil.determineBranding();
    // TODO: [bm][rh] move into util
    registerBrandingResources( branding );
    if( branding.getThemeId() != null ) {
      ThemeUtil.setCurrentThemeId( branding.getThemeId() );
    } else {
      ThemeUtil.setCurrentThemeId( RWT.DEFAULT_THEME_ID );
    }
    replacePlaceholder( template, StartupPageTemplateHolder.VAR_BODY, branding.getBody() );
    replacePlaceholder( template, StartupPageTemplateHolder.VAR_TITLE, branding.getTitle() );
    String headers = BrandingUtil.headerMarkup( branding );
    replacePlaceholder( template, StartupPageTemplateHolder.VAR_HEADERS, headers );
    String noScriptWarning = RWTMessages.getMessage( "RWT_NoScriptWarning" );
    replacePlaceholder( template, StartupPageTemplateHolder.VAR_NO_SCRIPT_MESSAGE, noScriptWarning );
  }

  private void addThemeDefinitions() {
    themeDefinitions.clear();
    ThemeManager themeManager = RWTFactory.getThemeManager();
    Theme fallbackTheme = themeManager.getTheme( ThemeManager.FALLBACK_THEME_ID );
    themeDefinitions.add( fallbackTheme.getRegisteredLocation() );
    Theme theme = ThemeUtil.getCurrentTheme();
    themeDefinitions.add( theme.getRegisteredLocation() );
  }

  private void registerBrandingResources( AbstractBranding branding ) throws IOException {
    synchronized( registeredBrandings ) {
      if( !registeredBrandings.contains( branding ) ) {
        branding.registerResources();
        registeredBrandings.add( branding );
      }
    }
  }

  private String getJsLibraries() {
    StringBuilder buffer = new StringBuilder();
    for( String location : jsLibraries ) {
      writeScriptTag( buffer, location );
    }
    for( String location : themeDefinitions ) {
      writeScriptTag( buffer, location );
    }
    IResource[] resources = resourceRegistry.get();
    for( IResource resource : resources ) {
      if( resource.isJSLibrary() && resource.isExternal() ) {
        writeScriptTag( buffer, resource.getLocation() );
      }
    }
    String location = RWTFactory.getJSLibraryConcatenator().getLocation();
    writeScriptTag( buffer, location );
    return buffer.toString();
  }

  static void replacePlaceholder( StartupPageTemplateHolder template,
                                  Variable variable,
                                  String replacement )
  {
    String safeReplacement = replacement == null ? "" : replacement;
    template.replace( variable, safeReplacement );
  }

  private static void writeScriptTag( StringBuilder buffer, String library ) {
    if( library != null ) {
      buffer.append( "<script type=\"text/javascript\" src=\"" );
      buffer.append( library );
      buffer.append( "\" charset=\"" );
      buffer.append( HTTP.CHARSET_UTF_8 );
      buffer.append( "\"></script>\n" );
    }
  }
}
