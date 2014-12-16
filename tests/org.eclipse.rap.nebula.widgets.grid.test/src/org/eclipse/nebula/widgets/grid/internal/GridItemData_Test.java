/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.grid.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class GridItemData_Test {

  private Display display;
  private GridItemData data;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    data = new GridItemData( 3 );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCreate_defaultValues() {
    assertNull( data.defaultFont );
    assertNull( data.defaultBackground );
    assertNull( data.defaultForeground );
    assertEquals( -1, data.customHeight );
    assertFalse( data.expanded );
    assertNull( data.children );
    assertNotNull( data.cellData );
  }

  @Test
  public void testCreate_zeroCells() {
    data = new GridItemData( 0 );

    assertEquals( 1, data.cellData.size() );
  }

  @Test
  public void testCreate_nonZeroCells() {
    assertEquals( 3, data.cellData.size() );
  }

  @Test
  public void testGetCellData() {
    assertNotNull( data.getCellData( 1 ) );
  }

  @Test( expected = IndexOutOfBoundsException.class )
  public void testGetCellData_invalidIndex() {
    data.getCellData( 4 );
  }

  @Test
  public void testAddCellData_atEnd() {
    data = new GridItemData( 1 );
    data.getCellData( 0 );

    data.addCellData( -1 );

    assertNotNull( data.cellData.get( 0 ) );
    assertNull( data.cellData.get( 1 ) );
  }

  @Test
  public void testAddCellData_atIndex() {
    data = new GridItemData( 1 );
    data.getCellData( 0 );

    data.addCellData( 0 );

    assertNull( data.cellData.get( 0 ) );
    assertNotNull( data.cellData.get( 1 ) );
  }

  @Test
  public void testRemoveCellData() {
    data = new GridItemData( 1 );

    data.removeCellData( 0 );

    assertEquals( 0, data.cellData.size() );
  }

  @Test
  public void testGetChildren() {
    assertNotNull( data.getChildren() );
  }

  @Test
  public void testClear() {
    data.defaultFont = new Font( display, "Arial", 14, SWT.NORMAL );
    data.defaultBackground = mock( Color.class );
    data.defaultForeground = mock( Color.class );
    data.customHeight = 10;
    data.expanded = true;
    data.getCellData( 1 );

    data.clear();

    assertEquals( 3, data.cellData.size() );
    assertNull( data.cellData.get( 1 ) );
    assertNull( data.defaultFont );
    assertNull( data.defaultBackground );
    assertNull( data.defaultForeground );
    assertEquals( 10, data.customHeight );
    assertTrue( data.expanded );
  }

}
