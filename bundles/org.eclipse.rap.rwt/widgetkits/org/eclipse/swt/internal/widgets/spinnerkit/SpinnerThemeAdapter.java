/*******************************************************************************
 * Copyright (c) 2007, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.spinnerkit;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapterImpl;
import org.eclipse.swt.widgets.Spinner;


public class SpinnerThemeAdapter extends ControlThemeAdapterImpl {

  public Rectangle getFieldPadding( Spinner spinner ) {
    return getCssBoxDimensions( "Spinner-Field", "padding", spinner );
  }

  public int getButtonWidth( Spinner spinner ) {
    int upButtonWidth = getCssDimension( "Spinner-UpButton",
                                         "width",
                                         spinner );
    int downButtonWidth = getCssDimension( "Spinner-DownButton",
                                           "width",
                                           spinner );
    return Math.max( upButtonWidth, downButtonWidth );
  }

}
