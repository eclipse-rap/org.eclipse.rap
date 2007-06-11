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
/**
 * This class contains static functions needed for labels.
 */
qx.Class.define( "org.eclipse.swt.LinkUtil", {
  /*
  *****************************************************************************
     STATICS
  *****************************************************************************
  */

  statics : {
    init : function( widget ) {
      widget.setTabIndex( -1 );
      widget.setUserData( "nextTabIndex", 0 );
      widget.addEventListener( "changeTabIndex",
                               org.eclipse.swt.LinkUtil._onTabIndexChange );
    },

    destroy : function( widget ) {
      org.eclipse.swt.LinkUtil.clear( widget );
      if (widget.hasEventListeners( "changeTabIndex" ) ) {
        widget.removeEventListener( "changeTabIndex",
                                    org.eclipse.swt.LinkUtil._onTabIndexChange );
      }
    },

    clear : function( widget ) {
      if( widget && !widget.isDisposed() ) {
        var children = widget.getChildren();
        var child = children[0];
        while ( child ) {
          widget.remove( child );
          if (child.hasEventListeners( "mousedown" ) ) {
            child.removeEventListener( "mousedown",
                                       org.eclipse.swt.LinkUtil._onMouseDown );
          }
          if (child.hasEventListeners( "keydown" ) ) {
            child.removeEventListener( "keydown",
                                       org.eclipse.swt.LinkUtil._onKeyDown );
          }
          child.dispose();
          child = children[0];
        }
      }
    },

    setSelectionListener : function( widget, value ) {
      widget.setUserData( "widgetSelectedListener", value );
    },

    addText : function( widget, text ) {
      if( widget ) {
        var newChild = org.eclipse.swt.LinkUtil._createLabel( widget, text );
        newChild.setAppearance( "link-text" );
        newChild.setWrap( false );
        widget.add( newChild );
      }
    },

    addLink : function( widget, text, index ) {
      if( widget ) {
        uText = "<u>" + text + "</u>";
        var newChild = org.eclipse.swt.LinkUtil._createLabel( widget, uText );
        newChild.setUserData( "index", index );
        newChild.setAppearance( "link-href" );
        newChild.setWrap( false );
        widget.add( newChild );
        var tabIndex = widget.getUserData( "nextTabIndex" );
        newChild.setTabIndex( tabIndex++ );
        widget.setUserData( "nextTabIndex", tabIndex );
        newChild.addEventListener( "mousedown",
                                   org.eclipse.swt.LinkUtil._onMouseDown,
                                   newChild );
        newChild.addEventListener( "keydown",
                                   org.eclipse.swt.LinkUtil._onKeyDown,
                                   newChild );
      }
    },

    _createLabel : function( parent, text ) {
      // replace spaces with nbsp
      var replaced = text.replace( /\s/g, "&nbsp;" );
      var label = new qx.ui.basic.Label( replaced, null, "html" );
      return label;
    },

    _onMouseDown : function( evt ) {
      var parent = this.getParent();
      if( parent.getUserData( "widgetSelectedListener" ) ) {
        var req = org.eclipse.swt.Request.getInstance();
        var wm = org.eclipse.swt.WidgetManager.getInstance();
        var id = wm.findIdByWidget( parent );
        var index = this.getUserData( "index" );
        req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
        req.addEvent( "org.eclipse.swt.events.widgetSelected.index", index );
        req.send();
      }
    },

    _onKeyDown : function( evt ) {
      var keyId = evt.getKeyIdentifier();
      if ( keyId == "Enter" ) {
        var parent = this.getParent();
        if( parent.getUserData( "widgetSelectedListener" ) ) {
          var req = org.eclipse.swt.Request.getInstance();
          var wm = org.eclipse.swt.WidgetManager.getInstance();
          var id = wm.findIdByWidget( parent );
          var index = this.getUserData( "index" );
          req.addEvent( "org.eclipse.swt.events.widgetSelected", id );
          req.addEvent( "org.eclipse.swt.events.widgetSelected.index", index );
          req.send();
        }
      }
    },

    _onTabIndexChange : function( evt ) {
      var tabIndex = evt.getData();
      if( tabIndex >= 0 ) {
        var target = evt.getCurrentTarget();
        var children = target.getChildren();
        for( var i = 0; i < children.length; i++ ) {
          child = children[ i ];
          child.setTabIndex( tabIndex++ );
        }
        target.setUserData( "nextTabIndex", tabIndex );
        target.setTabIndex( -1 );
      }
    }
  }
});
