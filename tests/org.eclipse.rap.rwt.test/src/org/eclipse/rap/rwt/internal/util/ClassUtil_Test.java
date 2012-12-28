/*******************************************************************************
 * Copyright (c) 2011, 2012 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    Frank Appel - improved exception handling (bug 340482)
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;


public class ClassUtil_Test {

  private ClassLoader classLoader;

  @Before
  public void setUp() {
    classLoader = ClassUtil_Test.class.getClassLoader();
  }

  @SuppressWarnings( "unchecked" )
  @Test
  public void testNewInstanceWithNullClass() {
    try {
      ClassUtil.newInstance( ( Class )null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testNewInstanceWithAbstractClass() {
    try {
      ClassUtil.newInstance( AbstractClass.class );
      fail();
    } catch( ClassInstantiationException expected ) {
    }
  }

  @Test
  public void testNewInstanceWithInterface() {
    try {
      ClassUtil.newInstance( Runnable.class );
      fail();
    } catch( ClassInstantiationException expected ) {
    }
  }

  @Test
  public void testNewInstanceWithPublicClass() {
    Object instance = ClassUtil.newInstance( ClassWithPrivateConstructor.class );
    assertEquals( instance.getClass(), ClassWithPrivateConstructor.class );
  }

  @Test
  public void testNewInstanceWithPrivateDefaultConstructor() {
    Object instance = ClassUtil.newInstance( PublicClass.class );
    assertEquals( instance.getClass(), PublicClass.class );
  }

  @Test
  public void testNewInstanceWithRuntimeExceptionInConstructor() {
    try {
      ClassUtil.newInstance( ClassWithRuntimeExceptionInConstructor.class );
      fail();
    } catch( ConstructorException expected ) {
    }
  }

  @Test
  public void testNewInstanceWithCheckedExceptionInConstructor() {
    try {
      ClassUtil.newInstance( ClassWithCheckedExceptionInConstructor.class );
      fail();
    } catch( ClassInstantiationException expected ) {
      assertEquals( expected.getCause().getClass(), IOException.class );
    }
  }

  @Test
  public void testNewInstanceWithNullClassNameAndClassLoader() {
    try {
      ClassUtil.newInstance( classLoader, null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testNewInstanceWithNullClassLoader() {
    try {
      ClassUtil.newInstance( null, "" );
      fail();
    } catch( NullPointerException expected ) {
    }
  }

  @Test
  public void testNewInstanceWithNonExistingClassName() {
    try {
      ClassUtil.newInstance( classLoader, "does.not.exist" );
      fail();
    } catch( ClassInstantiationException expected ) {
    }
  }

  @Test
  public void testNewInstanceWithExistingClassName() {
    Object instance = ClassUtil.newInstance( classLoader, PublicClass.class.getName() );
    assertEquals( instance.getClass(), PublicClass.class );
  }


  @Test
  public void testNewInstanceWithPublicParameterizedConstructor() {
    String stringValue = "string";
    Long longValue = new Long( 0 );
    Class[] paramTypes = new Class[] { String.class, Long.class };
    Object[] paramValues = new Object[] { stringValue, longValue };
    Object instance = ClassUtil.newInstance( PublicClass.class, paramTypes, paramValues );
    assertEquals( instance.getClass(), PublicClass.class );
    PublicClass publicClass = ( PublicClass )instance;
    assertEquals( stringValue, publicClass.stringParam );
    assertEquals( longValue, publicClass.longParam );
  }

  @Test
  public void testNewInstanceWithPrivateParameterizedConstructor() {
    Object objectValue = new Object();
    Class[] paramTypes = new Class[] { Object.class };
    Object[] paramValues = new Object[] { objectValue };
    Object instance = ClassUtil.newInstance( PublicClass.class, paramTypes, paramValues );
    assertEquals( instance.getClass(), PublicClass.class );
    PublicClass publicClass = ( PublicClass )instance;
    assertEquals( objectValue, publicClass.objectParam );
  }

  static class ConstructorException extends RuntimeException {
    private static final long serialVersionUID = 1L;
  }

  public static abstract class AbstractClass {
  }

  public static class PublicClass {
    Object objectParam;
    String stringParam;
    Long longParam;
    public PublicClass() {
      this( null ); // avoid unused private constructor marker
    }
    private PublicClass( Object objectParam ) {
      this.objectParam = objectParam;
    }
    public PublicClass( String stringParam, Long longParam ) {
      this.stringParam = stringParam;
      this.longParam = longParam;
    }
  }

  static class ClassWithPrivateConstructor {
    private ClassWithPrivateConstructor() {
    }
  }

  static class ClassWithRuntimeExceptionInConstructor {
    ClassWithRuntimeExceptionInConstructor() {
      throw new ConstructorException();
    }
  }

  static class ClassWithCheckedExceptionInConstructor {
    ClassWithCheckedExceptionInConstructor() throws IOException {
      throw new IOException();
    }
  }

}
