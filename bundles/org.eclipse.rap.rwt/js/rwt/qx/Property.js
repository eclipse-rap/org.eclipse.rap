/*******************************************************************************
 * Copyright (c) 2004, 2022 1&1 Internet AG, Germany, http://www.1und1.de,
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
 * Internal class for handling of dynamic properties. Should only be used
 * through the methods provided by {@link rwt.qx.Class}.
 *
 * For a complete documentation of properties take a
 * look at http://qooxdoo.org/documentation/developer_manual/properties.
 *
 *
 * *Normal properties*
 *
 * The <code>properties</code> key in the class definition map of {@link rwt.qx.Class#define}
 * is used to generate the properties.
 *
 * Valid keys of a property definition are:
 *
 * - check {Array, String, Function}
 *   The check is used to validate the incoming value of a property. The check can be:
 *   - a custom check function. The function takes the incoming value as a parameter and must
 *     return a boolean value to indicate whether the values is valid.
 *   - inline check code as a string e.g. <code>"value &gt; 0 && value &lt; 100"</code>
 *   - a class name e.g. <code>rwt.widgets.Button</code>
 *   - a name of an interface the value must implement, e.g. <code>qx.application.IAplpication</code>
 *   - an array of all valid values</li>
 *   - one of the predefined checks: Boolean, String, Number, Integer, Float, Double,
 *     Object, Array, Map, Class, Mixin, Interface, Theme, Error, RegExp, Function,
 *     Date, Node, Element, Document, Window, Event
 * - init {var}
 *   Sets the default/initial value of the property. If no property value is set or the property
 *   gets reset, the getter will return the <code>init</code> value.
 * - apply {String}
 *   On change of the property value the method of the specified name will be called. The
 *   signature of the method is <code>function(newValue, oldValue)</code>.
 * - event {String}
 *   On change of the property value an event with the given name will be dispached. The event
 *   type is {@link rwt.event.ChangeEvent}.
 * - themeable {Boolean}
 *   Whether this property can be set using themes.
 * - inheritable {Boolean}
 *   Whether the property value should be inheritable. If the property does not have a user
 *   defined or an init value, the property will try to get the value from the parent of the
 *   current object.
 * - nullable {Boolean}
 *   Whether <code>null</code> is an allowed value of the property. This is complemental to the
 *   check defined using the <code>check</code> key.
 * - refine {Boolean}
 *   Whether the property definition is a refinemnet of a property in one of the super classes of
 *   the class. Only the <code>init</code> value can be changed using refine.
 * - transform {String}
 *   On setting of the property value the method of the specified name will
 *   be called. The signature of the method is <code>function(value)</code>.
 *   The parameter <code>value</code> is the value passed to the setter.
 *   The function must return the modified or unmodified value.
 *   Transformation occurs before the check function, so both may be
 *   specified if desired.  Alternatively, the transform function may throw
 *   an error if the value passed to it is invalid.
 *
 * Property groups
 * ---------------
 *
 * Property groups are defined in a similar way but support a different set of keys:
 *
 * - group {String[]}
 *   A list of property names which should be set using the propery group.
 * - mode {String}
 *   If mode is set to <code>"shorthand"</code>, the properties can be set using a CSS like
 *   shorthand mode.
 * - themeable {Boolean}
 *   Whether this property can be set using themes.
 */
