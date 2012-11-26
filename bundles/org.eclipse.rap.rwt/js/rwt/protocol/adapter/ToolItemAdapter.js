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

rwt.protocol.AdapterRegistry.add( "rwt.widgets.ToolItem", {

  factory : function( properties ) {
    var styleMap = rwt.protocol.AdapterUtil.createStyleMap( properties.style );
    var type = "separator";
    if( styleMap.PUSH ) {
      type = "push";
    } else if( styleMap.CHECK ) {
      type = "check";
    } else if( styleMap.RADIO ) {
      type = "radio";
    } else if( styleMap.DROP_DOWN ) {
      type = "dropDown";
    }
    var result;
    rwt.protocol.AdapterUtil.callWithTarget( properties.parent, function( toolbar ) {
      if( type === "separator" ) {
        result = new rwt.widgets.ToolItemSeparator( toolbar.hasState( "rwt_FLAT" ),
                                                            toolbar.hasState( "rwt_VERTICAL" ) );
      } else {
        result = new rwt.widgets.ToolItem( type );
        result.setNoRadioGroup( toolbar.hasState( "rwt_NO_RADIO_GROUP" ) );
      }
      toolbar.addAt( result, properties.index );
      rwt.protocol.AdapterUtil.addDestroyableChild( toolbar, result );
      result.setUserData( "protocolParent", toolbar );
    } );
    rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    return result;
  },

  destructor : rwt.protocol.AdapterUtil.getWidgetDestructor(),

  properties : [
    "bounds",
    "visible",
    "enabled",
    "customVariant",
    "toolTip",
    "text",
    "image",
    "hotImage",
    "control",
    "selection"
  ],

  propertyHandler : {
    "bounds" : rwt.protocol.AdapterUtil.getControlPropertyHandler( "bounds" ),
    "visible" : function( widget, value ) {
      widget.setVisibility( value );
    },
    "toolTip" : rwt.protocol.AdapterUtil.getControlPropertyHandler( "toolTip" ),
    "text" : function( widget, value ) {
      var EncodingUtil = rwt.protocol.EncodingUtil;
      var text = EncodingUtil.escapeText( value, true );
      widget.setText( text );
    },
    "image" : function( widget, value ) {
      if( value === null ) {
        widget.setImage( null );
      } else {
        widget.setImage( value[ 0 ], value[ 1 ], value[ 2 ] );
      }
    },
    "hotImage" : function( widget, value ) {
      if( value === null ) {
        widget.setHotImage( null );
      } else {
        widget.setHotImage( value[ 0 ], value[ 1 ], value[ 2 ] );
      }
    },
    "control" : function( widget, value ) {
      widget.setLineVisible( value === null );
    }
  },

  listeners : [
    "Selection"
  ]

} );
