/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
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
 * The <code>IServiceStore</code> can be used to store and retrieve
 * information during a life cycle request.
 * 
 * <p><strong>Note:</strong> the <code>IServiceStore</code> has request-scope, 
 * meaning that all information stored here will be lost at the end of a 
 * request.</p>
 * 
 * @since 1.0
 * @see org.eclipse.rwt.RWT
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IServiceStore {
  
  Object getAttribute( String name );
  void setAttribute( String name, Object value );
  void removeAttribute( String name );
}
