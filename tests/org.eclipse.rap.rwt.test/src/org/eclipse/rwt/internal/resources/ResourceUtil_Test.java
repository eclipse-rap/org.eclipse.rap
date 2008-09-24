/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.resources;

import junit.framework.TestCase;

public class ResourceUtil_Test extends TestCase {

  public void testRemoveOneLineComments() {
    StringBuffer javaScript = new StringBuffer( "" );
    ResourceUtil.removeOneLineComments( javaScript );
    assertEquals( "", javaScript.toString() );

    javaScript = new StringBuffer( "// the one and only" );
    ResourceUtil.removeOneLineComments( javaScript );
    assertEquals( "", javaScript.toString() );

    javaScript = new StringBuffer( "// the one and only\nfunction xy(){}" ) ;
    ResourceUtil.removeOneLineComments( javaScript );
    assertEquals( "\nfunction xy(){}", javaScript.toString() );

    javaScript = new StringBuffer( "// the one and only\n\rfunction xy(){}" ) ;
    ResourceUtil.removeOneLineComments( javaScript );
    assertEquals( "\n\rfunction xy(){}", javaScript.toString() );

    javaScript = new StringBuffer( "// the one and only\rfunction xy(){}" ) ;
    ResourceUtil.removeOneLineComments( javaScript );
    assertEquals( "\rfunction xy(){}", javaScript.toString() );

    javaScript = new StringBuffer( "////" ) ;
    ResourceUtil.removeOneLineComments( javaScript );
    assertEquals( "", javaScript.toString() );

    javaScript = new StringBuffer( "//// \nfunction xyz(){}" ) ;
    ResourceUtil.removeOneLineComments( javaScript );
    assertEquals( "\nfunction xyz(){}", javaScript.toString() );

    javaScript = new StringBuffer( "// line1\n//line 2\nfunction xyz(){}" ) ;
    ResourceUtil.removeOneLineComments( javaScript );
    assertEquals( "\n\nfunction xyz(){}", javaScript.toString() );

    // tests for comments inside strings
    javaScript = new StringBuffer( "function() { var foo = \"//\"; }" );
    ResourceUtil.removeOneLineComments( javaScript );
    assertEquals( "function() { var foo = \"//\"; }", javaScript.toString() );

    javaScript = new StringBuffer( "function() { var foo = '//'; }" );
    ResourceUtil.removeOneLineComments( javaScript );
    assertEquals( "function() { var foo = '//'; }", javaScript.toString() );

    javaScript
      = new StringBuffer( "var a = '\"'; function() { var foo = \"//\"; }" );
    ResourceUtil.removeOneLineComments( javaScript );
    assertEquals( "var a = '\"'; function() { var foo = \"//\"; }",
                  javaScript.toString() );

    javaScript
      = new StringBuffer( "var a = \"'\"; function() { var foo = \"//\"; }" );
    ResourceUtil.removeOneLineComments( javaScript );
    assertEquals( "var a = \"'\"; function() { var foo = \"//\"; }",
                  javaScript.toString() );

    javaScript
      = new StringBuffer( "var a = \"\\\"\"; function() { var foo = \"//\"; }" );
    ResourceUtil.removeOneLineComments( javaScript );
    assertEquals( "var a = \"\\\"\"; function() { var foo = \"//\"; }",
                  javaScript.toString() );
  }

  public void testRemoveMultiLineComments() {
    StringBuffer javaScript = new StringBuffer( "/**/" ) ;
    ResourceUtil.removeMultiLineComments( javaScript );
    assertEquals( "", javaScript.toString() );

    javaScript = new StringBuffer( "/**/noMoreComment" ) ;
    ResourceUtil.removeMultiLineComments( javaScript );
    assertEquals( "noMoreComment", javaScript.toString() );

    javaScript
      = new StringBuffer( "/*a\ncomment\nwith\nlineBreaks*/noMoreComment" ) ;
    ResourceUtil.removeMultiLineComments( javaScript );
    assertEquals( "noMoreComment", javaScript.toString() );

    javaScript
      = new StringBuffer( "/** JavaDoc-like comment */noMoreComment" ) ;
    ResourceUtil.removeMultiLineComments( javaScript );
    assertEquals( "noMoreComment", javaScript.toString() );

    javaScript
      = new StringBuffer( "/* */noMoreComment" ) ;
    ResourceUtil.removeMultiLineComments( javaScript );
    assertEquals( "noMoreComment", javaScript.toString() );

    // tests for comments inside strings
    javaScript = new StringBuffer( "function() { var foo = \"/* */\"; }" );
    ResourceUtil.removeMultiLineComments( javaScript );
    assertEquals( "function() { var foo = \"/* */\"; }",
                  javaScript.toString() );

    javaScript
      = new StringBuffer( "function() { var foo = \"/*\"; var bar = \"*/\"; }");
    ResourceUtil.removeMultiLineComments( javaScript );
    assertEquals( "function() { var foo = \"/*\"; var bar = \"*/\"; }",
                  javaScript.toString() );
  }

