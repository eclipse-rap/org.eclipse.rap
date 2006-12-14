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
  qx.ui.pageview.AbstractPageView,
  function() {
    qx.ui.pageview.AbstractPageView.call( this, 
                                          org.eclipse.rap.rwt.custom.CTabFolderBar, 
                                          qx.ui.pageview.tabview.TabViewPane );
    this.setTabHeight( 20 );
    this.getBar().setMinWidth( 0 );
    this.getBar().setMinHeight( 0 );
    this.getPane().setMinWidth( 0 );
    this.getPane().setMinHeight( 0 );
  }
);

///////////////////////
// Property definitions

qx.OO.changeProperty( {
  name : "appearance", 
  type : "string", 
  defaultValue : "tab-view" } );

qx.OO.addProperty( {
  name : "alignTabsToLeft", 
  type : "boolean", 
  defaultValue : true } );
  
qx.OO.addProperty( { 
  name : "placeBarOnTop", 
  type : "boolean", 
  defaultValue : true } );

qx.OO.addProperty( { 
  name : "selection", 
  type : "number", 
  defaultValue : -1 } );

/////////////////
// Tab management

qx.Proto.createTab = function( tabId, canClose ) {
  var button = new qx.ui.pageview.tabview.TabViewButton();
  button.addEventListener( "changeChecked", this._onSelectTab, this );
  // TODO [rh] as of qooxdoo 0.6 it seems that showCloseButton is only supported 
  //      in newer versions of qooxdoo. At least the online documentation knows 
  //      about it.
  /*
  if( canClose ) {
    button.setShowCloseButton( true );
    button.setCloseButtonImage( "org/eclipse/rap/rwt/custom/ctabfolder/close.gif" );
  }
  */
  var page = new qx.ui.pageview.tabview.TabViewPage( button );
  this.getBar().add( button );
  this.getPane().add( page );
  org.eclipse.rap.rwt.WidgetManager.getInstance().add( button, tabId );
  org.eclipse.rap.rwt.WidgetManager.getInstance().add( page, tabId + "pg" );
  this.getBar()._layoutTopRightArea();
}

qx.Proto.registerTopRightArea = function() {
  this.getBar().registerTopRightArea();
}

////////////////////////
// Property change hooks

qx.Proto._modifyAlignTabsToLeft = function( propValue, propOldValue, propData ) {
  var vBar = this._bar;
  vBar.setHorizontalChildrenAlign( propValue ? "left" : "right" );
  // force re-apply of states for all tabs
  vBar._addChildrenToStateQueue();
  return true;
}

qx.Proto._modifyPlaceBarOnTop = function( propValue, propOldValue, propData ) {
  // This does not work if we use flexible zones
  // this.setReverseChildrenOrder(!propValue);
  var vBar = this._bar;
  // move bar around
  if( propValue ) {
    vBar.moveSelfToBegin();
  } else {
    vBar.moveSelfToEnd();
  }
  // force re-apply of states for all tabs
  vBar._addChildrenToStateQueue();
  return true;
}

qx.Proto._modifySelection = function( propValue, propOldValue, propData ) {
  var buttons = this.getBar().getChildren();
  for( var i = 0, l = buttons.length; i < l; i++ ) {
    var btn = buttons[ i ];
    if( btn instanceof qx.ui.pageview.tabview.TabViewButton ) {
      btn.setChecked( i == propValue );
    }
  }
  this._selection = propValue;
  return true;
}

qx.Proto.setMaximizeVisible = function( value ) {
  this.getBar().setMaximizeVisible( value );
}

qx.Proto.isMaximizeVisible = function() {
  return this.getBar().isMaximizeVisible();
}

qx.Proto.setMinimizeVisible = function( value ) {
  this.getBar().setMinimizeVisible( value );
}

qx.Proto.isMinimizeVisible = function() {
  return this.getBar().isMinimizeVisible();
}

qx.Proto.setMaximized = function( value ) {
  this.getBar().setMaximized( value );
}

qx.Proto.setMinimized = function( value ) {
  this.getBar().setMinimized( value );
}

qx.Proto.setMaxBarWidth = function( value ) {
  this.getBar().setMaxWidth( value );
  // clipWidth
  this.getBar().setClipWidth( value );
}

qx.Proto.setTabHeight = function( value ) {
  this.getBar().setHeight( value );
  this.getBar().setClipHeight( value );
}

qx.Proto.setTopRight = function( value ) {
  this.getBar().setTopRight( value );
}

qx.Proto.getTopRight = function() {
  this.getBar().getTopRight();  
}


///////////////////////////
// Event handling functions

qx.Proto._onSelectTab = function( e ) {
  // find the index for the selected button
  var index = this.getBar().indexOf( e.getTarget() );
  this.setSelection( index );
}

qx.Proto._onCloseTab = function( e ) {
  var btn = e.getData();
  var pages = this.getPane().getChildren();
  var pageToClose = null;
  for( var i = 0, l = pages.length; i < l; i++ ) {
    if( pages[ i ].getButton() === btn ) {
      pageToClose = pages[ i ];
    }
  }
  if( pageToClose != null ) {
    var buttons = this.getBar().getChildren();
    var btnIndex = buttons.indexOf( btn );
      
    // Select another tab
    if( btnIndex - 1 < buttons.length - 1 ) { 
      buttons[ btnIndex - 1 ].setChecked( true );
    }
    btn.getManager().remove( btn );
    this.getBar().remove( btn );
    
    this.getPane().remove( pageToClose );
    
    btn.dispose();
    pageToClose.dispose(); 
  }
  e.stopPropagation();
}

qx.Proto._onMinButtonExecute = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var req = org.eclipse.rap.rwt.Request.getInstance();
    // get the tabFolder: 
    //   Button -> ToolBar -> topRightArea (BoxLayout) -> TabViewBar -> CTabFolder
    var tabFolder = evt.getTarget().getParent().getParent().getParent().getParent();
    var id = widgetManager.findIdByWidget( tabFolder );
    req.addParameter( id + ".minimized", "" );
    req.send();
  }
}

qx.Proto._onMaxButtonExecute = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var req = org.eclipse.rap.rwt.Request.getInstance();
    // get the tabFolder: 
    //   Button -> ToolBar -> topRightArea (BoxLayout) -> TabViewBar -> CTabFolder
    var tabFolder = evt.getTarget().getParent().getParent().getParent().getParent();
    var id = widgetManager.findIdByWidget( tabFolder );
    req.addParameter( id + ".maximized", "" );
    req.send();
  }
}

/////////////////////////////////////////////////////////////
// Static event handler function that post back to the server

org.eclipse.rap.rwt.custom.CTabFolder.tabSelected = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var req = org.eclipse.rap.rwt.Request.getInstance();
    var id = widgetManager.findIdByWidget( evt.getTarget() );
    req.addParameter( id + ".selectionIndex", evt.getData() );
  }
};

org.eclipse.rap.rwt.custom.CTabFolder.tabSelectedAction = function( evt ) {
  org.eclipse.rap.rwt.custom.CTabFolder.tabSelected( evt );
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = widgetManager.findIdByWidget( evt.getTarget() );
    org.eclipse.rap.rwt.EventUtil.doWidgetSelected( id, 0, 0, 0, 0 );
  }
};
