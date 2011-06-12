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

import java.util.HashSet;
import java.util.Iterator;


class RWTContextContainer {
  private final HashSet< RWTContextImpl > contexts;

  RWTContextContainer() {
    this.contexts = new HashSet< RWTContextImpl >();
  }

  void add( RWTContextImpl context ) {
    removeDeadContexts();
    contexts.add( context );
  }
  
  void remove( RWTContextImpl context ) {
    removeDeadContexts();
    contexts.remove( context );
  }

  void clear() {
    contexts.clear();
  }
  
  // for testing purposes only...
  int size() {
    return contexts.size();
  }

  RWTContextImpl[] getAll() {
    removeDeadContexts();
    return contexts.toArray( new RWTContextImpl[ contexts.size() ] );
  }

  private void removeDeadContexts() {
    Iterator< RWTContextImpl > iterator = contexts.iterator();
    while( iterator.hasNext() ) {
      RWTContextImpl context = iterator.next();
      if( !context.isAlive() ) {
        iterator.remove();
      }
    }
  }
}