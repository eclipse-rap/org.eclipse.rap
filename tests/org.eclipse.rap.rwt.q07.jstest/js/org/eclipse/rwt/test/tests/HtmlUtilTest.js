/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.HtmlUtilTest", {
  extend : qx.core.Object,

  members : {
    
    testSetStylePropertyOnWidget : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var htmlUtil = org.eclipse.rwt.HtmlUtil;
      var red = "red";
      var widget = this._createWidget();
      htmlUtil.setStyleProperty( widget, "backgroundColor", red);
      assertEquals( red, testUtil.getCssBackgroundColor( widget ) );
      widget.destroy();
    },
    
    testSetStylePropertyOnElement : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var htmlUtil = org.eclipse.rwt.HtmlUtil;
      var red = "red";
      var element = document.createElement( "div" );
      htmlUtil.setStyleProperty( element, "backgroundColor", red );
      assertEquals( red, element.style.backgroundColor );
    },
    
    testRemoveStylePropertyOnWidget : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var htmlUtil = org.eclipse.rwt.HtmlUtil;
      var red = "red";
      var widget = this._createWidget();
      htmlUtil.setStyleProperty( widget, "backgroundColor", red );
      htmlUtil.removeStyleProperty( widget, "backgroundColor" );
      assertNull( testUtil.getCssBackgroundColor( widget ) );
      widget.destroy();
    },

    testRemoveStylePropertyOnElement : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var htmlUtil = org.eclipse.rwt.HtmlUtil;
      var red = "red";
      var element = document.createElement( "div" );
      htmlUtil.setStyleProperty( element, "backgroundColor", red );
      htmlUtil.removeStyleProperty( element, "backgroundColor" );
      assertEquals( "", element.style.backgroundColor );
    },
    
    testSetStylePropertyOnWidgetBeforeCreate : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var htmlUtil = org.eclipse.rwt.HtmlUtil;
      var widget = this._createWidget( true );
      var red = "red";
      htmlUtil.setStyleProperty( widget, "backgroundColor", red);
      testUtil.flush();
      assertEquals( red, testUtil.getCssBackgroundColor( widget ) );
      widget.destroy();
    },
    
    testRemoveStylePropertyOnWidgetBeforeCreate : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var htmlUtil = org.eclipse.rwt.HtmlUtil;
      var red = "red";
      var widget = this._createWidget( true );
      htmlUtil.setStyleProperty( widget, "backgroundColor", red );
      htmlUtil.removeStyleProperty( widget, "backgroundColor" );
      testUtil.flush();
      assertNull( testUtil.getCssBackgroundColor( widget ) );
      widget.destroy();
    },

    testSetOpacity : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var htmlUtil = org.eclipse.rwt.HtmlUtil;
      var element = document.createElement( "div" );
      var widget = this._createWidget();
      htmlUtil.setOpacity( element, 0.5 );
      htmlUtil.setOpacity( widget, 0.5 );
      assertTrue( testUtil.hasElementOpacity( element ) );
      assertTrue( testUtil.hasElementOpacity( widget.getElement() ) );
      htmlUtil.setOpacity( element, 1 );
      htmlUtil.setOpacity( widget, 1 );
      assertFalse( testUtil.hasElementOpacity( element ) );
      assertFalse( testUtil.hasElementOpacity( widget.getElement() ) );
      var css1 = element.style.cssText.toLowerCase();
      var css2 = widget.getElement().style.cssText.toLowerCase();
      // additional check for IE:
      assertTrue( css1.indexOf( "filter" ) == -1 );
      assertTrue( css2.indexOf( "filter" ) == -1 );      
      widget.destroy();
    },
    
    testSetOpacityBeforeWidgetCreation : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var htmlUtil = org.eclipse.rwt.HtmlUtil;
      var widgetNormal = this._createWidget( true );
      var widgetTransp = this._createWidget( true );
      widgetNormal.setOpacity( 0.5 );
      widgetTransp.setOpacity( 0.5 );
      widgetNormal.setOpacity( 1 );
      testUtil.flush();
      assertFalse( testUtil.hasElementOpacity( widgetNormal.getElement() ) );
      assertTrue( testUtil.hasElementOpacity( widgetTransp.getElement() ) );
      // additional check for IE:
      var css = widgetNormal.getElement().style.cssText.toLowerCase();
      assertTrue( css.indexOf( "filter" ) == -1 );
      widgetNormal.destroy();
      widgetTransp.destroy();
    },
    
    /////////
   // Helper
        
    _createWidget : function( noFlush ) {
      var result = new org.eclipse.rwt.widgets.MultiCellWidget( [] );
      result.addToDocument();
      result.setLocation( 0, 0 );
      result.setDimension( 100, 100 );
      if( noFlush !== true ) {
        qx.ui.core.Widget.flushGlobalQueues();
      }
      return result;
    }

  }
} );