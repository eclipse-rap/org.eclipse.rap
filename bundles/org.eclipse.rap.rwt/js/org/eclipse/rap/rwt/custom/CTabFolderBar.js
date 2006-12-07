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
  "org.eclipse.rap.rwt.custom.CTabFolderBar", 
  qx.ui.pageview.tabview.TabViewBar,
  function() {
    qx.ui.pageview.tabview.TabViewBar.call( this );
    this._maxVisible = true;
    this._minVisible = true;
    this._topRightArea = null;
    this._minMaxToolBar = null;
    this._minButton = null;
    this._maxButton = null;
    this._topRight = null;
    this._initTopRightArea();
  }
);

qx.Proto.setMaximizeVisible = function( value ) {
  if( this._maxVisible != value ) {
    if( value ) {
      this._minMaxToolBar.add( this._maxButton );
      this._maxButton.moveSelfToEnd();
    } else {
      this._minMaxToolBar.remove( this._maxButton );
    }
    this._maxVisible = value;
  }
};

qx.Proto.isMaximizeVisible = function() {
  return this._maxVisible;
}

qx.Proto.setMinimizeVisible = function( value ) {
  if( this._minVisible != value ) {
    if( value ) {
      this._minMaxToolBar.add( this._minButton );
      this._minButton.moveSelfToBegin();
    } else {
      this._minMaxToolBar.remove( this._minButton );
    }
    this._minVisible = value;
  }
};

qx.Proto.isMinimizeVisible = function() {
  return this._minVisible;
}

qx.Proto.setMaximized = function( value ) {
  if( value ) {
    this._maxButton.setIcon( "org/eclipse/rap/rwt/custom/ctabfolder/restore.gif" );
  } else {
    this._maxButton.setIcon( "org/eclipse/rap/rwt/custom/ctabfolder/maximize.gif" );
  }
}

qx.Proto.setMinimized = function( value ) {
  if( value ) {
    this._minButton.setIcon( "org/eclipse/rap/rwt/custom/ctabfolder/restore.gif" );
  } else {
    this._minButton.setIcon( "org/eclipse/rap/rwt/custom/ctabfolder/minimize.gif" );
  }
}

qx.Proto.setTopRight = function( value ) {
this.debug( "value: " + value );     
  if( value == null ) {
    if( this._topRight != null ) {
      this._topRightArea.remove( this._topRight );
    }
  } else {
    this._topRightArea.add( value );
    value.moveSelfToBegin();
  }
  this._topRight = value;
}

qx.Proto.getTopRight = function() {
  return this._topRight;  
}

qx.Proto._initTopRightArea = function() {
  this._topRightArea = new qx.ui.layout.BoxLayout();
  this._topRightArea.setWidth( "1*" );
  this._topRightArea.setHorizontalChildrenAlign( "right" );
  this._topRightArea.setVerticalChildrenAlign( "middle" );
  this._topRightArea.setAllowStretchY( true );
  this._topRightArea.setBackgroundColor( new qx.renderer.color.Color( "orange" ) );
  this.add( this._topRightArea );

  this._minMaxToolBar = new qx.ui.toolbar.ToolBar();
  this._minMaxToolBar.setWidth( "auto" );
  this._minMaxToolBar.setHeight( "auto" );
  this._topRightArea.add( this._minMaxToolBar );
  
  this._minButton = new qx.ui.toolbar.ToolBarButton();
  this.setMinimized( false );
  this._minButton.addEventListener( "execute", this._onMinButtonExecute, this );
  this._minMaxToolBar.add( this._minButton );

  this._maxButton = new qx.ui.toolbar.ToolBarButton();
  this.setMaximized( false );
  this._maxButton.addEventListener( "execute", this._onMaxButtonExecute, this );
  this._minMaxToolBar.add( this._maxButton );
}  

qx.Proto._layoutTopRightArea = function() {
  // make sure that _topRightArea is the rightmost widget in this bar
  if( !this._topRightArea.isLastChild() ) {
    this._topRightArea.moveSelfToEnd();
  }
}

qx.Proto.registerTopRightArea = function() {
  var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var parentId = widgetManager.findIdByWidget( this.getParent() );
  widgetManager.add( this._topRightArea, parentId + "topRight" );
}

// This detour is necessary since 'this.getParent()' is null when the event 
// listeners are added (see _initTopRightArea)
qx.Proto._onMinButtonExecute = function( evt ) {
  this.getParent()._onMinButtonExecute( evt );
}

qx.Proto._onMaxButtonExecute = function( evt ) {
  this.getParent()._onMaxButtonExecute( evt );
}

qx.Proto.dispose = function() {
  if( this.getDisposed() ) {
    return true;
  }
  if( this._topRightArea != null ) {
    this._topRightArea.dispose();
  }
  if( this._minMaxToolBar != null ) {
    this._minMaxToolBar.dispose();
  }
  this._topRight = null;
  return qx.ui.pageview.tabview.TabViewBar.prototype.dispose.call( this );
}