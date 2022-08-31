/*******************************************************************************
 * Copyright (c) 2013, 2022 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal;

import static org.eclipse.rap.rwt.internal.RWTProperties.ENABLE_LOAD_TESTS;
import static org.eclipse.rap.rwt.internal.RWTProperties.SERVICE_HANDLER_BASE_URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class RWTProperties_Test {

  private static final String TEST_PROPERTY = "rwt-test-property";

  @Before
  public void setUp() {
    System.getProperties().remove( TEST_PROPERTY );
  }

  @After
  public void tearDown() {
    System.getProperties().remove( TEST_PROPERTY );
    System.getProperties().remove( SERVICE_HANDLER_BASE_URL );
    System.getProperties().remove( ENABLE_LOAD_TESTS );
  }

  @Test
  public void testGetBooleanProperty_withTrue() {
    System.setProperty( TEST_PROPERTY, "true" );

    assertTrue( RWTProperties.getBooleanProperty( TEST_PROPERTY, false ) );
  }

  @Test
  public void testGetBooleanProperty_withFalse() {
    System.setProperty( TEST_PROPERTY, "false" );

    assertFalse( RWTProperties.getBooleanProperty( TEST_PROPERTY, true ) );
  }

  @Test
  public void testGetBooleanProperty_unknownIsHandledAsFalse() {
    // conforms to contract of Boolean.valueOf(String)
    System.setProperty( TEST_PROPERTY, "dontCare" );

    assertFalse( RWTProperties.getBooleanProperty( TEST_PROPERTY, true ) );
  }

  @Test
  public void testGetBooleanProperty_ignoresCase() {
    System.setProperty( TEST_PROPERTY, "TruE" );

    assertTrue( RWTProperties.getBooleanProperty( TEST_PROPERTY, false ) );
  }

  @Test
  public void testGetBooleanProperty_usesDefault() {
    assertTrue( RWTProperties.getBooleanProperty( TEST_PROPERTY, true ) );
    assertFalse( RWTProperties.getBooleanProperty( TEST_PROPERTY, false ) );
  }

  @Test
  public void testGetIntProperty_withValidInt() {
    System.setProperty( TEST_PROPERTY, "123" );

    assertEquals( 123, RWTProperties.getIntProperty( TEST_PROPERTY, 456 ) );
  }

  @Test
  public void testGetIntProperty_withInvalidInt() {
    System.setProperty( TEST_PROPERTY, "abc" );

    assertEquals( 456, RWTProperties.getIntProperty( TEST_PROPERTY, 456 ) );
  }

  @Test
  public void testGetIntProperty_usesDefault() {
    assertEquals( 456, RWTProperties.getIntProperty( TEST_PROPERTY, 456 ) );
  }

  @Test
  public void testGetServiceHandlerBaseUrl_returnsNullByDefault() {
    assertNull( RWTProperties.getServiceHandlerBaseUrl() );
  }

  @Test
  public void testGetServiceHandlerBaseUrl() {
    System.setProperty( SERVICE_HANDLER_BASE_URL, "http://foo/bar" );

    assertEquals( "http://foo/bar", RWTProperties.getServiceHandlerBaseUrl() );
  }

  @Test
  public void testIsLoadTestsEnabled() {
    assertFalse( RWTProperties.isLoadTestsEnabled() );

    System.setProperty( ENABLE_LOAD_TESTS, "true" );

    assertTrue( RWTProperties.isLoadTestsEnabled() );
  }

}
