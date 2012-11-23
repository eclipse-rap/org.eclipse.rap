/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.protocol;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.remote.Call;
import org.eclipse.rap.rwt.remote.EventNotification;
import org.eclipse.rap.rwt.remote.Property;
import org.eclipse.rap.rwt.remote.RemoteUtil;
import org.eclipse.rap.rwt.remote.RemoteUtil.CallHandler;
import org.eclipse.rap.rwt.remote.RemoteUtil.EventNotificationHandler;
import org.eclipse.rap.rwt.remote.RemoteUtil.PropertyHandler;


public class RemoteUtil_Test extends TestCase {
  
  private TestObject testObject;

  @Override
  protected void setUp() throws Exception {
    testObject = new TestObject();
  }
  
  public void testSetsBooleanProperty() {
    Property<TestObject> property = RemoteUtil.createBooleanProperty( TestObject.class, "boolProperty" );
    
    property.set( testObject, Boolean.TRUE );
    
    assertTrue( testObject.boolProperty );
  }
  
  public void testSetsStringProperty() {
    Property<TestObject> property = RemoteUtil.createStringProperty( TestObject.class, "stringProperty" );
    
    property.set( testObject, "foo" );
    
    assertEquals( "foo", testObject.stringProperty );
  }
  
  public void testSetsIntProperty() {
    Property<TestObject> property = RemoteUtil.createIntProperty( TestObject.class, "intProperty" );
    
    property.set( testObject, Integer.valueOf( 42 ) );
    
    assertEquals( 42, testObject.intProperty );
  }
  
  public void testSetsDoubleProperty() {
    Property<TestObject> property = RemoteUtil.createDoubleProperty( TestObject.class, "doubleProperty" );
    
    property.set( testObject, Double.valueOf( 42.42 ) );
    
    assertEquals( 42.42, testObject.doubleProperty, 0 );
  }
  
  public void testSetsCustomTypeProperty() {
    TestObject paramObject = new TestObject();
    Property<TestObject> property = RemoteUtil.createObjectProperty( TestObject.class, "objectProperty" );
    
    property.set( testObject, paramObject );
    
    assertEquals( paramObject, testObject.objectProperty );
  }
  
  @SuppressWarnings( "unchecked" )
  public void testSetsCustomProperty() {
    TestObject paramObject = new TestObject();
    PropertyHandler<TestObject> customPropertyHandler = mock( PropertyHandler.class );
    Property<TestObject> property = RemoteUtil.createProperty( "objectProperty", customPropertyHandler );
    
    property.set( testObject, paramObject );
    
    verify( customPropertyHandler ).set( testObject, paramObject );
  }
  
  @SuppressWarnings( "unchecked" )
  public void testNotifiesCustomEventNotification() {
    EventNotificationHandler<TestObject> handler = mock( EventNotificationHandler.class );
    EventNotification<TestObject> notification = RemoteUtil.createEventNotification( "foo", handler );
    Map<String, Object> properties = new HashMap<String, Object>();
    
    notification.notify( testObject, properties );
    
    verify( handler ).notify( testObject, properties );
  }
  
  @SuppressWarnings( "unchecked" )
  public void testCallsCustomCall() {
    CallHandler<TestObject> handler = mock( CallHandler.class );
    Call<TestObject> call = RemoteUtil.createCall( "foo", handler );
    Map<String, Object> properties = new HashMap<String, Object>();
    
    call.call( testObject, properties );
    
    verify( handler ).call( testObject, properties );
  }
  
  public void testCreateBooleanPropertyFailsWithNullPropertyName() {
    try {
      RemoteUtil.createBooleanProperty( TestObject.class, null );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testCreateStringPropertyFailsWithNullPropertyName() {
    try {
      RemoteUtil.createStringProperty( TestObject.class, null );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testCreateIntPropertyFailsWithNullPropertyName() {
    try {
      RemoteUtil.createIntProperty( TestObject.class, null );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testCreateDoublePropertyFailsWithNullPropertyName() {
    try {
      RemoteUtil.createDoubleProperty( TestObject.class, null );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testCreateObjectPropertyFailsWithNullPropertyName() {
    try {
      RemoteUtil.createObjectProperty( TestObject.class, null );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testCreateBooleanPropertyFailsWithNullType() {
    try {
      RemoteUtil.createBooleanProperty( null, "test" );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testCreateStringPropertyFailsWithNullType() {
    try {
      RemoteUtil.createStringProperty( null, "test" );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testCreateIntPropertyFailsWithNullType() {
    try {
      RemoteUtil.createIntProperty( null, "test" );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testCreateDoublePropertyFailsWithNullType() {
    try {
      RemoteUtil.createDoubleProperty( null, "test" );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testCreateObjectPropertyFailsWithNullType() {
    try {
      RemoteUtil.createObjectProperty( null, "test" );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testCreateBooleanPropertyFailsWithEmptyPropertyName() {
    try {
      RemoteUtil.createBooleanProperty( TestObject.class, "" );
      fail();
    } catch( IllegalArgumentException expected ) {}
  }
  
  public void testCreateStringPropertyFailsWithEmptyPropertyName() {
    try {
      RemoteUtil.createStringProperty( TestObject.class, "" );
      fail();
    } catch( IllegalArgumentException expected ) {}
  }
  
  public void testCreateIntPropertyFailsWithEmptyPropertyName() {
    try {
      RemoteUtil.createIntProperty( TestObject.class, "" );
      fail();
    } catch( IllegalArgumentException expected ) {}
  }
  
  public void testCreateDoublePropertyFailsWithEmptyPropertyName() {
    try {
      RemoteUtil.createDoubleProperty( TestObject.class, "" );
      fail();
    } catch( IllegalArgumentException expected ) {}
  }
  
  public void testCreateObjectPropertyFailsWithEmptyPropertyName() {
    try {
      RemoteUtil.createObjectProperty( TestObject.class, "" );
      fail();
    } catch( IllegalArgumentException expected ) {}
  }
  
  @SuppressWarnings( "unused" ) // this is ok because it's called via reflection.
  private class TestObject {
    
    boolean boolProperty;
    String stringProperty;
    int intProperty;
    double doubleProperty;
    Object objectProperty;
    
    public void setBoolProperty( boolean property ) {
      boolProperty = property;
    }
    
    public void setStringProperty( String property ) {
      stringProperty = property;
    }
    
    public void setIntProperty( int property ) {
      intProperty = property;
    }

    public void setDoubleProperty( double property ) {
      doubleProperty = property;
    }
    
    public void setObjectProperty( Object property ) {
      objectProperty = property;
    }
    
  }
  
}
