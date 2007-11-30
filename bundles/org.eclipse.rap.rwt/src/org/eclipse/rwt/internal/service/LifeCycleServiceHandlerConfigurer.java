/*******************************************************************************
 * Copyright (c) 2002-2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.service;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.branding.BrandingManager;
import org.eclipse.rwt.internal.branding.BrandingUtil;
import org.eclipse.rwt.internal.browser.Default;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.internal.service.LifeCycleServiceHandler.ILifeCycleServiceHandlerConfigurer;
import org.eclipse.rwt.internal.service.LifeCycleServiceHandler.LifeCycleServiceHandlerSync;
import org.eclipse.rwt.internal.theme.ThemeUtil;
import org.eclipse.rwt.internal.util.HTML;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;

public final class LifeCycleServiceHandlerConfigurer
  implements ILifeCycleServiceHandlerConfigurer
{

  private static final String PACKAGE_NAME 
    = LifeCycleServiceHandlerConfigurer.class.getPackage().getName();
  private final static String FOLDER = PACKAGE_NAME.replace( '.', '/' );
  private final static String INDEX_TEMPLATE = FOLDER + "/rwt-index.html";
  
  // TODO [fappel]: think about clusters
  // cache control variables
  private static int probeCount;
  private static long lastModified = System.currentTimeMillis();

  private static final LifeCycleServiceHandlerSync syncHandler
    = new RWTLifeCycleServiceHandlerSync();
  private static String content;
  private static final List registeredBrandings = new ArrayList();
  private static final Map templateBuffer = new HashMap();
  
  ////////////////////////////////////////////////////
  // ILifeCycleServiceHandlerConfigurer implementation 
  
  public InputStream getTemplateOfStartupPage() throws IOException {
    readContent();
    StringBuffer buffer = new StringBuffer( content );
    setDummyBrowser();
    try {
      String libs = getLibraries();
      BrowserSurvey.replacePlaceholder( buffer, "${libraries}", libs );
      String appScript = getAppScript();
      BrowserSurvey.replacePlaceholder( buffer, "${appScript}", appScript );
      applyBranding( buffer );
    } finally {
      removeDummyBrowser();
    }
    String templateString = buffer.toString();
    byte[] template;
    synchronized( templateBuffer ) {
      // buffer bytes to avoid unnecessary getBytes calls
      template = ( byte[] )templateBuffer.get( templateString );
      if( template == null ) {
        template = templateString.getBytes();
        templateBuffer.put( templateString, template );
      }
    }
    return new ByteArrayInputStream( template );
  }

  public synchronized boolean isStartupPageModifiedSince() {
    boolean result;

    int currentProbeCount = TextSizeDetermination.getProbeCount();
    if( probeCount != currentProbeCount ) {
      lastModified = System.currentTimeMillis();
      probeCount = currentProbeCount;
    }
    
    HttpServletRequest request = ContextProvider.getRequest();
    HttpServletResponse response = ContextProvider.getResponse();
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

  public LifeCycleServiceHandlerSync getSynchronizationHandler() {
    return syncHandler;
  }

  ///////////////////////////////////////
  // Helping methods to load startup page 
  
  private static void readContent() throws IOException {
    if( content == null ) {
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
        content = buffer.toString();
      } finally {
        reader.close();
      }
    }
  }
  
  private static InputStream loadTemplateFile() throws IOException {
    InputStream result = null;
    IResourceManager manager = ResourceManager.getInstance();
    ClassLoader buffer = manager.getContextLoader();
    manager.setContextLoader( LifeCycleServiceHandlerConfigurer.class.getClassLoader() );
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
    String key = LifeCycleServiceHandlerConfigurer.class.getName();
    stateInfo.setAttribute( key, original );
    HtmlResponseWriter fake = new HtmlResponseWriter();
    stateInfo.setResponseWriter( fake );
  }
  
  private static void restoreWriter() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    String key = LifeCycleServiceHandlerConfigurer.class.getName();
    HtmlResponseWriter writer
      = ( HtmlResponseWriter )stateInfo.getAttribute( key );
    stateInfo.setResponseWriter( writer );
  }

  private static void setDummyBrowser() {
    String id = ServiceContext.DETECTED_SESSION_BROWSER;
    ContextProvider.getSession().setAttribute( id, new Default( true ) );
  }
  
  private static void removeDummyBrowser() {
    String id = ServiceContext.DETECTED_SESSION_BROWSER;
    ContextProvider.getSession().setAttribute( id, null );
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

  private static void applyBranding( final StringBuffer content ) 
    throws IOException 
  {
    AbstractBranding branding = findBranding();
    registerBrandingResources( branding );
    HttpServletRequest request = ContextProvider.getRequest();
    String entryPoint = request.getParameter( RequestParams.STARTUP );
    if( entryPoint == null ) {
      entryPoint = EntryPointManager.DEFAULT;
    }
    if( branding.getThemeId() != null ) {
      ThemeUtil.setCurrentThemeId( branding.getThemeId() );
    }
    BrandingUtil.replacePlaceholder( content, "${body}", branding.getBody() );
    BrandingUtil.replacePlaceholder( content, "${title}", branding.getTitle() );
    String headers = BrandingUtil.headerMarkup( branding );
    BrandingUtil.replacePlaceholder( content, "${headers}", headers );
    BrandingUtil.replacePlaceholder( content, "${startup}", entryPoint );
    String script = BrandingUtil.exitMessageScript( branding );
    BrandingUtil.replacePlaceholder( content, "${exitConfirmation}", script );
  }

  private static AbstractBranding findBranding() {
    HttpServletRequest request = ContextProvider.getRequest();
    String servletName = BrowserSurvey.getSerlvetName();
    String entryPoint = request.getParameter( RequestParams.STARTUP );
    AbstractBranding branding = BrandingManager.get( servletName, entryPoint );
    return branding;
  }
  
  private static void registerBrandingResources( final AbstractBranding branding )
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