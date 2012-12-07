/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.remote;

import java.io.Serializable;
import java.util.Map;


/**
 * NOTE: This class is PROVISIONAL and may be exchanged with another mechanism of processing
 * operations from a remote object.
 */
public class RemoteOperationHandler implements Serializable {

  public void handleSet( Map<String, Object> properties ) {
  }

  public void handleCall( String method, Map<String, Object> properties ) {
  }

  public void handleNotify( String event, Map<String, Object> properties ) {
  }

}
