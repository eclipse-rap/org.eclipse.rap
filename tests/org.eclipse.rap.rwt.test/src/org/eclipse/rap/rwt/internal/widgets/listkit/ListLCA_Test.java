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

package org.eclipse.rap.rwt.internal.widgets.listkit;

import java.util.Arrays;
import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.events.SelectionListener;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.Fixture;

// TODO [rh] tests for selectionEvent (proper event fields and so on)
public class ListLCA_Test extends TestCase {

  public void testReadDataForSingle() {
    Display display = new Display();
    Shell shell = new Shell( display, RWT.NONE );
    List list = new List( shell, RWT.SINGLE );
    list.add( "item1" );
    list.add( "item2" );
    list.add( "item3" );
    AbstractWidgetLCA lca = WidgetUtil.getLCA( list );
    String listId = WidgetUtil.getId( list );
    // Test initial state for the followin tests
    assertEquals( -1, list.getSelectionIndex() );
    
    // Fake request that selected a single item
    Fixture.fakeRequestParam( listId + ".selection", "0" );
    lca.readData( list );
    assertEquals( 0, list.getSelectionIndex() );
    
    // Fake request that does not contain a selection parameter
    list.setSelection( 1 );
    Fixture.fakeRequestParam( listId + ".selection", null );
    lca.readData( list );
    assertEquals( 1, list.getSelectionIndex() );
  }
  
  public void testReadDataForMulti() {
    Display display = new Display();
    Shell shell = new Shell( display, RWT.NONE );
    List list = new List( shell, RWT.MULTI );
    list.add( "item1" );
    list.add( "item2" );
    list.add( "item3" );
    AbstractWidgetLCA lca = WidgetUtil.getLCA( list );
    String listId = WidgetUtil.getId( list );
    // Test initial state for the followin tests
    assertEquals( -1, list.getSelectionIndex() );
    
    // Fake request that selected 'item1' and 'item2'
    Fixture.fakeRequestParam( listId + ".selection", "0,1" );
    lca.readData( list );
    assertEquals( 0, list.getSelectionIndex() );
    int[] expected = new int[] { 0, 1 };
    assertTrue( Arrays.equals( expected, list.getSelectionIndices() ) );
    
    // Fake request that does not contain a selection parameter
    list.setSelection( 1 );
    Fixture.fakeRequestParam( listId + ".selection", null );
    lca.readData( list );
    assertEquals( 1, list.getSelectionIndex() );
  }
  
  public void testSelectionEvent() {
    final StringBuffer log = new StringBuffer();
    Display display = new Display();
    Shell shell = new Shell( display, RWT.NONE );
    final List list = new List( shell, RWT.SINGLE );
    list.add( "item1" );
    list.add( "item2" );
    list.setSelection( -1 );
    list.addSelectionListener( new SelectionListener() {
      public void widgetSelected( final SelectionEvent event ) {
        log.append( "selectionEvent" );
        assertSame( list, event.getSource() );
        assertEquals( 0, event.detail );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( null, event.item );
        assertEquals( true, event.doit );
      }
    } );
    AbstractWidgetLCA lca = WidgetUtil.getLCA( list );
    String listId = WidgetUtil.getId( list );
    Fixture.fakeRequestParam( listId + ".selection", "1" );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, listId );
    lca.readData( list );
    lca.processAction( list );
    assertEquals( "selectionEvent", log.toString() );
    assertEquals( 1, list.getSelectionIndex() );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
