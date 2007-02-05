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

qx.OO.defineClass(
  "org.eclipse.rap.rwt.custom.CTabFolder", 
  qx.ui.layout.CanvasLayout,
  function( canClose ) {
    qx.ui.layout.CanvasLayout.call( this );
    //
    this._canClose = canClose;
    this._hasFolderListener = false;
    this._tabHeight = 20;
    // 
    var black = new qx.renderer.color.Color( "black" );
    var border = new qx.renderer.border.Border( 1, "solid", black );
    this.setBorder( border );
    //
    this._topRight = null;
    this._chevron = null;
    this._chevronMenu = null;
    // Minimize/maximize buttons, initially non-existing
    this._minMaxState = "normal";  // valid states: min, max, normal
    this._maxButton = null;
    this._minButton = null;
    // Construct highlight border lines
    var highlightBorder = new qx.renderer.border.Border();
    highlightBorder.setLeft( 2, "solid", black );
    this._highlightLeft = new qx.ui.basic.Atom();
    this._highlightLeft.setBorder( highlightBorder );
    this._highlightLeft.setWidth( 2 );
    this.add( this._highlightLeft );
    highlightBorder = new qx.renderer.border.Border();
    highlightBorder.setRight( 2, "solid", black );
    this._highlightRight = new qx.ui.basic.Atom();
    this._highlightRight.setBorder( highlightBorder );
    this._highlightRight.setWidth( 2 );
    this.add( this._highlightRight );
    highlightBorder = new qx.renderer.border.Border();
    highlightBorder.setTop( 2, "solid", black );
    this._highlightTop = new qx.ui.basic.Atom();
    this._highlightTop.setBorder( highlightBorder );
    this._highlightTop.setLeft( 0 );
    this._highlightTop.setHeight( 2 );
    this.add( this._highlightTop );
    highlightBorder = new qx.renderer.border.Border();
    highlightBorder.setBottom( 2, "solid", black );
    this._highlightBottom = new qx.ui.basic.Atom();
    this._highlightBottom.setBorder( highlightBorder );
    this._highlightBottom.setLeft( 0 );
    this._highlightBottom.setHeight( 2 );
    this.add( this._highlightBottom );
    // Create horizontal line that separates the button bar from the rest of 
    // the client area
    border = new qx.renderer.border.Border();
    border.setTop( 1, "solid", black );
    this._separator = new qx.ui.basic.Atom();
    this._separator.setBorder( border );
    this._separator.setLeft( 0 );
    this._separator.setTop( this._tabHeight );
    this._separator.setHeight( 1 );
    this.add( this._separator );
    // Add resize listeners to update selection border (this._highlightXXX)
    this.addEventListener( "changeWidth", this._onChangeWidth, this );
    this.addEventListener( "changeHeight", this._onChangeHeight, this );
  }
);

org.eclipse.rap.rwt.custom.CTabFolder.BUTTON_SIZE = 18;

qx.Proto.setTabHeight = function( tabHeight ) {
  this._tabHeight = tabHeight;
  this._separator.setTop( this._tabHeight );
  this._highlightTop.setTop( this._separator.getTop() + this._separator.getHeight() );
  if( this._minButton != null ) {
    this._minButton.setTop( this._getButtonTop() );
  }
  if( this._maxButton != null ) {
    this._maxButton.setTop( this._getButtonTop() );
  }
  if( this._chevron != null ) {
    this._chevron.setTop( this._getButtonTop() );
  }
}

// TODO [rh] optimize usage of border objects (get, change, set)
qx.Proto.setSelectionBackground = function( color ) {
  var borderColor = new qx.renderer.color.Color( color );
  
  var highlightBorder = new qx.renderer.border.Border();
  highlightBorder.setLeft( 2, "solid", borderColor );
  this._highlightLeft.setBorder( highlightBorder );

  highlightBorder = new qx.renderer.border.Border();
  highlightBorder.setRight( 2, "solid", borderColor );
  this._highlightRight.setBorder( highlightBorder );

  highlightBorder = new qx.renderer.border.Border();
  highlightBorder.setTop( 2, "solid", borderColor );
  this._highlightTop.setBorder( highlightBorder );

  highlightBorder = new qx.renderer.border.Border();
  highlightBorder.setBottom( 2, "solid", borderColor );
  this._highlightBottom.setBorder( highlightBorder );
}

