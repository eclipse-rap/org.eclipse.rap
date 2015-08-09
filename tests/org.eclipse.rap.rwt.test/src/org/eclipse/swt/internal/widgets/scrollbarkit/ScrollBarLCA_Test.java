/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.scrollbarkit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;

import java.io.IOException;

import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectImpl;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ScrollBarLCA_Test {

  private Display display;
  private Shell shell;
  private ScrollBarLCA lca;
  private ScrollBar scrollBar;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    Scrollable scrollable = new ScrolledComposite( shell, SWT.H_SCROLL );
    scrollBar = scrollable.getHorizontalBar();
    lca = new ScrollBarLCA();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testRenderVisible_initial() throws IOException {
    Fixture.markInitialized( display );
    lca.render( scrollBar );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( scrollBar, "visibility" ) );
  }

  @Test
  public void testRenderVisible_initial_changed() throws IOException {
    Fixture.markInitialized( display );
    scrollBar.setVisible( true );
    lca.renderChanges( scrollBar );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findSetProperty( scrollBar, "visibility" ) );
  }

  @Test
  public void testRenderVisible_changed() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( scrollBar );
    scrollBar.setVisible( true );
    Fixture.preserveWidgets();

    scrollBar.setVisible( false );
    lca.renderChanges( scrollBar );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findSetProperty( scrollBar, "visibility" ) );
  }

  @Test
  public void testRenderVisible_unchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( scrollBar );
    scrollBar.setVisible( false );
    Fixture.preserveWidgets();

    lca.renderChanges( scrollBar );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( scrollBar, "visibility" ) );
  }

  @Test
  public void testRenderListenSelection() throws Exception {
    Fixture.markInitialized( scrollBar );
    Fixture.clearPreserved();

    lca.renderChanges( scrollBar );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( scrollBar, "Selection" ) );
  }

  @Test
  public void testRenderListenSelection_added() throws Exception {
    Fixture.markInitialized( scrollBar );
    Fixture.clearPreserved();

    scrollBar.addListener( SWT.Selection, mock( Listener.class ) );
    lca.renderChanges( scrollBar );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( scrollBar, "Selection" ) );
  }

  @Test
  public void testRenderListenSelection_removed() throws Exception {
    Fixture.markInitialized( scrollBar );
    Listener listener = mock( Listener.class );
    scrollBar.addListener( SWT.Selection, listener );
    Fixture.clearPreserved();

    scrollBar.removeListener( SWT.Selection, listener );
    lca.renderChanges( scrollBar );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( scrollBar, "Selection" ) );
  }

  @Test
  public void testRenderListenSelection_unchanged() throws Exception {
    Fixture.markInitialized( scrollBar );
    scrollBar.addListener( SWT.Selection, mock( Listener.class ) );
    Fixture.clearPreserved();

    lca.renderChanges( scrollBar );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( scrollBar, "Selection" ) );
  }

  @Test
  public void testRenderDispose_doesNotRenderDestroy() throws IOException {
    Fixture.markInitialized( scrollBar );

    scrollBar.dispose();
    lca.renderDispose( scrollBar );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findDestroyOperation( scrollBar ) );
    RemoteObjectImpl remoteObject = ( RemoteObjectImpl )getRemoteObject( scrollBar );
    assertTrue( remoteObject.isDestroyed() );
  }

}
