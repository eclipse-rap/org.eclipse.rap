/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
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

/*jshint nonew:false */
(function() {
  'use strict';

  rap.registerTypeHandler( "rwt.widgets.DropDown", {

    factory : function( properties ) {
      var control = rwt.remote.ObjectRegistry.getObject( properties.parent );
      var dropdown = new rwt.widgets.DropDown( {
        parent: control,
        markupEnabled: properties.markupEnabled,
        appearance: "dropdown",
        hScroll: properties.style.indexOf("H_SCROLL") !== -1
      } );
      new rwt.widgets.util.DropDownSynchronizer( dropdown );
      return dropdown;
    },

    properties : [ "items", "visible", "visibleItemCount", "columns", "data", "selectionIndex" ],

    events : [ "Selection", "DefaultSelection" ],

    methods : [ "addListener", "removeListener" ],

    methodHandler: {
      "addListener": function( widget, properties ) {
        rwt.remote.HandlerUtil.callWithTarget( properties.listenerId, function( targetFunction ) {
          widget.addListener( properties.eventType, targetFunction );
        } );
      },
      "removeListener": function( widget, properties ) {
        rwt.remote.HandlerUtil.callWithTarget( properties.listenerId, function( targetFunction ) {
          widget.removeListener( properties.eventType, targetFunction );
        } );
      }
    },

    destructor : "destroy"

  } );

}() );
