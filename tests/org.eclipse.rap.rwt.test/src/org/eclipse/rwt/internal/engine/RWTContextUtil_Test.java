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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import junit.framework.TestCase;

import org.eclipse.rwt.*;
import org.eclipse.rwt.internal.AdapterFactoryRegistryInstance;
import org.eclipse.rwt.internal.ConfigurationReaderInstance;
import org.eclipse.rwt.internal.branding.BrandingManagerInstance;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.resources.*;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.theme.ThemeAdapterUtil;
import org.eclipse.rwt.internal.theme.ThemeManagerInstance;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.internal.graphics.*;
import org.eclipse.swt.widgets.DisplaysHolder;


public class RWTContextUtil_Test extends TestCase {
  
  public void testRegisterDefaultRWTContext() {
    Fixture.createServiceContext();

    ISessionStore session = ContextProvider.getSession();
    HttpSession httpSession = session.getHttpSession();
    ServletContext servletContext = httpSession.getServletContext();
    RWTContext rwtContext
      = RWTContextUtil.registerDefaultRWTContext( servletContext );

    assertNotNull( rwtContext );
    assertNotNull( getSingleton( ThemeManagerInstance.class ) );
    assertSame( rwtContext.getInstance( ThemeManagerInstance.class ),
                getSingleton( ThemeManagerInstance.class ) );
    assertNotNull( getSingleton( BrandingManagerInstance.class ) );
    assertSame( rwtContext.getInstance( BrandingManagerInstance.class ),
                getSingleton( BrandingManagerInstance.class ) );
    assertNotNull( getSingleton( PhaseListenerRegistryInstance.class ) );
    assertSame( rwtContext.getInstance( PhaseListenerRegistryInstance.class ),
                getSingleton( PhaseListenerRegistryInstance.class ) );
    assertNotNull( getSingleton( LifeCycleFactoryInstance.class ) );
    assertSame( rwtContext.getInstance( LifeCycleFactoryInstance.class ),
                getSingleton( LifeCycleFactoryInstance.class ) );
    assertNotNull( getSingleton( EntryPointManagerInstance.class ) );
    assertSame( rwtContext.getInstance( EntryPointManagerInstance.class ),
                getSingleton( EntryPointManagerInstance.class ) );
    assertNotNull( getSingleton( ResourceFactoryInstance.class ) );
    assertSame( rwtContext.getInstance( ResourceFactoryInstance.class ),
                getSingleton( ResourceFactoryInstance.class ) );
    assertNotNull( getSingleton( ImageFactoryInstance.class ) );
    assertSame( rwtContext.getInstance( ImageFactoryInstance.class ),
                getSingleton( ImageFactoryInstance.class ) );
    assertNotNull( getSingleton( InternalImageFactoryInstance.class ) );
    assertSame( rwtContext.getInstance( InternalImageFactoryInstance.class ),
                getSingleton( InternalImageFactoryInstance.class ) );
    assertNotNull( getSingleton( ImageDataFactoryInstance.class ) );
    assertSame( rwtContext.getInstance( ImageDataFactoryInstance.class ),
                getSingleton( ImageDataFactoryInstance.class ) );
    assertNotNull( getSingleton( FontDataFactoryInstance.class ) );
    assertSame( rwtContext.getInstance( FontDataFactoryInstance.class ),
                getSingleton( FontDataFactoryInstance.class ) );
    assertNotNull( getSingleton( AdapterFactoryRegistryInstance.class ) );
    assertSame( rwtContext.getInstance( AdapterFactoryRegistryInstance.class ),
                getSingleton( AdapterFactoryRegistryInstance.class ) );
    assertNotNull( getSingleton( SettingStoreManagerInstance.class ) );
    assertSame( rwtContext.getInstance( SettingStoreManagerInstance.class ),
                getSingleton( SettingStoreManagerInstance.class ) );
    assertNotNull( getSingleton( ServiceManagerInstance.class ) );
    assertSame( rwtContext.getInstance( ServiceManagerInstance.class ),
                getSingleton( ServiceManagerInstance.class ) );
    assertNotNull( getSingleton( ResourceRegistryInstance.class ) );
    assertSame( rwtContext.getInstance( ResourceRegistryInstance.class ),
                getSingleton( ResourceRegistryInstance.class ) );
    assertNotNull( getSingleton( ConfigurationReaderInstance.class ) );
    assertSame( rwtContext.getInstance( ConfigurationReaderInstance.class ),
                getSingleton( ConfigurationReaderInstance.class ) );
    assertNotNull( getSingleton( ResourceManagerImpl.class ) );
    assertSame( rwtContext.getInstance( ResourceManagerImpl.class ),
                getSingleton( ResourceManagerImpl.class ) );
    assertNotNull( getSingleton( ResourceManager.class ) );
    assertSame( rwtContext.getInstance( ResourceManager.class ),
                getSingleton( ResourceManager.class ) );
    assertNotNull( getSingleton( RWTStartupPageConfigurer.class ) );
    assertSame( rwtContext.getInstance( RWTStartupPageConfigurer.class ),
                getSingleton( RWTStartupPageConfigurer.class ) );
    assertNotNull( getSingleton( StartupPage.class ) );
    assertSame( rwtContext.getInstance( StartupPage.class ),
                getSingleton( StartupPage.class ) );
    assertNotNull( getSingleton( ServiceManagerImpl.class ) );
    assertSame( rwtContext.getInstance( ServiceManagerImpl.class ),
                getSingleton( ServiceManagerImpl.class ) );
    assertNotNull( getSingleton( DisplaysHolder.class ) );
    assertSame( rwtContext.getInstance( DisplaysHolder.class ),
                getSingleton( DisplaysHolder.class ) );
    assertNotNull( getSingleton( ThemeAdapterUtil.class ) );
    assertSame( rwtContext.getInstance( ThemeAdapterUtil.class ),
                getSingleton( ThemeAdapterUtil.class ) );
    
    RWTContextUtil.deregisterRWTContext( servletContext );
    try {
      getSingleton( ThemeManagerInstance.class );
      fail( "After deregistration there must be no context available." );
    } catch( IllegalStateException expected ) {
    }
  }
  
