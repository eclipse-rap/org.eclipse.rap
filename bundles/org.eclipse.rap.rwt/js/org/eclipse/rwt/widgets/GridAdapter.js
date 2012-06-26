/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.Grid", {

  factory : function( properties ) {
    var styleMap = org.eclipse.rwt.protocol.AdapterUtil.createStyleMap( properties.style );
    var configMap = {
      appearance : properties.appearance,
      virtual : styleMap.VIRTUAL,
      noScroll : styleMap.NO_SCROLL,
      multiSelection : styleMap.MULTI,
      check : styleMap.CHECK,
      // TODO: Remove this check when bug 355408: [Table] Always uses FULL_SELECTION is fixed
      fullSelection : properties.appearance === "table" ? true : styleMap.FULL_SELECTION,
      hideSelection : styleMap.HIDE_SELECTION,
      checkBoxMetrics : properties.checkBoxMetrics,
      selectionPadding : properties.selectionPadding,
      indentionWidth : properties.indentionWidth,
      splitContainer : properties.splitContainer,
      markupEnabled : properties.markupEnabled
    };  
    var result = new org.eclipse.rwt.widgets.Grid( configMap );
    org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    org.eclipse.rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : org.eclipse.rwt.protocol.AdapterUtil.getControlDestructor(),

  properties : org.eclipse.rwt.protocol.AdapterUtil.extendControlProperties( [
    "itemCount",
    "itemHeight",
    "itemMetrics",
    // NOTE : Client currently requires itemMetrics before columnCount
    "columnCount",
    "treeColumn",
    "fixedColumns",
    "headerHeight",
    "headerVisible",
    "footerHeight",
    "footerVisible",
    "linesVisible",
    "topItemIndex",
    "scrollLeft",
    "selection",
    "focusItem",
    "sortDirection",
    "sortColumn",
    "scrollBarsVisible",
    "alwaysHideSelection",
    "enableCellToolTip",
    "cellToolTipText"
  ] ),

  propertyHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
    "itemMetrics" : function( widget, value ) {
      for( var i = 0; i < value.length; i++ ) {
        widget.setItemMetrics.apply( widget, value[ i ] );
      }
    },
    "fixedColumns" : function( widget, value ) {
      org.eclipse.rwt.GridUtil.setFixedColumns( widget, value );
    },
    "focusItem" : function( widget, value ) {
      org.eclipse.rwt.protocol.AdapterUtil.callWithTarget( value, function( item ) {
        widget.setFocusItem( item );
      } );
    },
    "selection" : function( widget, value ) {
      widget.deselectAll();
      for( var i = 0; i < value.length; i++ ) {
        org.eclipse.rwt.protocol.AdapterUtil.callWithTarget( value[ i ], function( item ) {
          widget.selectItem( item );
        } );
      }
    },
    "sortColumn" : function( widget, value ) {
      org.eclipse.rwt.protocol.AdapterUtil.callWithTarget( value, function( column ) {
        widget.setSortColumn( column );
      } );
    },
    "scrollBarsVisible" : function( widget, value ) {
      widget.setScrollBarsVisible( value[ 0 ], value[ 1 ] );
    },
    "cellToolTipText" : function( widget, value ) {
      var EncodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      var text = EncodingUtil.escapeText( value, false );
      text = EncodingUtil.replaceNewLines( text, "<br/>" );
      widget.setCellToolTipText( text );
    }
  } ),

  listeners : org.eclipse.rwt.protocol.AdapterUtil.extendControlListeners( [
    "selection",
    "scrollBarsSelection"
  ] ),

  listenerHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlListenerHandler( {
    "scrollBarsSelection" : function( widget, value ) {
      widget.setHasScrollBarsSelectionListener( value );
    }
  } ),

  methods : []

} );