/*******************************************************************************
 * Copyright (c) 2011, 2015 EclipseSource and others.
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
package org.eclipse.swt.internal.widgets.listkit;

import org.eclipse.rap.rwt.theme.BoxDimensions;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapterImpl;
import org.eclipse.swt.widgets.Control;


public class ListThemeAdapter extends ControlThemeAdapterImpl {

  public BoxDimensions getItemPadding( Control control ) {
    return getCssBoxDimensions( "List-Item", "padding", control ).dimensions;
  }

}
