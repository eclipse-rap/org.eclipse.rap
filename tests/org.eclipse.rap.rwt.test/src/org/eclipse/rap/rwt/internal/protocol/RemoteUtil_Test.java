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

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.protocol.ProtocolTestUtil.TestRemoteObject;
import org.eclipse.rap.rwt.internal.protocol.ProtocolTestUtil.TestRemoteObjectSpecification;
import org.eclipse.rap.rwt.remote.PropertyHandler;
import org.eclipse.rap.rwt.remote.RemoteUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;


public class RemoteUtil_Test extends TestCase {
  
  private TestObject testObject;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    testObject = new TestObject();
  }
  
  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testCreateRemoteObject() {
    RemoteObjectImpl<TestRemoteObject> remoteObject 
      = RemoteUtil.createRemoteObject( new TestRemoteObject(), TestRemoteObjectSpecification.class );
    
    assertNotNull( remoteObject );
  }
  
  public void testCreateRemoteObjectFailsWithNullObject() {
    try {
      RemoteUtil.createRemoteObject( null, TestRemoteObjectSpecification.class );
    } catch( NullPointerException expected ) {}
  }
  
  public void testCreateRemoteObjectFailsWithNullSpecification() {
    try {
      RemoteUtil.createRemoteObject( new TestRemoteObject(), null);
    } catch( NullPointerException expected ) {}
  }
  
  public void testSetsBooleanPropertyHandler() {
    PropertyHandler<TestObject> handler = RemoteUtil.createBooleanPropertyHandler( TestObject.class, "boolProperty" );
    
    handler.set( testObject, Boolean.TRUE );
    
    assertTrue( testObject.boolProperty );
  }
  
  public void testSetsStringPropertyHandler() {
    PropertyHandler<TestObject> handler = RemoteUtil.createStringPropertyHandler( TestObject.class, "stringProperty" );
    
    handler.set( testObject, "foo" );
    
    assertEquals( "foo", testObject.stringProperty );
  }
  
  public void testSetsIntPropertyHandler() {
    PropertyHandler<TestObject> handler = RemoteUtil.createIntPropertyHandler( TestObject.class, "intProperty" );
    
    handler.set( testObject, Integer.valueOf( 42 ) );
    
    assertEquals( 42, testObject.intProperty );
  }
  
  public void testSetsDoublePropertyHandler() {
    PropertyHandler<TestObject> handler = RemoteUtil.createDoublePropertyHandler( TestObject.class, "doubleProperty" );
    
    handler.set( testObject, Double.valueOf( 42.42 ) );
    
    assertEquals( 42.42, testObject.doubleProperty, 0 );
  }
  
  public void testSetsCustomTypePropertyHandler() {
    TestObject paramObject = new TestObject();
    PropertyHandler<TestObject> handler = RemoteUtil.createObjectPropertyHandler( TestObject.class, "objectProperty" );
    
    handler.set( testObject, paramObject );
    
    assertEquals( paramObject, testObject.objectProperty );
  }
  
  public void testCreateBooleanPropertyHandlerFailsWithNullPropertyName() {
    try {
      RemoteUtil.createBooleanPropertyHandler( TestObject.class, null );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testCreateStringPropertyHandlerFailsWithNullPropertyName() {
    try {
      RemoteUtil.createStringPropertyHandler( TestObject.class, null );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testCreateIntPropertyHandlerFailsWithNullPropertyName() {
    try {
      RemoteUtil.createIntPropertyHandler( TestObject.class, null );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testCreateDoublePropertyHandlerFailsWithNullPropertyName() {
    try {
      RemoteUtil.createDoublePropertyHandler( TestObject.class, null );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testCreateObjectPropertyHandlerFailsWithNullPropertyName() {
    try {
      RemoteUtil.createObjectPropertyHandler( TestObject.class, null );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testCreateBooleanPropertyHandlerFailsWithNullType() {
    try {
      RemoteUtil.createBooleanPropertyHandler( null, "test" );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testCreateStringPropertyHandlerFailsWithNullType() {
    try {
      RemoteUtil.createStringPropertyHandler( null, "test" );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testCreateIntPropertyHandlerFailsWithNullType() {
    try {
      RemoteUtil.createIntPropertyHandler( null, "test" );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testCreateDoublePropertyHandlerFailsWithNullType() {
    try {
      RemoteUtil.createDoublePropertyHandler( null, "test" );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testCreateObjectPropertyHandlerFailsWithNullType() {
    try {
      RemoteUtil.createObjectPropertyHandler( null, "test" );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testCreateBooleanPropertyHandlerFailsWithEmptyPropertyName() {
    try {
      RemoteUtil.createBooleanPropertyHandler( TestObject.class, "" );
      fail();
    } catch( IllegalArgumentException expected ) {}
  }
  
  public void testCreateStringPropertyHandlerFailsWithEmptyPropertyName() {
    try {
      RemoteUtil.createStringPropertyHandler( TestObject.class, "" );
      fail();
    } catch( IllegalArgumentException expected ) {}
  }
  
  public void testCreateIntPropertyHandlerFailsWithEmptyPropertyName() {
    try {
      RemoteUtil.createIntPropertyHandler( TestObject.class, "" );
      fail();
    } catch( IllegalArgumentException expected ) {}
  }
  
  public void testCreateDoublePropertyHandlerFailsWithEmptyPropertyName() {
    try {
      RemoteUtil.createDoublePropertyHandler( TestObject.class, "" );
      fail();
    } catch( IllegalArgumentException expected ) {}
  }
  
  public void testCreateObjectPropertyHandlerFailsWithEmptyPropertyName() {
    try {
      RemoteUtil.createObjectPropertyHandler( TestObject.class, "" );
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
