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

rwt.qx.Class.define( "rwt.widgets.CTabItem", {
  extend : rwt.widgets.base.Atom,

  construct : function( parent, canClose ) {
    this.base( arguments );
    if( parent.classname != "rwt.widgets.CTabFolder" ) {
      throw new Error( "illegal parent, must be a CTabFolder" );
    }
    this._parent = parent;
    this.setAppearance( "ctab-item" );
    this.setVerticalChildrenAlign( rwt.widgets.util.Layout.ALIGN_MIDDLE );
    this.setHorizontalChildrenAlign( rwt.widgets.util.Layout.ALIGN_LEFT );
    this.setOverflow( "hidden" );
    this.setTabIndex( null );
    // Set the label part to 'html mode'
    this.setLabel( "(empty)" );
    this.getLabelObject().setMode( "html" );
    this.getLabelObject().setVerticalAlign( rwt.widgets.util.Layout.ALIGN_MIDDLE );
    this.setLabel( "" );
    this._selected = false;
    this._showClose = false;
    this._rawText = null;
    this._mnemonicIndex = null;
    this._canClose = canClose;
    this.updateForeground();
    this.updateBackground();
    this.updateBackgroundImage();
    this.updateBackgroundGradient();
    this.setTabPosition( parent.getTabPosition() );
    // TODO [rst] change when a proper state inheritance concept exists
    if( parent.hasState( "rwt_BORDER" ) ) {
      this.addState( "rwt_BORDER" );
    }
    this._closeButton = new rwt.widgets.base.Image();
    this._closeButton.setAppearance( "ctab-close-button" );
    this._closeButton.setWidth( 20 );
    this._closeButton.addEventListener( "click", this._onClose, this );
    var wm = rwt.remote.WidgetManager.getInstance();
    wm.setToolTip( this._closeButton, rwt.widgets.CTabFolder.CLOSE_TOOLTIP );
    this.add( this._closeButton );
    this.updateCloseButton();
    this.addEventListener( "mouseover", this._onMouseOver, this );
    this.addEventListener( "mouseout", this._onMouseOut, this );
    this.addEventListener( "click", this._onClick, this );
    this.addEventListener( "dblclick", this._onDblClick, this );
    this.addEventListener( "changeParent", this._onChangeParent, this );
    this.addEventListener( "changeLeft", this._onChangeLeft, this );
  },

  destruct : function() {
    this.removeEventListener( "mouseover", this._onMouseOver, this );
    this.removeEventListener( "mouseout", this._onMouseOut, this );
    this.removeEventListener( "click", this._onClick, this );
    this.removeEventListener( "dblclick", this._onDblClick, this );
    this.removeEventListener( "changeParent", this._onChangeParent, this );
    this.removeEventListener( "changeLeft", this._onChangeLeft, this );
    this._closeButton.removeEventListener( "click", this._onClose, this );
    var wm = rwt.remote.WidgetManager.getInstance();
    wm.setToolTip( this._closeButton, null );
    this._closeButton.dispose();
    this._closeButton = null;
    this.setMnemonicIndex( null );
  },

  statics : {
    STATE_OVER : "over",
    STATE_SELECTED : "selected",

    IMG_CLOSE : rwt.remote.Server.RESOURCE_PATH + "widget/rap/ctabfolder/close.gif",
    IMG_CLOSE_HOVER : rwt.remote.Server.RESOURCE_PATH + "widget/rap/ctabfolder/close_hover.gif"
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

    _applyText : function( mnemonic ) {
      if( this._rawText ) {
        var mnemonicIndex = mnemonic ? this._mnemonicIndex : undefined;
        var text = rwt.util.Encoding.escapeText( this._rawText, mnemonicIndex );
        this.setLabel( text );
      } else {
        this.setLabel( null );
      }
    },

    setTabPosition : function( tabPosition ) {
      if( tabPosition === "top" ) {
        this.addState( "barTop" );
      } else {
        this.removeState( "barTop" );
      }
    },

    setSelected : function( selected ) {
      if( this._selected !== selected ) {
        this._selected = selected;
        if( selected ) {
          this.addState( rwt.widgets.CTabItem.STATE_SELECTED );
        } else {
          this.removeState( rwt.widgets.CTabItem.STATE_SELECTED );
        }
        this._updateNextSelected();
        this.updateForeground();
        this.updateBackground();
        this.updateBackgroundImage();
        this.updateBackgroundGradient();
        this.updateCloseButton();
      }
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
            this._parent._notifyItemClick( this );
            event.success = true;
          }
        break;
      }
    },

    _updateNextSelected : function() {
      var prevItem = null;
      var findSelected = false;
      var children = this._parent.getChildren();
      for( var i = 0; i < children.length && !findSelected; i++ ) {
        if( children[ i ].classname === "rwt.widgets.CTabItem" ) {
          findSelected = children[ i ].isSelected();
          if( prevItem != null ) {
            if( findSelected ) {
              prevItem.addState( "nextSelected" );
            } else {
              prevItem.removeState( "nextSelected" );
            }
          }
          prevItem = children[ i ];
        }
      }
    },

    isSelected : function() {
      return this._selected;
    },

    setShowClose : function( value ) {
      this._showClose = value;
      this.updateCloseButton();
    },

    updateForeground : function() {
      var color = this.isSelected()
                ? this._parent.getSelectionForeground()
                : this._parent.getTextColor();
      if( color != null ) {
        this.setTextColor( color );
      } else {
        this.resetTextColor();
      }
    },

    updateBackground : function() {
      var color = this.isSelected() ? this._parent.getSelectionBackground() : null;
      if( color != null ) {
        this.setBackgroundColor( color );
      } else {
        this.resetBackgroundColor();
      }
    },

    updateBackgroundImage : function() {
      var image = this.isSelected() ? this._parent.getSelectionBackgroundImage() : null;
      if( image != null ) {
        this.setUserData( "backgroundImageSize", image.slice( 1 ) );
        this.setBackgroundImage( image[ 0 ] );
      } else {
        this.resetBackgroundImage();
      }
    },

    updateBackgroundGradient : function() {
      var gradient = this.isSelected() ? this._parent.getSelectionBackgroundGradient() : null;
      if( gradient != null ) {
        this.setBackgroundGradient( gradient );
      } else {
        this.resetBackgroundGradient();
      }
    },

    updateCloseButton : function() {
      var visible = false;
      if( this._canClose || this._showClose ) {
        visible
          =  this.isSelected()
          || (    this._parent.getUnselectedCloseVisible()
               && this.hasState( rwt.widgets.CTabItem.STATE_OVER ) );
      }
      this._closeButton.setVisibility( visible );
    },

    _onMouseOver : function( evt ) {
      this.addState( rwt.widgets.CTabItem.STATE_OVER );
      if( evt.getTarget() == this._closeButton ) {
        this._closeButton.addState( rwt.widgets.CTabItem.STATE_OVER );
      }
      this.updateCloseButton();
    },

    _onMouseOut : function( evt ) {
      this.removeState( rwt.widgets.CTabItem.STATE_OVER );
      if( evt.getTarget() == this._closeButton ) {
        this._closeButton.removeState( rwt.widgets.CTabItem.STATE_OVER );
      }
      this.updateCloseButton();
    },

    _onClick : function( evt ) {
      if( !rwt.remote.EventUtil.getSuspended() ) {
        if( evt.getTarget() != this._closeButton ) {
          evt.getTarget().getParent()._notifyItemClick( evt.getTarget() );
        }
      }
    },

    _onDblClick : function( evt ) {
      if( evt.getTarget() != this._closeButton ) {
        evt.getTarget().getParent()._notifyItemDblClick( evt );
      }
    },

    _onClose : function( evt ) {
      if( !rwt.remote.EventUtil.getSuspended() ) {
        var server = rwt.remote.Server.getInstance();
        server.getRemoteObject( this.getParent() ).notify( "Folder", {
          "detail" : "close",
          "item" : rwt.remote.ObjectRegistry.getId( this )
        } );
      }
    },

    _onChangeParent : function( evt ) {
      if( !this._parent._isInGlobalDisposeQueue ) {
        this._updateNextSelected();
      }
    },

    _onChangeLeft : function( evt ) {
      if( this.getLeft() === 0 ) {
        this.addState( "firstItem" );
      } else {
        this.removeState( "firstItem" );
      }
    }
  }
} );
