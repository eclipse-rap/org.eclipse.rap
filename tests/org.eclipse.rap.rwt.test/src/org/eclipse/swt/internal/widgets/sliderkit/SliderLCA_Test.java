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

package org.eclipse.swt.internal.widgets.sliderkit;

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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;

public class SliderLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private SliderLCA lca;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display, SWT.NONE );
    lca = new SliderLCA();
    Fixture.fakeNewRequest( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testSliderPreserveValues() {
    Slider slider = new Slider( shell, SWT.HORIZONTAL );
    Fixture.markInitialized( display );
    // Test preserved minimum, maximum,
    // selection, increment, pageIncrement and thumb
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( slider );
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
    // Test preserved selection listeners
    testPreserveSelectionListener( slider );
  }

  public void testSelectionEvent() {
    Slider slider = new Slider( shell, SWT.HORIZONTAL );
    testSelectionEvent( slider );
  }

  private void testPreserveControlProperties( Slider slider ) {
    // bound
    Rectangle rectangle = new Rectangle( 10, 10, 10, 10 );
    slider.setBounds( rectangle );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( slider );
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

  private void testPreserveSelectionListener( Slider slider ) {
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( slider );
    Boolean hasListeners = ( Boolean )adapter.getPreserved( SliderLCA.PROP_SELECTION_LISTENER );
    assertEquals( Boolean.FALSE, hasListeners );
    Fixture.clearPreserved();
    SelectionListener selectionListener = new SelectionAdapter() {
    };
    slider.addSelectionListener( selectionListener );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( slider );
    hasListeners = ( Boolean )adapter.getPreserved( SliderLCA.PROP_SELECTION_LISTENER );
    assertEquals( Boolean.TRUE, hasListeners );
    Fixture.clearPreserved();
  }

  private void testSelectionEvent( final Slider slider ) {
    final StringBuffer log = new StringBuffer();
    SelectionListener selectionListener = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent event ) {
        assertEquals( slider, event.getSource() );
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
    slider.addSelectionListener( selectionListener );
    String dateTimeId = WidgetUtil.getId( slider );
    Fixture.fakeRequestParam( JSConst.EVENT_WIDGET_SELECTED, dateTimeId );
    Fixture.readDataAndProcessAction( slider );
    assertEquals( "widgetSelected", log.toString() );
  }

  public void testRenderCreate() throws IOException {
    Slider slider = new Slider( shell, SWT.NONE );

    lca.renderInitialization( slider );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( slider );
    assertEquals( "rwt.widgets.Slider", operation.getType() );
  }

  public void testRenderParent() throws IOException {
    Slider slider = new Slider( shell, SWT.NONE );

    lca.renderInitialization( slider );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( slider );
    assertEquals( WidgetUtil.getId( slider.getParent() ), operation.getParent() );
  }

  public void testRenderCreateWithHorizontal() throws IOException {
    Slider slider = new Slider( shell, SWT.HORIZONTAL );

    lca.renderInitialization( slider );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( slider );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "HORIZONTAL" ) );
  }

  public void testRenderInitialMinimum() throws IOException {
    Slider slider = new Slider( shell, SWT.NONE );

    lca.render( slider );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( slider );
    assertTrue( operation.getPropertyNames().indexOf( "minimum" ) == -1 );
  }

  public void testRenderMinimum() throws IOException {
    Slider slider = new Slider( shell, SWT.NONE );

    slider.setMinimum( 10 );
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( slider, "minimum" ) );
  }

  public void testRenderMinimumUnchanged() throws IOException {
    Slider slider = new Slider( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( slider );

    slider.setMinimum( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( slider, "minimum" ) );
  }

  public void testRenderInitialMaxmum() throws IOException {
    Slider slider = new Slider( shell, SWT.NONE );

    lca.render( slider );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( slider );
    assertTrue( operation.getPropertyNames().indexOf( "maximum" ) == -1 );
  }

  public void testRenderMaxmum() throws IOException {
    Slider slider = new Slider( shell, SWT.NONE );

    slider.setMaximum( 10 );
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( slider, "maximum" ) );
  }

  public void testRenderMaxmumUnchanged() throws IOException {
    Slider slider = new Slider( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( slider );

    slider.setMaximum( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( slider, "maximum" ) );
  }

  public void testRenderInitialSelection() throws IOException {
    Slider slider = new Slider( shell, SWT.NONE );

    lca.render( slider );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( slider );
    assertTrue( operation.getPropertyNames().indexOf( "selection" ) == -1 );
  }

  public void testRenderSelection() throws IOException {
    Slider slider = new Slider( shell, SWT.NONE );

    slider.setSelection( 10 );
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 10 ), message.findSetProperty( slider, "selection" ) );
  }

  public void testRenderSelectionUnchanged() throws IOException {
    Slider slider = new Slider( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( slider );

    slider.setSelection( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( slider, "selection" ) );
  }

  public void testRenderInitialIncrement() throws IOException {
    Slider slider = new Slider( shell, SWT.NONE );

    lca.render( slider );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( slider );
    assertTrue( operation.getPropertyNames().indexOf( "increment" ) == -1 );
  }

  public void testRenderIncrement() throws IOException {
    Slider slider = new Slider( shell, SWT.NONE );

    slider.setIncrement( 2 );
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 2 ), message.findSetProperty( slider, "increment" ) );
  }

  public void testRenderIncrementUnchanged() throws IOException {
    Slider slider = new Slider( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( slider );

    slider.setIncrement( 2 );
    Fixture.preserveWidgets();
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( slider, "increment" ) );
  }

  public void testRenderInitialPageIncrement() throws IOException {
    Slider slider = new Slider( shell, SWT.NONE );

    lca.render( slider );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( slider );
    assertTrue( operation.getPropertyNames().indexOf( "pageIncrement" ) == -1 );
  }

  public void testRenderPageIncrement() throws IOException {
    Slider slider = new Slider( shell, SWT.NONE );

    slider.setPageIncrement( 20 );
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 20 ), message.findSetProperty( slider, "pageIncrement" ) );
  }

  public void testRenderPageIncrementUnchanged() throws IOException {
    Slider slider = new Slider( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( slider );

    slider.setPageIncrement( 20 );
    Fixture.preserveWidgets();
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( slider, "pageIncrement" ) );
  }

  public void testRenderInitialThumb() throws IOException {
    Slider slider = new Slider( shell, SWT.NONE );

    lca.render( slider );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( slider );
    assertTrue( operation.getPropertyNames().indexOf( "thumb" ) == -1 );
  }

  public void testRenderThumb() throws IOException {
    Slider slider = new Slider( shell, SWT.NONE );

    slider.setThumb( 20 );
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 20 ), message.findSetProperty( slider, "thumb" ) );
  }

  public void testRenderThumbUnchanged() throws IOException {
    Slider slider = new Slider( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( slider );

    slider.setThumb( 20 );
    Fixture.preserveWidgets();
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( slider, "thumb" ) );
  }

  public void testRenderAddSelectionListener() throws Exception {
    Slider slider = new Slider( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( slider );
    Fixture.preserveWidgets();

    slider.addSelectionListener( new SelectionAdapter() { } );
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.TRUE, message.findListenProperty( slider, "selection" ) );
  }

  public void testRenderRemoveSelectionListener() throws Exception {
    Slider slider = new Slider( shell, SWT.NONE );
    SelectionListener listener = new SelectionAdapter() { };
    slider.addSelectionListener( listener );
    Fixture.markInitialized( display );
    Fixture.markInitialized( slider );
    Fixture.preserveWidgets();

    slider.removeSelectionListener( listener );
    lca.renderChanges( slider );

    Message message = Fixture.getProtocolMessage();
    assertEquals( Boolean.FALSE, message.findListenProperty( slider, "selection" ) );
  }
}
