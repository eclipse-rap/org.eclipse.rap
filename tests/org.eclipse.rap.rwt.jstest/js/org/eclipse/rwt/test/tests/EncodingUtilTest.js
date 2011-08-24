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

qx.Class.define( "org.eclipse.rwt.test.tests.EncodingUtilTest", {

  extend : qx.core.Object,
  
  members : {
    
    //////////////////////////////////////////////////////
    // Tests ported from WidgetLCAUtil_Test#testEscapeTest
    
    testEscapeTextWithNull : function() {
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      try {
        encodingUtil.escapeText( null, true );
        encodingUtil.escapeText( null, true );
        fail();
      } catch( e ) {
        // expected
      }
    },
    
    testEscapeTextNoChanges : function() {
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      assertEquals( "Test", encodingUtil.escapeText( "Test", false ) );
      assertEquals( "Test", encodingUtil.escapeText( "Test", true ) );
      assertEquals( "", encodingUtil.escapeText( "", false ) );
      assertEquals( "", encodingUtil.escapeText( "", true ) );
    },

    testEscapeBrackets : function() {
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      assertEquals( "&lt;", encodingUtil.escapeText( "<", false ) );
      assertEquals( "&gt;", encodingUtil.escapeText( ">", false ) );
      assertEquals( "&lt;&lt;&lt;", encodingUtil.escapeText( "<<<", false ) );
      var expected = "&lt;File &gt;";
      assertEquals( expected, encodingUtil.escapeText( "<File >", false ) );
      assertEquals( expected, encodingUtil.escapeText( "<File >", true ) );
    },

    testEscapeAmps : function() {
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      assertEquals( "&amp;&amp;&amp;File", encodingUtil.escapeText( "&&&File", false ) );

    },

    testEscapeMnemonics : function() {
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      assertEquals( "Open &amp; Close", encodingUtil.escapeText( "Open && Close", true ) );
      assertEquals( "E&lt;s&gt;ca'pe &amp; me",
                    encodingUtil.escapeText( "&E<s>ca'pe && me", true ) );
    },

    testEscapeQuotes : function() {
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      assertEquals( "&quot;File&quot;", encodingUtil.escapeText( "\"File\"", false ) );
      assertEquals( "&quot;&quot;File", encodingUtil.escapeText( "\"\"File", false ) );
      assertEquals( "&quot;File&quot;", encodingUtil.escapeText( "\"File\"", true ) );
      assertEquals( "&quot;&quot;File", encodingUtil.escapeText( "\"\"File", true ) );
    },

    testDontEscapeBackslash: function() {
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      assertEquals( "Test\\", encodingUtil.escapeText( "Test\\", false ) );
    },

    testTruncateAtZero : function() {
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      assertEquals( String.fromCharCode( 0 ), "\000" );
      assertEquals( "foo ", encodingUtil.escapeText( "foo \000 bar", false ) );
      assertEquals( "foo", encodingUtil.escapeText( "foo\000", false ) );
      assertEquals( "", encodingUtil.escapeText( "\000foo", false ) );
      assertEquals( "&lt;foo", encodingUtil.escapeText( "<foo\000>", false ) );
      assertEquals( "&lt;foo", encodingUtil.escapeText( "<foo\000>", true ) );
    },
    
    /////////////////////////////////////////////////////////
    // Tests ported from EncodingUtilTest#testReplaceNewLines

    testReplaceNewLines : function() {
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      var stringToReplace = "First line.\nSecond line.\nThird line.";
      var expected = "First line.\\nSecond line.\\nThird line.";
      assertEquals( expected, encodingUtil.replaceNewLines( stringToReplace ) );
    },

    testReplaceCarriageReturns : function() {
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      var stringToReplace = "First line.\rSecond line.\rThird line.";
      var expected = "First line.\\nSecond line.\\nThird line.";
      assertEquals( expected, encodingUtil.replaceNewLines( stringToReplace ) );
    },

    testReplaceDOSNewLines : function() {
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      var stringToReplace = "First line.\r\nSecond line.\r\nThird line.";
      var expected = "First line.\\nSecond line.\\nThird line.";
      assertEquals( expected, encodingUtil.replaceNewLines( stringToReplace ) );
    },

    testReplaceNewLinesWithBlank : function() {
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      var stringToReplace = "First line.\r\nSecond line.\r\nThird line.";
      var expected = "First line. Second line. Third line.";
      assertEquals( expected, encodingUtil.replaceNewLines( stringToReplace, " " ) );
    },
    
    ////////////////////////////////////////////////////////////
    // Tests ported from EncodingUtilTest#testReplaceWhiteSpaces

    testDontReplaceSingleWhiteSpaces : function() {
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      var stringToEscape = "test1 test2";
      var expected = "test1 test2";
      assertEquals( expected, encodingUtil.replaceWhiteSpaces( stringToEscape ) );
    },

    testReplaceDoubleWhiteSpaces : function() {
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      var stringToEscape = "test1  test2";
      var expected = "test1&nbsp; test2";
      assertEquals( expected, encodingUtil.replaceWhiteSpaces( stringToEscape ) );
    },

    testReplaceMultipleWhiteSpaces : function() {
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      var stringToEscape = "test1   test2";
      var expected = "test1&nbsp;&nbsp; test2";
      assertEquals( expected, encodingUtil.replaceWhiteSpaces( stringToEscape ) );
    },

    testReplaceStartingWhiteSpace : function() {
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      var stringToEscape = " test";
      var expected = "&nbsp;test";
      assertEquals( expected, encodingUtil.replaceWhiteSpaces( stringToEscape ) );
    },

    testReplaceStartingAndEndingWhiteSpaces : function() {
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      var stringToEscape = "  test  ";
      var expected = "&nbsp; test&nbsp;&nbsp;";
      assertEquals( expected, encodingUtil.replaceWhiteSpaces( stringToEscape ) );
    }

  }
  
} );