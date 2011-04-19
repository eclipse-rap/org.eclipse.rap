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
package org.eclipse.rwt.internal.textsize;

import org.eclipse.rwt.lifecycle.ILifeCycle;
import org.eclipse.rwt.service.ISessionStore;


class MeasurementHandlerRegistrar {
  private static final String HANDLER = MeasurementHandlerRegistrar.class.getName() + "#Handler";
  
  private final ISessionStore session;
  private final ILifeCycle lifeCycle;

  MeasurementHandlerRegistrar( ISessionStore session, ILifeCycle lifeCycle ) {
    this.session = session;
    this.lifeCycle = lifeCycle;
  }

  void register() {
    if( !isRegistered() ) {
      MeasurementHandler handler = new MeasurementHandler();
      session.setAttribute( HANDLER, handler );
      lifeCycle.addPhaseListener( handler );
    }
  }

  boolean isRegistered() {
    return session.getAttribute( HANDLER ) != null;
  }

  void deregister() {
    MeasurementHandler handler = ( MeasurementHandler )session.getAttribute( HANDLER );
    if( handler != null ) {
      lifeCycle.removePhaseListener( handler );
      session.removeAttribute( HANDLER );
    }
  }
}