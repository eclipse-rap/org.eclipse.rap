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

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLConnection;


public class Response {
  private static final String HTML_PROLOGUE = "<!DOCTYPE HTML";
  private static final String JAVASCRIPT_PROLOGUE
    = "var req = org.eclipse.swt.Request.getInstance();";
  
  private final int responseCode;
  private final String content;

  Response( HttpURLConnection connection ) throws IOException {
    this.responseCode = connection.getResponseCode();
    this.content = readResponseContent( connection );
  }
  
  public int getResponseCode() {
    return responseCode;
  }
  
  public String getContent() {
    return content;
  }

  public boolean isValidJavascript() {
    return responseCode == 200 && content.startsWith( JAVASCRIPT_PROLOGUE );
  }

  public boolean isValidStartupPage() {
    return responseCode == 200 && content.startsWith( HTML_PROLOGUE );
  }

  private static String readResponseContent( URLConnection connection ) throws IOException {
    String content;
    InputStream inputStream = connection.getInputStream();
    try {
      content = readResponseContent( inputStream );
    } finally {
      inputStream.close();
    }
    return content;
  }

  private static String readResponseContent( InputStream inputStream ) throws IOException {
    Reader reader = new InputStreamReader( inputStream, "utf-8" );
    StringBuffer buffer = new StringBuffer();
    int read = reader.read();
    while( read != -1 ) {
      buffer.append( ( char )read );
      read = reader.read();
    }
    return buffer.toString();
  }
}
