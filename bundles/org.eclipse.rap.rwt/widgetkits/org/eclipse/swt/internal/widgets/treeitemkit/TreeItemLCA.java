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
package org.eclipse.swt.internal.widgets.treeitemkit;

import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil.renderProperty;

import java.io.IOException;

import org.eclipse.rap.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rap.rwt.internal.protocol.IClientObject;
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.rap.rwt.lifecycle.WidgetLCAUtil;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.widgets.ITreeAdapter;
import org.eclipse.swt.internal.widgets.ITreeItemAdapter;
import org.eclipse.swt.internal.widgets.IWidgetColorAdapter;
import org.eclipse.swt.internal.widgets.IWidgetFontAdapter;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;


public final class TreeItemLCA extends AbstractWidgetLCA {

  private static final String TYPE = "rwt.widgets.GridItem";

  static final String PROP_INDEX = "index";
  static final String PROP_ITEM_COUNT = "itemCount";
  static final String PROP_TEXTS = "texts";
  static final String PROP_IMAGES = "images";
  static final String PROP_BACKGROUND = "background";
  static final String PROP_FOREGROUND = "foreground";
  static final String PROP_FONT = "font";
  static final String PROP_CELL_BACKGROUNDS = "cellBackgrounds";
  static final String PROP_CELL_FOREGROUNDS = "cellForegrounds";
  static final String PROP_CELL_FONTS = "cellFonts";
  static final String PROP_EXPANDED = "expanded";
  static final String PROP_CHECKED = "checked";
  static final String PROP_GRAYED = "grayed";

  private static final int DEFAULT_ITEM_COUNT = 0;

  @Override
  public void preserveValues( Widget widget ) {
    TreeItem item = ( TreeItem )widget;
    preserveProperty( item, PROP_INDEX, getIndex( item ) );
    if( isCached( item ) ) {
      preserveProperty( item, PROP_ITEM_COUNT, item.getItemCount() );
      preserveProperty( item, PROP_TEXTS, getTexts( item ) );
      preserveProperty( item, PROP_IMAGES, getImages( item ) );
      WidgetLCAUtil.preserveBackground( item, getUserBackground( item ) );
      WidgetLCAUtil.preserveForeground( item, getUserForeground( item ) );
      WidgetLCAUtil.preserveFont( item, getUserFont( item ) );
      WidgetLCAUtil.preserveCustomVariant( item );
      WidgetLCAUtil.preserveData( item );
      preserveProperty( item, PROP_CELL_BACKGROUNDS, getCellBackgrounds( item ) );
      preserveProperty( item, PROP_CELL_FOREGROUNDS, getCellForegrounds( item ) );
      preserveProperty( item, PROP_CELL_FONTS, getCellFonts( item ) );
      preserveProperty( item, PROP_EXPANDED, item.getExpanded() );
      preserveProperty( item, PROP_CHECKED, item.getChecked() );
      preserveProperty( item, PROP_GRAYED, item.getGrayed() );
    }
  }

  public void readData( Widget widget ) {
    final TreeItem item = ( TreeItem )widget;
    String checked = WidgetLCAUtil.readPropertyValue( widget, PROP_CHECKED );
    if( checked != null ) {
      item.setChecked( Boolean.valueOf( checked ).booleanValue() );
    }
    final String expanded = WidgetLCAUtil.readPropertyValue( widget, PROP_EXPANDED );
    if( expanded != null ) {
      ProcessActionRunner.add( new Runnable() {
        public void run() {
          item.setExpanded( Boolean.valueOf( expanded ).booleanValue() );
          preserveProperty( item, PROP_EXPANDED, item.getExpanded() );
        }
      } );
    }
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    TreeItem item = ( TreeItem )widget;
    IClientObject clientObject = ClientObjectFactory.getClientObject( item );
    clientObject.create( TYPE );
    Widget parent = item.getParentItem() == null ? item.getParent() : item.getParentItem();
    clientObject.set( "parent", WidgetUtil.getId( parent ) );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    TreeItem item = ( TreeItem )widget;
    renderProperty( item, PROP_INDEX, getIndex( item ), -1 );
    if( isCached( item ) ) {
      renderProperty( item, PROP_ITEM_COUNT, item.getItemCount(), DEFAULT_ITEM_COUNT );
      renderProperty( item, PROP_TEXTS, getTexts( item ), getDefaultTexts( item ) );
      renderProperty( item, PROP_IMAGES, getImages( item ), new Image[ getColumnCount( item ) ] );
      WidgetLCAUtil.renderBackground( item, getUserBackground( item ) );
      WidgetLCAUtil.renderForeground( item, getUserForeground( item ) );
      WidgetLCAUtil.renderFont( item, getUserFont( item ) );
      WidgetLCAUtil.renderCustomVariant( item );
      WidgetLCAUtil.renderData( item );
      renderProperty( item,
                      PROP_CELL_BACKGROUNDS,
                      getCellBackgrounds( item ),
                      new Color[ getColumnCount( item ) ] );
      renderProperty( item,
                      PROP_CELL_FOREGROUNDS,
                      getCellForegrounds( item ),
                      new Color[ getColumnCount( item ) ] );
      renderProperty( item,
                      PROP_CELL_FONTS,
                      getCellFonts( item ),
                      new Font[ getColumnCount( item ) ] );
      renderProperty( item, PROP_EXPANDED, item.getExpanded(), false );
      renderProperty( item, PROP_CHECKED, item.getChecked(), false );
      renderProperty( item, PROP_GRAYED, item.getGrayed(), false );
    }
  }

