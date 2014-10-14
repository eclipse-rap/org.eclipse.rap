/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

describe( "ScrollBarsActivator", function() {

  var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
  var shell;
  var clientArea;
  var scrollable;
  var activator;

  beforeEach( function() {
    shell = TestUtil.createShellByProtocol();
    clientArea = new rwt.widgets.base.Parent();
    scrollable = new rwt.widgets.base.Scrollable( clientArea );
    scrollable.setScrollBarsVisible( true, true );
    scrollable.setParent( shell );
    activator = new rwt.widgets.util.ScrollBarsActivator( scrollable );
    TestUtil.flush();
  } );

   afterEach( function() {
    shell.destroy();
  } );

  it( "scrollbars are not active by default", function() {
    expect( scrollable.getVerticalBar().hasState( "active" ) ).toBeFalsy();
    expect( scrollable.getHorizontalBar().hasState( "active" ) ).toBeFalsy();
  } );

  it( "activates scrollbars on client area mouse over", function() {
    TestUtil.mouseOver( scrollable );
    TestUtil.forceInterval( activator._activateScrollBarsTimer );

    expect( scrollable.getVerticalBar().hasState( "active" ) ).toBe( true );
    expect( scrollable.getHorizontalBar().hasState( "active" ) ).toBe( true );
  } );

  it( "deactivates scrollbars on client area mouse out", function() {
    scrollable.getVerticalBar().addState( "active" );
    scrollable.getHorizontalBar().addState( "active" );

    TestUtil.mouseOut( scrollable );

    expect( scrollable.getVerticalBar().hasState( "active" ) ).toBeFalsy();
    expect( scrollable.getHorizontalBar().hasState( "active" ) ).toBeFalsy();
  } );

  it( "activates scrollbars on scroll without client area mouse over", function() {
    clientArea.setScrollLeft( 10 );
    clientArea.setScrollTop( 20 );

    scrollable._onscroll( {} );

    expect( scrollable.getVerticalBar().hasState( "active" ) ).toBe( true );
    expect( scrollable.getHorizontalBar().hasState( "active" ) ).toBe( true );
  } );

  it( "deactivates scrollbars after scroll finished without client area mouse over", function() {
    clientArea.setScrollLeft( 10 );
    clientArea.setScrollTop( 20 );

    scrollable._onscroll( {} );
    TestUtil.forceInterval( activator._deactivateScrollBarsTimer );

    expect( scrollable.getVerticalBar().hasState( "active" ) ).toBeFalsy();
    expect( scrollable.getHorizontalBar().hasState( "active" ) ).toBeFalsy();
  } );

} );
