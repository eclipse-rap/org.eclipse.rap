/*******************************************************************************
 * Copyright (c) 2009, 2013 EclipseSource.
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
  },

  statics : {

    setToolTipText : function( widget, value ) {
      if( value != null && value !== "" ) {
        var EncodingUtil = rwt.util.Encoding;
        var text = EncodingUtil.escapeText( value, false );
        text = EncodingUtil.replaceNewLines( text, "<br/>" );
        widget.setUserData( "toolTipText", text );
        var toolTip = rwt.widgets.base.WidgetToolTip.getInstance();
        widget.setToolTip( toolTip );
        // make sure "boundToWidget" is initialized:
        if( toolTip.getParent() != null ) {
          if( toolTip.getBoundToWidget() == widget ) {
            toolTip.updateText( widget );
          }
        }
      } else {
        widget.setToolTip( null );
        widget.setUserData( "toolTipText", null );
      }
      widget.dispatchSimpleEvent( "changeToolTipText", widget ); // used by Synchronizer.js
    }

  },


  members : {

    _applyBoundToWidget : function( value, old ) {
      this.base( arguments, value, old );
      var manager = rwt.widgets.util.ToolTipManager.getInstance();
      manager.setCurrentToolTip( null );
    },

    show : function() {
      this.updateText( this.getBoundToWidget() );
      rwt.widgets.base.Widget.flushGlobalQueues();
      this.base( arguments );
    },

    updateText : function( widget ) {
      this._label.setCellContent( 0, widget.getUserData( "toolTipText" ) );
    },

    _positionAfterAppear : function( oldLeft, oldTop ) {
      var config = rwt.widgets.util.ToolTipConfig.getConfig( this.getBoundToWidget() );
      switch( config.position ) {
        case "horizontal-center":
          return this._positionHorizontalCenter( oldLeft, oldTop );
        default:
          return this.base( arguments, oldLeft, oldTop );
      }
    },

    _positionHorizontalCenter : function() {
      var target = this._getWidgetBounds();
      var left = Math.round( target[ 0 ] + ( target[ 2 ] / 2 ) - this.getWidthValue() / 2 );
      var top = target[ 1 ] + target[ 3 ] + 3;
      return [ left, top ];
    },

    _getWidgetBounds : function() {
      var widget = this.getBoundToWidget();
      var location = rwt.html.Location.get( widget.getElement() );
      return [
        location.left,
        location.top,
        widget.getWidthValue(),
        widget.getHeightValue()
      ];
    }

  }

} );
