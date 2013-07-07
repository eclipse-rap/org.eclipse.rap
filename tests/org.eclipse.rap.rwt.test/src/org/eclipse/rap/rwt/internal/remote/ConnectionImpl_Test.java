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
import static org.junit.Assert.assertTrue;
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

  @Test( expected = NullPointerException.class )
  public void testCreateRemoteObject_failsWithNullType() {
    new ConnectionImpl().createRemoteObject( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testCreateRemoteObject_failsWithEmptyType() {
    new ConnectionImpl().createRemoteObject( "" );
  }

  @Test
  public void testCreatedRemoteObjectHasGivenType() {
    RemoteObject remoteObject = new ConnectionImpl().createRemoteObject( "type" );

    assertRendersCreateWithType( remoteObject, "type" );
  }

  @Test
  public void testCreatedRemoteObjectsHaveIds() {
    RemoteObject remoteObject = new ConnectionImpl().createRemoteObject( "type" );

    assertTrue( remoteObject.getId().length() > 0 );
  }

  @Test
  public void testCreatedRemoteObjectsHaveDifferentIds() {
    RemoteObject remoteObject1 = new ConnectionImpl().createRemoteObject( "type" );
    RemoteObject remoteObject2 = new ConnectionImpl().createRemoteObject( "type" );

    assertFalse( remoteObject2.getId().equals( remoteObject1.getId() ) );
  }

  @Test
  public void testCreatedRemoteObjectsAreRegistered() {
    RemoteObject remoteObject = new ConnectionImpl().createRemoteObject( "type" );

    assertSame( remoteObject, RemoteObjectRegistry.getInstance().get( remoteObject.getId() ) );
  }

  @Test
  public void testCreateServiceObject_returnsAnObject() {
    RemoteObject remoteObject = new ConnectionImpl().createServiceObject( "id" );

    assertNotNull( remoteObject );
  }

  @Test( expected = NullPointerException.class )
  public void testCreateServiceObject_failsWithNullId() {
    new ConnectionImpl().createServiceObject( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testCreateServiceObject_failsWithEmptyId() {
    new ConnectionImpl().createServiceObject( "" );
  }

  @Test
  public void testCreatedServiceObjectHasGivenId() {
    RemoteObject remoteObject = new ConnectionImpl().createServiceObject( "id" );

    assertEquals( "id", remoteObject.getId() );
  }

  @Test
  public void testCreatedServiceObjectsAreRegistered() {
    RemoteObject remoteObject = new ConnectionImpl().createServiceObject( "id" );

    assertSame( remoteObject, RemoteObjectRegistry.getInstance().get( remoteObject.getId() ) );
  }

  private static void assertRendersCreateWithType( RemoteObject remoteObject, String type )
  {
    ProtocolMessageWriter writer = mock( ProtocolMessageWriter.class );

    ( ( DeferredRemoteObject )remoteObject ).render( writer );

    verify( writer ).appendCreate( anyString(), eq( type ) );
  }

}
