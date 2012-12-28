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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ResourceDirectory_Test {

  private File tempDir;
  private ResourceDirectory writer;

  @Before
  public void setUp() {
    tempDir = FileTestUtil.createTempDir();
    writer = new ResourceDirectory();
  }

  @After
  public void tearDown() {
    FileTestUtil.delete( tempDir );
  }

  @Test
  public void testConfigurationSetsResourceDirectory() {
    writer.configure( tempDir.getAbsolutePath() );

    assertEquals( tempDir, writer.getDirectory().getParentFile() );
  }

  @Test
  public void testConfigurationDoesNotCreateResourceDirectory() {
    writer.configure( tempDir.getAbsolutePath() );

    assertFalse( writer.getDirectory().exists() );
  }

  @Test
  public void testConfigurationFailsWithNullParameter() {
    try {
      writer.configure( null );
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testGetDirectoryFailsIfNotConfigured() {
    try {
      writer.getDirectory();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  @Test
  public void testDirectoryCreated() {
    writer.configure( tempDir.getAbsolutePath() );

    writer.createDirectory();

    assertTrue( writer.getDirectory().isDirectory() );
  }

  @Test
  public void testDirectoryDeleted() {
    writer.configure( tempDir.getAbsolutePath() );
    writer.createDirectory();
    new File( writer.getDirectory(), "subdirectory" ).mkdir();

    writer.deleteDirectory();

    assertFalse( writer.getDirectory().exists() );
  }

}
