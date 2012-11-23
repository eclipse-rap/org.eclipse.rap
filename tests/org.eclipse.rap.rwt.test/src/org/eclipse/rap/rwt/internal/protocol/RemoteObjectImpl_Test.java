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
import org.eclipse.rap.rwt.internal.protocol.ProtocolTestUtil.TestRemoteObjectSpecification;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.testfixture.Fixture;


public class RemoteObjectImpl_Test extends TestCase {

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testNewId() {
    String id1 = new RemoteObjectImpl<Object>( new Object(), null ).getId();
    String id2 = new RemoteObjectImpl<Object>( new Object(), null ).getId();

    assertFalse( id1.equals( id2 ) );
  }

  public void testSameId() {
    RemoteObjectImpl<Object> remoteObject = new RemoteObjectImpl<Object>( new Object(), null );
    String id1 = remoteObject.getId();
    String id2 = remoteObject.getId();

    assertEquals( id1, id2 );
  }

  public void testPrefix() {
    RemoteObjectImpl<Object> remoteObject = new RemoteObjectImpl<Object>( new Object(), null );
    String id1 = remoteObject.getId();

    assertTrue( id1.startsWith( "o" ) );
  }

  public void testCustomPrefix() {
    RemoteObjectImpl<Object> remoteObject = new RemoteObjectImpl<Object>(  new Object(), null, "gl" );
    String id1 = remoteObject.getId();

    assertTrue( id1.startsWith( "gl" ) );
  }
  
  public void testRegistersSpecifier() {
    new RemoteObjectImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecification.class, "gl" );
    
