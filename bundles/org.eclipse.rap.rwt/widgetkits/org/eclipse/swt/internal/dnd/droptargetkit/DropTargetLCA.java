/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.dnd.droptargetkit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swt.internal.dnd.dragsourcekit.*;


public final class DropTargetLCA extends AbstractWidgetLCA {

  private static final String JSFUNC_REGISTER
    = "org.eclipse.rwt.DNDSupport.getInstance().registerDropTarget";
  private static final String JSFUNC_DEREGISTER
    = "org.eclipse.rwt.DNDSupport.getInstance().deregisterDropTarget";
  private static final String JSFUNC_SET_TRANSFER_TYPES
    = "org.eclipse.rwt.DNDSupport.getInstance().setDropTargetTransferTypes";

  private static final Transfer[] DEFAULT_TRANSFER = new Transfer[ 0 ];

  private static final String PROP_CONTROL = "control";
  private static final String PROP_TRANSFER = "transfer";

  public void preserveValues( final Widget widget ) {
    DropTarget dropTarget = ( DropTarget )widget;
    IWidgetAdapter adapter = WidgetUtil.getAdapter( dropTarget );
    adapter.preserve( PROP_CONTROL, dropTarget.getControl() );
    adapter.preserve( PROP_TRANSFER, dropTarget.getTransfer() );
  }

  public void readData( final Widget widget ) {
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    DropTarget dropTarget = ( DropTarget )widget;
    JSWriter writer = JSWriter.getWriterFor( dropTarget );
    String[] operations = DNDLCAUtil.convertOperations( dropTarget.getStyle() );
    Object[] args = new Object[]{ dropTarget.getControl(), operations };
    writer.callStatic( JSFUNC_REGISTER, args );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    DropTarget dropTarget = ( DropTarget )widget;
    writeTransfer( dropTarget );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    DropTarget dropTarget = ( DropTarget )widget;
    IWidgetAdapter adapter =
      ( IWidgetAdapter )dropTarget.getAdapter( IWidgetAdapter.class );
    JSWriter writer = JSWriter.getWriterFor( dropTarget );
    Control control = ( Control )adapter.getPreserved( PROP_CONTROL );
    writer.callStatic( JSFUNC_DEREGISTER, new Object[]{ control } );
  }

  private static void writeTransfer( final DropTarget dropTarget )
    throws IOException
  {
    Transfer[] newValue = dropTarget.getTransfer();
    if( WidgetLCAUtil.hasChanged( dropTarget,
                                  PROP_TRANSFER,
                                  newValue,
                                  DEFAULT_TRANSFER ) )
    {
      JSWriter writer = JSWriter.getWriterFor( dropTarget );
      Object[] args = new Object[]{
        dropTarget.getControl(),
        DNDLCAUtil.convertTransferTypes( newValue )
      };
      writer.callStatic( JSFUNC_SET_TRANSFER_TYPES, args );
    }
  }

}
