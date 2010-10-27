/* ************************************************************************

   qooxdoo - the new era of web development

   http://qooxdoo.org

   Copyright:
     2004-2008 1&1 Internet AG, Germany, http://www.1und1.de

   License:
     LGPL: http://www.gnu.org/licenses/lgpl.html
     EPL: http://www.eclipse.org/org/documents/epl-v10.php
     See the LICENSE file in the project's top-level directory for details.

   Authors:
     * Sebastian Werner (wpbasti)

************************************************************************ */

/**
 * Contains methods to control and query the element's overflow properties.
 */
qx.Class.define("qx.bom.element.Overflow",
{
  /*
  *****************************************************************************
     STATICS
  *****************************************************************************
  */

  statics :
  {

    // Mozilla notes (http://developer.mozilla.org/en/docs/Mozilla_CSS_Extensions):
    // -moz-scrollbars-horizontal: Indicates that horizontal scrollbars should
    //    always appear and vertical scrollbars should never appear.
    // -moz-scrollbars-vertical: Indicates that vertical scrollbars should
    //    always appear and horizontal scrollbars should never appear.
    // -moz-scrollbars-none: Indicates that no scrollbars should appear but
    //    the element should be scrollable from script. (This is the same as
    //    hidden, and has been since Mozilla 1.6alpha.)
    //
    // Also a lot of interesting bugs:
    // * https://bugzilla.mozilla.org/show_bug.cgi?id=42676
    // * https://bugzilla.mozilla.org/show_bug.cgi?id=47710
    // * https://bugzilla.mozilla.org/show_bug.cgi?id=235524

    /**
     * Returns the computed value of the horizontal overflow
     *
     * @type static
     * @signature function(element, mode)
     * @param element {Element} DOM element to query
     * @param mode {Number} Choose one of the modes {@link qx.bom.element.Style#COMPUTED_MODE},
     *   {@link qx.bom.element.Style#CASCADED_MODE}, {@link qx.bom.element.Style#LOCAL_MODE}.
     *   The computed mode is the default one.
     * @return {String} computed overflow value
     */
    getX : qx.core.Variant.select("qx.client",
    {
      // gecko support differs
      "gecko" : qx.core.Client.getVersion() < 1.8 ?

      // older geckos do not support overflowX
      // it's also more safe to translate hidden to -moz-scrollbars-none
      // because of issues in older geckos
      function(element, mode)
      {
        var overflow = qx.bom.element.Style.get(element, "overflow", mode, false);

        if (overflow === "-moz-scrollbars-none") {
          overflow = "hidden";
        }

        return overflow;
      } :

      // gecko >= 1.8 supports overflowX, too
      function(element, mode) {
        return qx.bom.element.Style.get(element, "overflowX", mode, false);
      },

      // opera support differs
      "opera" : qx.core.Client.getVersion() < 9.5 ?

      // older versions of opera have no support for splitted overflow properties.
      function(element, mode) {
        return qx.bom.element.Style.get(element, "overflow", mode, false);
      } :

      // opera >=9.5 supports overflowX, too
      function(element, mode) {
        return qx.bom.element.Style.get(element, "overflowX", mode, false);
      },

      // use native overflowX property
      "default" : function(element, mode) {
        return qx.bom.element.Style.get(element, "overflowX", mode, false);
      }
    }),




    /**
     * Returns the computed value of the vertical overflow
     *
     * @type static
     * @signature function(element, mode)
     * @param element {Element} DOM element to query
     * @param mode {Number} Choose one of the modes {@link qx.bom.element.Style#COMPUTED_MODE},
     *   {@link qx.bom.element.Style#CASCADED_MODE}, {@link qx.bom.element.Style#LOCAL_MODE}.
     *   The computed mode is the default one.
     * @return {String} computed overflow value
     */
    getY : qx.core.Variant.select("qx.client",
    {
      // gecko support differs
      "gecko" : qx.core.Client.getVersion() < 1.8 ?

      // older geckos do not support overflowY
      // it's also more safe to translate hidden to -moz-scrollbars-none
      // because of issues in older geckos
      function(element, mode)
      {
        var overflow = qx.bom.element.Style.get(element, "overflow", mode, false);

        if (overflow === "-moz-scrollbars-none") {
          overflow = "hidden";
        }

        return overflow;
      } :

      // gecko >= 1.8 supports overflowY, too
      function(element, mode) {
        return qx.bom.element.Style.get(element, "overflowY", mode, false);
      },

      // opera support differs
      "opera" : qx.core.Client.getVersion() < 9.5 ?

      // older versions of opera have no support for splitted overflow properties.
      function(element, mode) {
        return qx.bom.element.Style.get(element, "overflow", mode, false);
      } :

      // opera >=9.5 supports overflowY, too
      function(element, mode) {
        return qx.bom.element.Style.get(element, "overflowY", mode, false);
      },

      // use native overflowY property
      "default" : function(element, mode) {
        return qx.bom.element.Style.get(element, "overflowY", mode, false);
      }
    })

  }
});
