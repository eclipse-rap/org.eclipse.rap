/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.swt.SWT;


public class ToolItem_Test extends TestCase {

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  public void testText() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    ToolBar toolbar = new ToolBar( shell, SWT.NONE );
    ToolItem item = new ToolItem( toolbar, SWT.NONE );
    ToolItem separator = new ToolItem( toolbar, SWT.SEPARATOR );
    String text0 = "text0";
    String text1 = "text1";

    // Test 'normal' tool item
    item.setText( text0 );
    assertEquals( text0, item.getText() );
    item.setText( text1 );
    assertEquals( text1, item.getText() );
    // Test separator tool item
    assertEquals( "", separator.getText() );
    separator.setText( text1 );
    assertEquals( "", separator.getText() );
  }

  public void testImage() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    ToolBar toolbar = new ToolBar( shell, SWT.NONE );
    ToolItem item = new ToolItem( toolbar, SWT.NONE );
    item.setImage( null );
    assertEquals( null, item.getImage() );
  }

  public void testEnabled() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    ToolBar toolbar = new ToolBar( shell, SWT.NONE );
    ToolItem item = new ToolItem( toolbar, SWT.NONE );
    ToolItem separator = new ToolItem( toolbar, SWT.SEPARATOR );
    separator.setControl( new Text( toolbar, SWT.NONE ) );

    // ToolItem must be enabled initially
    assertEquals( true, item.getEnabled() );

    // Test enabled ToolItem on disabled ToolBar
    toolbar.setEnabled( false );
    item.setEnabled( true );
    assertEquals( true, item.getEnabled() );
    assertEquals( false, item.isEnabled() );

    // Test disabled ToolItem on disabled ToolBar
    toolbar.setEnabled( false );
    item.setEnabled( false );
    assertEquals( false, item.getEnabled() );
    assertEquals( false, item.isEnabled() );

    // Test SEPARATOR ToolItem
    separator.setEnabled( false );
    assertEquals( true, separator.getControl().getEnabled() );
  }

  public void testSeparatorWithControl() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    ToolBar toolbar = new ToolBar( shell, SWT.NONE );
    ToolItem item = new ToolItem( toolbar, SWT.NONE );
    ToolItem separator = new ToolItem( toolbar, SWT.SEPARATOR );
    separator.setControl( new Text( toolbar, SWT.NONE ) );

    // Using control property on ToolItem without SEPARATOR style has no effect
    item.setControl( new Text( toolbar, SWT.NONE ) );
    assertEquals( null, item.getControl() );

    // Setting a valid control on a SEPARATOR ToolItem
    Control control = new Text( toolbar, SWT.NONE );
    separator.setControl( control );
    assertSame( control, separator.getControl() );
    separator.setControl( null );
    assertEquals( null, separator.getControl() );

    // Illegal values for setControl
    Control currentControl = new Text( toolbar, SWT.NONE );
    separator.setControl( currentControl );
    Control diposedControl = new Text( toolbar, SWT.NONE );
    diposedControl.dispose();
    try {
      separator.setControl( diposedControl );
      fail( "Must not allow to set diposed control in setControl" );
    } catch( IllegalArgumentException e ) {
      assertSame( currentControl, separator.getControl() );
    }
    separator.setControl( currentControl );
    Control shellControl = new Text( shell, SWT.NONE );
    shellControl.dispose();
    try {
      separator.setControl( shellControl );
      fail( "Must not allow to set control with other parent than ToolItem" );
    } catch( IllegalArgumentException e ) {
      assertSame( currentControl, separator.getControl() );
    }
    
    // Ensure visibility of control is adjusted in the right way
    currentControl.setVisible( false );
    separator.setControl( currentControl );
    assertTrue( currentControl.getVisible() );
    separator.setControl( null );
    assertFalse( currentControl.getVisible() );
    
    // Dispose of control that is currently set on the SEPARATOR
    Control tempControl = new Text( toolbar, SWT.NONE );
    separator.setControl( tempControl );
    tempControl.dispose();
    assertEquals( null, separator.getControl() );
  }
  
  public void testSeparatorWithControlBounds() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );    
    ToolBar toolBar = new ToolBar( shell, SWT.NONE );
    ToolItem push = new ToolItem( toolBar, SWT.PUSH );
    ToolItem separator = new ToolItem( toolBar, SWT.SEPARATOR );
    separator.setWidth( 60 );
    Text text = new Text( toolBar, SWT.BORDER );
    separator.setControl( text );
    push.setImage( display.getSystemImage( SWT.ICON_ERROR ) );
    toolBar.pack();
    assertEquals( separator.getBounds(), text.getBounds() );
  }
  
  public void testSeparatorWidthHorizontal() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );    
    ToolBar toolBar = new ToolBar( shell, SWT.NONE );
    ToolItem push = new ToolItem( toolBar, SWT.PUSH );
    ToolItem separator = new ToolItem( toolBar, SWT.SEPARATOR );
    push.setImage( display.getSystemImage( SWT.ICON_ERROR ) );
    toolBar.pack();    
    int initalWidth = separator.getSeparatorWidth();    
    assertEquals( initalWidth, separator.getWidth() );
    separator.setWidth( 60 );
    toolBar.pack();    
    assertEquals( 60, separator.getWidth() );
    separator.setWidth( 60 );
  }
  
  public void testSeparatorWidthVertical() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );    
    ToolBar toolBar = new ToolBar( shell, SWT.VERTICAL );
    ToolItem push = new ToolItem( toolBar, SWT.PUSH );
    ToolItem separator = new ToolItem( toolBar, SWT.SEPARATOR );
    push.setImage( display.getSystemImage( SWT.ICON_ERROR ) );
    toolBar.pack();    
    int initalWidth = push.getWidth();    
    assertEquals( initalWidth, separator.getWidth() );
    separator.setWidth( 60 );
    toolBar.pack();    
    assertEquals( 60, separator.getWidth() );
    separator.setWidth( 60 );
  }
  
  public void testPreferredHeight() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );    
    ToolBar toolBar = new ToolBar( shell, SWT.VERTICAL );
    ToolItem push = new ToolItem( toolBar, SWT.PUSH );
    assertEquals( 22, push.getPreferredHeight() );
    push.setText( "Hello" );
    assertEquals( 22, push.getPreferredHeight() );
    push.setImage(  display.getSystemImage( SWT.ICON_ERROR ) );
    assertEquals( 36, push.getPreferredHeight() );    
  }
  
  public void testPreferredWidth() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );    
    ToolBar toolBar = new ToolBar( shell, SWT.VERTICAL );
    ToolItem push = new ToolItem( toolBar, SWT.PUSH );
    assertEquals( 6, push.getPreferredWidth() );
    push.setImage( display.getSystemImage( SWT.ICON_ERROR ) );
    assertEquals( 38, push.getPreferredWidth() );
    push.setText( "x" );
    assertTrue( push.getPreferredWidth() > 44 );
  }
  
  public void testDropDownPreferredWidth() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );    
    ToolBar toolBar = new ToolBar( shell, SWT.VERTICAL );
    ToolItem push = new ToolItem( toolBar, SWT.DROP_DOWN );
    assertEquals( 22, push.getPreferredWidth() );
    push.setImage(  display.getSystemImage( SWT.ICON_ERROR ) );
    assertEquals( 54, push.getPreferredWidth() );
    push.setText( "x" );
    assertTrue( push.getPreferredWidth() > 60 );
  }
}
