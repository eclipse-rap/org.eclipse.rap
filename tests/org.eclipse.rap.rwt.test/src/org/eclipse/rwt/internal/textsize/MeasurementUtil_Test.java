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
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;


public class MeasurementUtil_Test extends TestCase {
  
  public void testAddMeasurementItem() {
    new Display();
    MeasurementItem item = createItem();
    
    MeasurementUtil.addMeasurementItem( item );

    assertEquals( 1, MeasurementUtil.getMeasurementItems().length );
    assertSame( item, MeasurementUtil.getMeasurementItems() [ 0 ] );
    assertTrue( createHandlerRegistrar().isRegistered() );
  }
  
  public void testAddMeasurementItemIsIdempotent() {
    MeasurementItem item = createItem();
    
    MeasurementUtil.addMeasurementItem( item );
    MeasurementUtil.addMeasurementItem( item );

    assertEquals( 1, MeasurementUtil.getMeasurementItems().length );
    assertSame( item, MeasurementUtil.getMeasurementItems() [ 0 ] );
  }
  
  public void testRegister() {
    new Display();
    
    MeasurementUtil.register();
    
    assertTrue( createHandlerRegistrar().isRegistered() );
  }
  
  public void testIsDisplayRelatedUIThread() {
    new Display();
    
    boolean isDisplayRelatedUIThread = MeasurementUtil.isDisplayRelatedUIThread();
    
    assertTrue( isDisplayRelatedUIThread );
  }

  public void testIsNonDisplayRelatedUIThread() throws InterruptedException {
    new Display();
    
    boolean isDisplayRelatedUIThread = checkInNonUIThread();

    assertFalse( isDisplayRelatedUIThread );
  }
  
  public void testGetAndSetOfMeasurementItems() {
    MeasurementItem item = createItem();
    
    MeasurementUtil.setMeasurementItems( new MeasurementItem[] { item } );
    MeasurementItem[] items = MeasurementUtil.getMeasurementItems();
    
    assertSame( item, items[ 0 ] );
  }
  
  public void testGetMeasurementItemsWithEmptyResult() {
    MeasurementItem[] items = MeasurementUtil.getMeasurementItems();
    
    assertEquals( 0, items.length );
  }
  
  public void testIsEquals() {
    MeasurementItem item1 = createItem();
    FontData fontData = new FontData( "helvetia", 34, SWT.NONE );
    MeasurementItem item2 = createItem( fontData, item1.getTextToMeasure(), item1.getWrapWidth() );
    MeasurementItem item3 = createItem( item1.getFontData(), "otherText", item1.getWrapWidth() );
    MeasurementItem item4 = createItem( item1.getFontData(), item1.getTextToMeasure(), 23 );

    assertTrue( MeasurementUtil.isEquals( item1, item1 ) );
    assertFalse( MeasurementUtil.isEquals( item1, item2 ) );
    assertFalse( MeasurementUtil.isEquals( item1, item3 ) );
    assertFalse( MeasurementUtil.isEquals( item1, item4 ) );
  }
  
  public void testContains() {
    MeasurementItem item1 = createItem();
    MeasurementItem item2 = createItem( item1.getFontData(), "otherText", item1.getWrapWidth() );

    assertTrue( MeasurementUtil.contains( new MeasurementItem[] { item1 }, item1 ) );
    assertFalse( MeasurementUtil.contains( new MeasurementItem[] { item1 }, item2 ) );
  }
  
  public void testConcatenate() {
    MeasurementItem item = createItem();
    
    MeasurementItem[] items = MeasurementUtil.concatenate( new MeasurementItem[ 0 ], item );
    
    assertEquals( 1, items.length );
    assertSame( item, items[ 0 ] );
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
  

  private boolean checkInNonUIThread() throws InterruptedException {
    final boolean[] result = new boolean[ 1 ];
    Thread nonUIThread = new Thread( new Runnable() {
      public void run() {
        result[ 0 ] = MeasurementUtil.isDisplayRelatedUIThread();
      }
    } );
    nonUIThread.start();
    nonUIThread.join();
    return result[ 0 ];
  }

  private MeasurementHandlerRegistrar createHandlerRegistrar() {
    return new MeasurementHandlerRegistrar( ContextProvider.getSession(), RWT.getLifeCycle() );
  }
}
