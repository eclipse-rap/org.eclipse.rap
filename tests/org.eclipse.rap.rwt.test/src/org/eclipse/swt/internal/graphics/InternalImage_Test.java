/*******************************************************************************
 * Copyright (c) 2011, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import static org.eclipse.rap.rwt.testfixture.SerializationTestUtil.serializeAndDeserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class InternalImage_Test {

  @Test( expected = NullPointerException.class )
  public void testConstructorWithNullResourceName() {
    new InternalImage( null, 1, 1, false );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testConstructorWithZeroWidth() {
    new InternalImage( "res", 0, 1, false );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testConstructorWithNegativeWidth() {
    new InternalImage( "res", -1, 1, false );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testConstructorWithZeroHeight() {
    new InternalImage( "res", 1, 0, false );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testConstructorWithNegativeHeight() {
    new InternalImage( "res", 1, 0, false );
  }

  @Test
  public void testConstructorWithExternalTrue() {
    InternalImage internalImage = new InternalImage( "res", 1, 2, true );

    assertTrue( internalImage.isExternal() );
  }

  @Test
  public void testConstructorWithExternalFalse() {
    InternalImage internalImage = new InternalImage( "res", 1, 2, false );

    assertFalse( internalImage.isExternal() );
  }

  @Test
  public void testGetBounds() {
    InternalImage internalImage = new InternalImage( "res", 1, 2, false );
    assertEquals( 0, internalImage.getBounds().x );
    assertEquals( 0, internalImage.getBounds().y );
    assertEquals( 1, internalImage.getBounds().width );
    assertEquals( 2, internalImage.getBounds().height );
  }

  @Test
  public void testGetResourceName() {
    String resourceName = "resourceName";
    InternalImage internalImage = new InternalImage( resourceName, 1, 2, false );
    assertEquals( resourceName, internalImage.getResourceName() );
  }

  @Test
  public void testSerialize() throws Exception {
    String resourceName = "resourceName";
    int width = 1;
    int height = 2;
    InternalImage internalImage = new InternalImage( resourceName, width, height, false );

    InternalImage deserializedInternalImage = serializeAndDeserialize( internalImage );

    assertEquals( resourceName, deserializedInternalImage.getResourceName() );
    assertEquals( width, deserializedInternalImage.getBounds().width );
    assertEquals( height, deserializedInternalImage.getBounds().height );
  }

}
