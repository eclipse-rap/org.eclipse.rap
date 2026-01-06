/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - bug 348056: Eliminate compiler warnings
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.swt.widgets.Display;


public final class DisplaysHolder {

  private final Map<Thread, WeakReference<Display>> displays;

  public DisplaysHolder() {
    displays = Collections.synchronizedMap( new WeakHashMap<>() );
  }

  public void addDisplay( Thread tread, Display display ) {
    displays.put( tread, new WeakReference<>( display ) );
  }

  public Display getDisplay( Thread thread ) {
    WeakReference<Display> wr = displays.get( thread );
    if ( wr != null ) {
      return wr.get();
    }
    return null;
  }

}
