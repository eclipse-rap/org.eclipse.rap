/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.resources;

import java.io.*;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.engine.RWTFactory;


public class ResourceUtil_Test extends TestCase {

  public void testCompress() {
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
      = "qx.Class.define(\"org.eclipse.swt.widgets.Test\",{"
      + "extend:qx.ui.layout.CanvasLayout,"
      + "construct:function(a){"
      + "arguments.callee.base.call(this)"
      + "},"
      + "members:{"
      + "setValue:function(a){"
      + "this._value=a;"
      + "this._url=\"http://www.eclipse.org\";"
      + "this._comment=\"/* This is a comment inside string*/\""
      + "}}});";
    try {
      ResourceUtil.compress( javaScript );
    } catch( IOException e ) {
      fail( "Should not throw exception" );
    }
    assertEquals( expected, javaScript.toString() );
  }

  public void testConcatenationEmpty() {
    JSLibraryConcatenator jsConcatenator = new JSLibraryConcatenator();
    jsConcatenator.startJSConcatenation();
    String result = jsConcatenator.getContent();
    assertEquals( "", result );
  }

  public void testConcatenation() throws IOException {
    JSLibraryConcatenator jsConcatenator = RWTFactory.getJSLibraryConcatenator();
    jsConcatenator.startJSConcatenation();
    File file = File.createTempFile( "test", ".js" );
    ResourceUtil.write( file, getStringAsIntArray( "foo" ) );
    ResourceUtil.write( file, getStringAsIntArray( "bar" ) );
    String result = jsConcatenator.getContent();
    assertEquals( "foo\nbar\n", result );
  }

  public void testReadText() throws IOException {
    String input = createTestString( 10000 );
    InputStream inputStream = new ByteArrayInputStream( input.getBytes( "UTF-8" ) );
    int[] result = ResourceUtil.readText( inputStream, "UTF-8", false );
    byte[] bytes = toByteArray( result );
    assertEquals( input, new String( bytes ) );
  }

  private static byte[] toByteArray( int[] result ) {
    byte[] bytes = new byte[ result.length ];
    for( int i = 0; i < bytes.length; i++ ) {
      bytes[ i ] = ( byte )result[ i ];
    }
    return bytes;
  }

  private static String createTestString( int length ) {
    StringBuffer buffer = new StringBuffer( length );
    for( int i = 0; i < length; i++ ) {
      buffer.append( (char) ( 32 + ( i % 32 ) ) );
    }
    return buffer.toString();
  }

  private static int[] getStringAsIntArray( String string ) {
    byte[] bytes = string.getBytes();
    int[] content = new int[ bytes.length ];
    for( int i = 0; i < content.length; i++ ) {
      content[ i ] = ( bytes[ i ] & 0x0ff );
    }
    return content;
  }

  protected void setUp() throws Exception {
    Fixture.createApplicationContext();
    Fixture.createServiceContext();
  }

  protected void tearDown() throws Exception {
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfApplicationContext();
  }
}
