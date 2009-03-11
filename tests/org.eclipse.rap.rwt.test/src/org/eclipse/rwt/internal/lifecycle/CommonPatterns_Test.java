/*******************************************************************************
 * Copyright (c) 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import junit.framework.TestCase;

public class CommonPatterns_Test extends TestCase {

  public void testEscapeDoubleQuoted() {
    String stringToEscape = "First line.\nSecond \"middle\" line.\nThird line.";
    String expected = "First line.\nSecond \\\"middle\\\" line.\nThird line.";
    String result = CommonPatterns.escapeDoubleQuoted( stringToEscape );
    assertEquals( expected, result );

    stringToEscape = "First line.\nSecond \\middle\\ line.\nThird line.";
    expected = "First line.\nSecond \\\\middle\\\\ line.\nThird line.";
    result = CommonPatterns.escapeDoubleQuoted( stringToEscape );
    assertEquals( expected, result );
  }

  public void testReplaceNewLines() {
    String stringToReplace = "First line.\nSecond line.\nThird line.";
    String expected = "First line.\\nSecond line.\\nThird line.";
    String result = CommonPatterns.replaceNewLines( stringToReplace );
    assertEquals( expected, result );

    stringToReplace = "First line.\rSecond line.\rThird line.";
    expected = "First line.\\nSecond line.\\nThird line.";
    result = CommonPatterns.replaceNewLines( stringToReplace );
    assertEquals( expected, result );

    stringToReplace = "First line.\r\nSecond line.\r\nThird line.";
    expected = "First line.\\nSecond line.\\nThird line.";
    result = CommonPatterns.replaceNewLines( stringToReplace );
    assertEquals( expected, result );

    stringToReplace = "First line.\r\nSecond line.\r\nThird line.";
    expected = "First line. Second line. Third line.";
    result = CommonPatterns.replaceNewLines( stringToReplace, " " );
    assertEquals( expected, result );
  }

  public void testEscapeLeadingTrailingSpaces() {
    String stringToEscape = "  All rights reserved.   ";
    String expected = "&nbsp;&nbsp;All rights reserved.&nbsp;&nbsp;&nbsp;";
    String result = CommonPatterns.escapeLeadingTrailingSpaces( stringToEscape );
    assertEquals( expected, result );

    stringToEscape = "All rights reserved. ";
    expected = "All rights reserved.&nbsp;";
    result = CommonPatterns.escapeLeadingTrailingSpaces( stringToEscape );
    assertEquals( expected, result );

    stringToEscape = "  All rights reserved.";
    expected = "&nbsp;&nbsp;All rights reserved.";
    result = CommonPatterns.escapeLeadingTrailingSpaces( stringToEscape );
    assertEquals( expected, result );

    stringToEscape = "All rights reserved.";
    expected = "All rights reserved.";
    result = CommonPatterns.escapeLeadingTrailingSpaces( stringToEscape );
    assertEquals( expected, result );

    stringToEscape = " \n  All rights reserved. \n ";
    expected = "&nbsp;\n  All rights reserved. \n&nbsp;";
    result = CommonPatterns.escapeLeadingTrailingSpaces( stringToEscape );
    assertEquals( expected, result );

    stringToEscape = "  ";
    expected = "&nbsp;&nbsp;";
    result = CommonPatterns.escapeLeadingTrailingSpaces( stringToEscape );
    assertEquals( expected, result );
  }
}
