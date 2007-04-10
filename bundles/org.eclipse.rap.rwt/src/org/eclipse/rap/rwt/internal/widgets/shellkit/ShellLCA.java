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

package org.eclipse.rap.rwt.internal.widgets.shellkit;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.ActivateEvent;
import org.eclipse.rap.rwt.events.ShellEvent;
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.engine.service.ContextProvider;


public final class ShellLCA extends AbstractWidgetLCA {
  
  private static final String PROP_ACTIVE_CONTROL = "activeControl";
  private static final String PROP_ACTIVE_SHELL = "activeShell";
  private static final String PROP_MODE = "mode";

  public void preserveValues( final Widget widget ) {
    ControlLCAUtil.preserveValues( ( Control )widget );
    Shell shell = ( Shell )widget;
    IWidgetAdapter adapter = WidgetUtil.getAdapter( shell );
    adapter.preserve( PROP_ACTIVE_CONTROL, getActiveControl( shell ) );
    adapter.preserve( PROP_ACTIVE_SHELL, shell.getDisplay().getActiveShell() );
    adapter.preserve( Props.TEXT, shell.getText() );
    adapter.preserve( Props.IMAGE, shell.getImage() );
    adapter.preserve( PROP_MODE, getMode( shell ) );
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
    // TODO [rst] Setting the "icon" property on a qx.ui.window.Window does not
    // work with the current qx version. Remove this workaround as soon as this
    // bug is fixed: http://bugzilla.qooxdoo.org/show_bug.cgi?id=87
    Shell shell = ( Shell )widget;
    Object[] args = new Object[]{
      showImage( shell ) ? getImagePath( shell.getImage() ) : ""
    };
    writer.newWidget( "org.eclipse.rap.rwt.widgets.Shell", args  );
    ControlLCAUtil.writeStyleFlags( widget );
    int style = widget.getStyle();
    if( ( style & RWT.APPLICATION_MODAL ) != 0 ) {
      writer.set( "modal", true );
    }
    if( ( style & RWT.TITLE ) != 0 ) {
      writer.call( "addState", new Object[]{ "rwt_TITLE" } );
    }
    writer.call( "fixTitlebar", new Object[ 0 ] );
    writer.set( "resizeable", ( style & RWT.RESIZE ) != 0 );
    writer.set( "showMinimize", ( style & RWT.MIN ) != 0 );
    writer.set( "showMaximize", ( style & RWT.MAX ) != 0 );
    writer.set( "showClose", ( style & RWT.CLOSE ) != 0 );
    writer.set( "alwaysOnTop", ( style & RWT.ON_TOP ) != 0 );
    writer.set( "overflow", "hidden" );
    if( shell.getParent() instanceof Shell ) {
      writer.call( "setDialogWindow", new Object[ 0 ] );
    }
    ControlLCAUtil.writeResizeNotificator( widget );
    ControlLCAUtil.writeMoveNotificator( widget );
    writer.addListener( JSConst.QX_EVENT_CHANGE_VISIBILITY, 
                        JSConst.JS_SHELL_CLOSED );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Shell shell = ( Shell )widget;
    ControlLCAUtil.writeChanges( shell );
    JSWriter writer = JSWriter.getWriterFor( widget );
    if( shell.getBounds().equals( shell.getDisplay().getBounds() ) ) {
      writer.call( "maximize", new Object[ 0 ] );
    }
    if( showImage( shell ) ) {
      writeImage( shell );
    }
    writer.set( Props.TEXT, JSConst.QX_FIELD_CAPTION, shell.getText(), "" );
    writeOpen( shell );
    // Important: Order matters, writing setActive() before open() leads to
    //            strange behavior!
    writeActiveShell( shell );
    writeActiveControl( shell );
    writeMode( shell );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.call( "close", null );
    writer.dispose();
  }
  
  //////////////////
  // Helping methods
  
  private static void writeOpen( final Shell shell ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( shell );
    // TODO [rst] workaround: qx window should be opened only once.
    Boolean defValue = Boolean.FALSE;
    Boolean actValue = Boolean.valueOf( shell.getVisible() );
    if( WidgetLCAUtil.hasChanged( shell, Props.VISIBLE, actValue, defValue )
        && shell.getVisible() )
    {
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
  
  private static void writeActiveControl( Shell shell ) throws IOException {
    Control activeControl = getActiveControl( shell );
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
    Object adapter = shell.getAdapter( IShellAdapter.class );
    IShellAdapter shellAdapter = ( IShellAdapter )adapter;
    shellAdapter.setActiveControl( ( Control )widget );
  }
  
  private static void writeImage( final Shell shell ) throws IOException {
    Image image = shell.getImage();
    if( WidgetLCAUtil.hasChanged( shell, Props.IMAGE, image, null ) ) {
      JSWriter writer = JSWriter.getWriterFor( shell );
      writer.set( JSConst.QX_FIELD_ICON, getImagePath( image ) );
    }
  }
  
  private static boolean showImage( Shell shell) {
    return (shell.getStyle() & ( RWT.MIN | RWT.MAX | RWT.CLOSE ) ) != 0;
  }
  
  private static String getImagePath( Image image ) {
    return image != null ? Image.getPath( image ) : "";
  }
  
  private static void writeMode( final Shell shell ) throws IOException {
    Object defValue = null;
    Object newValue = getMode( shell );
    if( WidgetLCAUtil.hasChanged( shell, PROP_MODE, newValue, defValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( shell );
      writer.set( "mode", newValue );
    }
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
