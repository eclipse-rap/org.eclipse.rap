/*******************************************************************************
 * Copyright (c) 2008, 2015 Innoopract Informationssysteme GmbH and others.
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

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil.getId;
import static org.eclipse.rap.rwt.internal.protocol.RemoteObjectFactory.getRemoteObject;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getParent;
import static org.eclipse.rap.rwt.testfixture.internal.TestMessage.getStyles;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.lifecycle.RemoteAdapter;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.internal.protocol.Operation.CreateOperation;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.remote.OperationHandler;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testCommonControlProperties() throws IOException {
    ControlLCATestUtil.testCommonControlProperties( slider );
  }

  @Test
  public void testSliderPreserveValues() {
    Fixture.markInitialized( display );
    // Test preserved minimum, maximum,
    // selection, increment, pageIncrement and thumb
    Fixture.preserveWidgets();
    RemoteAdapter adapter = WidgetUtil.getAdapter( slider );
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
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( slider );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( slider );
    assertEquals( "rwt.widgets.Slider", operation.getType() );
  }

  @Test
  public void testRenderInitialization_setsOperationHandler() throws IOException {
    String id = getId( slider );
    lca.renderInitialization( slider );

    OperationHandler handler = RemoteObjectRegistry.getInstance().get( id ).getHandler();
    assertTrue( handler instanceof SliderOperationHandler );
  }

  @Test
  public void testReadData_usesOperationHandler() {
    SliderOperationHandler handler = spy( new SliderOperationHandler( slider ) );
    getRemoteObject( getId( slider ) ).setHandler( handler );

    Fixture.fakeNotifyOperation( getId( slider ), "Help", new JsonObject() );
    lca.readData( slider );

    verify( handler ).handleNotifyHelp( slider, new JsonObject() );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( slider );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( slider );
    assertEquals( getId( slider.getParent() ), getParent( operation ) );
  }

  @Test
  public void testRenderCreateWithHorizontal() throws IOException {
    Slider slider = new Slider( shell, SWT.HORIZONTAL );

    lca.renderInitialization( slider );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( slider );
    assertTrue( getStyles( operation ).contains( "HORIZONTAL" ) );
  }

  @Test
  public void testRenderInitialMinimum() throws IOException {
    lca.render( slider );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( slider );
    assertFalse( operation.getProperties().names().contains( "minimum" ) );
  }

  @Test
  public void testRenderMinimum() throws IOException {
    slider.setMinimum( 10 );
    lca.renderChanges( slider );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 10, message.findSetProperty( slider, "minimum" ).asInt() );
  }

  @Test
  public void testRenderMinimumUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( slider );

    slider.setMinimum( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( slider );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( slider, "minimum" ) );
  }

  @Test
  public void testRenderInitialMaxmum() throws IOException {
    lca.render( slider );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( slider );
    assertFalse( operation.getProperties().names().contains( "maximum" ) );
  }

  @Test
  public void testRenderMaxmum() throws IOException {
    slider.setMaximum( 10 );
    lca.renderChanges( slider );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 10, message.findSetProperty( slider, "maximum" ).asInt() );
  }

  @Test
  public void testRenderMaxmumUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( slider );

    slider.setMaximum( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( slider );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( slider, "maximum" ) );
  }

  @Test
  public void testRenderInitialSelection() throws IOException {
    lca.render( slider );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( slider );
    assertFalse( operation.getProperties().names().contains( "selection" ) );
  }

  @Test
  public void testRenderSelection() throws IOException {
    slider.setSelection( 10 );
    lca.renderChanges( slider );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 10, message.findSetProperty( slider, "selection" ).asInt() );
  }

  @Test
  public void testRenderSelectionUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( slider );

    slider.setSelection( 10 );
    Fixture.preserveWidgets();
    lca.renderChanges( slider );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( slider, "selection" ) );
  }

  @Test
  public void testRenderInitialIncrement() throws IOException {
    lca.render( slider );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( slider );
    assertFalse( operation.getProperties().names().contains( "increment" ) );
  }

  @Test
  public void testRenderIncrement() throws IOException {
    slider.setIncrement( 2 );
    lca.renderChanges( slider );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 2, message.findSetProperty( slider, "increment" ).asInt() );
  }

  @Test
  public void testRenderIncrementUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( slider );

    slider.setIncrement( 2 );
    Fixture.preserveWidgets();
    lca.renderChanges( slider );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( slider, "increment" ) );
  }

  @Test
  public void testRenderInitialPageIncrement() throws IOException {
    lca.render( slider );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( slider );
    assertFalse( operation.getProperties().names().contains( "pageIncrement" ) );
  }

  @Test
  public void testRenderPageIncrement() throws IOException {
    slider.setPageIncrement( 20 );
    lca.renderChanges( slider );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 20, message.findSetProperty( slider, "pageIncrement" ).asInt() );
  }

  @Test
  public void testRenderPageIncrementUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( slider );

    slider.setPageIncrement( 20 );
    Fixture.preserveWidgets();
    lca.renderChanges( slider );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( slider, "pageIncrement" ) );
  }

  @Test
  public void testRenderInitialThumb() throws IOException {
    lca.render( slider );

    TestMessage message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( slider );
    assertFalse( operation.getProperties().names().contains( "thumb" ) );
  }

  @Test
  public void testRenderThumb() throws IOException {
    slider.setThumb( 20 );
    lca.renderChanges( slider );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( 20, message.findSetProperty( slider, "thumb" ).asInt() );
  }

  @Test
  public void testRenderThumbUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( slider );

    slider.setThumb( 20 );
    Fixture.preserveWidgets();
    lca.renderChanges( slider );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( slider, "thumb" ) );
  }

  @Test
  public void testRenderAddSelectionListener() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( slider );
    Fixture.preserveWidgets();

    slider.addListener( SWT.Selection, mock( Listener.class ) );
    lca.renderChanges( slider );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.TRUE, message.findListenProperty( slider, "Selection" ) );
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

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( JsonValue.FALSE, message.findListenProperty( slider, "Selection" ) );
  }

  @Test
  public void testRenderSelectionListenerUnchanged() throws Exception {
    Fixture.markInitialized( display );
    Fixture.markInitialized( slider );
    Fixture.preserveWidgets();

    slider.addSelectionListener( new SelectionAdapter() { } );
    Fixture.preserveWidgets();
    lca.renderChanges( slider );

    TestMessage message = Fixture.getProtocolMessage();
    assertNull( message.findListenOperation( slider, "selection" ) );
  }

}
