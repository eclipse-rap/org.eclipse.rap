/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import java.util.*;
import java.util.List;

class LoggingListener implements Listener {
  final List log = new ArrayList();

  public void handleEvent( Event event ) {
    log.add( event );
  }

  public void clear() {
    log.clear();
  }

  public int size() {
    return log.size();
  }

  public Event get( final int i ) {
    return ( Event )log.get( i );
  }

  public List getItems() {
    int size = log.size();
    List result = new ArrayList( size );
    for( int i = 0; i < size; i++ ) {
      result.add( get( i ).item );
    }
    return result;
  }
}
