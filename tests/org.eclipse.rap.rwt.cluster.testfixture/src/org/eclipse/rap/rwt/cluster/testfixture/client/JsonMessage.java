/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.cluster.testfixture.client;

import java.util.ArrayList;
import java.util.List;


public class JsonMessage {

  private final List<String> headers;
  private final List<String> operations;

  public JsonMessage() {
    headers = new ArrayList<String>();
    operations = new ArrayList<String>();
  }

  public void setInitialize( boolean initialize ) {
    headers.add( "\"rwt_initialize\":true" );
  }

  public void setRequestCounter( int counter ) {
    headers.add( "\"requestCounter\":" + counter );
  }

  public void addOperation( String operation ) {
    operations.add( operation );
  }

  @Override
  public String toString() {
    StringBuilder json = new StringBuilder();
    json.append( "{\"head\":{" );
    appendJoined( json, headers );
    json.append( "},\"operations\":[" );
    appendJoined( json, operations );
    json.append( "]}" );
    return json.toString();
  }

  private static void appendJoined( StringBuilder builder, List<String> elements ) {
    int count = 0;
    for( String element : elements ) {
      builder.append( count++ == 0 ? "" : "," );
      builder.append( element );
    }
  }

}
