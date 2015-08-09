/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.changed;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderListenKey;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderListener;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderToolTipMarkupEnabled;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.internal.util.MnemonicUtil.removeAmpersandControlCharacters;
import static org.eclipse.rap.rwt.remote.JsonMapping.toJson;
import static org.eclipse.swt.internal.widgets.MarkupUtil.isToolTipMarkupEnabledFor;

import java.lang.reflect.Field;

import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.util.ActiveKeysUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.ControlRemoteAdapter;
import org.eclipse.swt.internal.widgets.ControlUtil;
import org.eclipse.swt.internal.widgets.IControlAdapter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;


public class ControlLCAUtil {

  // Property names to preserve widget property values
  private static final String PROP_PARENT = "parent";
  private static final String PROP_CHILDREN = "children";
  private static final String PROP_BOUNDS = "bounds";
  private static final String PROP_TAB_INDEX = "tabIndex";
  private static final String PROP_TOOLTIP_TEXT = "toolTip";
  private static final String PROP_MENU = "menu";
  private static final String PROP_VISIBLE = "visibility";
  private static final String PROP_ENABLED = "enabled";
  private static final String PROP_FOREGROUND = "foreground";
  private static final String PROP_BACKGROUND = "background";
  private static final String PROP_BACKGROUND_IMAGE = "backgroundImage";
  private static final String PROP_FONT = "font";
  private static final String PROP_CURSOR = "cursor";
  private static final String PROP_ACTIVATE_LISTENER = "Activate";
  private static final String PROP_DEACTIVATE_LISTENER = "Deactivate";
  private static final String PROP_FOCUS_IN_LISTENER = "FocusIn";
  private static final String PROP_FOCUS_OUT_LISTENER = "FocusOut";
  private static final String PROP_MOUSE_DOWN_LISTENER = "MouseDown";
  private static final String PROP_MOUSE_DOUBLE_CLICK_LISTENER = "MouseDoubleClick";
  private static final String PROP_MOUSE_UP_LISTENER = "MouseUp";
  private static final String PROP_TRAVERSE_LISTENER = "Traverse";
  private static final String PROP_MENU_DETECT_LISTENER = "MenuDetect";
  private static final String PROP_HELP_LISTENER = "Help";

  private static final String CURSOR_UPARROW
    = "rwt-resources/resource/widget/rap/cursors/up_arrow.cur";

  private ControlLCAUtil() {
    // prevent instance creation
  }

  public static void preserveValues( Control control ) {
    preserveTabIndex( control );
    ActiveKeysUtil.preserveActiveKeys( control );
    ActiveKeysUtil.preserveCancelKeys( control );
  }

  public static void renderChanges( Control control ) {
    renderParent( control );
    renderChildren( control );
    renderBounds( control );
    renderTabIndex( control );
    renderToolTipMarkupEnabled( control );
    renderToolTipText( control );
    renderMenu( control );
    renderVisible( control );
    renderEnabled( control );
    renderForeground( control );
    renderBackground( control );
    renderBackgroundImage( control );
    renderFont( control );
    renderCursor( control );
    renderData( control );
    ActiveKeysUtil.renderActiveKeys( control );
    ActiveKeysUtil.renderCancelKeys( control );
    renderListenActivate( control );
    renderListenMouse( control );
    renderListenFocus( control );
    renderListenKey( control );
    renderListenTraverse( control );
    renderListenMenuDetect( control );
    renderListenHelp( control );
  }

  public static void preserveParent( Control control, Composite parent ) {
    ControlRemoteAdapter adapter = getRemoteAdapter( control );
    if( !adapter.hasPreservedParent() ) {
      adapter.preserveParent( parent );
    }
  }

  private static void renderParent( Control control ) {
    ControlRemoteAdapter remoteAdapter = getRemoteAdapter( control );
    if( remoteAdapter.isInitialized() && remoteAdapter.hasPreservedParent() ) {
      Composite actual = control.getParent();
      Composite preserved = remoteAdapter.getPreservedParent();
      if( changed( control, actual, preserved, null ) ) {
        getRemoteObject( control ).set( PROP_PARENT, getId( actual ) );
      }
    }
  }

