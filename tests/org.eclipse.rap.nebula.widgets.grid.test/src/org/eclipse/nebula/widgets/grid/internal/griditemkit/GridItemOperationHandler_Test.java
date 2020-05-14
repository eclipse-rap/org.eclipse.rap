/*******************************************************************************
 * Copyright (c) 2013, 2020 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.grid.internal.griditemkit;

import static org.eclipse.nebula.widgets.grid.GridTestUtil.createGridColumns;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings( {
  "restriction", "deprecation"
} )
public class GridItemOperationHandler_Test {

  private Grid grid;
  private GridItem item;
  private GridItemOperationHandler handler;

  @Before
  public void setUp() {
    Fixture.setUp();
    Display display = new Display();
    Shell shell = new Shell( display, SWT.NONE );
    grid = new Grid( shell, SWT.CHECK );
    grid.setBounds( 0, 0, 100, 100 );
    item = new GridItem( grid, SWT.NONE );
    new GridItem( item, SWT.NONE );
    handler = new GridItemOperationHandler( item );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testHandleSetChecked() {
    createGridColumns( grid, 3, SWT.NONE );

    JsonArray cellChecked = new JsonArray()
      .add( false )
      .add( true )
      .add( false )
      .add( true );
    handler.handleSet( new JsonObject().add( "cellChecked", cellChecked ) );

    assertTrue( item.getChecked( 0 ) );
    assertFalse( item.getChecked( 1 ) );
    assertTrue( item.getChecked( 2 ) );
  }

  @Test
  public void testHandleSetЕxpanded_expand() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );

    handler.handleSet( new JsonObject().add( "expanded", true ) );

    assertTrue( item.isExpanded() );
  }

  @Test
  public void testHandleSetЕxpanded_collaps() {
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    item.setExpanded( true );

    handler.handleSet( new JsonObject().add( "expanded", false ) );

    assertFalse( item.isExpanded() );
  }

  @Test
  public void testHandleSetHeight() {
    handler.handleSet( new JsonObject().add( "height", 123 ) );

    assertEquals( 123, item.getHeight() );
  }

}
