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

rwt.protocol.AdapterRegistry.add( "rwt.widgets.ExpandItem", {

  factory : function( properties ) {
    var result;
    rwt.protocol.AdapterUtil.callWithTarget( properties.parent, function( parent ) {
      result = new rwt.widgets.ExpandItem( parent );
      parent.addWidget( result );
      rwt.protocol.AdapterUtil.addDestroyableChild( parent, result );
      result.setUserData( "protocolParent", parent );
    } );
    return result;
  },

  destructor : rwt.protocol.AdapterUtil.getWidgetDestructor(),

  properties : [
    "customVariant",
    "bounds",
    "text",
    "image",
    "expanded",
    "headerHeight"
  ],

  propertyHandler : {
    "bounds" : function( widget, value ) {
      widget.setLeft( value[ 0 ] );
      widget.setTop( value[ 1 ] );
      widget.setWidth( value[ 2 ] );
      widget.setHeight( value[ 3 ] );
    },
    "text" : function( widget, value ) {
      var EncodingUtil = rwt.protocol.EncodingUtil;
      var text = EncodingUtil.escapeText( value, false );
      widget.setText( text );
    },
    "image" : function( widget, value ) {
      if( value === null ) {
        widget.setImage( null );
      } else {
        widget.setImage( value[ 0 ] );
      }
    }
  }

} );
