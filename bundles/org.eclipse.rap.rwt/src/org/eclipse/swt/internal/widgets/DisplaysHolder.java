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
package org.eclipse.swt.internal.widgets;

import java.lang.ref.WeakReference;


public class DisplaysHolder {
  private WeakReference[] displays;
  
  public DisplaysHolder() {
    displays = new WeakReference[ 4 ];
  }
  
  public WeakReference[] getDisplays() {
    return displays;
  }
  
  public void setDisplays( WeakReference[] displays ) {
    this.displays = displays;
  }
}
