/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.coolbarkit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;


public class CoolBarLCA extends AbstractWidgetLCA {

  public void preserveValues( final Widget widget ) {
    CoolBar coolBar = ( CoolBar )widget;
    ControlLCAUtil.preserveValues( coolBar );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( coolBar );
    adapter.preserve( Props.LOCKED, Boolean.valueOf( coolBar.getLocked() ) );
    WidgetLCAUtil.preserveCustomVariant( coolBar );
  }

  public void readData( final Widget widget ) {
    Control coolBar = ( Control )widget;
    ControlLCAUtil.processMouseEvents( coolBar );
    ControlLCAUtil.processKeyEvents( coolBar );
    WidgetLCAUtil.processHelp( coolBar );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    CoolBar coolBar = ( CoolBar )widget;
    JSWriter writer = JSWriter.getWriterFor( coolBar );
    writer.newWidget( "qx.ui.layout.CanvasLayout" );
    // TODO [rh] use constant from qx.constant.Style.js
    writer.set( "overflow", "hidden" );
    writer.set( JSConst.QX_FIELD_APPEARANCE, "coolbar" );
    ControlLCAUtil.writeStyleFlags( coolBar );    
  }

  public void renderChanges( final Widget widget ) throws IOException {
    CoolBar coolBar = ( CoolBar )widget;
    ControlLCAUtil.writeChanges( coolBar );
    WidgetLCAUtil.writeCustomVariant( coolBar );
}

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }
}
