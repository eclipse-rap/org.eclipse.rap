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
  extend : rwt.widgets.base.Popup,
  include : rwt.animation.VisibilityAnimationMixin,

  construct : function() {
    this.base( arguments );
    this._label = new rwt.widgets.base.MultiCellWidget( [ "label" ] );
    this._label.setParent( this );
    this._pointer = null;
    this._showTimer = new rwt.client.Timer();
    this._showTimer.addEventListener( "interval", this._onshowtimer, this );
    this._hideTimer = new rwt.client.Timer();
    this._hideTimer.addEventListener( "interval", this._onhidetimer, this );
    this.addEventListener( "mouseover", this._onmouseover );
    this.addEventListener( "mouseout", this._onmouseover );
    this._currentConfig = {};
    this.setRestrictToPageOnOpen( false );
  },

  statics : {

    setToolTipText : function( widget, value ) {
      var toolTip = rwt.widgets.base.WidgetToolTip.getInstance();
      if( value != null && value !== "" ) {
        var text = value;
        if( widget.getUserData( "toolTipMarkupEnabled" ) !== true ) {
          var EncodingUtil = rwt.util.Encoding;
          var text = EncodingUtil.escapeText( text, false );
          text = EncodingUtil.replaceNewLines( text, "<br/>" );
        }
        widget.setToolTipText( text );
        widget.dispatchSimpleEvent( "updateToolTip", widget );
      } else {
        widget.setToolTipText( null );
      }
    }

  },

  properties : {

    appearance : {
      refine : true,
      init : "widget-tool-tip"
    },

    mousePointerOffsetX : {
      check : "Integer",
      init : 1
    },

    mousePointerOffsetY : {
      check : "Integer",
      init : 20
    },

    boundToWidget : {
      check : "rwt.widgets.base.Widget",
      apply : "_applyBoundToWidget",
      nullable : true,
      init : null
    },

    pointers : {
      nullable : true,
      init : null,
      themeable : true
    },

    textAlign : {
      check : [ "left", "center", "right" ],
      init : "center",
      themeable : true,
      apply : "_applyTextAlign"
    }

  },

  members : {
    _minZIndex : 1e7,
    _targetDistance : 4,
    _isFocusRoot : false,

    updateText : function() {
      this._updateTextInternal();
      if( this.getText() && !this._showTimer.isEnabled() ) {
        this._onshowtimer();
      }
    },

    _applyElement : function( value, old ) {
      this.base( arguments, value, old );
    },

    _applyTextAlign : function( value, old ) {
      this._label.setHorizontalChildrenAlign( value );
    },

    _getPointerElement : function() {
      if( this._pointer == null ) {
        this._pointer = document.createElement( "div" );
        this._pointer.style.position = "absolute";
        this._pointer.style.backgroundRepeat = "no-repeat";
        rwt.html.Style.setPointerEvents( this._pointer, "none" );
        this.getElement().appendChild( this._pointer );
      }
      return this._pointer;
    },

    _applyBoundToWidget : function( value, old ) {
      var manager = rwt.widgets.util.ToolTipManager.getInstance();
      if( value ) {
        this.setParent( rwt.widgets.base.ClientDocument.getInstance() );
        this._config = rwt.widgets.util.ToolTipConfig.getConfig( this.getBoundToWidget() );
        this._showTimer.setInterval( this._config.appearDelay || 1000 );
        this._hideTimer.setInterval( this._config.disappearDelay || 200 );
        this.setAutoHide( this._config.autoHide === false ? false : true );
      } else if( old ) {
        this._config = {};
      }
    },

    _beforeAppear : function() {
      this.base( arguments );
      this._stopShowTimer();
    },

    _beforeDisappear : function() {
      this.base( arguments );
      this._stopHideTimer();
    },

    _startShowTimer : function() {
      if( this._config.appearOn === "enter" && this._allowQuickAppear() ) {
        this._onshowtimer();
      } else {
        if( !this._showTimer.getEnabled() ) {
          this._showTimer.start();
        }
      }
    },

    _allowQuickAppear : function() {
      var now = ( new Date() ).getTime();
      return this.isSeeable() || ( this._hideTimeStamp > 0 && ( now - this._hideTimeStamp ) < 300 );
    },

    _startHideTimer : function() {
      if( !this._hideTimer.getEnabled() ) {
        this._hideTimer.start();
      }
    },

    _stopShowTimer : function() {
      if( this._showTimer.getEnabled() ) {
        this._showTimer.stop();
      }
    },

    _stopHideTimer : function() {
      if( this._hideTimer.getEnabled() ) {
        this._hideTimer.stop();
      }
    },

    _onmouseover : function( event ) {
      if( this._pointer && this._pointer === event.getDomTarget() ) {
        return;
      }
      if( this._disappearAnimation && this._disappearAnimation.getDefaultRenderer().isActive() ) {
        this._disappearAnimation.getDefaultRenderer().setActive( false );
        this.hide();
        this._disappearAnimation.getDefaultRenderer().setActive( true );
      } else {
        this.hide();
      }
    },

    _handleMouseMove : function( event ) {
      if( this.getBoundToWidget() ) {
        if( this.isSeeable() ) {
          if( this._config.disappearOn === "move" ) {
            this._startHideTimer();
          } else if( this._config.disappearOn === "exitTargetBounds" ) {
            var bounds = this._getTargetBounds();
            var x = event.getPageX();
            var y = event.getPageY();
            if(    ( x < bounds.left ) || ( x > ( bounds.left + bounds.width ) )
                || ( y < bounds.top ) || ( y > ( bounds.top + bounds.height ) ) )
            {
              this._startHideTimer();
            }
          }
        } else {
          if( this._config.appearOn === "rest" ) {
            this._showTimer.restart(); // TODO: ignore small movements for start?
          }
        }
      }
    },

    _onshowtimer : function( e ) {
      this._stopShowTimer();
      this._stopHideTimer();
      this._updateTextInternal();
      if( this.getText() ) {
        if(    this._allowQuickAppear()
            && this._appearAnimation
            && this._appearAnimation.getDefaultRenderer().isActive()
            && !this.isSeeable() )
        {
          this._appearAnimation.getDefaultRenderer().setActive( false );
          this.show();
          this._appearAnimation.getDefaultRenderer().setActive( true );
        } else {
          this.show();
        }
        rwt.widgets.base.Widget.flushGlobalQueues(); // render new dimension
        this._afterAppearLayout();
        rwt.widgets.base.Widget.flushGlobalQueues(); // render position
      } else if( this.getBoundToWidget().requestToolTipText ) {
        this.getBoundToWidget().requestToolTipText();
      }
    },

    _onhidetimer : function(e) {
      if( this._config.appearOn === "rest" ) {
        this._showTimer.start();
      }
      return this.hide();
    },

    _afterAppearLayout : function() {
      var targetBounds = this._getTargetBounds();
      var docDimension = this._getDocumentDimension();
      var selfDimension = this._getOwnDimension();
      var newPosition = this._getPositionAfterAppear( targetBounds, docDimension, selfDimension );
      this.setLeft( newPosition[ 0 ] );
      this.setTop( newPosition[ 1 ] );
      var selfInnerBounds = {
        left : newPosition[ 0 ] + this._cachedBorderLeft,
        top : newPosition[ 1 ] + this._cachedBorderTop,
        width : selfDimension.width - this._cachedBorderLeft - this._cachedBorderRight,
        height : selfDimension.height - this._cachedBorderTop - this._cachedBorderBottom
      };
      this._renderPointer( targetBounds, selfInnerBounds );
    },

    _renderPointer : function( target, self ) {
      var pointers = this.getPointers();
      var enabled = this._config.position && this._config.position !== "mouse" && pointers;
      this._getPointerElement().style.display = "none";
      if( enabled ) {
        var direction = this._getDirection( target, self );
        if( direction === "up" && pointers[ 0 ] ) {
          this._renderPointerUp( target, self );
        } else if( direction === "down" && pointers[ 2 ] ) {
          this._renderPointerDown( target, self );
        } else if( direction === "left" && pointers[ 3 ] ) {
          this._renderPointerLeft( target, self );
        } else if( direction === "right" && pointers[ 1 ] ) {
          this._renderPointerRight( target, self );
        }
      }
    },

    _renderPointerUp : function( target, self ) {
      var pointer = this.getPointers()[ 0 ];
      var element = this._getPointerElement();
      rwt.html.Style.setBackgroundImage( element, pointer[ 0 ] );
      element.style.width = pointer[ 1 ] + "px";
      element.style.height = pointer[ 2 ] + "px";
      element.style.top = ( -1 * pointer[ 2 ] ) + "px";
      this._renderPointerHorizontalAlign( target, self, pointer );
      element.style.display = "";
    },

    _renderPointerDown : function( target, self ) {
      var pointer = this.getPointers()[ 2 ];
      var element = this._getPointerElement();
      rwt.html.Style.setBackgroundImage( element, pointer[ 0 ] );
      element.style.width = pointer[ 1 ] + "px";
      element.style.height = pointer[ 2 ] + "px";
      element.style.top = self.height + "px";
      this._renderPointerHorizontalAlign( target, self, pointer );
      element.style.display = "";
    },

    _renderPointerLeft : function( target, self ) {
      var pointer = this.getPointers()[ 3 ];
      var element = this._getPointerElement();
      var targetCenter = this._getBoundsCenter( target );
      rwt.html.Style.setBackgroundImage( element, pointer[ 0 ] );
      element.style.width = pointer[ 1 ] + "px";
      element.style.height = pointer[ 2 ] + "px";
      element.style.left = ( -1 * pointer[ 1 ] ) + "px";
      element.style.top = Math.round( targetCenter.top - self.top - pointer[ 2 ] / 2 ) + "px";
      element.style.display = "";
    },

    _renderPointerRight : function( target, self ) {
      var pointer = this.getPointers()[ 1 ];
      var element = this._getPointerElement();
      var targetCenter = this._getBoundsCenter( target );
      rwt.html.Style.setBackgroundImage( element, pointer[ 0 ] );
      element.style.width = pointer[ 1 ] + "px";
      element.style.height = pointer[ 2 ] + "px";
      element.style.left = self.width + "px";
      element.style.top = Math.round( targetCenter.top - self.top - pointer[ 2 ] / 2 ) + "px";
      element.style.display = "";
    },

    _renderPointerHorizontalAlign : function( target, self, pointer ) {
      var element = this._getPointerElement();
      if( this._config.position === "align-left" ) {
        var targetedLeft =   target.left
                           + this._targetDistance
                           + this._cachedBorderLeft
                           + this.getPaddingLeft();
        element.style.left = Math.round( targetedLeft - self.left ) + "px";
      } else {
        var targetCenter = this._getBoundsCenter( target );
        element.style.left = Math.round( targetCenter.left - self.left - pointer[ 1 ] / 2 ) + "px";
      }
    },

    _getDirection : function( target, self ) {
      var targetBetweenSelfX = target.left >= self.left && target.left <= ( self.left + self.width );
      var selfBetweenTargetX = self.left >= target.left && self.left <= ( target.left + target.width );
      if( targetBetweenSelfX || selfBetweenTargetX ) {
        return self.top > target.top ? "up" : "down";
      } else {
        return self.left > target.left ? "left" : "right";
      }
    },

    _getBoundsCenter : function( bounds ) {  // may return float!
      return {
        left : bounds.left + bounds.width / 2,
        top : bounds.top + bounds.height / 2
      };
    },

    _updateTextInternal : function() {
      this._label.setCellContent( 0, this.getBoundToWidget().getToolTipText() );
    },

    getText : function() {
      return this._label.getCellContent( 0 );
    },

    _getPositionAfterAppear : function( target, doc, self ) {
      var result;
      switch( this._config.position ) {
        case "horizontal-center":
          result = this._positionHorizontalCenter( target, doc, self );
        break;
        case "align-left":
          result = this._positionAlignLeft( target, doc, self );
        break;
        case "vertical-center":
          result = this._positionVerticalCenter( target, doc, self );
        break;
        default:
          result = this._positionMouseRelative( target, doc, self );
        break;
      }
      result[ 0 ] = Math.max( 0, Math.min( result[ 0 ], doc.width - self.width ) );
      result[ 1 ] = Math.max( 0, Math.min( result[ 1 ], doc.height - self.height ) );
      return result;
    },

    _positionMouseRelative : function( target, doc, self ) {
      return [
        rwt.event.MouseEvent.getPageX() + this.getMousePointerOffsetX(),
        rwt.event.MouseEvent.getPageY() + this.getMousePointerOffsetY()
      ];
    },

    _positionHorizontalCenter : function( target, doc, self ) {
      var left = this._getHorizontalOffsetCentered( target, self, doc );
      var top = this._getVerticalOffsetAutoByAbsolutePosition( target, self, doc );
      return [ left, top ];
    },

    _positionAlignLeft : function( target, doc, self ) {
      var left = this._getHorizontalOffsetAlignLeft( target, self, doc );
      var top = this._getVerticalOffsetAutoByAbsolutePosition( target, self, doc );
      return [ left, top ];
    },

    _positionVerticalCenter : function( target, doc, self ) {
      var left = this._getHorizontalOffsetAuto( target, self, doc );
      var top = this._getVerticalOffsetCentered( target, self, doc );
      return [ left, top ];
    },

    _getVerticalOffsetAutoByAbsolutePosition : function( target, self, doc ) {
      var top = this._getVerticalOffsetTop( target, self );
      if( top < 30 ) {
        top = this._getVerticalOffsetBottom( target, self );
      }
      return top;
    },

    _getVerticalOffsetTop : function( target, self ) {
      return target.top - self.height - this._getTargetDistance( "down" );
    },

    _getVerticalOffsetBottom : function( target, self ) {
      return target.top + target.height + this._getTargetDistance( "up" );
    },

    _getHorizontalOffsetCentered : function( target, self, doc ) {
      return Math.round( target.left + ( target.width / 2 ) - self.width / 2 );
    },

    _getHorizontalOffsetAlignLeft : function( target, self, doc ) {
      return target.left + this._targetDistance;
    },

    _getHorizontalOffsetAuto : function( target, self, doc ) {
      var leftSpace = target.left;
      var rightSpace = doc.width - leftSpace - target.width;
      if( leftSpace > rightSpace ) {
        return target.left - self.width - this._getTargetDistance( "right" );
      } else {
        return target.left + target.width + this._getTargetDistance( "left" );
      }
    },

    _getVerticalOffsetCentered : function( target, self, doc ) {
      return Math.round( target.top + ( target.height / 2 ) - self.height / 2 );
    },

    _getTargetBounds : function() {
      var widget = this.getBoundToWidget();
      var location = rwt.html.Location.get( widget.getElement() );
      var result = {
        "left" : location.left,
        "top" : location.top,
        "width" : widget.getBoxWidth(),
        "height" : widget.getBoxHeight()
      };
      if( widget.getToolTipTargetBounds ) {
        var innerBounds = widget.getToolTipTargetBounds();
        result.left += innerBounds.left;
        result.top += innerBounds.top;
        result.width = innerBounds.width;
        result.height = innerBounds.height;
      }
      return result;
    },

    _getDocumentDimension : function() {
      var doc = rwt.widgets.base.ClientDocument.getInstance();
      return { "width" : doc.getClientWidth(), "height" : doc.getClientHeight() };
    },

    _getOwnDimension : function() {
      return { "width" : this.getBoxWidth(), "height" : this.getBoxHeight() };
    },

    _getTargetDistance : function( direction ) {
      var overlap = typeof this._config.overlap === "number" ? this._config.overlap : 4;
      var pointers = this.getPointers();
      if( direction === "up" && pointers && pointers[ 0 ] ) {
        return pointers[ 0 ][ 2 ] - overlap;
      } else if( direction === "down" && pointers && pointers[ 2 ] ) {
        return pointers[ 2 ][ 2 ] - overlap;
      } else if( direction === "left" && pointers && pointers[ 3 ] ) {
        return pointers[ 3 ][ 1 ] - overlap;
      } else if( direction === "right" && pointers && pointers[ 1 ] ) {
        return pointers[ 1 ][ 1 ] - overlap;
      } else {
        return this._targetDistance;
      }
    }


  },

  destruct : function() {
    this._disposeObjects("_showTimer", "_hideTimer", "_label");
  }

} );
