/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

/**
 * Used to represent a visible TableItem in a Table widget.
 */
qx.Class.define( "org.eclipse.swt.widgets.TableRow", {
  extend : qx.ui.embed.HtmlEmbed,

  construct : function() {
    this.base( arguments );
    this.setAppearance( "table-row" );
    this.setSelectable( false );
    this._itemIndex = -1;
    this._variant = null;
    this.addEventListener( "mouseover", this._onMouseOver, this );
    this.addEventListener( "mouseout", this._onMouseOut, this );
  },
  
  destruct : function() {
    this.removeEventListener( "mouseover", this._onMouseOver, this );
    this.removeEventListener( "mouseout", this._onMouseOut, this );
  },

  members : {

    setLinesVisible : function( value ) {
      if( value ) {
        this.addState( "lines" );
      } else {
        this.removeState( "lines" );
      }
    },

    setItemIndex : function( value ) {
      if( value != this._itemIndex ) {
        this._itemIndex = value;
        if( value % 2 == 0 ) {
          this.addState( "even" );
        } else {
          this.removeState( "even" );
        }
        if( value == -1 ) {
          this.removeState( "over" );
        }
      }
    },

    getItemIndex : function() {
      return this._itemIndex;
    },

    setVariant : function( variant ) {
      if( this._variant != null && this._variant != variant ) {
        this.removeState( this._variant );
      }
      if( variant != null && variant != this._variant ) {
        this.addState( variant );
      }
      this._variant = variant;
    },
    
    _onMouseOver : function( evt ) {
      if( this._itemIndex != -1 ) {
        this.addState( "over" );
      }
    },
    
    _onMouseOut : function( evt ) {
      if( this._itemIndex != -1 ) {
        this.removeState( "over" );
      }
    },

    // Override default focus behaviour
    _applyStateStyleFocus : qx.core.Variant.select( "qx.client",
    {
      "mshtml" : function( states ) {
      },

      "gecko" : function( states ) {
        if( states.itemFocused ) {
          this.setStyleProperty( "MozOutline", "1px dotted invert" );
        } else {
          this.removeStyleProperty( "MozOutline" );
        }
      },

      "default" : function( states ) {
        if( states.itemFocused ) {
          this.setStyleProperty( "outline", "1px dotted invert" );
        } else {
          this.removeStyleProperty( "outline" );
        }
      }
    } )

  }
});
