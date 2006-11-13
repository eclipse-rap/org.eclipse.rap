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

