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
package org.eclipse.swt.internal.widgets.toolbarkit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ToolBarLCA_Test {

  private Display display;
  private Shell shell;
  private ToolBarLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    lca = new ToolBarLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testControlListeners() throws IOException {
    ToolBar toolBar = new ToolBar( shell, SWT.NONE );
    ControlLCATestUtil.testActivateListener( toolBar );
    ControlLCATestUtil.testFocusListener( toolBar );
    ControlLCATestUtil.testMouseListener( toolBar );
    ControlLCATestUtil.testKeyListener( toolBar );
    ControlLCATestUtil.testTraverseListener( toolBar );
    ControlLCATestUtil.testMenuDetectListener( toolBar );
    ControlLCATestUtil.testHelpListener( toolBar );
  }

  @Test
  public void testRenderCreate() throws IOException {
    ToolBar toolBar = new ToolBar( shell, SWT.NONE );

    lca.renderInitialization( toolBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolBar );
    assertEquals( "rwt.widgets.ToolBar", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "HORIZONTAL" ) );
    assertFalse( Arrays.asList( styles ).contains( "H_SCROLL" ) );
  }

  @Test
  public void testRenderCreate_Vertical() throws IOException {
    ToolBar toolBar = new ToolBar( shell, SWT.VERTICAL );

    lca.renderInitialization( toolBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolBar );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "VERTICAL" ) );
    assertFalse( Arrays.asList( styles ).contains( "V_SCROLL" ) );
  }

  @Test
  public void testRenderCreate_Flat() throws IOException {
    ToolBar toolBar = new ToolBar( shell, SWT.FLAT );

    lca.renderInitialization( toolBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolBar );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "FLAT" ) );
  }

  @Test
  public void testRenderParent() throws IOException {
    ToolBar toolBar = new ToolBar( shell, SWT.NONE );

    lca.renderInitialization( toolBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolBar );
    assertEquals( WidgetUtil.getId( toolBar.getParent() ), operation.getParent() );
  }

}
