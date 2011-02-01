/*******************************************************************************
 *  Copyright: 2004, 2011 1&1 Internet AG, Germany, http://www.1und1.de,
 *                        Derrell Lipman,
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

qx.Class.define("qx.io.remote.Response",
{
  extend : qx.event.type.Event,




  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  construct : function(eventType) {
    this.base(arguments, eventType);
  },




  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

  properties :
  {
    /*
    ---------------------------------------------------------------------------
      PROPERTIES
    ---------------------------------------------------------------------------
    */

    state :
    {
      check : "Integer",
      nullable : true
    },


    /** Status code of the response. */
    statusCode :
    {
      check : "Integer",
      nullable : true
    },

    content : {
      nullable : true
    },

    responseHeaders :
    {
      check : "Object",
      nullable : true
    }
  },




  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    /*
    ---------------------------------------------------------------------------
      USER METHODS
    ---------------------------------------------------------------------------
    */

    /**
     * TODOC
     *
     * @type member
     * @param vHeader {var} TODOC
     * @return {var | null} TODOC
     */
    getResponseHeader : function(vHeader)
    {
      var vAll = this.getResponseHeaders();

      if (vAll) {
        return vAll[vHeader] || null;
      }

      return null;
    },


    /**
     * @deprecated This method is no longer needed since the event object is now an
     *     instance of the Response class.
     */
     getData : function()
     {
       return this;
     }
  }
});
