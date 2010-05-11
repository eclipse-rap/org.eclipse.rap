/*******************************************************************************
 * Copyright (c) 2002, 2010 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.service;

import java.util.Enumeration;

import javax.servlet.http.HttpSession;


/**
 * The <code>ISessionStore</code> represents a storage place for objects
 * with session scope. The session store itself is stored in the servlet
 * container's session. Different than the <code>HttpSession</code> it is
 * possible to register programmatically a listener that is notified before
 * the session store will be destroyed (<code>HttpSessionListener</code>s 
 * don't work with * RAP). This gives the possibility to cleanup on session 
 * shutdown with the session singleton infrastructure intact.
 *
 * <p>This interface is not intended to be implemented by clients.</p>
 *
 * @since 1.0
 */
public interface ISessionStore {


  /**
   * Binds an object to this <code>ISessionStore</code>, using the name
   * specified. If an object of the same name is already bound to the
   * <code>ISessionStore</code> the object is replaced.
   *
   * <p>After this method executes, and if the new object
   * implements <code>HttpSessionBindingListener</code>, the
   * <code>ISessionStore</code> calls
   * <code>HttpSessionBindingListener.valueBound</code>.
   *
   * <p>If an object was already bound to this <code>ISessionStore</code> of
   * this name that implements <code>HttpSessionBindingListener</code>, its
   * <code>HttpSessionBindingListener.valueUnbound</code> method is called.
   *
   * <p>If the value passed in is null, this has the same effect as calling
   * <code>removeAttribute()<code>.
   *
   *
   * @param name the name to which the object is bound;
   *             cannot be null
   *
   * @param value the object to be bound
   * @return <code>true</code> if the attribute was set or <code>false</code>
   *         if the attribute could not be set because the session was
   *         invalidated.
   */
  boolean setAttribute( String name, Object value );

  /**
   * Returns the object bound with the specified name in this
   * <code>ISessionStore</code>, or <code>null</code> if no object is bound
   * under the name.
   *
   * @param name a string specifying the name of the object
   * @return the object with the specified name or <code>null</code> if the
   *         underlying session was invalidated.
   */
  Object getAttribute( String name );

  /**
   * Removes the object bound with the specified name from this
   * <code>ISessionStore</code>. If no object is bound with the specified name,
   * this method does nothing.
   * <p>
   * After this method executes, and if the object implements
   * <code>HttpSessionBindingListener</code>, the
   * <code>HttpSessionBindingListener.valueUnbound</code> is called.
   *
   * @param name The name of the object to remove from this
   *             <code>ISessionStore</code>.
   * @return <code>true</code> if the attribute was removed or
   *         <code>false</code> if the attribute could not be removed because
   *         the session was invalidated.
   * @see #isBound()
   */
  boolean removeAttribute( String name );

  /**
   * Returns an <code>Enumeration</code> of <code>String</code> objects
   * containing the names of all the objects bound to this
   * <code>ISessionStore</code>.
   *
   * @return An <code>Enumeration</code> of <code>String</code> objects
   *         specifying the names of all the objects bound to this
   *         <code>ISessionStore</code> or an empty enumeration if the
   *         underlying session was invalidated.
   * @see #isBound()
   */
  Enumeration getAttributeNames();

  /**
   * Returns a string containing the unique identifier assigned to the
   * underlying <code>HttpSession</code>.
   * The identifier is assigned by the servlet container and is implementation
   * dependent.
   *
   * @return A string specifying the identifier assigned to the
   *         underlying <code>HttpSession</code>.
   */
  String getId();

  /**
   * Adds an instance of <code>SessionStoreListener</code> to this
   * <code>ISessionStore</code>. <code>SessionStoreListener</code>s are
   * used to get notifications before the session store is destroyed.
   * If the given listener is already added the method has no effect.
   *
   * @param listener the SessionStoreListener to be added
   * @return <code>true</code> if the listener was added or <code>false</code>
   *         if the listener could not be added because the session was
   *         invalidated.
   * @see #isBound()
   */
  boolean addSessionStoreListener( SessionStoreListener listener );

  /**
   * Removes an instance of <code>SessionStoreListener</code> to this
   * <code>ISessionStore</code>. <code>SessionStoreListener</code>s are
   * used to get notifications before the session store is destroyed.
   * If the given listener is not added to the session store this method
   * has no effect.
   *
   * @param listener the SessionStoreListener to be removed
   * @return <code>true</code> if the listener was removed or <code>false</code>
   *         if the listener could not be removed because the session was
   *         invalidated.
   * @see #isBound()
   */
  boolean removeSessionStoreListener( SessionStoreListener listener );

  /**
   * Returns the underlying HttpSession instance.
   *
   * @return the HttpSession instance
   */
  HttpSession getHttpSession();

  /**
   * Returns whether this <code>ISessionStore</code> is bound to the
   * underlying <code>HttpSession</code> or not. If the session store is
   * unbound it behaves as if the http session it belonged to was invalidated.
   *
   *  @return true if the session store is bound, false otherwise.
   */
  boolean isBound();
}
