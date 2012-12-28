/*******************************************************************************
 * Copyright (c) 2011, 2012 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.graphics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.SWT;
import org.junit.Test;


public class LineAttributes_Test {

  @Test
  public void testSingleArgumentConstructor() {
    LineAttributes attributes = new LineAttributes( 1 );
    assertEquals( 1, attributes.width, 0 );
    assertEquals( SWT.CAP_FLAT, attributes.cap );
    assertEquals( SWT.JOIN_MITER, attributes.join );
  }

  @Test
  public void testThreeArgumentsConstructor() {
    LineAttributes attributes
      = new LineAttributes( 2, SWT.CAP_ROUND, SWT.JOIN_ROUND );
    assertEquals( 2, attributes.width, 0 );
    assertEquals( SWT.CAP_ROUND, attributes.cap );
    assertEquals( SWT.JOIN_ROUND, attributes.join );
  }

  @Test
  public void testEqualsWithDifferentWidth() {
    LineAttributes attributes1 = new LineAttributes( 1 );
    LineAttributes attributes2 = new LineAttributes( 2 );
    assertFalse( attributes1.equals( attributes2 ) );
  }

  @Test
  public void testEqualsWithDifferentCap() {
    LineAttributes attributes1
      = new LineAttributes( 1, SWT.CAP_FLAT, SWT.JOIN_BEVEL );
    LineAttributes attributes2
      = new LineAttributes( 1, SWT.CAP_ROUND, SWT.JOIN_BEVEL );
    assertFalse( attributes1.equals( attributes2 ) );
  }

  @Test
  public void testEqualsWithDifferentJoin() {
    LineAttributes attributes1
      = new LineAttributes( 1, SWT.CAP_FLAT, SWT.JOIN_BEVEL );
    LineAttributes attributes2
      = new LineAttributes( 1, SWT.CAP_FLAT, SWT.JOIN_MITER );
    assertFalse( attributes1.equals( attributes2 ) );
  }

  @Test
  public void testEqualsWithNull() {
    LineAttributes attributes = new LineAttributes( 0 );
    assertFalse( attributes.equals( null ) );
  }

  @Test
  public void testEqualsWithEqualAttribute() {
    LineAttributes attributes1
      = new LineAttributes( 1, SWT.CAP_FLAT, SWT.JOIN_BEVEL );
    LineAttributes attributes2
      = new LineAttributes( 1, SWT.CAP_FLAT, SWT.JOIN_BEVEL );
    assertTrue( attributes1.equals( attributes2 ) );
  }

  @Test
  public void testEqualsWithSameAttribute() {
    LineAttributes attributes = new LineAttributes( 1 );
    assertTrue( attributes.equals( attributes ) );
  }

  @Test
  public void testHashCodeWithDifferentWidth() {
    LineAttributes attributes1 = new LineAttributes( 1 );
    LineAttributes attributes2 = new LineAttributes( 2 );
    assertTrue( attributes1.hashCode() != attributes2.hashCode() );
  }

  @Test
  public void testHashCodeWithDifferentCap() {
    LineAttributes attributes1
      = new LineAttributes( 1, SWT.CAP_FLAT, SWT.JOIN_BEVEL );
    LineAttributes attributes2
      = new LineAttributes( 1, SWT.CAP_ROUND, SWT.JOIN_BEVEL );
    assertTrue( attributes1.hashCode() != attributes2.hashCode() );
  }

  @Test
  public void testHashCodeWithDifferentJoin() {
    LineAttributes attributes1
      = new LineAttributes( 1, SWT.CAP_FLAT, SWT.JOIN_BEVEL );
    LineAttributes attributes2
      = new LineAttributes( 1, SWT.CAP_FLAT, SWT.JOIN_ROUND );
    assertTrue( attributes1.hashCode() != attributes2.hashCode() );
  }

  @Test
  public void testHashCodeWithEqualAttribute() {
    LineAttributes attributes1
      = new LineAttributes( 1, SWT.CAP_FLAT, SWT.JOIN_BEVEL );
    LineAttributes attributes2
      = new LineAttributes( 1, SWT.CAP_FLAT, SWT.JOIN_BEVEL );
    assertEquals( attributes1.hashCode(), attributes2.hashCode() );
  }

}
