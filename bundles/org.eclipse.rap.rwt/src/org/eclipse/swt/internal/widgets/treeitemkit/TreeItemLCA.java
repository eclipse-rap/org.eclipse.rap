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

package org.eclipse.swt.internal.widgets.treeitemkit;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.widgets.*;
import org.eclipse.swt.lifecycle.*;
import org.eclipse.swt.widgets.*;

import com.w4t.engine.service.ContextProvider;


public final class TreeItemLCA extends AbstractWidgetLCA {

  public static final String PROP_CHECKED = "checked";
  public static final String PROP_EXPANDED = "expanded";
  public static final String PROP_SELECTION = "selection";
  public static final String PROP_BACKGROUND = "background";
  public static final String PROP_FOREGROUND = "foreground";
  public static final String PROP_GRAYED = "grayed";

  // Expanded/collapsed state constants, used by readData
  private static final String STATE_COLLAPSED = "collapsed";
  private static final String STATE_EXPANDED = "expanded";

  public void preserveValues( final Widget widget ) {
    TreeItem treeItem = ( TreeItem )widget;
    ItemLCAUtil.preserve( treeItem );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( treeItem );
    preserveFont( treeItem );
    adapter.preserve( PROP_CHECKED, Boolean.valueOf( treeItem.getChecked() ) );
    adapter.preserve( TreeItemLCA.PROP_EXPANDED,
                      Boolean.valueOf( treeItem.getExpanded() ) );
    boolean selection = isSelected( treeItem );
    adapter.preserve( PROP_SELECTION, Boolean.valueOf( selection ) );
    TreeItem item = ( TreeItem )widget;
    IWidgetColorAdapter colorAdapter
      = ( IWidgetColorAdapter )item.getAdapter( IWidgetColorAdapter.class );
    adapter.preserve( PROP_FOREGROUND, colorAdapter.getUserForegound() );
    adapter.preserve( PROP_BACKGROUND, colorAdapter.getUserBackgound() );
    adapter.preserve( PROP_GRAYED, Boolean.valueOf( item.getGrayed() ) );
  }

  public void readData( final Widget widget ) {
    TreeItem treeItem = ( TreeItem )widget;
    String value = WidgetLCAUtil.readPropertyValue( widget, "checked" );
    if( value != null ) {
      treeItem.setChecked( Boolean.valueOf( value ).booleanValue() );
    }
    // TODO [rh] TreeEvent behave different from SWT: SWT-style is to send
    //      the event and afterwards set the expanded property of the item
    String state = WidgetLCAUtil.readPropertyValue( widget, "state" );
    if( STATE_EXPANDED.equals( state ) || STATE_COLLAPSED.equals( state ) ) {
      treeItem.setExpanded( STATE_EXPANDED.equals( state ) );
    }
    processTreeExpandedEvent( widget );
    processTreeCollapsedEvent( widget );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    TreeItem treeItem = ( TreeItem )widget;
    JSWriter writer = JSWriter.getWriterFor( widget );
    Object parent;
    if( treeItem.getParentItem() == null ) {
      parent = treeItem.getParent();
    } else {
      parent = treeItem.getParentItem();
    }
    Object[] args = new Object[] {
      parent
    };
    writer.newWidget( "org.eclipse.swt.widgets.TreeItem", args );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    TreeItem treeItem = ( TreeItem )widget;
    ItemLCAUtil.writeText( treeItem );
    writeImage( treeItem );
    writeFont( treeItem );
    writeBackground( treeItem );
    writeForeground( treeItem );
    writeSelection( treeItem );
    writeExpanded( treeItem );
    writeChecked( treeItem );
    writeGrayed( treeItem );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    // safely remove tree item from tree
    writer.call( "destroy", null );
    writer.dispose();
  }

  ///////////////////////////////////
  // Helping methods to write changes

  // TODO [rh] workaround for qx bug #260 (TreeFullControl doesn't update icon
  //      when it is changed)
  private static void writeImage( final TreeItem item ) throws IOException {
    Image image = item.getImage();
    WidgetLCAUtil.writeImage( item, Props.IMAGE, "image", image );
  }

