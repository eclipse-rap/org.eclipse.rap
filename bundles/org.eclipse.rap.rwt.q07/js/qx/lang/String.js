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
#ignore(auto-require)

************************************************************************ */

/**
 * String helper functions
 *
 * The native JavaScript String is not modified by this class. However,
 * there are modifications to the native String in {@link qx.lang.Core} for
 * browsers that do not support certain features.
 *
 * The additions implemented here may be added directly to native String by
 * a setting in {@link qx.lang.Prototypes}. This feature is not enabled by
 * default.
 *
 * The string/array generics introduced in JavaScript 1.6 are supported by
 * {@link qx.lang.Generics}.
 */
qx.Class.define("qx.lang.String",
{
  statics :
  {

    /**
     * removes white space from the left and the right side of a string
     *
     * @type static
     * @param str {String} the string to trim
     * @return {String} TODOC
     */
    trim : function(str) {
      return str.replace(/^\s+|\s+$/g, "");
    },

    /**
     * Pad a string up to a given length. By default, padding characters are added to the
     * left of the string.
     *
     * @type static
     * @param str {String} the string to pad
     * @param length {Integer} the final length of the string
     * @param ch {String} character used to fill up the string
     * @param addRight {Boolean} true to add the padding characters to the right of the string
     * @return {String} paddded string
     */
    pad : function(str, length, ch, addRight)
    {
      if (typeof ch === "undefined") {
        ch = "0";
      }

      var temp = "";

      for (var i=str.length; i<length; i++) {
        temp += ch;
      }

      if (addRight == true){
        return str + temp;
      } else {
        return temp + str;
      }
    },

    /**
     * Convert the first character of the string to upper case.
     *
     * @type static
     * @param str {String} the string
     * @return {String} the string with a upper case first character
     */
    toFirstUp : function(str) {
      return str.charAt(0).toUpperCase() + str.substr(1);
    },

    /**
     * Check whether the string contains a given substring
     *
     * @type static
     * @param str {String} the string
     * @param substring {String} substring to search for
     * @return {Boolean} whether the string contains the substring
     */
    contains : function(str, substring) {
      return str.indexOf(substring) != -1;
    },


    /**
     * Print a list of arguments using a format string
     * In the format string occurences of %n are replaced by the n'th element of the args list.
     * Example:
     * <pre class='javascript'>qx.lang.String.format("Hello %1, my name is %2", ["Egon", "Franz"]) == "Hello Egon, my name is Franz"</pre>
     *
     * @type static
     * @param pattern {String} format string
     * @param args {Array} array of arguments to insert into the format string
     * @return {String} TODOC
     */
    format : function(pattern, args)
    {
      var str = pattern;

      for (var i=0; i<args.length; i++) {
        str = str.replace(new RegExp("%" + (i + 1), "g"), args[i]);
      }

      return str;
    },


    /**
     * Escapes all chars that have a special meaning in regular expressions
     *
     * @type static
     * @param str {String} the string where to escape the chars.
     * @return {String} the string with the escaped chars.
     */
    escapeRegexpChars : function(str) {
      return str.replace(/([\\\.\(\)\[\]\{\}\^\$\?\+\*])/g, "\\$1");
    }

  }
});
