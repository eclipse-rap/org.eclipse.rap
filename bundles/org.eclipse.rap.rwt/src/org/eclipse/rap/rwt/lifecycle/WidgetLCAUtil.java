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
package org.eclipse.rap.rwt.lifecycle;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.jsonToJava;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.getClientMessage;

import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.NotifyOperation;
import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Widget;


/**
 * Utility class that provides a number of useful static methods to support the implementation of
 * life cycle adapters (LCAs) for {@link Widget}s.
 *
 * @since 2.0
 * @deprecated New custom widgets should use the RemoteObject API instead of LCAs.
 * @see org.eclipse.rap.rwt.remote.RemoteObject
 * @see ControlLCAUtil
 */
@Deprecated
public final class WidgetLCAUtil {

  private WidgetLCAUtil() {
    // prevent instantiation
  }

  /**
   * Reads the bounds of the specified widget from the current request. If the
   * bounds of this widget was not sent with the current request, the specified
   * default is returned.
   *
   * @param widget the widget whose bounds to read
   * @param defaultValue the default bounds
   * @return the bounds as read from the request or the default bounds if no
   *         bounds were passed within the current request
   */
  public static Rectangle readBounds( Widget widget, Rectangle defaultValue ) {
    return org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.readBounds( widget, defaultValue );
  }

  /**
   * Reads the bounds of the widget specified by its id from the current
   * request. If the bounds of this widget was not sent with the current
   * request, the specified default is returned.
   *
   * @param widgetId the widget id of the widget whose bounds to read
   * @param defaultValue the default bounds
   * @return the bounds as read from the request or the default bounds if no
   *         bounds were passed within the current request
   */
  public static Rectangle readBounds( String widgetId, Rectangle defaultValue ) {
    return org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.readBounds( widgetId, defaultValue );
  }

  /**
   * Process a <code>HelpEvent</code> if the current request specifies that
   * there occurred a help event for the given <code>widget</code>.
   *
   * @param widget the widget to process
   */
  public static void processHelp( Widget widget ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.processHelp( widget );
  }

  /**
   * Preserves the value of the property <code>bounds</code> of the
   * specified widget.
   *
   * @param widget the widget whose bounds property to preserve
   * @param bounds the value to preserve
   * @see #renderBounds(Widget, Rectangle)
   */
  public static void preserveBounds( Widget widget, Rectangle bounds ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.preserveBounds( widget, bounds );
  }

  /**
   * Preserves the value of the property <code>enabled</code> of the specified
   * widget.
   *
   * @param widget the widget whose enabled property to preserve
   * @param enabled the value to preserve
   * @see #renderEnabled(Widget, boolean)
   */
  public static void preserveEnabled( Widget widget, boolean enabled ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.preserveEnabled( widget, enabled );
  }

  /**
   * Preserves the value of the property <code>toolTipText</code> of the
   * specified widget.
   *
   * @param widget the widget whose toolTip property to preserve
   * @param toolTip the value to preserve
   * @see #renderToolTip(Widget, String)
   */
  public static void preserveToolTipText( Widget widget, String toolTip ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.preserveToolTipText( widget, toolTip );
  }

  /**
   * Preserves the value of the property <code>font</code> of the specified
   * widget.
   *
   * @param widget the widget whose font property to preserve
   * @param font the value to preserve
   * @see #renderFont(Widget, Font)
   */
  public static void preserveFont( Widget widget, Font font ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.preserveFont( widget, font );
  }

  /**
   * Preserves the value of the property <code>foreground</code> of the
   * specified widget.
   *
   * @param widget the widget whose foreground property to preserve
   * @param foreground the value to preserve
   * @see #renderForeground(Widget, Color)
   */
  public static void preserveForeground( Widget widget, Color foreground ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.preserveForeground( widget, foreground );
  }

