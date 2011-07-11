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
package org.eclipse.rwt.internal.lifecycle;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.rwt.internal.util.SerializableLock;
import org.eclipse.swt.internal.SerializableCompatibility;

final class IdManager implements SerializableCompatibility {
  private final Set<String> ids;
  private final SerializableLock lock;

  IdManager() {
    ids = new HashSet<String>();
    lock = new SerializableLock();
  }

  int add( String id ) {
    synchronized( lock ) {
      ids.add( id );
      return ids.size();
    }
  }

  int remove( String id ) {
    synchronized( lock ) {
      ids.remove( id );
      return ids.size();
    }
  }

  boolean isEmpty() {
    synchronized( lock ) {
      return ids.isEmpty();
    }
  }
}