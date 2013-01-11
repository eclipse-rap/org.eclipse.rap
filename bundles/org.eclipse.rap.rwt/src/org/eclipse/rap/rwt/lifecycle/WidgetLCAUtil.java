/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.client.WidgetDataWhiteList;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.rap.rwt.internal.protocol.StylesUtil;
import org.eclipse.rap.rwt.internal.util.EncodingUtil;
import org.eclipse.rap.rwt.internal.util.NumberFormatUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.IWidgetGraphicsAdapter;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Widget;


/**
 * Utility class that provides a number of useful static methods to support the
 * implementation of life cycle adapters (LCAs) for {@link Widget}s.
 *
 * @see ControlLCAUtil
 * @since 2.0
 */
public final class WidgetLCAUtil {

  private static final String PARAM_X = "bounds.x";
  private static final String PARAM_Y = "bounds.y";
  private static final String PARAM_WIDTH = "bounds.width";
  private static final String PARAM_HEIGHT = "bounds.height";

  private static final String PROP_TOOL_TIP = "toolTip";
  private static final String PROP_FONT = "font";
  private static final String PROP_FOREGROUND = "foreground";
  private static final String PROP_BACKGROUND = "background";
  private static final String PROP_BACKGROUND_TRANSPARENCY = "backgroundTrans";
  private static final String PROP_BACKGROUND_GRADIENT_COLORS = "backgroundGradientColors";
  private static final String PROP_BACKGROUND_GRADIENT_PERCENTS = "backgroundGradientPercents";
  private static final String PROP_BACKGROUND_GRADIENT_VERTICAL = "backgroundGradientVertical";
  private static final String PROP_ROUNDED_BORDER_WIDTH = "roundedBorderWidth";
  private static final String PROP_ROUNDED_BORDER_COLOR = "roundedBorderColor";
  private static final String PROP_ROUNDED_BORDER_RADIUS = "roundedBorderRadius";
  private static final String PROP_ENABLED = "enabled";
  private static final String PROP_VARIANT = "variant";
  private static final String PROP_DATA = "data";
  private static final String PROP_HELP_LISTENER = "Help";

  static final String LISTENER_PREFIX = "listener_";

  private static final Rectangle DEF_ROUNDED_BORDER_RADIUS = new Rectangle( 0, 0, 0, 0 );

  private WidgetLCAUtil() {
    // prevent instantiation
  }

