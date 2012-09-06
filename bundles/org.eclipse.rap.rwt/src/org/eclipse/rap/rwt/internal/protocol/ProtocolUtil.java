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
      String json = request.getParameter( "message" );
      clientMessage = new ClientMessage( json );
      serviceStore.setAttribute( CLIENT_MESSAGE, clientMessage );
    }
    return clientMessage;
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

  public static String readPropertyValue( String target, String property ) {
    String result = null;
    ClientMessage message = getClientMessage();
    SetOperation[] operations =  message.getSetOperations( target, property );
    if( operations.length > 0 ) {
      result = operations[ operations.length - 1 ].getProperty( property ).toString();
    }
    return result;
  }

  public static String readEventPropertyValue( String target, String eventName, String property ) {
    String result = null;
    ClientMessage message = getClientMessage();
    NotifyOperation[] operations =  message.getNotifyOperations( target, eventName, property );
    if( operations.length > 0 ) {
      result = operations[ operations.length - 1 ].getProperty( property ).toString();
    }
    return result;
  }

  public static boolean wasEventSent( String target, String eventName ) {
    ClientMessage message = getClientMessage();
    NotifyOperation[] operations =  message.getNotifyOperations( target, eventName, null );
    return operations.length > 0;
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

}
