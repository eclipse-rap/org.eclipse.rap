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

package org.eclipse.swt.internal.widgets.shellkit;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.internal.widgets.*;
import org.eclipse.swt.widgets.*;


public final class ShellLCA extends AbstractWidgetLCA {

  private static final String QX_TYPE = "org.eclipse.swt.widgets.Shell";
  private static final String TYPE_POOL_ID = ShellLCA.class.getName();

  private static final String PROP_TEXT = "text";
  private static final String PROP_IMAGE = "image";
  private static final String PROP_ACTIVE_CONTROL = "activeControl";
  private static final String PROP_ACTIVE_SHELL = "activeShell";
  private static final String PROP_MODE = "mode";
  private static final String PROP_SHELL_LISTENER = "shellListener";

  private static final JSListenerInfo JS_CLOSE_LISTENER_INFO
    = new JSListenerInfo( "close",
                          "org.eclipse.swt.widgets.Shell.onClose",
                          JSListenerType.STATE_AND_ACTION );

  public void preserveValues( final Widget widget ) {
    ControlLCAUtil.preserveValues( ( Control )widget );
    Shell shell = ( Shell )widget;
    IWidgetAdapter adapter = WidgetUtil.getAdapter( shell );
    adapter.preserve( PROP_ACTIVE_CONTROL, getActiveControl( shell ) );
    adapter.preserve( PROP_ACTIVE_SHELL, shell.getDisplay().getActiveShell() );
    adapter.preserve( PROP_TEXT, shell.getText() );
    adapter.preserve( PROP_IMAGE, shell.getImage() );
    adapter.preserve( PROP_MODE, getMode( shell ) );
    adapter.preserve( PROP_SHELL_LISTENER, 
                      Boolean.valueOf( ShellEvent.hasListener( shell ) ) );
  }

  public void readData( final Widget widget ) {
    Shell shell = ( Shell )widget;
    ControlLCAUtil.readBounds( shell );
    if( WidgetLCAUtil.wasEventSent( shell, JSConst.EVENT_SHELL_CLOSED ) ) {
      shell.close();
    }
    processActiveShell( shell );
    processActivate( shell );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    Shell shell = ( Shell )widget;
    writer.newWidget( QX_TYPE, null );
    ControlLCAUtil.writeStyleFlags( shell );
    int style = widget.getStyle();
    if( ( style & SWT.APPLICATION_MODAL ) != 0 ) {
      writer.set( "modal", true );
    }
    if( ( style & SWT.TITLE ) != 0 ) {
      writer.call( "addState", new Object[]{ "rwt_TITLE" } );
    }
    writer.call( "fixTitlebar", new Object[ 0 ] );
    writer.set( "showMinimize", ( style & SWT.MIN ) != 0 );
    writer.set( "allowMinimize", ( style & SWT.MIN ) != 0 );
    writer.set( "showMaximize", ( style & SWT.MAX ) != 0 );
    writer.set( "allowMaximize", ( style & SWT.MAX ) != 0 );
    writer.set( "showClose", ( style & SWT.CLOSE ) != 0 );
    writer.set( "allowClose", ( style & SWT.CLOSE ) != 0 );
    Boolean resizable = Boolean.valueOf( ( style & SWT.RESIZE ) != 0 );
    writer.set( "resizable",
                new Object[] { resizable, resizable, resizable, resizable } );
    writer.set( "alwaysOnTop", ( style & SWT.ON_TOP ) != 0 );
    if( ( shell.getParent() instanceof Shell ) ) {
      writer.set( "dialogMode", true );
    }
    ControlLCAUtil.writeResizeNotificator( widget );
    ControlLCAUtil.writeMoveNotificator( widget );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Shell shell = ( Shell )widget;
    // TODO [rst] Do not render bounds if shell is maximized
    ControlLCAUtil.writeChanges( shell );
    writeImage( shell );
    writeText( shell );
    // Important: Order matters, writing setActive() before open() leads to
    //            strange behavior!
    writeOpen( shell );
    writeActiveShell( shell );
    writeMode( shell );
    writeCloseListener( shell );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.set( "visibility", false );
    writer.dispose();
  }

