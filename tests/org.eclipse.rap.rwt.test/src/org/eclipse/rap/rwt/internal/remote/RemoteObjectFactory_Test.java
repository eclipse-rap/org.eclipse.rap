/*******************************************************************************
* Copyright (c) 2012 EclipseSource and others.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    EclipseSource - initial API and implementation
*******************************************************************************/
package org.eclipse.rap.rwt.internal.remote;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.testfixture.Fixture;


public class RemoteObjectFactory_Test extends TestCase {

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testReturnsSingletonInstance() {
    RemoteObjectFactory factory = RemoteObjectFactory.getInstance();

    assertNotNull( factory );
    assertSame( factory, RemoteObjectFactory.getInstance() );
  }

  public void testCreateRemoteObject_returnsAnObject() {
    RemoteObject remoteObject = RemoteObjectFactory.getInstance().createRemoteObject( "type" );

    assertNotNull( remoteObject );
  }

  public void testCreateRemoteObject_failsWithNullType() {
    try {
      RemoteObjectFactory.getInstance().createRemoteObject( null );
      fail();
    } catch( NullPointerException exception ) {
    }
  }

  public void testCreateRemoteObject_failsWithEmptyType() {
    try {
      RemoteObjectFactory.getInstance().createRemoteObject( "" );
      fail();
    } catch( IllegalArgumentException exception ) {
    }
  }

  public void testCreatedRemoteObjectHasGivenType() {
    RemoteObject remoteObject = RemoteObjectFactory.getInstance().createRemoteObject( "type" );

    assertRendersCreateWithType( remoteObject, "type" );
  }

  public void testCreatedRemoteObjectsHaveDifferentIds() {
    RemoteObject remoteObject1 = RemoteObjectFactory.getInstance().createRemoteObject( "type" );
    RemoteObject remoteObject2 = RemoteObjectFactory.getInstance().createRemoteObject( "type" );

    assertFalse( getId( remoteObject2 ).equals( getId( remoteObject1 ) ) );
  }

  public void testCreatedRemoteObjectsAreRegistered() {
    RemoteObject remoteObject = RemoteObjectFactory.getInstance().createRemoteObject( "type" );

    assertSame( remoteObject, RemoteObjectRegistry.getInstance().get( getId( remoteObject ) ) );
  }

  public void testCreateServiceObject_returnsAnObject() {
    RemoteObject remoteObject = RemoteObjectFactory.getInstance().createServiceObject( "id" );

    assertNotNull( remoteObject );
  }

  public void testCreateServiceObject_failsWithNullId() {
    try {
      RemoteObjectFactory.getInstance().createServiceObject( null );
      fail();
    } catch( NullPointerException exception ) {
    }
  }

  public void testCreateServiceObject_failsWithEmptyId() {
    try {
      RemoteObjectFactory.getInstance().createServiceObject( "" );
      fail();
    } catch( IllegalArgumentException exception ) {
    }
  }

  public void testCreatedServiceObjectHasGivenId() {
    RemoteObject remoteObject = RemoteObjectFactory.getInstance().createServiceObject( "id" );

    assertEquals( "id", getId( remoteObject ) );
  }

  public void testCreatedServiceObjectsAreRegistered() {
    RemoteObject remoteObject = RemoteObjectFactory.getInstance().createServiceObject( "id" );

    assertSame( remoteObject, RemoteObjectRegistry.getInstance().get( getId( remoteObject ) ) );
  }

  private static String getId( RemoteObject remoteObject ) {
    return ( ( RemoteObjectImpl )remoteObject ).getId();
  }

  private static void assertRendersCreateWithType( RemoteObject remoteObject, String type ) {
    ProtocolMessageWriter writer = mock( ProtocolMessageWriter.class );

    ( ( RemoteObjectImpl )remoteObject ).render( writer );

    verify( writer ).appendCreate( anyString(), eq( type ) );
  }

}
