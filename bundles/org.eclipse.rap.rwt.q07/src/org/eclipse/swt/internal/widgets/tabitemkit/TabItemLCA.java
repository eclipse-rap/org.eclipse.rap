/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.widgets.tabitemkit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.internal.widgets.ItemLCAUtil;
import org.eclipse.swt.internal.widgets.WidgetAdapter;
import org.eclipse.swt.widgets.*;


public class TabItemLCA extends AbstractWidgetLCA {

  private static final String PROP_SELECTED = "selected";

  private static final String JS_FUNC_TAB_SELECTED 
    = "org.eclipse.swt.TabUtil.tabSelected";
  private static final String QX_EVENT_CHANGE_CHECKED = "changeChecked";


  public void preserveValues( final Widget widget ) {
    TabItem item = ( TabItem )widget;
    ItemLCAUtil.preserve( item );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( PROP_SELECTED, Boolean.valueOf( isSelected( item ) ) );
  }
  
  public void readData( final Widget widget ) {
    // TODO [rh] same hack as in CTabFolderLCA#readData
    // Read selected item and process selection event
    final TabItem item = ( TabItem )widget;
    if( WidgetLCAUtil.wasEventSent( item, JSConst.EVENT_WIDGET_SELECTED_ITEM ) ) 
    {
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          TabFolder folder = item.getParent();
          folder.setSelection( item );
          ControlLCAUtil.processSelection( folder, item, false );
        }
      } );
    }
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    TabItem tabItem = ( TabItem )widget;
    JSWriter writer = JSWriter.getWriterFor( widget );
    TabFolder parent = tabItem.getParent();
    Object[] args = new Object[] { 
      WidgetUtil.getId( tabItem ), 
      WidgetUtil.getId( parent ),
      new Integer( parent.indexOf( tabItem ) )
    };
    writer.callStatic( "org.eclipse.swt.TabUtil.createTabItem", args );
    WidgetLCAUtil.writeCustomVariant( widget );
    writer.addListener( QX_EVENT_CHANGE_CHECKED, JS_FUNC_TAB_SELECTED );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    TabItem tabItem = ( TabItem )widget;
    setJSParent( tabItem );
    ItemLCAUtil.writeChanges( tabItem );
    writeSelection( tabItem );
  }
  
  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.removeListener( QX_EVENT_CHANGE_CHECKED, JS_FUNC_TAB_SELECTED );
    Object[] args = new Object[]{ WidgetUtil.getId( widget ), };
    writer.callStatic( "org.eclipse.swt.TabUtil.releaseTabItem", args );
    writer.dispose();
  }

  public void createResetHandlerCalls( final String typePoolId ) 
    throws IOException 
  {
  }
  
  public String getTypePoolId( final Widget widget ) {
    return null;
  }
  

  //////////////////
  // helping methods
  
  private void writeSelection( final TabItem item ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( item );
    Boolean newValue = Boolean.valueOf( isSelected( item ) );
    writer.set( PROP_SELECTED, "checked", newValue, Boolean.FALSE );
  }
  
  private boolean isSelected( final TabItem tabItem ) {
    TabFolder parent = tabItem.getParent();
    int selectionIndex = parent.getSelectionIndex();
    return selectionIndex != -1 && parent.getItem( selectionIndex ) == tabItem;
  }
  
  private static void setJSParent( final TabItem tabItem ) {
    Control control = tabItem.getControl();
    if( control != null ) {
      StringBuffer replacementId = new StringBuffer();
      replacementId.append( WidgetUtil.getId( tabItem ) );
      replacementId.append( "pg" );
      WidgetAdapter controlAdapter 
        = ( WidgetAdapter )WidgetUtil.getAdapter( control );
      controlAdapter.setJSParent( replacementId.toString() );
    }
  }
}
