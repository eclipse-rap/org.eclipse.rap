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
 * qx.ui.core.ClientDocumentBlocker blocks the inputs from the user.
 * This will be used internally to allow better modal dialogs for example.
 *
 * @appearance blocker
 */
qx.Class.define("qx.ui.core.ClientDocumentBlocker",
{
  extend : qx.ui.basic.Terminator,




  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  construct : function()
  {
    this.base(arguments);

    this.initTop();
    this.initLeft();

    this.initWidth();
    this.initHeight();

    this.initZIndex();
  },




  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

  properties :
  {
    appearance :
    {
      refine : true,
      init : "client-document-blocker"
    },

    zIndex :
    {
      refine : true,
      init : 1e8
    },

    top :
    {
      refine : true,
      init : 0
    },

    left :
    {
      refine : true,
      init : 0
    },

    width :
    {
      refine : true,
      init : "100%"
    },

    height :
    {
      refine : true,
      init : "100%"
    },

    display :
    {
      refine : true,
      init : false
    }
  },




  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    // We must omit that the focus root is changed to the client document
    // when processing a mouse down event on this widget.
    getFocusRoot : function() {
      return null;
    }
  }
});
