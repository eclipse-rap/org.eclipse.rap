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
package org.eclipse.rwt.internal.textsize;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;


public class MeasurementUtil_Test extends TestCase {
  
  public void testAddItemToMeasure() {
    initializeSessionWithDisplay();
    MeasurementItem item = createItem();
    
    MeasurementUtil.addItemToMeasure( item );

    checkMeasurementItemBuffering( item );
  }

  public void testAddItemToMeasureIsIdempotent() {
    MeasurementItem item = createItem();
    
    MeasurementUtil.addItemToMeasure( item );
    MeasurementUtil.addItemToMeasure( item );

    checkMeasurementItemBuffering( item );
  }
  
  public void testHasItemsToMeasure() {
    MeasurementItem item = createItem();
    MeasurementUtil.addItemToMeasure( item );

    boolean hasItemsToMeasure = MeasurementUtil.hasItemsToMeasure();
    
    assertTrue( hasItemsToMeasure );
  }
    
  public void testGetAndSetOfItemsToMeasure() {
    MeasurementItem item = createItem();
    
    MeasurementUtil.setItemsToMeasure( new MeasurementItem[] { item } );
    MeasurementItem[] items = MeasurementUtil.getItemsToMeasure();
    
    assertSame( item, items[ 0 ] );
  }
  
  public void testGetItemsToMeasureWithEmptyResult() {
    MeasurementItem[] items = MeasurementUtil.getItemsToMeasure();
    
    assertEquals( 0, items.length );
  }
  
  public void testContains() {
    MeasurementItem item1 = createItem();
    MeasurementItem item2 = createItem( item1.getFontData(), "otherText", item1.getWrapWidth() );

    MeasurementItem[] items = new MeasurementItem[] { item1 };
    boolean itemsContainItem1 = MeasurementUtil.contains( items, item1 );
    boolean itemsContainItem2 = MeasurementUtil.contains( items, item2 );

    assertTrue( itemsContainItem1 );
    assertFalse( itemsContainItem2 );
  }
  
  public void testConcatenate() {
    MeasurementItem item = createItem();
    
    MeasurementItem[] items = MeasurementUtil.concatenate( new MeasurementItem[ 0 ], item );
    
    checkItemConcatenation( item, items );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  private MeasurementItem createItem() {
    FontData fontData = new FontData( "arial", 13, SWT.BOLD );
    String textToMeasure = "textToMeasure";
    int wrapWidth = 2;
    return createItem( fontData, textToMeasure, wrapWidth );
  }

  private MeasurementItem createItem( FontData fontData, String textToMeasure, int wrapWidth ) {
    return new MeasurementItem( textToMeasure, fontData, wrapWidth );
  }
  
  private Display initializeSessionWithDisplay() {
    return new Display();
  }
  
  private void checkMeasurementItemBuffering( MeasurementItem item ) {
    assertEquals( 1, MeasurementUtil.getItemsToMeasure().length );
    assertSame( item, MeasurementUtil.getItemsToMeasure() [ 0 ] );
  }

  private void checkItemConcatenation( MeasurementItem item, MeasurementItem[] items ) {
    assertEquals( 1, items.length );
    assertSame( item, items[ 0 ] );
  }
}
