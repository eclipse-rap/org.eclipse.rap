/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.remote.HandlerRegistry.add( "rwt.widgets.Canvas", {

  factory : function( properties ) {
    var result = new rwt.widgets.Composite();
    rwt.remote.HandlerUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    rwt.remote.HandlerUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : rwt.remote.HandlerUtil.getControlDestructor(),

  getDestroyableChildren : rwt.remote.HandlerUtil.getDestroyableChildrenFinder(),

  properties : rwt.remote.HandlerUtil.extendControlProperties( [
    "backgroundGradient",
    "roundedBorder",
    "clientArea"
  ] ),

  propertyHandler : rwt.remote.HandlerUtil.extendControlPropertyHandler( {
    "backgroundGradient" : rwt.remote.HandlerUtil.getBackgroundGradientHandler(),
    "roundedBorder" : rwt.remote.HandlerUtil.getRoundedBorderHandler()
  } ),

  listeners : rwt.remote.HandlerUtil.extendControlListeners( [] ),

  listenerHandler : rwt.remote.HandlerUtil.extendControlListenerHandler( {} ),

  methods: [ "addListener", "removeListener" ],

  methodHandler: {
    "addListener": function( widget, properties ) {
      rwt.remote.HandlerUtil.callWithTarget( properties.listenerId, function( targetFunction ) {
        rwt.scripting.EventBinding.addListener( widget, properties.eventType, targetFunction );
      } );
    },
    "removeListener": function( widget, properties ) {
      rwt.remote.HandlerUtil.callWithTarget( properties.listenerId, function( targetFunction ) {
        rwt.scripting.EventBinding.removeListener( widget, properties.eventType, targetFunction );
      } );
    }
  },

  scriptingMethods : rwt.remote.HandlerUtil.extendControlScriptingMethods( {
    "redraw" : function() {
      var gc = rwt.widgets.util.WidgetUtil.getGC( this );
      var width = this.getInnerWidth();
      var height = this.getInnerHeight();
      var fillStyle = this.getBackgroundColor();
      var strokeStyle = this.getTextColor();
      var font = [[]];
      if( this.getFont() ) {
        font[ 0 ] = this.getFont().getFamily();
        font[ 1 ] = this.getFont().getSize();
        font[ 2 ] = this.getFont().getBold();
        font[ 3 ] = this.getFont().getItalic();
      }
      gc.init(
        width,
        height,
        font,
        rwt.util.Colors.stringToRgb( fillStyle ? fillStyle : "#000000" ),
        rwt.util.Colors.stringToRgb( strokeStyle ? strokeStyle : "#000000" )
      );
    }
  } )


} );
