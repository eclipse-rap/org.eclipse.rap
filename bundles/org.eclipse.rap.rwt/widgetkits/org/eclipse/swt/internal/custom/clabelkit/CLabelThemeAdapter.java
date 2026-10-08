/*******************************************************************************
 * Copyright (c) 2009, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.custom.clabelkit;

import org.eclipse.rap.rwt.theme.BoxDimensions;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapterImpl;


public class CLabelThemeAdapter extends ControlThemeAdapterImpl {

  private static final BoxDimensions ONE = new BoxDimensions( 1, 1, 1, 1 );

  public BoxDimensions getBorder( CLabel clabel ) {
    int style = clabel.getStyle();
    if( ( style & SWT.SHADOW_IN ) != 0 || ( style & SWT.SHADOW_OUT ) != 0 ) {
      return ONE;
    }
    return super.getBorder( clabel );
  }

  public int getSpacing( CLabel clabel ) {
    return getCssDimension( "CLabel", "spacing", clabel );
  }

}
