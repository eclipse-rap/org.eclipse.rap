/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.coolitemkit;

import static org.eclipse.swt.internal.widgets.coolitemkit.CoolItemOperationHandler.moveItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.ICoolBarAdapter;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class CoolItemOperationHandler_Test {

  private CoolBar coolBar;
  private CoolItem item;
  private CoolItemOperationHandler handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    coolBar = new CoolBar( shell, SWT.NONE );
    coolBar.setSize( 400, 25 );
    item = new CoolItem( coolBar, SWT.NONE );
    item.setSize( 10, 10 );
    handler = new CoolItemOperationHandler( item );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHandleCallMove() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    CoolItem item1 = new CoolItem( coolBar, SWT.NONE );
    item1.setSize( 20, 10 );
    int oldX = item.getBounds().x;

    handler.handleCall( "move", new JsonObject().add( "left", 25 ) );

    assertTrue( oldX != item.getBounds().x );
  }

  @Test
  public void testMoveItem_1() {
    CoolItem item1 = new CoolItem( coolBar, SWT.NONE );
    item1.setSize( 10, 10 );
    CoolItem item2 = new CoolItem( coolBar, SWT.NONE );
    item2.setSize( 10, 10 );
    // get adapter to set item order
    ICoolBarAdapter cba = coolBar.getAdapter( ICoolBarAdapter.class );

    // ensure initial state
    assertEquals( 0, coolBar.getItemOrder()[ 0 ] );
    assertEquals( 1, coolBar.getItemOrder()[ 1 ] );
    assertEquals( 2, coolBar.getItemOrder()[ 2 ] );

    // Simulate that item2 is dragged left of item1
    moveItem( item2, item1.getBounds().x - 4 );

    assertEquals( 0, coolBar.getItemOrder()[ 0 ] );
    assertEquals( 2, coolBar.getItemOrder()[ 1 ] );
    assertEquals( 1, coolBar.getItemOrder()[ 2 ] );

    // Simulate that item is dragged after the last item
    cba.setItemOrder( new int[] { 0, 1, 2, } );

    moveItem( item, item2.getBounds().x + item2.getBounds().width + 10 );

    assertEquals( 1, coolBar.getItemOrder()[ 0 ] );
    assertEquals( 2, coolBar.getItemOrder()[ 1 ] );
    assertEquals( 0, coolBar.getItemOrder()[ 2 ] );

    // Simulate that item is dragged onto itself -> nothing should change
    cba.setItemOrder( new int[] { 0, 1, 2, } );

    moveItem( item, item.getBounds().x + 2 );

    assertEquals( 0, coolBar.getItemOrder()[ 0 ] );
    assertEquals( 1, coolBar.getItemOrder()[ 1 ] );
    assertEquals( 2, coolBar.getItemOrder()[ 2 ] );

    // Simulate that item1 is before the first item
    cba.setItemOrder( new int[] { 0, 1, 2, } );

    moveItem( item1, item.getBounds().x - 5 );

    assertEquals( 1, coolBar.getItemOrder()[ 0 ] );
    assertEquals( 0, coolBar.getItemOrder()[ 1 ] );
    assertEquals( 2, coolBar.getItemOrder()[ 2 ] );
  }

  @Test
  public void testMoveItem_2() {
    item.setSize( 250, 25 );
    CoolItem item1 = new CoolItem( coolBar, SWT.NONE );
    item1.setSize( 250, 25 );
    // get adapter to set item order
    ICoolBarAdapter cba = coolBar.getAdapter( ICoolBarAdapter.class );

    // Drag item and drop it inside the bounds of item1
    cba.setItemOrder( new int[] { 0, 1 } );

    moveItem( item, 483 );

    assertEquals( 1, coolBar.getItemOrder()[ 0 ] );
    assertEquals( 0, coolBar.getItemOrder()[ 1 ] );

    // Drag item and drop it beyond the bounds of item1
    cba.setItemOrder( new int[] { 0, 1 } );

    moveItem( item, 2000 );

    assertEquals( 1, coolBar.getItemOrder()[ 0 ] );
    assertEquals( 0, coolBar.getItemOrder()[ 1 ] );
  }

}
