/*******************************************************************************
 * Copyright (c) 2025 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.addons.internal.canvas;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.rap.rwt.addons.canvas.ClientDrawListener;

public class ClientDrawListenerAdapter {

  private final Collection<ClientDrawListener> drawListeners;

  public ClientDrawListenerAdapter() {
    drawListeners = new ArrayList<>();
  }

  public void notifyReceivedDrawing() {
    for( ClientDrawListener each : new ArrayList<>( drawListeners ) ) {
      each.receivedDrawing();
    }
  }

  public void addClientDrawListener( ClientDrawListener listener ) {
    if( listener != null ) {
      drawListeners.add( listener );
    }
  }

  public void removeClientDrawListener( ClientDrawListener listener ) {
    if( listener != null ) {
      drawListeners.remove( listener );
    }
  }

  public int listenerCount() {
    return drawListeners.size();
  }

}
