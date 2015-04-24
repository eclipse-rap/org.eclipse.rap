/*******************************************************************************
 * Copyright (c) 2009, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.util;

import static org.eclipse.rap.rwt.internal.util.EncodingUtil.replaceNewLines;
import static org.eclipse.rap.rwt.internal.util.EncodingUtil.splitNewLines;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import org.junit.Test;


public class EncodingUtil_Test {

  @Test
  public void testReplaceNewLines_emptyString() {
    assertSame( "", replaceNewLines( "" ) );
  }

  @Test
  public void testReplaceNewLines_withoutLineBreaks() {
    assertSame( "foo bar", replaceNewLines( "foo bar" ) );
  }

  @Test
  public void testReplaceNewLines_unixLineBreaks() {
    String stringToReplace = "First line.\nSecond line.\nThird line.";
    String expected = "First line.\\nSecond line.\\nThird line.";
    String result = replaceNewLines( stringToReplace );

    assertEquals( expected, result );
  }

  @Test
  public void testReplaceNewLines_windowsLineBreaks() {
    String stringToReplace = "First line.\r\nSecond line.\r\nThird line.";
    String expected = "First line.\\nSecond line.\\nThird line.";
    String result = replaceNewLines( stringToReplace );

    assertEquals( expected, result );
  }

  @Test
  public void testReplaceNewLines_oldMacLineBreaks() {
    String stringToReplace = "First line.\rSecond line.\rThird line.";
    String expected = "First line.\\nSecond line.\\nThird line.";
    String result = replaceNewLines( stringToReplace );

    assertEquals( expected, result );
  }

  @Test
  public void testReplaceNewLines_edgeCases() {
    assertEquals( "\\nfoo", replaceNewLines( "\nfoo" ) );
    assertEquals( "\\nfoo", replaceNewLines( "\r\nfoo" ) );
    assertEquals( "foo\\n", replaceNewLines( "foo\n" ) );
    assertEquals( "foo\\n", replaceNewLines( "foo\r\n" ) );
    assertEquals( "\\n", replaceNewLines( "\n" ) );
    assertEquals( "\\n", replaceNewLines( "\r\n" ) );
  }

  @Test
  public void testReplaceNewLines_withReplacement() {
    String stringToReplace = "First line.\r\nSecond line.\r\nThird line.";
    String expected = "First line. Second line. Third line.";
    String result = replaceNewLines( stringToReplace, " " );

    assertEquals( expected, result );
  }

  @Test
  public void testSplitNewlines_emptyString() {
    assertArrayEquals( new String[] { "" }, splitNewLines( "" ) );
  }

  @Test
  public void testSplitNewlines_withoutLineBreaks() {
    assertArrayEquals( new String[] { "foo bar" }, splitNewLines( "foo bar" ) );
  }

  @Test
  public void testSplitNewlines_unixLineBreaks() {
    String input = "First line.\nSecond line.\nThird line.";
    String[] expected = { "First line.", "Second line.", "Third line." };

    assertArrayEquals( expected, splitNewLines( input ) );
  }

  @Test
  public void testSplitNewlines_windowsLineBreaks() {
    String input = "First line.\r\nSecond line.\r\nThird line.";
    String[] expected = { "First line.", "Second line.", "Third line." };

    assertArrayEquals( expected, splitNewLines( input ) );
  }

  @Test
  public void testSplitNewlines_oldMacLineBreaks() {
    String input = "First line.\rSecond line.\rThird line.";
    String[] expected = { "First line.", "Second line.", "Third line." };

    assertArrayEquals( expected, splitNewLines( input ) );
  }

  @Test
  public void testSplitNewlines_edgeCases() {
    assertArrayEquals( new String[] { "", "foo" }, splitNewLines( "\nfoo" ) );
    assertArrayEquals( new String[] { "", "foo" }, splitNewLines( "\r\nfoo" ) );
    assertArrayEquals( new String[] { "foo", "" }, splitNewLines( "foo\n" ) );
    assertArrayEquals( new String[] { "foo", "" }, splitNewLines( "foo\r\n" ) );
    assertArrayEquals( new String[] { "", "" }, splitNewLines( "\n" ) );
    assertArrayEquals( new String[] { "", "" }, splitNewLines( "\r\n" ) );
  }

}
