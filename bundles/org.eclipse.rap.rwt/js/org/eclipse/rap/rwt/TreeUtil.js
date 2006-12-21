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

/**
 * This class contains static functions for trees and treeItems.
 */
qx.OO.defineClass( "org.eclipse.rap.rwt.TreeUtil" );

/**
 * Create a tree. The tree is assigned the given 'id'. The parent widget is
 * denoted by 'parent'.
 */
org.eclipse.rap.rwt.TreeUtil.createTree = function( id, parent ) {
  // TODO [rh] check whether this is proper usage of TreeRowStructure
  var trs = qx.ui.treefullcontrol.TreeRowStructure.getInstance().standard( "" );
  var tree = new qx.ui.treefullcontrol.Tree( trs );
  tree.setOverflow( "auto" );
  tree.setHideNode( true );
  org.eclipse.rap.rwt.WidgetManager.getInstance().add( tree, id );
  tree.setParent( parent );
  // TODO:[fappel] remove this after introduction of RWT color objects...
  tree.setBackgroundColor( new qx.renderer.color.Color( [255, 255, 255] ) );
};

/**
 * Creates a new tree item in the given 'tree'. The new tree item is assigned
 * the given 'id '. 'parentItem' is the direct parent tree item of the item
 * to be created or null for a root tree item.
 */
org.eclipse.rap.rwt.TreeUtil.createTreeItem = function( tree, id, parentItem ) {
  // TODO [rh] check whether this is proper usage of TreeRowStructure
  var row = qx.ui.treefullcontrol.TreeRowStructure.getInstance();
  // TODO [rh] remove this after TreeItem images are working properly
  var trs = row.standard( "", "resource/icon/nuvola/16/folder.png" );
  var treeItem = new qx.ui.treefullcontrol.TreeFolder( trs );
  org.eclipse.rap.rwt.WidgetManager.getInstance().add( treeItem, id );
  if( parentItem == null ) {
    tree.add( treeItem );
  } else {
    parentItem.add( treeItem );
  }
};

/**
 * Adds the _onWidgetSelected listener (defined in same class) to the given tree.
 */
org.eclipse.rap.rwt.TreeUtil.addSelectionListener = function( tree ) {
  var manager = tree.getManager();
  manager.addEventListener( "changeSelection",
                            org.eclipse.rap.rwt.TreeUtil._onWidgetSelected );
}

/**
 * Removes the _onWidgetSelected listener (defined in same class) from the given 
 * tree.
 */
org.eclipse.rap.rwt.TreeUtil.removeSelectionListener = function( tree ) {
  var manager = tree.getManager();
  manager.removeEventListener( "changeSelection", 
                               org.eclipse.rap.rwt.TreeUtil._onWidgetSelected );
}

org.eclipse.rap.rwt.TreeUtil.addTreeListener = function( tree ) {
  tree.setUserData( "fireTreeEvents", true );
}

org.eclipse.rap.rwt.TreeUtil.removeTreeListener = function( tree ) {
  tree.setUserData( "fireTreeEvents", false );
}

/**
 * Fires a widgetSelected event if the tree item wasn't already selected.
 */
org.eclipse.rap.rwt.TreeUtil._onWidgetSelected = function( evt ) {
  // target is instance of qx.manager.selection.TreeFullControlSelectionManager
  var selectionManager = evt.getTarget();
  var treeItem = selectionManager.getSelectedItem();
  var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var treeItemId = widgetManager.findIdByWidget( treeItem );
  org.eclipse.rap.rwt.EventUtil.doWidgetSelected( treeItemId, 0, 0, 0, 0 );
};

org.eclipse.rap.rwt.TreeUtil.itemExpanded = function( evt ) {
  var req = org.eclipse.rap.rwt.Request.getInstance();
  var treeItem = evt.getData();
  var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var treeItemId = widgetManager.findIdByWidget( treeItem );
  req.addParameter( treeItemId + ".state", "expanded" );
}

org.eclipse.rap.rwt.TreeUtil.itemExpandedAction = function( evt ) {
  var req = org.eclipse.rap.rwt.Request.getInstance();
  var treeItem = evt.getData();
  var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var treeItemId = widgetManager.findIdByWidget( treeItem );
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    req.addParameter( "org.eclipse.rap.rwt.events.treeExpanded", treeItemId );
    req.send();
  }
}

org.eclipse.rap.rwt.TreeUtil.itemCollapsed = function( evt ) {
  var req = org.eclipse.rap.rwt.Request.getInstance();
  var treeItem = evt.getData();
  var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var treeItemId = widgetManager.findIdByWidget( treeItem );
  req.addParameter( treeItemId + ".state", "collapsed" );
}

org.eclipse.rap.rwt.TreeUtil.itemCollapsedAction = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var treeItem = evt.getData();
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var treeItemId = widgetManager.findIdByWidget( treeItem );
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addParameter( "org.eclipse.rap.rwt.events.treeCollapsed", treeItemId );
    req.send();
  }
}
