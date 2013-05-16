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

(function(){

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var ObjectRegistry = rwt.remote.ObjectRegistry;
var MessageProcessor = rwt.remote.MessageProcessor;

var fireOnScroll;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.ListTest", {

  extend : rwt.qx.Object,

  members : {

    testCreateListByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ "SINGLE" ],
          "parent" : "w2"
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget instanceof rwt.widgets.List );
      assertIdentical( shell, widget.getParent() );
      assertTrue( widget.getUserData( "isControl") );
      assertFalse( widget.getManager().getMultiSelection() );
      assertFalse( widget._markupEnabled );
      shell.destroy();
      widget.destroy();
    },

    testCreateListWithMultiByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2"
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget.getManager().getMultiSelection() );
      shell.destroy();
      widget.destroy();
    },

    testCreateListWithMarkupEnabled : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2",
          "markupEnabled" : true
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget._markupEnabled );
      shell.destroy();
      widget.destroy();
    },

    testSetItemsByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "items" : [ "a", "b", "c" ]
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      var items = widget.getItems();
      assertEquals( 3, widget.getItemsCount() );
      assertEquals( "a", items[ 0 ].getLabel() );
      assertEquals( "b", items[ 1 ].getLabel() );
      assertEquals( "c", items[ 2 ].getLabel() );
      shell.destroy();
      widget.destroy();
    },

    testSetItemsEscapeTextByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "items" : [ "  foo &\nbar " ]
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      var items = widget.getItems();
      assertEquals( "&nbsp; foo &amp; bar&nbsp;", items[ 0 ].getLabel() );
      assertEquals( -1, items[ 0 ].getFlexibleCell() );
      shell.destroy();
      widget.destroy();
    },

    testSetItemsWithMarkupEnabledByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
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
      var widget = ObjectRegistry.getObject( "w3" );
      var items = widget.getItems();
      assertEquals( "<b>bold</b>  </br>  <i>italic</i>", items[ 0 ].getLabel() );
      shell.destroy();
      widget.destroy();
    },

    testItemsWithMarkupHaveFlexiCell : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
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
      var widget = ObjectRegistry.getObject( "w3" );

      var items = widget.getItems();

      assertEquals( 0, items[ 0 ].getFlexibleCell() );
      shell.destroy();
      widget.destroy();
    },

    testItemCellHeightIgnoresBottomPadding : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [],
          "parent" : "w2",
          "items" : [ "foo" ],
          "itemDimensions" : [ 100, 30 ]
        }
      } );
      TestUtil.flush();
      var widget = ObjectRegistry.getObject( "w3" );

      var item = widget.getItems()[ 0 ];

      var paddingTop = 6;
      assertEquals( 30 - paddingTop, item.getCellHeight( 0 ) );
      assertEquals( 6, parseInt( item.getCellNode( 0 ).style.top, 10 ) );
      shell.destroy();
      widget.destroy();
    },

    testSetSingleSelectionIndicesByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
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
      var widget = ObjectRegistry.getObject( "w3" );
      var items = widget.getSelectedItems();
      assertEquals( 1, items.length );
      assertEquals( "c", items[ 0 ].getLabel() );
      shell.destroy();
      widget.destroy();
    },

    testSetMultiSelectionIndicesByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
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
      var widget = ObjectRegistry.getObject( "w3" );
      var items = widget.getSelectedItems();
      assertEquals( 2, items.length );
      assertEquals( "a", items[ 0 ].getLabel() );
      assertEquals( "c", items[ 1 ].getLabel() );
      shell.destroy();
      widget.destroy();
    },

    testSetAllSelectionIndicesByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
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
      var widget = ObjectRegistry.getObject( "w3" );
      var items = widget.getSelectedItems();
      assertEquals( 3, items.length );
      assertEquals( "a", items[ 0 ].getLabel() );
      assertEquals( "b", items[ 1 ].getLabel() );
      assertEquals( "c", items[ 2 ].getLabel() );
      shell.destroy();
      widget.destroy();
    },

    testSetTopIndexByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );

      MessageProcessor.processOperation( {
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

      var widget = ObjectRegistry.getObject( "w3" );
      assertEquals( 2, widget._topIndex );
      assertEquals( 200, widget.getVerticalBar().getValue() );
      shell.destroy();
      widget.destroy();
    },

    testSetFocusIndexByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
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
      var widget = ObjectRegistry.getObject( "w3" );
      var focusItem = widget.getManager().getLeadItem();
      assertEquals( "c", focusItem.getLabel() );
      shell.destroy();
      widget.destroy();
    },

    testSetScrollBarsNotVisibleByDefault : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );

      MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2"
        }
      } );

      var widget = ObjectRegistry.getObject( "w3" );
      assertFalse( widget._horzScrollBar.getDisplay() );
      assertFalse( widget._vertScrollBar.getDisplay() );
      shell.destroy();
      widget.destroy();
    },

    testSetScrollBarsVisibleByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
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

      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget._horzScrollBar.getDisplay() );
      assertTrue( widget._vertScrollBar.getDisplay() );
      shell.destroy();
      widget.destroy();
    },

    testSetItemDimensionsByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2",
          "itemDimensions" : [ 10, 20 ]
        }
      } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertEquals( 10, widget._itemWidth );
      assertEquals( 20, widget._itemHeight );
      shell.destroy();
      widget.destroy();
    },

    testSetHasSelectionListenerByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2"
        }
      } );
      TestUtil.protocolListen( "w3", { "Selection" : true } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget._hasSelectionListener );
      shell.destroy();
      widget.destroy();
    },

    testSetHasDefaultSelectionListenerByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2"
        }
      } );
      TestUtil.protocolListen( "w3", { "DefaultSelection" : true } );
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget._hasDefaultSelectionListener );
      shell.destroy();
      widget.destroy();
    },

    testSetHasFocusListenerByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
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
      var widget = ObjectRegistry.getObject( "w3" );
      assertTrue( widget.hasEventListeners( "focusin" ) );
      assertTrue( widget.hasEventListeners( "focusout" ) );
      shell.destroy();
      widget.destroy();
    },

    testCreateDispose : function() {
      var list = this._createDefaultList();
      assertTrue( list instanceof rwt.widgets.List );
      list.destroy();
      TestUtil.flush();
      assertTrue( list.isDisposed() );
    },

    testSetItems : function() {
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
      var list = this._createDefaultList();
      this._addItems( list, 3 );
      TestUtil.flush();
      list.selectItem( 2 );
      var selection = this._getSelection( list );
      assertEquals( 1, selection.length );
      assertEquals( "item2", selection[ 0 ].getLabel() );
      list.destroy();
    },

    testSelectItem_ScrollDown : function() {
      var list = this._createDefaultList();
      this._addItems( list, 100 );
      TestUtil.flush();

      list.selectItem( 40 );

      var selection = this._getSelection( list );
      assertEquals( [ 0, 394 ], this._getScrollPosition( list ) );
      list.destroy();
    },

    testSelectItem_ScrollUp : function() {
      var list = this._createDefaultList();
      this._addItems( list, 100 );
      TestUtil.flush();

      list.selectItem( 99 );
      list.selectItem( 40 );

      var selection = this._getSelection( list );
      assertEquals( [ 0, 800 ], this._getScrollPosition( list ) );
      list.destroy();
    },

    testSelectItemByCharacter : function() {
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
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
        "target" : "w3",
        "action" : "create",
        "type" : "rwt.widgets.List",
        "properties" : {
          "style" : [ "MULTI" ],
          "parent" : "w2",
          "markupEnabled" : true
        }
      } );
      var list = ObjectRegistry.getObject( "w3" );

      list.setItems( [ "Akira", "Boogiepop", "<i>C</i> something", "Daria" ] );
      TestUtil.flush();

      TestUtil.press( list, "c" );

      var selection = this._getSelection( list );
      assertEquals( 1, selection.length );
      assertEquals( "<i>C</i> something", selection[ 0 ].getLabel() );
      list.destroy();
    },

    testSelectItems : function() {
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
      var list = this._createDefaultList();
      list.setItems( [ "item0", "item1", "item2" ] );
      list.focusItem( 1 );
      TestUtil.flush();
      assertEquals( "item1", this._getLeadItem( list ).getLabel() );
      list.selectAll();
      list.destroy();
    },

    testTopIndex : function() {
      var list = this._createDefaultList();
      this._addItems( list, 300 );
      TestUtil.flush();
      list.getVerticalBar().setHasSelectionListener( true );

      list.getVerticalBar().setValue( 40 );

      assertEquals( 2, list._topIndex );
      list.destroy();
    },

    testSendVerticalScrollPosition : function() {
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
      var list = this._createDefaultList();
      list.setItems( [ "item0", "item1", "item2" ] );
      TestUtil.flush();
      var item = this._getItems( list )[ 1 ];
      list.setHasSelectionListener( true );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.List" );
      ObjectRegistry.add( "w3", list, handler );

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

    testSetItemDimensions_UpdatesScrollPosition : function() {
      var list = this._createDefaultList( true );
      list.setItemDimensions( 200, 20 );
      this._addItems( list, 100 );
      list.setTopIndex( 20 );
      TestUtil.flush();

      list.setItemDimensions( 100, 30 );
      TestUtil.flush();

      assertEquals( 30 * 20, this._getScrollPosition( list )[ 1 ] );
      list.destroy();
    },

    testSendDefaultSelected : function() {
      var list = this._createDefaultList();
      list.setItems( [ "item0", "item1", "item2" ] );
      TestUtil.flush();
      var item = this._getItems( list )[ 1 ];
      list.setHasDefaultSelectionListener( true );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.List" );
      ObjectRegistry.add( "w3", list, handler );

      TestUtil.doubleClick( item );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var msg = TestUtil.getLastMessage();
      assertNotNull( msg.findNotifyOperation( "w3", "DefaultSelection" ) );
      list.selectAll();
      list.destroy();
    },

    testClickOnRWTHyperlinkWithHref : function() {
      var list = this._createDefaultList();
      list.setMarkupEnabled( true );
      list.setItems( [ "<a href=\"foo\" target=\"_rwt\">Test</a>" ] );
      list.setHasSelectionListener( true );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.List" );
      ObjectRegistry.add( "w3", list, handler );
      TestUtil.flush();
      TestUtil.initRequestLog();
      var node = this._getItems( list )[ 0 ]._getTargetNode().childNodes[ 0 ].childNodes[ 0 ];

      TestUtil.clickDOM( node );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "hyperlink", message.findNotifyProperty( "w3", "Selection", "detail" ) );
      var text = message.findNotifyProperty( "w3", "Selection", "text" );
      if( text.indexOf( "/" ) !== 0 ) {
        text = text.slice( text.lastIndexOf( "/" ) + 1 );
      }
      assertEquals( "foo", text );
    },

    testClickOnRWTHyperlinkWithoutHref : function() {
      var list = this._createDefaultList();
      list.setMarkupEnabled( true );
      list.setItems( [ "<a target=\"_rwt\">Test</a>" ] );
      list.setHasSelectionListener( true );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.List" );
      ObjectRegistry.add( "w3", list, handler );
      TestUtil.flush();
      TestUtil.initRequestLog();
      var node = this._getItems( list )[ 0 ]._getTargetNode().childNodes[ 0 ].childNodes[ 0 ];

      TestUtil.clickDOM( node );

      assertEquals( 1, TestUtil.getRequestsSend() );
      var message = TestUtil.getMessageObject();
      assertEquals( "hyperlink", message.findNotifyProperty( "w3", "Selection", "detail" ) );
      assertEquals( "Test", message.findNotifyProperty( "w3", "Selection", "text" ) );
    },

    testBasicLayout : function() {
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

    // see 395053
    testScrollManualAddItemsAndScroll : function() {
      var list = this._createDefaultList();
      list.setItemDimensions( 500, 20 );
      this._addItems( list, 30 );
      list.setTopIndex( 30 );
      TestUtil.flush();
      TestUtil.forceTimerOnce();
      TestUtil.click( list.getVerticalBar()._minButton );
      fireOnScroll();
      TestUtil.flush();
      TestUtil.forceTimerOnce();

      this._addItems( list, 70 );
      list.setTopIndex( 40 );
      fireOnScroll();
      TestUtil.flush();
      TestUtil.forceTimerOnce();

      var position = this._getScrollPosition( list );
      assertEquals( [ 0, 40 * 20 ], position );
      list.destroy();
    },

    testDispose: function() {
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
      var list = this._createDefaultList( true );
      this._addItems( list, 70 );
      list.setItemDimensions( 500, 20 );
      list.setHBarSelection( 10 );
      list.setTopIndex( 1 );
      TestUtil.flush();
      var position = this._getScrollPosition( list );
      assertEquals( [ 10, 20 ], position );
      list.destroy();
    },

    testSyncScrollBars : function() {
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
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
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

      var widget = ObjectRegistry.getObject( "w3" );
      var items = widget.getItems();
      assertTrue( items[ 0 ].hasState( "variant_myVariant" ) );
      assertTrue( items[ 1 ].hasState( "variant_myVariant" ) );
      assertTrue( items[ 2 ].hasState( "variant_myVariant" ) );
      shell.destroy();
      widget.destroy();
    },

    testApplyCustomVariantToNewItems : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
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

      var widget = ObjectRegistry.getObject( "w3" );
      var items = widget.getItems();
      assertTrue( items[ 0 ].hasState( "variant_myVariant" ) );
      assertTrue( items[ 1 ].hasState( "variant_myVariant" ) );
      assertTrue( items[ 2 ].hasState( "variant_myVariant" ) );
      shell.destroy();
      widget.destroy();
    },

    testReplaceCustomVariant : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      MessageProcessor.processOperation( {
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

      var widget = ObjectRegistry.getObject( "w3" );
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
      var list = new rwt.widgets.List( true );
      var onScrollFired = false;
      list.__onscroll = rwt.util.Functions.bindEvent( function( ev ) {
        list._onscroll.call( list, ev );
        onScrollFired = true;
      }, list );
      fireOnScroll = function() {
        if( !onScrollFired ) { // some browser fire the event synchronously, some don't
          list.__onscroll( {} );
          onScrollFired = false;
        }
      };
      list.setItemDimensions( 100, 20 );
      list.addToDocument();
      list.setSpace( 5, 238, 5, 436 );
      var handler = rwt.remote.HandlerRegistry.getHandler( "rwt.widgets.List" );
      ObjectRegistry.add( "w3", list, handler );
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

}());
