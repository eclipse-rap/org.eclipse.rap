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
rwt.qx.Class.define("rwt.util.html.String",
{
  statics :
  {

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
    }

  }
});
