/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.serverpush;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.rap.rwt.internal.util.SerializableLock;
import org.eclipse.swt.internal.SerializableCompatibility;


final class ServerPushActivationTracker implements SerializableCompatibility {
  private final Set<String> ids;
  private final SerializableLock lock;

  ServerPushActivationTracker() {
    ids = new HashSet<String>();
    lock = new SerializableLock();
  }

  void activate( String id ) {
    synchronized( lock ) {
      ids.add( id );
    }
  }

  void deactivate( String id ) {
    synchronized( lock ) {
      ids.remove( id );
    }
  }

  boolean isActive() {
    synchronized( lock ) {
      return !ids.isEmpty();
    }
  }

}