qx.Proto._getButtonTop = function() {
  return   ( this._tabHeight / 2 )
         - ( org.eclipse.rap.rwt.custom.CTabFolder.BUTTON_SIZE / 2 );
}

qx.Proto.showChevron = function( left ) {
  if( this._chevron == null ) {
    // Create chevron button
    this._chevron = new qx.ui.toolbar.Button();
    this._chevron.addState( "rwt_FLAT" );
    this._chevron.setTop( this._getButtonTop() );
    this._chevron.setHeight( org.eclipse.rap.rwt.custom.CTabFolder.BUTTON_SIZE );
    this._chevron.setWidth( org.eclipse.rap.rwt.custom.CTabFolder.BUTTON_SIZE );
    this._chevron.addEventListener( "execute", this._onChevronExecute, this );
    this._chevron.setIcon( "org/eclipse/rap/rwt/custom/ctabfolder/chevron.gif" );
    this.add( this._chevron );
  }
  this._chevron.setLeft( left );
}

qx.Proto.hideChevron = function() {
  if( this._chevron != null ) {
    var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
    wm.setToolTip( this._chevron, null );
    this._chevron.removeEventListener( "execute", this._onChevronExecute, this );
    this.remove( this._chevron );
    this._chevron.dispose();
    this._chevron = null;
  }
}

qx.Proto.setChevronToolTip = function( text ) {
  if( this._chevron != null ) {  
    var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
    wm.setToolTip( this._chevron, text );
  }
}

qx.Proto.setMinMaxState = function( state ) {
  this._minMaxState = state;
  var minIcon = "";
  var maxIcon = "";
  switch( state ) {
    case "min":
      minIcon = "org/eclipse/rap/rwt/custom/ctabfolder/restore.gif";
      maxIcon = "org/eclipse/rap/rwt/custom/ctabfolder/maximize.gif";
    break;
    case "max":
      minIcon = "org/eclipse/rap/rwt/custom/ctabfolder/minimize.gif";
      maxIcon = "org/eclipse/rap/rwt/custom/ctabfolder/restore.gif";
    break;
    case "normal":
      minIcon = "org/eclipse/rap/rwt/custom/ctabfolder/minimize.gif";
      maxIcon = "org/eclipse/rap/rwt/custom/ctabfolder/maximize.gif";
    break;
  }
  if( this._minButton != null ) {
    this._minButton.setIcon ( minIcon );
  }
  if( this._maxButton != null ) {
    this._maxButton.setIcon ( maxIcon );
  }
}

qx.Proto.showMaxButton = function( left, toolTipText ) {
  if( this._maxButton == null ) {
    this._maxButton = new qx.ui.toolbar.Button();
    this._maxButton.addState( "rwt_FLAT" );
    this._maxButton.setVerticalChildrenAlign( "middle" );
    this._maxButton.setHorizontalChildrenAlign( "center" );
    this.setMinMaxState( this._minMaxState ); // initializes the icon according to current state
    this._maxButton.setTop( this._getButtonTop() );
    this._maxButton.setHeight( org.eclipse.rap.rwt.custom.CTabFolder.BUTTON_SIZE );
    this._maxButton.setWidth( org.eclipse.rap.rwt.custom.CTabFolder.BUTTON_SIZE );
    this._maxButton.addEventListener( "execute", this._onMinMaxExecute, this );
    this.add( this._maxButton );
    var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
    wm.setToolTip( this._maxButton, toolTipText );
  }
  this._maxButton.setLeft( left );
}

qx.Proto.hideMaxButton = function() {
  if( this._maxButton != null ) {
    var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
    wm.setToolTip( this._maxButton, null );
    this._maxButton.removeEventListener( "execute", this._onMinMaxExecute, this );
    this.remove( this._maxButton );
    this._maxButton.dispose();
    this._maxButton = null;
  }
}

