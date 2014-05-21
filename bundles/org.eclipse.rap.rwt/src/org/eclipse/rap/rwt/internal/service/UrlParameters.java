/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.QUERY_STRING;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.RWT_INITIALIZE;
import static org.eclipse.rap.rwt.internal.util.HTTP.CHARSET_UTF_8;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.protocol.RequestMessage;


public final class UrlParameters {

  public static final String PARAM_CONNECTION_ID = "cid";

  static void merge( RequestMessage requestMessage ) {
    if( hasInitializeParameter( requestMessage ) ) {
      Map<String, String[]> parameters = getAll( requestMessage);
      if( parameters != null ) {
        HttpServletRequest request = ContextProvider.getRequest();
        WrappedRequest wrappedRequest = new WrappedRequest( request, parameters );
        ServiceContext context = ContextProvider.getContext();
        context.setRequest( wrappedRequest );
      }
    }
  }

  private static Map<String, String[]> getAll( RequestMessage requestMessage ) {
    JsonValue queryStringHeader = requestMessage.getHead().get( QUERY_STRING );
    return queryStringHeader == null ? null : createParametersMap( queryStringHeader.asString() );
  }

  static Map<String, String[]> createParametersMap( String queryString ) {
    Map<String, String[]> result = new HashMap<String, String[]>();
    String[] parameters = queryString.split( "&" );
    for( String parameter : parameters ) {
      String[] parts = parameter.split( "=" );
      try {
        String name = URLDecoder.decode( parts[ 0 ], CHARSET_UTF_8 );
        String value = parts.length == 1 ? "" : URLDecoder.decode( parts[ 1 ], CHARSET_UTF_8 );
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

  private static boolean hasInitializeParameter( RequestMessage requestMessage ) {
    return JsonValue.TRUE.equals( requestMessage.getHead().get( RWT_INITIALIZE ) );
  }

  private UrlParameters() {
    // prevent instantiation
  }

}
