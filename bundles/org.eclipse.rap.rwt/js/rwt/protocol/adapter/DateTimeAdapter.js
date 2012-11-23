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

rwt.protocol.AdapterRegistry.add( "rwt.widgets.DateTime", {

  factory : function( properties ) {
    var result;
    var styleMap = rwt.protocol.AdapterUtil.createStyleMap( properties.style );
    var styles;
    if( styleMap.SHORT ) {
      styles = "short";
    } else if( styleMap.LONG ) {
      styles = "long";
    } else {
      styles = "medium";
    }
    if( styleMap.DROP_DOWN ) {
      styles += "|drop_down";
    }
    if( styleMap.TIME ) {
      result = new rwt.widgets.DateTimeTime( styles );
    } else if( styleMap.CALENDAR ) {
      rwt.widgets.base.Calendar.CELL_WIDTH = properties.cellSize[ 0 ];
      rwt.widgets.base.Calendar.CELL_HEIGHT = properties.cellSize[ 1 ];
      result = new rwt.widgets.DateTimeCalendar( styles,
                                                             properties.monthNames,
                                                             properties.weekdayShortNames );
    } else {
      rwt.widgets.base.Calendar.CELL_WIDTH = properties.cellSize[ 0 ];
      rwt.widgets.base.Calendar.CELL_HEIGHT = properties.cellSize[ 1 ];
      result = new rwt.widgets.DateTimeDate( styles,
                                                         properties.monthNames,
                                                         properties.weekdayNames,
                                                         properties.weekdayShortNames,
                                                         properties.dateSeparator,
                                                         properties.datePattern );
    }
    rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : rwt.protocol.AdapterUtil.getControlDestructor(),

  getDestroyableChildren : rwt.protocol.AdapterUtil.getDestroyableChildrenFinder(),

  properties : rwt.protocol.AdapterUtil.extendControlProperties( [
    "year",
    "month",
    "day",
    "hours",
    "minutes",
    "seconds",
    "subWidgetsBounds"
  ] ),

  propertyHandler : rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
    "subWidgetsBounds" : function( widget, value ) {
      for( var i = 0; i < value.length; i++ ) {
        widget.setBounds.apply( widget, value[ i ] );
      }
    }
  } ),

  listeners : rwt.protocol.AdapterUtil.extendControlListeners( [
    "Selection"
  ] ),

  listenerHandler : rwt.protocol.AdapterUtil.extendControlListenerHandler( {} )

} );
