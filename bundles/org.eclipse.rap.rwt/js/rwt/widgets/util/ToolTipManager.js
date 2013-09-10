/*******************************************************************************
 * Copyright (c) 2004, 2013 1&1 Internet AG, Germany, http://www.1und1.de,
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

(function(){

var getToolTip = function() {
  return rwt.widgets.base.WidgetToolTip.getInstance();
};


rwt.qx.Class.define("rwt.widgets.util.ToolTipManager", {
  type : "singleton",
  extend : rwt.util.ObjectManager,


  properties : {
    currentToolTipTarget : {
      nullable : true,
      apply : "_applyCurrentToolTipTarget"
    }
  },


  members : {

    /**
     * TODOC
     *
     * @type member
     * @param value {var} Current value
     * @param old {var} Previous value
     * @return {void | Boolean} TODOC
     */
    _applyCurrentToolTipTarget : function(value, old) {

      // If old tooltip existing, hide it and clear widget binding
      if (old)
      {
        getToolTip()._stopShowTimer();
        getToolTip()._startHideTimer();
      }

      // If new tooltip is not null, set it up and start the timer
      if (value) {
        getToolTip().setBoundToWidget( value );
        getToolTip()._startShowTimer();
      }
    },

    handleMouseEvent : function( event ) {
      var type = event.getType();
      if( type === "mouseover" ) {
        this._handleMouseOver( event );
      } else if ( type === "mouseout" ) {
        this._handleMouseOut( event );
      } else if ( type === "mousemove" ) {
        this._handleMouseMove( event );
      }
    },

    _handleMouseOver : function( e ) {
      var vTarget = e.getTarget();
      var text = null;
      // Allows us to use DOM Nodes as tooltip target :)
      if (!(vTarget instanceof rwt.widgets.base.Widget) && vTarget.nodeType == 1) {
        vTarget = rwt.event.EventHandlerUtil.getTargetObject(vTarget);
      }
      while (vTarget != null && !(text = vTarget.getToolTipText())) {
        vTarget = vTarget.getParent();
      }
      this.setCurrentToolTipTarget( vTarget );
    },


    _handleMouseOut : function( e ) {
      var vTarget = e.getTarget();
      var vRelatedTarget = e.getRelatedTarget();

      var tTarget = this.getCurrentToolTipTarget();

      // If there was a tooltip and
      // - the destination target is the current tooltip
      //   or
      // - the current tooltip contains the destination target
      if (tTarget && (vRelatedTarget == getToolTip() || getToolTip().contains(vRelatedTarget))) {
        return;
      }

      // If the destination target exists and the target contains it
      if (vRelatedTarget && vTarget && vTarget.contains(vRelatedTarget)) {
        return;
      }

      // If there was a tooltip and there is no new one
      if (tTarget && !vRelatedTarget) {
        this.setCurrentToolTipTarget( null );
      }
    },

    _handleMouseMove : function( e ) {
      if( this.getCurrentToolTipTarget() ) {
        getToolTip()._handleMouseMove( e );
      }
    },

    handleFocus : function( e ) {
      // nothing to do
    },

    handleBlur : function(e)
    {
      var vTarget = e.getTarget();

      if (!vTarget) {
        return;
      }

      var tTarget = this.getCurrentToolTipTarget();

      // Only set to null if blured widget is the
      // one which has created the current tooltip
      if (tTarget === vTarget) {
        this.setCurrentToolTipTarget(null);
      }
    }
  }

} );


}());
