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
 * This singleton manages multiple instances of qx.ui.embed.Iframe.
 * <p>
 * The problem: When dragging over an iframe then all mouse events will be
 * passed to the document of the iframe, not the main document.
 * <p>
 * The solution: In order to be able to track mouse events over iframes, this
 * manager will block all iframes during a drag with a glasspane.
 */
qx.Class.define("qx.ui.embed.IframeManager",
{
  type : "singleton",
  extend : qx.util.manager.Object,




  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  construct : function() {
    this.base(arguments);

    this._blocked = {};
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
      METHODS
    ---------------------------------------------------------------------------
    */

    /**
     * TODOC
     *
     * @type member
     * @param evt {Event} TODOC
     * @return {void}
     */
    handleMouseDown : function(evt)
    {
      var iframeMap = this._blockData = qx.lang.Object.copy(this.getAll());
      // console.debug("Blocking frames: " + qx.lang.Object.getLength(iframeMap));

      for (var key in iframeMap) {
        iframeMap[key].block();
      }
    },


    /**
     * TODOC
     *
     * @type member
     * @param evt {Event} TODOC
     * @return {void}
     */
    handleMouseUp : function(evt)
    {
      var iframeMap = this._blockData;
      // console.debug("Releasing frames: " + qx.lang.Object.getLength(iframeMap));

      for (var key in iframeMap) {
        iframeMap[key].release();
      }
    }
  },


  /*
    ---------------------------------------------------------------------------
      DESTRUCTOR
    ---------------------------------------------------------------------------
  */
  destruct : function()
  {
    this._disposeFields("_blocked", "_blockData");
  }
});
