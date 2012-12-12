/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.service;

import java.util.Enumeration;

import javax.servlet.http.HttpSession;


/**
 * The <code>UISession</code> represents the current instance of the UI.
 * <p>
 * In contrast to the <code>HttpSession</code> it is possible to register a listener that is
 * notified <em>before</em> the session is destroyed. This listener can be used to cleanup on
 * session shutdown with the UI session still intact.
 * </p>
 *
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
@SuppressWarnings( "deprecation" )
public interface UISession extends ISessionStore {

  /**
   * Binds an object to this UI Session, using the name specified. If an object of the same name is
   * already bound to the UI session, the object is replaced.
   * <p>
   * If the value is null, this has the same effect as calling <code>removeAttribute()<code>.
   * </p>
   *
   * @param name the name to which the object is bound; cannot be <code>null</code>
   * @param value the object to be bound
   * @return <code>true</code> if the attribute was set or <code>false</code> if the attribute could
   *         not be set because the session was invalidated.
   */
  boolean setAttribute( String name, Object value );

  /**
   * Returns the object bound with the specified name in this UI session, or <code>null</code> if no
   * object is bound under the name.
   *
   * @param name a string specifying the name of the object; cannot be null
   * @return the object bound with the specified name or <code>null</code> if no object is bound
   *         with this name
   */
  Object getAttribute( String name );

  /**
   * Removes the object bound with the specified name from this UI session. If no object is bound
   * with the specified name, this method does nothing.
   *
   * @param name The name of the object to remove from this UI session, must not be
   *          <code>null</code>
   * @return <code>true</code> if the attribute was removed or <code>false</code> if the attribute
   *         could not be removed because the session was invalidated
   * @see #isBound()
   */
  boolean removeAttribute( String name );

  /**
   * Returns an <code>Enumeration</code> of the names of all objects bound to this UI session.
   *
   * @return An <code>Enumeration</code> of strings that contains the names of all objects bound to
   *         this UI session or an empty enumeration if the underlying session was invalidated
   * @see #isBound()
   */
  Enumeration<String> getAttributeNames();

  /**
   * Returns a unique identifier for this UI session.
   *
   * @return the unique identifier for this UI session
   */
  String getId();

  /**
   * Adds a <code>UISessionListener</code> to this UI session. <code>UISessionListener</code>s are
   * used to receive notifications before the UI session is destroyed. If the given listener is
   * already added the method has no effect.
   *
   * @param listener the listener to be added
   * @return <code>true</code> if the listener was added or <code>false</code> if the listener could
   *         not be added because the session was invalidated
   * @see #isBound()
   */
  boolean addUISessionListener( UISessionListener listener );

  /**
   * @deprecated Use addUISessionListener instead
   */
  @Deprecated
  boolean addSessionStoreListener( UISessionListener listener );

  /**
   * Removes a <code>UISessionListener</code> from this UI session. <code>UISessionListener</code>s
   * are used to receive notifications before the UI session is destroyed. If the given listener is
   * not added to the session store this method has no effect.
   *
   * @param listener the listener to be removed
   * @return <code>true</code> if the listener was removed or <code>false</code> if the listener
   *         could not be removed because the session was invalidated
   * @see #isBound()
   */
  boolean removeUISessionListener( UISessionListener listener );

  /**
   * @deprecated Use removeUISessionListener instead
   */
  @Deprecated
  boolean removeSessionStoreListener( UISessionListener listener );

  /**
   * Returns the underlying HttpSession instance.
   *
   * @return the HttpSession instance
   */
  HttpSession getHttpSession();

  /**
   * Returns whether this UI session is bound to the underlying <code>HttpSession</code> or not. If
   * the session store is unbound it behaves as if the HTTP session it belonged to was invalidated.
   *
   * @return true if the session store is bound, false otherwise.
   */
  boolean isBound();

  /**
   * Executes the given runnable in the context of this UI session. This method allows background
   * threads to access values that are stored in the UI session, including session singletons.
   *
   * @param runnable the runnable to execute in the context of this UI session
   * @see org.eclipse.rap.rwt.SingletonUtil
   */
  void exec( Runnable runnable );

}
