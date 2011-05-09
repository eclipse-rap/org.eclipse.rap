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


public class SilentLogger implements Logger {

  private final String name;

  SilentLogger() {
    this( SilentLogger.class.getName() );
  }
  
  public SilentLogger( String name ) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public Logger getLogger( String name ) {
    Logger result = this;
    if( !name.equals( getName() ) ) {
      result = new SilentLogger( name ); 
    }
    return result;
  }

  public boolean isDebugEnabled() {
    return false;
  }

  public void setDebugEnabled( boolean enabled ) {
    throw new UnsupportedOperationException();
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
  }

  public void debug( Throwable thrown ) {
  }

  public void debug( String msg, Throwable thrown ) {
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
