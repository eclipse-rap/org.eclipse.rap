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
 ******************************************************************************/
package org.eclipse.rwt.internal.util;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.service.ContextProvider;


public final class URLHelper {
  
  public static final String EQUAL = "=";
  public static final String AMPERSAND = "&";
  public static final String QUESTION_MARK = "?";

  private URLHelper() {
    // no instance creation
  }

  public static String getServletName() {
    String result = ContextProvider.getRequest().getServletPath();
    if( result.startsWith( "/" ) ) {
      result = result.substring( 1 );
    }
    return result;
  }

  /**
   * Returns the servlet's URL of the current RWT installation.
   */
  public static String getURLString() {
    HttpServletRequest request = ContextProvider.getRequest();
    StringBuffer result = new StringBuffer();
    result.append( getContextURLString() );
    result.append( request.getServletPath() );
    return result.toString();
  }

  /**
   * Returns the URL to the webapp's context root of the current RWT installation.
   */
  public static String getContextURLString() {
    HttpServletRequest request = ContextProvider.getRequest();
    StringBuffer result = new StringBuffer();
    result.append( getServerURL() );
    result.append( request.getContextPath() );
    return result.toString();
  }

  //////////////////
  // helping methods

  /**
   * Appends the given <code>key</code> and <code>value</code> to the given buffer by prepending a
   * question mark and separating key and value with an equals sign.
   */
  public static void appendFirstParam( StringBuffer buffer, String key, String value ) {
    buffer.append( QUESTION_MARK );
    buffer.append( key );
    buffer.append( EQUAL );
    buffer.append( value );
  }

  /**
   * Appends the given <code>key</code> and <code>value</code> to the given buffer by prepending an
   * ampersand and separating key and value with an equals sign.
   */
  public static void appendParam( StringBuffer buffer, String key, String value ) {
    buffer.append( AMPERSAND );
    buffer.append( key );
    buffer.append( EQUAL );
    buffer.append( value );
  }

  private static String getServerURL() {
    // TODO: [fappel] remove the creation of absolute addresses with
    //                relative ones, this should make this method obsolete
    HttpServletRequest request = ContextProvider.getRequest();

    ///////////////////////////////////////////////////////////////////////
    // use the following workaround to keep servlet 2.2 spec. compatibility
    String port = URLHelper.createPortPattern( request );
    StringBuffer result = new StringBuffer();
    String serverName = request.getServerName();
    result.append( request.getScheme() );
    result.append( "://" );
    result.append( serverName );
    result.append( port );
    ///////////////////////////////////////////////////////////////////////
    return result.toString();
  }
  
  private static String createPortPattern( final HttpServletRequest request ) {
    String result = String.valueOf( request.getServerPort() );
    if( result != null && !result.equals( "" ) ) {
      StringBuffer buffer = new StringBuffer();
      buffer.append( ":" );
      buffer.append( result );
      result = buffer.toString(); 
    } else {
      result = "";
    }
    return result;
  }
}
