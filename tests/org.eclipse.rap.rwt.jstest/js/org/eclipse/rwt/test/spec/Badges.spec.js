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

describe( "Badges", function() {

  var TestUtil = org.eclipse.rwt.test.fixture.TestUtil;
  var Badges = rwt.widgets.util.Badges;
  var shell;
  var widget;

  beforeEach( function() {
    shell = TestUtil.createShellByProtocol();
    // It's relevant that we use a MCW since they can clear recreate, potentially including badge
    widget = new rwt.widgets.base.MultiCellWidget( [ "image", "label" ] );
    widget.setParent( shell );
    widget.setCellContent( 1, "foo" );
  } );

  afterEach( function() {
    shell.destroy();
    org.eclipse.rwt.test.fixture.Fixture.reset();
  } );

  describe( "setBadge", function() {

    it( "adds badge with text to a new widget", function() {
      Badges.setBadge( widget, "bar" );
      TestUtil.flush();

      var element = Badges._getBadgeElement( widget );
      expect( element.parentNode ).toBe( widget.getElement() );
      expect( element.innerHTML ).toBe( "bar" );
    } );

    it( "add badge element after target node", function() {
      Badges.setBadge( widget, "bar" );
      TestUtil.flush();

      var element = Badges._getBadgeElement( widget );
      expect( element.previousSibling ).toBe( widget._getTargetNode() );
    } );

    it( "sets default css values", function() {
      Badges.setBadge( widget, "bar" );

      var element = Badges._getBadgeElement( widget );
      expect( element.style.position ).toBe( "absolute" );
      expect( element.style.textAlign ).toBe( "center" );
      expect( element.style.lineHeight ).toBe( "1.1" );
      expect( element.style.paddingTop ).toBe( "1px" );
      expect( element.style.paddingBottom ).toBe( "1px" );
    } );

    it( "sets theme css values", function() {
      var themeValues = mock( rwt.theme.ThemeValues );
      themeValues.getCssColor = function( element, key ) {
        if( element === "Widget-Badge" ) {
          return key === "background-color" ? "#ff00ff" : "#00ff00";
        }
      };
      themeValues.getCssBorder.andReturn( new rwt.html.Border( 2, "solid", "#fff000", 3 ) );
      themeValues.getCssFont.andReturn( new rwt.html.Font( 6, [ "fantasy" ] ) );
      spyOn( rwt.theme, "ThemeValues").andCallFake( function() {
        return themeValues;
      } );

      Badges.setBadge( widget, "bar" );

      var element = Badges._getBadgeElement( widget );
      expect( rwt.html.Style.getBackgroundColor( element ) ).toBe( "#ff00ff" );
      expect( rwt.util.Colors.stringToRgb( element.style.color ) ).toEqual( [ 0, 255, 0 ] );
      expect( rwt.util.Colors.stringToRgb( element.style.borderColor ) ).toEqual( [ 255, 240, 0 ] );
      expect( element.style.borderBottomLeftRadius ).toBe( "3px" );
      expect( element.style.borderBottomRightRadius ).toBe( "3px" );
      expect( element.style.borderTopLeftRadius ).toBe( "3px" );
      expect( element.style.borderTopRightRadius ).toBe( "3px" );
      expect( element.style.borderWidth ).toBe( "2px" );
      expect( element.style.fontSize ).toBe( "6px" );
      expect( element.style.fontFamily ).toBe( "fantasy" );
    } );

    it( "sets min-width to estimated height", function() {
      Badges.setBadge( widget, "bar" );

      var element = Badges._getBadgeElement( widget );
      expect( Math.round( parseInt( element.style.minWidth, 10 ) ) ).toBe( 13 );
    } );

    it( "sets horizontal padding to third of the height", function() {
      Badges.setBadge( widget, "bar" );

      var element = Badges._getBadgeElement( widget );
      expect( Math.round( parseInt( element.style.paddingLeft, 10 ) ) ).toBe( 3 );
      expect( Math.round( parseInt( element.style.paddingRight, 10 ) ) ).toBe( 3 );
    } );

    it( "changes text in an existing badge", function() {
      Badges.setBadge( widget, "bar" );
      var element = Badges._getBadgeElement( widget );
      TestUtil.flush();

      Badges.setBadge( widget, "foo" );

      expect( element.innerHTML ).toBe( "foo" );
    } );

    it( "removes badge for falsy value", function() {
      Badges.setBadge( widget, "bar" );
      var element = Badges._getBadgeElement( widget );
      TestUtil.flush();

      Badges.setBadge( widget, "" );

      expect( element.parentNode ).toBe( null );
    } );

    it( "re-uses badge elements", function() {
      TestUtil.flush();
      Badges.setBadge( widget, "bar" );
      var element = Badges._getBadgeElement( widget );

      Badges.setBadge( widget, "" );
      Badges.setBadge( widget, "foo" );

      expect( Badges._getBadgeElement( widget ) ).toBe( element );
      expect( element.parentNode ).toBe( widget.getElement() );
    } );

    it( "calls getBadgePosition from widget", function() {
      TestUtil.flush();
      widget.computeBadgePosition = jasmine.createSpy().andReturn( [ "auto", 4, 3, "auto" ] );

      Badges.setBadge( widget, "bar" );

      var element = Badges._getBadgeElement( widget );
      expect( element.style.top ).toBe( "auto" );
      expect( element.style.right ).toBe( "4px" );
      expect( element.style.bottom ).toBe( "3px" );
      expect( element.style.left ).toBe( "auto" );
      var size = [ element.offsetWidth, element.offsetHeight ];
      expect( widget.computeBadgePosition ).toHaveBeenCalledWith( size );
    } );

    it( "calls getBadgePosition on appear", function() {
      widget.computeBadgePosition = jasmine.createSpy().andReturn( [ "auto", 4, 3, "auto" ] );

      Badges.setBadge( widget, "bar" );
      TestUtil.flush();

      var element = Badges._getBadgeElement( widget );
      expect( element.style.top ).toBe( "auto" );
      expect( element.style.right ).toBe( "4px" );
      expect( element.style.bottom ).toBe( "3px" );
      expect( element.style.left ).toBe( "auto" );
      var size = [ element.offsetWidth, element.offsetHeight ];
      expect( widget.computeBadgePosition ).toHaveBeenCalledWith( size );
    } );

    it( "calls getBadgePosition on updateBadgePosition job", function() {
      TestUtil.flush();
      Badges.setBadge( widget, "bar" );
      widget.computeBadgePosition = jasmine.createSpy().andReturn( [ "auto", 4, 3, "auto" ] );

      widget.createDispatchDataEvent( "flush", {
        updateBadgePosition : true
      } );

      var element = Badges._getBadgeElement( widget );
      expect( element.style.top ).toBe( "auto" );
      expect( element.style.right ).toBe( "4px" );
      expect( element.style.bottom ).toBe( "3px" );
      expect( element.style.left ).toBe( "auto" );
      var size = [ element.offsetWidth, element.offsetHeight ];
      expect( widget.computeBadgePosition ).toHaveBeenCalledWith( size );
    } );

  } );

} );
