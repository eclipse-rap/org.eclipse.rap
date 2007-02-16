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

package org.eclipse.rap.demo;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.layout.*;
import org.eclipse.rap.rwt.lifecycle.IEntryPoint;
import org.eclipse.rap.rwt.widgets.*;

public class Snippet implements IEntryPoint{

  public Display createUI() {
    Display display = new Display();
    Shell shell = new Shell( display, RWT.SHELL_TRIM );

    // Create the layout.
    RowLayout rowLayout = new RowLayout();

    // Optionally set layout fields.
    rowLayout.marginLeft = 5;
    rowLayout.marginTop = 5;
    rowLayout.marginRight = 5;
    rowLayout.marginBottom = 5;
    rowLayout.spacing = 5;
    rowLayout.wrap = true;
    rowLayout.pack = true;
    rowLayout.justify = false;

    // Set the layout into the composite.
    shell.setLayout( rowLayout );

    // Create the children of the composite.
    new Button( shell, RWT.PUSH ).setText("B1");
    new Button( shell, RWT.PUSH ).setText("Wide Button 2");
    new Button( shell, RWT.PUSH ).setText("Button 3");

    shell.pack();
    shell.open();

    return display;
  }
}
