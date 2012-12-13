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

import static org.mockito.Mockito.mock;
import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.serverpush.ServerPushManager;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.swt.widgets.Display;


public class ServerPushRenderer_Test extends TestCase {

  private static final String REMOTE_OBJECT_ID = "rwt.client.ServerPush";

  private Display display;
  private ServerPushRenderer renderer;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    renderer = new ServerPushRenderer();
    Fixture.fakeNewRequest();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testDoNotCreateServerPushClientObject() {
    // Server push object is created by the client
    ServerPushManager.getInstance().activateServerPushFor( "id" );

    renderer.render();

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findCreateOperation( REMOTE_OBJECT_ID ) );
  }

  public void testNothingRenderedIfNotActivated() {
    renderer.render();

    Message message = Fixture.getProtocolMessage();
    assertEquals( 0, message.getOperationCount() );
  }

  public void testActivationIsRendered() {
    ServerPushManager.getInstance().activateServerPushFor( "id" );

    renderer.render();

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findSetProperty( REMOTE_OBJECT_ID, "active" ) );
  }

  public void testActivationIsPreserved() {
    ServerPushManager.getInstance().activateServerPushFor( "id" );
    renderer.render();

    Fixture.fakeNewRequest();
    ServerPushManager.getInstance().activateServerPushFor( "id" );
    renderer.render();

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( REMOTE_OBJECT_ID, "active" ) );
  }

  public void testDeactivationIsRendered() {
    ServerPushManager.getInstance().activateServerPushFor( "id" );
    renderer.render();

    Fixture.fakeNewRequest();
    ServerPushManager.getInstance().deactivateServerPushFor( "id" );
    renderer.render();

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findSetProperty( REMOTE_OBJECT_ID, "active" ) );
  }

  public void testDeactivationIsNotRenderedWhenRunnablesArePending() {
    ServerPushManager.getInstance().activateServerPushFor( "id" );
    renderer.render();
    display.asyncExec( mock( Runnable.class ) );

    Fixture.fakeNewRequest();
    ServerPushManager.getInstance().deactivateServerPushFor( "id" );
    renderer.render();

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( REMOTE_OBJECT_ID, "active" ) );
  }

}
