/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
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

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.IRenderRunnable;
import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.internal.widgets.ItemLCAUtil;
import org.eclipse.swt.internal.widgets.WidgetAdapter;
import org.eclipse.swt.widgets.*;


public class TabItemLCA extends AbstractWidgetLCA {

  private static final String PROP_SELECTED = "selected";
  private static final String PROP_CONTROL = "control";

  public void preserveValues( Widget widget ) {
    TabItem item = ( TabItem )widget;
    ItemLCAUtil.preserve( item );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_SELECTED, Boolean.valueOf( isSelected( item ) ) );
    adapter.preserve( PROP_CONTROL, item.getControl() );
    WidgetLCAUtil.preserveToolTipText( item, item.getToolTipText() );
    WidgetLCAUtil.preserveCustomVariant( item );
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
    JSWriter writer = JSWriter.getWriterFor( widget );
    TabFolder parent = tabItem.getParent();
    Object[] args = new Object[] {
      WidgetUtil.getId( tabItem ),
      WidgetUtil.getId( parent ),
      new Integer( parent.indexOf( tabItem ) )
    };
    writer.callStatic( "org.eclipse.swt.TabUtil.createTabItem", args );
  }

  public void renderChanges( Widget widget ) throws IOException {
    TabItem tabItem = ( TabItem )widget;
    writeControlJsParent( tabItem );
    ItemLCAUtil.writeChanges( tabItem );
    writeSelection( tabItem );
    WidgetLCAUtil.writeToolTip( tabItem, tabItem.getToolTipText() );
    WidgetLCAUtil.writeCustomVariant( tabItem );
  }

  public void renderDispose( Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    Object[] args = new Object[]{ WidgetUtil.getId( widget ), };
    writer.callStatic( "org.eclipse.swt.TabUtil.releaseTabItem", args );
    writer.dispose();
  }

  //////////////////
  // helping methods

  private void writeSelection( TabItem item ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( item );
    Boolean newValue = Boolean.valueOf( isSelected( item ) );
    writer.set( PROP_SELECTED, "checked", newValue, Boolean.FALSE );
  }

  private boolean isSelected( TabItem tabItem ) {
    TabFolder parent = tabItem.getParent();
    int selectionIndex = parent.getSelectionIndex();
    return selectionIndex != -1 && parent.getItem( selectionIndex ) == tabItem;
  }

  private static void writeControlJsParent( TabItem tabItem ) {
    Control control = tabItem.getControl();
    if( WidgetLCAUtil.hasChanged( tabItem, PROP_CONTROL, control, null ) ) {
      if( control != null ) {
        final JSWriter writer = JSWriter.getWriterFor( control );
        final String jsParentId = WidgetUtil.getId( tabItem ) + "pg";
        WidgetAdapter adapter = ( WidgetAdapter )WidgetUtil.getAdapter( control );
        adapter.setRenderRunnable( new IRenderRunnable() {
          public void afterRender() throws IOException {
            writer.setParent( jsParentId );
          }
        } );
      }
    }
  }
}
