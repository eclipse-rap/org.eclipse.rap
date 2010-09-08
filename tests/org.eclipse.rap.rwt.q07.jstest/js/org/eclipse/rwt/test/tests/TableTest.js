/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.TableTest", {

  extend : qx.core.Object,
  
  members : {
    
    testCreateTable : function() {
      var table = this._createDefaultTable();
      assertTrue( table instanceof org.eclipse.swt.widgets.Table );
      assertEquals( "table", table.getAppearance() );
      table.destroy();
    },
    
    testTableCreatesRows : function() {
      var table = this._createDefaultTable();
      assertEquals( 4, table._rows.length );
      assertTrue( table._rows[ 0 ] instanceof org.eclipse.swt.widgets.TableRow );
      table.destroy();
    },
    
    testTableLinesVisibleState : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var table = this._createDefaultTable( true );
      table.setLinesVisible( true );
      testUtil.flush();
      assertTrue( table.hasState( "linesvisible" ) );
      assertTrue( table._rows[ 0 ].hasState( "linesvisible" ) );
      table.setLinesVisible( false );
      assertFalse( table.hasState( "linesvisible" ) );
      assertFalse( table._rows[ 0 ].hasState( "linesvisible" ) );      
      table.destroy();
    },
    
    /////////
    // Helper
    
    _createDefaultTable : function( noflush ) {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var result = new org.eclipse.swt.widgets.Table( "w3", "" );
      result.setWidth( 100 );
      result.setHeight( 90 );
      result.setItemHeight( 20 );
      result.setItemMetrics( 0, 0, 100, 2, 10, 15, 70 );
      result.addToDocument();
      if( noflush !== false ) {
        testUtil.flush();
      }
      return result;
    }
    

  }
  
} );