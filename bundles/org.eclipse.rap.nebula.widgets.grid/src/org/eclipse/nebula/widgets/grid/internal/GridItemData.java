/*******************************************************************************
 * Copyright (c) 2014, 2020 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.grid.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.internal.SerializableCompatibility;


@SuppressWarnings( "restriction" )
public class GridItemData implements SerializableCompatibility {

  public Font defaultFont;
  public Color defaultBackground;
  public Color defaultForeground;
  public String defaultHeaderText;
  public String headerText;
  public Image headerImage;
  public Color defaultHeaderBackground;
  public Color headerBackground;
  public Color headerForeground;
  public Font headerFont;
  public int customHeight = -1;
  public boolean expanded;

  List<GridItem> children;
  final List<CellData> cellData;

  public GridItemData( int cells ) {
    cellData = new ArrayList<CellData>();
    for( int i = 0; i < Math.max( 1, cells ); i++ ) {
      cellData.add( null );
    }
  }

  public List<GridItem> getChildren() {
    if( children == null ) {
      children = new ArrayList<GridItem>();
    }
    return children;
  }

  public void addCellData( int index ) {
    if( index == -1 ) {
      cellData.add( null );
    } else {
      cellData.add( index, null );
    }
  }

  public void removeCellData( int index ) {
    if( cellData.size() > index ) {
      cellData.remove( index );
    }
  }

  public CellData getCellData( int index ) {
    if( cellData.get( index ) == null ) {
      cellData.set( index, new CellData() );
    }
    return cellData.get( index );
  }

  public void clear() {
    for( int index = 0; index < cellData.size(); index++ ) {
      cellData.set( index, null );
    }
    defaultFont = null;
    defaultBackground = null;
    defaultForeground = null;
    defaultHeaderText = null;
    headerText = null;
    headerImage = null;
    headerFont = null;
    headerBackground = null;
    headerForeground = null;
  }

  public static final class CellData implements SerializableCompatibility {
    public Font font;
    public Color background;
    public Color foreground;
    public String text = "";
    public String tooltip;
    public Image image;
    public boolean checked;
    public boolean grayed;
    public boolean checkable = true;
    public int columnSpan;
  }

}
