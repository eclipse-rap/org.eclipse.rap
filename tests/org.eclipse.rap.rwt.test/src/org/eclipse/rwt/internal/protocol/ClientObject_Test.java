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

import static org.eclipse.rwt.internal.resources.TestUtil.assertArrayEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.TestResponse;
import org.eclipse.rwt.internal.lifecycle.JavaScriptResponseWriter;
import org.eclipse.rwt.internal.protocol.Message.CallOperation;
import org.eclipse.rwt.internal.protocol.Message.CreateOperation;
import org.eclipse.rwt.internal.protocol.Message.DestroyOperation;
import org.eclipse.rwt.internal.protocol.Message.ExecuteScriptOperation;
import org.eclipse.rwt.internal.protocol.Message.ListenOperation;
import org.eclipse.rwt.internal.protocol.Message.SetOperation;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


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

  public void testCreateWithNullParams() {
    clientObject.create( null );

    CreateOperation operation = ( CreateOperation )getMessage().getOperation( 0 );
    assertEquals( WidgetUtil.getId( shell ), operation.getTarget() );
    assertEquals( shell.getClass().getName(), operation.getType() );
  }

  public void testClientWithParams() {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "key1", new Integer( 1 ) );
    properties.put( "key2", Boolean.TRUE );

    clientObject.create( properties );

    CreateOperation operation = ( CreateOperation )getMessage().getOperation( 0 );
    assertEquals( WidgetUtil.getId( shell ), operation.getTarget() );
    assertEquals( shell.getClass().getName(), operation.getType() );
    assertEquals( new Integer( 1 ), operation.getProperty( "key1" ) );
    assertEquals( Boolean.TRUE, operation.getProperty( "key2" ) );
  }

  public void testCreateStyles() {
    Button button = new Button( shell, SWT.PUSH | SWT.BORDER );
    IClientObject buttonObject = ClientObjectFactory.getForWidget( button );
    Map<String, Object> properties = new HashMap<String, Object>();
    String[] styles = new String[] { "PUSH", "BORDER" };
    properties.put( ProtocolConstants.CREATE_STYLE, styles );

    buttonObject.create( properties );

    CreateOperation operation = ( CreateOperation )getMessage().getOperation( 0 );
    assertEquals( WidgetUtil.getId( button ), operation.getTarget() );
    assertEquals( WidgetUtil.getId( shell ), operation.getParent() );
    assertArrayEquals( styles, operation.getStyles() );
  }

  public void testSetProperty() {
    clientObject.setProperty( "key", ( Object )"value" );
    clientObject.setProperty( "key2", 2 );
    clientObject.setProperty( "key3", 3.5 );
    clientObject.setProperty( "key4", true );
    clientObject.setProperty( "key5", "aString" );

    SetOperation operation = ( SetOperation )getMessage().getOperation( 0 );
    assertEquals( WidgetUtil.getId( shell ), operation.getTarget() );
    assertEquals( "value", operation.getProperty( "key" ) );
    assertEquals( new Integer( 2 ), operation.getProperty( "key2" ) );
    assertEquals( new Double( 3.5 ), operation.getProperty( "key3" ) );
    assertEquals( Boolean.TRUE, operation.getProperty( "key4" ) );
    assertEquals( "aString", operation.getProperty( "key5" ) );
  }

  public void testDestroy() {
    clientObject.destroy();

    DestroyOperation operation = ( DestroyOperation )getMessage().getOperation( 0 );
    assertEquals( WidgetUtil.getId( shell ), operation.getTarget() );
  }

  public void testAddListener() {
    clientObject.addListener( "selection" );
    clientObject.addListener( "fake" );

    ListenOperation operation = ( ListenOperation )getMessage().getOperation( 0 );
    assertEquals( WidgetUtil.getId( shell ), operation.getTarget() );
    assertTrue( operation.listensTo( "selection" ) );
    assertTrue( operation.listensTo( "fake" ) );
  }

  public void testRemoveListener() {
    clientObject.removeListener( "selection" );
    clientObject.removeListener( "fake" );
    clientObject.addListener( "fake2" );

    ListenOperation operation = ( ListenOperation )getMessage().getOperation( 0 );
    assertEquals( WidgetUtil.getId( shell ), operation.getTarget() );
    assertFalse( operation.listensTo( "selection" ) );
    assertFalse( operation.listensTo( "fake" ) );
    assertTrue( operation.listensTo( "fake2" ) );
  }

  public void testCall() {
    clientObject.call( "method", null );

    CallOperation operation = ( CallOperation )getMessage().getOperation( 0 );
    assertEquals( WidgetUtil.getId( shell ), operation.getTarget() );
    assertEquals( "method", operation.getMethodName() );
  }

  public void testCallTwice() {
    clientObject.call( "method", null );
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "key1", "a" );
    properties.put( "key2", new Integer( 3 ) );

    clientObject.call( "method2", properties );

    CallOperation operation = ( CallOperation )getMessage().getOperation( 1 );
    assertEquals( WidgetUtil.getId( shell ), operation.getTarget() );
    assertEquals( "method2", operation.getMethodName() );
    assertEquals( "a", operation.getProperty( "key1" ) );
    assertEquals( new Integer( 3 ), operation.getProperty( "key2" ) );
  }

  public void testExecuteScript() {
    clientObject.executeScript( "text/javascript", "var x = 5;" );

    ExecuteScriptOperation operation = ( ExecuteScriptOperation )getMessage().getOperation( 0 );
    assertEquals( WidgetUtil.getId( shell ), operation.getTarget() );
    assertEquals( "text/javascript", operation.getScriptType() );
    assertEquals( "var x = 5;", operation.getScript() );
  }
  
  public void testDoeNotCashProtocolWriter() throws IOException {
    // See bug 352738
    TestResponse response = new TestResponse();
    JavaScriptResponseWriter writer = new JavaScriptResponseWriter( response );
    ContextProvider.getStateInfo().setResponseWriter( writer );
    IClientObject clientObject = ClientObjectFactory.getForWidget( shell );
    
    clientObject.create( null );
    writer.write( "var x =5;" );
    clientObject.setProperty( "key", "value" );
    writer.finish();
    
    String message = response.getContent();
    assertTrue( message.contains( ProtocolConstants.ACTION_CREATE ) );
    assertTrue( message.contains( ProtocolConstants.ACTION_SET ) );
  }

  private Message getMessage() {
    closeProtocolWriter();
    String markup = Fixture.getAllMarkup();
    if( !markup.contains( JavaScriptResponseWriter.PROCESS_MESSAGE ) ) {
      throw new RuntimeException( "Seems that message is not wrapped anymore - cleanup NOW!" );
    }
    return new Message( markup );
  }

  private void closeProtocolWriter() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    JavaScriptResponseWriter writer = stateInfo.getResponseWriter();
    writer.finish();
  }
}
