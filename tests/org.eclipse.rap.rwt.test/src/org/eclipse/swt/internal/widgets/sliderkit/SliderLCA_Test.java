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
package org.eclipse.swt.internal.widgets.sliderkit;

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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class SliderLCA_Test {

  private Display display;
  private Shell shell;
  private SliderLCA lca;
  private Slider slider;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    slider = new Slider( shell, SWT.NONE );
    lca = new SliderLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testSliderPreserveValues() {
    Fixture.markInitialized( display );
    // Test preserved minimum, maximum,
    // selection, increment, pageIncrement and thumb
    Fixture.preserveWidgets();
    WidgetAdapter adapter = WidgetUtil.getAdapter( slider );
    Integer minimum = ( Integer )adapter.getPreserved( SliderLCA.PROP_MINIMUM );
    assertEquals( 0, minimum.intValue() );
    Integer maximum = ( Integer )adapter.getPreserved( SliderLCA.PROP_MAXIMUM );
    assertEquals( 100, maximum.intValue() );
    Integer selection = ( Integer )adapter.getPreserved( SliderLCA.PROP_SELECTION );
    assertEquals( 0, selection.intValue() );
    Integer increment = ( Integer )adapter.getPreserved( SliderLCA.PROP_INCREMENT );
    assertEquals( 1, increment.intValue() );
    Integer pageIncrement = ( Integer )adapter.getPreserved( SliderLCA.PROP_PAGE_INCREMENT );
    assertEquals( 10, pageIncrement.intValue() );
    Integer thumb = ( Integer )adapter.getPreserved( SliderLCA.PROP_THUMB );
    assertEquals( 10, thumb.intValue() );
    Fixture.clearPreserved();
    // Test preserved control properties
    testPreserveControlProperties( slider );
  }

  @Test
  public void testSelectionEvent() {
    SelectionListener listener = mock( SelectionListener.class );
    slider.addSelectionListener( listener );

    Fixture.fakeNotifyOperation( getId( slider ), ClientMessageConst.EVENT_SELECTION, null );
    Fixture.readDataAndProcessAction( slider );

    ArgumentCaptor<SelectionEvent> captor = ArgumentCaptor.forClass( SelectionEvent.class );
    verify( listener, times( 1 ) ).widgetSelected( captor.capture() );
    SelectionEvent event = captor.getValue();
    assertEquals( slider, event.getSource() );
    assertEquals( null, event.item );
    assertEquals( 0, event.x );
    assertEquals( 0, event.y );
    assertEquals( 0, event.width );
    assertEquals( 0, event.height );
    assertTrue( event.doit );
  }

  private void testPreserveControlProperties( Slider slider ) {
    // bound
    Rectangle rectangle = new Rectangle( 10, 10, 10, 10 );
    slider.setBounds( rectangle );
    Fixture.preserveWidgets();
    WidgetAdapter adapter = WidgetUtil.getAdapter( slider );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    // enabled
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( slider );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    slider.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( slider );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    // visible
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( slider );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    slider.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( slider );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    // menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( slider );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( slider );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    slider.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( slider );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    //foreground background font
    Color background = Graphics.getColor( 122, 33, 203 );
    slider.setBackground( background );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( slider );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    Fixture.clearPreserved();
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( slider );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( slider );
    assertEquals( "rwt.widgets.Slider", operation.getType() );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( slider );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( slider );
    assertEquals( WidgetUtil.getId( slider.getParent() ), operation.getParent() );
  }

  @Test
  public void testRenderCreateWithHorizontal() throws IOException {
    Slider slider = new Slider( shell, SWT.HORIZONTAL );

    lca.renderInitialization( slider );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( slider );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "HORIZONTAL" ) );
  }

  @Test
  public void testRenderInitialMinimum() throws IOException {
    lca.render( slider );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( slider );
    assertTrue( operation.getPropertyNames().indexOf( "minimum" ) == -1 );
  }

  @Test
  public void testRenderMinimum() throws IOException {
    slider.setMinimum( 10 );
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( slider, "minimum" ) );
  }

  @Test
  public void testRenderMinimumUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( slider );

    slider.setMinimum( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( slider, "minimum" ) );
  }

  @Test
  public void testRenderInitialMaxmum() throws IOException {
    lca.render( slider );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( slider );
    assertTrue( operation.getPropertyNames().indexOf( "maximum" ) == -1 );
  }

  @Test
  public void testRenderMaxmum() throws IOException {
    slider.setMaximum( 10 );
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( slider, "maximum" ) );
  }

  @Test
  public void testRenderMaxmumUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( slider );

    slider.setMaximum( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( slider, "maximum" ) );
  }

  @Test
  public void testRenderInitialSelection() throws IOException {
    lca.render( slider );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( slider );
    assertTrue( operation.getPropertyNames().indexOf( "selection" ) == -1 );
  }

  @Test
  public void testRenderSelection() throws IOException {
    slider.setSelection( 10 );
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( slider, "selection" ) );
  }

  @Test
  public void testRenderSelectionUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( slider );

    slider.setSelection( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( slider, "selection" ) );
  }

  @Test
  public void testRenderInitialIncrement() throws IOException {
    lca.render( slider );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( slider );
    assertTrue( operation.getPropertyNames().indexOf( "increment" ) == -1 );
  }

  @Test
  public void testRenderIncrement() throws IOException {
    slider.setIncrement( 2 );
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 2 ), message.findSetProperty( slider, "increment" ) );
  }

  @Test
  public void testRenderIncrementUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( slider );

    slider.setIncrement( 2 );
    Fixture.preserveWidgets();
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( slider, "increment" ) );
  }

  @Test
  public void testRenderInitialPageIncrement() throws IOException {
    lca.render( slider );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( slider );
    assertTrue( operation.getPropertyNames().indexOf( "pageIncrement" ) == -1 );
  }

  @Test
  public void testRenderPageIncrement() throws IOException {
    slider.setPageIncrement( 20 );
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 20 ), message.findSetProperty( slider, "pageIncrement" ) );
  }

  @Test
  public void testRenderPageIncrementUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( slider );

    slider.setPageIncrement( 20 );
    Fixture.preserveWidgets();
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( slider, "pageIncrement" ) );
  }

  @Test
  public void testRenderInitialThumb() throws IOException {
    lca.render( slider );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( slider );
    assertTrue( operation.getPropertyNames().indexOf( "thumb" ) == -1 );
  }

  @Test
  public void testRenderThumb() throws IOException {
    slider.setThumb( 20 );
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 20 ), message.findSetProperty( slider, "thumb" ) );
  }

  @Test
  public void testRenderThumbUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( slider );

    slider.setThumb( 20 );
    Fixture.preserveWidgets();
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( slider, "thumb" ) );
  }

  @Test
  public void testRenderAddSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( slider );
    Fixture.preserveWidgets();

    slider.addListener( SWT.Selection, mock( Listener.class ) );
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( slider, "Selection" ) );
  }

  @Test
  public void testRenderRemoveSelectionListener() throws Exception {
    Listener listener = mock( Listener.class );
    slider.addListener( SWT.Selection, listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( slider );
    Fixture.preserveWidgets();

    slider.removeListener( SWT.Selection, listener );
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( slider, "Selection" ) );
  }

  @Test
  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( slider );
    Fixture.preserveWidgets();

    slider.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( slider, "selection" ) );
  }
}
