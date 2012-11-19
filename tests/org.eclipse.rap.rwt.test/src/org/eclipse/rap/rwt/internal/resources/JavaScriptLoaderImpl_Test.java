/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.resources;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.resources.IResourceManager;
import org.eclipse.rap.rwt.testfixture.Fixture;


public class JavaScriptLoaderImpl_Test extends TestCase {

  public static String[] files;

  private JavaScriptLoader loader = new JavaScriptLoaderImpl();
  private IResourceManager resourceManager;


  public void testRegisterOnce() {
    ensureFiles( new String[]{ "resourcetest1.js" } );

    String expected = getRegistryPath() + "/resourcetest1.js";
    assertTrue( resourceManager.isRegistered( expected ) );
  }

  public void testDoNotRegisterTwice() {
    ensureFiles( new String[]{ "resourcetest1.js" } );
    ensureFiles( new String[]{ "utf-8-resource.js" } );

    // Same module, different return value: not a valid usecase!
    // Used to check for repeated registration
    String expected = getRegistryPath() + "/resourcetest1.js";
    String notExpected = getRegistryPath() + "/utf-8-resource.js";
    assertTrue( resourceManager.isRegistered( expected ) );
    assertFalse( resourceManager.isRegistered( notExpected ) );
  }

  /////////
  // Helper

  public void setUp() {
    Fixture.setUp();
    resourceManager = RWT.getResourceManager();
  }

  public void tearDown() {
    Fixture.tearDown();
  }

  private void ensureFiles( String[] files ) {
    JavaScriptLoaderImpl_Test.files = files;
    loader.ensureModule( DummyModule.class );
  }

  private String getRegistryPath() {
    return "DummyModule" + String.valueOf( DummyModule.class.hashCode() );
  }

}
