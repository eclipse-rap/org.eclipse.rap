/*******************************************************************************
 * Copyright: 2004, 2011 1&1 Internet AG, Germany, http://www.1und1.de,
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
      var appearance = null;
      if( value ) {
        appearance = value.meta.appearance || null;
      }
      if( old ) {
        this.setAutoSync( false );
      }
      qx.theme.manager.Appearance.getInstance().setAppearanceTheme( appearance );
      if( old ) {
        this.setAutoSync( true );
      }
    },

    _applyAutoSync : function( value, old ) {
      if( value ) {
        qx.theme.manager.Appearance.getInstance().syncAppearanceTheme();
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
      var theme = setting.get( "qx.theme" );
      if( theme ) {
        var obj = qx.Theme.getByName( theme );
        if( !obj ) {
          throw new Error( "The meta theme to use is not available: " + theme );
        }
        this.setTheme( obj );
      }
    }
  },

  settings : {
    "qx.theme" : "org.eclipse.swt.theme.Default"
  }

} );
