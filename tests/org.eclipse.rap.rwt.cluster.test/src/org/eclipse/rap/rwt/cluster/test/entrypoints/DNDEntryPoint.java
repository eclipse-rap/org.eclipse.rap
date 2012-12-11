/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.cluster.test.entrypoints;

import java.io.Serializable;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.service.UISession;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;


public class DNDEntryPoint implements EntryPoint {

  public static final String ID_SOURCE_LABEL = "sourceLabel";
  public static final String ID_TARGET_LABEL = "targetLabel";

  private static final String TRANSFER_DATA = "transfer data";
  private static final String ATTR_DRAG_FINISHED = "dragFinished";
  private static final String ATTR_DROP_FINISHED = "dropFinished";

  public static boolean isDragFinished( UISession uiSession ) {
    return Boolean.TRUE.equals( uiSession.getAttribute( ATTR_DRAG_FINISHED ) );
  }

  public static boolean isDropFinished( UISession uiSession ) {
    return Boolean.TRUE.equals( uiSession.getAttribute( ATTR_DROP_FINISHED ) );
  }

  public int createUI() {
    Display display = new Display();
    Shell shell = new Shell( display );
    Label sourceLabel = new Label( shell, SWT.NONE );
    sourceLabel.setText( "source label" );
    assignWidgetId( sourceLabel, ID_SOURCE_LABEL );
    DragSource dragSource = new DragSource( sourceLabel, DND.DROP_MOVE );
    dragSource.setTransfer( new Transfer[] { TextTransfer.getInstance() } );
    dragSource.addDragListener( new LabelDragSourceListener() );
    Label targetLabel = new Label( shell, SWT.NONE );
    targetLabel.setText( "target label" );
    assignWidgetId( targetLabel, ID_TARGET_LABEL );
    DropTarget dropTarget = new DropTarget( targetLabel, DND.DROP_MOVE );
    dropTarget.setTransfer( new Transfer[] { TextTransfer.getInstance() } );
    dropTarget.addDropListener( new LabelDropTargetListener() );
    shell.open();
    return 0;
  }

  private static void assignWidgetId( Widget widget, String id ) {
    widget.setData( WidgetUtil.CUSTOM_WIDGET_ID, id );
  }

  private static class LabelDragSourceListener extends DragSourceAdapter implements Serializable {

    public void dragSetData( DragSourceEvent event ) {
      event.data = TRANSFER_DATA;
    }

    public void dragFinished( DragSourceEvent event ) {
      RWT.getUISession().setAttribute( ATTR_DRAG_FINISHED, Boolean.TRUE );
    }
  }

  private static class LabelDropTargetListener extends DropTargetAdapter implements Serializable {

    public void drop( DropTargetEvent event ) {
      if( TRANSFER_DATA.equals( event.data ) ) {
        RWT.getUISession().setAttribute( ATTR_DROP_FINISHED, Boolean.TRUE );
      }
    }
  }
}
