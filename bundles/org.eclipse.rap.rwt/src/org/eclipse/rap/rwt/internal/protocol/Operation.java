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
package org.eclipse.rap.rwt.internal.protocol;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.rap.rwt.internal.theme.JsonArray;
import org.eclipse.rap.rwt.internal.theme.JsonValue;


final class Operation {

  private final String action;
  private final String target;
  private final Map<String, Object> details;
  private final Map<String, JsonValue> properties;

  Operation( String target, String action ) {
    this.target = target;
    this.action = action;
    details = new LinkedHashMap<String, Object>();
    properties = new LinkedHashMap<String, JsonValue>();
  }

  String getTarget() {
    return target;
  }

  String getAction() {
    return action;
  }

  void appendProperty( String key, JsonValue value ) {
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

  Object getDetail( String key ) {
    return details.get( key );
  }

  void appendDetail( String key, Object value ) {
    if( details.containsKey( key ) ) {
      throw new IllegalArgumentException( "Duplicate detail " + key );
    }
    replaceDetail( key, value );
  }

  void replaceDetail( String key, Object value ) {
    details.put( key, value );
  }

  JsonValue toJson() {
    JsonArray json = new JsonArray();
    json.append( action );
    json.append( target );
    if( !details.isEmpty() ) {
      Set<String> keySet = details.keySet();
      for( String key : keySet ) {
        json.append( JsonUtil.createJsonValue( details.get( key ) ) );
      }
    }
    if( !properties.isEmpty() ) {
      JsonValue jsonObject = JsonUtil.createJsonObject( properties );
      json.append( jsonObject );
    }
    return json;
  }
}
