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

import org.eclipse.rap.rwt.cluster.test.entrypoints.SessionTimeoutEntryPoint;
import org.eclipse.rap.rwt.cluster.test.entrypoints.UICallbackEntryPoint;
import org.eclipse.rap.rwt.cluster.testfixture.ClusterTestHelper;
import org.eclipse.rap.rwt.cluster.testfixture.client.RWTClient;
import org.eclipse.rap.rwt.cluster.testfixture.client.Response;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngineFactory;
import org.eclipse.rap.rwt.internal.uicallback.UICallBackManager;
import org.eclipse.rap.rwt.lifecycle.UICallBack;
import org.eclipse.swt.widgets.Display;


@SuppressWarnings("restriction")
public abstract class UICallBackTestBase extends TestCase {

  private IServletEngine servletEngine;
  private RWTClient client;

  abstract IServletEngineFactory getServletEngineFactory();

  public void testUICallbackRequestResponse() throws Exception {
    servletEngine.start( UICallbackEntryPoint.class );
    client.sendStartupRequest();
    client.sendInitializationRequest();
    HttpSession session = ClusterTestHelper.getFirstHttpSession( servletEngine );
    final Display display = ClusterTestHelper.getSessionDisplay( session );

    Thread thread = new Thread( new Runnable() {
      public void run() {
        sleep( 2000 );
        UICallBack.runNonUIThreadWithFakeContext( display, new Runnable() {
          public void run() {
            UICallBackManager uiCallBackManager = UICallBackManager.getInstance();
            uiCallBackManager.setRequestCheckInterval( 500 );
            uiCallBackManager.setHasRunnables( true );
            uiCallBackManager.releaseBlockedRequest();
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
    servletEngine.start( UICallbackEntryPoint.class );
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

    UICallBackManager uiCallBackManager = getUICallBackManager();
    assertFalse( uiCallBackManager.isCallBackRequestBlocked() );
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
    UICallBackManager uiCallBackManager = getUICallBackManager();
    asyncSendUICallBackRequest();
    while( !uiCallBackManager.isCallBackRequestBlocked() ) {
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

  private UICallBackManager getUICallBackManager() {
    final UICallBackManager[] result = { null };
    HttpSession session = ClusterTestHelper.getFirstHttpSession( servletEngine );
    Display display = ClusterTestHelper.getSessionDisplay( session );
    UICallBack.runNonUIThreadWithFakeContext( display, new Runnable() {
      public void run() {
        result[ 0 ] = UICallBackManager.getInstance();
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
    UICallBack.runNonUIThreadWithFakeContext( display, new Runnable() {
      public void run() {
        UICallBackManager.getInstance().setRequestCheckInterval( interval );
      }
    } );
  }
}