qx.Proto.showMinButton = function( left, toolTipText ) {
  if( this._minButton == null ) {
    this._minButton = new qx.ui.toolbar.Button();
    this._minButton.addState( "rwt_FLAT" );
    this.setMinMaxState( this._minMaxState );  // initializes the icon according to current state
    this._minButton.setTop( this._getButtonTop() );
    this._minButton.setHeight( org.eclipse.rap.rwt.custom.CTabFolder.BUTTON_SIZE );
    this._minButton.setWidth( org.eclipse.rap.rwt.custom.CTabFolder.BUTTON_SIZE );
    this._minButton.addEventListener( "execute", this._onMinMaxExecute, this );
    this.add( this._minButton );
    var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
    wm.setToolTip( this._minButton, toolTipText );
  }
  this._minButton.setLeft( left );
}

qx.Proto.hideMinButton = function( left ) {
  if( this._minButton != null ) {
    var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
    wm.setToolTip( this._minButton, null );
    this._minButton.removeEventListener( "execute", this._onMinMaxExecute, this );
    this.remove( this._minButton );
    this._minButton.dispose();
    this._minButton = null;
  }
}

qx.Proto.setTopRight = function( topRight ) {
  this._topRight = topRight;
}

qx.Proto.setHasFolderListener = function( hasFolderListener ) {
  this._hasFolderListener = hasFolderListener;
}

qx.Proto.dispose = function() {
  if ( this.getDisposed() ) {
    return;
  }
  // use hideMin/MaxButton to dispose of toolTips
  this.hideMinButton();
  this.hideMaxButton();
  this.removeEventListener( "changeWidth", this._onChangeWidth, this );
  this.removeEventListener( "changeHeight", this._onChangeHeight, this );
  return qx.ui.layout.CanvasLayout.prototype.dispose.call( this );
}

qx.Proto._onChangeWidth = function( evt ) {
  this._separator.setWidth( this.getWidth() - 2 );
  this._highlightRight.setLeft(   this.getWidth() 
                                - 2 
                                - this._highlightRight.getWidth() );
  this._highlightTop.setWidth( this.getWidth() - 2 );
  this._highlightBottom.setWidth( this.getWidth() - 2 );
}

qx.Proto._onChangeHeight = function( evt ) {
  var top = this._separator.getTop() + this._separator.getHeight() + 2;
  var height = this.getHeight() - top - 4;
  this._highlightLeft.setTop( top );
  this._highlightLeft.setHeight( height );
  this._highlightRight.setTop( top );
  this._highlightRight.setHeight( height );
  this._highlightBottom.setHeight( this.getHeight() - 2 );
}

qx.Proto._onChevronExecute = function( evt ) {
  if( this._chevronMenu == null || !this._chevronMenu.isSeeable() ) {
    if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
      var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
      var id = wm.findIdByWidget( this );
      var req = org.eclipse.rap.rwt.Request.getInstance();
      req.addEvent( "org.eclipse.rap.rwt.events.ctabFolderShowList", id );
      req.send();
    }  
  }
}

qx.Proto._onMinMaxExecute = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var event;
    if( evt.getTarget() == this._minButton ) {
      // Minimize button was pressed
      if( this._minMaxState == "min" ) {
        this.setMinMaxState( "normal" );
        event = "org.eclipse.rap.rwt.events.ctabFolderRestored";
      } else {
        this.setMinMaxState( "min" );
        event = "org.eclipse.rap.rwt.events.ctabFolderMinimized";
      }
    } else {
      // Maximize button was pressed
      if( this._minMaxState == "normal" || this.minMaxState == "min" ) {
        this.setMinMaxState( "max" );
        event = "org.eclipse.rap.rwt.events.ctabFolderMaximized";
      } else {
        this.setMinMaxState( "normal" );
        event = "org.eclipse.rap.rwt.events.ctabFolderRestored";
      }
    }
    var id = org.eclipse.rap.rwt.WidgetManager.getInstance().findIdByWidget( this );
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addParameter( id + ".minimized", this._minMaxState == "min" );
    req.addParameter( id + ".maximized", this._minMaxState == "max" );
    if( this._hasFolderListener ) {
      req.addEvent( event, id );
      req.send();
    }
  }
}