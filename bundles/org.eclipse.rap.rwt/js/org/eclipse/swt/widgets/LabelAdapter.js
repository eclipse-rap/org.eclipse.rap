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

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.Label", {

  factory : function( properties ) {
    var result = new qx.ui.basic.Atom();
    org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    org.eclipse.rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    org.eclipse.swt.LabelUtil.initialize( result );
    org.eclipse.swt.LabelUtil.setWrap( result, properties.style.indexOf( "WRAP" ) != -1 );    
    return result;
  },

  destructor : org.eclipse.rwt.protocol.AdapterUtil.getControlDestructor(),

  properties : org.eclipse.rwt.protocol.AdapterUtil.extendControlProperties( [
    "text",
    "image",
    "alignment"
  ] ),

  propertyHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
    "text" : function( widget, value ) {
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      // Order is important here: escapeText, replace line breaks
      var text = encodingUtil.escapeText( value, true );
      text = encodingUtil.replaceNewLines( text, "<br/>" );
      text = encodingUtil.replaceWhiteSpaces( text ); // fixes bug 192634
      org.eclipse.swt.LabelUtil.setText( widget, text );
    },
    "image" : function( widget, value ) {
      if( value === null ) {
        org.eclipse.swt.LabelUtil.setImage( widget, null );
      } else {
        org.eclipse.swt.LabelUtil.setImage( widget, value[ 0 ] );
      }
    },
    "alignment" : function( widget, value ) {
      org.eclipse.swt.LabelUtil.setAlignment( widget, value );
    }
  } ),

  listeners : org.eclipse.rwt.protocol.AdapterUtil.extendControlListeners( [] ),

  listenerHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlListenerHandler( {} ),

  methods : []

} );