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
package org.eclipse.swt.internal.dnd.dragsourcekit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.internal.dnd.IDNDAdapter;
import org.eclipse.swt.widgets.Widget;


public final class DragSourceLCA extends AbstractWidgetLCA {

  private static final Transfer[] DEFAULT_TRANSFER = new Transfer[ 0 ];

  private static final String PROP_TRANSFER = "transfer";
  private static final String TYPE = "rwt.widgets.DragSource";

  public void preserveValues( Widget widget ) {
    DragSource dragSource = ( DragSource )widget;
    IWidgetAdapter adapter = WidgetUtil.getAdapter( dragSource );
    adapter.preserve( PROP_TRANSFER, dragSource.getTransfer() );
  }

  public void readData( Widget widget ) {
  }

  public void renderInitialization( Widget widget ) throws IOException {
    DragSource dragSource = ( DragSource )widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( dragSource );
    clientObject.create( TYPE );
    clientObject.set( "control", WidgetUtil.getId( dragSource.getControl() ) );
    clientObject.set( "style", DNDLCAUtil.convertOperations( dragSource.getStyle() ) );
  }

  public void renderChanges( Widget widget ) throws IOException {
    DragSource dragSource = ( DragSource )widget;
    renderTransfer( dragSource );
    // TODO [tb] : is there a better place to render these: ?
    renderDetail( dragSource );
    renderFeedback( dragSource );
    renderDataType( dragSource );
    renderCancel( dragSource );
  }

  private static void renderTransfer( DragSource dragSource ) {
    Transfer[] newValue = dragSource.getTransfer();
    if( WidgetLCAUtil.hasChanged( dragSource, PROP_TRANSFER, newValue, DEFAULT_TRANSFER ) ) {
      String[] renderValue = DNDLCAUtil.convertTransferTypes( newValue );
      ClientObjectFactory.getClientObject( dragSource ).set( "transfer", renderValue );
    }
  }

  private void renderDetail( DragSource dragSource ) {
    IDNDAdapter dndAdapter = dragSource.getAdapter( IDNDAdapter.class  );
    // TODO [tb] : would be rendered by all DragSources:
    if( dndAdapter.hasDetailChanged() ) {
      String[] operations = DNDLCAUtil.convertOperations( dndAdapter.getDetailChangedValue() );
      String detail = operations.length > 0 ? operations[ 0 ] : "DROP_NONE";
      IClientObject clientObject = ClientObjectFactory.getClientObject( dragSource );
      Map<String, Object> properties = new HashMap<String, Object>();
      properties.put( "detail", detail );
      properties.put( "control", WidgetUtil.getId( dndAdapter.getDetailChangedControl() ) );
      clientObject.call( "changeDetail", properties );
    }
  }

  private void renderFeedback( DragSource dragSource ) {
    IDNDAdapter dndAdapter = dragSource.getAdapter( IDNDAdapter.class  );
    // TODO [tb] : would be rendered by all DragSources:
    if( dndAdapter.hasFeedbackChanged() ) {
      int value = dndAdapter.getFeedbackChangedValue();
      IClientObject clientObject = ClientObjectFactory.getClientObject( dragSource );
      Map<String, Object> properties = new HashMap<String, Object>();
      properties.put( "control", WidgetUtil.getId( dndAdapter.getFeedbackChangedControl() ) );
      properties.put( "flags", new Integer( value ) );
      properties.put( "feedback", convertFeedback( value ) );
      clientObject.call( "changeFeedback", properties );
    }
  }

  private void renderDataType( DragSource dragSource ) {
    IDNDAdapter dndAdapter = dragSource.getAdapter( IDNDAdapter.class  );
    // TODO [tb] : would be rendered by all DragSources:
    if( dndAdapter.hasDataTypeChanged() ) {
      IClientObject clientObject = ClientObjectFactory.getClientObject( dragSource );
      Map<String, Object> properties = new HashMap<String, Object>();
      properties.put( "control", WidgetUtil.getId( dndAdapter.getDataTypeChangedControl() ) );
      properties.put( "dataType", new Integer( dndAdapter.getDataTypeChangedValue().type ) );
      clientObject.call( "changeDataType", properties );
    }
  }

  private static void renderCancel( DragSource dragSource ) {
    IDNDAdapter dndAdapter = dragSource.getAdapter( IDNDAdapter.class  );
    // TODO [tb] : would be rendered by all DragSources:
    if( dndAdapter.isCanceled() ) {
      IClientObject clientObject = ClientObjectFactory.getClientObject( dragSource );
      clientObject.call( "cancel", null );
    }
  }

  private static String[] convertFeedback( int feedback ) {
    List<String> feedbackNames = new ArrayList<String>();
    if( ( feedback & DND.FEEDBACK_EXPAND ) != 0 ) {
      feedbackNames.add( "FEEDBACK_EXPAND" );
    }
    if( ( feedback & DND.FEEDBACK_INSERT_AFTER ) != 0 ) {
      feedbackNames.add( "FEEDBACK_INSERT_AFTER" );
    }
    if( ( feedback & DND.FEEDBACK_INSERT_BEFORE ) != 0 ) {
      feedbackNames.add( "FEEDBACK_INSERT_BEFORE" );
    }
    if( ( feedback & DND.FEEDBACK_SCROLL ) != 0 ) {
      feedbackNames.add( "FEEDBACK_SCROLL" );
    }
    if( ( feedback & DND.FEEDBACK_SELECT ) != 0 ) {
      feedbackNames.add( "FEEDBACK_SELECT" );
    }
    return feedbackNames.toArray( new String[ feedbackNames.size() ] );
  }
}
