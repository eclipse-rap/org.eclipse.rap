/*******************************************************************************
 * Copyright (c) 2002, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/

rwt.qx.Class.define( "rwt.widgets.Group", {
  extend : rwt.widgets.base.Parent,

  construct : function() {
    this.base( arguments );
    // Sub widgets
    this._createFrameObject();
    this._createLegendObject();

    // Processing parameters
    this.setLegend( "" );

    // Enable method remapping
    this.remapChildrenHandlingTo(this._frameObject);
    this.setOverflow( "hidden" );
    var themeValues = new rwt.theme.ThemeValues( {} );
    this._themeBackgroundColor = themeValues.getCssColor( "Group-Label", "background-color" );
    themeValues.dispose();
    // Make sure that the 'labelObject' is created
    var labelObject = this.getLegendObject().getLabelObject();
    if( labelObject == null ) {
      this.setLegend( "(empty)" );
      this.setLegend( "" );
    }
    labelObject = this.getLegendObject().getLabelObject();
    labelObject.setMode( "html" );
    this.addEventListener( "changeBackgroundColor",
                           this._onChangeBackgroundColor,
                           this );
    this.addEventListener( "changeFont",
                           this._onChangeFont,
                           this );
    this.getLegendObject().addEventListener( "mouseover",
                                             this._onMouseOver,
                                             this );
    this.getLegendObject().addEventListener( "mouseout",
                                             this._onMouseOut,
                                             this );
    // Disable scrolling (see bug 345903)
    rwt.widgets.base.Widget.disableScrolling( this );
  },

  destruct : function() {
    this.removeEventListener( "changeBackgroundColor",
                              this._onChangeBackgroundColor,
                              this );
    this.removeEventListener( "changeFont",
                              this._onChangeFont,
                              this );
    this.getLegendObject().removeEventListener( "mouseover",
                                                this._onMouseOver,
                                                this );
    this.getLegendObject().removeEventListener( "mouseout",
                                                this._onMouseOut,
                                                this );
    this._disposeObjects("_legendObject", "_frameObject");
    this.setMnemonicIndex( null );
  },

  properties :
  {
    appearance :
    {
      refine : true,
      init : "group-box"
    }
  },

  members : {

    setText : function( value ) {
      this._rawText = value;
      this._mnemonicIndex = null;
      this._applyText( false );
    },

    setMnemonicIndex : function( value ) {
      this._mnemonicIndex = value;
      var mnemonicHandler = rwt.widgets.util.MnemonicHandler.getInstance();
      if( ( typeof value === "number" ) && ( value >= 0 ) ) {
        mnemonicHandler.add( this, this._onMnemonic );
      } else {
        mnemonicHandler.remove( this );
      }
    },

    getMnemonicIndex : function() {
      return this._mnemonicIndex;
    },

    _onMnemonic : function( event ) {
      switch( event.type ) {
        case "show":
          this._applyText( true );
        break;
        case "hide":
          this._applyText( false );
        break;
        case "trigger":
          var charCode = this._rawText.toUpperCase().charCodeAt( this._mnemonicIndex );
          if( event.charCode === charCode ) {
            var widget = this._findFirstFocusableChild( this );
            if( widget != null ) {
              widget.focus();
            }
            event.success = true;
          }
        break;
      }
    },

    _applyText : function( mnemonic ) {
      var EncodingUtil = rwt.util.Encoding;
      if( this._rawText ) {
        var mnemonicIndex = mnemonic ? this._mnemonicIndex : undefined;
        var text = EncodingUtil.escapeText( this._rawText, mnemonicIndex );
        if( this.hasState( "rwt_WRAP" ) ) {
          text = EncodingUtil.replaceNewLines( text, "<br/>" );
        }
        this.setLegend( text );
      } else {
        this.setLegend( null );
      }
    },

    addState : function( state ) {
      this.base( arguments, state );
      if( state.substr( 0, 8 ) == "variant_" ) {
        this._legendObject.addState( state );
        this._frameObject.addState( state );
      }
    },

    removeState : function( state ) {
      this.base( arguments, state );
      if( state.substr( 0, 8 ) == "variant_" ) {
        this._legendObject.removeState( state );
        this._frameObject.removeState( state );
      }
    },

    _onChangeBackgroundColor : function( evt ) {
      var newColor = evt.getValue();
      if( this._themeBackgroundColor === "undefined" ) {
        this.getLegendObject().setBackgroundColor( newColor );
      }
    },

    _onChangeFont : function( evt ) {
      var newFont = evt.getValue();
      this.getLegendObject().setFont( newFont );
    },

    _onMouseOver : function( event ) {
      this.getLegendObject().addState( "over" );
      this.getFrameObject().addState( "over" );
    },

    _onMouseOut : function( event ) {
      this.getLegendObject().removeState( "over" );
      this.getFrameObject().removeState( "over" );
    },

    _layoutPost : function( changes ) {
      this.base( arguments, changes );
      this._frameObject._layoutPost( changes );
    },

    //////////////////
    // apply subelement IDs

    applyObjectId : function( id ) {
      this.base( arguments, id );
      if( rwt.widgets.base.Widget._renderHtmlIds ) {
        var labelObject = this.getLegendObject().getLabelObject();
        labelObject.setHtmlAttribute( "id", id + "-label" );
      }
    },

    /**
     * Creates the legend sub widget
     *
     * @type member
     * @return {void}
     */
    _createLegendObject : function() {
      this._legendObject = new rwt.widgets.base.Atom();
      this._legendObject.setAppearance( "group-box-legend" );
      this.add( this._legendObject );
    },

    /**
     * Creates the frame sub widget
     *
     * @type member
     * @return {void}
     */
    _createFrameObject : function() {
      this._frameObject = new rwt.widgets.base.Parent();
      this._frameObject.setAppearance( "group-box-frame" );
      this.add( this._frameObject );
    },




    /*
    ---------------------------------------------------------------------------
      GETTER FOR SUB WIDGETS
    ---------------------------------------------------------------------------
    */

    /**
     * Accessor method for the frame sub widget
     *
     * @type member
     * @return {rwt.widgets.base.Parent} frame sub widget
     */
    getFrameObject : function() {
      return this._frameObject;
    },


    /**
     * Accessor method for the legend sub widget
     *
     * @type member
     * @return {rwt.widgets.base.Atom} legend sub widget
     */
    getLegendObject : function() {
      return this._legendObject;
    },

    /*
    ---------------------------------------------------------------------------
      SETTER/GETTER
    ---------------------------------------------------------------------------
    */

    /**
     * Sets the label of the legend sub widget if the given string is
     * valid. Otherwise the legend sub widget get not displayed.
     *
     * @type member
     * @param vLegend {String} new label of the legend sub widget
     * @return {void}
     */
    setLegend : function(vLegend) {
      if (vLegend !== "" && vLegend !== null) {
        this._legendObject.setLabel(vLegend);
        this._legendObject.setDisplay(true);
      } else {
        this._legendObject.setDisplay(false);
      }
    },

    /**
     * Accessor method for the label of the legend sub widget
     *
     * @type member
     * @return {String} Label of the legend sub widget
     */
    getLegend : function() {
      return this._legendObject.getLabel();
    },


    /**
     * Sets the icon of the legend sub widget.
     *
     * @type member
     * @param vIcon {String} source of the new icon of the legend sub widget
     * @return {void}
     */
    setIcon : function(vIcon) {
      this._legendObject.setIcon(vIcon);
    },


    /**
     * Accessor method for the icon of the legend sub widget
     *
     * @type member
     * @return {String} source of the new icon of the legend sub widget
     */
    getIcon : function() {
      this._legendObject.getIcon();
    },

    _findFirstFocusableChild : function( parent ) {
      var ObjectRegistry = rwt.remote.ObjectRegistry;
      var WidgetUtil = rwt.widgets.util.WidgetUtil;
      var result = null;
      var ids = WidgetUtil.getChildIds( parent );
      for( var i = 0; i < ids.length && result === null; i++ ) {
        var child = ObjectRegistry.getObject( ids[ i ] );
        if( WidgetUtil.getChildIds( child ) ) {
          result = this._findFirstFocusableChild( child );
        } else if( child.isSeeable() && child.isEnabled() ) {
          result = child;
        }
      }
      return result;
    }


  }

});
