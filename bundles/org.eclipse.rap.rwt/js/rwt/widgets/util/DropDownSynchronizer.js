/*******************************************************************************
 * Copyright (c) 2014, 2018 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

namespace( "rwt.widgets.util" );

rwt.widgets.util.DropDownSynchronizer = function( dropdown ) {
  dropdown.addListener( "Show", this._onVisibleChanged );
  dropdown.addListener( "Hide", this._onVisibleChanged );
  dropdown.addListener( "Selection", this._onSelectionChanged );
  dropdown.addListener( "DefaultSelection", this._onSelectionChanged );
};

rwt.widgets.util.DropDownSynchronizer.prototype = {

  _onVisibleChanged : function( event ) {
    if( !rwt.remote.EventUtil.getSuspended() ) {
      var dropdown = event.widget;
      var connection = rwt.remote.Connection.getInstance();
      connection.getRemoteObject( dropdown ).set( "visible", dropdown.getVisible() );
    }
  },

  _onSelectionChanged : function( event ) {
    if( !rwt.remote.EventUtil.getSuspended() ) {
      var dropdown = event.widget;
      var remoteObject = rwt.remote.Connection.getInstance().getRemoteObject( dropdown );
      remoteObject.set( "selectionIndex", dropdown.getSelectionIndex() );
      // TODO : merge multiple changes? How long?
      remoteObject.notify( event.type, { "index" : event.index } );
    }
  }

};
