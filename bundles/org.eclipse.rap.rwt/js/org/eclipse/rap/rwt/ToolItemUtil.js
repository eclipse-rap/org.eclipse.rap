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
 * This class contains static functions for toolbar items.
 */
qx.OO.defineClass( "org.eclipse.rap.rwt.ToolItemUtil" );

qx.Class.createSeparator = function( id , parent, isFlat ) {
  var sep = new qx.ui.toolbar.Separator();
  var line = sep.getFirstChild();
  sep.setUserData( "line", line );
  if( isFlat ) {
    sep.addState( "rwt_FLAT" );
    line.addState( "rwt_FLAT" );
  }
  org.eclipse.rap.rwt.WidgetManager.getInstance().add( sep, id, false );
  sep.setParent( parent );
  parent.add( sep );
};

qx.Class.setControlForSeparator = function( id, parent, width, control ) {
  var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var sep = widgetManager.findWidgetById( id );
  if( width > 0 ) {
    sep.setWidth( width );
  }
  var oldControl = sep.getUserData( "control" );
  if( oldControl ) {
    sep.remove( oldControl );
    // TODO [rst] destroy old control?
  }
  sep.setUserData( "control", control );
  var line = sep.getUserData( "line" );
  if( control ) {
    var index = parent.indexOf( control );
    parent.removeAt( index );
    sep.add( control );
    line.setVisibility( false );
  } else {
    line.setVisibility( true );
  }
  parent.add( sep );
};

qx.Class.createRadio = function( id, parent, selected, neighbour ) {
  var radio = new qx.ui.toolbar.RadioButton();
  radio.setDisableUncheck( true );
  parent.add( radio );
  if( neighbour ){
    radio.radioManager = neighbour.radioManager;
  } else {
    radio.radioManager = new qx.manager.selection.RadioManager();
  }
  radio.radioManager.add( radio );
  if ( selected ) {
    radio.radioManager.setSelected( radio ) ;
  }
  org.eclipse.rap.rwt.WidgetManager.getInstance().add ( radio, id, false );
  radio.setParent( parent );
};

qx.Class.createPush = function( id, parent, isFlat ) {
  var push = new qx.ui.toolbar.Button();
  if( isFlat ) {
    push.addState( "rwt_FLAT" );
  }
  parent.add( push );
  org.eclipse.rap.rwt.WidgetManager.getInstance().add( push, id, false );
};

qx.Class.createDropDown = function( id, parent, isFlat ) {
  org.eclipse.rap.rwt.ToolItemUtil.createPush( id, parent, isFlat );
  var button = org.eclipse.rap.rwt.WidgetManager.getInstance().findWidgetById( id );
  var dropDown = new qx.ui.toolbar.Button( "", "widget/arrows/down.gif" );
  dropDown.setUserData( "buttonId", id );
  if( isFlat ) {
    dropDown.addState( "rwt_FLAT" );
  }
  parent.add( dropDown );
  var dropDownId = id + "_dropDown";
  org.eclipse.rap.rwt.WidgetManager.getInstance().add( dropDown, 
                                                       dropDownId, 
                                                       false );
  // Register enable listener that keeps enabled state of dropDown in sync
  // with the enabeled state of the actual button
  button.addEventListener( "changeEnabled", 
                           org.eclipse.rap.rwt.ToolItemUtil._onDropDownChangeEnabled );
};

org.eclipse.rap.rwt.ToolItemUtil._onDropDownChangeEnabled = function( evt ) {
  var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var button = evt.getTarget();
  var buttonId = widgetManager.findIdByWidget( button );
  var dropDownId = buttonId + "_dropDown";
  var dropDown = widgetManager.findWidgetById( dropDownId );
  dropDown.setEnabled( button.getEnabled() );
}

qx.Class.updateDropDownListener = function( id, remove ) {
  var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var dropDown = widgetManager.findWidgetById( id );
  var listener = org.eclipse.rap.rwt.ToolItemUtil._dropDownSelected;
  if( remove ) {
    dropDown.removeEventListener( "execute", listener );
  } else {
    dropDown.addEventListener( "execute",  listener );
  }
};

qx.Class.createCheck = function( id, parent ) {
  var push = new qx.ui.toolbar.CheckBox();
  parent.add( push );
  org.eclipse.rap.rwt.WidgetManager.getInstance().add( push, id, false );
};

qx.Class._dropDownSelected = function( evt ) { 
  var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var dropDown = evt.getTarget();
  var dropDownId = widgetManager.findIdByWidget( dropDown );
  var buttonId = dropDown.getUserData( "buttonId" );
  var button = widgetManager.findWidgetById( buttonId );
  var element = button.getElement();
  var left = qx.html.Location.getPageBoxLeft( element );
  var top = qx.html.Location.getPageBoxBottom( element );
  var req = org.eclipse.rap.rwt.Request.getInstance();
  org.eclipse.rap.rwt.EventUtil.doWidgetSelected( dropDownId, 
                                                  left, 
                                                  top, 
                                                  0,
                                                  0 );
};
