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

import static org.mockito.Mockito.mock;
import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.widgets.Widget;


public class IdGeneratorImpl_Test extends TestCase {

  private IdGeneratorImpl idGenerator;

  @Override
  protected void setUp() throws Exception {
    idGenerator = new IdGeneratorImpl();
  }

  public void testCreateId() {
    String id1 = idGenerator.createId( "w" );
    String id2 = idGenerator.createId( "w" );

    assertFalse( id1.equals( id2 ) );
  }

  public void testCreateId_WithNull() {
    assertTrue( idGenerator.createId( null ).startsWith( "o" ) );
  }

  public void testCreateId_WithObject() {
    assertTrue( idGenerator.createId( mock( Object.class ) ).startsWith( "o" ) );
  }

  public void testCreateId_WithWidget() {
    assertTrue( idGenerator.createId( mock( Widget.class ) ).startsWith( "w" ) );
  }

  public void testReproducibleIds() {
    String id1 = idGenerator.createId( "w" );
    String id2 = new IdGeneratorImpl().createId( "w" );

    assertEquals( id1, id2 );
  }

  public void testIdsArePrefixed() {
    assertTrue( idGenerator.createId( "x" ).startsWith( "x" ) );
    assertTrue( idGenerator.createId( "y" ).startsWith( "y" ) );
  }

  public void testSerialize() throws Exception {
    String id1 = idGenerator.createId( "w" );

    IdGeneratorImpl deserializedIdGenerator = Fixture.serializeAndDeserialize( idGenerator );

    assertFalse( id1.equals( deserializedIdGenerator.createId( "w" ) ) );
  }
}
