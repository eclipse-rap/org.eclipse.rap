/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.scripting;

import org.eclipse.rap.rwt.scripting.ClientListener;


public class ClientListenerBinding {

  private final ClientListener listener;
  private final int eventType;

  public ClientListenerBinding( ClientListener listener, int eventType ) {
    this.listener = listener;
    this.eventType = eventType;
  }

  public ClientListener getListener() {
    return listener;
  }

  public int getEventType() {
    return eventType;
  }

}
