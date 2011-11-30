/*******************************************************************************
* Copyright (c) 2010, 2011 EclipseSource and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.rwt.internal.protocol;

import static org.eclipse.rwt.internal.protocol.ProtocolConstants.ACTION_CALL;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.ACTION_CREATE;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.ACTION_DESTROY;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.ACTION_EXECUTE_SCRIPT;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.ACTION_LISTEN;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.ACTION_SET;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.CALL_METHOD_NAME;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.CREATE_TYPE;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.EXECUTE_SCRIPT_CONTENT;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.EXECUTE_SCRIPT_TYPE;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.META;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.OPERATIONS;

import java.util.Map;

import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.rwt.internal.theme.*;
import org.eclipse.rwt.lifecycle.JSWriter;


public final class ProtocolMessageWriter {

  // TODO [rst] Copy of JSWriter constant, remove when JSWriter is gone
  private static final String HAS_WIDGET_MANAGER = JSWriter.class.getName() + "#hasWidgetManager";
  // TODO [rst] Copy of JSWriter constant, remove when JSWriter is gone
  private static final String CURRENT_WIDGET_REF = JSWriter.class.getName() + "#currentWidgetRef";

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
    prepareOperation( target, ACTION_CALL );
    pendingOperation.appendDetail( CALL_METHOD_NAME, JsonValue.valueOf( methodName ) );
    pendingOperation.appendProperties( properties );
  }

  public void appendExecuteScript( String target, String scriptType, String code ) {
    prepareOperation( target, ACTION_EXECUTE_SCRIPT );
    Object pendingScriptType = pendingOperation.getDetail( EXECUTE_SCRIPT_TYPE );
    if( pendingScriptType != null ) {
      if( !pendingScriptType.equals( scriptType ) ) {
        throw new IllegalStateException( "Cannot mix different script types" );
      }
    } else {
      pendingOperation.appendDetail( EXECUTE_SCRIPT_TYPE, scriptType );
    }
    String pendingScript = ( String )pendingOperation.getDetail( EXECUTE_SCRIPT_CONTENT );
    if( pendingScript != null ) {
      pendingOperation.replaceDetail( EXECUTE_SCRIPT_CONTENT, pendingScript + code );
    } else {
      pendingOperation.appendDetail( EXECUTE_SCRIPT_CONTENT, code );
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
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    stateInfo.removeAttribute( HAS_WIDGET_MANAGER );
    stateInfo.removeAttribute( CURRENT_WIDGET_REF );
  }

  public String createMessage() {
    ensureMessagePending();
    alreadyCreated = true;
    JsonObject message = createMessageObject();
    return message.toString();
  }

  public boolean hasPendingExecuteOperation( String target ) {
    return pendingOperation != null
           && ACTION_EXECUTE_SCRIPT.equals( pendingOperation.getAction() )
           && pendingOperation.getTarget().equals( target );
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
      operations.append( pendingOperation.toJson() );
    }
  }
}
