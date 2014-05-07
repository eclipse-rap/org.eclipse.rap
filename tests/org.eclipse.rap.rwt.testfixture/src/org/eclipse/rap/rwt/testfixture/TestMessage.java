/*******************************************************************************
 * Copyright (c) 2011, 2014 EclipseSource and others.
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
import org.eclipse.rap.rwt.internal.protocol.Operation;
import org.eclipse.rap.rwt.internal.protocol.OperationReader;
import org.eclipse.rap.rwt.internal.protocol.Operation.CallOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.DestroyOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.ListenOperation;
import org.eclipse.rap.rwt.internal.protocol.Operation.SetOperation;
import org.eclipse.rap.rwt.internal.util.ParamCheck;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.swt.widgets.Widget;


/**
 * <strong>IMPORTANT:</strong> This class is <em>not</em> part the public RAP
 * API. It may change or disappear without further notice. Use this class at
 * your own risk.
 */
public final class TestMessage {

  private final JsonObject message;
  private final List<Operation> operations;

  public TestMessage( JsonObject json ) {
    ParamCheck.notNull( json, "json" );
    message = json;
    try {
      operations = readOperations( message.get( "operations" ).asArray() );
    } catch( Exception exception ) {
      throw new IllegalArgumentException( "Missing operations array: " + json, exception );
    }
  }

  private List<Operation> readOperations( JsonArray operationsArray ) {
    ArrayList<Operation> operations = new ArrayList<Operation>( operationsArray.size() );
    for( JsonValue operation : operationsArray ) {
      operations.add( OperationReader.readOperation( operation ) );
    }
    return operations;
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
    return operations.get( position );
  }

  public JsonValue findSetProperty( Widget widget, String property ) {
    return findSetProperty( getId( widget ), property );
  }

  public JsonValue findSetProperty( String target, String property ) {
    SetOperation operation = findSetOperation( target, property );
    if( operation == null ) {
      throw new IllegalStateException( "operation not found" );
    }
    return operation.getProperties().get( property );
  }

  public SetOperation findSetOperation( Widget widget, String property ) {
    return findSetOperation( getId( widget ), property );
  }

  public ListenOperation findListenOperation( Widget widget, String property ) {
    return findListenOperation( getId( widget ), property );
  }

  public ListenOperation findListenOperation( String target, String property ) {
    ListenOperation result = null;
    for( Operation operation : getOperations() ) {
      if( operation instanceof ListenOperation && operation.getTarget().equals( target ) ) {
        ListenOperation listenOperation = ( ListenOperation )operation;
        if( property == null || listenOperation.getProperties().get( property ) != null ) {
          result = listenOperation;
        }
      }
    }
    return result;
  }

  public JsonValue findListenProperty( Widget widget, String property ) {
    return findListenProperty( getId( widget ), property );
  }

  @Deprecated
  public JsonValue findListenProperty( String target, String property ) {
    ListenOperation operation = findListenOperation( target, property );
    if( operation == null ) {
      throw new IllegalStateException( "operation not found" );
    }
    return operation.getProperties().get( property );
  }

  public CreateOperation findCreateOperation( Widget widget ) {
    return findCreateOperation( getId( widget ) );
  }

  public JsonValue findCreateProperty( Widget widget, String property ) {
    return findCreateProperty( getId( widget ), property );
  }

  public JsonValue findCreateProperty( String target, String property ) {
    CreateOperation operation = findCreateOperation( target );
    if( operation == null || !operation.getProperties().names().contains( property ) ) {
      throw new IllegalStateException( "operation not found" );
    }
    return operation.getProperties().get( property );
  }

  public CreateOperation findCreateOperation( String target ) {
    CreateOperation result = null;
    for( Operation operation : getOperations() ) {
      if( operation instanceof CreateOperation && operation.getTarget().equals( target ) ) {
        CreateOperation createOperation = ( CreateOperation )operation;
        result = createOperation;
      }
    }
    return result;
  }

  public DestroyOperation findDestroyOperation( Widget widget ) {
    DestroyOperation result = null;
    Object target = WidgetUtil.getId( widget );
    for( Operation operation : getOperations() ) {
      if( operation instanceof DestroyOperation && operation.getTarget().equals( target ) ) {
        DestroyOperation destroyOperation = ( DestroyOperation )operation;
        result = destroyOperation;
      }
    }
    return result;
  }

  public SetOperation findSetOperation( String target, String property ) {
    SetOperation result = null;
    for( Operation operation : getOperations() ) {
      if( operation instanceof SetOperation && operation.getTarget().equals( target ) ) {
        SetOperation setOperation = ( SetOperation )operation;
        if( property == null || setOperation.getProperties().get( property ) != null ) {
          result = setOperation;
        }
      }
    }
    return result;
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
      TestMessage other = ( TestMessage )obj;
      equals = message.equals( other.message );
    }
    return equals;
  }

  public List<Operation> getOperations() {
    return operations;
  }

  public static String getParent( CreateOperation operation ) {
    JsonValue value = operation.getProperties().get( "parent" );
    return value == null ? null : value.asString();
  }

  public static List<String> getStyles( CreateOperation operation ) {
    JsonValue value = operation.getProperties().get( "style" );
    if( value == null ) {
      return null;
    }
    JsonArray styles = value.asArray();
    List<String> result = new ArrayList<String>( styles.size() );
    for( JsonValue style : styles ) {
      result.add( style.asString() );
    }
    return result;
  }

}
