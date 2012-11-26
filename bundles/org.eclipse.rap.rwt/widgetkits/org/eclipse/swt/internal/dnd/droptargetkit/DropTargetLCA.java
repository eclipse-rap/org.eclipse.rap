/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.dnd.droptargetkit;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.internal.dnd.dragsourcekit.DNDLCAUtil;
import org.eclipse.swt.widgets.Widget;


public final class DropTargetLCA extends AbstractWidgetLCA {

  private static final Transfer[] DEFAULT_TRANSFER = new Transfer[ 0 ];

  private static final String PROP_CONTROL = "control";
  private static final String PROP_TRANSFER = "transfer";
  private static final String TYPE = "rwt.widgets.DropTarget";

  public void preserveValues( Widget widget ) {
    DropTarget dropTarget = ( DropTarget )widget;
    IWidgetAdapter adapter = WidgetUtil.getAdapter( dropTarget );
    adapter.preserve( PROP_CONTROL, dropTarget.getControl() );
    adapter.preserve( PROP_TRANSFER, dropTarget.getTransfer() );
  }

  public void readData( Widget widget ) {
  }

  public void renderInitialization( Widget widget ) throws IOException {
    DropTarget dropTarget = ( DropTarget )widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( dropTarget );
    clientObject.create( TYPE );
    clientObject.set( "control", WidgetUtil.getId( dropTarget.getControl() ) );
    clientObject.set( "style", DNDLCAUtil.convertOperations( dropTarget.getStyle() ) );
  }

  public void renderChanges( Widget widget ) throws IOException {
    DropTarget dropTarget = ( DropTarget )widget;
    renderTransfer( dropTarget );
  }

  private static void renderTransfer( DropTarget dropTarget ) {
    Transfer[] newValue = dropTarget.getTransfer();
    if( WidgetLCAUtil.hasChanged( dropTarget, PROP_TRANSFER, newValue, DEFAULT_TRANSFER ) ) {
      String[] renderValue = DNDLCAUtil.convertTransferTypes( newValue );
      ClientObjectFactory.getClientObject( dropTarget ).set( "transfer", renderValue );
    }
  }

}
