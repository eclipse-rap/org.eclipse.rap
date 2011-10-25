/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.Tree", {

  factory : function( properties ) {
    var styleMap = org.eclipse.rwt.protocol.AdapterUtil.createStyleMap( properties.style );
    var configMap = {
      appearance : properties.appearance,
      virtual : styleMap.VIRTUAL,
      noScroll : styleMap.NO_SCROLL,
      multiSelection : styleMap.MULTI,
      check : styleMap.CHECK,
      fullSelection : styleMap.FULL_SELECTION,
      checkBoxMetrics : properties.checkBoxMetrics,
      selectionPadding : properties.selectionPadding,
      indentionWidth : properties.indentionWidth
    };  
    var result = new org.eclipse.rwt.widgets.Tree( configMap );
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
    "headerHeight",
    "headerVisible",
    "linesVisible",
    "topItemIndex",
    "scrollLeft",
    "selection",
    "scrollBarsVisible",
    "enableCellToolTip",
    "cellToolTipText"
  ] ),

  propertyHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
    "itemMetrics" : function( widget, value ) {
      for( var i = 0; i < value.length; i++ ) {
        widget.setItemMetrics( value[ i ][ 0 ],
                               value[ i ][ 1 ],
                               value[ i ][ 2 ],
                               value[ i ][ 3 ],
                               value[ i ][ 4 ],
                               value[ i ][ 5 ],
                               value[ i ][ 6 ] );
      }
    },
    "selection" : function( widget, value ) {
      widget.deselectAll();
      for( var i = 0; i < value.length; i++ ) {
        org.eclipse.rwt.protocol.AdapterUtil.callWithTarget( value[ i ], function( item ) {
          widget.selectItem( item );
          if( i === 0 ) {
            widget.setFocusItem( item );
          }
        } );
      }
    },
    "scrollBarsVisible" : function( widget, value ) {
      widget.setScrollBarsVisible( value[ 0 ], value[ 1 ] );
    },
    "cellToolTipText" : function( widget, value ) {
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      var text = encodingUtil.escapeText( value, false );
      text = encodingUtil.replaceNewLines( text, "<br/>" );
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