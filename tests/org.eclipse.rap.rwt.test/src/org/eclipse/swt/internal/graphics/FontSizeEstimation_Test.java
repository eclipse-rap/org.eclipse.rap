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

package org.eclipse.swt.internal.graphics;

import junit.framework.TestCase;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;

public class FontSizeEstimation_Test extends TestCase {

  public void test1() {
    Font font10 = Font.getFont( "Helvetica", 10, SWT.NORMAL );
    float avgCharWidth = FontSizeEstimation.getAvgCharWidth( font10 );
    assertTrue( avgCharWidth > 3 );
    assertTrue( avgCharWidth < 6 );
    int charHeight = FontSizeEstimation.getCharHeight( font10 );
    assertTrue( charHeight > 9 );
    assertTrue( charHeight < 13 );
    String string = "TestString";
    Point extent10 = FontSizeEstimation.stringExtent( string , font10 );
    assertTrue( extent10.x > 30 );
    assertTrue( extent10.x < 60 );
    assertTrue( extent10.y >= charHeight );
    assertTrue( extent10.y < charHeight * 2 );
    Font font12 = Font.getFont( "Helvetica", 12, SWT.NORMAL );
    Point extent12 = FontSizeEstimation.stringExtent( string , font12 );
    assertTrue( extent12.x > extent10.x );
    string = "Test1 Test2 Test3 Test4 Test5";
    int width = 0;
    Point extent = FontSizeEstimation.textExtent( string, width , font10 );
    assertTrue( extent.y >= charHeight );
    assertTrue( extent.y < charHeight * 2 );
    width = 40;
    extent = FontSizeEstimation.textExtent( string, width , font10 );
    assertTrue( extent.x <= width );
    assertTrue( extent.y >= charHeight * 2 );
    assertTrue( extent.y < charHeight * 1.5 * 5 );
  }
  
  // Test for a case where text width == wrapWidth
  public void testEndlessLoopProblem() {
    Font font = Font.getFont( "Helvetica", 11, SWT.NORMAL );
    Point extent = FontSizeEstimation.textExtent( "Zusatzinfo (Besuch)",
                                                  100,
                                                  font );
    assertEquals( 100, extent.x );
  }
}
