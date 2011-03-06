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

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.service.ContextProvider;
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
  
  public void testRegistration() {
    AdapterFactoryRegistry.add( TestAdapterFactory.class, TestAdaptable.class );
    AdapterFactoryRegistry.register();
    TestAdaptable adaptable = new TestAdaptable();
    Runnable runnable = ( Runnable )adaptable.getAdapter( Runnable.class );
    assertNotNull( runnable );
    
    try {
      AdapterFactoryRegistry.add( null, TestAdaptable.class );
      fail( "Parameter factory class must not be null." );
    } catch( final NullPointerException npe ) {
      // expected
    }
    
    try {
      AdapterFactoryRegistry.add( TestAdapterFactory.class, null );
      fail( "Parameter adaptable class must not be null." );
    } catch( final NullPointerException npe ) {
      // expected
    }
    
    try {
      AdapterFactoryRegistry.add( Object.class, TestAdaptable.class );
      fail( "Parameter factory class must not instance of AdapterFactory." );
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
    
    try {
      AdapterFactoryRegistry.add( TestAdapterFactory.class, Object.class );
      fail( "Parameter adaptable class must not instance of Adaptable." );
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
    
    try {
      AdapterFactoryRegistry.add( TestAdapterFactory.class,
                                  TestAdaptable.class );
      fail( "Factory - adaptable pair was already added." );
    } catch( final IllegalArgumentException iae ) {
      // expected
    }
  }
  
  public void testAdapterFactoryCreation() throws IOException {
    // [fappel]:
    // AdapterFactories are used in Session scope but the RWTLifeCycle which
    // is responsible for factory creation has application scope.
    // This tests that each session becomes its own instance of a particular
    // AdapterFactory implementation.
    EntryPointManager.register( EntryPointManager.DEFAULT,
                                TestEntryPoint.class );
    TestAdapterFactory.log = "";
    AdapterFactoryRegistry.add( TestAdapterFactory.class, TestAdaptable.class );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )LifeCycleFactory.getLifeCycle();
    assertEquals( "", TestAdapterFactory.log );
    ThemeManager.getInstance().initialize();
    lifeCycle.execute();
    assertEquals( TestAdapterFactory.CREATED, TestAdapterFactory.log );

    ContextProvider.disposeContext();
    Fixture.createServiceContext();
    Fixture.fakeResponseWriter();
    
    TestAdapterFactory.log = "";
    lifeCycle.execute();
    assertEquals( TestAdapterFactory.CREATED, TestAdapterFactory.log );
  }

  protected void setUp() throws Exception {
    Fixture.createRWTContext();
    Fixture.createServiceContext();
    Fixture.fakeNewRequest();
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }

}