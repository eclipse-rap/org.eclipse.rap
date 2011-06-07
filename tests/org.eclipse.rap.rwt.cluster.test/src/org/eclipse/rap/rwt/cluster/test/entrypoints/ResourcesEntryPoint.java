/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.cluster.test.entrypoints;

import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class ResourcesEntryPoint implements IEntryPoint {

  public int createUI() {
    Display display = new Display();
    Shell shell = new Shell( display );
    shell.setFont( new Font( display, "font-name", 12, SWT.BOLD ) );
    shell.setBackground( new Color( display, 1, 2, 3 ) );
    shell.setCursor( display.getSystemCursor( SWT.CURSOR_CROSS ) );
    return 0;
  }
}
