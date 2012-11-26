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

rwt.protocol.AdapterRegistry.add( "rwt.widgets.TabFolder", {

  factory : function( properties ) {
    var result = new rwt.widgets.TabFolder();
    rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    result.setHideFocus( true );
    result.setPlaceBarOnTop( properties.style.indexOf( "BOTTOM" ) === -1 );
    return result;
  },

  destructor : rwt.protocol.AdapterUtil.getControlDestructor(),

  getDestroyableChildren : rwt.protocol.AdapterUtil.getDestroyableChildrenFinder(),

  properties : rwt.protocol.AdapterUtil.extendControlProperties( [
    "selection"
  ] ),

  propertyHandler : rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
    "selection" : function( widget, value ) {
      rwt.protocol.AdapterUtil.callWithTarget( value, function( item ) {
        var items = widget.getBar().getChildren();
        for( var index = 0; index < items.length; index++ ) {
          if( items[ index ] === item ) {
            items[ index ].setChecked( true );
          } else if( items[ index ].getChecked() ) {
            items[ index ].setChecked( false );
          }
        }
      } );
    }
  } ),

  listeners : rwt.protocol.AdapterUtil.extendControlListeners( [] ),

  listenerHandler : rwt.protocol.AdapterUtil.extendControlListenerHandler( {} )

} );
