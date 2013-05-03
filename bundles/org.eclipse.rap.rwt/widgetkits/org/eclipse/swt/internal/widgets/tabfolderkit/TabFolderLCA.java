/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.tabfolderkit;

import static org.eclipse.rap.rwt.internal.protocol.JsonUtil.createJsonArray;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.getStyles;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.readEventPropertyValue;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.wasEventSent;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.find;
import static org.eclipse.rap.rwt.lifecycle.WidgetUtil.getId;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.ControlLCAUtil;
import org.eclipse.rap.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Widget;


public class TabFolderLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.TabFolder";
  private static final String[] ALLOWED_STYLES = new String[] {
    "TOP", "BOTTOM", "NO_RADIO_GROUP", "BORDER"
  };

  private static final String PROP_SELECTION = "selection";

  @Override
  public void preserveValues( Widget widget ) {
    TabFolder folder = ( TabFolder )widget;
    ControlLCAUtil.preserveValues( folder );
    WidgetLCAUtil.preserveCustomVariant( folder );
    preserveProperty( folder, PROP_SELECTION, getSelection( folder ) );
  }

  public void readData( Widget widget ) {
    TabFolder folder = ( TabFolder )widget;
    processSelectionEvent( folder );
    ControlLCAUtil.processEvents( folder );
    ControlLCAUtil.processKeyEvents( folder );
    ControlLCAUtil.processMenuDetect( folder );
    WidgetLCAUtil.processHelp( widget );
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    TabFolder folder = ( TabFolder )widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( folder );
    clientObject.create( TYPE );
    clientObject.set( "parent", getId( folder.getParent() ) );
    clientObject.set( "style", createJsonArray( getStyles( folder, ALLOWED_STYLES ) ) );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    TabFolder folder = ( TabFolder )widget;
    ControlLCAUtil.renderChanges( folder );
    WidgetLCAUtil.renderCustomVariant( folder );
    renderProperty( folder, PROP_SELECTION, getSelection( folder ), null );
  }

  //////////////////
  // Helping methods

  private static String getSelection( TabFolder folder ) {
    String selection = null;
    int selectionIndex = folder.getSelectionIndex();
    if( selectionIndex != -1 ) {
      selection = getId( folder.getItem( selectionIndex ) );
    }
    return selection;
  }

  private static void processSelectionEvent( final TabFolder folder ) {
    if( wasEventSent( folder, ClientMessageConst.EVENT_SELECTION ) ) {
      String itemId = readEventPropertyValue( folder,
                                              ClientMessageConst.EVENT_SELECTION,
                                              ClientMessageConst.EVENT_PARAM_ITEM );
      final TabItem item = ( TabItem )find( folder, itemId );
      if( item != null ) {
        ProcessActionRunner.add( new Runnable() {
          public void run() {
            folder.setSelection( item );
            preserveProperty( folder, PROP_SELECTION, getSelection( folder ) );
            ControlLCAUtil.processSelection( folder, item, false );
            ControlLCAUtil.processDefaultSelection( folder, item );
          }
        } );
      }
    }
  }

}