  public static void preserveChildren( Composite composite, Control[] children ) {
    ControlRemoteAdapter adapter = getRemoteAdapter( composite );
    if( !adapter.hasPreservedChildren() ) {
      adapter.preserveChildren( children );
    }
  }

  private static void renderChildren( Control control ) {
    ControlRemoteAdapter remoteAdapter = getRemoteAdapter( control );
    if( control instanceof Composite && remoteAdapter.hasPreservedChildren() ) {
      Composite composite = ( Composite )control;
      Control[] actual = composite.getChildren();
      Control[] preserved = remoteAdapter.getPreservedChildren();
      if( changed( control, actual, preserved, null ) ) {
        getRemoteObject( control ).set( PROP_CHILDREN, getIdsAsJson( actual ) );
      }
    }
  }

  public static void preserveBounds( Control control, Rectangle bounds ) {
    ControlRemoteAdapter adapter = getRemoteAdapter( control );
    if( !adapter.hasPreservedBounds() ) {
      adapter.preserveBounds( bounds );
    }
  }

  private static void renderBounds( Control control ) {
    ControlRemoteAdapter remoteAdapter = getRemoteAdapter( control );
    if( !remoteAdapter.isInitialized() || remoteAdapter.hasPreservedBounds() ) {
      Rectangle actual = ControlUtil.getControlAdapter( control ).getBounds();
      Rectangle preserved = remoteAdapter.getPreservedBounds();
      if( changed( control, actual, preserved, null ) ) {
        getRemoteObject( control ).set( PROP_BOUNDS, toJson( actual ) );
      }
    }
  }

  private static void preserveTabIndex( Control control ) {
    getRemoteAdapter( control ).preserveTabIndex( getTabIndex( control ) );
  }

  private static void renderTabIndex( Control control ) {
    if( control instanceof Shell ) {
      resetTabIndices( ( Shell )control );
      // tabIndex must be a positive value
      computeTabIndices( ( Shell )control, 1 );
    }
    ControlRemoteAdapter remoteAdapter = getRemoteAdapter( control );
    int actual = getTabIndex( control );
    int preserved = remoteAdapter.getPreservedTabIndex();
    if( !remoteAdapter.isInitialized() || actual != preserved ) {
      getRemoteObject( control ).set( PROP_TAB_INDEX, actual );
    }
  }

  public static void preserveToolTipText( Control control, String toolTipText ) {
    ControlRemoteAdapter remoteAdapter = getRemoteAdapter( control );
    if( !remoteAdapter.hasPreservedToolTipText() ) {
      remoteAdapter.preserveToolTipText( toolTipText );
    }
  }

  private static void renderToolTipText( Control control ) {
    ControlRemoteAdapter remoteAdapter = getRemoteAdapter( control );
    if( remoteAdapter.hasPreservedToolTipText() ) {
      String actual = control.getToolTipText();
      String preserved = getRemoteAdapter( control ).getPreservedToolTipText();
      if( changed( control, actual, preserved, null ) ) {
        String text = actual == null ? "" : actual;
        if( !isToolTipMarkupEnabledFor( control ) ) {
          text = removeAmpersandControlCharacters( text );
        }
        getRemoteObject( control ).set( PROP_TOOLTIP_TEXT, text );
      }
    }
  }

  public static void preserveMenu( Control control, Menu menu ) {
    ControlRemoteAdapter remoteAdapter = getRemoteAdapter( control );
    if( !remoteAdapter.hasPreservedMenu() ) {
      getRemoteAdapter( control ).preserveMenu( menu );
    }
  }

  private static void renderMenu( Control control ) {
    ControlRemoteAdapter remoteAdapter = getRemoteAdapter( control );
    if( remoteAdapter.hasPreservedMenu() ) {
      Menu actual = control.getMenu();
      Menu preserved = getRemoteAdapter( control ).getPreservedMenu();
      if( changed( control, actual, preserved, null ) ) {
        String actualMenuId = actual == null ? null : getId( actual );
        getRemoteObject( control ).set( PROP_MENU, actualMenuId );
      }
    }
  }

