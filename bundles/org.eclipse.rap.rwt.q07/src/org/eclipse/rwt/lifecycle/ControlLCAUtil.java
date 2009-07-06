/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.rwt.lifecycle;

import java.io.IOException;
import java.lang.reflect.Field;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.service.IServiceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.internal.widgets.IControlAdapter;
import org.eclipse.swt.internal.widgets.IWidgetGraphicsAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;


/**
 * Utility class that provides a number of useful static methods to support the
 * implementation of life cycle adapters for {@link Control}s.
 *
 * @see WidgetLCAUtil
 * @since 1.0
 */
public class ControlLCAUtil {

  private static final Object[] PARAM_STYLE_BORDER
    = new Object[] { JSConst.JS_STYLE_FLAG_BORDER };

  private static final Object[] PARAM_STYLE_FLAT
    = new Object[] { JSConst.JS_STYLE_FLAG_FLAT };

  private static final JSListenerInfo FOCUS_GAINED_LISTENER_INFO
    = new JSListenerInfo( "focusin",
                          "org.eclipse.swt.EventUtil.focusGained",
                          JSListenerType.ACTION );
  private static final JSListenerInfo FOCUS_LOST_LISTENER_INFO
    = new JSListenerInfo( "focusout",
                          "org.eclipse.swt.EventUtil.focusLost",
                          JSListenerType.ACTION );

  private static final JSListenerInfo MOUSE_DOWN_LISTENER_INFO
    = new JSListenerInfo( "mousedown",
                          "org.eclipse.swt.EventUtil.mouseDown",
                          JSListenerType.ACTION );
  private static final JSListenerInfo MOUSE_UP_LISTENER_INFO
    = new JSListenerInfo( "mouseup",
                          "org.eclipse.swt.EventUtil.mouseUp",
                          JSListenerType.ACTION );

  private static final String JS_FUNC_ADD_ACTIVATE_LISTENER_WIDGET
    = "addActivateListenerWidget";
  private static final String JS_FUNC_REMOVE_ACTIVATE_LISTENER_WIDGET
    = "removeActivateListenerWidget";

  // Property names to preserve widget property values
  private static final String PROP_ACTIVATE_LISTENER = "activateListener";
  private static final String PROP_FOCUS_LISTENER = "focusListener";
  private static final String PROP_MOUSE_LISTENER = "mouseListener";
  private static final String PROP_KEY_LISTENER = "keyListener";
  private static final String PROP_TRAVERSE_LISTENER = "traverseListener";
  private static final String PROP_TAB_INDEX = "tabIndex";
  private static final String PROP_CURSOR = "cursor";
  private static final String PROP_BACKGROUND_IMAGE = "backgroundImage";

  private static final String USER_DATA_KEY_LISTENER = "keyListener";
  private static final String USER_DATA_TRAVERSE_LISTENER = "traverseListener";
  private static final String ATT_CANCEL_KEY_EVENT
    = ControlLCAUtil.class.getName() + "#cancelKeyEvent";
  private static final String ATT_ALLOW_KEY_EVENT
    = ControlLCAUtil.class.getName() + "#allowKeyEvent";
  static final String JSFUNC_CANCEL_EVENT
    = "org.eclipse.rwt.AsyncKeyEventUtil.getInstance().cancelEvent";
  static final String JSFUNC_ALLOW_EVENT
    = "org.eclipse.rwt.AsyncKeyEventUtil.getInstance().allowEvent";

  static final int MAX_STATIC_ZORDER = 300;


  private ControlLCAUtil() {
    // prevent instance creation
  }

  /**
   * Preserves the values of the following properties of the specified control:
   * <ul>
   * <li>bounds</li>
   * <li>z-index (except for Shells)</li>
   * <li>tab index</li>
   * <li>tool tip text</li>
   * <li>menu</li>
   * <li>visible</li>
   * <li>enabled</li>
   * <li>foreground</li>
   * <li>background</li>
   * <li>background image</li>
   * <li>font</li>
   * <li>cursor</li>
   * <li>whether ControlListeners are registered</li>
   * <li>whether ActivateListeners are registered</li>
   * <li>whether MouseListeners are registered</li>
   * <li>whether FocusListeners are registered</li>
   * <li>whether KeyListeners are registered</li>
   * <li>whether TraverseListeners are registered</li>
   * <li>whether HelpListeners are registered</li>
   * </ul>
   *
   * @param control the control whose parameters to preserve
   * @see #writeChanges(Control)
   */
  public static void preserveValues( final Control control ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( control );
    adapter.preserve( Props.BOUNDS, control.getBounds() );
    // TODO [rh] revise this (see also writeZIndex)
    if( !( control instanceof Shell ) ) {
      adapter.preserve( Props.Z_INDEX, new Integer( getZIndex( control ) ) );
    }
    adapter.preserve( PROP_TAB_INDEX, new Integer( getTabIndex( control ) ) );
    WidgetLCAUtil.preserveToolTipText( control, control.getToolTipText() );
    adapter.preserve( Props.MENU, control.getMenu() );
    adapter.preserve( Props.VISIBLE, Boolean.valueOf( control.getVisible() ) );
    WidgetLCAUtil.preserveEnabled( control, control.getEnabled() );
    IControlAdapter controlAdapter
      = ( IControlAdapter )control.getAdapter( IControlAdapter.class );
    WidgetLCAUtil.preserveForeground( control,
                                      controlAdapter.getUserForeground() );
    WidgetLCAUtil.preserveBackground( control,
                                      controlAdapter.getUserBackground(),
                                      controlAdapter.getBackgroundTransparency() );
    preserveBackgroundGradient( control );
    preserveBackgroundImage( control );
    WidgetLCAUtil.preserveFont( control, controlAdapter.getUserFont() );
    preserveRoundedBorder( control );
    adapter.preserve( PROP_CURSOR, control.getCursor() );
    adapter.preserve( Props.CONTROL_LISTENERS,
                      Boolean.valueOf( ControlEvent.hasListener( control ) ) );
    adapter.preserve( PROP_ACTIVATE_LISTENER,
                      Boolean.valueOf( ActivateEvent.hasListener( control ) ) );
    adapter.preserve( PROP_MOUSE_LISTENER,
                      Boolean.valueOf( MouseEvent.hasListener( control ) ) );
    if( ( control.getStyle() & SWT.NO_FOCUS ) == 0 ) {
      adapter.preserve( PROP_FOCUS_LISTENER,
                        Boolean.valueOf( FocusEvent.hasListener( control ) ) );
    }
    adapter.preserve( PROP_KEY_LISTENER,
                      Boolean.valueOf( KeyEvent.hasListener( control ) ) );
    adapter.preserve( PROP_TRAVERSE_LISTENER,
                      Boolean.valueOf( TraverseEvent.hasListener( control ) ) );
    WidgetLCAUtil.preserveHelpListener( control );
  }

