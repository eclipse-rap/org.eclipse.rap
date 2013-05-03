/*******************************************************************************
* Copyright (c) 2011, 2013 EclipseSource and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.rap.rwt.internal.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.DestroyOperation;
import org.eclipse.rap.rwt.testfixture.Message.ListenOperation;
import org.eclipse.rap.rwt.testfixture.Message.SetOperation;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ClientObject_Test {

  private Shell shell;
  private String shellId;
  private IClientObject clientObject;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
    Display display = new Display();
    shell = new Shell( display );
    shellId = WidgetUtil.getId( shell );
    clientObject = ClientObjectFactory.getClientObject( shell );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCreate() {
    clientObject.create( "rwt.widgets.Shell" );

    CreateOperation operation = ( CreateOperation )getMessage().getOperation( 0 );
    assertEquals( shellId, operation.getTarget() );
    assertEquals( "rwt.widgets.Shell", operation.getType() );
  }

  @Test
  public void testCreateIncludesSetProperties() {
    clientObject.create( "rwt.widgets.Shell" );
    clientObject.set( "foo", 23 );

    Message message = getMessage();
    assertEquals( 1, message.getOperationCount() );
    assertTrue( message.getOperation( 0 ) instanceof CreateOperation );
    assertEquals( 23, message.getOperation( 0 ).getProperty( "foo" ).asInt() );
  }

  @Test
  public void testSet() {
    clientObject.set( "int", 2 );
    clientObject.set( "double", 3.5 );
    clientObject.set( "boolean", true );
    clientObject.set( "string", "foo" );
    clientObject.set( "array", new JsonArray().add( 23 ).add( 42 ) );
    clientObject.set( "object", new JsonObject().add( "foo", 23 ) );

    SetOperation operation = ( SetOperation )getMessage().getOperation( 0 );
    assertEquals( shellId, operation.getTarget() );
    assertEquals( JsonValue.valueOf( 2 ), operation.getProperty( "int" ) );
    assertEquals( JsonValue.valueOf( 3.5 ), operation.getProperty( "double" ) );
    assertEquals( JsonValue.TRUE, operation.getProperty( "boolean" ));
    assertEquals( JsonValue.valueOf( "foo" ), operation.getProperty( "string" ) );
    assertEquals( new JsonArray().add( 23 ).add( 42 ), operation.getProperty( "array" ) );
    assertEquals( new JsonObject().add( "foo", 23 ), operation.getProperty( "object" ) );
  }

  @Test
  public void testDestroy() {
    clientObject.destroy();

    DestroyOperation operation = ( DestroyOperation )getMessage().getOperation( 0 );
    assertEquals( shellId, operation.getTarget() );
  }

  @Test
  public void testAddListener() {
    clientObject.listen( "selection", true );
    clientObject.listen( "fake", true );

    ListenOperation operation = ( ListenOperation )getMessage().getOperation( 0 );
    assertEquals( shellId, operation.getTarget() );
    assertTrue( operation.listensTo( "selection" ) );
    assertTrue( operation.listensTo( "fake" ) );
  }

  @Test
  public void testRemoveListener() {
    clientObject.listen( "selection", false );
    clientObject.listen( "fake", false );
    clientObject.listen( "fake2", true );

    ListenOperation operation = ( ListenOperation )getMessage().getOperation( 0 );
    assertEquals( shellId, operation.getTarget() );
    assertFalse( operation.listensTo( "selection" ) );
    assertFalse( operation.listensTo( "fake" ) );
    assertTrue( operation.listensTo( "fake2" ) );
  }

  @Test
  public void testCall() {
    clientObject.call( "method", null );

    CallOperation operation = ( CallOperation )getMessage().getOperation( 0 );
    assertEquals( shellId, operation.getTarget() );
    assertEquals( "method", operation.getMethodName() );
  }

  @Test
  public void testCall_twice() {
    clientObject.call( "method", null );

    clientObject.call( "method2", new JsonObject().add( "key1", "a" ).add( "key2", 3 ) );

    CallOperation operation = ( CallOperation )getMessage().getOperation( 1 );
    assertEquals( shellId, operation.getTarget() );
    assertEquals( "method2", operation.getMethodName() );
    assertEquals( "a", operation.getProperty( "key1" ).asString() );
    assertEquals( 3, operation.getProperty( "key2" ).asInt() );
  }

  private Message getMessage() {
    ProtocolMessageWriter writer = ContextProvider.getProtocolWriter();
    return new Message( writer.createMessage() );
  }

}
