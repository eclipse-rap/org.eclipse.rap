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

rwt.protocol.HandlerRegistry.add( "rwt.widgets.Browser", {

  factory : function( properties ) {
    var result = new rwt.widgets.Browser();
    rwt.protocol.HandlerUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    rwt.protocol.HandlerUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : rwt.protocol.HandlerUtil.getControlDestructor(),

  getDestroyableChildren : rwt.protocol.HandlerUtil.getDestroyableChildrenFinder(),

  properties : rwt.protocol.HandlerUtil.extendControlProperties( [
    "url",
    "functionResult"
  ] ),

  propertyHandler : rwt.protocol.HandlerUtil.extendControlPropertyHandler( {
    "url" : function( widget, value ) {
      widget.setSource( value );
      widget.syncSource();
    },
    "functionResult" : function( widget, value ) {
      widget.setFunctionResult( value[ 0 ], value[ 1 ], value[ 2 ] );
    }
  } ),

  listeners : rwt.protocol.HandlerUtil.extendControlListeners( [
    "Progress"
  ] ),

  listenerHandler : rwt.protocol.HandlerUtil.extendControlListenerHandler( {} ),

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
