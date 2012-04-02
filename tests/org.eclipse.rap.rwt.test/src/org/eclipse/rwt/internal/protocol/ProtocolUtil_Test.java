/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.protocol;

import java.util.Arrays;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.internal.graphics.FontUtil;
import org.eclipse.swt.widgets.Display;

import junit.framework.TestCase;


public class ProtocolUtil_Test extends TestCase {

  private Display display;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testColorToArray() {
    Color red = display.getSystemColor( SWT.COLOR_RED );

    int[] array = ProtocolUtil.getColorAsArray( red, false );

    checkColorArray( 255, 0, 0, 255, array );
  }

  public void testColorToArray_RGB() {
    RGB red = new RGB( 255, 0, 0 );

    int[] array = ProtocolUtil.getColorAsArray( red, false );

    checkColorArray( 255, 0, 0, 255, array );
  }

  public void testColorToArray_Transparent() {
    Color red = display.getSystemColor( SWT.COLOR_RED );

    int[] array = ProtocolUtil.getColorAsArray( red, true );

    checkColorArray( 255, 0, 0, 0, array );
  }

  public void testColorToArray_Null() {
    assertNull( ProtocolUtil.getColorAsArray( ( Color )null, false ) );
  }

  public void testFontAsArray() {
    Font font = new Font( display, "Arial", 22, SWT.NONE );

    Object[] array = ProtocolUtil.getFontAsArray( font );

    checkFontArray( new String[] { "Arial" }, 22, false, false, array );
  }

  public void testFontAsArray_FontData() {
    Font font = new Font( display, "Arial", 22, SWT.NONE );

    Object[] array = ProtocolUtil.getFontAsArray( FontUtil.getData( font ) );

    checkFontArray( new String[] { "Arial" }, 22, false, false, array );
  }

  public void testFontAsArray_Bold() {
    Font font = new Font( display, "Arial", 22, SWT.BOLD );

    Object[] array = ProtocolUtil.getFontAsArray( font );

    checkFontArray( new String[] { "Arial" }, 22, true, false, array );
  }

  public void testFontAsArray_Italic() {
    Font font = new Font( display, "Arial", 22, SWT.ITALIC );

    Object[] array = ProtocolUtil.getFontAsArray( font );

    checkFontArray( new String[] { "Arial" }, 22, false, true, array );
  }

  public void testFontAsArray_Null() {
    assertNull( ProtocolUtil.getFontAsArray( ( Font )null ) );
  }

  @SuppressWarnings("deprecation")
  public void testImageAsArray() {
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );

    Object[] array = ProtocolUtil.getImageAsArray( image );

    assertNotNull( array[ 0 ] );
    assertEquals( Integer.valueOf( 100 ), array[ 1 ] );
    assertEquals( Integer.valueOf( 50 ), array[ 2 ] );
  }

  public void testImageAsArray_Null() {
    assertNull( ProtocolUtil.getImageAsArray( null ) );
  }

  private void checkColorArray( int red, int green, int blue, int alpha, int[] array ) {
    assertEquals( red, array[ 0 ] );
    assertEquals( green, array[ 1 ] );
    assertEquals( blue, array[ 2 ] );
    assertEquals( alpha, array[ 3 ] );
  }

  private void checkFontArray( String[] names,
                               int size,
                               boolean bold,
                               boolean italic,
                               Object[] array )
  {
    Arrays.equals( names, ( String[] )array[ 0 ] );
    assertEquals( Integer.valueOf( size ), array[ 1 ] );
    assertEquals( Boolean.valueOf( bold ), array[ 2 ] );
    assertEquals( Boolean.valueOf( italic ), array[ 3 ] );
  }
}
