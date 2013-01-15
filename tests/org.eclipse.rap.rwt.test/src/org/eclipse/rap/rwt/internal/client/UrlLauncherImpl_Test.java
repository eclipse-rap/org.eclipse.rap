/*******************************************************************************
 * Copyright (c) 2011, 2013 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.eclipse.rap.rwt.client.service.UrlLauncher;
import org.eclipse.rap.rwt.internal.remote.ConnectionImpl;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class UrlLauncherImpl_Test {

  private static final String URL = "http://someurl.com/foo/bar";

  @Before
  public void setUp() {
    Fixture.setUp();
    new Display();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCreatesRemoteObjectWithCorrectId() {
    ConnectionImpl connection = fakeConnection( mock( RemoteObject.class ) );

    UrlLauncher launcher = new UrlLauncherImpl();
    launcher.openURL( URL );

    verify( connection ).createServiceObject( eq( "rwt.client.UrlLauncher" ) );
  }

  @Test
  public void testWritesCallOperation() {
    RemoteObject remoteObject = mock( RemoteObject.class );
    fakeConnection( remoteObject );

    UrlLauncher launcher = new UrlLauncherImpl();
    launcher.openURL( URL );

    assertEquals( URL, getURL( remoteObject ) );
  }

  private ConnectionImpl fakeConnection( RemoteObject remoteObject ) {
    ConnectionImpl connection = mock( ConnectionImpl.class );
    when( connection.createServiceObject( anyString() ) ).thenReturn( remoteObject );
    Fixture.fakeConnection( connection );
    return connection;
  }

  @SuppressWarnings( "unchecked" )
  private static String getURL( RemoteObject remoteObject ) {
    ArgumentCaptor< Map > captor = ArgumentCaptor.forClass( Map.class );
    verify( remoteObject ).call( eq( "openURL" ), captor.capture() );
    return ( String )captor.getValue().get( "url" );
  }

}
