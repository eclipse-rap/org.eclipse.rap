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

rwt.remote.HandlerRegistry.add( "rwt.widgets.Combo", {

  factory : function( properties ) {
    var result = new rwt.widgets.Combo( properties.ccombo );
    rwt.remote.HandlerUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    rwt.remote.HandlerUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : rwt.remote.HandlerUtil.getControlDestructor(),

  getDestroyableChildren : rwt.remote.HandlerUtil.getDestroyableChildrenFinder(),

  properties : rwt.remote.HandlerUtil.extendControlProperties( [
    "itemHeight",
    "visibleItemCount",
    "items",
    "listVisible",
    "selectionIndex",
    "editable",
    "text",
    "selection",
    "textLimit"
  ] ),

  propertyHandler : rwt.remote.HandlerUtil.extendControlPropertyHandler( {
    "selectionIndex" : function( widget, value ) {
      widget.select( value );
    },
    "selection" : function( widget, value ) {
      var start = value[ 0 ];
      var length = value[ 1 ] - value[ 0 ];
      widget.setTextSelection( start, length );
    }
  } ),

  listeners : rwt.remote.HandlerUtil.extendControlListeners( [
    "Selection",
    "DefaultSelection",
    "Modify"
  ] ),

  listenerHandler : rwt.remote.HandlerUtil.extendControlListenerHandler( {} )

} );
