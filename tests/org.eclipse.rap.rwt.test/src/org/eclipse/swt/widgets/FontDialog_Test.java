/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ralf Zahn (ARS) - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.widgets;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.internal.application.RWTFactory;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;


public class FontDialog_Test extends TestCase {

  private Display display;
  private Shell shell;

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display );
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testCreateWithNull() {
    try {
      new FontDialog( null );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertEquals( "Argument cannot be null", expected.getMessage() );
    }
  }

  public void testDefaults() {
    FontDialog fontDialog = new FontDialog( shell );
    int defaultStyle = SWT.APPLICATION_MODAL | SWT.LEFT_TO_RIGHT;
    assertEquals( defaultStyle, fontDialog.getStyle() );
    assertNull( fontDialog.getFontList() );
    assertNull( fontDialog.getRGB() );
  }

  public void testSetRGB() {
    FontDialog fontDialog = new FontDialog( shell );
    RGB rgb = new RGB( 1, 2, 3 );
    fontDialog.setRGB( rgb );
    assertSame( rgb, fontDialog.getRGB() );
    fontDialog.setRGB( null );
    assertNull( fontDialog.getRGB() );
  }

  public void testSetFontList() {
    FontDialog fontDialog = new FontDialog( shell );
    FontData fontData = new FontData( "Test", 12, SWT.BOLD );
    FontData[] fontList = new FontData[]{ fontData };
    fontDialog.setFontList( fontList );
    assertNotSame( fontList, fontDialog.getFontList() );
    assertEquals( 1, fontDialog.getFontList().length );
    assertSame( fontList[ 0 ], fontDialog.getFontList()[ 0 ] );
  }

  public void testSetFontListEmpty() {
    FontDialog fontDialog = new FontDialog( shell );
    fontDialog.setFontList( new FontData[ 0 ] );
    assertNull( fontDialog.getFontList() );
  }

  public void testSetFontListWithTwoElements() {
    FontDialog fontDialog = new FontDialog( shell );
    fontDialog.setFontList( new FontData[ 0 ] );
    assertNull( fontDialog.getFontList() );
  }

  public void testSetNullFontList() {
    FontDialog fontDialog = new FontDialog( shell );
    FontData fontData1 = new FontData( "Test", 12, SWT.BOLD );
    FontData fontData2 = new FontData( "Test", 12, SWT.ITALIC );
    FontData[] fontList = new FontData[]{ fontData1, fontData2 };
    fontDialog.setFontList( fontList );
    assertEquals( 1, fontDialog.getFontList().length );
    assertSame( fontList[ 0 ], fontDialog.getFontList()[ 0 ] );
  }

  public void testFontData() {
    String result;
    result = FontDialog.getFirstFontName( "" );
    assertEquals( "", result );
    result = FontDialog.getFirstFontName( "x" );
    assertEquals( "x", result );
    result = FontDialog.getFirstFontName( "xxx" );
    assertEquals( "xxx", result );
    result = FontDialog.getFirstFontName( " TestOne, TestTwo " );
    assertEquals( "TestOne", result );
    result = FontDialog.getFirstFontName( "'Test Font'" );
    assertEquals( "Test Font", result );
    result = FontDialog.getFirstFontName( "\"Test Font\"" );
    assertEquals( "Test Font", result );
    result = FontDialog.getFirstFontName( "'Test One', 'Test Two', serif" );
    assertEquals( "Test One", result );
    result = FontDialog.getFirstFontName( "\"Test One\", \"Test Two\", serif" );
    assertEquals( "Test One", result );
  }

  public void testOpen_JEE_COMPATIBILITY() {
    // Activate SimpleLifeCycle
    RWTFactory.getLifeCycleFactory().deactivate();
    RWTFactory.getLifeCycleFactory().activate();
    FontDialog dialog = new FontDialog( shell );

    try {
      dialog.open();
      fail();
    } catch( UnsupportedOperationException expected ) {
      assertEquals( "Method not supported in JEE_COMPATIBILITY mode.", expected.getMessage() );
    }
  }
}
