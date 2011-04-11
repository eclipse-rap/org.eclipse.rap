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
import org.eclipse.swt.internal.graphics.TextSizeStorageRegistry.TextSizeStorageRegistryInstance;
import org.eclipse.swt.widgets.DisplaysHolder;


public class ApplicationContextUtil_Test extends TestCase {
  
  public void testRegisterDefaultApplicationContext() {
    Fixture.createServiceContext();

    ISessionStore session = ContextProvider.getSession();
    HttpSession httpSession = session.getHttpSession();
    ServletContext servletContext = httpSession.getServletContext();
    ApplicationContext applicationContext
      = ApplicationContextUtil.registerDefaultApplicationContext( servletContext );

    assertNotNull( applicationContext );
    assertNotNull( getSingleton( ThemeManagerInstance.class ) );
    assertSame( applicationContext.getInstance( ThemeManagerInstance.class ),
                getSingleton( ThemeManagerInstance.class ) );
    assertNotNull( getSingleton( BrandingManagerInstance.class ) );
    assertSame( applicationContext.getInstance( BrandingManagerInstance.class ),
                getSingleton( BrandingManagerInstance.class ) );
    assertNotNull( getSingleton( PhaseListenerRegistryInstance.class ) );
    assertSame( applicationContext.getInstance( PhaseListenerRegistryInstance.class ),
                getSingleton( PhaseListenerRegistryInstance.class ) );
    assertNotNull( getSingleton( LifeCycleFactoryInstance.class ) );
    assertSame( applicationContext.getInstance( LifeCycleFactoryInstance.class ),
                getSingleton( LifeCycleFactoryInstance.class ) );
    assertNotNull( getSingleton( EntryPointManagerInstance.class ) );
    assertSame( applicationContext.getInstance( EntryPointManagerInstance.class ),
                getSingleton( EntryPointManagerInstance.class ) );
    assertNotNull( getSingleton( ResourceFactoryInstance.class ) );
    assertSame( applicationContext.getInstance( ResourceFactoryInstance.class ),
                getSingleton( ResourceFactoryInstance.class ) );
    assertNotNull( getSingleton( ImageFactoryInstance.class ) );
    assertSame( applicationContext.getInstance( ImageFactoryInstance.class ),
                getSingleton( ImageFactoryInstance.class ) );
    assertNotNull( getSingleton( InternalImageFactoryInstance.class ) );
    assertSame( applicationContext.getInstance( InternalImageFactoryInstance.class ),
                getSingleton( InternalImageFactoryInstance.class ) );
    assertNotNull( getSingleton( ImageDataFactoryInstance.class ) );
    assertSame( applicationContext.getInstance( ImageDataFactoryInstance.class ),
                getSingleton( ImageDataFactoryInstance.class ) );
    assertNotNull( getSingleton( FontDataFactoryInstance.class ) );
    assertSame( applicationContext.getInstance( FontDataFactoryInstance.class ),
                getSingleton( FontDataFactoryInstance.class ) );
    assertNotNull( getSingleton( AdapterFactoryRegistryInstance.class ) );
    assertSame( applicationContext.getInstance( AdapterFactoryRegistryInstance.class ),
                getSingleton( AdapterFactoryRegistryInstance.class ) );
    assertNotNull( getSingleton( SettingStoreManagerInstance.class ) );
    assertSame( applicationContext.getInstance( SettingStoreManagerInstance.class ),
                getSingleton( SettingStoreManagerInstance.class ) );
    assertNotNull( getSingleton( ServiceManagerInstance.class ) );
    assertSame( applicationContext.getInstance( ServiceManagerInstance.class ),
                getSingleton( ServiceManagerInstance.class ) );
    assertNotNull( getSingleton( ResourceRegistryInstance.class ) );
    assertSame( applicationContext.getInstance( ResourceRegistryInstance.class ),
                getSingleton( ResourceRegistryInstance.class ) );
    assertNotNull( getSingleton( ConfigurationReaderInstance.class ) );
    assertSame( applicationContext.getInstance( ConfigurationReaderInstance.class ),
                getSingleton( ConfigurationReaderInstance.class ) );
    assertNotNull( getSingleton( ResourceManagerImpl.class ) );
    assertSame( applicationContext.getInstance( ResourceManagerImpl.class ),
                getSingleton( ResourceManagerImpl.class ) );
    assertNotNull( getSingleton( ResourceManager.class ) );
    assertSame( applicationContext.getInstance( ResourceManager.class ),
                getSingleton( ResourceManager.class ) );
    assertNotNull( getSingleton( RWTStartupPageConfigurer.class ) );
    assertSame( applicationContext.getInstance( RWTStartupPageConfigurer.class ),
                getSingleton( RWTStartupPageConfigurer.class ) );
    assertNotNull( getSingleton( StartupPage.class ) );
    assertSame( applicationContext.getInstance( StartupPage.class ),
                getSingleton( StartupPage.class ) );
    assertNotNull( getSingleton( ServiceManagerImpl.class ) );
    assertSame( applicationContext.getInstance( ServiceManagerImpl.class ),
                getSingleton( ServiceManagerImpl.class ) );
    assertNotNull( getSingleton( DisplaysHolder.class ) );
    assertSame( applicationContext.getInstance( DisplaysHolder.class ),
                getSingleton( DisplaysHolder.class ) );
    assertNotNull( getSingleton( ThemeAdapterUtil.class ) );
    assertSame( applicationContext.getInstance( ThemeAdapterUtil.class ),
                getSingleton( ThemeAdapterUtil.class ) );
    assertNotNull( getSingleton( JSLibraryConcatenator.class ) );
    assertSame( applicationContext.getInstance( JSLibraryConcatenator.class ),
                getSingleton( JSLibraryConcatenator.class ) );
    assertNotNull( getSingleton( TextSizeStorageRegistryInstance.class ) );
    assertSame( applicationContext.getInstance( TextSizeStorageRegistryInstance.class ),
                getSingleton( TextSizeStorageRegistryInstance.class ) );
    
    ApplicationContextUtil.deregisterApplicationContext( servletContext );
    try {
      getSingleton( ThemeManagerInstance.class );
      fail( "After deregistration there must be no context available." );
    } catch( IllegalStateException expected ) {
    }
  }
  
