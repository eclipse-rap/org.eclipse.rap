/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.io.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.RWTMessages;
import org.eclipse.rwt.internal.branding.BrandingUtil;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.internal.theme.ThemeUtil;
import org.eclipse.rwt.internal.util.EncodingUtil;
import org.eclipse.rwt.internal.util.HTML;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;

public final class RWTStartupPageConfigurer
  implements StartupPage.IStartupPageConfigurer
{

  private static final String PACKAGE_NAME 
    = RWTStartupPageConfigurer.class.getPackage().getName();
  private final static String FOLDER = PACKAGE_NAME.replace( '.', '/' );
  private final static String INDEX_TEMPLATE = FOLDER + "/rwt-index.html";
  
  // TODO [fappel]: think about clusters
  // cache control variables
  private static int probeCount;
  private static long lastModified = System.currentTimeMillis();

  private static StartupPageTemplateHolder template;
  private static final List registeredBrandings = new ArrayList();
  
  ////////////////////////////////////////////////////
  // ILifeCycleServiceHandlerConfigurer implementation 
  
  public StartupPageTemplateHolder getTemplate() throws IOException {
    readContent();
    template.reset();
    template.replace( StartupPageTemplateHolder.VAR_LIBRARIES, getLibraries() );
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
  
  private static void readContent() throws IOException {
    if( template == null ) {
      InputStream stream = loadTemplateFile();
      InputStreamReader isr 
        = new InputStreamReader( stream, HTML.CHARSET_NAME_ISO_8859_1 );
      BufferedReader reader = new BufferedReader( isr );
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
  
  private static InputStream loadTemplateFile() throws IOException {
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
  
  private static String getAppScript() throws IOException {
    fakeWriter();
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter writer = stateInfo.getResponseWriter();
    writer.startElement( HTML.SCRIPT, null );
    writer.writeText( "safd", null );
    writer.clearBody();
    try {
      // TODO: [fappel] this works only as long as only one display per
      //                session is supported...
      DisplayUtil.writeAppScript( "w1" );
      return getContent( writer );
    } finally {
      restoreWriter();
    }
  }
  
  private static void fakeWriter() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter original = stateInfo.getResponseWriter();
    String key = RWTStartupPageConfigurer.class.getName();
    stateInfo.setAttribute( key, original );
    HtmlResponseWriter fake = new HtmlResponseWriter();
    stateInfo.setResponseWriter( fake );
  }
  
  private static void restoreWriter() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    String key = RWTStartupPageConfigurer.class.getName();
    HtmlResponseWriter writer
      = ( HtmlResponseWriter )stateInfo.getAttribute( key );
    stateInfo.setResponseWriter( writer );
  }

  private static String getLibraries() throws IOException {
    fakeWriter();
    try {
      DisplayUtil.writeLibraries();
      return getContent( ContextProvider.getStateInfo().getResponseWriter() );
    } finally {
      restoreWriter();
    }
  }

  private static String getContent( final HtmlResponseWriter writer ) {
    StringBuffer msg = new StringBuffer();
    for( int i = 0; i < writer.getBodySize(); i ++ ) {
      msg.append( writer.getBodyToken( i ) );
    }
    return msg.toString();
  }

  //////////////////////////
  // Branding helper methods

  private static void applyBranding() throws IOException {
    AbstractBranding branding = BrandingUtil.findBranding();
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
    String script = BrandingUtil.exitMessageScript( branding );
    BrandingUtil.replacePlaceholder( template,
                                     StartupPageTemplateHolder.VAR_EXIT_CONFIRMATION,
                                     script );
    String noScriptWarning = RWTMessages.getMessage( "RWT_NoScriptWarning" );
    BrandingUtil.replacePlaceholder( template, 
                                     StartupPageTemplateHolder.VAR_NO_SCRIPT_MESSAGE, 
                                     noScriptWarning );
  }

  private static void registerBrandingResources( 
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
}