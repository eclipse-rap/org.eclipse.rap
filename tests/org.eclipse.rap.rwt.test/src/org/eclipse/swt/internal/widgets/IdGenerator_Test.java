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
package org.eclipse.swt.internal.widgets;

import static org.eclipse.rap.rwt.testfixture.internal.SerializationTestUtil.serializeAndDeserialize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.junit.Before;
import org.junit.Test;


public class IdGenerator_Test {

  private IdGenerator idGenerator;

  @Before
  public void setUp() {
    idGenerator = new IdGenerator();
  }

  @Test
  public void testCreateId_doesNotExpectNull() {
    assertTrue( idGenerator.createId( null ).startsWith( "o" ) );
  }

  @Test
  public void testCreateId_uses_w_prefixForDisplay() {
    assertEquals( "w1", idGenerator.createId( mock( Display.class ) ) );
  }

  @Test
  public void testCreateId_uses_w_prefixForWidget() {
    assertEquals( "w2", idGenerator.createId( mock( Widget.class ) ) );
    assertEquals( "w3", idGenerator.createId( mock( Widget.class ) ) );
  }

  @Test
  public void testCreateId_uses_o_prefixForOtherObjects() {
    assertTrue( idGenerator.createId( mock( Object.class ) ).startsWith( "o" ) );
  }

  @Test
  public void testCreateId_usesStringParameterAsPrefix() {
    String id = idGenerator.createId( "foo" );

    assertTrue( id.startsWith( "foo" ) );
  }

  @Test
  public void testCreateId_idsDifferWithCustomPrefix() {
    String id1 = idGenerator.createId( "foo" );
    String id2 = idGenerator.createId( "foo" );

    assertFalse( id2.equals( id1 ) );
  }

  @Test
  public void testReproducibleIds() {
    String id1 = idGenerator.createId( "x" );
    String id2 = new IdGenerator().createId( "x" );

    assertEquals( id1, id2 );
  }

  @Test
  public void testSerialize() throws Exception {
    String id1 = idGenerator.createId( "x" );

    IdGenerator deserializedIdGenerator = serializeAndDeserialize( idGenerator );
    String id2 = deserializedIdGenerator.createId( "x" );

    assertFalse( id2.equals( id1 ) );
  }

}
