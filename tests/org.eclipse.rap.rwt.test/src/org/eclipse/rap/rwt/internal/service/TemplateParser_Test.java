/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.service.StartupPageTemplate.TemplateParser;
import org.eclipse.rap.rwt.internal.service.StartupPageTemplate.Token;


public class TemplateParser_Test extends TestCase {
  
  public void testParseWithoutVariable() {
    String text = "<html without variables />";
    TemplateParser parser = new TemplateParser( text );
    
    Token[] tokens = parser.parse();
    
    assertEquals( 1, tokens.length );
    assertEqualsText( text, tokens[ 0 ] );
  }

  public void testParseWithEmptyString() {
    TemplateParser parser = new TemplateParser( "" );
    
    Token[] tokens = parser.parse();
    
    assertEquals( 0, tokens.length );
  }

  public void testParseWithEmptyVariable() {
    TemplateParser parser = new TemplateParser( "${}" );
    
    Token[] tokens = parser.parse();
    
    assertEquals( 0, tokens.length );
  }
  
  public void testParseWithOnlyVariable() {
    TemplateParser parser = new TemplateParser( "${var}" );
    
    Token[] tokens = parser.parse();
    
    assertEquals( 1, tokens.length );
    assertEqualsVariable( "var", tokens[ 0 ] );
  }

  public void testParseWithNestedVariable() {
    TemplateParser parser = new TemplateParser( "${${var}}" );
    
    Token[] tokens = parser.parse();
    
    assertEquals( 2, tokens.length );
    assertEqualsVariable( "${var", tokens[ 0 ] );
    assertEqualsText( "}", tokens[ 1 ] );
  }
  
  public void testParseWithoutEmbeddedVariable() {
    TemplateParser parser = new TemplateParser( "begin${var}end" );
    
    Token[] tokens = parser.parse();
    
    assertEquals( 3, tokens.length );
    assertEqualsText( "begin", tokens[ 0 ] );
    assertEqualsVariable( "var", tokens[ 1 ] );
    assertEqualsText( "end", tokens[ 2 ] );
  }

  private static void assertEqualsVariable( String variableName, Token token ) {
    assertEquals( variableName, token.toString() );
    assertTrue( token.isVariable() );
  }

  private static void assertEqualsText( String text, Token token ) {
    assertEquals( text, token.toString() );
    assertFalse( token.isVariable() );
  }
  
}
