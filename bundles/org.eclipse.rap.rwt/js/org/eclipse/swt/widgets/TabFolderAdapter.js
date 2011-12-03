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

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.TabFolder", {

  factory : function( properties ) {
    var result = new qx.ui.pageview.tabview.TabView();
    org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    org.eclipse.rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    result.setHideFocus( true );
    result.setPlaceBarOnTop( properties.style.indexOf( "BOTTOM" ) === -1 );
    return result;
  },

  destructor : org.eclipse.rwt.protocol.AdapterUtil.getControlDestructor(),

  properties : org.eclipse.rwt.protocol.AdapterUtil.extendControlProperties( [
    "selection"
  ] ),

  propertyHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
    "selection" : function( widget, value ) {
      org.eclipse.rwt.protocol.AdapterUtil.callWithTarget( value, function( item ) {
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

  listeners : org.eclipse.rwt.protocol.AdapterUtil.extendControlListeners( [] ),

  listenerHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlListenerHandler( {} ),

  methods : []

} );