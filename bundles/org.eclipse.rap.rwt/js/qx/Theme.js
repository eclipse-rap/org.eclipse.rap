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

/**
 * This class helps to create and manager so-named theme classes.
 *
 * Supported are: color, border, fonts, icons, widgets,
 * appearances and meta themes.
 */
qx.Class.define( "qx.Theme", {

  statics: {

    define : function( name, config ) {
      if( !config ) {
        throw new Error( "parameter config missing" );
      }

      // Create alias
      var theme = {
        $$type : "Theme",
        name : name,
        title : config.title
      };
      if( config.extend ) {
        theme.supertheme = config.extend;
      }

      // Assign to namespace
      theme.basename = qx.Class.createNamespace( name, theme );

      // Convert theme entry from Object to Function (for prototype inheritance)
      this.__convert( theme, config );

      // Store class reference in global class registry
      this.__registry[ name ] = theme;
    },

    /**
     * Return a map of all known themes
     */
    getAll : function() {
      return this.__registry;
    },

    /**
     * Returns a theme by name
     */
    getByName : function( name ) {
      return this.__registry[ name ];
    },

    /**
     * Determine if theme exists
     */
    isDefined : function( name ) {
      return this.getByName( name ) !== undefined;
    },

    __extractType : function( config ) {
      for( var i = 0, keys = this.__inheritableKeys, l = keys.length; i < l; i++ ) {
        if( config[ keys[ i ] ] ) {
          return keys[ i ];
        }
      }
    },

    /**
     * Convert existing entry to a prototype based inheritance function
     */
    __convert : function( theme, config ) {
      var type = this.__extractType( config );

      // Use theme key from extended theme if own one is not available
      if( config.extend && !type ) {
        type = config.extend.type;
      }

      // Save theme type
      theme.type = type || "other";

      // Return if there is no key defined at all
      if( !type ) {
        return;
      }

      // Create pseudo class
      var PseudoClass = function() {};

      // Process extend config
      if( config.extend ) {
        PseudoClass.prototype = new config.extend.$$clazz();
      }

      var target = PseudoClass.prototype;
      var source = config[ type ];

      // Copy entries to prototype
      for( var id in source ) {
        target[ id ] = source[ id ];

        // Appearance themes only:
        // Convert base flag to class reference (needed for mixin support)
        if( target[id].base ) {
          target[id].base = config.extend;
        }
      }

      // store pseudo class
      theme.$$clazz = PseudoClass;

      // and create instance under the old key
      theme[type] = new PseudoClass();
    },

    /** {Map} Internal theme registry */
    __registry : {},

    /** {Array} Keys which support inheritance */
    __inheritableKeys : [ "colors", "fonts", "appearances", "meta" ]

  }

} );
