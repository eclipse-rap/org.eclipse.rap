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
package org.eclipse.swt.internal.widgets.tableitemkit;

import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderProperty;

import java.io.IOException;

import org.eclipse.rwt.internal.protocol.ClientObjectFactory;
import org.eclipse.rwt.internal.protocol.IClientObject;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.widgets.*;
import org.eclipse.swt.widgets.*;


public final class TableItemLCA extends AbstractWidgetLCA {

  private static interface IRenderRunnable {
    void run() throws IOException;
  }

  private static final String TYPE = "rwt.widgets.TreeItem";

  static final String PROP_TEXTS = "texts";
  static final String PROP_IMAGES = "images";
  static final String PROP_BACKGROUND = "background";
  static final String PROP_FOREGROUND = "foreground";
  static final String PROP_FONT = "font";
  static final String PROP_CELL_BACKGROUNDS = "cellBackgrounds";
  static final String PROP_CELL_FOREGROUNDS = "cellForegrounds";
  static final String PROP_CELL_FONTS = "cellFonts";
  static final String PROP_CHECKED = "checked";
  static final String PROP_GRAYED = "grayed";
  static final String PROP_CACHED = "cached";
  static final String PROP_VARIANT = "variant";

  @Override
  public void preserveValues( Widget widget ) {
    TableItem item = ( TableItem )widget;
    if( isCached( item ) ) {
      preserveProperty( item, PROP_TEXTS, getTexts( item ) );
      preserveProperty( item, PROP_IMAGES, getImages( item ) );
      WidgetLCAUtil.preserveBackground( item, getUserBackground( item ) );
      WidgetLCAUtil.preserveForeground( item, getUserForeground( item ) );
      WidgetLCAUtil.preserveFont( item, getUserFont( item ) );
      preserveProperty( item, PROP_CELL_BACKGROUNDS, getCellBackgrounds( item ) );
      preserveProperty( item, PROP_CELL_FOREGROUNDS, getCellForegrounds( item ) );
      preserveProperty( item, PROP_CELL_FONTS, getCellFonts( item ) );
      preserveProperty( item, PROP_CHECKED, item.getChecked() );
      preserveProperty( item, PROP_GRAYED, item.getGrayed() );
      preserveProperty( item, PROP_VARIANT, getVariant( item ) );
    }
    preserveProperty( item, PROP_CACHED, isCached( item ) );
  }

  public void readData( Widget widget ) {
    TableItem item = ( TableItem )widget;
    readChecked( item );
  }

  @Override
  public void renderInitialization( Widget widget ) throws IOException {
    TableItem item = ( TableItem )widget;
    Table parent = item.getParent();
    int index = parent.indexOf( item );
    IClientObject clientObject = ClientObjectFactory.getForWidget( item );
    clientObject.create( TYPE );
    clientObject.setProperty( "parent", WidgetUtil.getId( parent ) );
    clientObject.setProperty( "index", index );
  }

  @Override
  public void renderChanges( Widget widget ) throws IOException {
    final TableItem item = ( TableItem )widget;
    if( wasCleared( item ) ) {
      renderClear( item );
    } else {
      if( isCached( item ) ) {
        preservingInitialized( item, new IRenderRunnable() {
          public void run() throws IOException {
            // items that were uncached and are now cached (materialized) are
            // handled as if they were just created (initialized = false)
            if( !wasCached( item ) ) {
              setInitialized( item, false );
            }
            renderProperties( item );
          }
        } );
      }
    }
  }

  @Override
  public void renderDispose( Widget widget ) throws IOException {
    TableItem item = ( TableItem )widget;
    if( !isParentDisposed( item ) ) {
      // The tree disposes the items itself on the client (faster)
      ClientObjectFactory.getForWidget( widget ).destroy();
    }
  }

  //////////////////
  // ReadData helper

  private void readChecked( TableItem item ) {
    String value = WidgetLCAUtil.readPropertyValue( item, "checked" );
    if( value != null ) {
      item.setChecked( Boolean.valueOf( value ).booleanValue() );
    }
  }

  ///////////////////////
  // RenderChanges helper

