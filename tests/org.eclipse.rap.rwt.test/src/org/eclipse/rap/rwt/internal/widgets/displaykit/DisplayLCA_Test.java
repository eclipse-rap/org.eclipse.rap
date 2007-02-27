/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.rwt.internal.widgets.displaykit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.events.SelectionAdapter;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.engine.PhaseListenerRegistry;
import org.eclipse.rap.rwt.internal.lifecycle.*;
import org.eclipse.rap.rwt.internal.widgets.WidgetAdapterFactory;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.*;
import com.w4t.engine.requests.RequestParams;
import com.w4t.util.browser.Ie6up;

public class DisplayLCA_Test extends TestCase {

  private AdapterFactory lifeCycleAdapterFactory;
  private WidgetAdapterFactory widgetAdapterFactory;
  private final List log = new ArrayList();
  private final List renderInitLog = new ArrayList();
  private final List renderChangesLog = new ArrayList();
  private final List renderDisposeLog = new ArrayList();

  private final class DisposeTestButton extends Button {

    public DisposeTestButton( final Composite parent, final int style ) {
      super( parent, style );
    }
    
  }
  
  public void testStartup() throws IOException {
    Fixture.fakeResponseWriter();
    Fixture.fakeBrowser( new Ie6up( true, true ) );
    Display display = new Display();
    Object adapter = display.getAdapter( ILifeCycleAdapter.class );
    IDisplayLifeCycleAdapter lcAdapter = ( IDisplayLifeCycleAdapter )adapter;
    // first request: render html to load JavaScript "application"
    lcAdapter.render( display );
    String allMarkup = Fixture.getAllMarkup();
    assertTrue( allMarkup.indexOf( "<html" ) != -1 );
    assertTrue( allMarkup.indexOf( "<body" ) != -1 );
    String expected = "var req = org.eclipse.rap.rwt.Request.getInstance();";
    assertTrue( allMarkup.indexOf( expected ) != -1 );
    assertTrue( allMarkup.indexOf( "req.setUIRootId( \"w1\" )" ) != -1 );
  }

  public void testRenderProcessing() throws IOException {
    Fixture.fakeResponseWriter();
    // fake request param to simulate second request
    Fixture.fakeRequestParam( RequestParams.UIROOT, "w1" );
    Display display = new Display();
    Composite shell1 = new Shell( display , RWT.NONE );
    Button button1 = new Button( shell1, RWT.PUSH );
    Composite shell2 = new Shell( display , RWT.NONE );
    Button button2 = new Button( shell2, RWT.PUSH );
    Object adapter = display.getAdapter( ILifeCycleAdapter.class );
    IDisplayLifeCycleAdapter lcAdapter = ( IDisplayLifeCycleAdapter )adapter;
    lcAdapter.render( display );
    assertEquals( 4, log.size() );
    assertSame( shell1, log.get( 0 ) );
    assertSame( button1, log.get( 1 ) );
    assertSame( shell2, log.get( 2 ) );
    assertSame( button2, log.get( 3 ) );
    clearLogs();
    new Composite( shell1, RWT.NONE );
    try {
      lcAdapter.render( display );
      String msg = "IOException of the renderer adapter in case of composite"
                 + "should be rethrown.";
      fail( msg );
    } catch( final IOException ioe ) {
      // expected
    }
    assertEquals( 2, log.size() );
    assertSame( shell1, log.get( 0 ) );
    assertSame( button1, log.get( 1 ) );
  }

  public void testReadDataProcessing() {
    Fixture.fakeResponseWriter();
    // fake request param to simulate second request
    Fixture.fakeRequestParam( RequestParams.UIROOT, "w1" );
    Display display = new Display();
    Composite shell = new Shell( display , RWT.NONE );
    Button button = new Button( shell, RWT.PUSH );
    Text text = new Text( shell, RWT.NONE );
    Object adapter = display.getAdapter( ILifeCycleAdapter.class );
    IDisplayLifeCycleAdapter lcAdapter = ( IDisplayLifeCycleAdapter )adapter;
    lcAdapter.readData( display );
    assertEquals( 3, log.size() );
    assertSame( shell, log.get( 0 ) );
    assertSame( button, log.get( 1 ) );
    assertSame( text, log.get( 2 ) );
  }
  
  public void testReadData() {
    Display display = new Display();
    IDisplayLifeCycleAdapter lca = DisplayUtil.getLCA( display );
    String displayId = DisplayUtil.getAdapter( display ).getId();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( displayId + ".bounds.width", "30" );
    Fixture.fakeRequestParam( displayId + ".bounds.height", "70" );
    lca.readData( display );
    assertEquals( new Rectangle( 0, 0, 30, 70 ), display.getBounds() );
  }
  
