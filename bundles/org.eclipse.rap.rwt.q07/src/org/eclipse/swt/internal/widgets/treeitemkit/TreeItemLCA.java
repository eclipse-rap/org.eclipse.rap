/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.treeitemkit;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.ResourceFactory;
import org.eclipse.swt.internal.widgets.*;
import org.eclipse.swt.widgets.*;

public final class TreeItemLCA extends AbstractWidgetLCA {

  public static final String PROP_CHECKED = "checked";
  public static final String PROP_EXPANDED = "expanded";
  public static final String PROP_SELECTION = "selection";
  public static final String PROP_BACKGROUND = "background";
  public static final String PROP_FOREGROUND = "foreground";
  public static final String PROP_FONT = "font";
  public static final String PROP_CELL_BACKGROUNDS = "backgrounds";
  public static final String PROP_CELL_FOREGROUNDS = "foregrounds";
  public static final String PROP_CELL_FONTS = "fonts";
  public static final String PROP_GRAYED = "grayed";
  public static final String PROP_TEXTS = "texts";
  public static final String PROP_IMAGES = "images";
  public static final String PROP_MATERIALIZED = "materialized";

  public void preserveValues( final Widget widget ) {
    TreeItem treeItem = ( TreeItem )widget;
    Tree tree = treeItem.getParent();
    ITreeAdapter treeAdapter
      = ( ITreeAdapter )tree.getAdapter( ITreeAdapter.class );
    ITreeItemAdapter itemAdapter
      = ( ITreeItemAdapter )treeItem.getAdapter( ITreeItemAdapter.class );
    IWidgetColorAdapter colorAdapter
      = ( IWidgetColorAdapter )treeItem.getAdapter( IWidgetColorAdapter.class );
    IWidgetFontAdapter fontAdapter
      = ( IWidgetFontAdapter )treeItem.getAdapter( IWidgetFontAdapter.class );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( treeItem );
    boolean selection = isSelected( treeItem );
    adapter.preserve( PROP_SELECTION, Boolean.valueOf( selection ) );
    if( treeAdapter.isCached( treeItem ) ) {
      preserveFont( treeItem );
      adapter.preserve( PROP_CHECKED,
                        Boolean.valueOf( treeItem.getChecked() ) );
      adapter.preserve( TreeItemLCA.PROP_EXPANDED,
                        Boolean.valueOf( treeItem.getExpanded() ) );
      adapter.preserve( PROP_TEXTS, getTexts( treeItem ) );
      adapter.preserve( PROP_IMAGES, getImages( treeItem ) );
      adapter.preserve( PROP_BACKGROUND, colorAdapter.getUserBackgound() );
      adapter.preserve( PROP_FOREGROUND, colorAdapter.getUserForegound() );
      adapter.preserve( PROP_FONT, fontAdapter.getUserFont() );
      adapter.preserve( PROP_CELL_BACKGROUNDS,
                        itemAdapter.getCellBackgrounds() );
      adapter.preserve( PROP_CELL_FOREGROUNDS,
                        itemAdapter.getCellForegrounds() );
      adapter.preserve( PROP_CELL_FONTS, itemAdapter.getCellFonts() );
      adapter.preserve( PROP_GRAYED, Boolean.valueOf( treeItem.getGrayed() ) );
    }
    adapter.preserve( PROP_MATERIALIZED,
                      Boolean.valueOf( treeAdapter.isCached( treeItem ) ) );
    WidgetLCAUtil.preserveCustomVariant( treeItem );
  }

