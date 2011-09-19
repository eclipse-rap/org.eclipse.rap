/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.scalekit;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class ScaleLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private ScaleLCA lca;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    lca = new ScaleLCA();
    Fixture.fakeNewRequest( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testScalePreserveValues() {
    Scale scale = new Scale( shell, SWT.HORIZONTAL );
    Fixture.markInitialized( display );
    // Test preserved minimum, maximum,
    // selection, increment and ageIncrement
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( scale );
    Integer minimum = ( Integer )adapter.getPreserved( ScaleLCA.PROP_MINIMUM );
    assertEquals( 0, minimum.intValue() );
    Integer maximum = ( Integer )adapter.getPreserved( ScaleLCA.PROP_MAXIMUM );
    assertEquals( 100, maximum.intValue() );
    Integer selection = ( Integer )adapter.getPreserved( ScaleLCA.PROP_SELECTION );
    assertEquals( 0, selection.intValue() );
    Integer increment = ( Integer )adapter.getPreserved( ScaleLCA.PROP_INCREMENT );
    assertEquals( 1, increment.intValue() );
    Integer pageIncrement = ( Integer )adapter.getPreserved( ScaleLCA.PROP_PAGE_INCREMENT );
    assertEquals( 10, pageIncrement.intValue() );
    Fixture.clearPreserved();
    // Test preserved control properties
    testPreserveControlProperties( scale );
    // Test preserved selection listeners
    testPreserveSelectionListener( scale );
  }

  public void testSelectionEvent() {
    Scale scale = new Scale( shell, SWT.HORIZONTAL );
    testSelectionEvent( scale );
  }

  private void testPreserveControlProperties( Scale scale ) {
    // bound
    Rectangle rectangle = new Rectangle( 10, 10, 10, 10 );
    scale.setBounds( rectangle );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( scale );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    // enabled
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( scale );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    scale.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( scale );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    scale.setEnabled( true );
    // visible
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( scale );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    scale.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( scale );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    // menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( scale );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( scale );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    scale.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( scale );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    //foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    scale.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    scale.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
    scale.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( scale );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
  }

  private void testPreserveSelectionListener( Scale scale ) {
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( scale );
    Boolean hasListeners = ( Boolean )adapter.getPreserved( ScaleLCA.PROP_SELECTION_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    scale.addSelectionListener( selectionListener );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( scale );
    hasListeners = ( Boolean )adapter.getPreserved( ScaleLCA.PROP_SELECTION_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
  }

  private void testSelectionEvent( final Scale scale ) {
    final StringBuffer log = new StringBuffer();
    SelectionListener selectionListener = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        assertEquals( scale, event.getSource() );
        assertEquals( null, event.item );
        assertEquals( SWT.NONE, event.detail );
        assertEquals( 0, event.x );
        assertEquals( 0, event.y );
        assertEquals( 0, event.width );
        assertEquals( 0, event.height );
        assertEquals( true, event.doit );
        log.append( "widgetSelected" );
      }
    };
    scale.addSelectionListener( selectionListener );
    String scaleId = WidgetUtil.getId( scale );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, scaleId );
    Fixture.readDataAndProcessAction( scale );
    assertEquals( "widgetSelected", log.toString() );
  }

  public void testRenderCreate() throws IOException {
    Scale scale = new Scale( shell, SWT.NONE );

    lca.renderInitialization( scale );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( scale );
    assertEquals( "rwt.widgets.Scale", operation.getType() );
  }

  public void testRenderParent() throws IOException {
    Scale scale = new Scale( shell, SWT.NONE );

    lca.renderInitialization( scale );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( scale );
    assertEquals( WidgetUtil.getId( scale.getParent() ), operation.getParent() );
  }

  public void testRenderCreateWithHorizontal() throws IOException {
    Scale scale = new Scale( shell, SWT.HORIZONTAL );

    lca.renderInitialization( scale );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( scale );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "HORIZONTAL" ) );
  }

  public void testRenderInitialMinimum() throws IOException {
    Scale scale = new Scale( shell, SWT.NONE );

    lca.render( scale );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( scale );
    assertTrue( operation.getPropertyNames().indexOf( "minimum" ) == -1 );
  }

  public void testRenderMinimum() throws IOException {
    Scale scale = new Scale( shell, SWT.NONE );

    scale.setMinimum( 10 );
    lca.renderChanges( scale );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( scale, "minimum" ) );
  }

  public void testRenderMinimumUnchanged() throws IOException {
    Scale scale = new Scale( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( scale );

    scale.setMinimum( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( scale );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( scale, "minimum" ) );
  }

  public void testRenderInitialMaxmum() throws IOException {
    Scale scale = new Scale( shell, SWT.NONE );

    lca.render( scale );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( scale );
    assertTrue( operation.getPropertyNames().indexOf( "maximum" ) == -1 );
  }

  public void testRenderMaxmum() throws IOException {
    Scale scale = new Scale( shell, SWT.NONE );

    scale.setMaximum( 10 );
    lca.renderChanges( scale );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( scale, "maximum" ) );
  }

  public void testRenderMaxmumUnchanged() throws IOException {
    Scale scale = new Scale( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( scale );

    scale.setMaximum( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( scale );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( scale, "maximum" ) );
  }

  public void testRenderInitialSelection() throws IOException {
    Scale scale = new Scale( shell, SWT.NONE );

    lca.render( scale );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( scale );
    assertTrue( operation.getPropertyNames().indexOf( "selection" ) == -1 );
  }

  public void testRenderSelection() throws IOException {
    Scale scale = new Scale( shell, SWT.NONE );

    scale.setSelection( 10 );
    lca.renderChanges( scale );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( scale, "selection" ) );
  }

  public void testRenderSelectionUnchanged() throws IOException {
    Scale scale = new Scale( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( scale );

    scale.setSelection( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( scale );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( scale, "selection" ) );
  }

  public void testRenderInitialIncrement() throws IOException {
    Scale scale = new Scale( shell, SWT.NONE );

    lca.render( scale );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( scale );
    assertTrue( operation.getPropertyNames().indexOf( "increment" ) == -1 );
  }

  public void testRenderIncrement() throws IOException {
    Scale scale = new Scale( shell, SWT.NONE );

    scale.setIncrement( 2 );
    lca.renderChanges( scale );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 2 ), message.findSetProperty( scale, "increment" ) );
  }

  public void testRenderIncrementUnchanged() throws IOException {
    Scale scale = new Scale( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( scale );

    scale.setIncrement( 2 );
    Fixture.preserveWidgets();
    lca.renderChanges( scale );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( scale, "increment" ) );
  }

  public void testRenderInitialPageIncrement() throws IOException {
    Scale scale = new Scale( shell, SWT.NONE );

    lca.render( scale );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( scale );
    assertTrue( operation.getPropertyNames().indexOf( "pageIncrement" ) == -1 );
  }

  public void testRenderPageIncrement() throws IOException {
    Scale scale = new Scale( shell, SWT.NONE );

    scale.setPageIncrement( 20 );
    lca.renderChanges( scale );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 20 ), message.findSetProperty( scale, "pageIncrement" ) );
  }

  public void testRenderPageIncrementUnchanged() throws IOException {
    Scale scale = new Scale( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( scale );

    scale.setPageIncrement( 20 );
    Fixture.preserveWidgets();
    lca.renderChanges( scale );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( scale, "pageIncrement" ) );
  }

  public void testRenderAddSelectionListener() throws Exception {
    Scale scale = new Scale( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( scale );
    Fixture.preserveWidgets();

    scale.addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( scale );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( scale, "selection" ) );
  }

  public void testRenderRemoveSelectionListener() throws Exception {
    Scale scale = new Scale( shell, SWT.NONE );
    SelectionListener listener = new SelectionAdapter() { };
    scale.addSelectionListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( scale );
    Fixture.preserveWidgets();

    scale.removeSelectionListener( listener );
    lca.renderChanges( scale );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( scale, "selection" ) );
  }
}
