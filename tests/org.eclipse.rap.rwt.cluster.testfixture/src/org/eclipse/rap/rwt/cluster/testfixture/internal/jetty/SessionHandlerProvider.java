/*******************************************************************************
 * Copyright (c) 2011, 2017 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.cluster.testfixture.internal.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.DefaultSessionCache;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.HouseKeeper;
import org.eclipse.jetty.server.session.NullSessionDataStore;
import org.eclipse.jetty.server.session.SessionHandler;

class SessionHandlerProvider implements ISessionHandlerProvider {

  private static final long SCAVENGE_INTERVAL = 1;

  @Override
  public SessionHandler createSessionHandler( Server server ) throws Exception {
    SessionHandler handler = new SessionHandler();
    DefaultSessionCache sessionCache = new DefaultSessionCache( handler );
    NullSessionDataStore sessionDataStore = new NullSessionDataStore();
    sessionCache.setSessionDataStore( sessionDataStore );
    DefaultSessionIdManager sessionIdManager = new DefaultSessionIdManager( server );
    HouseKeeper houseKeeper = new HouseKeeper();
    houseKeeper.setIntervalSec( SCAVENGE_INTERVAL );
    sessionIdManager.setSessionHouseKeeper( houseKeeper );
    handler.setSessionCache( sessionCache );
    handler.setSessionIdManager( sessionIdManager );
    return handler;
  }

}
