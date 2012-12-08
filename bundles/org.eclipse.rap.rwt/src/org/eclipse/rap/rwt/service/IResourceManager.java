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

import java.io.InputStream;


/**
 * @since 2.0
 * @deprecated Use {@link ResourceManager} instead
 */
@Deprecated
public interface IResourceManager {

  /**
   * @deprecated Use {@link ResourceManager} instead of {@link IResourceManager}
   */
  void register( String name, InputStream inputStream );

  /**
   * @deprecated Use {@link ResourceManager} instead of {@link IResourceManager}
   */
  boolean unregister( String name );

  /**
   * @deprecated Use {@link ResourceManager} instead of {@link IResourceManager}
   */
  boolean isRegistered( String name );

  /**
   * @deprecated Use {@link ResourceManager} instead of {@link IResourceManager}
   */
  String getLocation( String name );

  /**
   * @deprecated Use {@link ResourceManager} instead of {@link IResourceManager}
   */
  InputStream getRegisteredContent( String name );

}
