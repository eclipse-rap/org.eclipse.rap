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
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.displaykit;

import static org.eclipse.rap.rwt.internal.lifecycle.DisplayUtil.getAdapter;
import static org.eclipse.rap.rwt.internal.lifecycle.DisplayUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.readPropertyValueAsString;

import java.io.IOException;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.ExitConfirmation;
import org.eclipse.rap.rwt.internal.lifecycle.DisplayLifeCycleAdapter;
import org.eclipse.rap.rwt.internal.lifecycle.DisposedWidgets;
import org.eclipse.rap.rwt.internal.lifecycle.RequestCounter;
import org.eclipse.rap.rwt.internal.lifecycle.UITestUtil;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectLifeCycleAdapter;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.util.ActiveKeysUtil;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetLifeCycleAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.events.EventUtil;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.internal.widgets.IShellAdapter;
import org.eclipse.swt.internal.widgets.WidgetAdapterImpl;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;


public class DisplayLCA implements DisplayLifeCycleAdapter {

  private static final String PROP_REQUEST_COUNTER = "requestCounter";
  static final String PROP_FOCUS_CONTROL = "focusControl";
  static final String PROP_EXIT_CONFIRMATION = "exitConfirmation";
  private static final String METHOD_BEEP = "beep";
  private static final String PROP_RESIZE_LISTENER = "listener_Resize";

  ////////////////////////////////////////////////////////
  // interface implementation of DisplayLifeCycleAdapter

  public void readData( Display display ) {
    readBounds( display );
    readCursorLocation( display );
    readFocusControl( display );
    WidgetTreeVisitor visitor = new AllWidgetTreeVisitor() {
      @Override
      public boolean doVisit( Widget widget ) {
        WidgetLifeCycleAdapter adapter = WidgetUtil.getLCA( widget );
        adapter.readData( widget );
        return true;
      }
    };
    Shell[] shells = getShells( display );
    for( int i = 0; i < shells.length; i++ ) {
      Composite shell = shells[ i ];
      WidgetTreeVisitor.accept( shell, visitor );
    }
    for( int i = 0; i < shells.length; i++ ) {
      if( shells[ i ].getMaximized() || shells[ i ].getFullScreen() ) {
        Object adapter = shells[ i ].getAdapter( IShellAdapter.class );
        IShellAdapter shellAdapter = ( IShellAdapter )adapter;
        shellAdapter.setBounds( display.getBounds() );
      }
    }
    DNDSupport.processEvents();
    RemoteObjectLifeCycleAdapter.readData();
  }

  public void preserveValues( Display display ) {
    WidgetAdapter adapter = getAdapter( display );
    adapter.preserve( PROP_FOCUS_CONTROL, display.getFocusControl() );
    adapter.preserve( PROP_EXIT_CONFIRMATION, getExitConfirmation() );
    adapter.preserve( PROP_RESIZE_LISTENER, Boolean.valueOf( hasResizeListener( display ) ) );
    ActiveKeysUtil.preserveActiveKeys( display );
    ActiveKeysUtil.preserveCancelKeys( display );
    ActiveKeysUtil.preserveMnemonicActivator( display );
    if( adapter.isInitialized() ) {
      Shell[] shells = getShells( display );
      for( int i = 0; i < shells.length; i++ ) {
        WidgetTreeVisitor.accept( shells[ i ], new AllWidgetTreeVisitor() {
          @Override
          public boolean doVisit( Widget widget ) {
            AbstractWidgetLCA widgetLCA = WidgetUtil.getLCA( widget );
            widgetLCA.preserveValues( widget );
            return true;
          }
        } );
      }
    }
  }

  public void render( Display display ) throws IOException {
    disposeWidgets();
    renderRequestCounter();
    renderExitConfirmation( display );
    renderEnableUiTests( display );
    renderShells( display );
    renderFocus( display );
    renderBeep( display );
    renderResizeListener( display );
    renderUICallBack( display );
    markInitialized( display );
    ActiveKeysUtil.renderActiveKeys( display );
    ActiveKeysUtil.renderCancelKeys( display );
    ActiveKeysUtil.renderMnemonicActivator( display );
    RemoteObjectLifeCycleAdapter.render();
  }

  public void clearPreserved( Display display ) {
    WidgetAdapterImpl widgetAdapter = ( WidgetAdapterImpl )getAdapter( display );
    widgetAdapter.clearPreserved();
    Composite[] shells = getShells( display );
    for( int i = 0; i < shells.length; i++ ) {
      WidgetTreeVisitor.accept( shells[ i ], new AllWidgetTreeVisitor() {
        @Override
        public boolean doVisit( Widget widget ) {
          WidgetAdapterImpl widgetAdapter = ( WidgetAdapterImpl )WidgetUtil.getAdapter( widget );
          widgetAdapter.clearPreserved();
          return true;
        }
      } );
    }
  }

