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
import org.eclipse.rap.rwt.testfixture.Fixture;


public class RemoteObjectSynchronizerRegistry_Test extends TestCase {
  
  private RemoteObjectSynchronizerRegistry registry;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    registry = RemoteObjectSynchronizerRegistry.getInstance();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testInstanceIsSingleton() {
    assertSame( registry, RemoteObjectSynchronizerRegistry.getInstance() );
  }
  
  public void testDefinesDefinition() {
    TestRemoteObject remoteObject = new TestRemoteObject();
    Map<String, Object> setProperties = new HashMap<String, Object>();
    setProperties.put( TestRemoteObjectSpecification.TEST_PROPERTY, "foo" );
    
    registry.register( TestRemoteObject.class, TestRemoteObjectSpecification.class );
    RemoteObjectSynchronizer<TestRemoteObject> synchronizer = registry.getSynchronizerForType( TestRemoteObject.class );
    synchronizer.set( remoteObject, setProperties );
    
    assertNotNull( synchronizer );
    assertEquals( "foo", remoteObject.test );
  }
  
  public void testGetReigsteredSynchronizer() {
    registry.register( TestRemoteObject.class, TestRemoteObjectSpecification.class );
    
    assertNotNull( registry.getSynchronizerForType( TestRemoteObject.class ) );
  }
  
  public void testSynchronizerHasSameTypeAsRegisteredWith() {
    registry.register( TestRemoteObject.class, TestRemoteObjectSpecification.class );
    
    RemoteObjectSynchronizer<TestRemoteObject> synchronizer = registry.getSynchronizerForType( TestRemoteObject.class );
    assertEquals( TestRemoteObject.class, synchronizer.getType() );
  }
  
  public void testIgnoresRegisteringOfTypesTwice() {
    registry.register( TestRemoteObject.class, TestRemoteObjectSpecification.class );
    registry.register( TestRemoteObject.class, TestRemoteObjectSpecification.class );
  }
}
