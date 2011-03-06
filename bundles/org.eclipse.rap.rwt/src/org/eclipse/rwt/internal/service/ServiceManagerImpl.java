/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.IServiceManager;

public final class ServiceManagerImpl implements IServiceManager {

  public void registerServiceHandler( String id, IServiceHandler handler ) {
    ServiceManager.registerServiceHandler( id, handler );
  }

  public void unregisterServiceHandler( String id ) {
    ServiceManager.unregisterServiceHandler( id );
  }
}