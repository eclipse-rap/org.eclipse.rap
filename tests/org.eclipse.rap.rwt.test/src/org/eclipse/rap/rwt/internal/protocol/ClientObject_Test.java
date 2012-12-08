/*******************************************************************************
* Copyright (c) 2011, 2012 EclipseSource and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.rap.rwt.internal.protocol;

import static org.eclipse.rap.rwt.internal.resources.TestUtil.assertArrayEquals;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

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
import org.json.JSONArray;
import org.json.JSONException;


public class ClientObject_Test extends TestCase {

  private Shell shell;
  private String shellId;
  private IClientObject clientObject;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
    Display display = new Display();
    shell = new Shell( display );
    shellId = WidgetUtil.getId( shell );
    clientObject = ClientObjectFactory.getClientObject( shell );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testCreate() {
    clientObject.create( "rwt.widgets.Shell" );

    CreateOperation operation = ( CreateOperation )getMessage().getOperation( 0 );
    assertEquals( shellId, operation.getTarget() );
    assertEquals( "rwt.widgets.Shell", operation.getType() );
  }

  public void testCreateIncludesSetProperties() {
    clientObject.create( "rwt.widgets.Shell" );
    clientObject.set( "foo", 23 );

    Message message = getMessage();
    assertEquals( 1, message.getOperationCount() );
    assertTrue( message.getOperation( 0 ) instanceof CreateOperation );
    assertEquals( new Integer( 23 ), message.getOperation( 0 ).getProperty( "foo" ) );
  }

  public void testSetProperty() {
    clientObject.set( "key", ( Object )"value" );
    clientObject.set( "key2", 2 );
    clientObject.set( "key3", 3.5 );
    clientObject.set( "key4", true );
    clientObject.set( "key5", "aString" );

    SetOperation operation = ( SetOperation )getMessage().getOperation( 0 );
    assertEquals( shellId, operation.getTarget() );
    assertEquals( "value", operation.getProperty( "key" ) );
    assertEquals( new Integer( 2 ), operation.getProperty( "key2" ) );
    assertEquals( new Double( 3.5 ), operation.getProperty( "key3" ) );
    assertEquals( Boolean.TRUE, operation.getProperty( "key4" ) );
    assertEquals( "aString", operation.getProperty( "key5" ) );
  }

  public void testSetPropertyForIntArray() throws JSONException {
    clientObject.set( "key", new int[]{ 1, 2, 3 } );

    SetOperation operation = ( SetOperation )getMessage().getOperation( 0 );
    JSONArray result = ( JSONArray )operation.getProperty( "key" );
    assertEquals( 3, result.length() );
    assertEquals( 1, result.getInt( 0 ) );
    assertEquals( 2, result.getInt( 1 ) );
    assertEquals( 3, result.getInt( 2 ) );
  }

  public void testCreatePropertyGetStyle() {
    clientObject.create( "rwt.widgets.Shell"  );
    clientObject.set( "style", new String[] { "PUSH", "BORDER" } );

    CreateOperation operation = ( CreateOperation )getMessage().getOperation( 0 );
    assertArrayEquals( new String[] { "PUSH", "BORDER" }, operation.getStyles() );
  }

  public void testDestroy() {
    clientObject.destroy();

    DestroyOperation operation = ( DestroyOperation )getMessage().getOperation( 0 );
    assertEquals( shellId, operation.getTarget() );
  }

  public void testAddListener() {
    clientObject.listen( "selection", true );
    clientObject.listen( "fake", true );

    ListenOperation operation = ( ListenOperation )getMessage().getOperation( 0 );
    assertEquals( shellId, operation.getTarget() );
    assertTrue( operation.listensTo( "selection" ) );
    assertTrue( operation.listensTo( "fake" ) );
  }

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

  public void testCall() {
    clientObject.call( "method", null );

    CallOperation operation = ( CallOperation )getMessage().getOperation( 0 );
    assertEquals( shellId, operation.getTarget() );
    assertEquals( "method", operation.getMethodName() );
  }

  public void testCallTwice() {
    clientObject.call( "method", null );
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "key1", "a" );
    properties.put( "key2", new Integer( 3 ) );

    clientObject.call( "method2", properties );

    CallOperation operation = ( CallOperation )getMessage().getOperation( 1 );
    assertEquals( shellId, operation.getTarget() );
    assertEquals( "method2", operation.getMethodName() );
    assertEquals( "a", operation.getProperty( "key1" ) );
    assertEquals( new Integer( 3 ), operation.getProperty( "key2" ) );
  }

  private Message getMessage() {
    ProtocolMessageWriter writer = ContextProvider.getProtocolWriter();
    return new Message( writer.createMessage() );
  }
}
