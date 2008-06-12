/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.demo.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public final class Util {

  private Util() {
  }

  public static void textSizeAdjustment( final Label label, 
                                         final Control control )
  {
    final Composite parent = control.getParent();
    parent.addControlListener( new ControlAdapter() {

      public void controlResized( final ControlEvent e ) {
        int height = label.computeSize( SWT.DEFAULT, SWT.DEFAULT ).y;
        int width = height * 3;
        if( parent.getLayout() instanceof RowLayout ) {
          control.setLayoutData( new RowData( width, height ) );
        } else if( parent.getLayout() instanceof GridLayout ) {
          control.setLayoutData( new GridData( width, height ) );
        }
      }
    } );
  }
}
