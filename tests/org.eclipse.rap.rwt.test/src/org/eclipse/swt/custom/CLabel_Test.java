/*******************************************************************************
 * Copyright (c) 2002, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.custom;

import static org.eclipse.rap.rwt.testfixture.SerializationTestUtil.serializeAndDeserialize;
import static org.eclipse.rap.rwt.testfixture.internal.TestUtil.createImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.internal.theme.IThemeAdapter;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.custom.clabelkit.CLabelThemeAdapter;
import org.eclipse.swt.internal.widgets.IWidgetGraphicsAdapter;
import org.eclipse.swt.internal.widgets.MarkupValidator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class CLabel_Test {

  private Display display;
  private Shell shell;
  private CLabel label;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
    label = new CLabel( shell, SWT.NONE );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testSetBackgroundColor() {
    Color red = display.getSystemColor( SWT.COLOR_RED );
    label.setBackground( red );
    assertEquals( label.getBackground(), red );
  }

  @Test
  public void testSetToolTipText() {
    label.setToolTipText( "foo" );
    assertEquals( label.getToolTipText(), "foo" );
  }

  @Test
  public void testSetAlignment() {
    label = new CLabel( shell, SWT.LEFT );
    assertEquals( label.getAlignment(), SWT.LEFT );
    label.setAlignment( SWT.RIGHT );
    assertEquals( label.getAlignment(), SWT.RIGHT );
  }

  @Test
  public void testSetImage() throws IOException {
    assertEquals( label.getImage(), null );
    Image image = createImage( display, Fixture.IMAGE1 );
    label.setImage( image );
    assertEquals( image, label.getImage() );
  }

  @Test
  public void testSetText() {
    assertEquals( null, label.getText() );
    label.setText( "bar" );
    assertEquals( label.getText(), "bar" );
  }

  @Test
  public void testComputeSize() throws IOException {
    Point expected = new Point( 12, 26 );
    assertEquals( expected, label.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    label.setText( "bar" );
    expected = new Point( 32, 30 );
    assertEquals( expected, label.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    label.setImage( createImage( display, Fixture.IMAGE_100x50 ) );
    expected = new Point( 137, 62 );
    assertEquals( expected, label.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    label.setMargins( 1, 2, 3, 4 );
    expected = new Point( 129, 56 );
    assertEquals( expected, label.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
  }

  @Test
  public void testSetMargins() {
    CLabelThemeAdapter themeAdapter = ( CLabelThemeAdapter )label.getAdapter( IThemeAdapter.class );
    Rectangle padding = themeAdapter.getPadding( label );
    assertEquals( padding.x, label.getLeftMargin() );
    assertEquals( padding.y, label.getTopMargin() );
    assertEquals( padding.width - padding.x, label.getRightMargin() );
    assertEquals( padding.height - padding.y, label.getBottomMargin() );
    label.setMargins( 1, 2, 3, 4 );
    assertEquals( 1, label.getLeftMargin() );
    assertEquals( 2, label.getTopMargin() );
    assertEquals( 3, label.getRightMargin() );
    assertEquals( 4, label.getBottomMargin() );
    label.setLeftMargin( 6 );
    assertEquals( 6, label.getLeftMargin() );
    label.setTopMargin( 7 );
    assertEquals( 7, label.getTopMargin() );
    label.setRightMargin( 8 );
    assertEquals( 8, label.getRightMargin() );
    label.setBottomMargin( 9 );
    assertEquals( 9, label.getBottomMargin() );
    label.setLeftMargin( -1 );
    assertEquals( 6, label.getLeftMargin() );
    label.setTopMargin( -1 );
    assertEquals( 7, label.getTopMargin() );
    label.setRightMargin( -1 );
    assertEquals( 8, label.getRightMargin() );
    label.setBottomMargin( -1 );
    assertEquals( 9, label.getBottomMargin() );
    label.setMargins( -1, -1, -1, -1 );
    assertEquals( 0, label.getLeftMargin() );
    assertEquals( 0, label.getTopMargin() );
    assertEquals( 0, label.getRightMargin() );
    assertEquals( 0, label.getBottomMargin() );
  }

  @Test
  public void testSetBackgroundGradient_Horizontal() {
    Color[] colors = new Color[] {
      display.getSystemColor( SWT.COLOR_RED ),
      display.getSystemColor( SWT.COLOR_GREEN ),
      display.getSystemColor( SWT.COLOR_BLUE )
    };
    int[] percents = new int[] { 33, 66 };
    label.setBackground( colors, percents );
    IWidgetGraphicsAdapter adapter
      = label.getAdapter( IWidgetGraphicsAdapter.class );
    assertEquals( colors.length, adapter.getBackgroundGradientColors().length );
    assertEquals( percents.length + 1,
                  adapter.getBackgroundGradientPercents().length );
    assertFalse( adapter.isBackgroundGradientVertical() );
  }

  @Test
  public void testSetBackgroundGradient_Vertical() {
    Color[] colors = new Color[] {
      display.getSystemColor( SWT.COLOR_RED ),
      display.getSystemColor( SWT.COLOR_GREEN ),
      display.getSystemColor( SWT.COLOR_BLUE )
    };
    int[] percents = new int[] { 33, 66 };
    label.setBackground( colors, percents, true );
    IWidgetGraphicsAdapter adapter
      = label.getAdapter( IWidgetGraphicsAdapter.class );
    assertEquals( colors.length, adapter.getBackgroundGradientColors().length );
    assertEquals( percents.length + 1,
                  adapter.getBackgroundGradientPercents().length );
    assertTrue( adapter.isBackgroundGradientVertical() );
  }

  @Test
  public void testSetBackgroundGradient_NullValues() {
    Color[] colors = null;
    int[] percents = null;
    try {
      label.setBackground( colors, percents, true );
    } catch( IllegalArgumentException ex ) {
      fail( "Null colors not allowed" );
    }
    colors = new Color[] {
      display.getSystemColor( SWT.COLOR_RED ),
      display.getSystemColor( SWT.COLOR_GREEN ),
      display.getSystemColor( SWT.COLOR_BLUE )
    };
    try {
      label.setBackground( colors, percents, true );
      fail( "Null percents not allowed" );
    } catch( IllegalArgumentException ex ) {
      // expected
    }
  }

  @Test
  public void testSetBackgroundGradient_ArraysSize() {
    Color[] colors = new Color[] {
      display.getSystemColor( SWT.COLOR_RED ),
      display.getSystemColor( SWT.COLOR_GREEN ),
      display.getSystemColor( SWT.COLOR_BLUE )
    };
    int[] percents = new int[] { 66 };
    try {
      label.setBackground( colors, percents, true );
      fail( "Wrong arrays size" );
    } catch( IllegalArgumentException ex ) {
      // expected
    }
  }

  @Test
  public void testSetBackgroundGradient_InvalidPercents() {
    Color[] colors = new Color[] {
      display.getSystemColor( SWT.COLOR_RED ),
      display.getSystemColor( SWT.COLOR_GREEN ),
      display.getSystemColor( SWT.COLOR_BLUE )
    };
    int[] percents = new int[] { 66, 30 };
    try {
      label.setBackground( colors, percents, true );
      fail( "Percents are not in increase order" );
    } catch( IllegalArgumentException ex ) {
      // expected
    }
    percents = new int[] { -10, 66 };
    try {
      label.setBackground( colors, percents, true );
      fail( "Percents value out of range 0 - 100" );
    } catch( IllegalArgumentException ex ) {
      // expected
    }
    percents = new int[] { 66, 110 };
    try {
      label.setBackground( colors, percents, true );
      fail( "Percents value out of range 0 - 100" );
    } catch( IllegalArgumentException ex ) {
      // expected
    }
  }

  @Test
  public void testSetBackgroundGradient_NullColorReplace() {
    label.setBackground( display.getSystemColor( SWT.COLOR_GREEN ) );
    Color[] colors = new Color[] {
      display.getSystemColor( SWT.COLOR_RED ),
      null,
      display.getSystemColor( SWT.COLOR_BLUE )
    };
    int[] percents = new int[] { 33, 66 };
    label.setBackground( colors, percents );
    IWidgetGraphicsAdapter adapter
      = label.getAdapter( IWidgetGraphicsAdapter.class );
    assertEquals( colors.length, adapter.getBackgroundGradientColors().length );
    assertEquals( display.getSystemColor( SWT.COLOR_GREEN ),
                  adapter.getBackgroundGradientColors()[ 1 ] );
  }

  @Test
  public void testIsSerializable() throws Exception {
    label.setText( "text" );

    CLabel deserializedLabel = serializeAndDeserialize( label );

    assertEquals( "text", deserializedLabel.getText() );
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

}
