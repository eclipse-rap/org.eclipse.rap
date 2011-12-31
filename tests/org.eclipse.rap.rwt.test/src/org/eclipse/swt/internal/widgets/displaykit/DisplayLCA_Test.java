/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.DestroyOperation;
import org.eclipse.rap.rwt.testfixture.Message.SetOperation;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.internal.theme.ThemeUtil;
import org.eclipse.rwt.internal.uicallback.UICallBackManager;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.internal.widgets.WidgetAdapter;
import org.eclipse.swt.widgets.*;


public class DisplayLCA_Test extends TestCase {

  private static final List<Widget> log = new ArrayList<Widget>();
  private static final List<Widget> renderInitLog = new ArrayList<Widget>();
  private static final List<Widget> renderChangesLog = new ArrayList<Widget>();
  private static final List<Widget> renderDisposeLog = new ArrayList<Widget>();

  private Display display;
  private String displayId;
  private DisplayLCA displayLCA;

  protected void setUp() throws Exception {
    clearLogs();
    Fixture.setUp();
    Fixture.fakeNewRequest();
    Fixture.fakeResponseWriter();
    display = new Display();
    displayId = DisplayUtil.getId( display );
    displayLCA = new DisplayLCA();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
    clearLogs();
    setEnableUiTests( false );
  }

