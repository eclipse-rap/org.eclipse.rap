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
import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonValue;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.getJsonForColor;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.getJsonForFont;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.getJsonForImage;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.getJsonForPoint;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolUtil.getJsonForRectangle;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.internal.scripting.ClientListenerUtil.getClientListenerOperations;
import static org.eclipse.rap.rwt.internal.scripting.ClientListenerUtil.getRemoteId;
import static org.eclipse.rap.rwt.internal.util.MnemonicUtil.removeAmpersandControlCharacters;
import static org.eclipse.swt.internal.events.EventLCAUtil.isListening;
import static org.eclipse.swt.internal.widgets.MarkupUtil.isToolTipMarkupEnabledFor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.rap.rwt.internal.protocol.StylesUtil;
import org.eclipse.rap.rwt.internal.scripting.ClientListenerOperation;
import org.eclipse.rap.rwt.internal.scripting.ClientListenerOperation.AddListener;
import org.eclipse.rap.rwt.internal.scripting.ClientListenerOperation.RemoveListener;
import org.eclipse.rap.rwt.internal.scripting.ClientListenerUtil;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Widget;


public final class WidgetLCAUtil {

  private static final String PARAM_X = "bounds.x";
  private static final String PARAM_Y = "bounds.y";
  private static final String PARAM_WIDTH = "bounds.width";
  private static final String PARAM_HEIGHT = "bounds.height";

  private static final String PROP_TOOLTIP = "toolTip";
  private static final String PROP_TOOLTIP_MARKUP_ENABLED = "toolTipMarkupEnabled";
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

  private static final String LISTENER_PREFIX = "listener_";

  private static final Rectangle DEF_ROUNDED_BORDER_RADIUS = new Rectangle( 0, 0, 0, 0 );

  private WidgetLCAUtil() {
    // prevent instantiation
  }

  public static Rectangle readBounds( Widget widget, Rectangle defaultValue ) {
    return readBounds( WidgetUtil.getId( widget ), defaultValue );
  }

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

  public static void processHelp( Widget widget ) {
    if( wasEventSent( widget, ClientMessageConst.EVENT_HELP ) ) {
      widget.notifyListeners( SWT.Help, new Event() );
    }
  }

  public static void preserveBounds( Widget widget, Rectangle bounds ) {
    WidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( Props.BOUNDS, bounds );
  }

