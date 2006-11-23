/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rap.rwt.internal.widgets.tabitemkit;

import java.io.IOException;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.internal.widgets.IWidgetAdapter;
import org.eclipse.rap.rwt.internal.widgets.Props;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.*;


public class TabItemLCA extends AbstractWidgetLCA {

  private final static JSListenerInfo JS_LISTENER_INFO
    = new JSListenerInfo( "changeChecked",
                          "org.eclipse.rap.rwt.TabUtil.tabSelected",
                          JSListenerType.STATE_AND_ACTION );


  public void preserveValues( final Widget widget ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    TabItem tabItem = ( TabItem )widget;
    adapter.preserve( Props.CHECKED, Boolean.valueOf( isChecked( tabItem ) ) );
    adapter.preserve( Props.TEXT, tabItem.getText() );
    adapter.preserve( Props.IMAGE, Image.getPath( tabItem.getImage() ) );
    
    // preserve the listener state of the parent tabfolder here, since the
    // javascript handling is added to the clientside tab buttons and therefore
    // the jswriter will check the preserved state of the tabitem...
    TabFolder parent = tabItem.getParent();
    boolean hasListeners = SelectionEvent.hasListener( parent );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( hasListeners ) );

  }
  
  public void readData( final Widget widget ) {
    String value = WidgetUtil.readPropertyValue( widget, Props.CHECKED );
    if( value != null && Boolean.valueOf( value ).booleanValue() ) {
      TabItem tabItem = ( TabItem )widget;
      TabFolder parent = tabItem.getParent();
      TabItem[] items = parent.getItems();
      for( int i = 0; i < items.length; i++ ) {
        if( items[ i ] == tabItem ) {
          TabItem[] oldSelection = parent.getSelection();
          if( oldSelection.length == 1 ) {
            IWidgetAdapter adapter = WidgetUtil.getAdapter( oldSelection[ 0 ] );
            adapter.preserve( Props.CHECKED, Boolean.FALSE );
          }
          IWidgetAdapter adapter = WidgetUtil.getAdapter( tabItem );
          adapter.preserve( Props.CHECKED, Boolean.TRUE );
          // TODO: [fappel] see comment in TabFolderLCA.processAction
          parent.setSelection( i );
        }
      }
    }
  }
  
  public void processAction( final Widget widget ) {
  }
  
  public void renderInitialization( final Widget widget ) throws IOException {
    TabItem tabItem = ( TabItem )widget;
    JSWriter writer = JSWriter.getWriterFor( widget );
    Object[] args = new Object[] { 
      WidgetUtil.getId( tabItem ), 
      WidgetUtil.getId( tabItem.getParent() )
    };
    writer.callStatic( "org.eclipse.rap.rwt.TabUtil.createTabItem", args );
    setJSParent( tabItem );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    TabItem tabItem = ( TabItem )widget;
    JSWriter writer = JSWriter.getWriterFor( tabItem );
    writer.set( Props.TEXT, JSConst.QX_FIELD_LABEL, tabItem.getText() );
    if( tabItem.getImage()!=null ){
      writer.set( Props.IMAGE, 
                  JSConst.QX_FIELD_ICON,
                  Image.getPath( tabItem.getImage() ) );
    }
    writeCheckedState( widget );
    writer.updateListener( JS_LISTENER_INFO, 
                           Props.SELECTION_LISTENERS, 
                           SelectionEvent.hasListener( tabItem.getParent() ) );
  }
  
  public void renderDispose( final Widget widget ) throws IOException {
    // TODO [rh] preliminary: find out how to properly dispose of a TabItem
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }
  
  //////////////////
  // helping methods
  
  private void writeCheckedState( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    Boolean oldCheckState = ( Boolean )adapter.getPreserved( Props.CHECKED );
    TabItem tabItem = ( TabItem )widget;
    if( !adapter.isInitialized() 
        || isChecked( tabItem ) != oldCheckState.booleanValue() )
    {
      if( isChecked( tabItem )  ) {
        writer.set( Props.CHECKED, true );
      } else {
        writer.set( Props.CHECKED, false );
      }
    }
  }
  
  private boolean isChecked( final TabItem tabItem ) {
    TabFolder parent = tabItem.getParent();
    int selectionIndex = parent.getSelectionIndex();
    return selectionIndex != -1 && parent.getItem( selectionIndex ) == tabItem;
  }
  
  private void setJSParent( final TabItem tabItem ) {
    Control control = tabItem.getControl();
    if( control != null ) {
      IWidgetAdapter itemAdapter = WidgetUtil.getAdapter( tabItem );
      StringBuffer replacementId = new StringBuffer();
      replacementId.append( itemAdapter.getId() );
      replacementId.append( "pg" );
      IWidgetAdapter controlAdapter = WidgetUtil.getAdapter( control );
      controlAdapter.setJSParent( replacementId.toString() );
    }
  }
}
