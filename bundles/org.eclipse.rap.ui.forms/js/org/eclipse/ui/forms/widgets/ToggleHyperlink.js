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

rwt.qx.Class.define( "org.eclipse.ui.forms.widgets.ToggleHyperlink", {
  extend : rwt.widgets.base.Image,

  construct : function() {
    this.base( arguments );
    this.setCursor( "pointer" );
    this._hover = false;
    this._expanded = false;
    this._collapseNormal = null;
    this._collapseHover = null;
    this._expandHover = null;
    this._expandNormal = null;
    this.addEventListener( "mousemove", this._onMouseMove, this );
    this.addEventListener( "mouseout", this._onMouseOut, this );
  },

  destruct : function() {
    this.removeEventListener( "mousemove", this._onMouseMove, this );
    this.removeEventListener( "mouseout", this._onMouseOut, this );
  },

  members : {

    setImages : function( collapseNormal, collapseHover, expandNormal, expandHover ) {
      this._collapseNormal = collapseNormal;
      this._collapseHover = collapseHover != null ? collapseHover : collapseNormal;
      this._expandNormal = expandNormal;
      this._expandHover = expandHover != null ? expandHover : expandNormal;
      this._updateImage();
    },

    setExpanded : function( value ) {
      this._expanded = value;
      this._updateImage();
    },

    setHasDefaultSelectionListener : function( value ) {
      if( value ) {
        this.addEventListener( "click", rwt.remote.EventUtil.widgetDefaultSelected, this );
      } else {
        this.removeEventListener( "click", rwt.remote.EventUtil.widgetDefaultSelected, this );
      }
    },

    _onMouseMove : function( evt ) {
      this._hover = true;
      this._updateImage();
    },

    _onMouseOut : function( evt ) {
      this._hover = false;
      this._updateImage();
    },

    _updateImage : function() {
      var source;
      if( this._expanded ) {
        source = this._hover ? this._collapseHover : this._collapseNormal;
      } else {
        source = this._hover ? this._expandHover : this._expandNormal;
      }
      this.setSource( source );
    }

  }

} );

