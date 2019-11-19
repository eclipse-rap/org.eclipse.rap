/*******************************************************************************
 * Copyright (c) 2002, 2019 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serializeAndDeserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.MarkupValidator;
import org.eclipse.swt.internal.widgets.labelkit.LabelLCA;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class Label_Test {

  @Rule
  public TestContext context = new TestContext();

  private Display display;
  private Shell shell;
  private Label label;

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display , SWT.NONE );
    label = new Label( shell, SWT.NONE );
  }

  @Test
  public void testInitialValues() {
    assertEquals( "", label.getText() );
    assertEquals( SWT.LEFT, label.getAlignment() );
  }

  @Test
  public void testText() {
    label.setText( "abc" );

    assertEquals( "abc", label.getText() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testText_failsWithNull() {
    label.setText( null );
  }

  @Test
  public void testStyle() {
    assertTrue( ( label.getStyle() & SWT.SEPARATOR ) == 0 );
    assertTrue( ( label.getStyle() & SWT.LEFT ) != 0 );

    label = new Label( shell, SWT.SEPARATOR | SWT.VERTICAL | SWT.HORIZONTAL );
    assertTrue( ( label.getStyle() & SWT.SEPARATOR ) != 0 );
    assertTrue( ( label.getStyle() & SWT.VERTICAL ) != 0 );
    assertFalse( ( label.getStyle() & SWT.HORIZONTAL ) != 0 );

    label = new Label( shell, SWT.SEPARATOR | SWT.SHADOW_IN | SWT.SHADOW_OUT );
    assertTrue( ( label.getStyle() & SWT.SEPARATOR ) != 0 );
    assertTrue( ( label.getStyle() & SWT.VERTICAL ) != 0 );
    assertTrue( ( label.getStyle() & SWT.SHADOW_OUT ) != 0 );
    assertFalse( ( label.getStyle() & SWT.SHADOW_IN ) != 0 );
  }

  @Test
  public void testAlignment() {
    label.setAlignment( SWT.LEFT );
    assertEquals( SWT.LEFT, label.getAlignment() );
    label.setAlignment( SWT.RIGHT );
    assertEquals( SWT.RIGHT, label.getAlignment() );
    label.setAlignment( SWT.CENTER );
    assertEquals( SWT.CENTER, label.getAlignment() );

    label = new Label( shell, SWT.SEPARATOR );
    assertEquals( 0, label.getAlignment() );
    label.setAlignment( SWT.RIGHT );
    assertEquals( 0, label.getAlignment() );

    label = new Label( shell, SWT.NONE );
    assertEquals( SWT.LEFT, label.getAlignment() );
    label = new Label( shell, SWT.LEFT );
    assertEquals( SWT.LEFT, label.getAlignment() );
    label = new Label( shell, SWT.RIGHT );
    assertEquals( SWT.RIGHT, label.getAlignment() );
    label = new Label( shell, SWT.CENTER );
    assertEquals( SWT.CENTER, label.getAlignment() );
  }

  @Test
  public void testSeparatorLabel() throws IOException {
    Label label = new Label( shell, SWT.SEPARATOR );
    label.setText( "bla" );
    assertEquals( "", label.getText() );
    label.setImage( createImage() );
    assertNull( label.getImage() );
  }

  @Test
  public void testImageAndText() throws IOException {
    label.setText( "bla" );
    Image image = createImage();
    label.setImage( image );
    assertSame( image, label.getImage() );
    assertEquals( "", label.getText() );
    label.setText( "xyz" );
    assertEquals( "xyz", label.getText() );
    assertNull( label.getImage() );
  }

  @Test
  public void testSize() {
    Label labelWrap = new Label( shell, SWT.WRAP );
    Label labelNoWrap = new Label( shell, SWT.NONE );
    String wrapText = "Text that wraps. Text that wraps. Text that wraps. ";
    labelWrap.setText( wrapText );
    labelNoWrap.setText( wrapText );
    Point extentPlain = labelNoWrap.computeSize( SWT.DEFAULT, SWT.DEFAULT );
    assertTrue( extentPlain.x > 100 );
    Point extentNoWrap = labelNoWrap.computeSize( 100, SWT.DEFAULT );
    assertEquals( extentPlain.y, extentNoWrap.y );
    Point extentWrap = labelWrap.computeSize( 100, SWT.DEFAULT );
    assertTrue( extentWrap.y > extentNoWrap.y );
    // ensure that label with empty text has zero width but has a height
    labelNoWrap.setText( "" );
    extentNoWrap = labelNoWrap.computeSize( SWT.DEFAULT, SWT.DEFAULT );
    assertEquals( 0, extentNoWrap.x );
    assertTrue( extentNoWrap.y > 0 );
    labelWrap.setText( "" );
    extentWrap = labelWrap.computeSize( SWT.DEFAULT, SWT.DEFAULT );
    assertEquals( 0, extentWrap.x );
    assertTrue( extentWrap.y > 0 );
  }

  @Test
  public void testComputeSize() throws IOException {
    Point expected = new Point( 0, 14 );
    assertEquals( expected, label.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    label.setText( "label text" );
    expected = new Point( 67, 20 );
    assertEquals( expected, label.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    Image image = createImage();
    label.setImage( image );
    expected = new Point( image.getBounds().width, image.getBounds().height );
    assertEquals( expected, label.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    label = new Label( shell, SWT.BORDER );
    label.setImage( image );
    expected = new Point( 102, 52 );
    assertEquals( expected, label.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    label = new Label( shell, SWT.SEPARATOR | SWT.HORIZONTAL );
    expected = new Point( Widget.DEFAULT_WIDTH, 2 );
    assertEquals( expected, label.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    label = new Label( shell, SWT.SEPARATOR | SWT.VERTICAL );
    expected = new Point( 2, Widget.DEFAULT_HEIGHT );
    assertEquals( expected, label.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    expected = new Point( 100, 100 );
    assertEquals( expected, label.computeSize( 100, 100 ) );
  }

  @Test
  public void testComputeSizeWithMarkupEnabled() {
    label.setText( "foo bar" );
    Point textExtent = label.computeSize( SWT.DEFAULT, SWT.DEFAULT );

    label.setText( "" );
    label.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    label.setText( "<span>foo</span>" );
    Point markupExtent = label.computeSize( SWT.DEFAULT, SWT.DEFAULT );

    assertTrue( markupExtent.x < textExtent.x );
    assertEquals( markupExtent.y, textExtent.y );
  }

  @Test
  public void testIsSerializable() throws Exception {
    String text = "labelText";
    label.setText( text );

    Label deserializedLabel = serializeAndDeserialize( label );

    assertEquals( text, deserializedLabel.getText() );
  }

  @Test
  public void testIsSerializable_withMarkup() throws Exception {
    String text = "<i>labelText</i>";
    label.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    label.setText( text );

    Label deserializedLabel = serializeAndDeserialize( label );

    assertEquals( text, deserializedLabel.getText() );
  }

  @Test
  public void testMarkupTextWithoutMarkupEnabled() {
    label.setData( RWT.MARKUP_ENABLED, Boolean.FALSE );

    try {
      label.setText( "invalid xhtml: <<&>>" );
    } catch( IllegalArgumentException notExpected ) {
      fail();
    }
  }

  @Test
  public void testMarkupTextWithMarkupEnabled() {
    label.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );

    try {
      label.setText( "invalid xhtml: <<&>>" );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testMarkupTextWithMarkupEnabled_ValidationDisabled() {
    label.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    label.setData( MarkupValidator.MARKUP_VALIDATION_DISABLED, Boolean.TRUE );

    try {
      label.setText( "invalid xhtml: <<&>>" );
    } catch( IllegalArgumentException notExpected ) {
      fail();
    }
  }

  @Test
  public void testSetMarkupEnabled_onDirtyWidget() {
    label.setText( "something" );

    try {
      label.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
      fail();
    } catch( SWTException expected ) {
      assertTrue( expected.throwable instanceof IllegalStateException );
    }
  }

  @Test
  public void testSetMarkupEnabled_onDirtyWidget_onceEnabledBefore() {
    label.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
    label.setText( "something" );

    label.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
  }

  @Test
  public void testDisableMarkupIsIgnored() {
    label.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );

    label.setData( RWT.MARKUP_ENABLED, Boolean.FALSE );

    assertEquals( Boolean.TRUE, label.getData( RWT.MARKUP_ENABLED ) );
  }

  @Test
  public void testSetData() {
    label.setData( "foo", "bar" );

    assertEquals( "bar", label.getData( "foo" ) );
  }

  @Test
  public void testGetAdapter_LCA() {
    assertTrue( label.getAdapter( WidgetLCA.class ) instanceof LabelLCA );
    assertSame( label.getAdapter( WidgetLCA.class ), label.getAdapter( WidgetLCA.class ) );
  }

  private Image createImage() throws IOException {
    InputStream stream = Fixture.class.getClassLoader().getResourceAsStream( Fixture.IMAGE_100x50 );
    Image result = new Image( display, stream );
    stream.close();
    return result;
  }

}