  private static void renderProperties( TableItem item ) throws IOException {
    renderProperty( item, PROP_TEXTS, getTexts( item ), getDefaultTexts( item ) );
    renderProperty( item, PROP_IMAGES, getImages( item ), new Image[ getColumnCount( item ) ] );
    WidgetLCAUtil.renderBackground( item, getUserBackground( item ) );
    WidgetLCAUtil.renderForeground( item, getUserForeground( item ) );
    WidgetLCAUtil.renderFont( item, getUserFont( item ) );
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
    renderProperty( item, PROP_CHECKED, item.getChecked(), false );
    renderProperty( item, PROP_GRAYED, item.getGrayed(), false );
    renderProperty( item, PROP_VARIANT, getVariant( item ), null );
  }

  private static void renderClear( TableItem item ) {
    IClientObject clientObject = ClientObjectFactory.getForWidget( item );
    clientObject.call( "clear", null );
  }

  //////////////////
  // Helping methods

  private static boolean isCached( TableItem item ) {
    Table table = item.getParent();
    ITableAdapter adapter = table.getAdapter( ITableAdapter.class );
    return !adapter.isItemVirtual( table.indexOf( item ) );
  }

  static String[] getTexts( TableItem item ) {
    int columnCount = getColumnCount( item );
    String[] result = new String[ columnCount ];
    for( int i = 0; i < columnCount; i++ ) {
      result[ i ] = item.getText( i );
    }
    return result;
  }

  private static String[] getDefaultTexts( TableItem item ) {
    String[] result = new String[ getColumnCount( item ) ];
    for( int i = 0; i < result.length; i++ ) {
      result[ i ] = "";
    }
    return result;
  }

  static Image[] getImages( TableItem item ) {
    int columnCount = getColumnCount( item );
    Image[] result = new Image[ columnCount ];
    for( int i = 0; i < columnCount; i++ ) {
      result[ i ] = item.getImage( i );
    }
    return result;
  }

  private static Color getUserBackground( TableItem item ) {
    IWidgetColorAdapter colorAdapter = item.getAdapter( IWidgetColorAdapter.class );
    return colorAdapter.getUserBackground();
  }

  private static Color getUserForeground( TableItem item ) {
    IWidgetColorAdapter colorAdapter = item.getAdapter( IWidgetColorAdapter.class );
    return colorAdapter.getUserForeground();
  }

  private static Font getUserFont( TableItem item ) {
    IWidgetFontAdapter fontAdapter = item.getAdapter( IWidgetFontAdapter.class );
    return fontAdapter.getUserFont();
  }

  private static Color[] getCellBackgrounds( TableItem item ) {
    ITableItemAdapter itemAdapter = item.getAdapter( ITableItemAdapter.class );
    return itemAdapter.getCellBackgrounds();
  }

  private static Color[] getCellForegrounds( TableItem item ) {
    ITableItemAdapter itemAdapter = item.getAdapter( ITableItemAdapter.class );
    return itemAdapter.getCellForegrounds();
  }

  private static Font[] getCellFonts( TableItem item ) {
    ITableItemAdapter itemAdapter = item.getAdapter( ITableItemAdapter.class );
    return itemAdapter.getCellFonts();
  }

  private static String getVariant( TableItem item ) {
    String result = WidgetUtil.getVariant( item );
    if( result != null ) {
      result = "variant_" + result;
    }
    return result;
  }

  private static int getColumnCount( TableItem item ) {
    return Math.max( 1, item.getParent().getColumnCount() );
  }

  private static boolean wasCleared( TableItem item ) {
    boolean cached = isCached( item );
    boolean wasCached = wasCached( item );
    return !cached && wasCached;
  }

  private static boolean wasCached( TableItem item ) {
    boolean wasCached;
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    if( adapter.isInitialized() ) {
      Boolean preserved = ( Boolean )adapter.getPreserved( PROP_CACHED );
      wasCached = Boolean.TRUE.equals( preserved );
    } else {
      wasCached = true;
    }
    return wasCached;
  }

  private static void preservingInitialized( TableItem item, IRenderRunnable runnable )
    throws IOException
  {
    boolean initialized = WidgetUtil.getAdapter( item ).isInitialized();
    runnable.run();
    setInitialized( item, initialized );
  }

  private static void setInitialized( TableItem item, boolean initialized ) {
    WidgetAdapter adapter = ( WidgetAdapter )item.getAdapter( IWidgetAdapter.class );
    adapter.setInitialized( initialized );
  }

  private boolean isParentDisposed( TableItem item ) {
    ITableItemAdapter adapter = item.getAdapter( ITableItemAdapter.class );
    return adapter.isParentDisposed();
  }
}
