/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.ui.internal.progress;

import org.eclipse.rwt.lifecycle.UICallBack;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

// RAP [fappel]:
public final class ProgressUtil {
  
  private ProgressUtil() {
    // prevent instance creation
  }
  
  public static boolean isWorkbenchRunning( final Display display ) {
    final boolean[] result = new boolean[ 1 ];
    UICallBack.runNonUIThreadWithFakeContext( display, new Runnable() {
      public void run() {
        result[ 0 ] = PlatformUI.isWorkbenchRunning();
      }
    } );
    return result[ 0 ];
  }
}
