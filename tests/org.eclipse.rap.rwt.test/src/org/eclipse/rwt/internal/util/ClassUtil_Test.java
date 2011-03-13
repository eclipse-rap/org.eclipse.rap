/*******************************************************************************
 * Copyright (c) 2011 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.util;

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;


public class ClassUtil_Test extends TestCase {
  
  private ClassLoader classLoader;
  
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
  
  static class ClassWithExceptionInConstructor {
    ClassWithExceptionInConstructor() {
      throw new ConstructorException();
    }
  }
  
  public void testNewInstanceWithNullClass() {
    try {
      ClassUtil.newInstance( ( Class )null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testNewInstanceWithAbstractClass() {
    try {
      ClassUtil.newInstance( AbstractClass.class );
      fail();
    } catch( ClassInstantiationException expected ) {
    }
  }
  
  public void testNewInstanceWithInterface() {
    try {
      ClassUtil.newInstance( Runnable.class );
      fail();
    } catch( ClassInstantiationException expected ) {
    }
  }
  
  public void testNewInstanceWithPublicClass() {
    Object instance = ClassUtil.newInstance( ClassWithPrivateConstructor.class );
    assertEquals( instance.getClass(), ClassWithPrivateConstructor.class );
  }
  
  public void testNewInstanceWithPrivateDefaultConstructor() {
    Object instance = ClassUtil.newInstance( PublicClass.class );
    assertEquals( instance.getClass(), PublicClass.class );
  }
  
  public void testNewInstanceWithEceptionInConstructor() {
    try {
      ClassUtil.newInstance( ClassWithExceptionInConstructor.class );
      fail();
    } catch( ClassInstantiationException e ) {
      assertEquals( e.getCause().getClass(), InvocationTargetException.class );
    }
  }
  
  public void testNewInstanceWithNullClassNameAndClassLoader() {
    try {
      ClassUtil.newInstance( classLoader, null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testNewInstanceWithNullClassLoader() {
    try {
      ClassUtil.newInstance( null, "" );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testNewInstanceWithNonExistingClassName() {
    try {
      ClassUtil.newInstance( classLoader, "does.not.exist" );
      fail();
    } catch( ClassInstantiationException expected ) {
    }
  }
  
  public void testNewInstanceWithExistingClassName() {
    Object instance = ClassUtil.newInstance( classLoader, PublicClass.class.getName() );
    assertEquals( instance.getClass(), PublicClass.class );
  }
  
  
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
  
  public void testNewInstanceWithPrivateParameterizedConstructor() {
    Object objectValue = new Object();
    Class[] paramTypes = new Class[] { Object.class };
    Object[] paramValues = new Object[] { objectValue };
    Object instance = ClassUtil.newInstance( PublicClass.class, paramTypes, paramValues );
    assertEquals( instance.getClass(), PublicClass.class );
    PublicClass publicClass = ( PublicClass )instance;
    assertEquals( objectValue, publicClass.objectParam );
  }
  
  protected void setUp() throws Exception {
    classLoader = ClassUtil_Test.class.getClassLoader();
  }
}
