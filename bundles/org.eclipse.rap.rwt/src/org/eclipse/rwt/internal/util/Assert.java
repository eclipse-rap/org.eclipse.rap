/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.util;


/** <p>Provides some simple assertion facility that sends runtime 
  * exceptions on assertion fails.</p>
  */
public final class Assert {

  private Assert() {
    // prevent instantiation
  }

  /** <p>Fails if obj is null.</p> */
  public static void isNotNull( final Object obj ) {
    isNotNull( obj, "" );
  }
  
  /** <p>Fails if obj is null, and sends message.</p> */
  public static void isNotNull( final Object obj, final String message ) {
    if( obj == null ) {
      throw new AssertionFailedException( message );
    }
  }

  /** <p>Fails if expr is false.</p> */
  public static void isTrue( final boolean expr ) {
    isTrue( expr, "" );    
  }

  /** <p>Fails if expr is false and sends message.</p> */
  public static void isTrue( final boolean expr, final String message ) {
    if( !expr ) {
      throw new AssertionFailedException( message );      
    }
  }

  /** The common exception type (runtime exception) for failed assertions. */ 
  private static class AssertionFailedException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    AssertionFailedException( final String msg ) {
      super( msg );
    }
  }
}