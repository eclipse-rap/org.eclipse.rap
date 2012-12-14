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
package org.eclipse.swt.internal.widgets;

import org.eclipse.rap.rwt.internal.RWTProperties;
import org.eclipse.rap.rwt.internal.util.ClassInstantiationException;
import org.eclipse.rap.rwt.testfixture.Fixture;

import junit.framework.TestCase;


public class IdGeneratorProvider_Test extends TestCase {

  private static final String CUSTOM_ID_GENERATOR_CLASS_NAME
    = "org.eclipse.swt.internal.widgets.CustomIdGenerator";

  @Override
  protected void setUp() throws Exception {
    Fixture.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.tearDown();
    System.clearProperty( RWTProperties.ID_GENERATOR );
  }

  public void testDefaultIdGenerator() {
    IdGenerator idGenerator = IdGeneratorProvider.getIdGenerator();

    assertTrue( idGenerator instanceof IdGeneratorImpl );
  }

  public void testDefaultIdGenerator_SameInstance() {
    IdGenerator idGenerator1 = IdGeneratorProvider.getIdGenerator();
    IdGenerator idGenerator2 = IdGeneratorProvider.getIdGenerator();

    assertSame( idGenerator1, idGenerator2 );
  }

  public void testCustomIdGenerator() {
    System.setProperty( RWTProperties.ID_GENERATOR, CUSTOM_ID_GENERATOR_CLASS_NAME );
    IdGenerator idGenerator = IdGeneratorProvider.getIdGenerator();

    assertTrue( idGenerator instanceof CustomIdGenerator );
  }

  public void testCustomIdGenerator_NotIdGeneratorClass() {
    System.setProperty( RWTProperties.ID_GENERATOR, "java.lang.Object" );

    try {
      IdGeneratorProvider.getIdGenerator();
      fail();
    } catch( ClassInstantiationException expected ) {
      String expectedMessage = "Class is not an instance of IdGenerator: java.lang.Object";
      assertEquals( expectedMessage, expected.getMessage() );
    }
  }

  public void testCustomIdGenerator_MissingClass() {
    System.setProperty( RWTProperties.ID_GENERATOR, "foo.bar.Gen" );

    try {
      IdGeneratorProvider.getIdGenerator();
      fail();
    } catch( ClassInstantiationException expected ) {
      String expectedMessage = "Failed to load class: foo.bar.Gen";
      assertEquals( expectedMessage, expected.getMessage() );
    }
  }
}
