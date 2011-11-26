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

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

public class Item_Test extends TestCase {

  private Display display;
  private Composite shell;

  private static class TestItem extends Item {
    private TestItem( Widget parent, int style ) {
      super( parent, style );
    }
  }

  public void testText() {
    Item item = new TestItem( shell, SWT.NONE );
    assertEquals( "", item.getText() );
    item.setText( "x" );
    assertEquals( "x", item.getText() );
    try {
      item.setText( null );
      fail( "Must not allow to set null text" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testSetImage() throws IOException {
    Item item = new TestItem( shell, SWT.NONE );
    Image image = createImage();
    item.setImage( image );
    assertSame( image, item.getImage() );
    item.setImage( null );
    assertNull( item.getImage() );
  }
  
  public void testSetImageWithDisposedImage() throws IOException {
    Image image = createImage();
    image.dispose();
    Item item = new TestItem( shell, SWT.NONE );
    try {
      item.setImage( image );
      fail( "No exception thrown for a disposed image" );
    } catch( IllegalArgumentException expected ) {
    }
  }

  public void testDispose() {
    Item item = new TestItem( shell, SWT.NONE );
    item.dispose();
    assertTrue( item.isDisposed() );
  }

  protected void setUp() throws Exception {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display , SWT.NONE );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

  private Image createImage() throws IOException {
    InputStream stream = Fixture.class.getClassLoader().getResourceAsStream( Fixture.IMAGE1 );
    Image result = new Image( display, stream );
    stream.close();
    return result;
  }
}
