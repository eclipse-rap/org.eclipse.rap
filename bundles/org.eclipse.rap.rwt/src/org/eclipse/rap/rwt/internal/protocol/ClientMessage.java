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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.protocol.Operation.CallOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.NotifyOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.SetOperation;
import org.eclipse.rap.rwt.internal.util.ParamCheck;


public class ClientMessage {

  public static final String PROP_HEAD = "head";
  public static final String PROP_OPERATIONS = "operations";
  public static final String OPERATION_SET = "set";
  public static final String OPERATION_NOTIFY = "notify";
  public static final String OPERATION_CALL = "call";

  private final JsonObject message;
  private final JsonObject head;
  private final HashMap<String,List<Operation>> operationsMap;
  private final List<Operation> operationsList;

  public ClientMessage( JsonObject json ) {
    ParamCheck.notNull( json, "json" );
    message = json;
    try {
      head = message.get( PROP_HEAD ).asObject();
    } catch( Exception exception ) {
      throw new IllegalArgumentException( "Missing header object: " + json );
    }
    JsonArray operations;
    try {
      operations = message.get( PROP_OPERATIONS ).asArray();
    } catch( Exception exception ) {
      throw new IllegalArgumentException( "Missing operations array: " + json );
    }
    try {
      operationsMap = new HashMap<String, List<Operation>>();
      operationsList = new ArrayList<Operation>();
      processOperations( operations );
    } catch( UnsupportedOperationException exception ) {
      throw new IllegalArgumentException( "Invalid operations array: " + json );
    }
  }

  public JsonValue getHeader( String key ) {
    return head.get( key );
  }

  public List<Operation> getAllOperations() {
    return Collections.unmodifiableList( operationsList );
  }

  public List<Operation> getAllOperationsFor( String target ) {
    List<Operation> operations = operationsMap.get( target );
    if( operations == null ) {
      return Collections.emptyList();
    }
    return Collections.unmodifiableList( operations );
  }

  public List<CallOperation> getAllCallOperationsFor( String target, String methodName ) {
    List<CallOperation> result = new ArrayList<CallOperation>();
    List<Operation> operations = target == null ? operationsList : operationsMap.get( target );
    if( operations != null ) {
      for( Operation operation : operations ) {
        if( operation instanceof CallOperation ) {
          CallOperation currentOperation = ( CallOperation )operation;
          if( methodName == null || currentOperation.getMethodName().equals( methodName ) ) {
            result.add( currentOperation );
          }
        }
      }
    }
    return result;
  }

  public SetOperation getLastSetOperationFor( String target, String property ) {
    SetOperation result = null;
    List<Operation> operations = target == null ? operationsList : operationsMap.get( target );
    if( operations != null ) {
      for( Operation operation : operations ) {
        if( operation instanceof SetOperation ) {
          SetOperation setOperation = ( SetOperation )operation;
          if( property == null || setOperation.getProperties().get( property ) != null ) {
            result = setOperation;
          }
        }
      }
    }
    return result;
  }

  public NotifyOperation getLastNotifyOperationFor( String target, String eventName ) {
    NotifyOperation result = null;
    List<Operation> operations = target == null ? operationsList : operationsMap.get( target );
    if( operations != null ) {
      for( Operation operation : operations ) {
        if( operation instanceof NotifyOperation ) {
          NotifyOperation currentOperation = ( NotifyOperation )operation;
          if( eventName == null || currentOperation.getEventName().equals( eventName ) ) {
            result = currentOperation;
          }
        }
      }
    }
    return result;
  }

  @Override
  public String toString() {
    return message.toString();
  }

  private void processOperations( JsonArray operations ) {
    for( JsonValue element : operations ) {
      appendOperation( createOperation( element.asArray() ) );
    }
  }

  private Operation createOperation( JsonArray data ) {
    try {
      return readOperation( data );
    } catch( Exception exception ) {
      throw new IllegalArgumentException( "Could not read operation: " + data );
    }
  }

  private Operation readOperation( JsonArray data ) {
    String action = data.get( 0 ).asString();
    String target = data.get( 1 ).asString();
    if( action.equals( OPERATION_SET ) ) {
      JsonObject properties = data.get( 2 ).asObject();
      return new SetOperation( target, properties );
    }
    if( action.equals( OPERATION_NOTIFY ) ) {
      String event = data.get( 2 ).asString();
      JsonObject properties = data.get( 3 ).asObject();
      return new NotifyOperation( target, event, properties );
    }
    if( action.equals( OPERATION_CALL ) ) {
      String method = data.get( 2 ).asString();
      JsonObject parameters = data.get( 3 ).asObject();
      return new CallOperation( target, method, parameters );
    }
    throw new IllegalArgumentException( "Unknown operation action: " + action );
  }

  private void appendOperation( Operation operation ) {
    String target = operation.getTarget();
    List<Operation> targetOperations = operationsMap.get( target );
    if( targetOperations == null ) {
      targetOperations = new ArrayList<Operation>();
    }
    targetOperations.add( operation );
    operationsMap.put( target, targetOperations );
    operationsList.add( operation );
  }

}
