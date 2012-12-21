/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
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
var ObjectManager = rwt.remote.ObjectRegistry;
var Processor = rwt.remote.MessageProcessor;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ComboTest", {
  extend : rwt.qx.Object,

  members : {

    testCreateComboByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.Combo );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertEquals( "combo", widget.getAppearance() );
      shell.destroy();
      widget.destroy();
    },

    testCreateCComboByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [ "FLAT" ],
          "parent" : "w2",
          "ccombo" : true
        }
      } );
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.Combo );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertEquals( "ccombo", widget.getAppearance() );
      assertTrue( widget.hasState( "rwt_FLAT" ) );
      shell.destroy();
      widget.destroy();
    },

    testCreateComboWithCustomVariantByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );

      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "customVariant" : "variant_mystyle"
        }
      } );

      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget.hasState( "variant_mystyle" ) );
      assertTrue( widget._field.hasState( "variant_mystyle" ) );
      assertTrue( widget._list.hasState( "variant_mystyle" ) );
      assertTrue( widget._button.hasState( "variant_mystyle" ) );
      shell.destroy();
    },

    testRemoveCustomVariantByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );

      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "customVariant" : "variant_mystyle"
        }
      } );

      Processor.processOperation( {
        "target" : "w3",
        "action" : "set",
        "properties" : {
          "customVariant" : null
        }
      } );

      var widget = ObjectManager.getObject( "w3" );
      assertFalse( widget.hasState( "variant_mystyle" ) );
      assertFalse( widget._field.hasState( "variant_mystyle" ) );
      assertFalse( widget._list.hasState( "variant_mystyle" ) );
      assertFalse( widget._button.hasState( "variant_mystyle" ) );
      shell.destroy();
    },

    testSetItemHeightByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "itemHeight" : 18
        }
      } );
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 18, widget._itemHeight );
      assertEquals( 90, widget._getListMaxHeight() );
      shell.destroy();
      widget.destroy();
    },

    testSetVisibleItemCountByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "visibleItemCount" : 3
        }
      } );
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 3, widget._visibleItemCount );
      assertEquals( 60, widget._getListMaxHeight() );
      shell.destroy();
      widget.destroy();
    },

    testSetItemsByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "items" : [ "a", "b", "c" ]
        }
      } );
      var widget = ObjectManager.getObject( "w3" );
      var items = widget._list.getItems();
      assertEquals( 3, widget._list.getItemsCount() );
      assertEquals( "a", items[ 0 ].getLabel() );
      assertEquals( "b", items[ 1 ].getLabel() );
      assertEquals( "c", items[ 2 ].getLabel() );
      shell.destroy();
      widget.destroy();
    },

    testSetItemsEscapeTextByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "items" : [ "  foo &\nbar " ]
        }
      } );
      var widget = ObjectManager.getObject( "w3" );
      var items = widget._list.getItems();
      assertEquals( 1, widget._list.getItemsCount() );
      assertEquals( "&nbsp; foo &amp; bar&nbsp;", items[ 0 ].getLabel() );
      shell.destroy();
      widget.destroy();
    },

    testSetListVisibleByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
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
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget._dropped );
      shell.destroy();
      widget.destroy();
    },

    testSetSelectionIndexByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
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
      var widget = ObjectManager.getObject( "w3" );
      var items = widget._list.getItems();
      assertIdentical( items[ 1 ], widget._selected );
      shell.destroy();
      widget.destroy();
    },

    testSetEditableByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "editable" : false
        }
      } );
      var widget = ObjectManager.getObject( "w3" );
      assertFalse( widget._editable );
      assertTrue( widget._field.getReadOnly() );
      shell.destroy();
      widget.destroy();
    },

    testSetTextByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "text" : "foo"
        }
      } );
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( "foo", widget._field.getValue() );
      shell.destroy();
      widget.destroy();
    },

    testSetSelectionByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      TestUtil.flush();
      Processor.processOperation( {
        "target" : "w3",
        "action" : "set",
        "properties" : {
          "text" : "foo bar",
          "selection" : [ 2, 5 ]
        }
      } );
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 2, widget._selectionStart );
      assertEquals( 3, widget._selectionLength );
      shell.destroy();
      widget.destroy();
    },

    testSetTextLimitByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "textLimit" : 10
        }
      } );
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 10, widget._field.getMaxLength() );
      shell.destroy();
      widget.destroy();
    },

    testSetHasSelectionListenerByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      TestUtil.protocolListen( "w3", { "Selection" : true } );
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget._hasSelectionListener );
      shell.destroy();
      widget.destroy();
    },

    testSetHasDefaultSelectionListenerByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );

      TestUtil.protocolListen( "w3", { "DefaultSelection" : true } );

      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget._hasDefaultSelectionListener );
      shell.destroy();
      widget.destroy();
    },

    testSetHasModifyListenerByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      TestUtil.protocolListen( "w3", { "Modify" : true } );
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget._hasModifyListener );
      shell.destroy();
      widget.destroy();
    },

    testCreateDispose : function() {
      var combo = this._createDefaultCombo();
      assertTrue( combo instanceof rwt.widgets.Combo );
      combo.destroy();
      TestUtil.flush();
      assertTrue( combo.isDisposed() );
      combo.destroy();
    },

    testOpenList : function() {
      var combo = this._createDefaultCombo();
      combo.setListVisible( true );
      TestUtil.flush();
      assertTrue( combo._list.isSeeable() );
      assertEquals( "hidden", combo._list.getOverflow() );
      combo.destroy();
    },

    testCloseListOnBlur : function() {
      var combo = this._createDefaultCombo();
      combo.focus();
      combo.setListVisible( true );
      TestUtil.flush();
      assertTrue( combo._list.isSeeable() );
      combo.blur();
      TestUtil.flush();
      assertFalse( combo._list.isSeeable() );
      combo.destroy();
    },

    testItems : function() {
      var combo = this._createDefaultCombo();
      combo.setListVisible( true );
      TestUtil.flush();
      var items = this._getItems( combo );
      assertEquals( 6, items.length );
      assertEquals( "Eiffel", items[ 0 ].getLabel() );
      assertEquals( "Smalltalk", items[ 5 ].getLabel() );
      combo.destroy();
    },

    testSelectItem : function() {
      var combo = this._createDefaultCombo();
      combo.setListVisible( true );
      TestUtil.flush();
      combo.select( 1 );
      assertEquals( "Java", combo._field.getValue() );
      assertEquals( "Java", combo._list.getSelectedItems()[ 0 ].getLabel() );
      combo.destroy();
    },

    testHoverClickItem : function() {
      var combo = this._createDefaultCombo();
      combo.setListVisible( true );
      TestUtil.flush();
      var items = this._getItems( combo );
      TestUtil.mouseOver( items[ 1 ] );
      assertEquals( "Java", combo._list.getSelectedItems()[ 0 ].getLabel() );
      TestUtil.click( items[ 1 ] );
      assertEquals( "Java", combo._field.getValue() );
      combo.destroy();
    },

    testKeyDownScroll : function() {
      var combo = this._createDefaultCombo();
      combo.setVisibleItemCount( 2 );
      combo.setListVisible( true );
      combo.select( 0 );
      TestUtil.flush();

      assertEquals( "Eiffel", combo._list.getSelectedItems()[ 0 ].getLabel() );
      TestUtil.press( combo._field, "Down" );
      TestUtil.flush();
      TestUtil.press( combo._field, "Down" );
      TestUtil.flush();
      TestUtil.press( combo._field, "Down" );
      TestUtil.flush();

      assertEquals( "Ruby", combo._list.getSelectedItems()[ 0 ].getLabel() );
      assertEquals( ( 19 * 2 ), combo._list._clientArea.getScrollTop() );
      combo.destroy();
    },

    testHoverAndKeyDownScroll : function() {
      var combo = this._createDefaultCombo();
      combo.setVisibleItemCount( 2 );
      combo.setListVisible( true );
      TestUtil.flush();

      var items = this._getItems( combo );
      TestUtil.mouseOver( items[ 0 ] );
      assertEquals( "Eiffel", combo._list.getSelectedItems()[ 0 ].getLabel() );
      TestUtil.press( combo._field, "Down" );
      TestUtil.flush();
      TestUtil.press( combo._field, "Down" );
      TestUtil.flush();
      TestUtil.press( combo._field, "Down" );
      TestUtil.flush();

      assertEquals( "Ruby", combo._list.getSelectedItems()[ 0 ].getLabel() );
      assertEquals( ( 19 * 2 ), combo._list._clientArea.getScrollTop() );
      combo.destroy();
    },

    testScrollDoesNotChangeSelection : function() {
      var combo = this._createDefaultCombo();
      combo.setVisibleItemCount( 2 );
      combo.setListVisible( true );
      TestUtil.flush();

      var items = this._getItems( combo );
      TestUtil.mouseOver( items[ 0 ] );
      assertEquals( "Eiffel", combo._list.getSelectedItems()[ 0 ].getLabel() );
      combo._list._clientArea.setScrollTop( 19 );
      combo._list.createDispatchEvent( "userScroll" );
      TestUtil.mouseOver( items[ 1 ] ); // browser may fire mouse events on scroll
      TestUtil.mouseMove( items[ 1 ] );

      assertEquals( "Eiffel", combo._list.getSelectedItems()[ 0 ].getLabel() );
      combo.destroy();
    },

    testMoveAfterScrollDoesChangeSelection : function() {
      var combo = this._createDefaultCombo();
      combo.setVisibleItemCount( 2 );
      combo.setListVisible( true );
      TestUtil.flush();

      var items = this._getItems( combo );
      TestUtil.mouseOver( items[ 0 ] );
      assertEquals( "Eiffel", combo._list.getSelectedItems()[ 0 ].getLabel() );
      combo._list._clientArea.setScrollTop( 19 );
      combo._list.createDispatchEvent( "userScroll" );
      TestUtil.mouseOver( items[ 1 ] ); // browser may fire mouse events on scroll
      TestUtil.mouseMove( items[ 1 ] );
      TestUtil.forceTimerOnce();
      TestUtil.mouseMove( items[ 1 ] );

      assertEquals( "Java", combo._list.getSelectedItems()[ 0 ].getLabel() );
      combo.destroy();
    },

    testScrollBarClick : function() {
      var combo = this._createDefaultCombo();
      combo.setListVisible( true );
      TestUtil.flush();
      assertTrue( combo._list._vertScrollBar.isSeeable() );
      TestUtil.click( combo._list._vertScrollBar._thumb );
      assertTrue( combo._list.isSeeable() );
      combo.destroy();
    },

    testFieldClick : function() {
      var combo = this._createDefaultCombo();
      TestUtil.click( combo._field );
      TestUtil.flush();
      assertTrue( combo._list.isSeeable() );
      TestUtil.click( combo._list._vertScrollBar._thumb );
      TestUtil.click( combo._field );
      TestUtil.flush();
      assertFalse( combo._list.isSeeable() );
      combo.destroy();
    },

    // bug 343532
    testEventRedispatch : function() {
      var combo = this._createDefaultCombo();
      var otherCombo = this._createDefaultCombo();
      otherCombo.setSpace( 239, 81, 36, 23 );
      combo.setListVisible( true );
      TestUtil.flush();
      assertTrue( combo._list.isSeeable() );
      TestUtil.click( otherCombo._field );
      TestUtil.flush();
      assertFalse( combo._list.isSeeable() );
      assertFalse( otherCombo._list.isSeeable() );
      combo.destroy();
      otherCombo.destroy();
    },

    // bug 343557
    testEventRedispatch_2 : function() {
      var combo = this._createDefaultCombo();
      combo.setListVisible( true );
      var checkbox = new rwt.widgets.Button( "check" );
      checkbox.addState( "rwt_CHECK" );
      checkbox.addToDocument();
      checkbox.setEnabled( false );
      TestUtil.flush();
      assertTrue( combo._list.isSeeable() );
      assertFalse( checkbox.getEnabled() );
      assertFalse( checkbox.hasState( "selected" ) );
      TestUtil.click( checkbox );
      assertFalse( combo._list.isSeeable() );
      assertFalse( checkbox.hasState( "selected" ) );
      combo.destroy();
      checkbox.destroy();
    },

    // bug 388717
    testEventRedispatch_3 : function() {
      var logger = TestUtil.getLogger();
      var shell = TestUtil.createShellByProtocol( "w2" );
      shell.addEventListener( "mousedown", logger.log, logger );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var combo = ObjectManager.getObject( "w3" );
      combo.setSpace( 10, 81, 6, 23 );
      combo.setItemHeight( 19 );
      combo.setEditable( false );
      combo.setItems( [ "Eiffel", "Java", "Python", "Ruby", "Simula", "Smalltalk" ] );
      combo.setVisibleItemCount( 5 );
      TestUtil.flush();
      combo.setListVisible( true );
      TestUtil.flush();

      TestUtil.click( combo._list.getItems()[ 1 ] );

      assertEquals( 0, logger.getLog().length );
      shell.destroy();
    },

    testButtonClick : function() {
      var combo = this._createDefaultCombo();
      TestUtil.click( combo._button );
      TestUtil.flush();
      assertTrue( combo._list.isSeeable() );
      TestUtil.click( combo._list._vertScrollBar._thumb );
      TestUtil.click( combo._button );
      TestUtil.flush();
      assertFalse( combo._list.isSeeable() );
      combo.destroy();
    },

    testListPopUpBehavior : function() {
      var combo = this._createDefaultCombo();
      TestUtil.click( combo._field );
      TestUtil.flush();
      TestUtil.click( combo._list._vertScrollBar._thumb );
      assertTrue( combo._list.isSeeable() );
      TestUtil.click( TestUtil.getDocument() );
      assertFalse( combo._list.isSeeable() );
      TestUtil.click( combo._field );
      TestUtil.flush();
      assertTrue( combo._list.isSeeable() );
      combo.destroy();
    },

    testListHeight : function() {
      var combo = this._createDefaultCombo();
      combo.setItems( [ "Eiffel", "Java", "Python" ] );
      var correctHeight = 19 * 3 + combo._list.getFrameHeight();
      combo.setListVisible( true );
      TestUtil.flush();
      assertEquals( correctHeight, combo._list.getHeight() );
      combo.destroy();
    },

    testListSelection : function() {
      var combo = this._createDefaultCombo();
      combo.select( 1 );
      combo._setListSelection( combo._list.getItems()[ 5 ] );
      assertEquals( "Smalltalk", combo._list.getSelectedItems()[ 0 ].getLabel() );
      combo.setListVisible( true );
      assertEquals( "Java", combo._list.getSelectedItems()[ 0 ].getLabel() );
      combo.destroy();
    },

    testScrollToSelectedItem : function() {
      var combo = this._createDefaultCombo();
      combo.select( 5 );
      combo.setListVisible( true );
      TestUtil.flush();
      assertTrue( combo._list._clientArea.isSeeable() );
      assertEquals( "Smalltalk", combo._list.getSelectedItems()[ 0 ].getLabel() );
      assertEquals( 19, combo._list._clientArea.getScrollTop() );
      combo.destroy();
    },

    testSelectionByArrowKeys : function() {
      var combo = this._createDefaultCombo();
      combo.setEditable( true );
      combo.select( 3 );
      TestUtil.flush();
      combo.focus();
      TestUtil.keyDown( combo._field.getElement(), "Down" );
      assertEquals( "Simula", combo._list.getSelectedItems()[ 0 ].getLabel() );
      TestUtil.keyDown( combo._field.getElement(), "Up" );
      assertEquals( "Ruby", combo._list.getSelectedItems()[ 0 ].getLabel() );
      combo.destroy();
    },

    testHoldArrowKeysSendSelectionEventOnce : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var combo = ObjectManager.getObject( "w3" );
      combo.setItems( [ "Eiffel", "Java", "Python", "Ruby", "Simula", "Smalltalk" ] );
      combo.setVisibleItemCount( 5 );
      combo.setListVisible( true );
      combo.select( 3 );
      combo.setHasSelectionListener( true );
      TestUtil.flush();
      combo.focus();
      TestUtil.initRequestLog();

      TestUtil.keyDown( combo._field.getElement(), "Down" );
      TestUtil.keyHold( combo._field.getElement(), "Down" );
      TestUtil.keyHold( combo._field.getElement(), "Down" );
      TestUtil.keyHold( combo._field.getElement(), "Down" );
      TestUtil.keyUp( combo._field.getElement(), "Down" );

      assertEquals( "Simula", combo._list.getSelectedItems()[ 0 ].getLabel() );
      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( 4, message.findSetProperty( "w3", "selectionIndex" ) );
      assertNotNull( message.findNotifyOperation( "w3", "Selection" ) );
      assertNull( message.findNotifyOperation( "w3", "Modify" ) );
      combo.destroy();
      shell.destroy();
    },

    testHoldArrowKeysSendModifyEventOnce : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var combo = ObjectManager.getObject( "w3" );
      combo.setItems( [ "Eiffel", "Java", "Python", "Ruby", "Simula", "Smalltalk" ] );
      combo.setVisibleItemCount( 5 );
      combo.setListVisible( true );
      combo.select( 3 );
      combo.setHasModifyListener( true );
      TestUtil.flush();
      combo.focus();
      TestUtil.initRequestLog();

      TestUtil.keyDown( combo._field.getElement(), "Down" );
      TestUtil.keyHold( combo._field.getElement(), "Down" );
      TestUtil.keyHold( combo._field.getElement(), "Down" );
      TestUtil.keyHold( combo._field.getElement(), "Down" );
      TestUtil.keyUp( combo._field.getElement(), "Down" );

      assertEquals( "Simula", combo._list.getSelectedItems()[ 0 ].getLabel() );
      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( 4, message.findSetProperty( "w3", "selectionIndex" ) );
      assertNotNull( message.findNotifyOperation( "w3", "Modify" ) );
      assertNull( message.findNotifyOperation( "w3", "Selection" ) );
      combo.destroy();
      shell.destroy();
    },

    testSendDefaultSelectionEvent : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var combo = ObjectManager.getObject( "w3" );
      combo.setItems( [ "Eiffel", "Java", "Python", "Ruby", "Simula", "Smalltalk" ] );
      combo.setHasDefaultSelectionListener( true );
      TestUtil.flush();
      combo.focus();
      TestUtil.initRequestLog();

      TestUtil.keyDown( combo._field.getElement(), "Enter" );
      TestUtil.keyUp( combo._field.getElement(), "Enter" );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyOperation( "w3", "DefaultSelection" ) );
      combo.destroy();
      shell.destroy();
    },

    testSendText : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var combo = ObjectManager.getObject( "w3" );
      combo.setItems( [ "Eiffel", "Java", "Python", "Ruby", "Simula", "Smalltalk" ] );
      combo.setVisibleItemCount( 5 );
      combo.setListVisible( true );
      combo.select( 3 );
      TestUtil.flush();
      combo.focus();
      TestUtil.initRequestLog();

      combo._field.setValue( "a" );
      combo._field._oninput();
      rwt.remote.Server.getInstance().send();

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "a", message.findSetProperty( "w3", "text" ) );
      shell.destroy();
    },

    testSendModify : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      TestUtil.protocolListen( "w3", { "Modify" : true } );
      var combo = ObjectManager.getObject( "w3" );
      combo.setItems( [ "Eiffel", "Java", "Python", "Ruby", "Simula", "Smalltalk" ] );
      combo.setVisibleItemCount( 5 );
      combo.setListVisible( true );
      combo.select( 3 );
      TestUtil.flush();
      combo.focus();
      TestUtil.initRequestLog();

      combo._field.setValue( "a" );
      combo._field._oninput();
      TestUtil.forceTimerOnce();

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "a", message.findSetProperty( "w3", "text" ) );
      assertNotNull( message.findNotifyOperation( "w3", "Modify" ) );
      shell.destroy();
    },


    testPageUpOnNotCreatedList : function() {
      var combo = this._createDefaultCombo();
      combo.setEditable( true );
      combo.select( 3 );
      TestUtil.flush();
      combo.focus();
      TestUtil.keyDown( combo._field.getElement(), "PageUp" );
      assertEquals( "Ruby", combo._list.getSelectedItems()[ 0 ].getLabel() );
      combo.destroy();
    },

    testPageDownOnNotCreatedList : function() {
      var combo = this._createDefaultCombo();
      combo.setEditable( true );
      combo.select( 3 );
      TestUtil.flush();
      combo.focus();
      TestUtil.keyDown( combo._field.getElement(), "PageDown" );
      assertEquals( "Ruby", combo._list.getSelectedItems()[ 0 ].getLabel() );
      combo.destroy();
    },

    testSelectionByKeyboardReadOnly : function() {
      var combo = this._createDefaultCombo();
      combo.setEditable( false );
      TestUtil.flush();
      combo.focus();
      TestUtil.keyDown( combo._field.getElement(), "R" );
      assertEquals( "Ruby", combo._list.getSelectedItems()[ 0 ].getLabel() );
      combo.destroy();
    },

    testSelectionByKeyboardEditable : function() {
      var combo = this._createDefaultCombo();
      combo.setEditable( true );
      TestUtil.flush();
      combo.focus();
      TestUtil.keyDown( combo._field.getElement(), "R" );
      assertEquals( 0, combo._list.getSelectedItems().length );
      combo.destroy();
    },

    testFiresItemsChangedEvent : function() {
      var combo = new rwt.widgets.Combo();
      var log = 0;
      combo.addEventListener( "itemsChanged", function() {
        log++;
      } );

      combo.setItems( [ "Eiffel", "Java", "Python", "Ruby", "Simula", "Smalltalk" ] );
      combo.addToDocument();
      TestUtil.flush();

      assertEquals( 1, log );
      combo.destroy();
    },

    testFiresSelectionChangedEvent : function() {
      var combo = new rwt.widgets.Combo();
      var log = 0;
      combo.addEventListener( "selectionChanged", function() {
        log++;
      } );

      combo.setItems( [ "Eiffel", "Java", "Python", "Ruby", "Simula", "Smalltalk" ] );
      combo.addToDocument();
      TestUtil.flush();
      combo._setSelected( combo._list.getItems()[ 3 ] );

      assertEquals( 1, log );
      combo.destroy();
    },

    testApplyListId_renderHtmlIdsActivated : function() {
      var combo = new rwt.widgets.Combo();
      combo.addToDocument();

      rwt.widgets.base.Widget._renderHtmlIds = true;
      combo.applyObjectId( "123" );

      assertEquals( "123-listbox" ,combo._list.getHtmlAttribute( "id" ) );
      combo.destroy();
    },

    testApplyListId_renderHtmlIdsDeactivated : function() {
      var combo = new rwt.widgets.Combo();
      combo.addToDocument();

      rwt.widgets.base.Widget._renderHtmlIds = false;
      combo.applyObjectId( "123" );

      assertEquals( "" ,combo._list.getHtmlAttribute( "id" ) );
      combo.destroy();
    },

    testApplyListItemIds_renderHtmlIdsActivated : function() {
      var combo = new rwt.widgets.Combo();
      combo.addToDocument();

      rwt.widgets.base.Widget._renderHtmlIds = true;
      combo.applyObjectId( "123" );
      combo.setItems( [ "Eiffel", "Java", "Python", "Ruby", "Simula", "Smalltalk" ] );

      var listItemId = combo._list.getHtmlAttribute( "id" ) + "-listitem-3";
      assertEquals( listItemId, combo._list.getItems()[ 3 ].getHtmlAttribute( "id" ) );
      combo.destroy();
    },

    //////////
    // Helpers

    _createDefaultCombo : function() {
      var combo = new rwt.widgets.Combo();
      combo.setSpace( 239, 81, 6, 23 );
      combo.setItemHeight( 19 );
      combo.setEditable( false );
      combo.setItems( [ "Eiffel", "Java", "Python", "Ruby", "Simula", "Smalltalk" ] );
      combo.setVisibleItemCount( 5 );
      combo.addToDocument();
      TestUtil.flush();
      return combo;
    },

    _getItems : function( combo ) {
      return combo._list.getItems();
    }

  }

} );

}());
