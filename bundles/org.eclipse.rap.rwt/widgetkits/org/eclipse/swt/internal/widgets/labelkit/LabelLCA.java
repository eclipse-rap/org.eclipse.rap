/*******************************************************************************
 * Copyright (c) 2002, 2015 Innoopract Informationssysteme GmbH and others.
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
package org.eclipse.swt.internal.widgets.labelkit;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.lifecycle.WidgetLCA;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;


public class LabelLCA extends WidgetLCA<Label> {

  public static final LabelLCA INSTANCE = new LabelLCA();

  @Override
  public void preserveValues( Label label ) {
    getDelegate( label ).preserveValues( label );
  }

  @Override
  public void renderInitialization( Label label ) throws IOException {
    getDelegate( label ).renderInitialization( label );
  }

  @Override
  public void renderChanges( Label label ) throws IOException {
    getDelegate( label ).renderChanges( label );
  }

  private static AbstractLabelLCADelegate getDelegate( Widget widget ) {
    if( ( widget.getStyle() & SWT.SEPARATOR ) != 0 ) {
      return SeparatorLabelLCA.INSTANCE;
    }
    return StandardLabelLCA.INSTANCE;
  }

  private LabelLCA() {
    // prevent instantiation
  }

}
