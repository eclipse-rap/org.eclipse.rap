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
 * This singleton manages global resource aliases
 */
qx.Class.define("qx.io.Alias",
{
  type : "singleton",
  extend : qx.util.manager.Value,




  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  construct : function()
  {
    this.base(arguments);

    // Contains defined aliases (like icons/, widgets/, application/, ...)
    this._aliases = {};

    this._addStatic();
  },




  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {

    /**
     * Define static alias from setting, if the custom staticUri setting is set
     * we use it as alias else we take default resourceUri + /static
     *
     * @type member
     * @return {void}
     */
    _addStatic : function ()
    {
      this.add("static", qx.core.Setting.isSet("qx.staticUri") ? qx.core.Setting.get("qx.staticUri") : qx.core.Setting.get("qx.resourceUri") + "/static");
    },


    /**
     * pre process incoming dynamic value
     *
     * @param value {String} incoming value
     * @return {String} pre processed value
     */
    _preprocess : function(value)
    {
      var dynamics = this._dynamic;
      
      if (dynamics[value] === false) {
        // Resource already marked as "unmanaged"
        return value;
      }
      else if (dynamics[value] === undefined)
      {
        if (value.indexOf("http://") === 0 || value.indexOf("https://") === 0 || 
            value.indexOf("file://") === 0)
        {
          // Mark absolute URLs as unmanaged and leave them as is
          dynamics[value] = false;
          return value;
        }
        
        var alias = value.substring(0, value.indexOf("/"));
        var resolved = this._aliases[alias];
        
        if (resolved === undefined)
        {
          if (qx.core.Variant.isSet("qx.client", "mshtml"))
          {
            if (window.location.protocol === "https:")
            {
              var firstCharPointOrSlash = value.match(/^[\.\/]/);
              var firstCharAlphaNumeric = value.match(/^\w/);
              
              if (firstCharPointOrSlash != null || firstCharAlphaNumeric != null)
              {
                // rewrite unmanaged relative URL to an absolute if necessary
                // prefix any URL starting with an alphanumeric char
                if (firstCharAlphaNumeric != null && firstCharPointOrSlash == null) {
                  value = "./" + value;
                }
                
                return this.__rewriteUrl(value);
              }
            }
          }
          return value;
        } 
        else
        { 
          // rewrite relative URL to an absolute if necessary
          if (qx.core.Variant.isSet("qx.client", "mshtml")) {
            if (window.location.protocol === "https:") {
              resolved = this.__rewriteUrl(resolved);
            }
          }
          
          dynamics[value] = resolved + value.substring(alias.length);
        }
      }
      
      return value;
    },
    
    
    /**
     * Rewrites an relative URL to an absolute one to prevent the "mixed content"
     * warning under HTTPS in IE. 
     * 
     * @param value {String} Url of resource to rewrite for HTTPS
     * @return {String} rewritten absolute URL 
     */
    __rewriteUrl : function(value)
    {
      // To avoid a "mixed content" warning in IE when the application is
      // delivered via HTTPS a prefix has to be added. This will transform the
      // relative URL to an absolute one in IE.
      // Though this warning is only displayed in conjunction with images which
      // are referenced as a CSS "background-image", every resource path is
      // changed when the application is served with HTTPS.
       
      var urlPrefix = "";
      
      // SPECIAL CASE
      // It is valid to to begin a URL with "//" so this case has to
      // be considered. If the to resolved URL begins with "//" the
      // manager prefixes it with "https:" to avoid any problems for IE
      if (value.match(/^\/\//) != null) {
        urlPrefix = window.location.protocol;
      }
      // If the resolved URL begins with "./" the final URL has to be
      // put together using the document.URL property.
      // IMPORTANT: this is only applicable for the source version, because
      // the build version does itself add a "/" at the end of the URL. This 
      // would end up with e.g. "build//example.png" instead of "build/./example.png"
      else if (value.match(/^\.\//) != null && qx.core.Setting.get("qx.isSource"))
      {
        value = value.substring(1);
        urlPrefix = document.URL.substring(0, document.URL.lastIndexOf("/"));
      }
      // Prefix an relative URL beginning with "/" with the protocol and the 
      // host e.g. "https://yourdomain.com"
      else if (value.match(/^\//)) {
        urlPrefix = window.location.protocol + "//" + window.location.host;
      }
      // Let absolute URLs pass through (HTTPS and HTTP)
      else if (value.match(/^http/) != null) {
        // nothing to do
      }
      else {
        urlPrefix = window.location.href.substring(0, window.location.href.lastIndexOf("/") + 1);
      }
      
      return urlPrefix + value;
    },


    /**
     * Define an alias to a resource path
     *
     * @type member
     * @param alias {String} alias name for the resource path/url
     * @param base {String} first part of URI for all images which use this alias
     * @return {void}
     */
    add : function(alias, base)
    {
      // Store new alias value
      this._aliases[alias] = base;

      // Localify stores
      var dynamics = this._dynamic;
      var reg = this._registry;
      var entry;

      // Temporary data structure to optimize performance of update
      var paths = {};

      // Update old entries which use this alias
      for (var path in dynamics)
      {
        if (path.substring(0, path.indexOf("/")) === alias)
        {
          dynamics[path] = base + path.substring(alias.length);
          paths[path] = true;
        }
      }

      // Update the corresponding objects (which use this alias)
      for (var key in reg)
      {
        entry = reg[key];
        if (paths[entry.value]) {
          entry.callback.call(entry.object, dynamics[entry.value]);
        }
      }
    },


    /**
     * Remove a previously defined alias
     *
     * @type member
     * @param alias {String} alias name for the resource path/url
     * @return {void}
     */
    remove : function(alias)
    {
      delete this._aliases[alias];

      // No signal for depending objects here. These
      // will informed with the new value using add().
    },


    /**
     * Resolves a given path
     *
     * @type member
     * @param path {String} input path
     * @return {String} resulting path (with interpreted aliases)
     */
    resolve : function(path)
    {
      if (path !== null) {
        path = this._preprocess(path);
      }

      return this._dynamic[path] || path;
    }
  },



  /*
  *****************************************************************************
     DESTRUCTOR
  *****************************************************************************
  */

  destruct : function() {
    this._disposeFields("_aliases");
  }
});
