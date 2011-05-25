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
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.displaykit;

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.RWTMessages;
import org.eclipse.rwt.internal.branding.BrandingUtil;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.theme.*;
import org.eclipse.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.*;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;
import org.eclipse.swt.internal.widgets.shellkit.ShellLCA;
import org.eclipse.swt.widgets.*;


public class DisplayLCA implements IDisplayLifeCycleAdapter {

  private final static String PATTERN_REQUEST_COUNTER
    =   "var req = org.eclipse.swt.Request.getInstance();"
      + "req.setRequestCounter( \"{0,number,#}\" );";

  static final String PROP_FOCUS_CONTROL = "focusControl";
  static final String PROP_CURR_THEME = "currTheme";
  static final String PROP_EXIT_CONFIRMATION = "exitConfirmation";
  static final String PROP_TIMEOUT_PAGE = "timeoutPage";

  private static final class RenderVisitor extends AllWidgetTreeVisitor {

    private IOException ioProblem;

    public boolean doVisit( final Widget widget ) {
      ioProblem = null;
      boolean result = true;
      try {
        render( widget );
        runRenderRunnable( widget );
      } catch( final IOException ioe ) {
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

    private static void render( final Widget widget ) throws IOException {
      WidgetUtil.getLCA( widget ).render( widget );
    }

    private static void runRenderRunnable( final Widget widget )
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
    ActiveKeysUtil.readKeyEvents( display );
    WidgetTreeVisitor visitor = new AllWidgetTreeVisitor() {
      public boolean doVisit( final Widget widget ) {
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

  public void preserveValues( final Display display ) {
    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );
    adapter.preserve( PROP_FOCUS_CONTROL, display.getFocusControl() );
    adapter.preserve( PROP_CURR_THEME, ThemeUtil.getCurrentThemeId() );
    adapter.preserve( PROP_TIMEOUT_PAGE, getTimeoutPage() );
    adapter.preserve( PROP_EXIT_CONFIRMATION, getExitConfirmation() );
    ActiveKeysUtil.preserveActiveKeys( display );
    if( adapter.isInitialized() ) {
      Shell[] shells = getShells( display );
      for( int i = 0; i < shells.length; i++ ) {
        WidgetTreeVisitor.accept( shells[ i ], new AllWidgetTreeVisitor() {
          public boolean doVisit( final Widget widget ) {
            AbstractWidgetLCA widgetLCA = WidgetUtil.getLCA( widget );
            widgetLCA.preserveValues( widget );
            return true;
          }
        } );
      }
    }
  }

  public void render( final Display display ) throws IOException {
    HttpServletRequest request = ContextProvider.getRequest();
    // Note [rst] Startup page created in LifecycleServiceHandler#runLifeCycle
    // TODO [rh] should be replaced by requestCounter != 0
    if( request.getParameter( RequestParams.UIROOT ) != null ) {
      disposeWidgets();
      writeRequestCounter();
      writeTheme( display );
      writeErrorPages( display );
      writeExitConfirmation( display );
      renderShells( display );
      writeActiveControls( display );
      writeFocus( display );
      writeUICallBackActivation( display );
      markInitialized( display );
      ActiveKeysUtil.writeActiveKeys( display );
    }
  }
  
  public void clearPreserved( Display display ) {
    WidgetAdapter widgetAdapter = ( WidgetAdapter )DisplayUtil.getAdapter( display );
    widgetAdapter.clearPreserved();
    Composite[] shells = getShells( display );
    for( int i = 0; i < shells.length; i++ ) {
      WidgetTreeVisitor.accept( shells[ i ], new AllWidgetTreeVisitor() {
        public boolean doVisit( final Widget widget ) {
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

  private static void writeRequestCounter() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    JavaScriptResponseWriter responseWriter = stateInfo.getResponseWriter();
    Object[] args = new Object[] { RWTRequestVersionControl.getInstance().nextRequestId() };
    responseWriter.write( MessageFormat.format( PATTERN_REQUEST_COUNTER, args ) );
  }

  private static void writeTheme( Display display ) {
    String currThemeId = ThemeUtil.getCurrentThemeId();
    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );
    Object oldThemeId = adapter.getPreserved( PROP_CURR_THEME );
    if( !currThemeId.equals( oldThemeId ) ) {
      Theme theme = ThemeManager.getInstance().getTheme( currThemeId );
      StringBuffer buffer = new StringBuffer();
      buffer.append( "qx.theme.manager.Meta.getInstance().setTheme( " );
      buffer.append( theme.getJsId() );
      buffer.append( " );" );
      IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
      JavaScriptResponseWriter responseWriter = stateInfo.getResponseWriter();
      responseWriter.write( buffer.toString() );
    }
  }

  private static void writeErrorPages( Display display ) {
    String timeoutPage = getTimeoutPage();
    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );
    Object oldTimeoutPage = adapter.getPreserved( PROP_TIMEOUT_PAGE );
    if( !timeoutPage.equals( oldTimeoutPage ) ) {
      String pattern = "org.eclipse.swt.Request.getInstance().setTimeoutPage( \"{0}\" );";
      Object[] param = new Object[] { timeoutPage };
      String jsCode = MessageFormat.format( pattern, param );
      IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
      JavaScriptResponseWriter responseWriter = stateInfo.getResponseWriter();
      responseWriter.write( jsCode );
    }
  }

  private static String getTimeoutPage() {
    String timeoutTitle = RWTMessages.getMessage( "RWT_SessionTimeoutPageTitle" );
    String timeoutHeadline = RWTMessages.getMessage( "RWT_SessionTimeoutPageHeadline" );
    String pattern = RWTMessages.getMessage( "RWT_SessionTimeoutPageMessage" );
    Object[] arguments = new Object[]{ "<a {HREF_URL}>", "</a>" };
    String timeoutMessage = MessageFormat.format( pattern, arguments );
    // TODO Escape umlauts etc
    String timeoutPage = "<html><head><title>"
                         + timeoutTitle
                         + "</title></head><body><p>"
                         + timeoutHeadline
                         + "</p><p>"
                         + timeoutMessage
                         + "</p></body></html>";
    return timeoutPage;
  }

  private static void writeExitConfirmation( Display display ) {
    String exitConfirmation = getExitConfirmation();
    IWidgetAdapter adapter = DisplayUtil.getAdapter( display );
    Object oldExitConfirmation = adapter.getPreserved( PROP_EXIT_CONFIRMATION );
    boolean hasChanged = exitConfirmation == null
                       ? oldExitConfirmation != null
                       : !exitConfirmation.equals( oldExitConfirmation );
    if( hasChanged ) {
      String exitConfirmationStr = exitConfirmation == null
                                   ? "null"
                                   : "\"" + exitConfirmation + "\"";
      String code = "qx.core.Init.getInstance().getApplication()"
                    + ".setExitConfirmation( "
                    + exitConfirmationStr
                    + " );";
      IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
      JavaScriptResponseWriter responseWriter = stateInfo.getResponseWriter();
      responseWriter.write( code );
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

  static void registerResources() {
    new ClientResources( RWT.getResourceManager() ).registerResources();
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

  private static void writeFocus( Display display ) {
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
          // TODO [rh] use JSWriter to output focus JavaScript
          StringBuffer buffer = new StringBuffer();
          buffer.append( "org.eclipse.swt.WidgetManager.getInstance()." );
          buffer.append( "focus( \"" );
          buffer.append( WidgetUtil.getId( display.getFocusControl() ) );
          buffer.append( "\" );" );
          IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
          JavaScriptResponseWriter responseWriter = stateInfo.getResponseWriter();
          responseWriter.write( buffer.toString() );
        }
      }
    }
  }

  private static void writeUICallBackActivation( Display display ) {
    if( !display.isDisposed() ) {
      UICallBackServiceHandler.writeActivation();
    }
  }

  // TODO [rh] writing activeControl should be handled by the ShellLCA itself
  //      The reason why this is currently done here is, that the control to
  //      activate might not yet be created client-side, when ShellLCA writes
  //      the statement to set the active control.
  private static void writeActiveControls( final Display display )
    throws IOException
  {
    Shell[] shells = getShells( display );
    for( int i = 0; i < shells.length; i++ ) {
      ShellLCA.writeActiveControl( shells[ i ] );
    }
  }

  private static void markInitialized( final Display display ) {
    WidgetAdapter adapter = ( WidgetAdapter )DisplayUtil.getAdapter( display );
    adapter.setInitialized( true );
  }

  static void readBounds( final Display display ) {
    Rectangle oldBounds = display.getBounds();
    int width
      = readIntPropertyValue( display, "bounds.width", oldBounds.width );
    int height
      = readIntPropertyValue( display, "bounds.height", oldBounds.height );
    Rectangle bounds = new Rectangle( 0, 0, width, height );
    getDisplayAdapter( display ).setBounds( bounds );
  }

  private static void readCursorLocation( final Display display ) {
    int x = readIntPropertyValue( display, "cursorLocation.x", 0 );
    int y = readIntPropertyValue( display, "cursorLocation.y", 0 );
    getDisplayAdapter( display ).setCursorLocation( x, y );
  }

  static void readFocusControl( final Display display ) {
    // TODO [rh] revise this: traversing the widget tree once more only to find
    //      out which control is focused. Could that be optimized?
    HttpServletRequest request = ContextProvider.getRequest();
    StringBuffer focusControlParam = new StringBuffer();
    focusControlParam.append( DisplayUtil.getId( display ) );
    focusControlParam.append( ".focusControl" );
    String id = request.getParameter( focusControlParam.toString() );
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

  private static String readPropertyValue( final Display display,
                                           final String propertyName )
  {
    HttpServletRequest request = ContextProvider.getRequest();
    StringBuffer key = new StringBuffer();
    key.append( DisplayUtil.getId( display ) );
    key.append( "." );
    key.append( propertyName );
    return request.getParameter( key.toString() );
  }

  private static int readIntPropertyValue( final Display display,
                                           final String propertyName,
                                           final int defaultValue )
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

  private static IDisplayAdapter getDisplayAdapter( final Display display ) {
    Object adapter = display.getAdapter( IDisplayAdapter.class );
    return ( IDisplayAdapter )adapter;
  }

  private static Shell[] getShells( final Display display ) {
    return getDisplayAdapter( display ).getShells();
  }
}
