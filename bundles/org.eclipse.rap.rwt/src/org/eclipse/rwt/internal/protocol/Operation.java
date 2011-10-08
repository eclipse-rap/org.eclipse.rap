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

  String getTarget() {
    return target;
  }

  String getAction() {
    return action;
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
    json.append( ProtocolConstants.OPERATION_TARGET, target );
    json.append( ProtocolConstants.OPERATION_ACTION, action );
    if( !details.isEmpty() ) {
      Set<String> keySet = details.keySet();
      for( String key : keySet ) {
        json.append( key, JsonUtil.createJsonValue( details.get( key ) ) );
      }
    }
    if( !properties.isEmpty() ) {
      JsonValue jsonObject = JsonUtil.createJsonObject( properties );
      json.append( ProtocolConstants.OPERATION_PROPERTIES, jsonObject );
    }
    return json;
  }
}
