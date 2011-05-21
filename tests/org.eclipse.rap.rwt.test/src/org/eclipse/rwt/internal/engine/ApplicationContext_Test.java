/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import junit.framework.TestCase;

import org.eclipse.rwt.internal.engine.ApplicationContext.InstanceTypeFactory;
import org.eclipse.rwt.internal.util.ClassInstantiationException;


public class ApplicationContext_Test extends TestCase {
  
  private static class TestInstanceType {}
  
  private static abstract class AbstractClass  {} 
  
  private static class TestInstanceTypeFactory implements InstanceTypeFactory {
    public Object createInstance() {
      return new TestInstanceType();
    }
    public Class getInstanceType() {
      return TestInstanceType.class;
    }
  }

  private static class WrongTypeFactory implements InstanceTypeFactory {
    public Object createInstance() {
      return new TestInstanceType();
    }
    public Class getInstanceType() {
      return Runnable.class;
    }
  }
  
  public void testGetInstanceWithUnregisterClass() {
    try {
      createAndActivate().getInstance( TestInstanceType.class );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testGetInstanceWithNull() {
    try {
      createAndActivate().getInstance( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testRegisterInstanceTypes() {
    Class[] classes = new Class[] { TestInstanceType.class };
    ApplicationContext applicationContext = createAndActivate( classes );
    Object instance = applicationContext.getInstance( TestInstanceType.class );
    assertTrue( instance instanceof TestInstanceType );
  }
  
  public void testGetInstanceReturnsAlwaysSameInstance() {
    Class[] classes = new Class[] { TestInstanceType.class };
    ApplicationContext applicationContext = createAndActivate( classes );
    Object instance1 = applicationContext.getInstance( TestInstanceType.class );
    Object instance2 = applicationContext.getInstance( TestInstanceType.class );
    assertSame( instance1, instance2 );
  }
  
  public void testRegisterInstanceTypesWithNull() {
    try {
      new ApplicationContext( null );
      fail();
    } catch( NullPointerException expected ) {
    }    
  }

  public void testRegisterAbstractClass() {
    try {
      new ApplicationContext( new Class[] { AbstractClass.class } );
      fail();
    } catch( ClassInstantiationException expected ) {
    }    
  }
  
  public void testRegisterInstanceFactory() {
    ApplicationContext context = createAndActivate( new Class[] { TestInstanceTypeFactory.class } );
    Object instance = context.getInstance( TestInstanceType.class );
    assertTrue( instance instanceof TestInstanceType );
  }
  
  public void testRegisterInstanceTypeFactoryWithNonMatchingType() {
    try {
      new ApplicationContext( new Class[] { WrongTypeFactory.class } );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testDuplicateTypeRegistration() {
    Class[] instanceTypes = new Class[] {
      TestInstanceTypeFactory.class,
      TestInstanceTypeFactory.class
    };
    try {
      createAndActivate( instanceTypes );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testGetInstanceOfNonRegisterType() {
    Class[] classes = new Class[] { TestInstanceType.class };
    ApplicationContext applicationContext = createAndActivate( classes );
    try {
      applicationContext.getInstance( Runnable.class );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  private ApplicationContext createAndActivate() {
    return createAndActivate( new Class[ 0 ] );
  }

  private ApplicationContext createAndActivate( Class[] classes ) {
    ApplicationContext result = new ApplicationContext( classes );
    result.activate();
    return result;
  }
}