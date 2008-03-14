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
 * A setting store factory is responsible for creating and initializing
 * a specific kind of setting store.
 * <p>
 * At runtime, RWT will use a single ISettingStoreFactory implementation
 * to create new {@link ISettingStore} instances. Refer to documentation 
 * of the <code>org.eclipse.rap.ui.settingstores</code> for details on how
 * to configure which factory is used.
 * <p>
 * If nothing is specified the {@link FileSettingStoreFactory} will be used
 * by default.
 * <p>
 * This interface must be implemented by clients who wish to use their own
 * implementation of an {@link ISettingStore}.
 * <p>
 * @since 1.1 
 */
public interface ISettingStoreFactory {
  
  /**
   * Create, initialize and return a specific ISettingStore instance.
   * 
   * @param storeId a non-null, non-empty, non-whitespace-only String. 
   * @return an {@link ISettingStore} instance; never <code>null</code>
   * @throws NullPointerException if storeId is <code>null</null>
   * @throws IllegalArgumentException if storeId is empty or composed
   *         entirely of whitespace
   */
  ISettingStore createSettingStore( final String storeId );
  
}
