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
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.protocol.ProtocolTestUtil.TestRemoteObject;
import org.eclipse.rap.rwt.internal.protocol.ProtocolTestUtil.TestRemoteObjectSpecifier;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.remote.RemoteObjectAdapter;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.DestroyOperation;


public class RemoteObjects_Test extends TestCase {
  
  private RemoteObjectAdapterImpl adapter;
  private ProtocolTestUtil.TestRemoteObject remoteObject;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    mockRemoteObjectAdapter();
    RemoteObjectAdapterRegistry.getInstance().register( adapter );
  }

  private void mockRemoteObjectAdapter() {
    remoteObject = new TestRemoteObject();
    adapter = new RemoteObjectAdapterImpl<TestRemoteObject>( remoteObject, TestRemoteObjectSpecifier.class, "o" );
  }
  
  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testReadDataDispatchesSetProperty() {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( TestRemoteObjectSpecifier.TEST_PROPERTY, "fooBar" );
    Fixture.fakeSetOperation( adapter.getId(), parameters );
    
    RemoteObjects.readData();
    
    assertEquals( "fooBar", remoteObject.getTest() );
  }
  
  public void testReadDataDispatchesNotify() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( "foo", "bar" );
    Fixture.fakeNotifyOperation( adapter.getId(), TestRemoteObjectSpecifier.TEST_EVENT, parameters );
    
    RemoteObjects.readData();
    
    Map<String, Object> eventProperties = remoteObject.eventProperties;
    assertNotNull( eventProperties );
    assertEquals( "bar", eventProperties.get( "foo" ) );
    assertEquals( 1, eventProperties.size() );
  }
  
  public void testReadDataDispatchesCall() {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( "foo", "bar" );
    Fixture.fakeCallOperation( adapter.getId(), TestRemoteObjectSpecifier.TEST_CALL, parameters );
    
    RemoteObjects.readData();
    
    Map<String, Object> callProperties = remoteObject.callProperties;
    assertNotNull( callProperties );
    assertEquals( "bar", callProperties.get( "foo" ) );
    assertEquals( 1, callProperties.size() );
  }
  
  public void testCreateRendersCreateOperation() {
    RemoteObjects.render();
    
    CreateOperation createOperation = Fixture.getProtocolMessage().findCreateOperation( adapter.getId() );
    assertEquals( TestRemoteObjectSpecifier.TEST_TYPE, createOperation.getType() );
  }
  
  public void testRendersDestroyOperation() {
    adapter.destroy();
    
    RemoteObjects.render();
    
    DestroyOperation destroyOperation = Fixture.getProtocolMessage().findDestroyOperation( adapter.getId() );
    assertNotNull( destroyOperation );
  }
  
  public void testDestroyRemovesAdapterFromRegistry() {
    adapter.destroy();
    
    RemoteObjects.render();
    
    List<RemoteObjectAdapter> adapters = RemoteObjectAdapterRegistry.getInstance().getAdapters();
    assertFalse( adapters.contains( adapter ) );
  }
  
  public void testRendersRenderQueue() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    adapter.setInitialized( true );
    adapter.set( TestRemoteObjectSpecifier.TEST_PROPERTY, "hmpf" );
    
    RemoteObjects.render();
    
    Object property = Fixture.getProtocolMessage().findSetProperty( adapter.getId(), 
                                                                    TestRemoteObjectSpecifier.TEST_PROPERTY );
    assertNotNull( property );
    assertEquals( "hmpf", property );
  }
  
  public void testRendersInitialValue() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    adapter.set( TestRemoteObjectSpecifier.TEST_PROPERTY, "foo" );
    
    RemoteObjects.render();
    
    CreateOperation createOperation = Fixture.getProtocolMessage().findCreateOperation( adapter.getId() );
    Object property = createOperation.getProperty( TestRemoteObjectSpecifier.TEST_PROPERTY );
    assertEquals( "foo", property );
  }

}
