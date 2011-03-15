/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.buttonkit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;


final class PushButtonDelegateLCA extends ButtonDelegateLCA {

  private static final String QX_TYPE = "org.eclipse.rwt.widgets.Button";

  private static final Object[] PARAM_PUSH = new Object[] { "push" };
  private static final Object[] PARAM_TOGGLE = new Object[] { "toggle" };

  static final String PROP_DEFAULT = "defaultButton";

  void preserveValues( final Button button ) {    
    ButtonLCAUtil.preserveValues( button );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    adapter.preserve( PROP_DEFAULT,
                      Boolean.valueOf( isDefaultButton( button ) ) );
  }

  void readData( final Button button ) {
    ControlLCAUtil.processSelection( button, null, false );
    ButtonLCAUtil.readSelection( button );
    ControlLCAUtil.processMouseEvents( button );
    ControlLCAUtil.processKeyEvents( button );
    ControlLCAUtil.processMenuDetect( button );
    WidgetLCAUtil.processHelp( button );
  }

  void renderInitialization( final Button button ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( button );
    boolean isToggle = ( button.getStyle() & SWT.TOGGLE ) != 0;
    Object[] args = isToggle ? PARAM_TOGGLE : PARAM_PUSH;
    writer.newWidget( QX_TYPE, args );
    ControlLCAUtil.writeStyleFlags( button );
    WidgetLCAUtil.writeStyleFlag( button, SWT.PUSH, "PUSH" );
    WidgetLCAUtil.writeStyleFlag( button, SWT.FLAT, "FLAT" );
    WidgetLCAUtil.writeStyleFlag( button, SWT.TOGGLE, "TOGGLE" );
    ButtonLCAUtil.writeWrap( button );
  }

  // TODO [rh] highlight default button (e.g. with thick border as in Windows)
  void renderChanges( final Button button ) throws IOException {
    ButtonLCAUtil.writeChanges( button );
    writeDefault( button );
  }

  static void writeDefault( final Button button ) throws IOException {
    boolean isDefault = isDefaultButton( button );
    Boolean defValue = Boolean.FALSE;
    Boolean actValue = Boolean.valueOf( isDefault );
    if( WidgetLCAUtil.hasChanged( button, PROP_DEFAULT, actValue, defValue ) ) {
      if( isDefault ) {
        JSWriter writer = JSWriter.getWriterFor( button.getShell() );
        writer.set( "defaultButton", new Object[] { button } );
      }
    }
  }

  static boolean isDefaultButton( final Button button ) {
    return button.getShell().getDefaultButton() == button;
  }
}
