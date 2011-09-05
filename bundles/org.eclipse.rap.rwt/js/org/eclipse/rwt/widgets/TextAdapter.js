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

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.Text", {

  factory : function( properties ) {
    var styleMap = org.eclipse.rwt.protocol.AdapterUtil.createStyleMap( properties.style );
    var result = new org.eclipse.rwt.widgets.Text( styleMap.MULTI );
    org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    org.eclipse.rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    org.eclipse.swt.TextUtil.initialize( result );
    if( styleMap.RIGHT ) {
      result.setTextAlign( "right" );
    } else if( styleMap.CENTER ) {
      result.setTextAlign( "center" );
    }
    result.setWrap( styleMap.WRAP !== undefined );
    return result;
  },

  destructor : function( widget ) {
    org.eclipse.swt.TextUtil.disposeMessageLabel( widget);
    org.eclipse.rwt.protocol.AdapterUtil.getControlDestructor()( widget );
  },

  properties : org.eclipse.rwt.protocol.AdapterUtil.extendControlProperties( [
    "text",
    "message",
    "echoChar",
    "editable",
    "selection",
    "textLimit"
  ] ),

  propertyHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
    "text" : function( widget, value ) {
      var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
      var text = encodingUtil.truncateAtZero( value );
      if( !widget.hasState( "rwt_MULTI" ) ) {
        text = encodingUtil.replaceNewLines( text, " " );
      }
      widget.setValue( text );
    },
    "message" : function( widget, value ) {
      org.eclipse.swt.TextUtil.setMessage( widget, value );
    },
    "echoChar" : function( widget, value ) {
      widget.setPasswordMode( value !== null );
    },
    "editable" : function( widget, value ) {
      widget.setReadOnly( !value );
    },
    "selection" : function( widget, value ) {
      org.eclipse.swt.TextUtil.setSelection( widget, value[ 0 ], value[ 1 ] );
    },
    "textLimit" : function( widget, value ) {
      widget.setMaxLength( value < 0 ? null : value );
    }
  } ),

  listeners : org.eclipse.rwt.protocol.AdapterUtil.extendControlListeners( [
    "selection",
    "modify",
    "verify"
  ] ),

  listenerHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlListenerHandler( {} ),

  methods : []

} );