/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.testfixture;

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.util.ParamCheck;
import org.eclipse.swt.widgets.Widget;


/**
 * <strong>IMPORTANT:</strong> This class is <em>not</em> part the public RAP
 * API. It may change or disappear without further notice. Use this class at
 * your own risk.
 */
public final class Message {

  private final JsonObject message;
  private final JsonArray operations;

  public Message( JsonObject json ) {
    ParamCheck.notNull( json, "json" );
    message = json;
    try {
      operations = message.get( "operations" ).asArray();
    } catch( Exception e ) {
      throw new IllegalArgumentException( "Missing operations array: " + json );
    }
  }

  public JsonObject getHead() {
    return message.get( "head" ).asObject();
  }

  public int getRequestCounter() {
    return message.get( "head" ).asObject().get( "requestCounter" ).asInt();
  }

  public String getError() {
    return message.get( "head" ).asObject().get( "error" ).asString();
  }

  public String getErrorMessage() {
    return message.get( "head" ).asObject().get( "message" ).asString();
  }

  public int getOperationCount() {
    return operations.size();
  }

  public Operation getOperation( int position ) {
    Operation result;
    JsonArray operation = getOperationAsJson( position );
    String action = getOperationAction( operation );
    if( action.equals( "create" ) ) {
      result = new CreateOperation( operation, position );
    } else if( action.equals( "call" ) ) {
      result = new CallOperation( operation, position );
    } else if( action.equals( "set" ) ) {
      result = new SetOperation( operation, position );
    } else if( action.equals( "listen" ) ) {
      result = new ListenOperation( operation, position );
    } else if( action.equals( "destroy" ) ) {
      result = new DestroyOperation( operation, position );
    } else {
      throw new IllegalArgumentException( "Unknown operation action: " + action );
    }
    return result;
  }

  public JsonValue findSetProperty( Widget widget, String property ) {
    return findSetProperty( getId( widget ), property );
  }

  public JsonValue findSetProperty( String target, String property ) {
    SetOperation operation = findSetOperation( target, property );
    if( operation == null ) {
      throw new IllegalStateException( "operation not found" );
    }
    return operation.getProperty( property );
  }

  public SetOperation findSetOperation( Widget widget, String property ) {
    return findSetOperation( getId( widget ), property );
  }

  public ListenOperation findListenOperation( Widget widget, String property ) {
    return findListenOperation( getId( widget ), property );
  }

  public ListenOperation findListenOperation( String target, String property ) {
    return ( ListenOperation )findOperation( ListenOperation.class, target, property );
  }

  public JsonValue findListenProperty( Widget widget, String property ) {
    return findListenProperty( getId( widget ), property );
  }

  public JsonValue findListenProperty( String target, String property ) {
    ListenOperation operation = findListenOperation( target, property );
    if( operation == null ) {
      throw new IllegalStateException( "operation not found" );
    }
    return operation.getProperty( property );
  }

  public CreateOperation findCreateOperation( Widget widget ) {
    return findCreateOperation( getId( widget ) );
  }

  public JsonValue findCreateProperty( Widget widget, String property ) {
    return findCreateProperty( getId( widget ), property );
  }

  public JsonValue findCreateProperty( String target, String property ) {
    CreateOperation operation = findCreateOperation( target );
    if( operation == null || operation.getPropertyNames().indexOf( property ) == -1 ) {
      throw new IllegalStateException( "operation not found" );
    }
    return operation.getProperty( property );
  }

  public CreateOperation findCreateOperation( String target ) {
    return ( CreateOperation )findOperation( CreateOperation.class, target );
  }

  public DestroyOperation findDestroyOperation( Widget widget ) {
    return ( DestroyOperation )findOperation( DestroyOperation.class, getId( widget ) );
  }

  public SetOperation findSetOperation( String target, String property ) {
    return ( SetOperation )findOperation( SetOperation.class , target, property );
  }

  public CallOperation findCallOperation( Widget widget, String method ) {
    return findCallOperation( getId( widget ), method );
  }

  public CallOperation findCallOperation( String target, String method ) {
    CallOperation result = null;
    List<Operation> operations = getOperations();
    for( Operation operation : operations ) {
      if( operation.getTarget().equals( target ) && operation instanceof CallOperation ) {
        if( method.equals( ( ( CallOperation )operation ).getMethodName() ) ) {
          result = ( CallOperation )operation;
        }
      }
    }
    return result;
  }

  @Override
  public String toString() {
    return message.toString();
  }

  @Override
  public int hashCode() {
    return message.hashCode();
  }

  @Override
  public boolean equals( Object obj ) {
    boolean equals = false;
    if( this == obj ) {
      equals = true;
    } else if( obj != null && getClass() == obj.getClass() ) {
      Message other = ( Message )obj;
      equals = message.equals( other.message );
    }
    return equals;
  }

