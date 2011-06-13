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


class RWTContextContainer {
  private final HashSet< RWTContextImpl > contexts;

  RWTContextContainer() {
    this.contexts = new HashSet< RWTContextImpl >();
  }

  void add( RWTContextImpl context ) {
    contexts.add( context );
  }
  
  void remove( RWTContextImpl context ) {
    contexts.remove( context );
  }

  void clear() {
    contexts.clear();
  }

  RWTContextImpl[] getAll() {
    return contexts.toArray( new RWTContextImpl[ contexts.size() ] );
  }
}