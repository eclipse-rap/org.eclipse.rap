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

rwt.protocol.AdapterRegistry.add( "rwt.widgets.FileUpload", {

  factory : function( properties ) {
    var result = new rwt.widgets.FileUpload();
    rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : rwt.protocol.AdapterUtil.getControlDestructor(),

  getDestroyableChildren : rwt.protocol.AdapterUtil.getDestroyableChildrenFinder(),

  properties : rwt.protocol.AdapterUtil.extendControlProperties( [
    "text",
    "image"
  ] ),

  propertyHandler : rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
    "text" : function( widget, value ) {
      var EncodingUtil = rwt.protocol.EncodingUtil;
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

  listeners : rwt.protocol.AdapterUtil.extendControlListeners( [] ),

  listenerHandler : rwt.protocol.AdapterUtil.extendControlListenerHandler( {} ),

  methods : [
    "submit"
  ],

  methodHandler : {
    "submit" : function( widget, args ) {
      widget.submit( args.url );
    }
  }

} );
