/*******************************************************************************
 * Copyright (c) 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - ongoing development
 ******************************************************************************/

namespace( "org.eclipse.rwt" );

org.eclipse.rwt.TreeUtil = {
    
  /////////////////
  // API for Server
  
  setFixedColumns : function( tree, value ) {
    var container = tree.getRowContainer();
    if( container.setFixedColumns ) {
      container.setFixedColumns( value );
      tree.update();
    }
  },
  
  ///////////////
  // API for Tree

  createTreeRowContainer : function( argsmap ) {
    var result;
    if( argsmap.splitContainer ) {
      result = org.eclipse.rwt.TreeRowContainerWrapper.createInstance();
    } else {
      result = new org.eclipse.rwt.widgets.TreeRowContainer();
    }
    return result;
  },
  
  getColumnByPageX : function( tree, pageX ) {
    var container = tree.getRowContainer();
    var splitContainer = container instanceof org.eclipse.rwt.TreeRowContainerWrapper;
    if( splitContainer ) {
      container = tree.getRowContainer().getSubContainer( 0 );
    }
    var result = this._getColumnByPageX( container, pageX );
    if( result === -1 && splitContainer ) {
      container = tree.getRowContainer().getSubContainer( 1 );
      result = this._getColumnByPageX( container, pageX );
    }
    return result;
  },
  
  ////////////
  // Internals
  
  _getColumnByPageX : function( container, pageX ) {
    var config = container.getRenderConfig();
    var columnCount = config.columnCount;
    var columnIndex = columnCount == 0 ? 0 : -1;
    var element = container.getFirstChild().getElement();
    var leftOffset = qx.bom.element.Location.getLeft( element );
    for( var i = 0; columnIndex == -1 && i < columnCount; i++ ) {
      var pageLeft = leftOffset + config.itemLeft[ i ];
      if( pageX >= pageLeft && pageX < pageLeft + config.itemWidth[ i ] ) {
        columnIndex = i;
      }
    }
    return columnIndex;
  }
  
};
