/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.protocol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ClientObjectAdapter_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testGetId_returnsIdWithCustomPrefix() {
    ClientObjectAdapter adapter = new ClientObjectAdapter( "x" );

    String id = adapter.getId();

    assertTrue( id.startsWith( "x" ) );
  }

  @Test
  public void testGetId_returnsStableId() {
    ClientObjectAdapter adapter = new ClientObjectAdapter( "x" );

    String id1 = adapter.getId();
    String id2 = adapter.getId();

    assertEquals( id1, id2 );
  }

  @Test
  public void testGetId_differsForDifferentAdapters() {
    String id1 = new ClientObjectAdapter( "x" ).getId();
    String id2 = new ClientObjectAdapter( "x" ).getId();

    assertFalse( id2.equals( id1 ) );
  }

}
