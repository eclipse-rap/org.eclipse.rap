/*******************************************************************************
 * Copyright (c) 2008, 2015 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.coolbarkit;

import org.eclipse.rap.rwt.theme.BoxDimensions;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapterImpl;
import org.eclipse.swt.widgets.Control;


public class CoolBarThemeAdapter extends ControlThemeAdapterImpl {

  private static final BoxDimensions ZERO = new BoxDimensions( 0, 0, 0, 0 );

  @Override
  public BoxDimensions getBorder( Control control ) {
    return ZERO;
  }

  public int getHandleWidth( Control control ) {
    return getCssDimension( "CoolItem-Handle", "width", control );
  }

}