  public static void preserveVisible( Control control, boolean visible ) {
    ControlRemoteAdapter remoteAdapter = getRemoteAdapter( control );
    if( !remoteAdapter.hasPreservedVisible() ) {
      remoteAdapter.preserveVisible( visible );
    }
  }

  private static void renderVisible( Control control ) {
    ControlRemoteAdapter remoteAdapter = getRemoteAdapter( control );
    if( remoteAdapter.hasPreservedVisible() ) {
      boolean actual = control.getVisible();
      boolean preserved = remoteAdapter.getPreservedVisible();
      boolean defaultValue = control instanceof Shell ? false : true;
      if( changed( control, actual, preserved, defaultValue ) ) {
        getRemoteObject( control ).set( PROP_VISIBLE, actual );
      }
    }
  }

  public static void preserveEnabled( Control control, boolean enabled ) {
    ControlRemoteAdapter remoteAdapter = getRemoteAdapter( control );
    if( !remoteAdapter.hasPreservedEnabled() ) {
      getRemoteAdapter( control ).preserveEnabled( enabled );
    }
  }

  private static void renderEnabled( Control control ) {
    // Using isEnabled() would result in unnecessarily updating child widgets of
    // enabled/disabled controls.
    ControlRemoteAdapter remoteAdapter = getRemoteAdapter( control );
    if( remoteAdapter.hasPreservedEnabled() ) {
      boolean actual = control.getEnabled();
      boolean preserved = remoteAdapter.getPreservedEnabled();
      if( changed( control, actual, preserved, true ) ) {
        getRemoteObject( control ).set( PROP_ENABLED, actual );
      }
    }
  }

  public static void preserveForeground( Control control, Color foreground ) {
    ControlRemoteAdapter remoteAdapter = getRemoteAdapter( control );
    if( !remoteAdapter.hasPreservedForeground() ) {
      getRemoteAdapter( control ).preserveForeground( foreground );
    }
  }

  private static void renderForeground( Control control ) {
    ControlRemoteAdapter remoteAdapter = getRemoteAdapter( control );
    if( remoteAdapter.hasPreservedForeground() ) {
      IControlAdapter controlAdapter = ControlUtil.getControlAdapter( control );
      Color actual = controlAdapter.getUserForeground();
      Color preserved = getRemoteAdapter( control ).getPreservedForeground();
      if( changed( control, actual, preserved, null ) ) {
        getRemoteObject( control ).set( PROP_FOREGROUND, toJson( actual ) );
      }
    }
  }

  public static void preserveBackground( Control control, Color background, boolean transparency ) {
    ControlRemoteAdapter remoteAdapter = getRemoteAdapter( control );
    if( !remoteAdapter.hasPreservedBackground() ) {
      remoteAdapter.preserveBackground( background );
      remoteAdapter.preserveBackgroundTransparency( transparency );
    }
  }

  private static void renderBackground( Control control ) {
    ControlRemoteAdapter remoteAdapter = getRemoteAdapter( control );
    if( remoteAdapter.hasPreservedBackground() ) {
      IControlAdapter controlAdapter = ControlUtil.getControlAdapter( control );
      Color actualBackground = controlAdapter.getUserBackground();
      boolean actualTransparency = controlAdapter.getBackgroundTransparency();
      boolean colorChanged = changed( control,
                                      actualBackground,
                                      remoteAdapter.getPreservedBackground(),
                                      null );
      boolean transparencyChanged = changed( control,
                                             actualTransparency,
                                             remoteAdapter.getPreservedBackgroundTransparency(),
                                             false );
      if( transparencyChanged || colorChanged ) {
        JsonValue color = actualTransparency && actualBackground == null
            ? toJson( new RGB( 0, 0, 0 ), 0 )
            : toJson( actualBackground, actualTransparency ? 0 : 255 );
            getRemoteObject( control ).set( PROP_BACKGROUND, color );
      }
    }
  }

