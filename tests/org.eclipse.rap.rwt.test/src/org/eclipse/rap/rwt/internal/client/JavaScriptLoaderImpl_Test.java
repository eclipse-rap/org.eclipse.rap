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
package org.eclipse.rap.rwt.internal.client;

import static org.eclipse.rap.rwt.testfixture.Fixture.getProtocolMessage;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.client.service.JavaScriptLoader;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message.CallOperation;
import org.eclipse.rap.rwt.testfixture.Message.Operation;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.json.JSONArray;
import org.json.JSONException;


public class JavaScriptLoaderImpl_Test extends TestCase {

  private final JavaScriptLoader loader = new JavaScriptLoaderImpl();
  private Display display;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    Fixture.fakeNewRequest( display );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testCreatesLoadOperation() {
    loader.require( "url" );

    Operation operation = getProtocolMessage().getOperation( 0 );
    assertTrue( isLoadOperation( operation ) );
    assertEquals( list( "url" ), getFiles( operation ) );
  }

  public void testLoadsBeforeCreateWidget() {
    loader.require( "url" );
    Shell shell = new Shell( display );
    Fixture.executeLifeCycleFromServerThread();

    assertTrue( isLoadOperation( getProtocolMessage().getOperation( 0 ) ) );
    assertNotNull( getProtocolMessage().findCreateOperation( shell ) );
  }

  public void testDoesNotLoadUrlTwiceInSameRequest() {
    loader.require( "url" );
    loader.require( "url" );

    assertEquals( list( "url" ), getFiles( getProtocolMessage().getOperation( 0 ) ) );
    assertEquals( 1, getProtocolMessage().getOperationCount() );
  }

  public void testDoesNotLoadUrlTwiceInSameSession() {
    loader.require( "url" );

    Fixture.executeLifeCycleFromServerThread();
    Fixture.fakeNewRequest();
    loader.require( "url" );

    assertEquals( 0, getProtocolMessage().getOperationCount() );
  }

  private static boolean isLoadOperation( Operation operation ) {
    return    operation instanceof CallOperation
           && operation.getTarget().equals( "rwt.client.JavaScriptLoader" )
           && ( ( CallOperation )operation ).getMethodName().equals( "load" );
  }

  private static List<String> getFiles( Operation operation ) {
    List<String> result = new ArrayList<String>();
    JSONArray files = ( JSONArray )operation.getProperty( "files" );
    for( int i = 0; i < files.length(); i++ ) {
      try {
        result.add( files.getString( i ) );
      } catch( JSONException e ) {
        throw new RuntimeException( e );
      }
    }
    return result;
  }

  private static <T> List<T> list( T... elements ) {
    ArrayList<T> list = new ArrayList<T>();
    for( T element : elements ) {
      list.add( element );
    }
    return list;
  }

}

