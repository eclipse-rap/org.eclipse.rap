/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/

qx.Class.define( "rwt.widgets.GridColumn", {

  extend : qx.core.Target,

  construct : function( grid, isGroup ) {
    this.base( arguments );
    this._grid = grid;
    this._isGroup = isGroup ? true : false;
    this._index = 0;
    this._resizable = isGroup ? false : true;
    this._moveable = false;
    this._alignment = "left";
    this._group = null;
    this._left = 0;
    this._height = 0;
    this._visibility = true;
    this._expanded = true;
    this._hasSelectionListener = false;
    this._width = 0;
    this._toolTip = null;
    this._customVariant = null;
    this._objectId = null;
    this._text = "";
    this._font = null;
    this._image = null;
    this._footerText = "";
    this._footerFont = null;
    this._footerImage = null;
    this._sortDirection = null;
    this._check = false;
    this._grid.addColumn( this );
  },

  destruct : function() {
    this._grid.removeColumn( this );
    this.dispatchSimpleEvent( "dispose", { target : this } );
  },

  members : {

    setLeft : function( value ) {
      if( org.eclipse.swt.EventUtil.getSuspended() ) {
        this._left = value;
        this._update();
      } else {
        this._sendMove( value );
      }
    },

    getLeft : function() {
      return this._left;
    },

    setWidth : function( value ) {
      if( org.eclipse.swt.EventUtil.getSuspended() ) {
        this._width = value;
        this._update();
      } else {
        this._sendResize( value );
      }
    },

    getWidth : function() {
      return this._width;
    },

    setHeight : function( value ) {
      this._height = value;
      this._update();
    },

    getHeight : function() {
      return this._height;
    },

    setVisibility : function( value ) {
      this._visibility = value;
      this._update();
    },

    getVisibility : function() {
      return this._visibility;
    },

    setExpanded : function( value ) {
      this._expanded = value;
      this._update();
    },

    isExpanded : function() {
      return this._expanded;
    },

    setGroup : function( value ) {
      this._group = value;
    },

    getGroup : function() {
      return this._group;
    },

    setToolTip : function( value ) {
      this._toolTip = value;
      this._update();
    },

    getToolTip : function() {
      return this._toolTip;
    },

    setCustomVariant : function( value ) {
      this._customVariant = value;
      this._update();
    },

    getCustomVariant : function( value ) {
      return this._customVariant;
    },

    setText : function( value ) {
      this._text = value;
      this._update();
    },

    getText : function( value ) {
      return this._text;
    },

    setImage : function( value ) {
      this._image = value;
      this._update();
    },

    getImage : function( ) {
      return this._image;
    },

    setFont : function( value ) {
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      this._font = value ? wm._createFont.apply( wm, value ) : null;
      this._update();
    },

    getFont : function() {
      return this._font;
    },

    setFooterText : function( value ) {
      this._footerText = value;
      this._update();
    },

    getFooterText : function( value ) {
      return this._footerText;
    },

    setFooterImage : function( value ) {
      this._footerImage = value;
      this._update();
    },

    getFooterImage : function( ) {
      return this._footerImage;
    },

    setFooterFont : function( value ) {
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      this._footerFont = value ? wm._createFont.apply( wm, value ) : null;
      this._update();
    },

    getFooterFont : function() {
      return this._footerFont;
    },

    setIndex : function( value ) {
      this._index = value;
      this._update();
    },

    getIndex : function() {
      return this._index;
    },

    setSortDirection : function( value ) {
      this._sortDirection = value;
      this._update();
    },

    getSortDirection : function( value ) {
      return this._sortDirection;
    },

    setResizable : function( value ) {
      this._resizable = value;
    },

    getResizeable : function() {
      return this._resizable;
    },

    setMoveable : function( value ) {
      this._moveable = value;
    },

    getMoveable : function() {
      return this._moveable;
    },

    setCheck : function( value ) {
      this._grid.setCellCheck( this._index, value );
      this._check = value;
    },

    getCheck : function() {
      return this._check;
    },

    setHasSelectionListener : function( value ) {
      this._hasSelectionListener = value;
    },

    handleSelectionEvent : function( event ) {
      if( !org.eclipse.swt.EventUtil.getSuspended() ) {
        var isTreeEvent = this._isGroup && event.chevron;
        if( this._hasSelectionListener || isTreeEvent ) {
          var id = rwt.protocol.ObjectRegistry.getId( this );
          var req = rwt.remote.Server.getInstance();
          if( isTreeEvent ) {
            var eventStr = "org.eclipse.swt.events.";
            eventStr += this._expanded ? "treeCollapsed" : "treeExpanded";
            req.addEvent( eventStr, id );
          } else {
            req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
          }
          org.eclipse.swt.EventUtil.addWidgetSelectedModifier();
          req.send();
        }
      }
    },

    applyObjectId : function( id ) {
      this._objectId = id;
      this._update();
    },

    getObjectId : function() {
      return this._objectId;
    },

    setAlignment : function( value ) {
      this._grid.setAlignment( this._index, value );
      this._alignment = value;
      this._update();
    },

    getAlignment : function() {
      return this._alignment;
    },

    setFixed : function( value ) {
      this._fixed = value;
      this._update();
    },

    isFixed : function() {
      return this._fixed;
    },

    isGroup : function() {
      return this._isGroup;
    },

    _update : function() {
      this.dispatchSimpleEvent( "update" );
    },

    _sendResize : function( width ) {
      if( !org.eclipse.swt.EventUtil.getSuspended() ) {
//        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
//        var id = widgetManager.findIdByWidget( this );
//        var req = rwt.remote.Server.getInstance();
//        req.addParameter( id + ".width", width );
//        req.send();
        var serverColumn = rwt.remote.Server.getInstance().getServerObject( this );
        serverColumn.call( "resize", {
          "width" : width
        } );
      }
    },

    _sendMove : function( left ) {
      if( !org.eclipse.swt.EventUtil.getSuspended() ) {
//        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
//        var id = widgetManager.findIdByWidget( this );
//        var req = rwt.remote.Server.getInstance();
//        req.addParameter( id + ".left", left );
//        req.send();
        var serverColumn = rwt.remote.Server.getInstance().getServerObject( this );
        serverColumn.call( "move", {
          "left" : left
        } );
      }
    }

  }
} );
