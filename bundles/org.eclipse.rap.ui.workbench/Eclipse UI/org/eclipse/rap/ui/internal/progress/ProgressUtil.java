/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/

package org.eclipse.rap.ui.internal.progress;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

// RAP [fappel]:
public final class ProgressUtil {
  
  private ProgressUtil() {
    // prevent instance creation
  }
  
  public static boolean isWorkbenchRunning( final Display display ) {
    final boolean[] result = new boolean[ 1 ];
    RWT.getUISession( display ).exec( new Runnable() {
      public void run() {
        result[ 0 ] = PlatformUI.isWorkbenchRunning();
      }
    } );
    return result[ 0 ];
  }
}
