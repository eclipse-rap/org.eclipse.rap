/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

qx.Class.define( "org.eclipse.rwt.test.tests.AdapterUtilTest", {

  extend : qx.core.Object,
  
  members : {
    
    testGradientHandler : function() {
      var handler = org.eclipse.rwt.protocol.AdapterUtil.getBackgroundGradientHandler();
      var widget = new org.eclipse.swt.widgets.Composite();
      handler( widget, [ [ "red", "yellow" ], [ 0, 100 ], true ] );
      var gradient = widget.getBackgroundGradient();
      assertEquals( 2, gradient.length );
      assertEquals( [ 0, "red" ], gradient[ 0 ] );
      assertEquals( [ 1, "yellow" ], gradient[ 1] );
      assertFalse( gradient.horizontal === true );
    },

    testGradientHandlerReset : function() {
      var handler = org.eclipse.rwt.protocol.AdapterUtil.getBackgroundGradientHandler();
      var widget = new org.eclipse.swt.widgets.Composite();
      handler( widget, [ [ "red", "yellow" ], [ 0, 100 ], true ] );
      handler( widget, null );
      assertNull( widget.getBackgroundGradient() );
    },

    testBorderHandler : function() {
      var handler = org.eclipse.rwt.protocol.AdapterUtil.getRoundedBorderHandler();
      var widget = new org.eclipse.swt.widgets.Composite();
      handler( widget, [ 1, "blue", 2, 3, 4, 5 ] );
      var border = widget.getBorder();
      assertEquals( "rounded", border.getStyle() );
      assertEquals( "blue", border.getColor() );
      assertEquals( [ 2, 3, 4, 5 ], border.getRadii() );
    },

    testBorderHandlerReset : function() {
      var handler = org.eclipse.rwt.protocol.AdapterUtil.getRoundedBorderHandler();
      var widget = new org.eclipse.rwt.widgets.Button( "push" );
      var orgBorder = widget.getBorder();
      handler( widget, [ 1, "blue", 2, 3, 4, 5 ] );
      handler( widget, null );
      assertIdentical( orgBorder, widget.getBorder() );
    }

  }
  
} );