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

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.testfixture.Fixture;


public class RemoteObjectRegistry_Test extends TestCase {
  
  private RemoteObjectRegistry registry;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    registry = RemoteObjectRegistry.getInstance();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testInstanceIsSingleton() {
    assertSame( registry, RemoteObjectRegistry.getInstance() );
  }
  
  public void testCreateAddsAdapter() {
    RemoteObject remoteObject = mock( RemoteObject.class );
    
    registry.register( remoteObject );
    
    List<RemoteObject> remoteObjects = registry.getRemoteObjects();
    assertTrue( remoteObjects.contains( remoteObject ) );
  }
  
  public void testRemoveAdapterRemovesItFromAdapters() {
    RemoteObject remoteObject = mock( RemoteObject.class );
    registry.register( remoteObject );

    registry.remove( remoteObject );
    
    List<RemoteObject> remoteObjects = registry.getRemoteObjects();
    assertFalse( remoteObjects.contains( remoteObject ) );
  }
  
  public void testHoldsAllAdapters() {
    registry.register( mock( RemoteObject.class ) );
    registry.register( mock( RemoteObject.class ) );
    registry.register( mock( RemoteObject.class ) );
    
    List<RemoteObject> remoteObjects = registry.getRemoteObjects();
    assertEquals( 3, remoteObjects.size() );
  }
  
  public void testAdaptersIsSafeCopy() {
    List<RemoteObject> remoteObjects = registry.getRemoteObjects();
    
    assertNotSame( remoteObjects, registry.getRemoteObjects() );
  }
  
}
