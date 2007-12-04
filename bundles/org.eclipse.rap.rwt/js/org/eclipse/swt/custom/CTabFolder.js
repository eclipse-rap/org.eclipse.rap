/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.swt.custom.CTabFolder", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function() {
    this.base( arguments );
    this.setTabIndex( 1 );
    this.setHideFocus( true );
    this._hasFolderListener = false;
    this._hasSelectionListener = false;
    this._tabPosition = "top";
    this._tabHeight = 20;
    //
    var borderColor = "#c0c0c0";
    var border = new qx.ui.core.Border( 1, "solid", borderColor );
    this.setBorder( border );
    //
    this._chevron = null;
    this._chevronMenu = null;
    // Minimize/maximize buttons, initially non-existing
    this._minMaxState = "normal";  // valid states: min, max, normal
    this._maxButton = null;
    this._minButton = null;
    // Construct highlight border lines
    var highlightBorder = new qx.ui.core.Border();
    highlightBorder.setLeft( 2, "solid", borderColor );
    this._highlightLeft = new qx.ui.basic.Atom();
    this._highlightLeft.setBorder( highlightBorder );
    this._highlightLeft.setWidth( 2 );
    this.add( this._highlightLeft );
    highlightBorder = new qx.ui.core.Border();
    highlightBorder.setRight( 2, "solid", borderColor );
    this._highlightRight = new qx.ui.basic.Atom();
    this._highlightRight.setBorder( highlightBorder );
    this._highlightRight.setWidth( 2 );
    this.add( this._highlightRight );
    highlightBorder = new qx.ui.core.Border();
    highlightBorder.setTop( 2, "solid", borderColor );
    this._highlightTop = new qx.ui.basic.Atom();
    this._highlightTop.setBorder( highlightBorder );
    this._highlightTop.setLeft( 0 );
    this._highlightTop.setHeight( 2 );
    this.add( this._highlightTop );
    highlightBorder = new qx.ui.core.Border();
    highlightBorder.setTop( 2, "solid", borderColor );
    this._highlightBottom = new qx.ui.basic.Atom();
    this._highlightBottom.setBorder( highlightBorder );
    this._highlightBottom.setLeft( 0 );
    this._highlightBottom.setHeight( 2 );
    this.add( this._highlightBottom );
    // Create horizontal line that separates the button bar from the rest of
    // the client area
    border = new qx.ui.core.Border();
    border.setTop( 1, "solid", borderColor );
    this._separator = new qx.ui.basic.Atom();
    this._separator.setBorder( border );
    this._separator.setLeft( 0 );
