/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.sashkit;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.internal.lifecycle.JSConst;
import org.eclipse.rap.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.*;

public class SashLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private SashLCA lca;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    lca = new SashLCA();
    Fixture.fakeNewRequest( display );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testControlListeners() throws IOException {
    Sash sash = new Sash( shell, SWT.NONE );
    ControlLCATestUtil.testActivateListener( sash );
    ControlLCATestUtil.testFocusListener( sash );
    ControlLCATestUtil.testMouseListener( sash );
    ControlLCATestUtil.testKeyListener( sash );
    ControlLCATestUtil.testTraverseListener( sash );
    ControlLCATestUtil.testMenuDetectListener( sash );
    ControlLCATestUtil.testHelpListener( sash );
  }

  public void testPreserveValues() {
    Sash sash = new Sash( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.preserveWidgets();
    //control: enabled
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( sash );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    sash.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    sash.setEnabled( true );
    //visible
    sash.setSize( 10, 10 );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    sash.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    //menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( sash );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    sash.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    //bound
    Rectangle rectangle = new Rectangle( 10, 10, 30, 50 );
    sash.setBounds( rectangle );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    //foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    sash.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    sash.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    sash.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    //tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( null, sash.getToolTipText() );
    Fixture.clearPreserved();
    sash.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( sash );
    assertEquals( "some text", sash.getToolTipText() );
    Fixture.clearPreserved();
  }

  public void testSelectionEvent() {
    final Sash sash = new Sash( shell, SWT.NONE );
    final StringBuilder log = new StringBuilder();
    SelectionListener selectionListener = new SelectionAdapter() {
      @Override
      public void widgetSelected( SelectionEvent event ) {
        assertEquals( sash, event.getSource() );
        assertEquals( null, event.item );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( 0, event.stateMask );
        assertEquals( SWT.DRAG, event.detail );
        assertEquals( true, event.doit );
        log.append( "widgetSelected" );
      }
    };
    sash.addSelectionListener( selectionListener );
    String sashId = WidgetUtil.getId( sash );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, sashId );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED + ".detail", "drag" );
    Fixture.readDataAndProcessAction( sash );
    assertEquals( "widgetSelected", log.toString() );
  }

  public void testRenderCreate() throws IOException {
    Sash sash = new Sash( shell, SWT.NONE );

    lca.renderInitialization( sash );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( sash );
    assertEquals( "rwt.widgets.Sash", operation.getType() );
  }

  public void testRenderParent() throws IOException {
    Sash sash = new Sash( shell, SWT.NONE );

    lca.renderInitialization( sash );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( sash );
    assertEquals( WidgetUtil.getId( sash.getParent() ), operation.getParent() );
  }

  public void testRenderCreateWithHorizontal() throws IOException {
    Sash sash = new Sash( shell, SWT.HORIZONTAL );

    lca.renderInitialization( sash );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( sash );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "HORIZONTAL" ) );
  }
}