  private List<Operation> getOperations() {
    List<Operation> result = new ArrayList<Operation>();
    for( int i = 0; i < getOperationCount(); i++ ) {
      result.add( getOperation( i ) );
    }
    return result;
  }

  private Operation findOperation( Class opClass, String target ) {
    return findOperation( opClass, target, null );
  }

  private Operation findOperation( Class opClass, String target, String property ) {
    Operation result = null;
    List<Operation> operations = getOperations();
    for( Operation operation : operations ) {
      if(    operation.getTarget().equals( target )
          && opClass.isInstance( operation )
          && ( property == null || operation.getPropertyNames().contains( property ) ) )
      {
        result = operation;
      }
    }
    return result;
  }

  private JsonArray getOperationAsJson( int position ) {
    JsonArray result;
    try {
      result = operations.get( position ).asArray();
    } catch( Exception e ) {
      throw new IllegalStateException( "Could not find operation at position " + position );
    }
    return result;
  }

  private String getOperationAction( JsonArray operation ) {
    String action;
    try {
      action = operation.get( 0 ).asString();
    } catch( Exception e ) {
      throw new IllegalStateException( "Could not find action for operation " + operation );
    }
    return action;
  }

  public abstract class Operation {

    private final String target;
    private final int position;
    protected final JsonArray operation;

    private Operation( JsonArray operation, int position ) {
      this.operation = operation;
      this.position = position;
      try {
        target = operation.get( 1 ).asString();
      } catch( Exception e ) {
        throw new IllegalStateException( "Invalid operation target", e );
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

    public int getPosition() {
      return position;
    }

    abstract protected JsonObject getProperties();

  }

  public final class CreateOperation extends Operation {

    private CreateOperation( JsonArray operation, int position ) {
      super( operation, position );
    }

    public String getParent() {
      return getProperty( "parent" ).asString();
    }

    public String getType() {
      String result;
      try {
        result = operation.get( 2 ).asString();
      } catch( Exception e ) {
        throw new IllegalStateException( "Invalid create operation type", e );
      }
      return result;
    }

    @Override
    protected JsonObject getProperties() {
      JsonObject properties;
      try {
        properties = operation.get( 3 ).asObject();
      } catch( Exception e ) {
        throw new IllegalStateException( "Properties object missing in operation", e );
      }
      return properties;
    }

    public Object[] getStyles() {
      Object[] result = null;
      JsonValue detail = getProperty( "style" );
      if( detail != null ) {
        JsonArray parameters = detail.asArray();
        result = new Object[ parameters.size() ];
        for( int i = 0; i < result.length; i++ ) {
          try {
            result[ i ] = parameters.get( i ).asString();
          } catch( Exception e ) {
            String message = "Style array is not valid for operation ";
            throw new IllegalStateException( message );
          }
        }
      }
      return result;
    }
  }

  public final class CallOperation extends Operation {

    private CallOperation( JsonArray operation, int position ) {
      super( operation, position );
    }

    public String getMethodName() {
      String result;
      try {
        result = operation.get( 2 ).asString();
      } catch( Exception e ) {
        throw new IllegalStateException( "Invalid call operation method name", e );
      }
      return result;
    }

    @Override
    protected JsonObject getProperties() {
      JsonObject properties;
      try {
        properties = operation.get( 3 ).asObject();
      } catch( Exception e ) {
        throw new IllegalStateException( "Properties object missing in operation", e );
      }
      return properties;
    }

  }

  public final class SetOperation extends Operation {

    private SetOperation( JsonArray operation, int position ) {
      super( operation, position );
    }

    @Override
    protected JsonObject getProperties() {
      JsonObject properties;
      try {
        properties = operation.get( 2 ).asObject();
      } catch( Exception e ) {
        throw new IllegalStateException( "Properties object missing in operation", e );
      }
      return properties;
    }

  }

  public final class ListenOperation extends Operation {

    private ListenOperation( JsonArray operation, int position ) {
      super( operation, position );
    }

    public boolean listensTo( String eventName ) {
      return getProperty( eventName ).asBoolean();
    }

    @Override
    protected JsonObject getProperties() {
      JsonObject properties;
      try {
        properties = operation.get( 2 ).asObject();
      } catch( Exception e ) {
        throw new IllegalStateException( "Properties object missing in operation", e );
      }
      return properties;
    }

  }

  public final class DestroyOperation extends Operation {

    private DestroyOperation( JsonArray operation, int position ) {
      super( operation, position );
    }

    @Override
    protected JsonObject getProperties() {
      throw new IllegalStateException( "Destroy operation has no properties" );
    }

  }

}
