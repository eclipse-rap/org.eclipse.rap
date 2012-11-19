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
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class JavaScriptLoaderImpl_Test extends TestCase {

  private JavaScriptLoader loader = new JavaScriptLoaderImpl();
  private IResourceManager resourceManager;
  private Display display;

  public void testRegisterOnce() {
    ensureFiles( new String[]{ "resourcetest1.js" } );

    String expected = getRegistryPath() + "/resourcetest1.js";
    assertTrue( resourceManager.isRegistered( expected ) );
  }

  public void testDoNotRegisterTwice() {
    ensureFiles( new String[]{ "resourcetest1.js" } );
    ensureFiles( new String[]{ "utf-8-resource.js" } );

    // Same module, different return value: not a valid use case!
    // Used to check for repeated registration
    String expected = getRegistryPath() + "/resourcetest1.js";
    String notExpected = getRegistryPath() + "/utf-8-resource.js";
    assertTrue( resourceManager.isRegistered( expected ) );
    assertFalse( resourceManager.isRegistered( notExpected ) );
  }

  public void testRegisterMultipleFiles() {
    ensureFiles( new String[]{ "resourcetest1.js", "utf-8-resource.js" } );

    String expectedOne = getRegistryPath() + "/resourcetest1.js";
    String expectedTwo = getRegistryPath() + "/utf-8-resource.js";
    assertTrue( resourceManager.isRegistered( expectedOne ) );
    assertTrue( resourceManager.isRegistered( expectedTwo ) );
  }

  public void testFileNotFound() {
    try {
      ensureFiles( new String[]{ "this-file-does-not-exist.js" } );
      fail();
    } catch( Exception ex ) {
      // expected
    }
  }

  public void testNoFilesGiven() {
    try {
      ensureFiles( new String[]{} );
      fail();
    } catch( Exception ex ) {
      // expected
    }
  }

  public void testLoadOnce() {
    ensureFiles( new String[]{ "resourcetest1.js" } );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    String expected = "rwt-resources/" + getRegistryPath() + "/resourcetest1.js";
    assertNotNull( findLoadOperation( message, expected ) );
  }

  public void testLoadBeforeCreateWidget() {
    ensureFiles( new String[]{ "resourcetest1.js" } );
    Shell shell = new Shell( display );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    String expected = "rwt-resources/" + getRegistryPath() + "/resourcetest1.js";
    CreateOperation create = message.findCreateOperation( shell );
    CallOperation load = findLoadOperation( message, expected );
    assertTrue( load.getPosition() < create.getPosition() );
  }

  /////////
  // Helper

  public void setUp() {
    Fixture.setUp();
    display = new Display();
    Fixture.fakeNewRequest( display );
    resourceManager = RWT.getResourceManager();
  }

  public void tearDown() {
    Fixture.tearDown();
  }

  private void ensureFiles( String[] files ) {
    DummyModule.files = files;
    loader.ensureModule( DummyModule.class );
  }

  private String getRegistryPath() {
    return "DummyModule" + String.valueOf( DummyModule.class.hashCode() );
  }

  private CallOperation findLoadOperation( Message message, String file ) {
    CallOperation result = null;
    for( int i = 0; i < message.getOperationCount(); i++ ) {
      if( message.getOperation( i ) instanceof CallOperation ) {
        CallOperation operation = ( CallOperation )message.getOperation( i );
        if(    operation.getTarget().equals( "rwt.client.JavaScriptLoader" )
            && "load".equals( operation.getMethodName() )
            && file.equals( operation.getProperty( "url" ) )
        ) {
          result = operation;
        }
      }
    }
    return result;
  }

}

