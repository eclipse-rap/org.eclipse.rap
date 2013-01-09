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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.internal.theme.JsonArray;
import org.eclipse.rap.rwt.internal.theme.JsonObject;
import org.eclipse.rap.rwt.internal.theme.JsonValue;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


final class JsonUtil {

  static JsonValue createJsonObject( Map properties ) {
    JsonValue result;
    if( properties != null ) {
      JsonObject object = new JsonObject();
      Object[] keys = properties.keySet().toArray();
      for( int i = 0; i < keys.length; i++ ) {
        String key = ( String )keys[ i ];
        object.append( key, createJsonValue( properties.get( key ) ) );
      }
      result = object;
    } else {
      result = JsonValue.NULL;
    }
    return result;
  }

  static JsonValue createJsonArray( Object[] values ) {
    JsonValue result;
    if( values != null ) {
      JsonArray array = new JsonArray();
      for( int i = 0; i < values.length; i++ ) {
        array.append( createJsonValue( values[ i ] ) );
      }
      result = array;
    } else {
      result = JsonValue.NULL;
    }
    return result;
  }

  static JsonValue createJsonValue( Object value ) {
    JsonValue result;
    if( value == null ) {
      result = JsonValue.NULL;
    } else if( value instanceof String ) {
      result = JsonObject.valueOf( ( String )value );
    } else if( value instanceof Byte ) {
      result = JsonObject.valueOf( ( ( Byte )value ).intValue() );
    } else if( value instanceof Short ) {
      result = JsonObject.valueOf( ( ( Short )value ).intValue() );
    } else if( value instanceof Integer ) {
      result = JsonObject.valueOf( ( ( Integer )value ).intValue() );
    } else if( value instanceof Long ) {
      result = JsonObject.valueOf( ( ( Long )value ).longValue() );
    } else if( value instanceof Double ) {
      result = JsonObject.valueOf( ( ( Double )value ).doubleValue() );
    } else if( value instanceof Float ) {
      result = JsonObject.valueOf( ( ( Float )value ).floatValue() );
    } else if( value instanceof Boolean ) {
      result = JsonObject.valueOf( ( ( Boolean )value ).booleanValue() );
    } else if( value instanceof int[] ) {
      result = JsonArray.valueOf( ( int[] )value );
    } else if( value instanceof boolean[] ) {
      result = JsonArray.valueOf( ( boolean[] )value );
    } else if( value instanceof Object[] ) {
      result = createJsonArray( ( Object[] )value );
    } else if( value instanceof Map ) {
      result = createJsonObject( ( Map )value );
    } else if( value instanceof JsonValue ) {
      result = ( JsonValue )value;
    } else {
      String message = "Parameter object can not be converted to JSON value: " + value;
      throw new IllegalArgumentException( message );
    }
    return result;
  }

  public static Object[] jsonToJava( JSONArray object ) {
    Object[] result = new Object[ object.length() ];
    try {
      for( int i = 0; i < result.length; i++ ) {
        result[ i ] = jsonToJava( object.get( i ) );
      }
    } catch( JSONException e ) {
      // do nothing - keep result as json string
    }
    return result;
  }

  public static Object jsonToJava( JSONObject object ) {
    Map<String,Object> result = new HashMap<String, Object>();
    String[] propertiyNames = JSONObject.getNames( object );
    for( int i = 0; i < propertiyNames.length; i++ ) {
      try {
        result.put( propertiyNames[ i ], jsonToJava( object.get( propertiyNames[ i ] ) ) );
      } catch( JSONException e ) {
        throw new RuntimeException( "Unable to convert JSONObject to Java: " + object );
      }
    }
    return result;
  }

  private static Object jsonToJava( Object object ) {
    Object result = object;
    if( object instanceof JSONArray ) {
      result = jsonToJava( ( JSONArray )object );
    } else if( object instanceof JSONObject ) {
      result = jsonToJava( ( JSONObject )object );
    }
    return result;
  }

}
