/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.service;

import java.util.EventObject;

/**
 * <code>SessionStoreListener</code>s are
 * used to get notifications before the session store is destroyed.
 * 
 * @since 1.0
 */
public class SessionStoreEvent extends EventObject {
  private static final long serialVersionUID = 1L;
  
  /**
   * Creates a new instance of <code>SessionStoreEvent</code>.
   * 
   * @param sessionStore the session store which is about to be destroyed
   */
  public SessionStoreEvent( ISessionStore sessionStore ) {
    super( sessionStore );
  }
  
  /**
   * Returns the <code>ISessionStore</code> that is about to be destroyed.
   * 
   * @return the session store that is about to be destroyed.
   */
  public ISessionStore getSessionStore() {
    return ( ISessionStore )getSource();
  }
}
