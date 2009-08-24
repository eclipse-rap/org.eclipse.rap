/*******************************************************************************
 * Copyright (c) 2008, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/

qx.Class.define( "org.eclipse.swt.widgets.DateTimeCalendar", {
  extend : qx.ui.layout.CanvasLayout,

  construct : function( style, monthNames, weekdayNames ) {
    this.base( arguments );
    this.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
    this.setAppearance( "datetime-calendar" );
    
    // Has selection listener
    this._hasSelectionListener = false;
    
    // Get names of weekdays and months
    org.eclipse.swt.widgets.Calendar.MONTH_NAMES = monthNames;    
    org.eclipse.swt.widgets.Calendar.WEEKDAY_NAMES = weekdayNames;
    
    // The Calendar
    this._calendar = new org.eclipse.swt.widgets.Calendar();
    this._calendar.addEventListener( "changeDate", this._onChangeDate, this );
    this._calendar.setDate( new Date( 74, 5, 6 ) );
    this._calendar.setTabIndex( -1 );
    this.add( this._calendar );
    
    this.addEventListener( "contextmenu", this._onContextMenu, this );
    this.addEventListener( "keypress", this._onKeyPress, this );
    this.addEventListener( "mousewheel", this._onmousewheel, this );
  },

  destruct : function() {
    this._calendar.removeEventListener( "changeDate", this._onChangeDate, this );
    this.removeEventListener( "contextmenu", this._onContextMenu, this );
    this.removeEventListener( "keypress", this._onKeyPress, this );
    this.removeEventListener( "mousewheel", this._onmousewheel, this );
    this._disposeObjects( "_calendar" );
  },

  members : {
    addState : function( state ) {
      this.base( arguments, state );
      if( state.substr( 0, 8 ) == "variant_" ) {
        this._calendar.addState( state );
      }
    },

    removeState : function( state ) {
      this.base( arguments, state );
      if( state.substr( 0, 8 ) == "variant_" ) {
        this._calendar.removeState( state );
      }
    },
    
    _onChangeDate : function() {
      var date = this._calendar.getDate();      
      this._sendChanges( date.getDate(), date.getMonth(), date.getFullYear() );
    },
    
    _onContextMenu : function( evt ) {     
      var menu = this.getContextMenu();      
      if( menu != null ) {
        menu.setLocation( evt.getPageX(), evt.getPageY() );
        menu.setOpener( this );
        menu.show();
        evt.stopPropagation();
      }
    },
    
    _onKeyPress : function( evt ) {
      this._calendar._onkeypress( evt );
    },
    
    _onmousewheel : function( evt ) {
      evt.preventDefault();
      evt.stopPropagation();
    },
    
    _sendChanges : function( date, month, year ) {
      if( !org_eclipse_rap_rwt_EventUtil_suspend ) {        
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var req = org.eclipse.swt.Request.getInstance();
        var id = widgetManager.findIdByWidget( this );        
        req.addParameter( id + ".day", date );
        req.addParameter( id + ".month", month );
        req.addParameter( id + ".year", year );
        if( this._hasSelectionListener ) {
          req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
          req.send();
        }
      }
    },
    
    setMonth : function( value ) { 
      var date = this._calendar.getDate();
      date.setMonth( value );
      this._calendar.setDate( date );
    },
    
    setDay : function( value ) {
      var date = this._calendar.getDate();
      date.setDate( value );
      this._calendar.setDate( date );
    },
    
    setYear : function( value ) {
      var date = this._calendar.getDate();
      date.setYear( value );
      this._calendar.setDate( date );
    },
    
    setHasSelectionListener : function( value ) {
      this._hasSelectionListener = value;
    },
    
    setFont : function() {
      // TODO: [if] Calendar font is not implemented
    }
  }
} );
