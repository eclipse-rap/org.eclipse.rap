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
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderData;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderListenKey;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderListener;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderToolTipMarkupEnabled;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.internal.util.MnemonicUtil.removeAmpersandControlCharacters;
import static org.eclipse.swt.internal.widgets.ControlUtil.getControlAdapter;
import static org.eclipse.swt.internal.widgets.MarkupUtil.isToolTipMarkupEnabledFor;

import java.lang.reflect.Field;

import org.eclipse.rap.rwt.internal.util.ActiveKeysUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.internal.widgets.ControlRemoteAdapter;
import org.eclipse.swt.internal.widgets.ControlUtil;
import org.eclipse.swt.internal.widgets.IControlAdapter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;


public class ControlLCAUtil {

  private static final String PROP_TOOLTIP_TEXT = "toolTip";
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

  public static void renderChanges( Control control ) {
    ControlRemoteAdapter remoteAdapter = getRemoteAdapter( control );
    IControlAdapter controlAdapter = getControlAdapter( control );
    if( control instanceof Shell ) {
      recalculateTabIndex( ( Shell ) control );
    }
    remoteAdapter.renderParent( control );
    if( control instanceof Composite ) {
      remoteAdapter.renderChildren( ( Composite )control );
    }
    remoteAdapter.renderBounds( controlAdapter );
    remoteAdapter.renderTabIndex( control );
    renderToolTipMarkupEnabled( control );
    renderToolTipText( control );
    remoteAdapter.renderMenu( control );
    remoteAdapter.renderVisible( control );
    remoteAdapter.renderEnabled( control );
    remoteAdapter.renderOrientation( control );
    remoteAdapter.renderForeground( controlAdapter );
    remoteAdapter.renderBackground( controlAdapter );
    remoteAdapter.renderBackgroundImage( controlAdapter );
    remoteAdapter.renderFont( controlAdapter );
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

  private static void recalculateTabIndex( Shell shell ) {
    resetTabIndices( shell );
    // tabIndex must be a positive value
    computeTabIndices( shell, 1 );
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
      ControlUtil.getControlAdapter( control ).setTabIndex( result );
      // for Links, leave a range out to be assigned to hrefs on the client
      result += control instanceof Link ? 300 : 1;
      if( control instanceof Composite ) {
        result = computeTabIndices( ( Composite )control, result );
      }
    }
    return result;
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
      String preserved = remoteAdapter.getPreservedToolTipText();
      if( changed( control, actual, preserved, null ) ) {
        String text = actual == null ? "" : actual;
        if( !isToolTipMarkupEnabledFor( control ) ) {
          text = removeAmpersandControlCharacters( text );
        }
        getRemoteObject( control ).set( PROP_TOOLTIP_TEXT, text );
      }
    }
  }

  public static void preserveCursor( Control control, Cursor cursor ) {
    ControlRemoteAdapter remoteAdapter = getRemoteAdapter( control );
    if( !remoteAdapter.hasPreservedCursor() ) {
      remoteAdapter.preserveCursor( cursor );
    }
  }

  private static void renderCursor( Control control ) {
    ControlRemoteAdapter remoteAdapter = getRemoteAdapter( control );
    if( remoteAdapter.hasPreservedCursor() ) {
      Cursor actual = control.getCursor();
      Cursor preserved = remoteAdapter.getPreservedCursor();
      if( changed( control, actual, preserved, null ) ) {
        getRemoteObject( control ).set( PROP_CURSOR, getQxCursor( actual ) );
      }
    }
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
