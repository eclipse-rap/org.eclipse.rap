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

import static org.eclipse.rap.rwt.internal.json.JsonUtil.jsonToJava;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.rwt.internal.json.JsonArray;
import org.eclipse.rap.rwt.internal.json.JsonObject;
import org.eclipse.rap.rwt.internal.json.JsonValue;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.widgets.Widget;


/**
 * <strong>IMPORTANT:</strong> This class is <em>not</em> part the public RAP
 * API. It may change or disappear without further notice. Use this class at
 * your own risk.
 */
public final class Message {

  private JsonObject message;
  private JsonArray operations;

  public Message( String string ) {
    String json = string.trim();
    try {
      message = JsonObject.readFrom( json );
    } catch( Exception e ) {
      throw new IllegalArgumentException( "Could not parse json: " + json );
    }
    try {
      operations = message.get( "operations" ).asArray();
    } catch( Exception e ) {
      throw new IllegalArgumentException( "Missing operations array: " + json );
    }
  }

  @Override
  public String toString() {
    return message.toString();
  }

  public int getRequestCounter() {
    return ( ( Integer )findHeadProperty( "requestCounter" ) ).intValue();
  }

  public String getError() {
    return findHeadProperty( "error" ).toString();
  }

  public String getErrorMessage() {
    return findHeadProperty( "message" ).toString();
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

  public Object findHeadProperty( String property ) {
    try {
      return jsonToJava( message.get( "head" ).asObject().get( property ) );
    } catch( Exception e ) {
      throw new RuntimeException( "Head property does not exist for key: " + property );
    }
  }

  public Object findSetProperty( Widget widget, String property ) {
    String target = WidgetUtil.getId( widget );
    return findSetProperty( target, property );
  }

  public Object findSetProperty( String target, String property ) {
    SetOperation operation = findSetOperation( target, property );
    if( operation == null ) {
      throw new IllegalStateException( "operation not found" );
    }
    return operation.getProperty( property );
  }

  public SetOperation findSetOperation( Widget widget, String property ) {
    String target = WidgetUtil.getId( widget );
    return findSetOperation( target, property );
  }

  public ListenOperation findListenOperation( Widget widget, String property ) {
    String target = WidgetUtil.getId( widget );
    return findListenOperation( target, property );
  }

  public ListenOperation findListenOperation( String target, String property ) {
    return ( ListenOperation )findOperation( ListenOperation.class, target, property );
  }

  public Object findListenProperty( Widget widget, String property ) {
    String target = WidgetUtil.getId( widget );
    return findListenProperty( target, property );
  }

  public Object findListenProperty( String target, String property ) {
    ListenOperation operation = findListenOperation( target, property );
    if( operation == null ) {
      throw new IllegalStateException( "operation not found" );
    }
    return operation.getProperty( property );
  }

  public CreateOperation findCreateOperation( Widget widget ) {
    String target = WidgetUtil.getId( widget );
    return findCreateOperation( target );
  }

  public Object findCreateProperty( Widget widget, String property ) {
    String target = WidgetUtil.getId( widget );
    return findCreateProperty( target, property );
  }

  public Object findCreateProperty( String target, String property ) {
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
    String target = WidgetUtil.getId( widget );
    return ( DestroyOperation )findOperation( DestroyOperation.class, target );
  }

  public SetOperation findSetOperation( String target, String property ) {
    return ( SetOperation )findOperation( SetOperation.class , target, property );
  }

  public CallOperation findCallOperation( Widget widget, String method ) {
    String target = WidgetUtil.getId( widget );
    return findCallOperation( target, method );
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

    public Object getProperty( String key ) {
      JsonValue value = getProperties().get( key );
      if( value == null ) {
        throw new IllegalStateException( "Property does not exist for key: " + key );
      }
      Object result = value;
      if( !value.isObject() && !value.isArray() ) {
        result = jsonToJava( value );
      }
      return result;
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
      return ( String )getProperty( "parent" );
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
      Object detail = getProperty( "style" );
      Object[] result = null;
      if( !detail.equals( JsonObject.NULL ) ) {
        JsonArray parameters = ( JsonArray )detail;
        result = new Object[ parameters.size() ];
        for( int i = 0; i < parameters.size(); i++ ) {
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
      return ( ( Boolean )getProperty( eventName ) ).booleanValue();
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
