/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var EncodingUtil = rwt.util.Encoding;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.EncodingUtilTest", {

  extend : rwt.qx.Object,

  members : {

    //////////////////////////////////////////////////////
    // Tests ported from WidgetLCAUtil_Test#testEscapeTest

    testEscapeTextWithNull : function() {
      try {
        EncodingUtil.escapeText( null, true );
        EncodingUtil.escapeText( null, true );
        fail();
      } catch( e ) {
        // expected
      }
    },

    testEscapeTextNoChanges : function() {
      assertEquals( "Test", EncodingUtil.escapeText( "Test", false ) );
      assertEquals( "Test", EncodingUtil.escapeText( "Test", true ) );
      assertEquals( "", EncodingUtil.escapeText( "", false ) );
      assertEquals( "", EncodingUtil.escapeText( "", true ) );
    },

    testEscapeBrackets : function() {
      assertEquals( "&lt;", EncodingUtil.escapeText( "<", false ) );
      assertEquals( "&gt;", EncodingUtil.escapeText( ">", false ) );
      assertEquals( "&lt;&lt;&lt;", EncodingUtil.escapeText( "<<<", false ) );
      var expected = "&lt;File &gt;";
      assertEquals( expected, EncodingUtil.escapeText( "<File >", false ) );
      assertEquals( expected, EncodingUtil.escapeText( "<File >", true ) );
    },

    testEscapeAmps : function() {
      assertEquals( "&amp;&amp;&amp;File", EncodingUtil.escapeText( "&&&File", false ) );
    },

    testEscapeMnemonics : function() {
      assertEquals( "Open &amp; Close", EncodingUtil.escapeText( "Open && Close", true ) );
      assertEquals( "E&lt;s&gt;ca'pe&quot; &amp; me",
                    EncodingUtil.escapeText( "&E<s>ca'pe\" && me", true ) );
      // Explicitly call it twice to check that _mnemonicFound is reset
      assertEquals( "E&lt;s&gt;ca'pe&quot; &amp; me",
                    EncodingUtil.escapeText( "&E<s>ca'pe\" && me", true ) );
    },

    testEscapeQuotes : function() {
      assertEquals( "&quot;File&quot;", EncodingUtil.escapeText( "\"File\"", false ) );
      assertEquals( "&quot;&quot;File", EncodingUtil.escapeText( "\"\"File", false ) );
      assertEquals( "&quot;File&quot;", EncodingUtil.escapeText( "\"File\"", true ) );
      assertEquals( "&quot;&quot;File", EncodingUtil.escapeText( "\"\"File", true ) );
    },

    testDontEscapeBackslash: function() {
      assertEquals( "Test\\", EncodingUtil.escapeText( "Test\\", false ) );
    },

    testTruncateAtZero : function() {
      assertEquals( String.fromCharCode( 0 ), "\000" );
      assertEquals( "foo ", EncodingUtil.escapeText( "foo \000 bar", false ) );
      assertEquals( "foo", EncodingUtil.escapeText( "foo\000", false ) );
      assertEquals( "", EncodingUtil.escapeText( "\000foo", false ) );
      assertEquals( "&lt;foo", EncodingUtil.escapeText( "<foo\000>", false ) );
      assertEquals( "&lt;foo", EncodingUtil.escapeText( "<foo\000>", true ) );
    },

    /////////////////////////////////////////////////////////
    // Tests ported from EncodingUtilTest#testReplaceNewLines

    testReplaceNewLines : function() {
      var stringToReplace = "First line.\nSecond line.\nThird line.";
      var expected = "First line.\\nSecond line.\\nThird line.";
      assertEquals( expected, EncodingUtil.replaceNewLines( stringToReplace ) );
    },

    testReplaceCarriageReturns : function() {
      var stringToReplace = "First line.\rSecond line.\rThird line.";
      var expected = "First line.\\nSecond line.\\nThird line.";
      assertEquals( expected, EncodingUtil.replaceNewLines( stringToReplace ) );
    },

    testReplaceDOSNewLines : function() {
      var stringToReplace = "First line.\r\nSecond line.\r\nThird line.";
      var expected = "First line.\\nSecond line.\\nThird line.";
      assertEquals( expected, EncodingUtil.replaceNewLines( stringToReplace ) );
    },

    testReplaceNewLinesWithBlank : function() {
      var stringToReplace = "First line.\r\nSecond line.\r\nThird line.";
      var expected = "First line. Second line. Third line.";
      assertEquals( expected, EncodingUtil.replaceNewLines( stringToReplace, " " ) );
    },

    ////////////////////////////////////////////////////////////
    // Tests ported from EncodingUtilTest#testReplaceWhiteSpaces

    testDontReplaceSingleWhiteSpaces : function() {
      var stringToEscape = "test1 test2";
      var expected = "test1 test2";
      assertEquals( expected, EncodingUtil.replaceWhiteSpaces( stringToEscape ) );
    },

    testReplaceDoubleWhiteSpaces : function() {
      var stringToEscape = "test1  test2";
      var expected = "test1&nbsp; test2";
      assertEquals( expected, EncodingUtil.replaceWhiteSpaces( stringToEscape ) );
    },

    testReplaceMultipleWhiteSpaces : function() {
      var stringToEscape = "test1   test2";
      var expected = "test1&nbsp;&nbsp; test2";
      assertEquals( expected, EncodingUtil.replaceWhiteSpaces( stringToEscape ) );
    },

    testReplaceStartingWhiteSpace : function() {
      var stringToEscape = " test";
      var expected = "&nbsp;test";
      assertEquals( expected, EncodingUtil.replaceWhiteSpaces( stringToEscape ) );
    },

    testReplaceStartingAndEndingWhiteSpaces : function() {
      var stringToEscape = "  test  ";
      var expected = "&nbsp; test&nbsp;&nbsp;";
      assertEquals( expected, EncodingUtil.replaceWhiteSpaces( stringToEscape ) );
    },

    /////////////////////////////////////////////////////////////////////
    // Tests ported from EncodingUtilTest#testEscapeLeadingTrailingSpaces

    testEscapeBothLeadingTrailingSpaces : function() {
      var stringToEscape = "    All rights reserved.   ";
      var expected = "&nbsp;&nbsp;&nbsp;&nbsp;All rights reserved.&nbsp;&nbsp;&nbsp;";
      assertEquals( expected, EncodingUtil.escapeLeadingTrailingSpaces( stringToEscape ) );
    },

    testEscapeOnlyTrailingSpaces : function() {
      var stringToEscape = "All rights reserved. ";
      var expected = "All rights reserved.&nbsp;";
      assertEquals( expected, EncodingUtil.escapeLeadingTrailingSpaces( stringToEscape ) );
    },

    testEscapeOnlyLeadingSpaces : function() {
      var stringToEscape = "  All rights reserved.";
      var expected = "&nbsp;&nbsp;All rights reserved.";
      assertEquals( expected, EncodingUtil.escapeLeadingTrailingSpaces( stringToEscape ) );
    },

    testEscapeNoLeadingTrailingSpaces : function() {
      var stringToEscape = "All rights reserved.";
      var expected = "All rights reserved.";
      assertEquals( expected, EncodingUtil.escapeLeadingTrailingSpaces( stringToEscape ) );
    },

    testEscapeLeadingTrailingSpacesWithNewLines : function() {
      var stringToEscape = " \n  All rights reserved. \n ";
      var expected = "&nbsp;\n  All rights reserved. \n&nbsp;";
      assertEquals( expected, EncodingUtil.escapeLeadingTrailingSpaces( stringToEscape ) );
    },

    testEscapeLeadingTrailingSpacesWithWhitespaceString : function() {
      var stringToEscape = "  ";
      var expected = "&nbsp;&nbsp;";
      assertEquals( expected, EncodingUtil.escapeLeadingTrailingSpaces( stringToEscape ) );
    },

    testEscapeLeadingTrailingSpacesWithEmptyString : function() {
      var stringToEscape = "";
      var expected = "";
      assertEquals( expected, EncodingUtil.escapeLeadingTrailingSpaces( stringToEscape ) );
    },

    /////////////////////////////////
    // New tests for mnemonic support

    testRenderMnemonicChar : function() {
      var expected = "Pu<span style=\"text-decoration:underline\">s</span>h";
      assertEquals( expected, EncodingUtil.escapeText( "Push", 2 ) );
    }

  }

} );

}());