  private static void renderShells( Display display ) throws IOException {
    RenderVisitor visitor = new RenderVisitor();
    Composite[] shells = getShells( display );
    for( int i = 0; i < shells.length; i++ ) {
      WidgetTreeVisitor.accept( shells[ i ], visitor );
      visitor.reThrowProblem();
    }
  }

  private static void renderRequestCounter() {
    ProtocolMessageWriter protocolWriter = ContextProvider.getProtocolWriter();
    int requestId = RequestCounter.getInstance().nextRequestId();
    protocolWriter.appendHead( PROP_REQUEST_COUNTER, requestId );
  }

  private static void renderExitConfirmation( Display display ) {
    String exitConfirmation = getExitConfirmation();
    WidgetAdapter adapter = getAdapter( display );
    Object oldExitConfirmation = adapter.getPreserved( PROP_EXIT_CONFIRMATION );
    boolean hasChanged = exitConfirmation == null
                       ? oldExitConfirmation != null
                       : !exitConfirmation.equals( oldExitConfirmation );
    if( hasChanged ) {
      IClientObject clientObject = ClientObjectFactory.getClientObject( display );
      clientObject.set( PROP_EXIT_CONFIRMATION, exitConfirmation );
    }
  }

  private static String getExitConfirmation() {
    ExitConfirmation exitConfirmation = RWT.getClient().getService( ExitConfirmation.class );
    return exitConfirmation == null ? null : exitConfirmation.getMessage();
  }

  /////////////////////////////
  // Helping methods for render

  private static void disposeWidgets() throws IOException {
    Widget[] disposedWidgets = DisposedWidgets.getAll();
    // TODO [rh] get rid of dependency on DragSource/DropTarget
    // Must dispose of DragSources and DropTargets first
    for( int i = disposedWidgets.length - 1; i >= 0; i-- ) {
      Widget toDispose = disposedWidgets[ i ];
      if( toDispose instanceof DragSource || toDispose instanceof DropTarget ) {
        AbstractWidgetLCA lca = WidgetUtil.getLCA( toDispose );
        lca.renderDispose( toDispose );
      }
    }
    // TODO [rst] since widget pooling is removed, the loop should be reverted
    //            again
    // [fappel]: client side disposal order is crucial for the widget
    //           caching mechanism - we need to dispose of children first. This
    //           is reverse to the server side mechanism (which is analog to
    //           SWT).
    for( int i = disposedWidgets.length - 1; i >= 0; i-- ) {
      Widget toDispose = disposedWidgets[ i ];
      if( !( toDispose instanceof DragSource ) && !( toDispose instanceof DropTarget ) ) {
        AbstractWidgetLCA lca = WidgetUtil.getLCA( toDispose );
        lca.renderDispose( toDispose );
      }
    }
  }

  private static void renderFocus( Display display ) {
    if( !display.isDisposed() ) {
      IDisplayAdapter displayAdapter = getDisplayAdapter( display );
      WidgetAdapter widgetAdapter = getAdapter( display );
      Object oldValue = widgetAdapter.getPreserved( PROP_FOCUS_CONTROL );
      if(    !widgetAdapter.isInitialized()
          || oldValue != display.getFocusControl()
          || displayAdapter.isFocusInvalidated() )
      {
        // TODO [rst] Added null check as a NPE occurred in some rare cases
        Control focusControl = display.getFocusControl();
        if( focusControl != null ) {
          IClientObject clientObject = ClientObjectFactory.getClientObject( display );
          clientObject.set( PROP_FOCUS_CONTROL, WidgetUtil.getId( display.getFocusControl() ) );
        }
      }
    }
  }

  private static void renderBeep( Display display ) {
    IDisplayAdapter displayAdapter = getDisplayAdapter( display );
    if( displayAdapter.isBeepCalled() ) {
      displayAdapter.resetBeep();
      IClientObject clientObject = ClientObjectFactory.getClientObject( display );
      clientObject.call( METHOD_BEEP, null );
    }
  }

  private static void renderResizeListener( Display display ) {
    WidgetAdapter adapter = getAdapter( display );
    Boolean oldValue = ( Boolean )adapter.getPreserved( PROP_RESIZE_LISTENER );
    if( oldValue == null ) {
      oldValue = Boolean.FALSE;
    }
    Boolean newValue = Boolean.valueOf( hasResizeListener( display ) );
    if( !oldValue.equals( newValue ) ) {
      IClientObject clientObject = ClientObjectFactory.getClientObject( display );
      clientObject.listen( ClientMessageConst.EVENT_RESIZE, newValue.booleanValue() );
    }
  }

  private static void renderUICallBack( Display display ) {
    new ServerPushRenderer().render();
  }

