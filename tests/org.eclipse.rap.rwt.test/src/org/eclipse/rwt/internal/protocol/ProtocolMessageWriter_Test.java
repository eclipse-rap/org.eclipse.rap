/*******************************************************************************
* Copyright (c) 2011 EclipseSource and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.rwt.internal.protocol;

import static org.eclipse.rwt.internal.protocol.ProtocolConstants.MESSAGE_META;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.MESSAGE_OPERATIONS;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.META_REQUEST_COUNTER;
import static org.eclipse.rwt.internal.resources.TestUtil.assertArrayEquals;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rwt.internal.lifecycle.RWTRequestVersionControl;
import org.eclipse.rwt.internal.protocol.util.*;
import org.eclipse.rwt.internal.protocol.util.Message.CreateOperation;
import org.eclipse.rwt.internal.protocol.util.Message.DestroyOperation;
import org.eclipse.rwt.internal.protocol.util.Message.CallOperation;
import org.eclipse.rwt.internal.protocol.util.Message.ExecuteScriptOperation;
import org.eclipse.rwt.internal.protocol.util.Message.ListenOperation;
import org.eclipse.rwt.internal.protocol.util.Message.Operation;
import org.eclipse.rwt.internal.protocol.util.Message.SetOperation;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.json.*;


public class ProtocolMessageWriter_Test extends TestCase {

  private ProtocolMessageWriter writer;
  private Shell shell;

  protected void setUp() throws Exception {
    Fixture.setUp();
    Display display = new Display();
    shell = new Shell( display );
    writer = new ProtocolMessageWriter();
  }

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
    JSONObject meta = message.getJSONObject( MESSAGE_META );
    int requestCount = RWTRequestVersionControl.getInstance().getCurrentRequestId().intValue();
    assertEquals( requestCount, meta.getInt( META_REQUEST_COUNTER ) );
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    assertEquals( 0, operations.length() );
  }

  public void testWriteMessageTwice() {
    writer.createMessage();
    try {
      writer.createMessage();
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
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "key1", "a" );
    properties.put( "key2", "b" );

    writer.appendCreate( shellId, displayId, "org.Text", styles, properties );

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
    String displayId = DisplayUtil.getId( shell.getDisplay() );
    String buttonId = WidgetUtil.getId( button );

    writer.appendCreate( shellId, displayId, "org.Text", null, null );
    writer.appendCreate( buttonId, shellId, "org.Shell", null, null );

    Message message = getMessage();
    assertTrue( message.getOperation( 0 ) instanceof CreateOperation );
    assertTrue( message.getOperation( 1 ) instanceof CreateOperation );
  }

  public void testMessageWithIllegalParameterType() {
    Button wrongParameter = new Button( shell, SWT.PUSH );
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "key1", "a" );
    properties.put( "key2", wrongParameter );

    try {
      
      writer.appendCreate( DisplayUtil.getId( shell.getDisplay() ),
                                 WidgetUtil.getId( shell ),
                                 "org.Text",
                                 new String[] { "TRIM" },
                                 properties );
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
    Button button = new Button( shell, SWT.PUSH );
    String buttonId = WidgetUtil.getId( button );
    String scriptType = "text/javascript";
    String script = "var c = 4; c++;";

    writer.appendExecuteScript( buttonId, scriptType, script );
    
    ExecuteScriptOperation operation = ( ExecuteScriptOperation )getMessage().getOperation( 0 );
    assertEquals( buttonId, operation.getTarget() );
    assertEquals( scriptType, operation.getScriptType() );
    assertEquals( script, operation.getScript() );
  }

  public void testMessageWithExecuteScriptTwice() {
    Button button = new Button( shell, SWT.PUSH );
    String shellId = WidgetUtil.getId( shell );
    String buttonId = WidgetUtil.getId( button );
    String scriptType = "text/vb";
    String script = "really bad VB;";

    writer.appendExecuteScript( buttonId, "text/javascript", "var c = 4; c++;" );
    writer.appendExecuteScript( WidgetUtil.getId( shell ), scriptType, script );
    
    Message message = getMessage();
    assertTrue( message.getOperation( 0 ) instanceof ExecuteScriptOperation );
    ExecuteScriptOperation secondOperation = ( ExecuteScriptOperation )message.getOperation( 1 );
    assertEquals( shellId, secondOperation.getTarget() );
    assertEquals( scriptType, secondOperation.getScriptType() );
    assertEquals( script, secondOperation.getScript() );
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
    createShellOperations( shell );
    createButtonOperations( button );

    Message message = getMessage();
    assertShellCreated( message );
    checkShellSet( message );
    checkShellListen( message );
    checkButtonCreate( message, button );
    checkButtonCall( message, button );
  }

  private void createShellOperations( Shell shell ) {
    addShellCreate( shell );
    addShellSet( shell );
    addShellListeners( shell );
  }

  private void addShellCreate( Shell shell ) {
    String[] styles = new String[]{ "SHELL_TRIM" };
    writer.appendCreate( WidgetUtil.getId( shell ),
                               DisplayUtil.getId( shell.getDisplay() ),
                               shell.getClass().getName(),
                               styles,
                               null );
  }

  private void addShellSet( Shell shell ) {
    for( int i = 0; i < 5; i++ ) {
      writer.appendSet( WidgetUtil.getId( shell ), "key" + i, "value" + i );
    }
  }

  private void addShellListeners( Shell shell ) {
    for( int i = 0; i < 5; i++ ) {
      boolean listen = i % 2 == 0 ? true : false;
      writer.appendListen( WidgetUtil.getId( shell ), "listener" + i, listen );
    }
  }

  private void createButtonOperations( Button button ) {
    addButtonCreate( button );
    addButtonCall( button );
  }

  private void addButtonCreate( Button button ) {
    String[] styles = new String[] { "PUSH", "BORDER" };
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "key1", new Integer( 4 ) );
    properties.put( "key2", Boolean.TRUE );
    
    writer.appendCreate( WidgetUtil.getId( button ),
                                WidgetUtil.getId( button.getParent() ),
                                button.getClass().getName(),
                                styles,
                                properties );
  }

  private void addButtonCall( Button button ) {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "key1", "a1" );
    
    writer.appendCall( WidgetUtil.getId( button ), "select", properties );
  }

  private void assertShellCreated( Message message ) {
    String shellId = WidgetUtil.getId( shell );
    String displayId = DisplayUtil.getId( shell.getDisplay() );
    
    CreateOperation operation = ( CreateOperation )message.getOperation( 0 );
    assertEquals( shellId, operation.getTarget() );
    assertEquals( displayId, operation.getParent() );
    assertEquals( "SHELL_TRIM", operation.getStyles()[ 0 ] );
  }

  private void checkShellSet( Message message ) {
    String shellId = WidgetUtil.getId( shell );
    
    SetOperation operation = ( SetOperation )message.getOperation( 1 );
    assertEquals( shellId, operation.getTarget() );
    for( int i = 0; i < 5; i++ ) {
      String value = ( String )operation.getProperty( "key" + i );
      assertEquals( "value" + i, value );
    }
  }

  private void checkShellListen( Message message ) {
    String shellId = WidgetUtil.getId( shell );

    ListenOperation operation = ( ListenOperation )message.getOperation( 2 );
    assertEquals( shellId, operation.getTarget() );
    assertTrue( operation.listensTo( "listener0" ) );
    assertFalse( operation.listensTo( "listener1" ) );
  }

  private void checkButtonCreate( Message message, Button button ) {
    String buttonId = WidgetUtil.getId( button );
    String shellId = WidgetUtil.getId( shell );
    
    CreateOperation operation = ( CreateOperation )message.getOperation( 3 );
    assertEquals( buttonId, operation.getTarget() );
    assertEquals( shellId, operation.getParent() );
    assertEquals( new Integer( 4 ), operation.getProperty( "key1" ) );
    assertTrue( ( ( Boolean )operation.getProperty( "key2" ) ).booleanValue() );
    assertEquals( button.getClass().getName(), operation.getType() );
    assertEquals( "PUSH", operation.getStyles()[ 0 ] );
    assertEquals( "BORDER", operation.getStyles()[ 1 ] );
  }

  private void checkButtonCall( Message message, Widget button ) {
    String buttonId = WidgetUtil.getId( button );

    CallOperation operation = ( CallOperation )message.getOperation( 4 );
    assertEquals( buttonId, operation.getTarget() );
    assertEquals( "select", operation.getMethodName() );
    assertEquals( "a1", operation.getProperty( "key1" ) );
  }

  public void testAppendsToExistingOperation() {
    Button button = new Button( shell, SWT.PUSH );
    String shellId = WidgetUtil.getId( shell );
    String buttonId = WidgetUtil.getId( button );
    String[] styles = new String[] { "SYSTEM_MODAL" };

    writer.appendCreate( shellId, "parentId", "foo.Class", styles, null );
    writer.appendSet( shellId, "key", "value" );
    writer.appendSet( shellId, "key2", "value2" );
    writer.appendSet( shellId, "key3", "value3" );
    writer.appendSet( buttonId, "key", "value" );

    Message message = getMessage();
    assertFirstOperation( message.getOperation( 0 ) );
    assertSecondOperation( message.getOperation( 1 ) );
    assertThirdOperation( buttonId, message.getOperation( 2 ) );
  }

  private void assertFirstOperation( Operation operation ) {
    CreateOperation createOperation = ( CreateOperation )operation;
    assertEquals( "parentId", createOperation.getParent() );
  }

  private void assertSecondOperation( Operation operation ) {
    SetOperation setOperation = ( SetOperation )operation;
    assertEquals( "value", setOperation.getProperty( "key" ) );
    assertEquals( "value2", setOperation.getProperty( "key2" ) );
    assertEquals( "value3", setOperation.getProperty( "key3" ) );
  }

  private void assertThirdOperation( String buttonId, Operation operation ) {
    SetOperation setOperation = ( SetOperation )operation;
    assertEquals( buttonId, operation.getTarget() );
    assertEquals( "value", setOperation.getProperty( "key" ) );
  }

  public void testStartsNewOperation() {
    String shellId = WidgetUtil.getId( shell );
    Button button = new Button( shell, SWT.PUSH );
    String buttonId = WidgetUtil.getId( button );
    String type = button.getClass().getName();
    String[] styles = new String[] { "PUSH" };

    writer.appendCreate( shellId, "parentId", "foo.Class", styles, null );
    writer.appendCreate( buttonId, shellId, type, styles, null );
    writer.appendSet( buttonId, "key", "value" );
    writer.appendSet( buttonId, "key2", "value" );

    Message message = getMessage();
    CreateOperation createOperation = ( CreateOperation )message.getOperation( 1 );
    assertEquals( shellId, createOperation.getParent() );
    SetOperation setOperation = ( SetOperation )message.getOperation( 2 );
    assertEquals( "value", setOperation.getProperty( "key" ) );
    assertEquals( "value", setOperation.getProperty( "key2" ) );
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
    return new Message( writer.createMessage() );
  }
}
