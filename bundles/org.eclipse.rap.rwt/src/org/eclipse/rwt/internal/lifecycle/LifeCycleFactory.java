/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import java.text.MessageFormat;

import org.eclipse.rwt.internal.*;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.service.ISessionStore;



/** <p>Supplies a factory method for lifecycle managers for various 
  * <code>LifeCycle</code> implementations.</p>
  */
// TODO [w4t] revise: introduced getLifeCycle, made loadLifeCycle package private
public final class LifeCycleFactory {
  
  private static LifeCycle globalLifeCycle; 
  
  private LifeCycleFactory() {
    // prevent instantiation
  }

  public static ILifeCycle getLifeCycle() {
    ISessionStore session = ContextProvider.getSession();
    String id = LifeCycle.class.getName();
    ILifeCycle lifeCycle = ( ILifeCycle )session.getAttribute( id );
    if( lifeCycle == null ) {
      lifeCycle = loadLifeCycle();
      session.setAttribute( id, lifeCycle );
    }
    return lifeCycle;
  }
  
  public static ILifeCycle loadLifeCycle( ) {
    LifeCycle result = globalLifeCycle;
    if( result == null ) {
      String lifeCycleClassName = null;
      try {
        IConfiguration configuration = ConfigurationReader.getConfiguration();
        IInitialization initialization = configuration.getInitialization();
        lifeCycleClassName = initialization.getLifeCycle();
        Class lifeCycleClass = Class.forName( lifeCycleClassName );
        result = ( LifeCycle )lifeCycleClass.newInstance();
        if( result.getScope().equals( Scope.APPLICATION ) ) {
          globalLifeCycle = result;
        }
      } catch( Exception ex ) {
        // TODO [w4t] revise: throw exception instead of issuing a warning and
        //      returning the w4t standard life cycle
//        System.out.println( "Could not load lifecycle. " + ex.toString() );
//        result = new org.eclipse.rap.engine.lifecycle.standard.LifeCycle_Standard();
        String text = "Could not load life cycle implementation {0}: {1}";
        Object[] args = new Object[] { lifeCycleClassName, ex.toString() };
        String msg = MessageFormat.format( text, args );
        throw new IllegalStateException( msg );
      }
    }
    return result;
  }
}
