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
package org.eclipse.rwt.internal.protocol.util;

import static org.eclipse.rwt.internal.protocol.ProtocolConstants.CREATE_PARENT;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.CREATE_STYLE;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.CREATE_TYPE;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.DO_NAME;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.EXECUTE_SCRIPT_CONTENT;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.EXECUTE_SCRIPT_TYPE;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.MESSAGE_OPERATIONS;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.OPERATION_DETAILS;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.OPERATION_TARGET;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.OPERATION_TYPE;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.PARAMETER;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.TYPE_CREATE;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.TYPE_DESTROY;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.TYPE_DO;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.TYPE_EXECUTE_SCRIPT;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.TYPE_LISTEN;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.TYPE_SET;

import org.json.*;

public final class Message {

  private JSONArray operations;
  
  public Message( String json ) {
    try {
      JSONObject jsonObject = new JSONObject( json );
      operations = jsonObject.getJSONArray( MESSAGE_OPERATIONS );
    } catch( JSONException e ) {
      throw new IllegalStateException( "Could not parse json: " + json );
    }
  }

  public Operation getOperation( int position ) {
    Operation result = null;
    JSONObject operation = getOperationAsJson( position );
    String type = getOperationType( operation );
    if( type.equals( TYPE_CREATE ) ) {
      result = new CreateOperation( operation );
    } else if( type.equals( TYPE_DO ) ) {
      result = new DoOperation( operation );
    } else if( type.equals( TYPE_SET ) ) {
      result = new SetOperation( operation );
    } else if( type.equals( TYPE_LISTEN ) ) {
      result = new ListenOperation( operation );
    } else if( type.equals( TYPE_EXECUTE_SCRIPT ) ) {
      result = new ExecuteScriptOperation( operation );
    } else if( type.equals( TYPE_DESTROY ) ) {
      result = new DestroyOperation( operation );
    }
    return result;
  }

  private JSONObject getOperationAsJson( int position ) {
    JSONObject result = null;
    try {
      result = operations.getJSONObject( position );
    } catch( JSONException e ) {
      throw new IllegalStateException( "Could not find operation at position " + position );
    }
    return result;
  }

  private String getOperationType( JSONObject operation ) {
    String type = null;
    try {
      type = operation.getString( OPERATION_TYPE );
    } catch( JSONException e ) {
      throw new IllegalStateException( "Could not find type for operation " + operation );
    }
    return type;
  }

  public class Operation {
    
    private String target;
    private JSONObject operation;
  
    private Operation( JSONObject operation ) {
      this.operation = operation;
      target = ( String )getValue( OPERATION_TARGET );
    }
  
    public String getTarget() {
      return target;
    }
    
    protected Object getDetail( String key ) {
      Object result = null;
      try {
        JSONObject details = operation.getJSONObject( OPERATION_DETAILS );
        result = details.get( key );
      } catch( JSONException e ) {
        throw new IllegalStateException( "Detail does not exist for key: " + key );
      }
      return result;
    }
    
    protected Object getValue( String key ) {
      Object result = null;
      try {
        result = operation.get( key );
      } catch( JSONException e ) {
        throw new IllegalStateException( "Value is not valid for key: " + key );
      }
      return result;
    }
  }

  private class OperationWithParameters extends Operation {
  
    private OperationWithParameters( JSONObject operation ) {
      super( operation );
    }
    
    public Object[] getParameters() {
      return getParameters( PARAMETER );
    }
    
    protected Object[] getParameters( String key) {
      Object detail = getDetail( key );
      Object[] result = null;
      if( !detail.equals( JSONObject.NULL ) ) {
        JSONArray parameters = ( JSONArray )detail;
        result = new Object[ parameters.length() ];
        for( int i = 0; i < parameters.length(); i++ ) {
          try {
            result[ i ] = parameters.get( i );
          } catch( JSONException e ) {
            String message = "Parameter array is not valid for operation ";
            throw new IllegalStateException( message );
          }
        }
      }
      return result;
    }
  }

  public final class CreateOperation extends OperationWithParameters {
  
    private CreateOperation( JSONObject operation ) {
      super( operation );
    }
  
    public String getParent() {
      return ( String )getDetail( CREATE_PARENT );
    }
  
    public String getType() {
      return ( String )getDetail( CREATE_TYPE );
    }

    public Object[] getStyles() {
      return getParameters( CREATE_STYLE );
    }
  }

  public final class DoOperation extends OperationWithParameters {
  
    private DoOperation( JSONObject operation ) {
      super( operation );
    }
  
    public String getName() {
      return ( String )getDetail( DO_NAME );
    }
  }

  public final class SetOperation extends Operation {
  
    private SetOperation( JSONObject operation ) {
      super( operation );
    }
  
    public Object getProperty( String key ) {
      return getDetail( key );
    }
  }

  public final class ListenOperation extends Operation {
  
    private ListenOperation( JSONObject operation ) {
      super( operation );
    }
  
    public boolean listensTo( String eventName ) {
      return ( ( Boolean )getDetail( eventName ) ).booleanValue();
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
