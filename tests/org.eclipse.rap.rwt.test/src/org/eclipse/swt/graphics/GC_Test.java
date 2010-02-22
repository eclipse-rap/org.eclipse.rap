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

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
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
  
  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
