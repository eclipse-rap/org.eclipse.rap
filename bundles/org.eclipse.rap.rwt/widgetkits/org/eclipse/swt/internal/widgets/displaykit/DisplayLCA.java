/*******************************************************************************
 * Copyright (c) 2002, 2014 Innoopract Informationssysteme GmbH and others.
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
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_RESIZE;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.handleOperation;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;

import java.io.IOException;
import java.util.List;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.ExitConfirmation;
import org.eclipse.rap.rwt.internal.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.internal.lifecycle.DisplayLifeCycleAdapter;
import org.eclipse.rap.rwt.internal.lifecycle.DisposedWidgets;
import org.eclipse.rap.rwt.internal.lifecycle.RequestCounter;
import org.eclipse.rap.rwt.internal.lifecycle.UITestUtil;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.Operation;
import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectLifeCycleAdapter;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.util.ActiveKeysUtil;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.internal.widgets.WidgetAdapterImpl;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;


public class DisplayLCA implements DisplayLifeCycleAdapter {

  private static final String PROP_REQUEST_COUNTER = "requestCounter";
  static final String PROP_FOCUS_CONTROL = "focusControl";
  static final String PROP_EXIT_CONFIRMATION = "exitConfirmation";
  private static final String METHOD_BEEP = "beep";
  private static final String PROP_RESIZE_LISTENER = "listener_Resize";

  public void readData( Display display ) {
    handleOperations( display );
    visitWidgets( display );
    DNDSupport.handleOperations();
    RemoteObjectLifeCycleAdapter.readData( ProtocolUtil.getClientMessage() );
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

  private static void handleOperations( Display display ) {
    ClientMessage clientMessage = ProtocolUtil.getClientMessage();
    List<Operation> operations = clientMessage .getAllOperationsFor( getId( display ) );
    if( !operations.isEmpty() ) {
      OperationHandler handler = new DisplayOperationHandler( display );
      for( Operation operation : operations ) {
        handleOperation( handler, operation );
      }
    }
  }

  private static void visitWidgets( Display display ) {
    WidgetTreeVisitor visitor = new AllWidgetTreeVisitor() {
      @Override
      public boolean doVisit( Widget widget ) {
        WidgetUtil.getLCA( widget ).readData( widget );
        return true;
      }
    };
    for( Shell shell : getShells( display ) ) {
      WidgetTreeVisitor.accept( shell, visitor );
    }
  }

  private static void renderShells( Display display ) throws IOException {
    RenderVisitor visitor = new RenderVisitor();
    for( Shell shell : getShells( display ) ) {
      WidgetTreeVisitor.accept( shell, visitor );
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
      getRemoteObject( display ).set( PROP_EXIT_CONFIRMATION, exitConfirmation );
    }
  }

  private static String getExitConfirmation() {
    ExitConfirmation exitConfirmation = RWT.getClient().getService( ExitConfirmation.class );
    return exitConfirmation == null ? null : exitConfirmation.getMessage();
  }

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
          getRemoteObject( display ).set( PROP_FOCUS_CONTROL, getId( display.getFocusControl() ) );
        }
      }
    }
  }

  private static void renderBeep( Display display ) {
    IDisplayAdapter displayAdapter = getDisplayAdapter( display );
    if( displayAdapter.isBeepCalled() ) {
      displayAdapter.resetBeep();
      getRemoteObject( display ).call( METHOD_BEEP, null );
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
      getRemoteObject( display ).listen( EVENT_RESIZE, newValue.booleanValue() );
    }
  }

  private static void renderUICallBack( Display display ) {
    new ServerPushRenderer().render();
  }

  private static void renderEnableUiTests( Display display ) {
    if( UITestUtil.isEnabled() ) {
      if( !getAdapter( display ).isInitialized() ) {
        RemoteObjectFactory.getRemoteObject( display ).set( "enableUiTests", true );
      }
    }
  }

  private static void markInitialized( Display display ) {
    ( ( WidgetAdapterImpl )getAdapter( display ) ).setInitialized( true );
  }

  private static boolean hasResizeListener( Display display ) {
    return getDisplayAdapter( display ).isListening( SWT.Resize );
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

    private static void runRenderRunnable( Widget widget ) throws IOException {
      WidgetAdapterImpl adapter = ( WidgetAdapterImpl )WidgetUtil.getAdapter( widget );
      if( adapter.getRenderRunnable() != null ) {
        adapter.getRenderRunnable().afterRender();
        adapter.clearRenderRunnable();
      }
    }
  }

}
