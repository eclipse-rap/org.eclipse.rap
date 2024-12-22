/*******************************************************************************
 * Copyright (c) 2002, 2025 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.util;

import static java.net.URLDecoder.decode;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Constant utility class which provides commonly used strings for HTTP.
 */
public final class HTTP {

  public static final String CONTENT_TYPE_HTML = "text/html";
  public static final String CONTENT_TYPE_JAVASCRIPT = "text/javascript";
  public static final String CONTENT_TYPE_JSON = "application/json"; // RFC 4627
  public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data";

  public final static String CHARSET_UTF_8 = "UTF-8";
  public static final String METHOD_GET = "GET";
  public static final String METHOD_POST = "POST";
  public static final String HEADER_ACCEPT = "Accept";

  public static String getParameter( HttpServletRequest request, String name ) {
    // Note: Using getParameter directly in Tomcat 11 will parse the request body.
    // File upload requests will failed in this case as the request body is already processed.
    if( CONTENT_TYPE_MULTIPART.equals( getMediaType( request.getContentType() ) ) ) {
      return getQueryStringParameter( request, name );
    }
    return request.getParameter( name );
  }

  public static String getQueryStringParameter( HttpServletRequest request, String name ) {
    String queryString = request.getQueryString();
    if( queryString != null ) {
      Map<String, List<String>> parameterMap = getParameterMap( queryString );
      List<String> parameter = parameterMap.get( name );
      if( parameter != null ) {
        return parameter.get( 0 );
      }
    }
    return null;
  }

  public static Map<String, List<String>> getParameterMap( String queryString ) {
    Map<String, List<String>> parametersMap = new LinkedHashMap<>();
    try {
      String[] pairs = queryString.split( "&" );
      for( String pair : pairs ) {
        int idx = pair.indexOf( "=" );
        String key = idx > 0 ? decode( pair.substring( 0, idx ), CHARSET_UTF_8 ) : pair;
        if( !parametersMap.containsKey( key ) ) {
          parametersMap.put( key, new LinkedList<>() );
        }
        String value = null;
        if( idx > 0 && pair.length() > idx + 1 ) {
          value = decode( pair.substring( idx + 1 ), CHARSET_UTF_8 );
        }
        parametersMap.get( key ).add( value );
      }
    } catch ( @SuppressWarnings( "unused" ) UnsupportedEncodingException ex ) {
      // should never happen
    }
    return parametersMap;
  }

  public static String getMediaType( String contentType ) {
    String result = null;
    if( contentType != null ) {
      int semicolon = contentType.indexOf( ';' );
      if( semicolon > -1 ) {
        result = contentType.substring( 0, semicolon );
      } else {
        result = contentType;
      }
      result = result.trim().toLowerCase( Locale.ENGLISH );
    }
    return result;
  }

  private HTTP() {
    // prevent instantiation
  }

}
