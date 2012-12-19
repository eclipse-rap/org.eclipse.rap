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

rwt.protocol.HandlerRegistry.add( "rwt.widgets.Separator", {

  factory : function( properties ) {
    var result = new rwt.widgets.Separator();
    rwt.protocol.HandlerUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    rwt.protocol.HandlerUtil.setParent( result, properties.parent );
    var styleMap = rwt.protocol.HandlerUtil.createStyleMap( properties.style );
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

  destructor : rwt.protocol.HandlerUtil.getControlDestructor(),

  getDestroyableChildren : rwt.protocol.HandlerUtil.getDestroyableChildrenFinder(),

  properties : rwt.protocol.HandlerUtil.extendControlProperties( [] ),

  propertyHandler : rwt.protocol.HandlerUtil.extendControlPropertyHandler( {} ),

  listeners : rwt.protocol.HandlerUtil.extendControlListeners( [] ),

  listenerHandler : rwt.protocol.HandlerUtil.extendControlListenerHandler( {} )

} );
