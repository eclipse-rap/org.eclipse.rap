/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.dnd.droptargetkit;

import static org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory.getClientObject;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.hasChanged;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;
import static org.eclipse.swt.internal.dnd.dragsourcekit.DNDLCAUtil.convertOperations;
import static org.eclipse.swt.internal.dnd.dragsourcekit.DNDLCAUtil.convertTransferTypes;
import static org.eclipse.swt.internal.events.EventLCAUtil.isListening;

import java.io.IOException;

import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Widget;


public final class DropTargetLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.DropTarget";
  private static final String PROP_TRANSFER = "transfer";
  private static final String PROP_DRAG_ENTER_LISTENER = "DragEnter";
  private static final String PROP_DRAG_OVER_LISTENER = "DragOver";
  private static final String PROP_DRAG_LEAVE_LISTENER = "DragLeave";
  private static final String PROP_DRAG_OPERATION_CHANGED_LISTENER = "DragOperationChanged";
  private static final String PROP_DROP_ACCEPT_LISTENER = "DropAccept";

  private static final Transfer[] DEFAULT_TRANSFER = new Transfer[ 0 ];

  @Override
  public void preserveValues( Widget widget ) {
    DropTarget dropTarget = ( DropTarget )widget;
    preserveProperty( dropTarget, PROP_TRANSFER, dropTarget.getTransfer() );
    preserveListener( dropTarget,
                      PROP_DRAG_ENTER_LISTENER,
                      isListening( dropTarget, DND.DragEnter ) );
    preserveListener( dropTarget,
                      PROP_DRAG_OVER_LISTENER,
                      isListening( dropTarget, DND.DragOver ) );
    preserveListener( dropTarget,
                      PROP_DRAG_LEAVE_LISTENER,
                      isListening( dropTarget, DND.DragLeave ) );
    preserveListener( dropTarget,
                      PROP_DRAG_OPERATION_CHANGED_LISTENER,
                      isListening( dropTarget, DND.DragOperationChanged ) );
    preserveListener( dropTarget,
                      PROP_DROP_ACCEPT_LISTENER,
                      isListening( dropTarget, DND.DropAccept ) );
  }

  public void readData( Widget widget ) {
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    DropTarget dropTarget = ( DropTarget )widget;
    IClientObject clientObject = getClientObject( dropTarget );
    clientObject.create( TYPE );
    clientObject.set( "control", getId( dropTarget.getControl() ) );
    clientObject.set( "style", convertOperations( dropTarget.getStyle() ) );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    DropTarget dropTarget = ( DropTarget )widget;
    renderTransfer( dropTarget );
    renderListener( dropTarget,
                    PROP_DRAG_ENTER_LISTENER,
                    isListening( dropTarget, DND.DragEnter ),
                    false );
    renderListener( dropTarget,
                    PROP_DRAG_OVER_LISTENER,
                    isListening( dropTarget, DND.DragOver ),
                    false );
    renderListener( dropTarget,
                    PROP_DRAG_LEAVE_LISTENER,
                    isListening( dropTarget, DND.DragLeave ),
                    false );
    renderListener( dropTarget,
                    PROP_DRAG_OPERATION_CHANGED_LISTENER,
                    isListening( dropTarget, DND.DragOperationChanged ),
                    false );
    renderListener( dropTarget,
                    PROP_DROP_ACCEPT_LISTENER,
                    isListening( dropTarget, DND.DropAccept ),
                    false );
  }

  private static void renderTransfer( DropTarget dropTarget ) {
    Transfer[] newValue = dropTarget.getTransfer();
    if( hasChanged( dropTarget, PROP_TRANSFER, newValue, DEFAULT_TRANSFER ) ) {
      JsonValue renderValue = convertTransferTypes( newValue );
      getClientObject( dropTarget ).set( "transfer", renderValue );
    }
  }

}
