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

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
    WidgetDataUtil.registerDataKeys(  "a", "b", "c" );

    assertEquals( createSet( "a", "b", "c" ), WidgetDataUtil.getDataKeys() );
  }

  @Test
  public void testRegisterDataKeys_appendKeys() {
    WidgetDataUtil.registerDataKeys( "a", "b", "c" );
    WidgetDataUtil.registerDataKeys( "d", "e", "f" );

    assertEquals( createSet( "a", "b", "c", "d", "e", "f" ), WidgetDataUtil.getDataKeys() );
  }

  @Test
  public void testRegisterDataKeys_duplicateKeys() {
    WidgetDataUtil.registerDataKeys( "a", "b", "a", "c", "c" );

    assertEquals( createSet( "a", "b", "c" ), WidgetDataUtil.getDataKeys() );
  }

  @Test
  public void testRegisterDataKeys_nullKey() {
    WidgetDataUtil.registerDataKeys( "a", "b", null, "c" );

    assertEquals( createSet( "a", "b", "c" ), WidgetDataUtil.getDataKeys() );
  }

  @Test
  public void testGetDataKeys_returnsEmptySet() {
    assertEquals( createSet(), WidgetDataUtil.getDataKeys() );
  }

  @Test( expected = UnsupportedOperationException.class )
  public void testGetDataKeys_returnsUnmodifiableSet() {
    WidgetDataUtil.getDataKeys().add( "foo" );
  }

  private static Set<String> createSet( String... keys ) {
    return new HashSet<String>( Arrays.asList( keys ) );
  }

}