  public void createResetHandlerCalls( final String typePoolId )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.call( "removeState", new Object[]{ "rwt_TITLE" } );
    writer.set( "modal", false );
    writer.set( "dialogMode", false );
    ControlLCAUtil.resetStyleFlags();
    ControlLCAUtil.resetResizeNotificator();
    ControlLCAUtil.resetMoveNotificator();
    // TODO [rh] reset close listener when shell pooling is activated
    ControlLCAUtil.resetBounds();
    ControlLCAUtil.resetZIndex();
    ControlLCAUtil.resetToolTip();
  }

  public String getTypePoolId( final Widget widget ) {
//    return TYPE_POOL_ID;
    return null;
  }

  //////////////////
  // Helping methods

  private static void writeText( final Shell shell ) throws IOException {
    String text = shell.getText();
    if( WidgetLCAUtil.hasChanged( shell, PROP_TEXT, text, "" ) ) {
      JSWriter writer = JSWriter.getWriterFor( shell );
      text = WidgetLCAUtil.escapeText( text, false );
      writer.set( JSConst.QX_FIELD_CAPTION, text );
    }
  }

  private static void writeOpen( final Shell shell ) throws IOException {
    // TODO [rst] workaround: qx window should be opened only once.
    Boolean defValue = Boolean.FALSE;
    Boolean actValue = Boolean.valueOf( shell.getVisible() );
    if(    WidgetLCAUtil.hasChanged( shell, Props.VISIBLE, actValue, defValue )
        && shell.getVisible() )
    {
      JSWriter writer = JSWriter.getWriterFor( shell );
      writer.call( "open", null );
    }
  }

  /////////////////////////////////////////////
  // Methods to read and write the active shell

  private static void writeActiveShell( final Shell shell ) throws IOException {
    Shell activeShell = shell.getDisplay().getActiveShell();
    boolean hasChanged
      = WidgetLCAUtil.hasChanged( shell, PROP_ACTIVE_SHELL, activeShell, null );
    if( shell == activeShell && hasChanged ) {
      JSWriter writer = JSWriter.getWriterFor( shell );
      writer.set( "active", true );
    }
  }

  private static void processActiveShell( final Shell shell ) {
    if( WidgetLCAUtil.wasEventSent( shell, JSConst.EVENT_SHELL_ACTIVATED ) ) {
      Shell lastActiveShell = shell.getDisplay().getActiveShell();
      setActiveShell( shell );
      ActivateEvent event;
      event = new ActivateEvent( lastActiveShell, ActivateEvent.DEACTIVATED );
      event.processEvent();
      event = new ActivateEvent( shell, ActivateEvent.ACTIVATED );
      event.processEvent();
      ShellEvent shellEvent;
      shellEvent
        = new ShellEvent( lastActiveShell, ShellEvent.SHELL_DEACTIVATED );
      shellEvent.processEvent();
      shellEvent = new ShellEvent( shell, ShellEvent.SHELL_ACTIVATED );
      shellEvent.processEvent();
    } else {
      String displayId = DisplayUtil.getId( shell.getDisplay() );
      HttpServletRequest request = ContextProvider.getRequest();
      String activeShellId = request.getParameter( displayId + ".activeShell" );
      if( WidgetUtil.getId( shell ).equals( activeShellId ) ) {
        setActiveShell( shell );
      }
    }
  }

  private static void setActiveShell( final Shell shell ) {
    Object adapter = shell.getDisplay().getAdapter( IDisplayAdapter.class );
    IDisplayAdapter displayAdapter = ( IDisplayAdapter )adapter;
    displayAdapter.setActiveShell( shell );
  }

  /////////////////////////////////////////////////////
  // Methods to handle activeControl and ActivateEvents

  /* (intentionally non-JavaDoc'ed)
   * This method is declared public only to be accessible from DisplayLCA
   */
  public static void writeActiveControl( final Shell shell ) throws IOException
  {
    final Control activeControl = getActiveControl( shell );
    String prop = PROP_ACTIVE_CONTROL;
    if( WidgetLCAUtil.hasChanged( shell, prop, activeControl, null ) ) {
      JSWriter writer = JSWriter.getWriterFor( shell );
      writer.set( "activeControl", new Object[] { activeControl } );
    }
  }

  // TODO [rh] is this safe for multiple shells?
  private static void processActivate( final Shell shell ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String widgetId = request.getParameter( JSConst.EVENT_WIDGET_ACTIVATED );
    if( widgetId != null ) {
      Widget widget = WidgetUtil.find( shell, widgetId );
      if( widget != null ) {
        setActiveControl( shell, widget );
      }
    } else {
      String activeControlId
        = WidgetLCAUtil.readPropertyValue( shell, "activeControl" );
      Widget widget = WidgetUtil.find( shell, activeControlId );
      if( widget != null ) {
        setActiveControl( shell, widget );
      }
    }
  }

  private static Control getActiveControl( final Shell shell ) {
    Object adapter = shell.getAdapter( IShellAdapter.class );
    IShellAdapter shellAdapter = ( IShellAdapter )adapter;
    Control activeControl = shellAdapter.getActiveControl();
    return activeControl;
  }

  private static void setActiveControl( final Shell shell, final Widget widget )
  {
    if( EventUtil.isAccessible( widget ) ) {
      Object adapter = shell.getAdapter( IShellAdapter.class );
      IShellAdapter shellAdapter = ( IShellAdapter )adapter;
      shellAdapter.setActiveControl( ( Control )widget );
    }
  }

  private static void writeImage( final Shell shell ) throws IOException {
    if( showImage( shell ) ) {
      Image image = shell.getImage();
      if( WidgetLCAUtil.hasChanged( shell, PROP_IMAGE, image, null ) ) {
        JSWriter writer = JSWriter.getWriterFor( shell );
        writer.set( JSConst.QX_FIELD_ICON, ResourceFactory.getImagePath( image ) );
      }
    }
  }

  private static boolean showImage( final Shell shell ) {
    return ( shell.getStyle() & ( SWT.MIN | SWT.MAX | SWT.CLOSE ) ) != 0;
  }

  private static void writeMode( final Shell shell ) throws IOException {
    Object defValue = null;
    Object newValue = getMode( shell );
    if( WidgetLCAUtil.hasChanged( shell, PROP_MODE, newValue, defValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( shell );
      writer.set( "mode", newValue );
    }
  }

  private static void writeCloseListener( final Shell shell ) throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( shell );
    writer.updateListener( JS_CLOSE_LISTENER_INFO, 
                           PROP_SHELL_LISTENER, 
                           ShellEvent.hasListener( shell ) );
  }

  private static String getMode( final Shell shell ) {
    String result = null;
    if( shell.getMinimized() ) {
      result = "minimized";
    } else if( shell.getMaximized() ) {
      result = "maximized";
    }
    return result;
  }
}
