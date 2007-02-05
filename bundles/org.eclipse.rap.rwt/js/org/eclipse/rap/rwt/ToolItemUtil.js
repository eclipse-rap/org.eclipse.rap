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


org.eclipse.rap.rwt.ToolItemUtil.createSeparator
  = function( id , parent )
{
  var push = new qx.ui.toolbar.Separator ();
  parent.add( push );
  org.eclipse.rap.rwt.WidgetManager.getInstance().add ( push, id, false );
  push.setParent( parent );
};

org.eclipse.rap.rwt.ToolItemUtil.setControlForSeparator
  = function( id , parent , width, control)
{
  var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var push = widgetManager.findWidgetById( id );
  if( width > 0 ) {
    push.setWidth( width );
  }
  if( control ) {
    if( width > 0 ) {
      control.setWidth( width );
    }
    var index = parent.indexOf( control );
    parent.removeAt( index );
    push.add( control );
  }
  parent.add( push );
};

org.eclipse.rap.rwt.ToolItemUtil.createRadio
  = function( id, parent, selected, neighbour )
{
  var radio = new qx.ui.toolbar.RadioButton();
  radio.setDisableUncheck( true );
  parent.add( radio );
  if( neighbour ){
    radio.radioManager = neighbour.radioManager;
  } else {
    radio.radioManager = new qx.manager.selection.RadioManager();
  }
  radio.radioManager.add ( radio );
  if ( selected ) {
    radio.radioManager.setSelected ( radio ) ;
  }
  org.eclipse.rap.rwt.WidgetManager.getInstance().add ( radio, id, false );
  radio.setParent( parent );
};

org.eclipse.rap.rwt.ToolItemUtil.createPush = function( id, parent, isFlat ) {
  var push = new qx.ui.toolbar.Button();
  if( isFlat ) {
    push.addState( "rwt_FLAT" );
  }
  parent.add( push );
  org.eclipse.rap.rwt.WidgetManager.getInstance().add( push, id, false );
};

org.eclipse.rap.rwt.ToolItemUtil.createDropDown = function( id, parent, isFlat ) 
{
  org.eclipse.rap.rwt.ToolItemUtil.createPush( id, parent, isFlat );
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
};

org.eclipse.rap.rwt.ToolItemUtil.updateDropDownListener = function( id, remove ) 
{
  var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var dropDown = widgetManager.findWidgetById( id );
  var listener = org.eclipse.rap.rwt.ToolItemUtil._dropDownSelected;
  if( remove ) {
    dropDown.removeEventListener( "execute", listener );
  } else {
    dropDown.addEventListener( "execute",  listener );
  }
};

org.eclipse.rap.rwt.ToolItemUtil.createCheck = function( id, parent ) {
  var push = new qx.ui.toolbar.CheckBox();
  parent.add( push );
  org.eclipse.rap.rwt.WidgetManager.getInstance().add( push, id, false );
};

org.eclipse.rap.rwt.ToolItemUtil._dropDownSelected = function( evt ) { 
evt.getTarget().debug( "CALLED!")  ;
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



