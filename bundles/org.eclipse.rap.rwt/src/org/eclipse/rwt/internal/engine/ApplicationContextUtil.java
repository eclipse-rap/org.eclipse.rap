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

import java.io.File;
import java.io.Serializable;

import javax.servlet.ServletContext;

import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.ServiceContext;
import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.service.ISessionStore;


public class ApplicationContextUtil {
  private final static ThreadLocal<ApplicationContext> CONTEXT_HOLDER
    = new ThreadLocal<ApplicationContext>();
  private final static String ATTR_APPLICATION_CONTEXT
    = ApplicationContext.class.getName() + "#INSTANCE";
 
  private static class TransientValue implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final transient Object value;

    TransientValue( Object value ) {
      this.value = value;
    }
    
    Object getValue() {
      return value;
    }
  }
 
  public static void set( ServletContext servletContext, ApplicationContext applicationContext ) {
    servletContext.setAttribute( ATTR_APPLICATION_CONTEXT, applicationContext );
  }

  public static ApplicationContext get( ServletContext servletContext ) {
    return ( ApplicationContext )servletContext.getAttribute( ATTR_APPLICATION_CONTEXT );
  }

  public static void remove( ServletContext servletContext ) {
    servletContext.removeAttribute( ATTR_APPLICATION_CONTEXT );
  }
  
  public static void set( ISessionStore sessionStore, ApplicationContext applicationContext ) {
    TransientValue transientValue = new TransientValue( applicationContext );
    sessionStore.setAttribute( ATTR_APPLICATION_CONTEXT, transientValue );
  }

  public static ApplicationContext get( ISessionStore sessionStore ) {
    ApplicationContext result = null;
    TransientValue value = ( TransientValue )sessionStore.getAttribute( ATTR_APPLICATION_CONTEXT );
    if( value != null ) {
      result = ( ApplicationContext )value.getValue();
    }
    return result;
  }

  public static void remove( ISessionStore sessionStore ) {
    sessionStore.removeAttribute( ATTR_APPLICATION_CONTEXT );
  }
  
  public static ApplicationContext getInstance() {
    ApplicationContext result = CONTEXT_HOLDER.get();
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
      String msg = "Nested call of runWithInstance detected.";
      throw new IllegalStateException( msg );
    }
  }

  private static void checkApplicationContextExists( ApplicationContext applicationContext ) {
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
      String msg = "Could not delete: " + toDelete.getPath();
      throw new IllegalStateException( msg );
    }
  }
}