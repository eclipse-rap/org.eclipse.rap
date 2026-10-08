/*******************************************************************************
 * Copyright (c) 2002, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.resources;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ResourceUtil_Test {

  @Before
  public void setUp() {
    Fixture.createApplicationContext();
    Fixture.createServiceContext();
  }

  @After
  public void tearDown() {
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfApplicationContext();
  }

  @Test
  public void testWriteText() throws IOException {
    String input = createTestString( 10000 );
    byte[] content = input.getBytes( "UTF-8" );
    File tempFile = File.createTempFile( "rap-", ".test" );
    tempFile.deleteOnExit();

    ResourceUtil.write( tempFile, content );

    FileInputStream inputStream = new FileInputStream( tempFile );
    byte[] result = ResourceUtil.readBinary( inputStream );
    inputStream.close();
    assertEquals( input, new String( result, "UTF-8" ) );
  }

  private static String createTestString( int length ) {
    StringBuilder buffer = new StringBuilder( length );
    buffer.append( 'Ü' );
    for( int i = 1; i < length; i++ ) {
      buffer.append( ( char )( 32 + ( i % 32 ) ) );
    }
    return buffer.toString();
  }

}
