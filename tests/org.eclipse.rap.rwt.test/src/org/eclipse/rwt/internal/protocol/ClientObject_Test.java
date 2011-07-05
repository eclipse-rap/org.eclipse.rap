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

import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.JavaScriptResponseWriter;
import org.eclipse.rwt.internal.protocol.util.*;
import org.eclipse.rwt.internal.protocol.util.Message.CreateOperation;
import org.eclipse.rwt.internal.protocol.util.Message.DestroyOperation;
import org.eclipse.rwt.internal.protocol.util.Message.DoOperation;
import org.eclipse.rwt.internal.protocol.util.Message.ExecuteScriptOperation;
import org.eclipse.rwt.internal.protocol.util.Message.ListenOperation;
import org.eclipse.rwt.internal.protocol.util.Message.SetOperation;
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
    clientObject.create( new String[] { "SHELL_TRIM" } );

    CreateOperation operation = ( CreateOperation )getMessage().getOperation( 0 );
    assertEquals( WidgetUtil.getId( shell ), operation.getTarget() );
    assertEquals( shell.getClass().getName(), operation.getType() );
  }

  public void testClientWithParams() {
    Object[] parameters = new Object[] { new Integer( 1 ), new Boolean( true ) };

    clientObject.create( new String[] { "SHELL_TRIM" }, parameters );

    CreateOperation operation = ( CreateOperation )getMessage().getOperation( 0 );
    assertEquals( WidgetUtil.getId( shell ), operation.getTarget() );
    assertEquals( shell.getClass().getName(), operation.getType() );
    assertArrayEquals( parameters, operation.getParameters() );
  }

  public void testCreateStyles() {
    Button button = new Button( shell, SWT.PUSH | SWT.BORDER );
    IClientObject buttonObject = ClientObjectFactory.getForWidget( button );
    String[] styles = new String[] { "PUSH", "BORDER" };

    buttonObject.create( styles );

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
    clientObject.call( "method" );

    DoOperation operation = ( DoOperation )getMessage().getOperation( 0 );
    assertEquals( WidgetUtil.getId( shell ), operation.getTarget() );
    assertEquals( "method", operation.getName() );
    assertNull( operation.getParameters() );
  }

  public void testCallTwice() {
    clientObject.call( "method" );
    Object[] parameters = new Object[] { "a", new Integer( 3 ) };
    clientObject.call( "method2", parameters );

    DoOperation operation = ( DoOperation )getMessage().getOperation( 1 );
    assertEquals( WidgetUtil.getId( shell ), operation.getTarget() );
    assertEquals( "method2", operation.getName() );
    assertArrayEquals( parameters, operation.getParameters() );
  }

  public void testExecuteScript() {
    clientObject.executeScript( "text/javascript", "var x = 5;" );

    ExecuteScriptOperation operation = ( ExecuteScriptOperation )getMessage().getOperation( 0 );
    assertEquals( WidgetUtil.getId( shell ), operation.getTarget() );
    assertEquals( "text/javascript", operation.getScriptType() );
    assertEquals( "var x = 5;", operation.getScript() );
  }

  // TODO: Move to Fixture
  private Message getMessage() {
    closeProtocolWriter();
    String markup = Fixture.getAllMarkup();
    if( !markup.contains( JavaScriptResponseWriter.PROCESS_MESSAGE ) ) {
      throw new RuntimeException( "Seems that message is not wrapped anymore - cleanup NOW!" );
    }
    markup = markup.replaceAll( "^" + JavaScriptResponseWriter.PROCESS_MESSAGE + "\\(", "" );
    markup = markup.replaceAll( "\\);$", "" );
    return new Message( markup );
  }

  private void closeProtocolWriter() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    JavaScriptResponseWriter writer = stateInfo.getResponseWriter();
    writer.finish();
  }
  
  // TODO: Move to Fixture
  private static void assertArrayEquals( Object[] expected, Object[] actual ) {
    if( !Arrays.equals( expected, actual ) ) {
      fail( "Expected:\n" + expected + "\n but was:\n" + actual );
    }
  }
}
