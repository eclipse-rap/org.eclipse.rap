/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
/*global console:false, org:false */
(function() {
  var getPropsFromCode = function( fun ) {
    var reg = /properties\.\w+/g;
    var matches = fun.toString().match( reg );
    if( matches === null ) {
      matches = [];
    } else {
      matches = matches.sort();
    }
    var lastMatch = null;
    var result = [];
    while( matches.length > 0 ) {
      var prop = matches.pop().slice( 11 );
      if( prop !== lastMatch ) {
      result.push( prop );
      }
      lastMatch = prop;
    }
    return result;
  };

  var nl = "\n";
  var lv1 = "  ";
  var lv2 = "    ";

  var registry = rwt.remote.HandlerRegistry._registry;
  var text = "RAP Protocol Reference" + nl + nl;
  for( var key in registry ) {
    try {
      var adapter = registry[ key ];
      text += "Type \"" + key + "\"" + nl;
      var factoryProps = getPropsFromCode( adapter.factory );
      if( factoryProps.length > 0 ) {
        text += lv1 + "Contructor-Properties: \"" + factoryProps.join( "\", \"" ) + "\"" + nl;
      }
      if( adapter.properties.length > 0 ) {
        text += lv1 + "Properties: \"" + adapter.properties.sort().join( "\", \"" ) + "\"" + nl;
      }
      if( adapter.knownListeners && adapter.knownListeners.length > 0 ) {
        text += lv1 + "Events: \"" + adapter.knownListeners.sort().join( "\", \"" ) + "\"" + nl;
      }
      if( adapter.methods && adapter.methods.length > 0 ) {
        var methods = adapter.methods.sort();
        text += lv1 + "Methods: " + nl;
        for( var i = 0; i < methods.length; i++ ) {
          text += lv2 + "- \"" + methods[ i ] + "\"";
          var handler = adapter.methodHandler && adapter.methodHandler[ [ methods[ i ] ] ];
          if( handler ) {
            var methodProps = getPropsFromCode( adapter.methodHandler[ [ methods[ i ] ] ] );
            if( methodProps.length > 0 ) {
              text += " (Properties: \"" + methodProps.join( "\", \"" ) + "\")";
            }
          }
          text += nl;
        }
      }
      text += nl;
    } catch( ex ) {
      console.log( ex );
    }
  }
  console.log( text );
}());