  private static void preserveFont( final TreeItem treeItem ) {
    IWidgetFontAdapter fontAdapter
      = ( IWidgetFontAdapter )treeItem.getAdapter( IWidgetFontAdapter.class );
    Font font = fontAdapter.getUserFont();
    WidgetLCAUtil.preserveFont( treeItem, font );
  }

  private static void writeFont( final TreeItem treeItem ) throws IOException {
    Object adapter = treeItem.getAdapter( IWidgetFontAdapter.class );
    IWidgetFontAdapter fontAdapter = ( IWidgetFontAdapter )adapter;
    Font font = fontAdapter.getUserFont();
    WidgetLCAUtil.writeFont( treeItem, font );
  }

  private static void writeExpanded( final TreeItem item )
    throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( item );
    Boolean newValue = Boolean.valueOf( item.getExpanded() );
    writer.set( PROP_EXPANDED, "open", newValue, Boolean.FALSE );
  }

  private static void writeChecked( final TreeItem item ) throws IOException
  {
    JSWriter writer = JSWriter.getWriterFor( item );
    Boolean newValue = Boolean.valueOf( item.getChecked() );
    writer.set( PROP_CHECKED, "checked", newValue, Boolean.FALSE );
  }

  private static void writeSelection( final TreeItem item )
    throws IOException
  {
    Boolean newValue = Boolean.valueOf( isSelected( item ) );
    Boolean defValue = Boolean.FALSE;
    if( WidgetLCAUtil.hasChanged( item, PROP_SELECTION, newValue, defValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( item );
      Boolean focused = Boolean.valueOf( isFocused( item ) );
      writer.set( "selection", new Object[] { newValue, focused } );
    }
  }

  private static void writeBackground( final TreeItem item )
      throws IOException {
    IWidgetColorAdapter colorAdapter
      = ( IWidgetColorAdapter )item.getAdapter( IWidgetColorAdapter.class );
    WidgetLCAUtil.writeBackground( item, colorAdapter.getUserBackgound() );
  }

  private static void writeForeground( final TreeItem item )
      throws IOException {
    IWidgetColorAdapter colorAdapter
      = ( IWidgetColorAdapter )item.getAdapter( IWidgetColorAdapter.class );
    WidgetLCAUtil.writeForeground( item, colorAdapter.getUserForegound() );
  }

  private static void writeGrayed( final TreeItem item ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( item );
    Boolean newValue = Boolean.valueOf( item.getGrayed() );
    writer.set( PROP_GRAYED, "grayed", newValue, Boolean.FALSE );
  }

  private static boolean isFocused( final TreeItem item ) {
    Tree tree = item.getParent();
    return tree.getSelectionCount() > 0 && tree.getSelection()[ 0 ] == item;
  }

  private static boolean isSelected( final TreeItem item ) {
    boolean result = false;
    Tree tree = item.getParent();
    TreeItem[] selectedItems = tree.getSelection();
    for( int i = 0; !result && i < selectedItems.length; i++ ) {
      if( item == selectedItems[ i ] ) {
        result = true;
      }
    }
    return result;
  }

  /////////////////////////////////
  // Process expand/collapse events

  private static void processTreeExpandedEvent( final Widget widget ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String id = request.getParameter( JSConst.EVENT_TREE_EXPANDED );
    if( WidgetUtil.getId( widget ).equals( id ) ) {
      TreeItem treeItem = ( TreeItem )widget;
      TreeEvent event = new TreeEvent( treeItem.getParent(),
                                       treeItem,
                                       TreeEvent.TREE_EXPANDED );
      event.processEvent();
    }
  }

  private static void processTreeCollapsedEvent( final Widget widget ) {
    HttpServletRequest request = ContextProvider.getRequest();
    String id = request.getParameter( JSConst.EVENT_TREE_COLLAPSED );
    if( WidgetUtil.getId( widget ).equals( id ) ) {
      TreeItem treeItem = ( TreeItem )widget;
      TreeEvent event = new TreeEvent( treeItem.getParent(),
                                       treeItem,
                                       TreeEvent.TREE_COLLAPSED );
      event.processEvent();
    }
  }
}
