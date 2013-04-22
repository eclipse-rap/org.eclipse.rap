/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.readHeadPropertyValue;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.util.HTTP;


final class UrlParameters {

  static void merge() {
    if( hasInitializeParameter() ) {
      Map<String, String[]> parameters = getAll();
      if( parameters != null ) {
        HttpServletRequest request = ContextProvider.getRequest();
        WrappedRequest wrappedRequest = new WrappedRequest( request, parameters );
        ServiceContext context = ContextProvider.getContext();
        context.setRequest( wrappedRequest );
      }
    }
  }

  private static Map<String, String[]> getAll() {
    String queryString = readHeadPropertyValue( ClientMessageConst.QUERY_STRING );
    return queryString == null ? null : createParametersMap( queryString );
  }

  static Map<String, String[]> createParametersMap( String queryString ) {
    Map<String, String[]> result = new HashMap<String, String[]>();
    String[] parameters = queryString.split( "&" );
    for( String parameter : parameters ) {
      String[] parts = parameter.split( "=" );
      try {
        String name = URLDecoder.decode( parts[ 0 ], HTTP.CHARSET_UTF_8 );
        String value = parts.length == 1 ? "" : URLDecoder.decode( parts[ 1 ], HTTP.CHARSET_UTF_8 );
        String[] oldValues = result.get( name );
        result.put( name, appendValue( oldValues, value ) );
      } catch( UnsupportedEncodingException exception ) {
        // should never happens
      }
    }
    return result;
  }

  private static String[] appendValue( String[] oldValues, String newValue ) {
    String[] result = null;
    if( oldValues == null ) {
      result = new String[] { newValue };
    } else {
      result = new String[ oldValues.length + 1 ];
      System.arraycopy( oldValues, 0, result, 0, oldValues.length );
      result[ result.length - 1 ] = newValue;
    }
    return result;
  }

  private static boolean hasInitializeParameter() {
    return "true".equals( readHeadPropertyValue( ClientMessageConst.RWT_INITIALIZE ) );
  }

  private UrlParameters() {
    // prevent instantiation
  }

}
