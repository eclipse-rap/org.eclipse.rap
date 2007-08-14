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
package org.eclipse.rwt.internal;

import org.eclipse.rwt.Adaptable;
import org.eclipse.rwt.AdapterFactory;



/** <p>An <code>AdapterManager</code> appears as a registry for 
 *  <code>AdapterFactory</code> implementations. Clients directly de-/register 
 *  <code>AdapterFactory</code> implementations at the manager.</p>
 *  
 *  <p><code>Adaptable</code> objects tunnel invocations of 
 *  <code>Adaptable.getAdapter</code> to the manager's 
 *  <code>AdapterManager.getAdapter</code> method. The manager himself 
 *  dispatches the invokation to the <code>AdapterFactory</code> instance 
 *  registered for the given adaptable object and requested adapter.</p>  
 * 
 *  <p>Usage:
 *  <pre>
 *   AdapterFactory adapterFactory = new AdapterFactory() {
 * 
 *    public Class[] getAdapterList() {
 *      return new Class[] { MyAdapter.class };
 *    }
 * 
 *    public Object getAdapter( final Object adaptable, 
 *                              final adapter ) {
 *      MyAdaptableType adaptableInstance = ( MyAdaptableType )adaptable;
 *      return MyAdapterImpl( adaptableInstance );
 *    }
 *  }
 *  AdapterManager manager = W4TContext.getAdapterManager();
 *  manager.registerAdapters( adapterFactory, MyAdaptableType.class );
 *   </pre>
 * 
 * </p>
 *
 *  @see Adaptable
 *  @see AdapterFactory 
 *  @see W4TContext#getAdapterManager() */
public interface AdapterManager {
  
  /** <p>returns an object which is an instance of the given class associated
   *  with the given object or <code>null</code> if no such object can be 
   *  found.</p>
   *
   *  @param adaptable the <code>Adaptable</code> instance used as lookup key
   *  @param adapter the type of adapter to look up
   *  @return a object castable to the given adapter type or <code>null</code> 
   *          if there is no adapter of the given type availabe */
  Object getAdapter( Object adaptable, Class adapter );

  /** <p>registers the given adapter factory as extending objects of the given
   *  type.</p>
   * 
   * @param factory the adapter factory
   * @param adaptable the type being extended
   */
  void registerAdapters( AdapterFactory factory, Class adaptable );

  /** <p>registers the given adapter factory as extending objects of the given
   *  type.</p>
   * 
   * @param factory the adapter factory to remove
   * @param adaptable the type agains which the factory is being registered
   */
  void deregisterAdapters( AdapterFactory factory, Class adaptable );
}
