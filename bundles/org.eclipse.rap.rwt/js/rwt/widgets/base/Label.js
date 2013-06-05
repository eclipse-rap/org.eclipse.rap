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

/**
 * The Label widget displays plain text or HTML text.
 *
 * Most complex qooxdoo widgets use instances of Label to display text.
 * The label supports auto sizing and internationalization.
 *
 * @appearance label
 */
rwt.qx.Class.define("rwt.widgets.base.Label",
{
  extend : rwt.widgets.base.Terminator,




  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  /**
   * @param text {String} The text of the label (see property {@link #text}).
   * @param mnemonic {String} The mnemonic of the label (see property {@link #mnemonic}).
   * @param mode {String} The mode of the label (see property {@link #mode}).
   */
// TODO [rh] unused: replacement for below (no qx code calls 3-args ctor)
  construct : function(text)
//  construct : function(text, mnemonic, mode)
  {
    this.base(arguments);

    if (text != null) {
      this.setText(text);
    }

// TODO [rh] unused
/*
     if (mode != null) {
      this.setMode(mode);
    }

    if (text != null) {
      this.setText(text);
    }

    if (mnemonic != null) {
      this.setMnemonic(mnemonic);
    }
*/

    // Property init
    this.initWidth();
    this.initHeight();
    this.initSelectable();
    this.initCursor();
    this.initWrap();
  },


  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

  properties :
  {
    appearance :
    {
      refine : true,
      init : "label"
    },

    width :
    {
      refine : true,
      init : "auto"
    },

    height :
    {
      refine : true,
      init : "auto"
    },

    allowStretchX :
    {
      refine : true,
      init : false
    },

    allowStretchY :
    {
      refine : true,
      init : false
    },

    selectable :
    {
      refine : true,
      init : false
    },

// RAP [rst] qx bug 455 http://bugzilla.qooxdoo.org/show_bug.cgi?id=455
//    cursor :
//    {
//      refine : true,
//      init : "default"
//    },



    /**
     * The text of the label. How the text is interpreted depends on the value of the
     * property {@link #mode}.
     */
    text :
    {
      apply : "_applyText",
      init : "",
      dispose : true,
// TODO [rh] unused
//      event : "changeText",
      check : "Label"
    },


    /**
     * Whether the text should be automatically wrapped into the next line
     */
    wrap :
    {
      check : "Boolean",
      init : false,
      nullable : true,
      apply : "_applyWrap"
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


    /**
     * Whether an ellipsis symbol should be rendered if there is not enough room for the full text.
     *
     * Please note: If enabled this conflicts with a custom overflow setting.
     */
    textOverflow :
    {
      check : "Boolean",
      init : true
// TODO [rh] unused: removed as the corresponding impl was also removed
//      apply : "_applyText"
    },

    /**
     * Set how the label text should be interpreted
     *
     * <ul>
     *   <li><code>text</code> will set the text verbatim. Leading and trailing white space will be reserved.</li>
     *   <li><code>html</code> will interpret the label text as html.</li>
     *   <li><code>auto</code> will try to guess whether the text represents an HTML string or plain text.
     *       This is how older qooxdoo versions treated the text.
     *   </li>
     * <ul>
     */
    mode :
    {
      check : [ "html", "text", "auto" ],
      init : "auto"
// TODO [rh] unused: replace with empty get/setMode functions
//      apply : "_applyText"
    } // , TODO [rh] unused: removed trailing comma, see below


    /** A single character which will be underlined inside the text. */
// TODO [rh] unused
//    mnemonic :
//    {
//      check : "String",
//      nullable : true,
//      apply : "_applyMnemonic"
//    }
  },




  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    _content : "",

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
     * Apply the font to the label.
     *
     * @type member
     * @param font {rwt.html.Font} new font.
     */
    _styleFont : function( font ) {
      this._invalidatePreferredInnerDimensions();
      if( font ) {
        font.render( this );
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
     * Apply the text color to the label.
     *
     * @type member
     * @param value {String} any acceptable CSS color
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
      WRAP SUPPORT
    ---------------------------------------------------------------------------
    */

    _applyWrap : function( value, old ) {
      if( value == null ) {
        this.removeStyleProperty( "whiteSpace" );
      } else {
        this.setStyleProperty( "whiteSpace", value ? "normal" : "nowrap" );
      }
    },




    /*
    ---------------------------------------------------------------------------
      TEXT HANDLING
    ---------------------------------------------------------------------------
    */


    /**
     * TODOC
     *
     * @type member
     * @param value {var} Current value
     * @param old {var} Previous value
     */
    _applyText : function(value, old) {
//      qx.locale.Manager.getInstance().connect(this._syncText, this, this.getText());
      // DONT USE 'value' as this func is misued by other properties than text
      this._syncText( this.getText() );
    },


    /**
     * Apply a new label text
     *
     * @param text {String} new label text
     */
    _syncText : function(text) {
      this._content = text;

      if (this._isCreated) {
        this._renderContent();
      }
    },


    /*
    ---------------------------------------------------------------------------
      PREFERRED DIMENSIONS
    ---------------------------------------------------------------------------
    */

    /**
     * Computes the needed dimension for the current text.
     *
     * @type member
     */
    _computeObjectNeededDimensions : function() {
      var fontProps = this._styleProperties;
      var calc = rwt.widgets.util.FontSizeCalculation;
      var dimensions = calc.computeTextDimensions( this._content, fontProps );
      this._cachedPreferredInnerWidth = dimensions[ 0 ];
      this._cachedPreferredInnerHeight = dimensions[ 1 ];
    },


    /**
     * overridden
     * @return {Integer}
     */
    _computePreferredInnerWidth : function()
    {
      this._computeObjectNeededDimensions();
      return this._cachedPreferredInnerWidth;
    },


    /**
     * overridden
     * @return {Integer}
     */
    _computePreferredInnerHeight : function()
    {
      this._computeObjectNeededDimensions();
      return this._cachedPreferredInnerHeight;
    },




    /*
    ---------------------------------------------------------------------------
      LAYOUT APPLY
    ---------------------------------------------------------------------------
    */

    /**
     * Creates an HTML fragment for the overflow symbol
     *
     * @param html {String} html string of the label
     * @param inner {Integer} inner width of the label
     * @return {String} html Fragment of the label with overflow symbol
     */
// TODO [rh] unused as not called anymore from replaced _postApply
//    __patchTextOverflow : function(html, inner) {
//      return (
//        "<div style='float:left;width:" + (inner-14) +
//        "px;overflow:hidden;white-space:nowrap'>" + html +
//        "</div><span style='float:left'>&hellip;</span>"
//      );
//    },


    // TODO [rh] replacement for original function below
    _postApply : function() {
      var html = this._content;
      var element = this._getTargetNode();
      if( html == null ) {
        element.innerHTML = "";
      } else {
        var style = element.style;
        if( !this.getWrap() ) {
          if( this.getInnerWidth() < this.getPreferredInnerWidth() ) {
            style.overflow = "hidden";
          } else {
            style.overflow = "";
          }
        }
        element.innerHTML = html;
      }
    }

    /*
    // overridden
    _postApply : function()
    {
      var html = this._content;
      var element = this._getTargetNode();

      if (html == null)
      {
        element.innerHTML = "";
        return;
      }

      if (this.getMnemonic())
      {
        if (this._mnemonicTest.test(html))
        {
          html = RegExp.$1 + "<span style=\"text-decoration:underline\">" + RegExp.$7 + "</span>" + RegExp.rightContext;
          this._isHtml = true;
        }
        else
        {
          html += " (" + this.getMnemonic() + ")";
        }
      }

      var style = element.style;

      if (this.getTextOverflow() && !this.getWrap())
      {
        if (this.getInnerWidth() < this.getPreferredInnerWidth())
        {
          style.overflow = "hidden";

          if (rwt.util.Variant.isSet("qx.client", "mshtml|webkit"))
          {
            style.textOverflow = "ellipsis";
          }
          else if (rwt.util.Variant.isSet("qx.client", "opera"))
          {
            style.OTextOverflow = "ellipsis";
          }
          else
          {
            html = this.__patchTextOverflow(html, this.getInnerWidth());
            this._isHtml = true;
          }
        }
        else
        {
          style.overflow = "";

          if (rwt.util.Variant.isSet("qx.client", "mshtml|webkit"))
          {
            style.textOverflow = "";
          }
          else if (rwt.util.Variant.isSet("qx.client", "opera"))
          {
            style.OTextOverflow = "";
          }
        }
      }

      if (this._isHtml)
      {
        element.innerHTML = html;
      }
      else
      {
        element.innerHTML = "";
        rwt.html.Element.setTextContent(element, html);
      }
    }
      */
  }
});
