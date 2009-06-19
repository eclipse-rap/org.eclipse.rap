/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.program;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

// RAP [bm]: e4-enabling hacks
public class Program {

  public static Program findProgram( String extension ) {
    return new Program() {
    };
  }

  public ImageData getImageData() {
    return Display.getCurrent()
      .getSystemImage( SWT.ICON_INFORMATION )
      .getImageData();
  }
}
