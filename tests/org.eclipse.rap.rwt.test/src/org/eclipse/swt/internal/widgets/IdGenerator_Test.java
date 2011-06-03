/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
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

import org.eclipse.rwt.Fixture;


public class IdGenerator_Test extends TestCase {
  
  private IdGenerator idGenerator;

  public void testNewId() {
    String id1 = idGenerator.newId();
    String id2 = idGenerator.newId();
    assertFalse( id1.equals( id2 ) );
  }
  
  public void testSerialize() throws Exception {
    idGenerator.newId();
    
    IdGenerator deserializedIdGenerator = Fixture.serializeAndDeserialize( idGenerator );
    
    assertEquals( "w3", deserializedIdGenerator.newId() );
  }
  
  protected void setUp() throws Exception {
    idGenerator = new IdGenerator();
  }
}
