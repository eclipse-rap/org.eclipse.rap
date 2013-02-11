/*******************************************************************************
 * Copyright: 2004, 2012 1&1 Internet AG, Germany, http://www.1und1.de,
 *                       and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Remote Application Platform
 ******************************************************************************/

/** This manages ToolTip instances */
rwt.qx.Class.define("rwt.widgets.util.ToolTipManager",
{
  type : "singleton",
  extend : rwt.util.ObjectManager,





  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

  properties :
  {
    /** Holds the current ToolTip instance */
    currentToolTip :
    {
      check : "rwt.widgets.base.ToolTip",
      nullable : true,
      apply : "_applyCurrentToolTip"
    }
  },




  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    /*
    ---------------------------------------------------------------------------
      APPLY ROUTINES
    ---------------------------------------------------------------------------
    */

    /**
     * TODOC
     *
     * @type member
     * @param value {var} Current value
     * @param old {var} Previous value
     * @return {void | Boolean} TODOC
     */
    _applyCurrentToolTip : function(value, old)
    {
      // Return if the new tooltip is a child of the old one
      if (old && old.contains(value)) {
        return;
      }

      // If old tooltip existing, hide it and clear widget binding
      if (old && !old.isDisposed())
      {
        old.hide();

        old._stopShowTimer();
        old._stopHideTimer();
      }

      // If new tooltip is not null, set it up and start the timer
      if (value) {
        value._startShowTimer();
      }
    },




    /*
    ---------------------------------------------------------------------------
      EVENT INTERFACE: MOUSE
    ---------------------------------------------------------------------------
    */

    handleMouseEvent : function( event ) {
      var type = event.getType();
      if( type === "mouseover" ) {
        this._handleMouseOver( event );
      } else if ( type === "mouseout" ) {
        this._handleMouseOut( event );
      }
    },

    /**
     * Searches for the tooltip of the target widget. If any tooltip instance
     * is found this instance is bound to the target widget and the tooltip is
     * set as {@link #currentToolTip}
     *
     * @type member
     * @param e {rwt.event.MouseEvent} mouseOver event
     * @return {void}
     */
    _handleMouseOver : function( e ) {
      var vTarget = e.getTarget();
      var vToolTip;

      // Allows us to use DOM Nodes as tooltip target :)
      if (!(vTarget instanceof rwt.widgets.base.Widget) && vTarget.nodeType == 1) {
        vTarget = rwt.event.EventHandlerUtil.getTargetObject(vTarget);
      }

      // Search first parent which has a tooltip
      while (vTarget != null && !(vToolTip = vTarget.getToolTip())) {
        vTarget = vTarget.getParent();
      }

      // Bind tooltip to widget
      if (vToolTip != null) {
        vToolTip.setBoundToWidget(vTarget);
      }

      // Set Property
      this.setCurrentToolTip(vToolTip);
    },


    /**
     * Resets the property {@link #currentToolTip} if there was a
     * tooltip and no new one is created.
     *
     * @type member
     * @param e {rwt.event.MouseEvent} mouseOut event
     * @return {void}
     */
    _handleMouseOut : function( e ) {
      var vTarget = e.getTarget();
      var vRelatedTarget = e.getRelatedTarget();

      var vToolTip = this.getCurrentToolTip();

      // If there was a tooltip and
      // - the destination target is the current tooltip
      //   or
      // - the current tooltip contains the destination target
      if (vToolTip && (vRelatedTarget == vToolTip || vToolTip.contains(vRelatedTarget))) {
        return;
      }

      // If the destination target exists and the target contains it
      if (vRelatedTarget && vTarget && vTarget.contains(vRelatedTarget)) {
        return;
      }

      // If there was a tooltip and there is no new one
      if (vToolTip && !vRelatedTarget) {
        this.setCurrentToolTip(null);
      }
    },




    /*
    ---------------------------------------------------------------------------
      EVENT INTERFACE: FOCUS
    ---------------------------------------------------------------------------
    */

    handleFocus : function( e ) {
      // nothing to do
    },


    /**
     * Reset the property {@link #currentToolTip} if the
     * current tooltip is the tooltip of the target widget.
     *
     * @type member
     * @param e {rwt.event.FocusEvent} blur event
     * @return {void}
     */
    handleBlur : function(e)
    {
      var vTarget = e.getTarget();

      if (!vTarget) {
        return;
      }

      var vToolTip = this.getCurrentToolTip();

      // Only set to null if blured widget is the
      // one which has created the current tooltip
      if (vToolTip && vToolTip == vTarget.getToolTip()) {
        this.setCurrentToolTip(null);
      }
    }
  }
});
