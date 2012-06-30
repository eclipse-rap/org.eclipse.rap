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

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.Button", {

  factory : function( properties ) {
    var styleMap = org.eclipse.rwt.protocol.AdapterUtil.createStyleMap( properties.style );
    var buttonType = "push";
    if( styleMap.CHECK ) {
      buttonType = "check";
    } else if( styleMap.TOGGLE ) {
      buttonType = "toggle";
    } else if( styleMap.RADIO ) {
      buttonType = "radio";
    } else if( styleMap.ARROW ) {
      buttonType = "arrow";
    }
    var result = new org.eclipse.rwt.widgets.Button( buttonType );
    result.setWrap( styleMap.WRAP );
    org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    org.eclipse.rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    org.eclipse.rwt.protocol.AdapterUtil.callWithTarget( properties.parent, function( parent ) {
      result.setNoRadioGroup( parent.hasState( "rwt_NO_RADIO_GROUP" ) );
    } );
    return result;
  },

  destructor : org.eclipse.rwt.protocol.AdapterUtil.getControlDestructor(),

  properties : org.eclipse.rwt.protocol.AdapterUtil.extendControlProperties( [
    "text",
    "alignment",
    "image",
    "selection",
    "grayed"
  ] ),

  propertyHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
    "text" : function( widget, value ) {
      var EncodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      var text = EncodingUtil.escapeText( value, true );
      if( widget.hasState( "rwt_WRAP" ) ) {
        text = EncodingUtil.replaceNewLines( text, "<br/>" );
      }
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

  listeners : org.eclipse.rwt.protocol.AdapterUtil.extendControlListeners( [
    "selection"
  ] ),

  listenerHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlListenerHandler( {} ),

  methods : []

} );
