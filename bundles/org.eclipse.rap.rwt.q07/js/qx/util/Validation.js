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
