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
package org.eclipse.rap.rwt.internal.lifecycle;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_DEFAULT_SELECTION;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_FOCUS_IN;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_FOCUS_OUT;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_KEY_DOWN;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_MENU_DETECT;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_MOUSE_DOUBLE_CLICK;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_MOUSE_DOWN;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_MOUSE_UP;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_BUTTON;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_CHAR_CODE;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_DETAIL;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_KEY_CODE;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_TEXT;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_TIME;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_X;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_PARAM_Y;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_SELECTION;
import static org.eclipse.rap.rwt.internal.protocol.ClientMessageConst.EVENT_TRAVERSE;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.readEventPropertyValue;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.swt.internal.events.EventLCAUtil.isListening;

import java.lang.reflect.Field;

import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.util.ActiveKeysUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.events.EventLCAUtil;
import org.eclipse.swt.internal.widgets.ControlUtil;
import org.eclipse.swt.internal.widgets.IControlAdapter;
import org.eclipse.swt.internal.widgets.IControlHolderAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;


public class ControlLCAUtil {

  // Property names to preserve widget property values
  private static final String PROP_ACTIVATE_LISTENER = "Activate";
  private static final String PROP_DEACTIVATE_LISTENER = "Deactivate";
  private static final String PROP_FOCUS_IN_LISTENER = "FocusIn";
  private static final String PROP_FOCUS_OUT_LISTENER = "FocusOut";
  private static final String PROP_MOUSE_DOWN_LISTENER = "MouseDown";
  private static final String PROP_MOUSE_DOUBLE_CLICK_LISTENER = "MouseDoubleClick";
  private static final String PROP_MOUSE_UP_LISTENER = "MouseUp";
  private static final String PROP_KEY_LISTENER = "KeyDown";
  private static final String PROP_TRAVERSE_LISTENER = "Traverse";
  private static final String PROP_MENU_DETECT_LISTENER = "MenuDetect";
  private static final String PROP_TAB_INDEX = "tabIndex";
  private static final String PROP_CURSOR = "cursor";
  private static final String PROP_BACKGROUND_IMAGE = "backgroundImage";
  private static final String PROP_CHILDREN = "children";

  static final int MAX_STATIC_ZORDER = 300;

  private static final String CURSOR_UPARROW
    = "rwt-resources/resource/widget/rap/cursors/up_arrow.cur";

  private ControlLCAUtil() {
    // prevent instance creation
  }

  // TODO [rst] Revise: This seems to unnecessarily call getter and setter even
  //            when no bounds are submitted.
  public static void readBounds( Control control ) {
    Rectangle current = control.getBounds();
    Rectangle newBounds = WidgetLCAUtil.readBounds( control, current );
    control.setBounds( newBounds );
  }

  public static void processMenuDetect( Control control ) {
    if( WidgetLCAUtil.wasEventSent( control, EVENT_MENU_DETECT ) ) {
      Event event = new Event();
      Point point = readEventXYProperties( control, EVENT_MENU_DETECT );
      point = control.getDisplay().map( control, null, point );
      event.x = point.x;
      event.y = point.y;
      event.doit = true;
      control.notifyListeners( SWT.MenuDetect, event );
    }
  }

  public static void processEvents( Control control ) {
    processFocusEvents( control );
    processMouseEvents( control );
  }

  private static void processFocusEvents( Control control ) {
    if( WidgetLCAUtil.wasEventSent( control, EVENT_FOCUS_IN ) ) {
      control.notifyListeners( SWT.FocusIn, new Event() );
    }
    if( WidgetLCAUtil.wasEventSent( control, EVENT_FOCUS_OUT ) ) {
      control.notifyListeners( SWT.FocusOut, new Event() );
    }
  }

  public static void processMouseEvents( Control control ) {
    if( WidgetLCAUtil.wasEventSent( control, EVENT_MOUSE_DOWN ) ) {
      sendMouseEvent( control, EVENT_MOUSE_DOWN, SWT.MouseDown );
    }
    if( WidgetLCAUtil.wasEventSent( control, EVENT_MOUSE_DOUBLE_CLICK ) ) {
      sendMouseEvent( control, EVENT_MOUSE_DOUBLE_CLICK, SWT.MouseDoubleClick );
    }
    if( WidgetLCAUtil.wasEventSent( control, EVENT_MOUSE_UP ) ) {
      sendMouseEvent( control, EVENT_MOUSE_UP, SWT.MouseUp );
    }
  }

