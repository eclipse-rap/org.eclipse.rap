/*******************************************************************************
 *  Copyright: 2004, 2011 1&1 Internet AG, Germany, http://www.1und1.de,
 *                        and EclipseSource
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    1&1 Internet AG and others - original API and implementation
 *    EclipseSource - adaptation for the Eclipse Rich Ajax Platform
 ******************************************************************************/

qx.Class.define( "qx.theme.manager.Meta", {

  type : "singleton",
  extend : qx.core.Target,

  properties : {
    /**
     * Meta theme. Applies the defined color, ... themes to
     * the corresponding managers.
     */
    theme : {
      check : "Theme",
      nullable : true,
      apply : "_applyTheme",
      event : "changeTheme"
    },

    /**
     * Controls whether sync is done automatically
     */
    autoSync : {
      check : "Boolean",
      init : true,
      apply : "_applyAutoSync"
    }
  },

  members : {

    _applyTheme : function( value, old ) {
      var color = null;
      var font = null;
      var appearance = null;

      if( value ) {
        color = value.meta.color || null;
        font = value.meta.font || null;
        appearance = value.meta.appearance || null;
      }

      if( old ) {
        this.setAutoSync( false );
      }

      qx.theme.manager.Color.getInstance().setColorTheme( color );
      qx.theme.manager.Font.getInstance().setFontTheme( font );
      qx.theme.manager.Appearance.getInstance().setAppearanceTheme( appearance );

      if( old ) {
        this.setAutoSync( true );
      }
    },

    _applyAutoSync : function( value, old ) {
      if( value ) {
        qx.theme.manager.Appearance.getInstance().syncAppearanceTheme();
        qx.theme.manager.Font.getInstance().syncFontTheme();
        qx.theme.manager.Color.getInstance().syncColorTheme();
      }
    },

    /**
     * Initialize the themes which were selected using the settings. Should only
     * be called from qooxdoo based application.
     *
     * @type static
     */
    initialize : function() {
      var setting = qx.core.Setting;
      var theme, obj;

      theme = setting.get("qx.theme");
      if (theme) {
        obj = qx.Theme.getByName(theme);
        if (!obj) {
          throw new Error("The meta theme to use is not available: " + theme);
        }

        this.setTheme(obj);
      }

      theme = setting.get("qx.colorTheme");
      if (theme) {
        obj = qx.Theme.getByName(theme);
        if (!obj) {
          throw new Error("The color theme to use is not available: " + theme);
        }
        qx.theme.manager.Color.getInstance().setColorTheme(obj);
      }

      theme = setting.get("qx.fontTheme");
      if (theme) {
        obj = qx.Theme.getByName(theme);
        if (!obj) {
          throw new Error("The font theme to use is not available: " + theme);
        }
        qx.theme.manager.Font.getInstance().setFontTheme(obj);
      }

      theme = setting.get("qx.appearanceTheme");
      if (theme) {
        obj = qx.Theme.getByName(theme);
        if (!obj) {
          throw new Error("The appearance theme to use is not available: " + theme);
        }
        qx.theme.manager.Appearance.getInstance().setAppearanceTheme(obj);
      }
    },

    /**
     * Query the theme list to get all themes the given key
     *
     * @param key {String} the key to look for
     * @return {Theme[]} list of matching themes
     */
    __queryThemes : function( key ) {
      var reg = qx.Theme.getAll();
      var theme;
      var list = [];
      for( var name in reg ) {
        theme = reg[ name ];
        if( theme[ key ] ) {
          list.push( theme );
        }
      }
      return list;
    },

    /**
     * Returns a list of all registered meta themes
     *
     * @type static
     * @return {Theme[]} list of meta themes
     */
    getMetaThemes : function() {
      return this.__queryThemes("meta");
    },

    /**
     * Returns a list of all registered color themes
     *
     * @type static
     * @return {Theme[]} list of color themes
     */
    getColorThemes : function() {
      return this.__queryThemes("colors");
    },

    /**
     * Returns a list of all registered font themes
     *
     * @type static
     * @return {Theme[]} list of font themes
     */
    getFontThemes : function() {
      return this.__queryThemes("fonts");
    },

    /**
     * Returns a list of all registered appearance themes
     *
     * @type static
     * @return {Theme[]} list of appearance themes
     */
    getAppearanceThemes : function() {
      return this.__queryThemes("appearances");
    }
  },

  settings : {
    "qx.theme" : "org.eclipse.swt.theme.Default",
    "qx.colorTheme" : null,
    "qx.fontTheme" : null,
    "qx.appearanceTheme" : null
  }

} );