  private static void renderEnableUiTests( Display display ) {
    if( UITestUtil.isEnabled() ) {
      if( !getAdapter( display ).isInitialized() ) {
        IClientObject clientObject = ClientObjectFactory.getClientObject( display );
        clientObject.set( "enableUiTests", true );
      }
    }
  }

  private static void markInitialized( Display display ) {
    WidgetAdapterImpl adapter = ( WidgetAdapterImpl )getAdapter( display );
    adapter.setInitialized( true );
  }

  static void readBounds( Display display ) {
    Rectangle oldBounds = display.getBounds();
    Rectangle bounds = ProtocolUtil.readPropertyValueAsRectangle( getId( display ),  "bounds" );
    if( bounds == null ) {
      bounds = new Rectangle( 0, 0, oldBounds.width, oldBounds.height );
    }
    getDisplayAdapter( display ).setBounds( bounds );
    processResizeEvent( display );
  }

  private static void readCursorLocation( Display display ) {
    Point location = ProtocolUtil.readPropertyValueAsPoint( getId( display ), "cursorLocation" );
    if( location == null ) {
      location = new Point( 0, 0 );
    }
    getDisplayAdapter( display ).setCursorLocation( location.x, location.y );
  }

  static void readFocusControl( Display display ) {
    // TODO [rh] revise this: traversing the widget tree once more only to find
    //      out which control is focused. Could that be optimized?
    String id = readPropertyValue( display, "focusControl" );
    if( id != null ) {
      Control focusControl = null;
      // Even though the loop below would anyway result in focusControl == null
      // the client may send 'null' to indicate that no control on the active
      // shell currently has the input focus.
      if( !"null".equals(  id ) ) {
        Shell[] shells = getDisplayAdapter( display ).getShells();
        for( int i = 0; focusControl == null && i < shells.length; i++ ) {
          Widget widget = WidgetUtil.find( shells[ i ], id );
          if( widget instanceof Control ) {
            focusControl = ( Control )widget;
          }
        }
      }
      if( focusControl != null && EventUtil.isAccessible( focusControl ) ) {
        getDisplayAdapter( display ).setFocusControl( focusControl, false );
      }
    }
  }

  private static String readPropertyValue( Display display, String propertyName ) {
    return readPropertyValueAsString( getId( display ), propertyName );
  }

  private static void processResizeEvent( Display display ) {
    if( ProtocolUtil.wasEventSent( getId( display ), ClientMessageConst.EVENT_RESIZE ) ) {
      getDisplayAdapter( display ).notifyListeners( SWT.Resize, createResizeEvent( display ) );
    }
  }

  private static Event createResizeEvent( Display display ) {
    Event result = new Event();
    result.display = display;
    result.type = SWT.Resize;
    result.time = EventUtil.getLastEventTime();
    Rectangle bounds = display.getBounds();
    result.x = bounds.x;
    result.y = bounds.y;
    result.width = bounds.width;
    result.height = bounds.height;
    return result;
  }

  private static boolean hasResizeListener( Display display ) {
    IDisplayAdapter displayAdapter = getDisplayAdapter( display );
    return hasMaximizedShell( display ) ? true : displayAdapter.isListening( SWT.Resize );
  }

  private static boolean hasMaximizedShell( Display display ) {
    boolean result = false;
    Shell[] shells = getShells( display );
    for( Shell shell : shells ) {
      if( shell.getFullScreen() || shell.getMaximized() ) {
        result = true;
      }
    }
    return result;
  }

  private static IDisplayAdapter getDisplayAdapter( Display display ) {
    return display.getAdapter( IDisplayAdapter.class );
  }

  private static Shell[] getShells( Display display ) {
    return getDisplayAdapter( display ).getShells();
  }

  private static final class RenderVisitor extends AllWidgetTreeVisitor {

    private IOException ioProblem;

    @Override
    public boolean doVisit( Widget widget ) {
      ioProblem = null;
      boolean result = true;
      try {
        render( widget );
        runRenderRunnable( widget );
      } catch( IOException ioe ) {
        ioProblem = ioe;
        result = false;
      }
      return result;
    }

    private void reThrowProblem() throws IOException {
      if( ioProblem != null ) {
        throw ioProblem;
      }
    }

    private static void render( Widget widget ) throws IOException {
      WidgetUtil.getLCA( widget ).render( widget );
    }

    private static void runRenderRunnable( Widget widget )
      throws IOException
    {
      WidgetAdapterImpl adapter = ( WidgetAdapterImpl )WidgetUtil.getAdapter( widget );
      if( adapter.getRenderRunnable() != null ) {
        adapter.getRenderRunnable().afterRender();
        adapter.clearRenderRunnable();
      }
    }
  }

}
