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


/**
 * Setting store listeners are notified by an {@link ISettingStore}, when
 * an attribute inside the store has been changed (i.e. added, modified, 
 * removed).
 * <p>
 * @see ISettingStore#addSettingStoreListener(SettingStoreListener)
 * @see ISettingStore#removeSettingStoreListener(SettingStoreListener)
 * <p>
 * @since 1.1
 */
public interface SettingStoreListener {
  
  /**
   * This method is invoked by the setting store, when an attribute inside
   * the store has been changed (i.e. added, modified, removed).
   *
   * @param event a non-null {@link ISettingStoreEvent} instance with
   *                   specific information about the change
   */
  void settingChanged( SettingStoreEvent event );
  
}
