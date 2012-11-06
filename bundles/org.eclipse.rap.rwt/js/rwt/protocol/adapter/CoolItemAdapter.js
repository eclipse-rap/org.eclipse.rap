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

rwt.protocol.AdapterRegistry.add( "rwt.widgets.CoolItem", {

  factory : function( properties ) {
    var styles = rwt.protocol.AdapterUtil.createStyleMap( properties.style );
    var orientation = styles.VERTICAL ? "vertical" : "horizontal";
    var result = new rwt.widgets.CoolItem( orientation );
    rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    result.setMinWidth( 0 );
    result.setMinHeight( 0 );
    return result;
  },

  destructor : rwt.protocol.AdapterUtil.getWidgetDestructor(),

  properties : [ "bounds", "control", "customVariant" ],

  propertyHandler : {
    "bounds" : function( widget, bounds ) {
      widget.setLeft( bounds[ 0 ] );
      widget.setTop( bounds[ 1 ] );
      widget.setWidth( bounds[ 2 ] );
      widget.setHeight( bounds[ 3 ] );
      widget.updateHandleBounds();
    },
    "control" : function( widget, controlId ) {
      rwt.protocol.AdapterUtil.callWithTarget( controlId, function( control ) {
        widget.setControl( control );
      } );
    }
  }

} );
