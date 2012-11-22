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

import org.eclipse.rap.rwt.remote.RemoteObjectAdapter;
import org.eclipse.rap.rwt.testfixture.Fixture;


public class RemoteObjectAdapterRegistry_Test extends TestCase {
  
  private RemoteObjectAdapterRegistry registry;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    registry = RemoteObjectAdapterRegistry.getInstance();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testInstanceIsSingleton() {
    assertSame( registry, RemoteObjectAdapterRegistry.getInstance() );
  }
  
  public void testCreateAddsAdapter() {
    RemoteObjectAdapter adapter = mock( RemoteObjectAdapter.class );
    
    registry.register( adapter );
    
    List<RemoteObjectAdapter> adapters = registry.getAdapters();
    assertTrue( adapters.contains( adapter ) );
  }
  
  public void testRemoveAdapterRemovesItFromAdapters() {
    RemoteObjectAdapter adapter = mock( RemoteObjectAdapter.class );
    registry.register( adapter );

    registry.remove( adapter );
    
    List<RemoteObjectAdapter> adapters = registry.getAdapters();
    assertFalse( adapters.contains( adapter ) );
  }
  
  public void testHoldsAllAdapters() {
    registry.register( mock( RemoteObjectAdapter.class ) );
    registry.register( mock( RemoteObjectAdapter.class ) );
    registry.register( mock( RemoteObjectAdapter.class ) );
    
    List<RemoteObjectAdapter> adapters = registry.getAdapters();
    assertEquals( 3, adapters.size() );
  }
  
  public void testAdaptersIsSafeCopy() {
    List<RemoteObjectAdapter> adapters = registry.getAdapters();
    
    assertNotSame( adapters, registry.getAdapters() );
  }
  
}
