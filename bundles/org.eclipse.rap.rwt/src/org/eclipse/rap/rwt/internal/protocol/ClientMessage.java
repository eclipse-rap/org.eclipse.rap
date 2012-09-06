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
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ClientMessage {

  private final JSONObject message;
  private HashMap<String,List<Operation>> operationsMap;

  public ClientMessage( String string ) {
    String json = string.trim();
    try {
      message = new JSONObject( json );
    } catch( JSONException e ) {
      throw new IllegalArgumentException( "Could not parse json: " + json );
    }
    JSONArray operations;
    try {
      operations = message.getJSONArray( "operations" );
    } catch( JSONException e ) {
      throw new IllegalArgumentException( "Missing operations array: " + json );
    }
    try {
      createOperationsMap( operations );
    } catch( JSONException e ) {
      throw new IllegalArgumentException( "Invalid operations array: " + json );
    }
  }

  public Operation[] getAllOperations( String target ) {
    return getOperations( Operation.class, target, null );
  }

  public SetOperation[] getSetOperations( String target ) {
    return getOperations( SetOperation.class, target, null );
  }

  public SetOperation[] getSetOperations( String target, String property ) {
    return getOperations( SetOperation.class, target, property );
  }

  public NotifyOperation[] getNotifyOperations( String target ) {
    return getOperations( NotifyOperation.class, target, null );
  }

  public NotifyOperation[] getNotifyOperations( String target, String eventName, String property ) {
    List<NotifyOperation> result = new ArrayList<NotifyOperation>();
    List<Operation> operations = operationsMap.get( target );
    if( operations != null ) {
      for( Operation operation : operations ) {
        if( operation instanceof NotifyOperation ) {
          NotifyOperation currentOperation = ( NotifyOperation )operation;
          if(    ( eventName == null || currentOperation.getEventName().equals( eventName ) )
              && ( property == null || currentOperation.getPropertyNames().contains( property ) ) )
          {
            result.add( currentOperation );
          }
        }
      }
    }
    return result.toArray( new NotifyOperation[ 0 ] );
  }

  public CallOperation[] getCallOperations( String target ) {
    return getOperations( CallOperation.class, target, null );
  }

  @Override
  public String toString() {
    try {
      return message.toString( 2 );
    } catch( JSONException e ) {
      throw new RuntimeException( "Formatting failed" );
    }
  }

  private void createOperationsMap( JSONArray operations ) throws JSONException {
    operationsMap = new HashMap<String,List<Operation>>();
    for( int i = 0; i < operations.length(); i++ ) {
      Operation operation = createOperation( operations.getJSONArray( i ) );
      appendOperation( operation );
    }
  }

  private Operation createOperation( JSONArray data ) {
    Operation result = null;
    String action = getOperationAction( data );
    if( action.equals( "set" ) ) {
      result = new SetOperation( data );
    } else if( action.equals( "notify" ) ) {
      result = new NotifyOperation( data );
    } else if( action.equals( "call" ) ) {
      result = new CallOperation( data );
    } else {
      throw new IllegalArgumentException( "Unknown operation action: " + action );
    }
    return result;
  }

  private void appendOperation( Operation operation ) {
    String target = operation.getTarget();
    List<Operation> operationsList = operationsMap.get( target );
    if( operationsList == null ) {
      operationsList = new ArrayList<Operation>();
    }
    operationsList.add( operation );
    operationsMap.put( target, operationsList );
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

    public List<String> getPropertyNames() {
      JSONObject properties = getProperties();
      String[] names = JSONObject.getNames( properties );
      return Arrays.asList( names );
    }

    public Object getProperty( String key ) {
      Object result = null;
      JSONObject properties = getProperties();
      try {
        result = properties.get( key );
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
