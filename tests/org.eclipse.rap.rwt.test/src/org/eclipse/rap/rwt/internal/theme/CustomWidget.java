/*******************************************************************************
 * Copyright (c) 2008, 2012 Innoopract Informationssysteme GmbH and others.
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
package org.eclipse.rap.rwt.internal.theme;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;


public class CustomWidget extends Canvas {
  private static final long serialVersionUID = 1L;

  public CustomWidget( Composite parent, int style ) {
    super( parent, style );
  }
}
