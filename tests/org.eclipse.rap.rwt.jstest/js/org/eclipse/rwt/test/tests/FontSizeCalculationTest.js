/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

(function(){

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var MessageProcessor = rwt.remote.MessageProcessor;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.FontSizeCalculationTest", {

  extend : rwt.qx.Object,

  members : {

    testMeasureFontByProtocol : function() {
      var text
        = "!#$%&()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxy";
      var fontName = [ "Verdana", "Lucida Sans", "Arial", "Helvetica", "sans-serif" ];
      TestUtil.initRequestLog();

      MessageProcessor.processOperation( {
        "target" : "rwt.client.TextSizeMeasurement",
        "action" : "call",
        "method" : "measureItems",
        "properties" : {
          "items" : [
             [ -785380229, text, fontName, 11, false, false ],
             [ -785380485, text, fontName, 12, false, false ]
          ]
        }
      } );

      assertEquals( 1, TestUtil.getRequestsSend() ); // because timer is skipped in tests
      var message = TestUtil.getMessageObject();
      var op = message.findCallOperation( "rwt.client.TextSizeMeasurement", "storeMeasurements" );
      assertEquals( 2, op.properties.results[ "-785380229" ].length );
      assertEquals( 2, op.properties.results[ "-785380485" ].length );
    },

    testMeasureStringsByProtocol : function() {
      var fontName = [ "Verdana", "Lucida Sans", "Arial", "Helvetica", "sans-serif" ];
      TestUtil.initRequestLog();

      MessageProcessor.processOperation( {
        "target" : "rwt.client.TextSizeMeasurement",
        "action" : "call",
        "method" : "measureItems",
        "properties" : {
          "items" : [
             [ -1114032847, "Check", fontName, 12, false, false, -1 ],
             [ 1767849485, "  Push &&\n Button ", fontName, 12, false, false, -1 ]
          ]
        }
      } );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      var op = message.findCallOperation( "rwt.client.TextSizeMeasurement", "storeMeasurements" );
      assertEquals( 2, op.properties.results[ "-1114032847" ].length );
      assertEquals( 2, op.properties.results[ "1767849485" ].length );
    },


    testSizeWithSequentialWhitespacesNoWrap : function() {
      var FontSizeCalculation = rwt.widgets.util.FontSizeCalculation;
      var item1 = [ "id1", "foo bar", "Arial", 12, false, false, -1 ];
      var item2 = [ "id2", "foo  bar", "Arial", 12, false, false, -1 ];
      var item3 = [ "id3", " foo bar", "Arial", 12, false, false, -1 ];

      var size1 = FontSizeCalculation._measureItem( item1, true );
      var size2 = FontSizeCalculation._measureItem( item2, true );
      var size3 = FontSizeCalculation._measureItem( item3, true );

      assertTrue( size1[ 0 ] < size2[ 0 ] );
      assertTrue( size2[ 0 ] === size3[ 0 ] );
    },

    testSizeWithSequentialWhitespacesWrap : function() {
      var FontSizeCalculation = rwt.widgets.util.FontSizeCalculation;
      var item1 = [ "id1", "foo bar", "Arial", 12, false, false, -1 ];
      var item2 = [ "id2", "foo bar", "Arial", 12, false, false, 20 ];
      var item3 = [ "id3", "foo      bar", "Arial", 12, false, false, 20 ];

      var size1 = FontSizeCalculation._measureItem( item1, true );
      var size2 = FontSizeCalculation._measureItem( item2, true );
      var size3 = FontSizeCalculation._measureItem( item3, true );

      assertTrue( size1[ 0 ] > size2[ 0 ] );
      assertTrue( size1[ 1 ] < size2[ 1 ] );
      // TODO: div is resized in IE8 even the style.width (wrap width) is fixed
      if( !rwt.client.Client.isMshtml() ) {
        assertTrue( size2[ 0 ] === size3[ 0 ] );
      }
      assertTrue( size2[ 1 ] === size3[ 1 ] );
    },

    testMeasureVeryLongTextWithoutWrap : function() {
      var FontSizeCalculation = rwt.widgets.util.FontSizeCalculation;
      var veryLongText = "";
      for( var i = 0; i < 10; i++ ) {
        veryLongText += "foo bar foo bar foo bar foo bar foo bar foo bar foo bar foo bar foo bar ";
      }
      var item = [ "id1", veryLongText, "Arial", 12, false, false, -1, false ];

      var size = FontSizeCalculation._measureItem( item, true);

      assertTrue( size[ 1 ] < 25 );
    },

    testMeasureVeryLongTextWithWrap : function() {
      var FontSizeCalculation = rwt.widgets.util.FontSizeCalculation;
      var veryLongText = "";
      for( var i = 0; i < 10; i++ ) {
        veryLongText += "foo bar foo bar foo bar foo bar foo bar foo bar foo bar foo bar foo bar ";
      }
      var item = [ "id1", veryLongText, "Arial", 12, false, false, 500, false ];

      var size = FontSizeCalculation._measureItem( item, true);

      assertTrue( size[ 1 ] > 100 );
    }

  }

} );

}());
