/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;


public class IdGenerator_Test extends TestCase {

  private IdGenerator idGenerator;

  @Override
  protected void setUp() throws Exception {
    idGenerator = new IdGenerator();
  }

  public void testNewId() {
    String id1 = idGenerator.newId( "w" );
    String id2 = idGenerator.newId( "w" );

    assertFalse( id1.equals( id2 ) );
  }

  public void testReproducibleIds() {
    String id1 = idGenerator.newId( "w" );
    String id2 = new IdGenerator().newId( "w" );

    assertEquals( id1, id2 );
  }

  public void testIdsArePrefixed() {
    assertTrue( idGenerator.newId( "x" ).startsWith( "x" ) );
    assertTrue( idGenerator.newId( "y" ).startsWith( "y" ) );
  }

  public void testSerialize() throws Exception {
    String id1 = idGenerator.newId( "w" );

    IdGenerator deserializedIdGenerator = Fixture.serializeAndDeserialize( idGenerator );

    assertFalse( id1.equals( deserializedIdGenerator.newId( "w" ) ) );
  }
}
