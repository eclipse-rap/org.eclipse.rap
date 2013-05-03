/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.clientbuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;


public class StringReplacer_Test extends TestCase {

  public void testDiscoverStrings() throws IOException {
    TokenList tokens = TestUtil.parse( "var foo = 'foo';" );
    StringReplacer replacer = new StringReplacer();
    replacer.discoverStrings( tokens );
    assertEquals( 1, replacer.getStrings().length );
  }

  public void testDiscoverStrings_NoMatches() throws IOException {
    TokenList tokens = TestUtil.parse( "var foo = 1; var bar = { 'foo' : 1 };" );
    StringReplacer replacer = new StringReplacer();
    replacer.discoverStrings( tokens );
    assertEquals( 0, replacer.getStrings().length );
  }

  public void testDiscoverStrings_MultipleOccurences() throws IOException {
    TokenList tokens = TestUtil.parse(   "var foo1 = 'foo';\n"
                                       + "var foo2 = 'foo';\n"
                                       + "var bar1 = 'bar';\n"
                                       + "var bar2 = 'bar';" );
    StringReplacer replacer = new StringReplacer();
    replacer.discoverStrings( tokens );
    assertEquals( 2, replacer.getStrings().length );
  }

  public void testGetStrings_SortedByFrequency() throws IOException {
    List<String> list  = new ArrayList<String>();
    for( int frequency = 1; frequency <= 10; frequency++ ) {
      for( int i = 0; i < frequency; i++ ) {
        list.add( "var v" + frequency + "_" + i + " = '" + frequency + "x';" );
      }
    }
    Collections.shuffle( list, new Random() );
    StringBuffer buffer = new StringBuffer();
    for( String string : list ) {
      buffer.append( string );
    }
    TokenList tokens = TestUtil.parse( buffer.toString() );
    StringReplacer replacer = new StringReplacer();
    replacer.discoverStrings( tokens );
    assertEquals( 10, replacer.getStrings().length );
    assertEquals( "10x", replacer.getStrings()[ 0 ] );
    assertEquals( "9x", replacer.getStrings()[ 1 ] );
    assertEquals( "2x", replacer.getStrings()[ 8 ] );
    assertEquals( "1x", replacer.getStrings()[ 9 ] );
  }

  public void testReplaceStrings() throws IOException {
    TokenList tokens = TestUtil.parse( "var foo = 'foo' + 'foo'; var bar = 'bar';" );
    StringReplacer replacer = new StringReplacer();
    replacer.discoverStrings( tokens );
    replacer.replaceStrings( tokens );
    String result = JavaScriptPrinter.printTokens( tokens );
    String expected = "var  foo = $ [ 0 ] + $ [ 0 ];\n" + "var  bar = $ [ 1 ];";
    assertEquals( expected, result );
  }
}
