/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
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

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.graphics.TextSizeProbeStore.Probe;

public class TextSizeEstimation_Test extends TestCase {

  private Font font10;

  public void testAvgCharWidth() {
    float avgCharWidth = TextSizeEstimation.getAvgCharWidth( font10 );
    assertTrue( avgCharWidth > 3 );
    assertTrue( avgCharWidth < 6 );
  }
  
  public void testAvgCharWithUsesProbeResults() {
    Probe probe = new Probe( "X", font10.getFontData()[ 0 ] );
    TextSizeProbeResults.getInstance().createProbeResult( probe, new Point( 4711, 0 ) );
    float avgCharWidth = TextSizeEstimation.getAvgCharWidth( font10 );
    assertEquals( 4711.0, avgCharWidth, 0.01 );
  }
  
  public void testCharHeight() {
    int charHeight = TextSizeEstimation.getCharHeight( font10 );
    assertTrue( charHeight > 9 );
    assertTrue( charHeight < 13 );
  }
  
  public void testStringExtent() {
    String string = "TestString";
    int charHeight = TextSizeEstimation.getCharHeight( font10 );
    Point extent10 = TextSizeEstimation.stringExtent( font10 , string );
    assertTrue( extent10.x > 30 );
    assertTrue( extent10.x < 60 );
    assertTrue( extent10.y >= charHeight );
    assertTrue( extent10.y < charHeight * 2 );
    Font font12 = Graphics.getFont( "Helvetica", 12, SWT.NORMAL );
    Point extent12 = TextSizeEstimation.stringExtent( font12 , string );
    assertTrue( extent12.x > extent10.x );
    string = "Test1 Test2 Test3 Test4 Test5";
    int width = 0;
    Point extent = TextSizeEstimation.textExtent( font10, string , width );
    assertTrue( extent.y >= charHeight );
    assertTrue( extent.y < charHeight * 2 );
    width = 40;
    extent = TextSizeEstimation.textExtent( font10, string , width );
    assertTrue( extent.x <= width );
    assertTrue( extent.y >= charHeight * 2 );
    assertTrue( extent.y < charHeight * 1.5 * 5 );
  }
  
  public void testTextExtent() {
    int charHeight = TextSizeEstimation.getCharHeight( font10 );
    assertTrue( charHeight > 9 );
    assertTrue( charHeight < 13 );
    String testString = "Test String";
    Point stringExtent = TextSizeEstimation.stringExtent( font10 , testString );
    assertTrue( stringExtent.x > 40 );
    assertTrue( stringExtent.x < 70 );
    assertTrue( stringExtent.y >= charHeight );
    assertTrue( stringExtent.y < charHeight * 1.5 );
    String testString2L = testString + "\r\n" + testString;
    Point textExtent2L = TextSizeEstimation.textExtent( font10 , testString2L, 0 );
    assertEquals( stringExtent.x, textExtent2L.x );
    assertTrue( textExtent2L.y >= charHeight * 2 );
    assertTrue( textExtent2L.y < charHeight * 3 );
    String testString5L = testString + "\r\n" + "\r\n" + testString + "\r\n" + "\r\n";
    Point textExtent5L = TextSizeEstimation.textExtent( font10 , testString5L, 0 );
    assertEquals( stringExtent.x, textExtent5L.x );
    assertTrue( textExtent5L.y >= charHeight * 5 );
    assertTrue( textExtent5L.y < charHeight * 5 + charHeight * 1.5 );
  }
  
  // Test for a case where text width == wrapWidth
  public void testEndlessLoopProblem() {
    Font font = Graphics.getFont( "Helvetica", 11, SWT.NORMAL );
    Point extent = TextSizeEstimation.textExtent( font, "Zusatzinfo (Besuch)", 100 );
    assertEquals( 100, extent.x );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    font10 = Graphics.getFont( "Helvetica", 10, SWT.NORMAL );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
