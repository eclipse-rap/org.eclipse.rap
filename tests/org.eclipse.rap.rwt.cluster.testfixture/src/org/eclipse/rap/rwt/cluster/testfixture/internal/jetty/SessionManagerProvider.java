/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.cluster.testfixture.internal.jetty;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.server.session.HashSessionManager;

class SessionManagerProvider implements ISessionManagerProvider {

  public SessionManager createSessionManager( Server server ) {
    HashSessionManager result = new HashSessionManager();
    result.setUsingCookies( true );
    return result;
  }

  public SessionIdManager createSessionIdManager( Server server ) {
    return new HashSessionIdManager();
  }
}