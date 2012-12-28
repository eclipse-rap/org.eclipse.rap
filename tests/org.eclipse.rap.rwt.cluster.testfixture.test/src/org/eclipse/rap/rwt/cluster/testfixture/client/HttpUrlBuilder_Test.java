/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.cluster.testfixture.client;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;


public class HttpUrlBuilder_Test {

  private HttpUrlBuilder urlBuilder;

  @Test
  public void testToStringWithoutParameters() throws MalformedURLException {
    urlBuilder = new HttpUrlBuilder( "localhost", 80, "path" );

    assertEquals( new URL( "http://localhost:80/path" ), urlBuilder.toUrl() );
  }

  @Test
  public void testToStringWithSingleParameter() throws MalformedURLException {
    urlBuilder = new HttpUrlBuilder( "localhost", 80, "path" );
    urlBuilder.addParameter( "name", "value" );

    assertEquals( new URL(  "http://localhost:80/path?name=value" ), urlBuilder.toUrl() );
  }

  @Test
  public void testToStringWithMultipleParameters() throws MalformedURLException {
    urlBuilder = new HttpUrlBuilder( "localhost", 80, "path" );
    urlBuilder.addParameter( "name1", "value1" );
    urlBuilder.addParameter( "name2", "value2" );

    URL url = urlBuilder.toUrl();

    assertEquals( new URL( "http://localhost:80/path?name1=value1&name2=value2" ), url );
  }

  @Test
  public void testToStringWithAddedParameterMap() throws MalformedURLException {
    Map<String,String> parameters = new HashMap<String,String>();
    urlBuilder = new HttpUrlBuilder( "localhost", 80, "path" );
    parameters.put( "name1", "value1" );
    parameters.put( "name2", "value2" );
    urlBuilder.addParameters( parameters );

    URL url = urlBuilder.toUrl();

    assertEquals( new URL( "http://localhost:80/path?name1=value1&name2=value2" ), url );
  }

}
