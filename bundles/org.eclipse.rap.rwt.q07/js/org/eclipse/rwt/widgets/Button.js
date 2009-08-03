/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/


qx.Class.define( "org.eclipse.rwt.widgets.Button", {
  extend : org.eclipse.rwt.widgets.MultiCellWidget,

  construct : function( buttonType ) {
    this.base( arguments, [ "image", "image", "label" ] );
    this.initTabIndex();
    this.addEventListener( "mouseover", this._onmouseover );
    this.addEventListener( "mouseout", this._onmouseout );
    this.addEventListener( "mousedown", this._onmousedown );
    this.addEventListener( "mouseup", this._onmouseup );
    this.addEventListener( "keydown", this._onkeydown );
    this.addEventListener( "keyup", this._onkeyup );
    this.addEventListener( "keypress", this._onkeypress );

    switch( buttonType ) {
     case "push" :
      this._isSelectable = false;
      this._isDeselectable = false;
      this._sendEvent = true;
      this.setAppearance( "button" );
     break;
     case "toggle":
      this._isSelectable = true;
      this._isDeselectable = true;
      this._sendEvent = true;
      this.setAppearance( "button" );
     break;
     case "check":
      this._isSelectable = true;
      this._isDeselectable = true;
      this._sendEvent = true;
      this.setAppearance( "check-box" );
     break;
     case "radio":
      this._isSelectable = true;
      this._isDeselectable = false;
      this._sendEvent = false;
      this.setAppearance( "radio-button" );
      org.eclipse.rwt.RadioButtonUtil.register( this );
     break;
     default:
       throw( "Unkown button type " + buttonType );
     break;
    }

  },

  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

  properties : {

    selectionIndicator : {
      check : "String",
      apply : "_applySelectionIndicator",
      nullable : true,
      themeable : true
    },

    tabIndex : {
      refine : true,
      init : 1
    }
  },

  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members : {
    _hasSelectionListener : false,
    _selected : false,

    setImage : function( value, width, height ) {
      this.setCellContent( 1, value );
      this.setCellDimension( 1, width, height );
    },

    setText : function( value ) {
      this.setCellContent( 2, value );
    },

    _applySelectionIndicator : function( value, old ) {
      this.setCellContent( 0, value );
      this.setCellDimension( 0, 13, 13 );
    },

    setHasSelectionListener : function( value ) {
      this._hasSelectionListener = value;
    },

    setGrayed : function( value ) {
      if( value ) {
        this.addState( "grayed" );
      } else {
        this.removeState( "grayed" );
      }
    },

    execute : function() {
      this.base( arguments );
      if( this._isSelectable ) {
        this.setSelection( !( this._selected && this._isDeselectable ) );
      }
      this._sendChanges();
    },

    setSelection : function( value ) {
      if( this._selected != value ) {
        this._selected = value;
        if( this._selected ) {
          this.addState( "selected" );
        } else {
          this.removeState( "selected" );
        }
        if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
          var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
          var id = widgetManager.findIdByWidget( this );
          var req = org.eclipse.swt.Request.getInstance();
          req.addParameter( id + ".selection", this._selected );
        }
      }
    },

    // Not using EventUtil since no event should be sent for radio
    _sendChanges : function() {
      if(    !org_eclipse_rap_rwt_EventUtil_suspend
          && this._hasSelectionListener )
      {
        var req = org.eclipse.swt.Request.getInstance();
        if( this._sendEvent ) {
          var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
          var id = widgetManager.findIdByWidget( this );
          req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
        }
        req.send();
      }
    },

    _onmouseover : function( event ) {
      if ( event.getTarget() == this ) {
        if( this.hasState( "abandoned" ) ) {
          this.removeState( "abandoned" );
          this.addState( "pressed" );
        }
        this.addState( "over" );
      }
    },

    _onmouseout : function( event ) {
      if ( event.getTarget() == this ) {
        this.removeState( "over" );
        if ( this.hasState( "pressed" ) ) {
          this.setCapture( true );
          this.removeState( "pressed" );
          this.addState( "abandoned" );
        }
      }
    },

    _onmousedown : function( event ) {
      if ( event.getTarget() != this || !event.isLeftButtonPressed() ) {
        return;
      }
      this.removeState( "abandoned" );
      this.addState( "pressed" );
    },

    _onmouseup : function( event ) {
      this.setCapture( false );
      var hasPressed = this.hasState( "pressed" );
      var hasAbandoned = this.hasState( "abandoned" );
      if( hasPressed ) {
        this.removeState( "pressed" );
      }
      if ( hasAbandoned ) {
        this.removeState( "abandoned" );
      }
      if ( !hasAbandoned ) {
        this.addState( "over" );
        if ( hasPressed ) {
          this.execute();
        }
      }
    },

    _onkeydown : function( event ) {
      switch( event.getKeyIdentifier() ) {
        case "Enter":
        case "Space":
          this.removeState( "abandoned" );
          this.addState( "pressed" );
          event.preventDefault();
          event.stopPropagation();
      }
    },

    _onkeyup : function( event ) {
      switch( event.getKeyIdentifier() ) {
        case "Enter":
        case "Space":
          if ( this.hasState( "pressed" ) ) {
            this.removeState( "abandoned" );
            this.removeState( "pressed" );
            this.execute();
            event.preventDefault();
            event.stopPropagation();
          }
      }
    },

    _onkeypress : function( event ) {
      switch( event.getKeyIdentifier() ) {
        case "Left":
        case "Up":
        case "Right":
        case "Down":
        case "PageUp":
        case "PageDown":
        case "End":
        case "Home":
          event.preventDefault();
          event.stopPropagation();
        default:
      }

    }
  }
});
