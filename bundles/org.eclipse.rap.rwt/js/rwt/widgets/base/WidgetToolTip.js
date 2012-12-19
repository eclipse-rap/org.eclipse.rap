/*******************************************************************************
 * Copyright (c) 2009, 2012 EclipseSource.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.qx.Class.define( "rwt.widgets.base.WidgetToolTip", {
  type : "singleton",
  extend : rwt.widgets.base.ToolTip,
  include : rwt.animation.VisibilityAnimationMixin,

  construct : function() {
    this.base( arguments );
    this._atom._createLabel();
    this._atom.getLabelObject().setMode( "html" );
  },

  members : {

    _applyBoundToWidget : function( value, old ) {
      this.base( arguments, value, old );
      this.updateText( value );
      var manager = rwt.widgets.util.ToolTipManager.getInstance();
      manager.setCurrentToolTip( null );
    },

    updateText : function( widget ) {
      this._atom.setLabel( widget.getUserData( "toolTipText" ) );
    }

  }
} );
