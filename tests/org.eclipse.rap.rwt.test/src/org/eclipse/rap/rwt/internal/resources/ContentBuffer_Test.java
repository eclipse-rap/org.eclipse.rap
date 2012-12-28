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
package org.eclipse.rap.rwt.internal.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;


public class ContentBuffer_Test {

  private ContentBuffer contentBuffer;

  @Before
  public void setUp() {
    contentBuffer = new ContentBuffer();
  }

  @Test
  public void testGetContentWhenEmpty() {
    byte[] content = contentBuffer.getContent();

    assertNotNull( content );
    assertEquals( 0, content.length );
  }

  @Test
  @SuppressWarnings( "resource" )
  public void testGetContentAsStreamWhenEmpty() throws IOException {
    InputStream inputStream = contentBuffer.getContentAsStream();

    assertNotNull( inputStream );
    assertEquals( "", TestUtil.readContent( inputStream, "UTF-8" ) );
  }

  @Test
  public void testAppendEmptyBytes() throws IOException {
    contentBuffer.append( new ByteArrayInputStream( new byte[ 0 ] ) );

    byte[] content = contentBuffer.getContent();
    assertEquals( 0, content.length );
  }

  @Test
  public void testAppendEmptyStream() {
    contentBuffer.append( new byte[ 0 ] );

    byte[] content = contentBuffer.getContent();
    assertEquals( 0, content.length );
  }

  @Test
  public void testAppendBytes() throws IOException {
    contentBuffer.append( "foo".getBytes( "UTF-8" ) );

    byte[] content = contentBuffer.getContent();
    assertEquals( "foo\n", new String( content, "UTF-8" ) );
  }

  @Test
  public void testAppendStream() throws IOException {
    byte[] input = "foo".getBytes( "UTF-8" );

    contentBuffer.append( new ByteArrayInputStream( input ) );

    byte[] content = contentBuffer.getContent();
    assertEquals( "foo\n", new String( content, "UTF-8" ) );
  }

  @Test
  public void testConcatenation() throws IOException {
    contentBuffer.append( "foo".getBytes( "UTF-8" ) );
    contentBuffer.append( "bar".getBytes( "UTF-8" ) );

    byte[] content = contentBuffer.getContent();
    assertEquals( "foo\nbar\n", new String( content, "UTF-8" ) );
  }

  @Test
  @SuppressWarnings( "resource" )
  public void testGetContentAsStream() throws IOException {
    contentBuffer.append( "foo".getBytes( "UTF-8" ) );

    InputStream inputStream = contentBuffer.getContentAsStream();

    assertEquals( "foo\n", TestUtil.readContent( inputStream, "UTF-8" ) );
  }

}
