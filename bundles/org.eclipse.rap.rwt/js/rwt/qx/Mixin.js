/*******************************************************************************
 * Copyright (c) 2004, 2014 1&1 Internet AG, Germany, http://www.1und1.de,
 *                          EclipseSource, and others.
 *
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
 * This class is used to define mixins (similar to mixins in Ruby).
 *
 * Mixins are collections of code and variables, which can be merged into
 * other classes. They are similar to classes but don't support inheritance.
 *
 * See the description of the {@link #define} method how a mixin is defined.
 */
rwt.qx.Class.define( "rwt.qx.Mixin", {

  statics : {

    /**
     * Defines a new mixin.
     *
     * @param name {String} name of the mixin
     * @param config {Map ? null} Mixin definition structure. The configuration map has the
     *   following keys:
     *   - construct {Function} An optional mixin constructor. It is called on instantiation each
     *         class including this mixin. The constructor takes no parameters.
     *   - destruct {Function} An optional mixin destructor.
     *   - include {Mixin[]} Array of mixins, which will be merged into the mixin.
     *   - statics {Map} Map of statics of the mixin. The statics will not get copied into the
     *         target class. They remain acceccible from the mixin. This is the same behaviour as
     *         statics in interfaces ({@link qx.Interface#define}).
     *   - members {Map} Map of members of the mixin.
     *   - properties {Map} Map of property definitions.
     *   - events {Map} Map of events the mixin fires. The keys are the names of the events and the
     *         values are corresponding event type classes.
     */
    define : function( name, config ) {
      if( config ) {
        // Normalize include
        if( config.include && !( config.include instanceof Array ) ) {
          config.include = [ config.include ];
        }
        // Create Interface from statics
        var mixin = config.statics ? config.statics : {};
        for( var key in mixin ) {
          mixin[ key ].mixin = mixin;
        }
        // Attach configuration
        if( config.construct ) {
          mixin.$$constructor = config.construct;
        }
        if( config.include ) {
          mixin.$$includes = config.include;
        }
        if( config.properties ) {
          mixin.$$properties = config.properties;
        }
        if( config.members ) {
          mixin.$$members = config.members;
        }
        for( var key in mixin.$$members ) {
          if( mixin.$$members[ key ] instanceof Function ) {
            mixin.$$members[ key ].mixin = mixin;
          }
        }
        if( config.events ) {
          mixin.$$events = config.events;
        }
        if( config.destruct ) {
          mixin.$$destructor = config.destruct;
        }
      } else {
        var mixin = {};
      }
      // Add basics
      mixin.$$type = "Mixin";
      mixin.name = name;
      // Attach toString
      mixin.toString = this.genericToString;
      // Assign to namespace
      mixin.basename = rwt.qx.Class.createNamespace( name, mixin );
      // Store class reference in global mixin registry
      this.__registry[ name ] = mixin;
      // Return final mixin
      return mixin;
    },

    /**
     * Determine if mixin exists
     *
     * @name isDefined
     * @param name {String} mixin name to check
     * @return {Boolean} true if mixin exists
     */
    isDefined : function( name ) {
      return this.__registry[ name ] !== undefined;
    },

    /**
     * Generates a list of all mixins given plus all the
     * mixins these includes plus... (deep)
     *
     * @param mixins {Mixin[] ? []} List of mixins
     * @returns {Array} List of all mixins
     */
    flatten : function( mixins ) {
      if( !mixins ) {
        return [];
      }
      // we need to create a copy and not to modify the existing array
      var list = mixins.concat();
      for( var i = 0, l = mixins.length; i < l; i++ ) {
        if( mixins[ i ].$$includes ) {
          list.push.apply( list, this.flatten( mixins[ i ].$$includes ) );
        }
      }
      // console.log("Flatten: " + mixins + " => " + list);
      return list;
    },

    /**
     * This method will be attached to all mixins to return
     * a nice identifier for them.
     *
     * @internal
     * @return {String} The mixin identifier
     */
    genericToString : function() {
      return "[Mixin " + this.name + "]";
    },

    /** Registers all defined mixins */
    __registry : {}

  }

} );