  private static void sendMouseEvent( Control control, String eventName, int eventType ) {
    Event event = new Event();
    event.widget = control;
    event.type = eventType;
    event.button = readEventIntProperty( control, eventName, EVENT_PARAM_BUTTON );
    Point point = readEventXYProperties( control, eventName );
    event.x = point.x;
    event.y = point.y;
    event.time = readEventIntProperty( control, eventName, EVENT_PARAM_TIME );
    event.stateMask = EventLCAUtil.readStateMask( control, eventName )
                    | EventLCAUtil.translateButton( event.button );
    if( WidgetLCAUtil.wasEventSent( control, EVENT_MOUSE_DOUBLE_CLICK ) ) {
      event.count = 2;
    } else {
      event.count = 1;
    }
    checkAndProcessMouseEvent( event );
  }

  public static void processKeyEvents( Control control ) {
    if( WidgetLCAUtil.wasEventSent( control, EVENT_TRAVERSE ) ) {
      int keyCode = readEventIntProperty( control, EVENT_TRAVERSE, EVENT_PARAM_KEY_CODE );
      int charCode = readEventIntProperty( control, EVENT_TRAVERSE, EVENT_PARAM_CHAR_CODE );
      int stateMask = EventLCAUtil.readStateMask( control, EVENT_TRAVERSE );
      int traverseKey = getTraverseKey( keyCode, stateMask );
      if( traverseKey != SWT.TRAVERSE_NONE ) {
        Event event = createKeyEvent( keyCode, charCode, stateMask );
        event.detail = traverseKey;
        control.notifyListeners( SWT.Traverse, event );
      }
    }
    if( WidgetLCAUtil.wasEventSent( control, EVENT_KEY_DOWN ) ) {
      int keyCode = readEventIntProperty( control, EVENT_KEY_DOWN, EVENT_PARAM_KEY_CODE );
      int charCode = readEventIntProperty( control, EVENT_KEY_DOWN, EVENT_PARAM_CHAR_CODE );
      int stateMask = EventLCAUtil.readStateMask( control, EVENT_KEY_DOWN );
      Event event = createKeyEvent( keyCode, charCode, stateMask );
      control.notifyListeners( SWT.KeyDown, event );
      event = createKeyEvent( keyCode, charCode, stateMask );
      control.notifyListeners( SWT.KeyUp, event );
    }
  }

  public static void processSelection( Widget widget, Item item, boolean readBounds ) {
    if( WidgetLCAUtil.wasEventSent( widget, EVENT_SELECTION ) ) {
      Event event = createSelectionEvent( widget, readBounds, SWT.Selection );
      event.item = item;
      widget.notifyListeners( SWT.Selection, event );
    }
  }

  public static void processDefaultSelection( Widget widget, Item item ) {
    if( WidgetLCAUtil.wasEventSent( widget, EVENT_DEFAULT_SELECTION ) ) {
      Event event = createSelectionEvent( widget, false, SWT.DefaultSelection );
      event.item = item;
      widget.notifyListeners( SWT.DefaultSelection, event );
    }
  }

  public static void preserveValues( Control control ) {
    WidgetAdapter adapter = WidgetUtil.getAdapter( control );
    WidgetLCAUtil.preserveBounds( control, control.getBounds() );
    adapter.preserve( PROP_CHILDREN, getChildren( control ) );
    adapter.preserve( PROP_TAB_INDEX, new Integer( getTabIndex( control ) ) );
    WidgetLCAUtil.preserveToolTipText( control, control.getToolTipText() );
    adapter.preserve( Props.MENU, control.getMenu() );
    adapter.preserve( Props.VISIBLE, Boolean.valueOf( getVisible( control ) ) );
    WidgetLCAUtil.preserveEnabled( control, control.getEnabled() );
    IControlAdapter controlAdapter = ControlUtil.getControlAdapter( control );
    WidgetLCAUtil.preserveForeground( control, controlAdapter.getUserForeground() );
    WidgetLCAUtil.preserveBackground( control,
                                      controlAdapter.getUserBackground(),
                                      controlAdapter.getBackgroundTransparency() );
    preserveBackgroundImage( control );
    WidgetLCAUtil.preserveFont( control, controlAdapter.getUserFont() );
    adapter.preserve( PROP_CURSOR, control.getCursor() );
    preserveActivateListeners( control );
    preserveMouseListeners( control );
    if( ( control.getStyle() & SWT.NO_FOCUS ) == 0 ) {
      preserveFocusListeners( control );
    }
    WidgetLCAUtil.preserveListener( control,
                                    PROP_KEY_LISTENER,
                                    hasKeyListener( control ) );
    WidgetLCAUtil.preserveListener( control,
                                    PROP_TRAVERSE_LISTENER,
                                    isListening( control, SWT.Traverse ) );
    WidgetLCAUtil.preserveListener( control,
                                    PROP_MENU_DETECT_LISTENER,
                                    isListening( control, SWT.MenuDetect ) );
    WidgetLCAUtil.preserveHelpListener( control );
    ActiveKeysUtil.preserveActiveKeys( control );
    ActiveKeysUtil.preserveCancelKeys( control );
    WidgetLCAUtil.preserveData( control );
  }

