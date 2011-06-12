/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.osgi.internal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import junit.framework.TestCase;


public class RWTContextContainer_Test extends TestCase {
  
  private RWTContextContainer container;
  private RWTContextImpl context;

  public void testAdd() {
    container.add( context );
    
    assertEquals( 1, container.getAll().length );
  }
  
  public void testRemove() {
    container.add( context );
    
    container.remove( context );
    
    assertEquals( 0, container.getAll().length );
  }
  
  public void testClear() {
    container.add( context );

    container.clear();
    
    assertEquals( 0, container.getAll().length );
  }
  
  public void testRemoveDeadContextsOnAdd() {
    container.add( context );
    stopContext();
    mockRWTContext();
    
    container.add( context );
    
    assertEquals( 1, container.size() );
  }
  
  public void testRemoveDeadContextsOnGetAll() {
    container.add( context );
    stopContext();
    
    container.getAll();
    
    assertEquals( 0, container.size() );
  }
  
  public void testRemoveDeadContextsOnRemove() {
    RWTContextImpl toRemove = context;
    container.add( toRemove );
    mockRWTContext();
    container.add( context );
    stopContext();
    
    container.remove( toRemove );
    
    assertEquals( 0, container.size() );
    
  }

  protected void setUp() {
    container = new RWTContextContainer();
    context = mockRWTContext();
  }
  
  private RWTContextImpl mockRWTContext() {
    RWTContextImpl result = mock( RWTContextImpl.class );
    when( Boolean.valueOf( result.isAlive() ) ).thenReturn( Boolean.TRUE );
    return result;
  }
  
  private void stopContext() {
    when( Boolean.valueOf( context.isAlive() ) ).thenReturn( Boolean.FALSE );
  }
}