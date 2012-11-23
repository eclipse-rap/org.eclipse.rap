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

rwt.protocol.AdapterRegistry.add( "rwt.widgets.Separator", {

  factory : function( properties ) {
    var result = new rwt.widgets.Separator();
    rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    var styleMap = rwt.protocol.AdapterUtil.createStyleMap( properties.style );
    result.setLineOrientation( styleMap.VERTICAL ? "vertical" : "horizontal" );
    var lineStyle = "rwt_SHADOW_NONE";
    if( styleMap.SHADOW_IN ) {
      lineStyle = "rwt_SHADOW_IN";
    } else if( styleMap.SHADOW_OUT ) {
      lineStyle = "rwt_SHADOW_OUT";
    }
    result.setLineStyle( lineStyle );
    return result;
  },

  destructor : rwt.protocol.AdapterUtil.getControlDestructor(),

  getDestroyableChildren : rwt.protocol.AdapterUtil.getDestroyableChildrenFinder(),

  properties : rwt.protocol.AdapterUtil.extendControlProperties( [] ),

  propertyHandler : rwt.protocol.AdapterUtil.extendControlPropertyHandler( {} ),

  listeners : rwt.protocol.AdapterUtil.extendControlListeners( [] ),

  listenerHandler : rwt.protocol.AdapterUtil.extendControlListenerHandler( {} )

} );
