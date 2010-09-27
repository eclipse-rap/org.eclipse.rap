/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.swt.widgets;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.internal.theme.ThemeTestUtil;
import org.eclipse.rwt.internal.theme.ThemeUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.graphics.TextSizeDetermination;


public class Button_Test extends TestCase {

  public void testImage() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );

    Button button = new Button( shell, SWT.NONE );
    button.setImage( Graphics.getImage( Fixture.IMAGE1 ) );
    assertSame( Graphics.getImage( Fixture.IMAGE1 ), button.getImage() );

    Button button2 = new Button( shell, SWT.NONE );
    button2.setImage( Graphics.getImage( Fixture.IMAGE2 ) );
    assertSame( Graphics.getImage( Fixture.IMAGE2 ), button2.getImage() );

    button2.setImage( null );
    assertEquals( null, button2.getImage() );

    Button arrowButton = new Button( shell, SWT.ARROW );
    arrowButton.setImage( Graphics.getImage( Fixture.IMAGE1 ) );
    assertEquals( null, arrowButton.getImage() );
    
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE1 );
    Image image = new Image( display, stream );
    image.dispose();
    try {
      button.setImage( image );
      fail( "Must not allow disposed image" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testText() {
  	Display display = new Display();
  	Composite shell = new Shell( display, SWT.NONE );

  	Button button = new Button( shell, SWT.NONE );
  	button.setText( "Click me!" );
  	assertSame( "Click me!", button.getText() );

  	Button arrowButton = new Button( shell, SWT.ARROW );
  	arrowButton.setText( "Click me!" );
  	assertTrue( arrowButton.getText().length() == 0 );
  }

  public void testAlignment() {
  	Display display = new Display();
  	Composite shell = new Shell( display, SWT.NONE );

  	Button button = new Button( shell, SWT.NONE );
  	button.setAlignment( SWT.LEFT );
  	assertEquals( SWT.LEFT, button.getAlignment() );
  	button.setAlignment( SWT.RIGHT );
  	assertEquals( SWT.RIGHT, button.getAlignment() );
  	button.setAlignment( SWT.CENTER );
  	assertEquals( SWT.CENTER, button.getAlignment() );
  	button.setAlignment( SWT.UP );
  	assertEquals( SWT.CENTER, button.getAlignment() );

  	button = new Button( shell, SWT.NONE | SWT.LEFT );
  	assertEquals( SWT.LEFT, button.getAlignment() );
  	button = new Button( shell, SWT.NONE | SWT.RIGHT );
  	assertEquals( SWT.RIGHT, button.getAlignment() );
  	button = new Button( shell, SWT.NONE | SWT.CENTER );
  	assertEquals( SWT.CENTER, button.getAlignment() );

  	Button arrowButton = new Button( shell, SWT.ARROW );
  	arrowButton.setAlignment( SWT.LEFT );
  	assertEquals( SWT.LEFT, arrowButton.getAlignment() );
  	arrowButton.setAlignment( SWT.RIGHT );
  	assertEquals( SWT.RIGHT, arrowButton.getAlignment() );
  	arrowButton.setAlignment( SWT.UP );
  	assertEquals( SWT.UP, arrowButton.getAlignment() );
  	arrowButton.setAlignment( SWT.DOWN );
  	assertEquals( SWT.DOWN, arrowButton.getAlignment() );
  	arrowButton.setAlignment( SWT.FLAT );
  	assertEquals( SWT.UP, arrowButton.getAlignment() );

  	arrowButton = new Button( shell, SWT.ARROW | SWT.LEFT );
  	assertEquals( SWT.LEFT, arrowButton.getAlignment() );
  	arrowButton = new Button( shell, SWT.ARROW | SWT.RIGHT );
  	assertEquals( SWT.RIGHT, arrowButton.getAlignment() );
  	arrowButton = new Button( shell, SWT.ARROW | SWT.UP );
  	assertEquals( SWT.UP, arrowButton.getAlignment() );
  	arrowButton = new Button( shell, SWT.ARROW | SWT.DOWN );
  	assertEquals( SWT.DOWN, arrowButton.getAlignment() );
  	arrowButton = new Button( shell, SWT.ARROW | SWT.CENTER );
  	assertEquals( SWT.UP, arrowButton.getAlignment() );
  }

  public void testSelection() {
  	Display display = new Display();
  	Composite shell = new Shell( display, SWT.NONE );

  	Button button = new Button( shell, SWT.NONE );
    assertFalse( button.getSelection() );
  	button.setSelection( true );
  	assertFalse( button.getSelection() );

  	Button button1 = new Button( shell, SWT.CHECK );
  	assertFalse( button1.getSelection() );
  	button1.setSelection( true );
  	assertTrue( button1.getSelection() );
  	button1.setSelection( false );
  	assertFalse( button1.getSelection() );

  	Button button2 = new Button( shell, SWT.RADIO );
  	assertFalse( button2.getSelection() );
  	button2.setSelection( true );
  	assertTrue( button2.getSelection() );
  	button2.setSelection( false );
  	assertFalse( button2.getSelection() );

  	Button button3 = new Button( shell, SWT.TOGGLE );
    assertFalse( button3.getSelection() );
  	button3.setSelection( true );
  	assertTrue( button3.getSelection() );
  	button3.setSelection( false );
  	assertFalse( button3.getSelection() );
  }
  
  public void testGrayed() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );
    Button button = new Button( shell, SWT.CHECK );
    assertFalse( button.getGrayed() );
    button.setGrayed( true );
    assertTrue( button.getGrayed() );
    assertFalse( button.getSelection() );
    button.setSelection( true );
    assertTrue( button.getSelection() );
    assertTrue( button.getGrayed() );
    button.setGrayed( false );
    assertFalse( button.getGrayed() );
    assertTrue( button.getSelection() );
    button.setSelection( false );
    assertFalse( button.getSelection() );
    assertFalse( button.getGrayed() );
  }

  public void testComputeSize() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );

    // Text and image to use
    String text = "Click me!";
    Point extent = TextSizeDetermination.stringExtent( shell.getFont(), text );
    assertEquals( new Point( 52, 14 ), extent );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );

    // PUSH button
    Button button = new Button( shell, SWT.PUSH );
    Point expected = new Point( 13, 16 );
    assertEquals( expected, button.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    button.setText( text );
    expected = new Point( 64, 20 );
    assertEquals( expected, button.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    button.setImage( image );
    expected = new Point( 166, 56 );
    assertEquals( expected, button.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    button.setText( "" );
    expected = new Point( 112, 56 );
    assertEquals( expected, button.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    // PUSH button with BORDER
    button = new Button( shell, SWT.PUSH | SWT.BORDER );
    expected = new Point( 13, 16 );
    assertEquals( expected, button.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    button.setText( text );
    button.setImage( image );
    expected = new Point( 166, 56 );
    assertEquals( expected, button.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    // TOGGLE button
    button = new Button( shell, SWT.TOGGLE );
    button.setText( text );
    expected = new Point( 64, 20 );
    assertEquals( expected, button.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    button.setImage( image );
    expected = new Point( 166, 56 );
    assertEquals( expected, button.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    // TOGGLE button with border
    button = new Button( shell, SWT.TOGGLE | SWT.BORDER );
    expected = new Point( 13, 16 );
    assertEquals( expected, button.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    button.setText( text );
    button.setImage( image );
    expected = new Point( 166, 56 );
    assertEquals( expected, button.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    // CHECK button
    button = new Button( shell, SWT.CHECK );
    expected = new Point( 30, 23 );
    assertEquals( expected, button.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    button.setText( text );
    expected = new Point( 81, 23 );
    assertEquals( expected, button.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    button.setImage( image );
    expected = new Point( 185, 58 );
    assertEquals( expected, button.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    // CHECK button with border
    button = new Button( shell, SWT.CHECK | SWT.BORDER );
    expected = new Point( 32, 21 );
    assertEquals( expected, button.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    button.setText( text );
    expected = new Point( 83, 21 );
    assertEquals( expected, button.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    button.setImage( image );
    expected = new Point( 187, 56 );
    assertEquals( expected, button.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    // RADIO button
    button = new Button( shell, SWT.RADIO );
    expected = new Point( 30, 23 );
    assertEquals( expected, button.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    button.setText( text );
    expected = new Point( 81, 23 );
    assertEquals( expected, button.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    button.setImage( image );
    expected = new Point( 185, 58 );
    assertEquals( expected, button.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    // RADIO button with border
    button = new Button( shell, SWT.RADIO | SWT.BORDER );
    expected = new Point( 32, 21 );
    assertEquals( expected, button.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    button.setText( text );
    expected = new Point( 83, 21 );
    assertEquals( expected, button.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    button.setImage( image );
    expected = new Point( 187, 56 );
    assertEquals( expected, button.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    // fixed size
    expected = new Point( 102, 102 );
    assertEquals( expected, button.computeSize( 100, 100 ) );
  }

  public void testComputeSizeWithCustomTheme() throws IOException {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );

    String css = "Button {\nspacing: 10px;\n}";
    ThemeTestUtil.registerCustomTheme( "custom", css, null );
    ThemeUtil.setCurrentThemeId( "custom" );

    // Text and image to use
    String text = "Click me!";
    Point extent = TextSizeDetermination.stringExtent( shell.getFont(), text );
    assertEquals( new Point( 52, 14 ), extent );
    Image image = Graphics.getImage( Fixture.IMAGE_100x50 );
    
    // PUSH button
    Button button = new Button( shell, SWT.PUSH );
    button.setText( text );
    button.setImage( image );
    Point expected = new Point( 174, 56 );
    assertEquals( expected, button.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
  
    // CHECK button
    button = new Button( shell, SWT.CHECK );
    button.setText( text );
    expected = new Point( 87, 23 );
    assertEquals( expected, button.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
    button.setImage( image );
    expected = new Point( 197, 58 );
    assertEquals( expected, button.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
