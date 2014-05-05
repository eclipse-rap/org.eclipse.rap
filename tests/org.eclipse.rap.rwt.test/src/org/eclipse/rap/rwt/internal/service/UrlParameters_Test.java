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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.eclipse.rap.rwt.internal.protocol.Message;
import org.eclipse.rap.rwt.internal.protocol.MessageImpl;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class UrlParameters_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testMerge_InitialPostRequest() {
    Message message = new MessageImpl();
    fakeInitializeParameter( message );
    fakeQueryString( message, "key1=value1&key2=value2" );

    UrlParameters.merge( message );

    assertEquals( "value1", ContextProvider.getRequest().getParameter( "key1" ) );
    assertEquals( "value2", ContextProvider.getRequest().getParameter( "key2" ) );
  }

  @Test
  public void testMerge_NotInitialPostRequest() {
    Message message = new MessageImpl();
    fakeQueryString( message, "key1=value1&key2=value2" );

    UrlParameters.merge( message );

    assertNull( ContextProvider.getRequest().getParameter( "key1" ) );
    assertNull( ContextProvider.getRequest().getParameter( "key2" ) );
  }

  @Test
  public void testCreateParametersMap() {
    String queryString = "key1=value1&key2=value2";
    Map<String, String[]> parametersMap = UrlParameters.createParametersMap( queryString );

    assertEquals( 2, parametersMap.size() );
    assertEquals( "value1", parametersMap.get( "key1" )[ 0 ] );
    assertEquals( "value2", parametersMap.get( "key2" )[ 0 ] );
  }

  @Test
  public void testCreateParametersMap_MultipleValues() {
    String queryString = "key1=value1&key1=value2";
    Map<String, String[]> parametersMap = UrlParameters.createParametersMap( queryString );

    assertEquals( 1, parametersMap.size() );
    assertEquals( "value1", parametersMap.get( "key1" )[ 0 ] );
    assertEquals( "value2", parametersMap.get( "key1" )[ 1 ] );
  }

  @Test
  public void testCreateParametersMap_WithoutValue() {
    String queryString = "key1=";
    Map<String, String[]> parametersMap = UrlParameters.createParametersMap( queryString );

    assertEquals( "", parametersMap.get( "key1" )[ 0 ] );
  }

  @Test
  public void testCreateParametersMap_WithoutEqualAndValue() {
    String queryString = "key1";
    Map<String, String[]> parametersMap = UrlParameters.createParametersMap( queryString );

    assertEquals( "", parametersMap.get( "key1" )[ 0 ] );
  }

  @Test
  public void testCreateParametersMap_DecodeParameterName() {
    String queryString = "key%2F1=value1";
    Map<String, String[]> parametersMap = UrlParameters.createParametersMap( queryString );

    assertEquals( "value1", parametersMap.get( "key/1" )[ 0 ] );
  }

  @Test
  public void testCreateParametersMap_DecodeParameterValue() {
    String queryString = "key1=value%2F1";
    Map<String, String[]> parametersMap = UrlParameters.createParametersMap( queryString );

    assertEquals( "value/1", parametersMap.get( "key1" )[ 0 ] );
  }

  private static void fakeInitializeParameter( Message message ) {
    message.getHead().set( RWT_INITIALIZE, true );
  }

  private static void fakeQueryString( Message message, String queryString ) {
    message.getHead().set( QUERY_STRING, queryString );
  }

}