  /**
   * Reads the bounds of the specified control from the current request and
   * applies it to the control. If no bounds are not submitted for the control,
   * it remains unchanged.
   *
   * @param control the control whose bounds to read and set
   */
  // TODO [rst] Revise: This seems to unnecessarily call getter and setter even
  //            when no bounds are submitted.
  public static void readBounds( final Control control ) {
    Rectangle current = control.getBounds();
    Rectangle newBounds = WidgetLCAUtil.readBounds( control, current );
    control.setBounds( newBounds );
  }

  /**
   * Determines whether the bounds of the given control have changed during the
   * processing of the current request and if so, writes JavaScript code to the
   * response that updates the client-side bounds.
   *
   * @param control the control whose bounds to write
   * @throws IOException
   */
  public static void writeBounds( final Control control ) throws IOException {
    Composite parent = control.getParent();
    WidgetLCAUtil.writeBounds( control, parent, control.getBounds() );
  }

  /**
   * Writes JavaScript code to the response that resets the bounds of a control.
   * This method is intended to be used by implementations of the method
   * {@link AbstractWidgetLCA#createResetHandlerCalls(String)}.
   *
   * @throws IOException
   */
  public static void resetBounds() throws IOException {
    WidgetLCAUtil.resetBounds();
  }

  /**
   * Determines whether the z-index of the given control has changed during the
   * processing of the current request and if so, writes JavaScript code to the
   * response that updates the client-side z-index.
   *
   * @param control the control whose z-index to write
   * @throws IOException
   */
  public static void writeZIndex( final Control control ) throws IOException {
    // TODO [rst] remove surrounding if statement as soon as z-order on shells
    //      is completely implemented
    if( !( control instanceof Shell ) ) {
      JSWriter writer = JSWriter.getWriterFor( control );
      Integer newValue = new Integer( getZIndex( control ) );
      writer.set( Props.Z_INDEX, JSConst.QX_FIELD_Z_INDEX, newValue, null );
    }
  }

