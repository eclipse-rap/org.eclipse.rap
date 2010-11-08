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
 * Create namespace
 */
qx =
{
  /**
   * Bootstrap qx.Class to create myself later
   * This is needed for the API browser etc. to let them detect me
   */
  Class :
  {
    /**
     * Create namespace.
     * Replaced after bootstrapping phase by {@link qx.Class#createNamespace}.
     *
     * @type map
     * @param name {var} TODOC
     * @param object {var} TODOC
     * @return {var} TODOC
     */
    createNamespace : function(name, object)
    {
      var splits = name.split(".");
      var parent = window;
      var part = splits[0];

      for (var i=0, len=splits.length-1; i<len; i++, part=splits[i])
      {
        if (!parent[part]) {
          parent = parent[part] = {};
        } else {
          parent = parent[part];
        }
      }

      // store object
      parent[part] = object;

      // return last part name (e.g. classname)
      return part;
    },


    /**
     * Define class.
     * Replaced after bootstrapping phase by {@link qx.Class#define}.
     *
     * @type map
     * @param name {var} TODOC
     * @param config {var} TODOC
     * @return {void}
     */
    define : function(name, config)
    {
      if (!config) {
        var config = { statics : {} };
      }

      this.createNamespace(name, config.statics);

      if (config.defer) {
        config.defer(config.statics);
      }

      // Store class reference in global class registry
      qx.core.Bootstrap.__registry[name] = config.statics;
    }
  }
};


/**
 * Internal class that is responsible for bootstrapping the qooxdoo
 * framework at load time.
 *
 * Automatically loads JavaScript language fixes, core logging possibilities
 * and language addons for arrays, strings, etc.
 */
qx.Class.define("qx.core.Bootstrap",
{
  statics :
  {
    /** Timestamp of qooxdoo based application startup */
    LOADSTART : new Date,

    /**
     * Returns the current timestamp
     *
     * @type static
     * @return {Integer} Current timestamp (milliseconds)
     */
    time : function() {
      return new Date().getTime();
    },

    /**
     * Returns the time since initialisation
     *
     * @type static
     * @return {Integer} milliseconds since load
     */
    since : function() {
      return this.time() - this.LOADSTART;
    },

    /** Stores all defined classes */
    __registry : {}
  }
});
