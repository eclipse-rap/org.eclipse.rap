/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.graphics;

import java.io.InputStream;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.internal.graphics.*;
import org.eclipse.swt.internal.graphics.GCOperation.*;
import org.eclipse.swt.widgets.*;


public class GC_Test extends TestCase {

  public void testConstructorWithNullArgument() {
    new Display();
    try {
      new GC( null );
      fail( "GC( Device ): Must not allow null-argument" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testControlGCFont() {
    Shell shell = new Shell( new Display() );
    Control control = new Label( shell, SWT.NONE );
    GC gc = new GC( control );
    assertEquals( control.getFont(), gc.getFont() );
  }

  public void testDisplayGCFont() {
    Display display = new Display();
    GC gc = new GC( display );
    assertEquals( display.getSystemFont(), gc.getFont() );
  }

  public void testSetFont() {
    Display display = new Display();
    GC gc = new GC( display );
    Font font = new Font( display, "font-name", 11, SWT.NORMAL );
    gc.setFont( font );
    assertEquals( font, gc.getFont() );
    gc.setFont( null );
    assertEquals( display.getSystemFont(), gc.getFont() );
    Shell shell = new Shell( display );
    gc = new GC( shell );
    gc.setFont( font );
    IGCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    SetFont operation = ( SetFont )gcOperations[ 0 ];
    assertEquals( font, operation.font );
  }

  public void testDisposedGC() {
    Display display = new Display();
    GC gc = new GC( display );
    gc.dispose();
    assertTrue( gc.isDisposed() );
    try {
      gc.setFont( new Font( display, "font-name", 11, SWT.NORMAL ) );
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
      gc.setBackground( new Color( display, 1, 2, 3 ) );
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
      gc.setForeground( new Color( display, 1, 2, 3 ) );
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
  }

  public void testTextExtentWithNullArgument() {
    Display display = new Display();
    GC gc = new GC( display );
    try {
      gc.textExtent( null );
      fail( "textExtent must not allow null-argument" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testTextExtent() {
    Display display = new Display();
    GC gc = new GC( display );
    String string = "foo";
    Font systemFont = display.getSystemFont();
    Point gcTextExtent = gc.textExtent( string );
    Point textExtent = Graphics.textExtent( systemFont, string, 0 );
    assertEquals( gcTextExtent, textExtent );
  }

  public void testStringExtent() {
    Display display = new Display();
    GC gc = new GC( display );
    String string = "foo";
    Font systemFont = display.getSystemFont();
    Point gcStringExtent = gc.stringExtent( string );
    Point stringExtent = Graphics.stringExtent( systemFont, string );
    assertEquals( gcStringExtent, stringExtent );
  }

  public void testGetCharWidth() {
    Display display = new Display();
    GC gc = new GC( display );
    int width = gc.getCharWidth( 'A' );
    assertTrue( width > 0 );
  }

  public void testControlGCBackground() {
    Shell shell = new Shell( new Display() );
    Control control = new Label( shell, SWT.NONE );
    GC gc = new GC( control );
    assertEquals( control.getBackground(), gc.getBackground() );
  }

  public void testDisplayGCBackground() {
    Display display = new Display();
    GC gc = new GC( display );
    assertEquals( display.getSystemColor( SWT.COLOR_WHITE ),
                  gc.getBackground() );
  }

  public void testSetBackground() {
    Display display = new Display();
    GC gc = new GC( display );
    Color color = new Color( display, 1, 2, 3 );
    gc.setBackground( color );
    assertEquals( color, gc.getBackground() );
    Shell shell = new Shell( display );
    gc = new GC( shell );
    gc.setBackground( color );
    IGCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    SetProperty operation = ( SetProperty )gcOperations[ 0 ];
    assertEquals( SetProperty.BACKGROUND, operation.id );
    assertEquals( color, operation.value );
  }

  public void testSetBackgroundNullArgument() {
    Display display = new Display();
    GC gc = new GC( display );
    try {
      gc.setBackground( null );
      fail( "null not allowed on setBackground" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testSetBackgroundWithDisposedColor() {
    Display display = new Display();
    GC gc = new GC( display );
    Color color = new Color( display, 1, 2, 3 );
    color.dispose();
    try {
      gc.setBackground( color );
      fail( "disposed color not allowed on setBackground" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testControlGCForeground() {
    Shell shell = new Shell( new Display() );
    Control control = new Label( shell, SWT.NONE );
    GC gc = new GC( control );
    assertEquals( control.getForeground(), gc.getForeground() );
  }

  public void testDisplayGCForeground() {
    Display display = new Display();
    GC gc = new GC( display );
    assertEquals( display.getSystemColor( SWT.COLOR_BLACK ),
                  gc.getForeground() );
  }

  public void testSetForeground() {
    Display display = new Display();
    GC gc = new GC( display );
    Color color = new Color( display, 1, 2, 3 );
    gc.setForeground( color );
    assertEquals( color, gc.getForeground() );
    Shell shell = new Shell( display );
    gc = new GC( shell );
    gc.setForeground( color );
    IGCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    SetProperty operation = ( SetProperty )gcOperations[ 0 ];
    assertEquals( SetProperty.FOREGROUND, operation.id );
    assertEquals( color, operation.value );
  }

  public void testSetForegroundNullArgument() {
    Display display = new Display();
    GC gc = new GC( display );
    try {
      gc.setForeground( null );
      fail( "null not allowed on setForeground" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testSetForegroundWithDisposedColor() {
    Display display = new Display();
    GC gc = new GC( display );
    Color color = new Color( display, 1, 2, 3 );
    color.dispose();
    try {
      gc.setForeground( color );
      fail( "disposed color not allowed on setForeground" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testGetGCAdapter() {
    Display display = new Display();
    Shell shell = new Shell( display );
    GC gc = new GC( shell );
    IGCAdapter adapter1 = gc.getGCAdapter();
    assertNotNull( adapter1 );
    IGCAdapter adapter2 = gc.getGCAdapter();
    assertSame( adapter2, adapter1 );
    gc = new GC( display );
    assertNull( gc.getGCAdapter() );
    Button button = new Button( shell, SWT.NONE );
    gc = new GC( button );
    assertNull( gc.getGCAdapter() );
  }

  public void testDrawOperationWithNonCanvas() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Button button = new Button( shell, SWT.NONE );
    GC gc = new GC( button );
    gc.drawLine( 1, 2, 3, 4 );
    // This test has no assert. Ensures that no NPE is thrown.
  }

  public void testSetAlpha() {
    Display display = new Display();
    Control control = new Shell( display );
    GC gc = new GC( control );
    gc.setAlpha( 123 );
    assertEquals( 123, gc.getAlpha() );
    IGCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    SetProperty operation = ( SetProperty )gcOperations[ 0 ];
    assertEquals( SetProperty.ALPHA, operation.id );
    assertEquals( new Integer( 123 ), operation.value );
  }

  public void testSetLineWidth() {
    Display display = new Display();
    Control control = new Shell( display );
    GC gc = new GC( control );
    gc.setLineWidth( 5 );
    assertEquals( 5, gc.getLineWidth() );
    IGCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    SetProperty operation = ( SetProperty )gcOperations[ 0 ];
    assertEquals( SetProperty.LINE_WIDTH, operation.id );
    assertEquals( new Integer( 5 ), operation.value );
  }
  public void testSetLineCap() {
    Display display = new Display();
    Control control = new Shell( display );
    GC gc = new GC( control );
    gc.setLineCap( SWT.CAP_ROUND );
    assertEquals( SWT.CAP_ROUND, gc.getLineCap() );
    IGCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    SetProperty operation = ( SetProperty )gcOperations[ 0 ];
    assertEquals( SetProperty.LINE_CAP, operation.id );
    assertEquals( new Integer( SWT.CAP_ROUND ), operation.value );
    try {
      gc.setLineCap( 500 );
      fail( "value not allowed" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testSetLineJoin() {
    Display display = new Display();
    Control control = new Shell( display );
    GC gc = new GC( control );
    gc.setLineJoin( SWT.JOIN_ROUND );
    assertEquals( SWT.JOIN_ROUND, gc.getLineJoin() );
    IGCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    SetProperty operation = ( SetProperty )gcOperations[ 0 ];
    assertEquals( SetProperty.LINE_JOIN, operation.id );
    assertEquals( new Integer( SWT.JOIN_ROUND ), operation.value );
    try {
      gc.setLineCap( 500 );
      fail( "value not allowed" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testSetLineAttributes() {
    Display display = new Display();
    Control control = new Shell( display );
    GC gc = new GC( control );
    LineAttributes attributes
      = new LineAttributes( 5, SWT.CAP_ROUND, SWT.JOIN_BEVEL );
    gc.setLineAttributes( attributes );
    assertEquals( 5, gc.getLineWidth() );
    assertEquals( SWT.CAP_ROUND, gc.getLineCap() );
    assertEquals( SWT.JOIN_BEVEL, gc.getLineJoin() );
    assertEquals( 5, gc.getLineAttributes().width, 0 );
    assertEquals( SWT.CAP_ROUND, gc.getLineAttributes().cap );
    assertEquals( SWT.JOIN_BEVEL, gc.getLineAttributes().join );
    IGCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    SetProperty operation = ( SetProperty )gcOperations[ 0 ];
    assertEquals( SetProperty.LINE_WIDTH, operation.id );
    assertEquals( new Integer( 5 ), operation.value );
    operation = ( SetProperty )gcOperations[ 1 ];
    assertEquals( SetProperty.LINE_CAP, operation.id );
    assertEquals( new Integer( SWT.CAP_ROUND ), operation.value );
    operation = ( SetProperty )gcOperations[ 2 ];
    assertEquals( SetProperty.LINE_JOIN, operation.id );
    assertEquals( new Integer( SWT.JOIN_BEVEL ), operation.value );
    try {
      gc.setLineAttributes( null );
      fail( "null value not allowed" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testDrawLine() {
    Display display = new Display();
    Control control = new Shell( display );
    GC gc = new GC( control );
    gc.drawLine( 1, 2, 3, 4 );
    IGCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    DrawLine operation = ( DrawLine )gcOperations[ 0 ];
    assertEquals( 1, operation.x1 );
    assertEquals( 2, operation.y1 );
    assertEquals( 3, operation.x2 );
    assertEquals( 4, operation.y2 );
  }

  public void testDrawPoint() {
    Display display = new Display();
    Control control = new Shell( display );
    GC gc = new GC( control );
    gc.drawPoint( 1, 2 );
    IGCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    DrawPoint operation = ( DrawPoint )gcOperations[ 0 ];
    assertEquals( 1, operation.x );
    assertEquals( 2, operation.y );
  }

  public void testDrawRectangle() {
    Display display = new Display();
    Control control = new Shell( display );
    GC gc = new GC( control );
    gc.drawRectangle( 1, 2, 3, 4 );
    IGCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    DrawRectangle operation = ( DrawRectangle )gcOperations[ 0 ];
    assertEquals( 1, operation.x );
    assertEquals( 2, operation.y );
    assertEquals( 3, operation.width );
    assertEquals( 4, operation.height );
    assertFalse( operation.fill );
    try {
      gc.drawRectangle( null );
      fail( "null argument is not allowed on drawRectangle" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testDrawFocus() {
    Display display = new Display();
    Control control = new Shell( display );
    GC gc = new GC( control );
    gc.drawFocus( 1, 2, 3, 4 );
    IGCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    DrawRectangle operation = ( DrawRectangle )gcOperations[ 0 ];
    assertEquals( 1, operation.x );
    assertEquals( 2, operation.y );
    assertEquals( 3, operation.width );
    assertEquals( 4, operation.height );
    assertFalse( operation.fill );
  }

  public void testFillRectangle() {
    Display display = new Display();
    Control control = new Shell( display );
    GC gc = new GC( control );
    gc.fillRectangle( 1, 2, 3, 4 );
    IGCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    DrawRectangle operation = ( DrawRectangle )gcOperations[ 0 ];
    assertEquals( 1, operation.x );
    assertEquals( 2, operation.y );
    assertEquals( 3, operation.width );
    assertEquals( 4, operation.height );
    assertTrue( operation.fill );
    try {
      gc.fillRectangle( null );
      fail( "null argument is not allowed on fillRectangle" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testFillGradientRectangle() {
    Display display = new Display();
    Control control = new Shell( display );
    GC gc = new GC( control );
    gc.fillGradientRectangle( 1, 2, 3, 4, true );
    gc.fillGradientRectangle( 5, 6, 7, 8, false );
    IGCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    FillGradientRectangle operation = ( FillGradientRectangle )gcOperations[ 0 ];
    assertEquals( 1, operation.x );
    assertEquals( 2, operation.y );
    assertEquals( 3, operation.width );
    assertEquals( 4, operation.height );
    assertTrue( operation.vertical );
    assertTrue( operation.fill );
    operation = ( FillGradientRectangle )gcOperations[ 1 ];
    assertEquals( 5, operation.x );
    assertEquals( 6, operation.y );
    assertEquals( 7, operation.width );
    assertEquals( 8, operation.height );
    assertFalse( operation.vertical );
    assertTrue( operation.fill );
  }

  public void testDrawRoundRectangle() {
    Display display = new Display();
    Control control = new Shell( display );
    GC gc = new GC( control );
    gc.drawRoundRectangle( 1, 2, 3, 4, 5, 6 );
    IGCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    DrawRoundRectangle operation = ( DrawRoundRectangle )gcOperations[ 0 ];
    assertEquals( 1, operation.x );
    assertEquals( 2, operation.y );
    assertEquals( 3, operation.width );
    assertEquals( 4, operation.height );
    assertEquals( 5, operation.arcWidth );
    assertEquals( 6, operation.arcHeight );
    assertFalse( operation.fill );
  }

  public void testFillRoundRectangle() {
    Display display = new Display();
    Control control = new Shell( display );
    GC gc = new GC( control );
    gc.fillRoundRectangle( 1, 2, 3, 4, 5, 6 );
    IGCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    DrawRoundRectangle operation = ( DrawRoundRectangle )gcOperations[ 0 ];
    assertEquals( 1, operation.x );
    assertEquals( 2, operation.y );
    assertEquals( 3, operation.width );
    assertEquals( 4, operation.height );
    assertEquals( 5, operation.arcWidth );
    assertEquals( 6, operation.arcHeight );
    assertTrue( operation.fill );
  }

  public void testDrawArc() {
    Display display = new Display();
    Control control = new Shell( display );
    GC gc = new GC( control );
    gc.drawArc( 1, 2, 3, 4, 5, 6 );
    IGCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    DrawArc operation = ( DrawArc )gcOperations[ 0 ];
    assertEquals( 1, operation.x );
    assertEquals( 2, operation.y );
    assertEquals( 3, operation.width );
    assertEquals( 4, operation.height );
    assertEquals( 5, operation.startAngle );
    assertEquals( 6, operation.arcAngle );
    assertFalse( operation.fill );
  }

  public void testFillArc() {
    Display display = new Display();
    Control control = new Shell( display );
    GC gc = new GC( control );
    gc.fillArc( 1, 2, 3, 4, 5, 6 );
    IGCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    DrawArc operation = ( DrawArc )gcOperations[ 0 ];
    assertEquals( 1, operation.x );
    assertEquals( 2, operation.y );
    assertEquals( 3, operation.width );
    assertEquals( 4, operation.height );
    assertEquals( 5, operation.startAngle );
    assertEquals( 6, operation.arcAngle );
    assertTrue( operation.fill );
  }

  public void testDrawOval() {
    Display display = new Display();
    Control control = new Shell( display );
    GC gc = new GC( control );
    gc.drawOval( 1, 2, 3, 4 );
    IGCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    DrawArc operation = ( DrawArc )gcOperations[ 0 ];
    assertEquals( 1, operation.x );
    assertEquals( 2, operation.y );
    assertEquals( 3, operation.width );
    assertEquals( 4, operation.height );
    assertEquals( 0, operation.startAngle );
    assertEquals( 360, operation.arcAngle );
    assertFalse( operation.fill );
  }

  public void testFillOval() {
    Display display = new Display();
    Control control = new Shell( display );
    GC gc = new GC( control );
    gc.fillOval( 1, 2, 3, 4 );
    IGCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    DrawArc operation = ( DrawArc )gcOperations[ 0 ];
    assertEquals( 1, operation.x );
    assertEquals( 2, operation.y );
    assertEquals( 3, operation.width );
    assertEquals( 4, operation.height );
    assertEquals( 0, operation.startAngle );
    assertEquals( 360, operation.arcAngle );
    assertTrue( operation.fill );
  }

  public void testDrawPolygon() {
    Display display = new Display();
    Control control = new Shell( display );
    GC gc = new GC( control );
    int[] pointArray = new int[] { 1, 2, 3, 4 };
    gc.drawPolygon( pointArray );
    IGCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    DrawPolyline operation = ( DrawPolyline )gcOperations[ 0 ];
    assertTrue( Arrays.equals( pointArray, operation.points ) );
    assertTrue( operation.close );
    assertFalse( operation.fill );
  }

  public void testFillPolygon() {
    Display display = new Display();
    Control control = new Shell( display );
    GC gc = new GC( control );
    int[] pointArray = new int[] { 1, 2, 3, 4 };
    gc.fillPolygon( pointArray );
    IGCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    DrawPolyline operation = ( DrawPolyline )gcOperations[ 0 ];
    assertTrue( Arrays.equals( pointArray, operation.points ) );
    assertTrue( operation.close );
    assertTrue( operation.fill );
  }

  public void testDrawPolyline() {
    Display display = new Display();
    Control control = new Shell( display );
    GC gc = new GC( control );
    int[] pointArray = new int[] { 1, 2, 3, 4 };
    gc.drawPolyline( pointArray );
    IGCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    DrawPolyline operation = ( DrawPolyline )gcOperations[ 0 ];
    assertTrue( Arrays.equals( pointArray, operation.points ) );
    assertFalse( operation.close );
    assertFalse( operation.fill );
  }

  public void testDrawText() {
    Display display = new Display();
    Control control = new Shell( display );
    GC gc = new GC( control );
    gc.drawText( "text", 10, 10, SWT.DRAW_TRANSPARENT );
    GCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    DrawText operation = ( DrawText )gcOperations[ 0 ];
    assertEquals( "text", operation.text );
    assertEquals( 10, operation.x );
    assertEquals( 10, operation.y );
    assertEquals( SWT.DRAW_TRANSPARENT, operation.flags );
    try {
      gc.drawText( null, 10, 10, SWT.DRAW_TRANSPARENT );
      fail( "null argument is not allowed on drawText" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testDrawTextWithEmptyString() {
    Display display = new Display();
    Control control = new Shell( display );
    GC gc = new GC( control );
    gc.drawText( "", 10, 10, SWT.DRAW_TRANSPARENT );
    GCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    assertEquals( 0, gcOperations.length );
  }

  public void testDrawString() {
    Display display = new Display();
    Control control = new Shell( display );
    GC gc = new GC( control );
    gc.drawString( "text", 10, 10, true );
    GCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    DrawText operation = ( DrawText )gcOperations[ 0 ];
    assertEquals( "text", operation.text );
    assertEquals( 10, operation.x );
    assertEquals( 10, operation.y );
    assertEquals( SWT.DRAW_TRANSPARENT, operation.flags );
    try {
      gc.drawString( null, 10, 10, true );
      fail( "null argument is not allowed on drawText" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testDrawStringWithEmptyString() {
    Display display = new Display();
    Control control = new Shell( display );
    GC gc = new GC( control );
    gc.drawString( "", 10, 10, false );
    GCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    assertEquals( 0, gcOperations.length );
  }

  public void testDrawImageWithNullImage() {
    Display display = new Display();
    Control control = new Shell( display );
    GC gc = new GC( control );
    try {
      gc.drawImage( null, 1, 2 );
      fail( "null argument is not allowed on drawImage" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testDrawImageWithDisposedImage() {
    Display display = new Display();
    Control control = new Shell( display );
    GC gc = new GC( control );
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE1 );
    Image disposedImage = new Image( display, stream );
    disposedImage.dispose();
    try {
      gc.drawImage( disposedImage, 1, 2 );
      fail( "drawImage must not allow disposed image" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testDrawImage() {
    Display display = new Display();
    Control control = new Shell( display );
    GC gc = new GC( control );
    Image image = display.getSystemImage( SWT.ICON_INFORMATION );
    gc.drawImage( image, 1, 2 );
    GCAdapter adapter = gc.getGCAdapter();
    GCOperation[] gcOperations = adapter.getGCOperations();
    DrawImage operation = ( DrawImage )gcOperations[ 0 ];
    assertSame( image, operation.image );
    assertEquals( 0, operation.srcX );
    assertEquals( 0, operation.srcY );
    assertEquals( -1, operation.srcWidth );
    assertEquals( -1, operation.srcHeight );
    assertEquals( 1, operation.destX );
    assertEquals( 2, operation.destY );
    assertEquals( -1, operation.destWidth );
    assertEquals( -1, operation.destHeight );
    assertTrue( operation.simple );
    gc.drawImage( image, 1, 2, 3, 4, 5, 6, 7, 8 );
    gcOperations = adapter.getGCOperations();
    operation = ( DrawImage )gcOperations[ 1 ];
    assertSame( image, operation.image );
    assertEquals( 1, operation.srcX );
    assertEquals( 2, operation.srcY );
    assertEquals( 3, operation.srcWidth );
    assertEquals( 4, operation.srcHeight );
    assertEquals( 5, operation.destX );
    assertEquals( 6, operation.destY );
    assertEquals( 7, operation.destWidth );
    assertEquals( 8, operation.destHeight );
    assertFalse( operation.simple );
  }

  public void testGetClippingForControl() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Canvas canvas = new Canvas( shell, SWT.NONE );
    canvas.setSize( 100, 100 );
    GC gc = new GC( canvas );
    Rectangle clipping = gc.getClipping();
    assertEquals( new Rectangle( 0, 0, 100, 100), clipping );
  }

  public void testGetClippingForDisplay() {
    Display display = new Display();
    GC gc = new GC( display );
    Rectangle clipping = gc.getClipping();
    assertEquals( display.getBounds(), clipping );
  }

  public void testGetClippingOnDisposedGC() {
    Display display = new Display();
    GC gc = new GC( display );
    gc.dispose();
    try {
      gc.getClipping();
      fail( "getClipping must not return if GC was disposed" );
    } catch( SWTException e ) {
      // expected
    }
  }

  public void testStyle() {
    Display display = new Display();
    Shell shell = new Shell( display );
    GC gc = new GC( shell, SWT.NONE );
    assertEquals( SWT.LEFT_TO_RIGHT, gc.getStyle() );
    gc = new GC( shell, SWT.LEFT_TO_RIGHT );
    assertEquals( SWT.LEFT_TO_RIGHT, gc.getStyle() );
    gc = new GC( shell, SWT.PUSH );
    assertEquals( SWT.LEFT_TO_RIGHT, gc.getStyle() );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
