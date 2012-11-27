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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.eclipse.rap.rwt.internal.util.ParamCheck;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ClientMessage {

  public static final String PROP_HEAD = "head";
  public static final String PROP_OPERATIONS = "operations";
  public static final String OPERATION_SET = "set";
  public static final String OPERATION_NOTIFY = "notify";
  public static final String OPERATION_CALL = "call";

  private final JSONObject message;
  private final JSONObject head;
  private final HashMap<String,List<Operation>> operationsMap;
  private final List<Operation> operationsList;

  public ClientMessage( String json ) {
    ParamCheck.notNull( json, "json" );
    try {
      message = new JSONObject( json );
    } catch( JSONException e ) {
      throw new IllegalArgumentException( "Could not parse json message: " + json );
    }
    try {
      head = message.getJSONObject( PROP_HEAD );
    } catch( JSONException exception ) {
      throw new IllegalArgumentException( "Missing header object: " + json );
    }
    JSONArray operations;
    try {
      operations = message.getJSONArray( PROP_OPERATIONS );
    } catch( JSONException exception ) {
      throw new IllegalArgumentException( "Missing operations array: " + json );
    }
    try {
      operationsMap = new HashMap<String,List<Operation>>();
      operationsList = new ArrayList<Operation>();
      processOperations( operations );
    } catch( JSONException exception ) {
      throw new IllegalArgumentException( "Invalid operations array: " + json );
    }
  }

  public Operation[] getAllOperations() {
    return operationsList.toArray( new Operation[ 0 ] );
  }

  public Operation[] getAllOperationsFor( String target ) {
    return getOperations( Operation.class, target, null );
  }

  public SetOperation getLastSetOperationFor( String target, String property ) {
    SetOperation result = null;
    SetOperation[] operations = getOperations( SetOperation.class, target, property );
    if( operations.length > 0 ) {
      result = operations[ operations.length - 1 ];
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

  public CallOperation[] getAllCallOperationsFor( String target, String methodName ) {
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
    return result.toArray( new CallOperation[ 0 ] );
  }

  public Object getHeadProperty( String key ) {
    Object result = null;
    try {
      result = head.get( key );
    } catch( JSONException exception ) {
      // do nothing
    }
    return result;
  }

  @Override
  public String toString() {
    try {
      return message.toString( 2 );
    } catch( JSONException e ) {
      throw new RuntimeException( "Formatting failed" );
    }
  }

  private void processOperations( JSONArray operations ) throws JSONException {
    for( int i = 0; i < operations.length(); i++ ) {
      Operation operation = createOperation( operations.getJSONArray( i ) );
      appendOperation( operation );
    }
  }

  private Operation createOperation( JSONArray data ) {
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

  private String getOperationAction( JSONArray operation ) {
    String result;
    try {
      result = operation.getString( 0 );
    } catch( JSONException e ) {
      throw new IllegalArgumentException( "Could not find action for operation " + operation );
    }
    return result;
  }

  @SuppressWarnings( "unchecked" )
  private <T> T[] getOperations( Class<T> opClass, String target, String property ) {
    List<T> result = new ArrayList<T>();
    List<Operation> operations = operationsMap.get( target );
    if( operations != null ) {
      for( Operation operation : operations ) {
        if(    opClass.isInstance( operation )
            && ( property == null || operation.getPropertyNames().contains( property ) ) ) {
          result.add( ( T )operation );
        }
      }
    }
    return result.toArray( ( T[] )Array.newInstance( opClass, 0 ) );
  }

  public abstract class Operation {

    private final String target;

    private Operation( JSONArray operation ) {
      try {
        target = operation.getString( 1 );
      } catch( JSONException e ) {
        throw new IllegalArgumentException( "Invalid operation target", e );
      }
    }

    public String getTarget() {
      return target;
    }

    @SuppressWarnings( "unchecked" )
    public List<String> getPropertyNames() {
      JSONObject properties = getProperties();
      String[] names = JSONObject.getNames( properties );
      return names == null ? Collections.EMPTY_LIST : Arrays.asList( names );
    }

    public Object getProperty( String key ) {
      Object result = null;
      JSONObject properties = getProperties();
      try {
        Object value = properties.get( key );
        if( value instanceof JSONObject ) {
          result = JsonUtil.jsonToJava( ( JSONObject )value );
        } else if( value instanceof JSONArray ) {
          result = JsonUtil.jsonToJava( ( JSONArray )value );
        } else {
          result = value;
        }
      } catch( JSONException exception ) {
        // do nothing
      }
      return result;
    }

    abstract protected JSONObject getProperties();

  }

  public final class SetOperation extends Operation {

    private final JSONObject properties;

    private SetOperation( JSONArray operation ) {
      super( operation );
      try {
        properties = operation.getJSONObject( 2 );
      } catch( JSONException e ) {
        throw new IllegalArgumentException( "Properties object missing in operation", e );
      }
    }

    @Override
    protected JSONObject getProperties() {
      return properties;
    }

  }

  public final class NotifyOperation extends Operation {

    private final String eventName;
    private final JSONObject properties;

    private NotifyOperation( JSONArray operation ) {
      super( operation );
      try {
        eventName = operation.getString( 2 );
      } catch( JSONException e ) {
        throw new IllegalArgumentException( "Event type missing in operation", e );
      }
      try {
        properties = operation.getJSONObject( 3 );
      } catch( JSONException e ) {
        throw new IllegalArgumentException( "Properties object missing in operation", e );
      }
    }

    public String getEventName() {
      return eventName;
    }

    @Override
    protected JSONObject getProperties() {
      return properties;
    }

  }

  public final class CallOperation extends Operation {

    private final String methodName;
    private final JSONObject properties;

    private CallOperation( JSONArray operation ) {
      super( operation );
      try {
        methodName = operation.getString( 2 );
      } catch( JSONException e ) {
        throw new IllegalArgumentException( "Method name missing in operation", e );
      }
      try {
        properties = operation.getJSONObject( 3 );
      } catch( JSONException e ) {
        throw new IllegalArgumentException( "Properties object missing in operation", e );
      }
    }

    public String getMethodName() {
      return methodName;
    }

    @Override
    protected JSONObject getProperties() {
      return properties;
    }

  }

}
