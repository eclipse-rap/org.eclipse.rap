/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.remote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ConnectionImpl_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCreateRemoteObject_returnsAnObject() {
    RemoteObject remoteObject = new ConnectionImpl().createRemoteObject( "type" );

    assertNotNull( remoteObject );
  }

  @Test
  public void testCreateRemoteObject_failsWithNullType() {
    try {
      new ConnectionImpl().createRemoteObject( null );
      fail();
    } catch( NullPointerException exception ) {
    }
  }

  @Test
  public void testCreateRemoteObject_failsWithEmptyType() {
    try {
      new ConnectionImpl().createRemoteObject( "" );
      fail();
    } catch( IllegalArgumentException exception ) {
    }
  }

  @Test
  public void testCreatedRemoteObjectHasGivenType() {
    RemoteObject remoteObject = new ConnectionImpl().createRemoteObject( "type" );

    assertRendersCreateWithType( remoteObject, "type" );
  }

  @Test
  public void testCreatedRemoteObjectsHaveDifferentIds() {
    RemoteObject remoteObject1 = new ConnectionImpl().createRemoteObject( "type" );
    RemoteObject remoteObject2 = new ConnectionImpl().createRemoteObject( "type" );

    assertFalse( getId( remoteObject2 ).equals( getId( remoteObject1 ) ) );
  }

  @Test
  public void testCreatedRemoteObjectsAreRegistered() {
    RemoteObject remoteObject = new ConnectionImpl().createRemoteObject( "type" );

    assertSame( remoteObject, RemoteObjectRegistry.getInstance().get( getId( remoteObject ) ) );
  }

  @Test
  public void testCreateServiceObject_returnsAnObject() {
    RemoteObject remoteObject = new ConnectionImpl().createServiceObject( "id" );

    assertNotNull( remoteObject );
  }

  @Test
  public void testCreateServiceObject_failsWithNullId() {
    try {
      new ConnectionImpl().createServiceObject( null );
      fail();
    } catch( NullPointerException exception ) {
    }
  }

  @Test
  public void testCreateServiceObject_failsWithEmptyId() {
    try {
      new ConnectionImpl().createServiceObject( "" );
      fail();
    } catch( IllegalArgumentException exception ) {
    }
  }

  @Test
  public void testCreatedServiceObjectHasGivenId() {
    RemoteObject remoteObject = new ConnectionImpl().createServiceObject( "id" );

    assertEquals( "id", getId( remoteObject ) );
  }

  @Test
  public void testCreatedServiceObjectsAreRegistered() {
    RemoteObject remoteObject = new ConnectionImpl().createServiceObject( "id" );

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
