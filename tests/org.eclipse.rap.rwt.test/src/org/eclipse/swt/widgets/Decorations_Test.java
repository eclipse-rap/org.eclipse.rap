/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.eclipse.rap.rwt.testfixture.internal.TestUtil.createImage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.graphics.Image;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class Decorations_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testSetImages() {
    Display display = new Display();
    Decorations shell = new Shell( display );
    try {
      shell.setImages( null );
      fail( "null not allowed" );
    } catch ( IllegalArgumentException e ) {
      // expected
    }
    try {
      shell.setImages( new Image[]{ null } );
      fail( "null not allowed" );
    } catch ( IllegalArgumentException e ) {
      // expected
    }
  }

  @Test
  public void testGetImages() throws IOException {
    Display display = new Display();
    Decorations shell = new Shell( display );
    assertNotNull( shell.getImages() );
    assertEquals( 0, shell.getImages().length );
    Image image1 = createImage( display, Fixture.IMAGE1 );
    Image image2 = createImage( display, Fixture.IMAGE2 );
    shell.setImages( new Image[]{ image1, image2 } );
    Image[] images = shell.getImages();
    assertEquals( 2, images.length );
    assertEquals( image1, images[0] );
    assertEquals( image2, images[1] );
  }

  @Test
  public void testSetImage() throws IOException {
    Display display = new Display();
    Decorations shell = new Shell( display );
    assertNull( shell.getImage() );
    Image image = createImage( display, Fixture.IMAGE1 );
    shell.setImage( image );
    assertEquals( image, shell.getImage() );
  }

}
