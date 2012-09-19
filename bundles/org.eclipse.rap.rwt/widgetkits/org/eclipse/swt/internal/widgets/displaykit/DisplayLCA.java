/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
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

import static org.eclipse.rap.rwt.internal.lifecycle.DisplayUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.readPropertyValueAsString;

import java.io.IOException;
import org.eclipse.rap.rwt.branding.AbstractBranding;
import org.eclipse.rap.rwt.internal.application.RWTFactory;
import org.eclipse.rap.rwt.internal.branding.BrandingUtil;
import org.eclipse.rap.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rap.rwt.internal.lifecycle.DisposedWidgets;
import org.eclipse.rap.rwt.internal.lifecycle.IDisplayLifeCycleAdapter;
import org.eclipse.rap.rwt.internal.lifecycle.RWTRequestVersionControl;
import org.eclipse.rap.rwt.internal.lifecycle.UITestUtil;
import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.RequestParams;
import org.eclipse.rap.rwt.internal.theme.Theme;
import org.eclipse.rap.rwt.internal.theme.ThemeUtil;
import org.eclipse.rap.rwt.internal.uicallback.UICallBackServiceHandler;
import org.eclipse.rap.rwt.internal.util.ActiveKeysUtil;
import org.eclipse.rap.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.IWidgetLifeCycleAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.EventUtil;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.internal.widgets.IShellAdapter;
import org.eclipse.swt.internal.widgets.WidgetAdapter;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;


public class DisplayLCA implements IDisplayLifeCycleAdapter {

