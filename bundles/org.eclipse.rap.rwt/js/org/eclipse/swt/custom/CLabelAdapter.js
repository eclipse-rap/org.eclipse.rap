/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.CLabel", {

  factory : function( properties ) {
    var result = new qx.ui.basic.Atom();
    org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    org.eclipse.rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    org.eclipse.swt.CLabelUtil.initialize( result );
    return result;
  },

  destructor : org.eclipse.rwt.protocol.AdapterUtil.getControlDestructor(),

  properties : org.eclipse.rwt.protocol.AdapterUtil.extendControlProperties( [
    "text",
    "image",
    "alignment",
    "leftMargin",
    "topMargin",
    "rightMargin",
    "bottomMargin",
    "backgroundGradient"
  ] ),

  propertyHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
    "text" : function( widget, value ) {
      var EncodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      // Order is important here: escapeText, replace line breaks
      var text = EncodingUtil.escapeText( value, true );
      text = EncodingUtil.replaceNewLines( text, "<br/>" );
      text = EncodingUtil.replaceWhiteSpaces( text ); // fixes bug 192634
      widget.setLabel( text );
    },
    "image" : function( widget, value ) {
      if( value === null ) {
        widget.setIcon( null );
      } else {
        widget.setIcon( value[ 0 ] );
      }
    },
    "alignment" : function( widget, value ) {
      org.eclipse.swt.LabelUtil.setAlignment( widget, value );
    },
    "leftMargin" : function( widget, value ) {
      widget.setPaddingLeft( value );
    },
    "topMargin" : function( widget, value ) {
      widget.setPaddingTop( value );
    },
    "rightMargin" : function( widget, value ) {
      widget.setPaddingRight( value );
    },
    "bottomMargin" : function( widget, value ) {
      widget.setPaddingBottom( value );
    },
    "backgroundGradient" : org.eclipse.rwt.protocol.AdapterUtil.getBackgroundGradientHandler()
  } ),

  listeners : org.eclipse.rwt.protocol.AdapterUtil.extendControlListeners( [] ),

  listenerHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlListenerHandler( {} ),

  methods : []

} );