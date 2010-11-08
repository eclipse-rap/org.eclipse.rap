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

qx.Class.define("qx.ui.pageview.AbstractPage",
{
  type : "abstract",
  extend : qx.ui.layout.CanvasLayout,




  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  construct : function(vButton)
  {
    this.base(arguments);

    if (vButton !== undefined) {
      this.setButton(vButton);
    }

    this.initTop();
    this.initRight();
    this.initBottom();
    this.initLeft();
  },




  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

  properties :
  {
    top :
    {
      refine : true,
      init : 0
    },

    right :
    {
      refine : true,
      init : 0
    },

    bottom :
    {
      refine : true,
      init : 0
    },

    left :
    {
      refine : true,
      init : 0
    },

    /**
     * Make element displayed (if switched to true the widget will be created, if needed, too).
     *  Instead of qx.ui.core.Widget, the default is false here.
     */
    display :
    {
      refine: true,
      init : false
    },


    /** The attached tab of this page. */
    button :
    {
      check : "qx.ui.pageview.AbstractButton",
      apply : "_applyButton"
    }
  },




  /*
  *****************************************************************************
     APPLY ROUTINES
  *****************************************************************************
  */

  members :
  {
    _applyButton : function(value, old)
    {
      if (old) {
        old.setPage(null);
      }

      if (value) {
        value.setPage(this);
      }
    }
  }
});
