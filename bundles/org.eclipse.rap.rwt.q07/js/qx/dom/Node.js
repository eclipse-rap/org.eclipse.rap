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

/* ************************************************************************

#module(html2)

************************************************************************ */

/**
 * Basic node creation and type detection
 */
qx.Class.define("qx.dom.Node",
{
  /*
  *****************************************************************************
     STATICS
  *****************************************************************************
  */

  statics :
  {
    /*
    ---------------------------------------------------------------------------
      NODE TYPES
    ---------------------------------------------------------------------------
    */

    /**
     * {Map} Node type:
     *
     * * ELEMENT
     * * ATTRIBUTE
     * * TEXT
     * * CDATA_SECTION
     * * ENTITY_REFERENCE
     * * ENTITY
     * * PROCESSING_INSTRUCTION
     * * COMMENT
     * * DOCUMENT
     * * DOCUMENT_TYPE
     * * DOCUMENT_FRAGMENT
     * * NOTATION
     */
    ELEMENT                : 1,
    ATTRIBUTE              : 2,
    TEXT                   : 3,
    CDATA_SECTION          : 4,
    ENTITY_REFERENCE       : 5,
    ENTITY                 : 6,
    PROCESSING_INSTRUCTION : 7,
    COMMENT                : 8,
    DOCUMENT               : 9,
    DOCUMENT_TYPE          : 10,
    DOCUMENT_FRAGMENT      : 11,
    NOTATION               : 12,



    /**
     * Whether the given node is a DOM document node
     *
     * @type static
     * @param node {Node} the node which should be tested
     * @return {Boolean} true when the node is a document
     */
    isDocument : function(node) {
      return !!(node && node.nodeType === qx.dom.Node.DOCUMENT);
    },


    /*
    ---------------------------------------------------------------------------
      DOCUMENT ACCESS
    ---------------------------------------------------------------------------
    */

    /**
     * Returns the owner document of the given node
     *
     * @type static
     * @param node {Node} the node which should be tested
     * @return {Document | null} The document of the given DOM node
     */
    getDocument : function(node)
    {
      if (this.isDocument(node)) {
        return node;
      }

      return node.ownerDocument || node.document || null;
    },


    /**
     * Returns the DOM2 <code>defaultView</code> (window).
     *
     * @type static
     * @signature function(node)
     * @param node {Node} node to inspect
     * @return {Window} the <code>defaultView</code> of the given node
     */
    getWindow : qx.core.Variant.select("qx.client",
    {
      "mshtml" : function(node) {
        return this.getDocument(node).parentWindow;
      },

      "default" : function(node) {
        return this.getDocument(node).defaultView;
      }
    })

  }
});