  public static void preserveBackgroundImage( Control control ) {
    IControlAdapter controlAdapter = ControlUtil.getControlAdapter( control );
    Image image = controlAdapter.getUserBackgroundImage();
    WidgetAdapter adapter = WidgetUtil.getAdapter( control );
    adapter.preserve( PROP_BACKGROUND_IMAGE, image );
  }

  public static void renderChanges( Control control ) {
    renderBounds( control );
    renderChildren( control );
    renderTabIndex( control );
    renderToolTip( control );
    renderMenu( control );
    renderVisible( control );
    renderEnabled( control );
    renderForeground( control );
    renderBackground( control );
    renderBackgroundImage( control );
    renderFont( control );
    renderCursor( control );
    ActiveKeysUtil.renderActiveKeys( control );
    ActiveKeysUtil.renderCancelKeys( control );
//    TODO [rst] missing: writeControlListener( control );
    renderListenActivate( control );
    renderListenFocus( control );
    renderListenMouse( control );
    renderListenKey( control );
    renderListenTraverse( control );
    renderListenMenuDetect( control );
    WidgetLCAUtil.renderListenHelp( control );
    WidgetLCAUtil.renderData( control );
  }

  public static void renderBounds( Control control ) {
    WidgetLCAUtil.renderBounds( control, control.getBounds() );
  }

  static void renderChildren( Control control ) {
    if( control instanceof Composite ) {
      String[] newValue = getChildren( control );
      WidgetLCAUtil.renderProperty( control, PROP_CHILDREN, newValue, null );
    }
  }

  static void renderTabIndex( Control control ) {
    if( control instanceof Shell ) {
      resetTabIndices( ( Shell )control );
      // tabIndex must be a positive value
      computeTabIndices( ( Shell )control, 1 );
    }
    int tabIndex = getTabIndex( control );
    Integer newValue = new Integer( tabIndex );
    // there is no reliable default value for all controls
    if( WidgetLCAUtil.hasChanged( control, PROP_TAB_INDEX, newValue ) ) {
      getRemoteObject( control ).set( "tabIndex", tabIndex );
    }
  }

  public static void renderToolTip( Control control ) {
    WidgetLCAUtil.renderToolTip( control, control.getToolTipText() );
  }

  public static void renderMenu( Control control ) {
    WidgetLCAUtil.renderMenu( control, control.getMenu() );
  }

  public static void renderVisible( Control control ) {
    boolean visible = getVisible( control );
    Boolean newValue = Boolean.valueOf( visible );
    Boolean defValue = control instanceof Shell ? Boolean.FALSE : Boolean.TRUE;
    // TODO [tb] : Can we have a shorthand for this, like in JSWriter?
    if( WidgetLCAUtil.hasChanged( control, Props.VISIBLE, newValue, defValue ) ) {
      getRemoteObject( control ).set( "visibility", visible );
    }
  }

  public static void renderEnabled( Control control ) {
    // Using isEnabled() would result in unnecessarily updating child widgets of
    // enabled/disabled controls.
    WidgetLCAUtil.renderEnabled( control, control.getEnabled() );
  }

  public static void renderForeground( Control control ) {
    IControlAdapter controlAdapter = ControlUtil.getControlAdapter( control );
    WidgetLCAUtil.renderForeground( control, controlAdapter.getUserForeground() );
  }

  public static void renderBackground( Control control ) {
    IControlAdapter controlAdapter = ControlUtil.getControlAdapter( control );
    WidgetLCAUtil.renderBackground( control,
                                    controlAdapter.getUserBackground(),
                                    controlAdapter.getBackgroundTransparency() );
  }

