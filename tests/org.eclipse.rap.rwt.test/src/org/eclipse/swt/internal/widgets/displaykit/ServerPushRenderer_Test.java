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
package org.eclipse.swt.internal.widgets.displaykit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

import org.eclipse.rap.rwt.internal.serverpush.ServerPushManager;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ServerPushRenderer_Test {

  private static final Object HANDLE = new Object();
  private static final String REMOTE_OBJECT_ID = "rwt.client.ServerPush";

  private Display display;
  private ServerPushRenderer renderer;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    renderer = new ServerPushRenderer();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testDoNotCreateServerPushClientObject() {
    // Server push object is created by the client
    ServerPushManager.getInstance().activateServerPushFor( HANDLE );

    renderer.render();

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findCreateOperation( REMOTE_OBJECT_ID ) );
  }

  @Test
  public void testNothingRenderedIfNotActivated() {
    renderer.render();

    Message message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
  }

  @Test
  public void testActivationIsRendered() {
    ServerPushManager.getInstance().activateServerPushFor( HANDLE );

    renderer.render();

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( REMOTE_OBJECT_ID, "active" ) );
  }

  @Test
  public void testActivationIsPreserved() {
    ServerPushManager.getInstance().activateServerPushFor( HANDLE );
    renderer.render();

    Fixture.fakeNewRequest();
    ServerPushManager.getInstance().activateServerPushFor( HANDLE );
    renderer.render();

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( REMOTE_OBJECT_ID, "active" ) );
  }

  @Test
  public void testDeactivationIsRendered() {
    ServerPushManager.getInstance().activateServerPushFor( HANDLE );
    renderer.render();

    Fixture.fakeNewRequest();
    ServerPushManager.getInstance().deactivateServerPushFor( HANDLE );
    renderer.render();

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findSetProperty( REMOTE_OBJECT_ID, "active" ) );
  }

  @Test
  public void testDeactivationIsNotRenderedWhenRunnablesArePending() {
    ServerPushManager.getInstance().activateServerPushFor( HANDLE );
    renderer.render();
    display.asyncExec( mock( Runnable.class ) );

    Fixture.fakeNewRequest();
    ServerPushManager.getInstance().deactivateServerPushFor( HANDLE );
    renderer.render();

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( REMOTE_OBJECT_ID, "active" ) );
  }

}
