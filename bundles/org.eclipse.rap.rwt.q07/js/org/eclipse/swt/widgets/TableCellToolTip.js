/*******************************************************************************
 * Copyright (c) 2009, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.swt.widgets.TableCellToolTip", {
  extend : qx.ui.popup.ToolTip,
  include : org.eclipse.rwt.FadeAnimationMixin,

  construct : function( controlId ) {
    this.base( arguments );
    this._controlId = controlId;
    this._itemId = null;
    this._columnIndex = -1;
  },

  members : {

    _onshowtimer : function( evt ) {
      this._stopShowTimer();
      this._requestCellToolTipText();
    },

    setText : function( text ) {
      if( text && text != "" ) {
        this.getAtom().setLabel( text );
        this.setLeft(   qx.event.type.MouseEvent.getPageX()
                      + this.getMousePointerOffsetX());
        this.setTop(   qx.event.type.MouseEvent.getPageY()
                     + this.getMousePointerOffsetY());
        this.show();
      }
    },

    setCell : function( itemId, columnIndex ) {
      if( this._itemId != itemId || this._columnIndex != columnIndex ) {
        this._itemId = itemId;
        this._columnIndex = columnIndex;
        this.hide();
        if(   !this._showTimer.getEnabled()
            && itemId != null
            && columnIndex != -1 )
        {
          this._showTimer.start();
        }
      }
    },

    _requestCellToolTipText : function() {
      if( this._itemId != null && this._columnIndex != -1 ) {
        var req = org.eclipse.swt.Request.getInstance();
        req.addEvent( "org.eclipse.swt.events.cellToolTipTextRequested",
                      this._controlId );
        var cell = this._itemId + "," + this._columnIndex;
        req.addParameter( "org.eclipse.swt.events.cellToolTipTextRequested.cell",
                          cell );
        req.send();
      }
    }

  }
});
