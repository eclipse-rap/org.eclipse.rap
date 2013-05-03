/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.labelkit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.lifecycle.WidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.DestroyOperation;
import org.eclipse.rap.rwt.testfixture.internal.TestUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.internal.widgets.controlkit.ControlLCATestUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LabelLCA_Test {

  private Display display;
  private Shell shell;
  private Label label;
  private LabelLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    label = new Label( shell, SWT.NONE );
    lca = new LabelLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    display.dispose();
    Fixture.tearDown();
  }

  @Test
  public void testControlListeners() throws IOException {
    ControlLCATestUtil.testActivateListener( label );
    ControlLCATestUtil.testMouseListener( label );
    ControlLCATestUtil.testKeyListener( label );
    ControlLCATestUtil.testTraverseListener( label );
    ControlLCATestUtil.testMenuDetectListener( label );
    ControlLCATestUtil.testHelpListener( label );
  }

  @Test
  public void testStandardPreserveValues() throws IOException {
    Fixture.markInitialized( display );
    testPreserveValues( display, label );
    //Text
    Fixture.preserveWidgets();
    WidgetAdapter adapter = WidgetUtil.getAdapter( label );
    assertEquals( "", adapter.getPreserved( Props.TEXT ) );
    Fixture.clearPreserved();
    label.setText( "xyz" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( label.getText(), adapter.getPreserved( Props.TEXT ) );
    Fixture.clearPreserved();
    //Image
    Image image = TestUtil.createImage( display, Fixture.IMAGE1 );
    label.setImage( image );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertSame( image, adapter.getPreserved( Props.IMAGE ) );
    Fixture.clearPreserved();
    //aligment
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    String alignment = ( String )adapter.getPreserved( "alignment" );
    assertEquals( "left", alignment );
    Fixture.clearPreserved();
    label.setAlignment( SWT.RIGHT );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    alignment = ( String )adapter.getPreserved( "alignment" );
    assertEquals( "right", alignment );
    Fixture.clearPreserved();
    label.setAlignment( SWT.CENTER );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    alignment = ( String )adapter.getPreserved( "alignment" );
    assertEquals( "center", alignment );
    Fixture.clearPreserved();
    label.setAlignment( SWT.LEFT );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    alignment = ( String )adapter.getPreserved( "alignment" );
    assertEquals( "left", alignment );
  }

  private void testPreserveValues( Display display, Label label ) {
    // bound
    Rectangle rectangle = new Rectangle( 10, 10, 10, 10 );
    label.setBounds( rectangle );
    Fixture.preserveWidgets();
    WidgetAdapter adapter = WidgetUtil.getAdapter( label );
    assertEquals( rectangle, adapter.getPreserved( Props.BOUNDS ) );
    Fixture.clearPreserved();
    //menu
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( null, adapter.getPreserved( Props.MENU ) );
    Fixture.clearPreserved();
    Menu menu = new Menu( label );
    MenuItem item = new MenuItem( menu, SWT.NONE );
    item.setText( "1 Item" );
    label.setMenu( menu );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( menu, adapter.getPreserved( Props.MENU ) );
    //visible
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    label.setVisible( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.VISIBLE ) );
    Fixture.clearPreserved();
    //enabled
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( Boolean.TRUE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    label.setEnabled( false );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( Boolean.FALSE, adapter.getPreserved( Props.ENABLED ) );
    Fixture.clearPreserved();
    label.setEnabled( true );
    //foreground background font
    Color background = new Color( display, 122, 33, 203 );
    label.setBackground( background );
    Color foreground = new Color( display, 211, 178, 211 );
    label.setForeground( foreground );
    Font font = new Font( display, "font", 12, SWT.BOLD );
    label.setFont( font );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( background, adapter.getPreserved( Props.BACKGROUND ) );
    assertEquals( foreground, adapter.getPreserved( Props.FOREGROUND ) );
    assertEquals( font, adapter.getPreserved( Props.FONT ) );
    Fixture.clearPreserved();
    //tooltiptext
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( null, label.getToolTipText() );
    Fixture.clearPreserved();
    label.setToolTipText( "some text" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( "some text", label.getToolTipText() );
  }

  @Test
  public void testSeparatorPreserveValues() {
    label = new Label( shell, SWT.SEPARATOR | SWT.HORIZONTAL );
    Fixture.markInitialized( display );
    testPreserveValues( display, label );
  }

  @Test
  public void testRenderInitialText() throws IOException {
    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( label, "text" ) );
  }

  @Test
  public void testRenderText() throws IOException {
    label.setText( "test" );
    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "test", message.findSetProperty( label, "text" ).asString() );
  }

  @Test
  public void testRenderTextWithQuotationMarks() throws IOException {
    label.setText( "te\"s't" );
    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "te\"s't", message.findSetProperty( label, "text" ).asString() );
  }

  @Test
  public void testRenderTextWithMnemonic() throws IOException {
    label.setText( "te&st" );
    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "test", message.findSetProperty( label, "text" ).asString() );
  }

  @Test
  public void testRenderTextWithNewlines() throws IOException {
    label.setText( "\ntes\r\nt\n" );
    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "\ntes\r\nt\n", message.findSetProperty( label, "text" ).asString() );
  }

  @Test
  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( label );

    label.setText( "foo" );

    Fixture.preserveWidgets();
    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( label, "text" ) );
  }

  @Test
  public void testRenderDispose() throws IOException {
    label.dispose();
    LabelLCA labelLCA = new LabelLCA();

    labelLCA.renderDispose( label );

    Message message = Fixture.getProtocolMessage();
    DestroyOperation operation = ( DestroyOperation )message.getOperation( 0 );
    assertEquals( WidgetUtil.getId( label ), operation.getTarget() );
  }

  @Test
  public void testRenderCreate() throws IOException {
    label = new Label( shell, SWT.WRAP );

    lca.renderInitialization( label );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( label );
    assertEquals( "rwt.widgets.Label", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "WRAP" ) );
    assertFalse( operation.getPropertyNames().contains( "markupEnabled" ) );
  }

  @Test
  public void testRenderCreateWithMarkupEnabled() throws IOException {
    label = new Label( shell, SWT.WRAP );
    label.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );

    lca.renderInitialization( label );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( label );
    assertEquals( JsonValue.TRUE, operation.getProperty( "markupEnabled" ) );
  }

  @Test
  public void testRenderInitialImage() throws IOException {
    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( label, "image" ) );
  }

  @Test
  public void testRenderImage() throws IOException {
    Image image = TestUtil.createImage( display, Fixture.IMAGE_100x50 );

    label.setImage( image );
    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    JsonArray expected = new JsonArray().add( imageLocation ).add( 100 ).add( 50 );
    assertEquals( expected, message.findSetProperty( label, "image" ) );
  }

  @Test
  public void testRenderImageUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( label );
    Image image = TestUtil.createImage( display, Fixture.IMAGE_100x50 );

    label.setImage( image );
    Fixture.preserveWidgets();
    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( label, "image" ) );
  }

  @Test
  public void testRenderImageReset() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( label );
    Image image = TestUtil.createImage( display, Fixture.IMAGE_100x50 );
    label.setImage( image );

    Fixture.preserveWidgets();
    label.setImage( null );
    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( label, "image" ) );
  }

  @Test
  public void testRenderInitialAlignment() throws IOException {
    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( label, "alignment" ) );
  }

  @Test
  public void testRenderAlignment() throws IOException {
    label.setAlignment( SWT.RIGHT );
    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "right", message.findSetProperty( label, "alignment" ).asString() );
  }

  @Test
  public void testRenderAlignmentUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( label );

    label.setAlignment( SWT.RIGHT );
    Fixture.preserveWidgets();
    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( label, "alignment" ) );
  }

  @Test
  public void testRenderCreateSeparator() throws IOException {
    label = new Label( shell, SWT.SEPARATOR | SWT.SHADOW_IN | SWT.VERTICAL );

    lca.renderInitialization( label );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( label );
    assertEquals( "rwt.widgets.Separator", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "SHADOW_IN" ) );
    assertTrue( Arrays.asList( styles ).contains( "VERTICAL" ) );
  }

  @Test
  public void testRenderInitialMnemonicIndex() throws IOException {
    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( label, "mnemonicIndex" ) );
  }

  @Test
  public void testRenderMnemonicIndex() throws IOException {
    label.setText( "te&st" );
    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 2, message.findSetProperty( label, "mnemonicIndex" ).asInt() );
  }

  @Test
  public void testRenderMnemonicIndexUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( label );

    label.setText( "te&st" );
    Fixture.preserveWidgets();
    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( label, "mnemonicIndex" ) );
  }

}
