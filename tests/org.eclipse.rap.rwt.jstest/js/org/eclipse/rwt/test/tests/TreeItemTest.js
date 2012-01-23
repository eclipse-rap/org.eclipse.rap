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

qx.Class.define( "org.eclipse.rwt.test.tests.TreeItemTest", {

  extend : qx.core.Object,
  
  members : {

    testCreateTreeItemByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.TreeItem",
        "properties" : {
          "parent" : "w3",
          "index": 3
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var item = ObjectManager.getObject( "w4" );
      assertTrue( item instanceof org.eclipse.rwt.widgets.TreeItem );
      assertIdentical( tree.getRootItem(), item.getParent() );
      assertNull( item.getUserData( "isControl") );
      assertEquals( 3, tree.getRootItem().indexOf( item ) )
      shell.destroy();
      tree.destroy();
    },

    testDestroyTreeItemByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.TreeItem",
        "properties" : {
          "parent" : "w3",
          "index": 3
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var item = ObjectManager.getObject( "w4" );
      org.eclipse.rwt.protocol.Processor.processOperation( {
        "target" : "w4",
        "action" : "destroy"
      } );
      TestUtil.flush();
      assertIdentical( undefined, ObjectManager.getObject( "w4" ) );
      assertEquals( -1, tree.getRootItem().indexOf( item ) );
      shell.destroy();
      tree.destroy();
    },

    testSetItemCountByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.TreeItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "itemCount" : 10
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var item = ObjectManager.getObject( "w4" );
      assertEquals( 10, item._children.length );
      shell.destroy();
      tree.destroy();
    },

    testSetTextsByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.TreeItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "texts" : [ "1", "2&<  >\"", "3" ]
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var item = ObjectManager.getObject( "w4" );
      assertEquals( "1", item.getText( 0 ) );
      assertEquals( "2&amp;&lt;&nbsp; &gt;&quot;", item.getText( 1 ) );
      assertEquals( "3", item.getText( 2 ) );
      shell.destroy();
      tree.destroy();
    },

    testSetImagesByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.TreeItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "images" : [ [ "1.gif", 1, 1 ], [ "2.gif", 2, 2 ], [ "3.gif", 3, 3 ] ]
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var item = ObjectManager.getObject( "w4" );
      assertEquals( "1.gif", item.getImage( 0 ) );
      assertEquals( "2.gif", item.getImage( 1 ) );
      assertEquals( "3.gif", item.getImage( 2 ) );
      shell.destroy();
      tree.destroy();
    },

    testSetBackgroundByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.TreeItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "background" : [ 0, 255, 0, 255 ]
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var item = ObjectManager.getObject( "w4" );
      assertEquals( "rgb(0,255,0)", item.getBackground() );
      shell.destroy();
      tree.destroy();
    },

    testSetForegroundByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.TreeItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "foreground" : [ 0, 255, 0, 255 ]
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var item = ObjectManager.getObject( "w4" );
      assertEquals( "rgb(0,255,0)", item._foreground );
      shell.destroy();
      tree.destroy();
    },

    testSetFontByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.TreeItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "font" : [ ["Arial"], 20, true, false ]
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var item = ObjectManager.getObject( "w4" );
      assertEquals( "bold 20px Arial", item._font.toCss() );
      shell.destroy();
      tree.destroy();
    },

    testSetCellBackgroundsByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.TreeItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "cellBackgrounds" : [ null, [ 0, 255, 0, 255 ], null ]
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var item = ObjectManager.getObject( "w4" );
      assertNull( item.getCellBackground( 0 ) );
      assertEquals( "rgb(0,255,0)", item.getCellBackground( 1 ) );
      assertNull( item.getCellBackground( 2 ) );
      shell.destroy();
      tree.destroy();
    },

    testSetCellForegroundsByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.TreeItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "cellForegrounds" : [ null, [ 0, 255, 0, 255 ], null ]
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var item = ObjectManager.getObject( "w4" );
      assertNull( item.getCellForeground( 0 ) );
      assertEquals( "rgb(0,255,0)", item.getCellForeground( 1 ) );
      assertNull( item.getCellForeground( 2 ) );
      shell.destroy();
      tree.destroy();
    },

    testSetCellFontsByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.TreeItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "cellFonts" : [ null, [ ["Arial"], 20, true, false ], null ]
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var item = ObjectManager.getObject( "w4" );
      assertNull( item.getCellFont( 0 ) );
      assertEquals( "bold 20px Arial", item.getCellFont( 1 ) );
      assertNull( item.getCellFont( 2 ) );
      shell.destroy();
      tree.destroy();
    },

    testSetExpandedByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.TreeItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "expanded" : true
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var item = ObjectManager.getObject( "w4" );
      assertTrue( item.isExpanded() );
      shell.destroy();
      tree.destroy();
    },

    testSetCheckedByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [ "CHECK" ] );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.TreeItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "checked" : true
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var item = ObjectManager.getObject( "w4" );
      assertTrue( item.isChecked() );
      shell.destroy();
      tree.destroy();
    },

    testSetGrayedByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [ "CHECK" ] );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.TreeItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "grayed" : true
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var item = ObjectManager.getObject( "w4" );
      assertTrue( item.isGrayed() );
      shell.destroy();
      tree.destroy();
    },

    testSetCustomVariantByProtocol : function() {
      var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var shell = TestUtil.createShellByProtocol( "w2" );
      var tree = this._createTreeByProtocol( "w3", "w2", [] );
      var processor = org.eclipse.rwt.protocol.Processor;
      processor.processOperation( {
        "target" : "w4",
        "action" : "create",
        "type" : "rwt.widgets.TreeItem",
        "properties" : {
          "parent" : "w3",
          "index": 0,
          "customVariant" : "variant_blue"
        }
      } );
      var ObjectManager = org.eclipse.rwt.protocol.ObjectManager;
      var item = ObjectManager.getObject( "w4" );
      assertEquals( "variant_blue", item.getVariant() );
      shell.destroy();
      tree.destroy();
    },

    testCreateItem : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      assertTrue( item instanceof org.eclipse.rwt.widgets.TreeItem );
      assertEquals( "", item.getText( 0 ) );
      assertTrue( item.isCached() );
    },

    testCreateAndDisposeByServer: function() {
      var parent = new org.eclipse.rwt.widgets.TreeItem();
      var item = org.eclipse.rwt.widgets.TreeItem.createItem( parent, 0 );
      assertTrue( item instanceof org.eclipse.rwt.widgets.TreeItem );
      assertTrue( item.isCached() );
      item.dispose();
      assertIdentical( undefined, parent._children[ 0 ] );
    },

    testCreatePlaceholderItem : function() {
      var parent = new org.eclipse.rwt.widgets.TreeItem();
      var item = new org.eclipse.rwt.widgets.TreeItem( parent, 0, true );
      assertFalse( item.isCached() );
      item.markCached();
      assertTrue( item.isCached() );
    },

    testText : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      item.setTexts( [ "Test", "Test2" ] );
      assertEquals( "Test", item.getText( 0 ) );
      assertEquals( "Test2", item.getText( 1 ) );
    },

    testItemFont : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      item.setFont( "Arial" );
      assertEquals( "Arial", item.getCellFont( 0 ) );
    },

    testCellFonts : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      item.setCellFonts( [ "Arial", "Verdana", "monospace" ] );
      assertEquals( "Arial", item.getCellFont( 0 ) );       
      assertEquals( "Verdana", item.getCellFont( 1 ) );       
      assertEquals( "monospace", item.getCellFont( 2 ) );
    },

    testSomeCellFontsSet : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      item.setFont( "Arial" );
      item.setCellFonts( [ null, "Verdana" ] );
      assertEquals( "Arial", item.getCellFont( 0 ) );       
      assertEquals( "Verdana", item.getCellFont( 1 ) );       
      assertEquals( "Arial", item.getCellFont( 2 ) );       
    },

    testForeground : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      item.setForeground( "red" );
      assertEquals( "red", item.getCellForeground( 0 ) );
    },

    testCellForegrounds : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      item.setCellForegrounds( [ "red", "green" ] );
      assertEquals( "red", item.getCellForeground( 0 ) );       
      assertEquals( "green", item.getCellForeground( 1 ) );
    },

    testSomeCellForegroundsSets : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      item.setForeground( "red" );
      item.setCellForegrounds( [ null, "green" ] );
      assertEquals( "red", item.getCellForeground( 0 ) );
      assertEquals( "green", item.getCellForeground( 1 ) );
      assertEquals( "red", item.getCellForeground( 2 ) );
    },

    testBackground : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      item.setBackground( "red" );
      assertEquals( "red", item.getBackground() );
    },

    testCellBackgrounds: function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      item.setCellBackgrounds( [ "red", "green" ] );
      assertEquals( "red", item.getCellBackground( 0 ) );       
      assertEquals( "green", item.getCellBackground( 1 ) );       
    },

    testSomeCellBackgroundsSet : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      item.setCellBackgrounds( [ null, "green" ] );
      item.setBackground( "red" );
      assertEquals( null, item.getCellBackground( 0 ) );       
      assertEquals( "green", item.getCellBackground( 1 ) );              
    },

    testImages : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      item.setImages( [ "test1.jpg", "test2.jpg" ] );
      assertEquals( "test1.jpg", item.getImage( 0 ) );
      assertEquals( "test2.jpg", item.getImage( 1 ) );
      assertEquals( null, item.getImage( 2 ) );
    },

    testClear : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      item.setImages( [ "test1.jpg", "test2.jpg" ] );
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
      var item = new org.eclipse.rwt.widgets.TreeItem();
      var child = new org.eclipse.rwt.widgets.TreeItem( item, 0 );
      assertIdentical( item, child.getParent() );
    },

    testChildren : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      item.setItemCount( 2 );
      var child1 = new org.eclipse.rwt.widgets.TreeItem( item, 0 );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( item, 1 );
      assertEquals( [ child1, child2 ], item._children );
    },

    testHasChildren : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      assertFalse( item.hasChildren() );
      item.setItemCount( 1 );
      var child = new org.eclipse.rwt.widgets.TreeItem( item, 0 );
      assertTrue( item.hasChildren() );
    },

    testSetExpanded : function() {
      var root = new org.eclipse.rwt.widgets.TreeItem();
      root.setItemCount( 1 );
      var item = new org.eclipse.rwt.widgets.TreeItem( root );
      assertFalse( item.isExpanded() );
      item.setExpanded( true );
      assertTrue( item.isExpanded() );       
    },

    testHasPreviousSibling : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      item.setItemCount( 2 );
      var child1 = new org.eclipse.rwt.widgets.TreeItem( item, 0 );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( item, 1 );
      assertFalse( child1.hasPreviousSibling() );
      assertTrue( child2.hasPreviousSibling() );
    },

    testHasNextSibling : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      item.setItemCount( 2 );
      var child1 = new org.eclipse.rwt.widgets.TreeItem( item, 0 );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( item, 1 );
      assertTrue( child1.hasNextSibling() );
      assertFalse( child2.hasNextSibling() );
    },

    testGetLevel : function() {
      var root = new org.eclipse.rwt.widgets.TreeItem();
      root.setItemCount( 1 );
      var child1 = new org.eclipse.rwt.widgets.TreeItem( root, 0 );
      child1.setItemCount( 1 );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( child1, 0 );
      assertEquals( -1, root.getLevel() );
      assertEquals( 0, child1.getLevel() );
      assertEquals( 1, child2.getLevel() );
    },

    testIsRootItem : function() {
      var root = new org.eclipse.rwt.widgets.TreeItem();
      root.setItemCount( 1 );
      var child1 = new org.eclipse.rwt.widgets.TreeItem( root );
      child1.setItemCount( 1 );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( child1, 0 );
      assertTrue( root.isRootItem() );
      assertFalse( child1.isRootItem() );
      assertFalse( child2.isRootItem() );
    },

    testItemAddedEvent : function() {
      var log = [];
      var root = new org.eclipse.rwt.widgets.TreeItem();
      root.addEventListener( "update", function( event ) {
        log.push( event.msg );
      }, this);
      root.setItemCount( 1 );
      var child1 = new org.eclipse.rwt.widgets.TreeItem( root, 0 );
      assertEquals( 1, log.length );
      assertEquals( "add", log[ 0 ] );
    },

    testItemAddedEventBubbles : function() {
      var log = [];
      var root = new org.eclipse.rwt.widgets.TreeItem();
      root.addEventListener( "update", function( event ) {
        log.push( event );
      }, this);
      root.setItemCount( 1 );
      var child1 = new org.eclipse.rwt.widgets.TreeItem( root, 0 );
      child1.setItemCount( 1 );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( child1, 0 );
      assertEquals( 2, log.length );
    },

    testItemChangedEvent : function() {
      var log = [];
      var item = new org.eclipse.rwt.widgets.TreeItem();
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
      item.setImages( [ "bla.jpg" ] );
      assertEquals( 8, log.length );
    },

    testRemoveItem : function() {
      var root = new org.eclipse.rwt.widgets.TreeItem();
      root.setItemCount( 3 );
      var child1 = new org.eclipse.rwt.widgets.TreeItem( root, 0 );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( root, 1 );
      var child3 = new org.eclipse.rwt.widgets.TreeItem( root, 2 );
      assertEquals( [ child1, child2, child3 ], root._children );
      child2.dispose();
      assertEquals( [ child1, child3, undefined ], root._children );
    },

    testRemoveItemEvent : function() {
      var log = [];
      var root = new org.eclipse.rwt.widgets.TreeItem();
      root.setItemCount( 1 );
      var child = new org.eclipse.rwt.widgets.TreeItem( root, 0 );
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
      var root = new org.eclipse.rwt.widgets.TreeItem();
      root.setItemCount( 1 );
      var child1 = new org.eclipse.rwt.widgets.TreeItem( root, 0 );
      assertEquals( child1, root.getChild( 0 ) );
      assertIdentical( undefined, root.getChild( 1 ) );
    },

    testGetPreviousSibling : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      item.setItemCount( 2 );
      var child1 = new org.eclipse.rwt.widgets.TreeItem( item, 0 );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( item, 1 );
      assertEquals( child1, child2.getPreviousSibling() );
    },

    testGetNextSibling : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      item.setItemCount( 2 );
      var child1 = new org.eclipse.rwt.widgets.TreeItem( item, 0 );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( item, 1 );
      assertEquals( child2, child1.getNextSibling() );
    },

    testItemExpandedEvent : function() {
      var log = [];
      var root = new org.eclipse.rwt.widgets.TreeItem();
      root.setItemCount( 1 );
      var child1 = new org.eclipse.rwt.widgets.TreeItem( root, 0 );
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
      var root = new org.eclipse.rwt.widgets.TreeItem();
      root.setItemCount( 10 );
      for( var i = 0; i < 10; i++ ) {
        new org.eclipse.rwt.widgets.TreeItem( root, i );
      }
      assertEquals( 10, root.getVisibleChildrenCount() );
    },

    testVisibleChildrenCount : function() {
      var root = new org.eclipse.rwt.widgets.TreeItem();
      root.setItemCount( 2 );
      var item1 = new org.eclipse.rwt.widgets.TreeItem( root, 0 );
      var item2 = new org.eclipse.rwt.widgets.TreeItem( root, 1 );
      item1.setItemCount( 1 );
      item2.setItemCount( 1 );
      new org.eclipse.rwt.widgets.TreeItem( item1, 0 );
      new org.eclipse.rwt.widgets.TreeItem( item2, 0 );
      item1.setExpanded( true );
      assertEquals( 3, root.getVisibleChildrenCount() );
    },

    testVisibleChildrenCountResultIsZeroNoChildren : function() {
      var root = new org.eclipse.rwt.widgets.TreeItem();
      assertEquals( 0, root.getVisibleChildrenCount() );
    },

    testVisibleChildrenCountResultIsZeroWithCollapsedChildren : function() {
      var root = new org.eclipse.rwt.widgets.TreeItem();
      root.setItemCount( 1 );
      var item = new org.eclipse.rwt.widgets.TreeItem( root, 0 );
      item.setItemCount( 1 );
      new org.eclipse.rwt.widgets.TreeItem( item, 0 );
      assertEquals( 0, item.getVisibleChildrenCount() );
    },

    testSetChecked : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      assertFalse( item.isChecked() );
      item.setChecked( true );
      assertTrue( item.isChecked() );
      item.setChecked( false );
      assertFalse( item.isChecked() );
    },

    testItemCheckedEvent : function() {
      var log = [];
      var root = new org.eclipse.rwt.widgets.TreeItem();
      root.setItemCount( 1 );
      var child1 = new org.eclipse.rwt.widgets.TreeItem( root, 0 );
      root.addEventListener( "update", function( event ) {
        log.push( event );
      }, this);
      child1.setChecked( true );
      child1.setChecked( false );
      assertEquals( 2, log.length );
    },

    testAddItemAt : function() {
      var root = new org.eclipse.rwt.widgets.TreeItem();
      root.setItemCount( 3 );
      var child1 = org.eclipse.rwt.widgets.TreeItem.createItem( root, 0 );
      var child2 = org.eclipse.rwt.widgets.TreeItem.createItem( root, 1 );
      var child3 = org.eclipse.rwt.widgets.TreeItem.createItem( root, 1 );
      assertEquals( [ child1, child3, child2 ], root._children );
      root.dispose();
    },

    testSetGrayed : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      item.setGrayed( true );
      assertTrue( true, item.isGrayed() );
    },

    testIsDisplayable : function() {
      var root = new org.eclipse.rwt.widgets.TreeItem();
      root.setItemCount( 1 );
      var child1 = new org.eclipse.rwt.widgets.TreeItem( root, 0 );
      child1.setItemCount( 1 );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( child1, 0 );
      child2.setItemCount( 1 );
      var child3 = new org.eclipse.rwt.widgets.TreeItem( child2, 0 );
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
      var item = new org.eclipse.rwt.widgets.TreeItem();
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
      var item = new org.eclipse.rwt.widgets.TreeItem();
      var log = [];
      item.setItemCount( 4 );
      assertEquals( 4, item._children.length );
      assertEquals( [ undefined, undefined, undefined, undefined ], item._children );
      var child1 = new org.eclipse.rwt.widgets.TreeItem( item, 0 );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( item, 1 );
      assertEquals( 4, item._children.length );
      assertEquals( [ child1, child2, undefined, undefined ], item._children );
      var child3 = new org.eclipse.rwt.widgets.TreeItem( item, 1 );
      // setItemCount is not rendered by server since it stays the same
      assertEquals( 4, item._children.length );
      assertEquals( [ child1, child3, child2, undefined ], item._children );
      item.dispose();
    },

    testReplaceUndefinedItem : function() {
      var log = [];
      var root = new org.eclipse.rwt.widgets.TreeItem();
      root.addEventListener( "update", function( event ) {
        log.push( event.msg, event.target );
      }, this);
      root.setItemCount( 4 );
      var child1 = new org.eclipse.rwt.widgets.TreeItem( root, 0 );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( root, 3 );
      var child3 = new org.eclipse.rwt.widgets.TreeItem( root, 2 );
      assertEquals( [ child1, undefined, child3, child2 ], root._children );
      // NOTE: Only one "add" for setItemCount. The other items have never been rendered 
      //       (otherwise woulnt be undefined) and don't shift anything either. 
      assertEquals( [ "add", root], log );
    },

    testGetVirtualItem: function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      item.setItemCount( 4 );
      var child = item.getChild( 0 );
      assertFalse( child.isCached() );
      assertEquals( 0, item._children.indexOf( child ) );
      assertEquals( "...", child.getText( 0 ) );
      var sibling = child.getNextSibling();
      assertFalse( sibling.isCached() );
      assertEquals( 1, item._children.indexOf( sibling ) );
      item.dispose()
    },

    testHasVirtualSibling: function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      item.setItemCount( 4 );
      var child = item.getChild( 0 );
      assertEquals( 0, item._children.indexOf( child ) );
      assertTrue( child.hasNextSibling() );
      assertIdentical( undefined, item._children[ 1 ] );
      item.dispose()
    },

    testVirtualChildrenCount: function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      item.setItemCount( 4 );
      assertEquals( 4, item.getVisibleChildrenCount() );
      assertEquals( [ undefined, undefined, undefined, undefined ], item._children );
      item.dispose()
    },

    testVirtualGetLastChild: function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      item.setItemCount( 4 );
      var child = item.getLastChild();
      assertEquals( 3, item._children.indexOf( child ) );
      item.dispose()
    },

    testCacheItem : function() {
      var parent = new org.eclipse.rwt.widgets.TreeItem();
      parent.setItemCount( 4 );
      var item = parent.getChild( 2 );
      assertFalse( item.isCached() );
      var newItem = org.eclipse.rwt.widgets.TreeItem.createItem( parent, 2 );
      assertIdentical( item, newItem );
      assertTrue( item.isCached() );
      assertEquals( "", item.getText( 0 ) );
      item.dispose();
    },
    
    testFindItemByFlatIndexFirstItem : function() {
    	var root = this._createRoot();
    	var item = root.findItemByFlatIndex( 0 );
    	assertIdentical( item, root.getChild( 0 ) );
    	root.dispose();
    },

    testFindItemByFlatIndexLastItem: function() {
    	var root = this._createRoot();
    	var item = root.findItemByFlatIndex( 1434 );
    	assertIdentical( item, root.getChild( 19 ) );
    	root.dispose();
    },

    testFindItemByFlatIndexOutOfBounds: function() {
    	var root = this._createRoot();
    	assertIdentical( undefined, root.findItemByFlatIndex( 1435 ) );
    	root.dispose();
    },

    testFindItemByFlatIndex : function() {
    	var root = this._createRoot();
    	var item = root.findItemByFlatIndex( 1020 );
    	assertEquals( "x", item.getText( 0 ) );
    	assertFalse( root.isChildCreated( 7 ) );
    	root.dispose();
    },

    testFindItemByFlatIndexNegative : function() {
      var root = new org.eclipse.rwt.widgets.TreeItem();
    	assertNull( root.findItemByFlatIndex( -1 ) );
    	root.dispose();
    },

    testFindItemByFlatIndexNoItems : function() {
      var root = new org.eclipse.rwt.widgets.TreeItem();
    	assertIdentical( undefined, root.findItemByFlatIndex( 0 ) );
    	root.dispose();
    },

    testFindItemByFlatIndexFirstItemOfLayer : function() {
    	var root = this._createRoot();
    	var item = root.findItemByFlatIndex( 1031 );
    	assertEquals( "y", item.getText( 0 ) );
    	root.dispose();
    },

    testFindItemByFlatIndexLastItemOfLayer : function() {
    	var root = this._createRoot();
    	var item = root.findItemByFlatIndex( 1006 );
    	assertEquals( "z", item.getText( 0 ) );
    	root.dispose();
    },

    testFindItemByFlatIndexCollapse : function() {
      var root = this._createRoot();
      assertEquals( "x", root.findItemByFlatIndex( 1020 ).getText( 0 ) );
      root.getChild( 6 ).setExpanded( false );
      assertEquals( "x", root.findItemByFlatIndex( 20 ).getText( 0 ) );
      root.dispose();
    },

    testFindItemByFlatIndexRemoveChild : function() {
      var root = this._createRoot();
      assertEquals( "x", root.findItemByFlatIndex( 1020 ).getText( 0 ) );
      root.getChild( 6 ).dispose();
      assertEquals( "x", root.findItemByFlatIndex( 19 ).getText( 0 ) );
      root.dispose();
    },

    testFindItemByFlatIndexCollapseAdd : function() {
      var root = this._createRoot();
      assertEquals( "x", root.findItemByFlatIndex( 1020 ).getText( 0 ) );
      root.getChild( 6 ).setItemCount( 1010 );
      assertEquals( "x", root.findItemByFlatIndex( 1030 ).getText( 0 ) );
      root.dispose();
    },

    testFindItemByFlatIndexCollapseRemove : function() {
      var root = this._createRoot();
      assertEquals( "x", root.findItemByFlatIndex( 1020 ).getText( 0 ) );
      root.getChild( 6 ).setItemCount( 900 );
      assertEquals( "x", root.findItemByFlatIndex( 920 ).getText( 0 ) );
      root.dispose();
    },


    testGetFlatIndex : function() {
      var root = this._createRoot();
      assertEquals( 0, root.findItemByFlatIndex( 0 ).getFlatIndex() );
      assertEquals( 6, root.findItemByFlatIndex( 6 ).getFlatIndex() );
      assertEquals( 7, root.findItemByFlatIndex( 7 ).getFlatIndex() );
      assertEquals( 1020, root.findItemByFlatIndex( 1020 ).getFlatIndex() );
      assertEquals( 1434, root.findItemByFlatIndex( 1434 ).getFlatIndex() );
      assertFalse( root.isChildCreated( 7 ) );
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

    //////////////////////
    // create complex tree

    _createTreeByProtocol : function( id, parentId, styles ) {
      org.eclipse.rwt.protocol.Processor.processOperation( {
        "target" : id,
        "action" : "create",
        "type" : "rwt.widgets.Tree",
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
      return org.eclipse.rwt.protocol.ObjectManager.getObject( id );
    },

    _createRoot : function() {
      var result = new org.eclipse.rwt.widgets.TreeItem();
      result.setItemCount( 20 );
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