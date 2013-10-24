/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.template;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.Test;


public class RowTemplate_Test {

  @Test( expected = IllegalArgumentException.class )
  public void testAddCellFailsWithNullCell() {
    RowTemplate template = new RowTemplate();

    template.addCell( null );
  }

  @Test
  public void testAddsCell() {
    RowTemplate template = new RowTemplate();
    Cell cell = mock( Cell.class );

    template.addCell( cell );

    List<Cell<?>> cells = template.getCells();
    assertEquals( 1, cells.size() );
    assertSame( cell, cells.get( 0 ) );
  }

  @Test
  public void testAddsCells() {
    RowTemplate template = new RowTemplate();
    Cell cell1 = mock( Cell.class );
    Cell cell2 = mock( Cell.class );

    template.addCell( cell1 );
    template.addCell( cell2 );

    List<Cell<?>> cells = template.getCells();
    assertEquals( 2, cells.size() );
    assertSame( cell1, cells.get( 0 ) );
    assertSame( cell2, cells.get( 1 ) );
  }

  @Test
  public void testCellsAreSafeCopy() {
    RowTemplate template = new RowTemplate();
    List<Cell<?>> cells = template.getCells();

    template.addCell( mock( Cell.class ) );

    assertEquals( 0, cells.size() );
  }
}
