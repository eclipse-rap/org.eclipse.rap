/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.textsize;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;


public class MeasurementItem_Test extends TestCase {
  private static final FontData FONT_DATA = new FontData( "arial", 12, SWT.BOLD );

  public void testMeasurementItemCreation() {
    MeasurementItem item = createItem( "textToMeasure", FONT_DATA, 13 );
    
    assertSame( FONT_DATA, item.getFontData() );
    assertSame( "textToMeasure", item.getTextToMeasure() );
    assertEquals( 13, item.getWrapWidth() );
  }
  
  public void testEquals() {
    FontData otherFontData = new FontData( "helvetia", 12, SWT.BOLD );
    MeasurementItem item1 = createItem( "textToMeasure", FONT_DATA, 13 );

    assertTrue( item1.equals( item1 ) );
    assertFalse( item1.equals( null ) );
    assertFalse( item1.equals( new Object() ) );
    assertFalse( item1.equals( createItem( "otherText", FONT_DATA, 13 ) ) );
    assertFalse( item1.equals( createItem( "textToMeasure", otherFontData, 13 ) ) );
    assertFalse( item1.equals( createItem( "textToMeasure", FONT_DATA, 155 ) ) );
    assertTrue( item1.equals( createItem( "textToMeasure", FONT_DATA, 13 ) ) );
  }
  
  public void testHashCode() {
    MeasurementItem item = createItem( "textToMeasure", FONT_DATA, 13 );
    
    int hashCode = item.hashCode();
    
    assertEquals( -1805266056, hashCode );
  }
  
  public void testParamTextToMeasureMustNotBeNull() {
    try {
      new MeasurementItem( null, FONT_DATA, 1 );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testParamFontDataMustNotBeNull() {
    try {
      new MeasurementItem( "textToMeasure", null, 1 );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testIsSerializable() throws Exception {
    MeasurementItem measurementItem = createItem( "text", FONT_DATA, 155 );
    
    MeasurementItem deserialized = Fixture.serializeAndDeserialize( measurementItem );
    
    assertEquals( measurementItem.getTextToMeasure(), deserialized.getTextToMeasure() );
    assertEquals( measurementItem.getFontData(), deserialized.getFontData() );
    assertEquals( measurementItem.getWrapWidth(), deserialized.getWrapWidth() );
  }
  
  private MeasurementItem createItem( String textToMeasure, FontData fontData, int wrapWidth ) {
    return new MeasurementItem( textToMeasure, fontData, wrapWidth );
  }
}
