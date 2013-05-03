/*******************************************************************************
 * Copyright (c) 2009, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.custom.clabelkit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rap.rwt.testfixture.internal.TestUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class CLabelLCA_Test {

  private Display display;
  private Shell shell;
  private CLabel clabel;
  private CLabelLCA lca;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    clabel = new CLabel( shell, SWT.NONE );
    lca = new CLabelLCA();
    Fixture.fakeNewRequest();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  /*
   * 280291: [CLabel] causes NullPointerException when rendered uninitialized
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=280291
   */
  @Test
  public void testWriteText() throws IOException {
    assertNull( clabel.getText() ); // assert precondition: text == null

    lca.renderChanges( clabel );
    // the purpose of this test is to ensure that the LCA works without throwing
    // an exception - thus there is no assert
  }

  @Test
  public void testRenderCreate() throws IOException {
    lca.renderInitialization( clabel );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( clabel );
    assertEquals( "rwt.widgets.Label", operation.getType() );
    assertFalse( operation.getPropertyNames().contains( "markupEnabled" ) );
  }

  @Test
  public void testRenderCreateWithMarkupEnabled() throws IOException {
    clabel.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );

    lca.renderInitialization( clabel );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( clabel );
    assertEquals( JsonValue.TRUE, operation.getProperty( "markupEnabled" ) );
  }

  @Test
  public void testRenderCreateWithShadowIn() throws IOException {
    clabel = new CLabel( shell, SWT.SHADOW_IN );

    lca.renderInitialization( clabel );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( clabel );
    assertEquals( "rwt.widgets.Label", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "SHADOW_IN" ) );
  }

  @Test
  public void testRenderCreateWithAlignment() throws Exception {
    clabel = new CLabel( shell, SWT.CENTER );

    lca.render( clabel );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "center", message.findCreateProperty( clabel, "alignment" ).asString() );
  }

  @Test
  public void testRenderParent() throws IOException {
    lca.renderInitialization( clabel );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( clabel );
    assertEquals( WidgetUtil.getId( clabel.getParent() ), operation.getParent() );
  }

  @Test
  public void testRenderInitialText() throws IOException {
    lca.render( clabel );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( clabel );
    assertTrue( operation.getPropertyNames().indexOf( "text" ) == -1 );
  }

  @Test
  public void testRenderText() throws IOException {
    clabel.setText( "foo" );
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( clabel, "text" ).asString() );
  }

  @Test
  public void testRenderTextUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( clabel );

    clabel.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( clabel, "text" ) );
  }

  @Test
  public void testRenderInitialImage() throws IOException {
    lca.render( clabel );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( clabel );
    assertTrue( operation.getPropertyNames().indexOf( "image" ) == -1 );
  }

  @Test
  public void testRenderImage() throws IOException {
    Image image = TestUtil.createImage( display, Fixture.IMAGE_100x50 );

    clabel.setImage( image );
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    JsonArray expected = new JsonArray().add( imageLocation ).add( 100 ).add( 50 );
    assertEquals( expected, message.findSetProperty( clabel, "image" ) );
  }

  @Test
  public void testRenderImageUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( clabel );
    Image image = TestUtil.createImage( display, Fixture.IMAGE_100x50 );

    clabel.setImage( image );
    Fixture.preserveWidgets();
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( clabel, "image" ) );
  }

  @Test
  public void testRenderImageReset() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( clabel );
    Image image = TestUtil.createImage( display, Fixture.IMAGE_100x50 );
    clabel.setImage( image );

    Fixture.preserveWidgets();
    clabel.setImage( null );
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JsonObject.NULL, message.findSetProperty( clabel, "image" ) );
  }

  @Test
  public void testRenderInitialAlignment() throws IOException {
    lca.render( clabel );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( clabel );
    assertTrue( operation.getPropertyNames().indexOf( "alignment" ) == -1 );
  }

  @Test
  public void testRenderAlignment() throws IOException {
    clabel.setAlignment( SWT.RIGHT );
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "right", message.findSetProperty( clabel, "alignment" ).asString() );
  }

  @Test
  public void testRenderAlignmentUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( clabel );

    clabel.setAlignment( SWT.RIGHT );
    Fixture.preserveWidgets();
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( clabel, "alignment" ) );
  }

  @Test
  public void testRenderInitialLeftMargin() throws IOException {
    lca.render( clabel );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( clabel );
    assertTrue( operation.getPropertyNames().indexOf( "leftMargin" ) == -1 );
  }

  @Test
  public void testRenderLeftMargin() throws IOException {
    clabel.setLeftMargin( 5 );
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 5, message.findSetProperty( clabel, "leftMargin" ).asInt() );
  }

  @Test
  public void testRenderLeftMarginUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( clabel );

    clabel.setLeftMargin( 5 );
    Fixture.preserveWidgets();
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( clabel, "leftMargin" ) );
  }


  @Test
  public void testRenderInitialTopMargin() throws IOException {
    lca.render( clabel );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( clabel );
    assertTrue( operation.getPropertyNames().indexOf( "topMargin" ) == -1 );
  }

  @Test
  public void testRenderTopMargin() throws IOException {
    clabel.setTopMargin( 5 );
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 5, message.findSetProperty( clabel, "topMargin" ).asInt() );
  }

  @Test
  public void testRenderTopMarginUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( clabel );

    clabel.setTopMargin( 5 );
    Fixture.preserveWidgets();
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( clabel, "topMargin" ) );
  }

  @Test
  public void testRenderInitialRightMargin() throws IOException {
    lca.render( clabel );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( clabel );
    assertTrue( operation.getPropertyNames().indexOf( "rightMargin" ) == -1 );
  }

  @Test
  public void testRenderRightMargin() throws IOException {
    clabel.setRightMargin( 5 );
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 5, message.findSetProperty( clabel, "rightMargin" ).asInt() );
  }

  @Test
  public void testRenderRightMarginUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( clabel );

    clabel.setRightMargin( 5 );
    Fixture.preserveWidgets();
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( clabel, "rightMargin" ) );
  }

  @Test
  public void testRenderInitialBottomMargin() throws IOException {
    lca.render( clabel );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( clabel );
    assertTrue( operation.getPropertyNames().indexOf( "bottomMargin" ) == -1 );
  }

  @Test
  public void testRenderBottomMargin() throws IOException {
    clabel.setBottomMargin( 5 );
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertEquals( 5, message.findSetProperty( clabel, "bottomMargin" ).asInt() );
  }

  @Test
  public void testRenderBottomMarginUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( clabel );

    clabel.setBottomMargin( 5 );
    Fixture.preserveWidgets();
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( clabel, "bottomMargin" ) );
  }

  @Test
  public void testRenderInitialBackgroundGradient() throws IOException {
    lca.render( clabel );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( clabel );
    assertTrue( operation.getPropertyNames().indexOf( "backgroundGradient" ) == -1 );
  }

  @Test
  public void testRenderBackgroundGradient() throws IOException {
    Color[] gradientColors = new Color[] {
      display.getSystemColor( SWT.COLOR_RED ),
      display.getSystemColor( SWT.COLOR_GREEN )
    };
    int[] percents = new int[] { 50 };
    clabel.setBackground( gradientColors , percents );
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    JsonArray expected
      = JsonArray.readFrom( "[[[255, 0, 0, 255], [0, 255, 0, 255]], [0, 50], false]" );
    assertEquals( expected, message.findSetProperty( clabel, "backgroundGradient" ) );
  }

  @Test
  public void testRenderBackgroundGradientUnchanged() throws IOException {
    Fixture.markInitialized( display );
    Fixture.markInitialized( clabel );

    Color[] colors = new Color[] {
      display.getSystemColor( SWT.COLOR_RED ),
      display.getSystemColor( SWT.COLOR_GREEN )
    };
    int[] percents = new int[] { 50 };
    clabel.setBackground( colors , percents );
    Fixture.preserveWidgets();
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( clabel, "backgroundGradient" ) );
  }

}