  public void testRrenderChangedButDisposed() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display, RWT.NONE );
    final Button button = new DisposeTestButton( shell, RWT.PUSH );
    String displayId = DisplayUtil.getId( display );
    String buttonId = WidgetUtil.getId( button );
    
    // Run requests to initialize the 'system'
    RWTFixture.fakeNewRequest();
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    lifeCycle.execute();
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    lifeCycle.execute();
    
    // Run the actual test request: the button is clicked
    // It changes its text and disposes itself 
    clearLogs();
    button.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( final SelectionEvent event ) {
        button.setText( "should be ignored" );
        button.dispose();
      }
    } );
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, buttonId );
    lifeCycle.execute();
    
    assertEquals( 0, renderInitLog.size() );
    assertFalse( renderChangesLog.contains( button ) );
    assertTrue( renderDisposeLog.contains( button ) );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    AdapterManager manager = W4TContext.getAdapterManager();
    lifeCycleAdapterFactory = new AdapterFactory() {

      private AdapterFactory factory = new LifeCycleAdapterFactory();

      public Object getAdapter( final Object adaptable, final Class adapter ) {
        Object result = null;
        if( adaptable instanceof Display && adapter == ILifeCycleAdapter.class )
        {
          result = factory.getAdapter( adaptable, adapter );
        } else {
          result = new AbstractWidgetLCA() {

            public void preserveValues( final Widget widget ) {
            }

            public void readData( final Widget widget ) {
              log.add( widget );
              if( widget instanceof DisposeTestButton ) {
                SelectionEvent event 
                  = new SelectionEvent( widget, 
                                        null, 
                                        SelectionEvent.WIDGET_SELECTED );
                event.processEvent();
              }
            }

            public void renderInitialization( final Widget widget )
              throws IOException
            {
              renderInitLog.add( widget );
            }

            public void renderChanges( final Widget widget ) throws IOException
            {
              if( widget.getClass().equals( Composite.class ) ) {
                throw new IOException();
              }
              log.add( widget );
              renderChangesLog.add( widget );
            }

            public void renderDispose( final Widget widget ) throws IOException
            {
              renderDisposeLog.add( widget );
            }
          };
        }
        return result;
      }

      public Class[] getAdapterList() {
        return factory.getAdapterList();
      }
    };
    manager.registerAdapters( lifeCycleAdapterFactory, Display.class );
    manager.registerAdapters( lifeCycleAdapterFactory, Widget.class );
    widgetAdapterFactory = new WidgetAdapterFactory();
    manager.registerAdapters( widgetAdapterFactory, Display.class );
    manager.registerAdapters( widgetAdapterFactory, Widget.class );
    clearLogs();
    RWTFixture.registerResourceManager();
    PhaseListenerRegistry.add( new CurrentPhase.Listener() );
    PhaseListenerRegistry.add( new PreserveWidgetsPhaseListener() );
  }
  
  public void testIsInitializedState() throws IOException {
    final Boolean[] compositeInitState = new Boolean[] { null };
    Display display = new Display();
    Shell shell = new Shell( display, RWT.NONE );
    final Composite composite = new Shell( shell, RWT.NONE );
    Control control = new Button( composite, RWT.PUSH );
    IWidgetAdapter controlAdapter = WidgetUtil.getAdapter( control );
    controlAdapter.setRenderRunnable( new IRenderRunnable() {
      public void afterRender() throws IOException {
        boolean initState = WidgetUtil.getAdapter( composite ).isInitialized();
        compositeInitState[ 0 ] = Boolean.valueOf( initState );
      }
    } );
    
    // Ensure that the isInitialized state is to to true *right* after a widget
    // was rendered; as opposed to being set to true after the whole widget
    // tree was rendered
    Fixture.fakeResponseWriter();
    String displayId = DisplayUtil.getId( display );
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    // check precondition
    assertEquals( false, WidgetUtil.getAdapter( composite ).isInitialized() );
    IDisplayLifeCycleAdapter displayLCA = DisplayUtil.getLCA( display );
    displayLCA.render( display );
    assertEquals( Boolean.TRUE, compositeInitState[ 0 ] );
  }
  
  public void testFocusControl() throws IOException {
    Display display = new Display();
    Shell shell = new Shell( display, RWT.NONE );
    Control control = new Button( shell, RWT.PUSH );
    String displayId = DisplayUtil.getId( display );
    String controlId = WidgetUtil.getId( control );
    
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( displayId + ".focusControl", controlId );
    lifeCycle.execute();
    assertEquals( control, display.getFocusControl() );

    RWTFixture.fakeNewRequest();
    Fixture.fakeRequestParam( RequestParams.UIROOT, displayId );
    Fixture.fakeRequestParam( displayId + ".focusControl", "null" );
    new RWTLifeCycle().execute();
    assertEquals( null, display.getFocusControl() );
  }

  protected void tearDown() throws Exception {
    AdapterManager manager = W4TContext.getAdapterManager();
    manager.deregisterAdapters( lifeCycleAdapterFactory, Display.class );
    manager.deregisterAdapters( lifeCycleAdapterFactory, Widget.class );
    manager.deregisterAdapters( widgetAdapterFactory, Display.class );
    manager.deregisterAdapters( widgetAdapterFactory, Widget.class );
    RWTFixture.deregisterResourceManager();
    Fixture.tearDown();
  }

  private void clearLogs() {
    log.clear();
    renderInitLog.clear();
    renderChangesLog.clear();
    renderDisposeLog.clear();
  }
}