  public void testRegisterApplicationContext() {
    TestServletContext servletContext = new TestServletContext();
    ApplicationContext applicationContext = new ApplicationContext();
    
    ApplicationContextUtil.registerApplicationContext( servletContext, applicationContext );
    ApplicationContext found = ApplicationContextUtil.getApplicationContext( servletContext );
    assertSame( applicationContext, found );
    
    ApplicationContextUtil.deregisterApplicationContext( servletContext );
    assertNull( ApplicationContextUtil.getApplicationContext( servletContext ) );
  }
  
  public void testRegisterApplicationContextOnSessionStore() {
    SessionStoreImpl sessionStore = new SessionStoreImpl( new TestSession() );
    ApplicationContext applicationContext = new ApplicationContext();

    ApplicationContextUtil.registerApplicationContext( sessionStore, applicationContext );
    ApplicationContext found = ApplicationContextUtil.getApplicationContext( sessionStore );
    assertSame( applicationContext, found );
    
    ApplicationContextUtil.deregisterApplicationContext( sessionStore );
    assertNull( ApplicationContextUtil.getApplicationContext( sessionStore ) );
  }
  
  public void testRunWithInstance() {
    ApplicationContext applicationContext = new ApplicationContext();
    final ApplicationContext[] found = new ApplicationContext[ 1 ];
    Runnable runnable = new Runnable() {
      public void run() {
        found[0] = ApplicationContextUtil.getInstance();
      }
    };

    boolean before = ApplicationContextUtil.hasContext();
    ApplicationContextUtil.runWithInstance( applicationContext, runnable );
    boolean after = ApplicationContextUtil.hasContext();

    assertFalse( before );
    assertSame( applicationContext, found[ 0 ] );
    assertFalse( after );
  }

  public void testRunWithInstanceWithException() {
    final RuntimeException expected = new RuntimeException();
    Runnable runnable = new Runnable() {
      public void run() {
        throw expected;
      }
    };
    
    boolean before = ApplicationContextUtil.hasContext();
    RuntimeException actual = runWithExceptionExpected( runnable );
    boolean after = ApplicationContextUtil.hasContext();
    
    assertFalse( before );
    assertSame( expected, actual );
    assertFalse( after );
  }
  
  public void testParamApplicationContextNotNull() {
    try {
      ApplicationContextUtil.runWithInstance( null, new Runnable() {
        public void run() {}
      } );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testParamRunnableNotNull() {
    try {
      ApplicationContextUtil.runWithInstance( new ApplicationContext(), null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testRunWithInstanceWithNestedCall() {
    final ApplicationContext applicationContext = new ApplicationContext();
    Runnable runnable = new Runnable() {
      public void run() {
        ApplicationContextUtil.runWithInstance( applicationContext, this );
      }
    };
    
    try {
      ApplicationContextUtil.runWithInstance( applicationContext, runnable );
      fail( "Nested calls in same thread of runWithInstance are not allowed" );
    } catch( IllegalStateException expected ) {
    }
  }
  
  public void testGetInstanceWithoutContextProviderRegistration() {
    try {
      ApplicationContextUtil.getInstance();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }

  private static Object getSingleton( Class singletonType ) {
    return ApplicationContext.getSingleton( singletonType );
  }

  private static RuntimeException runWithExceptionExpected( Runnable runnable ) {
    RuntimeException actual = null;
    try {
      ApplicationContextUtil.runWithInstance( new ApplicationContext(), runnable );
      fail();
    } catch( RuntimeException runtimeException ) {
      actual = runtimeException;
    }
    return actual;
  }
}
