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

import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.ServiceContext;
import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.service.ISessionStore;


public class ApplicationContextUtil {
  private final static ThreadLocal CONTEXT_HOLDER = new ThreadLocal();
  private final static String ATTRIBUTE_APPLICATION_CONTEXT
    = ApplicationContext.class.getName() + "#INSTANCE";
 
  public static void set( ServletContext servletContext, ApplicationContext applicationContext ) {
    servletContext.setAttribute( ATTRIBUTE_APPLICATION_CONTEXT, applicationContext );
  }

  public static ApplicationContext get( ServletContext servletContext ) {
    return ( ApplicationContext )servletContext.getAttribute( ATTRIBUTE_APPLICATION_CONTEXT );
  }

  public static void remove( ServletContext servletContext ) {
    servletContext.removeAttribute( ATTRIBUTE_APPLICATION_CONTEXT );
  }
  
  public static void set( ISessionStore sessionStore, ApplicationContext applicationContext ) {
    sessionStore.setAttribute( ATTRIBUTE_APPLICATION_CONTEXT, applicationContext );
  }

  public static ApplicationContext get( ISessionStore sessionStore ) {
    return ( ApplicationContext )sessionStore.getAttribute( ATTRIBUTE_APPLICATION_CONTEXT );
  }

  public static void remove( ISessionStore sessionStore ) {
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

  public static void runWith( ApplicationContext applicationContext, Runnable runnable ) {
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
  
  static boolean hasContext() {
    return CONTEXT_HOLDER.get() != null;
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
}