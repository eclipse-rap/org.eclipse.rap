/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import junit.framework.TestCase;

import org.eclipse.rwt.internal.lifecycle.UICallBackManager.IdManager;
import org.eclipse.rwt.internal.util.ClassUtil;


public class IdManager_Test extends TestCase {
  
  private IdManager idManager;

  public void testAdd() {
    int size = idManager.add( "x" );
    assertEquals( 1, size );
  }

  public void testAddSameIdTwice() {
    idManager.add( "x" );
    int size = idManager.add( "x" );
    assertEquals( 1, size );
  }
  
  public void testIsEmpty() {
    assertTrue( idManager.isEmpty() );
  }
  
  public void testIsEmptyAfterAddingId() {
    idManager.add( "x" );
    assertFalse( idManager.isEmpty() );
  }
  
  public void testRemoveNonExistingId() {
    int size = idManager.remove( "does.nbot.exist" );
    assertEquals( 0, size );
  }
  
  public void testRemoveExistingId() {
    String id = "id";
    idManager.add( id );
    int size = idManager.remove( id );
    assertEquals( 0, size );
  }
  
  public void testRemoveExistingSameIdTwice() {
    String id = "id";
    idManager.add( id );
    idManager.remove( id );
    int size = idManager.remove( id );
    assertEquals( 0, size );
  }
  
  public void testAddMultipleIds() {
    idManager.add( "id1" );
    int size = idManager.add( "id2" );
    assertEquals( 2, size );
  }
  
  protected void setUp() throws Exception {
    idManager = newIdManager();
  }

  private static IdManager newIdManager() {
    return ( IdManager )ClassUtil.newInstance( IdManager.class );
  }
}