  @Override
  public void renderDispose( Widget widget ) throws IOException {
    TreeItem item = ( TreeItem )widget;
    ITreeItemAdapter itemAdapter = item.getAdapter( ITreeItemAdapter.class );
    // The parent by the clients logic is the parent-item, not the tree (except for root layer)
    if( !itemAdapter.isParentDisposed() ) {
      ClientObjectFactory.getClientObject( widget ).destroy();
    }
  }

  //////////////////
  // Helping methods

  private static int getIndex( TreeItem item ) {
    int result;
    if( item.getParentItem() == null ) {
      result = item.getParent().indexOf( item );
    } else {
      result = item.getParentItem().indexOf( item );
    }
    return result;
  }

  private static boolean isCached( TreeItem item ) {
    Tree tree = item.getParent();
    ITreeAdapter treeAdapter = tree.getAdapter( ITreeAdapter.class );
    return treeAdapter.isCached( item );
  }

  private static String[] getTexts( TreeItem item ) {
    int columnCount = getColumnCount( item );
    String[] texts = new String[ columnCount ];
    for( int i = 0; i < columnCount; i++ ) {
      texts[ i ] = item.getText( i );
    }
    return texts;
  }

  private static String[] getDefaultTexts( TreeItem item ) {
    String[] result = new String[ getColumnCount( item ) ];
    for( int i = 0; i < result.length; i++ ) {
      result[ i ] = "";
    }
    return result;
  }

  private static Image[] getImages( TreeItem item ) {
    int columnCount = getColumnCount( item );
    Image[] images = new Image[ columnCount ];
    for( int i = 0; i < columnCount; i++ ) {
      images[ i ] = item.getImage( i );
    }
    return images;
  }

  private static Color getUserBackground( TreeItem item ) {
    IWidgetColorAdapter colorAdapter = item.getAdapter( IWidgetColorAdapter.class );
    return colorAdapter.getUserBackground();
  }

  private static Color getUserForeground( TreeItem item ) {
    IWidgetColorAdapter colorAdapter = item.getAdapter( IWidgetColorAdapter.class );
    return colorAdapter.getUserForeground();
  }

  private static Font getUserFont( TreeItem item ) {
    IWidgetFontAdapter fontAdapter = item.getAdapter( IWidgetFontAdapter.class );
    return fontAdapter.getUserFont();
  }

  private static Color[] getCellBackgrounds( TreeItem item ) {
    ITreeItemAdapter itemAdapter = item.getAdapter( ITreeItemAdapter.class );
    return itemAdapter.getCellBackgrounds();
  }

  private static Color[] getCellForegrounds( TreeItem item ) {
    ITreeItemAdapter itemAdapter = item.getAdapter( ITreeItemAdapter.class );
    return itemAdapter.getCellForegrounds();
  }

  private static Font[] getCellFonts( TreeItem item ) {
    ITreeItemAdapter itemAdapter = item.getAdapter( ITreeItemAdapter.class );
    return itemAdapter.getCellFonts();
  }

  private static int getColumnCount( TreeItem item ) {
    return Math.max( 1, item.getParent().getColumnCount() );
  }

}
