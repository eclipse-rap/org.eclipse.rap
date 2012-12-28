/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.service;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class FileSettingStoreFactory_Test {

  private static final String SETTING_STORE_DIR = "org.eclipse.rap.rwt.service.FileSettingStore.dir";
  private SettingStoreFactory factory;

  @Before
  public void setUp() {
    Fixture.setUp();
    factory = new FileSettingStoreFactory();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCreate_failsWithNullId() {
    try {
      factory.createSettingStore( null );
      fail();
    } catch( NullPointerException exception ) {
      assertTrue( exception.getMessage().contains( "id" ) );
    }
  }

  @Test
  public void testCreate_failsWithEmptyId() {
    try {
      factory.createSettingStore( "" );
      fail();
    } catch( IllegalArgumentException exception ) {
      assertTrue( exception.getMessage().contains( "empty" ) );
    }
  }

  @Test
  public void testCreate_failsWithWhitespaceOnlyId() {
    try {
      factory.createSettingStore( " \t " );
      fail();
    } catch( IllegalArgumentException exception ) {
      assertTrue( exception.getMessage().contains( "empty" ) );
    }
  }

  @Test
  public void testCreate_createsInstancesWithCorrectType() {
    SettingStore instance = factory.createSettingStore( "id" );

    assertSame( FileSettingStore.class, instance.getClass() );
  }

  @Test
  public void testCreate_createsDirectory() {
    File file = new File( Fixture.TEMP_DIR, UUID.randomUUID().toString() );
    file.deleteOnExit();
    Fixture.getServletContext().setInitParameter( SETTING_STORE_DIR, file.getAbsolutePath() );

    factory.createSettingStore( "id" );

    assertTrue( file.exists() );
  }

  @Test
  public void testCreate_failsIfDirectoryCannotBeCreated() throws IOException {
    File file = new File( Fixture.TEMP_DIR, UUID.randomUUID().toString() );
    file.createNewFile();
    file.deleteOnExit();
    Fixture.getServletContext().setInitParameter( SETTING_STORE_DIR, file.getAbsolutePath() );

    try {
      factory.createSettingStore( "id" );
      fail();
    } catch( IllegalArgumentException exception ) {
      assertTrue( exception.getMessage().contains( file.getAbsolutePath() ) );
    }
  }

}
