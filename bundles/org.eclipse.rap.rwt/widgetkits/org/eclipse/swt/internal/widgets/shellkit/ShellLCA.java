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
package org.eclipse.swt.internal.widgets.shellkit;

import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.events.EventUtil;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.internal.widgets.IShellAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;


public final class ShellLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.Shell";
  private static final String[] ALLOWED_STYLES = new String[] {
    "CLOSE",
    "MIN",
    "MAX",
    "NO_TRIM",
    "RESIZE",
    "TITLE",
    "ON_TOP",
    "TOOL",
    "SHEET",
    "APPLICATION_MODAL",
    "MODELESS",
    "PRIMARY_MODAL",
    "SYSTEM_MODAL",
    "BORDER"
  };

  private static final String PROP_TEXT = "text";
  private static final String PROP_IMAGE = "image";
  private static final String PROP_ALPHA = "alpha";
  static final String PROP_ACTIVE_CONTROL = "activeControl";
  static final String PROP_ACTIVE_SHELL = "activeShell";
  static final String PROP_MODE = "mode";
  static final String PROP_FULLSCREEN = "fullScreen";
  static final String PROP_MINIMUM_SIZE = "minimumSize";
  static final String PROP_SHELL_LISTENER = "shell";
  static final String PROP_RESIZE_LISTENER = "Resize";
  static final String PROP_MOVE_LISTENER = "Move";
  private static final String PROP_DEFAULT_BUTTON = "defaultButton";

  @Override
  public void preserveValues( Widget widget ) {
    Shell shell = ( Shell )widget;
    ControlLCAUtil.preserveValues( shell );
    WidgetLCAUtil.preserveCustomVariant( shell );
    preserveProperty( shell, PROP_ACTIVE_CONTROL, getActiveControl( shell ) );
    preserveProperty( shell, PROP_ACTIVE_SHELL, shell.getDisplay().getActiveShell() );
    preserveProperty( shell, PROP_TEXT, shell.getText() );
    preserveProperty( shell, PROP_IMAGE, shell.getImage() );
    preserveProperty( shell, PROP_ALPHA, new Integer( shell.getAlpha() ) );
    preserveProperty( shell, PROP_MODE, getMode( shell ) );
    preserveProperty( shell, PROP_FULLSCREEN, Boolean.valueOf( shell.getFullScreen() ) );
    preserveProperty( shell, PROP_MINIMUM_SIZE, shell.getMinimumSize() );
    preserveProperty( shell, PROP_DEFAULT_BUTTON, shell.getDefaultButton() );
    preserveListener( shell, PROP_SHELL_LISTENER, hasShellListener( shell ) );
  }

  public void readData( Widget widget ) {
    Shell shell = ( Shell )widget;
    // [if] Preserve the menu bounds before setting the new shell bounds.
    preserveMenuBounds( shell );
    // Important: Order matters, readMode() before readBounds()
    readMode( shell );
    readBounds( shell );
    if( WidgetLCAUtil.wasEventSent( shell, ClientMessageConst.EVENT_SHELL_CLOSED ) ) {
      shell.close();
    }
    processActiveShell( shell );
    readActiveControl( shell );
    ControlLCAUtil.processEvents( shell );
    ControlLCAUtil.processKeyEvents( shell );
    ControlLCAUtil.processMenuDetect( shell );
    WidgetLCAUtil.processHelp( shell );
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    Shell shell = ( Shell )widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( shell );
    clientObject.create( TYPE );
    clientObject.set( "style", WidgetLCAUtil.getStyles( shell, ALLOWED_STYLES ) );
    Composite parent = shell.getParent();
    if( parent instanceof Shell ) {
      clientObject.set( "parentShell", WidgetUtil.getId( parent ) );
    }
    // TODO [tb] : These should be rendered only when there is an actual listener attached:
    clientObject.listen( PROP_MOVE_LISTENER, true );
    clientObject.listen( PROP_RESIZE_LISTENER, true );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    Shell shell = ( Shell )widget;
    WidgetLCAUtil.renderCustomVariant( shell ); // Order matters for animation
    renderImage( shell );
    renderText( shell );
    renderAlpha( shell );
    renderActiveShell( shell );
    renderMode( shell );
    renderMinimumSize( shell );
    renderDefaultButton( shell );
    renderActiveControl( shell );
    ControlLCAUtil.renderChanges( shell );
    renderListenShell( shell );
  }

  @Override
  public void renderDispose( Widget widget ) throws IOException {
    IClientObject clientObject = ClientObjectFactory.getClientObject( widget );
    clientObject.destroy();
  }

  //////////////////
  // Helping methods

  private static void renderText( Shell shell ) {
    renderProperty( shell, PROP_TEXT, shell.getText(), "" );
  }

  private void renderAlpha( Shell shell ) {
    renderProperty( shell, PROP_ALPHA, new Integer( shell.getAlpha() ), new Integer( 0xFF ) );
  }

  private static void renderMinimumSize( Shell shell ) {
    Point newValue = shell.getMinimumSize();
    if( WidgetLCAUtil.hasChanged( shell, PROP_MINIMUM_SIZE, newValue ) ) {
      IClientObject clientObject = ClientObjectFactory.getClientObject( shell );
      clientObject.set( "minimumSize", new int[]{ newValue.x, newValue.y } );
    }
  }

  private static void renderDefaultButton( Shell shell ) {
    Button defaultButton = shell.getDefaultButton();
    if( WidgetLCAUtil.hasChanged( shell, PROP_DEFAULT_BUTTON, defaultButton, null ) ) {
      IClientObject clientObject = ClientObjectFactory.getClientObject( shell );
      String defaultButtonId = null;
      if( defaultButton != null ) {
        defaultButtonId = WidgetUtil.getId( defaultButton );
      }
      clientObject.set( "defaultButton", defaultButtonId );
    }
  }

  /////////////////////////////////////////////
  // Methods to read and write the active shell

  private static void renderActiveShell( Shell shell ) {
    Shell activeShell = shell.getDisplay().getActiveShell();
    boolean hasChanged = WidgetLCAUtil.hasChanged( shell, PROP_ACTIVE_SHELL, activeShell, null );
    if( shell == activeShell && hasChanged ) {
      IClientObject clientObject = ClientObjectFactory.getClientObject( shell );
      clientObject.set( "active", true );
    }
  }

  private static void processActiveShell( Shell shell ) {
    if( WidgetLCAUtil.wasEventSent( shell, ClientMessageConst.EVENT_SHELL_ACTIVATED ) ) {
      IDisplayAdapter displayAdapter = shell.getDisplay().getAdapter( IDisplayAdapter.class );
      displayAdapter.setActiveShell( shell );
    }
  }

  private static void readActiveControl( Shell shell ) {
    String activeControlId = WidgetLCAUtil.readPropertyValue( shell, PROP_ACTIVE_CONTROL );
    Widget widget = WidgetUtil.find( shell, activeControlId );
    if( widget != null ) {
      setActiveControl( shell, widget );
    }
  }

  private static void renderActiveControl( Shell shell ) {
    final Control activeControl = getActiveControl( shell );
    if( WidgetLCAUtil.hasChanged( shell, PROP_ACTIVE_CONTROL, activeControl, null ) ) {
      String activeControlId = null;
      if( activeControl != null ) {
        activeControlId = WidgetUtil.getId( activeControl );
      }
      IClientObject clientObject = ClientObjectFactory.getClientObject( shell );
      clientObject.set( "activeControl", activeControlId );
    }
  }

  private static void renderImage( Shell shell ) {
    if( ( shell.getStyle() & SWT.TITLE ) != 0 ) {
      Image image = shell.getImage();
      if( image == null ) {
        Image[] defaultImages = shell.getImages();
        if( defaultImages.length > 0 ) {
          image = defaultImages[0];
        }
      }
      renderProperty( shell, PROP_IMAGE, image, null );
    }
  }

  private static void readBounds( Shell shell ) {
    Rectangle bounds = ProtocolUtil.readPropertyValueAsRectangle( getId( shell ), "bounds" );
    if( bounds != null ) {
      IShellAdapter shellAdapter = shell.getAdapter( IShellAdapter.class );
      shellAdapter.setBounds( bounds );
    }
  }

  private static void readMode( Shell shell ) {
    final String value = WidgetLCAUtil.readPropertyValue( shell, "mode" );
    if( value != null ) {
      if( "maximized".equals( value ) ) {
        shell.setMaximized( true );
      } else if( "minimized".equals( value ) ) {
        shell.setMinimized( true );
      } else {
        shell.setMinimized( false );
        shell.setMaximized( false );
      }
    }
  }

  private static void renderMode( Shell shell ) {
    Object defValue = null;
    Object newValue = getMode( shell );
    if( WidgetLCAUtil.hasChanged( shell, PROP_MODE, newValue, defValue ) ) {
      IClientObject clientObject = ClientObjectFactory.getClientObject( shell );
      clientObject.set( "mode", newValue );
    }
  }

  private static void renderListenShell( Shell shell ) {
    // Note that "shell" events include "activate", "deactivate" and "close" events.
    // "shellActivated" is sent the client in any case, event without listener.
    // "Shell_close" events are also always being sent, but with a listener the shell is not closed
    // by the client itself but by the server. Also, the "shellActivated" events are different from #
    // the "activeControl" property and "controlActivated" event (also sent by the shell and
    // processed in ShellLCA#processActivate).
    // The listener property for this event is rendered by ControlLCAUtil#renderActivateListener
    boolean newValue = hasShellListener( shell );
    renderListener( shell, PROP_SHELL_LISTENER, newValue, false );
  }

  private static void setActiveControl( Shell shell, Widget widget ) {
    if( EventUtil.isAccessible( widget ) ) {
      shell.getAdapter( IShellAdapter.class ).setActiveControl( ( Control )widget );
    }
  }

  private static Control getActiveControl( Shell shell ) {
    return shell.getAdapter( IShellAdapter.class ).getActiveControl();
  }

  private static boolean hasShellListener( Shell shell ) {
    return shell.isListening( SWT.Close )
        || shell.isListening( SWT.Activate )
        || shell.isListening( SWT.Deactivate );
  }

  private static String getMode( Shell shell ) {
    String result = null;
    if( shell.getMinimized() ) {
      result = "minimized";
    } else if( shell.getFullScreen() ) {
      result = "fullscreen";
    } else if( shell.getMaximized() ) {
      result = "maximized";
    }
    return result;
  }

  private static void preserveMenuBounds( Shell shell ) {
    Menu menuBar = shell.getMenuBar();
    if( menuBar != null ) {
      IShellAdapter shellAdapter = shell.getAdapter( IShellAdapter.class );
      Rectangle menuBounds = shellAdapter.getMenuBounds();
      IWidgetAdapter widgetAdapter = WidgetUtil.getAdapter( menuBar );
      widgetAdapter.preserve( Props.BOUNDS, menuBounds );
    }
  }

}
