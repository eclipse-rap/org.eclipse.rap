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

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;


public class FileSettingStoreFactory_Test extends TestCase {

  private static final String SETTING_STORE_DIR = "org.eclipse.rap.rwt.service.FileSettingStore.dir";
  private SettingStoreFactory factory;

  @Override
  protected void setUp() {
    Fixture.setUp();
    factory = new FileSettingStoreFactory();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testCreate_failsWithNullId() {
    try {
      factory.createSettingStore( null );
      fail();
    } catch( NullPointerException exception ) {
      assertTrue( exception.getMessage().contains( "id" ) );
    }
  }

  public void testCreate_failsWithEmptyId() {
    try {
      factory.createSettingStore( "" );
      fail();
    } catch( IllegalArgumentException exception ) {
      assertTrue( exception.getMessage().contains( "empty" ) );
    }
  }

  public void testCreate_failsWithWhitespaceOnlyId() {
    try {
      factory.createSettingStore( " \t " );
      fail();
    } catch( IllegalArgumentException exception ) {
      assertTrue( exception.getMessage().contains( "empty" ) );
    }
  }

  public void testCreate_createsInstancesWithCorrectType() {
    SettingStore instance = factory.createSettingStore( "id" );

    assertSame( FileSettingStore.class, instance.getClass() );
  }

  public void testCreate_createsDirectory() {
    File file = new File( Fixture.TEMP_DIR, UUID.randomUUID().toString() );
    file.deleteOnExit();
    Fixture.getServletContext().setInitParameter( SETTING_STORE_DIR, file.getAbsolutePath() );

    factory.createSettingStore( "id" );

    assertTrue( file.exists() );
  }

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
