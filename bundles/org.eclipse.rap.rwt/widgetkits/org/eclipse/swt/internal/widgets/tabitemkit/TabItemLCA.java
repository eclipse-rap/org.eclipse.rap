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
package org.eclipse.swt.internal.widgets.tabitemkit;

import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderProperty;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.lifecycle.JSConst;
import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.internal.widgets.ItemLCAUtil;
import org.eclipse.swt.widgets.*;


public class TabItemLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.TabItem";

  private static final String PROP_CONTROL = "control";

  public void preserveValues( Widget widget ) {
    TabItem item = ( TabItem )widget;
    WidgetLCAUtil.preserveCustomVariant( item );
    WidgetLCAUtil.preserveToolTipText( item, item.getToolTipText() );
    ItemLCAUtil.preserve( item );
    preserveProperty( item, PROP_CONTROL, item.getControl() );
  }

  public void readData( Widget widget ) {
    // TODO [rh] same hack as in CTabFolderLCA#readData
    // Read selected item and process selection event
    final TabItem item = ( TabItem )widget;
    if( WidgetLCAUtil.wasEventSent( item, JSConst.EVENT_WIDGET_SELECTED_ITEM ) ) {
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          TabFolder folder = item.getParent();
          folder.setSelection( item );
          ControlLCAUtil.processSelection( folder, item, false );
        }
      } );
    }
  }

  public void renderInitialization( Widget widget ) throws IOException {
    TabItem tabItem = ( TabItem )widget;
    TabFolder parent = tabItem.getParent();
    IClientObject clientObject = ClientObjectFactory.getClientObject( tabItem );
    clientObject.create( TYPE );
    clientObject.set( "id", WidgetUtil.getId( tabItem ) );
    clientObject.set( "parent", WidgetUtil.getId( parent ) );
    clientObject.set( "index", parent.indexOf( tabItem ) ) ;
  }

  public void renderChanges( Widget widget ) throws IOException {
    TabItem tabItem = ( TabItem )widget;
    WidgetLCAUtil.renderCustomVariant( tabItem );
    WidgetLCAUtil.renderToolTip( tabItem, tabItem.getToolTipText() );
    ItemLCAUtil.renderChanges( tabItem );
    renderProperty( tabItem, PROP_CONTROL, tabItem.getControl(), null );
  }

  public void renderDispose( Widget widget ) throws IOException {
    ClientObjectFactory.getClientObject( widget ).destroy();
  }
}
