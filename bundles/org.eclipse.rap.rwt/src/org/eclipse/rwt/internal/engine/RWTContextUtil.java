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

import org.eclipse.rwt.internal.AdapterFactoryRegistryInstance;
import org.eclipse.rwt.internal.ConfigurationReaderInstance;
import org.eclipse.rwt.internal.branding.BrandingManagerInstance;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.resources.*;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.internal.theme.ThemeAdapterUtil;
import org.eclipse.rwt.internal.theme.ThemeManagerInstance;
import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.internal.graphics.*;
import org.eclipse.swt.widgets.DisplaysHolder;


public class RWTContextUtil {
  private final static ThreadLocal CONTEXT_HOLDER = new ThreadLocal();
  private final static String ATTRIBUTE_RWT_CONTEXT
    = RWTContext.class.getName() + "#RWTContext";
 
  private static final Class[] INSTANCE_TYPES = new Class[] {
    ThemeManagerInstance.class,
    ResourceManagerImpl.class,
    ResourceManager.class,
    BrandingManagerInstance.class,
    PhaseListenerRegistryInstance.class,
    LifeCycleFactoryInstance.class,
    EntryPointManagerInstance.class,
    ResourceFactoryInstance.class,
    ImageFactoryInstance.class,
    InternalImageFactoryInstance.class,
    ImageDataFactoryInstance.class,
    FontDataFactoryInstance.class,
    AdapterFactoryRegistryInstance.class,
    SettingStoreManagerInstance.class,
    RWTStartupPageConfigurer.class,
    StartupPage.class,
    ServiceManagerInstance.class,
    ConfigurationReaderInstance.class,
    ResourceRegistryInstance.class,
    ServiceManagerImpl.class,
    DisplaysHolder.class,
    ThemeAdapterUtil.class
  };

  public static RWTContext registerDefaultRWTContext( ServletContext context ) {
    RWTContext result = createRWTContext();
    registerRWTContext( context, result );
    return result;
  }

  public static RWTContext createRWTContext() {
    RWTContext result = new RWTContext();
    result.registerInstanceTypes( INSTANCE_TYPES );
    return result;
  }

  public static void registerRWTContext( ServletContext servletContext,
                                         RWTContext rwtContext )
  {
    servletContext.setAttribute( ATTRIBUTE_RWT_CONTEXT, rwtContext );
  }

  public static RWTContext getRWTContext( ServletContext servletContext ) {
    return ( RWTContext )servletContext.getAttribute( ATTRIBUTE_RWT_CONTEXT );
  }

  public static void deregisterRWTContext( ServletContext servletContext ) {
    servletContext.removeAttribute( ATTRIBUTE_RWT_CONTEXT );
    ContextProvider.disposeContext();
  }
  
  public static void registerRWTContext( ISessionStore sessionStore,
                                         RWTContext rwtContext )
  {
    sessionStore.setAttribute( ATTRIBUTE_RWT_CONTEXT, rwtContext );
  }

  public static RWTContext getRWTContext( ISessionStore sessionStore ) {
    return ( RWTContext )sessionStore.getAttribute( ATTRIBUTE_RWT_CONTEXT );
  }

  public static void deregisterRWTContext( ISessionStore sessionStore ) {
    sessionStore.removeAttribute( ATTRIBUTE_RWT_CONTEXT );
  }
  
  public static RWTContext getInstance() {
    RWTContext result = ( RWTContext )CONTEXT_HOLDER.get();
    if( result == null  ) {
      ServiceContext context = ContextProvider.getContext();
      result = context.getRWTContext();
    }
    checkRWTContextExists( result );
    return result;
  }

  public static void runWithInstance( RWTContext rwtContext,
                                      Runnable runnable )
  {
    ParamCheck.notNull( rwtContext, "rwtContext" );
    ParamCheck.notNull( runnable, "runnable" );
    checkNestedCall();
    CONTEXT_HOLDER.set( rwtContext );
    try {
      runnable.run();
    } finally {
      CONTEXT_HOLDER.set( null );
    }
  }

  // TODO [RWTContext]: method is used by Fixture for performance speed up
  //                    of test suite. Think about a less intrusive solution.
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

  private static void checkRWTContextExists( RWTContext rwtContext ) {
    if( rwtContext == null ) {
      throw new IllegalStateException( "No RWTContext registered." );
    }
  }

  static boolean hasContext() {
    return CONTEXT_HOLDER.get() != null;
  }
}
