/*******************************************************************************
 * Copyright (c) 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - ongoing development
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.TreeUtil", {

  type : "static",

  statics : {
    
    ///////////////
    // API for Tree
    
    init : function( tree, argsMap ) {
      
    },
    
    createTreeRowContainer : function( argsmap ) {
      var result;
      if( typeof argsmap.fixedColumns === "number" ) {
        result = this._createContainerWrapper( argsmap.fixedColumns );
      } else {
        result = new org.eclipse.rwt.widgets.TreeRowContainer();
      }
      return result;
    },
    
    ////////////
    // Internals

    _createContainerWrapper : function( fixedColumns ) {
      if( !this._CONTAINERPROTO._protoInit ) {
        for( var i = 0; i < this._CONTAINER_DELEGATES.length; i++ ) {
          this._createContainerDelegater( this._CONTAINER_DELEGATES[ i ] );
        }
        for( var i = 0; i < this._CONTAINER_GETTER_DELEGATES.length; i++ ) {
          this._createContainerGetterDelegater( this._CONTAINER_GETTER_DELEGATES[ i ] );
        }
        this._CONTAINERPROTO._protoInit = true;
        this._CONTAINERCONSTR.prototype = this._CONTAINERPROTO;
      }
      return new this._CONTAINERCONSTR( fixedColumns );
    },

    _createContainerDelegater : function( funcName ) {
      this._CONTAINERPROTO[ funcName ] = function() {
        this._container[ 0 ][ funcName ].apply( this._container[ 0 ], arguments );
        this._container[ 1 ][ funcName ].apply( this._container[ 1 ], arguments );
      }
    },
    
    _createContainerGetterDelegater : function( funcName ) {
      this._CONTAINERPROTO[ funcName ] = function() {
        return this._container[ 0 ][ funcName ].apply( this._container[ 0 ], arguments );
      }
    },
    
    ///////////////////
    // internal classes

    _CONTAINER_DELEGATES : [ 
      "setParent", 
      "destroy", 
      "addEventListener", 
      "removeEventListener",
      "setSelectionProvider",
      "setRowAppearance",
      "setHeight",
      "setTop",
      "setBackgroundColor",
      "setBackgroundImage",
      "setRowHeight",
      "setTopItem",
      "renderItem",
      "renderItemQueue",
      "setRowLinesVisible",
      "setToolTip"
    ],

    _CONTAINER_GETTER_DELEGATES : [ 
      "getTop",
      "getHeight",
      "getHoverItem",
      "getElement"
    ],

    _CONTAINERCONSTR : function( fixedColumns ) {
      this._fixedColumns = fixedColumns;
      this._container = [];
      this._container[ 0 ] = new org.eclipse.rwt.widgets.TreeRowContainer();
      this._container[ 1 ] = new org.eclipse.rwt.widgets.TreeRowContainer();
      this._config = org.eclipse.rwt.widgets.TreeRowContainer.createRenderConfig();
      this._width = 0;
      this._splitOffset = 0;
      this._rowWidth = 0;
      this.addEventListener( "mouseover", this._onRowOver, this );
      this.addEventListener( "mouseout", this._onRowOver, this );
    },
    
    _CONTAINERPROTO : {
      
      _protoInit : false,

      getSubContainer : function( pos ) {
        return this._container[ pos ] || null;
      },

      getRenderConfig : function() {
        return this._config;
      },

      setPostRenderFunction : function() {
        // TODO [tb] : Dummy!
      },

      setWidth : function( value ) {
        this._width = value;
        this._layoutX();
      },

      getWidth : function() {
        return this._width;
      },

      setRowWidth : function( value ) {
        this._rowWidth = value; // will be updated with next renderAll
      },
      
      setScrollLeft : function( value ) {
        this._container[ 1 ].setScrollLeft( value );
      },
      
      findItemByRow : function( row ) {
        var result = this._container[ 0 ].findItemByRow( row );
        if( result == null ) {
          result = this._container[ 1 ].findItemByRow( row );
        }
        return result;
      },

      renderAll : function() {
        this._updateConfig();
        this._container[ 0 ].renderAll();
        this._container[ 1 ].renderAll();
      },
      
      _updateConfig : function() {
        var configLeft = this._container[ 0 ].getRenderConfig();
        var configRight = this._container[ 1 ].getRenderConfig();
        for( var key in this._config ) {
          if( this._config[ key ] instanceof Array ) {
            configLeft[ key ] = this._config[ key ].concat();
            configRight[ key ] = this._config[ key ].concat();
          } else {
            configLeft[ key ] = this._config[ key ];
            configRight[ key ] = this._config[ key ];
          }
        }
        configRight.hasCheckBoxes = false;
        var columnOrder = this._getColumnOrder();
        var rightColumnsOffset = 0;
        if( columnOrder.length > this._fixedColumns ) {
          rightColumnsOffset = this._config.itemLeft[ columnOrder[ this._fixedColumns ] ];
        }
        for( var i = 0; i < columnOrder.length; i++ ) {
          var column = columnOrder[ i ];
          if( i < this._fixedColumns ) {
            configRight.itemWidth[ column ] = 0;            
          } else {
            configLeft.itemWidth[ column ] = 0;
            configRight.itemLeft[ column ] -= rightColumnsOffset;
            configRight.itemImageLeft[ column ] -= rightColumnsOffset;
            configRight.itemTextLeft[ column ] -= rightColumnsOffset;
          }
        }
        if( this._splitOffset !== rightColumnsOffset ) {
          this._splitOffset = rightColumnsOffset;
          this._layoutX();
        }
      },

      _layoutX : function() {
        var leftWidth = Math.min( this._splitOffset, this._width );
        this._container[ 0 ].setWidth( leftWidth );
        this._container[ 0 ].setRowWidth( leftWidth );
        this._container[ 1 ].setLeft( leftWidth );
        this._container[ 1 ].setWidth( this._width - leftWidth );
        this._container[ 1 ].setRowWidth( this._rowWidth - leftWidth );
      },

      _getColumnOrder : function() {
        var result = [];
        var offsets = this._config.itemLeft.concat();
        var sorted = offsets.concat().sort( function( a, b ){ return a - b; } );
        for( var i = 0; i < sorted.length; i++ ) {
          var pos = offsets.indexOf( sorted[ i ] );
          result[ i ] = pos;
          offsets[ pos ] = null; // TODO [tb] : test
        }
        return result;
      },

      _onRowOver : function( event ) {
        var eventTarget = event.getCurrentTarget();
        for( var i = 0; i < this._container.length; i++ ) {
          if( this._container[ i ] !== eventTarget ) {
            this._container[ i ].setHoverItem( eventTarget.getHoverItem() );
          }
        }
      }

    }
    
  }

} );
