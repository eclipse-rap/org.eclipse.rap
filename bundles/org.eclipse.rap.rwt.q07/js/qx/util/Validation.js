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
     * Andreas Ecker (ecker)

************************************************************************ */

/* ************************************************************************

#module(core)

************************************************************************ */

/**
 * Collection of validation methods.
 *
 * All methods use the strict comparison operators as all modern
 * browsers (needs support for JavaScript 1.3) support this.
 *
 * http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Operators:Comparison_Operators
 */
qx.Class.define("qx.util.Validation",
{
  statics :
  {

    /**
     * Whether a value is a valid number. Valid numbers are:
     * <ul>
     *   <li>type is number</li>
     *   <li>not NaN</li>
     * </ul>
     *
     * @type static
     * @param v {var} the value to validate.
     * @return {Boolean} whether the variable is valid
     */
    isValidNumber : function(v) {
      return typeof v === "number" && !isNaN(v);
    },

    /**
     * Whether a value is valid string. Valid strings are:
     * <ul>
     *   <li>type is string</li>
     *   <li>not an empty string</li>
     * </ul>
     *
     * @type static
     * @param v {var} the value to validate.
     * @return {Boolean} whether the variable is valid
     */
    isValidString : function(v) {
      return typeof v === "string" && v !== "";
    }

  }
});
