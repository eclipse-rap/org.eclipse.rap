/*******************************************************************************
 *  Copyright: 2004, 2012 1&1 Internet AG, Germany, http://www.1und1.de,
 *                        and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Remote Application Platform
 ******************************************************************************/


rwt.qx.Class.define("rwt.html.Window",
{
  /*
  *****************************************************************************
     STATICS
  *****************************************************************************
  */

  statics :
  {
    /**
     * Get the inner width of the given browser window
     *
     * @type static
     * @param vWindow {window} browser window
     * @return {Integer} the window's inner width
     * @signature function(vWindow)
     */
    getInnerWidth  : rwt.util.Variant.select("qx.client",
    {
      "mshtml|webkit" : function(vWindow)
      {
        if (vWindow.document.documentElement && vWindow.document.documentElement.clientWidth) {
          return vWindow.document.documentElement.clientWidth;
        } else if (vWindow.document.body) {
          return vWindow.document.body.clientWidth;
        }

        return 0;
      },

      "default" : function(vWindow) {
        return vWindow.innerWidth;
      }
    }),


    /**
     * Get the inner height of the given browser window
     *
     * @type static
     * @param vWindow {window} browser window
     * @return {Integer} the window's inner height
     * @signature function(vWindow)
     */
    getInnerHeight : rwt.util.Variant.select("qx.client",
    {
      "mshtml|webkit" : function(vWindow)
      {
        if (vWindow.document.documentElement && vWindow.document.documentElement.clientHeight) {
          return vWindow.document.documentElement.clientHeight;
        } else if (vWindow.document.body) {
          return vWindow.document.body.clientHeight;
        }

        return 0;
      },

      "default" : function(vWindow) {
        return vWindow.innerHeight;
      }
    }),


    /**
     * Get the left scroll position of the given browser window
     *
     * @type static
     * @param vWindow {window} browser window
     * @return {Integer} the window's left scroll position
     * @signature function(vWindow)
     */
    getScrollLeft  : rwt.util.Variant.select("qx.client",
    {
      "mshtml" : function(vWindow)
      {
        if (vWindow.document.documentElement && vWindow.document.documentElement.scrollLeft) {
          return vWindow.document.documentElement.scrollLeft;
        } else if (vWindow.document.body) {
          return vWindow.document.body.scrollTop;
        }

        return 0;
      },

      "default" : function(vWindow) {
        return vWindow.document.body.scrollLeft;
      }
    }),


    /**
     * Get the top scroll position of the given browser window
     *
     * @type static
     * @param vWindow {window} browser window
     * @return {Integer} the window's top scroll position
     * @signature function(vWindow)
     */
    getScrollTop   : rwt.util.Variant.select("qx.client",
    {
      "mshtml" : function(vWindow)
      {
        if (vWindow.document.documentElement && vWindow.document.documentElement.scrollTop) {
          return vWindow.document.documentElement.scrollTop;
        } else if (vWindow.document.body) {
          return vWindow.document.body.scrollTop;
        }

        return 0;
      },

      "default" : function(vWindow) {
        return vWindow.document.body.scrollTop;
      }
    })
  }
});
