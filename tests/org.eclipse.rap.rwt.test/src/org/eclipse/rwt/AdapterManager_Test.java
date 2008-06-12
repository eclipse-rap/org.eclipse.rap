/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt;

import junit.framework.TestCase;

import org.eclipse.rwt.internal.AdapterManager;
import org.eclipse.rwt.internal.AdapterManagerImpl;



public class AdapterManager_Test extends TestCase {

  public interface Concealer {
    // empty, used only for test case
  }
  
  protected void setUp() throws Exception {
    Fixture.setUp();
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testAdapterManager() throws Exception {
    
    AdapterFactory adapterFactory = new AdapterFactory() {
      public Object getAdapter( final Object adaptable, 
                                final Class adapter ) {
        IDummyType dummy = ( IDummyType )adaptable;
        return new DummyAdapter( dummy );
      }
      public Class[] getAdapterList() {
        return new Class[] { IDummyAdapter.class };
      }
    };
    
    AdapterManager manager = AdapterManagerImpl.getInstance();
    manager.registerAdapters( adapterFactory, IDummyType.class );
    
    Dummy dummy = new Dummy();
    Object adapter = dummy.getAdapter( IDummyAdapter.class );
    assertTrue( adapter != null );
    assertTrue( adapter instanceof IDummyAdapter );
    adapter = dummy.getAdapter( Concealer.class );
    assertTrue( adapter == null );    
    

    AdapterFactory adapterFactory2 = new AdapterFactory() {
      public Object getAdapter( final Object adaptable, 
                                final Class adapter ) {
        IDummyType dummy = ( IDummyType )adaptable;
        Object result = null;
        if( adapter == IDummyAdapter2.class ) {
          result = new DummyAdapter2( dummy );
        } else if( adapter == Concealer.class ) {
          result = new Concealer(){ 
          };
        }
        return result;
      }
      public Class[] getAdapterList() {
        return new Class[] { IDummyAdapter2.class, Concealer.class };
      }
    };
    manager.registerAdapters( adapterFactory2, IDummyType.class );

    adapter = dummy.getAdapter( IDummyAdapter2.class );
    assertTrue( adapter != null );
    assertTrue( adapter instanceof IDummyAdapter2 );
    adapter = dummy.getAdapter( Concealer.class );
    assertTrue( adapter != null );
    assertTrue( adapter instanceof Concealer );
    
    manager.deregisterAdapters( adapterFactory, IDummyType.class );
    adapter = dummy.getAdapter( IDummyAdapter.class );
    assertTrue( adapter == null );
    adapter = dummy.getAdapter( Concealer.class );
    assertTrue( adapter != null );
    assertTrue( adapter instanceof Concealer );
    
  }
  
  /////////////
  // test types
  
  class Dummy implements IDummyType, Adaptable {
    public void doNothing() {
    }
    public Object getAdapter( final Class adapter ) {
      return AdapterManagerImpl.getInstance().getAdapter( this, adapter );
    }
  }
  
  class DummyAdapter implements IDummyAdapter {
    DummyAdapter( final IDummyType dummy ) {}
    public void doAnyThing() {}
  }
  
  class DummyAdapter2 implements IDummyAdapter2 {
    DummyAdapter2( final IDummyType dummy ) {}
    public void doSomesThing() {}
  }
  
  interface IDummyAdapter {
    void doAnyThing();
  }
  
  interface IDummyAdapter2 {
    void doSomesThing();
  }

  interface IDummyType {
    void doNothing();
  }
}