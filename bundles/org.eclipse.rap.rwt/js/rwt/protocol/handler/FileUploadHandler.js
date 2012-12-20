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

rwt.protocol.HandlerRegistry.add( "rwt.widgets.FileUpload", {

  factory : function( properties ) {
    var result = new rwt.widgets.FileUpload();
    rwt.protocol.HandlerUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    rwt.protocol.HandlerUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : rwt.protocol.HandlerUtil.getControlDestructor(),

  getDestroyableChildren : rwt.protocol.HandlerUtil.getDestroyableChildrenFinder(),

  properties : rwt.protocol.HandlerUtil.extendControlProperties( [
    "text",
    "image"
  ] ),

  propertyHandler : rwt.protocol.HandlerUtil.extendControlPropertyHandler( {
    "text" : function( widget, value ) {
      var EncodingUtil = rwt.util.Encoding;
      var text = EncodingUtil.escapeText( value, true );
      widget.setText( text === "" ? null : text );
    },
    "image" : function( widget, value ) {
      if( value === null ) {
        widget.setImage( value );
      } else {
        widget.setImage.apply( widget, value );
      }
    }
  } ),

  listeners : rwt.protocol.HandlerUtil.extendControlListeners( [] ),

  listenerHandler : rwt.protocol.HandlerUtil.extendControlListenerHandler( {} ),

  methods : [
    "submit"
  ],

  methodHandler : {
    "submit" : function( widget, args ) {
      widget.submit( args.url );
    }
  }

} );
