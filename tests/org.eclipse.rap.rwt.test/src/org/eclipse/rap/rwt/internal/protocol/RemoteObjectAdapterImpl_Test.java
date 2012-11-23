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


public class RemoteObjectAdapterImpl_Test extends TestCase {

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testNewId() {
    String id1 = new RemoteObjectAdapterImpl<Object>( new Object(), null ).getId();
    String id2 = new RemoteObjectAdapterImpl<Object>( new Object(), null ).getId();

    assertFalse( id1.equals( id2 ) );
  }

  public void testSameId() {
    RemoteObjectAdapterImpl<Object> adapter = new RemoteObjectAdapterImpl<Object>( new Object(), null );
    String id1 = adapter.getId();
    String id2 = adapter.getId();

    assertEquals( id1, id2 );
  }

  public void testPrefix() {
    RemoteObjectAdapterImpl<Object> adapter = new RemoteObjectAdapterImpl<Object>( new Object(), null );
    String id1 = adapter.getId();

    assertTrue( id1.startsWith( "o" ) );
  }

  public void testCustomPrefix() {
    RemoteObjectAdapterImpl<Object> adapter = new RemoteObjectAdapterImpl<Object>(  new Object(), null, "gl" );
    String id1 = adapter.getId();

    assertTrue( id1.startsWith( "gl" ) );
  }
  
  public void testRegistersSpecifier() {
    new RemoteObjectAdapterImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecifier.class, "gl" );
    
