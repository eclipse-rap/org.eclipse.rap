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

rwt.protocol.HandlerRegistry.add( "rwt.widgets.Text", {

  factory : function( properties ) {
    var styleMap = rwt.protocol.HandlerUtil.createStyleMap( properties.style );
    var result = new rwt.widgets.Text( styleMap.MULTI );
    rwt.protocol.HandlerUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    rwt.protocol.HandlerUtil.setParent( result, properties.parent );
    if( styleMap.RIGHT ) {
      result.setTextAlign( "right" );
    } else if( styleMap.CENTER ) {
      result.setTextAlign( "center" );
    }
    result.setWrap( styleMap.WRAP !== undefined );
    return result;
  },

  destructor : rwt.protocol.HandlerUtil.getControlDestructor(),

  getDestroyableChildren : rwt.protocol.HandlerUtil.getDestroyableChildrenFinder(),

  properties : rwt.protocol.HandlerUtil.extendControlProperties( [
    "text",
    "message",
    "echoChar",
    "editable",
    "selection",
    "textLimit"
  ] ),

  propertyHandler : rwt.protocol.HandlerUtil.extendControlPropertyHandler( {
    "text" : function( widget, value ) {
      var EncodingUtil = rwt.util.Encoding;
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

  listeners : rwt.protocol.HandlerUtil.extendControlListeners( [
    "DefaultSelection",
    "Modify"
  ] ),

  listenerHandler : rwt.protocol.HandlerUtil.extendControlListenerHandler( {} )

} );
