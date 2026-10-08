/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
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
package org.eclipse.swt.internal.widgets.progressbarkit;

import org.eclipse.rap.rwt.internal.theme.WidgetMatcher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.widgets.controlkit.ControlThemeAdapterImpl;
import org.eclipse.swt.widgets.ProgressBar;


public class ProgressBarThemeAdapter extends ControlThemeAdapterImpl {

  @Override
  protected void configureMatcher( WidgetMatcher matcher ) {
    matcher.addStyle( "HORIZONTAL", SWT.HORIZONTAL );
    matcher.addStyle( "VERTICAL", SWT.VERTICAL );
  }

  public int getProgressBarWidth( ProgressBar progressBar ) {
    return getCssDimension( "ProgressBar", "width", progressBar );
  }

}
