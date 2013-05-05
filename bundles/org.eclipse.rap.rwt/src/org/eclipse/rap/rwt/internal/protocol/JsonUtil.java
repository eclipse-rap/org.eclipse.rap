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
import java.util.List;
import java.util.Map;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;


public final class JsonUtil {

  /**
   * @deprecated
   */
  @Deprecated
  public static JsonValue createJsonValue( Object value ) {
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
      result = createJsonArray( ( int[] )value );
    } else if( value instanceof boolean[] ) {
      result = createJsonArray( ( boolean[] )value );
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

  /**
   * @deprecated
   */
  @Deprecated
  public static JsonValue createJsonObject( Map properties ) {
    JsonValue result;
    if( properties != null ) {
      JsonObject object = new JsonObject();
      Object[] keys = properties.keySet().toArray();
      for( int i = 0; i < keys.length; i++ ) {
        String key = ( String )keys[ i ];
        object.add( key, createJsonValue( properties.get( key ) ) );
      }
      result = object;
    } else {
      result = JsonValue.NULL;
    }
    return result;
  }

  public static JsonArray createJsonArray( int... values ) {
    JsonArray array = new JsonArray();
    for( int i = 0; i < values.length; i++ ) {
      array.add( values[ i ] );
    }
    return array;
  }

  public static JsonArray createJsonArray( float... values ) {
    JsonArray array = new JsonArray();
    for( int i = 0; i < values.length; i++ ) {
      array.add( values[ i ] );
    }
    return array;
  }

  public static JsonArray createJsonArray( boolean... values ) {
    JsonArray array = new JsonArray();
    for( int i = 0; i < values.length; i++ ) {
      array.add( values[ i ] );
    }
    return array;
  }

  public static JsonArray createJsonArray( String... values ) {
    JsonArray array = new JsonArray();
    for( int i = 0; i < values.length; i++ ) {
      array.add( values[ i ] );
    }
    return array;
  }

  /**
   * @deprecated
   */
  @Deprecated
  public static JsonValue createJsonArray( Object[] values ) {
    JsonValue result;
    if( values != null ) {
      JsonArray array = new JsonArray();
      for( int i = 0; i < values.length; i++ ) {
        array.add( createJsonValue( values[ i ] ) );
      }
      result = array;
    } else {
      result = JsonValue.NULL;
    }
    return result;
  }

  /**
   * @deprecated
   */
  @Deprecated
  public static Object jsonToJava( JsonValue object ) {
    Object result = null;
    if( object != null ) {
      if( object.isNull() ) {
        result = object;
      } else if( object.isBoolean() ) {
        result = Boolean.valueOf( object.asBoolean() );
      } else if( object.isString() ) {
        result = object.asString();
      } else if( object.isNumber() ) {
        try {
          result = Integer.valueOf( object.asInt() );
        } catch( NumberFormatException e ) {
          result = Double.valueOf( object.asDouble() );
        }
      } else if( object instanceof JsonArray ) {
        result = jsonToJava( ( JsonArray )object );
      } else if( object instanceof JsonObject ) {
        result = jsonToJava( ( JsonObject )object );
      }
    }
    return result;
  }

  private static Object[] jsonToJava( JsonArray object ) {
    Object[] result = new Object[ object.size() ];
    try {
      for( int i = 0; i < result.length; i++ ) {
        result[ i ] = jsonToJava( object.get( i ) );
      }
    } catch( Exception e ) {
      throw new RuntimeException( "Unable to convert JsonArray to Java: " + object );
    }
    return result;
  }

  private static Map<String,Object> jsonToJava( JsonObject object ) {
    Map<String,Object> result = new HashMap<String, Object>();
    List<String> names = object.names();
    for( String name : names ) {
      try {
        result.put( name, jsonToJava( object.get( name ) ) );
      } catch( Exception e ) {
        throw new RuntimeException( "Unable to convert JsonObject to Java: " + object );
      }
    }
    return result;
  }

}