  /**
   * Preserves the value of the property <code>background</code> of the
   * specified widget.
   *
   * @param widget the widget whose background property to preserve
   * @param background the value to preserve
   * @see #renderBackground(Widget, Color)
   */
  public static void preserveBackground( Widget widget, Color background ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.preserveBackground( widget, background );
  }

  /**
   * Preserves the value of the property <code>background</code> of the
   * specified widget.
   *
   * @param widget the widget whose background property to preserve
   * @param background the background color to preserve
   * @param transparency the background transparency to preserve
   * @see #renderBackground(Widget, Color, boolean)
   */
  public static void preserveBackground( Widget widget, Color background, boolean transparency ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.preserveBackground( widget,
                                                                             background,
                                                                             transparency );
  }

  /**
   * Preserves the background gradient properties of the specified widget.
   *
   * @param widget the widget whose background gradient properties to preserve
   * @see #renderBackgroundGradient(Widget)
   */
  public static void preserveBackgroundGradient( Widget widget ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.preserveBackgroundGradient( widget );
  }

  /**
   * Preserves the rounded border properties of the specified widget.
   *
   * @param widget the widget whose rounded border properties to preserve
   * @see #renderRoundedBorder(Widget)
   */
  public static void preserveRoundedBorder( Widget widget ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.preserveRoundedBorder( widget );
  }

  /**
   * Preserves the value of the custom variant of the specified
   * widget.
   *
   * @param widget the widget whose custom variant to preserve
   * @see #renderCustomVariant(Widget)
   */
  public static void preserveCustomVariant( Widget widget ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.preserveCustomVariant( widget );
  }

  /**
   * Preserves custom data for the given widget.
   *
   * @since 2.1
   */
  public static void preserveData( Widget widget ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.preserveData( widget );
  }

  /**
   * Preserves whether the given <code>widget</code> has one or more
   * <code>HelpListener</code>s attached.
   *
   * @param widget the widget to preserve
   */
  public static void preserveHelpListener( Widget widget ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.preserveHelpListener( widget );
  }

  /**
   * Determines whether the bounds of the given widget have changed during the
   * processing of the current request and if so, writes a set operation the
   * response that updates the client-side bounds of the specified widget. For
   * instances of {@link Control}, use the method
   * {@link ControlLCAUtil#renderBounds(Control)} instead.
   *
   * @param widget the widget whose bounds to write
   * @param bounds the new bounds of the widget
   */
  public static void renderBounds( Widget widget, Rectangle bounds ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderBounds( widget, bounds );
  }

  /**
   * Determines whether the property <code>enabled</code> of the given widget
   * has changed during the processing of the current request and if so, writes
   * a protocol message to the response that updates the client-side enabled
   * property of the specified widget. For instances of {@link Control}, use
   * the method {@link ControlLCAUtil#renderEnabled(Control)} instead.
   *
   * @param widget the widget whose enabled property to set
   * @param enabled the new value of the property
   * @see #preserveEnabled(Widget, boolean)
   */
  public static void renderEnabled( Widget widget, boolean enabled ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderEnabled( widget, enabled );
  }

  /**
   * Determines whether the custom variant of the given widget
   * has changed during the processing of the current request and if so, writes
   * a protocol Message to the response that updates the client-side variant.
   *
   * @param widget the widget whose custom variant to write
   */
  public static void renderCustomVariant( Widget widget ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderCustomVariant( widget );
  }

  /**
   * Renders custom data changes for the given widget.
   *
   * @since 2.1
   */
  public static void renderData( Widget widget ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderData( widget );
  }

  /**
   * Adds or removes client-side help listeners for the the given
   * <code>widget</code> as necessary.
   *
   * @param widget
   */
  public static void renderListenHelp( Widget widget ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderListenHelp( widget );
  }

  /**
   * Determines whether the property <code>menu</code> of the given widget has
   * changed during the processing of the current request and if so, writes
   * a protocol message to the response that updates the client-side menu property
   * of the specified widget. For instances of {@link Control}, use the method
   * {@link ControlLCAUtil#renderMenu(Control)} instead.
   *
   * @param widget the widget whose menu property to set
   * @param menu the new value of the property
   */
  public static void renderMenu( Widget widget, Menu menu ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderMenu( widget, menu );
  }

