/*******************************************************************************
 * Copyright (c) 2012, 2014 EclipseSource and others.
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
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.CallOperation;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.NotifyOperation;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.Operation;
import org.eclipse.rap.rwt.internal.protocol.ClientMessage.SetOperation;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.ServiceStore;
import org.eclipse.rap.rwt.internal.util.HTTP;
import org.eclipse.rap.rwt.internal.util.SharedInstanceBuffer;
import org.eclipse.rap.rwt.internal.util.SharedInstanceBuffer.IInstanceCreator;
import org.eclipse.rap.rwt.remote.OperationHandler;


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
        JsonObject json = JsonObject.readFrom( getReader( request ) );
        clientMessage = new ClientMessage( json );
      } catch( IOException ioe ) {
        throw new IllegalStateException( "Unable to read the json message", ioe );
      }
      serviceStore.setAttribute( CLIENT_MESSAGE, clientMessage );
    }
    return clientMessage;
  }

  /*
   * Workaround for bug in certain servlet containers where the reader is sometimes empty.
   * 411616: Application crash with very long messages
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=411616
   */
  private static Reader getReader( HttpServletRequest request ) throws IOException {
    String encoding = request.getCharacterEncoding();
    if( encoding == null ) {
      encoding = HTTP.CHARSET_UTF_8;
    }
    return new InputStreamReader( request.getInputStream(), encoding );
  }

  public static void setClientMessage( ClientMessage clientMessage ) {
    ServiceStore serviceStore = ContextProvider.getServiceStore();
    serviceStore.setAttribute( CLIENT_MESSAGE, clientMessage );
  }

  public static boolean isClientMessageProcessed() {
    ServiceStore serviceStore = ContextProvider.getServiceStore();
    return serviceStore.getAttribute( CLIENT_MESSAGE ) != null;
  }

  public static void handleOperation( OperationHandler handler, Operation operation ) {
    if( operation instanceof SetOperation ) {
      SetOperation setOperation = ( SetOperation )operation;
      handler.handleSet( setOperation.getProperties() );
    } else if( operation instanceof CallOperation ) {
      CallOperation callOperation = ( CallOperation )operation;
      handler.handleCall( callOperation.getMethodName(), callOperation.getProperties() );
    } else if( operation instanceof NotifyOperation ) {
      NotifyOperation notifyOperation = ( NotifyOperation )operation;
      handler.handleNotify( notifyOperation.getEventName(), notifyOperation.getProperties() );
    }
  }

  public static JsonValue readPropertyValue( String target, String property ) {
    SetOperation operation =  getClientMessage().getLastSetOperationFor( target, property );
    if( operation != null ) {
      return operation.getProperty( property );
    }
    return null;
  }

  public static String readPropertyValueAsString( String target, String property ) {
    JsonValue value = readPropertyValue( target, property );
    return value == null ? null : jsonToJava( value ).toString();
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

}
