/*******************************************************************************
 * Copyright (c) 2011, 2012 Rüdiger Herrmann and others.
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

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.client.service.UrlLauncher;
import org.eclipse.rap.rwt.internal.remote.RemoteObject;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectFactory;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.widgets.Display;
import org.mockito.ArgumentCaptor;


public class UrlLauncherImpl_Test extends TestCase {

  private static final String URL = "http://someurl.com/foo/bar";

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    new Display();
    Fixture.fakeNewRequest();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testCreatesRemoteObjectWithCorrectId() {
    RemoteObjectFactory factory = fakeRemoteObjectFactory( mock( RemoteObject.class ) );

    UrlLauncher launcher = new UrlLauncherImpl();
    launcher.openURL( URL );

    verify( factory ).createServiceObject( eq( "rwt.client.UrlLauncher" ) );
  }

  public void testWritesCallOperation() {
    RemoteObject remoteObject = mock( RemoteObject.class );
    fakeRemoteObjectFactory( remoteObject );

    UrlLauncher launcher = new UrlLauncherImpl();
    launcher.openURL( URL );

    assertEquals( URL, getURL( remoteObject ) );
  }

  private RemoteObjectFactory fakeRemoteObjectFactory( RemoteObject remoteObject ) {
    RemoteObjectFactory factory = mock( RemoteObjectFactory.class );
    when( factory.createServiceObject( anyString() ) ).thenReturn( remoteObject );
    Fixture.fakeRemoteObjectFactory( factory );
    return factory;
  }

  @SuppressWarnings( "unchecked" )
  private static String getURL( RemoteObject remoteObject ) {
    ArgumentCaptor< Map > captor = ArgumentCaptor.forClass( Map.class );
    verify( remoteObject ).call( eq( "openURL" ), captor.capture() );
    return ( String )captor.getValue().get( "url" );
  }
}
