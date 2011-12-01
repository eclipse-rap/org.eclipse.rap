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

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.MenuItem", {

  factory : function( properties ) {
    var result;
    org.eclipse.rwt.protocol.AdapterUtil.callWithTarget( properties.parent, function( menu ) {
      var styleMap = org.eclipse.rwt.protocol.AdapterUtil.createStyleMap( properties.style );
      var menuItemType = "push";
      if( menu.hasState( "rwt_BAR" ) ) {
        menuItemType = "bar";
      } else if( styleMap.CASCADE ) {
        menuItemType = "cascade";
      } else if( styleMap.CHECK ) {
        menuItemType = "check";
      } else if( styleMap.RADIO ) {
        menuItemType = "radio";
      }
      if( styleMap.SEPARATOR ) {
        result = new qx.ui.menu.Separator();
      } else {
        result = new org.eclipse.rwt.widgets.MenuItem( menuItemType );
        result.setNoRadioGroup( menu.hasState( "rwt_NO_RADIO_GROUP" ) );
      }
      menu.addMenuItemAt( result, properties.index );
    } );
    org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    return result;
  },

  destructor : org.eclipse.rwt.protocol.AdapterUtil.getWidgetDestructor(),

  properties : [
    "menu",
    "enabled",
    "text",
    "image",
    "selection",
    "customVariant"
  ],

  propertyHandler : {
    "menu" : function( widget, value ) {
      if( !widget.hasState( "rwt_SEPARATOR" ) ) {
        org.eclipse.rwt.protocol.AdapterUtil.callWithTarget( value, function( menu ) {
          widget.setMenu( menu );
        } );
      }
    },
    "text" : function( widget, value ) {
      if( !widget.hasState( "rwt_SEPARATOR" ) ) {
        var encodingUtil = org.eclipse.rwt.protocol.EncodingUtil;
        var text = value;
        // Strip accelerator text
        var index = text.indexOf( "\t" );
        if( index != -1 ) {
          text = text.substring( 0, index );
        }
        text = encodingUtil.escapeText( text, true );
        widget.setText( text );
      }
    },
    "image" : function( widget, value ) {
      if( !widget.hasState( "rwt_SEPARATOR" ) ) {
        if( value === null ) {
          widget.setImage( value );
        } else {
          widget.setImage.apply( widget, value );
        }
      }
    },
    "selection" : function( widget, value ) {
      if( !widget.hasState( "rwt_SEPARATOR" ) ) {
        widget.setSelection( value );
      }
    }
  },

  listeners : [
    "selection",
    "help"
  ],

  listenerHandler : {
    "selection" : function( widget, value ) {
      if( !widget.hasState( "rwt_SEPARATOR" ) ) {
        widget.setHasSelectionListener( value );
      }
    },
    "help" : org.eclipse.rwt.protocol.AdapterUtil.getControlListenerHandler( "help" )
  },

  methods : []
  
} );