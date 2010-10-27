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
#use(qx.lang.Core)
#ignore(auto-require)

************************************************************************ */

/**
 * Helper functions for arrays.
 *
 * The native JavaScript Array is not modified by this class. However,
 * there are modifications to the native Array in {@link qx.lang.Core} for
 * browsers that do not support certain JavaScript 1.6 features natively .
 *
 * The string/array generics introduced in JavaScript 1.6 are supported by
 * {@link qx.lang.Generics}.
 */
qx.Class.define("qx.lang.Array",
{
  statics :
  {
    /**
     * Convert an arguments object into an array
     *
     * @type static
     * @param args {arguments} arguments object
     * @return {Array} a newly created array (copy) with the content of the arguments object.
     */
    fromArguments : function(args) {
      return Array.prototype.slice.call(args, 0);
    },

    /**
     * Expand shorthand definition to a four element list.
     * This is an utility function for padding/margin and all other shorthand handling.
     *
     * @type static
     * @param input {Array} array with one to four elements
     * @return {Array} an array with four elements
     */
    fromShortHand : function(input)
    {
      var len = input.length;

      if (len > 4 || len == 0) {
        this.error("Invalid number of arguments!");
      }

      var result = qx.lang.Array.copy(input);

      // Copy Values (according to the length)
      switch(len)
      {
        case 1:
          result[1] = result[2] = result[3] = result[0];
          break;

        case 2:
          result[2] = result[0];

          // no break here

        case 3:
          result[3] = result[1];
      }

      // Return list with 4 items
      return result;
    },


    /**
     * Return a copy of the given array
     *
     * @type static
     * @param arr {Array} the array to copy
     * @return {Array} copy of the array
     */
    copy : function(arr) {
      return arr.concat();
    },

    /**
     * Return the last element of an array
     *
     * @type static
     * @param arr {Array} the array
     * @return {var} the last element of the array
     */
    getLast : function(arr) {
      return arr[arr.length - 1];
    },


    /**
     * Return the first element of an array
     *
     * @type static
     * @param arr {Array} the array
     * @return {var|null} the first element of the array
     */
    getFirst : function(arr) {
      return arr[0];
    },


    /**
     * Insert an element at a given position into the array
     *
     * @type static
     * @param arr {Array} the array
     * @param obj {var} the element to insert
     * @param i {Integer} position where to insert the element into the array
     * @return {Array} the array
     */
    insertAt : function(arr, obj, i)
    {
      arr.splice(i, 0, obj);

      return arr;
    },

    /**
     * Remove an element from the array at the given index
     *
     * @type static
     * @param arr {Array} the array
     * @param i {Integer} index of the element to be removed
     * @return {var} The removed element.
     */
    removeAt : function(arr, i) {
      return arr.splice(i, 1)[0];
    },


    /**
     * Remove an element from the array
     *
     * @type static
     * @param arr {Array} the array
     * @param obj {var} element to be removed from the array
     * @return {Array} the removed element
     */
    remove : function(arr, obj)
    {
      var i = arr.indexOf(obj);

      if (i != -1)
      {
        arr.splice(i, 1);
        return obj;
      }
    },


    /**
     * Whether the array contains the given element
     *
     * @type static
     * @param arr {Array} the array
     * @param obj {var} object to look for
     * @return {Boolean} whether the array contains the element
     */
    contains : function(arr, obj) {
      return arr.indexOf(obj) != -1;
    }


  }
});
