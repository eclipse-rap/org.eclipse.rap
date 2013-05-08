/*******************************************************************************
* Copyright (c) 2010, 2013 EclipseSource and others.
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
import static org.eclipse.rap.rwt.internal.protocol.ProtocolConstants.HEAD;
import static org.eclipse.rap.rwt.internal.protocol.ProtocolConstants.OPERATIONS;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;


public class ProtocolMessageWriter {

  private final JsonObject head;
  private final JsonArray operations;
  private Operation pendingOperation;
  private boolean alreadyCreated;

  public ProtocolMessageWriter() {
    head = new JsonObject();
    operations = new JsonArray();
  }

  public boolean hasOperations() {
    return pendingOperation != null;
  }

  public void appendHead( String property, int value ) {
    appendHead( property, JsonValue.valueOf( value ) );
  }

  public void appendHead( String property, String value ) {
    appendHead( property, JsonValue.valueOf( value ) );
  }

  public void appendHead( String property, JsonValue value ) {
    ensureMessagePending();
    head.add( property, value );
  }

  public void appendCreate( String target, String type ) {
    prepareOperation( target, ACTION_CREATE, type );
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

  public void appendSet( String target, String property, JsonValue value ) {
    prepareOperation( target, ACTION_SET );
    pendingOperation.putProperty( property, value );
  }

  public void appendListen( String target, String eventType, boolean listen ) {
    prepareOperation( target, ACTION_LISTEN );
    pendingOperation.putProperty( eventType, JsonValue.valueOf( listen ) );
  }

  public void appendCall( String target, String methodName, JsonObject parameters ) {
    prepareOperation( target, ACTION_CALL, methodName, parameters );
  }

  public void appendDestroy( String target ) {
    prepareOperation( target, ACTION_DESTROY );
  }

  private void prepareOperation( String target, String type ) {
    prepareOperation( target, type, null, null );
  }

  private void prepareOperation( String target, String type, String detail ) {
    prepareOperation( target, type, detail, null );
  }

  private void prepareOperation( String target, String type, String detail, JsonObject properties )
  {
    ensureMessagePending();
    if( !canAppendToCurrentOperation( target, type ) ) {
      appendPendingOperation();
      pendingOperation = new Operation( target, type, detail, properties );
    }
  }

  public JsonObject createMessage() {
    ensureMessagePending();
    alreadyCreated = true;
    return createMessageObject();
  }

  private void ensureMessagePending() {
    if( alreadyCreated ) {
      throw new IllegalStateException( "Message already created" );
    }
  }

  private JsonObject createMessageObject() {
    JsonObject message = new JsonObject();
    message.add( HEAD, head );
    appendPendingOperation();
    message.add( OPERATIONS, operations );
    return message;
  }

  private boolean canAppendToCurrentOperation( String target, String action ) {
    boolean result = false;
    if( pendingOperation != null && pendingOperation.getTarget().equals( target ) ) {
      String pendingAction = pendingOperation.getAction();
      if( ACTION_LISTEN.equals( action ) ) {
        result = pendingAction.equals( ACTION_LISTEN );
      } else if( ACTION_SET.equals( action ) ) {
        result = pendingAction.equals( ACTION_CREATE ) || pendingAction.equals( ACTION_SET );
      }
    }
    return result;
  }

  private void appendPendingOperation() {
    if( pendingOperation != null ) {
      operations.add( pendingOperation.toJson() );
    }
  }

}
