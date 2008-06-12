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

package org.eclipse.swt.internal.widgets.sashkit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.internal.widgets.Props;
import org.eclipse.swt.widgets.*;


public final class SashLCA extends AbstractWidgetLCA {

  private static final String QX_TYPE = "org.eclipse.swt.widgets.Sash";

//  private static final String TYPE_POOL_ID = SashLCA.class.getName();

  public void preserveValues( final Widget widget ) {
    ControlLCAUtil.preserveValues( ( Control )widget );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      SelectionEvent.getListeners( widget ) );
  }

  public void readData( final Widget widget ) {
    // TODO [rh] clarify whether bounds should be sent (last parameter)
    ControlLCAUtil.processSelection( widget, null, true );
    ControlLCAUtil.processMouseEvents( ( Sash )widget );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    Sash sash = ( Sash )widget;
    JSWriter writer = JSWriter.getWriterFor( sash );
    writer.newWidget( QX_TYPE );
    JSVar orientation
      = ( sash.getStyle() & SWT.HORIZONTAL ) != 0
      ? JSConst.QX_CONST_HORIZONTAL_ORIENTATION
      : JSConst.QX_CONST_VERTICAL_ORIENTATION;
    writer.set( JSConst.QX_FIELD_ORIENTATION, orientation );
    WidgetLCAUtil.writeCustomVariant( widget );
    ControlLCAUtil.writeStyleFlags( sash );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Sash sash = ( Sash )widget;
    ControlLCAUtil.writeChanges( sash );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  public void createResetHandlerCalls( final String typePoolId )
    throws IOException
  {
    ControlLCAUtil.resetChanges();
    ControlLCAUtil.resetStyleFlags();
  }

  public String getTypePoolId( final Widget widget ) {
//    return TYPE_POOL_ID;
    // TODO [bm] disabled due to cursor problems
    return null;
  }
}
