/*******************************************************************************
 * Copyright: 2004, 2012 1&1 Internet AG, Germany, http://www.1und1.de,
 *                       and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Rich Ajax Platform
 ******************************************************************************/

/**
 * Generic escaping and unescaping of DOM strings.
 *
 * {@link rwt.util.html.String} for (un)escaping of HTML strings.
 * {@link qx.xml.String} for (un)escaping of XML strings.
 */
qx.Class.define("rwt.util.html.String",
{
  statics :
  {
    /**
     * generic escaping method
     *
     * @type static
     * @param str {String} string to escape
     * @param charCodeToEntities {Map} entity to charcode map
     * @return {String} escaped string
     * @signature function(str, charCodeToEntities)
     */
    escapeEntities : rwt.util.Variant.select("qx.client",
    {
      // IE and Opera:
      //  - use [].join() to build strings
      "mshtml": function(str, charCodeToEntities)
      {
        var entity, result = [];

        for (var i=0, l=str.length; i<l; i++)
        {
          var chr = str.charAt(i);
          var code = chr.charCodeAt(0);

          if (charCodeToEntities[code]) {
            entity = "&" + charCodeToEntities[code] + ";";
          }
          else
          {
            if (code > 0x7F) {
              entity = "&#" + code + ";";
            } else {
              entity = chr;
            }
          }

          result[result.length] = entity;
        }

        return result.join("");
      },

      // other browsers:
      //  - use += to build strings
      "default": function(str, charCodeToEntities)
      {
        var entity, result = "";

        for (var i=0, l=str.length; i<l; i++)
        {
          var chr = str.charAt(i);
          var code = chr.charCodeAt(0);

          if (charCodeToEntities[code]) {
            entity = "&" + charCodeToEntities[code] + ";";
          }
          else
          {
            if (code > 0x7F) {
              entity = "&#" + code + ";";
            } else {
              entity = chr;
            }
          }

          result += entity;
        }

        return result;
      }
    }),


    /**
     * generic unescaping method
     *
     * @type static
     * @param str {String} string to unescape
     * @param entitiesToCharCode {Map} charcode to entity map
     * @return {var} TODOC
     */
    unescapeEntities : function( str, entitiesToCharCode ) {
      return str.replace( /&[#\w]+;/gi, function( entity ) {
        var chr = entity;
        var entity = entity.substring( 1, entity.length - 1 );
        var code = entitiesToCharCode[ entity ];
        if( code ) {
          chr = String.fromCharCode( code );
        } else {
          if( entity.charAt( 0 ) === '#' ) {
            if( entity.charAt(1).toUpperCase() === 'X' ) {
              code = entity.substring( 2 );
              // match hex number
              if( code.match( /^[0-9A-Fa-f]+$/gi ) ) {
                chr = String.fromCharCode( parseInt( code, 16 ) );
              }
            } else {
              code = entity.substring( 1 );
              // match integer
              if( code.match( /^\d+$/gi ) ) {
                chr = String.fromCharCode( parseInt( code, 10 ) );
              }
            }
          }
        }
        return chr;
      } );
    },

    /**
     * Remove HTML/XML tags from a string
     * Example:
     * <pre class='javascript'>rwt.util.html.String.stripTags("&lt;h1>Hello&lt;/h1>") == "Hello"</pre>
     *
     * @type static
     * @param str {String} string containing tags
     * @return {String} the string with stripped tags
     */
    stripTags : function(str) {
      return str.replace(/<\/?[^>]+>/gi, "");
    },

    /**
     * Escapes the characters in a <code>String</code> using HTML entities.
     *
     * For example: <tt>"bread" & "butter"</tt> => <tt>&amp;quot;bread&amp;quot; &amp;amp; &amp;quot;butter&amp;quot;</tt>.
     * Supports all known HTML 4.0 entities, including funky accents.
     *
     * * <a href="http://www.w3.org/TR/REC-html32#latin1">HTML 3.2 Character Entities for ISO Latin-1</a>
     * * <a href="http://www.w3.org/TR/REC-html40/sgml/entities.html">HTML 4.0 Character entity references</a>
     * * <a href="http://www.w3.org/TR/html401/charset.html#h-5.3">HTML 4.01 Character References</a>
     * * <a href="http://www.w3.org/TR/html401/charset.html#code-position">HTML 4.01 Code positions</a>
     *
     * @type static
     * @param str {String} the String to escape
     * @return {String} a new escaped String
     * @see #unescape
     */
    escape : function(str) {
      return rwt.util.html.String.escapeEntities(str, rwt.util.html.Entity.FROM_CHARCODE);
    },


    /**
     * Unescapes a string containing entity escapes to a string
     * containing the actual Unicode characters corresponding to the
     * escapes. Supports HTML 4.0 entities.
     *
     * For example, the string "&amp;lt;Fran&amp;ccedil;ais&amp;gt;"
     * will become "&lt;Fran&ccedil;ais&gt;"
     *
     * If an entity is unrecognized, it is left alone, and inserted
     * verbatim into the result string. e.g. "&amp;gt;&amp;zzzz;x" will
     * become "&gt;&amp;zzzz;x".
     *
     * @type static
     * @param str {String} the String to unescape, may be null
     * @return {var} a new unescaped String
     * @see #escape
     */
    unescape : function(str) {
      return rwt.util.html.String.unescapeEntities(str, rwt.util.html.Entity.TO_CHARCODE);
    },


    /**
     * Converts a plain text string into HTML.
     * This is similar to {@link #escape} but converts new lines to
     * <tt>&lt:br&gt:</tt> and preserves whitespaces.
     *
     * @type static
     * @param str {String} the String to convert
     * @return {String} a new converted String
     * @see #escape
     */
    fromText : function( str ) {
      return rwt.util.html.String.escape(str).replace( /( {2}|\n)/g, function( chr ) {
        var map = {
          "  " : " &nbsp;",
          "\n" : "<br>"
        };
        return map[ chr ] || chr;
      } );
    },


    /**
     * Converts HTML to plain text.
     *
     * * Strips all HTML tags
     * * converts <tt>&lt:br&gt:</tt> to new line
     * * unescapes HTML entities
     *
     * @type static
     * @param str {String} HTML string to converts
     * @return {String} plain text representaion of the HTML string
     */
    toText : function(str)
    {
      return rwt.util.html.String.unescape(str.replace(/\s+|<([^>])+>/gi, function(chr)
      {
        if (/\s+/.test(chr)) {
          return " ";
        } else if (/^<BR|^<br/gi.test(chr)) {
          return "\n";
        } else {
          return "";
        }
      }));
    }

  }
});