  public static void renderBackgroundImage( Control control ) {
    IControlAdapter controlAdapter = ControlUtil.getControlAdapter( control );
    Image image = controlAdapter.getUserBackgroundImage();
    WidgetLCAUtil.renderProperty( control, PROP_BACKGROUND_IMAGE, image, null );
  }

  public static void renderFont( Control control ) {
    IControlAdapter controlAdapter = ControlUtil.getControlAdapter( control );
    Font newValue = controlAdapter.getUserFont();
    WidgetLCAUtil.renderFont( control, newValue );
  }

  static void renderCursor( Control control ) {
    Cursor newValue = control.getCursor();
    if( WidgetLCAUtil.hasChanged( control, PROP_CURSOR, newValue, null ) ) {
      getRemoteObject( control ).set( PROP_CURSOR, getQxCursor( newValue ) );
    }
  }

  static void renderListenActivate( Control control ) {
    // Note: Shell "Activate" event is handled by ShellLCA
    if( !control.isDisposed() && !( control instanceof Shell ) ) {
      renderListen( control, SWT.Activate, PROP_ACTIVATE_LISTENER );
      renderListen( control, SWT.Deactivate, PROP_DEACTIVATE_LISTENER );
    }
  }

  static void renderListenFocus( Control control ) {
    if( ( control.getStyle() & SWT.NO_FOCUS ) == 0 ) {
      renderListen( control, SWT.FocusIn, PROP_FOCUS_IN_LISTENER );
      renderListen( control, SWT.FocusOut, PROP_FOCUS_OUT_LISTENER );
    }
  }

  static void renderListenMouse( Control control ) {
    renderListen( control, SWT.MouseDown, PROP_MOUSE_DOWN_LISTENER );
    renderListen( control, SWT.MouseUp, PROP_MOUSE_UP_LISTENER );
    renderListen( control, SWT.MouseDoubleClick, PROP_MOUSE_DOUBLE_CLICK_LISTENER );
  }

  static void renderListenKey( Control control ) {
    boolean newValue = hasKeyListener( control );
    WidgetLCAUtil.renderListener( control, PROP_KEY_LISTENER, newValue, false );
  }

  static void renderListenTraverse( Control control ) {
    boolean newValue = isListening( control, SWT.Traverse );
    WidgetLCAUtil.renderListener( control, PROP_TRAVERSE_LISTENER, newValue, false );
  }

  static void renderListenMenuDetect( Control control ) {
    boolean newValue = isListening( control, SWT.MenuDetect );
    WidgetLCAUtil.renderListener( control, PROP_MENU_DETECT_LISTENER, newValue, false );
  }

  private static void renderListen( Control control, int eventType, String eventName ) {
    WidgetLCAUtil.renderListener( control, eventName, isListening( control, eventType ), false );
  }

  private static Event createSelectionEvent( Widget widget, boolean readBounds, int type ) {
    Event result = new Event();
    if( widget instanceof Control && readBounds ) {
      Control control = ( Control )widget;
      Rectangle bounds = WidgetLCAUtil.readBounds( control, control.getBounds() );
      result.setBounds( bounds );
    }
    String eventName = type == SWT.Selection ? EVENT_SELECTION : EVENT_DEFAULT_SELECTION;
    result.stateMask = EventLCAUtil.readStateMask( widget, eventName );
    String detail = readEventStringProperty( widget, eventName, EVENT_PARAM_DETAIL );
    if( "check".equals( detail ) ) {
      result.detail = SWT.CHECK;
    } else if( "search".equals( detail ) ) {
      result.detail = SWT.ICON_SEARCH;
    } else if( "cancel".equals( detail ) ) {
      result.detail = SWT.ICON_CANCEL;
    } else if( "hyperlink".equals( detail ) ) {
      result.detail = RWT.HYPERLINK;
    }
    result.text = readEventStringProperty( widget, eventName, EVENT_PARAM_TEXT );
    return result;
  }

  private static Event createKeyEvent( int keyCode, int charCode, int stateMask ) {
    Event result = new Event();
    result.keyCode = translateKeyCode( keyCode );
    if( charCode == 0 ) {
      if( ( result.keyCode & SWT.KEYCODE_BIT ) == 0 ) {
        result.character = translateCharacter( result.keyCode );
      }
    } else {
      result.character = translateCharacter( charCode );
      if( Character.isLetter( charCode ) ) {
        // NOTE : keycodes from browser are the upper-case character, in SWT it is the lower-case
        result.keyCode = Character.toLowerCase( charCode );
      }
    }
    result.stateMask = stateMask;
    return result;
  }

