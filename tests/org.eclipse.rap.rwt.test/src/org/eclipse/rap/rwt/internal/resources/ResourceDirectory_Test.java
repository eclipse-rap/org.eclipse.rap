/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.resources;

import java.io.File;

import junit.framework.TestCase;


public class ResourceDirectory_Test extends TestCase {

  private File tempDir;
  private ResourceDirectory writer;

  @Override
  protected void setUp() {
    tempDir = FileTestUtil.createTempDir();
    writer = new ResourceDirectory();
  }

  @Override
  protected void tearDown() {
    FileTestUtil.delete( tempDir );
  }

  public void testConfigurationSetsResourceDirectory() {
    writer.configure( tempDir.getAbsolutePath() );

    assertEquals( tempDir, writer.getDirectory().getParentFile() );
  }

  public void testConfigurationDoesNotCreateResourceDirectory() {
    writer.configure( tempDir.getAbsolutePath() );

    assertFalse( writer.getDirectory().exists() );
  }

  public void testConfigurationFailsWithNullParameter() {
    try {
      writer.configure( null );
    } catch( NullPointerException expected ) {
    }
  }

  public void testGetDirectoryFailsIfNotConfigured() {
    try {
      writer.getDirectory();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  public void testDirectoryCreated() {
    writer.configure( tempDir.getAbsolutePath() );

    writer.createDirectory();

    assertTrue( writer.getDirectory().isDirectory() );
  }

  public void testDirectoryDeleted() {
    writer.configure( tempDir.getAbsolutePath() );
    writer.createDirectory();
    new File( writer.getDirectory(), "subdirectory" ).mkdir();

    writer.deleteDirectory();

    assertFalse( writer.getDirectory().exists() );
  }

}
