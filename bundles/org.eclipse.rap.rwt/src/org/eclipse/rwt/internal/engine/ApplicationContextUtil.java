/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import javax.servlet.ServletContext;

import org.eclipse.rwt.internal.AdapterFactoryRegistryInstance;
import org.eclipse.rwt.internal.ConfigurationReader;
import org.eclipse.rwt.internal.branding.BrandingManager;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.resources.*;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.theme.ThemeAdapterManager;
import org.eclipse.rwt.internal.theme.ThemeManagerHolder;
import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.internal.graphics.*;
import org.eclipse.swt.widgets.DisplaysHolder;


public class ApplicationContextUtil {
  private final static ThreadLocal CONTEXT_HOLDER = new ThreadLocal();
  private final static String ATTRIBUTE_APPLICATION_CONTEXT
    = ApplicationContext.class.getName() + "#instance";
 
  private static final Class[] INSTANCE_TYPES = new Class[] {
    ApplicationStoreImpl.class,
    ThemeManagerHolder.class,
    ResourceManagerImpl.class,
    ResourceManager.class,
    BrandingManager.class,
    PhaseListenerRegistry.class,
    LifeCycleFactory.class,
    EntryPointManager.class,
    ResourceFactoryInstance.class,
    ImageFactory.class,
    InternalImageFactory.class,
    ImageDataFactory.class,
    FontDataFactory.class,
    AdapterFactoryRegistryInstance.class,
    SettingStoreManager.class,
    StartupPageConfigurer.class,
    StartupPage.class,
    ServiceManager.class,
    ConfigurationReader.class,
    ResourceRegistryInstance.class,
    DisplaysHolder.class,
    ThemeAdapterManager.class,
    JSLibraryConcatenator.class,
    TextSizeStorageRegistry.class,
  };

  public static ApplicationContext registerDefaultApplicationContext( ServletContext context ) {
    ApplicationContext result = createApplicationContext();
    registerApplicationContext( context, result );
    return result;
  }

  public static ApplicationContext createApplicationContext() {
    return new ApplicationContext( INSTANCE_TYPES );
  }

  public static void registerApplicationContext( ServletContext servletContext,
                                                 ApplicationContext applicationContext )
  {
    servletContext.setAttribute( ATTRIBUTE_APPLICATION_CONTEXT, applicationContext );
  }

  public static ApplicationContext getApplicationContext( ServletContext servletContext ) {
    return ( ApplicationContext )servletContext.getAttribute( ATTRIBUTE_APPLICATION_CONTEXT );
  }

  public static void deregisterApplicationContext( ServletContext servletContext ) {
    servletContext.removeAttribute( ATTRIBUTE_APPLICATION_CONTEXT );
    ContextProvider.disposeContext();
  }
  
  public static void registerApplicationContext( ISessionStore sessionStore,
                                                 ApplicationContext applicationContext )
  {
    sessionStore.setAttribute( ATTRIBUTE_APPLICATION_CONTEXT, applicationContext );
  }

  public static ApplicationContext getApplicationContext( ISessionStore sessionStore ) {
    return ( ApplicationContext )sessionStore.getAttribute( ATTRIBUTE_APPLICATION_CONTEXT );
  }

  public static void deregisterApplicationContext( ISessionStore sessionStore ) {
    sessionStore.removeAttribute( ATTRIBUTE_APPLICATION_CONTEXT );
  }
  
  public static ApplicationContext getInstance() {
    ApplicationContext result = ( ApplicationContext )CONTEXT_HOLDER.get();
    if( result == null  ) {
      ServiceContext context = ContextProvider.getContext();
      result = context.getApplicationContext();
    }
    checkApplicationContextExists( result );
    return result;
  }

  public static void runWithInstance( ApplicationContext applicationContext, Runnable runnable ) {
    ParamCheck.notNull( applicationContext, "applicationContext" );
    ParamCheck.notNull( runnable, "runnable" );
    checkNestedCall();
    CONTEXT_HOLDER.set( applicationContext );
    try {
      runnable.run();
    } finally {
      CONTEXT_HOLDER.set( null );
    }
  }

  // TODO [ApplicationContext]: method is used by Fixture for performance speed up of test suite. 
  //      Think about a less intrusive solution.
  public static void replace( Class instanceType, Class replacementType ) {
    for( int i = 0; i < INSTANCE_TYPES.length; i++ ) {
      if( INSTANCE_TYPES[ i ] == instanceType ) {
        INSTANCE_TYPES[ i ] = replacementType;
      }
    }
  }

  private static void checkNestedCall() {
    if( CONTEXT_HOLDER.get() != null ) {
      String msg = "Nested call of runWithInstance detected.";
      throw new IllegalStateException( msg );
    }
  }

  private static void checkApplicationContextExists( ApplicationContext applicationContext ) {
    if( applicationContext == null ) {
      throw new IllegalStateException( "No ApplicationContext registered." );
    }
  }

  static boolean hasContext() {
    return CONTEXT_HOLDER.get() != null;
  }
}
