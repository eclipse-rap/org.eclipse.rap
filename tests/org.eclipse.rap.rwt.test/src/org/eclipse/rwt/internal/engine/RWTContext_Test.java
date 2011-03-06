/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import junit.framework.TestCase;

import org.eclipse.rwt.internal.engine.RWTContext.InstanceTypeFactory;


public class RWTContext_Test extends TestCase {
  
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
      new RWTContext().getInstance( TestInstanceType.class );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testGetInstanceWithNull() {
    try {
      new RWTContext().getInstance( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testRegisterInstanceTypes() {
    RWTContext rwtContext = new RWTContext();
    rwtContext.registerInstanceTypes( new Class[] { TestInstanceType.class } );
    
    Object instance = rwtContext.getInstance( TestInstanceType.class );

    assertTrue( instance instanceof TestInstanceType );
  }
  
  public void testGetInstanceReturnsAlwaysSameInstance() {
    RWTContext rwtContext = new RWTContext();
    rwtContext.registerInstanceTypes( new Class[] { TestInstanceType.class } );
    
    Object instance1 = rwtContext.getInstance( TestInstanceType.class );
    Object instance2 = rwtContext.getInstance( TestInstanceType.class );
    
    assertSame( instance1, instance2 );
  }
  
  public void testRegisterInstanceTypesWithNull() {
    try {
      new RWTContext().registerInstanceTypes( null );
      fail();
    } catch( NullPointerException expected ) {
    }    
  }

  public void testRegisterAbstractClass() {
    try {
      Class[] instanceTypes = new Class[] { AbstractClass.class };
      new RWTContext().registerInstanceTypes( instanceTypes );
      fail();
    } catch( IllegalArgumentException expected ) {
    }    
  }
  
  public void testOneTimeOnlyRegistration() {
    RWTContext rwtContext = new RWTContext();
    Class[] instanceTypes = new Class[] { TestInstanceType.class };
    rwtContext.registerInstanceTypes( instanceTypes );

    try {
      rwtContext.registerInstanceTypes( instanceTypes );
      fail();
    } catch( IllegalStateException expected ) {
    }
  }
  
  public void testRegisterInstanceFactory() {
    RWTContext context = new RWTContext();
    Class[] instanceTypes = new Class[] { TestInstanceTypeFactory.class };
    context.registerInstanceTypes( instanceTypes );
    
    Object instance = context.getInstance( TestInstanceType.class );

    assertTrue( instance instanceof TestInstanceType );
  }
  
  public void testRegisterInstanceTypeFactoryWithNonMatchingType() {
    RWTContext rwtContext = new RWTContext();
    
    try {
      Class type = WrongTypeFactory.class;
      rwtContext.registerInstanceTypes( new Class[] { type } );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testDuplicateTypeRegistration() {
    RWTContext rwtContext = new RWTContext();
    Class[] instanceTypes = new Class[]{
      TestInstanceTypeFactory.class,
      TestInstanceTypeFactory.class
    };
    
    try {
      rwtContext.registerInstanceTypes( instanceTypes );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testGetInstanceOfNonRegisterType() {
    RWTContext rwtContext = new RWTContext();
    rwtContext.registerInstanceTypes( new Class[] { TestInstanceType.class } );

    try {
      rwtContext.getInstance( Runnable.class );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
}