  static final String PROP_REQUEST_COUNTER = "requestCounter";
  static final String PROP_FOCUS_CONTROL = "focusControl";
  static final String PROP_CURRENT_THEME = "currentTheme";
  static final String PROP_EXIT_CONFIRMATION = "exitConfirmation";
  private static final String METHOD_BEEP = "beep";

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
      WidgetAdapter adapter = ( WidgetAdapter )WidgetUtil.getAdapter( widget );
      if( adapter.getRenderRunnable() != null ) {
        adapter.getRenderRunnable().afterRender();
        adapter.clearRenderRunnable();
      }
    }
  }

  ////////////////////////////////////////////////////////
  // interface implementation of IDisplayLifeCycleAdapter

  public void readData( Display display ) {
    readBounds( display );
    readCursorLocation( display );
    readFocusControl( display );
    WidgetTreeVisitor visitor = new AllWidgetTreeVisitor() {
      @Override
      public boolean doVisit( Widget widget ) {
        IWidgetLifeCycleAdapter adapter = WidgetUtil.getLCA( widget );
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
  }

  public void preserveValues( Display display ) {
    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );
    adapter.preserve( PROP_FOCUS_CONTROL, display.getFocusControl() );
    adapter.preserve( PROP_CURRENT_THEME, ThemeUtil.getCurrentThemeId() );
    adapter.preserve( PROP_EXIT_CONFIRMATION, getExitConfirmation() );
    ActiveKeysUtil.preserveActiveKeys( display );
    ActiveKeysUtil.preserveCancelKeys( display );
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
    // Note [rst] Startup page created in LifecycleServiceHandler#runLifeCycle
    // TODO [rh] should be replaced by requestCounter != 0
    if( ProtocolUtil.readHeaderPropertyValue( RequestParams.UIROOT ) != null ) {
      disposeWidgets();
      renderRequestCounter();
      renderTheme( display );
      renderExitConfirmation( display );
      renderEnableUiTests( display );
      renderShells( display );
      renderFocus( display );
      renderBeep( display );
      writeUICallBackActivation( display );
      markInitialized( display );
      ActiveKeysUtil.renderActiveKeys( display );
      ActiveKeysUtil.renderCancelKeys( display );
    }
  }

  public void clearPreserved( Display display ) {
    WidgetAdapter widgetAdapter = ( WidgetAdapter )DisplayUtil.getAdapter( display );
    widgetAdapter.clearPreserved();
    Composite[] shells = getShells( display );
    for( int i = 0; i < shells.length; i++ ) {
      WidgetTreeVisitor.accept( shells[ i ], new AllWidgetTreeVisitor() {
        @Override
        public boolean doVisit( Widget widget ) {
          WidgetAdapter widgetAdapter = ( WidgetAdapter )WidgetUtil.getAdapter( widget );
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
    Integer requestId = RWTRequestVersionControl.getInstance().nextRequestId();
    protocolWriter.appendMeta( PROP_REQUEST_COUNTER, requestId.intValue() );
  }

  private static void renderTheme( Display display ) {
    String currThemeId = ThemeUtil.getCurrentThemeId();
    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );
    Object oldThemeId = adapter.getPreserved( PROP_CURRENT_THEME );
    if( !currThemeId.equals( oldThemeId ) ) {
      Theme theme = RWTFactory.getThemeManager().getTheme( currThemeId );
      IClientObject clientObject = ClientObjectFactory.getClientObject( display );
      clientObject.set( PROP_CURRENT_THEME, theme.getJsId() );
    }
  }

  private static void renderExitConfirmation( Display display ) {
    String exitConfirmation = getExitConfirmation();
    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );
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
    AbstractBranding branding = BrandingUtil.determineBranding();
    String result = null; // does not display exit dialog
    if( branding.showExitConfirmation() ) {
      result = branding.getExitConfirmationText();
      if( result == null ) {
        result = ""; // displays an exit dialog with empty message
      }
    }
    return result;
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
      if(    !( toDispose instanceof DragSource )
          && !( toDispose instanceof DropTarget ) )
      {
        AbstractWidgetLCA lca = WidgetUtil.getLCA( toDispose );
        lca.renderDispose( toDispose );
      }
    }
  }

  private static void renderFocus( Display display ) {
    if( !display.isDisposed() ) {
      IDisplayAdapter displayAdapter = getDisplayAdapter( display );
      IWidgetAdapter widgetAdapter = DisplayUtil.getAdapter( display );
      Object oldValue = widgetAdapter.getPreserved( PROP_FOCUS_CONTROL );
      if(    !widgetAdapter.isInitialized()
          || oldValue != display.getFocusControl()
          || displayAdapter.isFocusInvalidated() )
      {
        // TODO [rst] Added null check as a NPE occurred in some rare cases
        Control focusControl = display.getFocusControl();
        if( focusControl != null ) {
          IClientObject clientObject = ClientObjectFactory.getClientObject( display );
          clientObject.set( PROP_FOCUS_CONTROL,
                                    WidgetUtil.getId( display.getFocusControl() ) );
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

  private static void renderEnableUiTests( Display display ) {
    if( UITestUtil.isEnabled() ) {
      WidgetAdapter adapter = ( WidgetAdapter )DisplayUtil.getAdapter( display );
      if( !adapter.isInitialized() ) {
        IClientObject clientObject = ClientObjectFactory.getClientObject( display );
        clientObject.set( "enableUiTests", true );
      }
    }
  }

  private static void writeUICallBackActivation( Display display ) {
    ProtocolMessageWriter protocolWriter = ContextProvider.getProtocolWriter();
    UICallBackServiceHandler.writeUICallBackActivation( protocolWriter );
  }

  private static void markInitialized( Display display ) {
    WidgetAdapter adapter = ( WidgetAdapter )DisplayUtil.getAdapter( display );
    adapter.setInitialized( true );
  }

  static void readBounds( Display display ) {
    Rectangle oldBounds = display.getBounds();
    int width = readIntPropertyValue( display, "bounds.width", oldBounds.width );
    int height = readIntPropertyValue( display, "bounds.height", oldBounds.height );
    Rectangle bounds = new Rectangle( 0, 0, width, height );
    getDisplayAdapter( display ).setBounds( bounds );
  }

  private static void readCursorLocation( Display display ) {
    int x = readIntPropertyValue( display, "cursorLocation.x", 0 );
    int y = readIntPropertyValue( display, "cursorLocation.y", 0 );
    getDisplayAdapter( display ).setCursorLocation( x, y );
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
        getDisplayAdapter( display ).setFocusControl( focusControl );
      }
    }
  }

  private static String readPropertyValue( Display display, String propertyName ) {
    return readPropertyValueAsString( getId( display ), propertyName );
  }

  private static int readIntPropertyValue( Display display, String propertyName, int defaultValue )
  {
    String value = readPropertyValue( display, propertyName );
    int result;
    if( value == null ) {
      result = defaultValue;
    } else {
      result = NumberFormatUtil.parseInt( value );
    }
    return result;
  }

  private static IDisplayAdapter getDisplayAdapter( Display display ) {
    Object adapter = display.getAdapter( IDisplayAdapter.class );
    return ( IDisplayAdapter )adapter;
  }

  private static Shell[] getShells( Display display ) {
    return getDisplayAdapter( display ).getShells();
  }
}
