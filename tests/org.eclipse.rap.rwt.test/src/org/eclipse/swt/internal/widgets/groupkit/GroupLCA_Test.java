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
package org.eclipse.swt.internal.widgets.groupkit;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;


public class GroupLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private GroupLCA lca;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    lca = new GroupLCA();
    Fixture.fakeNewRequest( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testControlListeners() throws IOException {
    Group group = new Group( shell, SWT.NONE );
    ControlLCATestUtil.testActivateListener( group );
    ControlLCATestUtil.testMouseListener( group );
    ControlLCATestUtil.testKeyListener( group );
    ControlLCATestUtil.testTraverseListener( group );
    ControlLCATestUtil.testMenuDetectListener( group );
    ControlLCATestUtil.testHelpListener( group );
  }

  public void testRenderCreate() throws IOException {
    Group group = new Group( shell, SWT.NONE );

    lca.renderInitialization( group );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( group );
    assertEquals( "rwt.widgets.Group", operation.getType() );
  }

  public void testRenderParent() throws IOException {
    Group group = new Group( shell, SWT.NONE );

    lca.renderInitialization( group );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( group );
    assertEquals( WidgetUtil.getId( group.getParent() ), operation.getParent() );
  }

  public void testRenderInitialText() throws IOException {
    Group group = new Group( shell, SWT.NONE );

    lca.render( group );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( group );
    assertTrue( operation.getPropertyNames().indexOf( "text" ) == -1 );
  }

  public void testRenderText() throws IOException {
    Group group = new Group( shell, SWT.NONE );

    group.setText( "foo" );
    lca.renderChanges( group );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( group, "text" ) );
  }

  public void testRenderTextUnchanged() throws IOException {
    Group group = new Group( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( group );

    group.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( group );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( group, "text" ) );
  }
}