  public void testRemoveMultipleBlanks() {
    StringBuffer javaScript = new StringBuffer( "  " ) ;
    ResourceUtil.removeMultipleBlanks( javaScript );
    assertEquals( " ", javaScript.toString() );

    javaScript = new StringBuffer( "   " ) ;
    ResourceUtil.removeMultipleBlanks( javaScript );
    assertEquals( " ", javaScript.toString() );

    javaScript
      = new StringBuffer( "  some  Text  with        loads of    blanks" ) ;
    ResourceUtil.removeMultipleBlanks( javaScript );
    assertEquals( " some Text with loads of blanks", javaScript.toString() );

    javaScript
      = new StringBuffer( "function() { var foo = \"Text  with  spaces\"; }" ) ;
    ResourceUtil.removeMultipleBlanks( javaScript );
    assertEquals( "function() { var foo = \"Text  with  spaces\"; }",
                  javaScript.toString() );
  }

  public void testRemoveLeadingBlanks() {
    StringBuffer javaScript
      = new StringBuffer( " function() { var foo = \"\"; }\r\n var a = 1;" );
    ResourceUtil.removeLeadingBlanks( javaScript );
    assertEquals( "function() { var foo = \"\"; }\r\nvar a = 1;",
                  javaScript.toString() );

    javaScript
      = new StringBuffer( "   function() {\n var foo = \"\"; }\r\n  var a = 1;" );
    ResourceUtil.removeLeadingBlanks( javaScript );
    assertEquals( "function() {\nvar foo = \"\"; }\r\nvar a = 1;",
                  javaScript.toString() );
  }

  public void testRemoveMultipleNewLines() {
    StringBuffer javaScript
      = new StringBuffer( "aaa\r\n\r\nbbbbb\r\n\r\n\r\nccccccc\r\nddddddddd" );
    ResourceUtil.removeMultipleNewLines( javaScript, "\r\n" );
    assertEquals( "aaa\r\nbbbbb\r\nccccccc\r\nddddddddd",
                  javaScript.toString() );

    javaScript
      = new StringBuffer( "aaa\r \rbbbbb\r\r\rccccccc\rddddddddd" );
    ResourceUtil.removeMultipleNewLines( javaScript, "\r" );
    assertEquals( "aaa\r \rbbbbb\rccccccc\rddddddddd",
                  javaScript.toString() );

    javaScript
      = new StringBuffer( "aaa\n \nbbbbb\n\n\nccccccc\nddddddddd" );
    ResourceUtil.removeMultipleNewLines( javaScript, "\n" );
    assertEquals( "aaa\n \nbbbbb\nccccccc\nddddddddd",
                  javaScript.toString() );
  }

  public void testReplace() {
    StringBuffer javaScript
      = new StringBuffer( "function() { var foo = \"\"; }" );
    ResourceUtil.replace( javaScript, " {", "{" );
    assertEquals( "function(){ var foo = \"\"; }",
                  javaScript.toString() );

    ResourceUtil.replace( javaScript, " = ", "=" );
    assertEquals( "function(){ var foo=\"\"; }",
                  javaScript.toString() );

    ResourceUtil.replace( javaScript, " }", "}" );
    assertEquals( "function(){ var foo=\"\";}",
                  javaScript.toString() );

    // Test to not replace inside strings
    javaScript
      = new StringBuffer( "function() { var foo = \" { = \"; }" );
    ResourceUtil.replace( javaScript, " {", "{" );
    assertEquals( "function(){ var foo = \" { = \"; }",
                  javaScript.toString() );

    ResourceUtil.replace( javaScript, " = ", "=" );
    assertEquals( "function(){ var foo=\" { = \"; }",
                  javaScript.toString() );
  }

