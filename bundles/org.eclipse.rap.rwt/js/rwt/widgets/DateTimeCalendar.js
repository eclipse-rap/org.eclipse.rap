/*******************************************************************************
 * Copyright (c) 2008, 2013 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/

rwt.qx.Class.define( "rwt.widgets.DateTimeCalendar", {
  extend : rwt.widgets.base.Parent,

  construct : function( style, monthNames, weekdayNames ) {
    this.base( arguments );
    this.setOverflow( "hidden" );
    this.setAppearance( "datetime-calendar" );

    // Has selection listener
    this._hasSelectionListener = false;

    // Get names of weekdays and months
    rwt.widgets.base.Calendar.MONTH_NAMES = monthNames;
    rwt.widgets.base.Calendar.WEEKDAY_NAMES = weekdayNames;

    // The Calendar
    this._calendar = new rwt.widgets.base.Calendar();
    this._calendar.setDate( new Date( 74, 5, 6 ) );
    this._calendar.setTabIndex( null );
    this._calendar.addEventListener( "changeDate", this._onChangeDate, this );
    this.add( this._calendar );

    this.addEventListener( "contextmenu", this._onContextMenu, this );
    this.addEventListener( "keypress", this._onKeyPress, this );
    this.addEventListener( "mousewheel", this._onmousewheel, this );
    this.addEventListener( "focus", this._onFocusIn, this );
    this.addEventListener( "blur", this._onFocusOut, this );

    this._updateSelectedDayState();
  },

  destruct : function() {
    this._calendar.removeEventListener( "changeDate", this._onChangeDate, this );
    this.removeEventListener( "contextmenu", this._onContextMenu, this );
    this.removeEventListener( "keypress", this._onKeyPress, this );
    this.removeEventListener( "mousewheel", this._onmousewheel, this );
    this.removeEventListener( "focus", this._onFocusIn, this );
    this.removeEventListener( "blur", this._onFocusOut, this );
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

    _onFocusIn : function( evt ) {
      this._updateSelectedDayState();
    },

    _onFocusOut : function( evt ) {
      this._updateSelectedDayState();
    },

    _updateSelectedDayState : function() {
      for( var i = 0; i < 6 * 7; i++ ) {
        if( this.getFocused() ) {
          this._calendar._dayLabelArr[ i ].removeState( "parent_unfocused" );
        } else {
          this._calendar._dayLabelArr[ i ].addState( "parent_unfocused" );
        }
      }
    },

    _sendChanges : function( date, month, year ) {
      if( !rwt.remote.EventUtil.getSuspended() ) {
        var remoteObject = rwt.remote.Server.getInstance().getRemoteObject( this );
        remoteObject.set( "day", date );
        remoteObject.set( "month", month );
        remoteObject.set( "year", year );
        if( this._hasSelectionListener ) {
          rwt.remote.EventUtil.notifySelected( this );
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
