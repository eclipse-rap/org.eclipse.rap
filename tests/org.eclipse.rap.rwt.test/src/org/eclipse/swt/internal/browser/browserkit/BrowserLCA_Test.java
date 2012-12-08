/*******************************************************************************
 * Copyright (c) 2008, 2012 Innoopract Informationssysteme GmbH and others.
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

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.testfixture.Fixture.fakeNewRequest;
import static org.eclipse.rap.rwt.testfixture.Fixture.fakeSetParameter;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.widgets.BrowserCallback;
import org.eclipse.rap.rwt.widgets.BrowserUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.internal.widgets.IBrowserAdapter;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class BrowserLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private Browser browser;
  private BrowserLCA lca;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    browser = new Browser( shell, SWT.NONE );
    lca = new BrowserLCA();
    Fixture.fakeNewRequest( display );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testControlListeners() throws IOException {
    ControlLCATestUtil.testActivateListener( browser );
    ControlLCATestUtil.testFocusListener( browser );
    ControlLCATestUtil.testMouseListener( browser );
    ControlLCATestUtil.testKeyListener( browser );
    ControlLCATestUtil.testTraverseListener( browser );
    ControlLCATestUtil.testMenuDetectListener( browser );
    ControlLCATestUtil.testHelpListener( browser );
  }

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

  public void testResetUrlChanged_NotInitialized() throws IOException {
    Fixture.markInitialized( display );
    browser.setUrl( "http://eclipse.org/rap" );
    Fixture.fakeResponseWriter();

    BrowserLCA lca = new BrowserLCA();
    lca.renderChanges( browser );

    assertFalse( getAdapter( browser).hasUrlChanged() );
  }

  public void testResetUrlChanged_Initialized() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( browser );
    browser.setUrl( "http://eclipse.org/rap" );
    Fixture.fakeResponseWriter();

    BrowserLCA lca = new BrowserLCA();
    lca.renderChanges( browser );

    assertFalse( getAdapter( browser).hasUrlChanged() );
  }

  public void testExecuteFunction() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    final List<Object> log = new ArrayList<Object>();
    new BrowserFunction( browser, "func" ) {
      @Override
      public Object function( Object[] arguments ) {
        log.addAll( Arrays.asList( arguments ) );
        return new Object[ 0 ];
      }
    };
    fakeNewRequest( display );
    fakeSetParameter( getId( browser ), BrowserLCA.PARAM_EXECUTE_FUNCTION, "func" );
    Object[] args = new Object[] { "eclipse", Double.valueOf( 3.6 )};
    fakeSetParameter( getId( browser ), BrowserLCA.PARAM_EXECUTE_ARGUMENTS, args );

    Fixture.readDataAndProcessAction( browser );

    Object[] expected = new Object[] { "eclipse", Double.valueOf( 3.6 ) };
    assertTrue( Arrays.equals( expected, log.toArray() ) );
  }

  public void testProgressEvent() {
    final ArrayList<String> log = new ArrayList<String>();
    Fixture.markInitialized( display );
    browser.addProgressListener( new ProgressListener() {
      public void changed( ProgressEvent event ) {
        log.add( "changed" );
      }
      public void completed( ProgressEvent event ) {
        log.add( "completed" );
      }
    } );

    Fixture.fakeNotifyOperation( getId( browser ), BrowserLCA.EVENT_PROGRESS, null );
    Fixture.readDataAndProcessAction( browser );

    assertEquals( 2, log.size() );
    assertEquals( "changed", log.get( 0 ) );
    assertEquals( "completed", log.get( 1 ) );
  }

  public void testProgressEvent_InvisibleBrowser() {
    Fixture.markInitialized( display );
    browser.setVisible( false );
    ProgressListener listener = mock( ProgressListener.class );
    browser.addProgressListener( listener );

    Fixture.fakeNotifyOperation( getId( browser ), BrowserLCA.EVENT_PROGRESS, null );
    Fixture.readDataAndProcessAction( browser );

    verify( listener ).changed( any( ProgressEvent.class ) );
    verify( listener ).completed( any( ProgressEvent.class ) );
  }

  public void testProgressEvent_DisabledBrowser() {
    Fixture.markInitialized( display );
    browser.setEnabled( false );
    browser.setVisible( false );
    ProgressListener listener = mock( ProgressListener.class );
    browser.addProgressListener( listener );

    Fixture.fakeNotifyOperation( getId( browser ), BrowserLCA.EVENT_PROGRESS, null );
    Fixture.readDataAndProcessAction( browser );

    verify( listener ).changed( any( ProgressEvent.class ) );
    verify( listener ).completed( any( ProgressEvent.class ) );
  }

  public void testRenderCreate() throws IOException {
    lca.renderInitialization( browser );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( browser );
    assertEquals( "rwt.widgets.Browser", operation.getType() );
  }

  public void testRenderParent() throws IOException {
    lca.renderInitialization( browser );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( browser );
    assertEquals( WidgetUtil.getId( browser.getParent() ), operation.getParent() );
  }

  public void testRenderAddSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( browser );
    Fixture.preserveWidgets();

    browser.addProgressListener( mock( ProgressListener.class ) );
    lca.renderChanges( browser );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( browser, "Progress" ) );
  }

  public void testRenderRemoveSelectionListener() throws Exception {
    ProgressListener listener = mock( ProgressListener.class );
    browser.addProgressListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( browser );
    Fixture.preserveWidgets();

    browser.removeProgressListener( listener );
    lca.renderChanges( browser );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( browser, "Progress" ) );
  }

  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( browser );
    Fixture.preserveWidgets();

    browser.addProgressListener( new ProgressListener(){
      public void changed( ProgressEvent event ) {
      }
      public void completed( ProgressEvent event ) {
      }
    } );
    Fixture.preserveWidgets();
    lca.renderChanges( browser );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( browser, "progress" ) );
  }

  public void testRenderInitialUrl() throws IOException {
    lca.render( browser );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( browser );
    assertTrue( operation.getPropertyNames().contains( "url" ) );
  }

  public void testRenderUrl() throws IOException {
    browser.setUrl( "http://eclipse.org/rap" );
    lca.renderChanges( browser );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "http://eclipse.org/rap", message.findSetProperty( browser, "url" ) );
  }

  public void testRenderUrlUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( browser );

    browser.setUrl( "http://eclipse.org/rap" );
    lca.renderChanges( browser );
    Fixture.fakeNewRequest( display );
    lca.renderChanges( browser );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( browser, "url" ) );
  }

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

    Message message = Fixture.getProtocolMessage();
    CallOperation callOperation = message.findCallOperation( browser, "evaluate" );
    assertEquals( "(function(){alert('33');})();", callOperation.getProperty( "script" ) );
  }

  public void testEvaluateResponse() {
    BrowserCallback browserCallback = mock( BrowserCallback.class );
    Fixture.markInitialized( display );
    Fixture.markInitialized( browser );

    BrowserUtil.evaluate( browser, "alert('33');", browserCallback );
    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeNewRequest( display );
    Fixture.fakeSetParameter( getId( browser ), "executeResult", Boolean.TRUE );
    Object[] result = new Object[]{ Integer.valueOf( 27 ) };
    Fixture.fakeSetParameter( getId( browser ), "evaluateResult", result );
    Fixture.executeLifeCycleFromServerThread();

    verify( browserCallback, times( 1 ) ).evaluationSucceeded( Integer.valueOf( 27 ) );
  }

  public void testCallCreateFunctions() throws JSONException, IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( browser );

    new BrowserFunction( browser, "func1" );
    new BrowserFunction( browser, "func2" );
    lca.renderChanges( browser );

    Message message = Fixture.getProtocolMessage();
    CallOperation callOperation = message.findCallOperation( browser, "createFunctions" );
    JSONArray actual = ( JSONArray )callOperation.getProperty( "functions" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[\"func1\",\"func2\"]", actual ) );
  }

  public void testCallDestroyFunctions() throws JSONException, IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( browser );

    BrowserFunction function = new BrowserFunction( browser, "func1" );
    function.dispose();
    lca.renderChanges( browser );

    Message message = Fixture.getProtocolMessage();
    CallOperation callOperation = message.findCallOperation( browser, "destroyFunctions" );
    JSONArray actual = ( JSONArray )callOperation.getProperty( "functions" );
    assertTrue( ProtocolTestUtil.jsonEquals( "[\"func1\"]", actual ) );
  }

  public void testRenderFunctionResult() throws JSONException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( browser );
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
    fakeNewRequest( display );
    fakeSetParameter( getId( browser ), BrowserLCA.PARAM_EXECUTE_FUNCTION, "func" );
    Object[] args = new Object[] { "eclipse", Double.valueOf( 3.6 )};
    fakeSetParameter( getId( browser ), BrowserLCA.PARAM_EXECUTE_ARGUMENTS, args );

    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    JSONArray actual =  ( JSONArray )message.findSetProperty( browser, "functionResult" );
    assertEquals( "func", actual.getString( 0 ) );
    JSONArray result = actual.getJSONArray( 1 );
    assertEquals( 3, result.getInt( 0 ) );
    assertTrue( result.getBoolean( 1 ) );
    assertEquals( JSONObject.NULL, result.get( 2 ) );
    assertTrue( ProtocolTestUtil.jsonEquals( "[\"a string\",false]", result.getJSONArray( 3 ) ) );
    assertEquals( "hi", result.getString( 4 ) );
    assertEquals( Double.valueOf( 0.6666667 ), result.get( 5 ) );
    assertEquals( 12l, result.getLong( 6 ) );
    assertEquals( JSONObject.NULL, actual.get( 2 ) );
  }

  private static IBrowserAdapter getAdapter( Browser browser ) {
    return browser.getAdapter( IBrowserAdapter.class );
  }
}
