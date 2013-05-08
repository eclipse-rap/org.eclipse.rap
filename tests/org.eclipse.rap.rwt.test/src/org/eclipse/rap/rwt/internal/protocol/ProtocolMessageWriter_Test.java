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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.DestroyOperation;
import org.eclipse.rap.rwt.testfixture.Message.ListenOperation;
import org.eclipse.rap.rwt.testfixture.Message.SetOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ProtocolMessageWriter_Test {

  private ProtocolMessageWriter writer;
  private Shell shell;
  private String shellId;

  @Before
  public void setUp() {
    Fixture.setUp();
    Display display = new Display();
    shell = new Shell( display );
    writer = new ProtocolMessageWriter();
    shellId = WidgetUtil.getId( shell );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHasNoOperations() {
    assertFalse( writer.hasOperations() );
  }

  @Test
  public void testHasOperationsAfterAppend() {
    writer.appendSet( "target", "foo", 23 );

    assertTrue( writer.hasOperations() );
  }

  @Test
  public void testEmptyMessage() {
    JsonObject message = writer.createMessage();
    JsonObject head = message.get( "head" ).asObject();
    assertEquals( 0, head.size() );
    JsonArray operations = message.get( "operations" ).asArray();
    assertEquals( 0, operations.size() );
  }

  @Test
  public void testMessageWithRequestCounter() {
    writer.appendHead( ProtocolConstants.REQUEST_COUNTER, 1 );

    assertEquals( 1, getMessage().getRequestCounter() );
  }

  @Test
  public void testWriteMessageTwice() {
    writer.createMessage();
    try {
      writer.createMessage();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  @Test
  public void testAppendAfterCreate() {
    writer.createMessage();
    try {
      writer.appendDestroy( "target" );
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  @Test
  public void testAppendCall() {
    JsonObject parameters = new JsonObject()
      .add( "key1", "a" )
      .add( "key2", "b" );

    writer.appendCall( shellId, "methodName", parameters );

    CallOperation operation = (CallOperation)getMessage().getOperation( 0 );
    assertEquals( shellId, operation.getTarget() );
    assertEquals( "methodName", operation.getMethodName() );
    assertEquals( "a", operation.getProperty( "key1" ).asString() );
    assertEquals( "b", operation.getProperty( "key2" ).asString() );
  }

  @Test
  public void testAppendCall_messageWithTwoCalls() {
    JsonObject parameters = new JsonObject()
      .add( "key1", 5 )
      .add( "key2", "b" )
      .add( "key3", false );

    writer.appendCall( shellId, "methodName", null );
    writer.appendCall( shellId, "methodName", parameters );

    CallOperation operation = ( CallOperation )getMessage().getOperation( 1 );
    assertEquals( shellId, operation.getTarget() );
    assertEquals( "methodName", operation.getMethodName() );
    assertEquals( 5, operation.getProperty( "key1" ).asInt() );
    assertEquals( "b", operation.getProperty( "key2" ).asString() );
    assertEquals( JsonValue.FALSE, operation.getProperty( "key3" ) );
  }

  @Test
  public void testMessageWithCreate() {
    String displayId = DisplayUtil.getId( shell.getDisplay() );
    String[] styles = new String[] { "TRIM", "FOO" };

    writer.appendCreate( shellId, "org.Text" );
    writer.appendSet( shellId, "parent", displayId );
    writer.appendSet( shellId, "style", JsonUtil.createJsonArray( styles ) );
    writer.appendSet( shellId, "key1", "a" );
    writer.appendSet( shellId, "key2", "b" );

    CreateOperation operation = ( CreateOperation )getMessage().getOperation( 0 );
    assertEquals( shellId, operation.getTarget() );
    assertEquals( displayId, operation.getParent() );
    assertEquals( "org.Text", operation.getType() );
    assertArrayEquals( styles, operation.getStyles() );
    assertEquals( "a", operation.getProperty( "key1" ).asString() );
    assertEquals( "b", operation.getProperty( "key2" ).asString() );
  }

  @Test
  public void testMessageWithMultipleOperations() {
    Button button = new Button( shell, SWT.PUSH );
    String buttonId = WidgetUtil.getId( button );

    writer.appendCreate( shellId, "org.Text" );
    writer.appendCreate( buttonId, "org.Shell" );

    Message message = getMessage();
    assertTrue( message.getOperation( 0 ) instanceof CreateOperation );
    assertTrue( message.getOperation( 1 ) instanceof CreateOperation );
  }

  @Test
  public void testMessageWithDestroy() {
    Button button = new Button( shell, SWT.PUSH );
    String buttonId = WidgetUtil.getId( button );

    writer.appendDestroy( buttonId );

    DestroyOperation operation = ( DestroyOperation )getMessage().getOperation( 0 );
    assertEquals( buttonId, operation.getTarget() );
  }

  @Test
  public void testMessageWithDestroyTwice() {
    Button button = new Button( shell, SWT.PUSH );
    String buttonId = WidgetUtil.getId( button );

    writer.appendDestroy( buttonId );
    writer.appendDestroy( shellId );

    Message message = getMessage();
    assertTrue( message.getOperation( 0 ) instanceof DestroyOperation );
    assertTrue( message.getOperation( 1 ) instanceof DestroyOperation );
  }

  @Test
  public void testMessageWithListen() {
    Button button = new Button( shell, SWT.PUSH );
    String buttonId = WidgetUtil.getId( button );

    writer.appendListen( buttonId, "selection", false );
    writer.appendListen( buttonId, "focus", true );
    writer.appendListen( buttonId, "fake", true );

    ListenOperation operation = ( ListenOperation )getMessage().getOperation( 0 );
    assertEquals( buttonId, operation.getTarget() );
    assertFalse( operation.listensTo( "selection" ) );
    assertTrue( operation.listensTo( "focus" ) );
    assertTrue( operation.listensTo( "fake" ) );
  }

  @Test
  public void testAppendSet_appendsSequentialPropertiesToSameOperation() {
    writer.appendSet( "id", "property-1", "value-1" );
    writer.appendSet( "id", "property-2", 23 );

    Message message = getMessage();
    assertEquals( 1, message.getOperationCount() );
    assertEquals( "value-1", message.getOperation( 0 ).getProperty( "property-1" ).asString() );
    assertEquals( 23, message.getOperation( 0 ).getProperty( "property-2" ).asInt() );
  }

  @Test
  public void testAppendSet_createsSeparateOperationsForDifferentTargets() {
    writer.appendSet( "id-1", "property", "value-1" );
    writer.appendSet( "id-2", "property", "value-2" );

    Message message = getMessage();
    assertEquals( 2, message.getOperationCount() );
    assertEquals( "id-1", message.getOperation( 0 ).getTarget() );
    assertEquals( "value-2", message.getOperation( 1 ).getProperty( "property" ).asString() );
    assertEquals( "id-2", message.getOperation( 1 ).getTarget() );
    assertEquals( "value-2", message.getOperation( 1 ).getProperty( "property" ).asString() );
  }

  @Test
  public void testAppendSet_overwritesDuplicatePropertyInSameOperation() {
    writer.appendSet( "id", "property", "value-1" );
    writer.appendSet( "id", "another-property", true );
    writer.appendSet( "id", "property", "value-2" );

    Message message = getMessage();
    assertEquals( 1, message.getOperationCount() );
    assertEquals( "value-2", message.getOperation( 0 ).getProperty( "property" ).asString() );
  }

  @Test
  public void testAppendSet_createsNewOperationWhenInterruptedByAnotherOperation() {
    writer.appendSet( "id", "property", "value-1" );
    writer.appendCall( "id", "method", null );
    writer.appendSet( "id", "property", "value-2" );

    Message message = getMessage();
    assertEquals( 3, message.getOperationCount() );
    assertEquals( "value-1", message.getOperation( 0 ).getProperty( "property" ).asString() );
    assertEquals( "value-2", message.getOperation( 2 ).getProperty( "property" ).asString() );
  }

  @Test
  public void testAppendSet_createsNewOperationWhenInterruptedBySetForDifferentTarget() {
    writer.appendSet( "id-1", "property", "value-1" );
    writer.appendSet( "id-2", "property", "value-2" );
    writer.appendSet( "id-1", "property", "value-3" );

    Message message = getMessage();
    assertEquals( 3, message.getOperationCount() );
    assertEquals( "id-1", message.getOperation( 0 ).getTarget() );
    assertEquals( "value-1", message.getOperation( 0 ).getProperty( "property" ).asString() );
    assertEquals( "id-2", message.getOperation( 1 ).getTarget() );
    assertEquals( "value-2", message.getOperation( 1 ).getProperty( "property" ).asString() );
    assertEquals( "id-1", message.getOperation( 2 ).getTarget() );
    assertEquals( "value-3", message.getOperation( 2 ).getProperty( "property" ).asString() );
  }

  @Test
  public void testMessageWithMixedOperations() {
    Button button = new Button( shell, SWT.PUSH );
    addShellCreate( shell );
    addShellListeners( shell );
    addButtonCreate( button );
    addButtonCall( button );
    String buttonId = WidgetUtil.getId( button );

    Message message = getMessage();
    assertEquals( 4, message.getOperationCount() );

    CreateOperation shellCreateOperation = ( CreateOperation )message.getOperation( 0 );
    assertEquals( shellId, shellCreateOperation.getTarget() );
    assertEquals( 2, shellCreateOperation.getPropertyNames().size() );

    ListenOperation shellListenOperation = ( ListenOperation )message.getOperation( 1 );
    assertEquals( shellId, shellListenOperation.getTarget() );
    assertEquals( 2, shellListenOperation.getPropertyNames().size() );

    CreateOperation buttonCreateOperation = ( CreateOperation )message.getOperation( 2 );
    assertEquals( buttonId, buttonCreateOperation.getTarget() );
    assertEquals( 3, buttonCreateOperation.getPropertyNames().size() );

    CallOperation buttonCallOperation = ( CallOperation )message.getOperation( 3 );
    assertEquals( buttonId, buttonCallOperation.getTarget() );
    assertEquals( 1, buttonCallOperation.getPropertyNames().size() );
  }

  private void addShellCreate( Shell shell ) {
    writer.appendCreate( shellId, "org.eclipse.swt.widgets.Shell" );
    writer.appendSet( shellId, "styles", new JsonArray().add( "SHELL_TRIM" ) );
    writer.appendSet( shellId, "foo", 23 );
  }

  private void addShellListeners( Shell shell ) {
    writer.appendListen( shellId, "event1", true );
    writer.appendListen( shellId, "event2", false );
  }

  private void addButtonCreate( Button button ) {
    String buttonId = WidgetUtil.getId( button );
    writer.appendCreate( buttonId, "org.eclipse.swt.widgets.Button" );
    writer.appendSet( buttonId, "parent", shellId );
    writer.appendSet( buttonId, "styles", new JsonArray().add( "PUSH" ).add( "BORDER" ) );
    writer.appendSet( buttonId, "text", "foo" );
  }

  private void addButtonCall( Button button ) {
    JsonObject parameters = new JsonObject().add( "key1", "a1" );
    writer.appendCall( WidgetUtil.getId( button ), "select", parameters );
  }

  @Test
  public void testAppendsToExistingSetOperation() {
    writer.appendSet( shellId, "key1", "value1" );
    writer.appendSet( shellId, "key2", "value2" );

    Message message = getMessage();
    SetOperation operation = ( SetOperation )message.getOperation( 0 );
    assertEquals( "value1", operation.getProperty( "key1" ).asString() );
    assertEquals( "value2", operation.getProperty( "key2" ).asString() );
  }

  @Test
  public void testAppendsToExistingCreateOperation() {
    writer.appendCreate( shellId, "foo.Class" );
    writer.appendSet( shellId, "key1", "value1" );
    writer.appendSet( shellId, "key2", "value2" );

    Message message = getMessage();
    CreateOperation createOperation = ( CreateOperation )message.getOperation( 0 );
    assertEquals( "value1", createOperation.getProperty( "key1" ).asString() );
    assertEquals( "value2", createOperation.getProperty( "key2" ).asString() );
  }

  @Test
  public void testDoesNotAppendToOtherWidgetsOperation() {
    Button button = new Button( shell, SWT.PUSH );
    String buttonId = WidgetUtil.getId( button );

    writer.appendSet( shellId, "key1", "value1" );
    writer.appendSet( buttonId, "key2", "value2" );

    Message message = getMessage();
    SetOperation firstOperation = ( SetOperation )message.getOperation( 0 );
    assertEquals( "value1", firstOperation.getProperty( "key1" ).asString() );
    assertFalse( firstOperation.getPropertyNames().contains( "key2" ) );
  }

  @Test
  public void testStartsNewOperation() {
    Button button = new Button( shell, SWT.PUSH );
    String buttonId = WidgetUtil.getId( button );

    writer.appendCreate( shellId, "foo.Class" );
    writer.appendCreate( buttonId, "org.eclipse.swt.widgets.Button" );
    writer.appendSet( buttonId, "parent", shellId );
    writer.appendSet( buttonId, "key1", "value1" );
    writer.appendSet( buttonId, "key2", "value2" );

    Message message = getMessage();
    CreateOperation createOperation = ( CreateOperation )message.getOperation( 1 );
    assertEquals( shellId, createOperation.getParent() );
    assertEquals( "value1", createOperation.getProperty( "key1" ).asString() );
    assertEquals( "value2", createOperation.getProperty( "key2" ).asString() );
  }

  @Test
  public void testAppendArrayParameter() {
    writer.appendSet( shellId, "key", new JsonArray().add( 1 ).add( 2 ) );

    SetOperation operation = ( SetOperation )getMessage().getOperation( 0 );
    JsonArray property = operation.getProperty( "key" ).asArray();
    assertEquals( 1, property.get( 0 ).asInt() );
    assertEquals( 2, property.get( 1 ).asInt() );
  }

  @Test
  public void testAppendEmptyArrayParameter() {
    writer.appendSet( shellId, "key", new JsonArray() );

    SetOperation operation = ( SetOperation )getMessage().getOperation( 0 );
    assertEquals( 0, operation.getProperty( "key" ).asArray().size() );
  }

  @Test
  public void testAppendMixedArrayParameter() {
    writer.appendSet( shellId, "key", new JsonArray().add( 23 ).add( "Hello" ) );

    SetOperation operation = ( SetOperation )getMessage().getOperation( 0 );
    JsonArray property = operation.getProperty( "key" ).asArray();
    assertEquals( 2, property.size() );
    assertEquals( 23, property.get( 0 ).asInt() );
    assertEquals( "Hello", property.get( 1 ).asString() );
  }

  private Message getMessage() {
    return new Message( writer.createMessage() );
  }

}
