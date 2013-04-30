/*******************************************************************************
 * Copyright (c) 2010, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.graphics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;

import org.eclipse.rap.rwt.internal.textsize.TextSizeUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class GC_Test {

  private Display display;
  private GC gc;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    gc = new GC( display );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testConstructorWithNullArgument() {
    try {
      new GC( null );
      fail( "GC( Device ): Must not allow null-argument" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testInitialValues() {
    assertEquals( 255, gc.getAlpha() );
    assertEquals( SWT.CAP_FLAT, gc.getLineCap() );
    assertEquals( SWT.JOIN_MITER, gc.getLineJoin() );
    assertEquals( 0, gc.getLineWidth() );
    LineAttributes lineAttributes = gc.getLineAttributes();
    assertEquals( SWT.CAP_FLAT, lineAttributes.cap );
    assertEquals( SWT.JOIN_MITER, lineAttributes.join );
    assertEquals( 0, ( int )lineAttributes.width );
    assertFalse( gc.getAdvanced() );
    assertEquals( SWT.DEFAULT, gc.getAntialias() );
    assertEquals( SWT.DEFAULT, gc.getTextAntialias() );
  }

  @Test
  public void testSetFontWithDisposedFont() {
    Font disposedFont = createFont();
    disposedFont.dispose();
    try {
      gc.setFont( disposedFont );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testDisposedGC() {
    gc.dispose();
    assertTrue( gc.isDisposed() );
    try {
      gc.setFont( createFont() );
      fail( "setFont not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.getFont();
      fail( "getFont not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.getCharWidth( 'X' );
      fail( "getCharWidth not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.stringExtent( "" );
      fail( "stringExtent not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.textExtent( "" );
      fail( "textExtent not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.getFontMetrics();
      fail( "getFontMetrics not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.setBackground( createColor() );
      fail( "setBackground not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.getBackground();
      fail( "getBackground not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.setForeground( createColor() );
      fail( "setForeground not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.getForeground();
      fail( "getForeground not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.setAlpha( 123 );
      fail( "setAlpha not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.getAlpha();
      fail( "getAlpha not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.setLineWidth( 5 );
      fail( "setLineWidth not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.getLineWidth();
      fail( "getLineWidth not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.setLineCap( SWT.CAP_ROUND );
      fail( "setLineCap not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.getLineCap();
      fail( "getLineCap not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.setLineJoin( SWT.JOIN_ROUND );
      fail( "setLineJoin not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.getLineJoin();
      fail( "getLineJoin not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.setLineAttributes( new LineAttributes( 1, 2, 3 ) );
      fail( "setLineAttributes not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.getLineAttributes();
      fail( "getLineAttributes not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.drawLine( 1, 2, 3, 4 );
      fail( "drawLine not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.drawPoint( 1, 2 );
      fail( "drawPoint not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.drawRectangle( 1, 2, 3, 4 );
      fail( "drawRectangle not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.fillRectangle( 1, 2, 3, 4 );
      fail( "fillRectangle not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.fillGradientRectangle( 1, 2, 3, 4, true );
      fail( "fillGradientRectangle not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.drawRoundRectangle( 1, 2, 3, 4, 5, 6 );
      fail( "drawRoundRectangle not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.fillRoundRectangle( 1, 2, 3, 4, 5, 6 );
      fail( "fillRoundRectangle not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.drawArc( 1, 2, 3, 4, 5, 6 );
      fail( "drawArc not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.fillArc( 1, 2, 3, 4, 5, 6 );
      fail( "fillArc not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.drawOval( 1, 2, 3, 4 );
      fail( "drawOval not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.fillOval( 1, 2, 3, 4 );
      fail( "fillOval not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.drawPolygon( new int[] { 1, 2, 3, 4 } );
      fail( "drawPolygon not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.fillPolygon( new int[] { 1, 2, 3, 4 } );
      fail( "fillPolygon not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.drawPolyline( new int[] { 1, 2, 3, 4 } );
      fail( "drawPolyline not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.drawText( "text", 1, 1, 0 );
      fail( "drawText not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.drawString( "text", 1, 1, true );
      fail( "drawString not allowed on disposed GC" );
    } catch( SWTException e ) {
      assertEquals( SWT.ERROR_GRAPHIC_DISPOSED, e.code );
    }
    try {
      gc.getClipping();
      fail( "getClipping must not return if GC was disposed" );
    } catch( SWTException e ) {
      // expected
    }
    try {
      gc.setAdvanced( false );
      fail( "setAdvanced is not allowed if GC was disposed" );
    } catch( SWTException e ) {
      // expected
    }
    try {
      gc.getAdvanced();
      fail( "getAdvanced must not return if GC was disposed" );
    } catch( SWTException e ) {
      // expected
    }
  }

  @Test
  public void testTextExtentWithNullArgument() {
    try {
      gc.textExtent( null );
      fail( "textExtent must not allow null-argument" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testTextExtent() {
    String string = "foo";
    Font systemFont = display.getSystemFont();
    Point gcTextExtent = gc.textExtent( string );
    Point textExtent = TextSizeUtil.textExtent( systemFont, string, 0 );
    assertEquals( gcTextExtent, textExtent );
  }

  @Test
  public void testStringExtent() {
    String string = "foo";
    Font systemFont = display.getSystemFont();
    Point gcStringExtent = gc.stringExtent( string );
    Point stringExtent = TextSizeUtil.stringExtent( systemFont, string );
    assertEquals( gcStringExtent, stringExtent );
  }

  @Test
  public void testGetCharWidth() {
    int width = gc.getCharWidth( 'A' );
    assertTrue( width > 0 );
  }

  @Test
  public void testSetBackgroundWithNullArgument() {
    try {
      gc.setBackground( null );
      fail( "null not allowed on setBackground" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testSetBackgroundWithDisposedColor() {
    Color color = createColor();
    color.dispose();
    try {
      gc.setBackground( color );
      fail( "disposed color not allowed on setBackground" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testSetForegroundWithNullArgument() {
    try {
      gc.setForeground( null );
      fail( "null not allowed on setForeground" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testSetForegroundWithDisposedColor() {
    Color color = createColor();
    color.dispose();
    try {
      gc.setForeground( color );
      fail( "disposed color not allowed on setForeground" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testSetAlpha() {
    gc.setAlpha( 123 );
    assertEquals( 123, gc.getAlpha() );
  }

  @Test
  public void testSetAlphaWithInvalidValue() {
    gc.setAlpha( 777 );
    assertEquals( 255, gc.getAlpha() );
  }

  @Test
  public void testSetLineWidth() {
    gc.setLineWidth( 5 );
    assertEquals( 5, gc.getLineWidth() );
  }

  @Test
  public void testSetLineWidthWithNegativeValue() {
    gc.setLineWidth( -2 );
    assertEquals( -2, gc.getLineWidth() );
  }

  @Test
  public void testSetLineCap() {
    gc.setLineCap( SWT.CAP_ROUND );
    assertEquals( SWT.CAP_ROUND, gc.getLineCap() );
  }

  @Test
  public void testSetLineCapWithInvalidValue() {
    try {
      gc.setLineCap( 500 );
      fail( "value not allowed" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testSetLineJoin() {
    gc.setLineJoin( SWT.JOIN_ROUND );
    assertEquals( SWT.JOIN_ROUND, gc.getLineJoin() );
  }

  @Test
  public void testSetLineJoinWithInvalidValue() {
    try {
      gc.setLineCap( 500 );
      fail( "value not allowed" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testSetLineAttributes() {
    LineAttributes attributes
      = new LineAttributes( 5, SWT.CAP_ROUND, SWT.JOIN_BEVEL );
    gc.setLineAttributes( attributes );
    assertEquals( 5, gc.getLineWidth() );
    assertEquals( SWT.CAP_ROUND, gc.getLineCap() );
    assertEquals( SWT.JOIN_BEVEL, gc.getLineJoin() );
    assertEquals( 5, gc.getLineAttributes().width, 0 );
    assertEquals( SWT.CAP_ROUND, gc.getLineAttributes().cap );
    assertEquals( SWT.JOIN_BEVEL, gc.getLineAttributes().join );
  }

  @Test
  public void testSetLineAttributesWithNullArgument() {
    try {
      gc.setLineAttributes( null );
      fail( "null value not allowed" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testGetLineAttributes() {
    LineAttributes attributes
      = new LineAttributes( 5, SWT.CAP_ROUND, SWT.JOIN_BEVEL );
    gc.setLineAttributes( attributes );
    LineAttributes returnedAttributes = gc.getLineAttributes();
    assertNotSame( attributes, returnedAttributes );
    assertEquals( attributes.cap, returnedAttributes.cap );
    assertEquals( attributes.join, returnedAttributes.join );
    assertEquals( attributes.width, returnedAttributes.width, 0 );
  }

  @Test
  public void testCheckBounds() {
    Rectangle rectangle = GC.checkBounds( 1, 2, 3, 4 );
    assertEquals( 1, rectangle.x );
    assertEquals( 2, rectangle.y );
    assertEquals( 3, rectangle.width );
    assertEquals( 4, rectangle.height );
  }

  @Test
  public void testCheckBoundsWithNegativeWidthAndHeight() {
    Rectangle rectangle = GC.checkBounds( 1, 2, -3, -4 );
    assertEquals( -2, rectangle.x );
    assertEquals( -2, rectangle.y );
    assertEquals( 3, rectangle.width );
    assertEquals( 4, rectangle.height );
  }

  @Test
  public void testDrawRectangeWithNullArgument() {
    try {
      gc.drawRectangle( null );
      fail( "null argument is not allowed on drawRectangle" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testFillRectangleWithNullArgument() {
    try {
      gc.fillRectangle( null );
      fail( "null argument is not allowed on fillRectangle" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testDrawPolygonWithNullArgument() {
    try {
      gc.drawPolygon( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testFillPolygonWithNullArgument() {
    try {
      gc.fillPolygon( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testDrawPolylineWithNullArgument() {
    try {
      gc.drawPolyline( null );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testDrawTextWithNullString() {
    try {
      gc.drawText( null, 10, 10, SWT.DRAW_TRANSPARENT );
      fail( "null argument is not allowed on drawText" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testDrawStringWithNullString() {
    try {
      gc.drawString( null, 10, 10, true );
      fail( "null argument is not allowed on drawText" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testDrawImageWithNullImage() {
    Control control = new Shell( display );
    GC gc = new GC( control );
    try {
      gc.drawImage( null, 1, 2 );
      fail( "null argument is not allowed on drawImage" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testDrawImageWithDisposedImage() throws IOException {
    Image disposedImage = createImage();
    disposedImage.dispose();
    try {
      gc.drawImage( disposedImage, 1, 2 );
      fail( "drawImage must not allow disposed image" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testDrawImageWithInvalidSourceRegion() {
    Image image = display.getSystemImage( SWT.ICON_INFORMATION );
    assertTrue( image.getBounds().width < 40 );  // precondition
    assertTrue( image.getBounds().height < 40 ); // precondition
    try {
      gc.drawImage( image, 10, 0, 50, 16, 0, 0, 100, 100 );
      fail( "srcWidth larger than srcX + image.width is not allowed" );
    } catch( IllegalArgumentException expected ) {
    }
    try {
      gc.drawImage( image, 0, 10, 16, 50, 0, 0, 100, 100 );
      fail( "srcHeight larger than srcY + image.height is not allowed" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testStyle() {
    GC gc = new GC( display, SWT.NONE );
    assertEquals( SWT.LEFT_TO_RIGHT, gc.getStyle() );
    gc = new GC( display, SWT.LEFT_TO_RIGHT );
    assertEquals( SWT.LEFT_TO_RIGHT, gc.getStyle() );
    gc = new GC( display, SWT.PUSH );
    assertEquals( SWT.LEFT_TO_RIGHT, gc.getStyle() );
  }

  @Test
  public void testAdvanced() {
    GC gc = new GC( display, SWT.NONE );
    gc.setAdvanced( true );
    assertTrue( gc.getAdvanced() );
  }

  @Test
  public void testGetAdvancedAfterUsingSetAlpha() {
    gc.setAlpha( 123 );
    assertTrue( gc.getAdvanced() );
  }

  @Test
  public void testGetAdvancedAfterUsingSetLineAttributes() {
    gc.setLineAttributes( new LineAttributes( 1 ) );
    assertTrue( gc.getAdvanced() );
  }

  @Test
  public void testGetAdvancedAfterUsingSetAntialias() {
    gc.setAntialias( SWT.ON );
    assertTrue( gc.getAdvanced() );
  }

  @Test
  public void testResetAdvancedAfterUsingAdvancedGrahpics() {
    gc.setAlpha( 123 );
    gc.setAdvanced( false );
    assertFalse( gc.getAdvanced() );
  }

  @Test
  public void testResetAlphaAfterUsingSetAdvanced() {
    gc.setAlpha( 64 );
    gc.setAdvanced( false );
    assertEquals( 255, gc.getAlpha() );
  }

  @Test
  public void testSerialization() throws Exception {
    // As of now, GC must not be serializable. It should only be used within a request (i.e. inside
    // a paint listener)
    try {
      Fixture.serialize( gc );
      fail();
    } catch( NotSerializableException expected ) {
    }
  }

  @Test
  public void testSetAntialias() {
    gc.setAntialias( SWT.ON );
    assertEquals( SWT.ON, gc.getAntialias() );
  }

  @Test
  public void testSetAntialiasWithInvalidArgument() {
    try {
      gc.setAntialias( 1234 );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testResetAntialiasAfterUsingSetAdvanced() {
    gc.setAntialias( SWT.ON );
    gc.setAdvanced( false );
    assertEquals( SWT.DEFAULT, gc.getAntialias() );
  }

  @Test
  public void testSetTextAntialias() {
    gc.setTextAntialias( SWT.ON );
    assertEquals( SWT.ON, gc.getTextAntialias() );
  }

  @Test
  public void testSetTextAntialiasWithInvalidArgument() {
    try {
      gc.setTextAntialias( 1234 );
    } catch( IllegalArgumentException expected ) {
    }
  }

  @Test
  public void testResetTextAntialiasAfterUsingSetAdvanced() {
    gc.setTextAntialias( SWT.ON );
    gc.setAdvanced( false );
    assertEquals( SWT.DEFAULT, gc.getTextAntialias() );
  }

  @Test
  public void testSetFontOverEqualDisplosedFont() {
    Font font = createFont();
    gc.setFont( font );
    font.dispose();

    font = createFont();
    gc.setFont( font );

    assertSame( font, gc.getFont() );
  }

  @Test
  public void testSetEqualFontTwice() {
    Font font = createFont();
    gc.setFont( font );

    font = createFont();
    gc.setFont( font );

    assertSame( font, gc.getFont() );
  }

  @Test
  public void testSetBackgroundOverEqualDisplosedColor() {
    Color color = createColor();
    gc.setBackground( color );
    color.dispose();

    color = createColor();
    gc.setBackground( color );

    assertSame( color, gc.getBackground() );
  }

  @Test
  public void testSetEqualBackgroundTwice() {
    Color color = createColor();
    gc.setBackground( color );

    color = createColor();
    gc.setBackground( color );

    assertSame( color, gc.getBackground() );
  }

  @Test
  public void testSetForegroundOverEqualDisplosedColor() {
    Color color = createColor();
    gc.setForeground( color );
    color.dispose();

    color = createColor();
    gc.setForeground( color );

    assertSame( color, gc.getForeground() );
  }

  @Test
  public void testSetEqualForegroundTwice() {
    Color color = createColor();
    gc.setForeground( color );

    color = createColor();
    gc.setForeground( color );

    assertSame( color, gc.getForeground() );
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDrawPath_withNullArgument() {
    gc.drawPath( null );
  }

  @Test(expected = SWTException.class)
  public void testDrawPath_withDisposedGC() {
    gc.dispose();

    gc.drawPath( new Path( display ) );
  }

  @Test(expected = IllegalArgumentException.class)
  public void testDrawPath_withDisposedPath() {
    Path path = new Path( display );
    path.dispose();

    gc.drawPath( path );
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFillPath_withNullArgument() {
    gc.fillPath( null );
  }

  @Test(expected = SWTException.class)
  public void testFillPath_withDisposedGC() {
    gc.dispose();

    gc.fillPath( new Path( display ) );
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFillPath_withDisposedPath() {
    Path path = new Path( display );
    path.dispose();

    gc.fillPath( path );
  }

  private Image createImage() throws IOException {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE1 );
    Image result = new Image( display, stream );
    stream.close();
    return result;
  }

  private Color createColor() {
    return new Color( display, 1, 2, 3 );
  }

  private Font createFont() {
    return new Font( display, "font-name", 11, SWT.NORMAL );
  }

}
