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
package org.eclipse.swt.internal.widgets.labelkit;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.graphics.Graphics;
import org.eclipse.rap.rwt.internal.protocol.ProtocolTestUtil;
import org.eclipse.rap.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.Message.DestroyOperation;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("deprecation")
public class LabelLCA_Test extends TestCase {

  private Display display;
  private Shell shell;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    Fixture.fakeNewRequest( display );
  }

  @Override
  protected void tearDown() throws Exception {
    display.dispose();
    Fixture.tearDown();
  }

  public void testControlListeners() throws IOException {
    Label label = new Label( shell, SWT.NONE );
    ControlLCATestUtil.testActivateListener( label );
    ControlLCATestUtil.testMouseListener( label );
    ControlLCATestUtil.testKeyListener( label );
    ControlLCATestUtil.testTraverseListener( label );
    ControlLCATestUtil.testMenuDetectListener( label );
    ControlLCATestUtil.testHelpListener( label );
  }

  public void testStandardPreserveValues() {
    Label label = new Label( shell, SWT.NONE );
    Fixture.markInitialized( display );
    testPreserveValues( display, label );
    //Text
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( label );
    assertEquals( "", adapter.getPreserved( Props.TEXT ) );
    Fixture.clearPreserved();
    label.setText( "xyz" );
    Fixture.preserveWidgets();
    adapter = WidgetUtil.getAdapter( label );
    assertEquals( label.getText(), adapter.getPreserved( Props.TEXT ) );
    Fixture.clearPreserved();
    //Image
    Image image = Graphics.getImage( Fixture.IMAGE1 );
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
    IWidgetAdapter adapter = WidgetUtil.getAdapter( label );
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
    Color background = Graphics.getColor( 122, 33, 203 );
    label.setBackground( background );
    Color foreground = Graphics.getColor( 211, 178, 211 );
    label.setForeground( foreground );
    Font font = Graphics.getFont( "font", 12, SWT.BOLD );
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

  public void testSeparatorPreserveValues() {
    int style = SWT.SEPARATOR | SWT.HORIZONTAL;
    Label label = new Label( shell, style );
    Fixture.markInitialized( display );
    testPreserveValues( display, label );
  }

  public void testRenderInitialText() throws IOException {
    Label label = new Label( shell, SWT.NONE );
    LabelLCA lca = new LabelLCA();

    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( label, "text" ) );
  }

  public void testRenderText() throws IOException {
    Label label = new Label( shell, SWT.NONE );
    LabelLCA lca = new LabelLCA();

    label.setText( "test" );
    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "test", message.findSetProperty( label, "text" ) );
  }

  public void testRenderTextWithQuotationMarks() throws IOException {
    Label label = new Label( shell, SWT.NONE );
    LabelLCA lca = new LabelLCA();

    label.setText( "te\"s't" );
    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "te\"s't", message.findSetProperty( label, "text" ) );
  }

  public void testRenderTextWithNewlines() throws IOException {
    Label label = new Label( shell, SWT.NONE );
    LabelLCA lca = new LabelLCA();

    label.setText( "\ntes\r\nt\n" );
    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "\ntes\r\nt\n", message.findSetProperty( label, "text" ) );
  }

  public void testRenderTextUnchanged() throws IOException {
    Label label = new Label( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( label );
    LabelLCA lca = new LabelLCA();

    label.setText( "foo" );

    Fixture.preserveWidgets();
    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( label, "text" ) );
  }

  public void testRenderDispose() throws IOException {
    Label label = new Label( shell, SWT.NONE );
    label.dispose();
    LabelLCA labelLCA = new LabelLCA();

    labelLCA.renderDispose( label );

    Message message = Fixture.getProtocolMessage();
    DestroyOperation operation = ( DestroyOperation )message.getOperation( 0 );
    assertEquals( WidgetUtil.getId( label ), operation.getTarget() );
  }

  public void testRenderCreate() throws IOException {
    Label label = new Label( shell, SWT.WRAP );
    LabelLCA lca = new LabelLCA();

    lca.renderInitialization( label );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( label );
    assertEquals( "rwt.widgets.Label", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "WRAP" ) );
    assertFalse( operation.getPropertyNames().contains( "markupEnabled" ) );
  }

  public void testRenderCreateWithMarkupEnabled() throws IOException {
    Label label = new Label( shell, SWT.WRAP );
    label.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    LabelLCA lca = new LabelLCA();

    lca.renderInitialization( label );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( label );
    assertEquals( Boolean.TRUE, operation.getProperty( "markupEnabled" ) );
  }

  public void testRenderInitialImage() throws IOException {
    Label label = new Label( shell, SWT.NONE );
    LabelLCA lca = new LabelLCA();

    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( label, "image" ) );
  }

  public void testRenderImage() throws IOException, JSONException {
    Label label = new Label( shell, SWT.NONE );
    LabelLCA lca = new LabelLCA();
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );

    label.setImage( image );
    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    String expected = "[\"" + imageLocation + "\", 100, 50 ]";
    JSONArray actual = ( JSONArray )message.findSetProperty( label, "image" );
    assertTrue( ProtocolTestUtil.jsonEquals( expected, actual ) );
  }

  public void testRenderImageUnchanged() throws IOException {
    Label label = new Label( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( label );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );
    LabelLCA lca = new LabelLCA();

    label.setImage( image );
    Fixture.preserveWidgets();
    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( label, "image" ) );
  }

  public void testRenderImageReset() throws IOException {
    Label label = new Label( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( label );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );
    LabelLCA lca = new LabelLCA();
    label.setImage( image );

    Fixture.preserveWidgets();
    label.setImage( null );
    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( label, "image" ) );
  }

  public void testRenderInitialAlignment() throws IOException {
    Label label = new Label( shell, SWT.NONE );
    LabelLCA lca = new LabelLCA();

    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( label, "alignment" ) );
  }

  public void testRenderAlignment() throws IOException {
    Label label = new Label( shell, SWT.NONE );
    LabelLCA lca = new LabelLCA();

    label.setAlignment( SWT.RIGHT );
    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "right", message.findSetProperty( label, "alignment" ) );
  }

  public void testRenderAlignmentUnchanged() throws IOException {
    Label label = new Label( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( label );
    LabelLCA lca = new LabelLCA();

    label.setAlignment( SWT.RIGHT );
    Fixture.preserveWidgets();
    lca.renderChanges( label );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( label, "alignment" ) );
  }

  public void testRenderCreateSeparator() throws IOException {
    Label label = new Label( shell, SWT.SEPARATOR | SWT.SHADOW_IN | SWT.VERTICAL );
    LabelLCA lca = new LabelLCA();

    lca.renderInitialization( label );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( label );
    assertEquals( "rwt.widgets.Separator", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "SHADOW_IN" ) );
    assertTrue( Arrays.asList( styles ).contains( "VERTICAL" ) );
  }

}