  /**
   * Determines whether the property <code>toolTip</code> of the given widget
   * has changed during the processing of the current request and if so, writes
   * a protocol message to the response that updates the client-side toolTip
   * property of the specified widget. For instances of {@link Control}, use
   * the method {@link ControlLCAUtil#renderToolTip(Control)} instead.
   *
   * @param widget the widget whose toolTip property to set
   * @param toolTip the new value of the property
   * @see #preserveToolTipText(Widget, String)
   */
  public static void renderToolTip( Widget widget, String toolTip ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderToolTip( widget, toolTip );
  }

  /**
   * Determines whether the property <code>font</code> of the given widget has
   * changed during the processing of the current request and if so, writes
   * JavaScript code to the response that updates the client-side font property
   * of the specified widget. For instances of {@link Control}, use the method
   * {@link ControlLCAUtil#renderFont(Control)} instead.
   *
   * @param widget the widget whose font property to set
   * @param font the new value of the property
   * @see #preserveFont(Widget, Font)
   */
  public static void renderFont( Widget widget, Font font ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderFont( widget, font );
  }

  /**
   * Determines whether the property <code>foreground</code> of the given
   * widget has changed during the processing of the current request and if so,
   * writes a protocol message to the response that updates the client-side
   * foreground property of the specified widget. For instances of
   * {@link Control}, use the method
   * {@link ControlLCAUtil#renderForeground(Control)} instead.
   *
   * @param widget the widget whose foreground property to set
   * @param newColor the new value of the property
   * @see #preserveForeground(Widget, Color)
   */
  public static void renderForeground( Widget widget, Color newColor ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderForeground( widget, newColor );
  }

  /**
   * Determines whether the property <code>background</code> of the given
   * widget has changed during the processing of the current request and if so,
   * writes a protocol message to the response that updates the client-side
   * background property of the specified widget. For instances of
   * {@link Control}, use the method
   * {@link ControlLCAUtil#renderBackground(Control)} instead.
   *
   * @param widget the widget whose background property to set
   * @param newColor the new value of the property
   * @see #preserveBackground(Widget, Color)
   */
  public static void renderBackground( Widget widget, Color newColor ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderBackground( widget, newColor );
  }

  /**
   * Determines whether the property <code>background</code> of the given
   * widget has changed during the processing of the current request and if so,
   * writes a protocol message to the response that updates the client-side
   * background property of the specified widget. For instances of
   * {@link Control}, use the method
   * {@link ControlLCAUtil#renderBackground(Control)} instead.
   *
   * @param widget the widget whose background property to set
   * @param background the new background color
   * @param transparency the new background transparency, if <code>true</code>,
   *            the <code>background</code> parameter is ignored
   * @see #preserveBackground(Widget, Color, boolean)
   */
  public static void renderBackground( Widget widget, Color background, boolean transparency ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderBackground( widget,
                                                                           background,
                                                                           transparency );
  }

  /**
   * Determines whether the background gradient properties of the
   * given widget have changed during the processing of the current request and
   * if so, writes a protocol message to the response that updates the client-side
   * background gradient properties of the specified widget.
   *
   * @param widget the widget whose background gradient properties to set
   * @see #preserveBackgroundGradient(Widget)
   */
  public static void renderBackgroundGradient( Widget widget ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderBackgroundGradient( widget );
  }

  /**
   * Determines whether the rounded border properties of the given widget has
   * changed during the processing of the current request and if so, writes
   * a protocol message to the response that updates the client-side rounded border
   * of the specified widget.
   *
   * @param widget the widget whose rounded border properties to set
   * @see #preserveRoundedBorder(Widget)
   */
  public static void renderRoundedBorder( Widget widget ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderRoundedBorder( widget );
  }

