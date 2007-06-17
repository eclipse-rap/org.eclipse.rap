
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

// TODO [rst] Setting the "icon" property on a qx.ui.window.Window does not
//      work with the current qx version. Changed constructor to be able to set
//      a window icon at all.
qx.Class.define( "org.eclipse.swt.widgets.Shell", {
  extend : qx.ui.window.Window,

  construct : function( icon ) {
    this.base( arguments, "", icon );
    this.setOverflow( qx.constant.Style.OVERFLOW_HIDDEN );
    // TODO [rh] HACK to set mode on Label that shows the caption, _captionTitle
    //      is a 'protected' field on class Window
    this._captionTitle.setMode( "html" );
    this._isDialogWindow = false;
    this._activeControl = null;
    this._activateListenerWidgets = new Array();
    // TODO [rh] check whether these listeners must be removed upon disposal
    this.addEventListener( "changeActiveChild", this._onChangeActiveChild );
    this.addEventListener( "changeActive", this._onChangeActive );
    this.addEventListener( "keydown", this._onKeydown );
    var req = org.eclipse.swt.Request.getInstance();
    req.addEventListener( "send", this._onSend, this );
  },
  
  destruct : function() {
    this.removeEventListener( "changeActiveChild", this._onChangeActiveChild );
    this.removeEventListener( "changeActive", this._onChangeActive );
    this.removeEventListener( "keydown", this._onKeydown );
    var req = org.eclipse.swt.Request.getInstance();
    req.removeEventListener( "send", this._onSend, this );
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

    setDialogWindow : function() {
      this._isDialogWindow = true;
    },

    /**
     * Adds a widget that has a server-side ActivateListener. If this widget or
     * any of its children are activated, an org.eclipse.swt.events.controlActivated 
     * is fired.
     */
    addActivateListenerWidget : function( widget ) {
      // TODO [rh] the line below leads to an error - investigate this
      //  qx.lang.Array.append( this._activateListenerWidgets, widget );
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
      } else if( keyId == "Escape" && this._isDialogWindow ) {
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
    }
  }
});
