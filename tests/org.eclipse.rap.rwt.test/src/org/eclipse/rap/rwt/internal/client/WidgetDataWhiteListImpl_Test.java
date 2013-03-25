/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.client;


import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class WidgetDataWhiteListImpl_Test {

  WidgetDataWhiteListImpl list;

  @Before
  public void setUp() {
    list = new WidgetDataWhiteListImpl();
  }

  @After
  public void tearDown() {
    list = null;
  }

  @Test
  public void testGetKeys_InitialValueIsNull() {
    assertNull( list.getKeys() );
  }

  @Test
  public void testGetKeys_ReturnsUserValue() {
    list.setKeys( new String[] { "valueA", "valueB" } );

    assertTrue( Arrays.equals( new String[] { "valueA", "valueB" }, list.getKeys() ) );
  }

  @Test
  public void testGetKeys_ReturnsSaveCopy() {
    list.setKeys( new String[] { "valueA", "valueB" } );

    list.getKeys()[ 0 ] = "foo";

    assertTrue( Arrays.equals( new String[] { "valueA", "valueB" }, list.getKeys() ) );
  }

}
