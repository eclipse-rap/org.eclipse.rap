/*******************************************************************************
 * Copyright (c) 2011, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class RemoteObjectFactory_Test {

  private Display display;
  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    Fixture.fakeResponseWriter();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCreate() {
    RemoteObject remoteObject = RemoteObjectFactory.createRemoteObject( shell, "type" );

    assertNotNull( remoteObject );
  }

  @Test( expected = NullPointerException.class )
  public void testCreate_failsWithNullWidget() {
    RemoteObjectFactory.createRemoteObject( ( Widget )null, "type" );
  }

  @Test( expected = NullPointerException.class )
  public void testCreate_failsWithNullDisplay() {
    RemoteObjectFactory.createRemoteObject( ( Widget )null, "type" );
  }

  @Test( expected = NullPointerException.class )
  public void testCreate_failsWithNullId() {
    RemoteObjectFactory.createRemoteObject( (String)null, "type" );
  }

  @Test( expected = NullPointerException.class )
  public void testCreate_failsWithNullType() {
    RemoteObjectFactory.createRemoteObject( "id", null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testCreate_failsIfAlreadyCreated() {
    RemoteObjectFactory.createRemoteObject( "id", "type" );

    RemoteObjectFactory.createRemoteObject( "id", "type" );
  }

  @Test
  public void testCreate_acceptsDisposedWidget() {
    shell.dispose();

    RemoteObject remoteObject = RemoteObjectFactory.createRemoteObject( shell, "type" );

    assertNotNull( remoteObject );
  }

  @Test
  public void testCreate_acceptsWidgetWhenDisplayDisposed() {
    display.dispose();

    RemoteObject remoteObject = RemoteObjectFactory.createRemoteObject( shell, "type" );

    assertNotNull( remoteObject );
  }

  @Test
  public void testCreate_appendsCreateOperation() {
    RemoteObjectFactory.createRemoteObject( shell, "type" );

    CreateOperation operation = ( CreateOperation )Fixture.getProtocolMessage().getOperation( 0 );
    assertEquals( "type", operation.getType() );
  }

  @Test
  public void testGet_createsClientObjectLazily() {
    // TODO [rst] Lazy initialization is needed by almost all LCA tests, revise
    RemoteObject remoteObject = RemoteObjectFactory.getRemoteObject( shell );

    assertNotNull( remoteObject );
  }

  @Test
  public void testGet_lazyCreationDoesNotAppendCreate() {
    RemoteObjectFactory.getRemoteObject( shell );

    assertEquals( 0, Fixture.getProtocolMessage().getOperationCount() );
  }

  @Test( expected = NullPointerException.class )
  public void testGet_failsWithNullWidget() {
    RemoteObjectFactory.getRemoteObject( (Widget)null );
  }

  @Test
  public void testGet_returnsSameInstance() {
    RemoteObject remoteObject = RemoteObjectFactory.createRemoteObject( shell, "type" );

    assertSame( remoteObject, RemoteObjectFactory.getRemoteObject( shell ) );
  }

  @Test
  public void testGet_acceptsDisposedDisplay() {
    display.dispose();

    RemoteObject remoteObject = RemoteObjectFactory.getRemoteObject( display );

    assertNotNull( remoteObject );
  }

}
