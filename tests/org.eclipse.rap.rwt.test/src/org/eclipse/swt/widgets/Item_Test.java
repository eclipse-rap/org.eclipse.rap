/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class Item_Test {

  private Display display;
  private Composite shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    shell = new Shell( display , SWT.NONE );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
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

  @Test
  public void testSetImage() throws IOException {
    Item item = new TestItem( shell, SWT.NONE );
    Image image = createImage();
    item.setImage( image );
    assertSame( image, item.getImage() );
    item.setImage( null );
    assertNull( item.getImage() );
  }

  @Test
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

  @Test
  public void testDispose() {
    Item item = new TestItem( shell, SWT.NONE );
    item.dispose();
    assertTrue( item.isDisposed() );
  }

  private Image createImage() throws IOException {
    InputStream stream = Fixture.class.getClassLoader().getResourceAsStream( Fixture.IMAGE1 );
    Image result = new Image( display, stream );
    stream.close();
    return result;
  }

  private static class TestItem extends Item {
    private TestItem( Widget parent, int style ) {
      super( parent, style );
    }
  }

}
