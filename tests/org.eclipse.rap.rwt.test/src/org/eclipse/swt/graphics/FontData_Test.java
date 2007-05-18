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


public class FontData_Test extends TestCase {
  
  public void testFontData() {    
    FontData fontData = new FontData( "roman", 1, SWT.NORMAL );
    assertEquals( "roman", fontData.getName() );
    assertEquals( 1, fontData.getHeight() );
    assertEquals( SWT.NORMAL, fontData.getStyle() );
    
    fontData = new FontData( "1|roman|1|0|" );
    assertEquals( "roman", fontData.getName() );
    assertEquals( 1, fontData.getHeight() );
    assertEquals( SWT.NORMAL, fontData.getStyle() );
  }
  
  public void testEquality() {
    FontData fontData1 = new FontData( "roman", 1, SWT.NORMAL );
    FontData fontData2 = new FontData( "roman", 1, SWT.NORMAL );
    assertTrue( fontData1.equals( fontData2 ) );
    assertFalse( fontData1.equals( null ) );
    assertFalse( fontData1.equals( new Object() ) );
    FontData fontData3 = new FontData( "roman", 1, SWT.BOLD );
    assertFalse( fontData1.equals( fontData3 ) );
    fontData3 = new FontData( "roman", 2, SWT.NORMAL );
    assertFalse( fontData1.equals( fontData3 ) );
    fontData3 = new FontData( "arial", 1, SWT.NORMAL );
    assertFalse( fontData1.equals( fontData3 ) );
  }
}
