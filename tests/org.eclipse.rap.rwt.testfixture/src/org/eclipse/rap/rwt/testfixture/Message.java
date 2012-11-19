/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.testfixture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.widgets.Widget;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * <strong>IMPORTANT:</strong> This class is <em>not</em> part the public RAP
 * API. It may change or disappear without further notice. Use this class at
 * your own risk.
 */
public final class Message {

  private JSONObject message;
  private JSONArray operations;

  public Message( String string ) {
    String json = string.trim();
    try {
      message = new JSONObject( json );
    } catch( JSONException e ) {
      throw new IllegalArgumentException( "Could not parse json: " + json );
    }
    try {
      operations = message.getJSONArray( "operations" );
    } catch( JSONException e ) {
      throw new IllegalArgumentException( "Missing operations array: " + json );
    }
  }

  @Override
  public String toString() {
    try {
      return message.toString( 2 );
    } catch( JSONException e ) {
      throw new RuntimeException( "Formatting failed" );
    }
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
    return operations.length();
  }

  public Operation getOperation( int position ) {
    Operation result;
    JSONArray operation = getOperationAsJson( position );
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
      return message.getJSONObject( "head" ).get( property );
    } catch( JSONException e ) {
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

  private JSONArray getOperationAsJson( int position ) {
    JSONArray result;
    try {
      result = operations.getJSONArray( position );
    } catch( JSONException e ) {
      throw new IllegalStateException( "Could not find operation at position " + position );
    }
    return result;
  }

  private String getOperationAction( JSONArray operation ) {
    String action;
    try {
      action = operation.getString( 0 );
    } catch( JSONException e ) {
      throw new IllegalStateException( "Could not find action for operation " + operation );
    }
    return action;
  }

  public abstract class Operation {

    private final String target;
    private final int position;
    protected final JSONArray operation;

    private Operation( JSONArray operation, int position ) {
      this.operation = operation;
      this.position = position;
      try {
        target = operation.getString( 1 );
      } catch( JSONException e ) {
        throw new IllegalStateException( "Invalid operation target", e );
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
      Object result;
      JSONObject properties = getProperties();
      try {
        result = properties.get( key );
      } catch( JSONException exception ) {
        throw new IllegalStateException( "Property does not exist for key: " + key );
      }
      return result;
    }

    public int getPosition() {
      return position;
    }

    abstract protected JSONObject getProperties();

  }

  public final class CreateOperation extends Operation {

    private CreateOperation( JSONArray operation, int position ) {
      super( operation, position );
    }

    public String getParent() {
      return ( String )getProperty( "parent" );
    }

    public String getType() {
      String result;
      try {
        result = operation.getString( 2 );
      } catch( JSONException e ) {
        throw new IllegalStateException( "Invalid create operation type", e );
      }
      return result;
    }

    @Override
    protected JSONObject getProperties() {
      JSONObject properties;
      try {
        properties = operation.getJSONObject( 3 );
      } catch( JSONException e ) {
        throw new IllegalStateException( "Properties object missing in operation", e );
      }
      return properties;
    }

    public Object[] getStyles() {
      Object detail = getProperty( "style" );
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

    private CallOperation( JSONArray operation, int position ) {
      super( operation, position );
    }

    public String getMethodName() {
      String result;
      try {
        result = operation.getString( 2 );
      } catch( JSONException e ) {
        throw new IllegalStateException( "Invalid call operation method name", e );
      }
      return result;
    }

    @Override
    protected JSONObject getProperties() {
      JSONObject properties;
      try {
        properties = operation.getJSONObject( 3 );
      } catch( JSONException e ) {
        throw new IllegalStateException( "Properties object missing in operation", e );
      }
      return properties;
    }

  }

  public final class SetOperation extends Operation {

    private SetOperation( JSONArray operation, int position ) {
      super( operation, position );
    }

    @Override
    protected JSONObject getProperties() {
      JSONObject properties;
      try {
        properties = operation.getJSONObject( 2 );
      } catch( JSONException e ) {
        throw new IllegalStateException( "Properties object missing in operation", e );
      }
      return properties;
    }

  }

  public final class ListenOperation extends Operation {

    private ListenOperation( JSONArray operation, int position ) {
      super( operation, position );
    }

    public boolean listensTo( String eventName ) {
      return ( ( Boolean )getProperty( eventName ) ).booleanValue();
    }

    @Override
    protected JSONObject getProperties() {
      JSONObject properties;
      try {
        properties = operation.getJSONObject( 2 );
      } catch( JSONException e ) {
        throw new IllegalStateException( "Properties object missing in operation", e );
      }
      return properties;
    }

  }

  public final class DestroyOperation extends Operation {

    private DestroyOperation( JSONArray operation, int position ) {
      super( operation, position );
    }

    @Override
    protected JSONObject getProperties() {
      throw new IllegalStateException( "Destroy operation has no properties" );
    }

  }

}
