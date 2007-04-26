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

package org.eclipse.swt.internal.engine;

import java.util.HashSet;
import java.util.Set;
import com.w4t.ParamCheck;
import com.w4t.engine.lifecycle.PhaseListener;


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
  
  public static PhaseListener[] get() {
    PhaseListener[] result = new PhaseListener[ phaseListeners.size() ];
    phaseListeners.toArray( result );
    return result;
  }
  
  public static void clear() {
    phaseListeners.clear();
  }
}
