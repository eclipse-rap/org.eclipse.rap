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

qx.Class.define( "org.eclipse.rwt.test.tests.ComboTest", {
  extend : qx.core.Object,
  
  members : {

    testCreateComboByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertTrue( widget instanceof org.eclipse.swt.widgets.Combo );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertEquals( "combo", widget.getAppearance() );
      shell.destroy();
      widget.destroy();
    },

    testCreateCComboByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [ "FLAT" ],
          "parent" : "w2",
          "ccombo" : true
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertTrue( widget instanceof org.eclipse.swt.widgets.Combo );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertEquals( "ccombo", widget.getAppearance() );
      assertTrue( widget.hasState( "rwt_FLAT" ) );
      shell.destroy();
      widget.destroy();
    },

    testSetItemHeightByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "itemHeight" : 18
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertEquals( 18, widget._itemHeight );
      assertEquals( 90, widget._getListMaxHeight() );
      shell.destroy();
      widget.destroy();
    },

    testSetVisibleItemCountByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "visibleItemCount" : 3
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertEquals( 3, widget._visibleItemCount );
      assertEquals( 60, widget._getListMaxHeight() );
      shell.destroy();
      widget.destroy();
    },

    testSetItemsByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "items" : [ "a", "b", "c" ]
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      var items = widget._list.getItems();
      assertEquals( 3, widget._list.getItemsCount() );
      assertEquals( "a", items[ 0 ].getLabel() );
      assertEquals( "b", items[ 1 ].getLabel() );
      assertEquals( "c", items[ 2 ].getLabel() );
      shell.destroy();
      widget.destroy();
    },

    testSetItemsEscapeTextByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "items" : [ "  foo &\nbar " ]
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      var items = widget._list.getItems();
      assertEquals( 1, widget._list.getItemsCount() );
      assertEquals( "&nbsp; foo &amp; bar&nbsp;", items[ 0 ].getLabel() );
      shell.destroy();
      widget.destroy();
    },

    testSetListVisibleByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "items" : [ "a", "b", "c" ],
          "listVisible" : true
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertTrue( widget._dropped );
      shell.destroy();
      widget.destroy();
    },

    testSetSelectionIndexByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "items" : [ "a", "b", "c" ],
          "selectionIndex" : 1
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      var items = widget._list.getItems();
      assertIdentical( items[ 1 ], widget._selected );
      shell.destroy();
      widget.destroy();
    },

    testSetEditableByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "editable" : false
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertFalse( widget._editable );
      assertTrue( widget._field.getReadOnly() );
      shell.destroy();
      widget.destroy();
    },

    testSetTextByProtocol : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = testUtil.createShellByProtocol( "w2" );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "text" : "foo"
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertEquals( "foo", widget._field.getValue() );
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
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      testUtil.flush();
      processor.processOperation( {
        "target" : "w3",
        "action" : "set",
        "properties" : {
          "text" : "foo bar",
          "selection" : [ 2, 5 ]
        }
      } );
      var objectManager = org.eclipse.rwt.protocol.ObjectManager;
      var widget = objectManager.getObject( "w3" );
      assertEquals( 2, widget._field.getSelectionStart() );
      assertEquals( 3, widget._field.getSelectionLength() );
      shell.destroy();
      widget.destroy();
    },

    testCreateDispose : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var combo = this._createDefaultCombo();
      assertTrue( combo instanceof org.eclipse.swt.widgets.Combo );
      combo.destroy();
      testUtil.flush();
      assertTrue( combo.isDisposed() );
      combo.destroy();
    },
    
    testOpenList : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var combo = this._createDefaultCombo();
      combo.setListVisible( true );
      testUtil.flush();
      assertTrue( combo._list.isSeeable() );
      assertEquals( "hidden", combo._list.getOverflow() );
      combo.destroy();
    },

    testCloseListOnBlur : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var combo = this._createDefaultCombo();
      combo.focus();
      combo.setListVisible( true );
      testUtil.flush();
      assertTrue( combo._list.isSeeable() );
      combo.blur();
      testUtil.flush();
      assertFalse( combo._list.isSeeable() );
      combo.destroy();
    },

    testItems : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var combo = this._createDefaultCombo();
      combo.setListVisible( true );
      testUtil.flush();
      var items = this._getItems( combo );
      assertEquals( 6, items.length );
      assertEquals( "Eiffel", items[ 0 ].getLabel() );
      assertEquals( "Smalltalk", items[ 5 ].getLabel() );
      combo.destroy();
    },

    testSelectItem : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var combo = this._createDefaultCombo();
      combo.setListVisible( true );
      testUtil.flush();
      combo.select( 1 );
      assertEquals( "Java", combo._field.getValue() );
      assertEquals( "Java", combo._list.getSelectedItems()[ 0 ].getLabel() );
      combo.destroy();
    },

    testHoverClickItem : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var combo = this._createDefaultCombo();
      combo.setListVisible( true );
      testUtil.flush();
      var items = this._getItems( combo );
      testUtil.mouseOver( items[ 1 ] );
      assertEquals( "Java", combo._list.getSelectedItems()[ 0 ].getLabel() )
      testUtil.click( items[ 1 ] );
      assertEquals( "Java", combo._field.getValue() );
      combo.destroy();      
    },
    
    testScrollBarClick : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var combo = this._createDefaultCombo();
      combo.setListVisible( true );
      testUtil.flush();
      assertTrue( combo._list._vertScrollBar.isSeeable() );
      testUtil.click( combo._list._vertScrollBar._thumb );
      assertTrue( combo._list.isSeeable() );
      combo.destroy();
    },

    testFieldClick : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var combo = this._createDefaultCombo();
      testUtil.click( combo._field );
      testUtil.flush();
      assertTrue( combo._list.isSeeable() );
      testUtil.click( combo._list._vertScrollBar._thumb );
      testUtil.click( combo._field );
      testUtil.flush();
      assertFalse( combo._list.isSeeable() );
      combo.destroy();
    },

    // bug 343532
    testEventRedispatch : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var combo = this._createDefaultCombo();
      var otherCombo = this._createDefaultCombo();
      otherCombo.setSpace( 239, 81, 36, 23 );
      combo.setListVisible( true );
      testUtil.flush();
      assertTrue( combo._list.isSeeable() );
      testUtil.click( otherCombo._field );
      testUtil.flush();
      assertFalse( combo._list.isSeeable() );
      assertFalse( otherCombo._list.isSeeable() );
      combo.destroy();
      otherCombo.destroy();
    },

    // bug 343557
    testEventRedispatch_2 : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var combo = this._createDefaultCombo();
      combo.setListVisible( true );
      var checkbox = new org.eclipse.rwt.widgets.Button( "check" );
      checkbox.addState( "rwt_CHECK" );
      checkbox.addToDocument();
      checkbox.setEnabled( false );
      testUtil.flush();
      assertTrue( combo._list.isSeeable() );
      assertFalse( checkbox.getEnabled() );
      assertFalse( checkbox.hasState( "selected" ) );
      testUtil.click( checkbox );
      assertFalse( combo._list.isSeeable() );
      assertFalse( checkbox.hasState( "selected" ) );
      combo.destroy();
      checkbox.destroy();
    },

    testButtonClick : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var combo = this._createDefaultCombo();
      testUtil.click( combo._button );
      testUtil.flush();
      assertTrue( combo._list.isSeeable() );
      testUtil.click( combo._list._vertScrollBar._thumb );
      testUtil.click( combo._button );
      testUtil.flush();
      assertFalse( combo._list.isSeeable() );
      combo.destroy();
    },

    testListPopUpBehavior : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var combo = this._createDefaultCombo();
      testUtil.click( combo._field );
      testUtil.flush();
      testUtil.click( combo._list._vertScrollBar._thumb );
      assertTrue( combo._list.isSeeable() );
      testUtil.click( testUtil.getDocument() );
      assertFalse( combo._list.isSeeable() );
      testUtil.click( combo._field );
      testUtil.flush();
      assertTrue( combo._list.isSeeable() );
      combo.destroy();
    },
   
    testListHeight : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var combo = this._createDefaultCombo();
      combo.setItems( [ "Eiffel", "Java", "Python" ] );
      var correctHeight = 19 * 3 + combo._list.getFrameHeight();
      combo.setListVisible( true );
      testUtil.flush();
      assertEquals( correctHeight, combo._list.getHeight() );
      combo.destroy();
    },

    testListSelection : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var combo = this._createDefaultCombo();
      combo.select( 1 );
      combo._setListSelection( combo._list.getItems()[ 5 ] );
      assertEquals( "Smalltalk", combo._list.getSelectedItems()[ 0 ].getLabel() );
      combo.setListVisible( true );
      assertEquals( "Java", combo._list.getSelectedItems()[ 0 ].getLabel() );
      combo.destroy();
    },

    testScrollToSelectedItem : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var combo = this._createDefaultCombo();
      combo.select( 5 );
      combo.setListVisible( true );
      testUtil.flush();
      assertTrue( combo._list._clientArea.isSeeable() );
      assertEquals( "Smalltalk", combo._list.getSelectedItems()[ 0 ].getLabel() );
      assertEquals( 19, combo._list._clientArea.getScrollTop() );
      combo.destroy();
    },

    testSelectionByArrowKeys : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var combo = this._createDefaultCombo();
      combo.setEditable( true );
      combo.select( 3 );
      testUtil.flush();
      combo.focus();
      testUtil.keyDown( combo._field.getElement(), "Down" );
      assertEquals( "Simula", combo._list.getSelectedItems()[ 0 ].getLabel() );
      testUtil.keyDown( combo._field.getElement(), "Up" );
      assertEquals( "Ruby", combo._list.getSelectedItems()[ 0 ].getLabel() );
      combo.destroy();
    },

    testPageUpOnNotCreatedList : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var combo = this._createDefaultCombo();
      combo.setEditable( true );
      combo.select( 3 );
      testUtil.flush();
      combo.focus();
      testUtil.keyDown( combo._field.getElement(), "PageUp" );
      assertEquals( "Ruby", combo._list.getSelectedItems()[ 0 ].getLabel() );
      combo.destroy();
    },

    testPageDownOnNotCreatedList : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var combo = this._createDefaultCombo();
      combo.setEditable( true );
      combo.select( 3 );
      testUtil.flush();
      combo.focus();
      testUtil.keyDown( combo._field.getElement(), "PageDown" );
      assertEquals( "Ruby", combo._list.getSelectedItems()[ 0 ].getLabel() );
      combo.destroy();
    },

    //////////
    // Helpers

    _createDefaultCombo : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var combo = new org.eclipse.swt.widgets.Combo();
      combo.setSpace( 239, 81, 6, 23 );
      combo.setItemHeight( 19 );
      combo.setEditable( false );
      combo.setItems( [ "Eiffel", "Java", "Python", "Ruby", "Simula", "Smalltalk" ] );
      combo.setVisibleItemCount( 5 ); 
      combo.addToDocument();
      testUtil.flush();
      return combo;
    },

    _getItems : function( combo ) {
      return combo._list.getItems();
    }

  }

} );