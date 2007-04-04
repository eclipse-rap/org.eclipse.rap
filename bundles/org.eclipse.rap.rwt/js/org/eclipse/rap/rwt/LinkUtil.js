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
qx.OO.defineClass( "org.eclipse.rap.rwt.LinkUtil" );

qx.Class.init = function( widget ) {
  widget.setTabIndex( -1 );
  widget.setUserData( "nextTabIndex", 0 );
  widget.addEventListener( "changeTabIndex",
                           org.eclipse.rap.rwt.LinkUtil._onTabIndexChange );
}

qx.Class.destroy = function( widget ) {
  org.eclipse.rap.rwt.LinkUtil.clear( widget );
  if( widget.hasEventListeners( "changeTabIndex" ) ) {
    widget.removeEventListener( "changeTabIndex",
                                org.eclipse.rap.rwt.LinkUtil._onTabIndexChange );
  }
}

qx.Class.clear = function( widget ) {
  if( widget && ! widget.isDisposed() ) {
    var children = widget.getChildren();    
    var child = children[ 0 ];
    while( child ) {
      widget.remove( child );
      if( child.hasEventListeners( "mousedown" ) ) {
        child.removeEventListener( "mousedown",
                                   org.eclipse.rap.rwt.LinkUtil._onMouseDown );
      }
      if( child.hasEventListeners( "keydown" ) ) {
        child.removeEventListener( "keydown",
                                   org.eclipse.rap.rwt.LinkUtil._onKeyDown );
      }
      child.dispose();
      child = children[ 0 ];
    }
  }
}

qx.Class.setSelectionListener = function( widget, value ) {
  widget.setUserData( "widgetSelectedListener", value );
}

qx.Class.addText = function( widget, text ) {
  if( widget ) {
    var newChild = org.eclipse.rap.rwt.LinkUtil._createLabel( widget, text );
    newChild.setAppearance( "link-text" );
    // TODO [rst] setAppearance() resets property wrap !
    newChild.setWrap( false );
    widget.add( newChild );
  }
}

qx.Class.addLink = function( widget, text, index ) {
  if( widget ) {
    var newChild = org.eclipse.rap.rwt.LinkUtil._createLabel( widget, text );
    newChild.setAppearance( "link-ref" );
    newChild.setUserData( "index", index );
    // TODO [rst] setAppearance() resets property wrap !
    newChild.setWrap( false );
    widget.add( newChild );
    var tabIndex = widget.getUserData( "nextTabIndex" );
    newChild.setTabIndex( tabIndex++ );
    widget.setUserData( "nextTabIndex", tabIndex );
    newChild.addEventListener( "mousedown",
                               org.eclipse.rap.rwt.LinkUtil._onMouseDown,
                               newChild );
    newChild.addEventListener( "keydown",
                               org.eclipse.rap.rwt.LinkUtil._onKeyDown,
                               newChild );
  }
}

qx.Class._createLabel = function( parent, text ) {
  var label = new qx.ui.basic.Label( text );
  if( text.match(/^\s+/) ) {
    label.setPaddingLeft( 3 );
  }
  if( text.match(/\S\s+$/) ) {
    label.setPaddingRight( 3 );
  }
  label.setWrap( false );
  return label;
}

qx.Class._onMouseDown = function( evt ) {
  var parent = this.getParent();
  if( parent.getUserData( "widgetSelectedListener" ) ) {
    var req = org.eclipse.rap.rwt.Request.getInstance();
    var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = wm.findIdByWidget( parent );
    var index = this.getUserData( "index" );
    req.addEvent( "org.eclipse.rap.rwt.events.widgetSelected", id );
    req.addEvent( "org.eclipse.rap.rwt.events.widgetSelected.index", index );
    req.send();
  }
}

qx.Class._onKeyDown = function( evt ) {
  var keyId = evt.getKeyIdentifier();
  if( keyId == "Enter" ) {
    var parent = this.getParent();  
    if( parent.getUserData( "widgetSelectedListener" ) ) {
      var req = org.eclipse.rap.rwt.Request.getInstance();
      var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
      var id = wm.findIdByWidget( parent );
      var index = this.getUserData( "index" );
      req.addEvent( "org.eclipse.rap.rwt.events.widgetSelected", id );
      req.addEvent( "org.eclipse.rap.rwt.events.widgetSelected.index", index );
      req.send();
    }
  }
}

qx.Class._onTabIndexChange = function( evt ) {
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
