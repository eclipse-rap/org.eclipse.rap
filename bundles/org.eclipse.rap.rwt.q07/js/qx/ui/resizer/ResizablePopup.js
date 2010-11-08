/*******************************************************************************
 *  Copyright: 2007, 2010 David Perez Carmona,
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
 * A popup that can be resized.
 */
qx.Class.define("qx.ui.resizer.ResizablePopup",
{
  extend   : qx.ui.popup.Popup,
  include  : qx.ui.resizer.MResizable,

  construct : function()
  {
    this.base(arguments);

    this.initMinWidth();
    this.initMinHeight();
    this.initWidth();
    this.initHeight();
  },




  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

  properties:
  {
    appearance :
    {
      refine : true,
      init : "resizer"
    },

    minWidth :
    {
      refine : true,
      init : "auto"
    },

    minHeight :
    {
      refine : true,
      init : "auto"
    },

    width :
    {
      refine : true,
      init : "auto"
    },

    height :
    {
      refine : true,
      init : "auto"
    }
  },






  members:
  {

    _changeWidth: function(value) {
      this.setWidth(value);
    },

    _changeHeight: function(value) {
      this.setHeight(value);
    },

    /**
     * @return {Widget}
     */
    _getResizeParent: function() {
      return this.getParent();
    },

    /**
     * @return {Widget}
     */
    _getMinSizeReference: function() {
      return this;
    }
  }
});
