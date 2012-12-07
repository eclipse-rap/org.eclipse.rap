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

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.remote.RemoteObject;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectFactory;
import org.eclipse.rap.rwt.internal.remote.RemoteOperationHandler;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.mockito.ArgumentCaptor;


public class CientInfoImpl_Test extends TestCase {

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testThrowsExceptionWhenValueNotSet() {
    fakeRemoteObjectFactory( mock( RemoteObject.class ) );

    ClientInfoImpl clientInfo = new ClientInfoImpl();
    try {
      clientInfo.getTimezoneOffset();
      fail();
    } catch( IllegalStateException ex ) {
      // expected
    }
  }

  public void testCreatesRemoteObjectWithCorrectId() {
    RemoteObjectFactory factory = fakeRemoteObjectFactory( mock( RemoteObject.class ) );

    new ClientInfoImpl();

    verify( factory ).createServiceObject( eq( "rwt.client.ClientInfo" ) );
  }

  public void testReadsTimezoneOffsetFromHandler() {
    RemoteObject remoteObject = mock( RemoteObject.class );
    fakeRemoteObjectFactory( remoteObject );
    ClientInfoImpl clientInfo = new ClientInfoImpl();
    RemoteOperationHandler handler = getHandler( remoteObject );

    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( "timezoneOffset", new Integer( -90 ) );
    handler.handleSet( parameters );

    assertEquals( -90, clientInfo.getTimezoneOffset() );
  }

  private static RemoteOperationHandler getHandler( RemoteObject remoteObject ) {
    ArgumentCaptor<RemoteOperationHandler> captor
      = ArgumentCaptor.forClass( RemoteOperationHandler.class );
    verify( remoteObject ).setHandler( captor.capture() );
    return captor.getValue();
  }

  private RemoteObjectFactory fakeRemoteObjectFactory( RemoteObject remoteObject ) {
    RemoteObjectFactory factory = mock( RemoteObjectFactory.class );
    when( factory.createServiceObject( anyString() ) ).thenReturn( remoteObject );
    Fixture.fakeRemoteObjectFactory( factory );
    return factory;
  }

}
