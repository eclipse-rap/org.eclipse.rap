/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.rwt.internal.widgets;

import java.util.List;
import org.eclipse.rap.rwt.internal.widgets.SlimList;
import junit.framework.TestCase;

public class SlimList_Test extends TestCase {

  public void testRemove() {
    List list = new SlimList();
    Object object1 = new Object();
    Object object2 = new Object();
    Object object3 = new Object();
   
    // Test removing an object that is not in the list
    list.clear();
    list.add( object1 );
    boolean result = list.remove( new Object() );
    assertEquals( false, result );
    assertEquals( 1, list.size() );
    
    // Test removing the sole object in the list
    list.clear();
    list.add( object1 );
    result = list.remove( object1 );
    assertEquals( true, result );
    assertEquals( -1, list.indexOf( object1 ) );
    assertEquals( 0, list.size() );
    
    // List [ object1, object2 ]: test removing object2
    list.clear();
    list.add( object1 );
    list.add( object2 );
    result = list.remove( object2 );
    assertEquals( true, result );
    assertEquals( -1, list.indexOf( object2 ) );
    assertEquals( 0, list.indexOf( object1 ) );
    assertEquals( 1, list.size() );
    
    // List [ object1, object2 ]: test removing object21
    list.clear();
    list.add( object1 );
    list.add( object2 );
    result = list.remove( object1 );
    assertEquals( true, result );
    assertEquals( -1, list.indexOf( object1 ) );
    assertEquals( 0, list.indexOf( object2 ) );
    assertEquals( 1, list.size() );
    
    // List [ object1, object2, object3 ]: test removing object21
    list.clear();
    list.add( object1 );
    list.add( object2 );
    list.add( object3 );
    result = list.remove( object2 );
    assertEquals( true, result );
    assertEquals( -1, list.indexOf( object2 ) );
    assertEquals( 0, list.indexOf( object1 ) );
    assertEquals( 1, list.indexOf( object3 ) );
    assertEquals( 2, list.size() );
  }

  public void testInsert() {
    List list = new SlimList();
    list.add( "a" );
    list.add( "c" );
    list.add( 1, "b" );
    assertEquals( "a", list.get( 0 ) );
    assertEquals( "b", list.get( 1 ) );
    assertEquals( "c", list.get( 2 ) );

    list.clear();
    list.add( "b" );
    list.add( "c" );
    list.add( 0, "a" );
    assertEquals( "a", list.get( 0 ) );
    assertEquals( "b", list.get( 1 ) );
    assertEquals( "c", list.get( 2 ) );

    list.clear();
    list.add( "a" );
    list.add( "b" );
    list.add( 2, "c" );
    assertEquals( "a", list.get( 0 ) );
    assertEquals( "b", list.get( 1 ) );
    assertEquals( "c", list.get( 2 ) );
  }
  
  public void testClear() {
    List list = new SlimList();
    list.clear();
    assertEquals( 0, list.size() );
    list.add( new Object() );
    list.clear();
    assertEquals( 0, list.size() );
  }
}