    RemoteObjectSynchronizer<TestRemoteObject> synchronizer 
      = RemoteObjectSynchronizerRegistry.getInstance().getSynchronizerForType( TestRemoteObject.class );
    assertNotNull( synchronizer );
  }
  
  public void testIsNotInitializedByDefault() {
    RemoteObjectImpl<Object> remoteObject = new RemoteObjectImpl<Object>( new Object(), null );
    
    assertFalse( remoteObject.isInitialized() );
  }
  
  public void testIsInitialized() {
    RemoteObjectImpl<Object> remoteObject = new RemoteObjectImpl<Object>( new Object(), null );
    
    remoteObject.setInitialized( true );
    
    assertTrue( remoteObject.isInitialized() );
  }
  
  public void testIsNotDestroyedByDefault() {
    RemoteObjectImpl<Object> remoteObject = new RemoteObjectImpl<Object>( new Object(), null );
    
    assertFalse( remoteObject.isDestroyed() );
  }
  
  public void testKeepsRemoteObject() {
    Object object = new Object();
    RemoteObjectImpl<Object> remoteObject = new RemoteObjectImpl<Object>( object, null );
    
    assertSame( object, remoteObject.getObject() );
  }
  
  public void testCreationRegistersInstanceInRegistry() {
    RemoteObjectImpl<TestRemoteObject> remoteObject 
      = new RemoteObjectImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecification.class, "gl" );

    List<RemoteObject> remoteObjects = RemoteObjectRegistry.getInstance().getRemoteObjects();
    assertTrue( remoteObjects.contains( remoteObject ) );
  }
  
  public void testIsDestroyed() {
    RemoteObjectImpl<TestRemoteObject> remoteObject 
      = new RemoteObjectImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecification.class, "gl" );
    
    remoteObject.destroy();
    
    assertTrue( remoteObject.isDestroyed() );
  }
  
  public void testQueuesCalls() {
    RemoteObjectImpl<TestRemoteObject> remoteObject 
      = new RemoteObjectImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecification.class, "gl" );
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "foo", "bar" );
    
    remoteObject.call( "fooBar", properties );
    
    List<Runnable> calls = remoteObject.getRenderQueue();
    assertEquals( 1, calls.size() );
  }
  
  public void testQueueSets() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    RemoteObjectImpl<TestRemoteObject> remoteObject 
      = new RemoteObjectImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecification.class, "gl" );
     
    remoteObject.set( "foo", "bar" );
    
    List<Runnable> sets = remoteObject.getRenderQueue();
    assertEquals( 1, sets.size() );
  }
  
  public void testQueueSetsWithoutPhase() {
    RemoteObjectImpl<TestRemoteObject> remoteObject 
    = new RemoteObjectImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecification.class, "gl" );
    
    remoteObject.set( "foo", "bar" );
    
    List<Runnable> sets = remoteObject.getRenderQueue();
    assertEquals( 1, sets.size() );
  }
  
  public void testQueueListens() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    RemoteObjectImpl<TestRemoteObject> remoteObject 
    = new RemoteObjectImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecification.class, "gl" );
    
    remoteObject.listen( "foo", true );
    
    List<Runnable> sets = remoteObject.getRenderQueue();
    assertEquals( 1, sets.size() );
  }
  
  public void testQueueListensOnlyIfChanged() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    RemoteObjectImpl<TestRemoteObject> remoteObject 
    = new RemoteObjectImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecification.class, "gl" );
    
    remoteObject.listen( "foo", true );
    remoteObject.listen( "foo", true );
    
    List<Runnable> sets = remoteObject.getRenderQueue();
    assertEquals( 1, sets.size() );
  }
  
  public void testQueueListensIfChanged() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    RemoteObjectImpl<TestRemoteObject> remoteObject 
    = new RemoteObjectImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecification.class, "gl" );
    
    remoteObject.listen( "foo", true );
    remoteObject.listen( "foo", false );
    
    List<Runnable> sets = remoteObject.getRenderQueue();
    assertEquals( 2, sets.size() );
  }
  
  public void testDoesNotQueueSetsInReadData() {
    Fixture.fakePhase( PhaseId.READ_DATA );
    RemoteObjectImpl<TestRemoteObject> remoteObject 
      = new RemoteObjectImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecification.class, "gl" );
     
    remoteObject.set( "foo", "bar" );
    
    List<Runnable> sets = remoteObject.getRenderQueue();
    assertTrue( sets.isEmpty() );
  }
  
  public void testQueueIsClearable() {
    RemoteObjectImpl<TestRemoteObject> remoteObject 
      = new RemoteObjectImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecification.class, "gl" );
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "foo", "bar" );
    remoteObject.call( "fooBar", properties );

    remoteObject.getRenderQueue().clear();
    
    List<Runnable> operations = remoteObject.getRenderQueue();
    assertTrue( operations.isEmpty() );
  }
  
  public void testQueuesCallsFailsWithNullProperty() {
    RemoteObjectImpl<TestRemoteObject> remoteObject 
      = new RemoteObjectImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecification.class, "gl" );
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "foo", "bar" );
    
    try {
      remoteObject.call( null, properties );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testQueuesCallsFailsWithEmptyProperty() {
    RemoteObjectImpl<TestRemoteObject> remoteObject 
    = new RemoteObjectImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecification.class, "gl" );
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "foo", "bar" );
    
    try {
      remoteObject.call( "", properties );
      fail();
    } catch( IllegalArgumentException expected ) {}
  }
  
  public void testQueueSetsFailsWithNullName() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    RemoteObjectImpl<TestRemoteObject> remoteObject 
      = new RemoteObjectImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecification.class, "gl" );
     
    try {
      remoteObject.set( null, "bar" );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testQueueSetsFailsWithEmptyName() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    RemoteObjectImpl<TestRemoteObject> remoteObject 
      = new RemoteObjectImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecification.class, "gl" );
    
    try {
      remoteObject.set( "", "bar" );
      fail();
    } catch( IllegalArgumentException expected ) {}
  }
  
  public void testQueueSetsFailsWithNullValue() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    RemoteObjectImpl<TestRemoteObject> remoteObject 
      = new RemoteObjectImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecification.class, "gl" );
    
    try {
      remoteObject.set( "foo", null );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testQueueListensFailsWithNullName() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    RemoteObjectImpl<TestRemoteObject> remoteObject 
      = new RemoteObjectImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecification.class, "gl" );
    
    try {
      remoteObject.listen( null, true );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testQueueListensFailsWithEmptyName() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    RemoteObjectImpl<TestRemoteObject> remoteObject 
      = new RemoteObjectImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecification.class, "gl" );
    
    try {
      remoteObject.listen( "", true );
      fail();
    } catch( IllegalArgumentException expected ) {}
  }
  
}
