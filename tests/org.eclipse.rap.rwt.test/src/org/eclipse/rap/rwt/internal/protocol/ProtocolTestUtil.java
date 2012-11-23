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
package org.eclipse.rap.rwt.internal.protocol;

import java.util.Map;

import org.eclipse.rap.rwt.remote.Call;
import org.eclipse.rap.rwt.remote.EventNotification;
import org.eclipse.rap.rwt.remote.Property;
import org.eclipse.rap.rwt.remote.RemoteObjectDefinition;
import org.eclipse.rap.rwt.remote.RemoteObjectSpecifier;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ProtocolTestUtil {

  public static boolean jsonEquals( String json, JSONArray actualArray ) throws JSONException {
    boolean result = true;
    JSONArray expectedArray = new JSONArray( json );
    if( expectedArray.length() == actualArray.length() ) {
      for( int i = 0; i < expectedArray.length(); i++ ) {
        Object expected = expectedArray.get( i );
        Object actual = actualArray.get( i );
        boolean equal =    ( expected == null && actual == JSONObject.NULL )
                        || ( expected != null && expected.equals( actual ) );
        if( !equal ) {
          result = false;
        }
      }
    } else {
      result = false;
    }
    return result;
  }

  // Test Remote Object
  public static class TestRemoteObject {
    
    String test;
    Map<String, Object> eventProperties;
    Map<String, Object> callProperties;
  
    public String getTest() {
      return test;
    }
  
    public void setTest( String test ) {
      this.test = test;
    }
    
    public void call( Map<String, Object> properties)  {
      this.callProperties = properties;
    }
    
    public void fireEvent( Map<String, Object> properties ) {
      this.eventProperties = properties;
    }
    
  }

  public static class TestRemoteObjectSpecifier implements RemoteObjectSpecifier<TestRemoteObject> {
  
    public static final String TEST_PROPERTY = "test";
    public static final String TEST_CALL = "call";
    public static final String TEST_EVENT = "testEvent";
    public static final String TEST_TYPE = "rwt.TestRemoteObject";

    public String getType() {
      return TEST_TYPE;
    }

    public void define( RemoteObjectDefinition<TestRemoteObject> definition ) {
      definition.addProperty( new Property<TestRemoteObject>() {
  
        public String getName() {
          return TEST_PROPERTY;
        }
  
        public void set( TestRemoteObject object, Object value ) {
          object.setTest( ( String )value );
        } 
        
      } );
      definition.addCall( new Call<TestRemoteObject>() {
        
        public String getName() {
          return TEST_CALL;
        }
        
        public void call( TestRemoteObject object, Map<String, Object> properties ) {
          object.call( properties );
        }
      } );
      definition.addEvent( new EventNotification<TestRemoteObject>() {
        
        public void notify( TestRemoteObject object, Map<String, Object> properties ) {
          object.fireEvent( properties );
        }
        
        public String getName() {
          return TEST_EVENT;
        }
      } );
    }
    
  }

}
