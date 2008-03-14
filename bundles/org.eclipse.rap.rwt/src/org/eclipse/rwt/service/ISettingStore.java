/*******************************************************************************
 * Copyright (c) 2002-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.service;

import java.util.Enumeration;

/**
 * An ISettingStore is a mechanism persisting settings (i.e. key/value
 * pairs) beyond the scope of a single session.
 * <p> 
 * A setting store instance is always associated with the current session.
 * Any settings being put into the store (via {@link #setAttribute(String, String)})
 * are considered persisted from that point on. 
 * <p>
 * To retrieve data stored in a previous session, client can invoke 
 * the {@link #loadById(String)} method with an appropriate <code>id</code>.
 * This will load any data stored under that id into the current setting store. 
 * <p>
 * This mechanism is intented to be used as follows: by default RWT will assign
 * a new setting store to each new session, based on the current session id. 
 * After the user has authenticated, application developers can use the 
 * {@link #loadById(String)} method to initialize the store with persisted data 
 * stored under that id during a previous session. Obviously, any data that 
 * is set into the store from that point on, will also be persisted under 
 * the current id and will also be available in the future.
 * <p>
 * Clients who wish to implement own setting store must
 * also provide a corresponding {@link ISettingStoreFactory}.
 * 
 * @see FileSettingStore
 * @since 1.1
 */
public interface ISettingStore {
  
  /**
   * Returns the attribute stored under the specified name in this 
   * {@link ISettingStore}, or <code>null</code> if no attribute is stored under
   * that name.
   * 
   * @param name a non-null String specifying the name of the attribute
   * @return a String value or <code>null</code>
   * @throw {@link NullPointerException} if name is <code>null</code>
   */
  String getAttribute( String name );
  
  /**
   * Returns an {@link Enumeration} of String objects with the names
   * of all attributes in this {@link ISettingStore}.
   * 
   * @return an {@link Enumeration}; never <code>null</code>
   */
  Enumeration getAttributeNames();
  
  /**
   * Stores an attribute to this {@link ISettingStore}, using the name 
   * specified. If an attribute with the same name is already stored in that
   * {@link ISettingStore} the previous value is replaced.
   * <p>
   * Any attribute stored to this ISettingStore using this method is considered
   * persisted from that point on.
   * <p>
   * If the value argument is <code>null</code>, this has the same effect 
   * as calling {@link #removeAttribute(String)}.
   * <p>
   * {@link ISettingStoreListener}s attached to this instance will be notified
   * after an attribute has been stored.
   * 
   * @param name   the name of the attribute; cannot be <code>null</code>
   * @param value  the String to store; may be <code>null</code>
   * 
   * @throws SettingStoreException if the load operation failed to complete
   *         normally
   * @throws NullPointerException if name is <code>null</code> 
   */
  void setAttribute( String name, String value ) throws SettingStoreException;
  
  /**
   * Removes the attribute stored under the specified name from this
   * {@link ISettingStore}. If no attribute is stored under the specified name,
   * this method does nothing.
   * <p>
   * {@link ISettingStoreListener}s attached to this instance will be notified
   * after an attribute has been removed.
   * 
   * @param name    the name of the attribute to remove;
   *                cannot be <code>null</code>
   *                
   * @throws SettingStoreException if the remove operation failed to complete
   *         normally
   * @throws NullPointerException if name is <code>null</code>
   */
  void removeAttribute( String name ) throws SettingStoreException;
  
  /**
   * Replace the contents of this setting store with all attributes persisted
   * under the given <code>id</code>.
   * <p>
   * The attributes of this setting store before the load operation will remain
   * associated with the old id, but will be removed from this store instance.
   * {@link ISettingStoreListener}s attached to this store will receive a
   * notification for each removed attribute.
   * <p> 
   * During the load operation this store will be filled with the attributes
   * associated with the new id value. {@link ISettingStoreListener}s attached
   * to this store will receive a notification for each added attribute.
   * <p>
   * After the load operation this store will only hold attributes associated 
   * with the new id value.
   * <p>
   * It is important to note that this operation does not create a new setting 
   * store, so that listeners still remain associated with the same store
   * instance. Instead the contents of this setting store are replaced
   * with the contents associated with the given <code>id</code>.
   * 
   * @param id a non-null; non-empty; non-whitespace-only String
   * 
   * @throws SettingStoreException if the load operation failed to complete
   *         normally
   * @throws NullPointerException if <code>id</code> is <code>null</code>
   * @throws IllegalArgumentException if <code>id</code> is empty or composed
   *         entirely of whitespace
   */
  void loadById( String id ) throws SettingStoreException;
  
  /**
   * Returns the unique identifier of this setting store
   * @return a non-empty String value; never null
   */
  String getId();
  
  /**
   * Attaches an {@link ISettingStoreListener} to this {@link ISettingStore}.
   * <p>
   * Listeners attached to this instance will notified of changes in the store.
   * 
   * @param listener the {@link ISettingStoreListener} to add; non-null
   * @throw {@link NullPointerException} if listener is <code>null</code>
   */
  void addSettingStoreListener( ISettingStoreListener listener );
  
  /**
   * Removes an {@link ISettingStoreListener} from this {@link ISettingStore}.
   *
   * @param listener the {@link ISettingStoreListener} to remove; non-null
   * @throw {@link NullPointerException} if listener is <code>null</code>
   */
  void removeSettingStoreListener( ISettingStoreListener listener );
}
