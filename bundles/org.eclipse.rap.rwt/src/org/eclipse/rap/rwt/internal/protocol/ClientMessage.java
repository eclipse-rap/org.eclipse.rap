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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
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
      operationsMap = new HashMap<String,List<Operation>>();
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
          SetOperation currentOperation = ( SetOperation )operation;
          if( property == null || operation.getPropertyNames().contains( property ) ) {
            result = currentOperation;
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
    for( int i = 0; i < operations.size(); i++ ) {
      Operation operation = createOperation( operations.get( i ).asArray() );
      appendOperation( operation );
    }
  }

  private Operation createOperation( JsonArray data ) {
    Operation result = null;
    String action = getOperationAction( data );
    if( action.equals( OPERATION_SET ) ) {
      result = new SetOperation( data );
    } else if( action.equals( OPERATION_NOTIFY ) ) {
      result = new NotifyOperation( data );
    } else if( action.equals( OPERATION_CALL ) ) {
      result = new CallOperation( data );
    } else {
      throw new IllegalArgumentException( "Unknown operation action: " + action );
    }
    return result;
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

  private String getOperationAction( JsonArray operation ) {
    String result;
    try {
      result = operation.get( 0 ).asString();
    } catch( Exception e ) {
      throw new IllegalArgumentException( "Could not find action for operation " + operation );
    }
    return result;
  }

  public abstract class Operation {

    private final String target;

    private Operation( JsonArray operation ) {
      try {
        target = operation.get( 1 ).asString();
      } catch( Exception e ) {
        throw new IllegalArgumentException( "Invalid operation target", e );
      }
    }

    public String getTarget() {
      return target;
    }

    public List<String> getPropertyNames() {
      return getProperties().names();
    }

    public JsonValue getProperty( String key ) {
      return getProperties().get( key );
    }

    abstract public JsonObject getProperties();

  }

  public final class SetOperation extends Operation {

    private final JsonObject properties;

    private SetOperation( JsonArray operation ) {
      super( operation );
      try {
        properties = operation.get( 2 ).asObject();
      } catch( Exception e ) {
        throw new IllegalArgumentException( "Properties object missing in operation", e );
      }
    }

    @Override
    public JsonObject getProperties() {
      return properties;
    }

  }

  public final class NotifyOperation extends Operation {

    private final String eventName;
    private final JsonObject properties;

    private NotifyOperation( JsonArray operation ) {
      super( operation );
      try {
        eventName = operation.get( 2 ).asString();
      } catch( Exception e ) {
        throw new IllegalArgumentException( "Event type missing in operation", e );
      }
      try {
        properties = operation.get( 3 ).asObject();
      } catch( Exception e ) {
        throw new IllegalArgumentException( "Properties object missing in operation", e );
      }
    }

    public String getEventName() {
      return eventName;
    }

    @Override
    public JsonObject getProperties() {
      return properties;
    }

  }

  public final class CallOperation extends Operation {

    private final String methodName;
    private final JsonObject properties;

    private CallOperation( JsonArray operation ) {
      super( operation );
      try {
        methodName = operation.get( 2 ).asString();
      } catch( Exception e ) {
        throw new IllegalArgumentException( "Method name missing in operation", e );
      }
      try {
        properties = operation.get( 3 ).asObject();
      } catch( Exception e ) {
        throw new IllegalArgumentException( "Properties object missing in operation", e );
      }
    }

    public String getMethodName() {
      return methodName;
    }

    @Override
    public JsonObject getProperties() {
      return properties;
    }

  }

}
