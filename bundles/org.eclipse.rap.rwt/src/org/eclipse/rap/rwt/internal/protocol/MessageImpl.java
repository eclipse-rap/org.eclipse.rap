/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.protocol;

import static org.eclipse.rap.rwt.internal.protocol.OperationReader.readOperation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.util.ParamCheck;


public class MessageImpl implements Message {

  private final JsonObject head;
  private final List<Operation> operations;
  public static final String PROP_HEAD = "head";
  public static final String PROP_OPERATIONS = "operations";
  public static final String OPERATION_SET = "set";
  public static final String OPERATION_NOTIFY = "notify";
  public static final String OPERATION_CALL = "call";

  public MessageImpl() {
    head = new JsonObject();
    operations = new ArrayList<Operation>();
  }

  public MessageImpl( Message message ) {
    ParamCheck.notNull( message, "message" );
    head = message.getHead();
    operations = message.getOperations();
  }

  public MessageImpl( JsonObject json ) {
    ParamCheck.notNull( json, "json" );
    head = readHead( json );
    operations = readOperations( json );
  }

  public JsonObject getHead() {
    return head;
  }

  public List<Operation> getOperations() {
    return operations;
  }

  @Override
  public String toString() {
    return toJson().toString();
  }

  private JsonObject toJson() {
    JsonArray operationsArray = new JsonArray();
    for( Operation operation : operations ) {
      operationsArray.add( operation.toJson() );
    }
    return new JsonObject().add( "head", head ).add( "operations", operationsArray );
  }

  private static JsonObject readHead( JsonObject message ) {
    try {
      return message.get( "head" ).asObject();
    } catch( Exception exception ) {
      throw new IllegalArgumentException( "Failed to read head from JSON message", exception );
    }
  }

  private static List<Operation> readOperations( JsonObject message ) {
    try {
      return processOperations( message.get( "operations" ).asArray() );
    } catch( Exception exception ) {
      throw new IllegalArgumentException( "Failed to read operations from JSON message", exception );
    }
  }

  private static List<Operation> processOperations( JsonArray operationsArray ) {
    List<Operation> operations = new ArrayList<Operation>( operationsArray.size() );
    for( JsonValue operation : operationsArray ) {
      operations.add( readOperation( operation.asArray() ) );
    }
    return operations;
  }

}
