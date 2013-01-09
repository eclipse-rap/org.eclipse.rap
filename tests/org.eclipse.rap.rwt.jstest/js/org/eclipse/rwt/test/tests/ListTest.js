/*******************************************************************************
 * Copyright (c) 2010, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ListTest", {

  extend : rwt.qx.Object,

  members : {

    testCreateListByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ "SINGLE" ],
          "parent" : "w2"
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.List );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertFalse( widget.getManager().getMultiSelection() );
      assertFalse( widget._markupEnabled );
      shell.destroy();
      widget.destroy();
    },

    testCreateListWithMultiByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2"
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget.getManager().getMultiSelection() );
      shell.destroy();
      widget.destroy();
    },

    testCreateListWithMarkupEnabled : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2",
          "markupEnabled" : true
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget._markupEnabled );
      shell.destroy();
      widget.destroy();
    },

    testSetItemsByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "items" : [ "a", "b", "c" ]
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      var items = widget.getItems();
      assertEquals( 3, widget.getItemsCount() );
      assertEquals( "a", items[ 0 ].getLabel() );
      assertEquals( "b", items[ 1 ].getLabel() );
      assertEquals( "c", items[ 2 ].getLabel() );
      shell.destroy();
      widget.destroy();
    },

    testSetItemsEscapeTextByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "items" : [ "  foo &\nbar " ]
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      var items = widget.getItems();
      assertEquals( "&nbsp; foo &amp; bar&nbsp;", items[ 0 ].getLabel() );
      shell.destroy();
      widget.destroy();
    },

    testSetItemsWithMarkupEnabledByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "markupEnabled" : true,
          "items" : [ "<b>bold</b>  </br>  <i>italic</i>" ]
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      var items = widget.getItems();
      assertEquals( "<b>bold</b>  </br>  <i>italic</i>", items[ 0 ].getLabel() );
      shell.destroy();
      widget.destroy();
    },

    testSetSingleSelectionIndicesByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ "SINGLE" ],
          "parent" : "w2",
          "items" : [ "a", "b", "c" ],
          "selectionIndices" : [ 2 ]
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      var items = widget.getSelectedItems();
      assertEquals( 1, items.length );
      assertEquals( "c", items[ 0 ].getLabel() );
      shell.destroy();
      widget.destroy();
    },

    testSetMultiSelectionIndicesByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2",
          "items" : [ "a", "b", "c" ],
          "selectionIndices" : [ 0, 2 ]
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      var items = widget.getSelectedItems();
      assertEquals( 2, items.length );
      assertEquals( "a", items[ 0 ].getLabel() );
      assertEquals( "c", items[ 1 ].getLabel() );
      shell.destroy();
      widget.destroy();
    },

    testSetAllSelectionIndicesByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2",
          "items" : [ "a", "b", "c" ],
          "selectionIndices" : [ 0, 1, 2 ]
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      var items = widget.getSelectedItems();
      assertEquals( 3, items.length );
      assertEquals( "a", items[ 0 ].getLabel() );
      assertEquals( "b", items[ 1 ].getLabel() );
      assertEquals( "c", items[ 2 ].getLabel() );
      shell.destroy();
      widget.destroy();
    },

    testSetTopIndexByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;

      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2",
          "bounds" : [ 0, 0, 100, 100 ],
          "items" : [ "a", "b", "c" ],
          "topIndex" : 2,
          "itemDimensions" : [ 100, 100 ]
        }
      } );
      TestUtil.flush();

      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 2, widget._topIndex );
      assertEquals( 200, widget.getVerticalBar().getValue() );
      shell.destroy();
      widget.destroy();
    },

    testSetFocusIndexByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2",
          "items" : [ "a", "b", "c" ],
          "focusIndex" : 2
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      var focusItem = widget.getManager().getLeadItem();
      assertEquals( "c", focusItem.getLabel() );
      shell.destroy();
      widget.destroy();
    },

    testSetScrollBarsNotVisibleByDefault : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;

      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2"
        }
      } );

      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertFalse( widget._horzScrollBar.getDisplay() );
      assertFalse( widget._vertScrollBar.getDisplay() );
      shell.destroy();
      widget.destroy();
    },

    testSetScrollBarsVisibleByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2"
        }
      } );
      this._createProtocolScrollBars( "w3" );

      TestUtil.protocolSet( "w3_vscroll", { "visibility" : true } );
      TestUtil.protocolSet( "w3_hscroll", { "visibility" : true } );

      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget._horzScrollBar.getDisplay() );
      assertTrue( widget._vertScrollBar.getDisplay() );
      shell.destroy();
      widget.destroy();
    },

    testSetItemDimensionsByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2",
          "itemDimensions" : [ 10, 20 ]
        }
      } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertEquals( 10, widget._itemWidth );
      assertEquals( 20, widget._itemHeight );
      shell.destroy();
      widget.destroy();
    },

    testSetHasSelectionListenerByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2"
        }
      } );
      TestUtil.protocolListen( "w3", { "Selection" : true } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget._hasSelectionListener );
      shell.destroy();
      widget.destroy();
    },

    testSetHasDefaultSelectionListenerByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2"
        }
      } );
      TestUtil.protocolListen( "w3", { "DefaultSelection" : true } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget._hasDefaultSelectionListener );
      shell.destroy();
      widget.destroy();
    },

    testSetHasFocusListenerByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2"
        }
      } );
      TestUtil.protocolListen( "w3", { "FocusIn" : true } );
      TestUtil.protocolListen( "w3", { "FocusOut" : true } );
      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      assertTrue( widget.hasEventListeners( "focusin" ) );
      assertTrue( widget.hasEventListeners( "focusout" ) );
      shell.destroy();
      widget.destroy();
    },

    testCreateDispose : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      assertTrue( list instanceof rwt.widgets.List );
      list.destroy();
      TestUtil.flush();
      assertTrue( list.isDisposed() );
    },

    testSetItems : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      this._addItems( list, 3 );
      TestUtil.flush();
      var items = this._getItems( list );
      assertEquals( 3, items.length );
      assertEquals( "item0", items[ 0 ].getLabel() );
      assertEquals( "item1", items[ 1 ].getLabel() );
      assertEquals( "item2", items[ 2 ].getLabel() );
      list.destroy();
    },

    testHoverItem : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      this._fakeAppearance();
      var list = this._createDefaultList();
      this._addItems( list, 3 );
      TestUtil.flush();
      var items = this._getItems( list );
      assertEquals( "white", TestUtil.getCssBackgroundColor( items[ 1 ] ) );
      TestUtil.mouseOver( items[ 1 ] );
      assertTrue( items[ 1 ].hasState( "over" ) );
      assertEquals( "green", TestUtil.getCssBackgroundColor( items[ 1 ] ) );
      TestUtil.mouseOut( items[ 1 ] );
      assertFalse( items[ 1 ].hasState( "over" ) );
      assertEquals( "white", TestUtil.getCssBackgroundColor( items[ 1 ] ) );
      list.destroy();
    },

    testHoverEvenItem : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      this._fakeAppearance();
      var list = this._createDefaultList();
      this._addItems( list, 3 );
      TestUtil.flush();
      var items = this._getItems( list );
      assertEquals( "blue", TestUtil.getCssBackgroundColor( items[ 0 ] ) );
      TestUtil.mouseOver( items[ 0 ] );
      assertTrue( items[ 0 ].hasState( "over" ) );
      assertEquals( "red", TestUtil.getCssBackgroundColor( items[ 0 ] ) );
      TestUtil.mouseOut( items[ 0 ] );
      assertFalse( items[ 0 ].hasState( "over" ) );
      assertEquals( "blue", TestUtil.getCssBackgroundColor( items[ 0 ] ) );
      list.destroy();
    },

    testSelectItem : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      this._addItems( list, 3 );
      TestUtil.flush();
      list.selectItem( 2 );
      var selection = this._getSelection( list );
      assertEquals( 1, selection.length );
      assertEquals( "item2", selection[ 0 ].getLabel() );
      list.destroy();
    },

    testSelectItemByCharacter : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      list.setItems( [ "Akira", "Boogiepop", "C something", "Daria" ] );
      TestUtil.flush();
      TestUtil.press( list, "c" );
      var selection = this._getSelection( list );
      assertEquals( 1, selection.length );
      assertEquals( "C something", selection[ 0 ].getLabel() );
      list.destroy();
    },

    testSelectMarkupItemByCharacter : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var ObjectManager = rwt.remote.ObjectRegistry;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2",
          "markupEnabled" : true
        }
      } );
      var list = ObjectManager.getObject( "w3" );

      list.setItems( [ "Akira", "Boogiepop", "<i>C</i> something", "Daria" ] );
      TestUtil.flush();

      TestUtil.press( list, "c" );

      var selection = this._getSelection( list );
      assertEquals( 1, selection.length );
      assertEquals( "<i>C</i> something", selection[ 0 ].getLabel() );
      list.destroy();
    },

    testSelectItems : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      this._addItems( list, 3 );
      TestUtil.flush();
      list.selectItems( [ 1, 2 ] );
      var selection = this._getSelection( list );
      assertEquals( 2, selection.length );
      assertEquals( "item1", selection[ 0 ].getLabel() );
      assertEquals( "item2", selection[ 1 ].getLabel() );
      list.destroy();
    },

    testSelectAll : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      this._addItems( list, 3 );
      TestUtil.flush();
      list.selectAll();
      var selection = this._getSelection( list );
      assertEquals( 3, selection.length );
      assertEquals( "item0", selection[ 0 ].getLabel() );
      assertEquals( "item1", selection[ 1 ].getLabel() );
      assertEquals( "item2", selection[ 2 ].getLabel() );
      list.destroy();
    },

    testFocusItem : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      list.setItems( [ "item0", "item1", "item2" ] );
      list.focusItem( 1 );
      TestUtil.flush();
      assertEquals( "item1", this._getLeadItem( list ).getLabel() );
      list.selectAll();
      list.destroy();
    },

    testSendVerticalScrollPosition : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      this._addItems( list, 300 );
      TestUtil.flush();
      list.getVerticalBar().setHasSelectionListener( true );

      list.getVerticalBar().setValue( 40 );
      rwt.remote.Server.getInstance().send();

      var message = TestUtil.getLastMessage();
      assertEquals( 2, message.findSetProperty( "w3", "topIndex" ) );
      list.destroy();
    },

    testSendSelection : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      list.setItems( [ "item0", "item1", "item2" ] );
      TestUtil.flush();
      var item = this._getItems( list )[ 1 ];
      list.setHasSelectionListener( true );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.List" );
      rwt.remote.ObjectRegistry.add( "w3", list, handler );

      TestUtil.click( item );

      assertEquals( 1, TestUtil.getRequestsSend() );
      assertEquals( [ 1 ], TestUtil.getMessageObject().findSetProperty( "w3", "selection" ) );
      list.destroy();
    },

    testItemHasNoOverflow : function() {
      var list = this._createDefaultList();
      this._addItems( list, 1 );

      var items = this._getItems( list );

      assertEquals( "hidden", items[ 0 ].getOverflow() );
      assertFalse( "hidden", items[ 0 ].getContainerOverflow() );
      list.destroy();
    },

    testSetItemDimensions : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      list.setItemDimensions( 200, 20 );
      this._addItems( list, 3 );
      TestUtil.flush();
      var items = this._getItems( list );
      assertEquals( 200, items[ 0 ].getWidth() );
      assertEquals( 20, items[ 0 ].getHeight() );
      assertEquals( 20, list._vertScrollBar._increment );
      assertEquals( 20, list._vertScrollBar._increment );
      list.setItemDimensions( 100, 30 );
      TestUtil.flush();
      items = this._getItems( list );
      assertEquals( 100, items[ 0 ].getWidth() );
      assertEquals( 30, items[ 0 ].getHeight() );
      assertEquals( 30, list._vertScrollBar._increment );
      list.destroy();
    },

    testSendDefaultSelected : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      list.setItems( [ "item0", "item1", "item2" ] );
      TestUtil.flush();
      var item = this._getItems( list )[ 1 ];
      list.setHasDefaultSelectionListener( true );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.List" );
      rwt.remote.ObjectRegistry.add( "w3", list, handler );

      TestUtil.doubleClick( item );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var msg = TestUtil.getLastMessage();
      assertNotNull( msg.findNotifyOperation( "w3", "DefaultSelection" ) );
      list.selectAll();
      list.destroy();
    },

    testBasicLayout : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      var client = list._clientArea;
      var hbar = list._horzScrollBar;
      var vbar = list._vertScrollBar;
      var barWidth = 10;
      assertIdentical( list, client.getParent() );
      assertIdentical( list, hbar.getParent() );
      assertIdentical( list, vbar.getParent() );
      var clientBounds = TestUtil.getElementBounds( client.getElement() );
      var hbarBounds = TestUtil.getElementBounds( hbar.getElement() );
      var vbarBounds = TestUtil.getElementBounds( vbar.getElement() );
      assertEquals( 0, clientBounds.left );
      assertEquals( 0, clientBounds.top );
      assertEquals( barWidth, clientBounds.right );
      assertEquals( barWidth, clientBounds.bottom );
      assertEquals( 0, hbarBounds.left );
      assertEquals( barWidth, hbarBounds.right );
      assertEquals( 0, vbarBounds.top );
      assertEquals( barWidth, vbarBounds.bottom );
      assertEquals( clientBounds.width, vbarBounds.left );
      assertEquals( clientBounds.height, hbarBounds.top );
      list.destroy();
    },

    testScrollBarVisibility : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      list.setScrollBarsVisible( false, false );
      TestUtil.flush();
      assertFalse( this._isScrollbarVisible( list, true ) );
      assertFalse( this._isScrollbarVisible( list, false ) );
      list.setScrollBarsVisible( true, false );
      TestUtil.flush();
      assertTrue( this._isScrollbarVisible( list, true ) );
      assertFalse( this._isScrollbarVisible( list, false ) );
      list.setScrollBarsVisible( false, true );
      TestUtil.flush();
      assertFalse( this._isScrollbarVisible( list, true ) );
      assertTrue( this._isScrollbarVisible( list, false ) );
      list.setScrollBarsVisible( true, true );
      TestUtil.flush();
      assertTrue( this._isScrollbarVisible( list, true ) );
      assertTrue( this._isScrollbarVisible( list, false ) );
      list.destroy();
    },

    testRelayoutOnScrollBarShowHide : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      list.setScrollBarsVisible( false, true );
      TestUtil.flush();
      var client = list._clientArea;
      var clientBounds = TestUtil.getElementBounds( client.getElement() );
      list.setScrollBarsVisible( true, false );
      TestUtil.flush();
      var newClientBounds = TestUtil.getElementBounds( client.getElement() );
      assertTrue( clientBounds.width < newClientBounds.width );
      assertTrue( clientBounds.height > newClientBounds.height );
      list.destroy();
    },

    testScrollBarMaximum : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      this._addItems( list, 10 );
      list.setItemDimensions( 240, 25 );
      var item = list._clientArea.getFirstChild();
      TestUtil.flush();
      assertEquals( 240, list._horzScrollBar.getMaximum() );
      assertEquals( 250, list._vertScrollBar.getMaximum() );
      list.destroy();
    },

    testAddItemsChangesScrollBarMaximum : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      this._addItems( list, 5 );
      list.setItemDimensions( 240, 25 );
      TestUtil.flush();

      this._addItems( list, 10 );
      TestUtil.flush();

      var item = list._clientArea.getFirstChild();
      assertEquals( 240, list._horzScrollBar.getMaximum() );
      assertEquals( 250, list._vertScrollBar.getMaximum() );
      list.destroy();
    },

    testScrollWhileInvisible : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      list.setItemDimensions( 500, 20 );
      this._addItems( list, 70 );
      TestUtil.flush();

      list.hide();
      list.setHBarSelection( 10 );
      list.setTopIndex( 1 );
      list.show();
      TestUtil.flush();

      var position = this._getScrollPosition( list );
      assertEquals( [ 10, 20 ], position );
      list.destroy();
    },

    testAddItemsAndScroll : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      list.setItemDimensions( 500, 20 );
      this._addItems( list, 10 );
      TestUtil.flush();

      this._addItems( list, 70 );
      list.setHBarSelection( 10 );
      list.setTopIndex( 20 );
      TestUtil.flush();
      TestUtil.forceTimerOnce();

      var position = this._getScrollPosition( list );
      assertEquals( [ 10, 400 ], position );
      list.destroy();
    },

    testDispose: function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      var clientArea = list._clientArea;
      var hbar = list._horzScrollBar;
      var vbar = list._vertScrollBar;
      var scrollNode = clientArea._getTargetNode();

      list.destroy();
      TestUtil.flush();

      assertNull( list._horzScrollBar );
      assertNull( list._vertScrollBar );
      assertNull( list._clientArea );
      assertTrue( list.isDisposed() );
      assertTrue( clientArea.isDisposed() );
      assertTrue( hbar.isDisposed() );
      assertTrue( vbar.isDisposed() );
      assertNull( list.hasEventListeners( "changeParent" ) );
      assertNull( clientArea.hasEventListeners( "appear" ) );
      assertNull( clientArea.hasEventListeners( "mousewheel" ) );
      assertNull( clientArea.hasEventListeners( "keypress" ) );
      assertNull( hbar.hasEventListeners( "changeValue" ) );
      assertNull( vbar.hasEventListeners( "changeValue" ) );
    },

    testInitialPosition : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList( true );
      list.setHBarSelection( 10 );
      list.setTopIndex( 1 );
      this._addItems( list, 70 );
      list.setItemDimensions( 500, 20 );
      TestUtil.flush();
      var position = this._getScrollPosition( list );
      assertEquals( [ 10, 20 ], position );
      list.destroy();
    },

    testSyncScrollBars : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      this._addItems( list, 70 );
      list.setItemDimensions( 500, 20 );
      TestUtil.flush();
      list._clientArea.setScrollLeft( 10 );
      list._clientArea.setScrollTop( 20 );
      list._onscroll( {} );
      assertEquals( 10, list._horzScrollBar.getValue() );
      assertEquals( 20, list._vertScrollBar.getValue() );
      list.destroy();
    },

    testNoScrollStyle : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      this._addItems( list, 70 );
      list.setItemDimensions( 500, 20 );
      list.setScrollBarsVisible( false, false );
      TestUtil.flush();
      list._clientArea.setScrollLeft( 50 );
      list._clientArea.setScrollTop( 70 );
      list._onscroll( {} );
      TestUtil.forceTimerOnce();
      var position = this._getScrollPosition( list );
      assertEquals( [ 0, 0 ], position );
      list.destroy();
    },

    testOnlyHScrollStyle : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      this._addItems( list, 70 );
      list.setItemDimensions( 500, 20 );
      list.setScrollBarsVisible( true, false );
      TestUtil.flush();
      TestUtil.flush();
      list._clientArea.setScrollLeft( 50 );
      list._clientArea.setScrollTop( 70 );
      list._onscroll( {} );
      var position = this._getScrollPosition( list );
      assertEquals( [ 50, 0 ], position );
      list.destroy();
    },

    testOnlyVScrollStyle : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      this._addItems( list, 70 );
      list.setItemDimensions( 500, 20 );
      list.setScrollBarsVisible( false, true );
      TestUtil.flush();
      list._clientArea.setScrollLeft( 50 );
      list._clientArea.setScrollTop( 70 );
      list._onscroll( {} );
      var position = this._getScrollPosition( list );
      assertEquals( [ 0, 70 ], position );
      list.destroy();
    },

    testApplyCustomVariantToExistingItems : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2",
          "items" : [ "a", "b", "c" ]
        }
      } );

      TestUtil.protocolSet( "w3", { "customVariant" : "variant_myVariant" } );

      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      var items = widget.getItems();
      assertTrue( items[ 0 ].hasState( "variant_myVariant" ) );
      assertTrue( items[ 1 ].hasState( "variant_myVariant" ) );
      assertTrue( items[ 2 ].hasState( "variant_myVariant" ) );
      shell.destroy();
      widget.destroy();
    },

    testApplyCustomVariantToNewItems : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2",
          "customVariant" : "variant_myVariant"
        }
      } );
      TestUtil.flush();

      TestUtil.protocolSet( "w3", { "items" : [ "a", "b", "c" ] } );

      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      var items = widget.getItems();
      assertTrue( items[ 0 ].hasState( "variant_myVariant" ) );
      assertTrue( items[ 1 ].hasState( "variant_myVariant" ) );
      assertTrue( items[ 2 ].hasState( "variant_myVariant" ) );
      shell.destroy();
      widget.destroy();
    },

    testReplaceCustomVariant : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var processor = rwt.remote.MessageProcessor;
      processor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2",
          "customVariant" : "variant_myVariant",
          "items" : [ "a", "b", "c" ]
        }
      } );
      TestUtil.flush();

      TestUtil.protocolSet( "w3", { "customVariant" : "variant_other" } );

      var ObjectManager = rwt.remote.ObjectRegistry;
      var widget = ObjectManager.getObject( "w3" );
      var items = widget.getItems();
      assertFalse( items[ 0 ].hasState( "variant_myVariant" ) );
      assertFalse( items[ 1 ].hasState( "variant_myVariant" ) );
      assertFalse( items[ 2 ].hasState( "variant_myVariant" ) );
      assertTrue( items[ 0 ].hasState( "variant_other" ) );
      assertTrue( items[ 1 ].hasState( "variant_other" ) );
      assertTrue( items[ 2 ].hasState( "variant_other" ) );
      shell.destroy();
      widget.destroy();
    },

    //////////
    // Helpers

    _createDefaultList : function( noflush ) {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = new rwt.widgets.List( true );
      list.setItemDimensions( 100, 20 );
      list.addToDocument();
      list.setSpace( 5, 238, 5, 436 );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.List" );
      rwt.remote.ObjectRegistry.add( "w3", list, handler );
      this._createProtocolScrollBars( "w3" );
      if( noflush !== true ) {
        TestUtil.flush();
      }

      return list;
    },

    _addItems : function( list, number ) {
      var items = [];
      for( var i = 0; i < number; i++ ) {
        items.push( "item" + i );
      }
      list.setItems( items );
    },

    _getItems : function( list ) {
      return list._clientArea.getChildren();
    },

    _getSelection : function( list ) {
      return list.getSelectedItems();
    },

    _getLeadItem : function( list ) {
      return list.getManager().getLeadItem();
    },

    _getTopItemIndex : function( list ) {
      return list._getTopIndex();
    },

    _isScrollbarVisible : function( list, horiz ) {
      var result;
      if( horiz ) {
        result = list._horzScrollBar.isSeeable();
      } else {
        result = list._vertScrollBar.isSeeable();
      }
      return result;
    },

    _getScrollPosition : function( list ) {
      var client = list._clientArea;
      return [ client.getScrollLeft(), client.getScrollTop() ];
    },

    _fakeAppearance : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      TestUtil.fakeAppearance( "list-item", {
        style : function( states ) {
          var result = {
            height : "auto",
            horizontalChildrenAlign : "left",
            verticalChildrenAlign : "middle",
            spacing : 4,
            padding : [ 3, 5 ],
            minWidth : "auto"
          };
          if( states.over && states.even ) {
            result.backgroundColor = "red";
          } else if( states.over && !states.even ) {
            result.backgroundColor = "green";
          } else if( !states.over && states.even ) {
            result.backgroundColor = "blue";
          } else {
            result.backgroundColor = "white";
          }
          result.textColor = "black";
          result.backgroundImage = null;
          result.backgroundGradient = null;
          return result;
        }
      } );
    },

    _createProtocolScrollBars : function( id ) {
      rwt.remote.MessageProcessor.processOperation( {
        "target" : id + "_vscroll",
        "action" : "create",
        "type" : "rwt.widgets.ScrollBar",
        "properties" : {
          "parent" : id,
          "style" : [ "VERTICAL" ],
          "visibility" : true
        }
      } );
      rwt.remote.MessageProcessor.processOperation( {
        "target" : id + "_hscroll",
        "action" : "create",
        "type" : "rwt.widgets.ScrollBar",
        "properties" : {
          "parent" : id,
          "style" : [ "HORIZONTAL" ],
          "visibility" : true
        }
      } );
    }

  }

} );
