/*******************************************************************************
 * Copyright (c) 2025 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.addons.camera;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;

import org.eclipse.swt.graphics.Point;
import org.junit.Test;


public class CameraOptions_Test {

  @Test
  public void testIsSerializable() {
    assertTrue( Serializable.class.isAssignableFrom( CameraOptions.class ) );
  }

  @Test
  public void testNoneOptions() {
    assertNull( CameraOptions.NONE.getResolution() );
    assertEquals( 1.0F, CameraOptions.NONE.getCompressionQuality(), 0 );
  }

  @Test
  public void testResolution() {
    CameraOptions cameraOptions = new CameraOptions();

    cameraOptions.setResolution( 500, 500 );

    assertEquals( new Point( 500, 500 ), cameraOptions.getResolution() );
  }

  @Test
  public void testDefaultResolutionIsNull() {
    CameraOptions cameraOptions = new CameraOptions();

    Point resolution = cameraOptions.getResolution();

    assertNull( resolution );
  }

  @Test
  public void testSetsComptressionQuality() {
    CameraOptions cameraOptions = new CameraOptions();

    cameraOptions.setCompressionQuality( 0.5F );

    assertEquals( 0.5F, cameraOptions.getCompressionQuality(), 0 );
  }

  @Test
  public void testDefaultCompressionQualityIsOne() {
    CameraOptions cameraOptions = new CameraOptions();

    float compressionQuality = cameraOptions.getCompressionQuality();

    assertEquals( 1.0F, compressionQuality, 0 );
  }
  
}
