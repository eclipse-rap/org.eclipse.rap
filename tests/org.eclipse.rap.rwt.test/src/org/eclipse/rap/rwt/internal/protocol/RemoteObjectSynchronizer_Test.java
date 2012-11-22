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
import org.eclipse.rap.rwt.internal.protocol.ProtocolTestUtil.TestRemoteObjectSpecifier;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.DestroyOperation;
import org.eclipse.rap.rwt.testfixture.Message.ListenOperation;


public class RemoteObjectSynchronizer_Test extends TestCase {
  
  private TestRemoteObject remoteObject;
  private RemoteObjectSynchronizer<TestRemoteObject> synchronizer;

  @Override
  public void setUp() throws Exception {
    Fixture.setUp();
    RemoteObjectDefinitionImpl<TestRemoteObject> configuration 
      = new RemoteObjectDefinitionImpl<TestRemoteObject>( TestRemoteObject.class );
    TestRemoteObjectSpecifier initializer = new TestRemoteObjectSpecifier();
    initializer.define( configuration );
    synchronizer = new RemoteObjectSynchronizer<TestRemoteObject>( configuration, TestRemoteObjectSpecifier.TEST_TYPE );
    remoteObject = new TestRemoteObject();
  }
  
  @Override
  public void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testSetsProperties() {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( TestRemoteObjectSpecifier.TEST_PROPERTY, "fooBar" );
    
    synchronizer.set( remoteObject, properties );
    
    assertEquals( "fooBar", remoteObject.test );
  }
  
  public void testCallsObject() {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "foo", "bar" );
    
    synchronizer.call( remoteObject, TestRemoteObjectSpecifier.TEST_CALL, properties );
    
    assertNotNull( remoteObject.callProperties );
    assertEquals( "bar", remoteObject.callProperties.get( "foo" ) );
  }
  
  public void testNotifiesObjectInProcessActionPhase() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "foo", "bar" );
    
    synchronizer.notify( remoteObject, TestRemoteObjectSpecifier.TEST_EVENT, properties );
    
    assertNotNull( remoteObject.eventProperties );
    assertEquals( "bar", remoteObject.eventProperties.get( "foo" ) );
  }
  
  public void testCreateRendersCreateOperation() {
    RemoteObjectAdapterImpl<TestRemoteObject> adapter 
      = new RemoteObjectAdapterImpl<TestRemoteObject>( remoteObject, null );
    
    synchronizer.create( adapter );
    
    CreateOperation createOperation = Fixture.getProtocolMessage().findCreateOperation( adapter.getId() );
    assertEquals( TestRemoteObjectSpecifier.TEST_TYPE, createOperation.getType() );
  }
  
  public void testDestroyRendersDestroyOperation() {
    RemoteObjectAdapterImpl<TestRemoteObject> adapter 
      = new RemoteObjectAdapterImpl<TestRemoteObject>( remoteObject, null );
    
    synchronizer.destroy( adapter );
    
    DestroyOperation destroyOperation = Fixture.getProtocolMessage().findDestroyOperation( adapter.getId() );
    assertNotNull( destroyOperation );
  }
  
  public void testRendersRenderQueueFromAdapterWithSets() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    RemoteObjectAdapterImpl<TestRemoteObject> adapter 
      = new RemoteObjectAdapterImpl<TestRemoteObject>( remoteObject, null );
    adapter.set( TestRemoteObjectSpecifier.TEST_PROPERTY, "foo" );
    
    synchronizer.render( adapter );
    
    Object property = Fixture.getProtocolMessage().findSetProperty( adapter.getId(), 
                                                                    TestRemoteObjectSpecifier.TEST_PROPERTY );
    assertEquals( "foo", property );
  }
  
  public void testRendersRenderQueueFromAdapterWithCalls() {
    RemoteObjectAdapterImpl<TestRemoteObject> adapter 
      = new RemoteObjectAdapterImpl<TestRemoteObject>( remoteObject, null );
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "foo", "bar" );
    adapter.call( "fooBar", properties );
    
    synchronizer.render( adapter );
    
    CallOperation operation = Fixture.getProtocolMessage().findCallOperation( adapter.getId(), "fooBar" );
    assertEquals( "bar", operation.getProperty( "foo" ) );
  }
  
  public void testRendersRenderQueueFromAdapterWithListen() {
    RemoteObjectAdapterImpl<TestRemoteObject> adapter 
      = new RemoteObjectAdapterImpl<TestRemoteObject>( remoteObject, null );
    adapter.listen( "foo", true );
    
    synchronizer.render( adapter );
    
    ListenOperation operation = Fixture.getProtocolMessage().findListenOperation( adapter.getId(), "foo" );
    assertNotNull( operation );
  }
  
  public void testClearsRenderQueueFromAdapterAfterRender() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    RemoteObjectAdapterImpl<TestRemoteObject> adapter  
      = new RemoteObjectAdapterImpl<TestRemoteObject>( remoteObject, null );
    adapter.set( TestRemoteObjectSpecifier.TEST_PROPERTY, "foo" );
    
    synchronizer.render( adapter );
    
    assertTrue( adapter.getRenderQueue().isEmpty() );
  }
  
}
