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

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;


final class Operation {

  private final String action;
  private final String target;
  private final String detail;
  private final JsonObject properties;

  Operation( String target, String action, String detail, JsonObject properties ) {
    this.target = target;
    this.action = action;
    this.detail = detail;
    this.properties = properties == null ? new JsonObject() : properties;
  }

  String getTarget() {
    return target;
  }

  String getAction() {
    return action;
  }

  void putProperty( String key, JsonValue value ) {
    properties.remove( key );
    properties.add( key, value );
  }

  JsonValue toJson() {
    JsonArray json = new JsonArray();
    json.add( action );
    json.add( target );
    if( detail != null ) {
      json.add( detail );
    }
    if( properties != null && !properties.isEmpty() ) {
      json.add( properties );
    }
    return json;
  }

}
