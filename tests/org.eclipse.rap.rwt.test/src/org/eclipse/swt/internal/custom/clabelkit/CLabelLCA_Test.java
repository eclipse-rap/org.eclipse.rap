/*******************************************************************************
 * Copyright (c) 2009, 2011 Innoopract Informationssysteme GmbH and others.
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

import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.Message;
import org.eclipse.rap.rwt.testfixture.Message.CreateOperation;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.protocol.*;
import org.eclipse.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.graphics.ImageFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.json.*;


public class CLabelLCA_Test extends TestCase {

  private Display display;
  private Shell shell;
  private CLabelLCA lca;

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
    lca = new CLabelLCA();
    Fixture.fakeNewRequest( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testPreserveValues() {
    CLabel label = new CLabel( shell, SWT.NONE );
    Fixture.markInitialized( display );
    label.setText( "text" );
    label.setAlignment( SWT.LEFT );
    label.setImage( Graphics.getImage( Fixture.IMAGE_100x50 ) );
    label.setMargins( 1, 2, 3, 4 );
    Fixture.preserveWidgets();
    IWidgetAdapter adapter = WidgetUtil.getAdapter( label );
    String text = ( String )adapter.getPreserved( CLabelLCA.PROP_TEXT );
    assertEquals( "text", text );
    Image image = ( Image )adapter.getPreserved( CLabelLCA.PROP_IMAGE );
    assertEquals( Graphics.getImage( Fixture.IMAGE_100x50 ), image );
    String alignment = ( String )adapter.getPreserved( CLabelLCA.PROP_ALIGNMENT );
    assertEquals( "left", alignment );
    Integer leftMargin = ( Integer )adapter.getPreserved( CLabelLCA.PROP_LEFT_MARGIN );
    assertEquals( 1, leftMargin.intValue() );
    Integer topMargin = ( Integer )adapter.getPreserved( CLabelLCA.PROP_TOP_MARGIN );
    assertEquals( 2, topMargin.intValue() );
    Integer rightMargin = ( Integer )adapter.getPreserved( CLabelLCA.PROP_RIGHT_MARGIN );
    assertEquals( 3, rightMargin.intValue() );
    Integer bottomMargin = ( Integer )adapter.getPreserved( CLabelLCA.PROP_BOTTOM_MARGIN );
    assertEquals( 4, bottomMargin.intValue() );
  }

  /*
   * 280291: [CLabel] causes NullPointerException when rendered uninitialized
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=280291
   */
  public void testWriteText() throws IOException {
    CLabel label = new CLabel( shell, SWT.NONE );
    assertNull( label.getText() ); // assert precondition: text == null

    lca.renderChanges( label );
    // the purpose of this test is to ensure that the LCA works without throwing
    // an exception - thus there is no assert
  }

  public void testRenderCreate() throws IOException {
    CLabel clabel = new CLabel( shell, SWT.NONE );

    lca.renderInitialization( clabel );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( clabel );
    assertEquals( "rwt.widgets.CLabel", operation.getType() );
  }

  public void testRenderCreateWithShadowIn() throws IOException {
    CLabel clabel = new CLabel( shell, SWT.SHADOW_IN );

    lca.renderInitialization( clabel );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( clabel );
    assertEquals( "rwt.widgets.CLabel", operation.getType() );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "SHADOW_IN" ) );
  }

  public void testRenderCreateWithAlignment() throws Exception {
    CLabel clabel = new CLabel( shell, SWT.CENTER );

    lca.renderInitialization( clabel );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( clabel );
    Object[] styles = operation.getStyles();
    assertTrue( Arrays.asList( styles ).contains( "CENTER" ) );
  }

  public void testRenderParent() throws IOException {
    CLabel clabel = new CLabel( shell, SWT.NONE );

    lca.renderInitialization( clabel );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( clabel );
    assertEquals( WidgetUtil.getId( clabel.getParent() ), operation.getParent() );
  }

  public void testRenderInitialText() throws IOException {
    CLabel clabel = new CLabel( shell, SWT.NONE );

    lca.render( clabel );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( clabel );
    assertTrue( operation.getPropertyNames().indexOf( "text" ) == -1 );
  }

  public void testRenderText() throws IOException {
    CLabel clabel = new CLabel( shell, SWT.NONE );

    clabel.setText( "foo" );
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "foo", message.findSetProperty( clabel, "text" ) );
  }

  public void testRenderTextUnchanged() throws IOException {
    CLabel clabel = new CLabel( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( clabel );

    clabel.setText( "foo" );
    Fixture.preserveWidgets();
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( clabel, "text" ) );
  }

  public void testRenderInitialImage() throws IOException {
    CLabel clabel = new CLabel( shell, SWT.NONE );

    lca.render( clabel );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( clabel );
    assertTrue( operation.getPropertyNames().indexOf( "image" ) == -1 );
  }

  public void testRenderImage() throws IOException, JSONException {
    CLabel clabel = new CLabel( shell, SWT.NONE );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );

    clabel.setImage( image );
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    String imageLocation = ImageFactory.getImagePath( image );
    String expected = "[\"" + imageLocation + "\", 100, 50 ]";
    JSONArray actual = ( JSONArray )message.findSetProperty( clabel, "image" );
    assertTrue( ProtocolTestUtil.jsonEquals( expected, actual ) );
  }

  public void testRenderImageUnchanged() throws IOException {
    CLabel clabel = new CLabel( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( clabel );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );

    clabel.setImage( image );
    Fixture.preserveWidgets();
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( clabel, "image" ) );
  }

  public void testRenderImageReset() throws IOException {
    CLabel clabel = new CLabel( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( clabel );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );
    clabel.setImage( image );

    Fixture.preserveWidgets();
    clabel.setImage( null );
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertEquals( JSONObject.NULL, message.findSetProperty( clabel, "image" ) );
  }

  public void testRenderInitialAlignment() throws IOException {
    CLabel clabel = new CLabel( shell, SWT.NONE );

    lca.render( clabel );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( clabel );
    assertTrue( operation.getPropertyNames().indexOf( "alignment" ) == -1 );
  }

  public void testRenderAlignment() throws IOException {
    CLabel clabel = new CLabel( shell, SWT.NONE );

    clabel.setAlignment( SWT.RIGHT );
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertEquals( "right", message.findSetProperty( clabel, "alignment" ) );
  }

  public void testRenderAlignmentUnchanged() throws IOException {
    CLabel clabel = new CLabel( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( clabel );

    clabel.setAlignment( SWT.RIGHT );
    Fixture.preserveWidgets();
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( clabel, "alignment" ) );
  }

  public void testRenderInitialLeftMargin() throws IOException {
    CLabel clabel = new CLabel( shell, SWT.NONE );

    lca.render( clabel );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( clabel );
    assertTrue( operation.getPropertyNames().indexOf( "leftMargin" ) == -1 );
  }

  public void testRenderLeftMargin() throws IOException {
    CLabel clabel = new CLabel( shell, SWT.NONE );

    clabel.setLeftMargin( 5 );
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 5 ), message.findSetProperty( clabel, "leftMargin" ) );
  }

  public void testRenderLeftMarginUnchanged() throws IOException {
    CLabel clabel = new CLabel( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( clabel );

    clabel.setLeftMargin( 5 );
    Fixture.preserveWidgets();
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( clabel, "leftMargin" ) );
  }


  public void testRenderInitialTopMargin() throws IOException {
    CLabel clabel = new CLabel( shell, SWT.NONE );

    lca.render( clabel );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( clabel );
    assertTrue( operation.getPropertyNames().indexOf( "topMargin" ) == -1 );
  }

  public void testRenderTopMargin() throws IOException {
    CLabel clabel = new CLabel( shell, SWT.NONE );

    clabel.setTopMargin( 5 );
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 5 ), message.findSetProperty( clabel, "topMargin" ) );
  }

  public void testRenderTopMarginUnchanged() throws IOException {
    CLabel clabel = new CLabel( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( clabel );

    clabel.setTopMargin( 5 );
    Fixture.preserveWidgets();
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( clabel, "topMargin" ) );
  }

  public void testRenderInitialRightMargin() throws IOException {
    CLabel clabel = new CLabel( shell, SWT.NONE );

    lca.render( clabel );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( clabel );
    assertTrue( operation.getPropertyNames().indexOf( "rightMargin" ) == -1 );
  }

  public void testRenderRightMargin() throws IOException {
    CLabel clabel = new CLabel( shell, SWT.NONE );

    clabel.setRightMargin( 5 );
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 5 ), message.findSetProperty( clabel, "rightMargin" ) );
  }

  public void testRenderRightMarginUnchanged() throws IOException {
    CLabel clabel = new CLabel( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( clabel );

    clabel.setRightMargin( 5 );
    Fixture.preserveWidgets();
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( clabel, "rightMargin" ) );
  }

  public void testRenderInitialBottomMargin() throws IOException {
    CLabel clabel = new CLabel( shell, SWT.NONE );

    lca.render( clabel );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( clabel );
    assertTrue( operation.getPropertyNames().indexOf( "bottomMargin" ) == -1 );
  }

  public void testRenderBottomMargin() throws IOException {
    CLabel clabel = new CLabel( shell, SWT.NONE );

    clabel.setBottomMargin( 5 );
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertEquals( new Integer( 5 ), message.findSetProperty( clabel, "bottomMargin" ) );
  }

  public void testRenderBottomMarginUnchanged() throws IOException {
    CLabel clabel = new CLabel( shell, SWT.NONE );
    Fixture.markInitialized( display );
    Fixture.markInitialized( clabel );

    clabel.setBottomMargin( 5 );
    Fixture.preserveWidgets();
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    assertNull( message.findSetOperation( clabel, "bottomMargin" ) );
  }

  public void testRenderInitialBackgroundGradient() throws IOException {
    CLabel clabel = new CLabel( shell, SWT.NONE );

    lca.render( clabel );

    Message message = Fixture.getProtocolMessage();
    CreateOperation operation = message.findCreateOperation( clabel );
    assertTrue( operation.getPropertyNames().indexOf( "backgroundGradient" ) == -1 );
  }

  public void testRenderBackgroundGradient() throws IOException, JSONException {
    CLabel clabel = new CLabel( shell, SWT.NONE );

    Color[] gradientColors = new Color[] {
      display.getSystemColor( SWT.COLOR_RED ),
      display.getSystemColor( SWT.COLOR_GREEN )
    };
    int[] percents = new int[] { 50 };
    clabel.setBackground( gradientColors , percents );
    lca.renderChanges( clabel );

    Message message = Fixture.getProtocolMessage();
    JSONArray gradient = ( JSONArray )message.findSetProperty( clabel, "backgroundGradient" );
    JSONArray colors = ( JSONArray )gradient.get( 0 );
    JSONArray stops = ( JSONArray )gradient.get( 1 );
    assertEquals( "#ff0000", colors.get( 0 ) );
    assertEquals( "#00ff00", colors.get( 1 ) );
    assertEquals( new Integer( 0 ), stops.get( 0 ) );
    assertEquals( new Integer( 50 ), stops.get( 1 ) );
    assertEquals( Boolean.FALSE, gradient.get( 2 ) );
  }

  public void testRenderBackgroundGradientUnchanged() throws IOException {
    CLabel clabel = new CLabel( shell, SWT.NONE );
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
