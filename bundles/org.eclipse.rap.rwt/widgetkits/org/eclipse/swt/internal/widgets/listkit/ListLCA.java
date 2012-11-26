/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.listkit;

import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderListener;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;

import java.io.IOException;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.internal.protocol.ProtocolUtil;
import org.eclipse.rap.rwt.internal.util.NumberFormatUtil;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.widgets.IListAdapter;
import org.eclipse.swt.internal.widgets.ScrollBarLCAUtil;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Widget;


public class ListLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.List";
  private static final String[] ALLOWED_STYLES = new String[] { "SINGLE", "MULTI", "BORDER" };

  private static final String PROP_ITEMS = "items";
  private static final String PROP_SELECTION_INDICES = "selectionIndices";
  private static final String PROP_TOP_INDEX = "topIndex";
  private static final String PROP_FOCUS_INDEX = "focusIndex";
  private static final String PROP_ITEM_DIMENSIONS = "itemDimensions";
  private static final String PROP_SELECTION_LISTENER = "Selection";
  private static final String PROP_DEFAULT_SELECTION_LISTENER = "DefaultSelection";
  private static final String PROP_MARKUP_ENABLED = "markupEnabled";

  private static final String[] DEFAUT_ITEMS = new String[ 0 ];
  private static final int[] DEFAUT_SELECTION_INDICES = new int[ 0 ];
  private static final int DEFAULT_TOP_INDEX = 0;
  private static final int DEFAULT_FOCUS_INDEX = -1;
  private static final Point DEFAULT_ITEM_DIMENSIONS = new Point( 0, 0 );

  @Override
  public void preserveValues( Widget widget ) {
    List list = ( List  )widget;
    ControlLCAUtil.preserveValues( list );
    WidgetLCAUtil.preserveCustomVariant( list );
    preserveProperty( list, PROP_ITEMS, list.getItems() );
    preserveProperty( list, PROP_SELECTION_INDICES, list.getSelectionIndices() );
    preserveProperty( list, PROP_TOP_INDEX, list.getTopIndex() );
    preserveProperty( list, PROP_FOCUS_INDEX, list.getFocusIndex() );
    preserveProperty( list, PROP_ITEM_DIMENSIONS, getItemDimensions( list ) );
    preserveListener( list, PROP_SELECTION_LISTENER, list.isListening( SWT.Selection ) );
    preserveListener( list,
                      PROP_DEFAULT_SELECTION_LISTENER,
                      list.isListening( SWT.DefaultSelection ) );
    ScrollBarLCAUtil.preserveValues( list );
  }

  public void readData( Widget widget ) {
    List list = ( List )widget;
    readTopIndex( list );
    readSelection( list );
    readFocusIndex( list );
    ControlLCAUtil.processSelection( list, null, true );
    ControlLCAUtil.processDefaultSelection( list, null );
    ControlLCAUtil.processEvents( list );
    ControlLCAUtil.processKeyEvents( list );
    ControlLCAUtil.processMenuDetect( list );
    WidgetLCAUtil.processHelp( list );
    ScrollBarLCAUtil.processSelectionEvent( list );
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    List list = ( List )widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( list );
    clientObject.create( TYPE );
    clientObject.set( "parent", WidgetUtil.getId( list.getParent() ) );
    clientObject.set( "style", WidgetLCAUtil.getStyles( list, ALLOWED_STYLES ) );
    clientObject.set( PROP_MARKUP_ENABLED, isMarkupEnabled( list ) );
    ScrollBarLCAUtil.renderInitialization( list );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    List list = ( List )widget;
    ControlLCAUtil.renderChanges( list );
    WidgetLCAUtil.renderCustomVariant( list );
    renderProperty( list, PROP_ITEMS, list.getItems(), DEFAUT_ITEMS );
    renderProperty( list,
                    PROP_SELECTION_INDICES,
                    list.getSelectionIndices(),
                    DEFAUT_SELECTION_INDICES );
    renderProperty( list, PROP_TOP_INDEX, list.getTopIndex(), DEFAULT_TOP_INDEX );
    renderProperty( list, PROP_FOCUS_INDEX, list.getFocusIndex(), DEFAULT_FOCUS_INDEX );
    renderListener( list, PROP_SELECTION_LISTENER, list.isListening( SWT.Selection ), false );
    renderListener( list,
                    PROP_DEFAULT_SELECTION_LISTENER,
                    list.isListening( SWT.DefaultSelection ),
                    false );
    renderProperty( list,
                    PROP_ITEM_DIMENSIONS,
                    getItemDimensions( list ),
                    DEFAULT_ITEM_DIMENSIONS );
    ScrollBarLCAUtil.renderChanges( list );
  }

  ////////////////////////////////////////////
  // Helping methods to read client-side state

  private static void readSelection( List list ) {
    int[] value = ProtocolUtil.readPropertyValueAsIntArray( getId( list ), "selection" );
    if( value != null ) {
      list.setSelection( value );
    }
  }

  private static void readTopIndex( List list ) {
    String value = WidgetLCAUtil.readPropertyValue( list, PROP_TOP_INDEX );
    if( value != null ) {
      list.setTopIndex( NumberFormatUtil.parseInt( value ) );
    }
  }

  private static void readFocusIndex( List list ) {
    String paramValue = WidgetLCAUtil.readPropertyValue( list, PROP_FOCUS_INDEX );
    if( paramValue != null ) {
      int focusIndex = NumberFormatUtil.parseInt( paramValue );
      getAdapter( list ).setFocusIndex( focusIndex );
    }
  }

  //////////////////
  // Helping methods

  private static boolean isMarkupEnabled( List list ) {
    return Boolean.TRUE.equals( list.getData( RWT.MARKUP_ENABLED ) );
  }

  private static Point getItemDimensions( List list ) {
    return getAdapter( list ).getItemDimensions();
  }

  private static IListAdapter getAdapter( List list ) {
    return list.getAdapter( IListAdapter.class );
  }
}
