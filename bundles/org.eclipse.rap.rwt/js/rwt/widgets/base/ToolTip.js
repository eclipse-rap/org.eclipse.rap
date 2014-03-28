/*******************************************************************************
 * Copyright (c) 2004, 2014 1&1 Internet AG, Germany, http://www.1und1.de,
 *                          EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Remote Application Platform
 ******************************************************************************/

rwt.qx.Class.define("rwt.widgets.base.ToolTip", {
  extend : rwt.widgets.base.Popup,

  construct : function() {
    this.base( arguments );
    this._label = new rwt.widgets.base.MultiCellWidget( [ "label" ] );
    this._label.setParent( this );
    this._showTimer = new rwt.client.Timer(this.getShowInterval());
    this._showTimer.addEventListener("interval", this._onshowtimer, this);
    this._hideTimer = new rwt.client.Timer(this.getHideInterval());
    this._hideTimer.addEventListener("interval", this._onhidetimer, this);
    this.addEventListener("mouseover", this._onmouseover);
    this.addEventListener("mouseout", this._onmouseover);
  },

  properties : {

    appearance : {
      refine : true,
      init : "widget-tool-tip"
    },

    hideOnHover : {
      check : "Boolean",
      init : true
    },

    mousePointerOffsetX : {
      check : "Integer",
      init : 1
    },

    mousePointerOffsetY : {
      check : "Integer",
      init : 20
    },

    showInterval : {
      check : "Integer",
      init : 500,
      apply : "_applyShowInterval"
    },

    hideInterval : {
      check : "Integer",
      init : 500,
      apply : "_applyHideInterval"
    },

    boundToWidget : {
      check : "rwt.widgets.base.Widget",
      apply : "_applyBoundToWidget"
    }

  },


  members : {
    _minZIndex : 1e7,

    _applyHideInterval : function(value, old) {
      this._hideTimer.setInterval(value);
    },

    _applyShowInterval : function(value, old) {
      this._showTimer.setInterval(value);
    },

    _applyBoundToWidget : function(value, old) {
      if (value) {
        this.setParent(value.getTopLevelWidget());
      } else if (old) {
        this.setParent(null);
      }
    },

    _beforeAppear : function() {
      this.base(arguments);
      this._stopShowTimer();
    },

    _beforeDisappear : function() {
      this.base(arguments);
      this._stopHideTimer();
    },

    _afterAppear : function() {
      this.base( arguments );
      this._afterAppearLayout();
    },

    _afterAppearLayout : function() {
      var oldLeft = this.getLeft();
      var oldTop = this.getTop();
      var newPosition = this._getPositionAfterAppear( oldLeft, oldTop );
      if( newPosition[ 0 ] !== oldLeft || newPosition[ 1 ] !== oldTop ) {
        rwt.client.Timer.once( function() {
          this.setLeft( newPosition[ 0 ] );
          this.setTop( newPosition[ 1 ] );
        }, this, 0 );
      }
    },

    _getPositionAfterAppear : function( oldLeft, oldTop ) {
      var result = [ oldLeft, oldTop ];
      if( this.getRestrictToPageOnOpen() ) {
        var left   = (this._wantedLeft == null) ? this.getLeft() : this._wantedLeft;
        var top    = this.getTop();
        var width  = this.getBoxWidth();
        var height = this.getBoxHeight();
        var doc = rwt.widgets.base.ClientDocument.getInstance();
        var docWidth = doc.getClientWidth();
        var docHeight = doc.getClientHeight();
        var restrictToPageLeft = parseInt( this.getRestrictToPageLeft(), 10 );
        var restrictToPageRight = parseInt( this.getRestrictToPageRight(), 10 );
        var restrictToPageTop = parseInt( this.getRestrictToPageTop(), 10 );
        var restrictToPageBottom = parseInt( this.getRestrictToPageBottom(), 10 );
        var mouseX = rwt.event.MouseEvent.getPageX();
        var mouseY = rwt.event.MouseEvent.getPageY();
        // NOTE: We check right and bottom first, because top and left should have
        //       priority, when both sides are violated.
        if (left + width > docWidth - restrictToPageRight) {
          left = docWidth - restrictToPageRight - width;
        }
        if (top + height > docHeight - restrictToPageBottom) {
          top = docHeight - restrictToPageBottom - height;
        }
        if (left < restrictToPageLeft) {
          left = restrictToPageLeft;
        }
        if (top < restrictToPageTop) {
          top = restrictToPageTop;
        }

        // REPAIR: If mousecursor /within/ newly positioned popup, move away.
        if (left <= mouseX && mouseX <= left+width &&
            top <= mouseY && mouseY <= top+height){
            // compute possible movements in all four directions
            var deltaYdown = mouseY - top;
            var deltaYup = deltaYdown - height;
            var deltaXright = mouseX - left;
            var deltaXleft = deltaXright - width;
            var violationUp = Math.max(0, restrictToPageTop - (top+deltaYup));
            var violationDown = Math.max(0, top+height+deltaYdown - (docHeight-restrictToPageBottom));
            var violationLeft = Math.max(0, restrictToPageLeft - (left+deltaXleft));
            var violationRight = Math.max(0, left+width+deltaXright - (docWidth-restrictToPageRight));
            var possibleMovements = [// (deltaX, deltaY, violation)
                [0, deltaYup,    violationUp], // up
                [0, deltaYdown,  violationDown], // down
                [deltaXleft, 0,  violationLeft], // left
                [deltaXright, 0, violationRight] // right
            ];

            possibleMovements.sort(function(a, b){
                // first sort criterion: overlap/clipping - fewer, better
                // second criterion: combined movements - fewer, better
                return a[2]-b[2] || (Math.abs(a[0]) + Math.abs(a[1])) - (Math.abs(b[0]) + Math.abs(b[1]));
            });

            var minimalNonClippingMovement = possibleMovements[0];
            left = left + minimalNonClippingMovement[0];
            top = top + minimalNonClippingMovement[1];
        }
        result = [ left, top ];
      }
      return result;
    },

    _startShowTimer : function() {
      if (!this._showTimer.getEnabled()) {
        this._showTimer.start();
      }
    },

    _startHideTimer : function() {
      if(!this._hideTimer.getEnabled()) {
        this._hideTimer.start();
      }
    },

    _stopShowTimer : function() {
      if (this._showTimer.getEnabled()) {
        this._showTimer.stop();
      }
    },

    _stopHideTimer : function() {
      if (this._hideTimer.getEnabled()) {
        this._hideTimer.stop();
      }
    },

    _onmouseover : function(e) {
      if (this.getHideOnHover()) {
        this.hide();
      }
    },

    _onshowtimer : function(e) {
      this.setLeft(rwt.event.MouseEvent.getPageX() + this.getMousePointerOffsetX());
      this.setTop(rwt.event.MouseEvent.getPageY() + this.getMousePointerOffsetY());
      this.show();
    },

    _onhidetimer : function(e) {
      return this.hide();
    }

  },

  destruct : function() {
    this._disposeObjects("_showTimer", "_hideTimer", "_label");
  }

} );
