/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
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
package org.eclipse.swt.internal.widgets.displaykit;

import static org.eclipse.rap.rwt.internal.lifecycle.DisplayUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Field;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.client.service.ExitConfirmation;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.lifecycle.DisplayLifeCycleAdapter;
import org.eclipse.rap.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rap.rwt.internal.lifecycle.IRenderRunnable;
import org.eclipse.rap.rwt.internal.lifecycle.LifeCycleUtil;
import org.eclipse.rap.rwt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.rap.rwt.internal.lifecycle.UITestUtil;
import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectImpl;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.internal.serverpush.ServerPushManager;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.PhaseEvent;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.PhaseListener;
import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetLifeCycleAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.DestroyOperation;
import org.eclipse.rap.rwt.testfixture.Message.SetOperation;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.internal.widgets.WidgetAdapterImpl;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;


public class DisplayLCA_Test {

  private Display display;
  private String displayId;
  private DisplayLCA displayLCA;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    displayId = DisplayUtil.getId( display );
    displayLCA = new DisplayLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
    setEnableUiTests( false );
  }

  @Test
  public void testPreserveValues() {
    Shell shell = new Shell( display );
    new Button( shell, SWT.PUSH );
    Fixture.markInitialized( display );
    shell.setFocus();
    shell.open();

    Fixture.preserveWidgets();

    WidgetAdapter adapter = DisplayUtil.getAdapter( display );
    assertEquals( shell, adapter.getPreserved( DisplayLCA.PROP_FOCUS_CONTROL ) );
    Object exitConfirmation = adapter.getPreserved( DisplayLCA.PROP_EXIT_CONFIRMATION );
    assertNull( exitConfirmation );
  }

  @Test
  public void testRender() throws IOException {
    AbstractWidgetLCA lca = mock( AbstractWidgetLCA.class );
    Shell shell1 = new CustomLCAShell( display, lca );
    Widget button1 = new CustomLCAWidget( shell1, lca );
    Shell shell2 = new CustomLCAShell( display, lca );
    Widget button2 = new CustomLCAWidget( shell2, lca );

    displayLCA.render( display );

    InOrder inOrder = inOrder( lca );
    inOrder.verify( lca ).render( shell1 );
    inOrder.verify( lca ).render( button1 );
    inOrder.verify( lca ).render( shell2 );
    inOrder.verify( lca ).render( button2 );
  }

  @Test
  public void testRenderWithIOException() {
    Composite shell = new Shell( display , SWT.NONE );
    new CustomLCAWidget( shell, new TestWidgetLCA() {
      @Override
      public void renderChanges( Widget widget ) throws IOException {
        throw new IOException();
      }
    } );

    try {
      displayLCA.render( display );
      fail( "IOException of the renderer adapter in case of composite should be rethrown." );
    } catch( IOException expected ) {
    }
  }

  @Test
  public void testReadData() {
    AbstractWidgetLCA lca = mock( AbstractWidgetLCA.class );
    Composite shell = new CustomLCAShell( display, lca );
    Widget button = new CustomLCAWidget( shell, lca );
    Widget text = new CustomLCAWidget( shell, lca );

    displayLCA.readData( display );

    InOrder inOrder = inOrder( lca );
    inOrder.verify( lca ).readData( shell );
    inOrder.verify( lca ).readData( button );
    inOrder.verify( lca ).readData( text );
    verifyNoMoreInteractions( lca );
  }

  @Test
  public void testReadDisplayBounds() {
    Fixture.fakeSetProperty( getId( display ), "bounds", createJsonArray( 0, 0, 30, 70 ) );

    displayLCA.readData( display );

    assertEquals( new Rectangle( 0, 0, 30, 70 ), display.getBounds() );
  }

  @Test
  public void testFireResizeEvent() {
    Listener listener = mock( Listener.class );
    display.addListener( SWT.Resize, listener  );

    Fixture.fakeSetProperty( getId( display ), "bounds", createJsonArray( 1, 2, 3, 4 ) );
    Fixture.fakeNotifyOperation( getId( display ), "Resize", null );
    Fixture.readDataAndProcessAction( display );

    ArgumentCaptor<Event> captor = ArgumentCaptor.forClass( Event.class );
    verify( listener ).handleEvent( captor.capture() );
    Event event = captor.getValue();
    assertSame( display, event.display );
    assertEquals( 3, event.width );
    assertEquals( 4, event.height );
  }

  @Test
  public void testRenderInitialResizeListener() throws IOException {
    displayLCA.render( display );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( getId( display ), "Resize" ) );
  }

  @Test
  public void testRenderResizeListener() throws IOException {
    Listener listener = mock( Listener.class );
    display.addListener( SWT.Resize, listener  );

    displayLCA.render( display );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( getId( display ), "Resize" ) );
  }

  @Test
  public void testRenderResizeListener_MaximizedShell() throws IOException {
    Shell shell = new Shell( display );
    shell.setMaximized( true );

    displayLCA.render( display );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( getId( display ), "Resize" ) );
  }

  @Test
  public void testRenderResizeListener_FullScreenShell() throws IOException {
    Shell shell = new Shell( display );
    shell.setFullScreen( true );

    displayLCA.render( display );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( getId( display ), "Resize" ) );
  }

  @Test
  public void testRenderResizeListenerUnchanged() throws IOException {
    Listener listener = mock( Listener.class );
    display.addListener( SWT.Resize, listener  );

    displayLCA.preserveValues( display );
    displayLCA.render( display );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( getId( display ), "Resize" ) );
  }

  @Test
  public void testRenderWithChangedAndDisposedWidget() throws IOException {
    Shell shell = new Shell( display, SWT.NONE );
    AbstractWidgetLCA lca = mock( AbstractWidgetLCA.class );
    Composite composite = new CustomLCAWidget( shell, lca );
    Fixture.markInitialized( composite );
    Fixture.preserveWidgets();
    composite.setBounds( 1, 2, 3, 4 );
    composite.dispose();

    displayLCA.render( display );

    verify( lca ).renderDispose( composite );
    verifyNoMoreInteractions( lca );
  }

  @Test
  public void testIsInitializedState() throws IOException {
    final Boolean[] compositeInitState = new Boolean[] { null };
    Shell shell = new Shell( display, SWT.NONE );
    final Composite composite = new Shell( shell, SWT.NONE );
    Control control = new Button( composite, SWT.PUSH );
    WidgetAdapterImpl controlAdapter = ( WidgetAdapterImpl )WidgetUtil.getAdapter( control );
    controlAdapter.setRenderRunnable( new IRenderRunnable() {
      public void afterRender() throws IOException {
        boolean initState = WidgetUtil.getAdapter( composite ).isInitialized();
        compositeInitState[ 0 ] = Boolean.valueOf( initState );
      }
    } );
    // Ensure that the isInitialized state is to to true *right* after a widget
    // was rendered; as opposed to being set to true after the whole widget
    // tree was rendered
    // check precondition
    assertFalse( WidgetUtil.getAdapter( composite ).isInitialized() );

    displayLCA.render( display );

    assertEquals( Boolean.TRUE, compositeInitState[ 0 ] );
  }

  @Test
  public void testRenderInitiallyDisposed() {
    ApplicationContextImpl applicationContext = getApplicationContext();
    applicationContext.getEntryPointManager().register( TestRequest.DEFAULT_SERVLET_PATH,
                                                        TestRenderInitiallyDisposedEntryPoint.class,
                                                        null );
    RWTLifeCycle lifeCycle
      = ( RWTLifeCycle )getApplicationContext().getLifeCycleFactory().getLifeCycle();
    LifeCycleUtil.setSessionDisplay( null );
    // ensure that life cycle execution succeeds with disposed display
    try {
      lifeCycle.execute();
    } catch( Throwable e ) {
      fail( "Life cycle execution must succeed even with a disposed display" );
    }
  }

  @Test
  public void testRenderDisposed() throws Exception {
    ApplicationContextImpl applicationContext = getApplicationContext();
    applicationContext.getEntryPointManager().register( TestRequest.DEFAULT_SERVLET_PATH,
                                                        TestRenderDisposedEntryPoint.class,
                                                        null );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )applicationContext.getLifeCycleFactory().getLifeCycle();
    LifeCycleUtil.setSessionDisplay( null );
    lifeCycle.execute();
    Fixture.fakeNewRequest();
    lifeCycle.execute();
    Fixture.fakeNewRequest();
    final Shell[] shell = new Shell[ 1 ];
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public PhaseId getPhaseId() {
        return PhaseId.PROCESS_ACTION;
      }
      public void beforePhase( PhaseEvent event ) {
        shell[ 0 ] = Display.getCurrent().getShells()[ 0 ];
        shell[ 0 ].dispose();
      }
      public void afterPhase( PhaseEvent event ) {
      }
    } );

    lifeCycle.execute();

    Message message = Fixture.getProtocolMessage();
    assertTrue( message.getOperation( 0 ) instanceof DestroyOperation );
    String shellId = WidgetUtil.getId( shell[ 0 ] );
    assertEquals( shellId, message.getOperation( 0 ).getTarget() );
  }

  @Test
  public void testRenderRunnableIsExecutedAndCleared() throws IOException {
    Widget widget = new Shell( display );
    WidgetAdapterImpl widgetAdapter = ( WidgetAdapterImpl )WidgetUtil.getAdapter( widget );
    IRenderRunnable renderRunnable = mock( IRenderRunnable.class );
    widgetAdapter.setRenderRunnable( renderRunnable );
    DisplayLifeCycleAdapter displayLCA = DisplayUtil.getLCA( display );

    displayLCA.render( display );

    verify( renderRunnable ).afterRender();
    assertEquals( null, widgetAdapter.getRenderRunnable() );
  }


  @Test
  public void testFocusControl() {
    Shell shell = new Shell( display, SWT.NONE );
    Control control = new Button( shell, SWT.PUSH );
    shell.open();

    Fixture.fakeSetProperty( getId( display ), "focusControl", getId( control ) );
    Fixture.readDataAndProcessAction( display );
    assertEquals( control, display.getFocusControl() );

    // Request parameter focusControl with value 'null' is ignored
    Control previousFocusControl = display.getFocusControl();
    Fixture.fakeNewRequest();
    Fixture.fakeSetProperty( getId( display ), "focusControl", "null" );
    Fixture.readDataAndProcessAction( display );
    assertEquals( previousFocusControl, display.getFocusControl() );
  }

  @Test
  public void testResizeMaximizedShells() {
    Object adapter = display.getAdapter( IDisplayAdapter.class );
    IDisplayAdapter displayAdapter = ( IDisplayAdapter )adapter;
    displayAdapter.setBounds( new Rectangle( 0, 0, 800, 600 ) );
    Shell shell1 = new Shell( display, SWT.NONE );
    shell1.setBounds( 0, 0, 800, 600 );
    Shell shell2 = new Shell( display, SWT.NONE );
    shell2.setBounds( 0, 0, 300, 400 );
    shell2.setMaximized( true );
    // fake display resize
    Fixture.fakeSetProperty( getId( display ), "bounds", createJsonArray( 0, 0, 700, 500 ) );

    displayLCA.readData( display );

    // shell1 is not resized although it has the same size as the display
    assertEquals( new Rectangle( 0, 0, 800, 600 ), shell1.getBounds() );
    // shell2 is resized because it's maximized
    assertEquals( new Rectangle( 0, 0, 700, 500 ), shell2.getBounds() );
  }

  @Test
  public void testReadCursorLocation() {
    Object adapter = display.getAdapter( IDisplayAdapter.class );
    IDisplayAdapter displayAdapter = ( IDisplayAdapter )adapter;
    displayAdapter.setBounds( new Rectangle( 0, 0, 800, 600 ) );
    Fixture.fakeSetProperty( getId( display ), "cursorLocation", createJsonArray( 1, 2 ) );

    displayLCA.readData( display );

    assertEquals( new Point( 1, 2 ), display.getCursorLocation() );
  }

  @Test
  public void testServerPushRendered() throws IOException {
    ServerPushManager.getInstance().activateServerPushFor( new Object() );

    displayLCA.render( display );

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findSetProperty( "rwt.client.ServerPush", "active" ) );
  }

  @Test
  public void testRenderBeep() throws IOException {
    display.beep();
    displayLCA.render( display );

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findCallOperation( displayId, "beep" ) );
    assertFalse( display.getAdapter( IDisplayAdapter.class ).isBeepCalled() );
  }

  @Test
  public void testRenderEnableUiTests() throws IOException {
    setEnableUiTests( true );

    displayLCA.render( display );

    Message message = Fixture.getProtocolMessage();
    // must be the first operation, before any widgets are rendered
    SetOperation firstOperation = (SetOperation)message.getOperation( 0 );
    assertEquals( DisplayUtil.getId( display ), firstOperation.getTarget() );
    assertEquals( JsonValue.TRUE, firstOperation.getProperty( "enableUiTests" ) );
  }

  @Test
  public void testRenderEnableUiTests_WhenAlreadyInitialized() throws IOException {
    Fixture.markInitialized( display );
    setEnableUiTests( true );

    displayLCA.render( display );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( displayId, "enableUiTests" ) );
  }

  @Test
  public void testInvalidCustomId() {
    Fixture.markInitialized( display );
    setEnableUiTests( true );
    Shell shell = new Shell( display );

    try {
      shell.setData( WidgetUtil.CUSTOM_WIDGET_ID, "in/valid id" );
      fail();
    } catch( IllegalArgumentException e ) {
      assertTrue( e.getMessage().contains( "widget id contains illegal characters" ) );
    }
  }

  @Test
  public void testRenderWithCustomId() throws IOException {
    Shell shell = new Shell( display, SWT.NONE );
    setEnableUiTests( true );

    shell.setData( WidgetUtil.CUSTOM_WIDGET_ID, "myShell" );
    WidgetUtil.getLCA( shell ).renderInitialization( shell );

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findCreateOperation( "myShell" ) );
  }

  @Test
  public void testRendersExitConfirmation() throws IOException {
    RWT.getClient().getService( ExitConfirmation.class ).setMessage( "test" );

    displayLCA.render( display );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "test", message.findSetProperty( displayId, "exitConfirmation" ).asString() );
  }

  @Test
  public void testPreservesExitConfirmation() throws IOException {
    RWT.getClient().getService( ExitConfirmation.class ).setMessage( "test" );

    displayLCA.preserveValues( display );
    displayLCA.render( display );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( displayId, "exitConfirmation" ) );
  }

  @Test
  public void testRendersExitConfirmationReset() throws IOException {
    RWT.getClient().getService( ExitConfirmation.class ).setMessage( "test" );
    displayLCA.preserveValues( display );

    RWT.getClient().getService( ExitConfirmation.class ).setMessage( null );
    displayLCA.render( display );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( displayId, "exitConfirmation" ) );
  }

  @Test
  public void testRendersRemoteObjects() throws IOException {
    RemoteObjectImpl remoteObject = mock( RemoteObjectImpl.class );
    when( remoteObject.getId() ).thenReturn( "id" );
    RemoteObjectRegistry.getInstance().register( remoteObject );

    displayLCA.render( display );

    verify( remoteObject ).render( any( ProtocolMessageWriter.class ) );
  }

  @Test
  public void testReadDataDelegatesToRemoteObjects() {
    OperationHandler handler = mock( OperationHandler.class );
    RemoteObjectImpl remoteObject = mock( RemoteObjectImpl.class );
    when( remoteObject.getId() ).thenReturn( "id" );
    when( remoteObject.getHandler() ).thenReturn( handler );
    RemoteObjectRegistry.getInstance().register( remoteObject );
    JsonObject properties = new JsonObject().add( "foo", "bar" );
    Fixture.fakeCallOperation( "id", "method", properties );

    displayLCA.readData( display );

    verify( handler ).handleCall( eq( "method" ), eq( new JsonObject().add( "foo", "bar" ) ) );
  }

  private static void setEnableUiTests( boolean value ) {
    Field field;
    try {
      field = UITestUtil.class.getDeclaredField( "enabled" );
      field.setAccessible( true );
      field.setBoolean( null, value );
    } catch( Exception e ) {
      throw new RuntimeException( "Failed to set enabled field", e );
    }
  }

  private static class TestWidgetLCA extends AbstractWidgetLCA {
    public void readData( Widget widget ) {
    }
    @Override
    public void preserveValues( Widget widget ) {
    }
    @Override
    public void renderInitialization( Widget widget ) throws IOException {
    }
    @Override
    public void renderChanges( Widget widget ) throws IOException {
    }
    @Override
    public void renderDispose( Widget widget ) throws IOException {
    }
  }

  private static class CustomLCAWidget extends Composite {
    private static final long serialVersionUID = 1L;

    private final AbstractWidgetLCA widgetLCA;

    CustomLCAWidget( Composite parent, AbstractWidgetLCA widgetLCA ) {
      super( parent, 0 );
      this.widgetLCA = widgetLCA;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAdapter( Class<T> adapter ) {
      Object result;
      if( adapter == WidgetLifeCycleAdapter.class ) {
        result = widgetLCA;
      } else {
        result = super.getAdapter( adapter );
      }
      return ( T )result;
    }
  }

  private static class CustomLCAShell extends Shell {
    private static final long serialVersionUID = 1L;

    private final AbstractWidgetLCA widgetLCA;

    CustomLCAShell( Display display, AbstractWidgetLCA widgetLCA ) {
      super( display );
      this.widgetLCA = widgetLCA;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAdapter( Class<T> adapter ) {
      Object result;
      if( adapter == WidgetLifeCycleAdapter.class ) {
        result = widgetLCA;
      } else {
        result = super.getAdapter( adapter );
      }
      return ( T )result;
    }
  }

  public static final class TestRenderInitiallyDisposedEntryPoint implements EntryPoint {
    public int createUI() {
      Display display = new Display();
      display.dispose();
      return 0;
    }
  }

  public static final class TestRenderDisposedEntryPoint implements EntryPoint {
    public int createUI() {
      Display display = new Display();
      Shell shell = new Shell( display );
      while( !shell.isDisposed() ) {
        if( !display.readAndDispatch() ) {
          display.sleep();
        }
      }
      display.dispose();
      return 0;
    }
  }

}
