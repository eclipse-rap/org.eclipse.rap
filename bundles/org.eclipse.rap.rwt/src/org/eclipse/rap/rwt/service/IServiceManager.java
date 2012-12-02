/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
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


/**
 * @since 2.0
 * @deprecated Use {@link ServiceManager} instead
 */
@Deprecated
public interface IServiceManager {

  /**
   * @deprecated Use {@link ServiceManager} instead of {@link IServiceManager}
   */
  void registerServiceHandler( String id, ServiceHandler serviceHandler );

  /**
   * @deprecated Use {@link ServiceManager} instead of {@link IServiceManager}
   */
  void unregisterServiceHandler( String id );

  /**
   * @deprecated Use {@link ServiceManager} instead of {@link IServiceManager}
   */
  String getServiceHandlerUrl( String id );

}
