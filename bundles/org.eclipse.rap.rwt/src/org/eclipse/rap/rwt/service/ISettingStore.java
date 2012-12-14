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

import java.io.IOException;
import java.util.Enumeration;


/**
 * @deprecated Use {@link SettingStore} instead.
 * @since 2.0
 */
@Deprecated
public interface ISettingStore {

  /**
   * @deprecated Use {@link SettingStore} instead of {@link ISettingStore}.
   */
  @Deprecated
  String getAttribute( String name );

  /**
   * @deprecated Use {@link SettingStore} instead of {@link ISettingStore}.
   */
  @Deprecated
  Enumeration getAttributeNames();

  /**
   * @deprecated Use {@link SettingStore} instead of {@link ISettingStore}.
   */
  @Deprecated
  void setAttribute( String name, String value ) throws IOException;

  /**
   * @deprecated Use {@link SettingStore} instead of {@link ISettingStore}.
   */
  @Deprecated
  void removeAttribute( String name ) throws IOException;

  /**
   * @deprecated Use {@link SettingStore} instead of {@link ISettingStore}.
   */
  @Deprecated
  void loadById( String id ) throws IOException;

  /**
   * @deprecated Use {@link SettingStore} instead of {@link ISettingStore}.
   */
  @Deprecated
  String getId();

  /**
   * @deprecated Use {@link SettingStore} instead of {@link ISettingStore}.
   */
  @Deprecated
  void addSettingStoreListener( SettingStoreListener listener );

  /**
   * @deprecated Use {@link SettingStore} instead of {@link ISettingStore}.
   */
  @Deprecated
  void removeSettingStoreListener( SettingStoreListener listener );

}
