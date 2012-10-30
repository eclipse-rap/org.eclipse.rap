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
package org.eclipse.rap.rwt.internal.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;


public class ResourceUtil_Test extends TestCase {

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

  protected void setUp() throws Exception {
    Fixture.createApplicationContext();
    Fixture.createServiceContext();
  }

  protected void tearDown() throws Exception {
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfApplicationContext();
  }
}
