/*******************************************************************************
 * Copyright (c) 2010, 2014 EclipseSource and others.
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
var ObjectRegistry = rwt.remote.ObjectRegistry;
var Processor = rwt.remote.MessageProcessor;

var shell;
var combo;
var list;
var field;
var button;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ComboTest", {
  extend : rwt.qx.Object,

  members : {

    setUp : function() {
      shell = TestUtil.createShellByProtocol( "w2" );
      shell.open();
      shell.setBorder( null );
      shell.setLocation( 10, 20 );
      combo = this._createDefaultCombo();
      TestUtil.flush();
    },

    tearDown : function() {
      shell.destroy();
      combo.destroy();
    },

    testComboHandlerEventsList : function() {
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Combo" );

      assertEquals( [ "Selection", "DefaultSelection", "Modify" ], handler.events );
    },

    testCreateComboByProtocol : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.Combo );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertEquals( "combo", widget.getAppearance() );
      widget.destroy();
    },

    testCreateCComboByProtocol : function() {
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
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.Combo );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertEquals( "ccombo", widget.getAppearance() );
      assertTrue( widget.hasState( "rwt_FLAT" ) );
      widget.destroy();
    },

    testCreateComboWithCustomVariantByProtocol : function() {
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

      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget.hasState( "variant_mystyle" ) );
      assertTrue( widget._field.hasState( "variant_mystyle" ) );
      assertTrue( widget._button.hasState( "variant_mystyle" ) );
      widget.destroy();
    },

    testRemoveCustomVariantByProtocol : function() {
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

      var widget = ObjectRegistry.getObject( "w3" );
      assertFalse( widget.hasState( "variant_mystyle" ) );
      assertFalse( widget._field.hasState( "variant_mystyle" ) );
      assertFalse( widget._button.hasState( "variant_mystyle" ) );
      widget.destroy();
    },

    testSetVisibleItemCountByProtocol : function() {
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
      var widget = ObjectRegistry.getObject( "w3" );
      assertEquals( 3, widget._list.getVisibleItemCount() );
      widget.destroy();
    },

    testSetItemsByProtocol : function() {
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
      var widget = ObjectRegistry.getObject( "w3" );
      assertEquals( [ "a", "b", "c" ], widget._list.getItems() );
      widget.destroy();
    },

    testSetListVisibleByProtocol : function() {
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
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget._list.getVisible() );
      widget.destroy();
    },

    testSetSelectionIndexByProtocol : function() {
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
      var widget = ObjectRegistry.getObject( "w3" );
      var items = widget._list.getItems();
      assertIdentical( 1, widget._list.getSelectionIndex() );
      widget.destroy();
    },

    testSetEditableByProtocol : function() {
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
      var widget = ObjectRegistry.getObject( "w3" );
      assertFalse( widget._editable );
      assertTrue( widget._field.getReadOnly() );
      widget.destroy();
    },

    testSetTextByProtocol : function() {
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
      var widget = ObjectRegistry.getObject( "w3" );
      assertEquals( "foo", widget._field.getValue() );
      widget.destroy();
    },

    testSetSelectionByProtocol : function() {
      Processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.Combo",
        "properties" : {
          "style" : [],
          "parent" : "w2"
        }
      } );

      Processor.processOperation( {
        "target" : "w3",
        "action" : "set",
        "properties" : {
          "text" : "foo bar",
          "selection" : [ 2, 5 ]
        }
      } );
      TestUtil.flush();
      var widget = ObjectRegistry.getObject( "w3" );
      widget.focus();


      assertEquals( [ 2, 5 ], widget._field.getSelection() );
      widget.destroy();
    },

    testSetTextLimitByProtocol : function() {
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
      var widget = ObjectRegistry.getObject( "w3" );
      assertEquals( 10, widget._field.getMaxLength() );
      widget.destroy();
    },

    testCreateDispose : function() {
      assertTrue( combo instanceof rwt.widgets.Combo );

      combo.destroy();
      TestUtil.flush();

      assertTrue( combo.isDisposed() );
    },

    testCreateListAfterParentIsSet : function() {
      var combo = new rwt.widgets.Combo();
      assertTrue( typeof combo._list === "undefined" );

      combo.addToDocument();
      assertTrue( combo._list instanceof rwt.widgets.DropDown );
      combo.destroy();
    },

    testDispose_disposesSubwidgets : function() {
      combo.destroy();
      TestUtil.flush();

      assertTrue( list.isDisposed() );
      assertTrue( field.isDisposed() );
      assertTrue( button.isDisposed() );
    },

    testSetFonts: function() {
      combo.setFont( rwt.html.Font.fromString( "Arial 20px" ) );

      assertEquals( "20px Arial", combo.getFont().toCss() );
      assertEquals( "20px Arial", field.getFont().toCss() );
    },

    testSetTextColor : function() {
      combo.setTextColor( "#00000F" );

      assertEquals( "#00000F", combo.getTextColor() );
      assertEquals( "#00000F", field.getTextColor() );
    },

    testSetBackgroundColor : function() {
      combo.setBackgroundColor( "#00000F" );

      assertEquals( "#00000F", combo.getBackgroundColor() );
      assertEquals( "#00000F", field.getBackgroundColor() );
    },

    testSetCursor : function() {
      combo.setCursor( "help" );

      assertEquals( "help", combo.getCursor() );
      assertEquals( "help", field.getCursor() );
      assertEquals( "help", button.getCursor() );
    },

    testOpenList : function() {
      combo.setListVisible( true );
      TestUtil.flush();

      assertTrue( list.getVisible() );
    },

    testShowList_markFieldNotEditableAndRemoveFocus : function() {
      combo.setEditable( true );
      TestUtil.flush();

      combo.setListVisible( true );

      assertTrue( field.getReadOnly() );
      assertFalse( field.hasState( "focused" ) );
      combo.destroy();
    },

    testHideList_markFieldEditableAndRestoreFocus : function() {
      combo.setEditable( true );
      combo.setListVisible( true );
      TestUtil.flush();

      combo.setListVisible( false );

      assertFalse( field.getReadOnly() );
      assertTrue( field.hasState( "focused" ) );
    },

    testItems : function() {
      combo.setListVisible( true );
      TestUtil.flush();
      var items = list.getItems();
      assertEquals( 6, items.length );
      assertEquals( "Eiffel", items[ 0 ] );
      assertEquals( "Smalltalk", items[ 5 ] );
    },

    testItems_recalculatesMinWidthOnVisibleList : function() {
      combo.setListVisible( true );
      var oldMinWidth = combo._listMinWidth;

      combo.setItems( [ "a very very long item" ] );

      assertTrue( combo._listMinWidth > oldMinWidth );
    },

    testSetVisibleItemCount : function() {
      combo.setVisibleItemCount( 7 );

      assertEquals( 7, list.getVisibleItemCount() );
    },

    testSetEditable : function() {
      combo.setEditable( false );
      assertTrue( field.getReadOnly() );

      combo.setEditable( true );
      assertFalse( field.getReadOnly() );
    },

    testSetText_onReadOnly : function() {
      combo.setText( "foo" );

      assertEquals( "", field.getValue() );
    },

    testSetText_onEditable : function() {
      combo.setEditable( true );

      combo.setText( "foo" );

      assertEquals( "foo", field.getValue() );
    },

    testSetTextSelection : function() {
      combo.setEditable( true );
      combo.setText( "foo bar" );
      TestUtil.flush();

      combo.setTextSelection( [ 2, 4 ] );

      assertEquals( [ 2, 4 ], field.getSelection() );
    },

    testSetTextLimit : function() {
      combo.setTextLimit( 7 );

      assertEquals( 7, field.getMaxLength() );
    },

    testSetListVisible : function() {
      combo.setListVisible( true );
      assertTrue( list.getVisible() );

      combo.setListVisible( false );
      assertFalse( list.getVisible() );
    },

    testSetVisible_recalculatesMinWidth : function() {
      assertEquals( -1, combo._listMinWidth );

      combo.setListVisible( true );

      assertTrue( combo._listMinWidth > 0 );
    },

    testSelect : function() {
      combo.select( 1 );

      assertEquals( "Java", field.getValue() );
      assertEquals( 1, list.getSelectionIndex() );
    },

    testScroll_Down : function() {
      combo.select( 0 );
      TestUtil.flush();

      TestUtil.press( field, "Down" );
      TestUtil.flush();

      assertEquals( 1, list.getSelectionIndex() );
    },

    testScroll_PageDown : function() {
      combo.select( 0 );
      TestUtil.flush();

      TestUtil.press( field, "PageDown" );
      TestUtil.flush();

      assertEquals( 4, list.getSelectionIndex() );
    },

    testScroll_Up : function() {
      combo.select( 5 );
      TestUtil.flush();

      TestUtil.press( field, "Up" );
      TestUtil.flush();

      assertEquals( 4, list.getSelectionIndex() );
    },

    testScroll_PageUp : function() {
      combo.select( 5 );
      TestUtil.flush();

      TestUtil.press( field, "PageUp" );
      TestUtil.flush();

      assertEquals( 1, list.getSelectionIndex() );
    },

    testFieldClick_togglesListVisibility : function() {
      TestUtil.click( field );
      TestUtil.flush();
      assertTrue( list.getVisible() );

      TestUtil.click( field );
      TestUtil.flush();
      assertFalse( list.getVisible() );
    },

    testFiledClick_focusesCombo : function() {
      combo.setFocused( false );

      TestUtil.click( field );
      TestUtil.flush();

      assertTrue( combo.getFocused() );
    },

    testButtonClick_togglesListVisibility : function() {
      TestUtil.click( button );
      TestUtil.flush();
      assertTrue( list.getVisible() );

      TestUtil.click( button );
      TestUtil.flush();
      assertFalse( list.getVisible() );
    },

    testButtonClick_focusesCombo : function() {
      combo.setFocused( false );

      TestUtil.click( button );
      TestUtil.flush();

      assertTrue( combo.getFocused() );
    },

    testListPopUpBehavior : function() {
      TestUtil.click( field );
      TestUtil.flush();
      assertTrue( list.getVisible() );

      TestUtil.click( TestUtil.getDocument() );
      assertFalse( list.getVisible() );

      TestUtil.click( field );
      TestUtil.flush();
      assertTrue( list.getVisible() );
    },

    testListSelection : function() {
      combo.select( 1 );
      assertEquals( 1, list.getSelectionIndex() );
      assertEquals( "Java", field.getValue() );

      combo.setListVisible( true );
      assertEquals( 1, list.getSelectionIndex() );
    },

    testSelectionByArrowKeys : function() {
      combo.setEditable( true );
      combo.select( 3 );
      TestUtil.flush();

      TestUtil.press( field, "Down" );
      assertEquals( 4, list.getSelectionIndex() );
      assertEquals( "Simula", field.getValue() );

      TestUtil.press( field, "Up" );
      assertEquals( 3, list.getSelectionIndex() );
      assertEquals( "Ruby", field.getValue() );
    },

    testSelectionByArrowKeys_SendsSelectionEvent : function() {
      combo.setListVisible( true );
      combo.select( 1 );
      TestUtil.fakeListener( combo, "Selection", true );
      TestUtil.flush();
      TestUtil.initRequestLog();

      TestUtil.press( field, "Down" );

      assertEquals( "Python", field.getValue() );
      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( 2, message.findSetProperty( "w3", "selectionIndex" ) );
      assertNotNull( message.findNotifyOperation( "w3", "Selection" ) );
    },

    testSelectionByArrowKeys_SendsTextOnEditable : function() {
      combo.setEditable( true );
      combo.select( 1 );
      TestUtil.fakeListener( combo, "Selection", true );
      TestUtil.flush();
      TestUtil.initRequestLog();

      TestUtil.press( field, "Down" );

      assertEquals( "Python", field.getValue() );
      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "Python", message.findSetProperty( "w3", "text" ) );
      assertNotNull( message.findNotifyOperation( "w3", "Selection" ) );
    },

    testSelectionByArrowKeys_SendsModifyEvent : function() {
      combo.setListVisible( true );
      combo.select( 1 );
      TestUtil.fakeListener( combo, "Modify", true );
      TestUtil.flush();
      combo.focus();
      TestUtil.initRequestLog();

      TestUtil.press( field, "Down" );

      assertEquals( "Python", field.getValue() );
      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( 2, message.findSetProperty( "w3", "selectionIndex" ) );
      assertNotNull( message.findNotifyOperation( "w3", "Modify" ) );
    },

    testSendDefaultSelectionEvent : function() {
      TestUtil.fakeListener( combo, "DefaultSelection", true );
      TestUtil.flush();
      combo.focus();
      TestUtil.initRequestLog();

      TestUtil.press( field, "Enter" );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertNotNull( message.findNotifyOperation( "w3", "DefaultSelection" ) );
    },

    testSendListVislible : function() {
      TestUtil.click( button );
      TestUtil.flush();

      rwt.remote.Connection.getInstance().send();

      var message = TestUtil.getMessageObject();
      assertTrue( message.findSetProperty( "w3", "listVisible" ) );
    },

    testSendText : function() {
      combo.setEditable( true );
      combo.setListVisible( true );
      combo.select( 3 );
      TestUtil.flush();
      combo.focus();
      TestUtil.initRequestLog();

      field.setValue( "a" );
      field._oninput();
      rwt.remote.Connection.getInstance().send();

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "a", message.findSetProperty( "w3", "text" ) );
    },

    testSendModify : function() {
      TestUtil.fakeListener( combo, "Modify", true );
      combo.setEditable( true );
      combo.setListVisible( true );
      combo.select( 3 );
      TestUtil.flush();
      combo.focus();
      TestUtil.initRequestLog();

      field.setValue( "a" );
      field._oninput();
      TestUtil.forceInterval( rwt.remote.Connection.getInstance()._delayTimer );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "a", message.findSetProperty( "w3", "text" ) );
      assertNotNull( message.findNotifyOperation( "w3", "Modify" ) );
    },

    testSendModify_onChangeSelection : function() {
      TestUtil.fakeListener( combo, "Modify", true );
      combo.setEditable( true );
      combo.setListVisible( true );
      combo.select( 3 );
      TestUtil.flush();
      combo.focus();
      TestUtil.initRequestLog();

      field.setValue( "a" );
      field._oninput();
      TestUtil.press( field, "Down" );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "Eiffel", message.findSetProperty( "w3", "text" ) );
      assertNotNull( message.findNotifyOperation( "w3", "Modify" ) );
    },

    testSelectionByFirstLetter_ReadOnly : function() {
      TestUtil.press( field, "R" );

      assertEquals( "Ruby", field.getValue() );
    },

    testSelectionByFirstLetter_Editable : function() {
      combo.setEditable( true );

      TestUtil.keyDown( field.getElement(), "R" );

      assertEquals( -1, list.getSelectionIndex() );
    },

    testSelectionByFirstLetter_Wraps : function() {
      TestUtil.press( field, "S" );
      assertEquals( "Simula", field.getValue() );

      TestUtil.press( field, "S" );
      assertEquals( "Smalltalk", field.getValue() );

      TestUtil.press( field, "S" );
      assertEquals( "Simula", field.getValue() );
    },

    testSelectionByMouseWhell_Down : function() {
      combo.select( 3 );

      TestUtil.fakeWheel( field, -1 );

      assertEquals( 4, list.getSelectionIndex() );
      assertEquals( "Simula", field.getValue() );
    },

    testSelectionByMouseWhell_Up : function() {
      combo.select( 3 );

      TestUtil.fakeWheel( field, 1 );

      assertEquals( 2, list.getSelectionIndex() );
      assertEquals( "Python", field.getValue() );
    },

    testFiresItemsChangedEvent : function() {
      var log = 0;
      combo.addEventListener( "itemsChanged", function() {
        log++;
      } );

      combo.setItems( [ "Simula", "Smalltalk" ] );
      TestUtil.flush();

      assertEquals( 1, log );
    },

    testFiresSelectionChangedEvent : function() {
      var log = 0;
      combo.addEventListener( "selectionChanged", function() {
        log++;
      } );

      combo.select( 3 );

      assertEquals( 1, log );
    },

    testApplyListId_renderHtmlIdsActivated : function() {
      rwt.widgets.base.Widget._renderHtmlIds = true;

      combo.applyObjectId( "123" );

      assertEquals( "123-listbox" ,list._.popup.getHtmlAttribute( "id" ) );
      rwt.widgets.base.Widget._renderHtmlIds = false;
    },

    testApplyListId_renderHtmlIdsDeactivated : function() {
      rwt.widgets.base.Widget._renderHtmlIds = false;

      combo.applyObjectId( "123" );

      assertEquals( "" ,list._.popup.getHtmlAttribute( "id" ) );
    },

    testApplyListItemIds_renderHtmlIdsActivated : function() {
      rwt.widgets.base.Widget._renderHtmlIds = true;

      combo.applyObjectId( "123" );
      combo.setItems( [ "Eiffel", "Java", "Python", "Ruby", "Simula", "Smalltalk" ] );

      var listItemId = list._.popup.getHtmlAttribute( "id" ) + "-listitem-3";
      var item3 = list._.grid.getRootItem().getChild( 3 );
      assertEquals( listItemId, item3.getHtmlAttribute( "id" ) );
      rwt.widgets.base.Widget._renderHtmlIds = false;
    },

    testPropagateEscKey_ListIsVisible : function() {
      combo.setListVisible( true );
      var logger = TestUtil.getLogger();
      TestUtil.getDocument().addEventListener( "keydown", logger.log );

      TestUtil.press( field, "Escape" );

      assertEquals( 0, logger.getLog().length );
    },

    testPropagateEscKey_ListIsNotVisible : function() {
      combo.setListVisible( false );
      var logger = TestUtil.getLogger();
      TestUtil.getDocument().addEventListener( "keydown", logger.log );

      TestUtil.press( field, "Escape" );

      assertEquals( 1, logger.getLog().length );
    },

    testSendSelectionChangeOnMouseDown : function() {
      combo.setEditable( true );
      combo.setText( "foobar" );
      TestUtil.flush();

      TestUtil.fakeMouseEvent( field, "mousedown" );
      this._setTextSelection( field, [ 3, 3 ] );
      TestUtil.fakeMouseEvent( field, "mouseup" );
      rwt.remote.Connection.getInstance().send();

      assertEquals( [ 3, 3 ], TestUtil.getMessageObject().findSetProperty( "w3", "selection" ) );
    },

    testSendSelectionChangeOnKeyPress : function() {
      combo.setEditable( true );
      combo.setText( "foobar" );
      TestUtil.flush();
      field.focus();

      this._setTextSelection( field, [ 3, 3 ] );
      TestUtil.keyDown( field, "Enter" );
      rwt.remote.Connection.getInstance().send();

      assertEquals( [ 3, 3 ], TestUtil.getMessageObject().findSetProperty( "w3", "selection" ) );
    },

    //////////
    // Helpers

    _createDefaultCombo : function() {
      var combo = new rwt.widgets.Combo();
      combo.addToDocument();
      combo.setSpace( 239, 81, 6, 23 );
      combo.setEditable( false );
      combo.setItems( [ "Eiffel", "Java", "Python", "Ruby", "Simula", "Smalltalk" ] );
      combo.setVisibleItemCount( 5 );
      combo.setFocused( true );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.Combo" );
      rwt.remote.ObjectRegistry.add( "w3", combo, handler );
      list = combo._list;
      field = combo._field;
      button = combo._button;
      TestUtil.flush();
      return combo;
    },

    _setTextSelection : function( text, selection ) {
      text._setSelectionStart( selection[ 0 ] );
      text._setSelectionLength( selection[ 1 ] - selection[ 0 ] );
    }

  }

} );

}() );