  public static void preserveEnabled( Widget widget, boolean enabled ) {
    WidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_ENABLED, Boolean.valueOf( enabled ) );
  }

  public static void preserveToolTipText( Widget widget, String toolTip ) {
    String text = toolTip == null ? "" : toolTip;
    WidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_TOOLTIP, text );
  }

  public static void preserveFont( Widget widget, Font font ) {
    WidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_FONT, font );
  }

  public static void preserveForeground( Widget widget, Color foreground ) {
    WidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_FOREGROUND, foreground );
  }

  public static void preserveBackground( Widget widget, Color background ) {
    preserveBackground( widget, background, false );
  }

  public static void preserveBackground( Widget widget, Color background, boolean transparency ) {
    WidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_BACKGROUND, background );
    adapter.preserve( PROP_BACKGROUND_TRANSPARENCY, Boolean.valueOf( transparency ) );
  }

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

  public static void preserveCustomVariant( Widget widget ) {
    String variant = WidgetUtil.getVariant( widget );
    WidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_VARIANT, variant );
  }

  public static void preserveData( Widget widget ) {
    preserveProperty( widget, PROP_DATA, getDataAsArray( widget ) );
  }

  public static void preserveHelpListener( Widget widget ) {
    preserveListener( widget, PROP_HELP_LISTENER, isListening( widget, SWT.Help ) );
  }

  public static void renderBounds( Widget widget, Rectangle bounds ) {
    renderProperty( widget, Props.BOUNDS, bounds, null );
  }

  public static void renderEnabled( Widget widget, boolean enabled ) {
    renderProperty( widget, Props.ENABLED, enabled, true );
  }

  public static void renderCustomVariant( Widget widget ) {
    String newValue = WidgetUtil.getVariant( widget );
    if( hasChanged( widget, PROP_VARIANT, newValue, null ) ) {
      String value = null;
      if( newValue != null ) {
        value = "variant_" + newValue;
      }
      getRemoteObject( widget ).set( "customVariant", value );
    }
  }

  @SuppressWarnings( "deprecation" )
  public static void renderData( Widget widget ) {
    Object[] newValue = getDataAsArray( widget );
    if( hasChanged( widget, PROP_DATA, newValue, new Object[ 0 ] ) ) {
      JsonObject data = new JsonObject();
      for( int i = 0; i < newValue.length; i++ ) {
        data.add( (String)newValue[ i ], createJsonValue( newValue[ ++i ] ) );
      }
      getRemoteObject( widget ).set( PROP_DATA, data );
    }
  }

  private static Object[] getDataAsArray( Widget widget ) {
    List<Object> result = new ArrayList<Object>();
    for( String key : WidgetDataUtil.getDataKeys() ) {
      Object value = widget.getData( key );
      if( value != null ) {
        result.add( key );
        result.add( value );
      }
    }
    return result.toArray();
  }

  public static void renderListenHelp( Widget widget ) {
    renderListener( widget, PROP_HELP_LISTENER, isListening( widget, SWT.Help ), false );
  }

  public static void renderMenu( Widget widget, Menu menu ) {
    renderProperty( widget, Props.MENU, menu, null );
  }

  public static void renderToolTip( Widget widget, String toolTip ) {
    renderToolTipMarkupEnabled( widget );
    String text = toolTip == null ? "" : toolTip;
    if( hasChanged( widget, PROP_TOOLTIP, text, "" ) ) {
      if( !isToolTipMarkupEnabledFor( widget ) ) {
        text = removeAmpersandControlCharacters( text );
      }
      getRemoteObject( widget ).set( PROP_TOOLTIP, text );
    }
  }

  private static void renderToolTipMarkupEnabled( Widget widget ) {
    WidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    if( !adapter.isInitialized() && isToolTipMarkupEnabledFor( widget ) ) {
      getRemoteObject( widget ).set( PROP_TOOLTIP_MARKUP_ENABLED, true );
    }
  }

  public static void renderFont( Widget widget, Font font ) {
    if( hasChanged( widget, PROP_FONT, font, null ) ) {
      getRemoteObject( widget ).set( PROP_FONT, getJsonForFont( font ) );
    }
  }

  public static void renderForeground( Widget widget, Color newColor ) {
    if( hasChanged( widget, PROP_FOREGROUND, newColor, null ) ) {
      getRemoteObject( widget ).set( PROP_FOREGROUND, getJsonForColor( newColor, false ) );
    }
  }

  public static void renderBackground( Widget widget, Color newColor ) {
    renderBackground( widget, newColor, false );
  }

  public static void renderBackground( Widget widget, Color background, boolean transparency ) {
    boolean transparencyChanged = hasChanged( widget,
                                              PROP_BACKGROUND_TRANSPARENCY,
                                              Boolean.valueOf( transparency ),
                                              Boolean.FALSE );
    boolean colorChanged = hasChanged( widget, PROP_BACKGROUND, background, null );
    if( transparencyChanged || colorChanged ) {
      JsonValue color = JsonValue.NULL;
      if( transparency || background != null ) {
        color = getJsonForColor( background, transparency );
      }
      getRemoteObject( widget ).set( PROP_BACKGROUND, color );
    }
  }

  public static void renderBackgroundGradient( Widget widget ) {
    if( hasBackgroundGradientChanged( widget ) ) {
      Object adapter = widget.getAdapter( IWidgetGraphicsAdapter.class );
      IWidgetGraphicsAdapter graphicsAdapter = ( IWidgetGraphicsAdapter )adapter;
      Color[] bgGradientColors = graphicsAdapter.getBackgroundGradientColors();
      JsonValue args = JsonValue.NULL;
      if( bgGradientColors!= null ) {
        JsonArray colors = new JsonArray();
        for( int i = 0; i < bgGradientColors.length; i++ ) {
          colors.add( getJsonForColor( bgGradientColors[ i ], false ) );
        }
        int[] bgGradientPercents = graphicsAdapter.getBackgroundGradientPercents();
        JsonValue percents = createJsonArray( bgGradientPercents );
        boolean bgGradientVertical = graphicsAdapter.isBackgroundGradientVertical();
        args = new JsonArray()
          .add( colors )
          .add( percents )
          .add( bgGradientVertical );
      }
      getRemoteObject( widget ).set( "backgroundGradient", args );
    }
  }

  private static boolean hasBackgroundGradientChanged( Widget widget ) {
    IWidgetGraphicsAdapter graphicsAdapter = widget.getAdapter( IWidgetGraphicsAdapter.class );
    Color[] bgGradientColors = graphicsAdapter.getBackgroundGradientColors();
    int[] bgGradientPercents = graphicsAdapter.getBackgroundGradientPercents();
    boolean bgGradientVertical = graphicsAdapter.isBackgroundGradientVertical();
    return    hasChanged( widget,
                          PROP_BACKGROUND_GRADIENT_COLORS,
                          bgGradientColors,
                          null )
           || hasChanged( widget,
                          PROP_BACKGROUND_GRADIENT_PERCENTS,
                          bgGradientPercents,
                          null )
           || hasChanged( widget,
                          PROP_BACKGROUND_GRADIENT_VERTICAL,
                          Boolean.valueOf( bgGradientVertical ),
                          Boolean.FALSE );
  }

  public static void renderRoundedBorder( Widget widget ) {
    if( hasRoundedBorderChanged( widget ) ) {
      Object adapter = widget.getAdapter( IWidgetGraphicsAdapter.class );
      IWidgetGraphicsAdapter graphicAdapter = ( IWidgetGraphicsAdapter )adapter;
      JsonValue args = JsonValue.NULL;
      int width = graphicAdapter.getRoundedBorderWidth();
      Color color = graphicAdapter.getRoundedBorderColor();
      if( width > 0 && color != null ) {
        Rectangle radius = graphicAdapter.getRoundedBorderRadius();
        args = new JsonArray()
          .add( width )
          .add( getJsonForColor( color, false ) )
          .add( radius.x )
          .add( radius.y )
          .add( radius.width )
          .add( radius.height );
      }
      getRemoteObject( widget ).set( "roundedBorder", args );
    }
  }

  private static boolean hasRoundedBorderChanged( Widget widget ) {
    Object adapter = widget.getAdapter( IWidgetGraphicsAdapter.class );
    IWidgetGraphicsAdapter graphicsAdapter = ( IWidgetGraphicsAdapter )adapter;
    int width = graphicsAdapter.getRoundedBorderWidth();
    Color color = graphicsAdapter.getRoundedBorderColor();
    Rectangle radius = graphicsAdapter.getRoundedBorderRadius();
    return
         hasChanged( widget,
                     PROP_ROUNDED_BORDER_WIDTH,
                     new Integer( width ),
                     new Integer( 0 ) )
      || hasChanged( widget,
                     PROP_ROUNDED_BORDER_COLOR,
                     color,
                     null )
      || hasChanged( widget,
                     PROP_ROUNDED_BORDER_RADIUS,
                     radius,
                     DEF_ROUNDED_BORDER_RADIUS );
  }

  public static String readPropertyValue( Widget widget, String property ) {
    String widgetId = WidgetUtil.getId( widget );
    return ProtocolUtil.readPropertyValueAsString( widgetId, property );
  }

  public static String readEventPropertyValue( Widget widget, String eventName, String property ) {
    String widgetId = WidgetUtil.getId( widget );
    return ProtocolUtil.readEventPropertyValueAsString( widgetId, eventName, property );
  }

  public static boolean wasEventSent( Widget widget, String eventName ) {
    String widgetId = WidgetUtil.getId( widget );
    return ProtocolUtil.wasEventSent( widgetId, eventName );
  }

  public static void preserveProperty( Widget widget, String property, Object value ) {
    WidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( property, value );
  }

  public static void preserveProperty( Widget widget, String property, int value ) {
    preserveProperty( widget, property, Integer.valueOf( value ) );
  }

  public static void preserveProperty( Widget widget, String property, boolean value ) {
    preserveProperty( widget, property, Boolean.valueOf( value ) );
  }

  public static void preserveListener( Widget widget, String listener, boolean value ) {
    WidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( LISTENER_PREFIX + listener, new Boolean( value ) );
  }

  @SuppressWarnings( "deprecation" )
  public static void renderProperty( Widget widget,
                                     String property,
                                     Object newValue,
                                     Object defaultValue )
  {
    if( hasChanged( widget, property, newValue, defaultValue ) ) {
      getRemoteObject( widget ).set( property, createJsonValue( newValue ) );
    }
  }

  public static void renderProperty( Widget widget,
                                     String property,
                                     String newValue,
                                     String defaultValue )
  {
    if( hasChanged( widget, property, newValue, defaultValue ) ) {
      getRemoteObject( widget ).set( property, newValue );
    }
  }

  public static void renderProperty( Widget widget,
                                     String property,
                                     Integer newValue,
                                     Integer defaultValue )
  {
    if( hasChanged( widget, property, newValue, defaultValue ) ) {
      JsonValue value = newValue == null ? JsonValue.NULL : JsonValue.valueOf( newValue.intValue() );
      getRemoteObject( widget ).set( property, value );
    }
  }

  public static void renderProperty( Widget widget,
                                     String property,
                                     String[] newValue,
                                     String[] defaultValue )
  {
    if( hasChanged( widget, property, newValue, defaultValue ) ) {
      JsonValue value = newValue == null ? JsonValue.NULL : createJsonArray( newValue );
      getRemoteObject( widget ).set( property, value );
    }
  }

  public static void renderProperty( Widget widget,
                                     String property,
                                     boolean[] newValue,
                                     boolean[] defaultValue )
  {
    if( hasChanged( widget, property, newValue, defaultValue ) ) {
      JsonValue value = newValue == null ? JsonValue.NULL : createJsonArray( newValue );
      getRemoteObject( widget ).set( property, value );
    }
  }

  public static void renderProperty( Widget widget,
                                     String property,
                                     int[] newValue,
                                     int[] defaultValue )
  {
    if( hasChanged( widget, property, newValue, defaultValue ) ) {
      JsonValue value = newValue == null ? JsonValue.NULL : createJsonArray( newValue );
      getRemoteObject( widget ).set( property, value );
    }
  }

  public static void renderProperty( Widget widget,
                                     String property,
                                     int newValue,
                                     int defaultValue )
  {
    Integer newValueObject = Integer.valueOf( newValue );
    Integer defaultValueObject = Integer.valueOf( defaultValue );
    if( hasChanged( widget, property, newValueObject, defaultValueObject ) ) {
      getRemoteObject( widget ).set( property, newValue );
    }
  }

  public static void renderProperty( Widget widget,
                                     String property,
                                     boolean newValue,
                                     boolean defaultValue )
  {
    Boolean newValueObject = Boolean.valueOf( newValue );
    Boolean defaultValueObject = Boolean.valueOf( defaultValue );
    if( hasChanged( widget, property, newValueObject, defaultValueObject ) ) {
      getRemoteObject( widget ).set( property, newValue );
    }
  }

  public static void renderProperty( Widget widget,
                                     String property,
                                     Image newValue,
                                     Image defaultValue )
  {
    if( hasChanged( widget, property, newValue, defaultValue ) ) {
      getRemoteObject( widget ).set( property, getJsonForImage( newValue ) );
    }
  }

  public static void renderProperty( Widget widget,
                                     String property,
                                     Image[] newValue,
                                     Image[] defaultValue )
  {
    if( hasChanged( widget, property, newValue, defaultValue ) ) {
      JsonArray images = new JsonArray();
      for( int i = 0; i < newValue.length; i++ ) {
        images.add( getJsonForImage( newValue[ i ] ) );
      }
      getRemoteObject( widget ).set( property, images );
    }
  }

  public static void renderProperty( Widget widget,
                                     String property,
                                     Color newValue,
                                     Color defaultValue )
  {
    if( hasChanged( widget, property, newValue, defaultValue ) ) {
      getRemoteObject( widget ).set( property, getJsonForColor( newValue, false ) );
    }
  }

  public static void renderProperty( Widget widget,
                                     String property,
                                     Color[] newValue,
                                     Color[] defaultValue )
  {
    if( hasChanged( widget, property, newValue, defaultValue ) ) {
      JsonArray colors = new JsonArray();
      for( int i = 0; i < newValue.length; i++ ) {
        colors.add( getJsonForColor( newValue[ i ], false ) );
      }
      getRemoteObject( widget ).set( property, colors );
    }
  }

  public static void renderProperty( Widget widget,
                                     String property,
                                     Font[] newValue,
                                     Font[] defaultValue )
  {
    if( hasChanged( widget, property, newValue, defaultValue ) ) {
      JsonArray fonts = new JsonArray();
      for( int i = 0; i < newValue.length; i++ ) {
        fonts.add( getJsonForFont( newValue[ i ] ) );
      }
      getRemoteObject( widget ).set( property, fonts );
    }
  }

  public static void renderProperty( Widget widget,
                                     String property,
                                     Point newValue,
                                     Point defaultValue )
  {
    if( hasChanged( widget, property, newValue, defaultValue ) ) {
      getRemoteObject( widget ).set( property, getJsonForPoint( newValue ) );
    }
  }

  public static void renderProperty( Widget widget,
                                     String property,
                                     Rectangle newValue,
                                     Rectangle defaultValue )
  {
    if( hasChanged( widget, property, newValue, defaultValue ) ) {
      getRemoteObject( widget ).set( property, getJsonForRectangle( newValue ) );
    }
  }

  public static void renderProperty( Widget widget,
                                     String property,
                                     Widget newValue,
                                     Widget defaultValue )
  {
    if( hasChanged( widget, property, newValue, defaultValue ) ) {
      String widgetId = newValue == null ? null : getId( newValue );
      getRemoteObject( widget ).set( property, widgetId );
    }
  }

  public static void renderListener( Widget widget,
                                     String listener,
                                     boolean newValue,
                                     boolean defaultValue )
  {
    String property = LISTENER_PREFIX + listener;
    Boolean newValueObject = Boolean.valueOf( newValue );
    Boolean defaultValueObject = Boolean.valueOf( defaultValue );
    if( hasChanged( widget, property, newValueObject, defaultValueObject ) ) {
      getRemoteObject( widget ).listen( listener, newValue );
    }
  }

  public static void renderClientListeners( Widget widget ) {
    List<ClientListenerOperation> operations = getClientListenerOperations( widget );
    if( operations != null ) {
      for( ClientListenerOperation operation : operations ) {
        JsonObject parameters = new JsonObject();
        parameters.add( "listenerId", getRemoteId( operation.getListener() ) );
        parameters.add( "eventType", ClientListenerUtil.getEventType( operation.getEventType() ) );
        if( operation instanceof AddListener ) {
          getRemoteObject( widget ).call( "addListener", parameters );
        } else if( operation instanceof RemoveListener ) {
          getRemoteObject( widget ).call( "removeListener", parameters );
        }
      }
    }
    ClientListenerUtil.clearClientListenerOperations( widget );
  }

  public static boolean hasChanged( Widget widget, String property, Object newValue ) {
    WidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    Object oldValue = adapter.getPreserved( property );
    return !equals( oldValue, newValue );
  }

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

  public static String replaceNewLines( String input, String replacement ) {
    return EncodingUtil.replaceNewLines( input, replacement );
  }

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