    RemoteObjectSynchronizer<TestRemoteObject> synchronizer 
      = RemoteObjectSynchronizerRegistry.getInstance().getSynchronizerForType( TestRemoteObject.class );
    assertNotNull( synchronizer );
  }
  
  public void testIsNotInitializedByDefault() {
    RemoteObjectAdapterImpl<Object> adapter = new RemoteObjectAdapterImpl<Object>( new Object(), null );
    
    assertFalse( adapter.isInitialized() );
  }
  
  public void testIsInitialized() {
    RemoteObjectAdapterImpl<Object> adapter = new RemoteObjectAdapterImpl<Object>( new Object(), null );
    
    adapter.setInitialized( true );
    
    assertTrue( adapter.isInitialized() );
  }
  
  public void testIsNotDestroyedByDefault() {
    RemoteObjectAdapterImpl<Object> adapter = new RemoteObjectAdapterImpl<Object>( new Object(), null );
    
    assertFalse( adapter.isDestroyed() );
  }
  
  public void testKeepsRemoteObject() {
    Object remoteObject = new Object();
    RemoteObjectAdapterImpl<Object> adapter = new RemoteObjectAdapterImpl<Object>( remoteObject, null );
    
    assertSame( remoteObject, adapter.getRemoteObject() );
  }
  
  public void testCreationRegistersInstanceInRegistry() {
    RemoteObjectAdapterImpl<TestRemoteObject> adapter 
      = new RemoteObjectAdapterImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecifier.class, "gl" );

    List<RemoteObjectAdapter> adapters = RemoteObjectAdapterRegistry.getInstance().getAdapters();
    assertTrue( adapters.contains( adapter ) );
  }
  
  public void testIsDestroyed() {
    RemoteObjectAdapterImpl<TestRemoteObject> adapter 
      = new RemoteObjectAdapterImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecifier.class, "gl" );
    
    adapter.destroy();
    
    assertTrue( adapter.isDestroyed() );
  }
  
  public void testQueuesCalls() {
    RemoteObjectAdapterImpl<TestRemoteObject> adapter 
      = new RemoteObjectAdapterImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecifier.class, "gl" );
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "foo", "bar" );
    
    adapter.call( "fooBar", properties );
    
    List<Runnable> calls = adapter.getRenderQueue();
    assertEquals( 1, calls.size() );
  }
  
  public void testQueueSets() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    RemoteObjectAdapterImpl<TestRemoteObject> adapter 
      = new RemoteObjectAdapterImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecifier.class, "gl" );
     
    adapter.set( "foo", "bar" );
    
    List<Runnable> sets = adapter.getRenderQueue();
    assertEquals( 1, sets.size() );
  }
  
  public void testQueueListens() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    RemoteObjectAdapterImpl<TestRemoteObject> adapter 
    = new RemoteObjectAdapterImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecifier.class, "gl" );
    
    adapter.listen( "foo", true );
    
    List<Runnable> sets = adapter.getRenderQueue();
    assertEquals( 1, sets.size() );
  }
  
  public void testQueueListensOnlyIfChanged() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    RemoteObjectAdapterImpl<TestRemoteObject> adapter 
    = new RemoteObjectAdapterImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecifier.class, "gl" );
    
    adapter.listen( "foo", true );
    adapter.listen( "foo", true );
    
    List<Runnable> sets = adapter.getRenderQueue();
    assertEquals( 1, sets.size() );
  }
  
  public void testQueueListensIfChanged() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    RemoteObjectAdapterImpl<TestRemoteObject> adapter 
    = new RemoteObjectAdapterImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecifier.class, "gl" );
    
    adapter.listen( "foo", true );
    adapter.listen( "foo", false );
    
    List<Runnable> sets = adapter.getRenderQueue();
    assertEquals( 2, sets.size() );
  }
  
  public void testDoesNotQueueSetsInReadData() {
    Fixture.fakePhase( PhaseId.READ_DATA );
    RemoteObjectAdapterImpl<TestRemoteObject> adapter 
      = new RemoteObjectAdapterImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecifier.class, "gl" );
     
    adapter.set( "foo", "bar" );
    
    List<Runnable> sets = adapter.getRenderQueue();
    assertTrue( sets.isEmpty() );
  }
  
  public void testQueueIsClearable() {
    RemoteObjectAdapterImpl<TestRemoteObject> adapter 
      = new RemoteObjectAdapterImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecifier.class, "gl" );
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "foo", "bar" );
    adapter.call( "fooBar", properties );

    adapter.getRenderQueue().clear();
    
    List<Runnable> operations = adapter.getRenderQueue();
    assertTrue( operations.isEmpty() );
  }
  
  public void testQueuesCallsFailsWithNullProperty() {
    RemoteObjectAdapterImpl<TestRemoteObject> adapter 
      = new RemoteObjectAdapterImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecifier.class, "gl" );
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "foo", "bar" );
    
    try {
      adapter.call( null, properties );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testQueuesCallsFailsWithEmptyProperty() {
    RemoteObjectAdapterImpl<TestRemoteObject> adapter 
    = new RemoteObjectAdapterImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecifier.class, "gl" );
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "foo", "bar" );
    
    try {
      adapter.call( "", properties );
      fail();
    } catch( IllegalArgumentException expected ) {}
  }
  
  public void testQueueSetsFailsWithNullName() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    RemoteObjectAdapterImpl<TestRemoteObject> adapter 
      = new RemoteObjectAdapterImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecifier.class, "gl" );
     
    try {
      adapter.set( null, "bar" );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testQueueSetsFailsWithEmptyName() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    RemoteObjectAdapterImpl<TestRemoteObject> adapter 
      = new RemoteObjectAdapterImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecifier.class, "gl" );
    
    try {
      adapter.set( "", "bar" );
      fail();
    } catch( IllegalArgumentException expected ) {}
  }
  
  public void testQueueSetsFailsWithNullValue() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    RemoteObjectAdapterImpl<TestRemoteObject> adapter 
      = new RemoteObjectAdapterImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecifier.class, "gl" );
    
    try {
      adapter.set( "foo", null );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testQueueListensFailsWithNullName() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    RemoteObjectAdapterImpl<TestRemoteObject> adapter 
      = new RemoteObjectAdapterImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecifier.class, "gl" );
    
    try {
      adapter.listen( null, true );
      fail();
    } catch( NullPointerException expected ) {}
  }
  
  public void testQueueListensFailsWithEmptyName() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    RemoteObjectAdapterImpl<TestRemoteObject> adapter 
      = new RemoteObjectAdapterImpl<TestRemoteObject>(  new TestRemoteObject(), TestRemoteObjectSpecifier.class, "gl" );
    
    try {
      adapter.listen( "", true );
      fail();
    } catch( IllegalArgumentException expected ) {}
  }
  
}
