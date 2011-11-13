/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.resources;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.internal.application.RWTFactory;
import org.eclipse.rwt.resources.IResourceManager;

import junit.framework.TestCase;


public class JSLibraryConcatenator_Test extends TestCase {
  private static final byte[] CONTENT = new byte[] { ( byte )'a' };

  private JSLibraryConcatenator jsConcatenator;
  private IResourceManager resourceManager;

  protected void setUp() {
    Fixture.setUp();
    Fixture.useDefaultResourceManager();
    resourceManager = RWTFactory.getResourceManager();
    jsConcatenator = new JSLibraryConcatenator( resourceManager );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testConcatenation() throws IOException {
    jsConcatenator.startJSConcatenation();
    jsConcatenator.appendJSLibrary( CONTENT );
    jsConcatenator.activate();

    String location = jsConcatenator.getLocation();
    assertEquals( "rwt-resources/resources.js", location );
    assertTrue( resourceManager.isRegistered( "resources.js" ) );
    assertEquals( "a\n", getRegisteredContent( "resources.js" ) );
  }

  public void testActivate() {
    jsConcatenator.startJSConcatenation();
    jsConcatenator.appendJSLibrary( CONTENT );
    jsConcatenator.activate();
    jsConcatenator.deactivate();

    assertNull( jsConcatenator.getLocation() );
    assertFalse( resourceManager.isRegistered( "resources.js" ) );
  }

  public void testIgnoreConcatenation() {
    jsConcatenator.appendJSLibrary( CONTENT );
    jsConcatenator.activate();

    assertNull( jsConcatenator.getLocation() );
    assertFalse( resourceManager.isRegistered( "resources.js" ) );
  }

  public void testEmptyFileContent() {
    jsConcatenator.startJSConcatenation();
    jsConcatenator.appendJSLibrary( new byte[ 0 ] );
    jsConcatenator.activate();

    assertNull( jsConcatenator.getLocation() );
    assertFalse( resourceManager.isRegistered( "resources.js" ) );
  }

  public void testIgnoreAppendJSLibraryAfterFinishJSConcatenation() {
    jsConcatenator.startJSConcatenation();
    jsConcatenator.activate();
    jsConcatenator.appendJSLibrary( CONTENT );

    assertNull( jsConcatenator.getLocation() );
    assertFalse( resourceManager.isRegistered( "resources.js" ) );
  }

  String getRegisteredContent( String location ) throws IOException {
    String result = null;
    InputStream inputStream = resourceManager.getRegisteredContent( location );
    if( inputStream != null ) {
      try {
        result = TestUtil.readContent( inputStream, "UTF-8" );
      } finally {
        inputStream.close();
      }
    }
    return result;
  }

}
