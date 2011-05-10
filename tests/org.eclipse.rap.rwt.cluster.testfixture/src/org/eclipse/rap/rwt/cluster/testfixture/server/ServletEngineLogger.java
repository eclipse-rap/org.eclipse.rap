/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.cluster.testfixture.server;

import org.eclipse.jetty.util.log.Logger;


class ServletEngineLogger implements Logger {

  private final String name;
  private boolean debugEnabled;

  ServletEngineLogger() {
    this( ServletEngineLogger.class.getName(), false );
  }
  
  ServletEngineLogger( String name, boolean debugEnabled ) {
    this.name = name;
    this.debugEnabled = debugEnabled;
  }

  public String getName() {
    return name;
  }

  public Logger getLogger( String name ) {
    ServletEngineLogger result = this;
    if( !name.equals( getName() ) ) {
      result = new ServletEngineLogger( name, debugEnabled );
    }
    return result;
  }

  public boolean isDebugEnabled() {
    return debugEnabled;
  }

  public void setDebugEnabled( boolean enabled ) {
    this.debugEnabled = enabled;
  }

  public void warn( String msg, Object[] args ) {
    throw new RuntimeException( msg );
  }

  public void warn( Throwable thrown ) {
    throw rethrow( thrown );
  }

  public void warn( String msg, Throwable thrown ) {
    throw new RuntimeException( msg, thrown );
  }

  public void info( String msg, Object[] args ) {
  }

  public void info( Throwable thrown ) {
  }

  public void info( String msg, Throwable thrown ) {
  }

  public void debug( String msg, Object[] args ) {
    debug( msg, ( Throwable )null );
  }

  public void debug( Throwable throwable ) {
    debug( null, throwable );
  }

  public void debug( String msg, Throwable throwable ) {
    if( debugEnabled ) {
      if( msg != null ) {
        System.out.println( msg );
      }
      if( throwable != null ) {
        System.out.println( throwable );
      }
    }
  }

  public void ignore( Throwable ignored ) {
    throw rethrow( ignored );
  }

  private static RuntimeException rethrow( Throwable throwable ) {
    if( throwable instanceof RuntimeException ) {
      throw ( RuntimeException )throwable;
    }
    return new RuntimeException( throwable );
  }
}
