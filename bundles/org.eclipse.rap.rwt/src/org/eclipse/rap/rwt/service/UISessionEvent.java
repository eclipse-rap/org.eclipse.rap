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

import java.util.EventObject;


/**
 * <code>UISessionListener</code>s are
 * used to get notifications before the session store is destroyed.
 *
 * @since 2.0
 * @noextend This class is not intended to be subclassed by clients.
 */
public class UISessionEvent extends EventObject {
  private static final long serialVersionUID = 1L;

  /**
   * Creates a new instance of <code>UISessionEvent</code>.
   *
   * @param uiSession the session store which is about to be destroyed
   */
  public UISessionEvent( UISession uiSession ) {
    super( uiSession );
  }

  /**
   * Returns the <code>UISession</code> that is about to be destroyed.
   *
   * @return the session store that is about to be destroyed.
   */
  public UISession getUISession() {
    return ( UISession )getSource();
  }

  /**
   * @deprecated Use getUISession instead
   */
  @Deprecated
  public UISession getSessionStore() {
    return ( UISession )getSource();
  }

}
