/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.SpinnerTest", {
  extend : qx.core.Object,
  
  construct : function() {
    org.eclipse.rwt.test.fixture.TestUtil.prepareTimerUse();
  },
  
  members : {

    testCreate : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var spinner = new org.eclipse.swt.widgets.Spinner();
      spinner.addToDocument();
      spinner.addState( "rwt_BORDER" );
      spinner.setEditable( true );
      spinner.setSpace( 59, 60, 5, 20 );
      spinner.setZIndex( 299 );
      spinner.setTabIndex( 58 );
      spinner.setMinMaxSelection( 0, 20, 4 );
      spinner.setHasModifyListener( true );
      testUtil.flush();
      assertTrue( spinner.isSeeable() );
      spinner.destroy();
    },
    
    testGetManager : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var spinner = new org.eclipse.swt.widgets.Spinner();
      spinner.addToDocument();
      spinner.setSpace( 59, 60, 5, 20 );
      spinner.setZIndex( 299 );
      spinner.setTabIndex( 58 );
      assertTrue( spinner.getManager() instanceof qx.util.range.Range );
      spinner.destroy();
    },
    
    testSetSeparator: function() {
      var w = new org.eclipse.swt.widgets.Spinner();
      w.addToDocument();
      w.addState( "rwt_BORDER" );
      w.setEditable( true );
      w.setSpace( 59, 60, 5, 20 );
      w.setZIndex( 299 );
      w.setTabIndex( 58 );
      w.setMinMaxSelection( 0, 20, 4 );
      w.setHasModifyListener( true );
      w.setDecimalSeparator( "," );
      w.destroy();
    },
  
    testDispose : function() {
      var testUtil = org.eclipse.rwt.test.fixture.TestUtil;
      var spinner = new org.eclipse.swt.widgets.Spinner();
      spinner.addToDocument();
      spinner.setSpace( 59, 60, 5, 20 );
      testUtil.flush();
      spinner.destroy();
      testUtil.flush();
      assertTrue( spinner.isDisposed() );
    }
  
  }

} );