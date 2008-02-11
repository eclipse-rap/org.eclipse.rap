/*******************************************************************************
 * Copyright (c) 2002-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.lifecycle;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.events.ActivateEvent;
import org.eclipse.swt.internal.widgets.IControlAdapter;
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

  private static final String JS_FUNC_ADD_ACTIVATE_LISTENER_WIDGET
    = "addActivateListenerWidget";
  private static final String JS_FUNC_REMOVE_ACTIVATE_LISTENER_WIDGET
    = "removeActivateListenerWidget";

  // Property names to preserve widget property values
  private static final String PROP_ACTIVATE_LISTENER = "activateListener";
  private static final String PROP_FOCUS_LISTENER = "focusListener";
  private static final String PROP_TAB_INDEX = "tabIndex";

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
   * <li>font</li>
   * <li>whether ControlListeners are registered</li>
   * <li>whether ActivateListeners are registered</li>
   * <li>whether FocusListeners are registered</li>
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
                                      controlAdapter.getUserBackground() );
    WidgetLCAUtil.preserveFont( control, controlAdapter.getUserFont() );
    adapter.preserve( Props.CONTROL_LISTENERS,
                      Boolean.valueOf( ControlEvent.hasListener( control ) ) );
    adapter.preserve( PROP_ACTIVATE_LISTENER,
                      Boolean.valueOf( ActivateEvent.hasListener( control ) ) );
    if( ( control.getStyle() & SWT.NO_FOCUS ) == 0 ) {
      adapter.preserve( PROP_FOCUS_LISTENER,
                        Boolean.valueOf( FocusEvent.hasListener( control ) ) );
    }
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
   * <li>font</li>
   * <!--li>whether ControlListeners are registered</li-->
   * <li>whether ActivateListeners are registered</li>
   * <li>whether FocusListeners are registered</li>
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
    writeFont( control );
//    TODO [rst] missing: writeControlListener( control );
    writeActivateListener( control );
    writeFocusListener( control );
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
                                   controlAdapter.getUserBackground() );
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
   * Writes SWT style flags that must be handled on the client side (e.g.
   * <code>SWT.BORDER</code>). Flags are transmitted as qooxdoo <q>states</q>
   * that will be respected by the appearance that renders the widget.
   *
   * @param control
   * @throws IOException
   */
  public static void writeStyleFlags( final Control control ) throws IOException 
  {
    JSWriter writer = JSWriter.getWriterFor( control );
    if( ( control.getStyle() & SWT.BORDER ) != 0 ) {
      writer.call( JSConst.QX_FUNC_ADD_STATE, PARAM_STYLE_BORDER );
    }
    if( ( control.getStyle() & SWT.FLAT ) != 0 ) {
      writer.call( JSConst.QX_FUNC_ADD_STATE, PARAM_STYLE_FLAT );
    }
  }

  /**
   * Writes JavaScript code to the response that resets the style flags
   * TODO
   * property
   * <code>enabled</code> of a control. This method is intended to be used by
   * implementations of the method
   * {@link AbstractWidgetLCA#createResetHandlerCalls(String)}.
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
      //      eliminate Composite#getChildrenCount() which no API in SWT
      max = Math.max( control.getParent().getChildren().length, max );
    }
    Object adapter = control.getAdapter( IControlAdapter.class );
    IControlAdapter controlAdapter = ( IControlAdapter )adapter;
    return max - controlAdapter.getZIndex();
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

  /**
   * Determines the tab index to write for a given control.
   */
  private static int getTabIndex( final Control control ) {
    int result = -1;
    if( takesFocus( control ) ) {
      Object adapter = control.getAdapter( IControlAdapter.class );
      IControlAdapter controlAdapter = ( IControlAdapter )adapter;
      result = controlAdapter.getTabIndex();
    }
    return result;
  }

  // TODO [rst] Refactor: should this method be part of Control?
  private static boolean takesFocus( final Control control ) {
    boolean result = true;
    result &= ( control.getStyle() & SWT.NO_FOCUS ) == 0;
    result &= control.getClass() != Composite.class;
    result &= control.getClass() != ToolBar.class;
    result &= control.getClass() != SashForm.class;
    return result;
  }

  /////////////////////
  // SELECTION LISTENER

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
}