  ///////////////////////////////////////////////////////
  // Methods to read and process request parameter values

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
    return readBounds( WidgetUtil.getId( widget ), defaultValue );
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
    int x = readBoundsX( widgetId, defaultValue.x );
    int y = readBoundsY( widgetId, defaultValue.y );
    int width = readBoundsWidth( widgetId, defaultValue.width );
    int height = readBoundsHeight( widgetId, defaultValue.height );
    return new Rectangle( x, y, width, height );
  }

  private static int readBoundsY( String widgetId, int defaultValue ) {
    String value = ProtocolUtil.readPropertyValueAsString( widgetId, PARAM_Y );
    return readBoundsValue( value, defaultValue );
  }

  private static int readBoundsX( String widgetId, int defaultValue ) {
    String value = ProtocolUtil.readPropertyValueAsString( widgetId, PARAM_X );
    return readBoundsValue( value, defaultValue );
  }

  private static int readBoundsWidth( String widgetId, int defaultValue ) {
    String value = ProtocolUtil.readPropertyValueAsString( widgetId, PARAM_WIDTH );
    return readBoundsValue( value, defaultValue );
  }

  private static int readBoundsHeight( String widgetId, int defaultValue ) {
    String value = ProtocolUtil.readPropertyValueAsString( widgetId, PARAM_HEIGHT );
    return readBoundsValue( value, defaultValue );
  }

  private static int readBoundsValue( String value, int current ) {
    int result;
    if( value != null && !"null".equals( value ) ) {
      result = NumberFormatUtil.parseInt( value );
    } else {
      result = current;
    }
    return result;
  }

  /**
   * Process a <code>HelpEvent</code> if the current request specifies that
   * there occurred a help event for the given <code>widget</code>.
   *
   * @param widget the widget to process
   */
  public static void processHelp( Widget widget ) {
    if( WidgetLCAUtil.wasEventSent( widget, ClientMessageConst.EVENT_HELP ) ) {
      widget.notifyListeners( SWT.Help, new Event() );
    }
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
    WidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( Props.BOUNDS, bounds );
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
    WidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_ENABLED, Boolean.valueOf( enabled ) );
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
    String text = toolTip == null ? "" : toolTip;
    WidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_TOOL_TIP, text );
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
    WidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_FONT, font );
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
    WidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_FOREGROUND, foreground );
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
    preserveBackground( widget, background, false );
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
    WidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_BACKGROUND, background );
    adapter.preserve( PROP_BACKGROUND_TRANSPARENCY, Boolean.valueOf( transparency ) );
  }

  /**
   * Preserves the background gradient properties of the specified widget.
   *
   * @param widget the widget whose background gradient properties to preserve
   * @see #renderBackgroundGradient(Widget)
   */
  public static void preserveBackgroundGradient( Widget widget ) {
    Object adapter = widget.getAdapter( IWidgetGraphicsAdapter.class );
    if( adapter != null ) {
      IWidgetGraphicsAdapter gfxAdapter = ( IWidgetGraphicsAdapter )adapter;
      Color[] bgGradientColors = gfxAdapter.getBackgroundGradientColors();
      int[] bgGradientPercents = gfxAdapter.getBackgroundGradientPercents();
      boolean bgGradientVertical = gfxAdapter.isBackgroundGradientVertical();
      WidgetAdapter widgetAdapter = WidgetUtil.getAdapter( widget );
      widgetAdapter.preserve( PROP_BACKGROUND_GRADIENT_COLORS, bgGradientColors );
      widgetAdapter.preserve( PROP_BACKGROUND_GRADIENT_PERCENTS, bgGradientPercents );
      widgetAdapter.preserve( PROP_BACKGROUND_GRADIENT_VERTICAL,
                              Boolean.valueOf( bgGradientVertical ) );
    }
  }

  /**
   * Preserves the rounded border properties of the specified widget.
   *
   * @param widget the widget whose rounded border properties to preserve
   * @see #renderRoundedBorder(Widget)
   */
  public static void preserveRoundedBorder( Widget widget ) {
    Object adapter = widget.getAdapter( IWidgetGraphicsAdapter.class );
    if( adapter != null ) {
      IWidgetGraphicsAdapter gfxAdapter = ( IWidgetGraphicsAdapter )adapter;
      int width = gfxAdapter.getRoundedBorderWidth();
      Color color = gfxAdapter.getRoundedBorderColor();
      Rectangle radius = gfxAdapter.getRoundedBorderRadius();
      WidgetAdapter widgetAdapter = WidgetUtil.getAdapter( widget );
      widgetAdapter.preserve( PROP_ROUNDED_BORDER_WIDTH, Integer.valueOf( width ) );
      widgetAdapter.preserve( PROP_ROUNDED_BORDER_COLOR, color );
      widgetAdapter.preserve( PROP_ROUNDED_BORDER_RADIUS, radius );
    }
  }

  /**
   * Preserves the value of the custom variant of the specified
   * widget.
   *
   * @param widget the widget whose custom variant to preserve
   * @see #renderCustomVariant(Widget)
   */
  public static void preserveCustomVariant( Widget widget ) {
    String variant = WidgetUtil.getVariant( widget );
    WidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_VARIANT, variant );
  }

  static void preserveData( Widget widget ) {
    preserveProperty( widget, PROP_DATA, getDataAsArray( widget ) );
  }

  /**
   * Preserves whether the given <code>widget</code> has one or more
   * <code>HelpListener</code>s attached.
   *
   * @param widget the widget to preserve
   */
  public static void preserveHelpListener( Widget widget ) {
    preserveListener( widget, PROP_HELP_LISTENER, widget.isListening( SWT.Help ) );
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
    renderProperty( widget, Props.BOUNDS, bounds, null );
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
    renderProperty( widget, Props.ENABLED, enabled, true );
  }

  /**
   * Determines whether the custom variant of the given widget
   * has changed during the processing of the current request and if so, writes
   * a protocol Message to the response that updates the client-side variant.
   *
   * @param widget the widget whose custom variant to write
   */
  public static void renderCustomVariant( Widget widget ) {
    String newValue = WidgetUtil.getVariant( widget );
    if( WidgetLCAUtil.hasChanged( widget, PROP_VARIANT, newValue, null ) ) {
      String value = null;
      if( newValue != null ) {
        value = "variant_" + newValue;
      }
      IClientObject clientObject = ClientObjectFactory.getClientObject( widget );
      clientObject.set( "customVariant", value );
    }
  }

  static void renderData( Widget widget ) {
    Object[] newValue = getDataAsArray( widget );
    if( WidgetLCAUtil.hasChanged( widget, PROP_DATA, newValue, new Object[ 0 ] ) ) {
      IClientObject clientObject = ClientObjectFactory.getClientObject( widget );
      Map<Object, Object> data = new HashMap<Object, Object>();
      for( int i = 0; i < newValue.length; i++ ) {
        data.put( newValue[ i ], newValue[ ++i ] );
      }
      clientObject.set( PROP_DATA, data );
    }
  }

  private static Object[] getDataAsArray( Widget widget ) {
    List<Object> result = new ArrayList<Object>();
    WidgetDataWhiteList service = RWT.getClient().getService( WidgetDataWhiteList.class );
    String[] dataKeys = service == null ? null : service.getKeys();
    if( dataKeys != null ) {
      for( String key : dataKeys ) {
        if( key != null ) {
          Object value = widget.getData( key );
          if( value != null ) {
            result.add( key );
            result.add( value );
          }
        }
      }
    }
    return result.toArray();
  }

  /**
   * Adds or removes client-side help listeners for the the given
   * <code>widget</code> as necessary.
   *
   * @param widget
   */
  public static void renderListenHelp( Widget widget ) {
    renderListener( widget, PROP_HELP_LISTENER, widget.isListening( SWT.Help ), false );
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
    renderProperty( widget, Props.MENU, menu, null );
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
    String text = toolTip == null ? "" : toolTip;
    WidgetLCAUtil.renderProperty( widget, PROP_TOOL_TIP, text, "" );
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
    if( WidgetLCAUtil.hasChanged( widget, PROP_FONT, font, null ) ) {
      IClientObject clientObject = ClientObjectFactory.getClientObject( widget );
      clientObject.set( PROP_FONT, ProtocolUtil.getFontAsArray( font ) );
    }
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
    if( WidgetLCAUtil.hasChanged( widget, PROP_FOREGROUND, newColor, null ) ) {
      IClientObject clientObject = ClientObjectFactory.getClientObject( widget );
      clientObject.set( PROP_FOREGROUND, ProtocolUtil.getColorAsArray( newColor, false ) );
    }
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
    renderBackground( widget, newColor, false );
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
    boolean transparencyChanged = WidgetLCAUtil.hasChanged( widget,
                                                            PROP_BACKGROUND_TRANSPARENCY,
                                                            Boolean.valueOf( transparency ),
                                                            Boolean.FALSE );
    boolean colorChanged = WidgetLCAUtil.hasChanged( widget, PROP_BACKGROUND, background, null );
    if( transparencyChanged || colorChanged ) {
      IClientObject clientObject = ClientObjectFactory.getClientObject( widget );
      int[] color = null;
      if( transparency || background != null ) {
        color = ProtocolUtil.getColorAsArray( background, transparency );
      }
      clientObject.set( PROP_BACKGROUND, color );
    }
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
    if( hasBackgroundGradientChanged( widget ) ) {
      Object adapter = widget.getAdapter( IWidgetGraphicsAdapter.class );
      IWidgetGraphicsAdapter graphicsAdapter = ( IWidgetGraphicsAdapter )adapter;
      Color[] bgGradientColors = graphicsAdapter.getBackgroundGradientColors();
      Object[] args = null;
      if( bgGradientColors!= null ) {
        Object[] colors = new Object[ bgGradientColors.length ];
        int[] bgGradientPercents = graphicsAdapter.getBackgroundGradientPercents();
        Integer[] percents = new Integer[ bgGradientPercents.length ];
        for( int i = 0; i < colors.length; i++ ) {
          colors[ i ] = ProtocolUtil.getColorAsArray( bgGradientColors[ i ], false );
        }
        for( int i = 0; i < bgGradientPercents.length; i++ ) {
          percents[ i ] =  new Integer( bgGradientPercents[ i ] );
        }
        boolean bgGradientVertical = graphicsAdapter.isBackgroundGradientVertical();
        args = new Object[] {
          colors,
          percents,
          new Boolean( bgGradientVertical )
        };
      }
      IClientObject clientObject = ClientObjectFactory.getClientObject( widget );
      clientObject.set( "backgroundGradient", args );
    }
  }

  private static boolean hasBackgroundGradientChanged( Widget widget ) {
    IWidgetGraphicsAdapter graphicsAdapter = widget.getAdapter( IWidgetGraphicsAdapter.class );
    Color[] bgGradientColors = graphicsAdapter.getBackgroundGradientColors();
    int[] bgGradientPercents = graphicsAdapter.getBackgroundGradientPercents();
    boolean bgGradientVertical = graphicsAdapter.isBackgroundGradientVertical();
    return    WidgetLCAUtil.hasChanged( widget,
                                        PROP_BACKGROUND_GRADIENT_COLORS,
                                        bgGradientColors,
                                        null )
           || WidgetLCAUtil.hasChanged( widget,
                                        PROP_BACKGROUND_GRADIENT_PERCENTS,
                                        bgGradientPercents,
                                        null )
           || WidgetLCAUtil.hasChanged( widget,
                                        PROP_BACKGROUND_GRADIENT_VERTICAL,
                                        Boolean.valueOf( bgGradientVertical ),
                                        Boolean.FALSE );
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
    if( hasRoundedBorderChanged( widget ) ) {
      Object adapter = widget.getAdapter( IWidgetGraphicsAdapter.class );
      IWidgetGraphicsAdapter graphicAdapter = ( IWidgetGraphicsAdapter )adapter;
      Object[] args = null;
      int width = graphicAdapter.getRoundedBorderWidth();
      Color color = graphicAdapter.getRoundedBorderColor();
      if( width > 0 && color != null ) {
        Rectangle radius = graphicAdapter.getRoundedBorderRadius();
        args = new Object[] {
          new Integer( width ),
          ProtocolUtil.getColorAsArray( color, false ),
          new Integer( radius.x ),
          new Integer( radius.y ),
          new Integer( radius.width ),
          new Integer( radius.height )
        };
      }
      IClientObject clientObject = ClientObjectFactory.getClientObject( widget );
      clientObject.set( "roundedBorder", args );
    }
  }

  private static boolean hasRoundedBorderChanged( Widget widget ) {
    Object adapter = widget.getAdapter( IWidgetGraphicsAdapter.class );
    IWidgetGraphicsAdapter graphicsAdapter = ( IWidgetGraphicsAdapter )adapter;
    int width = graphicsAdapter.getRoundedBorderWidth();
    Color color = graphicsAdapter.getRoundedBorderColor();
    Rectangle radius = graphicsAdapter.getRoundedBorderRadius();
    return
         WidgetLCAUtil.hasChanged( widget,
                                   PROP_ROUNDED_BORDER_WIDTH,
                                   new Integer( width ),
                                   new Integer( 0 ) )
      || WidgetLCAUtil.hasChanged( widget,
                                   PROP_ROUNDED_BORDER_COLOR,
                                   color,
                                   null )
      || WidgetLCAUtil.hasChanged( widget,
                                   PROP_ROUNDED_BORDER_RADIUS,
                                   radius,
                                   DEF_ROUNDED_BORDER_RADIUS );
  }

  //////////////////////////////////////////
  // Generic methods to read property values

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
    String widgetId = WidgetUtil.getId( widget );
    return ProtocolUtil.readPropertyValueAsString( widgetId, property );
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
    String widgetId = WidgetUtil.getId( widget );
    return ProtocolUtil.readEventPropertyValueAsString( widgetId, eventName, property );
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
    String widgetId = WidgetUtil.getId( widget );
    return ProtocolUtil.wasEventSent( widgetId, eventName );
  }



  //////////////////////////////////////////////
  // Generic methods to preserve property values

  /**
   * Preserves the value of the property of the specified widget.
   *
   * @param widget the widget whose property to preserve
   * @param property the name of the property
   * @param value the value to preserve
   */
  public static void preserveProperty( Widget widget, String property, Object value ) {
    WidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( property, value );
  }

  /**
   * Preserves the value of the property of the specified widget.
   *
   * @param widget the widget whose property to preserve
   * @param property the name of the property
   * @param value the value to preserve
   */
  public static void preserveProperty( Widget widget, String property, int value ) {
    preserveProperty( widget, property, Integer.valueOf( value ) );
  }

  /**
   * Preserves the value of the property of the specified widget.
   *
   * @param widget the widget whose property to preserve
   * @param property the name of the property
   * @param value the value to preserve
   */
  public static void preserveProperty( Widget widget, String property, boolean value ) {
    preserveProperty( widget, property, Boolean.valueOf( value ) );
  }

  /**
   * Preserves the value of the listener of the specified widget.
   *
   * @param widget the widget whose listener to preserve
   * @param listener the type of the listener
   * @param value the value to preserve
   */
  public static void preserveListener( Widget widget, String listener, boolean value ) {
    WidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( LISTENER_PREFIX + listener, new Boolean( value ) );
  }

  ////////////////////////////////////////////
  // Generic methods to render property values

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
    if( WidgetLCAUtil.hasChanged( widget, property, newValue, defaultValue ) ) {
      IClientObject clientObject = ClientObjectFactory.getClientObject( widget );
      clientObject.set( property, newValue );
    }
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
    Integer newValueObject = Integer.valueOf( newValue );
    Integer defaultValueObject = Integer.valueOf( defaultValue );
    renderProperty( widget, property, newValueObject, defaultValueObject );
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
    Boolean newValueObject = Boolean.valueOf( newValue );
    Boolean defaultValueObject = Boolean.valueOf( defaultValue );
    renderProperty( widget, property, newValueObject, defaultValueObject );
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
    if( WidgetLCAUtil.hasChanged( widget, property, newValue, defaultValue ) ) {
      IClientObject clientObject = ClientObjectFactory.getClientObject( widget );
      clientObject.set( property, ProtocolUtil.getImageAsArray( newValue ) );
    }
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
    if( WidgetLCAUtil.hasChanged( widget, property, newValue, defaultValue ) ) {
      Object[] images = new Object[ newValue.length ];
      for( int i = 0; i < images.length; i++ ) {
        images[ i ] = ProtocolUtil.getImageAsArray( newValue[ i ] );
      }
      IClientObject clientObject = ClientObjectFactory.getClientObject( widget );
      clientObject.set( property, images );
    }
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
    if( WidgetLCAUtil.hasChanged( widget, property, newValue, defaultValue ) ) {
      IClientObject clientObject = ClientObjectFactory.getClientObject( widget );
      clientObject.set( property, ProtocolUtil.getColorAsArray( newValue, false ) );
    }
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
    if( WidgetLCAUtil.hasChanged( widget, property, newValue, defaultValue ) ) {
      Object[] colors = new Object[ newValue.length ];
      for( int i = 0; i < colors.length; i++ ) {
        int[] colorProperties = null;
        if( newValue[ i ] != null ) {
          colorProperties = ProtocolUtil.getColorAsArray( newValue[ i ], false );
        }
        colors[ i ] = colorProperties;
      }
      IClientObject clientObject = ClientObjectFactory.getClientObject( widget );
      clientObject.set( property, colors );
    }
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
    if( WidgetLCAUtil.hasChanged( widget, property, newValue, defaultValue ) ) {
      Object[] fonts = new Object[ newValue.length ];
      for( int i = 0; i < fonts.length; i++ ) {
        fonts[ i ] = ProtocolUtil.getFontAsArray( newValue[ i ] );
      }
      IClientObject clientObject = ClientObjectFactory.getClientObject( widget );
      clientObject.set( property, fonts );
    }
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
    if( WidgetLCAUtil.hasChanged( widget, property, newValue, defaultValue ) ) {
      int[] args = null;
      if( newValue != null ) {
        args = new int[] { newValue.x, newValue.y };
      }
      IClientObject clientObject = ClientObjectFactory.getClientObject( widget );
      clientObject.set( property, args );
    }
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
    if( WidgetLCAUtil.hasChanged( widget, property, newValue, defaultValue ) ) {
      int[] args = null;
      if( newValue != null ) {
        args = new int[] { newValue.x, newValue.y, newValue.width, newValue.height };
      }
      IClientObject clientObject = ClientObjectFactory.getClientObject( widget );
      clientObject.set( property, args );
    }
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
    if( WidgetLCAUtil.hasChanged( widget, property, newValue, defaultValue ) ) {
      String widgetId = newValue == null ? null : WidgetUtil.getId( newValue );
      IClientObject clientObject = ClientObjectFactory.getClientObject( widget );
      clientObject.set( property, widgetId );
    }
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
    String property = LISTENER_PREFIX + listener;
    Boolean newValueObject = Boolean.valueOf( newValue );
    Boolean defaultValueObject = Boolean.valueOf( defaultValue );
    if( WidgetLCAUtil.hasChanged( widget, property, newValueObject, defaultValueObject ) ) {
      IClientObject clientObject = ClientObjectFactory.getClientObject( widget );
      clientObject.listen( listener, newValue );
    }
  }

  ////////////////////
  // Auxiliary methods

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
    WidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    Object oldValue = adapter.getPreserved( property );
    return !WidgetLCAUtil.equals( oldValue, newValue );
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
    boolean result;
    WidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    if( adapter.isInitialized() ) {
      result = hasChanged( widget, property, newValue );
    } else {
      result = !equals( newValue, defaultValue );
    }
    return result;
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
    return EncodingUtil.replaceNewLines( input, replacement );
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
    return StylesUtil.filterStyles( widget, styles );
  }

  static boolean equals( Object object1, Object object2 ) {
    boolean result;
    if( object1 == object2 ) {
      result = true;
    } else if( object1 == null ) {
      result = false;
    } else if( object1 instanceof boolean[] && object2 instanceof boolean[] ) {
      result = Arrays.equals( ( boolean[] )object1, ( boolean[] )object2 );
    } else if( object1 instanceof int[] && object2 instanceof int[] ) {
      result = Arrays.equals( ( int[] )object1, ( int[] )object2 );
    } else if( object1 instanceof long[] && object2 instanceof long[] ) {
      result = Arrays.equals( ( long[] )object1, ( long[] )object2 );
    } else if( object1 instanceof float[] && object2 instanceof float[] ) {
      result = Arrays.equals( ( float[] )object1, ( float[] )object2 );
    } else if( object1 instanceof double[] && object2 instanceof double[] ) {
      result = Arrays.equals( ( double[] )object1, ( double[] )object2 );
    } else if( object1 instanceof Object[] && object2 instanceof Object[] ) {
      result = Arrays.equals( ( Object[] )object1, ( Object[] )object2 );
    } else {
      result = object1.equals( object2 );
    }
    return result;
  }

}
