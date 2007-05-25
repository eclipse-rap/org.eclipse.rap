
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
 * This class contains static functions for the Combo widget.
 */
qx.Class.define( "org.eclipse.swt.ComboUtil", {

  statics : {
    
    onSelectionChanged : function( evt ) {
      // TODO [rst] This listener was also called on focus out, if no item was
      //      selected. This fix should work since combos cannot be deselected.
      if( evt.getData() != null ) {
        var combo = evt.getTarget();
        var list = combo.getList();
        var listItem = list.getSelectedItem();
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var cboId = widgetManager.findIdByWidget( combo );
        var req = org.eclipse.swt.Request.getInstance();
        req.addParameter( cboId + ".selectedItem", list.indexOf( listItem ) );
      }
    },

    onSelectionChangedAction : function( evt ) {
      // TODO [rst] This listener was also called on focus out, if no item was
      //      selected. This fix should work since combos cannot be deselected.
      if( evt.getData() != null ) {
        org.eclipse.swt.ComboUtil.onSelectionChanged( evt );      
        var widgetManager = org.eclipse.swt.WidgetManager.getInstance();
        var id = widgetManager.findIdByWidget( evt.getTarget() );
        org.eclipse.swt.EventUtil.doWidgetSelected( id, 0, 0, 0, 0 );
      }
    },
    
    /** Populates the Combo denoted by the given id with the items. */
    createComboBoxItems : function(id, items) {
      var combo 
        = org.eclipse.swt.WidgetManager.getInstance().findWidgetById( id );
      combo.removeAll();
      for( var i = 0; i < items.length; i++ ) {
        var item = new qx.ui.form.ListItem();
        item.setLabel( "(empty)" );
        item.getLabelObject().setMode( "html" );
        item.setLabel( items[ i ] );
        combo.add( item );
      }
      org.eclipse.swt.WidgetManager.getInstance().add( combo, id, false );
    },

    /** Selects a comboBox item. */
    select : function( id, index ) {
      var combo 
        = org.eclipse.swt.WidgetManager.getInstance().findWidgetById( id );
      var items = combo.getList().getChildren();
      var item = null;
      if( index >= 0 && index <= items.length - 1 ) {
        item = items[ index ];
      }
      combo.setSelected( item );
    }
  }
});
