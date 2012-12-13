/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing developemnt
 ******************************************************************************/
package org.eclipse.rap.rwt.service;

import javax.servlet.http.HttpServletRequest;


/**
 * The service store can be used to store and retrieve information in the scope of a request.
 *
 * @since 2.0
 * @see org.eclipse.rap.rwt.RWT
 * @noimplement This interface is not intended to be implemented by clients.
 * @deprecated This interface will be removed in a future release without a replacement. If you need
 *             to store data in the scope of a request, consider to use the request object (e.g.
 *             {@link HttpServletRequest#setAttribute(String, Object)}).
 */
@Deprecated
public interface IServiceStore {

  Object getAttribute( String name );
  void setAttribute( String name, Object value );
  void removeAttribute( String name );

}
