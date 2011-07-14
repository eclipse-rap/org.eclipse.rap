/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.protocol;

import static org.eclipse.rwt.internal.protocol.ProtocolConstants.CREATE_PARENT;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.CREATE_STYLE;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.CREATE_TYPE;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.CALL_METHOD_NAME;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.EXECUTE_SCRIPT_CONTENT;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.EXECUTE_SCRIPT_TYPE;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.OPERATIONS;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.OPERATION_ACTION;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.OPERATION_PROPERTIES;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.OPERATION_TARGET;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.ACTION_CREATE;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.ACTION_DESTROY;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.ACTION_CALL;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.ACTION_EXECUTE_SCRIPT;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.ACTION_LISTEN;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.ACTION_SET;

import org.json.*;


public final class Message {

  private JSONArray operations;
  
  public Message( String json ) {
    JSONObject jsonObject;
    try {
      jsonObject = new JSONObject( json );
    } catch( JSONException e ) {
      throw new IllegalArgumentException( "Could not parse json: " + json );
    }
    try {
      operations = jsonObject.getJSONArray( OPERATIONS );
    } catch( JSONException e ) {
      throw new IllegalArgumentException( "Missing operations array: " + json );
    }
  }

  public Operation getOperation( int position ) {
    Operation result;
    JSONObject operation = getOperationAsJson( position );
    String action = getOperationAction( operation );
    if( action.equals( ACTION_CREATE ) ) {
      result = new CreateOperation( operation );
    } else if( action.equals( ACTION_CALL ) ) {
      result = new CallOperation( operation );
    } else if( action.equals( ACTION_SET ) ) {
      result = new SetOperation( operation );
    } else if( action.equals( ACTION_LISTEN ) ) {
      result = new ListenOperation( operation );
    } else if( action.equals( ACTION_EXECUTE_SCRIPT ) ) {
      result = new ExecuteScriptOperation( operation );
    } else if( action.equals( ACTION_DESTROY ) ) {
      result = new DestroyOperation( operation );
    } else {
      throw new IllegalArgumentException( "Unknown operation action: " + action );
    }
    return result;
  }

  private JSONObject getOperationAsJson( int position ) {
    JSONObject result;
    try {
      result = operations.getJSONObject( position );
    } catch( JSONException e ) {
      throw new IllegalStateException( "Could not find operation at position " + position );
    }
    return result;
  }

  private String getOperationAction( JSONObject operation ) {
    String action;
    try {
      action = operation.getString( OPERATION_ACTION );
    } catch( JSONException e ) {
      throw new IllegalStateException( "Could not find action for operation " + operation );
    }
    return action;
  }

  public abstract class Operation {
    
    private String target;
    private JSONObject operation;
  
    private Operation( JSONObject operation ) {
      this.operation = operation;
      target = ( String )getDetail( OPERATION_TARGET );
    }
  
    public String getTarget() {
      return target;
    }
    
    public Object getProperty( String key ) {
      Object result;
      try {
        JSONObject properties = operation.getJSONObject( OPERATION_PROPERTIES );
        result = properties.get( key );
      } catch( JSONException e ) {
        throw new IllegalStateException( "Property does not exist for key: " + key );
      }
      return result;
    }

    protected Object getDetail( String key ) {
      Object result;
      try {
        result = operation.get( key );
      } catch( JSONException e ) {
        throw new IllegalStateException( "Value is not valid for key: " + key );
      }
      return result;
    }
  }

  public final class CreateOperation extends Operation {
  
    private CreateOperation( JSONObject operation ) {
      super( operation );
    }
  
    public String getParent() {
      return ( String )getProperty( CREATE_PARENT );
    }
  
    public String getType() {
      return ( String )getDetail( CREATE_TYPE );
    }

    public Object[] getStyles() {
      Object detail = getProperty( CREATE_STYLE );
      Object[] result = null;
      if( !detail.equals( JSONObject.NULL ) ) {
        JSONArray parameters = ( JSONArray )detail;
        result = new Object[ parameters.length() ];
        for( int i = 0; i < parameters.length(); i++ ) {
          try {
            result[ i ] = parameters.get( i );
          } catch( JSONException e ) {
            String message = "Style array is not valid for operation ";
            throw new IllegalStateException( message );
          }
        }
      }
      return result;
    }
  }

  public final class CallOperation extends Operation {
  
    private CallOperation( JSONObject operation ) {
      super( operation );
    }
  
    public String getMethodName() {
      return ( String )getDetail( CALL_METHOD_NAME );
    }
  }

  public final class SetOperation extends Operation {
  
    private SetOperation( JSONObject operation ) {
      super( operation );
    }
  }

  public final class ListenOperation extends Operation {
  
    private ListenOperation( JSONObject operation ) {
      super( operation );
    }
  
    public boolean listensTo( String eventName ) {
      return ( ( Boolean )getProperty( eventName ) ).booleanValue();
    }
  }

  public final class ExecuteScriptOperation extends Operation {
  
    private ExecuteScriptOperation( JSONObject operation ) {
      super( operation );
    }
  
    public String getScriptType() {
      return ( String )getDetail( EXECUTE_SCRIPT_TYPE );
    }
  
    public String getScript() {
      return ( String )getDetail( EXECUTE_SCRIPT_CONTENT );
    }
  }

  public final class DestroyOperation extends Operation {
  
    private DestroyOperation( JSONObject operation ) {
      super( operation );
    }
  }
}
