/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.ListTest", {
  extend : qx.core.Object,
  
  members : {

    testCreateDispose : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      assertTrue( list instanceof org.eclipse.swt.widgets.List );
      list.destroy();
      testUtil.flush();
      assertTrue( list.isDisposed() );
    },
    
    testSetItems : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      this._addItems( list, 3 );
      testUtil.flush();
      var items = this._getItems( list );
      assertEquals( 3, items.length );
      assertEquals( "item0", items[ 0 ].getLabel() );
      assertEquals( "item1", items[ 1 ].getLabel() );
      assertEquals( "item2", items[ 2 ].getLabel() );
      list.destroy();
    },
    
    testSelectItem : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      this._addItems( list, 3 );
      testUtil.flush();
      list.selectItem( 2 );
      var selection = this._getSelection( list ); 
      assertEquals( 1, selection.length );
      assertEquals( "item2", selection[ 0 ].getLabel() );
      list.destroy();
    },
    
    testSelectItemByCharacter : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      list.setItems( [ "Akira", "Boogiepop", "C something", "Daria" ] );
      testUtil.flush();
      testUtil.press( list, "c" )
      var selection = this._getSelection( list ); 
      assertEquals( 1, selection.length );
      assertEquals( "C something", selection[ 0 ].getLabel() );
      list.destroy();
    },

    testSelectItems : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      this._addItems( list, 3 );
      testUtil.flush();
      list.selectItems( [ 1, 2 ] );
      var selection = this._getSelection( list ); 
      assertEquals( 2, selection.length );
      assertEquals( "item1", selection[ 0 ].getLabel() );
      assertEquals( "item2", selection[ 1 ].getLabel() );
      list.destroy();      
    },

    testSelectAll : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      this._addItems( list, 3 );
      testUtil.flush();
      list.selectAll();
      var selection = this._getSelection( list ); 
      assertEquals( 3, selection.length );
      assertEquals( "item0", selection[ 0 ].getLabel() );
      assertEquals( "item1", selection[ 1 ].getLabel() );
      assertEquals( "item2", selection[ 2 ].getLabel() );
      list.destroy();            
    },
    
    testFocusItem : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      list.setItems( [ "item0", "item1", "item2" ] );
      list.focusItem( 1 );
      testUtil.flush();
      assertEquals( "item1", this._getLeadItem( list ).getLabel() );
      list.selectAll();
      list.destroy();
    },
    
    testSetTopIndex : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      this._addItems( list, 300 );
      testUtil.flush();
      list.setTopIndex( 40 );
      assertEquals( 40, this._getTopItemIndex( list ) );
      list.selectAll();
      list.destroy();
    },
    
    testSendSelection : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = this._createDefaultList();
      list.setItems( [ "item0", "item1", "item2" ] );
      testUtil.flush();
      var item = this._getItems( list )[ 1 ];
      list.setChangeSelectionNotification( "action" );
      list.setUserData( "id", "w3" );
      testUtil.click( item );
      assertEquals( 1, testUtil.getRequestsSend() );
      assertTrue( testUtil.getMessage().indexOf( "w3.selection=1" ) != -1 );
      list.selectAll();
      list.destroy();
    },
    
    //////////
    // Helpers
    
    _createDefaultList : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var list = new org.eclipse.swt.widgets.List( true );
      list.addToDocument(),
      list.setSpace( 5, 238, 5, 436 );
      testUtil.flush();
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
      return list.getChildren();
    },
    
    _getSelection : function( list ) {
      return list.getSelectedItems();
    },
    
    _getLeadItem : function( list ) {
      return list.getManager().getLeadItem();
    },
    
    _getTopItemIndex : function( list ) {
      return list._getTopIndex();
    }
    
  }
  
} );