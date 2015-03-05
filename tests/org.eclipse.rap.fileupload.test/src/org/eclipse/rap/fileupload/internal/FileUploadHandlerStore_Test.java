/*******************************************************************************
 * Copyright (c) 2011, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.fileupload.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.fileupload.FileUploadHandler;
import org.eclipse.rap.fileupload.test.TestFileUploadReceiver;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings( "restriction" )
public class FileUploadHandlerStore_Test {

  private FileUploadHandlerStore handlerStore;
  private FileUploadHandler testHandler;

  @Before
  public void setUp() {
    Fixture.setUp();
    handlerStore = FileUploadHandlerStore.getInstance();
    testHandler = new FileUploadHandler( new TestFileUploadReceiver() );
  }

  @After
  public void tearDown() {
    testHandler.dispose();
    testHandler = null;
    handlerStore = null;
    Fixture.tearDown();
  }

  @Test
  public void testGetInstance_returnsInstance() {
    FileUploadHandlerStore instance = FileUploadHandlerStore.getInstance();

    assertNotNull( instance );
  }

  @Test
  public void testGetInstance_sameInstanceInDifferentUISession() {
    Fixture.disposeOfServiceContext();
    Fixture.createServiceContext();

    assertSame( handlerStore, FileUploadHandlerStore.getInstance() );
  }

  @Test
  public void testGetInstance_differentInstanceInOtherApplication() {
    Fixture.disposeOfApplicationContext();
    Fixture.createApplicationContext();
    Fixture.createServiceContext();

    FileUploadHandlerStore instance = FileUploadHandlerStore.getInstance();

    assertNotNull( instance );
    assertNotSame( handlerStore, instance );
  }

  @Test
  public void testGetNotExistingHandler() {
    FileUploadHandler result = handlerStore.getHandler( "testId" );

    assertNull( result );
  }

  @Test
  public void testRegisterAndGetHandler() {
    handlerStore.registerHandler( "testId", testHandler );

    FileUploadHandler result = handlerStore.getHandler( "testId" );

    assertSame( testHandler, result );
  }

  @Test
  public void testGetHandlerWithDifferentId() {
    handlerStore.registerHandler( "testId", testHandler );

    FileUploadHandler result = handlerStore.getHandler( "anotherId" );

    assertNull( result );
  }

  @Test
  public void testDeregisterHandler() {
    handlerStore.registerHandler( "testId", testHandler );

    handlerStore.deregisterHandler( "testId" );
    FileUploadHandler result = handlerStore.getHandler( "testId" );

    assertNull( result );
  }

  @Test
  public void testCreateToken() {
    String token = FileUploadHandlerStore.createToken();

    assertNotNull( token );
    assertTrue( token.length() > 0 );
    assertFalse( token.equals( FileUploadHandlerStore.createToken() ) );
  }

}
