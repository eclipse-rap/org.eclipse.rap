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

import org.junit.Test;


public class TextCell_Test {

  @Test
  public void testHasType() {
    TextCell cell = new TextCell( new RowTemplate() );

    String type = cell.getType();

    assertEquals( TextCell.TYPE_TEXT, type );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testSetDefaultTextFailsWithNullText() {
    TextCell cell = new TextCell( new RowTemplate() );

    cell.setDefaultText( null );
  }

  @Test
  public void testSetsDefaultText() {
    TextCell cell = new TextCell( new RowTemplate() );

    cell.setDefaultText( "foo" );

    Object actualText = cell.getAdapter( CellData.class ).getAttributes().get( TextCell.PROPERTY_DEFAULT_TEXT );
    assertEquals( "foo", actualText );
  }

  @Test
  public void testSetDefaultTextReturnsCell() {
    TextCell cell = new TextCell( new RowTemplate() );

    TextCell actualCell = cell.setDefaultText( "foo" );

    assertSame( cell, actualCell );
  }

  @Test
  public void testSetsWrapWithTrue() {
    TextCell cell = new TextCell( new RowTemplate() );

    cell.setWrap( true );

    Object actualStyle = cell.getAdapter( CellData.class ).getAttributes().get( TextCell.PROPERTY_WRAP );
    assertEquals( Boolean.TRUE, actualStyle );
  }

  @Test
  public void testSetsWrapWithFalse() {
    TextCell cell = new TextCell( new RowTemplate() );

    cell.setWrap( false );

    Object actualStyle = cell.getAdapter( CellData.class ).getAttributes().get( TextCell.PROPERTY_WRAP );
    assertEquals( Boolean.FALSE, actualStyle );
  }

  @Test
  public void testSetStyleReturnsCell() {
    TextCell cell = new TextCell( new RowTemplate() );

    TextCell actualCell = cell.setWrap( true );

    assertSame( cell, actualCell );
  }
}
