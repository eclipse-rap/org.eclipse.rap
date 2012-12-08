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

import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;


public class ProgressBarLCA_Test extends TestCase {

  private Shell shell;
  private Display display;
  private ProgressBarLCA lca;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    lca = new ProgressBarLCA();
    Fixture.fakeNewRequest( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testControlListeners() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.NONE );
    ControlLCATestUtil.testActivateListener( progressBar );
    ControlLCATestUtil.testMouseListener( progressBar );
    ControlLCATestUtil.testKeyListener( progressBar );
    ControlLCATestUtil.testTraverseListener( progressBar );
    ControlLCATestUtil.testMenuDetectListener( progressBar );
    ControlLCATestUtil.testHelpListener( progressBar );
  }

  public void testPreserveValues() {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( progressBar );
    Object preserved = adapter.getPreserved( ProgressBarLCA.PROP_STATE );
    assertNull( preserved );
  }

  public void testRenderCreate() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );

    lca.renderInitialization( progressBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( progressBar );
    assertEquals( "rwt.widgets.ProgressBar", operation.getType() );
  }

  public void testRenderParent() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );

    lca.renderInitialization( progressBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( progressBar );
    assertEquals( WidgetUtil.getId( progressBar.getParent() ), operation.getParent() );
  }

  public void testRenderCreateWithVerticalAndIndeterminate() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.VERTICAL | SWT.INDETERMINATE );

    lca.renderInitialization( progressBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( progressBar );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "VERTICAL" ) );
    assertTrue( Arrays.asList( styles ).contains( "INDETERMINATE" ) );
  }

  public void testRenderInitialMinimum() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );

    lca.render( progressBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( progressBar );
    assertTrue( operation.getPropertyNames().indexOf( "minimum" ) == -1 );
  }

  public void testRenderMinimum() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );

    progressBar.setMinimum( 10 );
    lca.renderChanges( progressBar );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( progressBar, "minimum" ) );
  }

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

  public void testRenderInitialMaxmum() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );

    lca.render( progressBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( progressBar );
    assertTrue( operation.getPropertyNames().indexOf( "maximum" ) == -1 );
  }

  public void testRenderMaxmum() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );

    progressBar.setMaximum( 10 );
    lca.renderChanges( progressBar );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( progressBar, "maximum" ) );
  }

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

  public void testRenderInitialSelection() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );

    lca.render( progressBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( progressBar );
    assertTrue( operation.getPropertyNames().indexOf( "selection" ) == -1 );
  }

  public void testRenderSelection() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );

    progressBar.setSelection( 10 );
    lca.renderChanges( progressBar );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( progressBar, "selection" ) );
  }

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

  public void testRenderInitialState() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );

    lca.render( progressBar );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( progressBar );
    assertTrue( operation.getPropertyNames().indexOf( "state" ) == -1 );
  }

  public void testRenderState() throws IOException {
    ProgressBar progressBar = new ProgressBar( shell, SWT.HORIZONTAL );

    progressBar.setState( SWT.ERROR );
    lca.renderChanges( progressBar );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "error", message.findSetProperty( progressBar, "state" ) );
  }

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
