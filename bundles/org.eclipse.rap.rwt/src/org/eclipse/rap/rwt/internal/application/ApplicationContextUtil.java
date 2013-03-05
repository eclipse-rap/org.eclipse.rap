/*******************************************************************************
 * Copyright (c) 2011, 2013 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.application;

import java.io.File;

import javax.servlet.ServletContext;

import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.service.ServiceContext;
import org.eclipse.rap.rwt.internal.util.ParamCheck;


public class ApplicationContextUtil {
  private final static ThreadLocal<ApplicationContextImpl> CONTEXT_HOLDER
    = new ThreadLocal<ApplicationContextImpl>();
  private final static String ATTR_APPLICATION_CONTEXT
    = ApplicationContextImpl.class.getName() + "#instance";

  public static void set( ServletContext servletContext, ApplicationContextImpl applicationContext )
  {
    servletContext.setAttribute( ATTR_APPLICATION_CONTEXT, applicationContext );
  }

  public static ApplicationContextImpl get( ServletContext servletContext ) {
    return ( ApplicationContextImpl )servletContext.getAttribute( ATTR_APPLICATION_CONTEXT );
  }

  public static void remove( ServletContext servletContext ) {
    servletContext.removeAttribute( ATTR_APPLICATION_CONTEXT );
  }

  public static ApplicationContextImpl getInstance() {
    ApplicationContextImpl result = CONTEXT_HOLDER.get();
    if( result == null  ) {
      ServiceContext context = ContextProvider.getContext();
      result = context.getApplicationContext();
    }
    checkApplicationContextExists( result );
    return result;
  }

  public static void runWith( ApplicationContextImpl applicationContext, Runnable runnable ) {
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

  public static void delete( File toDelete ) {
    if( toDelete.exists() ) {
      doDelete( toDelete );
    }
  }

  static boolean hasContext() {
    return CONTEXT_HOLDER.get() != null;
  }

  private static void checkNestedCall() {
    if( CONTEXT_HOLDER.get() != null ) {
      throw new IllegalStateException( "Nested call of runWithInstance detected." );
    }
  }

  private static void checkApplicationContextExists( ApplicationContextImpl applicationContext ) {
    if( applicationContext == null ) {
      throw new IllegalStateException( "No ApplicationContext registered." );
    }
  }

  private static void doDelete( File toDelete ) {
    if( toDelete.isDirectory() ) {
      deleteChildren( toDelete );
    }
    deleteFile( toDelete );
  }

  private static void deleteChildren( File toDelete ) {
    File[] children = toDelete.listFiles();
    for( int i = 0; i < children.length; i++ ) {
      delete( children[ i ] );
    }
  }

  private static void deleteFile( File toDelete ) {
    boolean deleted = toDelete.delete();
    if( !deleted ) {
      throw new IllegalStateException( "Could not delete: " + toDelete.getPath() );
    }
  }

}
