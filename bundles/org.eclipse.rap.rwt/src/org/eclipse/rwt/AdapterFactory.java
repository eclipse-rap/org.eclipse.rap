/*******************************************************************************
 * Copyright (c) 2002-2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt;

import org.eclipse.rwt.internal.AdapterManager;


/** 
 * <p>An AdapterFactory creates the requested adapters associated with
 * a given object, if available. Adapters are used to add dynamically
 * interfaces to a class (behavioral extension).</p>
 * 
 * <p>AdapterFactories are registered with an global adapter manager.</p>
 * 
 * @since 1.0
 * @see AdapterManager
 * @see Adaptable
 * <!-- @see W4TContext#getAdapterManager() --> */
public interface AdapterFactory {
    
  /**
   * <p>returns an implementation of the given class associated with the 
   * given object. Returns <code>null</code> if no such implementation can 
   * be found.</p>
   *
   * @param adaptable the <code>Adaptable</code> instance used as lookup key
   * @param adapter the type of adapter to look up
   * @return a object castable to the given adapter type or <code>null</code> 
   *          if there is no adapter of the given type availabe
   */
  Object getAdapter( Object adaptable, Class adapter );
  
  /**
   * <p>returns an array of adapter types handled by this factory.</p>
   * 
   * <p>This method is generally used to discover which adapter types are 
   * supported, in advance of actually calling <code>getAdapter</code>.</p>
   * 
   * @return a list of supported adapter types
   */
  Class[] getAdapterList();
}
