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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ProtocolMessageWriter_Test extends TestCase {

  private ProtocolMessageWriter writer;
  private Shell shell;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    Display display = new Display();
    shell = new Shell( display );
    writer = new ProtocolMessageWriter();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testHasNoOperations() {
    assertFalse( writer.hasOperations() );
  }

  public void testHasOperationsAfterAppend() {
    writer.appendSet( "target", "foo", 23 );

    assertTrue( writer.hasOperations() );
  }

  public void testEmptyMessage() throws JSONException {
    String messageString = writer.createMessage();
    JSONObject message = new JSONObject( messageString );
    JSONObject head = message.getJSONObject( "head" );
    assertEquals( 0, head.length() );
    JSONArray operations = message.getJSONArray( "operations" );
    assertEquals( 0, operations.length() );
  }

  public void testMessageWithRequestCounter() {
    writer.appendHead( ProtocolConstants.REQUEST_COUNTER, 1 );

    assertEquals( 1, getMessage().getRequestCounter() );
  }

  public void testWriteMessageTwice() {
    writer.createMessage();
    try {
      writer.createMessage();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  public void testAppendAfterCreate() {
    writer.createMessage();
    try {
      writer.appendDestroy( "target" );
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  public void testMessageWithCall() {
    String shellId = WidgetUtil.getId( shell );
    String methodName = "methodName";
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "key1", "a" );
    properties.put( "key2", "b" );

    writer.appendCall( shellId, methodName, properties );

    CallOperation operation = (CallOperation)getMessage().getOperation( 0 );
    assertEquals( shellId, operation.getTarget() );
    assertEquals( methodName, operation.getMethodName() );
    assertEquals( "a", operation.getProperty( "key1" ) );
    assertEquals( "b", operation.getProperty( "key2" ) );
  }

  public void testMessageWithTwoCallss() {
    String shellId = WidgetUtil.getId( shell );
    String methodName = "methodName";
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "key1", new Integer( 5 ) );
    properties.put( "key2", "b" );
    properties.put( "key3", Boolean.FALSE );

    writer.appendCall( shellId, methodName, null );
    writer.appendCall( shellId, methodName, properties );

    CallOperation operation = ( CallOperation )getMessage().getOperation( 1 );
    assertEquals( shellId, operation.getTarget() );
    assertEquals( methodName, operation.getMethodName() );
    assertEquals( new Integer( 5 ), operation.getProperty( "key1" ) );
    assertEquals( "b", operation.getProperty( "key2" ) );
    assertEquals( Boolean.FALSE, operation.getProperty( "key3" ) );
  }

  public void testMessageWithCreate() {
    String displayId = DisplayUtil.getId( shell.getDisplay() );
    String shellId = WidgetUtil.getId( shell );
    String[] styles = new String[] { "TRIM", "FOO" };

    writer.appendCreate( shellId, "org.Text" );
    writer.appendSet( shellId, "parent", displayId );
    writer.appendSet( shellId, "style", styles );
    writer.appendSet( shellId, "key1", "a" );
    writer.appendSet( shellId, "key2", "b" );

    CreateOperation operation = ( CreateOperation )getMessage().getOperation( 0 );
    assertEquals( shellId, operation.getTarget() );
    assertEquals( displayId, operation.getParent() );
    assertEquals( "org.Text", operation.getType() );
    assertArrayEquals( styles, operation.getStyles() );
    assertEquals( "a", operation.getProperty( "key1" ) );
    assertEquals( "b", operation.getProperty( "key2" ) );
  }

  public void testMessageWithMultipleOperations() {
    Button button = new Button( shell, SWT.PUSH );
    String shellId = WidgetUtil.getId( shell );
    String buttonId = WidgetUtil.getId( button );

    writer.appendCreate( shellId, "org.Text" );
    writer.appendCreate( buttonId, "org.Shell" );

    Message message = getMessage();
    assertTrue( message.getOperation( 0 ) instanceof CreateOperation );
    assertTrue( message.getOperation( 1 ) instanceof CreateOperation );
  }

  public void testMessageWithIllegalParameterType() {
    Button wrongParameter = new Button( shell, SWT.PUSH );
    String shellId = WidgetUtil.getId( shell );

    try {
      writer.appendSet( shellId, "text", wrongParameter );
      fail();
    } catch ( IllegalArgumentException expected ) {
    }
  }

  public void testMessageWithDestroy() {
    Button button = new Button( shell, SWT.PUSH );
    String buttonId = WidgetUtil.getId( button );

    writer.appendDestroy( buttonId );

    DestroyOperation operation = ( DestroyOperation )getMessage().getOperation( 0 );
    assertEquals( buttonId, operation.getTarget() );
  }

  public void testMessageWithDestroyTwice() {
    Button button = new Button( shell, SWT.PUSH );
    String shellId = WidgetUtil.getId( shell );
    String buttonId = WidgetUtil.getId( button );

    writer.appendDestroy( buttonId );
    writer.appendDestroy( shellId );

    Message message = getMessage();
    assertTrue( message.getOperation( 0 ) instanceof DestroyOperation );
    assertTrue( message.getOperation( 1 ) instanceof DestroyOperation );
  }

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

  public void testMessageWithExecuteScript() {
    String script = "var c = 4; c++;";
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "content", script );

    writer.appendCall( "jsex", "execute", properties );

    CallOperation operation = ( CallOperation )getMessage().getOperation( 0 );
    assertEquals( "jsex", operation.getTarget() );
    assertEquals( script, operation.getProperty( "content" ) );
  }

  public void testMessageWithSet() {
    Button button = new Button( shell, SWT.PUSH );
    String buttonId = WidgetUtil.getId( button );

    writer.appendSet( buttonId, "text", "newText" );
    writer.appendSet( buttonId, "image", "aUrl" );
    writer.appendSet( buttonId, "fake", 1 );

    SetOperation operation = ( SetOperation )getMessage().getOperation( 0 );
    assertEquals( buttonId, operation.getTarget() );
    assertEquals( "newText", operation.getProperty( "text" ) );
    assertEquals( "aUrl", operation.getProperty( "image" ) );
    assertEquals( new Integer( 1 ), operation.getProperty( "fake" ) );
  }

  public void testMessageWithSetTwice() {
    Button button = new Button( shell, SWT.PUSH );
    String shellId = WidgetUtil.getId( shell );
    String buttonId = WidgetUtil.getId( button );

    writer.appendSet( shellId, "text", "newText" );
    writer.appendSet( shellId, "image", true );
    writer.appendSet( shellId, "fake", 1 );
    writer.appendSet( buttonId, "text", "newText" );
    writer.appendSet( buttonId, "image", true );
    writer.appendSet( buttonId, "fake", 1 );

    SetOperation operation = ( SetOperation )getMessage().getOperation( 1 );
    assertEquals( buttonId, operation.getTarget() );
    assertEquals( "newText", operation.getProperty( "text" ) );
    assertEquals( new Integer( 1 ), operation.getProperty( "fake" ) );
    assertTrue( ( ( Boolean )operation.getProperty( "image" ) ).booleanValue() );
  }

  public void testMessageWithSetDuplicateProperty() {
    Button button = new Button( shell, SWT.PUSH );
    String buttonId = WidgetUtil.getId( button );

    writer.appendSet( buttonId, "text", "newText" );
    try {
      writer.appendSet( buttonId, "text", "newText" );
      fail();
    } catch( IllegalArgumentException e ) {
    }
  }

  public void testMessageWithMixedOperations() {
    Button button = new Button( shell, SWT.PUSH );
    addShellCreate( shell );
    addShellListeners( shell );
    addButtonCreate( button );
    addButtonCall( button );
    String shellId = WidgetUtil.getId( shell );
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
    String shellId = WidgetUtil.getId( shell );
    writer.appendCreate( shellId, "org.eclipse.swt.widgets.Shell" );
    writer.appendSet( shellId, "styles", new String[]{ "SHELL_TRIM" } );
    writer.appendSet( shellId, "foo", 23 );
  }

  private void addShellListeners( Shell shell ) {
    writer.appendListen( WidgetUtil.getId( shell ), "event1", true );
    writer.appendListen( WidgetUtil.getId( shell ), "event2", false );
  }

  private void addButtonCreate( Button button ) {
    String buttonId = WidgetUtil.getId( button );
    String shellId = WidgetUtil.getId( shell );
    writer.appendCreate( buttonId, "org.eclipse.swt.widgets.Button" );
    writer.appendSet( buttonId, "parent", shellId );
    writer.appendSet( buttonId, "styles", new String[] { "PUSH", "BORDER" } );
    writer.appendSet( buttonId, "text", "foo" );
  }

  private void addButtonCall( Button button ) {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "key1", "a1" );
    writer.appendCall( WidgetUtil.getId( button ), "select", properties );
  }

  public void testAppendsToExistingSetOperation() {
    String shellId = WidgetUtil.getId( shell );

    writer.appendSet( shellId, "key1", "value1" );
    writer.appendSet( shellId, "key2", "value2" );

    Message message = getMessage();
    SetOperation operation = ( SetOperation )message.getOperation( 0 );
    assertEquals( "value1", operation.getProperty( "key1" ) );
    assertEquals( "value2", operation.getProperty( "key2" ) );
  }

  public void testAppendsToExistingCreateOperation() {
    String shellId = WidgetUtil.getId( shell );

    writer.appendCreate( shellId, "foo.Class" );
    writer.appendSet( shellId, "key1", "value1" );
    writer.appendSet( shellId, "key2", "value2" );

    Message message = getMessage();
    CreateOperation createOperation = ( CreateOperation )message.getOperation( 0 );
    assertEquals( "value1", createOperation.getProperty( "key1" ) );
    assertEquals( "value2", createOperation.getProperty( "key2" ) );
  }

  public void testDoesNotAppendToOtherWidgetsOperation() {
    Button button = new Button( shell, SWT.PUSH );
    String shellId = WidgetUtil.getId( shell );
    String buttonId = WidgetUtil.getId( button );

    writer.appendSet( shellId, "key1", "value1" );
    writer.appendSet( buttonId, "key2", "value2" );

    Message message = getMessage();
    SetOperation firstOperation = ( SetOperation )message.getOperation( 0 );
    assertEquals( "value1", firstOperation.getProperty( "key1" ) );
    assertFalse( firstOperation.getPropertyNames().contains( "key2" ) );
  }

  public void testStartsNewOperation() {
    Button button = new Button( shell, SWT.PUSH );
    String shellId = WidgetUtil.getId( shell );
    String buttonId = WidgetUtil.getId( button );

    writer.appendCreate( shellId, "foo.Class" );
    writer.appendCreate( buttonId, "org.eclipse.swt.widgets.Button" );
    writer.appendSet( buttonId, "parent", shellId );
    writer.appendSet( buttonId, "key1", "value1" );
    writer.appendSet( buttonId, "key2", "value2" );

    Message message = getMessage();
    CreateOperation createOperation = ( CreateOperation )message.getOperation( 1 );
    assertEquals( shellId, createOperation.getParent() );
    assertEquals( "value1", createOperation.getProperty( "key1" ) );
    assertEquals( "value2", createOperation.getProperty( "key2" ) );
  }

  public void testAppendArrayParameter() throws JSONException {
    String shellId = WidgetUtil.getId( shell );
    Integer[] arrayParameter = new Integer[] { new Integer( 1 ), new Integer( 2 ) };

    writer.appendSet( shellId, "key", arrayParameter );

    SetOperation operation = ( SetOperation )getMessage().getOperation( 0 );
    assertEquals( 1, ( ( JSONArray )operation.getProperty( "key" ) ).getInt( 0 ) );
    assertEquals( 2, ( ( JSONArray )operation.getProperty( "key" ) ).getInt( 1 ) );
  }

  public void testAppendEmptyArrayParameter() {
    String shellId = WidgetUtil.getId( shell );
    Object[] emptyArray = new Object[ 0 ];

    writer.appendSet( shellId, "key", emptyArray );

    SetOperation operation = ( SetOperation )getMessage().getOperation( 0 );
    assertEquals( 0, ( ( JSONArray )operation.getProperty( "key" ) ).length() );
  }

  public void testAppendMixedArrayParameter() throws JSONException {
    String shellId = WidgetUtil.getId( shell );
    Object[] mixedArray = new Object[] { new Integer( 23 ), "Hello" };

    writer.appendSet( shellId, "key", mixedArray );

    SetOperation operation = ( SetOperation )getMessage().getOperation( 0 );
    assertEquals( 2, ( ( JSONArray )operation.getProperty( "key" ) ).length() );
    assertEquals( "Hello", ( ( JSONArray )operation.getProperty( "key" ) ).get( 1 ) );
  }

  private Message getMessage() {
    String message = writer.createMessage();
    return new Message( message );
  }
}
