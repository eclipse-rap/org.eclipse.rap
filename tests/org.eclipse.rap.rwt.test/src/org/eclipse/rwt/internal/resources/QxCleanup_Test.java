/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.resources;

import junit.framework.TestCase;


public class QxCleanup_Test extends TestCase {

  public void testRemoveEmptyDebugVariantConditional() throws Exception {
    String input = "if( qx.core.Variant.isSet( \"qx.debug\", \"on\" ) ) {\n"
                   + "}\n";
    TokenList tokens = TestUtil.parse( input );
    QxCodeCleaner cleaner = new QxCodeCleaner( tokens );
    cleaner.cleanupQxCode();
    assertEquals( 0, tokens.size() );
  }

  public void testRemoveCompatVariantConditional() throws Exception {
    String input = "if( qx.core.Variant.isSet( \"qx.compatibility\", \"on\" ) ) {\n"
                   + "}\n";
    TokenList tokens = TestUtil.parse( input );
    QxCodeCleaner cleaner = new QxCodeCleaner( tokens );
    cleaner.cleanupQxCode();
    assertEquals( 0, tokens.size() );
  }

  public void testRemoveAspectVariantConditional() throws Exception {
    String input = "if( qx.core.Variant.isSet( \"qx.aspects\", \"on\" ) ) {\n"
      + "}\n";
    TokenList tokens = TestUtil.parse( input );
    QxCodeCleaner cleaner = new QxCodeCleaner( tokens );
    cleaner.cleanupQxCode();
    assertEquals( 0, tokens.size() );
  }

  public void testRemoveMultipleVariantConditionals() throws Exception {
    String input = "if( qx.core.Variant.isSet( \"qx.debug\", \"on\" ) ) {\n"
                   + "}\n";
    TokenList tokens = TestUtil.parse( input );
    QxCodeCleaner cleaner = new QxCodeCleaner( tokens );
    cleaner.cleanupQxCode();
    assertEquals( 0, tokens.size() );
  }

  public void testRemoveVariantConditionalBetweenStatements() throws Exception {
    String input = "a = 1;\n"
                   + "if( qx.core.Variant.isSet( \"qx.debug\", \"on\" ) ) {\n"
                   + "  if( false ) { throw \"ERROR\" }\n"
                   + "}\n"
                   + "b = 2;";
    TokenList tokens = TestUtil.parse( input );
    QxCodeCleaner cleaner = new QxCodeCleaner( tokens );
    cleaner.cleanupQxCode();
    String result = JavaScriptPrinter.printTokens( tokens );
    assertEquals( "a = 1;\nb = 2;", result );
  }

  public void testRemoveVariantConditionalWithElseBlock() throws Exception {
    String input = "if( qx.core.Variant.isSet( \"qx.debug\", \"on\" ) ) {\n"
                   + "  a = 1;\n"
                   + "}\n else {\n"
                   + "  b = 2;\n"
                   + "}";
    TokenList tokens = TestUtil.parse( input );
    QxCodeCleaner cleaner = new QxCodeCleaner( tokens );
    cleaner.cleanupQxCode();
    String result = JavaScriptPrinter.printTokens( tokens );
    assertEquals( "b = 2;", result );
  }

  public void testRemoveNestedVariantConditional() throws Exception {
    String input =   "if( vObject && vObject.__disposed === false ) {\n"
                   + "  try {\n"
                   + "    vObject.dispose();\n"
                   + "  }\n"
                   + "  catch( ex ) {\n"
                   + "    if( qx.core.Variant.isSet( \"qx.debug\", \"on\" ) ) {\n"
                   + "      qx.core.Log.warn( \"Could not dispose: \" + vObject + \":\", ex );\n"
                   + "    }\n"
                   + "  }\n"
                   + "}\n";
    String expected = "if ( vObject && vObject.__disposed === false ) {\n"
                      + "  try {\n"
                      + "    vObject.dispose ( );\n"
                      + "  }\n"
                      + "  catch ( ex ) {\n"
                      + "  }\n"
                      + "}";
    TokenList tokens = TestUtil.parse( input );
    QxCodeCleaner cleaner = new QxCodeCleaner( tokens );
    cleaner.cleanupQxCode();
    String result = JavaScriptPrinter.printTokens( tokens );
    assertEquals( expected, result );
  }

  public void testReplaceVariantSelection() throws Exception {
    String input = "result = qx.core.Variant.select( \"qx.debug\", {\n"
                   + "  \"on\": {\n"
                   + "    \"foo\" : 23,\n"
                   + "    \"bar\" : 42\n"
                   + "  },\n"
                   + "  \"default\" : null\n"
                   + "} )";
    TokenList tokens = TestUtil.parse( input );
    QxCodeCleaner cleaner = new QxCodeCleaner( tokens );
    cleaner.cleanupQxCode();
    String result = JavaScriptPrinter.printTokens( tokens );
    assertEquals( "result = null;", result );
  }

  public void testReplaceBaseCall() throws Exception {
    String input = "result = this.base( arguments );";
    TokenList tokens = TestUtil.parse( input );
    QxCodeCleaner cleaner = new QxCodeCleaner( tokens );
    cleaner.cleanupQxCode();
    String result = JavaScriptPrinter.printTokens( tokens );
    String expected = "result = arguments.callee.base.call ( this );";
    assertEquals( expected, result );
  }

  public void testReplaceBaseCallWithParameters() throws Exception {
    String input = "result = this.base( arguments, 23, 'foo' );";
    TokenList tokens = TestUtil.parse( input );
    QxCodeCleaner cleaner = new QxCodeCleaner( tokens );
    cleaner.cleanupQxCode();
    String result = JavaScriptPrinter.printTokens( tokens );
    String expected = "result = arguments.callee.base.call ( this, 23, \"foo\" );";
    assertEquals( expected, result );
  }
}
