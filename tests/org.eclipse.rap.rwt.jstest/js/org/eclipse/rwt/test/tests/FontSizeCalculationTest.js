/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.FontSizeCalculationTest", {

  extend : qx.core.Object,

  members : {

    testSizeWithSequentialWhitespacesNoWrap : function() {
      var FontSizeCalculation = org.eclipse.swt.FontSizeCalculation;
      var item1 = [ "id1", "foo bar", "Arial", 12, false, false, -1 ];
      var item2 = [ "id2", "foo  bar", "Arial", 12, false, false, -1 ];
      var item3 = [ "id3", " foo bar", "Arial", 12, false, false, -1 ];
      
      var size1 = FontSizeCalculation._measureItem( item1, true);
      var size2 = FontSizeCalculation._measureItem( item2, true);
      var size3 = FontSizeCalculation._measureItem( item3, true);
      
      assertTrue( size1[ 0 ] < size2[ 0 ] );
      assertTrue( size2[ 0 ] === size3[ 0 ] );
    },

    testSizeWithSequentialWhitespacesWrap : function() {
      var FontSizeCalculation = org.eclipse.swt.FontSizeCalculation;
      var item1 = [ "id1", "foo bar", "Arial", 12, false, false, -1 ];
      var item2 = [ "id2", "foo bar", "Arial", 12, false, false, 20 ];
      var item3 = [ "id3", "foo      bar", "Arial", 12, false, false, 20 ];
      
      var size1 = FontSizeCalculation._measureItem( item1, true);
      var size2 = FontSizeCalculation._measureItem( item2, true);
      var size3 = FontSizeCalculation._measureItem( item3, true);
      
      assertTrue( size1[ 0 ] > size2[ 0 ] );
      assertTrue( size1[ 1 ] < size2[ 1 ] );
      // TODO: div is resized in IE8 even the style.width (wrap width) is fixed
      if( !rwt.client.Client.isMshtml() ) {
        assertTrue( size2[ 0 ] === size3[ 0 ] );
      }
      assertTrue( size2[ 1 ] === size3[ 1 ] );
    }

  }

} );