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

rwt.remote.HandlerRegistry.add( "rwt.widgets.MenuItem", {

  factory : function( properties ) {
    var result;
    rwt.remote.HandlerUtil.callWithTarget( properties.parent, function( menu ) {
      var styleMap = rwt.remote.HandlerUtil.createStyleMap( properties.style );
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
        result = new rwt.widgets.MenuItemSeparator();
      } else {
        result = new rwt.widgets.MenuItem( menuItemType );
        result.setNoRadioGroup( menu.hasState( "rwt_NO_RADIO_GROUP" ) );
      }
      menu.addMenuItemAt( result, properties.index );
      rwt.remote.HandlerUtil.addDestroyableChild( menu, result );
      result.setUserData( "protocolParent", menu );
    } );
    rwt.remote.HandlerUtil.addStatesForStyles( result, properties.style );
    return result;
  },

  destructor : rwt.remote.HandlerUtil.getWidgetDestructor(),

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
        rwt.remote.HandlerUtil.callWithTarget( value, function( menu ) {
          widget.setMenu( menu );
        } );
      }
    },
    "text" : function( widget, value ) {
      if( !widget.hasState( "rwt_SEPARATOR" ) ) {
        var EncodingUtil = rwt.util.Encoding;
        var text = value;
        // Strip accelerator text
        var index = text.indexOf( "\t" );
        if( index != -1 ) {
          text = text.substring( 0, index );
        }
        text = EncodingUtil.escapeText( text, true );
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
    "Selection",
    "Help"
  ],

  listenerHandler : {
    "Selection" : function( widget, value ) {
      if( !widget.hasState( "rwt_SEPARATOR" ) ) {
        widget.setHasSelectionListener( value );
      }
    },
    "Help" : rwt.remote.HandlerUtil.getControlListenerHandler( "Help" )
  }

} );
