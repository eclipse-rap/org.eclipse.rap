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

import static org.eclipse.rwt.internal.protocol.ProtocolConstants.OPERATION_DETAILS;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.OPERATION_TARGET;
import static org.eclipse.rwt.internal.protocol.ProtocolConstants.OPERATION_TYPE;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.rwt.internal.theme.JsonObject;
import org.eclipse.rwt.internal.theme.JsonValue;


final class Operation {

  private final String type;
  private final String target;
  private Map<String, Object> details;

  Operation( String target, String type ) {
    this.target = target;
    this.type = type;
    details = new LinkedHashMap<String, Object>();
  }
  
  boolean matches( String target, String type ) {
    return target.equals( this.target ) && type.equals( this.type );
  }

  void appendProperty( String key, JsonValue value ) {
    if( details.containsKey( key ) ) {
      throw new IllegalArgumentException( "Duplicate property " + key );
    }
    details.put( key, value );
  }

  JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.append( OPERATION_TARGET, target );
    json.append( OPERATION_TYPE, type );
    if( details.isEmpty() ) {
      json.append( OPERATION_DETAILS, JsonValue.NULL );
    } else {
      json.append( OPERATION_DETAILS, JsonUtil.createJsonObject( details ) );
    }
    return json;
  }
}