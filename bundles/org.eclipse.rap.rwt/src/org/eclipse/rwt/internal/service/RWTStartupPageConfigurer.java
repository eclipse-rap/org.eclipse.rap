/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
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

import java.io.*;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.RWTMessages;
import org.eclipse.rwt.internal.branding.BrandingUtil;
import org.eclipse.rwt.internal.engine.ApplicationContext;
import org.eclipse.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rwt.internal.resources.*;
import org.eclipse.rwt.internal.theme.ThemeUtil;
import org.eclipse.rwt.internal.util.EncodingUtil;
import org.eclipse.rwt.internal.util.HTTP;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;
import org.eclipse.swt.internal.widgets.displaykit.DisplayLCAFacade;


public final class RWTStartupPageConfigurer
  implements StartupPage.IStartupPageConfigurer
{

  private static final String PACKAGE_NAME 
    = RWTStartupPageConfigurer.class.getPackage().getName();
  private final static String FOLDER = PACKAGE_NAME.replace( '.', '/' );
  private final static String INDEX_TEMPLATE = FOLDER + "/rwt-index.html";
  
  // TODO [fappel]: think about clusters
  // cache control variables
  private int probeCount;
  private long lastModified = System.currentTimeMillis();

  private StartupPageTemplateHolder template;
  private final List registeredBrandings = new LinkedList();
  
  ////////////////////////////////////////////////////
  // ILifeCycleServiceHandlerConfigurer implementation 
  
  public StartupPageTemplateHolder getTemplate() throws IOException {
    readContent();
    template.reset();
    DisplayLCAFacade.registerResources();
    template.replace( StartupPageTemplateHolder.VAR_LIBRARIES, getJsLibraries() );
    template.replace( StartupPageTemplateHolder.VAR_APPSCRIPT, getAppScript() );
    applyBranding();
    return template;
  }

  public synchronized boolean isModifiedSince() {
    boolean result;

    int currentProbeCount = TextSizeDetermination.getProbeCount();
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
    response.addHeader( "Cache-Control", 
                        "max-age=0, no-cache, must-revalidate, no-store" );
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


  ///////////////////////////////////////
  // Helping methods to load startup page 
  
  private void readContent() throws IOException {
    if( template == null ) {
      InputStream stream = loadTemplateFile();
      InputStreamReader streamReader = new InputStreamReader( stream, HTTP.CHARSET_UTF_8 );
      BufferedReader reader = new BufferedReader( streamReader );
      try {
        String line = reader.readLine();
        StringBuffer buffer = new StringBuffer();
        while( line != null ) {
          buffer.append( line );
          buffer.append( "\n" );
          line = reader.readLine();
        }
        template = new StartupPageTemplateHolder( buffer.toString() );
      } finally {
        reader.close();
      }
    }
  }
  
  private InputStream loadTemplateFile() throws IOException {
    InputStream result = null;
    IResourceManager manager = ResourceManager.getInstance();
    ClassLoader buffer = manager.getContextLoader();
    manager.setContextLoader( RWTStartupPageConfigurer.class.getClassLoader() );
    try {        
      result = manager.getResourceAsStream( INDEX_TEMPLATE );
      if ( result == null ) {
        String text =   "Failed to load Browser Survey HTML Page. "
                      + "Resource {0} could not be found.";
        Object[] param = new Object[]{ INDEX_TEMPLATE };
        String msg = MessageFormat.format( text, param );
        throw new IOException( msg );
      }
    } finally {
      manager.setContextLoader( buffer );          
    }
    return result;
  }

  /////////////////////////////////////////
  // Helping methods to adjust startup page

  private static String getAppScript() {
    StringBuffer code = new StringBuffer();
    code.append( getTextSizeProbeCode() );
    code.append( getApplicationJsCode( "w1" ) );
    return code.toString();
  }

  private static String getTextSizeProbeCode() {
    return TextSizeDetermination.getStartupProbeCode();
  }

  private static String getApplicationJsCode( String id ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String url = request.getServletPath().substring( 1 );
    String encodedURL = ContextProvider.getResponse().encodeURL( url );
    return "var req = org.eclipse.swt.Request.getInstance();"
       + "req.setUrl( \"" + encodedURL + "\" );"
       + "req.setUIRootId( \"" + id + "\" );"
       + "var app = new org.eclipse.swt.Application();"
       + "qx.core.Init.getInstance().setApplication( app );";
  }

  //////////////////////////
  // Branding helper methods

  private void applyBranding() throws IOException {
    AbstractBranding branding = BrandingUtil.determineBranding();
    registerBrandingResources( branding );
    HttpServletRequest request = ContextProvider.getRequest();
    // TODO: [bm][rh] move into util
    String entryPoint = request.getParameter( RequestParams.STARTUP );
    if( entryPoint == null ) {
      entryPoint = branding.getDefaultEntryPoint();
      if( entryPoint == null || "".equals( entryPoint ) ) {
        entryPoint = EntryPointManager.DEFAULT;
      }
    }
    if( branding.getThemeId() != null ) {
      ThemeUtil.setCurrentThemeId( branding.getThemeId() );
    }
    BrandingUtil.replacePlaceholder( template,
                                     StartupPageTemplateHolder.VAR_BODY,
                                     branding.getBody() );
    BrandingUtil.replacePlaceholder( template,
                                     StartupPageTemplateHolder.VAR_TITLE,
                                     branding.getTitle() );
    String headers = BrandingUtil.headerMarkup( branding );
    BrandingUtil.replacePlaceholder( template,
                                     StartupPageTemplateHolder.VAR_HEADERS,
                                     headers );
    String encodedEntryPoint = EncodingUtil.encodeHTMLEntities( entryPoint );
    BrandingUtil.replacePlaceholder( template,
                                     StartupPageTemplateHolder.VAR_STARTUP,
                                     encodedEntryPoint );
    String noScriptWarning = RWTMessages.getMessage( "RWT_NoScriptWarning" );
    BrandingUtil.replacePlaceholder( template, 
                                     StartupPageTemplateHolder.VAR_NO_SCRIPT_MESSAGE, 
                                     noScriptWarning );
  }

  private void registerBrandingResources( 
    final AbstractBranding branding )
    throws IOException
  {
    synchronized( registeredBrandings ) {
      if( !registeredBrandings.contains( branding ) ) {
        branding.registerResources();
        registeredBrandings.add( branding );
      }
    }
  }

  public static RWTStartupPageConfigurer getInstance() {
    Class singletonType = RWTStartupPageConfigurer.class;
    Object singleton = ApplicationContext.getSingleton( singletonType );
    return ( RWTStartupPageConfigurer )singleton;
  }

  private static String getJsLibraries() {
    StringBuffer buffer = new StringBuffer();
    IResource[] resources = ResourceRegistry.get();
    for( int i = 0; i < resources.length; i++ ) {
      if( resources[ i ].isExternal() && resources[ i ].isJSLibrary() ) {
        writeScriptTag( buffer, resources[ i ].getLocation() );
      }
    }
    writeScriptTag( buffer, JSLibraryServiceHandler.getRequestURL() );
    return buffer.toString();
  }

  private static void writeScriptTag( StringBuffer buffer, String library ) {
    buffer.append( "<script type=\"text/javascript\" src=\"" );
    buffer.append( library );
    buffer.append( "\" charset=\"" );
    buffer.append( HTTP.CHARSET_UTF_8 );
    buffer.append( "\"></script>" );
  }

  private RWTStartupPageConfigurer() {
    // prevent instance creation
  }
}
