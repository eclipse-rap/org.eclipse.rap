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
qx.OO.defineClass( "org.eclipse.rap.rwt.ComboUtil" );

/**
 * Fires a widgetSelected event if the list item wasn't laready selected.
 */
org.eclipse.rap.rwt.ComboUtil.widgetSelected = function( evt ) {
    var combo = evt.getTarget();
    var list = combo.getList();
    var listItem = list.getSelectedItem();
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var cboId = widgetManager.findIdByWidget( combo );
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addParameter( cboId + ".selectedItem", list.indexOf( listItem ) );
    
    var left = combo.getLeft();
    var top = combo.getTop();
    var width = combo.getWidth();
    var height = combo.getHeight();
    org.eclipse.rap.rwt.EventUtil.doWidgetSelected( cboId, 
                                                    left, 
                                                    top, 
                                                    width,
                                                    height );
};

/**
 * Creates a comboBox incl.  all listItems.
 */
org.eclipse.rap.rwt.ComboUtil.createComboBoxItems = function( id, items ) {
  var combo
    = org.eclipse.rap.rwt.WidgetManager.getInstance().findWidgetById( id );
  combo.removeAll();
  for( var i = 0; i < items.length; i++ ) {
    var listItem = new qx.ui.form.ListItem( items[ i ] );
    combo.add( listItem );
  }
  org.eclipse.rap.rwt.WidgetManager.getInstance().add( combo, id, false );
};
