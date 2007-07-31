
/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.swt.widgets.Shell", {
  extend : qx.ui.window.Window,

  construct : function() {
    this.base( arguments );
    this.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
    // TODO [rh] HACK to set mode on Label that shows the caption, _captionTitle
    //      is a 'protected' field on class Window
    this._captionTitle.setMode( "html" );
    this._activeControl = null;
    this._activateListenerWidgets = new Array();
    // TODO [rh] check whether these listeners must be removed upon disposal
    this.addEventListener( "changeActiveChild", this._onChangeActiveChild );
    this.addEventListener( "changeActive", this._onChangeActive );
    this.addEventListener( "keydown", this._onKeydown );
    var req = org.eclipse.swt.Request.getInstance();
    req.addEventListener( "send", this._onSend, this );
    
////////////////////////////////////////////////
// TODO [fappel] experimental (rounded corners)
//    this._createRoundedCorners();
////////////////////////////////////////////////    
  },
  
  statics : {
    TOP_LEFT : "topLeft",
    TOP_RIGHT : "topRight",
    BOTTOM_LEFT : "bottomLeft",
    BOTTOM_RIGHT : "bottomRight",
    CORNER_NAMES : [ 
      "topLeft", 
      "topRight", 
      "bottomLeft", 
      "bottomRight"
    ]
  },
  
  destruct : function() {
    this.removeEventListener( "changeActiveChild", this._onChangeActiveChild );
    this.removeEventListener( "changeActive", this._onChangeActive );
    this.removeEventListener( "keydown", this._onKeydown );
    var req = org.eclipse.swt.Request.getInstance();
    req.removeEventListener( "send", this._onSend, this );

////////////////////////////////////////////////
// TODO [fappel] experimental (rounded corners)
//    this.removeEventListener( "changeWidth", this._adjustCornerLeft() );
//    this.removeEventListener( "changeHeight", this._adjustCornerTop() );
// TODO [fappel]: remove rounded corners
/////////////////////////////////////////////////
    
  },

  properties : {
    defaultButton : {
      // TODO [rh] remove _legacy
      _legacy : true,
      type    : "object"
    },

    alwaysOnTop : {
      // TODO [rh] remove _legacy
      _legacy : true,
      type    : "boolean"
    },
    
    dialogMode : {
      type : "boolean"
    }
  },

  members : {
    setActiveControl : function( control ) {
      this._activeControl = control;
    },

    /** To be called after rwt_TITLE is set */
    fixTitlebar : function() {
      if( this.hasState( "rwt_TITLE" ) ) {
        this._captionBar.addState( "rwt_TITLE" );
      } else {
        this.setShowCaption( false );
      }
    },

    /**
     * Adds a widget that has a server-side ActivateListener. If this widget or
     * any of its children are activated, an org.eclipse.swt.events.controlActivated 
     * is fired.
     */
    addActivateListenerWidget : function( widget ) {
      this._activateListenerWidgets.push( widget );
    },

    removeActivateListenerWidget : function( widget ) {
      qx.lang.Array.remove( this._activateListenerWidgets, widget );
    },

    _isRelevantActivateEvent : function( widget ) {
      var result = false;
      for( var i = 0; !result && i < this._activateListenerWidgets.length; i++ ) 
      {
        var listeningWidget = this._activateListenerWidgets[ i ];
        if(    !listeningWidget.contains( this._activeControl ) 
            && listeningWidget.contains( widget ) ) 
        {
          result = true;
        }
      }
      return result;
    },

    _onChangeActiveChild : function( evt ) {
      // Work around qooxdoo bug #254: the changeActiveChild is fired twice when
      // a widget was activated by keyboard (getData() is null in this case)
      var widget = this._getParentControl( evt.getData() );
      if( !org_eclipse_rap_rwt_EventUtil_suspend && widget != null ) {
        var widgetMgr = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetMgr.findIdByWidget( widget );
        var shellId = widgetMgr.findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
        if( this._isRelevantActivateEvent( widget ) ) {
          this._activeControl = widget;
          req.removeParameter( shellId + ".activeControl" );
          req.addEvent( "org.eclipse.swt.events.controlActivated", id );
          req.send();
        } else {
          req.addParameter( shellId + ".activeControl", id );
        }
      }
    },

    _onChangeActive : function( evt ) {
      // TODO [rst] This hack is a workaround for bug 345 in qooxdoo, remove this
      //      block as soon as the bug is fixed.
      //      See http://bugzilla.qooxdoo.org/show_bug.cgi?id=345
      if( !this.getActive() && !isFinite( this.getZIndex() ) ) {
        this.setZIndex( 1e8 );
      }
      // end of workaround
      if( !org_eclipse_rap_rwt_EventUtil_suspend && this.getActive() ) {
        var widgetMgr = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetMgr.findIdByWidget( this );
        var req = org.eclipse.swt.Request.getInstance();
        if( qx.lang.Array.contains( this._activateListenerWidgets, this ) ) {
          req.removeParameter( req.getUIRootId() + ".activeShell" );
          req.addEvent( "org.eclipse.swt.events.shellActivated", id );
          req.send();
        } else {
          req.addParameter( req.getUIRootId() + ".activeShell", id );
        }
      }
      var active = evt.getData();
      if( active ) {
        this._minimizeButton.addState( "active" );
        this._maximizeButton.addState( "active" );
        this._restoreButton.addState( "active" );
        this._closeButton.addState( "active" );
      } else {
        this._minimizeButton.removeState( "active" );
        this._maximizeButton.removeState( "active" );
        this._restoreButton.removeState( "active" );
        this._closeButton.removeState( "active" );
      }
    },

    _onKeydown : function( evt ) {
      var keyId = evt.getKeyIdentifier();
      if(    keyId == "Enter" 
          && !evt.isShiftPressed()
          && !evt.isAltPressed() 
          && !evt.isCtrlPressed() 
          && !evt.isMetaPressed() ) 
      {
        var defButton = this.getDefaultButton();
        if( defButton != null && defButton.isSeeable() ) {
          defButton.execute();
        }
      } else if( keyId == "Escape" && this.getDialogMode() ) {
        this.close();
      }
    },

    _onSend : function( evt ) {
      if( this.getActive() ) {
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var focusedChildId = null;
        if( this.getFocusedChild() != null ) {
          focusedChildId = widgetManager.findIdByWidget( this.getFocusedChild() );
        }
        var req = org.eclipse.swt.Request.getInstance();
        req.addParameter( req.getUIRootId() + ".focusControl", focusedChildId );
      }
    },

    /**
     * Returns the parent Control for the given widget. If widget is a Control 
     * itself, the widget is returned. Otherwise its parent is returned or null
     * if there is no parent
     */
    _getParentControl : function( widget ) {
      var widgetMgr = org.eclipse.swt.WidgetManager.getInstance();
      var result = widget;
      while( result != null && !widgetMgr.isControl( result ) ) {
        if( result.getParent ) {
          result = result.getParent();
        } else {
          result = null;
        }
      }
      return result;
    },

    /* TODO [rst] Revise when upgrading: overrides the _sendTo() function in
     *      superclass Window to allow for always-on-top.
     *      --> http://bugzilla.qooxdoo.org/show_bug.cgi?id=367
     */
    _sendTo : function() {
      var vAll = qx.lang.Object.getValues( this.getWindowManager().getAll() );
      vAll = vAll.sort( qx.util.Compare.byZIndex );
      var vLength = vAll.length;
      var vIndex = this._minZIndex;
      for( var i = 0; i < vLength; i++ ) {
        var newZIndex = vIndex++;
        if( vAll[ i ].getAlwaysOnTop() ) {
          newZIndex += vLength;
        }
        vAll[ i ].setZIndex( newZIndex );
      }
    },
    
    
    //////////////////
    // rounded corners
    
    _createRoundedCorners : function() {
      this._corners = new Object();
      this._radius = 20;
      this._titleBarSpacer = 26 - this._radius;

      this._blockBackgroundColorListener = false;
      this._clientAreaBg = new qx.ui.layout.VerticalBoxLayout();
      this._clientAreaBg.setEdge( 0 );
      this.add( this._clientAreaBg );
      
      this.addEventListener( "appear", function() {
        var bgColor = this.getBackgroundColor();
        this._clientAreaBg.setBackgroundColor( bgColor );
        this.setBackgroundColor( "transparent" );
        
        var element = this.getElement();
        for( i = 0; i < 4; i++ ) {
          var corner = document.createElement( "div" );
          corner.id = i;
          corner.style.position = "absolute";
          corner.style.width = this._radius + "px";
          if( i > 2 ) {
            corner.style.height = this._radius + "px";
          } else {
            corner.style.height
              = ( this._radius + this._titleBarSpacer ) + "px";
          }
          corner.style.backgroundColor = "transparent";
          
          this._computeCurve( corner );
          var cornerName = org.eclipse.swt.widgets.Shell.CORNER_NAMES[ i ];
          this._corners[ cornerName ] = corner.cloneNode( true );
          element.appendChild( this._corners[ cornerName ] );
        }
        var bottom = document.createElement( "div" );
        bottom.style.position = "absolute";
        bottom.style.left = this._radius;
        bottom.style.height = this._radius - 1;
        bottom.style.backgroundColor = "#9dd0ea"; // TODO Color
        this._bottom = bottom.cloneNode( true );
        element.appendChild( this._bottom );
        
        this.addEventListener( "changeWidth", function() {
          this._adjustCornerLeft();
        } );
        this.addEventListener( "changeHeight", function() {
          this._adjustCornerTop();
        } );
        this.addEventListener( "changeBackgroundColor", function() {
          if( !this._blockBackgroundColorListener ) {
            this._clientAreaBg.setBackgroundColor( this.getBackgroundColor() );
            this._blockBackgroundColorListener = true;
            this.setBackgroundColor( "transparent" );
            this._blockBackgroundColorListener = false;
          }
        } );
        
        this._captionBar.setLeft( this._radius );
        this._adjustCornerLeft();
        this._adjustCornerTop();
      } );
      
    },
    
    _computeCurve : function( corner ) {
      for( var i = 0; i < this._radius; i++ ) {
        var angle = Math.asin( i / this._radius );
        var ak;
        if( angle != 0 ) {
          var hyp = i / Math.sin( angle );
          ak = Math.cos( angle ) * hyp;
        } else {
          ak = this._radius;
        }
       
        var span = document.createElement( "span" );
        span.style.position = "absolute";
        span.style.height = "1px";
        span.style.backgroundColor = "#9dd0ea"; // TODO Color
        span.style.display = "block";
        switch( eval( corner.id ) ) {
          case 0:
            span.style.left = ( this._radius - ak ) + "px";
            span.style.width = ak + "px";
            span.style.top = ( this._radius - i ) + "px";
          break;
          case 1:
            span.style.left = "0px";
            span.style.width = ak + "px";
            span.style.top = ( this._radius - i ) + "px";
          break;
          case 2:
            span.style.left = ( this._radius - ak ) + "px";
            span.style.width = ak + "px";
            span.style.top = ( i - 1 ) + "px";
          break;
          case 3:
            span.style.left = "0px";
            span.style.width = ak + "px";
            span.style.top = ( i - 1 ) + "px";
          break;
        }
        corner.appendChild( span.cloneNode( true ) );
        this._appendTitleBarSpacer( corner );
      }
    },
    
    _appendTitleBarSpacer : function( corner ) {
      if( eval( corner.id ) < 3 ) {
        var titleBarSpacer = document.createElement( "span" );
        titleBarSpacer.style.position = "absolute";
        titleBarSpacer.style.top = this._radius;
        titleBarSpacer.style.left = 0;
        titleBarSpacer.style.width = this._radius;
        titleBarSpacer.style.height = this._titleBarSpacer;
        titleBarSpacer.style.backgroundColor = "#9dd0ea"; // TODO color
        corner.appendChild( titleBarSpacer.cloneNode( true ) );
      }
    },
    
    _adjustCornerLeft : function() {
      var width = this.getWidth();
      this._getTopLeft().style.left = "0px";
      this._getTopRight().style.left = ( width - this._radius ) + "px";
      this._getBottomLeft().style.left = "0px";
      this._getBottomRight().style.left = ( width - this._radius ) + "px";
      this._captionBar.setWidth( width - 2 * this._radius );
      this._bottom.style.width = ( width - this._radius * 2 ) + "px";
    },
    
    _adjustCornerTop : function() {
      var height = this.getHeight();
      var clientAreaHeight
        = this.getHeight() + 1 - this._titleBarSpacer - this._radius * 2;
      this._clientAreaBg.setHeight( clientAreaHeight );
      this._getTopLeft().style.top = "-1px";
      this._getTopRight().style.top = "-1px";
      this._getBottomLeft().style.top = ( height - this._radius ) + "px";
      this._getBottomRight().style.top = ( height - this._radius ) + "px";
      this._bottom.style.top = ( height - this._radius ) + "px";
    },
    
    _getTopLeft : function() {
      return this._corners[ org.eclipse.swt.widgets.Shell.TOP_LEFT ];
    },
    
    _getTopRight : function() {
      return this._corners[ org.eclipse.swt.widgets.Shell.TOP_RIGHT ];
    },
    
    _getBottomLeft : function() {
      return this._corners[ org.eclipse.swt.widgets.Shell.BOTTOM_LEFT ];
    },
    
    _getBottomRight : function() {
      return this._corners[ org.eclipse.swt.widgets.Shell.BOTTOM_RIGHT ];
    }
  }
});
