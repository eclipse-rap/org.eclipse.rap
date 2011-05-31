/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.cluster.testfixture.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


class HttpUrlBuilder {

  private final String hostName;
  private final int port;
  private final String path;
  private String sessionId;
  private final Map<String,String> parameters;
  
  HttpUrlBuilder( String hostName, int port, String path ) {
    this.hostName = hostName;
    this.port = port;
    this.path = path;
    this.sessionId = "";
    this.parameters = new HashMap<String,String>();
  }
  
  void setSessionId( String string ) {
    this.sessionId = string;
  }
  
  String getSessionId() {
    return sessionId;
  }

  void addParameter( String name, String value ) {
    parameters.put( name, value );
  }
  
  void addParameters( Map parameters ) {
    Iterator iterator = parameters.keySet().iterator();
    while( iterator.hasNext() ) {
      String name = ( String )iterator.next();
      String value = ( String )parameters.get( name );
      addParameter( name, value );
    }
  }

  URL toUrl() {
    String urlString = toUrlString();
    try {
      return new URL( urlString );
    } catch( MalformedURLException exception ) {
      throw new RuntimeException( "Failed to construct URL from string: " + urlString, exception );
    }
  }

  private String toUrlString() {
    StringBuilder buffer = new StringBuilder();
    buffer.append( "http://" );
    buffer.append( hostName );
    buffer.append( ":" );
    buffer.append( port );
    buffer.append( "/" );
    buffer.append( path );
    if( sessionId != null && sessionId.length() > 0 ) {
      buffer.append( ";jsessionid=" );
      buffer.append( sessionId );
    }
    if( parameters.size() > 0 ) {
      buffer.append( "?" );
      Iterator iterator = parameters.keySet().iterator();
      while( iterator.hasNext() ) {
        String name = ( String )iterator.next();
        String value = parameters.get( name );
        buffer.append( name );
        buffer.append( "=" );
        buffer.append( value );
        if( iterator.hasNext() ) {
          buffer.append( "&" );
        }
      }
    }
    return buffer.toString();
  }
}
