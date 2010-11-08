/*******************************************************************************
 *  Copyright: 2004, 2010 1&1 Internet AG, Germany, http://www.1und1.de,
 *                        and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Rich Ajax Platform
 ******************************************************************************/

/**
 * @appearance tab-view
 */
qx.Class.define("qx.ui.pageview.tabview.TabView",
{
  extend : qx.ui.pageview.AbstractPageView,




  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  construct : function() {
    this.base(arguments, qx.ui.pageview.tabview.Bar, qx.ui.pageview.tabview.Pane);
  },




  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

  properties :
  {
    appearance :
    {
      refine : true,
      init : "tab-view"
    },

    orientation :
    {
      refine : true,
      init : "vertical"
    },

    alignTabsToLeft :
    {
      check : "Boolean",
      init : true,
      apply : "_applyAlignTabsToLeft"
    },

    placeBarOnTop :
    {
      check : "Boolean",
      init : true,
      apply : "_applyPlaceBarOnTop"
    }
  },




  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    /**
     * TODOC
     *
     * @type member
     * @param value {var} Current value
     * @param old {var} Previous value
     */
    _applyAlignTabsToLeft : function(value, old)
    {
      var vBar = this._bar;

      vBar.setHorizontalChildrenAlign(value ? "left" : "right");

      // force re-apply of states for all tabs
      vBar._addChildrenToStateQueue();
    },


    /**
     * TODOC
     *
     * @type member
     * @param value {var} Current value
     * @param old {var} Previous value
     */
    _applyPlaceBarOnTop : function(value, old)
    {
      // This does not work if we use flexible zones
      // this.setReverseChildrenOrder(!value);
      var vBar = this._bar;

      // move bar around
      if (value) {
        vBar.moveSelfToBegin();
      } else {
        vBar.moveSelfToEnd();
      }

      // force re-apply of states for all tabs
      vBar._addChildrenToStateQueue();
    }
  }
});