  private static void checkAndProcessMouseEvent( Event event ) {
    boolean pass = false;
    Control control = ( Control )event.widget;
    if( control instanceof Scrollable ) {
      Scrollable scrollable = ( Scrollable )control;
      Rectangle clientArea = scrollable.getClientArea();
      pass = clientArea.contains( event.x, event.y );
    } else {
      pass = event.x >= 0 && event.y >= 0;
    }
    if( pass ) {
      event.widget.notifyListeners( event.type, event );
    }
  }

  private static Point readEventXYProperties( Control control, String eventName ) {
    int x = readEventIntProperty( control, eventName, EVENT_PARAM_X );
    int y = readEventIntProperty( control, eventName, EVENT_PARAM_Y );
    return control.getDisplay().map( null, control, x, y );
  }

  private static int readEventIntProperty( Widget widget, String eventName, String property ) {
    return readEventPropertyValue( getId( widget ), eventName, property ).asInt();
  }

  private static String readEventStringProperty( Widget widget, String eventName, String property )
  {
    JsonValue value = readEventPropertyValue( getId( widget ), eventName, property );
    return value != null ? value.asString() : null;
  }

  private static String[] getChildren( Control control ) {
    String[] result = null;
    if( control instanceof Composite ) {
      Composite composite = ( Composite )control;
      IControlHolderAdapter controlHolder = composite.getAdapter( IControlHolderAdapter.class );
      Control[] children = controlHolder.getControls();
      result = new String[ children.length ];
      for( int i = 0; i < result.length; i++ ) {
        result[ i ] = WidgetUtil.getId( children[ i ] );
      }
    }
    return result;
  }

