/*******************************************************************************
 * Copyright (c) 2004, 2013 1&1 Internet AG, Germany, http://www.1und1.de,
 *                          EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Remote Application Platform
 ******************************************************************************/

rwt.qx.Class.define("rwt.widgets.base.HtmlEmbed",
{
  extend : rwt.widgets.base.Terminator,




  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  construct : function(vHtml)
  {
    this.base(arguments);

    if (vHtml != null) {
      this.setHtml(vHtml);
    }
  },




  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

  properties :
  {
    /** Any text string which can contain HTML, too */
    html :
    {
      check : "String",
      init : "",
      apply : "_applyHtml",
      event : "changeHtml"
    },


    /**
     * The alignment of the text inside the box
     */
    textAlign :
    {
      check : [ "left", "center", "right", "justify" ],
      nullable : true,
      themeable : true,
      apply : "_applyTextAlign"
    },


    /** Block inheritance as default for font property */
    font :
    {
      refine : true,
      init : null
    },


    /** Block inheritance as default for textColor property */
    textColor :
    {
      refine : true,
      init : null
    }
  },




  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    /*
    ---------------------------------------------------------------------------
      APPLY ROUTINES
    ---------------------------------------------------------------------------
    */

    /**
     * TODOC
     *
     * @type member
     */
    _applyHtml : function()
    {
      if (this._isCreated) {
        this._syncHtml();
      }
    },





    /*
    ---------------------------------------------------------------------------
      TEXTALIGN SUPPORT
    ---------------------------------------------------------------------------
    */

    _applyTextAlign : function( value, old ) {
      if( value === null ) {
        this.removeStyleProperty( "textAlign" );
      } else {
        this.setStyleProperty( "textAlign", value );
      }
    },





    /*
    ---------------------------------------------------------------------------
      FONT SUPPORT
    ---------------------------------------------------------------------------
    */

    _applyFont : function(value, old) {
      this._styleFont( value );
    },


    /**
     * @type member
     * @param value {rwt.html.Font}
     */
    _styleFont : function( value ) {
      if( value ) {
        value.render( this );
      } else {
        rwt.html.Font.reset( this );
      }
    },




    /*
    ---------------------------------------------------------------------------
      TEXT COLOR SUPPORT
    ---------------------------------------------------------------------------
    */

    _applyTextColor : function(value, old) {
      this._styleTextColor( value );
    },

    /**
     * @type member
     * @param value {var} any acceptable CSS color property
     */
    _styleTextColor : function( value ) {
      if( value ) {
        this.setStyleProperty( "color", value );
      } else {
        this.removeStyleProperty( "color" );
      }
    },




    /*
    ---------------------------------------------------------------------------
      ELEMENT HANDLING
    ---------------------------------------------------------------------------
    */

    /**
     * TODOC
     *
     * @type member
     * @return {void}
     */
    _applyElementData : function() {
      this._syncHtml();
    },


    /**
     * TODOC
     *
     * @type member
     * @return {void}
     */
    _syncHtml : function() {
      this._getTargetNode().innerHTML = this.getHtml();
    }
  }
});
