/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/


qx.Class.define( "org.eclipse.rwt.widgets.AbstractButton", {
  extend : org.eclipse.rwt.widgets.MultiCellWidget,
  type : "abstract",

  construct : function( buttonType ) {
    this.base( arguments, [ "image", "image", "label", "label", "image" ] );
    this._hasSelectionListener = false;
    this._selected = false;
    this._image = [ null, null, null ] ;
    this._hotImage = [ null, null, null ];
    this.addEventListener( "mouseover", this._onMouseOver );
    this.addEventListener( "mouseout", this._onMouseOut );
    this.addEventListener( "mousedown", this._onMouseDown );
    this.addEventListener( "mouseup", this._onMouseUp );
    this.addEventListener( "keydown", this._onKeyDown );
    this.addEventListener( "keyup", this._onKeyUp );
    this.addEventListener( "keypress", this._onKeyPress );
    this.addState( buttonType );
    switch( buttonType ) {
     case "push":
      this._isSelectable = false;
      this._isDeselectable = false;
      this._sendEvent = true;
     break;
     case "toggle":
     case "check":
      this._isSelectable = true;
      this._isDeselectable = true;
      this._sendEvent = true;
     break;
     case "radio":
      this._isSelectable = true;
      this._isDeselectable = false;
      this._sendEvent = false;
      org.eclipse.rwt.RadioButtonUtil.registerExecute( this );
      org.eclipse.rwt.RadioButtonUtil.registerKeypress( this );
     break;
    }

  },

  properties : {

    selectionIndicator : {
      apply : "_applySelectionIndicator",
      nullable : true,
      themeable : true
    }
    
  },

  members : {

    setImage : function( value, width, height ) {
      this._image = [ value, width, height ];
      this._updateButtonImage();
    },
            
    setHotImage : function( value, width, height ) {
      this._hotImage = [ value, width, height ];
      this._updateButtonImage();
    },        

    _updateButtonImage : function() {
      var image = 
          ( this._hotImage[ 0 ] != null && this.hasState( "over" ) ) 
        ? this._hotImage 
        : this._image;
      var current = this.getCellContent( 1 );
      if( current != image[ 0 ] ) {
        this.setCellContent( 1, image[ 0 ] );
        this.setCellDimension( 1, image[ 1 ], image[ 2 ] );
      }
    },        

    setText : function( value ) {
      this.setCellContent( 2, value );
    },

    _applySelectionIndicator : function( value, old ) {
      var url = value ? value[ 0 ] : null;
      var width = value ? value[ 1 ] : 0;
      var height = value ? value[ 2 ] : 0;      
      this.setCellContent( 0, url );
      this.setCellDimension( 0, width, height ); 
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
          org.eclipse.swt.EventUtil.addWidgetSelectedModifier();
        }
      }
    },

    // Not using EventUtil since no event should be sent for radio
    _sendChanges : function() {
      if( !org_eclipse_rap_rwt_EventUtil_suspend && this._hasSelectionListener )
      {
        var req = org.eclipse.swt.Request.getInstance();
        if( this._sendEvent ) {
          var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
          var id = widgetManager.findIdByWidget( this );
          req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
          org.eclipse.swt.EventUtil.addWidgetSelectedModifier();
        }
        req.send();
      }
    },
    
    _onMouseOver : function( event ) {
      if ( event.getTarget() == this ) {
        if( this.hasState( "abandoned" ) ) {
          this.removeState( "abandoned" );
          this.addState( "pressed" );
        }
        this.addState( "over" );
        this._updateButtonImage();
      }
    },

    _onMouseOut : function( event ) {
      if ( event.getTarget() == this ) {
        this.removeState( "over" );
        this._updateButtonImage();
        if ( this.hasState( "pressed" ) ) {
          this.setCapture( true );
          this.removeState( "pressed" );
          this.addState( "abandoned" );
        }
      }
    },

    _onMouseDown : function( event ) {
      if ( event.getTarget() == this && event.isLeftButtonPressed() ) {
        this.removeState( "abandoned" );
        this.addState( "pressed" );
      }
    },
    
    _onMouseUp : function( event ) {
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
        this._updateButtonImage();
        if ( hasPressed ) {
          this.execute();
        }
      }
    },

    _onKeyDown : function( event ) {
      switch( event.getKeyIdentifier() ) {
        case "Enter":
        case "Space":
          this.removeState( "abandoned" );
          this.addState( "pressed" );
          event.preventDefault();
          event.stopPropagation();
      }
    },

    _onKeyUp : function( event ) {
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

    _onKeyPress : function( event ) {
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
      }
    }
    
  }
});