  // [if] Fix for bug 263025, 297466, 223873 and more
  // some qooxdoo widgets with size (0,0) are not invisible
  private static boolean getVisible( Control control ) {
    Point size = control.getSize();
    return control.getVisible() && size.x > 0 && size.y > 0;
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

  static int getTraverseKey( int keyCode, int stateMask ) {
    int result = SWT.TRAVERSE_NONE;
    switch( keyCode ) {
      case 27:
        result = SWT.TRAVERSE_ESCAPE;
      break;
      case 13:
        result = SWT.TRAVERSE_RETURN;
      break;
      case 9:
        if( ( stateMask & SWT.MODIFIER_MASK ) == 0 ) {
          result = SWT.TRAVERSE_TAB_NEXT;
        } else if( stateMask == SWT.SHIFT ) {
          result = SWT.TRAVERSE_TAB_PREVIOUS;
        }
      break;
    }
    return result;
  }

  static int translateKeyCode( int keyCode ) {
    int result;
    switch( keyCode ) {
      case 16:
        result = SWT.SHIFT;
      break;
      case 17:
        result = SWT.CONTROL;
      break;
      case 18:
        result = SWT.ALT;
      break;
      case 20:
        result = SWT.CAPS_LOCK;
      break;
      case 38:
        result = SWT.ARROW_UP;
      break;
      case 37:
        result = SWT.ARROW_LEFT;
      break;
      case 39:
        result = SWT.ARROW_RIGHT;
      break;
      case 40:
        result = SWT.ARROW_DOWN;
      break;
      case 33:
        result = SWT.PAGE_UP;
      break;
      case 34:
        result = SWT.PAGE_DOWN;
      break;
      case 35:
        result = SWT.END;
      break;
      case 36:
        result = SWT.HOME;
      break;
      case 45:
        result = SWT.INSERT;
      break;
      case 46:
        result = SWT.DEL;
      break;
      case 112:
        result = SWT.F1;
      break;
      case 113:
        result = SWT.F2;
      break;
      case 114:
        result = SWT.F3;
      break;
      case 115:
        result = SWT.F4;
      break;
      case 116:
        result = SWT.F5;
      break;
      case 117:
        result = SWT.F6;
      break;
      case 118:
        result = SWT.F7;
      break;
      case 119:
        result = SWT.F8;
      break;
      case 120:
        result = SWT.F9;
      break;
      case 121:
        result = SWT.F10;
      break;
      case 122:
        result = SWT.F11;
      break;
      case 123:
        result = SWT.F12;
      break;
      case 144:
        result = SWT.NUM_LOCK;
      break;
      case 44:
        result = SWT.PRINT_SCREEN;
      break;
      case 145:
        result = SWT.SCROLL_LOCK;
      break;
      case 19:
        result = SWT.PAUSE;
      break;
      default:
        result = keyCode;
    }
    return result;
  }

  private static char translateCharacter( int keyCode ) {
    char result = ( char )0;
    if( Character.isDefined( ( char )keyCode ) ) {
      result = ( char )keyCode;
    }
    return result;
  }

  private static String getQxCursor( Cursor newValue ) {
    String result = null;
    if( newValue != null ) {
      // TODO [rst] Find a better way of obtaining the Cursor value
      // TODO [tb] adjust strings to match name of constants
      int value = 0;
      try {
        Class cursorClass = Cursor.class;
        Field field = cursorClass.getDeclaredField( "value" );
        field.setAccessible( true );
        value = field.getInt( newValue );
      } catch( Exception e ) {
        throw new RuntimeException( e );
      }
      switch( value ) {
        case SWT.CURSOR_ARROW:
          result = "default";
        break;
        case SWT.CURSOR_WAIT:
          result = "wait";
        break;
        case SWT.CURSOR_APPSTARTING:
          result = "progress";
          break;
        case SWT.CURSOR_CROSS:
          result = "crosshair";
        break;
        case SWT.CURSOR_HELP:
          result = "help";
        break;
        case SWT.CURSOR_SIZEALL:
          result = "move";
        break;
        case SWT.CURSOR_SIZENS:
          result = "row-resize";
        break;
        case SWT.CURSOR_SIZEWE:
          result = "col-resize";
        break;
        case SWT.CURSOR_SIZEN:
          result = "n-resize";
        break;
        case SWT.CURSOR_SIZES:
          result = "s-resize";
        break;
        case SWT.CURSOR_SIZEE:
          result = "e-resize";
        break;
        case SWT.CURSOR_SIZEW:
          result = "w-resize";
        break;
        case SWT.CURSOR_SIZENE:
        case SWT.CURSOR_SIZENESW:
          result = "ne-resize";
        break;
        case SWT.CURSOR_SIZESE:
          result = "se-resize";
        break;
        case SWT.CURSOR_SIZESW:
          result = "sw-resize";
        break;
        case SWT.CURSOR_SIZENW:
        case SWT.CURSOR_SIZENWSE:
          result = "nw-resize";
        break;
        case SWT.CURSOR_IBEAM:
          result = "text";
        break;
        case SWT.CURSOR_HAND:
          result = "pointer";
        break;
        case SWT.CURSOR_NO:
          result = "not-allowed";
        break;
        case SWT.CURSOR_UPARROW:
          result = CURSOR_UPARROW;
        break;
      }
    }
    return result;
  }

  private static boolean hasKeyListener( Control control ) {
    return isListening( control, SWT.KeyUp ) || isListening( control, SWT.KeyDown );
  }

  private static void preserveMouseListeners( Control control ) {
    WidgetLCAUtil.preserveListener( control,
                                    PROP_MOUSE_DOWN_LISTENER,
                                    isListening( control, SWT.MouseDown ) );
    WidgetLCAUtil.preserveListener( control,
                                    PROP_MOUSE_UP_LISTENER,
                                    isListening( control, SWT.MouseUp ) );
    WidgetLCAUtil.preserveListener( control,
                                    PROP_MOUSE_DOUBLE_CLICK_LISTENER,
                                    isListening( control, SWT.MouseDoubleClick ) );
  }

  private static void preserveFocusListeners( Control control ) {
    WidgetLCAUtil.preserveListener( control,
                                    PROP_FOCUS_IN_LISTENER,
                                    isListening( control, SWT.FocusIn ) );
    WidgetLCAUtil.preserveListener( control,
                                    PROP_FOCUS_OUT_LISTENER,
                                    isListening( control, SWT.FocusOut ) );
  }

  private static void preserveActivateListeners( Control control ) {
    // Note: Shell "Activate" event is handled by ShellLCA
    if( !( control instanceof Shell ) ) {
      WidgetLCAUtil.preserveListener( control,
                                      PROP_ACTIVATE_LISTENER,
                                      isListening( control, SWT.Activate ) );
      WidgetLCAUtil.preserveListener( control,
                                      PROP_DEACTIVATE_LISTENER,
                                      isListening( control, SWT.Deactivate ) );
    }
  }

}
