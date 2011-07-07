/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.internal.theme.ThemeUtil;
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
  
  private DisplayLCA displayLCA;
  
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
    
    public Object getAdapter( Class adapter ) {
      Object result;
      if( adapter == ILifeCycleAdapter.class ) {
        result = widgetLCA;
      } else {
        result = super.getAdapter( adapter );
      }
      return result;
    }
  }

  private static class CustomLCAShell extends Shell {
    private static final long serialVersionUID = 1L;
    
    private final AbstractWidgetLCA widgetLCA;
    
    CustomLCAShell( Display display, AbstractWidgetLCA widgetLCA ) {
      super( display );
      this.widgetLCA = widgetLCA;
    }
    
    public Object getAdapter( Class adapter ) {
      Object result;
      if( adapter == ILifeCycleAdapter.class ) {
        result = widgetLCA;
      } else {
        result = super.getAdapter( adapter );
      }
      return result;
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

  public void testPreserveValues() {
    Display display = new Display();
    Shell shell = new Shell( display );
    new Button( shell, SWT.PUSH );
    Fixture.markInitialized( display );
    shell.setFocus();
    shell.open();
    
    Fixture.preserveWidgets();
    
    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );
    assertEquals( shell, adapter.getPreserved( DisplayLCA.PROP_FOCUS_CONTROL ) );
    Object currentTheme = adapter.getPreserved( DisplayLCA.PROP_CURR_THEME );
    assertEquals( ThemeUtil.getCurrentThemeId(), currentTheme );
    Object exitConfirmation = adapter.getPreserved( DisplayLCA.PROP_EXIT_CONFIRMATION );
    assertNull( exitConfirmation );
  }

  public void testStartup() throws IOException {
    Display display = new Display();

    displayLCA.render( display );
    
    String allMarkup = Fixture.getAllMarkup();
    assertEquals( "", allMarkup );
  }

  public void testRender() throws IOException {
    // fake request param to simulate second request
    Fixture.fakeRequestParam( RequestParams.UIROOT, "w1" );
    Display display = new Display();
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
    Display display = new Display();
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
    Display display = new Display();
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
    Display display = new Display();
    String displayId = DisplayUtil.getAdapter( display ).getId();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( displayId + ".bounds.width", "30" );
    Fixture.fakeRequestParam( displayId + ".bounds.height", "70" );
    
    displayLCA.readData( display );
    
    assertEquals( new Rectangle( 0, 0, 30, 70 ), display.getBounds() );
  }

  public void testRenderWithChangedAndDisposedWidget() throws IOException {
    // fake request param to simulate subsequent request
    Fixture.fakeRequestParam( RequestParams.UIROOT, "w1" );
    Display display = new Display();
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
    Display display = new Display();
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
    Fixture.fakeRequestParam( RequestParams.STARTUP, EntryPointManager.DEFAULT );
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
    Fixture.fakeRequestParam( RequestParams.STARTUP, EntryPointManager.DEFAULT );
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
    
    String expected = "wm.dispose( \"w2\" );";
    assertTrue( Fixture.getAllMarkup().indexOf( expected ) != - 1 );
  }

  public void testFocusControl() {
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    Control control = new Button( shell, SWT.PUSH );
    shell.open();
    String displayId = DisplayUtil.getId( display );
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
    Display display = new Display();
    Object adapter = display.getAdapter( IDisplayAdapter.class );
    IDisplayAdapter displayAdapter = ( IDisplayAdapter )adapter;
    displayAdapter.setBounds( new Rectangle( 0, 0, 800, 600 ) );
    Shell shell1 = new Shell( display, SWT.NONE );
    shell1.setBounds( 0, 0, 800, 600 );
    Shell shell2 = new Shell( display, SWT.NONE );
    shell2.setBounds( 0, 0, 300, 400 );
    shell2.setMaximized( true );
    // fake display resize
    String displayId = DisplayUtil.getAdapter( display ).getId();
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
    Display display = new Display();
    Object adapter = display.getAdapter( IDisplayAdapter.class );
    IDisplayAdapter displayAdapter = ( IDisplayAdapter )adapter;
    displayAdapter.setBounds( new Rectangle( 0, 0, 800, 600 ) );
    String displayId = DisplayUtil.getAdapter( display ).getId();
    Fixture.fakeNewRequest( display );
    Fixture.fakeRequestParam( displayId + ".cursorLocation.x", "1" );
    Fixture.fakeRequestParam( displayId + ".cursorLocation.y", "2" );

    displayLCA.readData( display );
    
    assertEquals( new Point( 1, 2 ), display.getCursorLocation() );
  }

  protected void setUp() throws Exception {
    clearLogs();
    Fixture.setUp();
    Fixture.fakeNewRequest();
    Fixture.fakeResponseWriter();
    displayLCA = new DisplayLCA();
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
    clearLogs();
  }

  private void clearLogs() {
    log.clear();
    renderInitLog.clear();
    renderChangesLog.clear();
    renderDisposeLog.clear();
  }
}