  public void testCompress() throws Exception {
    StringBuffer javaScript = new StringBuffer(
        "/********************************************************\n"
      + "* Copyright (c) 2008 Innoopract Informationssysteme GmbH.\n"
      + "********************************************************/\n"
      + "\n"
      + "qx.Class.define( \"org.eclipse.swt.widgets.Test\", {\n"
      + "  extend : qx.ui.layout.CanvasLayout,\n"
      + "\n"
      + "  construct : function( style ) {\n"
      + "    this.base( arguments );\n"
      + "  },\n"
      + "\n"
      + "  members : {\n"
      + "    // TODO: Fix me\n"
      + "    setValue : function( value ) {\n"
      + "      this._value = value;\n"
      + "      this._url = \"http://www.eclipse.org\";\n"
      + "      this._comment = \"/* This is a comment inside string*/\";\n"
      + "    }\n"
      + "  }\n"
      + "} );"
    );
    String expected
      = "\nqx.Class.define(\"org.eclipse.swt.widgets.Test\",{"
      + "extend:qx.ui.layout.CanvasLayout,"
      + "construct:function(style){"
      + "this.base(arguments);"
      + "},"
      + "members:{"
      + "setValue:function(value){"
      + "this._value=value;"
      + "this._url=\"http://www.eclipse.org\";"
      + "this._comment=\"/* This is a comment inside string*/\";"
      + "}}});";
    ResourceUtil.compress( javaScript );
    assertEquals( expected, javaScript.toString() );
  }

  public void testGetLineAtPosition() {
    StringBuffer javaScript
      = new StringBuffer( "aaa\rbbbbb\nccccccc\r\nddddddddd" );
    String line = ResourceUtil.getLineAtPosition( javaScript, 1 );
    assertEquals( "aaa", line );

    line = ResourceUtil.getLineAtPosition( javaScript, 6 );
    assertEquals( "bbbbb", line );

    line = ResourceUtil.getLineAtPosition( javaScript, 12 );
    assertEquals( "ccccccc", line );

    line = ResourceUtil.getLineAtPosition( javaScript, 22 );
    assertEquals( "ddddddddd", line );

    line = ResourceUtil.getLineAtPosition( javaScript, -35 );
    assertEquals( "", line );

    line = ResourceUtil.getLineAtPosition( javaScript, 35 );
    assertEquals( "", line );

    javaScript
      = new StringBuffer( "\r\naaa\rbbbbb\nccccccc\rddddddddd\r\n" );

    line = ResourceUtil.getLineAtPosition( javaScript, 1 );
    assertEquals( "", line );

    line = ResourceUtil.getLineAtPosition( javaScript, 2 );
    assertEquals( "aaa", line );

    line = ResourceUtil.getLineAtPosition( javaScript, 22 );
    assertEquals( "ddddddddd", line );

    line = ResourceUtil.getLineAtPosition( javaScript, 29 );
    assertEquals( "", line );

    javaScript
      = new StringBuffer( "\r\n  aaa //Single line comment\r\n" );
    line = ResourceUtil.getLineAtPosition( javaScript, 8 );
    assertEquals( "  aaa //Single line comment", line );
  }

  public void testGetPositionInLine() {
    StringBuffer javaScript
      = new StringBuffer( "abc\rqwerty\nmnbvcxz\r\npoiuytrewq" );
    int pos = ResourceUtil.getPositionInLine( javaScript, 7 );
    assertEquals( 'r', javaScript.charAt( 7 ) );
    assertEquals( 3, pos );

    pos = ResourceUtil.getPositionInLine( javaScript, 13 );
    assertEquals( 'b', javaScript.charAt( 13 ) );
    assertEquals( 2, pos );

    pos = ResourceUtil.getPositionInLine( javaScript, 25 );
    assertEquals( 't', javaScript.charAt( 25 ) );
    assertEquals( 5, pos );

    javaScript
      = new StringBuffer( "\r\n  // TODO [rh] causes JavaScript error\r\n" );
    pos = ResourceUtil.getPositionInLine( javaScript, 4 );
    assertEquals( '/', javaScript.charAt( 4 ) );
    assertEquals( 2, pos );
  }
}
