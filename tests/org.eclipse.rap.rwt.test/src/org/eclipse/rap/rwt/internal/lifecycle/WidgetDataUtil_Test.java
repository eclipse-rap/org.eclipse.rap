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
package org.eclipse.rap.rwt.internal.lifecycle;

import static org.junit.Assert.assertArrayEquals;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class WidgetDataUtil_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testRegisterDataKeys() {
    String[] keys = new String[] { "a", "b", "c" };

    WidgetDataUtil.registerDataKeys( keys );

    assertArrayEquals( keys, WidgetDataUtil.getDataKeys().toArray() );
  }

  @Test
  public void testRegisterDataKeys_appendKeys() {
    String[] keys = new String[] { "a", "b", "c", "d", "e", "f" };

    WidgetDataUtil.registerDataKeys( "a", "b", "c" );
    WidgetDataUtil.registerDataKeys( "d", "e", "f" );

    assertArrayEquals( keys, WidgetDataUtil.getDataKeys().toArray() );
  }

  @Test
  public void testRegisterDataKeys_duplicateKeys() {
    String[] keys = new String[] { "a", "b", "c" };

    WidgetDataUtil.registerDataKeys( "a", "b", "a", "c", "c" );

    assertArrayEquals( keys, WidgetDataUtil.getDataKeys().toArray() );
  }

  @Test
  public void testRegisterDataKeys_nullKey() {
    String[] keys = new String[] { "a", "b", "c" };

    WidgetDataUtil.registerDataKeys( "a", "b", null, "c" );

    assertArrayEquals( keys, WidgetDataUtil.getDataKeys().toArray() );
  }

}
