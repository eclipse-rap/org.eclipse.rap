/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.SpinnerTest", {
  extend : qx.core.Object,
  
  construct : function() {
    org.eclipse.rwt.test.fixture.TestUtil.prepareTimerUse();
  },
  
  members : {

    testCreateSpinnerByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertTrue( widget instanceof org.eclipse.swt.widgets.Spinner );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertEquals( "spinner", widget.getAppearance() );
      assertTrue( widget.getEditable() );
      assertFalse( widget.getWrap() );
      assertEquals( 0, widget.getMin() );
      assertEquals( 100, widget.getMax() );
      assertEquals( 0, widget.getValue() );
      assertEquals( 0, widget.getDigits() );
      assertFalse( widget._hasModifyListener );
      assertFalse( widget._hasSelectionListener );
      shell.destroy();
      widget.destroy();
    },

    testCreateSpinnerWithWrapAdndReadOnlyByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [ "READ_ONLY", "WRAP" ],
          "parent" : "w2"
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertFalse( widget.getEditable() );
      assertTrue( widget.getWrap() );
      shell.destroy();
      widget.destroy();
    },

    testSetMinimumByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "minimum" : 50
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertEquals( 50, widget.getMin() );
      shell.destroy();
      widget.destroy();
    },

    testSetMaximumByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "maximum" : 150
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertEquals( 150, widget.getMax() );
      shell.destroy();
      widget.destroy();
    },

    testSetMinimumBiggerThanCurrentMaximumByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "minimum" : 150,
          "maximum" : 200
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertEquals( 150, widget.getMin() );
      assertEquals( 200, widget.getMax() );
      shell.destroy();
      widget.destroy();
    },

    testSetSelectionByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "selection" : 50
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertEquals( 50, widget.getValue() );
      shell.destroy();
      widget.destroy();
    },

    testSetDigitsByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "digits" : 2
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertEquals( 2, widget.getDigits() );
      shell.destroy();
      widget.destroy();
    },

    testSetIncrementByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "increment" : 5
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertEquals( 5, widget.getIncrementAmount() );
      assertEquals( 5, widget.getWheelIncrementAmount() );
      shell.destroy();
      widget.destroy();
    },

    testSetPageIncrementByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "pageIncrement" : 20
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertEquals( 20, widget.getPageIncrementAmount() );
      shell.destroy();
      widget.destroy();
    },

    testSetTextLimitByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "textLimit" : 3
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertEquals( 3, widget._textfield.getMaxLength() );
      shell.destroy();
      widget.destroy();
    },

    testSetDecimalSeparatorLimitByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "decimalSeparator" : ","
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertEquals( ",", widget.getDecimalSeparator() );
      shell.destroy();
      widget.destroy();
    },

    testSetHasSelectionListenerByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      testUtil.protocolListen( "w3", { "selection" : true } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertTrue( widget._hasSelectionListener );
      shell.destroy();
      widget.destroy();
    },

    testSetHasModifyListenerByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Spinner",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      testUtil.protocolListen( "w3", { "modify" : true } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertTrue( widget._hasModifyListener );
      shell.destroy();
      widget.destroy();
    },

    testCreate : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var spinner = new org.eclipse.swt.widgets.Spinner();
      spinner.addToDocument();
      spinner.addState( "rwt_BORDER" );
      spinner.setEditable( true );
      spinner.setSpace( 59, 60, 5, 20 );
      spinner.setZIndex( 299 );
      spinner.setTabIndex( 58 );
      spinner.setMinMaxSelection( 0, 20, 4 );
      spinner.setHasModifyListener( true );
      testUtil.flush();
      assertTrue( spinner.isSeeable() );
      spinner.destroy();
    },
    
    testGetManager : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var spinner = new org.eclipse.swt.widgets.Spinner();
      spinner.addToDocument();
      spinner.setSpace( 59, 60, 5, 20 );
      spinner.setZIndex( 299 );
      spinner.setTabIndex( 58 );
      assertTrue( spinner.getManager() instanceof qx.util.range.Range );
      spinner.destroy();
    },
    
    testSetSeparator: function() {
      var w = new org.eclipse.swt.widgets.Spinner();
      w.addToDocument();
      w.addState( "rwt_BORDER" );
      w.setEditable( true );
      w.setSpace( 59, 60, 5, 20 );
      w.setZIndex( 299 );
      w.setTabIndex( 58 );
      w.setMinMaxSelection( 0, 20, 4 );
      w.setHasModifyListener( true );
      w.setDecimalSeparator( "," );
      w.destroy();
    },
  
    testDispose : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var spinner = new org.eclipse.swt.widgets.Spinner();
      spinner.addToDocument();
      spinner.setSpace( 59, 60, 5, 20 );
      testUtil.flush();
      spinner.destroy();
      testUtil.flush();
      assertTrue( spinner.isDisposed() );
    }
  
  }

} );