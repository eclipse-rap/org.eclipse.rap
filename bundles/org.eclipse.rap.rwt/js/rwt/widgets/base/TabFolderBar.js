/*******************************************************************************
 *  Copyright: 2004, 2012 1&1 Internet AG, Germany, http://www.1und1.de,
 *                        and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Remote Application Platform
 ******************************************************************************/

/**
 * @appearance tab-view-bar
 */
rwt.qx.Class.define("rwt.widgets.base.TabFolderBar",
{
  extend : rwt.widgets.base.BoxLayout,




  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  construct : function()
  {
    this.base(arguments);

    this.initZIndex();
    this.initHeight();
    this._manager = new rwt.widgets.util.RadioManager();
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
      init : "tab-view-bar"
    },

    zIndex :
    {
      refine : true,
      init : 2
    },

    height :
    {
      refine : true,
      init : "auto"
    }
  },

  members : {

    /**
     * Get the selection manager.
     *
     * @type member
     * @return {rwt.widgets.util.RadioManager} the selection manager of the bar.
     */
    getManager : function() {
      return this._manager;
    }

  },

  destruct : function() {
    this._disposeObjects( "_manager" );
  }

} );
