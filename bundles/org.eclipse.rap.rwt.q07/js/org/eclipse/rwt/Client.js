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
 * Basic client detection implementation.
 *
 * Version names follow the wikipedia scheme: major.minor[.revision[.build]] at
 * http://en.wikipedia.org/wiki/Software_version
 */
qx.Class.define( "org.eclipse.rwt.Client", {
  
  statics : {

    __init : function() {
      this._engineName = null;
      this._browserName = null;
      this._engineVersion = null;
      this._engineVersionMajor = 0;
      this._engineVersionMinor = 0;
      this._engineVersionRevision = 0;
      this._engineVersionBuild = 0;
      this._browserPlatform = null;      
      this._runsLocally = window.location.protocol === "file:";
      this._engineQuirksMode = document.compatMode !== "CSS1Compat";
      this._defaultLocale = "en";
      // NOTE: Order is important!
      this._initOpera();
      this._initKonqueror();
      this._initWebkit();
      this._initGecko();
      this._initMshtml();
      this._initBoxSizing();
      this._initLocale();      
      this._initPlatform();
    },

    getRunsLocally : function() {
      return this._runsLocally;
    },

    getEngine : function() {
      return this._engineName;
    },

    getBrowser : function() {
      return this._browserName;
    },

    getVersion : function() {
      return this._engineVersion;
    },

    getMajor : function() {
      return this._engineVersionMajor;
    },

    getMinor : function() {
      return this._engineVersionMinor;
    },

    getRevision : function() {
      return this._engineVersionRevision;
    },

    getBuild : function() {
      return this._engineVersionBuild;
    },

    isMshtml : function() {
      return this._engineName === "mshtml";
    },

    isGecko : function() {
      return this._engineName === "gecko";
    },

    isOpera : function() {
      return this._engineName === "opera";
    },

    isWebkit : function() {
      return this._engineName === "webkit";
    },

    isInQuirksMode : function() {
      return this._engineQuirksMode;
    },

    getLocale : function() {
      return this._browserLocale;
    },
    
    getLanguage : function() {
      var locale = this.getLocale();
      var language;
      var pos = locale.indexOf( "_" );
      if( pos == -1 ) {
        language = locale;
      } else {
        language = locale.substring( 0, pos );
      }
      return language;
    },
    
    getTerritory : function() {
      return this.getLocale().split( "_" )[ 1 ] || "";
    },

    getDefaultLocale : function() {
      return this._defaultLocale;
    },

    usesDefaultLocale : function() {
      return this._browserLocale === this._defaultLocale;
    },

    getEngineBoxSizingAttributes : function() {
      return this._engineBoxSizingAttributes;
    },

    getPlatform : function() {
      return this._browserPlatform;
    },

    isMobileSafari : function() {
      return this.getPlatform() === "ios" && this.getBrowser() === "safari";
    },
    
    isAndroidBrowser : function() {
      return this.getPlatform() === "android" && this.getBrowser() === "chrome";
    },

    supportsVml : function() {
      return ( this.getEngine() === "mshtml" ) && ( this.getVersion() >= 5.5 );
    },

    supportsSvg : function() {
      // NOTE: IE9 supports SVG, but not in quirksmode.
      var engine = org.eclipse.rwt.Client.getEngine();
      var version = org.eclipse.rwt.Client.getVersion();
      var result =    engine === "gecko" && version >= 1.8
                   || engine === "webkit" && version >= 523 
                   || engine === "opera" && version >= 9;
      if( this.isAndroidBrowser() ) {
        result = false;
      }
      return result;
    },
    
    // NOTE: This returns true if the browser sufficiently implements 
    // border-radius, drop-shadow and linear-gradient. IE9 (currently) ignored.
    supportsCss3 : function() {
      var engine = org.eclipse.rwt.Client.getEngine();
      var version = org.eclipse.rwt.Client.getVersion();
      var result =    engine === "webkit" && version >= 522
                   || engine === "gecko" && version >= 1.9;
      return result;
    },

    //////////
    // Helper
    
    _initOpera : function() {
      if( this._engineName === null ) {
        var isOpera =    window.opera 
                       && /Opera[\s\/]([0-9\.]*)/.test( navigator.userAgent );
        if( isOpera ) {
          this._browserName = "opera";
          this._engineName = "opera";
          var version = RegExp.$1;
          // Fix Opera version to match wikipedia style
          version = version.substring( 0, 3 ) + "." + version.substring ( 3);
          this._parseVersion( version );
        }
      }
    },

    _initKonqueror : function() {
      if( this._engineName === null ) {
        var vendor = navigator.vendor;
        var isKonqueror =    typeof vendor === "string" && vendor === "KDE" 
                          && /KHTML\/([0-9-\.]*)/.test( navigator.userAgent );
        if( isKonqueror ) {
          this._engineName = "webkit";
          this._browserName = "konqueror";
          // Howto translate KDE Version to Webkit Version? Currently emulate Safari 3.0.x for all versions.
          // this._engineVersion = RegExp.$1;
          this._parseVersion( "420" );
        }
      }
    },

    _initWebkit : function() {
      if( this._engineName === null ) {
        var userAgent = navigator.userAgent;
        var isWebkit =    userAgent.indexOf( "AppleWebKit" ) != -1
                       && /AppleWebKit\/([^ ]+)/.test( userAgent );
        if( isWebkit ) {
          this._engineName = "webkit";
          var version = RegExp.$1;
          var invalidCharacter = RegExp("[^\\.0-9]").exec( version );
          if( invalidCharacter ) {
            version = version.slice( 0, invalidCharacter.index );
          }
          this._parseVersion( version );
          if( userAgent.indexOf( "Chrome" ) != -1 ) {
            this._browserName = "chrome";
          } else if( userAgent.indexOf( "Safari" ) != -1 ) {
            if( userAgent.indexOf( "Android" ) != -1 ) {
              this._browserName = "chrome";
            } else {
              this._browserName = "safari";              
            }
          } else if( userAgent.indexOf( "OmniWeb" ) != -1 ) {
            this._browserName = "omniweb";
          } else if( userAgent.indexOf( "Shiira" ) != -1 ) {
            this._browserName = "shiira";
          } else if( userAgent.indexOf( "NetNewsWire" ) != -1 ) {
            this._browserName = "netnewswire";
          } else if( userAgent.indexOf( "RealPlayer" ) != -1 ) {
            this._browserName = "realplayer";
          } else {
            this._browserName = "other webkit";
          }
        }
      }
    },
    
    _initGecko : function() {
      if( this._engineName === null ) {
        var product = navigator.product;
        var userAgent = navigator.userAgent;
        var isGecko =    window.controllers 
                      && typeof product === "string" 
                      && product === "Gecko" 
                      && /rv\:([^\);]+)(\)|;)/.test( userAgent );
        if( isGecko ) {
          // http://www.mozilla.org/docs/dom/domref/dom_window_ref13.html
          this._engineName = "gecko";
          this._parseVersion( RegExp.$1 );
          if( userAgent.indexOf( "Firefox" ) != -1) {
            this._browserName = "firefox";
          } else if ( userAgent.indexOf( "Camino" ) != -1) {
            this._browserName = "camino";
          } else if ( userAgent.indexOf( "Galeon" ) != -1) {
            this._browserName = "galeon";
          } else {
            this._browserName = "other gecko";
          }
        }
      }
    },
    
    _initMshtml : function() {
      if( this._engineName === null ) {
        var isMshtml = /MSIE\s+([^\);]+)(\)|;)/.test( navigator.userAgent );
        if( isMshtml ) {
          this._engineName = "mshtml";
          this._parseVersion( RegExp.$1 );
          this._browserName = "explorer";
        }
      }
    },
    
    _parseVersion : function( versionStr ) {
      if( typeof versionStr === "string" ) {
        versionArr = versionStr.split( "." );
        this._engineVersion = parseFloat( versionStr );
        this._engineVersionMajor = parseInt( versionArr[ 0 ] || 0 );
        this._engineVersionMinor = parseFloat( versionArr[ 1 ] || 0 );
        this._engineVersionRevision = parseFloat( versionArr[ 2 ] || 0 );
        this._engineVersionBuild = parseInt( versionArr[ 3 ] || 0 );
      }
    },
    
    _initBoxSizing : function() {      
      var vEngineBoxSizingAttr = [];
      switch( this._engineName ) {
        case "gecko":
          vEngineBoxSizingAttr.push( "-moz-box-sizing" );
        break;
        case "webkit":
          vEngineBoxSizingAttr.push( "-khtml-box-sizing" );
          vEngineBoxSizingAttr.push( "-webkit-box-sizing" );
        break;
      }
      vEngineBoxSizingAttr.push( "box-sizing" );
      this._engineBoxSizingAttributes = vEngineBoxSizingAttr;
    },    
    
    _initLocale : function() {
      var language =   this._engineName == "mshtml" 
                     ? navigator.userLanguage 
                     : navigator.language;
      var browserLocale = language.toLowerCase();
      var browserLocaleVariantIndex = browserLocale.indexOf( "-" );
      if( browserLocaleVariantIndex != -1 ) {
        browserLocale = browserLocale.substr( 0, browserLocaleVariantIndex );
      }
      this._browserLocale = browserLocale;
    },

    _initPlatform : function() {
      var platformStr = navigator.platform;
      if(    platformStr.indexOf( "Windows" ) != -1 
          || platformStr.indexOf( "Win32" ) != -1 
          || platformStr.indexOf( "Win64" ) != -1 )
      {
        this._browserPlatform = "win";
      } else if(    platformStr.indexOf( "Macintosh" ) != -1 
                 || platformStr.indexOf( "MacPPC" ) != -1 
                 || platformStr.indexOf( "MacIntel" ) != -1 )
      {
        this._browserPlatform = "mac";
      }else if(    platformStr.indexOf( "X11" ) != -1 
                || platformStr.indexOf( "Linux" ) != -1 
                || platformStr.indexOf( "BSD" ) != -1 )
      {
        if( navigator.userAgent.indexOf( "Android" ) != -1 ) {
          this._browserPlatform = "android";
        } else {
          this._browserPlatform = "unix";          
        }
      } else if(    platformStr.indexOf( "iPhone" ) != -1 
                 || platformStr.indexOf( "iPod" ) != -1  
                 || platformStr.indexOf( "iPad" ) != -1 )  
      {
        this._browserPlatform = "ios";
      } else {
        this._browserPlatform = "other";
      }
      
    }

  },

  defer : function( statics, members, properties ) {
    statics.__init();
    qx.core.Variant.define( "qx.client", 
                            [ "gecko", "mshtml", "opera", "webkit" ], 
                            org.eclipse.rwt.Client.getEngine() );
  }
} );
