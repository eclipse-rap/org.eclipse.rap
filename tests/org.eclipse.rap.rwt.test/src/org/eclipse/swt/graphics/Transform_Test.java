/*******************************************************************************
 * Copyright (c) 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.graphics;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

public class Transform_Test {

  private Device device;
  private Transform transform;

  @Before
  public void setUp() {
    device = mock( Device.class );
    transform = new Transform( device );
  }

  @Test
  public void testCreate() {
    checkElements( 1, 0, 0, 1, 0, 0 );
  }

  @Test
  public void testCreate_withInitialElementsArray() {
    transform = new Transform( device, new float[] { 6, 5, 4, 3, 2, 1 } );

    checkElements( 6, 5, 4, 3, 2, 1 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testCreate_withNullElementsArray() {
    new Transform( device, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testCreate_withLessElementsArray() {
    new Transform( device, new float[] { 6, 5, 4, 3 } );
  }

  @Test
  public void testCreate_withInitialElements() {
    transform = new Transform( device, 6, 5, 4, 3, 2, 1 );

    checkElements( 6, 5, 4, 3, 2, 1 );
  }

  @Test
  public void testIdentity() {
    transform = new Transform( device, 6, 5, 4, 3, 2, 1 );

    transform.identity();

    checkElements( 1, 0, 0, 1, 0, 0 );
  }

  @Test
  public void testIsIdentity() {
    assertTrue( transform.isIdentity() );
  }

  @Test
  public void testInvert() {
    transform = new Transform( device, 6, 5, 4, 3, 2, 1 );

    transform.invert();

    checkElements( -1.5f, 2.5f, 2f, -3f, 1f, -2f );
  }

  @Test
  public void testMultiply() {
    transform = new Transform( device, 6, 5, 4, 3, 2, 1 );
    Transform transform2 = new Transform( device, 1, 2, 3, 4, 5, 6 );

    transform.multiply( transform2 );

    checkElements( 14f, 11f, 34f, 27f, 56f, 44f );
  }

  @Test
  public void testRotate_identity_0() {
    transform.rotate( 0 );

    checkElements( 1f, 0f, 0f, 1f, 0f, 0f );
  }

  @Test
  public void testRotate_identity_30() {
    transform.rotate( 30 );

    checkElements( 0.86f, 0.5f, -0.5f, 0.86f, 0f, 0f );
  }

  @Test
  public void testRotate_identity_60() {
    transform.rotate( 60 );

    checkElements( 0.5f, 0.86f, -0.86f, 0.5f, 0f, 0f );
  }

  @Test
  public void testRotate_identity_90() {
    transform.rotate( 90 );

    checkElements( 0f, 1f, -1f, 0f, 0f, 0f );
  }

  @Test
  public void testRotate_30() {
    transform = new Transform( device, 1, 2, 3, 4, 5, 6 );

    transform.rotate( 30 );

    checkElements( 2.4f, 3.7f, 2.1f, 2.5f, 5f, 6f );
  }

  @Test
  public void testRotate_60() {
    transform = new Transform( device, 1, 2, 3, 4, 5, 6 );

    transform.rotate( 60 );

    checkElements( 3.1f, 4.5f, 0.6f, 0.3f, 5f, 6f );
  }

  @Test
  public void testRotate_90() {
    transform = new Transform( device, 1, 2, 3, 4, 5, 6 );

    transform.rotate( 90 );

    checkElements( 3f, 4f, -1f, -2f, 5f, 6f );
  }

  @Test
  public void testScale_identity() {
    transform.scale( 7, 8 );

    checkElements( 7f, 0f, 0f, 8f, 0f, 0f );
  }

  @Test
  public void testScale() {
    transform = new Transform( device, 1, 2, 3, 4, 5, 6 );

    transform.scale( 7, 8 );

    checkElements( 7f, 14f, 24f, 32f, 5f, 6f );
  }

  @Test
  public void testTranslate_identity() {
    transform.translate( 7, 8 );

    checkElements( 1f, 0f, 0f, 1f, 7f, 8f );
  }

  @Test
  public void testTranslate() {
    transform = new Transform( device, 1, 2, 3, 4, 5, 6 );

    transform.translate( 7, 8 );

    checkElements( 1f, 2f, 3f, 4f, 36f, 52f );
  }

  @Test
  public void testShear_identity() {
    transform.shear( 7, 8 );

    checkElements( 1f, 8f, 7f, 1f, 0f, 0f );
  }

  @Test
  public void testShear() {
    transform = new Transform( device, 1, 2, 3, 4, 5, 6 );

    transform.shear( 7, 8 );

    checkElements( 25f, 34f, 10f, 18f, 5f, 6f );
  }

  @Test
  public void testTransform() {
    transform = new Transform( device, 1, 2, 3, 4, 5, 6 );
    float[] points = new float[] { 7, 8, 9, 10 };

    transform.transform( points );

    assertArrayEquals( new float[] { 36f, 52f, 44f, 64f }, points, 0f );
  }

  @Test
  public void testTransform_oddValues() {
    transform = new Transform( device, 1, 2, 3, 4, 5, 6 );
    float[] points = new float[] { 7, 8, 9, 10, 11 };

    transform.transform( points );

    assertArrayEquals( new float[] { 36f, 52f, 44f, 64f, 11f }, points, 0f );
  }

  private void checkElements( float m11, float m12, float m21, float m22, float dx, float dy ) {
    float[] elements = new float[ 6 ];
    transform.getElements( elements );
    assertEquals( m11, elements[ 0 ], 0.05 );
    assertEquals( m12, elements[ 1 ], 0.05 );
    assertEquals( m21, elements[ 2 ], 0.05 );
    assertEquals( m22, elements[ 3 ], 0.05 );
    assertEquals( dx, elements[ 4 ], 0.05 );
    assertEquals( dy, elements[ 5 ], 0.05 );
  }

}
