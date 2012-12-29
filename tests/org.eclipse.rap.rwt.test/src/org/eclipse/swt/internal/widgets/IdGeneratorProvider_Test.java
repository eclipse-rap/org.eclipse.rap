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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.eclipse.rap.rwt.internal.RWTProperties;
import org.eclipse.rap.rwt.internal.util.ClassInstantiationException;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class IdGeneratorProvider_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
    System.clearProperty( RWTProperties.ID_GENERATOR );
  }

  @Test
  public void testGetIdGenerator_returnsDefaultGenerator() {
    IdGenerator idGenerator = IdGeneratorProvider.getIdGenerator();

    assertSame( IdGeneratorImpl.class, idGenerator.getClass() );
  }

  @Test
  public void testGetIdGenerator_returnsSameInstance() {
    IdGenerator idGenerator1 = IdGeneratorProvider.getIdGenerator();
    IdGenerator idGenerator2 = IdGeneratorProvider.getIdGenerator();

    assertSame( idGenerator1, idGenerator2 );
  }

  @Test
  public void testGetIdGenerator_CustomGenerator() {
    System.setProperty( RWTProperties.ID_GENERATOR, CustomIdGenerator.class.getName() );

    IdGenerator idGenerator = IdGeneratorProvider.getIdGenerator();

    assertSame( CustomIdGenerator.class, idGenerator.getClass() );
  }

  @Test
  public void testGetIdGenerator_failsForIncompatibleClass() {
    System.setProperty( RWTProperties.ID_GENERATOR, "java.lang.Object" );

    try {
      IdGeneratorProvider.getIdGenerator();
      fail();
    } catch( ClassInstantiationException expected ) {
      String expectedMessage = "Class is not an instance of IdGenerator: java.lang.Object";
      assertEquals( expectedMessage, expected.getMessage() );
    }
  }

  @Test
  public void testGetIdGenerator_failsForMissingClass() {
    System.setProperty( RWTProperties.ID_GENERATOR, "foo.bar.Generator" );

    try {
      IdGeneratorProvider.getIdGenerator();
      fail();
    } catch( ClassInstantiationException expected ) {
      String expectedMessage = "Failed to load class: foo.bar.Generator";
      assertEquals( expectedMessage, expected.getMessage() );
    }
  }

}
