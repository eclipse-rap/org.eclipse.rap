/*******************************************************************************
* Copyright (c) 2010, 2012 EclipseSource and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.rap.rwt.internal.protocol;

import static org.eclipse.rap.rwt.internal.protocol.ProtocolConstants.ACTION_CALL;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolConstants.ACTION_CREATE;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolConstants.ACTION_DESTROY;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolConstants.ACTION_LISTEN;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolConstants.ACTION_SET;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolConstants.CALL_METHOD_NAME;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolConstants.CREATE_TYPE;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolConstants.META;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolConstants.OPERATIONS;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.theme.JsonArray;
import org.eclipse.rap.rwt.internal.theme.JsonObject;
import org.eclipse.rap.rwt.internal.theme.JsonValue;
import org.eclipse.rwt.lifecycle.JSWriter;
import org.eclipse.rwt.service.IServiceStore;


@SuppressWarnings("deprecation")
public class ProtocolMessageWriter {
 
  // TODO [rst] Copy of JSWriter constant, remove when JSWriter is gone
  private static final String HAS_WIDGET_MANAGER = JSWriter.class.getName() + "#hasWidgetManager";
  // TODO [rst] Copy of JSWriter constant, remove when JSWriter is gone
  private static final String CURRENT_WIDGET_REF = JSWriter.class.getName() + "#currentWidgetRef";
  // TODO [if] Remove when JSWriter is gone
  private static final String JSEXECUTOR_ID = "jsex";
  // TODO [if] Moved from ProtocolConstants, remove when JSWriter is gone
  private static final String ACTION_EXECUTE_SCRIPT = "execute";
  // TODO [if] Moved from ProtocolConstants, remove when JSWriter is gone
  private static final String EXECUTE_SCRIPT_CONTENT = "content";

  private final JsonObject meta;
  private final JsonArray operations;
  private Operation pendingOperation;
  private boolean alreadyCreated;

  public ProtocolMessageWriter() {
    meta = new JsonObject();
    operations = new JsonArray();
  }

  public boolean hasOperations() {
    return pendingOperation != null;
  }

  public void appendMeta( String property, int value ) {
    appendMeta( property, JsonValue.valueOf( value ) );
  }

  public void appendMeta( String property, JsonValue value ) {
    ensureMessagePending();
    meta.append( property, value );
  }

  public void appendCreate( String target, String type ) {
    prepareOperation( target, ACTION_CREATE );
    pendingOperation.appendDetail( CREATE_TYPE, JsonValue.valueOf( type ) );
  }

  public void appendSet( String target, String property, int value ) {
    appendSet( target, property, JsonValue.valueOf( value ) );
  }

  public void appendSet( String target, String property, double value ) {
    appendSet( target, property, JsonValue.valueOf( value ) );
  }

  public void appendSet( String target, String property, boolean value ) {
    appendSet( target, property, JsonValue.valueOf( value ) );
  }

  public void appendSet( String target, String property, String value ) {
    appendSet( target, property, JsonValue.valueOf( value ) );
  }

  public void appendSet( String target, String property, Object value ) {
    appendSet( target, property, JsonUtil.createJsonValue( value ) );
  }

  private void appendSet( String target, String property, JsonValue value ) {
    prepareOperation( target, ACTION_SET );
    pendingOperation.appendProperty( property, value );
  }

  public void appendListen( String target, String eventType, boolean listen ) {
    prepareOperation( target, ACTION_LISTEN );
    pendingOperation.appendProperty( eventType, JsonValue.valueOf( listen ) );
  }

  public void appendCall( String target, String methodName, Map<String, Object> properties ) {
    // TODO [if] Needed to append JavaScript in JSWriter, remove when JSWriter is gone
    if( JSEXECUTOR_ID.equals( target ) && ACTION_EXECUTE_SCRIPT.equals( methodName ) ) {
      appendExecuteScript( target, ( String )properties.get( EXECUTE_SCRIPT_CONTENT ) );
    } else {
      prepareOperation( target, ACTION_CALL );
      pendingOperation.appendDetail( CALL_METHOD_NAME, JsonValue.valueOf( methodName ) );
      pendingOperation.appendProperties( properties );
    }
  }

  public void appendDestroy( String target ) {
    prepareOperation( target, ACTION_DESTROY );
  }

  private void prepareOperation( String target, String type ) {
    ensureMessagePending();
    if( !canAppendToCurrentOperation( target, type ) ) {
      appendPendingOperation();
      pendingOperation = new Operation( target, type );
      invalidateJsWriterState();
    }
  }

  // TODO [rst] Needed to invalidate JavaScript context of JSWriter, remove when JSWriter is gone
  private static void invalidateJsWriterState() {
    IServiceStore serviceStore = ContextProvider.getServiceStore();
    serviceStore.removeAttribute( HAS_WIDGET_MANAGER );
    serviceStore.removeAttribute( CURRENT_WIDGET_REF );
  }

  public String createMessage() {
    ensureMessagePending();
    alreadyCreated = true;
    JsonObject message = createMessageObject();
    return message.toString();
  }

  private void ensureMessagePending() {
    if( alreadyCreated ) {
      throw new IllegalStateException( "Message already created" );
    }
  }

  private JsonObject createMessageObject() {
    JsonObject message = new JsonObject();
    message.append( META, meta );
    appendPendingOperation();
    message.append( OPERATIONS, operations );
    return message;
  }

  private boolean canAppendToCurrentOperation( String target, String action ) {
    boolean result = false;
    if( pendingOperation != null && pendingOperation.getTarget().equals( target ) ) {
      String pendingAction = pendingOperation.getAction();
      if( ACTION_LISTEN.equals( action ) ) {
        result = pendingAction.equals( ACTION_LISTEN );
      } else if( ACTION_EXECUTE_SCRIPT.equals( action ) ) {
        result = pendingAction.equals( ACTION_EXECUTE_SCRIPT );
      } else if( ACTION_SET.equals( action ) ) {
        result = pendingAction.equals( ACTION_CREATE ) || pendingAction.equals( ACTION_SET );
      }
    }
    return result;
  }

  private void appendPendingOperation() {
    if( pendingOperation != null ) {
      replaceExecuteScriptOperation();
      operations.append( pendingOperation.toJson() );
    }
  }
  
  // TODO [if] Needed to append JavaScript in JSWriter, remove when JSWriter is gone
  private void appendExecuteScript( String target, String code ) {
    prepareOperation( target, ACTION_EXECUTE_SCRIPT );
    String pendingScript = ( String )pendingOperation.getDetail( EXECUTE_SCRIPT_CONTENT );
    if( pendingScript != null ) {
      pendingOperation.replaceDetail( EXECUTE_SCRIPT_CONTENT, pendingScript + code );
    } else {
      pendingOperation.appendDetail( EXECUTE_SCRIPT_CONTENT, code );
    }
  }
  
  // TODO [if] Needed to append JavaScript in JSWriter, remove when JSWriter is gone
  private void replaceExecuteScriptOperation() {
    if( pendingOperation.getAction().equals( ACTION_EXECUTE_SCRIPT ) ) {
      String code = ( String )pendingOperation.getDetail( EXECUTE_SCRIPT_CONTENT );
      pendingOperation = new Operation( JSEXECUTOR_ID, ACTION_CALL );
      pendingOperation.appendDetail( CALL_METHOD_NAME, JsonValue.valueOf( ACTION_EXECUTE_SCRIPT ) );
      Map<String, Object> properties = new HashMap<String, Object>();
      properties.put( EXECUTE_SCRIPT_CONTENT, code );
      pendingOperation.appendProperties( properties );
    }
  }
}
