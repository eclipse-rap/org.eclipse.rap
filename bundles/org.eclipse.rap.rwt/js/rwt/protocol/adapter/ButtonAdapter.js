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

rwt.protocol.AdapterRegistry.add( "rwt.widgets.Button", {

  factory : function( properties ) {
    var styleMap = rwt.protocol.AdapterUtil.createStyleMap( properties.style );
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
    var result = new rwt.widgets.Button( buttonType );
    result.setWrap( styleMap.WRAP );
    rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    rwt.protocol.AdapterUtil.callWithTarget( properties.parent, function( parent ) {
      result.setNoRadioGroup( parent.hasState( "rwt_NO_RADIO_GROUP" ) );
    } );
    return result;
  },

  destructor : rwt.protocol.AdapterUtil.getControlDestructor(),

  getDestroyableChildren : rwt.protocol.AdapterUtil.getDestroyableChildrenFinder(),

  properties : rwt.protocol.AdapterUtil.extendControlProperties( [
    "text",
    "alignment",
    "image",
    "selection",
    "grayed"
  ] ),

  propertyHandler : rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
    "text" : function( widget, value ) {
      var EncodingUtil = rwt.protocol.EncodingUtil;
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

  listeners : rwt.protocol.AdapterUtil.extendControlListeners( [
    "Selection"
  ] ),

  listenerHandler : rwt.protocol.AdapterUtil.extendControlListenerHandler( {} )

} );
