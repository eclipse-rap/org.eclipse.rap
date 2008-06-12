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
package org.eclipse.rwt.internal.events;


/**
 * <p>The <code>IEventAdapter</code> interface is used to access a components
 * event listeners.</p>
 * <p>This interface is not intended to be implemented by clients.</p>
 */
// TODO:[fappel] describe event mechnanism in package.html
public interface IEventAdapter {

  /**
   * <p>Returns an array containing all registered listeners for the object
   * that returned this <code>IEventAdapter</code>.</p> 
   * @return an array of listeners or an empty array if no listeners are
   * available.
   */
  Object[] getListener();
  
  /**
   * <p>Returns an array containing all listeners of type 
   * <code>listenerType</code> for the object that returned this 
   * <code>IEventAdapter</code>.</p>
   * @param listenerType the type of the listeners to be returned  
   * @return an array of listeners or an empty array if no listeners are
   * available.
   */
  Object[] getListener( Class listenerType );
  
  /**
   * <p>Returns whether there are any listeners of type 
   * <code>listenerType</code> registered for the object that returned this 
   * <code>IEventAdapter</code>.</p>
   * @param listenerType the type of the listeners to be returned  
   * @return <code>true</code> if any listeners of <code>listenerType</code>
   * are registered; <code>false</code> otherwise.
   */
  boolean hasListener( Class listenerType );
  
  /**
   * <p>Adds the given listener for the object that returned this 
   * <code>IEventAdapter</code>.</p>
   * @param listenerType the type of the listeners to be added
   * @param listener the listener to be added 
   */
  void addListener( Class listenerType, Object listener );
  
  /**
   * <p>Removes the given listener from the object that returned this 
   * <code>IEventAdapter</code>.</p>
   * @param listenerType the type of the listeners to be removed
   * @param listener the listener to be removed
   */
  void removeListener( Class listenerType, Object listener );
}
