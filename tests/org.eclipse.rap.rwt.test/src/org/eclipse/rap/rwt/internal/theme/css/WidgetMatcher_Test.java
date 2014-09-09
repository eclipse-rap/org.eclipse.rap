/*******************************************************************************
 * Copyright (c) 2008, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme.css;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.theme.CssBorder;
import org.eclipse.rap.rwt.internal.theme.CssColor;
import org.eclipse.rap.rwt.internal.theme.CssType;
import org.eclipse.rap.rwt.internal.theme.WidgetMatcher;
import org.eclipse.rap.rwt.internal.theme.WidgetMatcher.Constraint;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class WidgetMatcher_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testWidgetMatcher() {
    // Create matcher for a certain widget instance
    WidgetMatcher matcher = new WidgetMatcher();
    matcher.addStyle( "BORDER", SWT.BORDER );
    matcher.addStyle( "PUSH", SWT.PUSH );
    matcher.addStyle( "TOGGLE", SWT.TOGGLE );
    matcher.addState( "selected", new Constraint() {

      public boolean matches( Widget widget ) {
        Button button = ( Button )widget;
        return button.getSelection();
      }
    } );

    // Get set of conditional results
    // rule 1
    ConditionalValue value1 = new ConditionalValue(
      new String[] { "[BORDER", "[TOGGLE", ":selected" },
      CssBorder.create( 2, "solid", CssColor.valueOf( "red" ) ) );
    // rule 2
    ConditionalValue value2 = new ConditionalValue(
      new String[] { "[BORDER", "[TOGGLE" },
      CssBorder.create( 2, "dotted", CssColor.valueOf( "blue" ) ) );
    // rule 3
    ConditionalValue value3 = new ConditionalValue(
      new String[] { ".special" },
      CssBorder.create( 1, "solid", CssColor.valueOf( "green" ) ) );
    ConditionalValue[] values = new ConditionalValue[] { value1, value2, value3 };

    // Test matcher with example widgets
    Display display = new Display();
    Shell shell = new Shell( display );

    // A button that matches none of the rules
    Widget button1 = new Button( shell, SWT.TOGGLE );
    CssType result = matcher.select( values, button1 );
    assertNull( result );

    // A button that matches rule 2
    Button button2 = new Button( shell, SWT.TOGGLE | SWT.BORDER );
    result = matcher.select( values, button2 );
    assertEquals( value2.value, result );

    // now matches rule 1 and rule 2, but 1 takes precedence
    button2.setSelection( true );
    result = matcher.select( values, button2 );
    assertEquals( value1.value, result );

    // now matches all three rules, still 1 takes precedence
    button2.setData( RWT.CUSTOM_VARIANT, "special" );
    result = matcher.select( values, button2 );
    assertEquals( value1.value, result );

    // A button that only matches rule 3
    Button button3 = new Button( shell, SWT.TOGGLE );
    button3.setData( RWT.CUSTOM_VARIANT, "special" );
    result = matcher.select( values, button3 );
    assertEquals( value3.value, result );

    // After this change it does not match anymore
    button3.setData( RWT.CUSTOM_VARIANT, "other" );
    result = matcher.select( values, button3 );
    assertNull( result );
  }

}
