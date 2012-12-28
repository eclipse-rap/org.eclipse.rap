/*******************************************************************************
 * Copyright (c) 2008, 2012 Innoopract Informationssysteme GmbH and others.
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

import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;


public class ScaleLCA_Test {

  private Display display;
  private Shell shell;
  private Scale scale;
  private ScaleLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    scale = new Scale( shell, SWT.NONE );
    lca = new ScaleLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testScalePreserveValues() {
    Scale scale = new Scale( shell, SWT.HORIZONTAL );
    Fixture.markInitialized( display );
    // Test preserved minimum, maximum,
    // selection, increment and ageIncrement
    Fixture.preserveWidgets();
    WidgetAdapter adapter = WidgetUtil.getAdapter( scale );
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
  }

  @Test
  public void testSelectionEvent() {
    SelectionListener listener = mock( SelectionListener.class );
    scale.addSelectionListener( listener );

    Fixture.fakeNotifyOperation( getId( scale ), ClientMessageConst.EVENT_SELECTION, null );
    Fixture.readDataAndProcessAction( scale );

    ArgumentCaptor<SelectionEvent> captor = ArgumentCaptor.forClass( SelectionEvent.class );
    verify( listener, times( 1 ) ).widgetSelected( captor.capture() );
    SelectionEvent event = captor.getValue();
    assertEquals( scale, event.getSource() );
    assertEquals( null, event.item );
    assertEquals( 0, event.x );
    assertEquals( 0, event.y );
    assertEquals( 0, event.width );
    assertEquals( 0, event.height );
    assertTrue( event.doit );
  }

  private void testPreserveControlProperties( Scale scale ) {
    // bound
    Rectangle rectangle = new Rectangle( 10, 10, 10, 10 );
    scale.setBounds( rectangle );
    Fixture.preserveWidgets();
    WidgetAdapter adapter = WidgetUtil.getAdapter( scale );
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

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( scale );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( scale );
    assertEquals( "rwt.widgets.Scale", operation.getType() );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( scale );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( scale );
    assertEquals( WidgetUtil.getId( scale.getParent() ), operation.getParent() );
  }

  @Test
  public void testRenderCreateWithHorizontal() throws IOException {
    Scale scale = new Scale( shell, SWT.HORIZONTAL );

    lca.renderInitialization( scale );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( scale );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "HORIZONTAL" ) );
  }

  @Test
  public void testRenderInitialMinimum() throws IOException {
    lca.render( scale );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( scale );
    assertTrue( operation.getPropertyNames().indexOf( "minimum" ) == -1 );
  }

  @Test
  public void testRenderMinimum() throws IOException {
    scale.setMinimum( 10 );
    lca.renderChanges( scale );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( scale, "minimum" ) );
  }

  @Test
  public void testRenderMinimumUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( scale );

    scale.setMinimum( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( scale );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( scale, "minimum" ) );
  }

  @Test
  public void testRenderInitialMaxmum() throws IOException {
    lca.render( scale );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( scale );
    assertTrue( operation.getPropertyNames().indexOf( "maximum" ) == -1 );
  }

  @Test
  public void testRenderMaxmum() throws IOException {
    scale.setMaximum( 10 );
    lca.renderChanges( scale );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( scale, "maximum" ) );
  }

  @Test
  public void testRenderMaxmumUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( scale );

    scale.setMaximum( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( scale );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( scale, "maximum" ) );
  }

  @Test
  public void testRenderInitialSelection() throws IOException {
    lca.render( scale );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( scale );
    assertTrue( operation.getPropertyNames().indexOf( "selection" ) == -1 );
  }

  @Test
  public void testRenderSelection() throws IOException {
    scale.setSelection( 10 );
    lca.renderChanges( scale );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( scale, "selection" ) );
  }

  @Test
  public void testRenderSelectionUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( scale );

    scale.setSelection( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( scale );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( scale, "selection" ) );
  }

  @Test
  public void testRenderInitialIncrement() throws IOException {
    lca.render( scale );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( scale );
    assertTrue( operation.getPropertyNames().indexOf( "increment" ) == -1 );
  }

  @Test
  public void testRenderIncrement() throws IOException {
    scale.setIncrement( 2 );
    lca.renderChanges( scale );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 2 ), message.findSetProperty( scale, "increment" ) );
  }

  @Test
  public void testRenderIncrementUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( scale );

    scale.setIncrement( 2 );
    Fixture.preserveWidgets();
    lca.renderChanges( scale );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( scale, "increment" ) );
  }

  @Test
  public void testRenderInitialPageIncrement() throws IOException {
    lca.render( scale );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( scale );
    assertTrue( operation.getPropertyNames().indexOf( "pageIncrement" ) == -1 );
  }

  @Test
  public void testRenderPageIncrement() throws IOException {
    scale.setPageIncrement( 20 );
    lca.renderChanges( scale );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 20 ), message.findSetProperty( scale, "pageIncrement" ) );
  }

  @Test
  public void testRenderPageIncrementUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( scale );

    scale.setPageIncrement( 20 );
    Fixture.preserveWidgets();
    lca.renderChanges( scale );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( scale, "pageIncrement" ) );
  }

  @Test
  public void testRenderAddSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( scale );
    Fixture.preserveWidgets();

    scale.addListener( SWT.Selection, mock( Listener.class ) );
    lca.renderChanges( scale );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( scale, "Selection" ) );
    assertNull( message.findListenOperation( scale, "DefaultSelection" ) );
  }

  @Test
  public void testRenderRemoveSelectionListener() throws Exception {
    Listener listener = mock( Listener.class );
    scale.addListener( SWT.Selection, listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( scale );
    Fixture.preserveWidgets();

    scale.removeListener( SWT.Selection, listener );
    lca.renderChanges( scale );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( scale, "Selection" ) );
    assertNull( message.findListenOperation( scale, "DefaultSelection" ) );
  }

  @Test
  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( scale );
    Fixture.preserveWidgets();

    scale.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( scale );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( scale, "selection" ) );
  }
}
