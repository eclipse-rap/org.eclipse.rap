/*******************************************************************************
 * Copyright (c) 2011, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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

  private final Set<Object> handles;
  private final SerializableLock lock;

  ServerPushActivationTracker() {
    handles = new HashSet<>();
    lock = new SerializableLock();
  }

  void activate( Object handle ) {
    synchronized( lock ) {
      handles.add( handle );
    }
  }

  void deactivate( Object handle ) {
    synchronized( lock ) {
      handles.remove( handle );
    }
  }

  boolean isActive() {
    synchronized( lock ) {
      return !handles.isEmpty();
    }
  }

}
