/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.remote.HandlerRegistry.add( "rwt.widgets.Button", {

  factory : function( properties ) {
    var styleMap = rwt.remote.HandlerUtil.createStyleMap( properties.style );
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
    rwt.remote.HandlerUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    rwt.remote.HandlerUtil.setParent( result, properties.parent );
    rwt.remote.HandlerUtil.callWithTarget( properties.parent, function( parent ) {
      result.setNoRadioGroup( parent.hasState( "rwt_NO_RADIO_GROUP" ) );
    } );
    return result;
  },

  destructor : rwt.remote.HandlerUtil.getControlDestructor(),

  getDestroyableChildren : rwt.remote.HandlerUtil.getDestroyableChildrenFinder(),

  properties : rwt.remote.HandlerUtil.extendControlProperties( [
    "text",
    "mnemonicIndex",
    "alignment",
    "image",
    "selection",
    "grayed"
  ] ),

  propertyHandler : rwt.remote.HandlerUtil.extendControlPropertyHandler( {
    "image" : function( widget, value ) {
      if( value === null ) {
        widget.setImage( value );
      } else {
        widget.setImage.apply( widget, value );
      }
    }
  } ),

  listeners : rwt.remote.HandlerUtil.extendControlListeners( [
    "Selection"
  ] ),

  listenerHandler : rwt.remote.HandlerUtil.extendControlListenerHandler( {} )

} );
