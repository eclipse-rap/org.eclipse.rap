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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.lifecycle.PhaseListener;



/**
 * <p>This class holds <code>PhaseListener</code>s that were configured
 * by init parameters in the servlet context (e.g. in web.xml).</p>
 */
public final class PhaseListenerRegistry {
  
  private static final Set phaseListeners = new HashSet();

  private PhaseListenerRegistry() {
    // prevent instantiation
  }
  
  public static void add( final PhaseListener listener ) {
    ParamCheck.notNull( listener, "listener" );
    phaseListeners.add( listener );
  }
  
  public static void remove( final PhaseListener listener ) {
    ParamCheck.notNull( listener, "listener" );
    phaseListeners.remove( listener );
  }

  public static PhaseListener[] get() {
    PhaseListener[] result = new PhaseListener[ phaseListeners.size() ];
    phaseListeners.toArray( result );
    return result;
  }
  
  public static void clear() {
    phaseListeners.clear();
  }
}
