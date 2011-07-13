/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.protocol;

import static org.eclipse.rwt.internal.protocol.ProtocolConstants.OPERATION_ACTION;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.OPERATION_PROPERTIES;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.OPERATION_TARGET;

import java.util.*;

import org.eclipse.rwt.internal.theme.JsonObject;
import org.eclipse.rwt.internal.theme.JsonValue;


final class Operation {

  private final String action;
  private final String target;
  private Map<String, Object> details;
  private Map<String, Object> properties;

  Operation( String target, String action ) {
    this.target = target;
    this.action = action;
    details = new LinkedHashMap<String, Object>();
    properties = new LinkedHashMap<String, Object>();
  }
  
  boolean matches( String target, String action ) {
    return target.equals( this.target ) && action.equals( this.action );
  }

  void appendProperty( String key, JsonValue value ) {
    if( properties.containsKey( key ) ) {
      throw new IllegalArgumentException( "Duplicate property " + key );
    }
    properties.put( key, value );
  }
  
  void appendProperties( Map<String, Object> properties ) {
    if( properties != null && !properties.isEmpty() ) {
      Set<String> keySet = properties.keySet();
      for( String key : keySet ) {
        appendProperty( key, JsonUtil.createJsonValue( properties.get( key ) ) );
      }
    }
  }
  
  void appendDetail( String key, JsonValue value ) {
    if( details.containsKey( key ) ) {
      throw new IllegalArgumentException( "Duplicate detail " + key );
    }
    details.put( key, value );
  }

  JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.append( OPERATION_TARGET, target );
    json.append( OPERATION_ACTION, action );
    if( !details.isEmpty() ) {
      Set<String> keySet = details.keySet();
      for( String key : keySet ) {
        json.append( key, JsonUtil.createJsonValue( details.get( key ) ) );
      }
    }
    if( !properties.isEmpty() ) {
      json.append( OPERATION_PROPERTIES, JsonUtil.createJsonObject( properties ) );
    }
    return json;
  }
}