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

qx.Class.define( "org.eclipse.rwt.widgets.GridColumn", {

  extend : qx.core.Target,

  construct : function( grid ) {
    this.base( arguments );
    this._grid = grid;
    this._index = 0;
    this._resizable = true;
    this._moveable = false;
    this._alignment = "left";
    this._left = 0;
    this._hasSelectionListener = false;
    this._width = 0;
    this._toolTip = null;
    this._customVariant = null;
    this._text = "";
    this._objectId;
    this._image = null;
    this._sortDirection = null;
    this._grid.addColumn( this );
  },

  destruct : function() {
    this._grid.removeColumn( this );
  },

  members : {

    setLeft : function( value ) {
      if( org.eclipse.swt.EventUtil.getSuspended() ) {
        this._left = value;
        this._update();
      } else {
        this._sendMoved( value );
      }
    },

    setWidth : function( value ) {
      if( org.eclipse.swt.EventUtil.getSuspended() ) {
        this._width = value;
        this._update();
      } else {
        this._sendResized( value );
      }
    },

    getLeft : function() {
      return this._left;
    },

    getWidth : function() {
      return this._width;
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

    setHasSelectionListener : function( value ) {
      this._hasSelectionListener = value;
    },

    handleSelectionEvent : function() {
      if( this._hasSelectionListener && !org.eclipse.swt.EventUtil.getSuspended() ) {
        var id = org.eclipse.rwt.protocol.ObjectManager.getId( this );
        var req = org.eclipse.swt.Request.getInstance();
        req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
        org.eclipse.swt.EventUtil.addWidgetSelectedModifier();
        req.send();
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

    _update : function() {
      this.dispatchSimpleEvent( "update" );
    },

    _sendResized : function( width ) {
      if( !org.eclipse.swt.EventUtil.getSuspended() ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
        req.addParameter( id + ".width", width );
        req.send();
      }
    },

    _sendMoved : function( left ) {
      if( !org.eclipse.swt.EventUtil.getSuspended() ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
        req.addParameter( id + ".left", left );
        req.send();
      }
    }

  }
} );
