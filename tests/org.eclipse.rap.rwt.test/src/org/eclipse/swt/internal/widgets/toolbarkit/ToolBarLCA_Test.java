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

import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;


public class ToolBarLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private ToolBarLCA lca;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    lca = new ToolBarLCA();
    Fixture.fakeNewRequest( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

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

  public void testRenderCreate_Vertical() throws IOException {
    ToolBar toolBar = new ToolBar( shell, SWT.VERTICAL );

    lca.renderInitialization( toolBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolBar );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "VERTICAL" ) );
    assertFalse( Arrays.asList( styles ).contains( "V_SCROLL" ) );
  }

  public void testRenderCreate_Flat() throws IOException {
    ToolBar toolBar = new ToolBar( shell, SWT.FLAT );

    lca.renderInitialization( toolBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolBar );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "FLAT" ) );
  }

  public void testRenderParent() throws IOException {
    ToolBar toolBar = new ToolBar( shell, SWT.NONE );

    lca.renderInitialization( toolBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( toolBar );
    assertEquals( WidgetUtil.getId( toolBar.getParent() ), operation.getParent() );
  }

}
