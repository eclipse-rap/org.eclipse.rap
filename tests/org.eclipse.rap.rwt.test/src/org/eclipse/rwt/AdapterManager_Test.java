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

import org.eclipse.rwt.internal.AdapterManager;
import org.eclipse.rwt.internal.AdapterManagerImpl;


public class AdapterManager_Test extends TestCase {

  private AdapterManager adapterManager;
  private DummyType dummy;

  protected void setUp() {
    Fixture.createApplicationContext();
    Fixture.createServiceContext();
    adapterManager = AdapterManagerImpl.getInstance();
    dummy = new DummyType();
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testRegisterSingleAdapterFactory() {
    AdapterFactory adapterFactory = new TestAdapterFactory1();
    adapterManager.registerAdapters( adapterFactory, IDummyType.class );
    
    Object adapter1 = dummy.getAdapter( IDummyAdapter1.class );
    Object adapter3 = dummy.getAdapter( IDummyAdapter3.class );

    assertTrue( adapter1 instanceof IDummyAdapter1 );
    assertNull( adapter3 );    
  }
  
  public void testRegisterMultipleAdapterFactories() {
    AdapterFactory adapterFactory1 = new TestAdapterFactory1();
    adapterManager.registerAdapters( adapterFactory1, IDummyType.class );
    AdapterFactory adapterFactory2 = new TestAdapterFactory2();
    adapterManager.registerAdapters( adapterFactory2, IDummyType.class );
    
    Object adapter2 = dummy.getAdapter( IDummyAdapter2.class );
    Object adapter3 = dummy.getAdapter( IDummyAdapter3.class );
    
    assertTrue( adapter2 instanceof IDummyAdapter2 );
    assertTrue( adapter3 instanceof IDummyAdapter3 );
  }
  
  public void testDeregister() {
    TestAdapterFactory1 adapterFactory1 = new TestAdapterFactory1();
    adapterManager.registerAdapters( adapterFactory1, IDummyType.class );
    adapterManager.registerAdapters( new TestAdapterFactory2(), IDummyType.class );

    adapterManager.deregisterAdapters( adapterFactory1, IDummyType.class );
    Object dummyAdapter1 = dummy.getAdapter( IDummyAdapter1.class );
    Object dummyAdapter2 = dummy.getAdapter( IDummyAdapter2.class );
    Object dummyAdapter3 = dummy.getAdapter( IDummyAdapter3.class );

    assertNull( dummyAdapter1 );
    assertNotNull( dummyAdapter2 );
    assertNotNull( dummyAdapter3 );
    assertTrue( dummyAdapter3 instanceof IDummyAdapter3 );
  }
  
  /////////////
  // test types
  
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
  
  private interface IDummyType {
    void doNothing();
  }

  private static class DummyType implements IDummyType, Adaptable {
    public void doNothing() {
    }
    public Object getAdapter( Class adapter ) {
      return AdapterManagerImpl.getInstance().getAdapter( this, adapter );
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