  /**
   * Reads the value of the specified property for the specified widget from the message
   * sent by the client that is currently processed. If this property is not submitted for
   * the given widget, <code>null</code> is returned.
   *
   * @param widget the widget whose property to read
   * @param property the name of the property to read
   * @return the value read from the client message or <code>null</code> if no value
   *         was submitted for the given property
   */
  public static String readPropertyValue( Widget widget, String property ) {
    JsonValue value = ProtocolUtil.readPropertyValue( getId( widget ), property );
    return value == null ? null : jsonToJava( value ).toString();
  }

  /**
   * Reads the value of the specified event property for the specified widget from the message
   * sent by the client that is currently processed. If this event property is not submitted for
   * the given widget, <code>null</code> is returned.
   *
   * @param widget the widget whose property to read
   * @param eventName the name of the event whose property to read
   * @param property the name of the property to read
   * @return the value read from the client message or <code>null</code> if no value
   *         was submitted for the given property
   * @since 2.0
   */
  public static String readEventPropertyValue( Widget widget, String eventName, String property ) {
    ClientMessage message = getClientMessage();
    NotifyOperation operation = message.getLastNotifyOperationFor( getId( widget ), eventName );
    if( operation != null ) {
      JsonValue value = operation.getProperty( property );
      if( value != null ) {
        return jsonToJava( value ).toString();
      }
    }
    return null;
  }

  /**
   * Determines whether an event with the specified name was submitted for the
   * specified widget within the current message sent by the client.
   *
   * @param widget the widget that should receive the event
   * @param eventName the name of the event to check for
   * @return <code>true</code> if the event was sent for the widget, false
   *         otherwise.
   */
  public static boolean wasEventSent( Widget widget, String eventName ) {
    return org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.wasEventSent( widget, eventName );
  }

  /**
   * Preserves the value of the property of the specified widget.
   *
   * @param widget the widget whose property to preserve
   * @param property the name of the property
   * @param value the value to preserve
   */
  public static void preserveProperty( Widget widget, String property, Object value ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.preserveProperty( widget, property, value );
  }

  /**
   * Preserves the value of the property of the specified widget.
   *
   * @param widget the widget whose property to preserve
   * @param property the name of the property
   * @param value the value to preserve
   */
  public static void preserveProperty( Widget widget, String property, int value ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.preserveProperty( widget, property, value );
  }

  /**
   * Preserves the value of the property of the specified widget.
   *
   * @param widget the widget whose property to preserve
   * @param property the name of the property
   * @param value the value to preserve
   */
  public static void preserveProperty( Widget widget, String property, boolean value ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.preserveProperty( widget, property, value );
  }

  /**
   * Preserves the value of the listener of the specified widget.
   *
   * @param widget the widget whose listener to preserve
   * @param listener the type of the listener
   * @param value the value to preserve
   */
  public static void preserveListener( Widget widget, String listener, boolean value ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.preserveListener( widget, listener, value );
  }

  /**
   * Determines whether the property of the given widget has changed during the processing of the
   * current request and if so, writes a protocol message to the response that updates the
   * client-side property of the specified widget.
   *
   * @param widget the widget whose property to set
   * @param property the property name
   * @param newValue the new value of the property
   * @param defaultValue the default value of the property
   */
  public static void renderProperty( Widget widget,
                                     String property,
                                     Object newValue,
                                     Object defaultValue )
  {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderProperty( widget,
                                                                         property,
                                                                         newValue,
                                                                         defaultValue );
  }

  /**
   * Determines whether the property of the given widget has changed during the processing of the
   * current request and if so, writes a protocol message to the response that updates the
   * client-side property of the specified widget.
   *
   * @param widget the widget whose property to set
   * @param property the property name
   * @param newValue the new value of the property
   * @param defaultValue the default value of the property
   * @since 2.3
   */
  public static void renderProperty( Widget widget,
                                     String property,
                                     String newValue,
                                     String defaultValue )
  {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderProperty( widget,
                                                                         property,
                                                                         newValue,
                                                                         defaultValue );
  }

