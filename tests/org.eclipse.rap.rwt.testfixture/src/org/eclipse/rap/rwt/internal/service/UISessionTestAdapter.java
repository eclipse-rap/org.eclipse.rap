/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import org.eclipse.rap.rwt.internal.service.UISessionImpl;
import org.eclipse.rap.rwt.remote.Connection;
import org.eclipse.rap.rwt.service.UISession;


public class UISessionTestAdapter {

  public static void setConnection( UISession uiSession, Connection connection ) {
    ( ( UISessionImpl )uiSession ).setConnection( connection );
  }

}