  public void readData( final Widget widget ) {
    final TreeItem treeItem = ( TreeItem )widget;
    String value = WidgetLCAUtil.readPropertyValue( widget, "checked" );
    if( value != null ) {
      treeItem.setChecked( Boolean.valueOf( value ).booleanValue() );
    }
    if( WidgetLCAUtil.wasEventSent( treeItem, JSConst.EVENT_TREE_EXPANDED ) ) {
      // The event is fired before the setter is called. Order like in SWT.
      processTreeExpandedEvent( treeItem );
      ProcessActionRunner.add( new Runnable() {
        public void run() {          
          treeItem.setExpanded( true );
        }
      } );
    }
    if( WidgetLCAUtil.wasEventSent( treeItem, JSConst.EVENT_TREE_COLLAPSED ) ) {
      processTreeCollapsedEvent( treeItem );
      ProcessActionRunner.add( new Runnable() {
        public void run() {         
          treeItem.setExpanded( false );
        }
      } );
    }
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    TreeItem treeItem = ( TreeItem )widget;
    JSWriter writer = JSWriter.getWriterFor( widget );
    Object parent;
    Integer index;
    if( treeItem.getParentItem() == null ) {
      parent = treeItem.getParent();
      index  = new Integer( treeItem.getParent().indexOf( treeItem ) );
    } else {
      parent = treeItem.getParentItem();
      index = new Integer( treeItem.getParentItem().indexOf( treeItem ) );
    }
    writer.newWidget( "org.eclipse.rwt.widgets.TreeItem", new Object[]{
      parent, index
    } );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    TreeItem treeItem = ( TreeItem )widget;
    Tree tree = treeItem.getParent();
    ITreeAdapter adapter = ( ITreeAdapter )tree.getAdapter( ITreeAdapter.class );
    if( adapter.isCached( treeItem ) ) {
      writeImages( treeItem );
      writeTexts( treeItem );
      writeBackground( treeItem );
      writeForeground( treeItem );
      writeFont( treeItem );
      writeCellBackgrounds( treeItem );
      writeCellForegrounds( treeItem );
      writeCellFonts( treeItem );
      writeSelection( treeItem );
      writeExpanded( treeItem );
      writeChecked( treeItem );
      writeGrayed( treeItem );
    }
    WidgetLCAUtil.writeCustomVariant( treeItem );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    TreeItem item = ( TreeItem )widget;
    ITreeItemAdapter itemAdapter
      = ( ITreeItemAdapter )item.getAdapter( ITreeItemAdapter.class );
    if( !itemAdapter.isParentDisposed() ) {
      // The tree disposes the items itself on the client (faster)
      JSWriter writer = JSWriter.getWriterFor( item );
      writer.call( "dispose", null );
    }
  }

  private static void writeImages( final TreeItem item ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( item );
    // TODO [rh] optimize when !isInitialized: for all images != null: setImg
    Image[] images = getImages( item );
    if( WidgetLCAUtil.hasChanged( item, PROP_IMAGES, images ) ) {
      String[] imagePaths = new String[ images.length ];
      for( int i = 0; i < imagePaths.length; i++ ) {
        imagePaths[ i ] = ResourceFactory.getImagePath( images[ i ] );
      }
      writer.set( "images", new Object[]{
        imagePaths
      } );
    }
  }

  private static void preserveFont( final TreeItem treeItem ) {
    IWidgetFontAdapter fontAdapter
      = ( IWidgetFontAdapter )treeItem.getAdapter( IWidgetFontAdapter.class );
    Font font = fontAdapter.getUserFont();
    WidgetLCAUtil.preserveFont( treeItem, font );
  }

  private static void writeExpanded( final TreeItem item ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( item );
    Boolean newValue = Boolean.valueOf( item.getExpanded() );
    writer.set( PROP_EXPANDED, "expanded", newValue, Boolean.FALSE );
  }

  private static void writeChecked( final TreeItem item ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( item );
    Boolean newValue = Boolean.valueOf( item.getChecked() );
    writer.set( PROP_CHECKED, "checked", newValue, Boolean.FALSE );
  }

  private static void writeSelection( final TreeItem item ) throws IOException {
    Boolean newValue = Boolean.valueOf( isSelected( item ) );
    Boolean defValue = Boolean.FALSE;
    if( WidgetLCAUtil.hasChanged( item, PROP_SELECTION, newValue, defValue ) ) {
      JSWriter writer = JSWriter.getWriterFor( item.getParent() );
      String jsFunction = isSelected( item ) ? "selectItem" : "deselectItem";
      writer.call( jsFunction, new Object[]{ item } );
      if( isFocused( item ) ) {
        writer.set( "focusItem", new Object[]{ item } );
      }
    }
  }

  private static void writeFont( final TreeItem treeItem ) throws IOException {
    Object adapter = treeItem.getAdapter( IWidgetFontAdapter.class );
    IWidgetFontAdapter fontAdapter = ( IWidgetFontAdapter )adapter;
    Font font = fontAdapter.getUserFont();
    WidgetLCAUtil.writeFont( treeItem, font );
  }

  private static void writeCellFonts( final TreeItem item ) throws IOException {
    ITreeItemAdapter itemAdapter
      = ( ITreeItemAdapter )item.getAdapter( ITreeItemAdapter.class );
    Font[] fonts = itemAdapter.getCellFonts();
    JSWriter writer = JSWriter.getWriterFor( item );
    // TODO [rst] Revise when properly implemented in TreeItem.js
    // writer.set( PROP_CELL_FONTS, "fonts", fonts, null );
    if( WidgetLCAUtil.hasChanged( item, PROP_CELL_FONTS, fonts, null ) ) {
      String[] css = null;
      if( fonts != null ) {
        css = new String[ fonts.length ];
        for( int i = 0; i < fonts.length; i++ ) {
          css[ i ] = toCss( fonts[ i ] );
        }
      }
      writer.set( "cellFonts", new Object[]{ css } );
    }
  }