//    this._separator.setTop( this._tabHeight );
    this._separator.setHeight( 1 );
    this.add( this._separator );
    // Add resize listeners to update selection border (this._highlightXXX)
    this.addEventListener( "changeWidth", this._updateHighlightBorders, this );
    this.addEventListener( "changeHeight", this._updateHighlightBorders, this );
    // Add keypress listener to select items with left/right keys
    this.addEventListener( "keypress", this._onKeyPress, this );
  },

  destruct : function() {
    // use hideMin/MaxButton to dispose of toolTips
    this.hideMinButton();
    this.hideMaxButton();
    this.removeEventListener( "changeWidth", this._updateHighlightBorders, this );
    this.removeEventListener( "changeHeight", this._updateHighlightBorders, this );
    this.removeEventListener( "keypress", this._onKeyPress, this );
  },

  statics : { 
    BUTTON_SIZE : 18,
    
    MIN_TOOLTIP : "Minimize",
    MAX_TOOLTIP : "Maximize",
    RESTORE_TOOLTIP : "Restore",
    CHEVRON_TOOLTIP : "Show List",
    CLOSE_TOOLTIP : "Close",
    
    setToolTipTexts : function( min, max, restore, chevron, close ) {
      org.eclipse.swt.custom.CTabFolder.MIN_TOOLTIP = min;
      org.eclipse.swt.custom.CTabFolder.MAX_TOOLTIP = max;
      org.eclipse.swt.custom.CTabFolder.RESTORE_TOOLTIP = restore;
      org.eclipse.swt.custom.CTabFolder.CHEVRON_TOOLTIP = chevron;
      org.eclipse.swt.custom.CTabFolder.CLOSE_TOOLTIP = close;
    }
  },

  members : {
    /* valid values; "top", "bottom" */
    setTabPosition : function( tabPosition ) {
      this._tabPosition = tabPosition;
      // update tab items
      var children = this.getChildren();
      for( var i = 0; i < children.length; i++ ) {
      	if( children[ i ].classname === "org.eclipse.swt.custom.CTabItem" ) {
      	  children[ i ].setTabPosition( tabPosition );
      	}
      }
      this._updateHighlightBorders();
    },
    
    getTabPosition : function() {
      return this._tabPosition;
    },
    
    setTabHeight : function( tabHeight ) {
      this._tabHeight = tabHeight;
      this._separator.setTop( this._tabHeight );
      var buttonTop = this._getButtonTop();
      if( this._minButton != null ) {
        this._minButton.setTop( buttonTop );
      }
      if( this._maxButton != null ) {
        this._maxButton.setTop( buttonTop );
      }
      if( this._chevron != null ) {
        this._chevron.setTop( buttonTop );
      }
      this._updateHighlightBorders();
    },

    // TODO [rh] optimize usage of border objects (get, change, set)
    setSelectionBackground : function( color ) {
      var highlightBorder = new qx.ui.core.Border();
      highlightBorder.setLeft( 2, qx.constant.Style.BORDER_SOLID, color );
      this._highlightLeft.setBorder( highlightBorder );

      highlightBorder = new qx.ui.core.Border();
      highlightBorder.setRight( 2, qx.constant.Style.BORDER_SOLID, color );
      this._highlightRight.setBorder( highlightBorder );

      highlightBorder = new qx.ui.core.Border();
      highlightBorder.setTop( 2, qx.constant.Style.BORDER_SOLID, color );
      this._highlightTop.setBorder( highlightBorder );

      highlightBorder = new qx.ui.core.Border();
      highlightBorder.setTop( 2, qx.constant.Style.BORDER_SOLID, color );
      this._highlightBottom.setBorder( highlightBorder );
    },

    _getButtonTop : function() {
      return ( this._tabHeight / 2 ) - ( org.eclipse.swt.custom.CTabFolder.BUTTON_SIZE / 2 );
    },

    showChevron : function( left, top, width, height ) {
      if( this._chevron == null ) {
        // Create chevron button
        this._chevron = new qx.ui.toolbar.Button();
        this._chevron.addState( "rwt_FLAT" );
        this._chevron.setShow( qx.constant.Style.BUTTON_SHOW_ICON );
        this._chevron.addEventListener( "execute", this._onChevronExecute, this );
        this._chevron.setIcon( "widget/ctabfolder/chevron.gif" );
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        wm.setToolTip( this._chevron, 
                       org.eclipse.swt.custom.CTabFolder.CHEVRON_TOOLTIP );
        this.add( this._chevron );
      }
      this._chevron.setTop( top );
      this._chevron.setLeft( left );
      this._chevron.setWidth( width );
      this._chevron.setHeight( height );
    },

    hideChevron : function() {
      if( this._chevron != null ) {
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        wm.setToolTip( this._chevron, null );
        this._chevron.removeEventListener( "execute", this._onChevronExecute, this );
        this.remove( this._chevron );
        this._chevron.dispose();
        this._chevron = null;
      }
    },

    setMinMaxState : function( state ) {
      this._minMaxState = state;
      var minIcon = "";
      var maxIcon = "";
      var minToolTip = "";
      var maxToolTip = "";
      switch( state ) {
        case "min":
          minIcon = "widget/ctabfolder/restore.gif";
          maxIcon = "widget/ctabfolder/maximize.gif";
          minToolTip = org.eclipse.swt.custom.CTabFolder.RESTORE_TOOLTIP;
          maxToolTip = org.eclipse.swt.custom.CTabFolder.MAX_TOOLTIP;
          break;
        case "max":
          minIcon = "widget/ctabfolder/minimize.gif";
          maxIcon = "widget/ctabfolder/restore.gif";
          minToolTip = org.eclipse.swt.custom.CTabFolder.MIN_TOOLTIP;
          maxToolTip = org.eclipse.swt.custom.CTabFolder.RESTORE_TOOLTIP;
          break;
        case "normal":
          minIcon = "widget/ctabfolder/minimize.gif";
          maxIcon = "widget/ctabfolder/maximize.gif";
          minToolTip = org.eclipse.swt.custom.CTabFolder.MIN_TOOLTIP;
          maxToolTip = org.eclipse.swt.custom.CTabFolder.MAX_TOOLTIP;
          break;
      }
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      if( this._minButton != null ) {
        this._minButton.setIcon( minIcon );
        wm.setToolTip( this._minButton, minToolTip );
      }
      if( this._maxButton != null ) {
        this._maxButton.setIcon( maxIcon );
        wm.setToolTip( this._maxButton, maxToolTip );
      }
    },

    showMaxButton : function( left, top, width, height ) {
      if( this._maxButton == null ) {
        this._maxButton = new qx.ui.toolbar.Button();
        this._maxButton.addState( "rwt_FLAT" );
        this._maxButton.setShow( qx.constant.Style.BUTTON_SHOW_ICON );
        this.setMinMaxState( this._minMaxState );  // initializes the icon according to current state
        this._maxButton.addEventListener( "execute", this._onMinMaxExecute, this );
        this.add( this._maxButton );
      }
      this._maxButton.setTop( top );
      this._maxButton.setLeft( left );
      this._maxButton.setWidth( width );
      this._maxButton.setHeight( height );
    },

    hideMaxButton : function() {
      if( this._maxButton != null ) {
        this._maxButton.removeEventListener( "execute", 
                                             this._onMinMaxExecute, 
                                             this );
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        wm.setToolTip( this._maxButton, null );
        this.remove( this._maxButton );
        this._maxButton.dispose();
        this._maxButton = null;
      }
    },

    showMinButton : function( left, top, width, height ) {
      if( this._minButton == null ) {
        this._minButton = new qx.ui.toolbar.Button();
        this._minButton.addState( "rwt_FLAT" );
        this._minButton.setShow( qx.constant.Style.BUTTON_SHOW_ICON );
        this.setMinMaxState( this._minMaxState );  // initializes the icon according to current state
        this._minButton.addEventListener( "execute", this._onMinMaxExecute, this );
        this.add( this._minButton );
      }
      this._minButton.setTop( top );
      this._minButton.setLeft( left );
      this._minButton.setWidth( width );
      this._minButton.setHeight( height );
    },

    hideMinButton : function( left ) {
      if( this._minButton != null ) {
        this._minButton.removeEventListener( "execute", 
                                             this._onMinMaxExecute, 
                                             this );
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        wm.setToolTip( this._minButton, null );
        this.remove( this._minButton );
        this._minButton.dispose();
        this._minButton = null;
      }
    },

    setHasFolderListener : function( hasFolderListener ) {
      this._hasFolderListener = hasFolderListener;
    },

    setHasSelectionListener : function( value ) {
      this._hasSelectionListener = value;
    },

    _updateHighlightBorders : function() {
      var separatorHeight = this._separator.getHeight();
      // ex _onChangeWidth
      this._separator.setWidth( this.getWidth() - 2 );
      this._highlightRight.setLeft( this.getWidth() - 2 - this._highlightRight.getWidth() );
      this._highlightTop.setWidth( this.getWidth() - 2 );
      this._highlightBottom.setWidth( this.getWidth() - 2 );
      // ex _onChangeHeight
      var top;
      if( this._tabPosition === "top" ) {
        this._separator.setTop( this._tabHeight );
        this._highlightBottom.setTop( this.getHeight() - 4 );
        this._highlightTop.setTop( this._tabHeight + separatorHeight );
        top = this._tabHeight + separatorHeight + 2;
      } else { // tabPosition == "bottom"
        this._separator.setTop( this.getHeight() - ( this._tabHeight + 1 ) );
        this._highlightBottom.setTop( this.getHeight() - ( this._tabHeight + separatorHeight + 2 ) );
        this._highlightTop.setTop( 0 );
        top = 2;
      }
      var height = this.getHeight() - ( this._tabHeight + separatorHeight + 4 );
      this._highlightLeft.setTop( top );
      this._highlightLeft.setHeight( height );
      this._highlightRight.setTop( top );
      this._highlightRight.setHeight( height );
    },

    _onChevronExecute : function( evt ) {
      if( this._chevronMenu == null || !this._chevronMenu.isSeeable() ) {
        if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
          var wm = org.eclipse.swt.WidgetManager.getInstance();
          var id = wm.findIdByWidget( this );
          var req = org.eclipse.swt.Request.getInstance();
          req.addEvent( "org.eclipse.swt.events.ctabFolderShowList", id );
          req.send();
        }
      }
    },

    _onMinMaxExecute : function( evt ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        var event;
        if ( evt.getTarget() == this._minButton ) {
          // Minimize button was pressed
          if( this._minMaxState == "min" ) {
            this.setMinMaxState( "normal" );
            event = "org.eclipse.swt.events.ctabFolderRestored";
          } else {
            this.setMinMaxState( "min" );
            event = "org.eclipse.swt.events.ctabFolderMinimized";
          }
        } else {
          // Maximize button was pressed
          if( this._minMaxState == "normal" || this.minMaxState == "min" ) {
            this.setMinMaxState( "max" );
            event = "org.eclipse.swt.events.ctabFolderMaximized";
          } else {
            this.setMinMaxState( "normal" );
            event = "org.eclipse.swt.events.ctabFolderRestored";
          }
        }
        var id = org.eclipse.swt.WidgetManager.getInstance().findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
        req.addParameter( id + ".minimized", this._minMaxState == "min" );
        req.addParameter( id + ".maximized", this._minMaxState == "max" );
        if( this._hasFolderListener ) {
          req.addEvent( event, id );
          req.send();
        }
      }
    },

    _onKeyPress : function( evt ) {
      switch( evt.getKeyIdentifier() ) {
        case "Left":
          // TODO [rh] implementatin missing: select tab item to the left
          evt.stopPropagation();
          break;
        case "Right":
          // TODO [rh] implementatin missing: select tab item to the right
          evt.stopPropagation();
          break;
      }
    },

    // TODO [rst] Change to respect _hasSelectionListener as soon as server-side
    // code is revised accordingly -> CTabFolderLCA.readData().
    _notifyItemClick : function(item) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        if( !item.isSelected() ) {
          // deselect any previous selected CTabItem
          var items = this.getChildren();
          for( var i = 0; i < items.length; i++ ) {
            if ( items[ i ].classname == "org.eclipse.swt.custom.CTabItem" ) {
              items[ i ].setSelected( false );
            }
          }
          item.setSelected( true );
          var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
          var req = org.eclipse.swt.Request.getInstance();
          var id = widgetManager.findIdByWidget( this );
          var itemId = widgetManager.findIdByWidget( item );
          req.addParameter( id + ".selectedItemId", itemId );
          req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
          req.send();
        }
      }
    },

    _notifyItemDblClick : function( item ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
        if( this._hasSelectionListener ) {
          var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
          var req = org.eclipse.swt.Request.getInstance();
          var id = widgetManager.findIdByWidget( this );
          var itemId = widgetManager.findIdByWidget( item );
          // TODO [rst] remove this parameter as soon as server-side code is revised
          //      -> CTabFolderLCA.readData()
          req.addParameter( id + ".selectedItemId", itemId );
          req.addEvent( "org.eclipse.swt.events.widgetDefaultSelected", id );
          req.send();
        }
      }
    }
  }
});
