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
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.swt.graphics.Image;

public class Decorations_Test extends TestCase {

  public void testSetImages() throws Exception {
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

  public void testGetImages() throws Exception {
    Display display = new Display();
    Decorations shell = new Shell( display );
    assertNotNull( shell.getImages() );
    assertEquals( 0, shell.getImages().length );
    Image image1 = Graphics.getImage( Fixture.IMAGE1 );
    Image image2 = Graphics.getImage( Fixture.IMAGE2 );
    shell.setImages( new Image[]{ image1, image2 } );
    Image[] images = shell.getImages();
    assertEquals( 2, images.length );
    assertEquals( image1, images[0] );
    assertEquals( image2, images[1] );
  }

  public void testSetImage() throws Exception {
    Display display = new Display();
    Decorations shell = new Shell( display );
    assertNull( shell.getImage() );
    Image image = Graphics.getImage( Fixture.IMAGE1 );
    shell.setImage( image );
    assertEquals( image, shell.getImage() );
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
