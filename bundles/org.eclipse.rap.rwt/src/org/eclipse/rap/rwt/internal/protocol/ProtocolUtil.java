/*******************************************************************************
 * Copyright (c) 2012, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.protocol;

import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.jsonToJava;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.CallOperation;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.NotifyOperation;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.SetOperation;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.ServiceStore;
import org.eclipse.rap.rwt.internal.util.SharedInstanceBuffer;
import org.eclipse.rap.rwt.internal.util.SharedInstanceBuffer.IInstanceCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.FontUtil;
import org.eclipse.swt.internal.graphics.ImageFactory;


public final class ProtocolUtil {

  private static final Pattern FONT_NAME_FILTER_PATTERN = Pattern.compile( "\"|\\\\" );
  private static final String CLIENT_MESSAGE = ProtocolUtil.class.getName() + "#clientMessage";

  //////////////////////////////////////////////////////////////////////////////
  // TODO [fappel]: Experimental - profiler seems to indicate that buffering
  //                improves performance - still under investigation.
  private final static SharedInstanceBuffer<String,String[]> parsedFonts
    = new SharedInstanceBuffer<String,String[]>();
  //////////////////////////////////////////////////////////////////////////////

  private ProtocolUtil() {
    // prevent instantiation
  }

  public static ClientMessage getClientMessage() {
    ServiceStore serviceStore = ContextProvider.getServiceStore();
    ClientMessage clientMessage = ( ClientMessage )serviceStore.getAttribute( CLIENT_MESSAGE );
    if( clientMessage == null ) {
      HttpServletRequest request = ContextProvider.getRequest();
      try {
        JsonObject json = JsonObject.readFrom( request.getReader() );
        clientMessage = new ClientMessage( json );
      } catch( IOException e ) {
        throw new IllegalStateException( "Unable to read the json message" );
      }
      serviceStore.setAttribute( CLIENT_MESSAGE, clientMessage );
    }
    return clientMessage;
  }

  public static void setClientMessage( ClientMessage clientMessage ) {
    ServiceStore serviceStore = ContextProvider.getServiceStore();
    serviceStore.setAttribute( CLIENT_MESSAGE, clientMessage );
  }

  public static boolean isClientMessageProcessed() {
    ServiceStore serviceStore = ContextProvider.getServiceStore();
    return serviceStore.getAttribute( CLIENT_MESSAGE ) != null;
  }

  public static JsonValue readPropertyValue( String target, String property ) {
    JsonValue result = null;
    ClientMessage message = getClientMessage();
    SetOperation operation =  message.getLastSetOperationFor( target, property );
    if( operation != null ) {
      result = operation.getProperty( property );
    }
    return result;
  }

  public static String readPropertyValueAsString( String target, String property ) {
    return readPropertyValueAs( target, property, String.class );
  }

  public static Point readPropertyValueAsPoint( String target, String property ) {
    return readPropertyValueAs( target, property, Point.class );
  }

  public static Rectangle readPropertyValueAsRectangle( String target, String property ) {
    return readPropertyValueAs( target, property, Rectangle.class );
  }

  public static int[] readPropertyValueAsIntArray( String target, String property ) {
    return readPropertyValueAs( target, property, int[].class );
  }

  public static boolean[] readPropertyValueAsBooleanArray( String target, String property ) {
    return readPropertyValueAs( target, property, boolean[].class );
  }

  public static String[] readPropertyValueAsStringArray( String target, String property ) {
    return readPropertyValueAs( target, property, String[].class );
  }

  @SuppressWarnings( "unchecked" )
  private static <T> T readPropertyValueAs( String target, String property, Class<T> clazz ) {
    T result = null;
    ClientMessage message = getClientMessage();
    SetOperation operation =  message.getLastSetOperationFor( target, property );
    if( operation != null ) {
      Object value = jsonToJava( operation.getProperty( property ) );
      if( value != null ) {
        if( String.class.equals( clazz ) ) {
          result = ( T )value.toString();
        } else if( Point.class.equals( clazz ) ) {
          result = ( T )toPoint( value );
        } else if( Rectangle.class.equals( clazz ) ) {
          result = ( T )toRectangle( value );
        } else if( int[].class.equals( clazz ) ) {
          result = ( T )toIntArray( value );
        } else if( boolean[].class.equals( clazz ) ) {
          result = ( T )toBooleanArray( value );
        } else if( String[].class.equals( clazz ) ) {
          result = ( T )toStringArray( value );
        } else {
          throw new IllegalStateException( "Could not convert property to " + clazz.getName() );
        }
      }
    }
    return result;
  }

  public static String readEventPropertyValueAsString( String target,
                                                       String eventName,
                                                       String property )
  {
    String result = null;
    ClientMessage message = getClientMessage();
    NotifyOperation operation = message.getLastNotifyOperationFor( target, eventName );
    if( operation != null ) {
      Object value = jsonToJava( operation.getProperty( property ) );
      if( value != null ) {
        result = value.toString();
      }
    }
    return result;
  }

  public static boolean wasEventSent( String target, String eventName ) {
    ClientMessage message = getClientMessage();
    NotifyOperation operation =  message.getLastNotifyOperationFor( target, eventName );
    return operation != null;
  }

  public static String readCallPropertyValueAsString( String target,
                                                      String methodName,
                                                      String property )
  {
    String result = null;
    ClientMessage message = getClientMessage();
    List<CallOperation> operations = message.getAllCallOperationsFor( target, methodName );
    if( !operations.isEmpty() ) {
      CallOperation operation = operations.get( operations.size() - 1 );
      Object value = jsonToJava( operation.getProperty( property ) );
      if( value != null ) {
        result = value.toString();
      }
    }
    return result;
  }

  public static boolean wasCallReceived( String target, String methodName ) {
    ClientMessage message = getClientMessage();
    List<CallOperation> operations = message.getAllCallOperationsFor( target, methodName );
    return !operations.isEmpty();
  }

  public static JsonValue getJsonForFont( Font font ) {
    FontData fontData = font == null ? null : FontUtil.getData( font );
    return getJsonForFont( fontData );
  }

  public static JsonValue getJsonForFont( FontData fontData ) {
    if( fontData != null ) {
      return new JsonArray()
        .add( JsonUtil.createJsonArray( parseFontName( fontData.getName() ) ) )
        .add( fontData.getHeight() )
        .add( ( fontData.getStyle() & SWT.BOLD ) != 0 )
        .add( ( fontData.getStyle() & SWT.ITALIC ) != 0 );
    }
    return JsonValue.NULL;
  }

  public static String[] parseFontName( final String name ) {
    return parsedFonts.get( name, new IInstanceCreator<String[]>() {
      public String[] createInstance() {
        return parseFontNameInternal( name );
      }
    } );
  }

  private static String[] parseFontNameInternal( String name ) {
    String[] result = name.split( "," );
    for( int i = 0; i < result.length; i++ ) {
      result[ i ] = result[ i ].trim();
      Matcher matcher = FONT_NAME_FILTER_PATTERN.matcher( result[ i ] );
      result[ i ] = matcher.replaceAll( "" );
    }
    return result;
  }

  public static JsonValue getJsonForImage( Image image ) {
    JsonValue result = JsonValue.NULL;
    if( image != null ) {
      String imagePath = ImageFactory.getImagePath( image );
      Rectangle bounds = image.getBounds();
      result = new JsonArray()
        .add( imagePath )
        .add( bounds.width )
        .add( bounds.height );
    }
    return result;
  }

  public static JsonValue getJsonForColor( Color color, boolean transparent ) {
    RGB rgb = color == null ? null : color.getRGB();
    return getJsonForColor( rgb, transparent );
  }

  public static JsonValue getJsonForColor( RGB rgb, boolean transparent ) {
    if( rgb != null ) {
      int transparency = transparent ? 0 : 255;
      return new JsonArray().add( rgb.red ).add( rgb.green ).add( rgb.blue ).add( transparency );
    } else if( transparent ) {
      return new JsonArray().add( 0 ).add( 0 ).add( 0 ).add( 0 );
    }
    return JsonValue.NULL;
  }

  public static JsonValue getJsonForPoint( Point point ) {
    return point == null ? JsonValue.NULL : new JsonArray().add( point.x ).add( point.y );
  }

  public static JsonValue getJsonForRectangle( Rectangle rect ) {
    if( rect == null ) {
      return JsonValue.NULL;
    }
    return new JsonArray().add( rect.x ).add( rect.y ).add( rect.width ).add( rect.height );
  }

  public static Point toPoint( JsonValue value ) {
    try {
      JsonArray array = value.asArray();
      if( array.size() != 2 ) {
        throw new IllegalArgumentException( "Expected array of size 2" );
      }
      return new Point( array.get( 0 ).asInt(), array.get( 1 ).asInt() );
    } catch( Exception exception ) {
      String message = "Could not convert property to Point: " + value;
      throw new IllegalArgumentException( message, exception );
    }
  }

  public static Rectangle toRectangle( JsonValue value ) {
    try {
      JsonArray array = value.asArray();
      if( array.size() != 4 ) {
        throw new IllegalArgumentException( "Expected array of size 4" );
      }
      return new Rectangle( array.get( 0 ).asInt(),
                            array.get( 1 ).asInt(),
                            array.get( 2 ).asInt(),
                            array.get( 3 ).asInt() );
    } catch( Exception exception ) {
      String message = "Could not convert property to Rectangle: " + value;
      throw new IllegalArgumentException( message, exception );
    }
  }

  public static Rectangle toRectangle( Object value ) {
    int[] array = toIntArray( value );
    checkArrayLength( array, 4 );
    return new Rectangle( array[ 0 ], array[ 1 ], array[ 2 ], array[ 3 ] );
  }

  public static Point toPoint( Object value ) {
    int[] array = toIntArray( value );
    checkArrayLength( array, 2 );
    return new Point( array[ 0 ], array[ 1 ] );
  }

  private static int[] toIntArray( Object value ) {
    int[] result;
    if( value instanceof Object[] ) {
      Object[] array = ( Object[] )value;
      result = new int[ array.length ];
      for( int i = 0; i < array.length; i++ ) {
        try {
          result[ i ] = ( ( Integer )array[ i ] ).intValue();
        } catch( ClassCastException exception ) {
          String message = "Could not convert to int array: array contains non-int value";
          throw new IllegalStateException( message );
        }
      }
    } else {
      throw new IllegalStateException( "Could not convert to int array: property is not an array" );
    }
    return result;
  }

  private static String[] toStringArray( Object value ) {
    String[] result;
    if( value instanceof Object[] ) {
      Object[] array = ( Object[] )value;
      result = new String[ array.length ];
      for( int i = 0; i < array.length; i++ ) {
        try {
          result[ i ] = ( String )array[ i ];
        } catch( ClassCastException exception ) {
          String message = "Could not convert to string array: array contains non-string value";
          throw new IllegalStateException( message );
        }
      }
    } else {
      String message = "Could not convert to string array: property is not a string";
      throw new IllegalStateException( message );
    }
    return result;
  }

  private static boolean[] toBooleanArray( Object value ) {
    boolean[] result;
    if( value instanceof Object[] ) {
      Object[] array = ( Object[] )value;
      result = new boolean[ array.length ];
      for( int i = 0; i < array.length; i++ ) {
        try {
          result[ i ] = ( ( Boolean )array[ i ] ).booleanValue();
        } catch( ClassCastException exception ) {
          String message = "Could not convert to boolean array: array contains non-boolean value";
          throw new IllegalStateException( message );
        }
      }
    } else {
      String message = "Could not convert to boolean array: property is not an array";
      throw new IllegalStateException( message );
    }
    return result;
  }

  private static void checkArrayLength( int[] array, int length ) {
    if( array.length != length ) {
      String message = "Could not convert property to point: invalid array length";
      throw new IllegalStateException( message );
    }
  }

}
