/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.widgets;

import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;


public class Label_Test extends TestCase {
  
  public void testInitialValues() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Label label = new Label( shell, SWT.NONE );
    assertEquals( "", label.getText() ); 
    assertEquals( SWT.LEFT, label.getAlignment() ); 
  }
  
  public void testText() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Label label = new Label( shell, SWT.NONE );
    label.setText( "abc" );
    assertEquals( "abc", label.getText() );
    try {
      label.setText( null );
      fail( "Must not allow to set null-text." );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }
  
  public void testStyle() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Label label;
    
    label = new Label( shell, SWT.NONE );
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
  
  public void testSeparatorLabel() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Label label = new Label( shell, SWT.SEPARATOR );
    label.setText( "bla" );
    assertEquals( "", label.getText() );
    label.setImage( Image.find( RWTFixture.IMAGE1 ) );
    assertEquals( null, label.getImage() );
  }

  public void testImageAndText() {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
    Label label = new Label( shell, SWT.NONE );
    label.setText( "bla" );
    Image image = Image.find( RWTFixture.IMAGE1 );
    label.setImage( image );
    assertSame( image, label.getImage() );
    assertEquals( "", label.getText() );
    label.setText( "xyz" );
    assertEquals( "xyz", label.getText() );
    assertNull( label.getImage() );
  }
  
  public void testSize() throws Exception {
    Display display = new Display();
    Shell shell = new Shell( display , SWT.NONE );
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
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
