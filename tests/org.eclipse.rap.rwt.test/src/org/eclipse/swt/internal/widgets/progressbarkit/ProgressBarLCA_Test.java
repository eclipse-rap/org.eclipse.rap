/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.progressbarkit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ProgressBarLCA_Test {

  private Shell shell;
  private Display display;
  private ProgressBarLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    lca = new ProgressBarLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testControlListeners() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.NONE );
    ControlLCATestUtil.testActivateListener( progressBar );
    ControlLCATestUtil.testMouseListener( progressBar );
    ControlLCATestUtil.testKeyListener( progressBar );
    ControlLCATestUtil.testTraverseListener( progressBar );
    ControlLCATestUtil.testMenuDetectListener( progressBar );
    ControlLCATestUtil.testHelpListener( progressBar );
  }

  @Test
  public void testPreserveValues() {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );
    Fixture.preserveWidgets();
    WidgetAdapter adapter = WidgetUtil.getAdapter( progressBar );
    Object preserved = adapter.getPreserved( ProgressBarLCA.PROP_STATE );
    assertNull( preserved );
  }

  @Test
  public void testRenderCreate() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );

    lca.renderInitialization( progressBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( progressBar );
    assertEquals( "rwt.widgets.ProgressBar", operation.getType() );
  }

  @Test
  public void testRenderParent() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );

    lca.renderInitialization( progressBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( progressBar );
    assertEquals( WidgetUtil.getId( progressBar.getParent() ), operation.getParent() );
  }

  @Test
  public void testRenderCreateWithVerticalAndIndeterminate() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.VERTICAL | SWT.INDETERMINATE );

    lca.renderInitialization( progressBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( progressBar );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "VERTICAL" ) );
    assertTrue( Arrays.asList( styles ).contains( "INDETERMINATE" ) );
  }

  @Test
  public void testRenderInitialMinimum() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );

    lca.render( progressBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( progressBar );
    assertTrue( operation.getPropertyNames().indexOf( "minimum" ) == -1 );
  }

  @Test
  public void testRenderMinimum() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );

    progressBar.setMinimum( 10 );
    lca.renderChanges( progressBar );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( progressBar, "minimum" ) );
  }

  @Test
  public void testRenderMinimumUnchanged() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );
    Fixture.markInitialized( display );
    Fixture.markInitialized( progressBar );

    progressBar.setMinimum( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( progressBar );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( progressBar, "minimum" ) );
  }

  @Test
  public void testRenderInitialMaxmum() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );

    lca.render( progressBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( progressBar );
    assertTrue( operation.getPropertyNames().indexOf( "maximum" ) == -1 );
  }

  @Test
  public void testRenderMaxmum() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );

    progressBar.setMaximum( 10 );
    lca.renderChanges( progressBar );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( progressBar, "maximum" ) );
  }

  @Test
  public void testRenderMaxmumUnchanged() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );
    Fixture.markInitialized( display );
    Fixture.markInitialized( progressBar );

    progressBar.setMaximum( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( progressBar );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( progressBar, "maximum" ) );
  }

  @Test
  public void testRenderInitialSelection() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );

    lca.render( progressBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( progressBar );
    assertTrue( operation.getPropertyNames().indexOf( "selection" ) == -1 );
  }

  @Test
  public void testRenderSelection() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );

    progressBar.setSelection( 10 );
    lca.renderChanges( progressBar );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( progressBar, "selection" ) );
  }

  @Test
  public void testRenderSelectionUnchanged() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );
    Fixture.markInitialized( display );
    Fixture.markInitialized( progressBar );

    progressBar.setSelection( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( progressBar );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( progressBar, "selection" ) );
  }

  @Test
  public void testRenderInitialState() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );

    lca.render( progressBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( progressBar );
    assertTrue( operation.getPropertyNames().indexOf( "state" ) == -1 );
  }

  @Test
  public void testRenderState() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );

    progressBar.setState( SWT.ERROR );
    lca.renderChanges( progressBar );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "error", message.findSetProperty( progressBar, "state" ) );
  }

  @Test
  public void testRenderStateUnchanged() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );
    Fixture.markInitialized( display );
    Fixture.markInitialized( progressBar );

    progressBar.setState( SWT.ERROR );
    Fixture.preserveWidgets();
    lca.renderChanges( progressBar );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( progressBar, "state" ) );
  }
}
