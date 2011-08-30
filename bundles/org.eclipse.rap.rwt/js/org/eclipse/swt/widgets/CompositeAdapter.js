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

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.Composite", {

  factory : function( properties ) {
    var result = new org.eclipse.swt.widgets.Composite();
    org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    org.eclipse.rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    return result;
  },
  
  destructor : org.eclipse.rwt.protocol.AdapterUtil.getControlDestructor(),

  properties : org.eclipse.rwt.protocol.AdapterUtil.extendControlProperties( [
    "backgroundGradient",
    "roundedBorder"
  ] ),

  propertyHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
    "backgroundGradient" : org.eclipse.rwt.protocol.AdapterUtil.getBackgroundGradientHandler(),
    "roundedBorder" : org.eclipse.rwt.protocol.AdapterUtil.getRoundedBorderHandler()
  } ),     

  knownListeners : org.eclipse.rwt.protocol.AdapterUtil.extendControlListeners( [] ),

  listenerHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlListenerHandler( {} ),

  knownMethods : []


} );