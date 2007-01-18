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

package org.eclipse.rap.rwt.graphics;

import org.eclipse.rap.rwt.RWT;
import junit.framework.TestCase;


public class Font_Test extends TestCase {
  
  public void testGetFont() {
    Font font = Font.getFont( "roman", 1, RWT.NORMAL );
    assertEquals( "roman", font.getName() );
    assertEquals( 1, font.getSize() );
    assertEquals( RWT.NORMAL, font.getStyle() );
    Font sameFont = Font.getFont( "roman", 1, RWT.NORMAL );
    assertSame( font, sameFont );
    Font otherFont = Font.getFont( "arial", 2, RWT.NORMAL );
    assertTrue( otherFont != font );
    Font boldFont  = Font.getFont( "arial", 11, RWT.BOLD );
    assertTrue( ( boldFont.getStyle() & RWT.BOLD ) != 0 ) ;
    Font italicFont  = Font.getFont( "arial", 11, RWT.ITALIC );
    assertTrue( ( italicFont.getStyle() & RWT.ITALIC ) != 0 ) ;
  }
  
  public void testGetFontWithIllegalArguments() {
    try {
      Font.getFont( null, 1, RWT.NONE );
      fail( "The font name must not be null" );
    } catch( NullPointerException e ) {
      // Expected
    }
    try {
      Font.getFont( "abc", -1, RWT.NONE );
      fail( "The font size must not be negative" );
    } catch( IllegalArgumentException e ) {
      // Expected
    }
  }
}
