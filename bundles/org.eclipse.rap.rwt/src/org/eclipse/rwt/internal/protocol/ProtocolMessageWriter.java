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

import static org.eclipse.rwt.internal.protocol.ProtocolConstants.CREATE_PARENT;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.CREATE_STYLE;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.CREATE_TYPE;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.DO_NAME;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.EXECUTE_SCRIPT_CONTENT;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.EXECUTE_SCRIPT_TYPE;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.MESSAGE_META;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.MESSAGE_OPERATIONS;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.META_REQUEST_COUNTER;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.PARAMETER;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.TYPE_CREATE;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.TYPE_DESTROY;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.TYPE_DO;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.TYPE_EXECUTE_SCRIPT;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.TYPE_LISTEN;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.TYPE_SET;

import org.eclipse.rwt.internal.lifecycle.RWTRequestVersionControl;
import org.eclipse.rwt.internal.theme.*;


public final class ProtocolMessageWriter {

  private final JsonArray operations;
  private Operation pendingOperation;
  private boolean alreadyCreated;

  public ProtocolMessageWriter() {
    operations = new JsonArray();
  }

  public boolean hasOperations() {
    return pendingOperation != null;
  }

  public void appendCreate( String target,
                            String parentId,
                            String type,
                            String[] styles,
                            Object[] parameters )
  {
    prepareOperation( target, TYPE_CREATE );
    pendingOperation.appendProperty( CREATE_PARENT, JsonValue.valueOf( parentId ) );
    pendingOperation.appendProperty( CREATE_TYPE, JsonValue.valueOf( type ) );
    pendingOperation.appendProperty( CREATE_STYLE, JsonUtil.createJsonArray( styles ) );
    pendingOperation.appendProperty( PARAMETER, JsonUtil.createJsonArray( parameters ) );
  }

  public void appendSet( String target, String key, int value ) {
    appendSet( target, key, JsonValue.valueOf( value ) );
  }

  public void appendSet( String target, String key, double value ) {
    appendSet( target, key, JsonValue.valueOf( value ) );
  }

  public void appendSet( String target, String key, boolean value ) {
    appendSet( target, key, JsonValue.valueOf( value ) );
  }

  public void appendSet( String target, String key, String value ) {
    appendSet( target, key, JsonValue.valueOf( value ) );
  }

  public void appendSet( String target, String key, Object value ) {
    appendSet( target, key, JsonUtil.createJsonValue( value ) );
  }

  private void appendSet( String target, String key, JsonValue value ) {
    prepareOperation( target, TYPE_SET );
    pendingOperation.appendProperty( key, value );
  }

  public void appendListen( String target, String listener, boolean listen ) {
    prepareOperation( target, TYPE_LISTEN );
    pendingOperation.appendProperty( listener, JsonValue.valueOf( listen ) );
  }

  public void appendDo( String target, String name, Object[] parameters ) {
    prepareOperation( target, TYPE_DO );
    pendingOperation.appendProperty( DO_NAME, JsonValue.valueOf( name ) );
    pendingOperation.appendProperty( PARAMETER, JsonUtil.createJsonArray( parameters ) );
  }

  public void appendExecuteScript( String target, String type, String content ) {
    prepareOperation( target, TYPE_EXECUTE_SCRIPT );
    pendingOperation.appendProperty( EXECUTE_SCRIPT_TYPE, JsonValue.valueOf( type ) );
    pendingOperation.appendProperty( EXECUTE_SCRIPT_CONTENT, JsonValue.valueOf( content ) );
  }

  public void appendDestroy( String target ) {
    prepareOperation( target, TYPE_DESTROY );
  }

  private void prepareOperation( String target, String type ) {
    if( !canAppendToCurrentOperation( target, type ) ) {
      appendPendingOperation();
      pendingOperation = new Operation( target, type );
    }
  }

  public String createMessage() {
    if( alreadyCreated ) {
      throw new IllegalStateException( "Message already created" );
    }
    alreadyCreated = true;
    JsonObject message = createMessageObject();
    return message.toString();
  }

  private JsonObject createMessageObject() {
    JsonObject message = new JsonObject();
    JsonObject meta = new JsonObject();
    int requestCount = RWTRequestVersionControl.getInstance().getCurrentRequestId().intValue();
    meta.append( META_REQUEST_COUNTER, requestCount );
    message.append( MESSAGE_META, meta );
    appendPendingOperation();
    message.append( MESSAGE_OPERATIONS, operations );
    return message;
  }

  private boolean canAppendToCurrentOperation( String target, String type ) {
    return    pendingOperation != null
           && pendingOperation.matches( target, type )
           && isStreamableType( type );
  }

  private void appendPendingOperation() {
    if( pendingOperation != null ) {
      operations.append( pendingOperation.toJson() );
    }
  }

  private static boolean isStreamableType( String type ) {
    return type.equals( TYPE_SET  ) || type.equals( TYPE_LISTEN );
  }
}
