/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.util;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.RequestParams;


/**<p>Utility class to construct URL's.</p> */
public final class URLHelper {
  
  public static final String EQUAL = "=";
  public static final String AMPERSAND = "&";
  public static final String QUESTION_MARK = "?";

  
  private URLHelper() {
    // no instance creation
  }

  /** returns the servlets URL of the current W4Toolkit installation. */
  public static String getURLString( final boolean addEncodingDummy ) {
    HttpServletRequest request = ContextProvider.getRequest();
    StringBuffer result = new StringBuffer();
    result.append( getContextURLString() );
    result.append( request.getServletPath() );
    
    ///////////////////////////////////////////////////////////////////////
    // add a dummy parameter to create a complete session id encoding
    // if cookies are disabled
    if( addEncodingDummy ) {
      result.append( QUESTION_MARK );
      result.append( RequestParams.ENCODING_DUMMY );
      result.append( "=no" );
    }
    ///////////////////////////////////////////////////////////////////////
    return result.toString();
  }

  /** returns the url to the webapps context root of the current W4Toolkit 
   *  installation. */
  public static String getContextURLString() {
    HttpServletRequest request = ContextProvider.getRequest();
    StringBuffer result = new StringBuffer();
    result.append( getServerURL() );
    result.append( request.getContextPath() );
    return result.toString();
  }

  /** returns the servlets URL of current W4Toolkit installation with
   *  session id encoding if cookies are not accepted by the browser. */  
  public static String getEncodedURLString() {
    String result = getURLString( true );
    return ContextProvider.getResponse().encodeURL( result );
  }

  
  //////////////////
  // helping methods

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

  /**
   * <p>Appends the given <code>key</code> and <code>value</code> to the given 
   * buffer by prepending a question mark and separating key and value with an 
   * equals sign.</p>
   */
  public static void appendFirstParam( final StringBuffer buffer, 
                                       final String key, 
                                       final String value ) 
  {
    buffer.append( QUESTION_MARK );
    buffer.append( key );
    buffer.append( EQUAL );
    buffer.append( value );
  }
  
  /**
   * <p>Appends the given <code>key</code> and <code>value</code> to the given 
   * buffer by prepending an ampersand and separating key and value with an 
   * equals sign.</p>
   */
  public static void appendParam( final StringBuffer buffer, 
                                     final String key, 
                                     final String value ) 
  {
    buffer.append( AMPERSAND );
    buffer.append( key );
    buffer.append( EQUAL );
    buffer.append( value );
  }
}