  /**
   * Determines whether the property of the given widget has changed during the processing of the
   * current request and if so, writes a protocol message to the response that updates the
   * client-side property of the specified widget.
   *
   * @param widget the widget whose property to set
   * @param property the property name
   * @param newValue the new value of the property
   * @param defaultValue the default value of the property
   * @since 2.3
   */
  public static void renderProperty( Widget widget,
                                     String property,
                                     Integer newValue,
                                     Integer defaultValue )
  {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderProperty( widget,
                                                                         property,
                                                                         newValue,
                                                                         defaultValue );
  }

  /**
   * Determines whether the property of the given widget has changed during the processing of the
   * current request and if so, writes a protocol message to the response that updates the
   * client-side property of the specified widget.
   *
   * @param widget the widget whose property to set
   * @param property the property name
   * @param newValue the new value of the property
   * @param defaultValue the default value of the property
   * @since 2.3
   */
  public static void renderProperty( Widget widget,
                                     String property,
                                     String[] newValue,
                                     String[] defaultValue )
  {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderProperty( widget,
                                                                         property,
                                                                         newValue,
                                                                         defaultValue );
  }

  /**
   * Determines whether the property of the given widget has changed during the processing of the
   * current request and if so, writes a protocol message to the response that updates the
   * client-side property of the specified widget.
   *
   * @param widget the widget whose property to set
   * @param property the property name
   * @param newValue the new value of the property
   * @param defaultValue the default value of the property
   * @since 2.3
   */
  public static void renderProperty( Widget widget,
                                     String property,
                                     boolean[] newValue,
                                     boolean[] defaultValue )
  {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderProperty( widget,
                                                                         property,
                                                                         newValue,
                                                                         defaultValue );
  }

  /**
   * Determines whether the property of the given widget has changed during the processing of the
   * current request and if so, writes a protocol message to the response that updates the
   * client-side property of the specified widget.
   *
   * @param widget the widget whose property to set
   * @param property the property name
   * @param newValue the new value of the property
   * @param defaultValue the default value of the property
   * @since 2.3
   */
  public static void renderProperty( Widget widget,
                                     String property,
                                     int[] newValue,
                                     int[] defaultValue )
  {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderProperty( widget,
                                                                         property,
                                                                         newValue,
                                                                         defaultValue );
  }

  /**
   * Determines whether the property of the given widget has changed during the processing of the
   * current request and if so, writes a protocol message to the response that updates the
   * client-side property of the specified widget.
   *
   * @param widget the widget whose property to set
   * @param property the property name
   * @param newValue the new value of the property
   * @param defaultValue the default value of the property
   */
  public static void renderProperty( Widget widget,
                                     String property,
                                     int newValue,
                                     int defaultValue )
  {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderProperty( widget,
                                                                         property,
                                                                         newValue,
                                                                         defaultValue );
  }

  /**
   * Determines whether the property of the given widget has changed during the processing of the
   * current request and if so, writes a protocol message to the response that updates the
   * client-side property of the specified widget.
   *
   * @param widget the widget whose property to set
   * @param property the property name
   * @param newValue the new value of the property
   * @param defaultValue the default value of the property
   */
  public static void renderProperty( Widget widget,
                                     String property,
                                     boolean newValue,
                                     boolean defaultValue )
  {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderProperty( widget,
                                                                         property,
                                                                         newValue,
                                                                         defaultValue );
  }

  /**
   * Determines whether the property of the given widget has changed during the processing of the
   * current request and if so, writes a protocol message to the response that updates the
   * client-side property of the specified widget.
   *
   * @param widget the widget whose property to set
   * @param property the property name
   * @param newValue the new value of the property
   * @param defaultValue the default value of the property
   */
  public static void renderProperty( Widget widget,
                                     String property,
                                     Image newValue,
                                     Image defaultValue )
  {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderProperty( widget,
                                                                         property,
                                                                         newValue,
                                                                         defaultValue );
  }

