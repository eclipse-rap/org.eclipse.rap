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

package org.eclipse.rwt.internal.lifecycle;

import java.io.IOException;
import java.util.Iterator;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.browser.*;
import org.eclipse.rwt.internal.util.CssClass;


/** <p>Tests functionality of org.eclipse.rap.HtmlResponseWriter</p>
  */
public class HtmlResponseWriter_Test extends TestCase {
  
  private static final String SAMPLE_CSS_1
    = "background-color:#ffffff;font-family:arial,verdana;font-size:8pt;";
  private static final String SAMPLE_CSS_2
    = "background-color:#f3fb48;font-size:12pt;";

  private HtmlResponseWriter tb;
  
  
  public HtmlResponseWriter_Test( final String name ) {
    super( name );
    tb = new HtmlResponseWriter();
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeBrowser( new Default( true, true ) );
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  
  // actual testing code
  //////////////////////
  
  /** tests the functionality for collecting css classes with equal content
    * that are named (the names of the classes must be pooled correctly into
    * a compound name).*/
  public void testNamedCssClasses() {
    tb.addNamedCssClass( new CssClass( "someClassName", SAMPLE_CSS_1 ) );
    check( new String[] { ".someClassName" } );
    
    tb.addNamedCssClass( new CssClass( "anotherClassName", SAMPLE_CSS_2 ) );
    check( new String[] { ".anotherClassName", ".someClassName" } );
    
    tb.addNamedCssClass( new CssClass( "thirdClassName", SAMPLE_CSS_1 ) );    
    check( new String[] { ".anotherClassName", 
                          ".someClassName, .thirdClassName" } );
    
    tb.addNamedCssClass( new CssClass( "lastClassName", SAMPLE_CSS_1 ) );
    check( new String[] { ".anotherClassName", 
                          ".someClassName, .thirdClassName, .lastClassName" } );
  }
  
  public void testTokenAppending() throws Exception {
    HtmlResponseWriter tokenBuffer = new HtmlResponseWriter();
    tokenBuffer.append( "|Token 1" );
    tokenBuffer.append( "|Token 2" );
    tokenBuffer.write( '|' );
    tokenBuffer.write( new char[] { 'a', 'b' } );
    tokenBuffer.write( new char[] { 'a', 'b', '|', 'c', 'd' }, 2, 3 );
    tokenBuffer.write( 124 );
    tokenBuffer.write( "Token 3" );
    tokenBuffer.write( "my|Token 4|trallala", 2, 8 );
    
    Iterator iterator = tokenBuffer.bodyTokens();
    String result = new String();
    while( iterator.hasNext() ) {
      result += iterator.next().toString();
    }
    assertTrue( result.equals( "|Token 1|Token 2|ab|cd|Token 3|Token 4" ) );
    
//    try {
//      tokenBuffer.flush();
//      fail();
//    } catch( UnsupportedOperationException use ) {
//    }
//    try {
//      tokenBuffer.close();
//      fail();
//    } catch( UnsupportedOperationException use ) {
//    }
  }
  
  public void testInputTagCreationNoXHTML() throws IOException {
    HtmlResponseWriter writer = new HtmlResponseWriter();
    writer.startElement( "input", null );
    writer.writeAttribute( "type", "text", null );
    writer.endElement( "input" );
    String expected = "<input type=\"text\">";
    assertEquals( expected, getContent( writer ) );
  }
  
  public void testInputTagCreationXHTML() throws IOException {
    Fixture.fakeBrowser( new Ie5_5up( true, true ) );
    HtmlResponseWriter writer = new HtmlResponseWriter();
    writer.startElement( "input", null );
    writer.writeAttribute( "type", "text", null );
    writer.endElement( "input" );
    String expected = "<input type=\"text\" />";
    assertEquals( expected, getContent( writer ) );
  }

  public void testScriptTagCreation() throws IOException {
    HtmlResponseWriter writer = new HtmlResponseWriter();
    writer.startElement( "script", null );
    writer.writeText( new char[] { '<', '>' }, 0, 2 );
    writer.writeText( "<>", null );
    writer.endElement( "script" );
    String expected = "<script><><></script>";
    assertEquals( expected, getContent( writer ) );
  }

  public void testClose() throws IOException {
    HtmlResponseWriter writer = new HtmlResponseWriter();
    String element = "element";
    writer.startElement( element, null );
    writer.close();
    String expected = "<" + element + ">";
    assertEquals( expected, getContent( writer ) );
  }

//  private String assertWriteURIAttribute( final HtmlResponseWriter tokenBuffer, 
//                                          final ResponseWriter writer, 
//                                          final String out )
//    throws IOException 
//  {
//    String element = "element";
//    writer.endElement( element );
//    String result = out + "></" + element + ">";
//    try {
//      writer.writeURIAttribute( null, null, null );
//      fail();
//    } catch( NullPointerException npe ) {
//    }
//    String uriAttribute1 = "uriAttribute1";
//    try {
//      writer.writeURIAttribute( uriAttribute1, "trallala", "hopsasa" );
//      fail();
//    } catch( IllegalStateException ise ) {
//    }
//    writer.startElement( element, null );
//    result += "<" + element;
//    assertEquals( result, RendererTestUtil.getContent( tokenBuffer ) );
//    final String uriValue1 = "http://server:port/path/my action?param=+ <>";
//    String uriValueExpected1 
//      = "http://server:port/path/my+action?param=+%20%3C%3E";
//    writer.writeURIAttribute( uriAttribute1, new Object() {
//      public String toString() {
//        return uriValue1;
//      }
//    }, null );
//    result += " " + uriAttribute1 + "=\"" + uriValueExpected1 + "\"";
//    assertEquals( result, RendererTestUtil.getContent( tokenBuffer ) );
//    
//    String uriAttribute2 = "uriAttribute2";
//    String javaScript = "javascript:";
//    writer.writeURIAttribute( uriAttribute2, javaScript, null );
//    result += " " + uriAttribute2 + "=\""+ javaScript + "\"";    
//    assertEquals( result, RendererTestUtil.getContent( tokenBuffer ) );
//    return result;
//  }

  public void testWriteAttribute() throws IOException {
    HtmlResponseWriter writer = new HtmlResponseWriter();
    String element = "element";
    try {
      writer.writeAttribute( null, null, null );
      fail();
    } catch( NullPointerException npe ) {
    }
    String attribute1 = "attribute1";
    try {
      writer.writeAttribute( attribute1, "trallala", "hopsasa" );
      fail();
    } catch( IllegalStateException ise ) {
    }
    writer.startElement( element, null );
    String expected = "<" + element;
    assertEquals( expected, getContent( writer ) );
    final String value1 = "<value1>";
    String valueExpected = "&lt;value1&gt;";
    writer.writeAttribute( attribute1, new Object() {
      public String toString() {
        return value1;
      }
    }, null );
    expected += " " + attribute1 + "=\"" + valueExpected + "\"";
    assertEquals( expected, getContent( writer ) );
    String enabled = "enabled";
    writer.writeAttribute( enabled, null, null );
    expected += " " + enabled;
    assertEquals( expected, getContent( writer ) );
  }
  
  public void testWriteAttributeXHTML() throws Exception {
    Fixture.fakeBrowser( new Ie5_5up( true, true ) );
    HtmlResponseWriter writer = new HtmlResponseWriter();
    String element = "element";
    writer.startElement( element, null );
    String attribute = "enabled";
    writer.writeAttribute( attribute, null, null );
    String expected 
      = "<" + element + " " + attribute + "=\"" + attribute + "\"";
    assertEquals( expected, getContent( writer ) );
  }

  public void testEndDocument() throws IOException {
    HtmlResponseWriter writer = new HtmlResponseWriter();
    String element = "element";
    writer.startElement( element, null );
    String result = "<" + element;
    assertEquals( result, getContent( writer ) );
    writer.endDocument();
    result += ">";
    assertEquals( result, getContent( writer ) );
  }

  public void testWriteWithoutEscape() throws IOException {
    HtmlResponseWriter writer = new HtmlResponseWriter();
    String element = "element";
    writer.startElement( element, null );
    String result = "<" + element;
    assertEquals( result, getContent( writer ) );
    String unescaped = "</tag>";
    writer.write( unescaped );
    result += ">" + unescaped;
    assertEquals( result, getContent( writer ) );
    
    writer.startElement( element, null );
    result += "<" + element;
    assertEquals( result, getContent( writer ) );
    unescaped = "</tag>";
    writer.write( unescaped.toCharArray(), 0, unescaped.length() );
    result += ">" + unescaped;
    assertEquals( result, getContent( writer ) );
  }
  

  public void testFlush() throws IOException {
    HtmlResponseWriter writer = new HtmlResponseWriter();
    String element = "element";
    writer.startElement( element, null );
    String expected = "<" + element;
    assertEquals( expected, getContent( writer ) );
    writer.flush();
    expected += ">";
    assertEquals( expected, getContent( writer ) );
  }

  public void testEndElement() throws IOException {
    HtmlResponseWriter writer = new HtmlResponseWriter();
    String element = "element";
    writer.startElement( element, null );
    String expected = "<" + element;
    assertEquals( expected, getContent( writer ) );
    
    try {
      writer.endElement( null );
      fail();
    } catch( NullPointerException npe ) {
    }
    
    writer.endElement( element );
    expected += "></" + element + ">";
    assertEquals( expected, getContent( writer ) );
  }
  
  public void testEndElementForXHTML() throws Exception {
    Fixture.fakeBrowser( new Ie5_5up( true, false ) );
    HtmlResponseWriter writer = new HtmlResponseWriter();
    writer.startElement( "img", null );
    writer.writeAttribute( "src", "image.gif", null );
    writer.endElement( "img" );
    String expected = "<img src=\"image.gif\" />";
    assertEquals( expected, getContent( writer ) );
  }

  public void testEndElementForNonXHTML() throws Exception {
    Fixture.fakeBrowser( new Default( true, false ) );
    HtmlResponseWriter writer = new HtmlResponseWriter();
    writer.startElement( "img", null );
    writer.writeAttribute( "src", "image.gif", null );
    writer.endElement( "img" );
    String expected = "<img src=\"image.gif\">";
    assertEquals( expected, getContent( writer ) );
  }
  
  public void testWrite() throws IOException {
    HtmlResponseWriter writer;
    Fixture.fakeBrowser( new Ie6( true ) );
    // script & style tags must not be encoded
    writer = new HtmlResponseWriter();
    writer.startElement( "style", null );
    writer.writeText( "\u00e4\u00f6\u00fc?", null );
    writer.endElement( "style" );
    assertEquals( "<style>\u00e4\u00f6\u00fc?</style>", getContent( writer ) );
    writer = new HtmlResponseWriter();
    writer.startElement( "script", null );
    writer.writeText( "\u00e4\u00f6\u00fc?", null );
    writer.endElement( "script" );
    assertEquals( "<script>\u00e4\u00f6\u00fc?</script>", getContent( writer ) );
    // all other elements must be encoded 
    writer = new HtmlResponseWriter();
    writer.startElement( "whaetever", null );
    writer.writeText( "\u00e4\u00f6\u00fc?", null );
    writer.endElement( "whaetever" );
    assertEquals( "<whaetever>&auml;&ouml;&uuml;?</whaetever>", 
                  getContent( writer ) );
  }
  
  public void testWriteTextWithObject() throws IOException {
    HtmlResponseWriter writer = new HtmlResponseWriter();
    String element3 = "element3";
    writer.startElement( element3, null );
    String expected = "<" + element3;
    assertEquals( expected, getContent( writer ) );

    try {
      writer.writeText( null, null );
      fail();
    } catch( NullPointerException npe ) {
    }
    final String text2 = "my <text>";
    writer.writeText( new Object() { 
      public String toString() {
        return text2;
      } 
    }, null );
    
    final String expectedText2 = "my &lt;text&gt;";
    expected += ">" + expectedText2;
    assertEquals( expected, getContent( writer ) );
  }

  public void testWriteTextWithArray() throws IOException {
    HtmlResponseWriter writer = new HtmlResponseWriter();
    String element2 = "element2";
    writer.startElement( element2, null );
    String expected = "<" + element2;
    assertEquals( expected, getContent( writer ) );
    
    try {
      writer.writeText( null, 0, 0 );
      fail();
    } catch( NullPointerException npe ) {
    }
    
    char[] text1 = new char[] { '|', '<', 'a', '>', '|' };
    writer.writeText( text1, 1, 3 );
    expected += ">&lt;" + text1[ 2 ] + "&gt;";
    assertEquals( expected, getContent( writer ) );
    
    try {
      writer.writeText( text1, -1, 0 );
      fail();
    } catch( IndexOutOfBoundsException ioobe ) {
    }
    try {
      writer.writeText( text1, 1, 7 );
      fail();
    } catch( IndexOutOfBoundsException ioobe ) {
    }
    assertEquals( expected, getContent( writer ) );
  }

  public void testWriteComment() throws IOException {
    HtmlResponseWriter writer = new HtmlResponseWriter();
    final String comment = "my <comment>";
    final String expectedComment = "my &lt;comment&gt;";
    writer.writeComment( new Object() { 
      public String toString() {
        return comment;
      } 
    } );
    String result = "<!-- " + expectedComment + " -->";
    assertEquals( result, getContent( writer ) );
  }
  
  public void testStartElement() throws IOException {
    HtmlResponseWriter writer = new HtmlResponseWriter();
    try {
      writer.startElement( null, null );
      fail();
    } catch( NullPointerException npe ) {
    }
    
    String root = "root";
    writer.startElement( root, null  );
    String expected = "<" + root;
    assertEquals( expected, getContent( writer ) );
    
    String element1 = "element1";
    writer.startElement( element1, null );
    expected += "><" + element1;
    assertEquals( expected, getContent( writer ) );
    
    try {
      writer.writeComment( null );
      fail();
    } catch( NullPointerException npe ) {
    }
  }

  public void testClosedAssertions() throws IOException {
    HtmlResponseWriter writer = new HtmlResponseWriter();
    writer.close();
    try {
      writer.close();
      fail();
    } catch( IOException ioe ) {
    }
    try {
      writer.close();
      fail();
    } catch( IOException ioe ) {
    }
    try {
      writer.endDocument();
      fail();
    } catch( IOException ioe ) {
    }
    try {
      writer.endElement( "xxx" );
      fail();
    } catch( IOException ioe ) {
    }
    try {
      writer.flush();
      fail();
    } catch( IOException ioe ) {
    }
    try {
      writer.startDocument();
      fail();
    } catch( IOException ioe ) {
    }
    try {
      writer.startElement( "xxx", null );
      fail();
    } catch( IOException ioe ) {
    }
    try {
      writer.write( new char[]{ 'X' } );
      fail();
    } catch( IOException ioe ) {
    }
    try {
      writer.write( new char[]{ 'X' }, 0, 1 );
      fail();
    } catch( IOException ioe ) {
    }
    try {
      writer.write( 13 );
      fail();
    } catch( IOException ioe ) {
    }
    try {
      writer.write( "xxx" );
      fail();
    } catch( IOException ioe ) {
    }
    try {
      writer.write( "xxx", 0, 3 );
      fail();
    } catch( IOException ioe ) {
    }
    try {
      writer.writeAttribute( "xxx", "trallala", "xyz" );
      fail();
    } catch( IOException ioe ) {
    }
    try {
      writer.writeComment( "xxx" );
      fail();
    } catch( IOException ioe ) {
    }
    try {
      writer.writeText( new char[] { 'X' }, 0, 1 );
      fail();
    } catch( IOException ioe ) {
    }
    try {
      writer.writeText( "xxx", "nothing" );
      fail();
    } catch( IOException ioe ) {
    }
    try {
      writer.writeNBSP();
      fail();
    } catch( IOException ioe ) {
    }
//    try {
//      writer.writeURIAttribute( "xxx", "nothing", "tirili" );
//      fail();
//    } catch( IOException ioe ) {
//    }
  }

  public void testWriteNBSP() throws IOException {
    HtmlResponseWriter writer = new HtmlResponseWriter();
    writer.writeNBSP();
    assertEquals( "&nbsp;", Fixture.getAllMarkup( writer ) );
    writer = new HtmlResponseWriter();
    writer.startElement( "span",  null );
    writer.writeNBSP();
    assertEquals( "<span>&nbsp;", Fixture.getAllMarkup( writer ) );
    writer = new HtmlResponseWriter();
    writer.startElement( "span",  null );
    writer.writeAttribute( "id", "p2", null );
    writer.writeNBSP();
    assertEquals( "<span id=\"p2\">&nbsp;", Fixture.getAllMarkup( writer ) );
    writer = new HtmlResponseWriter();
    writer.startElement( "span",  null );
    writer.writeAttribute( "id", "p2", null );
    writer.writeNBSP();
    writer.endElement( "span" );
    String expected = "<span id=\"p2\">&nbsp;</span>";
    assertEquals( expected, Fixture.getAllMarkup( writer ) );
  }
  
  public void testUseJSLibrary() {
    HtmlResponseWriter writer = new HtmlResponseWriter();
    try {
      writer.useJSLibrary( null );
      fail( "NullPointerException expected" );
    } catch( NullPointerException e ) {
      // expected
    }
    writer.useJSLibrary( "z" );
    writer.useJSLibrary( "a" );
    writer.useJSLibrary( "b" );
    writer.useJSLibrary( "a" );
    String[] libraries = writer.getJSLibraries();
    assertEquals( 3, libraries.length );
    String[] expected = new String[] { "z", "a", "b" };
    for( int i = 0; i < expected.length; i++ ) {
      assertEquals( expected[ i ], libraries[ i ] );
    }
  }
  
  // helping methods
  //////////////////
  
  static String getContent( final HtmlResponseWriter tokenBuffer ) {
    String result = "";
    Iterator tokens = tokenBuffer.bodyTokens();
    while( tokens.hasNext() ) {
      result += tokens.next().toString();
    }
    return result;
  }

  
  private void check( final String[] expectedClassNames ) {
    assertTrue( createMessage( expectedClassNames ),
                expectedClassNames.length == tb.getCssClasses().length );

    for( int i = 0; i < expectedClassNames.length; i++ ) {
      String compoundName = tb.getCssClasses()[ i ].getClassName();
      assertTrue(   "Expected compound class name mathes not the actually "                  + "created one.",
                  expectedClassNames[ i ].equals( compoundName ) );
    }

  }

  private String createMessage( final String[] expectedClassNames ) {
    return   "Number of entries for named classes is incorrect: "
           + tb.getCssClasses().length 
           + " instead of " 
           + expectedClassNames.length
           + ".";
  }
}