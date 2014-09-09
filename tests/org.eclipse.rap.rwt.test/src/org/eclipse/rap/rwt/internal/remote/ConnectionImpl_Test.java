/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
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

import org.eclipse.rap.rwt.internal.SingletonManager;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.rap.rwt.testfixture.TestSession;
import org.junit.Before;
import org.junit.Test;


public class ConnectionImpl_Test {

  private UISession uiSession;

  @Before
  public void setUp() {
    uiSession = createUISession();
  }

  @Test
  public void testCreateRemoteObject_returnsAnObject() {
    RemoteObject remoteObject = new ConnectionImpl( uiSession ).createRemoteObject( "type" );

    assertNotNull( remoteObject );
  }

  @Test( expected = NullPointerException.class )
  public void testCreateRemoteObject_failsWithNullType() {
    new ConnectionImpl( uiSession ).createRemoteObject( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testCreateRemoteObject_failsWithEmptyType() {
    new ConnectionImpl( uiSession ).createRemoteObject( "" );
  }

  @Test
  public void testCreatedRemoteObjectHasGivenType() {
    RemoteObject remoteObject = new ConnectionImpl( uiSession ).createRemoteObject( "type" );
    ProtocolMessageWriter writer = mock( ProtocolMessageWriter.class );

    ( ( DeferredRemoteObject )remoteObject ).render( writer );

    verify( writer ).appendCreate( anyString(), eq( "type" ) );
  }

  @Test
  public void testCreatedRemoteObjectsHaveIds() {
    RemoteObject remoteObject = new ConnectionImpl( uiSession ).createRemoteObject( "type" );

    assertTrue( remoteObject.getId().length() > 0 );
  }

  @Test
  public void testCreatedRemoteObjectsHaveDifferentIds() {
    RemoteObject remoteObject1 = new ConnectionImpl( uiSession ).createRemoteObject( "type" );
    RemoteObject remoteObject2 = new ConnectionImpl( uiSession ).createRemoteObject( "type" );

    assertFalse( remoteObject2.getId().equals( remoteObject1.getId() ) );
  }

  @Test
  public void testCreatedRemoteObjectsAreRegistered() {
    RemoteObject remoteObject = new ConnectionImpl( uiSession ).createRemoteObject( "type" );

    RemoteObjectRegistry remoteObjectRegistry = RemoteObjectRegistry.getInstance( uiSession );
    assertSame( remoteObject, remoteObjectRegistry.get( remoteObject.getId() ) );
  }

  @Test
  public void testCreateServiceObject_returnsAnObject() {
    RemoteObject remoteObject = new ConnectionImpl( uiSession ).createServiceObject( "id" );

    assertNotNull( remoteObject );
  }

  @Test( expected = NullPointerException.class )
  public void testCreateServiceObject_failsWithNullId() {
    new ConnectionImpl( uiSession ).createServiceObject( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testCreateServiceObject_failsWithEmptyId() {
    new ConnectionImpl( uiSession ).createServiceObject( "" );
  }

  @Test
  public void testCreatedServiceObjectHasGivenId() {
    RemoteObject remoteObject = new ConnectionImpl( uiSession ).createServiceObject( "id" );

    assertEquals( "id", remoteObject.getId() );
  }

  @Test
  public void testCreatedServiceObjectsAreRegistered() {
    RemoteObject remoteObject = new ConnectionImpl( uiSession ).createServiceObject( "id" );

    RemoteObjectRegistry remoteObjectRegistry = RemoteObjectRegistry.getInstance( uiSession );
    assertSame( remoteObject, remoteObjectRegistry.get( remoteObject.getId() ) );
  }

  private static UISession createUISession() {
    ApplicationContextImpl applicationContext = mock( ApplicationContextImpl.class );
    UISession uiSession = new UISessionImpl( applicationContext, new TestSession() );
    SingletonManager.install( uiSession );
    return uiSession;
  }

}