  public static void preserveBackgroundImage( Control control, Image image ) {
    ControlRemoteAdapter remoteAdapter = getRemoteAdapter( control );
    if( !remoteAdapter.hasPreservedBackgroundImage() ) {
      getRemoteAdapter( control ).preserveBackgroundImage( image );
    }
  }

  private static void renderBackgroundImage( Control control ) {
    ControlRemoteAdapter remoteAdapter = getRemoteAdapter( control );
    if( remoteAdapter.hasPreservedBackgroundImage() ) {
      IControlAdapter controlAdapter = ControlUtil.getControlAdapter( control );
      Image actual = controlAdapter.getUserBackgroundImage();
      Image preserved = getRemoteAdapter( control ).getPreservedBackgroundImage();
      if( changed( control, actual, preserved, null ) ) {
        getRemoteObject( control ).set( PROP_BACKGROUND_IMAGE, toJson( actual ) );
      }
    }
  }

  public static void preserveFont( Control control, Font font ) {
    ControlRemoteAdapter remoteAdapter = getRemoteAdapter( control );
    if( !remoteAdapter.hasPreservedFont() ) {
      getRemoteAdapter( control ).preserveFont( font );
    }
  }

  private static void renderFont( Control control ) {
    ControlRemoteAdapter remoteAdapter = getRemoteAdapter( control );
    if( remoteAdapter.hasPreservedFont() ) {
      IControlAdapter controlAdapter = ControlUtil.getControlAdapter( control );
      Font actual = controlAdapter.getUserFont();
      Font preserved = getRemoteAdapter( control ).getPreservedFont();
      if( changed( control, actual, preserved, null ) ) {
        getRemoteObject( control ).set( PROP_FONT, toJson( actual ) );
      }
    }
  }

  public static void preserveCursor( Control control, Cursor cursor ) {
    ControlRemoteAdapter remoteAdapter = getRemoteAdapter( control );
    if( !remoteAdapter.hasPreservedCursor() ) {
      getRemoteAdapter( control ).preserveCursor( cursor );
    }
  }

  private static void renderCursor( Control control ) {
    ControlRemoteAdapter remoteAdapter = getRemoteAdapter( control );
    if( remoteAdapter.hasPreservedCursor() ) {
      Cursor actual = control.getCursor();
      Cursor preserved = getRemoteAdapter( control ).getPreservedCursor();
      if( changed( control, actual, preserved, null ) ) {
        getRemoteObject( control ).set( PROP_CURSOR, getQxCursor( actual ) );
      }
    }
  }

  private static void renderData( Control control ) {
    WidgetLCAUtil.renderData( control );
  }

  private static void renderListenActivate( Control control ) {
    // Note: Shell "Activate" event is handled by ShellLCA
    if( !( control instanceof Shell ) ) {
      renderListener( control, SWT.Activate, PROP_ACTIVATE_LISTENER );
      renderListener( control, SWT.Deactivate, PROP_DEACTIVATE_LISTENER );
    }
  }

  private static void renderListenMouse( Control control ) {
    renderListener( control, SWT.MouseDown, PROP_MOUSE_DOWN_LISTENER );
    renderListener( control, SWT.MouseUp, PROP_MOUSE_UP_LISTENER );
    renderListener( control, SWT.MouseDoubleClick, PROP_MOUSE_DOUBLE_CLICK_LISTENER );
  }

  private static void renderListenFocus( Control control ) {
    if( ( control.getStyle() & SWT.NO_FOCUS ) == 0 ) {
      renderListener( control, SWT.FocusIn, PROP_FOCUS_IN_LISTENER );
      renderListener( control, SWT.FocusOut, PROP_FOCUS_OUT_LISTENER );
    }
  }

  private static void renderListenTraverse( Control control ) {
    renderListener( control, SWT.Traverse, PROP_TRAVERSE_LISTENER );
  }

  private static void renderListenMenuDetect( Control control ) {
    renderListener( control, SWT.MenuDetect, PROP_MENU_DETECT_LISTENER );
  }