rwt.qx.Class.define( "rwt.qx.Property", {

  statics : {

    /**
     * Built-in checks
     * The keys could be used in the check of the properties
     */
    __checks : {
      "Boolean"   : function( value ) { return typeof value === "boolean"; },
      "String"    : function( value ) { return typeof value === "string"; },
      "NonEmptyString" : function( value ) { return typeof value === "string" && value.length > 0; },

      "Number"    : function( value ) { return typeof value === "number" && isFinite( value ); },
      "Integer"   : function( value ) { return typeof value === "number" && isFinite( value ) && value%1 === 0; },
      "Float"     : function( value ) { return typeof value === "number" && isFinite( value ); },
      "Double"    : function( value ) { return typeof value === "number" && isFinite( value ); },

      "Error"     : function( value ) { return value instanceof Error; },
      "RegExp"    : function( value ) { return value instanceof RegExp; },

      "Object"    : function( value ) { return value !== null && typeof value === "object"; },
      "Array"     : function( value ) { return value instanceof Array; },
      "Map"       : function( value ) { return value !== null && typeof value === "object" && !( value instanceof Array ) && !( value instanceof rwt.qx.Object ); },

      "Function"  : function( value ) { return value instanceof Function; },
      "Date"      : function( value ) { return value instanceof Date; },
      "Node"      : function( value ) { return value !== null && value.nodeType !== undefined; },
      "Element"   : function( value ) { return value !== null && value.nodeType === 1 && value.attributes; },
      "Document"  : function( value ) { return value !== null && value.nodeType === 9 && value.documentElement; },
      "Window"    : function( value ) { return value !== null && window.document; },
      "Event"     : function( value ) { return value !== null && value.type !== undefined; },

      "Class"     : function( value ) { return value !== null && value.$$type === "Class"; },
      "Mixin"     : function( value ) { return value !== null && value.$$type === "Mixin"; },
      "Interface" : function( value ) { return value !== null && value.$$type === "Interface"; },
      "Theme"     : function( value ) { return value !== null && value.$$type === "Theme"; },

      "Color"     : function( value ) { return typeof value === "string" && rwt.util.Colors.isValid( value ); },
      "Border"    : function( value ) { return value !== null; },
      "Font"      : function( value ) { return value !== null; },
      "Label"     : function( value ) { return value !== null && typeof value === "string"; }
    },

    /**
     * Contains types from {@link #__checks} list which need to be disposed
     */
    __dispose : {
      "Object"    : true,
      "Array"     : true,
      "Map"       : true,
      "Function"  : true,
      "Date"      : true,
      "Node"      : true,
      "Element"   : true,
      "Document"  : true,
      "Window"    : true,
      "Event"     : true,
      "Class"     : true,
      "Mixin"     : true,
      "Interface" : true,
      "Theme"     : true,
      "Border"    : true,
      "Font"      : true
    },

    /**
     * Inherit value, used to override defaults etc. to force inheritance
     * even if property value is not undefined (through multi-values)
     */
    $$inherit : "inherit",

    /**
     * Used in build version for storage names
     */
    $$idcounter : 0,

    /**
     * Caching field names for each property created
     */
    $$store : {
      user : {},
      theme : {},
      inherit : {},
      init : {},
      useinit : {}
    },

    /**
     * Caching function names for each property created
     */
    $$method : {
      get : {},
      set : {},
      reset : {},
      init : {},
      refresh : {},
      style : {},
      unstyle : {}
    },

    /**
     * Supported keys for property defintions
     */
    $$allowedKeys : {
      name         : "string",   // String
      dispose     : "boolean",  // Boolean
      inheritable : "boolean",  // Boolean
      nullable    : "boolean",  // Boolean
      themeable   : "boolean",  // Boolean
      refine      : "boolean",  // Boolean
      init        : null,       // var
      apply       : "string",   // String
      event       : "string",   // String
      check       : null,       // Array, String, Function
      transform   : "string",   // String
      deferredInit : "boolean"   // Boolean
    },

    $$allowedGroupKeys : {
      name       : "string",   // String
      group      : "object",   // Array
      mode       : "string",   // String
      themeable  : "boolean"   // Boolean
    },

    /** Contains names of inheritable properties, filled by {@link rwt.qx.Class.define} */
    $$inheritable : {},

    /**
     * Refreshes widget whose parent has changed (including the children)
     *
     * @param widget {rwt.widgets.base.Widget} the widget
     */
    refresh : function( widget ) {
      var parent = widget.getParent();
      if( parent ) {
        var clazz = widget.constructor;
        var inherit = this.$$store.inherit;
        var refresh = this.$$method.refresh;
        var properties;
        while( clazz ) {
          properties = clazz.$$properties;
          if( properties ) {
            for( var name in this.$$inheritable ) {
              // Whether the property is available in this class
              // and whether it is inheritable in this class as well
              if( properties[name] && widget[refresh[name]] ) {
                widget[refresh[name]]( parent[inherit[name]] );
              }
            }
          }
          clazz = clazz.superclass;
        }
      }
    },

    /**
     * Attach properties to class prototype
     *
     * @param clazz {Class} Class to attach properties to
     */
    attach : function( clazz ) {
      var properties = clazz.$$properties;
      if( properties ) {
        for( var name in properties ) {
          this.attachMethods( clazz, name, properties[ name ] );
        }
      }
      clazz.$$propertiesAttached = true;
    },

    /**
     * Attach one property to class
     *
     * @param clazz {Class} Class to attach properties to
     * @param name {String} Name of property
     * @param config {Map} Configuration map of property
     */
    attachMethods : function( clazz, name, config ) {
      // Filter old properties
      if( config._fast || config._cached ) {
        return;
      }

      // Generate property method prefixes and postfixes
      var prefix, postfix;

      if( name.charAt(0) === "_" ) {
        if( name.charAt(1) === "_" ) {
          prefix = "__";
          postfix = rwt.util.Strings.toFirstUp(name.substring(2));
        } else {
          prefix = "_";
          postfix = rwt.util.Strings.toFirstUp(name.substring(1));
        }
      } else {
        prefix = "";
        postfix = rwt.util.Strings.toFirstUp(name);
      }

      // Attach methods
      if( config.group ) {
        this.__attachGroupMethods( clazz, config, prefix, postfix );
      } else {
        this.__attachPropertyMethods( clazz, config, prefix, postfix );
      }
    },

    /**
     * Attach group methods
     *
     * @param clazz {Class} Class to attach properties to
     * @param config {Map} Property configuration
     * @param prefix {String} Prefix of property e.g. "__" or "_" for private or protected properties
     * @param postfix {String} Camelcase name of property e.g. name=width => postfix=Width
     */
    __attachGroupMethods : function( clazz, config, prefix, postfix ) {
      var members = clazz.prototype;
      var name = config.name;
      var themeable = config.themeable === true;

      var method = this.$$method;

      if( rwt.util.Variant.isSet( "qx.debug", "on" ) ) {
        for( var i = 0, prop = config.group, l = prop.length; i < l; i++ ) {
          if( !method.set[ prop[ i ] ] || !method.reset[ prop[ i ] ] ) {
            throw new Error( "Cannot create property group '" + name + "' including non-existing property '" + prop[ i ] + "'!" );
          }
          if( themeable && !method.style[ prop[ i ] ] ) {
            throw new Error( "Cannot add the non themable property '" + prop[ i ] + "' to the themable property group '" + name + "'" );
          }
        }
      }

      // Attach setter
      method.set[ name ] = prefix + "set" + postfix;
      members[ method.set[ name ] ] = function() {
        var a = arguments[ 0 ] instanceof Array ? arguments[ 0 ] : arguments;
        if( config.mode == "shorthand" ) {
          a = rwt.util.Arrays.fromShortHand( rwt.util.Arrays.fromArguments( a ) );
        }
        for( var i = 0, prop = config.group, l = prop.length; i < l; i++ ) {
          this[ method.set[ prop[ i ] ] ]( a[ i ] );
        }
      };
      // Attach resetter
      method.reset[ name ] = prefix + "reset" + postfix;
      members[ method.reset[ name ] ] = function() {
        for( var i = 0, prop = config.group, l = prop.length; i < l; i++ ) {
          this[ method.reset[ prop[ i ] ] ]();
        }
      };
      if( themeable ) {
        // Attach styler
        method.style[ name ] = prefix + "style" + postfix;
        members[ method.style[ name ] ] = function() {
          var a = arguments[ 0 ] instanceof Array ? arguments[ 0 ] : arguments;
          if( config.mode == "shorthand" ) {
            a = rwt.util.Arrays.fromShortHand( rwt.util.Arrays.fromArguments( a ) );
          }
          for( var i = 0, prop = config.group, l = prop.length; i < l; i++ ) {
            this[ method.style[ prop[ i ] ] ]( a[ i ] );
          }
        };
        // Attach unstyler
        method.unstyle[ name ] = prefix + "unstyle" + postfix;
        members[ method.unstyle[ name ] ] = function() {
          for( var i = 0, prop = config.group, l = prop.length; i < l; i++ ) {
            this[ method.unstyle[ prop[ i ] ] ]();
          }
        };
      }
    },

    /**
     * Attach property methods
     *
     * @param clazz {Class} Class to attach properties to
     * @param config {Map} Property configuration
     * @param prefix {String} Prefix of property e.g. "__" or "_" for private or protected properties
     * @param postfix {String} Camelcase name of property e.g. name=width => postfix=Width
     */
    __attachPropertyMethods : function( clazz, config, prefix, postfix ) {
      var members = clazz.prototype;
      var name = config.name;

      // Fill dispose value
      if( config.dispose === undefined && typeof config.check === "string" ) {
        config.dispose = this.__dispose[ config.check ] || rwt.qx.Class.isDefined( config.check );
      }

      var method = this.$$method;
      var store = this.$$store;

      store.user[ name ] = "__user$" + name;
      store.theme[ name ] = "__theme$" + name;
      store.init[ name ] = "__init$" + name;
      store.inherit[ name ] = "__inherit$" + name;
      store.useinit[ name ] = "__useinit$" + name;

      method.get[name] = prefix + "get" + postfix;
      members[method.get[name]] = function() {
        return rwt.qx.Property.executeOptimizedGetter( this, clazz, name, "get" );
      };

      method.set[name] = prefix + "set" + postfix;
      members[method.set[name]] = function() {
        return rwt.qx.Property.executeOptimizedSetter( this, clazz, name, "set", arguments );
      };

      method.reset[name] = prefix + "reset" + postfix;
      members[method.reset[name]] = function() {
        return rwt.qx.Property.executeOptimizedSetter( this, clazz, name, "reset" );
      };

      if( config.inheritable || config.apply || config.event || config.deferredInit ) {
        method.init[name] = prefix + "init" + postfix;
        members[method.init[name]] = function() {
          return rwt.qx.Property.executeOptimizedSetter( this, clazz, name, "init", arguments );
        };
      }

      if( config.inheritable ) {
        method.refresh[name] = prefix + "refresh" + postfix;
        members[method.refresh[name]] = function() {
          return rwt.qx.Property.executeOptimizedSetter( this, clazz, name, "refresh", arguments );
        };
      }

      if( config.themeable ) {
        method.style[name] = prefix + "style" + postfix;
        members[method.style[name]] = function() {
          return rwt.qx.Property.executeOptimizedSetter( this, clazz, name, "style", arguments );
        };

        method.unstyle[name] = prefix + "unstyle" + postfix;
        members[method.unstyle[name]] = function() {
          return rwt.qx.Property.executeOptimizedSetter( this, clazz, name, "unstyle" );
        };
      }

      if( config.check === "Boolean" ) {
        members[prefix + "toggle" + postfix] = function() {
          return this[method.set[name]](!this[method.get[name]]());
        };
        members[prefix + "is" + postfix] = function() {
          return this[method.get[name]]();
        };
      }
    },

    /** {Map} Internal data field for error messages used by {@link #error} */
    __errors : {
      0 : 'Could not change or apply init value after constructing phase!',
      1 : 'Requires exactly one argument!',
      2 : 'Undefined value is not allowed!',
      3 : 'Does not allow any arguments!',
      4 : 'Null value is not allowed!',
      5 : 'Is invalid!'
    },

    /**
     * Error method used by the property system to report errors.
     *
     * @param obj {rwt.qx.Object} Any qooxdoo object
     * @param id {Integer} Numeric error identifier
     * @param property {String} Name of the property
     * @param variant {String} Name of the method variant e.g. "set", "reset", ...
     * @param value {var} Incoming value
     */
    error : function( obj, id, property, variant, value ) {
      var classname = obj.constructor.classname;
      var msg = "Error in property " + property + " of class " + classname + " in method " + this.$$method[variant][property] + " with incoming value '" + value + "': ";

      // Additional object error before throwing exception because gecko
      // often has issues to throw the error correctly in the debug console otherwise

      throw new Error( msg + ( this.__errors[id] || "Unknown reason: " + id ) );
    },

    __executeOptimizedFunction : function( instance, members, name, variant, func, args ) {
      var store = this.$$method[ variant ][ name ];

      members[store] = func;

      // Executing new function
      if( args === undefined ) {
        return instance[ store ]();
      } else if( rwt.util.Variant.isSet( "qx.debug", "on" ) ) {
        return instance[ store ].apply( instance, args );
      } else {
        return instance[ store ]( args[ 0 ] );
      }
    },

    /**
     * Generates the optimized getter
     * Supported variants: get
     *
     * @param instance {Object} the instance which calls the method
     * @param clazz {Class} the class which originally defined the property
     * @param name {String} name of the property
     * @param variant {String} Method variant.
     * @return {var} Execute return value of apply generated function, generally the incoming value
     */
    executeOptimizedGetter : function( instance, clazz, name, variant ) {
      var config = clazz.$$properties[name];
      var members = clazz.prototype;
      var store = this.$$store;

      var func = function() {
        if( config.inheritable && this[ store.inherit[ name ] ] !== undefined ) {
          return this[ store.inherit[ name ] ];
        } else if( this[ store.user[ name ] ] !== undefined ) {
          return this[ store.user[ name ] ];
        } else if( config.themeable && this[ store.theme[ name ] ] !== undefined ) {
          return this[ store.theme[ name ] ];
        } else if( config.deferredInit && config.init === undefined && this[ store.init[ name ] ] !== undefined ) {
          return this[ store.init[ name ] ];
        } else if( config.init !== undefined ) {
          return this[ store.init[ name ] ];
        } else if( config.inheritable || config.nullable ) {
          return null;
        } else {
          throw new Error( "Property " + name + " of an instance of " + clazz.classname + " is not (yet) ready!" );
        }
      };

      return this.__executeOptimizedFunction( instance, members, name, variant, func );
    },

    /**
     * Generates the optimized setter
     * Supported variants: set, reset, init, refresh, style, unstyle
     *
     * @param instance {Object} the instance which calls the method
     * @param clazz {Class} the class which originally defined the property
     * @param name {String} name of the property
     * @param variant {String} Method variant.
     * @param args {arguments} Incoming arguments of wrapper method
     * @return {var} Execute return value of apply generated function, generally the incoming value
     */
    executeOptimizedSetter : function( instance, clazz, name, variant, args ) {
      var config = clazz.$$properties[ name ];
      var members = clazz.prototype;
      var storeInit = this.$$store.init[ name ];
      var storeInherit = this.$$store.inherit[ name ];
      var storeTheme = this.$$store.theme[ name ];
      var storeUser = this.$$store.user[ name ];
      var storeUseinit = this.$$store.useinit[ name ];
      var method = this.$$method;

      var incomingValue =    variant === "set"
                          || variant === "style"
                          || ( variant === "init" && config.init === undefined );
      var resetValue = variant === "reset" || variant === "unstyle";
      var hasCallback = config.apply || config.event || config.inheritable;

      var store = storeUser;
      if( variant === "style" || variant === "unstyle" ) {
        store = storeTheme;
      } else if( variant === "init" ) {
        store = storeInit;
      }

      var func = function( value ) {
        // [1] INTEGRATE ERROR HELPER METHOD

        // [2] PRE CONDITIONS
        var prop = rwt.qx.Property;
        var inherit = prop.$$inherit;

        if( rwt.util.Variant.isSet( "qx.debug", "on" ) && variant === "set" && value === undefined ) {
          prop.error( this, 2, name, variant, value );
        }

        // [3] PREPROCESSING INCOMING VALUE
        if( incomingValue ) {
          // Call user-provided transform method, if one is provided.  Transform
          // method should either throw an error or return the new value.
          if( config.transform ) {
            value = this[ config.transform ]( value );
          }
        }

        // [4] COMPARING (LOCAL) NEW AND OLD VALUE
        if( hasCallback ) {
          if( incomingValue ) {
            if( this[ store ] === value ) return value;
          } else if( resetValue ) {
            if( this[ store ] === undefined ) return;
          }
        }

        // [5] CHECKING VALUE
        if( incomingValue && rwt.util.Variant.isSet( "qx.debug", "on" ) ) {
          if( !config.nullable && value === null ) {
            prop.error( this, 4, name, variant, value );
          }
          // Processing check definition
          if( config.check !== undefined ) {
            if(    config.nullable && value !== null && !config.inheritable
                || !config.nullable && config.inheritable && value !== inherit
                || config.nullable && value !== null && config.inheritable && value !== inherit )
            {
              if( prop.__checks[ config.check ] !== undefined ) {
                if( !( prop.__checks[ config.check ]( value ) ) ) {
                  prop.error( this, 5, name, variant , value );
                }
              } else if( rwt.qx.Class.isDefined( config.check ) ) {
                if( !( value instanceof rwt.qx.Class.getByName( config.check ) ) ) {
                  prop.error( this, 5, name, variant , value );
                }
              } else if( typeof config.check === "function" ) {
                if( !config.check.call( this, value ) ) {
                  prop.error( this, 5, name, variant , value );
                }
              } else if( typeof config.check === "string" ) {
                if( !config.check ) {
                  prop.error( this, 5, name, variant , value );
                }
              } else if( config.check instanceof Array ) {
                if( !config.check.includes( value ) ) {
                  prop.error( this, 5, name, variant , value );
                }
              } else {
                throw new Error( "Could not execute check to property " + name + " of class " + clazz.classname );
              }
            }
          }
        }

        if( !hasCallback ) {
          switch( variant ) {
            case "set":
              this[ storeUser ] = value;
            break;
            case "reset":
              if( this[ storeUser ] !== undefined ) {
                delete this[ storeUser ];
              }
            break;
            case "style":
              this[ storeTheme ] = value;
            break;
            case "unstyle":
              if( this[ storeTheme ] !== undefined ) {
                delete this[ storeTheme ];
              }
            break;
            case "init":
              if( incomingValue ) {
                this[ storeInit ] = value;
              }
            break;
          }
        } else {
          var computed, old = config.inheritable ? this[ storeInherit ] : undefined;

          if( this[ storeUser ] !== undefined ) {
            // OLD = USER VALUE
            if( variant === "set" ) {
              if( !config.inheritable ) {
                // Remember old value
                old = this[ storeUser ];
              }
              // Replace it with new value
              computed = this[ storeUser ] = value;
            } else if( variant === "reset" ) {
              if( !config.inheritable ) {
                // Remember old value
                old = this[ storeUser ];
              }
              // Delete field
              delete this[ storeUser ];
              // Complex compution of new value
              if( this[ storeTheme ] !== undefined ) {
                computed = this[ storeTheme ];
              } else if( this[ storeInit ] !== undefined ) {
                computed = this[ storeInit ];
                this[ storeUseinit ] = true;
              }
            } else {
              // Use user value where it has higher priority
              if( config.inheritable ) {
                computed = this[ storeUser ];
              } else {
                old = computed = this[ storeUser ];
              }
              // Store incoming value
              if( variant === "style" ) {
                this[ storeTheme ] = value;
              } else if( variant === "unstyle" ) {
                delete this[ storeTheme ];
              } else if( variant === "init" && incomingValue ) {
                this[ storeInit ] = value;
              }
            }
          } else if( config.themeable && this[ storeTheme ] !== undefined ) {
            // OLD = THEMED VALUE
            if( !config.inheritable ) {
              old = this[ storeTheme ];
            }
            // reset() is impossible, because the user has higher priority than
            // the themed value, so the themed value has no chance to ever get used,
            // when there is a user value, too.
            if( variant === "set" ) {
              computed = this[ storeUser ] = value;
            } else if( variant === "style" ) {
              computed = this[ storeTheme ] = value;
            } else if( variant === "unstyle" ) {
              // Delete entry
              delete this[ storeTheme ];
              // Fallback to init value
              if( this[ storeInit ] !== undefined ) {
                computed = this[ storeInit ];
                this[ storeUseinit ] = true;
              }
            } else if( variant === "init" ) {
              if( incomingValue ) {
                this[ storeInit ] = value;
              }
              computed = this[ storeTheme ];
            } else if( variant === "refresh" ) {
              computed = this[ storeTheme ];
            }
          } else if( this[ storeUseinit ] ) {
            // OLD = INIT VALUE
            if( !config.inheritable ) {
              old = this[ storeInit ];
            }
            // reset() and unstyle() are impossible, because the user and themed values have a
            // higher priority than the init value, so the themed value has no chance to ever get used,
            // when there is a user or themed value, too.
            if( variant === "init" ) {
              if( incomingValue ) {
                computed = this[ storeInit ] = value;
              } else {
                computed = this[ storeInit ];
              }
              // useinit flag is already initialized
            } else if( variant === "set" || variant === "style" || variant === "refresh" ) {
              delete this[ storeUseinit ];
              if( variant === "set" ) {
                computed = this[ storeUser ] = value;
              } else if( variant === "style" ) {
                computed = this[ storeTheme ] = value;
              } else if( variant === "refresh" ) {
                computed = this[ storeInit ];
              }
            }
          } else {
            // OLD = NONE
            // reset() and unstyle() are impossible because otherwise there
            // is already an old value
            if( variant === "set" ) {
              computed = this[ storeUser ] = value;
            } else if( variant === "style" ) {
              computed = this[ storeTheme ] = value;
            } else if( variant === "init" ) {
              if( incomingValue ) {
                computed = this[ storeInit ] = value;
              } else {
                computed = this[ storeInit ];
              }
              this[ storeUseinit ] = true;
            }
            // refresh() will work with the undefined value, later
          }
        }
        if( config.inheritable ) {
          if( computed === undefined || computed === inherit ) {
            if( variant === "refresh" ) {
              computed = value;
            } else {
              var pa = this.getParent();
              if( pa ) {
                computed = pa[ storeInherit ];
              }
            }
            // Fallback to init value if inheritance was unsuccessful
            if(    ( computed === undefined || computed === inherit )
                && this[ storeInit ] !== undefined
                && this[ storeInit ] !== inherit )
            {
              computed = this[ storeInit ];
              this[ storeUseinit ] = true;
            } else {
              delete this[ storeUseinit ];
            }
          }
          // Compare old/new computed value
          if( old === computed ) {
            return value;
          }
          // Note: At this point computed can be "inherit" or "undefined".
          // Normalize "inherit" to undefined and delete inherited value
          if( computed === inherit ) {
            computed = undefined;
            delete this[ storeInherit ];
          } else if( computed===undefined ) {
            // Only delete inherited value
            delete this[ storeInherit ];
          } else {
            // Store inherited value
            this[ storeInherit ] = computed;
          }
          // Protect against normalization
          var backup = computed;
          // After storage finally normalize computed and old value
          if( computed === undefined ) {
            computed = null;
          }
          if( old === undefined ) {
            old = null;
          }
        } else if( hasCallback ) {
          // Properties which are not inheritable have no possiblity to get
          // undefined at this position. (Hint: set() and style() only allow non undefined values)
          if( variant !== "set" && variant !== "style" && computed === undefined ) {
            computed = null;
          }
          // Compare old/new computed value
          if( old === computed ) {
            return value;
          }
          // Normalize old value
          if( old === undefined ) {
            old = null;
          }
        }
        // [12] NOTIFYING DEPENDEND OBJECTS
        if( hasCallback ) {
          // Execute user configured setter
          if( config.apply ) {
            this[ config.apply ]( computed, old );
          }
          // Fire event
          if( config.event ) {
            this.createDispatchChangeEvent( config.event, computed, old );
          }
          // Refresh children
          // Require the parent/children interface
          if( config.inheritable && members.getChildren ) {
            var children = this.getChildren();
            if( children ) {
              for( var i = 0, l = children.length; i < l; i++ ) {
                if( children[ i ][ method.refresh[ name ] ] ) {
                  children[ i ][ method.refresh[ name ] ]( backup );
                }
              }
            }
          }
        }
        // [13] RETURNING WITH ORIGINAL INCOMING VALUE
        // Return value
        if( incomingValue ) {
          return value;
        }
      };

      return this.__executeOptimizedFunction( instance, members, name, variant, func, args );
    }
  }

} );
