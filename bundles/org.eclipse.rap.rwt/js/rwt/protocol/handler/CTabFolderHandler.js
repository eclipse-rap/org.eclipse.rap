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

rwt.protocol.HandlerRegistry.add( "rwt.widgets.CTabFolder", {

  factory : function( properties ) {
    var result = new rwt.widgets.CTabFolder();
    rwt.protocol.HandlerUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    rwt.widgets.CTabFolder.setToolTipTexts.apply( result, properties.toolTipTexts );
    rwt.protocol.HandlerUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : rwt.protocol.HandlerUtil.getControlDestructor(),

  getDestroyableChildren : rwt.protocol.HandlerUtil.getDestroyableChildrenFinder(),

  properties : rwt.protocol.HandlerUtil.extendControlProperties( [
    "tabPosition",
    "tabHeight",
    "minMaxState",
    "minimizeBounds",
    "minimizeVisible",
    "maximizeBounds",
    "maximizeVisible",
    "chevronBounds",
    "chevronVisible",
    "unselectedCloseVisible",
    "selection",
    "selectionBackground",
    "selectionForeground",
    "selectionBackgroundImage",
    "selectionBackgroundGradient",
    "borderVisible"
  ] ),

  propertyHandler : rwt.protocol.HandlerUtil.extendControlPropertyHandler( {
    "minimizeBounds" : function( widget, value ) {
      widget.setMinButtonBounds.apply( widget, value );
    },
    "minimizeVisible" : function( widget, value ) {
      if( value ) {
        widget.showMinButton();
      } else {
        widget.hideMinButton();
      }
    },
    "maximizeBounds" : function( widget, value ) {
      widget.setMaxButtonBounds.apply( widget, value );
    },
    "maximizeVisible" : function( widget, value ) {
      if( value ) {
        widget.showMaxButton();
      } else {
        widget.hideMaxButton();
      }
    },
    "chevronBounds" : function( widget, value ) {
      widget.setChevronBounds.apply( widget, value );
    },
    "chevronVisible" : function( widget, value ) {
      if( value ) {
        widget.showChevron();
      } else {
        widget.hideChevron();
      }
    },
    "selection" : function( widget, value ) {
      widget.deselectAll();
      rwt.protocol.HandlerUtil.callWithTarget( value, function( item ) {
        if( item != null ) {
          item.setSelected( true );
        }
      } );
    },
    "selectionBackground" : function( widget, value ) {
      if( value === null ) {
        widget.setSelectionBackground( null );
      } else {
        widget.setSelectionBackground( rwt.util.ColorUtil.rgbToRgbString( value ) );
      }
    },
    "selectionForeground" : function( widget, value ) {
      if( value === null ) {
        widget.setSelectionForeground( null );
      } else {
        widget.setSelectionForeground( rwt.util.ColorUtil.rgbToRgbString( value ) );
      }
    },
    "selectionBackgroundGradient" : function( widget, value ) {
      var gradient = null;
      if( value ) {
        var colors = value[ 0 ];
        var percents = value[ 1 ];
        var vertical = value[ 2 ];
        gradient = [];
        for( var i = 0; i < colors.length; i++ ) {
          gradient[ i ] = [ percents[ i ] / 100, rwt.util.ColorUtil.rgbToRgbString( colors[ i ] ) ];
        }
        gradient.horizontal = !vertical;
      }
      widget.setSelectionBackgroundGradient( gradient );
    }
  } ),

  listeners : rwt.protocol.HandlerUtil.extendControlListeners( [
    "Folder",
    "Selection",
    "DefaultSelection"
  ] ),

  listenerHandler : rwt.protocol.HandlerUtil.extendControlListenerHandler( {} )

} );
