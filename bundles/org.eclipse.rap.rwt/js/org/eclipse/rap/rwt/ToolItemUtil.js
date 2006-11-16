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
 * This class contains static functions for combo.
 */
qx.OO.defineClass( "org.eclipse.rap.rwt.ToolItemUtil" );

/**
 * Creates a toolitem of type separator.
 */
org.eclipse.rap.rwt.ToolItemUtil.createToolItemSeparatorUtil
  = function( id , parent )
{
  var push = new qx.ui.toolbar.ToolBarSeparator ();
  parent.add(push);
  org.eclipse.rap.rwt.WidgetManager.getInstance().add ( push, id );
  push.setParent( parent );
};

/**
 * Creates a toolItem of type radio, registered to a RadioManager.
 */
org.eclipse.rap.rwt.ToolItemUtil.createToolItemRadioButton
  = function( id , parent , selected , neighbour )
{
  var radio = new qx.ui.toolbar.ToolBarRadioButton();
  parent.add(radio);
  if( neighbour ){
    radio.radioManager = neighbour.radioManager;
  } else {
    radio.radioManager = new qx.manager.selection.RadioManager();
  }
  radio.radioManager.add ( radio );
  if ( selected ) {
    radio.radioManager.setSelected ( radio ) ;
  }
  org.eclipse.rap.rwt.WidgetManager.getInstance().add ( radio, id );
  radio.setParent( parent );
};

/**
 * Creates a toolitem of type push.
 */
org.eclipse.rap.rwt.ToolItemUtil.createToolItemPush = function( id , parent ) {
  var push = new qx.ui.toolbar.ToolBarButton();
  parent.add(push);
  org.eclipse.rap.rwt.WidgetManager.getInstance().add ( push, id );
  push.setParent( parent );
};

/**
 * Creates a toolitem of type push for a drop down menu.
 */
org.eclipse.rap.rwt.ToolItemUtil.createToolItemPushMenu = function( id , parent ) {
  var push = new qx.ui.toolbar.ToolBarButton("V");
  parent.add(push);
  org.eclipse.rap.rwt.WidgetManager.getInstance().add ( push, id );
  push.setParent( parent );
};

/**
 * Creates a toolitem of type check.
 */
org.eclipse.rap.rwt.ToolItemUtil.createToolItemCheckUtil
  = function( id , parent )
{
  var push = new qx.ui.toolbar.ToolBarCheckBox();
  parent.add(push);
  org.eclipse.rap.rwt.WidgetManager.getInstance().add ( push, id );
  push.setParent( parent );
};

org.eclipse.rap.rwt.ToolItemUtil.addEventListenerForDropDownButton = function( id , eventType , listener ){ 
  var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var item = widgetManager.findWidgetById( id );
  item.addEventListener( "click" , org.eclipse.rap.rwt.ToolItemUtil.widgetSelected , null);
};

/**
 * Creates a toolitem of type check.
 */
org.eclipse.rap.rwt.ToolItemUtil.widgetSelected = function( evt ){ 
  var toolItem=evt.getTarget();
  var toolBar = toolItem.getParent();
  var index = toolBar.indexOf(toolItem);
  var neighbour = toolBar.getChildren ()[index-1];
  var el = neighbour.getElement();
  var x = qx.dom.DomLocation.getPageBoxLeft(el);
  var y = qx.dom.DomLocation.getPageBoxBottom(el);
  var item = evt.getTarget();
  var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var itemId = widgetManager.findIdByWidget( item );
  var req = org.eclipse.rap.rwt.Request.getInstance();
  req.addParameter( itemId + ".boundsMenu.x", x );
  req.addParameter( itemId + ".boundsMenu.y", y );
  var left = item.getLeft();
  var top = item.getTop();
  var width = item.getWidth();
  var height = item.getHeight();
  org.eclipse.rap.rwt.EventUtil.doWidgetSelected( itemId, 
                                                    left, 
                                                    top, 
                                                    width,
                                                    height );
};

