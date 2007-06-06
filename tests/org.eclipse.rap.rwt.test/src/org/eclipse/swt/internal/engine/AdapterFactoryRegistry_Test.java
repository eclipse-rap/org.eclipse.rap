/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.engine;

import java.io.IOException;
import javax.servlet.http.HttpSession;
import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.RWTFixture.TestEntryPoint;
import org.eclipse.swt.internal.lifecycle.EntryPointManager;
import org.eclipse.swt.internal.lifecycle.RWTLifeCycle;
import org.eclipse.swt.internal.theme.ThemeManager;

import com.w4t.Fixture;
import com.w4t.Fixture.*;
import com.w4t.engine.service.ContextProvider;
import com.w4t.util.browser.Ie6up;


public class AdapterFactoryRegistry_Test extends TestCase {
  
  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
    Fixture.fakeBrowser( new Ie6up( true, true ) );
  }
  
  protected void tearDown() throws Exception {
    Fixture.tearDown();
    AdapterFactoryRegistry.clear();
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
    }
    
    try {
      AdapterFactoryRegistry.add( TestAdapterFactory.class, null );
      fail( "Parameter adaptable class must not be null." );
    } catch( final NullPointerException npe ) {
    }
    
    try {
      AdapterFactoryRegistry.add( Object.class, TestAdaptable.class );
      fail( "Parameter factory class must not instance of AdapterFactory." );
    } catch( final IllegalArgumentException iae ) {
    }
    
    try {
      AdapterFactoryRegistry.add( TestAdapterFactory.class, Object.class );
      fail( "Parameter adaptable class must not instance of Adaptable." );
    } catch( final IllegalArgumentException iae ) {
    }
    
    try {
      AdapterFactoryRegistry.add( TestAdapterFactory.class,
                                  TestAdaptable.class );
      fail( "Factory - adaptable pair was already added." );
    } catch( final IllegalArgumentException iae ) {
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
    RWTFixture.registerAdapterFactories();
    RWTFixture.registerResourceManager();
    ThemeManager.getInstance().initialize();
    
    TestAdapterFactory.log = "";
    AdapterFactoryRegistry.add( TestAdapterFactory.class, TestAdaptable.class );
    RWTLifeCycle lifeCycle = new RWTLifeCycle();
    assertEquals( "", TestAdapterFactory.log );
    lifeCycle.execute();
    assertEquals( TestAdapterFactory.CREATED, TestAdapterFactory.log );

    ContextProvider.disposeContext();
    HttpSession session = new TestSession();
    TestResponse response = new TestResponse();
    TestRequest request = new TestRequest();
    request.setSession( session );
    Fixture.fakeContextProvider( response, request );
    Fixture.fakeResponseWriter();
    Fixture.fakeBrowser( new Ie6up( true, true ) );
    RWTFixture.registerAdapterFactories();
  
    TestAdapterFactory.log = "";
    lifeCycle.execute();
    assertEquals( TestAdapterFactory.CREATED, TestAdapterFactory.log );
    
    RWTFixture.deregisterAdapterFactories();
    RWTFixture.deregisterResourceManager();
    EntryPointManager.deregister( EntryPointManager.DEFAULT );
  }
}