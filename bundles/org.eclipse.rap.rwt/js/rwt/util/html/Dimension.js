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
 *    EclipseSource - adaptation for the Eclipse Rich Ajax Platform
 ******************************************************************************/


/**
 * <pre>
 * +-Outer----------------------------------------+
 * |  Margin                                      |
 * |  +-Box------------------------------+        |
 * |  |  Border (+ Scrollbar)            |        |
 * |  |  +-Area--------------------+     |        |
 * |  |  |  Padding                |     |        |
 * |  |  |  +-Inner----------+     |     |        |
 * |  |  |  |                |     |     |        |
 * |  |  |  +----------------+     |     |        |
 * |  |  +-------------------------+     |        |
 * |  +----------------------------------+        |
 * +----------------------------------------------+
 * </pre>
 */

rwt.qx.Class.define("rwt.util.html.Dimension",
{
  /*
  *****************************************************************************
     STATICS
  *****************************************************************************
  */

  statics :
  {


    /**
     * TODOC
     *
     * @type static
     * @param el {Element} TODOC
     * @return {var} TODOC
     */
    getBoxWidth : function(el) {
      return el.offsetWidth;
    },


    /**
     * TODOC
     *
     * @type static
     * @param el {Element} TODOC
     * @return {var} TODOC
     */
    getBoxHeight : function(el) {
      return el.offsetHeight;
    },


    /**
     * TODOC
     *
     * @type static
     * @param el {Element} TODOC
     * @return {void}
     * @signature function(el)
     */
    getAreaWidth : rwt.util.Variant.select("qx.client",
    {
      "gecko" : function(el)
      {
        // 0 in clientWidth could mean both: That it is really 0 or
        // that the element is not rendered by the browser and
        // therefore it is 0, too
        // In Gecko based browsers there is sometimes another
        // behaviour: The clientHeight is equal to the border
        // sum. This is normally not correct and so we
        // fix this value with a more complex calculation.
        // (Mozilla/5.0 (Windows; U; Windows NT 5.1; de-DE; rv:1.7.6) Gecko/20050223 Firefox/1.0.1)
        var Style = rwt.util.html.Style;
        if(    el.clientWidth !== 0
            && el.clientWidth !== ( Style.getBorderLeft( el ) + Style.getBorderRight( el ) )
        ) {
          return el.clientWidth;
        } else {
          return rwt.util.html.Dimension.getBoxWidth(el) - rwt.util.html.Dimension.getInsetLeft(el) - rwt.util.html.Dimension.getInsetRight(el);
        }
      },

      "default" : function(el)
        {
          // 0 in clientWidth could mean both: That it is really 0 or
          // that the element is not rendered by the browser and
          // therefore it is 0, too
          return el.clientWidth !== 0 ? el.clientWidth : (rwt.util.html.Dimension.getBoxWidth(el) - rwt.util.html.Dimension.getInsetLeft(el) - rwt.util.html.Dimension.getInsetRight(el));
        }
    }),


    /**
     * TODOC
     *
     * @type static
     * @param el {Element} TODOC
     * @return {void}
     * @signature function(el)
     */
    getAreaHeight : rwt.util.Variant.select("qx.client",
    {
      "gecko" : function(el)
      {
        // 0 in clientHeight could mean both: That it is really 0 or
        // that the element is not rendered by the browser and
        // therefore it is 0, too
        // In Gecko based browsers there is sometimes another
        // behaviour: The clientHeight is equal to the border
        // sum. This is normally not correct and so we
        // fix this value with a more complex calculation.
        // (Mozilla/5.0 (Windows; U; Windows NT 5.1; de-DE; rv:1.7.6) Gecko/20050223 Firefox/1.0.1)
        if (el.clientHeight !== 0 && el.clientHeight !== (rwt.util.html.Style.getBorderTop(el) + rwt.util.html.Style.getBorderBottom(el))) {
          return el.clientHeight;
        } else {
          return rwt.util.html.Dimension.getBoxHeight(el) - rwt.util.html.Dimension.getInsetTop(el) - rwt.util.html.Dimension.getInsetBottom(el);
        }
      },

      "default" : function(el)
      {
        // 0 in clientHeight could mean both: That it is really 0 or
        // that the element is not rendered by the browser and
        // therefore it is 0, too
        return el.clientHeight !== 0 ? el.clientHeight : (rwt.util.html.Dimension.getBoxHeight(el) - rwt.util.html.Dimension.getInsetTop(el) - rwt.util.html.Dimension.getInsetBottom(el));
      }
    }),


    // Insets
    /**
     * TODOC
     *
     * @type static
     * @param el {Element} TODOC
     * @return {void}
     * @signature function(el)
     */
    getInsetLeft : rwt.util.Variant.select("qx.client",
    {
      "mshtml" : function(el) {
        return el.clientLeft;
      },

      "default" : function(el) {
        return rwt.util.html.Style.getBorderLeft(el);
      }
    }),


    /**
     * TODOC
     *
     * @type static
     * @param el {Element} TODOC
     * @return {void}
     * @signature function(el)
     */
    getInsetTop : rwt.util.Variant.select("qx.client",
    {
      "mshtml" : function(el) {
        return el.clientTop;
      },

      "default" : function(el) {
        return rwt.util.html.Style.getBorderTop(el);
      }
    }),


    /**
     * TODOC
     *
     * @type static
     * @param el {Element} TODOC
     * @return {void}
     * @signature function(el)
     */
    getInsetRight : rwt.util.Variant.select("qx.client",
    {
      "mshtml" : function(el)
      {
        if (rwt.util.html.Style.getStyleProperty(el, "overflowY") === "hidden" || el.clientWidth === 0) {
          return rwt.util.html.Style.getBorderRight(el);
        }

        return Math.max(0, el.offsetWidth - el.clientLeft - el.clientWidth);
      },

      "default" : function(el)
      {
        // Alternative method if clientWidth is unavailable
        // clientWidth == 0 could mean both: unavailable or really 0
        if (el.clientWidth === 0)
        {
          var ov = rwt.util.html.Style.getStyleProperty(el, "overflow");
          var sbv = ov == "scroll" || ov == "-moz-scrollbars-vertical" ? 16 : 0;
          return Math.max(0, rwt.util.html.Style.getBorderRight(el) + sbv);
        }

        return Math.max(0, el.offsetWidth - el.clientWidth - rwt.util.html.Style.getBorderLeft(el));
      }
    }),


    /**
     * TODOC
     *
     * @type static
     * @param el {Element} TODOC
     * @return {void}
     * @signature function(el)
     */
    getInsetBottom : rwt.util.Variant.select("qx.client",
    {
      "mshtml" : function(el)
      {
        if (rwt.util.html.Style.getStyleProperty(el, "overflowX") === "hidden" || el.clientHeight === 0) {
          return rwt.util.html.Style.getBorderBottom(el);
        }

        return Math.max(0, el.offsetHeight - el.clientTop - el.clientHeight);
      },

      "default" : function(el)
      {
        // Alternative method if clientHeight is unavailable
        // clientHeight == 0 could mean both: unavailable or really 0
        if (el.clientHeight === 0)
        {
          var ov = rwt.util.html.Style.getStyleProperty(el, "overflow");
          var sbv = ov == "scroll" || ov == "-moz-scrollbars-horizontal" ? 16 : 0;
          return Math.max(0, rwt.util.html.Style.getBorderBottom(el) + sbv);
        }

        return Math.max(0, el.offsetHeight - el.clientHeight - rwt.util.html.Style.getBorderTop(el));
      }
    }),

    /**
     * TODOC
     *
     * @type static
     * @param el {Element} TODOC
     * @return {var} TODOC
     */
    getScrollBarSizeRight : function(el) {
      return rwt.util.html.Dimension.getInsetRight(el) - rwt.util.html.Style.getBorderRight(el);
    },


    /**
     * TODOC
     *
     * @type static
     * @param el {Element} TODOC
     * @return {var} TODOC
     */
    getScrollBarSizeBottom : function(el) {
      return rwt.util.html.Dimension.getInsetBottom(el) - rwt.util.html.Style.getBorderBottom(el);
    }

  }
});
