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
package org.eclipse.rwt.internal;

import junit.framework.TestCase;

import org.eclipse.rwt.Adaptable;
import org.eclipse.rwt.AdapterFactory;


public class AdapterFactoryRegistry_Test extends TestCase {
  
  private static class TestAdapterFactory implements AdapterFactory {
    public Object getAdapter( Object adaptable, Class adapter ) {
      return null;
    }
    public Class[] getAdapterList() {
      return null;
    }
  }

  private static class TestAdaptable implements Adaptable {
    public Object getAdapter( Class adapter ) {
      return null;
    }
  }

  private static class OtherTestAdaptable implements Adaptable {
    public Object getAdapter( Class adapter ) {
      return null;
    }
  }
  
  private AdapterFactoryRegistry registry;

  public void testRegisterWithNullAdapterFactory() {
    try {
      registry.register( TestAdaptable.class, null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testRegisterWithNullAdaptableClass() {
    AdapterFactory adapterFactory = new TestAdapterFactory();
    try {
      registry.register( null, adapterFactory );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testRegisterAdaptersWithInvalidAdaptableClass() {
    AdapterFactory adapterFactory = new TestAdapterFactory();
    try {
      registry.register( Object.class, adapterFactory );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testRegisterSingleAdapterFactory() {
    registry.register( TestAdaptable.class, new TestAdapterFactory() );
    
    assertEquals( 1, registry.getAdaptableClasses().length );
    assertSame( TestAdaptable.class, registry.getAdaptableClasses()[ 0 ] );
  }

  public void testRegisterMultipleAdapterFactories() {
    TestAdapterFactory adapterFactory1 = new TestAdapterFactory();
    TestAdapterFactory adapterFactory2 = new TestAdapterFactory();
    registry.register( TestAdaptable.class, adapterFactory1 );
    registry.register( TestAdaptable.class, adapterFactory2 );
    
    assertEquals( 1, registry.getAdaptableClasses().length );
    assertSame( TestAdaptable.class, registry.getAdaptableClasses()[ 0 ] );
    AdapterFactory[] adapterFactories = registry.getAdapterFactories( TestAdaptable.class );
    assertEquals( 2, adapterFactories.length );
    assertContains( adapterFactories, adapterFactory1 );
    assertContains( adapterFactories, adapterFactory2 );
  }
  
  public void testRegisterMultipleAdaptables() {
    TestAdapterFactory testAdapterFactory = new TestAdapterFactory();
    TestAdapterFactory otherTestAdapterFactory = new TestAdapterFactory();
    registry.register( TestAdaptable.class, testAdapterFactory );
    registry.register( OtherTestAdaptable.class, otherTestAdapterFactory );
    
    Class[] adaptableClasses = registry.getAdaptableClasses();
    assertEquals( 2, adaptableClasses.length );
    assertContains( adaptableClasses, TestAdaptable.class );
    assertContains( adaptableClasses, OtherTestAdaptable.class );
    AdapterFactory[] adapterFactories = registry.getAdapterFactories( TestAdaptable.class );
    assertEquals( 1, adapterFactories.length );
    assertSame( testAdapterFactory, adapterFactories[ 0 ] );
    adapterFactories = registry.getAdapterFactories( OtherTestAdaptable.class );
    assertEquals( 1, adapterFactories.length );
    assertSame( otherTestAdapterFactory, adapterFactories[ 0 ] );
  }
  
  public void testRegisterSameAdapterFactoryMultipleTimes() {
    TestAdapterFactory adapterFactory = new TestAdapterFactory();
    registry.register( TestAdaptable.class, adapterFactory );
    registry.register( TestAdaptable.class, adapterFactory );
    
    Class[] adaptableClasses = registry.getAdaptableClasses();
    assertEquals( 1, adaptableClasses.length );
    assertSame( TestAdaptable.class, adaptableClasses[ 0 ] );
    AdapterFactory[] adapterFactories = registry.getAdapterFactories( TestAdaptable.class );
    assertEquals( 1, adapterFactories.length );
    assertSame( adapterFactory, adapterFactories[ 0 ] );
  }

  public void testGetAdaptableClassesWhenEmpty() {
    Class[] adaptableClasses = registry.getAdaptableClasses();
    assertEquals( 0, adaptableClasses.length );
  }
  
  public void testGetAdapterFactoriesForNonExistingAdaptable() {
    AdapterFactory[] adapterFactories = registry.getAdapterFactories( Object.class );
    assertEquals( 0, adapterFactories.length );
  }
  
  private static void assertContains( Object[] objects, Object object ) {
    boolean found = false;
    for( int i = 0; !found && i < objects.length; i++ ) {
      if( objects[ i ] == object ) {
        found = true;
      }
    }
    if( !found ) {
      fail( "Object not contained in array: " + object );
    }
  }

  protected void setUp() throws Exception {
    registry = new AdapterFactoryRegistry();
  }
}
