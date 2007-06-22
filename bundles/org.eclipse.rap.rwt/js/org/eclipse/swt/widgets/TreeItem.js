
/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

/**
 * This class encapulates the qx.ui.treefullcontrol.TreeFolder to make it more
 * suitable for usage in RWT.
 */
qx.Class.define( "org.eclipse.swt.widgets.TreeItem", {
  extend : qx.ui.treefullcontrol.TreeFolder,

  construct : function( parentItem ) {
    var row = qx.ui.treefullcontrol.TreeRowStructure.getInstance().newRow();
    // Indentation
    row.addIndent();
    // CheckBox
    this._checkBox = null;
    if ( qx.lang.String.contains( parentItem.getTree().getRWTStyle(), "check" ) ) 
    {
      this._checkBox = new qx.ui.form.CheckBox();
      this._checkBox.setTabIndex( -1 );
      this._checkBox.addEventListener( "changeChecked", this._onChangeChecked, this );
      this._checkBox.addEventListener( "dblclick", this._onCheckBoxDblClick, this );
      row.addObject( this._checkBox, false );
    }
    // Image
    // TODO [rh] these dummy images are necessary since it is currently not
    //      possible to change images when they were not set here initially
    row.addIcon( "widget/tree/folder_closed.gif", 
                 "widget/tree/folder_open.gif" );
    // Text
    row.addLabel( "" );
    // Construct TreeItem
    this.base( arguments, row );
    this.addEventListener( "click", this._onClick, this );
    this.addEventListener( "dblclick", this._onDblClick, this );
    parentItem.add( this );
    this.getLabelObject().setMode( "html" );
  },
  
  destruct : function() {
    if( this._checkBox != null ) {
      this._checkBox.removeEventListener( "changeChecked", this._onChangeChecked, this );
      this._checkBox.removeEventListener( "dblclick", this._onCheckBoxDblClick, this );
      this._checkBox.dispose();
    }
    this.removeEventListener( "click", this._onClick, this );
    this.removeEventListener( "dblclick", this._onDblClick, this );
  },

  members : {
    setChecked : function( value ) {
      if( this._checkBox != null ) {
        this._checkBox.setChecked( value );
      }
    },
    
    setSelection : function( value, focus ) {
      var manager = this.getTree().getManager();
      manager.setItemSelected( this, value );
      if( focus ) {
        manager.setLeadItem( this );
      }
    },

    // TODO [rh] workaround for qx bug #260 (TreeFullControl doesn't update icon
    //      when it is changed)
    setImage : function( image ) {
      this.setIcon( image );
      this.getIconObject().setSource( image );
      this.setIconSelected( image );
    },

    /*
     * Notifies tree of clicks on the item's area.
     */
    _onClick : function( evt ) {
      if( this._checkEventTarget( evt ) ) {
        this.getTree()._notifyItemClick( this );
      }
    },

    /*
     * Notifies tree of double clicks in the item's area.
     */
    _onDblClick : function( evt ) {
      if( this._checkEventTarget( evt ) ) {
        this.getTree()._notifyItemDblClick( this );
      }
    },

    /*
     * Checks if a given event should be handled or not. Returns true if the
     * event's original target is either the icon or the label.
     */
    _checkEventTarget : function( evt ) {
      var result = false;
      var target = evt.getOriginalTarget();
      if( target && target == this._iconObject || target == this._labelObject ) {
        result = true;
      }
      return result;
    },

    /*
     * Prevent double clicks on check boxes from being propageted to the tree.
     */
    _onCheckBoxDblClick : function( evt ) {
      evt.stopPropagation();
    },

    _onChangeChecked : function( evt ) {
      var wm = org.eclipse.swt.WidgetManager.getInstance();
      var id = wm.findIdByWidget( this );
      var req = org.eclipse.swt.Request.getInstance();
      req.addParameter( id + ".checked", this._checkBox.getChecked() );
      this.getTree()._notifyChangeItemCheck( this );
    },

    /*
     * Prevent auto expand on click
     */
    _onmouseup : function( evt ) {
    }
  }
});
