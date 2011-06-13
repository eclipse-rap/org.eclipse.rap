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
package org.eclipse.rap.rwt.osgi.internal;

import org.eclipse.rap.rwt.osgi.RWTServiceObserver;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;


public class RWTServiceObserverTracker
  extends ServiceTracker< RWTServiceObserver, RWTServiceObserver >
{
  private final RWTServiceImpl rwtService;

  public RWTServiceObserverTracker( BundleContext context, RWTServiceImpl rwtService ) {
    super( context, RWTServiceObserver.class.getName(), null );
    this.rwtService = rwtService;
  }

  @Override
  public RWTServiceObserver addingService( ServiceReference<RWTServiceObserver> reference ) {
    rwtService.addObserver( context.getService( reference ) );
    return super.addingService( reference );
  }
  
  @Override
  public void removedService( ServiceReference<RWTServiceObserver> reference,
                              RWTServiceObserver service )
  {
    rwtService.removeObserver( service );
  }
}