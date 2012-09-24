/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rap.rwt.internal.protocol.ClientMessage.NotifyOperation;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.SetOperation;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.util.SharedInstanceBuffer;
import org.eclipse.rap.rwt.internal.util.SharedInstanceBuffer.IInstanceCreator;
import org.eclipse.rap.rwt.service.IServiceStore;
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
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    ClientMessage clientMessage = ( ClientMessage )serviceStore.getAttribute( CLIENT_MESSAGE );
    if( clientMessage == null ) {
      HttpServletRequest request = ContextProvider.getRequest();
      StringBuilder json = new StringBuilder();
      try {
        InputStreamReader inputStreamReader = new InputStreamReader( request.getInputStream() );
        BufferedReader reader = new BufferedReader( inputStreamReader );
        String line = reader.readLine();
        while( line != null ) {
          json.append( line + "\n" );
          line = reader.readLine();
        }
        reader.close();
      } catch( IOException e ) {
        throw new IllegalStateException( "Unable to read the json message" );
      }
      clientMessage = new ClientMessage( json.toString() );
      serviceStore.setAttribute( CLIENT_MESSAGE, clientMessage );
    }
    return clientMessage;
  }

  public static void setClientMessage( ClientMessage clientMessage ) {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    serviceStore.setAttribute( CLIENT_MESSAGE, clientMessage );
  }

  public static boolean isClientMessageProcessed() {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    return serviceStore.getAttribute( CLIENT_MESSAGE ) != null;
  }

  public static String readHeaderPropertyValue( String property ) {
    ClientMessage message = getClientMessage();
    Object result = message.getHeaderProperty( property );
    return result == null ? null : result.toString();
  }

  public static String readPropertyValueAsString( String target, String property ) {
    String result = null;
    ClientMessage message = getClientMessage();
    SetOperation operation =  message.getLastSetOperationFor( target, property );
    if( operation != null ) {
      Object value = operation.getProperty( property );
      if( value != null ) {
        result = value.toString();
      }
    }
    return result;
  }

  public static Point readPropertyValueAsPoint( String target, String property ) {
    Point result = null;
    ClientMessage message = getClientMessage();
    SetOperation operation =  message.getLastSetOperationFor( target, property );
    if( operation != null ) {
      Object value = operation.getProperty( property );
      if( value != null ) {
        result = toPoint( value );
      }
    }
    return result;
  }

  public static Rectangle readPropertyValueAsRectangle( String target, String property ) {
    Rectangle result = null;
    ClientMessage message = getClientMessage();
    SetOperation operation =  message.getLastSetOperationFor( target, property );
    if( operation != null ) {
      Object value = operation.getProperty( property );
      if( value != null ) {
        result = toRectangle( value );
      }
    }
    return result;
  }


  public static int[] readPropertyValueAsIntArray( String target, String property ) {
    int[] result = null;
    ClientMessage message = getClientMessage();
    SetOperation operation =  message.getLastSetOperationFor( target, property );
    if( operation != null ) {
      Object value = operation.getProperty( property );
      if( value != null ) {
        result = toIntArray( value );
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
    NotifyOperation operation =  message.getLastNotifyOperationFor( target, eventName );
    if( operation != null ) {
      Object value = operation.getProperty( property );
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

  public static Object[] getFontAsArray( Font font ) {
    FontData fontData = font == null ? null : FontUtil.getData( font );
    return getFontAsArray( fontData );
  }

  public static Object[] getFontAsArray( FontData fontData ) {
    Object[] result = null;
    if( fontData != null ) {
      result = new Object[] {
        parseFontName( fontData.getName() ),
        Integer.valueOf( fontData.getHeight() ),
        Boolean.valueOf( ( fontData.getStyle() & SWT.BOLD ) != 0 ),
        Boolean.valueOf( ( fontData.getStyle() & SWT.ITALIC ) != 0 )
      };
    }
    return result;
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

  public static Object[] getImageAsArray( Image image ) {
    Object[] result = null;
    if( image != null ) {
      String imagePath = ImageFactory.getImagePath( image );
      Rectangle bounds = image.getBounds();
      result = new Object[] {
        imagePath,
        Integer.valueOf( bounds.width ),
        Integer.valueOf( bounds.height )
      };
    }
    return result;
  }

  public static int[] getColorAsArray( Color color, boolean transparent ) {
    RGB rgb = color == null ? null : color.getRGB();
    return getColorAsArray( rgb, transparent );
  }

  public static int[] getColorAsArray( RGB rgb, boolean transparent ) {
    int[] result = null;
    if( rgb != null ) {
      result = new int[ 4 ];
      result[ 0 ] = rgb.red;
      result[ 1 ] = rgb.green;
      result[ 2 ] = rgb.blue;
      result[ 3 ] = transparent ? 0 : 255;
    } else if( transparent ) {
      result = new int[] { 0, 0, 0, 0 };
    }
    return result;
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

  private static void checkArrayLength( int[] array, int length ) {
    if( array.length != length ) {
      String message = "Could not convert property to point: invalid array length";
      throw new IllegalStateException( message );
    }
  }

}
