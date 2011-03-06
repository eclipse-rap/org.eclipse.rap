/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.lifecycle;

import org.eclipse.rwt.internal.engine.RWTContext;
import org.eclipse.rwt.lifecycle.PhaseListener;


/**
 * <p>This class holds <code>PhaseListener</code>s that were configured
 * by init parameters in the servlet context (e.g. in web.xml).</p>
 */
public final class PhaseListenerRegistry {
  
  public static void add( final PhaseListener listener ) {
    getInstance().add( listener );
  }
  
  public static void remove( final PhaseListener listener ) {
    getInstance().remove( listener );
  }
  
  public static PhaseListener[] get() {
    return getInstance().get();
  }
  
  public static void clear() {
    getInstance().clear();
  }
  
  private static PhaseListenerRegistryInstance getInstance() {
    Class singletonType = PhaseListenerRegistryInstance.class;
    Object singleton = RWTContext.getSingleton( singletonType );
    return ( PhaseListenerRegistryInstance )singleton;
  }
    
  private PhaseListenerRegistry() {
    // prevent instantiation
  }
}
