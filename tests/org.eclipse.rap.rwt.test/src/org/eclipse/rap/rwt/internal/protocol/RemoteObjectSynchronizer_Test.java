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

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.protocol.ProtocolTestUtil.TestRemoteObject;
import org.eclipse.rap.rwt.internal.protocol.ProtocolTestUtil.TestRemoteObjectSpecification;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.DestroyOperation;
import org.eclipse.rap.rwt.testfixture.Message.ListenOperation;


public class RemoteObjectSynchronizer_Test extends TestCase {
  
  private TestRemoteObject object;
  private RemoteObjectSynchronizer<TestRemoteObject> synchronizer;

  @Override
  public void setUp() throws Exception {
    Fixture.setUp();
    RemoteObjectDefinitionImpl<TestRemoteObject> definition 
      = new RemoteObjectDefinitionImpl<TestRemoteObject>( TestRemoteObject.class );
    TestRemoteObjectSpecification specification = new TestRemoteObjectSpecification();
    specification.define( definition );
    synchronizer = new RemoteObjectSynchronizer<TestRemoteObject>( definition, TestRemoteObjectSpecification.TEST_TYPE );
    object = new TestRemoteObject();
  }
  
  @Override
  public void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testSetsProperties() {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( TestRemoteObjectSpecification.TEST_PROPERTY, "fooBar" );
    
    synchronizer.set( object, properties );
    
    assertEquals( "fooBar", object.test );
  }
  
  public void testSetFailsWithNotDefinedProperties() {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "42", "fooBar" );
    
    try {
      synchronizer.set( object, properties );
      fail();
    } catch( IllegalStateException expected ) {}
  }
  
  public void testCallsObject() {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "foo", "bar" );
    
    synchronizer.call( object, TestRemoteObjectSpecification.TEST_CALL, properties );
    
    assertNotNull( object.callProperties );
    assertEquals( "bar", object.callProperties.get( "foo" ) );
  }
  
  public void testCallFailsWithNotDefinedmethod() {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "42", "fooBar" );
    
    try {
      synchronizer.call( object, "42", properties );
      fail();
    } catch( IllegalStateException expected ) {}
  }
  
  public void testNotifiesObjectInProcessActionPhase() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "foo", "bar" );
    
    synchronizer.notify( object, TestRemoteObjectSpecification.TEST_EVENT, properties );
    
    assertNotNull( object.eventProperties );
    assertEquals( "bar", object.eventProperties.get( "foo" ) );
  }
  
  public void testNotifyFailsWithNotDefinedEvent() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "42", "fooBar" );
    
    try {
      synchronizer.notify( object, "42", properties );
      fail();
    } catch( IllegalStateException expected ) {}
  }
  
  public void testCreateRendersCreateOperation() {
    RemoteObjectImpl<TestRemoteObject> remoteObject 
      = new RemoteObjectImpl<TestRemoteObject>( object, null );
    
    synchronizer.create( remoteObject );
    
    CreateOperation createOperation = Fixture.getProtocolMessage().findCreateOperation( remoteObject.getId() );
    assertEquals( TestRemoteObjectSpecification.TEST_TYPE, createOperation.getType() );
  }
  
  public void testDestroyRendersDestroyOperation() {
    RemoteObjectImpl<TestRemoteObject> remoteObject 
      = new RemoteObjectImpl<TestRemoteObject>( object, null );
    
    synchronizer.destroy( remoteObject );
    
    DestroyOperation destroyOperation = Fixture.getProtocolMessage().findDestroyOperation( remoteObject.getId() );
    assertNotNull( destroyOperation );
  }
  
  public void testRendersRenderQueueFromAdapterWithSets() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    RemoteObjectImpl<TestRemoteObject> remoteObject 
      = new RemoteObjectImpl<TestRemoteObject>( object, null );
    remoteObject.set( TestRemoteObjectSpecification.TEST_PROPERTY, "foo" );
    
    synchronizer.render( remoteObject );
    
    Object property = Fixture.getProtocolMessage().findSetProperty( remoteObject.getId(), 
                                                                    TestRemoteObjectSpecification.TEST_PROPERTY );
    assertEquals( "foo", property );
  }
  
  public void testRendersRenderQueueFromAdapterWithCalls() {
    RemoteObjectImpl<TestRemoteObject> remoteObject 
      = new RemoteObjectImpl<TestRemoteObject>( object, null );
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "foo", "bar" );
    remoteObject.call( "fooBar", properties );
    
    synchronizer.render( remoteObject );
    
    CallOperation operation = Fixture.getProtocolMessage().findCallOperation( remoteObject.getId(), "fooBar" );
    assertEquals( "bar", operation.getProperty( "foo" ) );
  }
  
  public void testRendersRenderQueueFromAdapterWithListen() {
    RemoteObjectImpl<TestRemoteObject> remoteObject 
      = new RemoteObjectImpl<TestRemoteObject>( object, null );
    remoteObject.listen( "foo", true );
    
    synchronizer.render( remoteObject );
    
    ListenOperation operation = Fixture.getProtocolMessage().findListenOperation( remoteObject.getId(), "foo" );
    assertNotNull( operation );
  }
  
  public void testClearsRenderQueueFromAdapterAfterRender() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    RemoteObjectImpl<TestRemoteObject> remoteObject  
      = new RemoteObjectImpl<TestRemoteObject>( object, null );
    remoteObject.set( TestRemoteObjectSpecification.TEST_PROPERTY, "foo" );
    
    synchronizer.render( remoteObject );
    
    assertTrue( remoteObject.getRenderQueue().isEmpty() );
  }
  
}
