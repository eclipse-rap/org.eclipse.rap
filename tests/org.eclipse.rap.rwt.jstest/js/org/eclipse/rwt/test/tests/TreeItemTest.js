/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.TreeItemTest", {

  extend : qx.core.Object,
  
  members : {

    testCreateItem : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      assertEquals( "", item.getText( 0 ) );
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
     
    testParent : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      var child = new org.eclipse.rwt.widgets.TreeItem( item );
      assertIdentical( item, child.getParent() );
    },
     
    testChildren : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      var child1 = new org.eclipse.rwt.widgets.TreeItem( item );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( item );
      assertEquals( [ child1, child2 ], item.getChildren() );
    },
     
    testHasChildren : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      assertFalse( item.hasChildren() );
      var child = new org.eclipse.rwt.widgets.TreeItem( item );
      assertTrue( item.hasChildren() );
    },
     
    testSetExpanded : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      assertFalse( item.isExpanded() );
      item.setExpanded( true );
      assertTrue( item.isExpanded() );       
    },
     
    testHasPreviousSibling : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      var child1 = new org.eclipse.rwt.widgets.TreeItem( item );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( item );
      assertFalse( child1.hasPreviousSibling() );
      assertTrue( child2.hasPreviousSibling() );
    },
     
    testHasNextSibling : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      var child1 = new org.eclipse.rwt.widgets.TreeItem( item );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( item );
      assertTrue( child1.hasNextSibling() );
      assertFalse( child2.hasNextSibling() );
    },
    
    testgetLevel : function() {
      var root = new org.eclipse.rwt.widgets.TreeItem();
      var child1 = new org.eclipse.rwt.widgets.TreeItem( root );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( child1 );
      assertEquals( -1, root.getLevel() );
      assertEquals( 0, child1.getLevel() );
      assertEquals( 1, child2.getLevel() );
    },
    
    testIsRootItem : function() {
      var root = new org.eclipse.rwt.widgets.TreeItem();
      var child1 = new org.eclipse.rwt.widgets.TreeItem( root );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( child1 );
      assertTrue( root.isRootItem() );
      assertFalse( child1.isRootItem() );
      assertFalse( child2.isRootItem() );
    },
     
    testItemAddedEvent : function() {
      var log = [];
      var root = new org.eclipse.rwt.widgets.TreeItem();
      root.addEventListener( "update", function( event ) {
        log.push( event.getData() );
      }, this);
      var child1 = new org.eclipse.rwt.widgets.TreeItem( root );
      assertEquals( 1, log.length );
      assertEquals( "add", log[ 0 ] );
    },
     
    testItemAddedEventBubbles : function() {
      var log = [];
      var root = new org.eclipse.rwt.widgets.TreeItem();
      root.addEventListener( "update", function( event ) {
        log.push( event );
      }, this);
      var child1 = new org.eclipse.rwt.widgets.TreeItem( root );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( child1 );
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
      var child1 = new org.eclipse.rwt.widgets.TreeItem( root );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( root );
      var child3 = new org.eclipse.rwt.widgets.TreeItem( root );
      assertEquals( [ child1, child2, child3 ], root.getChildren() );
      child2.dispose();
      assertEquals( [ child1, child3 ], root.getChildren() );
    },
    
    testRemoveItemEvent : function() {
      var log = [];
      var root = new org.eclipse.rwt.widgets.TreeItem();
      var child = new org.eclipse.rwt.widgets.TreeItem( root );
      root.addEventListener( "update", function( event ) {
        log.push( event.getData() );
      }, this);
      child.dispose();
      assertEquals( 1, log.length );
      assertEquals( "remove", log[ 0 ] );
    },
    
    testGetChild : function() {
      var root = new org.eclipse.rwt.widgets.TreeItem();
      var child1 = new org.eclipse.rwt.widgets.TreeItem( root );
      assertEquals( child1, root.getChild( 0 ) );
      assertNull( root.getChild( 1 ) );
    },
    
    testGetPreviousSibling : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      var child1 = new org.eclipse.rwt.widgets.TreeItem( item );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( item );
      assertEquals( child1, child2.getPreviousSibling() );
    },
     
    testGetNextSibling : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      var child1 = new org.eclipse.rwt.widgets.TreeItem( item );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( item );
      assertEquals( child2, child1.getNextSibling() );
    },
    
    testItemExpandedEvent : function() {
      var log = [];
      var root = new org.eclipse.rwt.widgets.TreeItem();
      var child1 = new org.eclipse.rwt.widgets.TreeItem( root );
      root.addEventListener( "update", function( event ) {
        log.push( event.getData() );
      }, this);
      child1.setExpanded( true );
      child1.setExpanded( false );
      assertEquals( 2, log.length );
      assertEquals( "expanded", log[ 0 ] );
      assertEquals( "collapsed", log[ 1 ] );
    },
    
    testVisibleChildrenCountOnlyOneLayer : function() {
      var root = new org.eclipse.rwt.widgets.TreeItem();
      for( var i = 0; i < 10; i++ ) {
        new org.eclipse.rwt.widgets.TreeItem( root );
      }
      assertEquals( 10, root.getVisibleChildrenCount() );
    },
    
    testVisibleChildrenCount : function() {
      var root = new org.eclipse.rwt.widgets.TreeItem();
      var item1 = new org.eclipse.rwt.widgets.TreeItem( root );
      var item2 = new org.eclipse.rwt.widgets.TreeItem( root );
      new org.eclipse.rwt.widgets.TreeItem( item1 );
      new org.eclipse.rwt.widgets.TreeItem( item2 );
      item1.setExpanded( true );
      assertEquals( 3, root.getVisibleChildrenCount() );
    },
    
    testVisibleChildrenCountResultIsZeroNoChildren : function() {
      var root = new org.eclipse.rwt.widgets.TreeItem();
      assertEquals( 0, root.getVisibleChildrenCount() );
    },
    
    testVisibleChildrenCountResultIsZeroWithCollapsedChildren : function() {
      var root = new org.eclipse.rwt.widgets.TreeItem();
      var item = new org.eclipse.rwt.widgets.TreeItem( root );
      new org.eclipse.rwt.widgets.TreeItem( item );
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
      var child1 = new org.eclipse.rwt.widgets.TreeItem( root );
      root.addEventListener( "update", function( event ) {
        log.push( event );
      }, this);
      child1.setChecked( true );
      child1.setChecked( false );
      assertEquals( 2, log.length );
    },
    
    testAddItemAt : function() {
      var root = new org.eclipse.rwt.widgets.TreeItem();
      var child1 = new org.eclipse.rwt.widgets.TreeItem( root, 0 );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( root, 1 );
      var child3 = new org.eclipse.rwt.widgets.TreeItem( root, 1 );
      assertEquals( [ child1, child3, child2 ], root.getChildren() );
    },

    testSetGrayed : function() {
      var item = new org.eclipse.rwt.widgets.TreeItem();
      item.setGrayed( true );
      assertTrue( true, item.isGrayed() );
    },
    
    testIsDisplayable : function() {
      var root = new org.eclipse.rwt.widgets.TreeItem();
      var child1 = new org.eclipse.rwt.widgets.TreeItem( root, 0 );
      var child2 = new org.eclipse.rwt.widgets.TreeItem( child1, 0 );
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
    }

 }
  
} );