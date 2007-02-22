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
qx.OO.defineClass(
  "org.eclipse.rap.rwt.widgets.TreeItem", 
  qx.ui.treefullcontrol.TreeFolder,
  function( parent ) {
    var row = qx.ui.treefullcontrol.TreeRowStructure.getInstance().newRow();
    // Indentation
    row.addIndent();
    // CheckBox
    this._checkBox = null;
    if( qx.lang.String.contains( parent.getTree().getRWTStyle(), "check" ) ) {
      this._checkBox = new qx.ui.form.CheckBox();
      this._checkBox.setTabIndex( -1 );
      this._checkBox.addEventListener( "changeChecked", this._onChangeChecked, this );
      row.addObject( this._checkBox, false );
    }
    // Image
    // TODO [rh] these dummy images are necessary since it is currently not 
    //      possible to change images when they were not set here initially
    row.addIcon( "org/eclipse/rap/rwt/widgets/tree/folder_closed.gif",
                 "org/eclipse/rap/rwt/widgets/tree/folder_open.gif" );
    // Text
    row.addLabel( "" );
    // Construct TreeItem
    qx.ui.treefullcontrol.TreeFolder.call( this, row );
    parent.add( this );
  }
);

qx.Proto.setChecked = function( value ){
  if( this._checkBox != null ) {
    this._checkBox.setChecked( value );    
  }
}

// TODO [rh] workaround for qx bug #260 (TreeFullControl doesn't update icon 
//      when it is changed
qx.Proto.setImage = function( image ) {
  this.setIcon( image );
  this.getIconObject().setSource( this._evalCurrentIcon() );
  this.setIconSelected( image );  
}

qx.Proto.dispose = function() {
  if( this.getDisposed() ) {
    return true;
  }
  if( this._checkBox != null ) {
    this._checkBox.removeEventListener( "changeChecked", this._onChangeChecked, this );
    this._checkBox.dispose();
  }
  return qx.ui.treefullcontrol.TreeFolder.prototype.dispose.call( this );
}

qx.Proto._onChangeChecked = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    var wm = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = wm.findIdByWidget( this );
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addParameter( id + ".checked", this._checkBox.getChecked() );
    if( this.getTree().hasWidgetSelectedListeners() ) {
      req.addEvent( "org.eclipse.rap.rwt.events.widgetSelected.detail", "check" );
      req.addEvent( "org.eclipse.rap.rwt.events.widgetSelected", id );
      req.send();
    }
  }
}