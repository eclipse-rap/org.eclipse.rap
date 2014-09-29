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

/*jshint nonew:false */
(function(){

var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
var MessageProcessor = rwt.remote.MessageProcessor;
var ObjectRegistry = rwt.remote.ObjectRegistry;

rwt.qx.Class.define( "org.eclipse.rwt.test.tests.GridItemTest", {

  extend : rwt.qx.Object,

  members : {

    testCreateTreeItemByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.GridItem",
        "properties" : {
          "parent" : "w3",
          "index": 3
        }
      } );
      var item = ObjectRegistry.getObject( "w4" );
      assertTrue( item instanceof rwt.widgets.GridItem );
      assertIdentical( tree.getRootItem(), item.getParent() );
      assertNull( item.getUserData( "isControl") );
      assertEquals( 3, tree.getRootItem().indexOf( item ) );
      shell.destroy();
      tree.destroy();
    },

    testDestroyTreeItemByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.GridItem",
        "properties" : {
          "parent" : "w3",
          "index": 3
        }
      } );
      var item = ObjectRegistry.getObject( "w4" );
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "destroy"
      } );
      TestUtil.flush();
      assertNull( ObjectRegistry.getObject( "w4" ) );
      assertEquals( -1, tree.getRootItem().indexOf( item ) );
      assertTrue( TestUtil.hasNoObjects( item, true ) );
      shell.destroy();
      tree.destroy();
    },

    testDestroyGridItemWithChildItemByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.GridItem",
        "properties" : {
          "parent" : "w3",
          "index": 0
        }
      } );
      MessageProcessor.processOperation( {
        "target" : "w5",
        "action" : "create",
        "type" : "rwt.widgets.GridItem",
        "properties" : {
          "parent" : "w4",
          "index": 0
        }
      } );
      var item = ObjectRegistry.getObject( "w4" );
      var childItem = ObjectRegistry.getObject( "w5" );

      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "destroy"
      } );
      TestUtil.flush();

      assertNull( ObjectRegistry.getObject( "w4" ) );
      assertNull( ObjectRegistry.getObject( "w5" ) );
      shell.destroy();
    },


    testDestroyGridItemWithVirtualChildItemByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.GridItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "itemCount" : 1
        }
      } );
      var item = ObjectRegistry.getObject( "w4" );
      var childItem = item.getChild( 0 );

      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "destroy"
      } );
      TestUtil.flush();

      assertNull( ObjectRegistry.getObject( "w4" ) );
      assertTrue( childItem.isDisposed() );
      shell.destroy();
    },

    testSetItemCountByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.GridItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "itemCount" : 10
        }
      } );
      var item = ObjectRegistry.getObject( "w4" );
      assertEquals( 10, item._children.length );
      shell.destroy();
      tree.destroy();
    },

    testSetTextsByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.GridItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "texts" : [ "1", "2&<  >\"", "3" ]
        }
      } );
      var item = ObjectRegistry.getObject( "w4" );
      assertEquals( "1", item.getText( 0 ) );
      assertEquals( "2&<  >\"", item.getText( 1 ) );
      assertEquals( "3", item.getText( 2 ) );
      shell.destroy();
      tree.destroy();
    },

    testSetImagesByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.GridItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "images" : [ [ "1.gif", 1, 1 ], [ "2.gif", 2, 2 ], [ "3.gif", 3, 3 ] ]
        }
      } );
      var item = ObjectRegistry.getObject( "w4" );
      assertEquals( [ "1.gif", 1, 1 ], item.getImage( 0 ) );
      assertEquals( [ "2.gif", 2, 2 ], item.getImage( 1 ) );
      assertEquals( [ "3.gif", 3, 3 ], item.getImage( 2 ) );
      shell.destroy();
      tree.destroy();
    },

    testSetBackgroundByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.GridItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "background" : [ 0, 255, 0, 255 ]
        }
      } );
      var item = ObjectRegistry.getObject( "w4" );
      assertEquals( "rgb(0,255,0)", item.getBackground() );
      shell.destroy();
      tree.destroy();
    },

    testSetForegroundByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.GridItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "foreground" : [ 0, 255, 0, 255 ]
        }
      } );
      var item = ObjectRegistry.getObject( "w4" );
      assertEquals( "rgb(0,255,0)", item._foreground );
      shell.destroy();
      tree.destroy();
    },

    testSetFontByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.GridItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "font" : [ ["Arial"], 20, true, false ]
        }
      } );
      var item = ObjectRegistry.getObject( "w4" );
      assertEquals( "bold 20px Arial", item._font );
      shell.destroy();
      tree.destroy();
    },

    testSetCellBackgroundsByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.GridItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "cellBackgrounds" : [ null, [ 0, 255, 0, 255 ], null ]
        }
      } );
      var item = ObjectRegistry.getObject( "w4" );
      assertNull( item.getCellBackground( 0 ) );
      assertEquals( "rgb(0,255,0)", item.getCellBackground( 1 ) );
      assertNull( item.getCellBackground( 2 ) );
      shell.destroy();
      tree.destroy();
    },

    testSetCellForegroundsByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.GridItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "cellForegrounds" : [ null, [ 0, 255, 0, 255 ], null ]
        }
      } );
      var item = ObjectRegistry.getObject( "w4" );
      assertNull( item.getCellForeground( 0 ) );
      assertEquals( "rgb(0,255,0)", item.getCellForeground( 1 ) );
      assertNull( item.getCellForeground( 2 ) );
      shell.destroy();
      tree.destroy();
    },

    testSetCellFontsByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.GridItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "cellFonts" : [ null, [ ["Arial"], 20, true, false ], null ]
        }
      } );
      var item = ObjectRegistry.getObject( "w4" );
      assertNull( item.getCellFont( 0 ) );
      assertEquals( "bold 20px Arial", item.getCellFont( 1 ) );
      assertNull( item.getCellFont( 2 ) );
      shell.destroy();
      tree.destroy();
    },

    testSetExpandedByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.GridItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "expanded" : true
        }
      } );
      var item = ObjectRegistry.getObject( "w4" );
      assertTrue( item.isExpanded() );
      shell.destroy();
      tree.destroy();
    },

    testSetCheckedByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [ "CHECK" ] );
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.GridItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "checked" : true
        }
      } );
      var item = ObjectRegistry.getObject( "w4" );
      assertTrue( item.isChecked() );
      shell.destroy();
      tree.destroy();
    },

    testSetCellCheckedByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [ "CHECK" ] );
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.GridItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "cellChecked" : [ true, true, false ]
        }
      } );
      var item = ObjectRegistry.getObject( "w4" );
      assertTrue( item.isCellChecked( 0 ) );
      assertTrue( item.isCellChecked( 1 ) );
      assertFalse( item.isCellChecked( 2 ) );
      shell.destroy();
      tree.destroy();
    },

    testSetCellGrayedByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [ "CHECK" ] );
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.GridItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "cellGrayed" : [ true, true, false ]
        }
      } );
      var item = ObjectRegistry.getObject( "w4" );
      assertTrue( item.isCellGrayed( 0 ) );
      assertTrue( item.isCellGrayed( 1 ) );
      assertFalse( item.isCellGrayed( 2 ) );
      shell.destroy();
      tree.destroy();
    },

    testSetCellCheckableByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [ "CHECK" ] );
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.GridItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "cellCheckable" : [ true, true, false ]
        }
      } );
      var item = ObjectRegistry.getObject( "w4" );
      assertTrue( item.isCellCheckable( 0 ) );
      assertTrue( item.isCellCheckable( 1 ) );
      assertFalse( item.isCellCheckable( 2 ) );
      shell.destroy();
      tree.destroy();
    },

    testSetGrayedByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [ "CHECK" ] );
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.GridItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "grayed" : true
        }
      } );
      var item = ObjectRegistry.getObject( "w4" );
      assertTrue( item.isGrayed() );
      shell.destroy();
      tree.destroy();
    },

    testSetCustomVariantByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.GridItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "customVariant" : "variant_blue"
        }
      } );
      var item = ObjectRegistry.getObject( "w4" );
      assertEquals( "variant_blue", item.getVariant() );
      shell.destroy();
      tree.destroy();
    },

    testSetHeightByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setItemHeight( 10 );

      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.GridItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "height" : 33
        }
      } );

      var item = ObjectRegistry.getObject( "w4" );
      assertEquals( 33, item.getOwnHeight() );
      shell.destroy();
      tree.destroy();
    },

    testResetSetHeightByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      tree.setItemHeight( 10 );
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.GridItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "height" : 33
        }
      } );

      TestUtil.protocolSet( "w4", { "height" : null } );

      var item = ObjectRegistry.getObject( "w4" );
      assertEquals( 10, item.getOwnHeight() );
      shell.destroy();
      tree.destroy();
    },

    testClearByProtocol : function() {
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      MessageProcessor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.GridItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "texts" : [ "foo" ]
        }
      } );

      TestUtil.protocolCall( "w4", "clear" );

      var item = ObjectRegistry.getObject( "w4" );
      assertFalse( item.isCached() );
      assertEquals( "...", item.getText( 0 ) );
      shell.destroy();
      tree.destroy();
    },

    testCreateItem : function() {
      var item = new rwt.widgets.GridItem();
      assertTrue( item instanceof rwt.widgets.GridItem );
      assertEquals( "", item.getText( 0 ) );
      assertTrue( item.isCached() );
    },

    testCreateAndDisposeByServer: function() {
      var parent = new rwt.widgets.GridItem();
      var item = rwt.widgets.GridItem.createItem( parent, 0 );
      assertTrue( item instanceof rwt.widgets.GridItem );
      assertTrue( item.isCached() );
      item.dispose();
      assertIdentical( undefined, parent._children[ 0 ] );
    },

    testCreatePlaceholderItem : function() {
      var parent = new rwt.widgets.GridItem();
      var item = new rwt.widgets.GridItem( parent, 0, true );
      assertFalse( item.isCached() );
      item.markCached();
      assertTrue( item.isCached() );
    },

    testGetText : function() {
      var item = new rwt.widgets.GridItem();
      item.setTexts( [ "<b>Test</b>", "<i>Test2</i>" ] );
      assertEquals( "<b>Test</b>", item.getText( 0 ) );
      assertEquals( "<i>Test2</i>", item.getText( 1 ) );
    },

    testItemFont : function() {
      var item = new rwt.widgets.GridItem();
      item.setFont( "Arial" );
      assertEquals( "Arial", item.getCellFont( 0 ) );
    },

    testCellFonts : function() {
      var item = new rwt.widgets.GridItem();
      item.setCellFonts( [ "Arial", "Verdana", "monospace" ] );
      assertEquals( "Arial", item.getCellFont( 0 ) );
      assertEquals( "Verdana", item.getCellFont( 1 ) );
      assertEquals( "monospace", item.getCellFont( 2 ) );
    },

    testSomeCellFontsSet : function() {
      var item = new rwt.widgets.GridItem();
      item.setFont( "Arial" );
      item.setCellFonts( [ null, "Verdana" ] );
      assertEquals( "Arial", item.getCellFont( 0 ) );
      assertEquals( "Verdana", item.getCellFont( 1 ) );
      assertEquals( "Arial", item.getCellFont( 2 ) );
    },

    testForeground : function() {
      var item = new rwt.widgets.GridItem();
      item.setForeground( "red" );
      assertEquals( "red", item.getCellForeground( 0 ) );
    },

    testCellForegrounds : function() {
      var item = new rwt.widgets.GridItem();
      item.setCellForegrounds( [ "red", "green" ] );
      assertEquals( "red", item.getCellForeground( 0 ) );
      assertEquals( "green", item.getCellForeground( 1 ) );
    },

    testSomeCellForegroundsSets : function() {
      var item = new rwt.widgets.GridItem();
      item.setForeground( "red" );
      item.setCellForegrounds( [ null, "green" ] );
      assertEquals( "red", item.getCellForeground( 0 ) );
      assertEquals( "green", item.getCellForeground( 1 ) );
      assertEquals( "red", item.getCellForeground( 2 ) );
    },

    testBackground : function() {
      var item = new rwt.widgets.GridItem();
      item.setBackground( "red" );
      assertEquals( "red", item.getBackground() );
    },

    testCellBackgrounds: function() {
      var item = new rwt.widgets.GridItem();
      item.setCellBackgrounds( [ "red", "green" ] );
      assertEquals( "red", item.getCellBackground( 0 ) );
      assertEquals( "green", item.getCellBackground( 1 ) );
    },

    testSomeCellBackgroundsSet : function() {
      var item = new rwt.widgets.GridItem();
      item.setCellBackgrounds( [ null, "green" ] );
      item.setBackground( "red" );
      assertEquals( null, item.getCellBackground( 0 ) );
      assertEquals( "green", item.getCellBackground( 1 ) );
    },

    testImages : function() {
      var item = new rwt.widgets.GridItem();
      item.setImages( [ [ "test1.jpg", 10, 10 ], [ "test2.jpg", 10, 10 ] ] );
      assertEquals( [ "test1.jpg", 10, 10 ], item.getImage( 0 ) );
      assertEquals( [ "test2.jpg", 10, 10 ], item.getImage( 1 ) );
      assertEquals( null, item.getImage( 2 ) );
    },

    testClear : function() {
      var item = new rwt.widgets.GridItem();
      item.setImages( [ [ "test1.jpg", 10, 10 ], [ "test2.jpg", 10, 10 ] ] );
      item.setTexts( [ "bla", "blubg" ] );
      item.setCellFonts( "arial", "windings" ) ;
      item.setCellForegrounds( "red", "blue" );
      item.setCellBackgrounds( "red", "blue" );
      item.setFont( "arial" );
      item.setForeground( "green" );
      item.setBackground( "yellow" );
      item.setVariant( "foo" );
      item.clear();
      assertEquals( null, item.getCellForeground( 0 ) );
      assertEquals( null, item.getCellBackground( 0 ) );
      assertEquals( null, item.getCellFont( 0 ) );
      assertEquals( null, item.getBackground() );
      assertEquals( "...", item.getText( 0 ) );
      assertEquals( "", item.getText( 1 ) );
      assertEquals( null, item.getImage( 0 ) );
      assertEquals( null, item.getImage( 1 ) );
      assertEquals( null, item.getVariant() );
    },

    testParent : function() {
      var item = new rwt.widgets.GridItem();
      var child = new rwt.widgets.GridItem( item, 0 );
      assertIdentical( item, child.getParent() );
    },

    testChildren : function() {
      var item = new rwt.widgets.GridItem();
      item.setItemCount( 2 );
      var child1 = new rwt.widgets.GridItem( item, 0 );
      var child2 = new rwt.widgets.GridItem( item, 1 );
      assertEquals( [ child1, child2 ], item._children );
    },

    testHasChildren : function() {
      var item = new rwt.widgets.GridItem();
      assertFalse( item.hasChildren() );
      item.setItemCount( 1 );
      var child = new rwt.widgets.GridItem( item, 0 );
      assertTrue( item.hasChildren() );
    },

    testSetExpanded : function() {
      var root = new rwt.widgets.GridItem();
      root.setItemCount( 1 );
      var item = new rwt.widgets.GridItem( root );
      assertFalse( item.isExpanded() );
      item.setExpanded( true );
      assertTrue( item.isExpanded() );
    },

    testHasPreviousSibling : function() {
      var item = new rwt.widgets.GridItem();
      item.setItemCount( 2 );
      var child1 = new rwt.widgets.GridItem( item, 0 );
      var child2 = new rwt.widgets.GridItem( item, 1 );
      assertFalse( child1.hasPreviousSibling() );
      assertTrue( child2.hasPreviousSibling() );
    },

    testHasNextSibling : function() {
      var item = new rwt.widgets.GridItem();
      item.setItemCount( 2 );
      var child1 = new rwt.widgets.GridItem( item, 0 );
      var child2 = new rwt.widgets.GridItem( item, 1 );
      assertTrue( child1.hasNextSibling() );
      assertFalse( child2.hasNextSibling() );
    },

    testGetLevel : function() {
      var root = new rwt.widgets.GridItem();
      root.setItemCount( 1 );
      var child1 = new rwt.widgets.GridItem( root, 0 );
      child1.setItemCount( 1 );
      var child2 = new rwt.widgets.GridItem( child1, 0 );
      assertEquals( -1, root.getLevel() );
      assertEquals( 0, child1.getLevel() );
      assertEquals( 1, child2.getLevel() );
    },

    testIsRootItem : function() {
      var root = new rwt.widgets.GridItem();
      root.setItemCount( 1 );
      var child1 = new rwt.widgets.GridItem( root );
      child1.setItemCount( 1 );
      var child2 = new rwt.widgets.GridItem( child1, 0 );
      assertTrue( root.isRootItem() );
      assertFalse( child1.isRootItem() );
      assertFalse( child2.isRootItem() );
    },

    testItemAddedEvent : function() {
      var log = [];
      var root = new rwt.widgets.GridItem();
      root.addEventListener( "update", function( event ) {
        log.push( event.msg );
      }, this);
      root.setItemCount( 1 );
      new rwt.widgets.GridItem( root, 0 );
      assertEquals( 1, log.length );
      assertEquals( "add", log[ 0 ] );
    },

    testItemAddedEventBubbles : function() {
      var log = [];
      var root = new rwt.widgets.GridItem();
      root.addEventListener( "update", function( event ) {
        log.push( event );
      }, this);
      root.setItemCount( 1 );
      var child1 = new rwt.widgets.GridItem( root, 0 );
      child1.setItemCount( 1 );
      var child2 = new rwt.widgets.GridItem( child1, 0 );
      assertEquals( 2, log.length );
    },

    testItemChangedEvent : function() {
      var log = [];
      var item = new rwt.widgets.GridItem();
      item.addEventListener( "update", function( event ) {
        log.push( event );
      }, this);
      item.setTexts( [ "Test" ] );
      item.setFont( "Arial" );
      item.setCellFonts( [ "Arial" ] );
      item.setForeground( "red" );
      item.setCellForegrounds( [ "red" ] );
      item.setBackground( "red" );
      item.setCellBackgrounds( [ "red" ] );
      item.setImages( [ [ "bla.jpg", 10, 10 ] ] );
      assertEquals( 8, log.length );
    },

    testRemoveItem : function() {
      var root = new rwt.widgets.GridItem();
      root.setItemCount( 3 );
      var child1 = new rwt.widgets.GridItem( root, 0 );
      var child2 = new rwt.widgets.GridItem( root, 1 );
      var child3 = new rwt.widgets.GridItem( root, 2 );
      assertEquals( [ child1, child2, child3 ], root._children );
      child2.dispose();
      assertEquals( [ child1, child3, undefined ], root._children );
    },

    testRemoveItemUncachedAndUpdateIndex : function() {
      var root = new rwt.widgets.GridItem();
      root.setItemCount( 3 );
      var child3 = new rwt.widgets.GridItem( root, 2 );

      root.setItemCount( 2 );
      child3.setIndex( 1 );

      assertEquals( [ undefined, child3 ], root._children );
    },

    testRemoveMultipleItemsUncachedAndUpdateIndex : function() {
      var root = new rwt.widgets.GridItem();
      root.setItemCount( 5 );
      var child3 = new rwt.widgets.GridItem( root, 2 );
      var child4 = new rwt.widgets.GridItem( root, 3 );
      var child5 = new rwt.widgets.GridItem( root, 4 );

      root.setItemCount( 3 );
      child3.setIndex( 0 );
      child4.setIndex( 1 );
      child5.setIndex( 2 );

      assertEquals( [ child3, child4, child5 ], root._children );
    },

    testRemoveItemUncachedDisposesPlaceholder : function() {
      var root = new rwt.widgets.GridItem();
      root.setItemCount( 3 );
      var child2 = new rwt.widgets.GridItem( root, 1, true );
      var child3 = new rwt.widgets.GridItem( root, 2 );

      root.setItemCount( 2 );
      child3.setIndex( 1 );
      TestUtil.flush();

      assertTrue( child2.isDisposed() );
      assertEquals( [ undefined, child3 ], root._children );
    },

    testRemoveMultipleItemsUncachedDisposesPlaceHolder : function() {
      var root = new rwt.widgets.GridItem();
      root.setItemCount( 5 );
      var child1 = new rwt.widgets.GridItem( root, 0, true );
      var child2 = new rwt.widgets.GridItem( root, 1, true );
      var child3 = new rwt.widgets.GridItem( root, 2 );
      var child4 = new rwt.widgets.GridItem( root, 3 );
      var child5 = new rwt.widgets.GridItem( root, 4 );

      root.setItemCount( 3 );
      child3.setIndex( 0 );
      child4.setIndex( 1 );
      child5.setIndex( 2 );

      assertEquals( [ child3, child4, child5 ], root._children );
      assertTrue( child1.isDisposed() );
      assertTrue( child2.isDisposed() );
    },

    testRemoveItemEvent : function() {
      var log = [];
      var root = new rwt.widgets.GridItem();
      root.setItemCount( 1 );
      var child = new rwt.widgets.GridItem( root, 0 );
      root.addEventListener( "update", function( event ) {
        log.push( event.msg );
      }, this);
      root.setItemCount( 0 );
      child.dispose();
      // Two events, one from the dispose, one from the setItemCount. Both are needed,
      // since there are cases where one of them can be missing (virtual or replacing items)
      assertEquals( [ "remove", "remove" ], log );
    },

    testGetChild : function() {
      var root = new rwt.widgets.GridItem();
      root.setItemCount( 1 );
      var child1 = new rwt.widgets.GridItem( root, 0 );
      assertEquals( child1, root.getChild( 0 ) );
      assertIdentical( undefined, root.getChild( 1 ) );
    },

    testGetPreviousSibling : function() {
      var item = new rwt.widgets.GridItem();
      item.setItemCount( 2 );
      var child1 = new rwt.widgets.GridItem( item, 0 );
      var child2 = new rwt.widgets.GridItem( item, 1 );
      assertEquals( child1, child2.getPreviousSibling() );
    },

    testGetNextSibling : function() {
      var item = new rwt.widgets.GridItem();
      item.setItemCount( 2 );
      var child1 = new rwt.widgets.GridItem( item, 0 );
      var child2 = new rwt.widgets.GridItem( item, 1 );
      assertEquals( child2, child1.getNextSibling() );
    },

    testItemExpandedEvent : function() {
      var log = [];
      var root = new rwt.widgets.GridItem();
      root.setItemCount( 1 );
      var child1 = new rwt.widgets.GridItem( root, 0 );
      root.addEventListener( "update", function( event ) {
        log.push( event.msg );
      }, this);
      child1.setExpanded( true );
      child1.setExpanded( false );
      assertEquals( 2, log.length );
      assertEquals( "expanded", log[ 0 ] );
      assertEquals( "collapsed", log[ 1 ] );
    },

    testVisibleChildrenCountOnlyOneLayer : function() {
      var root = new rwt.widgets.GridItem();
      root.setItemCount( 10 );
      for( var i = 0; i < 10; i++ ) {
        new rwt.widgets.GridItem( root, i );
      }
      assertEquals( 10, root.getVisibleChildrenCount() );
    },

    testVisibleChildrenCount : function() {
      var root = new rwt.widgets.GridItem();
      root.setItemCount( 2 );
      var item1 = new rwt.widgets.GridItem( root, 0 );
      var item2 = new rwt.widgets.GridItem( root, 1 );
      item1.setItemCount( 1 );
      item2.setItemCount( 1 );
      new rwt.widgets.GridItem( item1, 0 );
      new rwt.widgets.GridItem( item2, 0 );
      item1.setExpanded( true );
      assertEquals( 3, root.getVisibleChildrenCount() );
    },

    testVisibleChildrenCountResultIsZeroNoChildren : function() {
      var root = new rwt.widgets.GridItem();
      assertEquals( 0, root.getVisibleChildrenCount() );
    },

    testVisibleChildrenCountResultIsZeroWithCollapsedChildren : function() {
      var root = new rwt.widgets.GridItem();
      root.setItemCount( 1 );
      var item = new rwt.widgets.GridItem( root, 0 );
      item.setItemCount( 1 );
      new rwt.widgets.GridItem( item, 0 );
      assertEquals( 0, item.getVisibleChildrenCount() );
    },

    testSetChecked : function() {
      var item = new rwt.widgets.GridItem();
      assertFalse( item.isChecked() );
      item.setChecked( true );
      assertTrue( item.isChecked() );
      item.setChecked( false );
      assertFalse( item.isChecked() );
    },

    testItemCheckedEvent : function() {
      var log = [];
      var root = new rwt.widgets.GridItem();
      root.setItemCount( 1 );
      var child1 = new rwt.widgets.GridItem( root, 0 );
      root.addEventListener( "update", function( event ) {
        log.push( event );
      }, this);
      child1.setChecked( true );
      child1.setChecked( false );
      assertEquals( 2, log.length );
    },

    testItemHeightChangeEvent : function() {
      var log = [];
      var root = new rwt.widgets.GridItem();
      root.setItemCount( 1 );
      var child1 = new rwt.widgets.GridItem( root, 0 );
      root.addEventListener( "update", function( event ) {
        log.push( rwt.util.Objects.copy( event ) );
      }, this);

      child1.setHeight( 23 );

      assertEquals( 1, log.length );
      assertEquals( "height", log[ 0 ].msg );
      assertTrue( !log[ 0 ].rendering );
    },

    testItemHeightChangeEventWithRendering : function() {
      var log = [];
      var root = new rwt.widgets.GridItem();
      root.setItemCount( 1 );
      var child1 = new rwt.widgets.GridItem( root, 0 );
      root.addEventListener( "update", function( event ) {
        log.push( rwt.util.Objects.copy( event ) );
      }, this);

      child1.setHeight( 23, true );

      assertEquals( 1, log.length );
      assertTrue( log[ 0 ].rendering );
    },

    testNoItemHeightChangeEventWithUnchangedHeight : function() {
      var log = [];
      var root = new rwt.widgets.GridItem();
      root.setItemCount( 1 );
      var child1 = new rwt.widgets.GridItem( root, 0 );
      child1.setHeight( null );
      root.addEventListener( "update", function( event ) {
        log.push( rwt.util.Objects.copy( event ) );
      }, this);

      child1.setHeight( null );

      assertEquals( 0, log.length );
    },

    testAddItemAt : function() {
      var root = new rwt.widgets.GridItem();
      root.setItemCount( 3 );
      var child1 = rwt.widgets.GridItem.createItem( root, 0 );
      var child2 = rwt.widgets.GridItem.createItem( root, 1 );
      var child3 = rwt.widgets.GridItem.createItem( root, 1 );
      assertEquals( [ child1, child3, child2 ], root._children );
      root.dispose();
    },

    testSetGrayed : function() {
      var item = new rwt.widgets.GridItem();
      item.setGrayed( true );
      assertTrue( true, item.isGrayed() );
    },

    testIsDisplayable : function() {
      var root = new rwt.widgets.GridItem();
      root.setItemCount( 1 );
      var child1 = new rwt.widgets.GridItem( root, 0 );
      child1.setItemCount( 1 );
      var child2 = new rwt.widgets.GridItem( child1, 0 );
      child2.setItemCount( 1 );
      var child3 = new rwt.widgets.GridItem( child2, 0 );
      child1.setExpanded( true );
      child2.setExpanded( false );
      assertFalse( child3.isDisplayable() );
      child1.setExpanded( false );
      child2.setExpanded( true );
      assertFalse( child3.isDisplayable() );
      child1.setExpanded( true );
      child2.setExpanded( true );
      assertTrue( child3.isDisplayable() );
    },

    testSetItemCount : function() {
      var item = new rwt.widgets.GridItem();
      var log = [];
      item.addEventListener( "update", function( event ) {
        log.push( event.msg, event.target );
      }, this);
      assertEquals( 0, item._children.length );
      item.setItemCount( 4 );
      assertEquals( 4, item._children.length );
      assertEquals( [ undefined, undefined, undefined, undefined ], item._children );
      item.setItemCount( 2 );
      assertEquals( [ "add", item, "remove", item ], log );
    },

    testSetItemCountStaysOnInsert : function() {
      var item = new rwt.widgets.GridItem();
      var log = [];
      item.setItemCount( 4 );
      assertEquals( 4, item._children.length );
      assertEquals( [ undefined, undefined, undefined, undefined ], item._children );
      var child1 = new rwt.widgets.GridItem( item, 0 );
      var child2 = new rwt.widgets.GridItem( item, 1 );
      assertEquals( 4, item._children.length );
      assertEquals( [ child1, child2, undefined, undefined ], item._children );
      var child3 = new rwt.widgets.GridItem( item, 1 );
      // setItemCount is not rendered by server since it stays the same
      assertEquals( 4, item._children.length );
      assertEquals( [ child1, child3, child2, undefined ], item._children );
      item.dispose();
    },

    testReplaceUndefinedItem : function() {
      var log = [];
      var root = new rwt.widgets.GridItem();
      root.addEventListener( "update", function( event ) {
        log.push( event.msg, event.target );
      }, this);
      root.setItemCount( 4 );
      var child1 = new rwt.widgets.GridItem( root, 0 );
      var child2 = new rwt.widgets.GridItem( root, 3 );
      var child3 = new rwt.widgets.GridItem( root, 2 );
      assertEquals( [ child1, undefined, child3, child2 ], root._children );
      // NOTE: Only one "add" for setItemCount. The other items have never been rendered
      //       (otherwise woulnt be undefined) and don't shift anything either.
      assertEquals( [ "add", root], log );
    },

    testGetVirtualItem: function() {
      var item = new rwt.widgets.GridItem();
      item.setItemCount( 4 );
      var child = item.getChild( 0 );
      assertFalse( child.isCached() );
      assertEquals( 0, item._children.indexOf( child ) );
      assertEquals( "...", child.getText( 0 ) );
      var sibling = child.getNextSibling();
      assertFalse( sibling.isCached() );
      assertEquals( 1, item._children.indexOf( sibling ) );
      item.dispose();
    },

    testHasVirtualSibling: function() {
      var item = new rwt.widgets.GridItem();
      item.setItemCount( 4 );
      var child = item.getChild( 0 );
      assertEquals( 0, item._children.indexOf( child ) );
      assertTrue( child.hasNextSibling() );
      assertIdentical( undefined, item._children[ 1 ] );
      item.dispose();
    },

    testVirtualChildrenCount: function() {
      var item = new rwt.widgets.GridItem();
      item.setItemCount( 4 );
      assertEquals( 4, item.getVisibleChildrenCount() );
      assertEquals( [ undefined, undefined, undefined, undefined ], item._children );
      item.dispose();
    },

    testVirtualGetLastChild: function() {
      var item = new rwt.widgets.GridItem();
      item.setItemCount( 4 );
      var child = item.getLastChild();
      assertEquals( 3, item._children.indexOf( child ) );
      item.dispose();
    },

    testCacheItem : function() {
      var parent = new rwt.widgets.GridItem();
      parent.setItemCount( 4 );
      var item = parent.getChild( 2 );
      assertFalse( item.isCached() );
      var newItem = rwt.widgets.GridItem.createItem( parent, 2 );
      assertIdentical( item, newItem );
      assertTrue( item.isCached() );
      assertEquals( "", item.getText( 0 ) );
      item.dispose();
    },

    testSetDefaultHeight : function() {
      var root = this._createRoot();

      root.setDefaultHeight( 23 );

      assertEquals( 23, root.getChild( 0 ).getDefaultHeight() );
      root.dispose();
    },

    testSetDefaultHeightOnNonRootItem : function() {
      var root = this._createRoot();

      try {
        root.getChild( 0 ).setDefaultHeight( 23 );
        fail();
      } catch( ex ) {
        // expected
      }

      root.dispose();
    },

    testFindItemByOffsetFirstItem : function() {
      var root = this._createRoot();

      var item = root.findItemByOffset( 0 );

      assertIdentical( item, root.getChild( 0 ) );
      root.dispose();
    },

    testFindItemByOffsetLastItem: function() {
      var root = this._createRoot();

      var item = root.findItemByOffset( 14340 );

      assertIdentical( item, root.getChild( 19 ) );
      root.dispose();
    },

    testFindItemByOffsetOutOfBounds: function() {
      var root = this._createRoot();

      assertIdentical( undefined, root.findItemByOffset( 14350 ) );

      root.dispose();
    },

    testFindItemByOffset : function() {
      var root = this._createRoot();

      var item = root.findItemByOffset( 10200 );

      assertEquals( "x", item.getText( 0 ) );
      assertFalse( root.isChildCreated( 7 ) );
      root.dispose();
    },

    testFindItemByOffsetFirstItemExpanded : function() {
      var root = this._createRoot();
      root.getChild( 0 ).setItemCount( 1 );
      root.getChild( 0 ).setExpanded( true );

      var item1 = root.findItemByOffset( 0 );
      var item2 = root.findItemByOffset( 10 );
      var item3 = root.findItemByOffset( 20 );

      assertIdentical( root.getChild( 0 ), item1 );
      assertIdentical( root.getChild( 0 ).getChild( 0 ), item2 );
      assertIdentical( root.getChild( 1 ), item3 );
      root.dispose();
    },

    testFindItemByOffsetRoundFloor : function() {
      var root = this._createRoot();

      var item = root.findItemByOffset( 10209 );

      assertEquals( "x", item.getText( 0 ) );
      assertFalse( root.isChildCreated( 7 ) );
      root.dispose();
    },

    testFindItemByOffsetChangedDefaultHeight : function() {
      var root = this._createRoot();
      var itemOrg = root.findItemByOffset( 10200 );

      root.setDefaultHeight( 20 );

      var itemNew = root.findItemByOffset( 20400 );
      assertIdentical( itemOrg, itemNew );
      root.dispose();
    },

    testFindItemByOffsetNegative : function() {
      var root = new rwt.widgets.GridItem();

      assertNull( root.findItemByOffset( -1 ) );
      root.dispose();
    },

    testFindItemByOffsetNoItems : function() {
      var root = new rwt.widgets.GridItem();

      assertNull( root.findItemByOffset( 0 ) );
      root.dispose();
    },

    testFindItemByOffsetFirstItemOfLayer : function() {
      var root = this._createRoot();

      var item = root.findItemByOffset( 10310 );

      assertEquals( "y", item.getText( 0 ) );
      root.dispose();
    },

    testFindItemByOffsetLastItemOfLayer : function() {
      var root = this._createRoot();

      var item = root.findItemByOffset( 10060 );

      assertEquals( "z", item.getText( 0 ) );
      root.dispose();
    },

    testFindItemByOffsetCollapse : function() {
      var root = this._createRoot();
      assertEquals( "x", root.findItemByOffset( 10200 ).getText( 0 ) );

      root.getChild( 6 ).setExpanded( false );

      assertEquals( "x", root.findItemByOffset( 200 ).getText( 0 ) );
      root.dispose();
    },

    testFindItemByOffsetRemoveChild : function() {
      var root = this._createRoot();
      assertEquals( "x", root.findItemByOffset( 10200 ).getText( 0 ) );

      root.getChild( 6 ).dispose();

      assertEquals( "x", root.findItemByOffset( 190 ).getText( 0 ) );
      root.dispose();
    },

    testFindItemByOffsetCollapseAdd : function() {
      var root = this._createRoot();
      assertEquals( "x", root.findItemByOffset( 10200 ).getText( 0 ) );

      root.getChild( 6 ).setItemCount( 1010 );

      assertEquals( "x", root.findItemByOffset( 10300 ).getText( 0 ) );
      root.dispose();
    },

    testFindItemByOffsetCollapseRemove : function() {
      var root = this._createRoot();
      assertEquals( "x", root.findItemByOffset( 10200 ).getText( 0 ) );

      root.getChild( 6 ).setItemCount( 900 );

      assertEquals( "x", root.findItemByOffset( 9200 ).getText( 0 ) );
      root.dispose();
    },

    testGetFlatIndex : function() {
      var root = this._createRoot();
      assertEquals( 0, root.findItemByOffset( 0 ).getFlatIndex() );
      assertEquals( 6, root.findItemByOffset( 60 ).getFlatIndex() );
      assertEquals( 7, root.findItemByOffset( 70 ).getFlatIndex() );
      assertEquals( 1020, root.findItemByOffset( 10200 ).getFlatIndex() );
      assertEquals( 1434, root.findItemByOffset( 14340 ).getFlatIndex() );
      assertFalse( root.isChildCreated( 7 ) );
      root.dispose();
    },

    testGetOffset : function() {
      var root = this._createRoot();
      assertEquals( 0, root.findItemByOffset( 0 ).getOffset() );
      assertEquals( 60, root.findItemByOffset( 60 ).getOffset() );
      assertEquals( 70, root.findItemByOffset( 70 ).getOffset() );
      assertEquals( 10200, root.findItemByOffset( 10200 ).getOffset() );
      assertEquals( 14340, root.findItemByOffset( 14340 ).getOffset() );
      assertFalse( root.isChildCreated( 7 ) );
      root.dispose();
    },

    testGetOffsetWithCustomHeight : function() {
      var root = this._createRoot();

      root.getChild( 0 ).setHeight( 50 );

      assertEquals( 50, root.getChild( 1 ).getOffset() );
      root.dispose();
    },

    testGetOffsetWithChangeDefaultItemHeight : function() {
      var root = this._createRoot();
      assertEquals( 14340, root.findItemByOffset( 14340 ).getOffset() );

      root.setDefaultHeight( 20 );

      assertEquals( 14340, root.findItemByOffset( 14340 ).getOffset() );
      root.dispose();
    },

    testGetOffsetWithCustomHeightItemRemoved : function() {
      var root = this._createRoot();

      root.getChild( 0 ).setHeight( 50 );
      root.getChild( 0 ).dispose();

      assertEquals( 10, root.getChild( 1 ).getOffset() );
      assertEquals( 50, root.getChild( 5 ).getOffset() );
      root.dispose();
    },

    testGetOffsetWithCustomHeightItemReset : function() {
      var root = this._createRoot();

      root.getChild( 0 ).setHeight( 50 );
      root.getChild( 0 ).setHeight( null );

      assertEquals( 10, root.getChild( 1 ).getOffset() );
      assertEquals( 50, root.getChild( 5 ).getOffset() );
      root.dispose();
    },

    testGetOffsetWithCustomHeightInPreviousSibling : function() {
      var root = this._createRoot();
      root.getChild( 0 ).setItemCount( 1 );
      root.getChild( 0 ).setExpanded( true );
      var subItem = root.getChild( 0 ).getChild( 0 );

      subItem.setHeight( 50 );

      assertEquals( 60, root.getChild( 1 ).getOffset() );
      root.dispose();
    },

    testGetOffsetWithParentCustomHeight : function() {
      var root = this._createRoot();
      root.getChild( 0 ).setItemCount( 1 );
      root.getChild( 0 ).setExpanded( true );

      root.getChild( 0 ).setHeight( 15 );

      assertEquals( 15, root.getChild( 0 ).getChild( 0 ).getOffset() );
      root.dispose();
    },

    testGetOffsetWithMultipleCustomHeightInPreviousSiblings : function() {
      var root = this._createRoot();
      root.getChild( 1 ).setItemCount( 3 );
      root.getChild( 1 ).setExpanded( true );
      var subItem1 = root.getChild( 1 ).getChild( 0 );
      var subItem2 = root.getChild( 1 ).getChild( 1 );

      root.getChild( 0 ).setHeight( 15 );
      root.getChild( 1 ).setHeight( 25 );
      subItem1.setHeight( 20 );
      subItem2.setHeight( 40 );

      assertEquals( 0, root.getChild( 0 ).getOffset() );
      assertEquals( 15, root.getChild( 1 ).getOffset() );
      assertEquals( 40, root.getChild( 1 ).getChild( 0 ).getOffset() );
      assertEquals( 60, root.getChild( 1 ).getChild( 1 ).getOffset() );
      assertEquals( 100, root.getChild( 1 ).getChild( 2 ).getOffset() );
      assertEquals( 110, root.getChild( 2 ).getOffset() );
      root.dispose();
    },

    testFindItemByOffsetWithCustomHeight : function() {
      var root = this._createRoot();

      root.getChild( 0 ).setHeight( 50 );

      assertEquals( root.getChild( 0 ), root.findItemByOffset( 0 ) );
      assertEquals( root.getChild( 0 ), root.findItemByOffset( 49 ) );
      assertEquals( root.getChild( 1 ), root.findItemByOffset( 50 ) );
      root.dispose();
    },

    testFindItemByOffsetWithCustomHeightInPreviousSibling : function() {
      var root = this._createRoot();
      root.getChild( 0 ).setItemCount( 1 );
      root.getChild( 0 ).setExpanded( true );
      var subItem = root.getChild( 0 ).getChild( 0 );

      subItem.setHeight( 50 );

      assertEquals( root.getChild( 0 ).getChild( 0 ), root.findItemByOffset( 59 ) );
      assertEquals( root.getChild( 1 ), root.findItemByOffset( 60 ) );
      assertEquals( root.getChild( 1 ), root.findItemByOffset( 69 ) );
      assertEquals( root.getChild( 2 ), root.findItemByOffset( 70 ) );
      root.dispose();
    },

    testFindItemByOffsetWithParentCustomHeight : function() {
      var root = this._createRoot();
      root.getChild( 0 ).setItemCount( 1 );
      root.getChild( 0 ).setExpanded( true );

      root.getChild( 0 ).setHeight( 15 );

      assertEquals( root.getChild( 0 ), root.findItemByOffset( 14 ) );
      assertEquals( root.getChild( 0 ).getChild( 0 ), root.findItemByOffset( 15 ) );
      assertEquals( root.getChild( 0 ).getChild( 0 ), root.findItemByOffset( 24 ) );
      assertEquals( root.getChild( 1 ), root.findItemByOffset( 25 ) );
      root.dispose();
    },

    testFindItemByOffsetWithMultipleCustomHeightInPreviousSiblings : function() {
      var root = this._createRoot();
      root.getChild( 1 ).setItemCount( 3 );
      root.getChild( 1 ).setExpanded( true );
      var subItem1 = root.getChild( 1 ).getChild( 0 );
      var subItem2 = root.getChild( 1 ).getChild( 1 );

      root.getChild( 0 ).setHeight( 15 );
      root.getChild( 1 ).setHeight( 25 );
      subItem1.setHeight( 20 );
      subItem2.setHeight( 40 );

      assertEquals( root.getChild( 0 ), root.findItemByOffset( 14 ) );
      assertEquals( root.getChild( 1 ), root.findItemByOffset( 15 ) );
      assertEquals( root.getChild( 1 ), root.findItemByOffset( 39 ) );
      assertEquals( root.getChild( 1 ).getChild( 0 ), root.findItemByOffset( 40 ) );
      assertEquals( root.getChild( 1 ).getChild( 0 ), root.findItemByOffset( 59 ) );
      assertEquals( root.getChild( 1 ).getChild( 1 ), root.findItemByOffset( 60 ) );
      assertEquals( root.getChild( 1 ).getChild( 1 ), root.findItemByOffset( 99 ) );
      assertEquals( root.getChild( 1 ).getChild( 2 ), root.findItemByOffset( 100 ) );
      assertEquals( root.getChild( 1 ).getChild( 2 ), root.findItemByOffset( 109 ) );
      assertEquals( root.getChild( 2 ), root.findItemByOffset( 110 ) );
      assertEquals( root.getChild( 2 ), root.findItemByOffset( 119 ) );
      root.dispose();
    },

    testIsChildOf : function() {
      var root = this._createRoot();
      assertTrue( root.getChild( 0 ).isChildOf( root ) );
      assertTrue( root.getChild( 5 ).getChild( 5 ).isChildOf( root ) );
      var item10 = root.getChild( 10 );
      assertTrue( item10.getChild( 5 ).getChild( 3 ).isChildOf( item10 ) );
      assertTrue( item10.getChild( 5 ).getChild( 3 ).isChildOf( root ) );
      assertFalse( item10.isChildOf( root.getChild( 5 ) ) );
      assertFalse( item10.isChildOf( item10 ) );
      root.dispose();
    },

    testGetHtmlAttributes_returnEmptyObjectWhenNoAttributesSet : function() {
      var item = new rwt.widgets.GridItem();

      assertEquals( {}, item.getHtmlAttributes() );
      item.dispose();
    },

    testSetHtmlAttribute : function() {
      var item = new rwt.widgets.GridItem();

      item.setHtmlAttribute( "foo", "bar" );
      item.setHtmlAttribute( "foo2", "bar2" );

      var expected = { "foo" : "bar", "foo2" : "bar2" };
      assertEquals( expected, item.getHtmlAttributes() );
      item.dispose();
    },

    testSetHtmlAttribute_removesAttribute : function() {
      var item = new rwt.widgets.GridItem();

      item.setHtmlAttribute( "foo", "bar" );
      item.setHtmlAttribute( "foo", null );

      assertEquals( {}, item.getHtmlAttributes() );
      item.dispose();
    },

    //////////////////////
    // create complex tree

    _createTreeByProtocol : function( id, parentId, styles ) {
      MessageProcessor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.Grid",
        "properties" : {
          "style" : styles,
          "parent" : parentId,
          "appearance" : "tree",
          "selectionPadding" : [ 2, 4 ],
          "indentionWidth" : 16,
          "checkBoxMetrics" : [ 5, 16 ],
          "bounds" : [ 0, 0, 100, 100 ],
          "columnCount" : 3
        }
      } );
      return rwt.remote.ObjectRegistry.getObject( id );
    },

    // 20 + 1000 + 10 + 5 + 400 = 1435 items
    _createRoot : function() {
      var result = new rwt.widgets.GridItem();
      result.setItemCount( 20 );
      result.setDefaultHeight( 10 );
      result.getChild( 5 ).setItemCount( 1000 );
      result.getChild( 6 ).setItemCount( 1000 );
      result.getChild( 6 ).setExpanded( true );
      result.getChild( 6 ).getChild( 999 ).setTexts( [ "z" ] );
      result.getChild( 10 ).setExpanded( true );
      result.getChild( 10 ).setItemCount( 10 );
      result.getChild( 10 ).getChild( 5 ).setExpanded( true );
      result.getChild( 10 ).getChild( 5 ).setItemCount( 5 );
      result.getChild( 10 ).getChild( 5 ).getChild( 3 ).setTexts( [ "x" ] );
      result.getChild( 15 ).setExpanded( true );
      result.getChild( 15 ).setItemCount( 400 );
      result.getChild( 15 ).getChild( 0 ).setTexts( [ "y" ] );
      return result;
    }

 }

} );

}() );
