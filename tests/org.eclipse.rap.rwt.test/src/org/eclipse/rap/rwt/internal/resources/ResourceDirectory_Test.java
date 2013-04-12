/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
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

import org.eclipse.rap.rwt.testfixture.FileUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ResourceDirectory_Test {

  private File tempDir;
  private ResourceDirectory resourceDirectory;

  @Before
  public void setUp() {
    tempDir = FileUtil.createTempDir();
    resourceDirectory = new ResourceDirectory();
  }

  @After
  public void tearDown() {
    FileUtil.delete( tempDir );
  }

  @Test
  public void testConfigurationSetsResourceDirectory() {
    resourceDirectory.configure( tempDir.getAbsolutePath() );

    assertEquals( tempDir, resourceDirectory.getDirectory().getParentFile() );
  }

  @Test
  public void testConfigurationDoesNotCreateResourceDirectory() {
    resourceDirectory.configure( tempDir.getAbsolutePath() );

    assertFalse( resourceDirectory.getDirectory().exists() );
  }

  @Test
  public void testConfigurationFailsWithNullParameter() {
    try {
      resourceDirectory.configure( null );
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testGetDirectoryFailsIfNotConfigured() {
    try {
      resourceDirectory.getDirectory();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  @Test
  public void testDirectoryCreated() {
    resourceDirectory.configure( tempDir.getAbsolutePath() );

    resourceDirectory.createDirectory();

    assertTrue( resourceDirectory.getDirectory().isDirectory() );
  }

  @Test
  public void testDirectoryDeleted() {
    resourceDirectory.configure( tempDir.getAbsolutePath() );
    resourceDirectory.createDirectory();
    new File( resourceDirectory.getDirectory(), "subdirectory" ).mkdir();

    resourceDirectory.deleteDirectory();

    assertFalse( resourceDirectory.getDirectory().exists() );
  }

  @Test
  public void testDirectoryDeleted_doesNotFailIfMissing() {
    resourceDirectory.configure( tempDir.getAbsolutePath() );

    resourceDirectory.deleteDirectory();
  }

}
