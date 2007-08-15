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

package org.eclipse.swt.graphics;

import junit.framework.TestCase;
import org.eclipse.swt.SWT;


public class Font_Test extends TestCase {
  
  public void testGetFont() {
    Font font = Font.getFont( "roman", 1, SWT.NORMAL );
    assertEquals( "roman", font.getFontData()[ 0 ].getName() );
    assertEquals( 1, font.getFontData()[ 0 ].getHeight() );
    assertEquals( SWT.NORMAL, font.getFontData()[ 0 ].getStyle() );
    Font sameFont = Font.getFont( "roman", 1, SWT.NORMAL );
    assertSame( font, sameFont );
    Font otherFont = Font.getFont( "arial", 2, SWT.NORMAL );
    assertTrue( otherFont != font );
    Font boldFont  = Font.getFont( "arial", 11, SWT.BOLD );
    assertTrue( ( boldFont.getFontData()[ 0 ].getStyle() & SWT.BOLD ) != 0 ) ;
    Font italicFont  = Font.getFont( "arial", 11, SWT.ITALIC );
    assertTrue( ( italicFont.getFontData()[ 0 ].getStyle() & SWT.ITALIC ) != 0 );
    
    sameFont = Font.getFont( new FontData( "roman", 1, SWT.NORMAL ) );
    assertSame( font, sameFont );
    assertSame( font.getFontData()[ 0 ], font.getFontData()[ 0 ] );
  }
  
  public void testGetFontData() {
    // Derive bold font from regular font 
    Font regularFont = Font.getFont( "roman", 1, SWT.NORMAL );
    FontData[] fontDatas = regularFont.getFontData();
    fontDatas[ 0 ] = new FontData( fontDatas[ 0 ].getName(),
                                   fontDatas[ 0 ].getHeight(),
                                   fontDatas[ 0 ].getStyle() | SWT.BOLD );
    Font boldFont = Font.getFont( fontDatas[ 0 ] );
    // Ensure bold font is actually bold
    assertEquals( boldFont.getFontData()[ 0 ].getStyle(), SWT.BOLD );
    // Ensure that the font we derived from stays the same
    assertEquals( regularFont.getFontData()[ 0 ].getStyle(), SWT.NORMAL );
  }
  
  public void testGetFontWithIllegalArguments() {
    try {
      Font.getFont( null, 1, SWT.NONE );
      fail( "The font name must not be null" );
    } catch( IllegalArgumentException e ) {
      // Expected
    }
    try {
      Font.getFont( "abc", -1, SWT.NONE );
      fail( "The font size must not be negative" );
    } catch( IllegalArgumentException e ) {
      // Expected
    }
  }
}
