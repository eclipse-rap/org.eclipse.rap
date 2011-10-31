/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
appearances = {
// BEGIN TEMPLATE //

  "table" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        textColor : tv.getCssColor( "Table", "color" ),
        font : tv.getCssFont( "*", "font" ),
        border : tv.getCssBorder( "Table", "border" ),
        backgroundColor : tv.getCssColor( "Table", "background-color" ),
        backgroundImage : tv.getCssImage( "Table", "background-image" ),
        backgroundGradient : tv.getCssGradient( "Table", "background-image" )
      };
    }
  },

  "table-column" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {
        cursor : "default",
        spacing : 2,
        opacity : states.moving ? 0.6 : 1.0
      };
      result.padding = tv.getCssBoxDimensions( "TableColumn", "padding" );
      result.textColor = tv.getCssColor( "TableColumn", "color" );
      result.font = tv.getCssFont( "TableColumn", "font" );
      result.backgroundColor = tv.getCssColor( "TableColumn", "background-color" );
      result.backgroundImage = tv.getCssImage( "TableColumn", "background-image" );
      result.backgroundGradient = tv.getCssGradient( "TableColumn", "background-image" );
      var borderColors = [ null, null, null, null ];
      var borderWidths = [ 0, 0, 0, 0 ];
      var borderStyles = [ "solid", "solid", "solid", "solid" ];
      if( !states.dummy ) {
        var verticalState = { "vertical" : true };
        var tvGrid = new org.eclipse.swt.theme.ThemeValues( verticalState );
        var gridColor = tvGrid.getCssColor( "Table-GridLine", "color" );
        gridColor = gridColor == "undefined" ? "transparent" : gridColor;
        borderColors[ 1 ] = gridColor;
        borderWidths[ 1 ] = 1;
      }
      var borderBottom = tv.getCssBorder( "TableColumn", "border-bottom" );
      borderWidths[ 2 ] = borderBottom.getWidthBottom();
      borderStyles[ 2 ] = borderBottom.getStyleBottom();
      borderColors[ 2 ] = borderBottom.getColorBottom();
      result.border = new org.eclipse.rwt.Border( borderWidths, borderStyles, borderColors );
      result.textShadow = tv.getCssShadow( "TableColumn", "text-shadow" );
      return result;
    }
  },

  "table-column-resizer" : {
    style : function( states ) {
      return {
        width : 3,
        opacity : 0.3,
        backgroundColor : "black"
      }
    }
  },

  "table-column-sort-indicator" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.source = tv.getCssImage( "TableColumn-SortIndicator", "background-image" );
      return result;
    }
  },

  "table-row" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      var result = {};
      result.itemBackground = tv.getCssColor( "TableItem", "background-color" );
      result.itemBackgroundImage = tv.getCssImage( "TableItem", "background-image" );
      result.itemBackgroundGradient = tv.getCssGradient( "TableItem", "background-image" );
      result.itemForeground = tv.getCssColor( "TableItem", "color" );
      result.textDecoration = tv.getCssIdentifier( "TableItem", "text-decoration" );
      result.textShadow = tv.getCssShadow( "TableItem", "text-shadow" );
      return result;
    }
  },

  "table-row-check-box" : {
    style : function( states ) {
      var tv = new org.eclipse.swt.theme.ThemeValues( states );
      return {
        backgroundImage : tv.getCssImage( "Table-Checkbox", "background-image" )
      }
    }
  },

  "table-gridline-vertical" : {
    style : function( states ) {
      var verticalState = { "vertical" : true };
      var tv = new org.eclipse.swt.theme.ThemeValues( verticalState );
      var gridColor = tv.getCssColor( "Table-GridLine", "color" );
      gridColor = gridColor == "undefined" ? "transparent" : gridColor;
      var result = {};
      result.border = new org.eclipse.rwt.Border( [ 0, 0, 0, 1 ], "solid", gridColor );
      return result;
    }
  }

// END TEMPLATE //
};