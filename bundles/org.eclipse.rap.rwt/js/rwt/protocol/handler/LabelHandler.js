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

rwt.protocol.HandlerRegistry.add( "rwt.widgets.Label", {

  factory : function( properties ) {
    var styleMap = rwt.protocol.HandlerUtil.createStyleMap( properties.style );
    styleMap.MARKUP_ENABLED = properties.markupEnabled;
    var result = new rwt.widgets.Label( styleMap );
    rwt.protocol.HandlerUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    rwt.protocol.HandlerUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : rwt.protocol.HandlerUtil.getControlDestructor(),

  getDestroyableChildren : rwt.protocol.HandlerUtil.getDestroyableChildrenFinder(),

  properties : rwt.protocol.HandlerUtil.extendControlProperties( [
    "text",
    "image",
    "alignment",
    "appearance",
    "leftMargin",
    "topMargin",
    "rightMargin",
    "bottomMargin",
    "backgroundGradient"
  ] ),

  propertyHandler : rwt.protocol.HandlerUtil.extendControlPropertyHandler( {
    "backgroundGradient" : rwt.protocol.HandlerUtil.getBackgroundGradientHandler()
  } ),

  listeners : rwt.protocol.HandlerUtil.extendControlListeners( [] ),

  listenerHandler : rwt.protocol.HandlerUtil.extendControlListenerHandler( {} )

} );
