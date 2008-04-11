/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import junit.framework.TestCase;

import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;

public class Button_Test extends TestCase {

  public void testImage() {
    Display display = new Display();
    Composite shell = new Shell( display, SWT.NONE );

    Button button = new Button( shell, SWT.NONE );
    button.setImage( Graphics.getImage( RWTFixture.IMAGE1 ) );
    assertSame( Graphics.getImage( RWTFixture.IMAGE1 ), button.getImage() );

    Button button2 = new Button( shell, SWT.NONE );
    button2.setImage( Graphics.getImage( RWTFixture.IMAGE2 ) );
    assertSame( Graphics.getImage( RWTFixture.IMAGE2 ), button2.getImage() );

    button2.setImage( null );
    assertEquals( null, button2.getImage() );

    Button arrowButton = new Button( shell, SWT.ARROW );
    arrowButton.setImage( Graphics.getImage( RWTFixture.IMAGE1 ) );
    assertEquals( null, arrowButton.getImage() );
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

  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
