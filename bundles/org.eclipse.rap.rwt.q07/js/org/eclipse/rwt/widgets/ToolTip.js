/*******************************************************************************
 * Copyright (c) 2009 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     EclipseSource - initial API and implementation
 ******************************************************************************/
 
qx.Class.define( "org.eclipse.rwt.widgets.ToolTip", {
  type : "singleton",
  extend : qx.ui.popup.ToolTip,
  
  construct : function() {
    this.base( arguments );
    this._atom._createLabel();
    this._atom.getLabelObject().setMode( "html" );
  },
  
  members : {
    
    _applyBoundToWidget : function( value, old ) {
      this.base( arguments, value, old );
      this._atom.setLabel( value.getUserData( "toolTipText" ) );
      var manager = qx.ui.popup.ToolTipManager.getInstance();
      manager.setCurrentToolTip( null );
    }
    
  }
} );
