/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.dnd.dragsourcekit;

import java.io.IOException;

import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.internal.dnd.IDNDAdapter;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;


public final class DragSourceLCA extends AbstractWidgetLCA {

  private static final String JSFUNC_REGISTER
    = "org.eclipse.rwt.DNDSupport.getInstance().registerDragSource";
  private static final String JSFUNC_DEREGISTER
    = "org.eclipse.rwt.DNDSupport.getInstance().deregisterDragSource";
  private static final String JSFUNC_SET_TRANSFER_TYPES
    = "org.eclipse.rwt.DNDSupport.getInstance().setDragSourceTransferTypes";
  private static final String JSFUNC_SET_OPERATION_OVERWRITE
    = "org.eclipse.rwt.DNDSupport.getInstance().setOperationOverwrite";

  private static final Transfer[] DEFAULT_TRANSFER = new Transfer[ 0 ];

  private static final String PROP_CONTROL = "control";
  private static final String PROP_TRANSFER = "transfer";

  public void preserveValues( final Widget widget ) {
    DragSource dragSource = ( DragSource )widget;
    IWidgetAdapter adapter = WidgetUtil.getAdapter( dragSource );
    adapter.preserve( PROP_CONTROL, dragSource.getControl() );
    adapter.preserve( PROP_TRANSFER, dragSource.getTransfer() );
  }

  public void readData( final Widget widget ) {
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    DragSource dragSource = ( DragSource )widget;
    JSWriter writer = JSWriter.getWriterFor( dragSource );
    String[] operations = DNDLCAUtil.convertOperations( dragSource.getStyle() );
    Object[] args = new Object[]{ dragSource.getControl(), operations };
    writer.callStatic( JSFUNC_REGISTER, args );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    DragSource dragSource = ( DragSource )widget;
    writeTransfer( dragSource );
    writeDetail( dragSource );
    writeCancel( dragSource );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    DragSource dragSource = ( DragSource )widget;
    IWidgetAdapter adapter = WidgetUtil.getAdapter( dragSource );
    Control control = ( Control )adapter.getPreserved( PROP_CONTROL );
    JSWriter writer = JSWriter.getWriterFor( dragSource );
    writer.callStatic( JSFUNC_DEREGISTER, new Object[]{ control } );
  }

  private static void writeTransfer( final DragSource dragSource )
    throws IOException
  {
    Transfer[] newValue = dragSource.getTransfer();
    if( WidgetLCAUtil.hasChanged( dragSource,
                                  PROP_TRANSFER,
                                  newValue,
                                  DEFAULT_TRANSFER ) )
    {
        JSWriter writer = JSWriter.getWriterFor( dragSource );
        Object[] args = new Object[]{
          dragSource.getControl(),
          DNDLCAUtil.convertTarnsferTypes( newValue )
        };
        writer.callStatic( JSFUNC_SET_TRANSFER_TYPES, args );
    }
  }

  private void writeDetail( final DragSource dragSource ) throws IOException {
    IDNDAdapter dndAdapter
      = ( IDNDAdapter )dragSource.getAdapter( IDNDAdapter.class  );
    if( dndAdapter.hasDetailChanged() ) {
      JSWriter writer = JSWriter.getWriterFor( dragSource );
      Object[] args = new Object[]{
        dndAdapter.getDetailChangedControl(),
        convertOperation( dndAdapter.getDetailChangedValue() )
      };
      writer.callStatic( JSFUNC_SET_OPERATION_OVERWRITE, args );
    }
  }

  private static void writeCancel( final DragSource dragSource )
    throws IOException
  {
    IDNDAdapter dndAdapter
      = ( IDNDAdapter )dragSource.getAdapter( IDNDAdapter.class  );
    if( dndAdapter.isCanceled() ) {
      JSWriter writer = JSWriter.getWriterFor( dragSource );
      String function = "org.eclipse.rwt.DNDSupport.getInstance().cancel";
      writer.callStatic( function, new Object[]{} );
    }
  }

  private static String convertOperation( final int operation ) {
    String result = "none";
    switch( operation ) {
      case DND.DROP_COPY:
        result = "copy";
      break;
      case DND.DROP_MOVE:
        result = "move";
      break;
      case DND.DROP_LINK:
        result = "link";
      break;
    }
    return result;
  }
}