  /**
   * Determines whether the property of the given widget has changed during the processing of the
   * current request and if so, writes a protocol message to the response that updates the
   * client-side property of the specified widget.
   *
   * @param widget the widget whose property to set
   * @param property the property name
   * @param newValue the new value of the property
   * @param defaultValue the default value of the property
   */
  public static void renderProperty( Widget widget,
                                     String property,
                                     Image[] newValue,
                                     Image[] defaultValue )
  {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderProperty( widget,
                                                                         property,
                                                                         newValue,
                                                                         defaultValue );
  }

  /**
   * Determines whether the property of the given widget has changed during the processing of the
   * current request and if so, writes a protocol message to the response that updates the
   * client-side property of the specified widget.
   *
   * @param widget the widget whose property to set
   * @param property the property name
   * @param newValue the new value of the property
   * @param defaultValue the default value of the property
   */
  public static void renderProperty( Widget widget,
                                     String property,
                                     Color newValue,
                                     Color defaultValue )
  {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderProperty( widget,
                                                                         property,
                                                                         newValue,
                                                                         defaultValue );
  }

  /**
   * Determines whether the property of the given widget has changed during the processing of the
   * current request and if so, writes a protocol message to the response that updates the
   * client-side property of the specified widget.
   *
   * @param widget the widget whose property to set
   * @param property the property name
   * @param newValue the new value of the property
   * @param defaultValue the default value of the property
   */
  public static void renderProperty( Widget widget,
                                     String property,
                                     Color[] newValue,
                                     Color[] defaultValue )
  {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderProperty( widget,
                                                                         property,
                                                                         newValue,
                                                                         defaultValue );
  }

  /**
   * Determines whether the property of the given widget has changed during the processing of the
   * current request and if so, writes a protocol message to the response that updates the
   * client-side property of the specified widget.
   *
   * @param widget the widget whose property to set
   * @param property the property name
   * @param newValue the new value of the property
   * @param defaultValue the default value of the property
   */
  public static void renderProperty( Widget widget,
                                     String property,
                                     Font[] newValue,
                                     Font[] defaultValue )
  {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderProperty( widget,
                                                                         property,
                                                                         newValue,
                                                                         defaultValue );
  }

  /**
   * Determines whether the property of the given widget has changed during the processing of the
   * current request and if so, writes a protocol message to the response that updates the
   * client-side property of the specified widget.
   *
   * @param widget the widget whose property to set
   * @param property the property name
   * @param newValue the new value of the property
   * @param defaultValue the default value of the property
   */
  public static void renderProperty( Widget widget,
                                     String property,
                                     Point newValue,
                                     Point defaultValue )
  {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderProperty( widget,
                                                                         property,
                                                                         newValue,
                                                                         defaultValue );
  }

  /**
   * Determines whether the property of the given widget has changed during the processing of the
   * current request and if so, writes a protocol message to the response that updates the
   * client-side property of the specified widget.
   *
   * @param widget the widget whose property to set
   * @param property the property name
   * @param newValue the new value of the property
   * @param defaultValue the default value of the property
   */
  public static void renderProperty( Widget widget,
                                     String property,
                                     Rectangle newValue,
                                     Rectangle defaultValue )
  {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderProperty( widget,
                                                                         property,
                                                                         newValue,
                                                                         defaultValue );
  }

  /**
   * Determines whether the property of the given widget has changed during the processing of the
   * current request and if so, writes a protocol message to the response that updates the
   * client-side property of the specified widget.
   *
   * @param widget the widget whose property to set
   * @param property the property name
   * @param newValue the new value of the property
   * @param defaultValue the default value of the property
   */
  public static void renderProperty( Widget widget,
                                     String property,
                                     Widget newValue,
                                     Widget defaultValue )
  {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderProperty( widget,
                                                                         property,
                                                                         newValue,
                                                                         defaultValue );
  }

