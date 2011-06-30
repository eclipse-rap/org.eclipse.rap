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

import static org.eclipse.rwt.internal.protocol.ProtocolConstants.CREATE_PARENT;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.CREATE_STYLE;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.CREATE_TYPE;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.DO_NAME;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.EXECUTE_SCRIPT_CONTENT;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.EXECUTE_SCRIPT_TYPE;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.MESSAGE_OPERATIONS;
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
import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.JavaScriptResponseWriter;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.json.*;


public class ClientObject_Test extends TestCase {

  private Shell shell;
  private IClientObject clientObject;

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
    Display display = new Display();
    shell = new Shell( display );
    clientObject = ClientObjectFactory.getForWidget( shell );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testCreateWithNullParams() throws JSONException {
    clientObject.create( new String[] { "SHELL_TRIM" } );

    JSONObject message = getMessage();
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 0 );
    assertEquals( WidgetUtil.getId( shell ), operation.getString( OPERATION_TARGET ) );
    assertEquals( TYPE_CREATE, operation.getString( OPERATION_TYPE ) );
    JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
    assertEquals( shell.getClass().getName(), details.getString( CREATE_TYPE ) );
  }

  public void testClientWithParams() throws JSONException {
    Object[] parameters = new Object[] { new Integer( 1 ), new Boolean( true ) };

    clientObject.create( new String[] { "SHELL_TRIM" }, parameters );

    JSONObject message = getMessage();
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 0 );
    assertEquals( WidgetUtil.getId( shell ), operation.getString( OPERATION_TARGET ) );
    assertEquals( TYPE_CREATE, operation.getString( OPERATION_TYPE ) );
    JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
    assertEquals( shell.getClass().getName(), details.getString( CREATE_TYPE ) );
    JSONArray params = details.getJSONArray( PARAMETER );
    assertEquals( 1, params.getInt( 0 ) );
    assertEquals( true, params.getBoolean( 1 ) );
  }

  public void testCreateStyles() throws JSONException {
    Button button = new Button( shell, SWT.PUSH | SWT.BORDER );
    IClientObject buttonObject = ClientObjectFactory.getForWidget( button );

    buttonObject.create( new String[] { "PUSH", "BORDER" } );

    JSONObject message = getMessage();
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 0 );
    assertEquals( WidgetUtil.getId( button ), operation.getString( OPERATION_TARGET ) );
    assertEquals( TYPE_CREATE, operation.getString( OPERATION_TYPE ) );
    JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
    assertEquals( WidgetUtil.getId( shell ), details.getString( CREATE_PARENT ) );
    assertEquals( button.getClass().getName(), details.getString( CREATE_TYPE ) );
    JSONArray styles = details.getJSONArray( CREATE_STYLE );
    assertEquals( "PUSH", styles.getString( 0 ) );
    assertEquals( "BORDER", styles.getString( 1 ) );
  }

  public void testSetProperty() throws JSONException {
    clientObject.setProperty( "key", ( Object )"value" );
    clientObject.setProperty( "key2", 2 );
    clientObject.setProperty( "key3", 3.5 );
    clientObject.setProperty( "key4", true );
    clientObject.setProperty( "key5", "aString" );

    JSONObject message = getMessage();
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 0 );
    assertEquals( WidgetUtil.getId( shell ), operation.getString( OPERATION_TARGET ) );
    assertEquals( TYPE_SET, operation.getString( OPERATION_TYPE ) );
    JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
    assertEquals( "value", details.getString( "key" ) );
    assertEquals( 2, details.getInt( "key2" ) );
    assertEquals( 3.5, details.getDouble( "key3" ), 0.0 );
    assertEquals( true, details.getBoolean( "key4" ) );
    assertEquals( "aString", details.getString( "key5" ) );
  }

  public void testDestroy() throws JSONException {
    clientObject.destroy();

    JSONObject message = getMessage();
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 0 );
    assertEquals( WidgetUtil.getId( shell ), operation.getString( OPERATION_TARGET ) );
    assertEquals( TYPE_DESTROY, operation.getString( OPERATION_TYPE ) );
    Object details = operation.get( OPERATION_DETAILS );
    assertSame( JSONObject.NULL, details );
  }

  public void testAddListener() throws JSONException {
    clientObject.addListener( "selection" );
    clientObject.addListener( "fake" );

    JSONObject message = getMessage();
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 0 );
    assertEquals( WidgetUtil.getId( shell ), operation.getString( OPERATION_TARGET ) );
    assertEquals( TYPE_LISTEN, operation.getString( OPERATION_TYPE ) );
    JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
    assertTrue( details.getBoolean( "selection" ) );
    assertTrue( details.getBoolean( "fake" ) );
  }

  public void testRemoveListener() throws JSONException {
    clientObject.removeListener( "selection" );
    clientObject.removeListener( "fake" );
    clientObject.addListener( "fake2" );

    JSONObject message = getMessage();
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 0 );
    assertEquals( WidgetUtil.getId( shell ), operation.getString( OPERATION_TARGET ) );
    assertEquals( TYPE_LISTEN, operation.getString( OPERATION_TYPE ) );
    JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
    assertFalse( details.getBoolean( "selection" ) );
    assertFalse( details.getBoolean( "fake" ) );
    assertTrue( details.getBoolean( "fake2" ) );
  }

  public void testCall() throws JSONException {
    clientObject.call( "method" );

    JSONObject message = getMessage();
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 0 );
    assertEquals( WidgetUtil.getId( shell ), operation.getString( OPERATION_TARGET ) );
    String actualOperationType = operation.getString( OPERATION_TYPE );
    assertEquals( TYPE_DO, actualOperationType );
    JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
    String method = details.getString( DO_NAME );
    assertEquals( "method", method );
    Object params = details.get( PARAMETER );
    assertSame( JSONObject.NULL, params );
  }

  public void testCallTwice() throws JSONException {
    clientObject.call( "method" );
    clientObject.call( "method2", new Object[] { "a", new Integer( 3 ) } );

    JSONObject message = getMessage();
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 1 );
    assertEquals( WidgetUtil.getId( shell ), operation.getString( OPERATION_TARGET ) );
    assertEquals( TYPE_DO, operation.getString( OPERATION_TYPE ) );
    JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
    assertEquals( "method2", details.getString( DO_NAME ) );
    JSONArray list = details.getJSONArray( PARAMETER );
    assertEquals( "a", list.getString( 0 ) );
    assertEquals( 3, list.getInt( 1 ) );
  }

  public void testExecuteScript() throws JSONException {
    clientObject.executeScript( "text/javascript", "var x = 5;" );

    JSONObject message = getMessage();
    JSONArray operations = message.getJSONArray( MESSAGE_OPERATIONS );
    JSONObject operation = operations.getJSONObject( 0 );
    assertEquals( WidgetUtil.getId( shell ), operation.getString( OPERATION_TARGET ) );
    assertEquals( TYPE_EXECUTE_SCRIPT, operation.getString( OPERATION_TYPE ) );
    JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
    assertEquals( details.getString( EXECUTE_SCRIPT_TYPE ), "text/javascript" );
    assertEquals( "var x = 5;", details.getString( EXECUTE_SCRIPT_CONTENT ) );
  }

  private JSONObject getMessage() throws JSONException {
    closeProtocolWriter();
    String markup = Fixture.getAllMarkup();
    if( !markup.contains( JavaScriptResponseWriter.PROCESS_MESSAGE ) ) {
      throw new RuntimeException( "Seems that message is not wrapped anymore - cleanup NOW!" );
    }
    markup = markup.replaceAll( "^" + JavaScriptResponseWriter.PROCESS_MESSAGE + "\\(", "" );
    markup = markup.replaceAll( "\\);$", "" );
    return new JSONObject( markup );
  }

  private void closeProtocolWriter() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    JavaScriptResponseWriter writer = stateInfo.getResponseWriter();
    writer.finish();
  }

}
