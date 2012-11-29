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
package org.eclipse.rap.rwt.internal.service;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.client.WebClient;
import org.eclipse.rap.rwt.internal.resources.JavaScriptModule;
import org.eclipse.rap.rwt.service.JavaScriptLoader;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.json.JSONArray;
import org.json.JSONException;


public class JavaScriptLoaderImpl_Test extends TestCase {

  private static final String URL_1 = "rwt-resources/path/resourcetest1.js";
  private static final String URL_2 = "rwt-resources/path/utf-8-resource.js";

  private JavaScriptLoader loader = new JavaScriptLoaderImpl();
  private Display display;

  public void testLoadOnce() {
    loader.ensure( URL_1 );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertNotNull( findLoadOperation( message, URL_1 ) );
  }

  public void testLoadBeforeCreateWidget() {
    loader.ensure( URL_1 );
    Shell shell = new Shell( display );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    CreateOperation create = message.findCreateOperation( shell );
    CallOperation load = findLoadOperation( message, URL_1 );
    assertTrue( load.getPosition() < create.getPosition() );
  }

  public void testDoNotLoadTwiceForSameRequest() {
    loader.ensure( URL_1 );
    loader.ensure( URL_1 );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertNotNull( findLoadOperation( message, URL_1 ) );
    assertEquals( 1, message.getOperationCount() );
  }

  public void testDoNotLoadTwiceForSameSession() {
    loader.ensure( URL_1 );
    Fixture.executeLifeCycleFromServerThread();

    Fixture.fakeNewRequest();
    loader.ensure( URL_1 );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertNull( findLoadOperation( message, URL_1 ) );
  }

  public void testDoLoadTwiceForSameApplication() {
    loader.ensure( URL_1 );
    Fixture.executeLifeCycleFromServerThread();

    newSession();
    loader.ensure( URL_1 );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertNotNull( findLoadOperation( message, URL_1 ) );
  }

  public void testLoadMultipleFiles() {
    loader.ensure( new String[]{ URL_1, URL_2 } );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    CallOperation operationOne = findLoadOperation( message, URL_1 );
    CallOperation operationTwo = findLoadOperation( message, URL_2 );
    assertNotNull( operationOne );
    assertEquals( operationOne.getPosition(), operationTwo.getPosition() );
  }

  public void testLoadMultipleFilesInOrder() throws JSONException {
    loader.ensure( URL_1, URL_2 );

    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    CallOperation operation = findLoadOperation( message, URL_1 );
    JSONArray files = ( JSONArray )operation.getProperty( "files" );
    assertEquals( URL_1, files.getString( 0 ) );
    assertEquals( URL_2, files.getString( 1 ) );
  }

  public void testLoadMultipleFilesTwice() {
    loader.ensure( new String[]{ URL_1, URL_2 } );
    loader.ensure( new String[]{ URL_1, URL_2 } );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    assertNotNull( findLoadOperation( message, URL_1 ) );
    assertNotNull(  findLoadOperation( message, URL_2 ) );
    assertEquals( 1, message.getOperationCount() );
  }

  public void testLoadMultipleFilesPariallyTwice() {
    loader.ensure( new String[]{ URL_2 } );
    loader.ensure( new String[]{ URL_1, URL_2 } );
    loader.ensure( new String[]{ URL_2 } );
    Fixture.executeLifeCycleFromServerThread();

    Message message = Fixture.getProtocolMessage();
    CallOperation operationOne = findLoadOperation( message, URL_1 );
    CallOperation operationTwo = findLoadOperation( message, URL_2 );
    JSONArray filesOne = ( JSONArray )operationOne.getProperty( "files" );
    JSONArray filesTwo = ( JSONArray )operationTwo.getProperty( "files" );
    assertEquals( 2, message.getOperationCount() );
    assertTrue( operationOne.getPosition() > operationTwo.getPosition() );
    assertEquals( 1, filesOne.length() );
    assertEquals( 1, filesTwo.length() );
  }

  /////////
  // Helper

  public void setUp() {
    Fixture.setUp();
    display = new Display();
    Fixture.fakeNewRequest( display );
  }

  public void tearDown() {
    Fixture.tearDown();
  }

  private String getRegistryPath( boolean secondModule ) {
    String result;
    if( secondModule ) {
      result = "DummyModuleTwo" + String.valueOf( DummyModuleTwo.class.hashCode() );
    } else {
      result = "DummyModule" + String.valueOf( DummyModule.class.hashCode() );
    }
    return result;
  }

  private CallOperation findLoadOperation( Message message, String file ) {
    CallOperation result = null;
    for( int i = 0; i < message.getOperationCount(); i++ ) {
      if( message.getOperation( i ) instanceof CallOperation ) {
        CallOperation operation = ( CallOperation )message.getOperation( i );
        if(    operation.getTarget().equals( "rwt.client.JavaScriptLoader" )
            && "load".equals( operation.getMethodName() )
        ) {
          JSONArray files = ( JSONArray )operation.getProperty( "files" );
          for( int j = 0; j < files.length(); j++ ) {
            try {
              if( files.getString( j ).equals( file ) ) {
                result = operation;
              }
            } catch( JSONException e ) {
              throw new RuntimeException( e );
            }
          }
        }
      }
    }
    return result;
  }

  private void newSession() {
    ContextProvider.disposeContext();
    Fixture.createServiceContext();
    Fixture.fakeClient( new WebClient() );
    display = new Display();
    Fixture.fakeNewRequest( display );
  }

  /////////////////
  // helper classes

  static public class DummyModule implements JavaScriptModule {

    public static String[] files;

    public String getDirectory() {
      return "org/eclipse/rap/rwt/internal/resources";
    }

    public String[] getFileNames() {
      return files;
    }

    public ClassLoader getLoader() {
      return this.getClass().getClassLoader();
    }

  }

  static public class DummyModuleTwo implements JavaScriptModule {

    public static String[] files;

    public String getDirectory() {
      return "org/eclipse/rap/rwt/internal/resources";
    }

    public String[] getFileNames() {
      return files;
    }

    public ClassLoader getLoader() {
      return this.getClass().getClassLoader();
    }

  }

}

