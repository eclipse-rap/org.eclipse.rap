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

package org.eclipse.rap.rwt.widgets;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.RWTFixture;
import org.eclipse.rap.rwt.graphics.Image;
import junit.framework.TestCase;


public class Label_Test extends TestCase {
  
  public void testInitialValues() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Label label = new Label( shell, RWT.NONE );
    assertEquals( "", label.getText() ); 
    assertEquals( RWT.LEFT, label.getAlignment() ); 
  }
  
  public void testText() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Label label = new Label( shell, RWT.NONE );
    label.setText( "abc" );
    assertEquals( "abc", label.getText() );
    try {
      label.setText( null );
      fail( "Must not allow to set null-text." );
    } catch( NullPointerException e ) {
      // expected
    }
  }
  
  public void testStyle() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Label label;
    
    label = new Label( shell, RWT.NONE );
    assertTrue( ( label.getStyle() & RWT.SEPARATOR ) == 0 );
    assertTrue( ( label.getStyle() & RWT.LEFT ) != 0 );

    label = new Label( shell, RWT.SEPARATOR | RWT.VERTICAL | RWT.HORIZONTAL );
    assertTrue( ( label.getStyle() & RWT.SEPARATOR ) != 0 );
    assertTrue( ( label.getStyle() & RWT.VERTICAL ) != 0 );
    assertFalse( ( label.getStyle() & RWT.HORIZONTAL ) != 0 );

    label = new Label( shell, RWT.SEPARATOR | RWT.SHADOW_IN | RWT.SHADOW_OUT );
    assertTrue( ( label.getStyle() & RWT.SEPARATOR ) != 0 );
    assertTrue( ( label.getStyle() & RWT.VERTICAL ) != 0 );
    assertTrue( ( label.getStyle() & RWT.SHADOW_OUT ) != 0 );
    assertFalse( ( label.getStyle() & RWT.SHADOW_IN ) != 0 );
  }
  
  public void testSeparatorLabel() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Label label = new Label( shell, RWT.SEPARATOR );
    label.setText( "bla" );
    assertEquals( "", label.getText() );
    label.setImage( Image.find( RWTFixture.IMAGE1 ) );
    assertEquals( null, label.getImage() );
  }

  public void testImageAndText() {
    Display display = new Display();
    Shell shell = new Shell( display , RWT.NONE );
    Label label = new Label( shell, RWT.NONE );
    label.setText( "bla" );
    Image image = Image.find( RWTFixture.IMAGE1 );
    label.setImage( image );
    assertSame( image, label.getImage() );
    assertEquals( "", label.getText() );
    label.setText( "xyz" );
    assertEquals( "xyz", label.getText() );
    assertNull( label.getImage() );
  }
  
  protected void setUp() throws Exception {
    RWTFixture.setUp();
  }

  protected void tearDown() throws Exception {
    RWTFixture.tearDown();
  }
}
