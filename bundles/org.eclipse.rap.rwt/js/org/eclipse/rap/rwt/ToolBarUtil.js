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

qx.OO.defineClass( "org.eclipse.rap.rwt.ToolBarUtil" );

/**
 * Listener for change of property enabled, passes enablement to children
 * TODO [rst] Once qx can disable a ToolBar completely, this listener can be
 *      removed
 */
org.eclipse.rap.rwt.ToolBarUtil.enablementChanged = function( evt ) {
  var enabled = evt.getData();
  var items = this.getChildren();
  for( var i = 0; i < items.length; i++ ) {
    var item = items[ i ];
    item.setEnabled( enabled );
  }
};
