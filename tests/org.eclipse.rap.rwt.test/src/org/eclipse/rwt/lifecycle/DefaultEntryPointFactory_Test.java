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
package org.eclipse.rwt.lifecycle;

import junit.framework.TestCase;

import org.eclipse.rwt.internal.lifecycle.TestEntryPoint;


public class DefaultEntryPointFactory_Test extends TestCase {

  public void testConstructorWithNullParam() {
    try {
      new DefaultEntryPointFactory( ( Class<? extends IEntryPoint> )null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  public void testConstructorFailsWithAbstractClass() {
    try {
      new DefaultEntryPointFactory( AbstractClass.class );
      fail();
    } catch( IllegalArgumentException exception ) {
      String message = exception.getMessage();
      assertTrue( message.startsWith( "Abstract class or interface given as entrypoint" ) );
    }
  }

  public void testConstructorFailsWithInterface() {
    try {
      new DefaultEntryPointFactory( Interface.class );
      fail();
    } catch( IllegalArgumentException exception ) {
      String message = exception.getMessage();
      assertTrue( message.startsWith( "Abstract class or interface given as entrypoint" ) );
    }
  }

  public void testConstructorFailsWithNonStaticInnerClass() {
    try {
      new DefaultEntryPointFactory( NonStaticInnerClass.class );
      fail();
    } catch( IllegalArgumentException exception ) {
      String message = exception.getMessage();
      assertTrue( message.startsWith( "Non-static inner class given as entrypoint" ) );
    }
  }

  public void testCreate() {
    DefaultEntryPointFactory factory = new DefaultEntryPointFactory( TestEntryPoint.class );

    IEntryPoint entryPoint = factory.create();

    assertTrue( entryPoint instanceof TestEntryPoint );
  }

  public void testCreateWithInnerClass() {
    DefaultEntryPointFactory factory = new DefaultEntryPointFactory( EntryPoint.class );

    IEntryPoint entryPoint = factory.create();

    assertTrue( entryPoint instanceof EntryPoint );
  }

  public void testCreateWithPrivateConstructor() {
    DefaultEntryPointFactory factory
      = new DefaultEntryPointFactory( EntryPointWithPrivateConstructor.class );

    IEntryPoint entryPoint = factory.create();

    assertTrue( entryPoint instanceof EntryPointWithPrivateConstructor );
  }

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

  private static class EntryPoint implements IEntryPoint {

    public int createUI() {
      return 0;
    }
  }

  private static class EntryPointWithPrivateConstructor implements IEntryPoint {

    private EntryPointWithPrivateConstructor() {
    }

    public int createUI() {
      return 0;
    }
  }

  private static class EntryPointWithFailingConstructor implements IEntryPoint {

    @SuppressWarnings("unused")
    public EntryPointWithFailingConstructor() {
      throw new IllegalArgumentException();
    }

    public int createUI() {
      return 0;
    }
  }

  private class NonStaticInnerClass implements IEntryPoint {

    public int createUI() {
      return 0;
    }
  }

  private static abstract class AbstractClass implements IEntryPoint {

    public int createUI() {
      return 0;
    }
  }

  private static interface Interface extends IEntryPoint {

  }

}
