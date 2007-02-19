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

package org.eclipse.rap.rwt.internal.widgets.coolbarkit;

import junit.framework.TestCase;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;
import com.w4t.Fixture;


public final class CoolBarLCA_Test extends TestCase {

  public void testItemPreserveValues() {
    Display display = new Display();
    Shell shell  = new Shell( display, RWT.NONE );
    CoolBar bar = new CoolBar( shell, RWT.FLAT );
    CoolItem item = new CoolItem( bar, RWT.NONE );

    RWTFixture.markInitialized( item );
    item.setText( "some text" );
    item.setImage( Image.find( RWTFixture.IMAGE1 ) );
    AbstractWidgetLCA lca = WidgetUtil.getLCA( item );
    lca.preserveValues( item );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    assertEquals( null, adapter.getPreserved( Props.TEXT ) );
    assertEquals( null, adapter.getPreserved( Props.IMAGE ) );
  }
  
  public void testItemReordering() {
    Display display = new Display();
    Shell shell  = new Shell( display, RWT.NONE );
    CoolBar bar = new CoolBar( shell, RWT.FLAT );
    CoolItem item0 = new CoolItem( bar, RWT.NONE );
    item0.setSize( 10, 10 );
    CoolItem item1 = new CoolItem( bar, RWT.NONE );
    item1.setSize( 10, 10 );
    CoolItem item2 = new CoolItem( bar, RWT.NONE );
    item2.setSize( 10, 10 );
    String item0Id = WidgetUtil.getId( item0 );
    String item2Id = WidgetUtil.getId( item2 );
    String item1Id = WidgetUtil.getId( item1 );
    AbstractWidgetLCA coolItemLCA = WidgetUtil.getLCA( item2 );

    // ensure initial state
    assertEquals( 0, bar.getItemOrder()[ 0 ] );
    assertEquals( 1, bar.getItemOrder()[ 1 ] );
    assertEquals( 2, bar.getItemOrder()[ 2 ] );
    
    // Simulate that item2 is dragged left of item1
    int newX = item1.getBounds().x - 4;
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_MOVED, item2Id );
    Fixture.fakeRequestParam( item2Id + ".bounds.x", String.valueOf( newX ) );
    coolItemLCA.readData( item2 );
    assertEquals( 0, bar.getItemOrder()[ 0 ] );
    assertEquals( 2, bar.getItemOrder()[ 1 ] );
    assertEquals( 1, bar.getItemOrder()[ 2 ] );
    
    // Simulate that item0 is dragged after the last item
    bar.setItemOrder( new int[] { 0, 1, 2, } );
    newX = item2.getBounds().x + item2.getBounds().width + 10;
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_MOVED, item0Id );
    Fixture.fakeRequestParam( item0Id + ".bounds.x", String.valueOf( newX ) );
    coolItemLCA.readData( item0 );
    assertEquals( 0, bar.getItemOrder()[ bar.indexOf( item1 ) ] );
    assertEquals( 1, bar.getItemOrder()[ bar.indexOf( item2 ) ] );
    assertEquals( 2, bar.getItemOrder()[ bar.indexOf( item0 ) ] );

    // Simulate that item0 is dragged onto itself -> nothing shoudl change
    bar.setItemOrder( new int[] { 0, 1, 2, } );
    newX = item0.getBounds().x + 2;
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_MOVED, item0Id );
    Fixture.fakeRequestParam( item0Id + ".bounds.x", String.valueOf( newX ) );
    coolItemLCA.readData( item0 );
    assertEquals( 0, bar.getItemOrder()[ bar.indexOf( item0 ) ] );
    assertEquals( 1, bar.getItemOrder()[ bar.indexOf( item1 ) ] );
    assertEquals( 2, bar.getItemOrder()[ bar.indexOf( item2 ) ] );

    // Simulate that item1 is before the first item
    bar.setItemOrder( new int[] { 0, 1, 2, } );
    newX = item0.getBounds().x - 5;
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_MOVED, item1Id );
    Fixture.fakeRequestParam( item1Id + ".bounds.x", String.valueOf( newX ) );
    coolItemLCA.readData( item1 );
    assertEquals( 0, bar.getItemOrder()[ bar.indexOf( item1 ) ] );
    assertEquals( 1, bar.getItemOrder()[ bar.indexOf( item0 ) ] );
    assertEquals( 2, bar.getItemOrder()[ bar.indexOf( item2 ) ] );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
