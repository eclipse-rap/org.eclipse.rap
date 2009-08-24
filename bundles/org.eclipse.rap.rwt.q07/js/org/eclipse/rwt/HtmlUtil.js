/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/


qx.Class.define( "org.eclipse.rwt.HtmlUtil", {

  statics : {
    __helperDiv : null,
    __attributeHelper : new qx.util.StringBuilder(),
    __innerHtmlHelper : new qx.util.StringBuilder(),
    __styleRegExp : /([a-z])([A-Z])/g,

    getHelperDiv : function() {
      if( !this.__helperDiv ) {
        this.__helperDiv = document.createElement( "div" );
      }
      return this.__helperDiv;
    },

    createHtmlData : function( tag ) {
      var data = {
        startTagOpen : "<" + tag + " ",
        styleOpen : "style='",
        styleInner : [ {} ],
        styleClose : "' ",
        attributes : [ {} ],
        startTagClose : " >",
        innerHtml : [],
        endTag : "</" + tag + ">"
      };
      return data;
    },

    // maps should not contain attributes that are also other maps, or
    // they will be in the string twice! No checks there!
    // Multiple maps are only supported to avoid the copy-process
    // while also not touching the map. (Same for style-properties!)
    addHtmlAttributes : function( htmlData, map ) {
      htmlData.attributes.push( map );
    },

    addStyleProperties : function( htmlData, map ) {
      htmlData.styleInner.push( map );
    },

    setHtmlAttribute : function( htmlData, attribute, value ) {
      htmlData.attributes[ 0 ][ attribute ] = value;
    },

    setStyleProperty : function( htmlData, attribute, value ) {
      htmlData.styleInner[ 0 ][ attribute ] = value;
    },

    addInnerHtml : function( htmlData, htmlDataInner ) {
      htmlData.innerHtml.push( htmlDataInner );
    },

    _joinHtmlAttributes : function( maps ) {
      var str = this.__attributeHelper;
      str.clear();
      var l = maps.length;
      var map = null;
      for( var i = 0; i < l; i++ ) {
        var map = maps[ i ];
        for( var attribute in map ) {
          str.add( attribute, '="', map[ attribute ], '" ');
        }
      }
      return str.get();
    },

    _joinStyleProperties : function( maps ) {
      // targetNode support with filter!
      var str = this.__attributeHelper;
      str.clear();
      var l = maps.length;
      var map = null;
      var value = null;
      for( var i = 0; i < l; i++ ) {
        var map = maps[ i ];
        for( var attribute in map ) {
          var value = map[ attribute ];
          if( value ) {
            str.add( attribute, ':', map[ attribute ], ';');
          }
        }
      }
      return str.get().replace( this.__styleRegExp, "$1-$2" ).toLowerCase();
    },

    _joinHtmlData : function( htmlData ) {
      this.__innerHtmlHelper.add( this.getStartTag( htmlData ) );
      this._joinInnerHtmlData( htmlData );
      this.__innerHtmlHelper.add( this.getEndTag( htmlData ) );
    },

    _joinInnerHtmlData : function( htmlData ) {
      var inner = htmlData.innerHtml;
      var l = inner.length;
      var data = null;
      for( var i = 0; i < l; i++ ) {
        data = inner[ i ];
        if( data instanceof Object ) {
          this._joinHtmlData( data );
        } else {
          this.__innerHtmlHelper.add( data );
        }
      }
    },

    getHtml : function( htmlData ) {
      var str = this.__innerHtmlHelper;
      str.clear();
      this._joinHtmlData( htmlData );
      return str.get();
    },

    getInnerHtml : function( htmlData ) {
      this.__innerHtmlHelper.clear();
      this._joinInnerHtmlData( htmlData );
      return this.__innerHtmlHelper.get();
    },

    getStartTag : function( htmlData ) {
      return   htmlData.startTagOpen
             + htmlData.styleOpen
             + this._joinStyleProperties( htmlData.styleInner )
             + htmlData.styleClose
             + this._joinHtmlAttributes( htmlData.attributes )
             + htmlData.startTagClose;
    },


    getEndTag : function( htmlData ) {
      return  htmlData.endTag;
    },

    createDOM : qx.core.Variant.select( "qx.client", {
      "mshtml" : function( htmlData ) {
        var outer = this.getStartTag( htmlData ) + this.getEndTag( htmlData );
        var inner = this.getInnerHtml( htmlData );
        var node = document.createElement( outer );
        if( inner != "" ) {
          node.innerHTML = inner;
        }
        return node;
      },
      "default" : function( htmlData ) {
        var html = this.getHtml( htmlData );
        var helper = this.getHelperDiv();
        helper.innerHTML = html;
        var node = helper.firstChild;
        helper.innerHTML = "";
        return node;
      }
    } ),

    NO_USER_SELECT :  qx.core.Variant.select( "qx.client", {
      "mshtml" : "",
      "gecko" : "-moz-user-select:none;",
      "webkit": "-webkit-user-select:none;-khtml-user-select:none;",
      "default" : "user-select:none;"
    } )
  }
} );
