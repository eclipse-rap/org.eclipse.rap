/*******************************************************************************
 * Copyright (c) 2011, 2013 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/

namespace( "rwt.widgets.util" );

rwt.widgets.util.GridUtil = {

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
      result = rwt.widgets.util.GridRowContainerWrapper.createInstance();
    } else {
      result = new rwt.widgets.base.GridRowContainer();
    }
    return result;
  },

  getColumnByPageX : function( tree, pageX ) {
    var container = tree.getRowContainer();
    var splitContainer = container instanceof rwt.widgets.util.GridRowContainerWrapper;
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
    var columnIndex = columnCount === 0 ? 0 : -1;
    var element = container.getFirstChild().getElement();
    var leftOffset = rwt.html.Location.getLeft( element );
    for( var i = 0; columnIndex == -1 && i < columnCount; i++ ) {
      var pageLeft = leftOffset + config.itemLeft[ i ];
      if( pageX >= pageLeft && pageX < pageLeft + config.itemWidth[ i ] ) {
        columnIndex = i;
      }
    }
    return columnIndex;
  }

};
