/*******************************************************************************
 * Copyright (c) 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.client;

import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.StartupParameters;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectImpl;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class StartupParametersImpl_Test {

  private static final String TYPE = "rwt.client.StartupParameters";

  @Rule
  public TestContext context = new TestContext();

  private StartupParameters service;

  @Before
  public void setUp() {
    service = RWT.getClient().getService( StartupParameters.class );
  }

  @Test
  public void testGetParametersNames() {
    JsonObject parameters = new JsonObject()
      .add( "param1", new JsonArray().add( "foo" ) )
      .add( "param2", new JsonArray().add( "bar" ) );
    fakeParameters( parameters );

    Collection<String> parameterNames = service.getParameterNames();

    assertEquals( 2, parameterNames.size() );
    assertTrue( parameterNames.contains( "param1" ) );
    assertTrue( parameterNames.contains( "param2" ) );
  }

  @Test( expected = NullPointerException.class )
  public void testGetParameter_nullName() {
    service.getParameter( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testGetParameter_emptyStringName() {
    service.getParameter( "" );
  }

  @Test
  public void testGetParameter() {
    JsonObject parameters = new JsonObject()
      .add( "param1", new JsonArray().add( "foo" ) );
    fakeParameters( parameters );

    assertEquals( "foo", service.getParameter( "param1" ) );
  }

  @Test
  public void testGetParameter_multipleValues() {
    JsonObject parameters = new JsonObject()
      .add( "param1", new JsonArray().add( "foo" ).add( "bar" ) );
    fakeParameters( parameters );

    assertEquals( "foo", service.getParameter( "param1" ) );
  }

  @Test
  public void testGetParameter_missingParameter() {
    JsonObject parameters = new JsonObject()
      .add( "param1", new JsonArray().add( "foo" ) );
    fakeParameters( parameters );

    assertNull( service.getParameter( "param2" ) );
  }

  @Test( expected = NullPointerException.class )
  public void testGetParameterValues_nullName() {
    service.getParameterValues( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testGetParameterValues_emptyStringName() {
    service.getParameterValues( "" );
  }

  @Test
  public void testGetParameterValues() {
    JsonObject parameters = new JsonObject()
      .add( "param1", new JsonArray().add( "foo" ).add( "bar" ) );
    fakeParameters( parameters );

    List<String> parameterValues = service.getParameterValues( "param1" );

    assertEquals( 2, parameterValues.size() );
    assertEquals( "foo", parameterValues.get( 0 ) );
    assertEquals( "bar", parameterValues.get( 1 ) );
  }

  @Test
  public void testGetParameterValues_missingParameter() {
    JsonObject parameters = new JsonObject()
      .add( "param1", new JsonArray().add( "foo" ) );
    fakeParameters( parameters );

    assertNull( service.getParameterValues( "param2" ) );
  }

  private void fakeParameters( JsonObject parameters ) {
    OperationHandler handler = ( ( RemoteObjectImpl )getRemoteObject( TYPE ) ).getHandler();
    handler.handleSet( new JsonObject().add( "parameters", parameters ) );
  }

}
