/*******************************************************************************
* Copyright (c) 2011, 2014 EclipseSource and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.rap.rwt.internal.protocol;

import static java.util.Arrays.asList;
import static org.eclipse.rap.rwt.testfixture.TestMessage.getParent;
import static org.eclipse.rap.rwt.testfixture.TestMessage.getStyles;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.internal.protocol.Operation.CallOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.DestroyOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.ListenOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.SetOperation;
import org.eclipse.rap.rwt.testfixture.Fixture;
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
  public void testEmptyMessage() {
    Message message = writer.createMessage();

    assertTrue( message.getHead().isEmpty() );
    assertTrue( message.getOperations().isEmpty() );
  }

  @Test
  public void testAppendHead() {
    writer.appendHead( "requestCounter", 1 );

    assertEquals( 1, createMessage().getHead().get( "requestCounter" ).asInt() );
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

    CallOperation operation = (CallOperation)createMessage().getOperations().get( 0 );
    assertEquals( shellId, operation.getTarget() );
    assertEquals( "methodName", operation.getMethodName() );
    assertEquals( "a", operation.getParameters().get( "key1" ).asString() );
    assertEquals( "b", operation.getParameters().get( "key2" ).asString() );
  }

  @Test
  public void testAppendCall_messageWithTwoCalls() {
    JsonObject parameters = new JsonObject()
      .add( "key1", 5 )
      .add( "key2", "b" )
      .add( "key3", false );

    writer.appendCall( shellId, "methodName", null );
    writer.appendCall( shellId, "methodName", parameters );

    CallOperation operation = ( CallOperation )createMessage().getOperations().get( 1 );
    assertEquals( shellId, operation.getTarget() );
    assertEquals( "methodName", operation.getMethodName() );
    assertEquals( 5, operation.getParameters().get( "key1" ).asInt() );
    assertEquals( "b", operation.getParameters().get( "key2" ).asString() );
    assertEquals( JsonValue.FALSE, operation.getParameters().get( "key3" ) );
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

    CreateOperation operation = ( CreateOperation )createMessage().getOperations().get( 0 );
    assertEquals( shellId, operation.getTarget() );
    assertEquals( displayId, getParent( operation ) );
    assertEquals( "org.Text", operation.getType() );
    assertEquals( asList( styles ), getStyles( operation ) );
    assertEquals( "a", operation.getProperties().get( "key1" ).asString() );
    assertEquals( "b", operation.getProperties().get( "key2" ).asString() );
  }

  @Test
  public void testMessageWithMultipleOperations() {
    Button button = new Button( shell, SWT.PUSH );
    String buttonId = WidgetUtil.getId( button );

    writer.appendCreate( shellId, "org.Text" );
    writer.appendCreate( buttonId, "org.Shell" );

    List<Operation> operations = createMessage().getOperations();
    assertTrue( operations.get( 0 ) instanceof CreateOperation );
    assertTrue( operations.get( 1 ) instanceof CreateOperation );
  }

  @Test
  public void testMessageWithDestroy() {
    Button button = new Button( shell, SWT.PUSH );
    String buttonId = WidgetUtil.getId( button );

    writer.appendDestroy( buttonId );

    DestroyOperation operation = ( DestroyOperation )createMessage().getOperations().get( 0 );
    assertEquals( buttonId, operation.getTarget() );
  }

  @Test
  public void testMessageWithDestroyTwice() {
    Button button = new Button( shell, SWT.PUSH );
    String buttonId = WidgetUtil.getId( button );

    writer.appendDestroy( buttonId );
    writer.appendDestroy( shellId );

    List<Operation> operations = createMessage().getOperations();
    assertTrue( operations.get( 0 ) instanceof DestroyOperation );
    assertTrue( operations.get( 1 ) instanceof DestroyOperation );
  }

  @Test
  public void testMessageWithListen() {
    Button button = new Button( shell, SWT.PUSH );
    String buttonId = WidgetUtil.getId( button );

    writer.appendListen( buttonId, "selection", false );
    writer.appendListen( buttonId, "focus", true );
    writer.appendListen( buttonId, "fake", true );

    ListenOperation operation = ( ListenOperation )createMessage().getOperations().get( 0 );
    assertEquals( buttonId, operation.getTarget() );
    assertEquals( JsonValue.FALSE, operation.getProperties().get( "selection" ) );
    assertEquals( JsonValue.TRUE, operation.getProperties().get( "focus" ) );
    assertEquals( JsonValue.TRUE, operation.getProperties().get( "fake" ) );
  }

  @Test
  public void testAppendSet_appendsSequentialPropertiesToSameOperation() {
    writer.appendSet( "id", "property-1", "value-1" );
    writer.appendSet( "id", "property-2", 23 );

    List<Operation> operations = createMessage().getOperations();
    assertEquals( 1, operations.size() );
    SetOperation setOperation = ( SetOperation )operations.get( 0 );
    assertEquals( "value-1", setOperation.getProperties().get( "property-1" ).asString() );
    assertEquals( 23, setOperation.getProperties().get( "property-2" ).asInt() );
  }

  @Test
  public void testAppendSet_createsSeparateOperationsForDifferentTargets() {
    writer.appendSet( "id-1", "property", "value-1" );
    writer.appendSet( "id-2", "property", "value-2" );

    List<Operation> operations = createMessage().getOperations();
    assertEquals( 2, operations.size() );
    SetOperation operation1 = ( SetOperation )operations.get( 0 );
    SetOperation operation2 = ( SetOperation )operations.get( 1 );
    assertEquals( "id-1", operation1.getTarget() );
    assertEquals( "value-1", operation1.getProperties().get( "property" ).asString() );
    assertEquals( "id-2", operation2.getTarget() );
    assertEquals( "value-2", operation2.getProperties().get( "property" ).asString() );
  }

  @Test
  public void testAppendSet_overwritesDuplicatePropertyInSameOperation() {
    writer.appendSet( "id", "property", "value-1" );
    writer.appendSet( "id", "another-property", true );
    writer.appendSet( "id", "property", "value-2" );

    List<Operation> operations = createMessage().getOperations();
    assertEquals( 1, operations.size() );
    SetOperation operation1 = ( SetOperation )operations.get( 0 );
    assertEquals( "value-2", operation1.getProperties().get( "property" ).asString() );
  }

  @Test
  public void testAppendSet_createsNewOperationWhenInterruptedByAnotherOperation() {
    writer.appendSet( "id", "property", "value-1" );
    writer.appendCall( "id", "method", null );
    writer.appendSet( "id", "property", "value-2" );

    List<Operation> operations = createMessage().getOperations();
    assertEquals( 3, operations.size() );
    SetOperation operation1 = ( SetOperation )operations.get( 0 );
    assertEquals( "value-1", operation1.getProperties().get( "property" ).asString() );
    SetOperation operation2 = ( SetOperation )operations.get( 2 );
    assertEquals( "value-2", operation2.getProperties().get( "property" ).asString() );
  }

  @Test
  public void testAppendSet_createsNewOperationWhenInterruptedBySetForDifferentTarget() {
    writer.appendSet( "id-1", "property", "value-1" );
    writer.appendSet( "id-2", "property", "value-2" );
    writer.appendSet( "id-1", "property", "value-3" );

    List<Operation> operations = createMessage().getOperations();

    assertEquals( 3, operations.size() );
    SetOperation operation1 = ( SetOperation )operations.get( 0 );
    SetOperation operation2 = ( SetOperation )operations.get( 1 );
    SetOperation operation3 = ( SetOperation )operations.get( 2 );
    assertEquals( "id-1", operation1.getTarget() );
    assertEquals( "value-1", operation1.getProperties().get( "property" ).asString() );
    assertEquals( "id-2", operation2.getTarget() );
    assertEquals( "value-2", operation2.getProperties().get( "property" ).asString() );
    assertEquals( "id-1", operation3.getTarget() );
    assertEquals( "value-3", operation3.getProperties().get( "property" ).asString() );
  }

  @Test
  public void testMessageWithMixedOperations() {
    Button button = new Button( shell, SWT.PUSH );
    addShellCreate( shell );
    addShellListeners( shell );
    addButtonCreate( button );
    addButtonCall( button );
    String buttonId = WidgetUtil.getId( button );

    List<Operation> operations = createMessage().getOperations();
    assertEquals( 4, operations.size() );

    CreateOperation shellCreateOperation = ( CreateOperation )operations.get( 0 );
    assertEquals( shellId, shellCreateOperation.getTarget() );
    assertEquals( 2, shellCreateOperation.getProperties().size() );

    ListenOperation shellListenOperation = ( ListenOperation )operations.get( 1 );
    assertEquals( shellId, shellListenOperation.getTarget() );
    assertEquals( 2, shellListenOperation.getProperties().size() );

    CreateOperation buttonCreateOperation = ( CreateOperation )operations.get( 2 );
    assertEquals( buttonId, buttonCreateOperation.getTarget() );
    assertEquals( 3, buttonCreateOperation.getProperties().size() );

    CallOperation buttonCallOperation = ( CallOperation )operations.get( 3 );
    assertEquals( buttonId, buttonCallOperation.getTarget() );
    assertEquals( 1, buttonCallOperation.getParameters().size() );
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

    SetOperation operation = ( SetOperation )createMessage().getOperations().get( 0 );
    assertEquals( "value1", operation.getProperties().get( "key1" ).asString() );
    assertEquals( "value2", operation.getProperties().get( "key2" ).asString() );
  }

  @Test
  public void testAppendsToExistingCreateOperation() {
    writer.appendCreate( shellId, "foo.Class" );
    writer.appendSet( shellId, "key1", "value1" );
    writer.appendSet( shellId, "key2", "value2" );

    CreateOperation createOperation = ( CreateOperation )createMessage().getOperations().get( 0 );
    assertEquals( "value1", createOperation.getProperties().get( "key1" ).asString() );
    assertEquals( "value2", createOperation.getProperties().get( "key2" ).asString() );
  }

  @Test
  public void testDoesNotAppendToOtherWidgetsOperation() {
    Button button = new Button( shell, SWT.PUSH );
    String buttonId = WidgetUtil.getId( button );

    writer.appendSet( shellId, "key1", "value1" );
    writer.appendSet( buttonId, "key2", "value2" );

    SetOperation firstOperation = ( SetOperation )createMessage().getOperations().get( 0 );
    assertEquals( "value1", firstOperation.getProperties().get( "key1" ).asString() );
    assertFalse( firstOperation.getProperties().names().contains( "key2" ) );
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

    CreateOperation createOperation = ( CreateOperation )createMessage().getOperations().get( 1 );
    assertEquals( shellId, getParent( createOperation ) );
    assertEquals( "value1", createOperation.getProperties().get( "key1" ).asString() );
    assertEquals( "value2", createOperation.getProperties().get( "key2" ).asString() );
  }

  @Test
  public void testAppendArrayParameter() {
    writer.appendSet( shellId, "key", new JsonArray().add( 1 ).add( 2 ) );

    SetOperation operation = ( SetOperation )createMessage().getOperations().get( 0 );
    JsonArray property = operation.getProperties().get( "key" ).asArray();
    assertEquals( 1, property.get( 0 ).asInt() );
    assertEquals( 2, property.get( 1 ).asInt() );
  }

  @Test
  public void testAppendEmptyArrayParameter() {
    writer.appendSet( shellId, "key", new JsonArray() );

    SetOperation operation = ( SetOperation )createMessage().getOperations().get( 0 );
    assertEquals( 0, operation.getProperties().get( "key" ).asArray().size() );
  }

  @Test
  public void testAppendMixedArrayParameter() {
    writer.appendSet( shellId, "key", new JsonArray().add( 23 ).add( "Hello" ) );

    SetOperation operation = ( SetOperation )createMessage().getOperations().get( 0 );
    JsonArray property = operation.getProperties().get( "key" ).asArray();
    assertEquals( 2, property.size() );
    assertEquals( 23, property.get( 0 ).asInt() );
    assertEquals( "Hello", property.get( 1 ).asString() );
  }

  private Message createMessage() {
    return writer.createMessage();
  }

}
