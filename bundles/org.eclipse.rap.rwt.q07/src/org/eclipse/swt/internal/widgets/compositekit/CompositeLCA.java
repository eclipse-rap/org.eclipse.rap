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

package org.eclipse.swt.internal.widgets.compositekit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.widgets.*;


public class CompositeLCA extends AbstractWidgetLCA {

  private static final String QX_TYPE = "qx.ui.layout.CanvasLayout";

  public void preserveValues( final Widget widget ) {
    ControlLCAUtil.preserveValues( ( Control )widget );
    WidgetLCAUtil.preserveCustomVariant( widget );
  }

  public void readData( final Widget widget ) {
    ControlLCAUtil.processMouseEvents( ( Control )widget );
    ControlLCAUtil.processKeyEvents( ( Control )widget );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    Composite composite = ( Composite )widget;
    JSWriter writer = JSWriter.getWriterFor( composite );
    writer.newWidget( QX_TYPE );
    writer.set( "overflow", "hidden" );
    writer.set( "hideFocus", true );
    writer.set( JSConst.QX_FIELD_APPEARANCE, "composite" );    
    ControlLCAUtil.writeStyleFlags( composite );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    ControlLCAUtil.writeChanges( ( Control )widget );
    WidgetLCAUtil.writeCustomVariant( widget );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }
}
