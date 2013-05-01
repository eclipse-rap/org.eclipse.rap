/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.groupkit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class GroupLCA_Test {

  private Display display;
  private Shell shell;
  private Group group;
  private GroupLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    group = new Group( shell, SWT.NONE );
    lca = new GroupLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testControlListeners() throws IOException {
    ControlLCATestUtil.testActivateListener( group );
    ControlLCATestUtil.testMouseListener( group );
    ControlLCATestUtil.testKeyListener( group );
    ControlLCATestUtil.testTraverseListener( group );
    ControlLCATestUtil.testMenuDetectListener( group );
    ControlLCATestUtil.testHelpListener( group );
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( group );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( group );
    assertEquals( "rwt.widgets.Group", operation.getType() );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( group );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( group );
    assertEquals( WidgetUtil.getId( group.getParent() ), operation.getParent() );
  }

  @Test
  public void testRenderInitialText() throws IOException {
    lca.render( group );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( group );
    assertTrue( operation.getPropertyNames().indexOf( "text" ) == -1 );
  }

  @Test
  public void testRenderText() throws IOException {
    group.setText( "foo" );
    lca.renderChanges( group );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( group, "text" ).asString() );
  }

  @Test
  public void testRenderTextWithMnemonic() throws IOException {
    group.setText( "te&st" );
    lca.renderChanges( group );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "test", message.findSetProperty( group, "text" ).asString() );
  }

  @Test
  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( group );

    group.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( group );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( group, "text" ) );
  }

  @Test
  public void testRenderInitialMnemonicIndex() throws IOException {
    lca.renderChanges( group );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( group, "mnemonicIndex" ) );
  }

  @Test
  public void testRenderMnemonicIndex() throws IOException {
    group.setText( "te&st" );
    lca.renderChanges( group );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 2, message.findSetProperty( group, "mnemonicIndex" ).asInt() );
  }

  @Test
  public void testRenderMnemonicIndexUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( group );

    group.setText( "te&st" );
    Fixture.preserveWidgets();
    lca.renderChanges( group );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( group, "mnemonicIndex" ) );
  }

}
