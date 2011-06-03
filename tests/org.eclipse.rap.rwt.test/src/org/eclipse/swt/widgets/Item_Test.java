/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
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

import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

public class Item_Test extends TestCase {

  private static class TestItem extends Item {
    private static final long serialVersionUID = 1L;
    private TestItem( Widget parent, int style ) {
      super( parent, style );
    }
  }

  public void testText() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Item item = new TestItem( shell, SWT.NONE );
    assertEquals( "", item.getText() );
    item.setText( "x" );
    assertEquals( "x", item.getText() );
    try {
      item.setText( null );
      fail( "Must not allow to set null text" );
    } catch( IllegalArgumentException iae ) {
      // expected
    }
  }

  public void testImage() {
    Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Item item = new TestItem( shell, SWT.NONE );
    item.setImage( Graphics.getImage( Fixture.IMAGE1 ) );
    assertSame( Graphics.getImage( Fixture.IMAGE1 ), item.getImage() );
    item.setImage( null );
    assertEquals( null, item.getImage() );
    Item item2 = new TestItem( shell, SWT.NONE );
    item2.setImage( Graphics.getImage( Fixture.IMAGE2 ) );
    assertSame( Graphics.getImage( Fixture.IMAGE2 ), item2.getImage() );
    // Test for a disposed Image as argument
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream stream = loader.getResourceAsStream( Fixture.IMAGE1 );
    Image image2 = new Image( display, stream );
    image2.dispose();
    try {
      item.setImage( image2 );
      fail( "No exception thrown for a disposed image" );
    } catch( IllegalArgumentException e ) {
      // expected
    }
  }

  public void testDispose() {
    final Display display = new Display();
    Composite shell = new Shell( display , SWT.NONE );
    Item item = new TestItem( shell, SWT.NONE );
    item.dispose();
    assertEquals( true, item.isDisposed() );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
