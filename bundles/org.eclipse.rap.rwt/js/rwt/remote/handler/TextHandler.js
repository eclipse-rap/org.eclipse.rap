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

rwt.remote.HandlerRegistry.add( "rwt.widgets.Text", {

  factory : function( properties ) {
    var styleMap = rwt.remote.HandlerUtil.createStyleMap( properties.style );
    var result = new rwt.widgets.Text( styleMap.MULTI );
    rwt.remote.HandlerUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    rwt.remote.HandlerUtil.setParent( result, properties.parent );
    if( styleMap.RIGHT ) {
      result.setTextAlign( "right" );
    } else if( styleMap.CENTER ) {
      result.setTextAlign( "center" );
    }
    result.setWrap( styleMap.WRAP !== undefined );
    return result;
  },

  destructor : rwt.remote.HandlerUtil.getControlDestructor(),

  getDestroyableChildren : rwt.remote.HandlerUtil.getDestroyableChildrenFinder(),

  properties : rwt.remote.HandlerUtil.extendControlProperties( [
    "text",
    "message",
    "echoChar",
    "editable",
    "selection",
    "textLimit"
  ] ),

  propertyHandler : rwt.remote.HandlerUtil.extendControlPropertyHandler( {
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

  listeners : rwt.remote.HandlerUtil.extendControlListeners( [
    "DefaultSelection",
    "Modify"
  ] ),

  listenerHandler : rwt.remote.HandlerUtil.extendControlListenerHandler( {} )

} );
