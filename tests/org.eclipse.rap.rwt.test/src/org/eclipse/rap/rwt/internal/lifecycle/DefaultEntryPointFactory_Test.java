/*******************************************************************************
 * Copyright (c) 2011, 2012 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.rap.rwt.application.EntryPoint;
import org.junit.Test;


public class DefaultEntryPointFactory_Test {

  @Test
  public void testConstructorWithNullParam() {
    try {
      new DefaultEntryPointFactory( ( Class<? extends EntryPoint> )null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testConstructorFailsWithAbstractClass() {
    try {
      new DefaultEntryPointFactory( AbstractClass.class );
      fail();
    } catch( IllegalArgumentException exception ) {
      String message = exception.getMessage();
      assertTrue( message.startsWith( "Abstract class or interface given as entrypoint" ) );
    }
  }

  @Test
  public void testConstructorFailsWithInterface() {
    try {
      new DefaultEntryPointFactory( Interface.class );
      fail();
    } catch( IllegalArgumentException exception ) {
      String message = exception.getMessage();
      assertTrue( message.startsWith( "Abstract class or interface given as entrypoint" ) );
    }
  }

  @Test
  public void testConstructorFailsWithNonStaticInnerClass() {
    try {
      new DefaultEntryPointFactory( NonStaticInnerClass.class );
      fail();
    } catch( IllegalArgumentException exception ) {
      String message = exception.getMessage();
      assertTrue( message.startsWith( "Non-static inner class given as entrypoint" ) );
    }
  }

  @Test
  public void testCreate() {
    DefaultEntryPointFactory factory = new DefaultEntryPointFactory( TestEntryPoint.class );

    EntryPoint entryPoint = factory.create();

    assertTrue( entryPoint instanceof TestEntryPoint );
  }

  @Test
  public void testCreateTwice() {
    DefaultEntryPointFactory factory = new DefaultEntryPointFactory( TestEntryPoint.class );

    EntryPoint entryPoint1 = factory.create();
    EntryPoint entryPoint2 = factory.create();

    assertNotNull( entryPoint1 );
    assertNotSame( entryPoint1, entryPoint2 );
  }

  @Test
  public void testCreateWithInnerClass() {
    DefaultEntryPointFactory factory = new DefaultEntryPointFactory( InnerEntryPoint.class );

    EntryPoint entryPoint = factory.create();

    assertTrue( entryPoint instanceof InnerEntryPoint );
  }

  @Test
  public void testCreateWithPrivateConstructor() {
    DefaultEntryPointFactory factory
      = new DefaultEntryPointFactory( EntryPointWithPrivateConstructor.class );

    EntryPoint entryPoint = factory.create();

    assertTrue( entryPoint instanceof EntryPointWithPrivateConstructor );
  }

  @Test
  public void testCreateWithFailingConstructor() {
    DefaultEntryPointFactory factory
      = new DefaultEntryPointFactory( EntryPointWithFailingConstructor.class );

    try {
      factory.create();
      fail();
    } catch( Exception exception ) {
      assertTrue( exception.getMessage().startsWith( "Could not create entrypoint instance" ) );
    }
  }

  private static class InnerEntryPoint implements EntryPoint {

    public int createUI() {
      return 0;
    }
  }

  private static class EntryPointWithPrivateConstructor implements EntryPoint {

    private EntryPointWithPrivateConstructor() {
    }

    public int createUI() {
      return 0;
    }
  }

  private static class EntryPointWithFailingConstructor implements EntryPoint {

    @SuppressWarnings("unused")
    public EntryPointWithFailingConstructor() {
      throw new IllegalArgumentException();
    }

    public int createUI() {
      return 0;
    }
  }

  private class NonStaticInnerClass implements EntryPoint {

    public int createUI() {
      return 0;
    }
  }

  private static abstract class AbstractClass implements EntryPoint {

    public int createUI() {
      return 0;
    }
  }

  private static interface Interface extends EntryPoint {

  }

}
