/*******************************************************************************
 * Copyright (c) 2004, 2012 1&1 Internet AG, Germany, http://www.1und1.de,
 *                          and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   1&1 Internet AG and others - original API and implementation
 *   EclipseSource - adaptation for the Eclipse Rich Ajax Platform
 ******************************************************************************/

/**
 * @appearance tab-view-button
 * @state checked Set by {@link #checked}
 * @state over
 */
qx.Class.define( "qx.ui.pageview.tabview.Button", {

  extend : qx.ui.pageview.AbstractButton,

  events: {
    "closetab" : "qx.event.type.Event"
  },

  properties : {
    appearance : {
      refine : true,
      init : "tab-view-button"
    },

    /** default Close Tab Button */
    showCloseButton : {
      check : "Boolean",
      init : false,
      apply : "_applyShowCloseButton",
      event : "changeShowCloseButton"
    },

    /** Close Tab Icon */
    closeButtonImage : {
      check : "String",
      init : "icon/16/actions/dialog-cancel.png",
      apply : "_applyCloseButtonImage"
    }
  },

  members : {

    /**
     * TODOC
     *
     * @type member
     * @param e {Event} TODOC
     * @return {void}
     */
    _onkeydown : function( e ) {
      var identifier = e.getKeyIdentifier();
      if( identifier == "Enter" || identifier == "Space" ) {
        // there is no toggeling, just make it checked
        this.setChecked( true );
      }
    },

    /**
     * TODOC
     *
     * @type member
     * @param e {Event} TODOC
     * @return {void}
     */
    _onkeypress : function( e ) {
      switch( e.getKeyIdentifier() ) {
        case "Left":
          var vPrev = this.getPreviousActiveSibling();
          if ( vPrev && vPrev != this ) {
            // we want to enable the outline border, because
            // the user used the keyboard for activation
            delete qx.event.handler.FocusHandler.mouseFocus;
            // focus previous tab
            vPrev.setFocused(true);
            // and naturally make it also checked
            vPrev.setChecked(true);
          }
        break;
        case "Right":
          var vNext = this.getNextActiveSibling();
          if( vNext && vNext != this ) {
            // we want to enable the outline border, because
            // the user used the keyboard for activation
            delete qx.event.handler.FocusHandler.mouseFocus;
            // focus next tab
            vNext.setFocused(true);
            // and naturally make it also checked
            vNext.setChecked(true);
          }
        break;
      }
    },

    /**
     * TODOC
     *
     * @type member
     * @param e {Event} TODOC
     * @return {void}
     */
    _ontabclose : function( e ) {
      this.createDispatchDataEvent( "closetab", this );
      e.stopPropagation();
    },

    _applyChecked : function( value, old ) {
      this.base( arguments, value, old );
      this.setZIndex( value ? 1 : 0 );
    },

    /**
     * TODOC
     *
     * @type member
     * @param value {var} Current value
     * @param old {var} Previous value
     */
    _applyShowCloseButton : function( value, old ) {
      // if no image exists, then create one
      if( !this._closeButtonImage ) {
        this._closeButtonImage = new qx.ui.basic.Image( this.getCloseButtonImage() );
      }
      if( value ) {
        this._closeButtonImage.addEventListener( "click", this._ontabclose, this );
        this.add( this._closeButtonImage );
      } else {
        this.remove( this._closeButtonImage );
        this._closeButtonImage.removeEventListener( "click", this._ontabclose, this );
      }
    },

    /**
     * TODOC
     *
     * @type member
     * @param value {var} Current value
     * @param old {var} Previous value
     */
    _applyCloseButtonImage : function( value, old ) {
      if( this._closeButtonImage ) {
        this._closeButtonImage.setSource( value );
      }
    },

    /**
     * TODOC
     *
     * @type member
     * @return {void}
     */
    _renderAppearance : function() {
      if( this.getView() ) {
        if( this.isFirstVisibleChild() ) {
         this.addState( "firstChild" );
        } else {
          this.removeState( "lastChild" );
        }
        if( this.isLastVisibleChild() ) {
          this.addState( "lastChild" );
        } else {
          this.removeState( "lastChild" );
        }
        if( this.getView().getAlignTabsToLeft() ) {
          this.addState( "alignLeft" );
        } else {
          this.removeState( "alignLeft" );
        }
        if( !this.getView().getAlignTabsToLeft() ) {
          this.addState( "alignRight" );
        } else {
          this.removeState( "alignRight" );
        }
        if( this.getView().getPlaceBarOnTop() ) {
          this.addState( "barTop" );
        } else {
          this.removeState( "barTop" );
        }
        if( !this.getView().getPlaceBarOnTop() ) {
          this.addState( "barBottom" );
        } else {
          this.removeState( "barBottom" );
        }
      }
      this.base( arguments );
    }
  },

  destruct : function() {
    this._disposeObjects( "_closeButtonImage" );
  }

} );