  /**
   * Writes JavaScript code to the response that resets the z-index property of
   * a control. This method is intended to be used by implementations of the
   * method {@link AbstractWidgetLCA#createResetHandlerCalls(String)}.
   *
   * @throws IOException
   */
  public static void resetZIndex() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.reset( JSConst.QX_FIELD_Z_INDEX );
  }

  /**
   * Determines whether the visibility of the given control has changed during
   * the processing of the current request and if so, writes JavaScript code to
   * the response that updates the client-side visibility.
   *
   * @param control the control whose visibility to write
   * @throws IOException
   */
  // TODO [rh] there seems to be a qooxdoo problem when trying to change the
  //      visibility of a newly created widget (no flushGlobalQueues was called)
  //      MSG: Modification of property "visibility" failed with exception:
  //           Error - Element must be created previously!
  public static void writeVisible( final Control control )
    throws IOException
  {
    // we only need getVisible here (not isVisible), as qooxdoo also hides/shows
    // contained controls
    Boolean newValue = Boolean.valueOf( control.getVisible() );
    Boolean defValue = control instanceof Shell ? Boolean.FALSE : Boolean.TRUE;
    JSWriter writer = JSWriter.getWriterFor( control );
    writer.set( Props.VISIBLE, JSConst.QX_FIELD_VISIBLE, newValue, defValue );
  }

  /**
   * Writes JavaScript code to the response that resets the property
   * <code>visible</code> of a control. This method is intended to be used by
   * implementations of the method
   * {@link AbstractWidgetLCA#createResetHandlerCalls(String)}.
   *
   * @throws IOException
   */
  public static void resetVisible() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    // TODO [fappel]: check whether to use reset
    writer.set( JSConst.QX_FIELD_VISIBLE, true );
  }

  /**
   * Determines whether the property <code>enabled</code> of the given control
   * has changed during the processing of the current request and if so, writes
   * JavaScript code to the response that updates the client-side enabled
   * property.
   *
   * @param control the control whose enabled property to write
   * @throws IOException
   */
  public static void writeEnabled( final Control control )
    throws IOException
  {
    // Using isEnabled() would result in unnecessarily updating child widgets of
    // enabled/disabled controls.
    WidgetLCAUtil.writeEnabled( control, control.getEnabled() );
  }

  /**
   * Writes JavaScript code to the response that resets the property
   * <code>enabled</code> of a control. This method is intended to be used by
   * implementations of the method
   * {@link AbstractWidgetLCA#createResetHandlerCalls(String)}.
   *
   * @throws IOException
   */
  public static void resetEnabled() throws IOException {
    WidgetLCAUtil.resetEnabled();
  }

  /**
   * Determines for all of the following properties of the specified control
   * whether the property has changed during the processing of the current
   * request and if so, writes JavaScript code to the response that updates the
   * corresponding client-side property.
   * <ul>
   * <li>bounds</li>
   * <li>z-index (except for Shells)</li>
   * <li>tab index</li>
   * <li>tool tip text</li>
   * <li>menu</li>
   * <li>visible</li>
   * <li>enabled</li>
   * <li>foreground</li>
   * <li>background</li>
   * <li>background image</li>
   * <li>font</li>
   * <li>cursor</li>
   * <!--li>whether ControlListeners are registered</li-->
   * <li>whether ActivateListeners are registered</li>
   * <li>whether MouseListeners are registered</li>
   * <li>whether FocusListeners are registered</li>
   * <li>whether KeyListeners are registered</li>
   * <li>whether TraverseListeners are registered</li>
   * <li>whether HelpListeners are registered</li>
   * </ul>
   *
   * @param control the control whose properties to set
   * @throws IOException
   * @see #preserveValues(Control)
   */
  public static void writeChanges( final Control control ) throws IOException {
    writeBounds( control );
    writeZIndex( control );
    writeTabIndex( control );
    writeToolTip( control );
    writeMenu( control );
    writeVisible( control );
    writeEnabled( control );
    writeForeground( control );
    writeBackground( control );
    writeBackgroundGradient( control );
    writeBackgroundImage( control );
    writeFont( control );
    writeRoundedBorder( control );
    writeCursor( control );
//    TODO [rst] missing: writeControlListener( control );
    writeActivateListener( control );
    writeFocusListener( control );
    writeMouseListener( control );
    writeKeyListener( control );
    writeTraverseListener( control );
    writeKeyEventResponse( control );
    WidgetLCAUtil.writeHelpListener( control );
  }

  /**
   * Writes JavaScript code to the response that resets the following properties
   * of a control.
   * <ul>
   * <li>bounds</li>
   * <li>z-index (except for Shells)</li>
   * <li>tab index</li>
   * <li>tool tip text</li>
   * <li>menu</li>
   * <li>visible</li>
   * <li>enabled</li>
   * <li>foreground</li>
   * <li>background</li>
   * <li>font</li>
   * <!--li>whether ControlListeners are registered</li>
   * <li>whether ActivateListeners are registered</li>
   * <li>whether FocusListeners are registered</li-->
   * </ul>
   * This method is intended to be used by implementations of the method
   * {@link AbstractWidgetLCA#createResetHandlerCalls(String)}.
   *
   * @throws IOException
   */
  public static void resetChanges() throws IOException {
    resetFocusListener();
    // resetting Activation Listener is automatically done by JSWriter#dispose
    // TODO [rst] What about resetting Control- and FocusListener? document this
    resetMenu();
    resetToolTip();
    resetFont();
    resetBackground();
    resetForeground();
    resetEnabled();
    resetVisible();
    resetTabIndex();
    resetZIndex();
    resetBounds();
  }

  /**
   * Writes JavaScript code to the response that adds client-side resize
   * listeners to a control. These listeners send notifications when the control
   * is resized.
   *
   * @param control the control to add a resize notification listener to
   * @throws IOException
   */
  // TODO [rst] Change parameter type to Control
  public static void writeResizeNotificator( final Widget control )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( control );
    writer.addListener( JSConst.QX_EVENT_CHANGE_WIDTH,
                        JSConst.JS_WIDGET_RESIZED );
    writer.addListener( JSConst.QX_EVENT_CHANGE_HEIGHT,
                        JSConst.JS_WIDGET_RESIZED );
  }

  /**
   * Writes JavaScript code to the response that removes the client-side resize
   * notification listeners from a control.
   *
   * @throws IOException
   */
  public static void resetResizeNotificator()
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.removeListener( JSConst.QX_EVENT_CHANGE_WIDTH,
                           JSConst.JS_WIDGET_RESIZED );
    writer.removeListener( JSConst.QX_EVENT_CHANGE_HEIGHT,
                           JSConst.JS_WIDGET_RESIZED );
  }

  /**
   * Writes JavaScript code to the response that adds client-side move listeners
   * to a control. These listeners send notifications when the control is moved.
   * @param control the control to add move notification listeners to
   *
   * @throws IOException
   */
  // TODO [rst] Change parameter type to Control
  public static void writeMoveNotificator( final Widget control )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( control );
    writer.addListener( JSConst.QX_EVENT_CHANGE_LOCATION_X,
                        JSConst.JS_WIDGET_MOVED );
    writer.addListener( JSConst.QX_EVENT_CHANGE_LOCATION_Y,
                        JSConst.JS_WIDGET_MOVED );
  }

  /**
   * Writes JavaScript code to the response that removes the client-side move
   * notification listeners from a control.
   *
   * @throws IOException
   */
  public static void resetMoveNotificator()
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.removeListener( JSConst.QX_EVENT_CHANGE_LOCATION_X,
                           JSConst.JS_WIDGET_MOVED );
    writer.removeListener( JSConst.QX_EVENT_CHANGE_LOCATION_Y,
                           JSConst.JS_WIDGET_MOVED );
  }

  /**
   * Determines whether the property <code>menu</code> of the given control
   * has changed during the processing of the current request and if so, writes
   * JavaScript code to the response that updates the client-side menu
   * property.
   *
   * @param control the control whose menu property to write
   * @throws IOException
   */
  public static void writeMenu( final Control control ) throws IOException {
    WidgetLCAUtil.writeMenu( control, control.getMenu() );
  }

  /**
   * Writes JavaScript code to the response that resets the property
   * <code>menu</code> of a control. This method is intended to be used by
   * implementations of the method
   * {@link AbstractWidgetLCA#createResetHandlerCalls(String)}.
   *
   * @throws IOException
   */
  public static void resetMenu() throws IOException {
    WidgetLCAUtil.resetMenu();
  }

  /**
   * Determines whether the tool tip of the given control has changed during the
   * processing of the current request and if so, writes JavaScript code to the
   * response that updates the client-side tool tip.
   *
   * @param control the control whose tool tip to write
   * @throws IOException
   */
  public static void writeToolTip( final Control control )
    throws IOException
  {
    WidgetLCAUtil.writeToolTip( control, control.getToolTipText() );
  }

  /**
   * Writes JavaScript code to the response that resets the tool tip of a
   * control. This method is intended to be used by implementations of the
   * method {@link AbstractWidgetLCA#createResetHandlerCalls(String)}.
   *
   * @throws IOException
   */
  public static void resetToolTip() throws IOException {
    WidgetLCAUtil.resetToolTip();
  }

  /**
   * Determines whether the property <code>foreground</code> of the given
   * control has changed during the processing of the current request and if so,
   * writes JavaScript code to the response that updates the client-side
   * foreground property.
   *
   * @param control the control whose foreground property to write
   * @throws IOException
   */
  public static void writeForeground( final Control control )
    throws IOException
  {
    IControlAdapter controlAdapter
      = ( IControlAdapter )control.getAdapter( IControlAdapter.class );
    WidgetLCAUtil.writeForeground( control,
                                   controlAdapter.getUserForeground() );
  }

  /**
   * Writes JavaScript code to the response that resets the property
   * <code>foreground</code> of a control. This method is intended to be used
   * by implementations of the method
   * {@link AbstractWidgetLCA#createResetHandlerCalls(String)}.
   *
   * @throws IOException
   */
  public static void resetForeground() throws IOException {
    WidgetLCAUtil.resetForeground();
  }

  /**
   * Determines whether the property <code>background</code> of the given
   * control has changed during the processing of the current request and if so,
   * writes JavaScript code to the response that updates the client-side
   * background property.
   *
   * @param control the control whose background property to write
   * @throws IOException
   */
  public static void writeBackground( final Control control ) throws IOException
  {
    IControlAdapter controlAdapter
      = ( IControlAdapter )control.getAdapter( IControlAdapter.class );
    WidgetLCAUtil.writeBackground( control,
                                   controlAdapter.getUserBackground(),
                                   controlAdapter.getBackgroundTransparency() );
  }

  /**
   * Preserves the value of the specified widget's background image.
   *
   * @param control the control whose background image property to preserve
   * @see #writeBackgroundImage(Control)
   */
  public static void preserveBackgroundImage( final Control control ) {
    IControlAdapter controlAdapter
      = ( IControlAdapter )control.getAdapter( IControlAdapter.class );
    Image image = controlAdapter.getUserBackgroundImage();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( control );
    adapter.preserve( PROP_BACKGROUND_IMAGE, image );
  }

  /**
   * Determines whether the background image of the given control has changed
   * during the processing of the current request and if so, writes JavaScript
   * code to the response that updates the client-side background image
   * property.
   *
   * @param control the control whose background image property to write
   * @throws IOException
   */
  public static void writeBackgroundImage( final Control control )
    throws IOException
  {
    IControlAdapter controlAdapter
      = ( IControlAdapter )control.getAdapter( IControlAdapter.class );
    Image image = controlAdapter.getUserBackgroundImage();
    if( WidgetLCAUtil.hasChanged( control, PROP_BACKGROUND_IMAGE, image, null ) )
    {
      JSWriter writer = JSWriter.getWriterFor( control );
      if( image != null ) {
        String imagePath = ResourceFactory.getImagePath( image );
        writer.set( JSConst.QX_FIELD_BG_IMAGE, imagePath );
      } else {
        writer.reset( JSConst.QX_FIELD_BG_IMAGE );
      }
    }
  }

  /**
   * Writes JavaScript code to the response that resets the property
   * <code>background</code> of a control. This method is intended to be used
   * by implementations of the method
   * {@link AbstractWidgetLCA#createResetHandlerCalls(String)}.
   *
   * @throws IOException
   */
  public static void resetBackground() throws IOException {
    WidgetLCAUtil.resetBackground();
  }

  /**
   * Checks the given control for common SWT style flags (e.g.
   * <code>SWT.BORDER</code>) and if present, writes code to pass the according
   * states to the client.
   *
   * @param control
   * @throws IOException
   */
  public static void writeStyleFlags( final Control control ) throws IOException
  {
    WidgetLCAUtil.writeStyleFlag( control, SWT.BORDER, "BORDER" );
    WidgetLCAUtil.writeStyleFlag( control, SWT.FLAT, "FLAT" );
  }

  /**
   * Writes JavaScript code to the response that resets the style flags.
   * <p>This method is intended to be used by implementations of the method
   * {@link AbstractWidgetLCA#createResetHandlerCalls(String)}.</p>
   *
   * @throws IOException
   */
  public static void resetStyleFlags() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.call( JSConst.QX_FUNC_REMOVE_STATE, PARAM_STYLE_BORDER );
    writer.call( JSConst.QX_FUNC_REMOVE_STATE, PARAM_STYLE_FLAT );
  }

  /**
   * Determines whether the property <code>font</code> of the given control
   * has changed during the processing of the current request and if so, writes
   * JavaScript code to the response that updates the client-side font property.
   *
   * @param control the control whose font property to write
   * @throws IOException
   */
  public static void writeFont( final Control control ) throws IOException {
    Object adapter = control.getAdapter( IControlAdapter.class );
    IControlAdapter controlAdapter = ( IControlAdapter )adapter;
    Font newValue = controlAdapter.getUserFont();
    WidgetLCAUtil.writeFont( control, newValue );
  }

  /**
   * Writes JavaScript code to the response that resets the property
   * <code>font</code> of a control. This method is intended to be used by
   * implementations of the method
   * {@link AbstractWidgetLCA#createResetHandlerCalls(String)}.
   *
   * @throws IOException
   */
  public static void resetFont() throws IOException {
    WidgetLCAUtil.resetFont();
  }

  static void writeCursor( final Control control ) throws IOException {
    Cursor newValue = control.getCursor();
    if( WidgetLCAUtil.hasChanged( control, PROP_CURSOR, newValue, null ) ) {
      String qxCursor = getQxCursor( newValue );
      JSWriter writer = JSWriter.getWriterFor( control );
      if( qxCursor == null ) {
        writer.reset( JSConst.QX_FIELD_CURSOR );
      } else {
        writer.set( JSConst.QX_FIELD_CURSOR, qxCursor );
      }
    }
  }

  static void resetCursor() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.reset( JSConst.QX_FIELD_CURSOR );
  }

  public static void writeActivateListener( final Control control )
    throws IOException
  {
    Boolean newValue = Boolean.valueOf( ActivateEvent.hasListener( control ) );
    Boolean defValue = Boolean.FALSE;
    String prop = PROP_ACTIVATE_LISTENER;
    Shell shell = control.getShell();
    if(    !shell.isDisposed()
        && WidgetLCAUtil.hasChanged( control, prop, newValue, defValue ) )
    {
      String function = newValue.booleanValue()
                      ? JS_FUNC_ADD_ACTIVATE_LISTENER_WIDGET
                      : JS_FUNC_REMOVE_ACTIVATE_LISTENER_WIDGET;
      JSWriter writer = JSWriter.getWriterFor( control );
      Object[] args = new Object[] { control };
      writer.call( shell, function, args );
    }
  }

  static void resetActivateListener( final Control control )
    throws IOException
  {
    Shell shell = control.getShell();
    if( !shell.isDisposed() && ActivateEvent.hasListener( control ) ) {
      JSWriter writer = JSWriter.getWriterFor( control );
      writer.call( shell,
                   JS_FUNC_REMOVE_ACTIVATE_LISTENER_WIDGET,
                   new Object[] { control } );
    }
  }

  /**
   * Note that there is no corresponding readData method to fire the focus
   * events that are send by the JavaScript event listeners that are registered
   * below.
   * FocusEvents are thrown when the focus is changed programmatically and when
   * it is change by the user.
   * Therefore the methods in Display that maintain the current focusControl
   * also fire FocusEvents. The current client-side focusControl is read in
   * DisplayLCA#readData.
   */
  private static void writeFocusListener( final Control control )
    throws IOException
  {
    if( ( control.getStyle() & SWT.NO_FOCUS ) == 0 ) {
      JSWriter writer = JSWriter.getWriterFor( control );
      boolean hasListener = FocusEvent.hasListener( control );
      writer.updateListener( FOCUS_GAINED_LISTENER_INFO,
                             PROP_FOCUS_LISTENER,
                             hasListener );
      writer.updateListener( FOCUS_LOST_LISTENER_INFO,
                             PROP_FOCUS_LISTENER,
                             hasListener );
    }
  }

  private static void resetFocusListener() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.removeListener( FOCUS_GAINED_LISTENER_INFO.getEventType(),
                           FOCUS_GAINED_LISTENER_INFO.getJSListener() );
    writer.removeListener( FOCUS_LOST_LISTENER_INFO.getEventType(),
                           FOCUS_LOST_LISTENER_INFO.getJSListener() );
  }

  private static void writeMouseListener( final Control control )
    throws IOException
  {
    boolean hasListener = MouseEvent.hasListener( control );
    JSWriter writer = JSWriter.getWriterFor( control );
    writer.updateListener( MOUSE_UP_LISTENER_INFO,
                           PROP_MOUSE_LISTENER,
                           hasListener );
    writer.updateListener( MOUSE_DOWN_LISTENER_INFO,
                           PROP_MOUSE_LISTENER,
                           hasListener );
  }

  static void writeKeyListener( final Control control )
    throws IOException
  {
    String prop = PROP_KEY_LISTENER;
    Boolean hasListener = Boolean.valueOf( KeyEvent.hasListener( control ) );
    Boolean defValue = Boolean.FALSE;
    if( WidgetLCAUtil.hasChanged( control, prop, hasListener, defValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( control );
      if( hasListener.booleanValue() ) {
        Object[] args = new Object[] { USER_DATA_KEY_LISTENER, hasListener };
        writer.call( "setUserData", args );
      } else {
        Object[] args = new Object[] { USER_DATA_KEY_LISTENER, null };
        writer.call( "setUserData", args );
      }
    }
  }

  static void writeTraverseListener( final Control control )
    throws IOException
  {
    String prop = PROP_TRAVERSE_LISTENER;
    Boolean hasListener
      = Boolean.valueOf( TraverseEvent.hasListener( control ) );
    Boolean defValue = Boolean.FALSE;
    if( WidgetLCAUtil.hasChanged( control, prop, hasListener, defValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( control );
      if( hasListener.booleanValue() ) {
        Object[] args = new Object[] {
          USER_DATA_TRAVERSE_LISTENER,
          hasListener
        };
        writer.call( "setUserData", args );
      } else {
        Object[] args = new Object[] { USER_DATA_TRAVERSE_LISTENER, null };
        writer.call( "setUserData", args );
      }
    }
  }

  //////////
  // Z-Index

  /**
   * Determines the z-index to render for a given control.
   * @param control the control whose z-index is requested
   * @return the z-index
   */
  // TODO [rst] also document the meaning of the returned number
  public static int getZIndex( final Control control ) {
    int max = MAX_STATIC_ZORDER;
    if( control.getParent() != null ) {
      // TODO [rh] revise: determining the childrenCount by getting all the
      //      children might be bad performance-wise. This was done in order to
      //      eliminate Composite#getChildrenCount() which is no API in SWT
      max = Math.max( control.getParent().getChildren().length, max );
    }
    Object adapter = control.getAdapter( IControlAdapter.class );
    IControlAdapter controlAdapter = ( IControlAdapter )adapter;
    return max - controlAdapter.getZIndex();
  }

  //////////////////////
  // Background gradient

  private static void preserveBackgroundGradient( final Control control ) {
    Object adapter = control.getAdapter( IWidgetGraphicsAdapter.class );
    IWidgetGraphicsAdapter gfxAdapter = ( IWidgetGraphicsAdapter )adapter;
    Color[] bgGradientColors = gfxAdapter.getBackgroundGradientColors();
    int[] bgGradientPercents = gfxAdapter.getBackgroundGradientPercents();
    WidgetLCAUtil.preserveBackgroundGradient( control,
                                              bgGradientColors,
                                              bgGradientPercents );
  }

  private static void writeBackgroundGradient( final Control control )
    throws IOException
  {
    Object adapter = control.getAdapter( IWidgetGraphicsAdapter.class );
    IWidgetGraphicsAdapter gfxAdapter = ( IWidgetGraphicsAdapter )adapter;
    Color[] bgGradientColors = gfxAdapter.getBackgroundGradientColors();
    int[] bgGradientPercents = gfxAdapter.getBackgroundGradientPercents();
    WidgetLCAUtil.writeBackgroundGradient( control,
                                           bgGradientColors,
                                           bgGradientPercents );
  }

  /////////////////
  // Rounded border

  private static void preserveRoundedBorder( final Control control ) {
    Object adapter = control.getAdapter( IWidgetGraphicsAdapter.class );
    IWidgetGraphicsAdapter gfxAdapter = ( IWidgetGraphicsAdapter )adapter;
    int width = gfxAdapter.getRoundedBorderWidth();
    Color color = gfxAdapter.getRoundedBorderColor();
    Rectangle radius = gfxAdapter.getRoundedBorderRadius();
    WidgetLCAUtil.preserveRoundedBorder( control, width, color, radius );
  }

  private static void writeRoundedBorder( final Control control )
    throws IOException
  {
    Object adapter = control.getAdapter( IWidgetGraphicsAdapter.class );
    IWidgetGraphicsAdapter gfxAdapter = ( IWidgetGraphicsAdapter )adapter;
    int width = gfxAdapter.getRoundedBorderWidth();
    Color color = gfxAdapter.getRoundedBorderColor();
    Rectangle radius = gfxAdapter.getRoundedBorderRadius();
    WidgetLCAUtil.writeRoundedBorder( control, width, color, radius );
  }

  ////////////
  // Tab index

  private static void writeTabIndex( final Control control ) throws IOException
  {
    if( control instanceof Shell ) {
      // tabIndex must be a positive value
      computeTabIndices( ( Shell )control, 1 );
    }
    int tabIndex = getTabIndex( control );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( control );
    // Don't write tabIndex when it is -1 initially
    // With this we assume that every client-side widget has a proper initial
    // tabIndex setting
    if( tabIndex > -1 || adapter.isInitialized() ) {
      Integer newValue = new Integer( tabIndex );
      JSWriter writer = JSWriter.getWriterFor( control );
      // there is no reliable default value for all controls
      writer.set( PROP_TAB_INDEX, JSConst.QX_FIELD_TAB_INDEX, newValue );
    }
  }

  private static void resetTabIndex() throws IOException {
    JSWriter writer = JSWriter.getWriterForResetHandler();
    writer.reset( JSConst.QX_FIELD_TAB_INDEX );
  }

  /**
   * Recursively computes the tab indices for all child controls of a given
   * composite and stores the resulting values in the control adapters.
   */
  private static int computeTabIndices( final Composite comp, final int index )
  {
    Control[] children = comp.getChildren();
    for( int i = 0; i < children.length; i++ ) {
      Control control = children[ i ];
      Object adapter = control.getAdapter( IControlAdapter.class );
      IControlAdapter controlAdapter = ( IControlAdapter )adapter;
      controlAdapter.setTabIndex( -1 );
    }
    Control[] tabList = comp.getTabList();
    int nextIndex = index;
    for( int i = 0; i < tabList.length; i++ ) {
      Control control = tabList[ i ];
      Object adapter = control.getAdapter( IControlAdapter.class );
      IControlAdapter controlAdapter = ( IControlAdapter )adapter;
      controlAdapter.setTabIndex( nextIndex );
      // for Links, leave a range out to be assigned to hrefs on the client
      if( control instanceof Link ) {
        nextIndex += 300;
      } else {
        nextIndex += 1;
      }
      if( control instanceof Composite ) {
        nextIndex = computeTabIndices( ( Composite )control, nextIndex );
      }
    }
    return nextIndex;
  }

  private static int getTabIndex( final Control control ) {
    int result = -1;
    if( takesFocus( control ) ) {
      Object adapter = control.getAdapter( IControlAdapter.class );
      IControlAdapter controlAdapter = ( IControlAdapter )adapter;
      result = controlAdapter.getTabIndex();
    }
    return result;
  }

  // TODO [rh] Eliminate instance checks. Let the respective classes always return NO_FOCUS
  private static boolean takesFocus( final Control control ) {
    boolean result = true;
    result &= ( control.getStyle() & SWT.NO_FOCUS ) == 0;
    result &= control.getClass() != Composite.class;
    result &= control.getClass() != ToolBar.class;
    result &= control.getClass() != SashForm.class;
    return result;
  }
  
  /////////////////////
  // Selection Listener

  public static void processSelection( final Widget widget,
                                       final Item item,
                                       final boolean readBounds )
  {
    String eventId = JSConst.EVENT_WIDGET_SELECTED;
    if( WidgetLCAUtil.wasEventSent( widget, eventId ) ) {
      SelectionEvent event;
      event = createSelectionEvent( widget,
                                    item,
                                    readBounds,
                                    SelectionEvent.WIDGET_SELECTED );
      event.processEvent();
    }
    eventId = JSConst.EVENT_WIDGET_DEFAULT_SELECTED;
    if( WidgetLCAUtil.wasEventSent( widget, eventId ) ) {
      SelectionEvent event;
      event = createSelectionEvent( widget,
                                    item,
                                    readBounds,
                                    SelectionEvent.WIDGET_DEFAULT_SELECTED );
      event.processEvent();
    }
  }

  private static SelectionEvent createSelectionEvent( final Widget widget,
                                                      final Item item,
                                                      final boolean readBounds,
                                                      final int type )
  {
    Rectangle bounds;
    if( widget instanceof Control && readBounds ) {
      Control control = ( Control )widget;
      bounds = WidgetLCAUtil.readBounds( control, control.getBounds() );
    } else {
      bounds = new Rectangle( 0, 0, 0, 0 );
    }
    return new SelectionEvent( widget,
                               item,
                               type,
                               bounds,
                               null,
                               true,
                               SWT.NONE );
  }

  public static void processKeyEvents( final Control control ) {
    if( WidgetLCAUtil.wasEventSent( control, JSConst.EVENT_KEY_DOWN ) ) {
      final int keyCode = readIntParam( JSConst.EVENT_KEY_DOWN_KEY_CODE );
      final int charCode = readIntParam( JSConst.EVENT_KEY_DOWN_CHAR_CODE );
      String modifier = readStringParam( JSConst.EVENT_KEY_DOWN_MODIFIER );
      final int stateMask = translateModifier( modifier );
      final int traverseKey = getTraverseKey( keyCode, stateMask );
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          boolean allow = true;
          if( traverseKey != SWT.TRAVERSE_NONE ) {
            TraverseEvent traverseEvent = new TraverseEvent( control );
            initializeKeyEvent( traverseEvent, keyCode, charCode, stateMask );
            traverseEvent.detail = traverseKey;
            traverseEvent.processEvent();
            if( !traverseEvent.doit ) {
              allow = false;
            }
          }
          KeyEvent pressedEvent = new KeyEvent( control, KeyEvent.KEY_PRESSED );
          initializeKeyEvent( pressedEvent, keyCode, charCode, stateMask );
          pressedEvent.processEvent();
          if( pressedEvent.doit ) {
            KeyEvent releasedEvent
              = new KeyEvent( control, KeyEvent.KEY_RELEASED );
            initializeKeyEvent( releasedEvent, keyCode, charCode, stateMask );
            releasedEvent.processEvent();
          } else {
            allow = false;
          }
          if( allow ) {
            allowKeyEvent( control );
          } else {
            cancelKeyEvent( control );
          }
        }
      } );
    }
  }

  static int getTraverseKey( final int keyCode, final int stateMask ) {
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

  private static void initializeKeyEvent( final KeyEvent event,
                                          final int keyCode,
                                          final int charCode,
                                          final int stateMask )
  {
    if( charCode == 0 ) {
      event.keyCode = translateKeyCode( keyCode );
      if( ( event.keyCode & SWT.KEYCODE_BIT ) == 0 ) {
        event.character = translateCharacter( event.keyCode );
      }
    } else {
      event.keyCode = charCode;
      event.character = translateCharacter( charCode );
    }
    event.stateMask = stateMask;
  }

  static int translateKeyCode( final int keyCode ) {
    int result;
    switch( keyCode ) {
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

  private static char translateCharacter( final int keyCode ) {
    char result = ( char )0;
    if( Character.isDefined( ( char )keyCode ) ) {
      result = ( char )keyCode;
    }
    return result;
  }

  static int translateModifier( final String value ) {
    String[] modifiers = value.split( "," );
    int result = 0;
    for( int i = 0; i < modifiers.length; i++ ) {
      if( "ctrl".equals( modifiers[ i ] ) ) {
        result  |= SWT.CTRL;
      } else if( "alt".equals( modifiers[ i ] ) ) {
        result |= SWT.ALT;
      } else if ( "shift".equals(  modifiers[ i ] ) ) {
        result |= SWT.SHIFT;
      }
    }
    return result;
  }

  private static void cancelKeyEvent( final Widget widget) {
    RWT.getServiceStore().setAttribute( ATT_CANCEL_KEY_EVENT, widget );
  }

  private static void allowKeyEvent( final Widget widget ) {
    RWT.getServiceStore().setAttribute( ATT_ALLOW_KEY_EVENT, widget );
  }

  private static void writeKeyEventResponse( final Control control )
    throws IOException
  {
    IServiceStore serviceStore = RWT.getServiceStore();
    if( serviceStore.getAttribute( ATT_ALLOW_KEY_EVENT ) == control ) {
      JSWriter writer = JSWriter.getWriterFor( control );
      writer.callStatic( JSFUNC_ALLOW_EVENT, null );
    } else if( serviceStore.getAttribute( ATT_CANCEL_KEY_EVENT ) == control ) {
      JSWriter writer = JSWriter.getWriterFor( control );
      writer.callStatic( JSFUNC_CANCEL_EVENT, null );
    }
  }

  public static void processMouseEvents( final Control control ) {
    if( WidgetLCAUtil.wasEventSent( control, JSConst.EVENT_MOUSE_DOWN ) ) {
      MouseEvent event = new MouseEvent( control, MouseEvent.MOUSE_DOWN );
      event.button
        = readIntParam( JSConst.EVENT_MOUSE_DOWN_BUTTON );
      Point point = readXYParams( control,
                                  JSConst.EVENT_MOUSE_DOWN_X,
                                  JSConst.EVENT_MOUSE_DOWN_Y );
      event.x = point.x;
      event.y = point.y;
      event.time = readIntParam( JSConst.EVENT_MOUSE_DOWN_TIME );
      checkAndProcessMouseEvent( event );
    }
    String eventId = JSConst.EVENT_MOUSE_DOUBLE_CLICK;
    if( WidgetLCAUtil.wasEventSent( control, eventId ) ) {
      MouseEvent event
        = new MouseEvent( control, MouseEvent.MOUSE_DOUBLE_CLICK );
      event.button
        = readIntParam( JSConst.EVENT_MOUSE_DOUBLE_CLICK_BUTTON );
      Point point = readXYParams( control,
                                  JSConst.EVENT_MOUSE_DOUBLE_CLICK_X,
                                  JSConst.EVENT_MOUSE_DOUBLE_CLICK_Y );
      event.x = point.x;
      event.y = point.y;
      event.time = readIntParam( JSConst.EVENT_MOUSE_DOUBLE_CLICK_TIME );
      checkAndProcessMouseEvent( event );
    }
    if( WidgetLCAUtil.wasEventSent( control, JSConst.EVENT_MOUSE_UP ) ) {
      MouseEvent event = new MouseEvent( control, MouseEvent.MOUSE_UP );
      event.button = readIntParam( JSConst.EVENT_MOUSE_UP_BUTTON );
      Point point = readXYParams( control,
                                  JSConst.EVENT_MOUSE_UP_X,
                                  JSConst.EVENT_MOUSE_UP_Y );
      event.x = point.x;
      event.y = point.y;
      event.time = readIntParam( JSConst.EVENT_MOUSE_UP_TIME );
      checkAndProcessMouseEvent( event );
    }
  }

  private static void checkAndProcessMouseEvent( final MouseEvent event ) {
    boolean pass = false;
    Control control = ( Control )event.widget;
    if( control instanceof Scrollable ) {
      Scrollable scrollable = ( Scrollable )control;
      Rectangle clientArea = scrollable.getClientArea();
      pass = event.x >= clientArea.x && event.y >= clientArea.y;
    } else {
      pass = event.x >= 0 && event.y >= 0;
    }
    if( pass ) {
      event.processEvent();
    }
  }

  private static String readStringParam( final String paramName ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String value = request.getParameter( paramName );
    return value;
  }

  private static int readIntParam( final String paramName ) {
    String value = readStringParam( paramName );
    return Integer.parseInt( value );
  }

  private static Point readXYParams( final Control control,
                                     final String paramNameX,
                                     final String paramNameY )
  {
    int x = readIntParam( paramNameX );
    int y = readIntParam( paramNameY );
    return control.getDisplay().map( null, control, x, y );
  }

  private static String getQxCursor( final Cursor newValue ) {
    String result = null;
    if( newValue != null ) {
      // TODO [rst] Find a better way of obtaining the Cursor value
      int value = 0;
      try {
        Class cursorClass = Cursor.class;
        Field field = cursorClass.getDeclaredField( "value" );
        field.setAccessible( true );
        value = field.getInt( newValue );
      } catch( Exception e ) {
        throw new RuntimeException();
      }
      switch( value ) {
        case SWT.CURSOR_ARROW:
          result = "default";
        break;
        case SWT.CURSOR_WAIT:
          result = "wait";
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
          result = "ne-resize";
        break;
        case SWT.CURSOR_SIZESE:
          result = "se-resize";
        break;
        case SWT.CURSOR_SIZESW:
          result = "sw-resize";
        break;
        case SWT.CURSOR_SIZENW:
          result = "nw-resize";
        break;
        case SWT.CURSOR_IBEAM:
          result = "text";
        break;
        case SWT.CURSOR_HAND:
          result = "pointer";
        break;
      }
    }
    return result;
  }
}