  private static void writeBackground( final TreeItem item ) throws IOException
  {
    IWidgetColorAdapter colorAdapter
      = ( IWidgetColorAdapter )item.getAdapter( IWidgetColorAdapter.class );
    Color background = colorAdapter.getUserBackgound();
    if( WidgetLCAUtil.hasChanged( item, PROP_BACKGROUND, background, null ) ) {
      JSWriter writer = JSWriter.getWriterFor( item );
      writer.set( "background", background );
    }
  }

  private static void writeForeground( final TreeItem item ) throws IOException
  {
    IWidgetColorAdapter colorAdapter
      = ( IWidgetColorAdapter )item.getAdapter( IWidgetColorAdapter.class );
    Color foreground = colorAdapter.getUserForegound();
    if( WidgetLCAUtil.hasChanged( item, PROP_FOREGROUND, foreground, null ) ) {
      JSWriter writer = JSWriter.getWriterFor( item );
      writer.set( "foreground", foreground );
    }
  }

  private static void writeCellBackgrounds( final TreeItem item )
    throws IOException
  {
    ITreeItemAdapter itemAdapter
      = ( ITreeItemAdapter )item.getAdapter( ITreeItemAdapter.class );
    Color[] backgrounds = itemAdapter.getCellBackgrounds();
    JSWriter writer = JSWriter.getWriterFor( item );
    writer.set( PROP_CELL_BACKGROUNDS, "cellBackgrounds", backgrounds, null );
  }

  private static void writeCellForegrounds( final TreeItem item )
    throws IOException
  {
    ITreeItemAdapter itemAdapter
      = ( ITreeItemAdapter )item.getAdapter( ITreeItemAdapter.class );
    Color[] foregrounds = itemAdapter.getCellForegrounds();
    JSWriter writer = JSWriter.getWriterFor( item );
    writer.set( PROP_CELL_FOREGROUNDS, "cellForegrounds", foregrounds, null );
  }

  private static void writeTexts( final TreeItem item ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( item );
    String[] texts = getTexts( item );
    if( WidgetLCAUtil.hasChanged( item, PROP_TEXTS, texts ) ) {
      for( int i = 0; i < texts.length; i++ ) {
        texts[ i ] = WidgetLCAUtil.escapeText( texts[ i ], false );
        texts[ i ] = texts[ i ].replaceAll( " ", "&nbsp;" );
      }
      writer.set( "texts", new Object[]{
        texts
      } );
    }
  }

  private static String[] getTexts( final TreeItem item ) {
    int columnCount = getColumnCount( item );
    String[] texts = new String[ columnCount ];
    for( int i = 0; i < columnCount; i++ ) {
      texts[ i ] = item.getText( i );
    }
    return texts;
  }

  private static Image[] getImages( final TreeItem item ) {
    int columnCount = getColumnCount( item );
    Image[] images = new Image[ columnCount ];
    for( int i = 0; i < columnCount; i++ ) {
      images[ i ] = item.getImage( i );
    }
    return images;
  }

  private static int getColumnCount( final TreeItem item ) {
    return Math.max( 1, item.getParent().getColumnCount() );
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

  private static String toCss( final Font font ) {
    StringBuffer result = new StringBuffer();
    if( font != null ) {
      FontData fontData = font.getFontData()[ 0 ];
      if( ( fontData.getStyle() & SWT.ITALIC ) != 0 ) {
        result.append( "italic " );
      }
      if( ( fontData.getStyle() & SWT.BOLD ) != 0 ) {
        result.append( "bold " );
      }
      result.append( fontData.getHeight() );
      result.append( "px " );
      // TODO [rh] preliminary: low budget font-name-escaping
      String escapedName = fontData.getName().replaceAll( "\"", "" );
      result.append( escapedName );
    } else {
      // TODO [if] Revise when properly implemented in TreeItem.js
      result.append( "" );
    }
    return result.toString();
  }

  /////////////////////////////////
  // Process expand/collapse events

  private static void processTreeExpandedEvent( final TreeItem treeItem ) {
    if( TreeEvent.hasListener( treeItem.getParent() ) ) {
      TreeEvent event = new TreeEvent( treeItem.getParent(),
                                       treeItem,
                                       TreeEvent.TREE_EXPANDED );
      event.processEvent();
    }
  }

  private static void processTreeCollapsedEvent( final TreeItem treeItem ) {
    if( TreeEvent.hasListener( treeItem.getParent() ) ) {
      TreeEvent event = new TreeEvent( treeItem.getParent(),
                                       treeItem,
                                       TreeEvent.TREE_COLLAPSED );
      event.processEvent();
    }
  }
}
