/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which
 * is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import java.util.ArrayList;
import java.util.List;

class LoggingListener implements Listener {
  final List<Event> log = new ArrayList<Event>();

  public void handleEvent( Event event ) {
    log.add( event );
  }

  public void clear() {
    log.clear();
  }

  public int size() {
    return log.size();
  }

  public Event get( int index ) {
    return log.get( index );
  }

  public List<Widget> getItems() {
    int size = log.size();
    List<Widget> result = new ArrayList<Widget>( size );
    for( int i = 0; i < size; i++ ) {
      result.add( get( i ).item );
    }
    return result;
  }
}
