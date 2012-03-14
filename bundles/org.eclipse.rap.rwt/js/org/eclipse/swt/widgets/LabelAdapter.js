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

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.Label", {

  factory : function( properties ) {
    var styleMap = org.eclipse.rwt.protocol.AdapterUtil.createStyleMap( properties.style );
    styleMap.MARKUP_ENABLED = properties.markupEnabled;
    var result = new org.eclipse.rwt.widgets.Label( styleMap );
    org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    org.eclipse.rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : org.eclipse.rwt.protocol.AdapterUtil.getControlDestructor(),

  properties : org.eclipse.rwt.protocol.AdapterUtil.extendControlProperties( [
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
  
  propertyHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
    "backgroundGradient" : org.eclipse.rwt.protocol.AdapterUtil.getBackgroundGradientHandler()
  } ),

  listeners : org.eclipse.rwt.protocol.AdapterUtil.extendControlListeners( [] ),

  listenerHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlListenerHandler( {} ),

  methods : []

} );