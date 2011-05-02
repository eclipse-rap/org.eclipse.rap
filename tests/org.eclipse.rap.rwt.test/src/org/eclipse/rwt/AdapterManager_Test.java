/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt;

import junit.framework.TestCase;

import org.eclipse.rwt.internal.*;


public class AdapterManager_Test extends TestCase {

  private AdapterManager adapterManager;
  private DummyType dummy;

  protected void setUp() {
    adapterManager = new AdapterManagerImpl();
    dummy = new DummyType( adapterManager );
  }
  
  public void testRegisterWithNullAdapterFactory() {
    try {
      adapterManager.registerAdapters( null, DummyType.class );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testRegisterWithNullAdaptableClass() {
    AdapterFactory adapterFactory = new TestAdapterFactory1();
    try {
      adapterManager.registerAdapters( adapterFactory, null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testRegisterWithInvalidAdaptableClass() {
    AdapterFactory adapterFactory = new TestAdapterFactory1();
    try {
      adapterManager.registerAdapters( adapterFactory, Object.class );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testGetAdapterWithNoAdapterFactory() {
    Object adapter1 = adapterManager.getAdapter( dummy, IDummyAdapter1.class );
    assertNull( adapter1 );
  }

  public void testGetAdapterWithSingleAdapterFactory() {
    AdapterFactory adapterFactory = new TestAdapterFactory1();
    adapterManager.registerAdapters( adapterFactory, IDummyType.class );
    
    Object adapter1 = adapterManager.getAdapter( dummy, IDummyAdapter1.class );
    Object adapter3 = adapterManager.getAdapter( dummy, IDummyAdapter3.class );

    assertTrue( adapter1 instanceof IDummyAdapter1 );
    assertNull( adapter3 );    
  }
  
  public void testGetAdapterDoesNotBufferAdapters() {
    LoggingAdapterFactory adapterFactory = new LoggingAdapterFactory();
    adapterManager.registerAdapters( adapterFactory, IDummyType.class );

    adapterManager.getAdapter( dummy, IDummyAdapter1.class );
    adapterFactory.resetLog();
    adapterManager.getAdapter( dummy, IDummyAdapter1.class );
    
    assertSame( dummy, adapterFactory.adaptable );
    assertSame( IDummyAdapter1.class, adapterFactory.adapter );
  }
  
  public void testGetAdapterWithMultipleAdapterFactories() {
    AdapterFactory adapterFactory1 = new TestAdapterFactory1();
    adapterManager.registerAdapters( adapterFactory1, IDummyType.class );
    AdapterFactory adapterFactory2 = new TestAdapterFactory2();
    adapterManager.registerAdapters( adapterFactory2, IDummyType.class );
    
    Object adapter2 = adapterManager.getAdapter( dummy, IDummyAdapter2.class );
    Object adapter3 = adapterManager.getAdapter( dummy, IDummyAdapter3.class );
    
    assertTrue( adapter2 instanceof IDummyAdapter2 );
    assertTrue( adapter3 instanceof IDummyAdapter3 );
  }
  
  /////////////
  // test types
  
  private static class LoggingAdapterFactory implements AdapterFactory {
  
    Object adaptable;
    Class adapter;
    
    void resetLog() {
      adaptable = null;
      adapter = null;
    }
    
    public Object getAdapter( Object adaptable, Class adapter ) {
      this.adaptable = adaptable;
      this.adapter = adapter;
      return new DummyAdapter1();
    }
  
    public Class[] getAdapterList() {
      return new Class[] { IDummyAdapter1.class };
    }
  }

  private static class TestAdapterFactory1 implements AdapterFactory {
    
    public Object getAdapter( Object adaptable, Class adapter ) {
      return new DummyAdapter1();
    }
    
    public Class[] getAdapterList() {
      return new Class[] { IDummyAdapter1.class };
    }
  }
  
  private static class TestAdapterFactory2 implements AdapterFactory {

    public Object getAdapter( Object adaptable, Class adapter ) {
      Object result = null;
      if( adapter == IDummyAdapter2.class ) {
        result = new DummyAdapter2();
      } else if( adapter == IDummyAdapter3.class ) {
        result = new IDummyAdapter3(){ 
        };
      }
      return result;
    }

    public Class[] getAdapterList() {
      return new Class[] { IDummyAdapter2.class, IDummyAdapter3.class };
    }
  }

  private interface IDummyAdapter1 {
    void doAnything();
  }

  private interface IDummyAdapter2 {
    void doSomething();
  }

  private interface IDummyAdapter3 {
    // empty, used only for test case
  }
  
  private interface IDummyType extends Adaptable {
    void doNothing();
  }

  private static class DummyType implements IDummyType {
    private final AdapterManager adapterManager;
    public DummyType( AdapterManager adapterManager ) {
      this.adapterManager = adapterManager;
    }
    public void doNothing() {
    }
    public Object getAdapter( Class adapter ) {
      return adapterManager.getAdapter( this, adapter );
    }
  }
  
  private static class DummyAdapter1 implements IDummyAdapter1 {
    public void doAnything() {
    }
  }
  
  static class DummyAdapter2 implements IDummyAdapter2 {
    public void doSomething() {
    }
  }
}