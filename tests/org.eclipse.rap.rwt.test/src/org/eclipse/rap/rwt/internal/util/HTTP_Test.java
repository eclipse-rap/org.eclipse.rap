/*******************************************************************************
 * Copyright (c) 2025 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.util;

import static org.eclipse.rap.rwt.internal.util.HTTP.getMediaType;
import static org.eclipse.rap.rwt.internal.util.HTTP.getParameter;
import static org.eclipse.rap.rwt.internal.util.HTTP.getParameterMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Map;

import org.eclipse.rap.rwt.testfixture.internal.TestRequest;
import org.junit.Test;


public class HTTP_Test {

  @Test
  public void testGetMediaType() {
    assertEquals( "application/json", getMediaType( "application/json" ) );
    assertEquals( "multipart/form-data", getMediaType( "multipart/form-data; boundary=123" ) );
  }

  @Test
  public void testGetMediaType_nullValue() {
    assertNull( "application/json", getMediaType( null ) );
  }

  @Test
  public void testGetMediaType_emptyValue() {
    assertEquals( "", getMediaType( "" ) );
  }

  @Test
  public void testGetParameterMap_singleValue() {
    Map<String, List<String>> parameterMap = getParameterMap( "a=1&b=2&c=3" );
    assertEquals( "1", parameterMap.get( "a" ).get( 0 ) );
    assertEquals( "2", parameterMap.get( "b" ).get( 0 ) );
    assertEquals( "3", parameterMap.get( "c" ).get( 0 ) );
  }

  @Test
  public void testGetParameterMap_doubleValue() {
    Map<String, List<String>> parameterMap = getParameterMap( "a=1&b=2&c=3&b&c=4" );
    assertEquals( "3", parameterMap.get( "c" ).get( 0 ) );
    assertEquals( "4", parameterMap.get( "c" ).get( 1 ) );
  }

  @Test
  public void testGetParameterMap_doubleValueWithNull() {
    Map<String, List<String>> parameterMap = getParameterMap( "a=1&b=2&c=3&b&c=4" );
    assertEquals( "2", parameterMap.get( "b" ).get( 0 ) );
    assertNull( parameterMap.get( "b" ).get( 1 ) );
  }

  @Test
  public void testGetParameterMap_missing() {
    Map<String, List<String>> parameterMap = getParameterMap( "a=1&b=2&c=3&b&c=4" );
    assertNull( parameterMap.get( "e" ) );
  }

  @Test
  public void testGetParameter_notMultipart_usesRequestGetParameter() {
    TestRequest request = new TestRequest();
    request.setContentType( HTTP.CONTENT_TYPE_JSON );
    request.setParameter( "a", "1" );
    request.setQueryString( "b=2" );

    assertEquals( "1", getParameter( request, "a" ) );
    assertNull( getParameter( request, "b" ) );
  }

  @Test
  public void testGetParameter_multipart_usesRequestQueryString() {
    TestRequest request = new TestRequest();
    request.setContentType( HTTP.CONTENT_TYPE_MULTIPART );
    request.setParameter( "a", "1" );
    request.setQueryString( "b=2" );

    assertNull( getParameter( request, "a" ) );
    assertEquals( "2", getParameter( request, "b" ) );
  }

}
