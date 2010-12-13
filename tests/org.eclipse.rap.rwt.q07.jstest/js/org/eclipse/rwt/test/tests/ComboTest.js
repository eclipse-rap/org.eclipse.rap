/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.ComboTest", {
  extend : qx.core.Object,
  
  members : {

    testCreateDispose : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var combo = this._createDefaultCombo();
      assertTrue( combo instanceof org.eclipse.swt.widgets.Combo );
      combo.destroy();
      testUtil.flush();
      assertTrue( combo.isDisposed() );
    },
    
    testOpenList : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var combo = this._createDefaultCombo();
      combo.setListVisible( true );
      testUtil.flush();
      assertTrue( combo._list.isSeeable() );
      assertEquals( "hidden", combo._list.getOverflow() );
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
    },

    testSelectItem : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var combo = this._createDefaultCombo();
      combo.setListVisible( true );
      testUtil.flush();
      combo.select( 1 );
      assertEquals( "Java", combo._field.getValue() );
      assertEquals( "Java", combo._list.getSelectedItems()[ 0 ].getLabel() )
      combo.destroy();
    },
    
    testScrollBarClick : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var combo = this._createDefaultCombo();
      combo.setListVisible( true );
      testUtil.flush();
      assertTrue( combo._list._vertScrollBar.isSeeable() );
      testUtil.click( combo._list._vertScrollBar );
      assertTrue( combo._list.isSeeable() );
      combo.destroy();
    },

    //////////
    // Helpers

    _createDefaultCombo : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var combo = new org.eclipse.swt.widgets.Combo();
      combo.setSpace( 239, 81, 6, 23 );
      combo.setListItemHeight( 19 );
      combo.setItems( [ "Eiffel", "Java", "Python", "Ruby", "Simula", "Smalltalk" ] );
      combo.setMaxListHeight( 95 ); 
      combo.addToDocument(),
      testUtil.flush();
      return combo;
    },

    _getItems : function( combo ) {
      return combo._list.getItems();
    }

  }

} );