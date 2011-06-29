/******************************************************************************* 
* Copyright (c) 2011 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rwt.internal.protocol;

import static org.eclipse.rwt.internal.protocol.ProtocolConstants.CREATE_PARENT;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.CREATE_STYLE;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.CREATE_TYPE;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.DO_NAME;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.EXECUTE_SCRIPT_CONTENT;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.EXECUTE_SCRIPT_TYPE;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.MESSAGE_META;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.MESSAGE_OPERATIONS;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.META_REQUEST_COUNTER;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.OPERATION_DETAILS;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.OPERATION_TARGET;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.OPERATION_TYPE;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.PARAMETER;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.TYPE_CREATE;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.TYPE_DESTROY;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.TYPE_DO;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.TYPE_EXECUTE_SCRIPT;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.TYPE_LISTEN;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.TYPE_SET;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rwt.internal.lifecycle.RWTRequestVersionControl;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.json.*;


public class ProtocolMessageWriter_Test extends TestCase {
  
  private StringWriter stringWriter;
  private ProtocolMessageWriter writer;
  private Shell shell;

  protected void setUp() throws Exception {
    Fixture.setUp();
    Display display = new Display();
    shell = new Shell( display );
    stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter( stringWriter );
    writer = new ProtocolMessageWriter( printWriter );
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  } 
  
  public void testEmptyMessage() throws JSONException {
    writer.writeMessage();

    JSONObject message = getMessageJson();
    JSONObject meta = message.getJSONObject( MESSAGE_META );
    int requestCount = RWTRequestVersionControl.getInstance().getCurrentRequestId().intValue();
    assertEquals( requestCount, meta.getInt( META_REQUEST_COUNTER ) );
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    assertEquals( 0, operations.length() );
  }
  
  public void testWriteMessageTwice() {
    writer.writeMessage();
    try {
      writer.writeMessage();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }
  
  public void testMessageWithDo() throws JSONException {
    String shellId = WidgetUtil.getId( shell );
    String methodName = "methodName";

    writer.appendDo( shellId, methodName, new Object[] { "a", "b" } );
    writer.writeMessage();

    JSONObject message = getMessageJson();
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 0 );
    assertEquals( shellId, operation.getString( OPERATION_TARGET ) );
    assertEquals( TYPE_DO, operation.getString( OPERATION_TYPE ) );
    JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
    assertEquals( methodName, details.getString( DO_NAME ) );
    JSONArray params = details.getJSONArray( PARAMETER );
    assertEquals( "a", params.getString( 0 ) );
    assertEquals( "b", params.getString( 1 ) );
  }
  
  public void testMessageWithTwoDos() throws JSONException {
    String shellId = WidgetUtil.getId( shell );
    String methodName = "methodName";
    Object[] array = new Object[] { new Integer( 5 ), "b", new Boolean( false ) };
    
    writer.appendDo( shellId, methodName, new Object[] { "a", "b" } );
    writer.appendDo( shellId, methodName, array );
    writer.writeMessage();

    JSONObject message = getMessageJson();
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 1 );
    assertEquals( TYPE_DO, operation.getString( OPERATION_TYPE ) );
    assertEquals( shellId, operation.getString( OPERATION_TARGET ) );
    JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
    assertEquals( methodName, details.getString( DO_NAME ) );
    JSONArray params = details.getJSONArray( PARAMETER );
    assertEquals( 5, params.getInt( 0 ) );
    assertEquals( "b", params.getString( 1 ) );
    assertFalse( params.getBoolean( 2 ) );
  }

  public void testMessageWithCreate() throws JSONException {
    String displayId = DisplayUtil.getId( shell.getDisplay() );
    String shellId = WidgetUtil.getId( shell );
    String[] styles = new String[] { "TRIM" };
    Object[] parameters = new Object[] { "a", "b" };

    writer.appendCreate( shellId, displayId, "org.Text", styles, parameters );
    writer.writeMessage();
    
    JSONObject message = getMessageJson();
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 0 );
    assertEquals( TYPE_CREATE, operation.getString( OPERATION_TYPE ) );
    assertEquals( shellId, operation.getString( OPERATION_TARGET ) );
    JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
    assertEquals( displayId, details.getString( CREATE_PARENT ) );
    assertEquals( "org.Text", details.getString( CREATE_TYPE ) );
    assertEquals( "TRIM", details.getJSONArray( CREATE_STYLE ).getString( 0 ) );
    assertEquals( "a", details.getJSONArray( PARAMETER ).getString( 0 ) );
    assertEquals( "b", details.getJSONArray( PARAMETER ).getString( 1 ) );
  }
  
  public void testMessageWithMultipleOperations() throws JSONException {
    Button button = new Button( shell, SWT.PUSH );
    String shellId = WidgetUtil.getId( shell );
    String displayId = DisplayUtil.getId( shell.getDisplay() );
    String buttonId = WidgetUtil.getId( button );
    
    writer.appendCreate( shellId, displayId, "org.Text", null, null );
    writer.appendCreate( buttonId, shellId, "org.Shell", null, null );
    writer.writeMessage();
    
    JSONObject message = getMessageJson();
    JSONArray operationsArray = message.getJSONArray( MESSAGE_OPERATIONS );
    assertEquals( 2, operationsArray.length() );
  }

  public void testMessageWithIllegalParameterType() {
    Button wrongParameter = new Button( shell, SWT.PUSH );

    try {
      writer.appendCreate( DisplayUtil.getId( shell.getDisplay() ), 
                                 WidgetUtil.getId( shell ), 
                                 "org.Text",
                                 new String[] { "TRIM" }, 
                                 new Object[] { "a", wrongParameter } );
      fail();
    } catch ( IllegalArgumentException e ) {
    }
  }

  public void testMessageWithDestroy() throws JSONException {
    Button button = new Button( shell, SWT.PUSH );
    String buttonId = WidgetUtil.getId( button );    

    writer.appendDestroy( buttonId );
    writer.writeMessage();

    JSONObject message = getMessageJson();
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 0 );
    assertEquals( TYPE_DESTROY, operation.getString( OPERATION_TYPE ) );
    assertEquals( buttonId, operation.getString( OPERATION_TARGET ) );
    assertEquals( JSONObject.NULL, operation.get( OPERATION_DETAILS ) );
  }
  
  public void testMessageWithDestroyTwice() throws JSONException {
    Button button = new Button( shell, SWT.PUSH );
    String shellId = WidgetUtil.getId( shell );    
    String buttonId = WidgetUtil.getId( button );    
    
    writer.appendDestroy( buttonId );
    writer.appendDestroy( shellId );
    writer.writeMessage();

    JSONObject message = getMessageJson();
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    assertEquals( 2, operations.length() );
    JSONObject operation = operations.getJSONObject( 1 );
    assertEquals( TYPE_DESTROY, operation.getString( OPERATION_TYPE ) );
    assertEquals( shellId, operation.getString( OPERATION_TARGET ) );
  }
  
  public void testMessageWithListen() throws JSONException {
    Button button = new Button( shell, SWT.PUSH );
    String buttonId = WidgetUtil.getId( button );
    
    writer.appendListen( buttonId, "selection", false );
    writer.appendListen( buttonId, "focus", true );
    writer.appendListen( buttonId, "fake", true );
    writer.writeMessage();

    JSONObject message = getMessageJson();
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 0 );
    assertEquals( buttonId, operation.getString( OPERATION_TARGET ) );
    assertEquals( TYPE_LISTEN, operation.getString( OPERATION_TYPE ) );
    JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
    assertFalse( details.getBoolean( "selection" ) );
    assertTrue( details.getBoolean( "focus" ) );
    assertTrue( details.getBoolean( "fake" ) );
  }
  
  public void testMessageWithExecuteScript() throws JSONException {
    Button button = new Button( shell, SWT.PUSH );
    String buttonId = WidgetUtil.getId( button );
    String scriptType = "text/javascript";
    String script = "var c = 4; c++;";

    writer.appendExecuteScript( buttonId, scriptType, script );
    writer.writeMessage();
    
    JSONObject message = getMessageJson();
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 0 );
    assertEquals( TYPE_EXECUTE_SCRIPT, operation.getString( OPERATION_TYPE ) );
    assertEquals( buttonId, operation.getString( OPERATION_TARGET ) );
    JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
    assertEquals( scriptType, details.getString( EXECUTE_SCRIPT_TYPE ) );
    assertEquals( script, details.getString( EXECUTE_SCRIPT_CONTENT ) );
  }
  
  public void testMessageWithExecuteScriptTwice() throws JSONException {
    Button button = new Button( shell, SWT.PUSH );
    String shellId = WidgetUtil.getId( shell );    
    String buttonId = WidgetUtil.getId( button );
    String scriptType = "text/vb";
    String script = "really bad VB;";
    
    writer.appendExecuteScript( buttonId, "text/javascript", "var c = 4; c++;" );
    writer.appendExecuteScript( WidgetUtil.getId( shell ), scriptType, script );
    writer.writeMessage();

    JSONObject message = getMessageJson();
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 1 );
    assertEquals( TYPE_EXECUTE_SCRIPT, operation.getString( OPERATION_TYPE ) );
    assertEquals( shellId, operation.getString( OPERATION_TARGET ) );
    JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
    assertEquals( scriptType, details.getString( EXECUTE_SCRIPT_TYPE ) );
    assertEquals( script, details.getString( EXECUTE_SCRIPT_CONTENT ) );
  }

  public void testMessageWithSet() throws JSONException {
    Button button = new Button( shell, SWT.PUSH );
    String buttonId = WidgetUtil.getId( button );

    writer.appendSet( buttonId, "text", "newText" );
    writer.appendSet( buttonId, "image", "aUrl" );
    writer.appendSet( buttonId, "fake", 1 );
    writer.writeMessage();

    JSONObject message = getMessageJson();
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 0 );
    assertEquals( TYPE_SET, operation.getString( OPERATION_TYPE ) );
    assertEquals( buttonId, operation.getString( OPERATION_TARGET ) );
    JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
    assertEquals( "newText", details.getString( "text" ) );
    assertEquals( "aUrl", details.getString( "image" ) );
    assertEquals( 1, details.getInt( "fake" ) );
  }

  public void testMessageWithSetTwice() throws JSONException {
    Button button = new Button( shell, SWT.PUSH );
    String shellId = WidgetUtil.getId( shell );
    String buttonId = WidgetUtil.getId( button );

    writer.appendSet( shellId, "text", "newText" );
    writer.appendSet( shellId, "image", true );
    writer.appendSet( shellId, "fake", 1 );
    writer.appendSet( buttonId, "text", "newText" );
    writer.appendSet( buttonId, "image", true );
    writer.appendSet( buttonId, "fake", 1 );
    writer.writeMessage();

    JSONObject message = getMessageJson();
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 1 );
    assertEquals( TYPE_SET, operation.getString( OPERATION_TYPE ) );
    assertEquals( buttonId, operation.getString( OPERATION_TARGET ) );
    JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
    assertEquals( "newText", details.getString( "text" ) );
    assertEquals( 1, details.getInt( "fake" ) );
    assertTrue( details.getBoolean( "image" ) );
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

  public void testMessageWithMixedOperations() throws JSONException {
    Button button = new Button( shell, SWT.PUSH );
    createShellOperations( shell );
    createButtonOperations( button );

    writer.writeMessage();

    assertShellCreated();
    checkShellSet();      
    checkShellListen();      
    checkButtonCreate( button );      
    checkButtonExecute( button );    
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
    addButtonDo( button );
  }

  private void addButtonCreate( Button button ) {
    String[] styles = new String[] { "PUSH", "BORDER" };
    Object[] arguments = new Object[] { new Integer( 4 ), new Boolean( true ) };
    writer.appendCreate( WidgetUtil.getId( button ),
                                WidgetUtil.getId( button.getParent() ),
                                button.getClass().getName(), 
                                styles, 
                                arguments );
  }

  private void addButtonDo( Button button ) {
    Object[] arguments = new Object[] { "a1" };
    writer.appendDo( WidgetUtil.getId( button ), "select", arguments );
  }

  private void assertShellCreated() throws JSONException {
    JSONObject message = getMessageJson();
    String shellId = WidgetUtil.getId( shell );
    String displayId = DisplayUtil.getId( shell.getDisplay() );
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 0 );
    assertEquals( shellId, operation.getString( OPERATION_TARGET ) );
    assertEquals( TYPE_CREATE, operation.getString( OPERATION_TYPE ) );
    JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
    assertEquals( displayId, details.getString( CREATE_PARENT ) );
    assertEquals( "SHELL_TRIM", details.getJSONArray( CREATE_STYLE ).getString( 0 ) );
    assertSame( JSONObject.NULL, details.get( PARAMETER ) );
  }

  private void checkShellSet() throws JSONException {
    JSONObject message = getMessageJson();
    String shellId = WidgetUtil.getId( shell );
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 1 );
    assertEquals( shellId, operation.getString( OPERATION_TARGET ) );
    assertEquals( TYPE_SET, operation.getString( OPERATION_TYPE ) );
    JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
    for( int i = 0; i < 5; i++ ) {
      String value = details.getString( "key" + i );
      assertEquals( "value" + i, value );
    }
  }

  private void checkShellListen() throws JSONException {
    JSONObject message = getMessageJson();
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 2 );
    String shellId = WidgetUtil.getId( shell );
    assertEquals( shellId, operation.getString( OPERATION_TARGET ) );
    assertEquals( TYPE_LISTEN, operation.getString( OPERATION_TYPE ) );
    JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
    assertTrue( details.getBoolean( "listener0" ) );
    assertFalse( details.getBoolean( "listener1" ) );
  }

  private void checkButtonCreate( Button button ) throws JSONException {
    JSONObject message = getMessageJson();
    String buttonId = WidgetUtil.getId( button );
    String shellId = WidgetUtil.getId( shell );
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 3 );
    assertEquals( buttonId, operation.getString( OPERATION_TARGET ) );
    String type = operation.getString( OPERATION_TYPE );
    assertEquals( TYPE_CREATE, type );
    JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
    assertEquals( shellId, details.getString( CREATE_PARENT ) );
    JSONArray params = details.getJSONArray( PARAMETER );
    assertEquals( 4, params.getInt( 0 ) );
    assertTrue( params.getBoolean( 1 ) );
    type = details.getString( CREATE_TYPE );
    assertEquals( button.getClass().getName(), type );
    JSONArray styles = details.getJSONArray( CREATE_STYLE );
    assertEquals( "PUSH", styles.getString( 0 ) );
    assertEquals( "BORDER", styles.getString( 1 ) );
  }

  private void checkButtonExecute( Widget button ) throws JSONException {
    JSONObject message = getMessageJson();
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 4 );
    String buttonId = WidgetUtil.getId( button );
    assertEquals( buttonId, operation.getString( OPERATION_TARGET ) );
    assertEquals( TYPE_DO, operation.getString( OPERATION_TYPE ) );
    JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
    assertEquals( "select", details.getString( DO_NAME ) );
    assertEquals( "a1", details.getJSONArray( PARAMETER ).getString( 0 ) );
  }

  public void testAppendsToExistingOperation() throws JSONException {
    Button button = new Button( shell, SWT.PUSH );
    String shellId = WidgetUtil.getId( shell );
    String buttonId = WidgetUtil.getId( button );
    String[] styles = new String[] { "SYSTEM_MODAL" };

    writer.appendCreate( shellId, "parentId", "foo.Class", styles, null );
    writer.appendSet( shellId, "key", "value" );
    writer.appendSet( shellId, "key2", "value2" );
    writer.appendSet( shellId, "key3", "value3" );
    writer.appendSet( buttonId, "key", "value" );
    writer.writeMessage();
    
    JSONObject message = getMessageJson();
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    assertFirstOperation( operations );
    assertSecondOperation( operations );
    assertThirdOperation( buttonId, operations );
  }

  private void assertFirstOperation( JSONArray operations ) throws JSONException {
    JSONObject operation = operations.getJSONObject( 0 );
    JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
    assertEquals( "parentId", details.getString( CREATE_PARENT ) );
  }

  private void assertSecondOperation( JSONArray operations ) throws JSONException {
    JSONObject details;
    JSONObject operation2 = operations.getJSONObject( 1 );
    details = operation2.getJSONObject( OPERATION_DETAILS );
    assertEquals( "value", details.getString( "key" ) );
    assertEquals( "value2", details.getString( "key2" ) );
    assertEquals( "value3", details.getString( "key3" ) );
  }

  private void assertThirdOperation( String buttonId, JSONArray operations ) throws JSONException {
    JSONObject details;
    JSONObject operation3 = operations.getJSONObject( 2 );
    details = operation3.getJSONObject( OPERATION_DETAILS );
    String actualId = operation3.getString( OPERATION_TARGET );
    assertEquals( buttonId, actualId );
    assertEquals( "value", details.getString( "key" ) );
  }

  public void testStartsNewOperation() throws JSONException {
    String shellId = WidgetUtil.getId( shell );
    Button button = new Button( shell, SWT.PUSH );
    String buttonId = WidgetUtil.getId( button );
    String type = button.getClass().getName();
    String[] styles = new String[] { "PUSH" };

    writer.appendCreate( shellId, "parentId", "foo.Class", styles, null );
    writer.appendCreate( buttonId, shellId, type, styles, null );
    writer.appendSet( buttonId, "key", "value" );
    writer.appendSet( buttonId, "key2", "value" );
    writer.writeMessage();
    
    JSONObject message = getMessageJson();
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 1 );
    JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
    assertEquals( shellId, details.getString( CREATE_PARENT ) );
    JSONObject operation2 = operations.getJSONObject( 2 );
    JSONObject details2 = operation2.getJSONObject( OPERATION_DETAILS );
    assertEquals( "value", details2.getString( "key" ) );
    assertEquals( "value", details2.getString( "key2" ) );
  }
  
  public void testAppendArrayParameter() throws JSONException {
    String shellId = WidgetUtil.getId( shell );
    Integer[] arrayParameter = new Integer[] { new Integer( 1 ), new Integer( 2 ) };

    writer.appendSet( shellId, "key", arrayParameter );
    writer.writeMessage();
    
    JSONObject message = getMessageJson();
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 0 );
    JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
    assertEquals( 1, details.getJSONArray( "key" ).getInt( 0 ) );
    assertEquals( 2, details.getJSONArray( "key" ).getInt( 1 ) );
  }

  public void testAppendEmptyArrayParameter() throws JSONException {
    String shellId = WidgetUtil.getId( shell );
    Object[] emptyArray = new Object[ 0 ];
    
    writer.appendSet( shellId, "key", emptyArray );
    writer.writeMessage();

    JSONObject message = getMessageJson();
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 0 );
    JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
    JSONArray value = details.getJSONArray( "key" );
    assertEquals( 0, value.length() );
  }

  public void testAppendMixedArrayParameter() throws JSONException {
    String shellId = WidgetUtil.getId( shell );
    Object[] mixedArray = new Object[] { new Integer( 23 ), "Hello" };
    
    writer.appendSet( shellId, "key", mixedArray );
    writer.writeMessage();

    JSONObject message = getMessageJson();
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 0 );
    JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
    JSONArray value = details.getJSONArray( "key" );
    assertEquals( 2, value.length() );
    assertEquals( "Hello", value.getString( 1 ) );
  }
  
  private JSONObject getMessageJson() throws JSONException {
    String actual = stringWriter.getBuffer().toString();
    return new JSONObject( actual );
  }
}