  public void testRegisterRWTContext() {
    TestServletContext servletContext = new TestServletContext();
    RWTContext rwtContext = new RWTContext();
    
    RWTContextUtil.registerRWTContext( servletContext, rwtContext );
    RWTContext found = RWTContextUtil.getRWTContext( servletContext );
    assertSame( rwtContext, found );
    
    RWTContextUtil.deregisterRWTContext( servletContext );
    assertNull( RWTContextUtil.getRWTContext( servletContext ) );
  }
  
  public void testRegisterRWTContextOnSessionStore() {
    SessionStoreImpl sessionStore = new SessionStoreImpl( new TestSession() );
    RWTContext rwtContext = new RWTContext();

    RWTContextUtil.registerRWTContext( sessionStore, rwtContext );
    RWTContext found = RWTContextUtil.getRWTContext( sessionStore );
    assertSame( rwtContext, found );
    
    RWTContextUtil.deregisterRWTContext( sessionStore );
    assertNull( RWTContextUtil.getRWTContext( sessionStore ) );
  }
  
  public void testRunWithInstance() {
    RWTContext rwtContext = new RWTContext();
    final RWTContext[] found = new RWTContext[ 1 ];
    Runnable runnable = new Runnable() {
      public void run() {
        found[0] = RWTContextUtil.getInstance();
      }
    };

    boolean before = RWTContextUtil.hasContext();
    RWTContextUtil.runWithInstance( rwtContext, runnable );
    boolean after = RWTContextUtil.hasContext();

    assertFalse( before );
    assertSame( rwtContext, found[ 0 ] );
    assertFalse( after );
  }

  public void testRunWithInstanceWithException() {
    final RuntimeException expected = new RuntimeException();
    Runnable runnable = new Runnable() {
      public void run() {
        throw expected;
      }
    };
    
    boolean before = RWTContextUtil.hasContext();
    RuntimeException actual = runWithExceptionExpected( runnable );
    boolean after = RWTContextUtil.hasContext();
    
    assertFalse( before );
    assertSame( expected, actual );
    assertFalse( after );
  }
  
  public void testParamRWTContextNotNull() {
    try {
      RWTContextUtil.runWithInstance( null, new Runnable() {
        public void run() {}
      } );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testParamRunnableNotNull() {
    try {
      RWTContextUtil.runWithInstance( new RWTContext(), null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testRunWithInstanceWithNestedCall() {
    final RWTContext rwtContext = new RWTContext();
    Runnable runnable = new Runnable() {
      public void run() {
        RWTContextUtil.runWithInstance( rwtContext, this );
      }
    };
    
    try {
      RWTContextUtil.runWithInstance( rwtContext, runnable );
      fail( "Nested calls in same thread of runWithInstance are not allowed" );
    } catch( IllegalStateException expected ) {
    }
  }
  
  public void testGetInstanceWithoutContextProviderRegistration() {
    try {
      RWTContextUtil.getInstance();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  private static Object getSingleton( Class singletonType ) {
    return RWTContext.getSingleton( singletonType );
  }

  private static RuntimeException runWithExceptionExpected( Runnable runnable ) {
    RuntimeException actual = null;
    try {
      RWTContextUtil.runWithInstance( new RWTContext(), runnable );
      fail();
    } catch( RuntimeException runtimeException ) {
      actual = runtimeException;
    }
    return actual;
  }
}
