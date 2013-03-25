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
package org.eclipse.rap.rwt.internal.client;



public class WidgetDataWhiteListImpl implements WidgetDataWhiteList {

  private String[] keys = null;

  public String[] getKeys() {
    return keys != null ? keys.clone() : null;
  }

  public void setKeys( String[] keys ) {
    this.keys = keys;
  }

}