  public void testPreserveValues() {
    Shell shell = new Shell( display );
    new Button( shell, SWT.PUSH );
    Fixture.markInitialized( display );
    shell.setFocus();
    shell.open();

    Fixture.preserveWidgets();

    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );
    assertEquals( shell, adapter.getPreserved( DisplayLCA.PROP_FOCUS_CONTROL ) );
    Object currentTheme = adapter.getPreserved( DisplayLCA.PROP_CURRENT_THEME );
    assertEquals( ThemeUtil.getCurrentThemeId(), currentTheme );
    Object exitConfirmation = adapter.getPreserved( DisplayLCA.PROP_EXIT_CONFIRMATION );
    assertNull( exitConfirmation );
  }

  public void testStartup() throws IOException {
    displayLCA.render( display );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
  }

  public void testRender() throws IOException {
    // fake request param to simulate second request
    Fixture.fakeRequestParam( RequestParams.UIROOT, "w1" );
    LoggingWidgetLCA loggingWidgetLCA = new LoggingWidgetLCA();
    Shell shell1 = new CustomLCAShell( display, loggingWidgetLCA );
    Widget button1 = new CustomLCAWidget( shell1, loggingWidgetLCA );
    Shell shell2 = new CustomLCAShell( display, loggingWidgetLCA );
    Widget button2 = new CustomLCAWidget( shell2, loggingWidgetLCA );

    displayLCA.render( display );

    assertEquals( 4, log.size() );
    assertSame( shell1, log.get( 0 ) );
    assertSame( button1, log.get( 1 ) );
    assertSame( shell2, log.get( 2 ) );
    assertSame( button2, log.get( 3 ) );
  }

  public void testRenderWithIOException() {
    // fake request param to simulate second request
    Fixture.fakeRequestParam( RequestParams.UIROOT, "w1" );
    Composite shell = new Shell( display , SWT.NONE );
    new CustomLCAWidget( shell, new TestWidgetLCA() {
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

  public void testReadData() {
    // fake request param to simulate second request
    Fixture.fakeRequestParam( RequestParams.UIROOT, "w1" );
    LoggingWidgetLCA loggingWidgetLCA = new LoggingWidgetLCA();
    Composite shell = new CustomLCAShell( display, loggingWidgetLCA );
    Widget button = new CustomLCAWidget( shell, loggingWidgetLCA );
    Widget text = new CustomLCAWidget( shell, loggingWidgetLCA );

    displayLCA.readData( display );

    assertEquals( 3, log.size() );
    assertSame( shell, log.get( 0 ) );
    assertSame( button, log.get( 1 ) );
    assertSame( text, log.get( 2 ) );
  }

  public void testReadDisplayBounds() {
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( displayId + ".bounds.width", "30" );
    Fixture.fakeRequestParam( displayId + ".bounds.height", "70" );

    displayLCA.readData( display );

    assertEquals( new Rectangle( 0, 0, 30, 70 ), display.getBounds() );
  }

  public void testRenderWithChangedAndDisposedWidget() throws IOException {
    // fake request param to simulate subsequent request
    Fixture.fakeRequestParam( RequestParams.UIROOT, "w1" );
    Shell shell = new Shell( display, SWT.NONE );
    Composite composite = new CustomLCAWidget( shell, new LoggingWidgetLCA() );
    Fixture.markInitialized( composite );
    Fixture.preserveWidgets();
    composite.setBounds( 1, 2, 3, 4 );
    composite.dispose();

    displayLCA.render( display );

    assertEquals( 0, renderInitLog.size() );
    assertFalse( renderChangesLog.contains( composite ) );
    assertTrue( renderDisposeLog.contains( composite ) );
  }

  public void testIsInitializedState() throws IOException {
    final Boolean[] compositeInitState = new Boolean[] { null };
    Shell shell = new Shell( display, SWT.NONE );
    final Composite composite = new Shell( shell, SWT.NONE );
    Control control = new Button( composite, SWT.PUSH );
    WidgetAdapter controlAdapter = ( WidgetAdapter )WidgetUtil.getAdapter( control );
    controlAdapter.setRenderRunnable( new IRenderRunnable() {
      public void afterRender() throws IOException {
        boolean initState = WidgetUtil.getAdapter( composite ).isInitialized();
        compositeInitState[ 0 ] = Boolean.valueOf( initState );
      }
    } );
    // Ensure that the isInitialized state is to to true *right* after a widget
    // was rendered; as opposed to being set to true after the whole widget
    // tree was rendered
    Fixture.fakeNewRequest( display );
    // check precondition
    assertEquals( false, WidgetUtil.getAdapter( composite ).isInitialized() );

    displayLCA.render( display );

    assertEquals( Boolean.TRUE, compositeInitState[ 0 ] );
  }

  public void testRenderInitiallyDisposed() throws Exception {
    RWTFactory.getEntryPointManager().register( EntryPointManager.DEFAULT,
                                                TestRenderInitiallyDisposedEntryPoint.class );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    LifeCycleUtil.setSessionDisplay( null );
    // ensure that life cycle execution succeeds with disposed display
    try {
      lifeCycle.execute();
    } catch( Throwable e ) {
      fail( "Life cycle execution must succeed even with a disposed display" );
    }
  }

  public void testRenderDisposed() throws Exception {
    RWTFactory.getEntryPointManager().register( EntryPointManager.DEFAULT,
                                                TestRenderDisposedEntryPoint.class );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )RWTFactory.getLifeCycleFactory().getLifeCycle();
    LifeCycleUtil.setSessionDisplay( null );
    lifeCycle.execute();
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.STARTUP, null );
    Fixture.fakeRequestParam( RequestParams.UIROOT, "w1" );
    lifeCycle.execute();
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, "w1" );
    lifeCycle.addPhaseListener( new PhaseListener() {
      private static final long serialVersionUID = 1L;
      public PhaseId getPhaseId() {
        return PhaseId.PROCESS_ACTION;
      }
      public void beforePhase( PhaseEvent event ) {
        Display.getCurrent().getShells()[ 0 ].dispose();
      }
      public void afterPhase( PhaseEvent event ) {
      }
    } );

    lifeCycle.execute();

    Message message = Fixture.getProtocolMessage();
    assertTrue( message.getOperation( 0 ) instanceof DestroyOperation );
    assertEquals( "w2", message.getOperation( 0 ).getTarget() );
  }

  public void testRenderRunnableIsExecutedAndCleared() throws IOException {
    Widget widget = new Shell( display );
    WidgetAdapter widgetAdapter = ( WidgetAdapter )WidgetUtil.getAdapter( widget );
    IRenderRunnable renderRunnable = mock( IRenderRunnable.class );
    widgetAdapter.setRenderRunnable( renderRunnable );
    Fixture.fakeNewRequest( display );
    IDisplayLifeCycleAdapter displayLCA = DisplayUtil.getLCA( display );

    displayLCA.render( display );

    verify( renderRunnable ).afterRender();
    assertEquals( null, widgetAdapter.getRenderRunnable() );
  }


  public void testFocusControl() {
    Shell shell = new Shell( display, SWT.NONE );
    Control control = new Button( shell, SWT.PUSH );
    shell.open();
    String controlId = WidgetUtil.getId( control );

    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( displayId + ".focusControl", controlId );
    Fixture.readDataAndProcessAction( display );
    assertEquals( control, display.getFocusControl() );

    // Request parameter focusControl with value 'null' is ignored
    Control previousFocusControl = display.getFocusControl();
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( displayId + ".focusControl", "null" );
    Fixture.readDataAndProcessAction( display );
    assertEquals( previousFocusControl, display.getFocusControl() );
  }

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
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( displayId + ".bounds.width", "700" );
    Fixture.fakeRequestParam( displayId + ".bounds.height", "500" );

    displayLCA.readData( display );

    // shell1 is not resized although it has the same size as the display
    assertEquals( new Rectangle( 0, 0, 800, 600 ), shell1.getBounds() );
    // shell2 is resized because it's maximized
    assertEquals( new Rectangle( 0, 0, 700, 500 ), shell2.getBounds() );
  }

  public void testReadCursorLocation() {
    Object adapter = display.getAdapter( IDisplayAdapter.class );
    IDisplayAdapter displayAdapter = ( IDisplayAdapter )adapter;
    displayAdapter.setBounds( new Rectangle( 0, 0, 800, 600 ) );
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( displayId + ".cursorLocation.x", "1" );
    Fixture.fakeRequestParam( displayId + ".cursorLocation.y", "2" );

    displayLCA.readData( display );

    assertEquals( new Point( 1, 2 ), display.getCursorLocation() );
  }

  public void testUICallBackUpdated() throws IOException {
    Fixture.fakeNewRequest( display );
    Fixture.preserveWidgets();

    UICallBackManager.getInstance().activateUICallBacksFor( "id" );
    displayLCA.render( display );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( "uicb", "active" ) );
  }

  public void testRenderCurrentTheme() throws IOException {
    Fixture.fakeNewRequest( display );

    displayLCA.render( display );

    Message message = Fixture.getProtocolMessage();
    String expected = "org.eclipse.swt.theme.Default";
    assertEquals( expected, message.findSetProperty( displayId, "currentTheme" ) );
  }

  public void testRenderTimeoutPage() throws IOException {
    Fixture.fakeNewRequest( display );

    displayLCA.render( display );

    Message message = Fixture.getProtocolMessage();
    String actual = ( String )message.findSetProperty( displayId, "timeoutPage" );
    assertTrue( actual.startsWith( "<html><head><title>" ) );
  }

  public void testRenderBeep() throws IOException {
    Fixture.fakeNewRequest( display );

    display.beep();
    displayLCA.render( display );

    Message message = Fixture.getProtocolMessage();
    assertNotNull( message.findCallOperation( displayId, "beep" ) );
    assertFalse( display.getAdapter( IDisplayAdapter.class ).isBeepCalled() );
  }

  public void testRenderEnableUiTests() throws IOException {
    Fixture.fakeNewRequest( display );
    setEnableUiTests( true );

    displayLCA.render( display );

    Message message = Fixture.getProtocolMessage();
    // must be the first operation, before any widgets are rendered
    SetOperation firstOperation = (SetOperation)message.getOperation( 0 );
    assertEquals( DisplayUtil.getId( display ), firstOperation.getTarget() );
    assertEquals( Boolean.TRUE, firstOperation.getProperty( "enableUiTests" ) );
  }

  public void testRenderEnableUiTests_whenAlreadyInitialized() throws IOException {
    Fixture.fakeNewRequest( display );
    Fixture.markInitialized( display );
    setEnableUiTests( true );

    displayLCA.render( display );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( displayId, "enableUiTests" ) );
  }

  public void testCheckUiTests() throws IOException {
    Fixture.fakeNewRequest( display );
    Fixture.markInitialized( display );
    setEnableUiTests( true );
    Shell shell = new Shell( display );
    shell.setData( WidgetUtil.CUSTOM_WIDGET_ID, "in/valid id" );

    try {
      displayLCA.render( display );
      fail();
    } catch( IllegalArgumentException e ) {
      assertTrue( e.getMessage().contains( "widget id contains illegal characters" ) );
    }
  }

  private void clearLogs() {
    log.clear();
    renderInitLog.clear();
    renderChangesLog.clear();
    renderDisposeLog.clear();
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
    public void preserveValues( Widget widget ) {
    }
    public void renderInitialization( Widget widget ) throws IOException {
    }
    public void renderChanges( Widget widget ) throws IOException {
    }
    public void renderDispose( Widget widget ) throws IOException {
    }
  }

  private static class LoggingWidgetLCA extends AbstractWidgetLCA {

    public void preserveValues( Widget widget ) {
    }

    public void readData( Widget widget ) {
      log.add( widget );
    }

    public void renderInitialization( Widget widget ) throws IOException {
      renderInitLog.add( widget );
    }

    public void renderChanges( Widget widget ) throws IOException {
      log.add( widget );
      renderChangesLog.add( widget );
    }

    public void renderDispose( Widget widget ) throws IOException {
      renderDisposeLog.add( widget );
    }
  }

  private static class CustomLCAWidget extends Composite {
    private static final long serialVersionUID = 1L;

    private final AbstractWidgetLCA widgetLCA;

    CustomLCAWidget( Composite parent, AbstractWidgetLCA widgetLCA ) {
      super( parent, 0 );
      this.widgetLCA = widgetLCA;
    }

    @SuppressWarnings("unchecked")
    public <T> T getAdapter( Class<T> adapter ) {
      Object result;
      if( adapter == ILifeCycleAdapter.class ) {
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

    @SuppressWarnings("unchecked")
    public <T> T getAdapter( Class<T> adapter ) {
      Object result;
      if( adapter == ILifeCycleAdapter.class ) {
        result = widgetLCA;
      } else {
        result = super.getAdapter( adapter );
      }
      return ( T )result;
    }
  }

  public static final class TestRenderInitiallyDisposedEntryPoint implements IEntryPoint {
    public int createUI() {
      Display display = new Display();
      display.dispose();
      return 0;
    }
  }

  public static final class TestRenderDisposedEntryPoint implements IEntryPoint {
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
