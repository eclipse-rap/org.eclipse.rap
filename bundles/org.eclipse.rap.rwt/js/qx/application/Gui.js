/*******************************************************************************
 *  Copyright: 2004, 2011 1&1 Internet AG, Germany, http://www.1und1.de,
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
 * This is the base class for all GUI qooxdoo applications.
 *
 * All applications using qooxdoo widgets should be derived from
 * this class. Normally at least the {@link #main} method will
 * be overridden to define the GUI.
 */
qx.Class.define("qx.application.Gui",
{
  extend : qx.core.Target,

  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

  properties : {
    /** Whether the user interfacce has already been rendered */
    uiReady :
    {
      check : "Boolean",
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

    /**
     * Called in the document.onload event of the browser. This method should
     * be overridden to implement the GUI setup code of the application.
     *
     * @type member
     */
    main : function()
    {
      // Initialize themes
      qx.theme.manager.Meta.getInstance().initialize();

      // Force creation of client document
      qx.ui.core.ClientDocument.getInstance();

      // Call preloader
      qx.client.Timer.once(this._preload, this, 0);
    },


    /**
     * Called in the document.onbeforeunload event of the browser. If the method
     * returns a string value, the user will be asked by the browser, whether
     * he really wants to leave the page. The return string will be displayed in
     * the message box.
     *
     * @type member
     * @return {String?null} message text on unloading the page
     */
    close : function() {},


    /**
     * Called in the document.onunload event of the browser. This method contains the last
     * code which is run inside the page and may contain cleanup code.
     *
     * @type member
     */
    terminate : function() {
      org.eclipse.rwt.EventHandler.detachEvents();
      org.eclipse.rwt.EventHandler.cleanUp();
    },


    /**
     * Start pre loading of the initially visible images.
     */
    _preload : function()
    {
      this.__preloader = new qx.io.image.PreloaderSystem(qx.io.image.Manager.getInstance().getVisibleImages(), this._preloaderDone, this);
      this.__preloader.start();
    },

    /**
     * Callback which is called once the pre loading of the required images
     * is completed.
     */
    _preloaderDone : function()
    {
      this.setUiReady(true);

      this.__preloader.dispose();
      this.__preloader = null;

      var start = (new Date).valueOf();

      // Show initial widgets
      qx.ui.core.Widget.flushGlobalQueues();

      // Finally attach event to make the GUI ready for the user
      org.eclipse.rwt.EventHandler.init();
      org.eclipse.rwt.EventHandler.attachEvents();

      // Call postloader
      qx.client.Timer.once(this._postload, this, 100);
    },


    /**
     * Preload all remaining images.
     */
    _postload : function()
    {
      this.__postloader = new qx.io.image.PreloaderSystem(qx.io.image.Manager.getInstance().getHiddenImages(), this._postloaderDone, this);
      this.__postloader.start();
    },


    /**
     * Callback which is called once the post loading is completed.
     */
    _postloaderDone : function()
    {
      this.__postloader.dispose();
      this.__postloader = null;
    }
  }
});
