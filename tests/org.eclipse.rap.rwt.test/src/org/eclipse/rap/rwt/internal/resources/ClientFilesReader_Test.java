/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;


public class ClientFilesReader_Test {

  @Test
  public void testRead_emptyList() throws IOException {
    ClientFilesReader reader = createReader( "" );

    reader.read();

    assertEquals( Collections.emptyList(), reader.getFiles() );
  }

  @Test
  public void testRead() throws IOException {
    ClientFilesReader reader = createReader( "foo\nbar" );

    reader.read();

    assertEquals( Arrays.asList( "foo", "bar" ), reader.getFiles() );
  }

  @Test
  public void testRead_skipsComments() throws IOException {
    ClientFilesReader reader = createReader( "#foo\nbar\n#baz" );

    reader.read();

    assertEquals( Arrays.asList( "bar" ), reader.getFiles() );
  }

  @Test
  public void testRead_skipsWhitespace() throws IOException {
    ClientFilesReader reader = createReader( "  foo\t  \n \tbar \n # comment  " );

    reader.read();

    assertEquals( Arrays.asList( "foo", "bar" ), reader.getFiles() );
  }

  private static ClientFilesReader createReader( final String content ) {
    return new ClientFilesReader( "foo" ) {
      @Override
      InputStream openInputStream() throws IOException {
        return new ByteArrayInputStream( content.getBytes( "UTF-8" ) );
      }
    };
  }

}
