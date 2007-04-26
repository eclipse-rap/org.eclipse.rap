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
qx.OO.defineClass( "org.eclipse.swt.ComboUtil" );

/**
 * Fires a widgetSelected event if the list item wasn't laready selected.
 * TODO [rst] Update documentation: there is no logic that checks if the item
 *      was already selected.
 */
org.eclipse.swt.ComboUtil.widgetSelected = function( evt ) {
  var combo = evt.getTarget();
  // TODO [rst] This listener was also called on focus out, if no item was
  //      selected. This fix should work since combos cannot be deselected.
  if( evt.getData() != null ) {
    var list = combo.getList();
    var listItem = list.getSelectedItem();
    var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
    var cboId = widgetManager.findIdByWidget( combo );
    var req = org.eclipse.swt.Request.getInstance();
    req.addParameter( cboId + ".selectedItem", list.indexOf( listItem ) );
    var left = combo.getLeft();
    var top = combo.getTop();
    var width = combo.getWidth();
    var height = combo.getHeight();
    org.eclipse.swt.EventUtil.doWidgetSelected( cboId, 
                                                    left, 
                                                    top, 
                                                    width,
                                                    height );
  }
};

/**
 * Creates a comboBox incl.  all listItems.
 */
org.eclipse.swt.ComboUtil.createComboBoxItems = function( id, items ) {
  var combo
    = org.eclipse.swt.WidgetManager.getInstance().findWidgetById( id );
  combo.removeAll();
  for( var i = 0; i < items.length; i++ ) {
    var listItem = new qx.ui.form.ListItem( items[ i ] );
    combo.add( listItem );
  }
  org.eclipse.swt.WidgetManager.getInstance().add( combo, id, false );
};

/**
 * Selects a comboBox item.
 */
org.eclipse.swt.ComboUtil.selectComboBoxItem = function( id, i ) {
  var combo
    = org.eclipse.swt.WidgetManager.getInstance().findWidgetById( id );
  var items = combo.getList().getChildren();
  if( i >= 0 && i <= items.length ) {
    combo.setSelected( items[ i ] );
  }
};

/**
 * Listener for change of property enabled, passes enablement to children.
 * TODO [rst] Once qx can disable a Combo completely (including gray-out),
 *  this listener can be removed
 */
org.eclipse.swt.ComboUtil.enablementChanged = function( evt ) {
  var enabled = evt.getData();
  var items = this.getChildren();
  for( var i = 0; i < items.length; i++ ) {
    var item = items[ i ];
    item.setEnabled( enabled );
  }
};
