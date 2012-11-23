/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.protocol.AdapterRegistry.add( "rwt.widgets.Browser", {

  factory : function( properties ) {
    var result = new rwt.widgets.Browser();
    rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : rwt.protocol.AdapterUtil.getControlDestructor(),

  getDestroyableChildren : rwt.protocol.AdapterUtil.getDestroyableChildrenFinder(),

  properties : rwt.protocol.AdapterUtil.extendControlProperties( [
    "url",
    "functionResult"
  ] ),

  propertyHandler : rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
    "url" : function( widget, value ) {
      widget.setSource( value );
      widget.syncSource();
    },
    "functionResult" : function( widget, value ) {
      widget.setFunctionResult( value[ 0 ], value[ 1 ], value[ 2 ] );
    }
  } ),

  listeners : rwt.protocol.AdapterUtil.extendControlListeners( [
    "Progress"
  ] ),

  listenerHandler : rwt.protocol.AdapterUtil.extendControlListenerHandler( {} ),

  methods : [
    "evaluate",
    "destroyFunctions",
    "createFunctions"
  ],

  methodHandler : {
    "evaluate" : function( widget, properties ) {
      widget.execute( properties.script );
    },
    "createFunctions" : function( widget, properties ) {
      var functions = properties.functions;
      for( var i = 0; i < functions.length; i++ ) {
        widget.createFunction( functions[ i ] );
      }
    },
    "destroyFunctions" : function( widget, properties ) {
      var functions = properties.functions;
      for( var i = 0; i < functions.length; i++ ) {
        widget.destroyFunction( functions[ i ] );
      }
    }
  }

} );
