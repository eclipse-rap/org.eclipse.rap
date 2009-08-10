/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipsevent.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/


/**
 * This class contains static functions for radio buttons
 */
qx.Class.define( "org.eclipse.rwt.RadioButtonUtil", {

  statics : {

    register : function( button ) {
      button.addEventListener( "execute", this._onSelection, this );
      button.addEventListener( "keypress", this._onKeypress, this );
    },

    _onKeypress : function( event ) {
      var widget = event.getTarget();
      switch( event.getKeyIdentifier() ) {
        case "Left":
        case "Up":
          this._setNextOrPrevious( widget, "previous" );
          widget._sendChanges();
          event.preventDefault();
          event.stopPropagation();
          break;
        case "Right":
        case "Down":
          this._setNextOrPrevious( widget, "next" );
          widget._sendChanges();
          event.preventDefault();
          event.stopPropagation();
          break;
      }
    },

    _onSelection : function( event ) {
      this._unselectSiblings( event.getTarget() );
    },

    // Set the "checked" property and focus on the following (next or previous)
    // radio button of the same group, after a corresponding key press.
    _setNextOrPrevious : function( widget, command ) {
      // array of all radio buttons from this group
      var allRadioButtons = new Array();
      // the index of the current radio button in allRadioButtons-array
      var currentRbIndex;
      // the index of the next selected radio button in allRadioButtons-array
      var nextSelectedRbIndex;
      var parent = widget.getParent();
      var siblings = parent.getChildren();
      for( var i = 0; i < siblings.length; i++ ) {
        if( siblings[ i ].hasState( "rwt_RADIO" ) ) {
          allRadioButtons.push( siblings[ i ] );
        }
      }
      for( var j = 0; j < allRadioButtons.length; j++ ) {
        if( allRadioButtons[ j ] == widget ) {
          currentRbIndex = j;
        }
      }
      // assign a value to 'nextSelectedRbIndex',
      // in case the 'command' is unrecognizable
      nextSelectedRbIndex = currentRbIndex;
      if ( command == "next" ) {
        nextSelectedRbIndex = currentRbIndex + 1;
        if( nextSelectedRbIndex >= allRadioButtons.length ) {
          nextSelectedRbIndex = 0;
        }
      }
      if ( command == "previous" ) {
        nextSelectedRbIndex = currentRbIndex - 1;
        if( nextSelectedRbIndex < 0 ) {
          nextSelectedRbIndex = allRadioButtons.length - 1;
        }
      }
      var nextRb = allRadioButtons[ nextSelectedRbIndex ];
      this._unselectSiblings( nextRb );
      nextRb.setSelection( true );
      nextRb.setFocused( true );
    },

    _unselectSiblings : function( widget ) {
      var siblings = widget.getParent().getChildren();
      for( var i = 0; i < siblings.length; i++ ) {
        if(    siblings[ i ] != widget
            && siblings[ i ].hasState( "rwt_RADIO" ) )
        {
          siblings[ i ].setSelection( false );
        }
      }
    }
    
  }

} );
