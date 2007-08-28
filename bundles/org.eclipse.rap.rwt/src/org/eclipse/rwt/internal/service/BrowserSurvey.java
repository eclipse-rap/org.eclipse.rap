/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.lifecycle.HtmlResponseWriter;
import org.eclipse.rwt.internal.util.*;


/** 
 * <p>A helping class that loads a special html page in order to
 * determine which browser has originated the request.</p>
 */
public final class BrowserSurvey {

  /** 
   * <p>Writes a special html page into the passed HtmlResponseWriter, in order to
   *  determine which browser has originated the request.</p> 
   */
  static void sendBrowserSurvey() throws ServletException {
    if( LifeCycleServiceHandler.configurer.isStartupPageModifiedSince() ) {
      HttpServletRequest request = ContextProvider.getRequest();
      // first check whether a survey has already been sent
      String survey = request.getParameter( RequestParams.SURVEY );
      if( survey != null && survey.equals( "true" ) ) {
        String msg = "Initialization fault. Browser survey failed.";
        throw new ServletException( msg );
      }
      // send out the survey
      try {
        if( isAjaxRequest() ) {
          renderAjax();
        } else {
          renderScript();
        }
      } catch( IOException e ) {
        String txt = "Failed to load Browser Survey HTML Page (Reason: {0})";
        String msg = MessageFormat.format( txt, new Object[] { e.getMessage() } );
        throw new ServletException( msg, e );
      }
    }
  }

  private static void renderScript() throws IOException {
    ContextProvider.getResponse().setContentType( HTML.CONTENT_TEXT_HTML );
    StringBuffer buffer = new StringBuffer();
    load( buffer );
    LifeCycleServiceHandler.configurer.registerResources();
    String servletName = ContextProvider.getRequest().getServletPath();
    if( servletName.startsWith( "/" ) ) {
      servletName = servletName.substring( 1 );
    }
    replacePlaceholder( buffer, "${servlet}", servletName );
    replacePlaceholder( buffer, "${fallbackUrl}", createURL() );
    replacePlaceholder( buffer, "${adminOrStartup}", adminOrStartup() );
    replacePlaceholder( buffer, "${entrypoint}", getEntryPoint() );
    getResponseWriter().append( buffer.toString() );
  }

  private static void renderAjax() {
    ContextProvider.getResponse().setContentType( HTML.CONTENT_TEXT_XML );
    HtmlResponseWriter writer = getResponseWriter();
    writer.append( HTMLUtil.createXmlProcessingInstruction() );
    writer.append( HTML.START_AJAX_RESPONSE );
    StringBuffer code = new StringBuffer();
    code.append( "alert('" );
    code.append( "Your request was sent to an expired session.\\n" ); 
    code.append( "A new session will be initiated." );
    code.append( "');");
    code.append( "window.open( '");
    code.append( URLHelper.getURLString( false ) );
    code.append( "', '_self' );");
    writer.append( HTMLUtil.createJavaScriptInline( code.toString() ) );
    writer.append( HTML.END_AJAX_RESPONSE );
  }

  public static void replacePlaceholder( final StringBuffer buffer, 
                                         final String placeHolder, 
                                         final String replacement ) 
  {
    int index;
    index = buffer.indexOf( placeHolder );
    while( index != -1 ) {
      buffer.replace( index, index + placeHolder.length(), replacement );
      index = buffer.indexOf( placeHolder );
    }
  }

  // helping methods
  //////////////////
  
  // TODO [rh] replace this by ResourceUtil#read - encoding is misssing here
  static void load( final StringBuffer buffer ) throws IOException {
    InputStream inputStream 
      = LifeCycleServiceHandler.configurer.getTemplateOfStartupPage();
    try {
      byte[] bytes = new byte[ 512 ];
      int bytesRead = inputStream.read( bytes );
      while( bytesRead != -1 ) {
        buffer.append( new String( bytes, 0, bytesRead ) );
        bytesRead = inputStream.read( bytes );
      }
    } finally {
      inputStream.close();
    }
  }

  static String getResourceName() {
    String name = BrowserSurvey.class.getPackage().getName();
    return name.replace( '.', '/' ) + "/index.html";
  }
  
  private static String createURL() {
    StringBuffer url = new StringBuffer();
    url.append( URLHelper.getURLString( false ) );
    URLHelper.appendFirstParam( url, adminOrStartup(), getEntryPoint() );
    URLHelper.appendParam( url, RequestParams.SCRIPT, "false" );
    URLHelper.appendParam( url, RequestParams.AJAX_ENABLED, "false" );
    return ContextProvider.getResponse().encodeURL( url.toString() );
  }

  private static String getEntryPoint() {
    HttpServletRequest request = ContextProvider.getRequest();
    String result = request.getParameter( RequestParams.STARTUP );
    if( result == null ) {
      result = "default";
    }
    return result;
  }

  private static String adminOrStartup() {
    HttpServletRequest request = ContextProvider.getRequest();
    String parameter = request.getParameter( RequestParams.ADMIN );
    boolean admin = "true".equals( parameter );
    return admin ? RequestParams.ADMIN : RequestParams.STARTUP;
  }

  private static HtmlResponseWriter getResponseWriter() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    return stateInfo.getResponseWriter();
  }
  
  private static boolean isAjaxRequest() {
    HttpServletRequest request = ContextProvider.getRequest();
    String paramValue = request.getParameter( RequestParams.IS_AJAX_REQUEST );
    return "true".equals( paramValue );
  }
}