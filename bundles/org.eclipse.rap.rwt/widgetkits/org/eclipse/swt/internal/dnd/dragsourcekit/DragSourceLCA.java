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
package org.eclipse.swt.internal.dnd.dragsourcekit;

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

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonObject;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.internal.dnd.IDNDAdapter;
import org.eclipse.swt.widgets.Widget;


public final class DragSourceLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.DragSource";
  private static final String PROP_TRANSFER = "transfer";
  private static final String PROP_DRAG_START_LISTENER = "DragStart";
  private static final String PROP_DRAG_END_LISTENER = "DragEnd";

  private static final Transfer[] DEFAULT_TRANSFER = new Transfer[ 0 ];

  @Override
  public void preserveValues( Widget widget ) {
    DragSource dragSource = ( DragSource )widget;
    preserveProperty( dragSource, PROP_TRANSFER, dragSource.getTransfer() );
    preserveListener( dragSource,
                      PROP_DRAG_START_LISTENER,
                      isListening( dragSource, DND.DragStart ) );
    preserveListener( dragSource, PROP_DRAG_END_LISTENER, isListening( dragSource, DND.DragEnd ) );
  }

  public void readData( Widget widget ) {
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    DragSource dragSource = ( DragSource )widget;
    IClientObject clientObject = getClientObject( dragSource );
    clientObject.create( TYPE );
    clientObject.set( "control", getId( dragSource.getControl() ) );
    clientObject.set( "style", convertOperations( dragSource.getStyle() ) );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    DragSource dragSource = ( DragSource )widget;
    renderTransfer( dragSource );
    // TODO [tb] : is there a better place to render these: ?
    renderDetail( dragSource );
    renderFeedback( dragSource );
    renderDataType( dragSource );
    renderCancel( dragSource );
    renderListener( dragSource,
                    PROP_DRAG_START_LISTENER,
                    isListening( dragSource, DND.DragStart ),
                    false );
    renderListener( dragSource,
                    PROP_DRAG_END_LISTENER,
                    isListening( dragSource, DND.DragEnd ),
                    false );
  }

  private static void renderTransfer( DragSource dragSource ) {
    Transfer[] newValue = dragSource.getTransfer();
    if( hasChanged( dragSource, PROP_TRANSFER, newValue, DEFAULT_TRANSFER ) ) {
      JsonValue renderValue = convertTransferTypes( newValue );
      getClientObject( dragSource ).set( "transfer", renderValue );
    }
  }

  private void renderDetail( DragSource dragSource ) {
    IDNDAdapter dndAdapter = dragSource.getAdapter( IDNDAdapter.class  );
    // TODO [tb] : would be rendered by all DragSources:
    if( dndAdapter.hasDetailChanged() ) {
      JsonArray operations = convertOperations( dndAdapter.getDetailChangedValue() );
      JsonObject parameters = new JsonObject()
        .add( "detail", operations.isEmpty() ? JsonValue.valueOf( "DROP_NONE" ) : operations.get( 0 ) )
        .add( "control", getId( dndAdapter.getDetailChangedControl() ) );
      getClientObject( dragSource ).call( "changeDetail", parameters );
    }
  }

  private void renderFeedback( DragSource dragSource ) {
    IDNDAdapter dndAdapter = dragSource.getAdapter( IDNDAdapter.class  );
    // TODO [tb] : would be rendered by all DragSources:
    if( dndAdapter.hasFeedbackChanged() ) {
      int value = dndAdapter.getFeedbackChangedValue();
      String feedbackChangedControlId = getId( dndAdapter.getFeedbackChangedControl() );
      JsonObject parameters = new JsonObject()
        .add( "control", feedbackChangedControlId )
        .add( "flags", value )
        .add( "feedback", convertFeedback( value ) );
      getClientObject( dragSource ).call( "changeFeedback", parameters );
    }
  }

  private void renderDataType( DragSource dragSource ) {
    IDNDAdapter dndAdapter = dragSource.getAdapter( IDNDAdapter.class  );
    // TODO [tb] : would be rendered by all DragSources:
    if( dndAdapter.hasDataTypeChanged() ) {
      String dataTypeChangedControlId = getId( dndAdapter.getDataTypeChangedControl() );
      JsonObject parameters = new JsonObject()
        .add( "control", dataTypeChangedControlId )
        .add( "dataType", dndAdapter.getDataTypeChangedValue().type );
      getClientObject( dragSource ).call( "changeDataType", parameters );
    }
  }

  private static void renderCancel( DragSource dragSource ) {
    IDNDAdapter dndAdapter = dragSource.getAdapter( IDNDAdapter.class  );
    // TODO [tb] : would be rendered by all DragSources:
    if( dndAdapter.isCanceled() ) {
      getClientObject( dragSource ).call( "cancel", null );
    }
  }

  private static JsonArray convertFeedback( int feedback ) {
    JsonArray feedbackNames = new JsonArray();
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
    return feedbackNames;
  }

}
