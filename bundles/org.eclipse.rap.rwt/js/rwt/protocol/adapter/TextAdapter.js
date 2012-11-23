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

rwt.protocol.AdapterRegistry.add( "rwt.widgets.Text", {

  factory : function( properties ) {
    var styleMap = rwt.protocol.AdapterUtil.createStyleMap( properties.style );
    var result = new rwt.widgets.Text( styleMap.MULTI );
    rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    if( styleMap.RIGHT ) {
      result.setTextAlign( "right" );
    } else if( styleMap.CENTER ) {
      result.setTextAlign( "center" );
    }
    result.setWrap( styleMap.WRAP !== undefined );
    return result;
  },

  destructor : rwt.protocol.AdapterUtil.getControlDestructor(),

  getDestroyableChildren : rwt.protocol.AdapterUtil.getDestroyableChildrenFinder(),

  properties : rwt.protocol.AdapterUtil.extendControlProperties( [
    "text",
    "message",
    "echoChar",
    "editable",
    "selection",
    "textLimit"
  ] ),

  propertyHandler : rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
    "text" : function( widget, value ) {
      var EncodingUtil = rwt.protocol.EncodingUtil;
      var text = EncodingUtil.truncateAtZero( value );
      if( !widget.hasState( "rwt_MULTI" ) ) {
        text = EncodingUtil.replaceNewLines( text, " " );
      }
      widget.setValue( text );
    },
    "echoChar" : function( widget, value ) {
      if( !widget.hasState( "rwt_MULTI" ) ) {
        widget.setPasswordMode( value !== null );
      }
    },
    "editable" : function( widget, value ) {
      widget.setReadOnly( !value );
    },
    "textLimit" : function( widget, value ) {
      widget.setMaxLength( value );
    }
  } ),

  listeners : rwt.protocol.AdapterUtil.extendControlListeners( [
    "DefaultSelection",
    "Modify"
  ] ),

  listenerHandler : rwt.protocol.AdapterUtil.extendControlListenerHandler( {} )

} );
