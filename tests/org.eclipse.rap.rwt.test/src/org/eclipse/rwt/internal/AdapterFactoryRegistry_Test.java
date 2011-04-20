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
package org.eclipse.rwt.internal;

import junit.framework.TestCase;

import org.eclipse.rwt.*;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.LifeCycleServiceHandler;
import org.eclipse.rwt.internal.theme.ThemeManager;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.widgets.Display;



public class AdapterFactoryRegistry_Test extends TestCase {
  
  public static class TestEntryPoint implements IEntryPoint {
    public int createUI() {
      Display display = new Display();
      if( display.readAndDispatch() ) {
        display.sleep();
      }
      return 0;
    }
  }
  
  public void testAdd() {
    RWTFactory.getAdapterFactoryRegistry().add( TestAdapterFactory.class, TestAdaptable.class );
    RWTFactory.getAdapterFactoryRegistry().register();
    TestAdaptable adaptable = new TestAdaptable();
    Runnable runnable = ( Runnable )adaptable.getAdapter( Runnable.class );
    assertNotNull( runnable );
  }
  
  public void testAddWithNullFactoryClass() {
    try {
      RWTFactory.getAdapterFactoryRegistry().add( null, TestAdaptable.class );
      fail( "Parameter factory class must not be null." );
    } catch( NullPointerException expected ) {
    }
  }

  public void testAddWithNullAdaptableClass() {
    try {
      RWTFactory.getAdapterFactoryRegistry().add( TestAdapterFactory.class, null );
      fail( "Parameter adaptable class must not be null." );
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testAddWithInvalidFactoryClass() {
    try {
      RWTFactory.getAdapterFactoryRegistry().add( Object.class, TestAdaptable.class );
      fail( "Parameter factory class must not instance of AdapterFactory." );
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testAddWithInvalidAdaptableClass() {
    try {
      RWTFactory.getAdapterFactoryRegistry().add( TestAdapterFactory.class, Object.class );
      fail( "Parameter adaptable class is not an instance of Adaptable." );
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testAddWithExistingAdaptable() {
    RWTFactory.getAdapterFactoryRegistry().add( TestAdapterFactory.class, TestAdaptable.class );
    try {
      RWTFactory.getAdapterFactoryRegistry().add( TestAdapterFactory.class, TestAdaptable.class );
      fail( "Factory - adaptable pair was already added." );
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testAdapterFactoryCreation() throws Exception {
    // [fappel]:
    // AdapterFactories are used in Session scope but the RWTLifeCycle which is responsible for 
    // factory creation has application scope.
    // This tests that each session gets its own instance of a particular AdapterFactory 
    // implementation.
    RWTFactory.getEntryPointManager().register( EntryPointManager.DEFAULT, TestEntryPoint.class );
    TestAdapterFactory.log = "";
    RWTFactory.getAdapterFactoryRegistry().add( TestAdapterFactory.class, TestAdaptable.class );
    assertEquals( "", TestAdapterFactory.log );
    Fixture.fakeNewRequest();
    Fixture.fakeResponseWriter();
    Fixture.fakeRequestParam( LifeCycleServiceHandler.RWT_INITIALIZE, "true" );
    ThemeManager.getInstance().initialize();
    new LifeCycleServiceHandler().service();
    assertEquals( TestAdapterFactory.CREATED, TestAdapterFactory.log );

    ContextProvider.disposeContext();
    Fixture.createServiceContext();
    Fixture.fakeNewRequest();
    Fixture.fakeRequestParam( LifeCycleServiceHandler.RWT_INITIALIZE, "true" );
    Fixture.fakeResponseWriter();
    TestAdapterFactory.log = "";
    new LifeCycleServiceHandler().service();
    assertEquals( TestAdapterFactory.CREATED, TestAdapterFactory.log );
  }

  protected void setUp() throws Exception {
    Fixture.createApplicationContext();
    Fixture.createServiceContext();
    Fixture.fakeNewRequest();
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
}
