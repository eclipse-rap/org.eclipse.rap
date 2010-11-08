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

qx.Class.define("qx.ui.pageview.AbstractBar",
{
  type : "abstract",
  extend : qx.ui.layout.BoxLayout,




  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  construct : function()
  {
    this.base(arguments);

    this._manager = new qx.ui.selection.RadioManager;

    this.addEventListener("mousewheel", this._onmousewheel);
  },




  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    /**
     * Get the selection manager.
     *
     * @type member
     * @return {qx.ui.selection.RadioManager} the selection manager of the bar.
     */
    getManager : function() {
      return this._manager;
    },


    _lastDate : (new Date(0)).valueOf(),


    /**
     * TODOC
     *
     * @type member
     * @param e {Event} TODOC
     * @return {void}
     */
    _onmousewheel : function(e)
    {
      // prevents scrolling the parent
      e.preventDefault();
      e.stopPropagation();
      // Make it a bit lazier than it could be
      // Hopefully this is a better behaviour for fast scrolling users
      var vDate = (new Date).valueOf();

      if ((vDate - 50) < this._lastDate) {
        return;
      }

      this._lastDate = vDate;

      var vManager = this.getManager();
      var vItems = vManager.getEnabledItems();
      var vPos = vItems.indexOf(vManager.getSelected());

      if (this.getWheelDelta(e) > 0)
      {
        var vNext = vItems[vPos + 1];

        if (!vNext) {
          vNext = vItems[0];
        }
      }
      else if (vPos > 0)
      {
        var vNext = vItems[vPos - 1];

        if (!vNext) {
          vNext = vItems[0];
        }
      }
      else
      {
        vNext = vItems[vItems.length - 1];
      }

      vManager.setSelected(vNext);
    },


    /**
     * TODOC
     *
     * @type member
     * @param e {Event} TODOC
     * @return {var} TODOC
     */
    getWheelDelta : function(e) {
      return e.getWheelDelta();
    }
  },




  /*
  *****************************************************************************
     DESTRUCTOR
  *****************************************************************************
  */

  destruct : function() {
    this._disposeObjects("_manager");
  }
});
