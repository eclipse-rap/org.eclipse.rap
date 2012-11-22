/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.remote;



/**
 * @since 2.0
 */
public interface RemoteObjectAdapter {

  /**
   * Returns the id that identifies the object on the client.
   *
   * @return the remote object id
   */
  String getId();

}
