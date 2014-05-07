/*******************************************************************************
 * Copyright (c) 2008, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.swt.internal.browser.browserkit;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.testfixture.TestMessage.getParent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.protocol.Operation.CallOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestMessage;
import org.eclipse.rap.rwt.widgets.BrowserCallback;
import org.eclipse.rap.rwt.widgets.BrowserUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.internal.widgets.IBrowserAdapter;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class BrowserLCA_Test {

  private Display display;
  private Shell shell;
  private Browser browser;
  private BrowserLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    browser = new Browser( shell, SWT.NONE );
    lca = new BrowserLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testControlListeners() throws IOException {
    ControlLCATestUtil.testActivateListener( browser );
    ControlLCATestUtil.testFocusListener( browser );
    ControlLCATestUtil.testMouseListener( browser );
    ControlLCATestUtil.testKeyListener( browser );
    ControlLCATestUtil.testTraverseListener( browser );
    ControlLCATestUtil.testMenuDetectListener( browser );
    ControlLCATestUtil.testHelpListener( browser );
  }

  @Test
  public void testTextChanged() throws IOException {
    Fixture.markInitialized( display );

    assertTrue( BrowserLCA.hasUrlChanged( browser ) );
    String expected = String.valueOf( BrowserLCA.BLANK_HTML.hashCode() );
    assertTrue( BrowserLCA.getUrl( browser ).contains( expected ) );

    Fixture.markInitialized( browser );
    Fixture.preserveWidgets();
    assertFalse( BrowserLCA.hasUrlChanged( browser ) );

    browser = new Browser( shell, SWT.NONE );
    browser.setText( "Hello" );
    assertTrue( BrowserLCA.hasUrlChanged( browser ) );
    expected = String.valueOf( "Hello".hashCode() );
    assertTrue( BrowserLCA.getUrl( browser ).contains( expected ) );

    Fixture.markInitialized( browser );
    Fixture.preserveWidgets();
    browser.setText( "GoodBye" );
    assertTrue( BrowserLCA.hasUrlChanged( browser ) );
    expected = String.valueOf( "GoodBye".hashCode() );
    assertTrue( BrowserLCA.getUrl( browser ).contains( expected ) );
    Fixture.preserveWidgets();
    browser.setText( "GoodBye" );
    assertTrue( BrowserLCA.hasUrlChanged( browser ) );
    expected = String.valueOf( "GoodBye".hashCode() );
    assertTrue( BrowserLCA.getUrl( browser ).contains( expected ) );

    browser = new Browser( shell, SWT.NONE );
    browser.setText( "" );
    assertTrue( BrowserLCA.hasUrlChanged( browser ) );
    expected = String.valueOf( BrowserLCA.BLANK_HTML.hashCode() );
    assertTrue( BrowserLCA.getUrl( browser ).contains( expected ) );
  }

  @Test
  public void testUrlChanged() throws IOException {
    Fixture.markInitialized( display );

    assertTrue( BrowserLCA.hasUrlChanged( browser ) );
    String expected = String.valueOf( BrowserLCA.BLANK_HTML.hashCode() );
    assertTrue( BrowserLCA.getUrl( browser ).contains( expected ) );

    Fixture.markInitialized( browser );
    Fixture.preserveWidgets();
    assertFalse( BrowserLCA.hasUrlChanged( browser ) );

    browser = new Browser( shell, SWT.NONE );
    browser.setUrl( "http://eclipse.org/rap" );
    assertTrue( BrowserLCA.hasUrlChanged( browser ) );
    assertEquals( "http://eclipse.org/rap", BrowserLCA.getUrl( browser ) );

    Fixture.markInitialized( browser );
    Fixture.preserveWidgets();
    browser.setUrl( "http://eclipse.org/rip" );
    assertTrue( BrowserLCA.hasUrlChanged( browser ) );
    assertEquals( "http://eclipse.org/rip", BrowserLCA.getUrl( browser ) );
    Fixture.preserveWidgets();
    browser.setUrl( "http://eclipse.org/rip" );
    assertTrue( BrowserLCA.hasUrlChanged( browser ) );
    assertEquals( "http://eclipse.org/rip", BrowserLCA.getUrl( browser ) );
  }

  @Test
  public void testResetUrlChanged_NotInitialized() throws IOException {
    Fixture.markInitialized( display );
    browser.setUrl( "http://eclipse.org/rap" );
    Fixture.fakeResponseWriter();

    BrowserLCA lca = new BrowserLCA();
    lca.renderChanges( browser );

    assertFalse( getAdapter( browser).hasUrlChanged() );
  }

  @Test
  public void testResetUrlChanged_Initialized() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( browser );
    browser.setUrl( "http://eclipse.org/rap" );
    Fixture.fakeResponseWriter();

    BrowserLCA lca = new BrowserLCA();
    lca.renderChanges( browser );

    assertFalse( getAdapter( browser).hasUrlChanged() );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( browser );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( browser );
    assertEquals( "rwt.widgets.Browser", operation.getType() );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( browser );
    lca.renderInitialization( browser );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof BrowserOperationHandler );
  }

  @Test
  public void testReadData_usesOperationHandler() {
    BrowserOperationHandler handler = spy( new BrowserOperationHandler( browser ) );
    getRemoteObject( getId( browser ) ).setHandler( handler );

    Fixture.fakeNotifyOperation( getId( browser ), "Help", new JsonObject() );
    lca.readData( browser );

    verify( handler ).handleNotifyHelp( browser, new JsonObject() );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( browser );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( browser );
    assertEquals( getId( browser.getParent() ), getParent( operation ) );
  }

  @Test
  public void testRenderAddSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( browser );
    Fixture.preserveWidgets();

    browser.addProgressListener( mock( ProgressListener.class ) );
    lca.renderChanges( browser );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( browser, "Progress" ) );
  }

  @Test
  public void testRenderRemoveSelectionListener() throws Exception {
    ProgressListener listener = mock( ProgressListener.class );
    browser.addProgressListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( browser );
    Fixture.preserveWidgets();

    browser.removeProgressListener( listener );
    lca.renderChanges( browser );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( browser, "Progress" ) );
  }

  @Test
  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( browser );
    Fixture.preserveWidgets();

    browser.addProgressListener( mock( ProgressListener.class ) );
    Fixture.preserveWidgets();
    lca.renderChanges( browser );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( browser, "progress" ) );
  }

  @Test
  public void testRenderInitialUrl() throws IOException {
    lca.render( browser );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( browser );
    assertTrue( operation.getProperties().names().contains( "url" ) );
  }

  @Test
  public void testRenderUrl() throws IOException {
    browser.setUrl( "http://eclipse.org/rap" );
    lca.renderChanges( browser );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "http://eclipse.org/rap", message.findSetProperty( browser, "url" ).asString() );
  }

  @Test
  public void testRenderUrlUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( browser );

    browser.setUrl( "http://eclipse.org/rap" );
    lca.renderChanges( browser );
    Fixture.fakeNewRequest();
    lca.renderChanges( browser );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( browser, "url" ) );
  }

  @Test
  public void testCallEvaluate() {
    BrowserCallback browserCallback = new BrowserCallback() {
      public void evaluationSucceeded( Object result ) {
      }
      public void evaluationFailed( Exception exception ) {
      }
    };
    Fixture.markInitialized( display );
    Fixture.markInitialized( browser );

    BrowserUtil.evaluate( browser, "alert('33');", browserCallback );
    Fixture.executeLifeCycleFromServerThread();

    TestMessage message = Fixture.getProtocolMessage();
    CallOperation callOperation = message.findCallOperation( browser, "evaluate" );
    assertEquals( "(function(){alert('33');})();", callOperation.getParameters().get( "script" ).asString() );
  }

  @Test
  public void testCallCreateFunctions() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( browser );

    new BrowserFunction( browser, "func1" );
    new BrowserFunction( browser, "func2" );
    lca.renderChanges( browser );

    TestMessage message = Fixture.getProtocolMessage();
    CallOperation callOperation = message.findCallOperation( browser, "createFunctions" );
    JsonArray expected = new JsonArray().add( "func1" ).add( "func2" );
    assertEquals( expected, callOperation.getParameters().get( "functions" ) );
  }

  @Test
  public void testCallDestroyFunctions() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( browser );

    BrowserFunction function = new BrowserFunction( browser, "func1" );
    function.dispose();
    lca.renderChanges( browser );

    TestMessage message = Fixture.getProtocolMessage();
    CallOperation callOperation = message.findCallOperation( browser, "destroyFunctions" );
    JsonArray expected = new JsonArray().add( "func1" );
    assertEquals( expected, callOperation.getParameters().get( "functions" ) );
  }

  @Test
  public void testRenderFunctionResult() throws IOException {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    BrowserOperationHandler handler = new BrowserOperationHandler( browser );
    new BrowserFunction( browser, "func" ) {
      @Override
      public Object function( Object[] arguments ) {
        return new Object[]{
          Short.valueOf( ( short )3 ),
          Boolean.TRUE,
          null,
          new Object[] { "a string", Boolean.FALSE },
          "hi",
          Float.valueOf( 0.6666667f ),
          Long.valueOf( 12l )
        };
      }
    };
    JsonObject parameters = new JsonObject()
      .add( "name", "func" )
      .add( "arguments", new JsonArray().add( "eclipse" ).add( 3.6 ) );
    Fixture.fakeCallOperation( getId( browser ), "executeFunction", parameters );

    handler.handleCall( "executeFunction", parameters );
    lca.renderChanges( browser );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expectedArgs = new JsonArray()
      .add( 3 )
      .add( true )
      .add( JsonValue.NULL )
      .add( new JsonArray().add( "a string" ).add( false ) )
      .add( "hi" )
      .add( Float.valueOf( 0.6666667f ).doubleValue() )
      .add( 12l );
    JsonArray expected = new JsonArray()
      .add( "func" )
      .add( expectedArgs )
      .add( JsonValue.NULL );
    assertEquals( expected, message.findSetProperty( browser, "functionResult" ) );
  }

  @Test
  public void testRenderFunctionResult_afterException() throws IOException {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    BrowserOperationHandler handler = new BrowserOperationHandler( browser );
    new BrowserFunction( browser, "func" ) {
      @Override
      public Object function( Object[] arguments ) {
        throw new RuntimeException( "exception" );
      }
    };
    JsonObject parameters = new JsonObject()
      .add( "name", "func" )
      .add( "arguments", new JsonArray().add( "eclipse" ).add( 3.6 ) );

    handler.handleCall( "executeFunction", parameters );
    lca.renderChanges( browser );

    TestMessage message = Fixture.getProtocolMessage();
    JsonArray expected = new JsonArray()
      .add( "func" )
      .add( JsonValue.NULL )
      .add( "exception" );
    assertEquals( expected, message.findSetProperty( browser, "functionResult" ) );
  }

  private static IBrowserAdapter getAdapter( Browser browser ) {
    return browser.getAdapter( IBrowserAdapter.class );
  }

}