  private static void renderListenHelp( Control control ) {
    renderListener( control, SWT.Help, PROP_HELP_LISTENER );
  }

  // TODO [rh] Eliminate instance checks. Let the respective classes always return NO_FOCUS
  private static boolean takesFocus( Control control ) {
    boolean result = true;
    result &= ( control.getStyle() & SWT.NO_FOCUS ) == 0;
    result &= control.getClass() != Composite.class;
    result &= control.getClass() != SashForm.class;
    return result;
  }

  private static int getTabIndex( Control control ) {
    int result = -1;
    if( takesFocus( control ) ) {
      result = ControlUtil.getControlAdapter( control ).getTabIndex();
    }
    return result;
  }

  private static void resetTabIndices( Composite composite ) {
    for( Control control : composite.getChildren() ) {
      ControlUtil.getControlAdapter( control ).setTabIndex( -1 );
      if( control instanceof Composite ) {
        resetTabIndices( ( Composite )control );
      }
    }
  }

  private static int computeTabIndices( Composite composite, int startIndex ) {
    int result = startIndex;
    for( Control control : composite.getTabList() ) {
      IControlAdapter controlAdapter = ControlUtil.getControlAdapter( control );
      controlAdapter.setTabIndex( result );
      // for Links, leave a range out to be assigned to hrefs on the client
      result += control instanceof Link ? 300 : 1;
      if( control instanceof Composite ) {
        result = computeTabIndices( ( Composite )control, result );
      }
    }
    return result;
  }

  private static JsonValue getIdsAsJson( Control[] controls ) {
    String[] controlIds = new String[ controls.length ];
    for( int i = 0; i < controls.length; i++ ) {
      controlIds[ i ] = getId( controls[ i ] );
    }
    // TODO [rst] Can we also render an empty array instead of null?
    return controlIds.length == 0 ? JsonValue.NULL : createJsonArray( controlIds );
  }

  private static String getQxCursor( Cursor newValue ) {
    if( newValue != null ) {
      // TODO [rst] Find a better way of obtaining the Cursor value
      // TODO [tb] adjust strings to match name of constants
      int value = 0;
      try {
        Field field = Cursor.class.getDeclaredField( "value" );
        field.setAccessible( true );
        value = field.getInt( newValue );
      } catch( Exception e ) {
        throw new RuntimeException( e );
      }
      switch( value ) {
        case SWT.CURSOR_ARROW:
          return "default";
        case SWT.CURSOR_WAIT:
          return "wait";
        case SWT.CURSOR_APPSTARTING:
          return "progress";
        case SWT.CURSOR_CROSS:
          return "crosshair";
        case SWT.CURSOR_HELP:
          return "help";
        case SWT.CURSOR_SIZEALL:
          return "move";
        case SWT.CURSOR_SIZENS:
          return "row-resize";
        case SWT.CURSOR_SIZEWE:
          return "col-resize";
        case SWT.CURSOR_SIZEN:
          return "n-resize";
        case SWT.CURSOR_SIZES:
          return "s-resize";
        case SWT.CURSOR_SIZEE:
          return "e-resize";
        case SWT.CURSOR_SIZEW:
          return "w-resize";
        case SWT.CURSOR_SIZENE:
        case SWT.CURSOR_SIZENESW:
          return "ne-resize";
        case SWT.CURSOR_SIZESE:
          return "se-resize";
        case SWT.CURSOR_SIZESW:
          return "sw-resize";
        case SWT.CURSOR_SIZENW:
        case SWT.CURSOR_SIZENWSE:
          return "nw-resize";
        case SWT.CURSOR_IBEAM:
          return "text";
        case SWT.CURSOR_HAND:
          return "pointer";
        case SWT.CURSOR_NO:
          return "not-allowed";
        case SWT.CURSOR_UPARROW:
          return CURSOR_UPARROW;
        default:
          break;
      }
    }
    return null;
  }

  private static ControlRemoteAdapter getRemoteAdapter( Control control ) {
    return ( ControlRemoteAdapter )control.getAdapter( RemoteAdapter.class );
  }

}