  /**
   * Determines whether the listener of the given widget has changed during the processing of the
   * current request and if so, writes a protocol message to the response that updates the
   * client-side listener of the specified widget.
   *
   * @param widget the widget whose property to set
   * @param listener the listener type
   * @param newValue the new value of the listener (true if listener is attached, false otherwise)
   * @param defaultValue the default value of the listener
   */
  public static void renderListener( Widget widget,
                                     String listener,
                                     boolean newValue,
                                     boolean defaultValue )
  {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderListener( widget,
                                                                         listener,
                                                                         newValue,
                                                                         defaultValue );
  }

  /**
   * Renders client listeners that have been added to or removed from the given widget in the
   * current request. Client listeners don't have to be preserved.
   *
   * @param widget the widget to render client listeners for
   * @see org.eclipse.rap.rwt.scripting.ClientListener
   * @since 2.2
   */
  public static void renderClientListeners( Widget widget ) {
    org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderClientListeners( widget );
  }

  /**
   * Determines whether the property of the given widget has changed during the
   * processing of the current request and thus the changes must be rendered in
   * the response. This is done by comparing the current value with the
   * preserved value.
   * <p>
   * If there is no preserved value, <code>null</code> is assumed.
   * </p>
   *
   * @param widget the widget whose property is to be compared, must not be
   *            <code>null</code>.
   * @param property the name of the property under which the preserved value
   *            can be looked up. Must not be <code>null</code>.
   * @param newValue the value to compare the preserved value with
   * @return <code>true</code> if the property has changed, <code>false</code>
   *         otherwise
   */
  public static boolean hasChanged( Widget widget, String property, Object newValue ) {
    return org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.hasChanged( widget,
                                                                            property,
                                                                            newValue );
  }

  /**
   * Determines whether the property of the given widget has changed during the
   * processing of the current request and thus the changes must be rendered in
   * the response. This is done by comparing the current value with the
   * preserved value.
   * <p>
   * In case it is the first time that the widget is rendered (it is not yet
   * present on the client side) <code>true</code> is only returned if the
   * <code>newValue</code> differs from the <code>defaultValue</code>.
   * Otherwise the decision is delegated to
   * {@link #hasChanged(Widget,String,Object)}.
   * </p>
   *
   * @param widget the widget whose property is to be compared, must not be
   *            <code>null</code>.
   * @param property the name of the property under which the preserved value
   *            can be looked up. Must not be <code>null</code>.
   * @param newValue the value that is compared to the preserved value
   * @param defaultValue the default value
   * @return <code>true</code> if the property has changed or if the widget is
   *         not yet initialized and the property is at its default value,
   *         <code>false</code> otherwise
   */
  public static boolean hasChanged( Widget widget,
                                    String property,
                                    Object newValue,
                                    Object defaultValue )
  {
    return org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.hasChanged( widget,
                                                                            property,
                                                                            newValue,
                                                                            defaultValue );
  }

  /**
   * Replaces all newline characters in the specified input string with the
   * given replacement string.
   *
   * @param input the string to process
   * @param replacement the string to replace line feeds with
   * @return a new string with all line feeds replaced
   */
  public static String replaceNewLines( String input, String replacement ) {
    return org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.replaceNewLines( input, replacement );
  }

  /**
   * Obtains a list of SWT style flags that are present in the given widget.
   *
   * @param widget the widget to get the styles for
   * @param styles the names of the SWT style flags to check for, elements must
   *          be valid SWT style flags
   * @return the names of those styles from the <code>styles</code> parameter
   *         that are present in the given widget, i.e. where
   *         <code>( widget.getStyle() &amp; SWT.&lt;STYLE&gt; ) != 0</code>
   * @see SWT
   * @see Widget#getStyle()
   */
  public static String[] getStyles( Widget widget, String[] styles ) {
    return org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.getStyles( widget, styles );
  }

}
