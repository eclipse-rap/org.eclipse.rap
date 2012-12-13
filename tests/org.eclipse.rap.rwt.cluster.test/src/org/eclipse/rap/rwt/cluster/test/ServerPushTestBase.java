/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.cluster.test;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.cluster.test.entrypoints.SessionTimeoutEntryPoint;
import org.eclipse.rap.rwt.cluster.test.entrypoints.ServerPushEntryPoint;
import org.eclipse.rap.rwt.cluster.testfixture.ClusterTestHelper;
import org.eclipse.rap.rwt.cluster.testfixture.client.RWTClient;
import org.eclipse.rap.rwt.cluster.testfixture.client.Response;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngineFactory;
import org.eclipse.rap.rwt.internal.serverpush.ServerPushManager;
import org.eclipse.swt.widgets.Display;


@SuppressWarnings("restriction")
public abstract class ServerPushTestBase extends TestCase {

  private IServletEngine servletEngine;
  private RWTClient client;

  abstract IServletEngineFactory getServletEngineFactory();

  public void testCallbackRequestResponse() throws Exception {
    servletEngine.start( ServerPushEntryPoint.class );
    client.sendStartupRequest();
    client.sendInitializationRequest();
    HttpSession session = ClusterTestHelper.getFirstHttpSession( servletEngine );
    final Display display = ClusterTestHelper.getSessionDisplay( session );

    Thread thread = new Thread( new Runnable() {
      public void run() {
        sleep( 2000 );
        RWT.getUISession( display ).exec( new Runnable() {
          public void run() {
            ServerPushManager pushManager = ServerPushManager.getInstance();
            pushManager.setRequestCheckInterval( 500 );
            pushManager.setHasRunnables( true );
            pushManager.releaseBlockedRequest();
          }
        } );
      }
    } );
    thread.setDaemon( true );
    thread.start();

    Response response = client.sendUICallBackRequest( 0 );
    thread.join();

    String expected = "[ \"call\", \"uicb\", \"sendUIRequest\" ]";
    assertTrue( response.getContentText().contains( expected ) );
  }

  public void testAbortConnectionDuringUICallbackRequest() throws Exception {
    servletEngine.start( ServerPushEntryPoint.class );
    client.sendStartupRequest();
    client.sendInitializationRequest();
    configureCallbackRequestCheckInterval( 400 );

    try {
      client.sendUICallBackRequest( 200 );
      fail();
    } catch( IOException expected ) {
      assertEquals( "Read timed out", expected.getMessage() );
    }

    Thread.sleep( 800 );

    ServerPushManager pushManager = getUICallBackManager();
    assertFalse( pushManager.isCallBackRequestBlocked() );
  }

  public void testUICallBackRequestDoesNotKeepSessionAlive() throws Exception {
    servletEngine.start( SessionTimeoutEntryPoint.class );
    client.sendStartupRequest();
    client.sendInitializationRequest();
    getUICallBackManager().setRequestCheckInterval( 100 );

    asyncSendUICallBackRequest();
    Thread.sleep( SessionTimeoutEntryPoint.SESSION_SWEEP_INTERVAL );

    assertTrue( SessionTimeoutEntryPoint.isSessionInvalidated() );
  }

  public void testUICallBackRequestDoesNotPreventEngineShutdown() throws Exception {
    servletEngine.start( SessionTimeoutEntryPoint.class );
    client.sendStartupRequest();
    client.sendInitializationRequest();
    ServerPushManager pushManager = getUICallBackManager();
    asyncSendUICallBackRequest();
    while( !pushManager.isCallBackRequestBlocked() ) {
      Thread.yield();
    }

    servletEngine.stop( 2000 );

    assertTrue( SessionTimeoutEntryPoint.isSessionInvalidated() );
  }

  @Override
  protected void setUp() throws Exception {
    servletEngine = getServletEngineFactory().createServletEngine();
    client = new RWTClient( servletEngine );
  }

  @Override
  protected void tearDown() throws Exception {
    servletEngine.stop();
  }

  private ServerPushManager getUICallBackManager() {
    final ServerPushManager[] result = { null };
    HttpSession session = ClusterTestHelper.getFirstHttpSession( servletEngine );
    Display display = ClusterTestHelper.getSessionDisplay( session );
    RWT.getUISession( display ).exec( new Runnable() {
      public void run() {
        result[ 0 ] = ServerPushManager.getInstance();
      }
    } );
    return result[ 0 ];
  }

  private void sleep( int duration ) {
    try {
      Thread.sleep( duration );
    } catch( InterruptedException ie ) {
      throw new RuntimeException( ie );
    }
  }

  private void asyncSendUICallBackRequest() {
    Thread thread = new Thread( new Runnable() {
      public void run() {
        try {
          client.sendUICallBackRequest( 0 );
        } catch( IOException ignore ) {
        }
      }
    } );
    thread.setDaemon( true );
    thread.start();
  }

  private void configureCallbackRequestCheckInterval( final int interval ) {
    HttpSession session = ClusterTestHelper.getFirstHttpSession( servletEngine );
    Display display = ClusterTestHelper.getSessionDisplay( session );
    RWT.getUISession( display ).exec( new Runnable() {
      public void run() {
        ServerPushManager.getInstance().setRequestCheckInterval( interval );
      }
    } );
  }

}
