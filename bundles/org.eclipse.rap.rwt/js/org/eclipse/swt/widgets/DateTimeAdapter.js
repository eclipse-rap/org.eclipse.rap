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

org.eclipse.rwt.protocol.AdapterRegistry.add( "rwt.widgets.DateTime", {

  factory : function( properties ) {
    var result;
    var styleMap = org.eclipse.rwt.protocol.AdapterUtil.createStyleMap( properties.style );
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
      result = new org.eclipse.swt.widgets.DateTimeTime( styles );
    } else if( styleMap.CALENDAR ) {
      org.eclipse.swt.widgets.Calendar.CELL_WIDTH = properties.cellSize[ 0 ];
      org.eclipse.swt.widgets.Calendar.CELL_HEIGHT = properties.cellSize[ 1 ];
      result = new org.eclipse.swt.widgets.DateTimeCalendar( styles,
                                                             properties.monthNames,
                                                             properties.weekdayShortNames );
    } else {
      org.eclipse.swt.widgets.Calendar.CELL_WIDTH = properties.cellSize[ 0 ];
      org.eclipse.swt.widgets.Calendar.CELL_HEIGHT = properties.cellSize[ 1 ];
      result = new org.eclipse.swt.widgets.DateTimeDate( styles,
                                                         properties.monthNames,
                                                         properties.weekdayNames,
                                                         properties.weekdayShortNames,
                                                         properties.dateSeparator,
                                                         properties.datePattern );
    }
    org.eclipse.rwt.protocol.AdapterUtil.addStatesForStyles( result, properties.style );
    result.setUserData( "isControl", true );
    org.eclipse.rwt.protocol.AdapterUtil.setParent( result, properties.parent );
    return result;
  },

  destructor : org.eclipse.rwt.protocol.AdapterUtil.getControlDestructor(),

  properties : org.eclipse.rwt.protocol.AdapterUtil.extendControlProperties( [
    "year",
    "month",
    "day",
    "hours",
    "minutes",
    "seconds",
    "subWidgetsBounds"
  ] ),

  propertyHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlPropertyHandler( {
    "subWidgetsBounds" : function( widget, value ) {
      for( var i = 0; i < value.length; i++ ) {
	      widget.setBounds.apply( widget, value[ i ] );
      }
    }
  } ),

  listeners : org.eclipse.rwt.protocol.AdapterUtil.extendControlListeners( [
    "selection"
  ] ),

  listenerHandler : org.eclipse.rwt.protocol.AdapterUtil.extendControlListenerHandler( {} ),

  methods : []

} );