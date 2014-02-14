/*******************************************************************************
 * Copyright (c) 2004, 2014 1&1 Internet AG, Germany, http://www.1und1.de,
 *                          EclipseSource, and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Remote Application Platform
 ******************************************************************************/


/**
 * A button.
 *
 * @appearance button
 * @state abandoned
 * @state over
 * @state pressed
 */
rwt.qx.Class.define( "rwt.widgets.base.Button", {

  extend : rwt.widgets.base.Atom,

  construct : function( vText, vIcon, vIconWidth, vIconHeight ) {
    this.base( arguments, vText, vIcon, vIconWidth, vIconHeight );
    this.initTabIndex();
    this.addEventListener( "mouseover", this._onmouseover );
    this.addEventListener( "mouseout", this._onmouseout );
    this.addEventListener( "mousedown", this._onmousedown );
    this.addEventListener( "mouseup", this._onmouseup );
    this.addEventListener( "keydown", this._onkeydown );
    this.addEventListener( "keyup", this._onkeyup );
  },

  properties : {

    appearance : {
      refine : true,
      init : "button"
    },

    tabIndex : {
      refine : true,
      init : 1
    }
  },

  members : {

    _onmouseover : function( event ) {
      if( event.getTarget() !== this ) {
        return;
      }
      if( this.hasState( "abandoned" ) ) {
        this.removeState( "abandoned" );
        this.addState( "pressed" );
      }
      this.addState( "over" );
    },

    _onmouseout : function( event ) {
      if( event.getTarget() !== this ) {
        return;
      }
      this.removeState( "over" );
      if( this.hasState( "pressed" ) ) {
        // Activate capturing if the button get a mouseout while
        // the button is pressed.
        this.setCapture( true );
        this.removeState( "pressed" );
        this.addState( "abandoned" );
      }
    },

    _onmousedown : function( event ) {
      if( event.getTarget() != this || !event.isLeftButtonPressed() ) {
        return;
      }
      this.removeState( "abandoned" );
      this.addState( "pressed" );
    },

    _onmouseup : function() {
      this.setCapture( false );
      // We must remove the states before executing the command
      // because in cases were the window lost the focus while
      // executing we get the capture phase back (mouseout).
      var hasPressed = this.hasState( "pressed" );
      var hasAbandoned = this.hasState( "abandoned" );
      if( hasPressed ) {
        this.removeState( "pressed" );
      }
      if( hasAbandoned ) {
        this.removeState( "abandoned" );
      }
      if( !hasAbandoned ) {
        this.addState( "over" );
        if( hasPressed ) {
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
          event.stopPropagation();
      }
    },

    _onkeyup : function( event ) {
      switch( event.getKeyIdentifier() ) {
        case "Enter":
        case "Space":
          if( this.hasState( "pressed" ) ) {
            this.removeState( "abandoned" );
            this.removeState( "pressed" );
            this.execute();
            event.stopPropagation();
          }
      }
    }
  }

} );
