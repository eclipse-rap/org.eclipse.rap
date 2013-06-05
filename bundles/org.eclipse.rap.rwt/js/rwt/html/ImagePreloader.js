/*******************************************************************************
 * Copyright (c) 2004, 2013 1&1 Internet AG, Germany, http://www.1und1.de,
 *                          EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Remote Application Platform
 ******************************************************************************/

/**
 * This class preloads one image and provides information about this image
 * after it is loaded.
 *
 * This class should not be used directly. Better use {@link rwt.html.ImagePreloaderManager}:
 *
 * <pre class='javascript'>
 * rwt.html.ImagePreloaderManager.getInstance().create(imageUrl)
 * </pre>
 */
rwt.qx.Class.define("rwt.html.ImagePreloader",
{
  extend : rwt.qx.Target,

  events :
  {
    /** Dispatched after the images has successfully been loaded */
    "load" : "rwt.event.Event",

    /** Dispatched if the image could not be loaded */
    "error" : "rwt.event.Event"
  },




  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  /**
   * @param imageUrl {String} URL of the image to pre load
   */
  construct : function(imageUrl)
  {
    if (rwt.html.ImagePreloaderManager.getInstance().has(imageUrl))
    {
      return rwt.html.ImagePreloaderManager.getInstance().get(imageUrl);
    }

    this.base(arguments);

    // Create Image-Node
    // Does not work with document.createElement("img") in Webkit. Interesting.
    // Compare this to the bug in rwt.widgets.base.Image.
    this._element = new Image();

    // Define handler if image events occurs
    this._element.onload = rwt.util.Functions.bind(this.__onload, this);
    this._element.onerror = rwt.util.Functions.bind(this.__onerror, this);

    // Set Source
    this._source = imageUrl;
    this._element.src = imageUrl;

    this._checkPng();

    rwt.html.ImagePreloaderManager.getInstance().add(this);
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
      STATE MANAGERS
    ---------------------------------------------------------------------------
    */

    _source : null,
    _isLoaded : false,
    _isErroneous : false,




    /*
    ---------------------------------------------------------------------------
      CROSSBROWSER GETTERS
    ---------------------------------------------------------------------------
    */

    /**
     * Get the full URI of the image
     *
     * @type member
     * @return {String} The URI of the image
     */
    getUri : function() {
      return this._source;
    },


    /**
     * Get the full URI of the image
     *
     * @type member
     * @return {String} The URI of the image
     */
    getSource : function() {
      return this._source;
    },


    /**
     * Check whether the image is already loaded
     *
     * @type member
     * @return {Boolean} Whether the image is already loaded
     */
    isLoaded : function() {
      return this._isLoaded;
    },


    /**
     * Check whether the loading of the image failed
     *
     * @type member
     * @return {Boolean} Whether the loading of the image failed
     */
    isErroneous : function() {
      return this._isErroneous;
    },

    _checkPng : rwt.util.Variant.select( "qx.client", {
      "default": rwt.util.Functions.returnTrue,
      "mshtml" : function() {
        this._isPng = /\.png$/i.test(this._element.nameProp);
      }
    } ),

    // only used in mshtml: true when the image format is in png
    _isPng : false,


    /**
     * Check whether the image format if PNG
     *
     * @type member
     * @return {Boolean} whether the image format if PNG
     */
    getIsPng : function() {
      // TODO should be renamedto isPng to be consistent with the rest of the framework.
      return this._isPng;
    },


    /**
     * Return the width of the image in pixel.
     *
     * @type member
     * @return {Integer} The width of the image in pixel.
     * @signature function()
     */
    getWidth : rwt.util.Variant.select("qx.client",
    {
      "gecko" : function() {
        return this._element.naturalWidth;
      },

      "default" : function() {
        return this._element.width;
      }
    }),


    /**
     * Return the height of the image in pixel.
     *
     * @type member
     * @return {Integer} The height of the image in pixel.
     * @signature function()
     */
    getHeight : rwt.util.Variant.select("qx.client",
    {
      "gecko" : function() {
        return this._element.naturalHeight;
      },

      "default" : function() {
        return this._element.height;
      }
    }),


    /**
     * Load handler
     *
     * @type member
     * @return {void}
     */
    __onload : function()
    {
      if (this._isLoaded || this._isErroneous) {
        return;
      }

      this._isLoaded = true;
      this._isErroneous = false;

      if (this.hasEventListeners("load")) {
        this.dispatchEvent(new rwt.event.Event("load"), true);
      }
    },


    /**
     * Error handler
     *
     * @type member
     * @return {void}
     */
    __onerror : function()
    {
      if (this._isLoaded || this._isErroneous) {
        return;
      }

      this._isLoaded = false;
      this._isErroneous = true;

      if (this.hasEventListeners("error")) {
        this.dispatchEvent(new rwt.event.Event("error"), true);
      }
    }
  },




  /*
  *****************************************************************************
     DESTRUCTOR
  *****************************************************************************
  */

  destruct : function()
  {
    if (this._element)
    {
      // terminate any downloading in progress and free memory for image
      // this._element.src = "";

      this._element.onload = this._element.onerror = null;
    }

    this._disposeFields("_element", "_isLoaded", "_isErroneous", "_isPng");
  }
});
