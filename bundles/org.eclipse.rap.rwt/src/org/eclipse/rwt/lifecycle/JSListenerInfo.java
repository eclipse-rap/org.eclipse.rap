/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.lifecycle;


public final class JSListenerInfo {
  
  private final String eventType;
  private final String jsListener;
  private final JSListenerType jsListenerType;
  
  public JSListenerInfo( final String eventType,
                         final String jsListener,
                         final JSListenerType jsListenerType )
  {
    this.eventType = eventType;
    this.jsListener = jsListener;
    this.jsListenerType = jsListenerType;
  }
  
  public String getEventType() {
    return eventType;
  }
  
  public String getJSListener() {
    return jsListener;
  }
  
  public JSListenerType getJSListenerType() {
    return jsListenerType;
  }
}
