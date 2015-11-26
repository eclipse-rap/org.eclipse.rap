/*******************************************************************************
 * Copyright (c) 2010, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function() {

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var Client = rwt.client.Client;
var Style = rwt.html.Style;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.StyleTest", {

  extend : rwt.qx.Object,

  members : {

    testSetStyleProperty_onWidget : function() {
      var red = "red";
      var widget = this._createWidget();

      Style.setStyleProperty( widget, "backgroundColor", red);

      assertEquals( red, TestUtil.getCssBackgroundColor( widget ) );
      widget.destroy();
    },

    testSetStyleProperty_onElement : function() {
      var red = "red";
      var element = document.createElement( "div" );

      Style.setStyleProperty( element, "backgroundColor", red );

      assertEquals( red, element.style.backgroundColor );
    },

    testGetStyleProperty_inlineStyleWithoutParent : function() {
      var element = document.createElement( "div" );
      Style.setStyleProperty( element, "backgroundColor", "#ff0000" );

      var result = Style.getStyleProperty( element, "backgroundColor" );

      assertEquals( [255, 0, 0], rwt.util.Colors.stringToRgb( result ) );
    },

    testGetStyleProperty_inheritedStyle : function() {
      var element = document.createElement( "div" );
      var child = document.createElement( "div" );
      Style.setStyleProperty( element, "backgroundColor", "#ff0000" );
      document.body.appendChild( element );
      element.appendChild( child );

      Style.setStyleProperty( child, "backgroundColor", "inherit" );
      var result = Style.getStyleProperty( child, "backgroundColor" );

      assertEquals( [255, 0, 0], rwt.util.Colors.stringToRgb( result ) );
      document.body.removeChild( element );
    },

    testRemoveStyleProperty_onWidget : function() {
      var red = "red";
      var widget = this._createWidget();
      Style.setStyleProperty( widget, "backgroundColor", red );

      Style.removeStyleProperty( widget, "backgroundColor" );

      assertNull( TestUtil.getCssBackgroundColor( widget ) );
      widget.destroy();
    },

    testRemoveStyleProperty_onElement : function() {
      var red = "red";
      var element = document.createElement( "div" );
      Style.setStyleProperty( element, "backgroundColor", red );

      Style.removeStyleProperty( element, "backgroundColor" );

      assertEquals( "", element.style.backgroundColor );
    },

    testSetStyleProperty_onWidgetBeforeCreate : function() {
      var widget = this._createWidget( true );
      var red = "red";

      Style.setStyleProperty( widget, "foo", red );
      TestUtil.flush();

      assertEquals( red, widget._style.foo );
      widget.destroy();
    },

    testRemoveStyleProperty_onWidgetBeforeCreate : function() {
      var red = "red";
      var widget = this._createWidget( true );
      Style.setStyleProperty( widget, "foo", red );

      Style.removeStyleProperty( widget, "foo" );
      TestUtil.flush();

      assertEquals( undefined, widget._style.foo );
      widget.destroy();
    },

    testSetTextShadow_toNull : function() {
      var widget = this._createWidget( true );
      var shadow = [ false, 1, 1, 0, 0, "#ff0000", 0.5 ];
      widget.setTextShadow( shadow );
      TestUtil.flush();
      var element = widget.getElement();
      var css = element.style.cssText.toLowerCase();
      if( Client.isTrident() && Client.getMajor() < 10 ) {
        assertFalse( css.indexOf( "text-shadow:" ) !== -1 );
      } else {
        assertTrue( css.indexOf( "text-shadow:" ) !== -1 );
      }

      widget.setTextShadow( null );
      TestUtil.flush();

      css = element.style.cssText.toLowerCase();
      assertTrue( css.indexOf( "text-shadow:" ) === -1 );
      widget.destroy();
    },

    testSetBackgroundImage : function() {
      var el = document.createElement( "div" );

      Style.setBackgroundImage( el, "foo.png" );

      var actual = TestUtil.getCssBackgroundImage( el );
      if( Client.isWebkit() || Client.isBlink() ) {
        assertTrue( actual.indexOf( "foo.png" ) !== -1 );
      } else {
        assertEquals( "foo.png", actual );
      }
    },

    testSetBackgroundImage_externalImage : function() {
      var el = document.createElement( "div" );

      Style.setBackgroundImage( el, "http://foo.org/bar.png" );

      var actual = TestUtil.getCssBackgroundImage( el );
      assertEquals( "http://foo.org/bar.png", actual );
    },

    /////////
    // Helper

    _createWidget : function( noFlush ) {
      var result = new rwt.widgets.base.MultiCellWidget( [] );
      result.addToDocument();
      result.setLocation( 0, 0 );
      result.setDimension( 100, 100 );
      if( noFlush !== true ) {
        rwt.widgets.base.Widget.flushGlobalQueues();
      }
      return result;
    }

  }

} );

}() );
