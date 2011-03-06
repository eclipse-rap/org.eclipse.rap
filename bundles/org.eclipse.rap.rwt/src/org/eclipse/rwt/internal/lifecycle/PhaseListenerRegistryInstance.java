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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.lifecycle.PhaseListener;


public class PhaseListenerRegistryInstance {
  private final Set phaseListeners;

  private PhaseListenerRegistryInstance() {
    phaseListeners = new HashSet();
  }
  
  void add( final PhaseListener listener ) {
    ParamCheck.notNull( listener, "listener" );
    phaseListeners.add( listener );
  }
  
  void remove( final PhaseListener listener ) {
    ParamCheck.notNull( listener, "listener" );
    phaseListeners.remove( listener );
  }
  
  
  PhaseListener[] get() {
    PhaseListener[] result = new PhaseListener[ phaseListeners.size() ];
    phaseListeners.toArray( result );
    return result;
  }

  void clear() {
    phaseListeners.clear();
  }
}
