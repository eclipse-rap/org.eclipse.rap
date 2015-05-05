/*******************************************************************************
 * Copyright (c) 2008, 2015 Innoopract Informationssysteme GmbH and others.
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
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.theme.CssColor;
import org.eclipse.rap.rwt.internal.theme.CssValue;
import org.eclipse.rap.rwt.internal.theme.WidgetMatcher;
import org.eclipse.rap.rwt.internal.theme.WidgetMatcher.Constraint;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class WidgetMatcher_Test {

  private static final CssColor RED = CssColor.valueOf( "red" );
  private static final CssColor BLUE = CssColor.valueOf( "blue" );
  private static final CssColor GREEN = CssColor.valueOf( "green" );

  private static final Constraint ALWAYS_TRUE = new Constraint() {
    @Override
    public boolean matches( Widget widget ) {
      return true;
    }
  };
  private static final Constraint ALWAYS_FALSE = new Constraint() {
    @Override
    public boolean matches( Widget widget ) {
      return false;
    }
  };

  @Rule
  public TestContext context = new TestContext();

  private Shell shell;
  private WidgetMatcher matcher;

  @Before
  public void setUp() {
    Display display = new Display();
    shell = new Shell( display );
    matcher = new WidgetMatcher();
  }

  @Test
  public void testSelect_withoutValues() {
    Widget widget = new Button( shell, SWT.PUSH );

    CssValue result = matcher.select( widget );

    assertNull( result );
  }

  @Test
  public void testSelect_withStyle_match() {
    Widget widget = new Button( shell, SWT.PUSH );
    matcher.addStyle( "PUSH", SWT.PUSH );
    ConditionalValue[] values = { new ConditionalValue( RED, "[PUSH" ) };

    CssValue result = matcher.select( widget, values );

    assertSame( RED, result );
  }

  @Test
  public void testSelect_withStyle_mismatch() {
    Widget widget = new Button( shell, SWT.CHECK );
    matcher.addStyle( "PUSH", SWT.PUSH );
    ConditionalValue[] values = { new ConditionalValue( RED, "[PUSH" ) };

    CssValue result = matcher.select( widget, values );

    assertNull( result );
  }

  @Test
  public void testSelect_withState_callsConstraintWithWidget() {
    Constraint constraint = mock( Constraint.class );
    Widget widget = new Button( shell, SWT.PUSH );
    matcher.addState( "selected", constraint );
    ConditionalValue[] values = { new ConditionalValue( RED, ":selected" ) };

    matcher.select( widget, values );

    verify( constraint ).matches( widget );
  }

  @Test
  public void testSelect_withState_match() {
    Widget widget = new Button( shell, SWT.PUSH );
    matcher.addState( "selected", ALWAYS_TRUE );
    ConditionalValue[] values = { new ConditionalValue( RED, ":selected" ) };

    CssValue result = matcher.select( widget, values );

    assertSame( RED, result );
  }

  @Test
  public void testSelect_withState_mismatch() {
    Widget widget = new Button( shell, SWT.PUSH );
    matcher.addState( "selected", ALWAYS_FALSE );
    ConditionalValue[] values = { new ConditionalValue( RED, ":selected" ) };

    CssValue result = matcher.select( widget, values );

    assertNull( result );
  }

  @Test
  public void testSelect_withVariant_match() {
    Widget widget = new Button( shell, SWT.PUSH );
    widget.setData( RWT.CUSTOM_VARIANT, "special" );
    ConditionalValue[] values = { new ConditionalValue( RED, ".special" ) };

    CssValue result = matcher.select( widget, values );

    assertSame( RED, result );
  }

  @Test
  public void testSelect_withVariant_mismatch() {
    Widget widget = new Button( shell, SWT.PUSH );
    ConditionalValue[] values = { new ConditionalValue( RED, ".special" ) };

    CssValue result = matcher.select( widget, values );

    assertNull( result );
  }

  @Test
  public void testSelect_withCombinations_returnsFirstMatch() {
    matcher.addStyle( "BORDER", SWT.BORDER );
    matcher.addStyle( "PUSH", SWT.PUSH );
    matcher.addState( "selected", ALWAYS_FALSE );
    ConditionalValue[] values = {
      new ConditionalValue( RED, "[BORDER", "[PUSH", ":selected" ),
      new ConditionalValue( BLUE, "[BORDER", "[PUSH" ),
      new ConditionalValue( GREEN, "[BORDER" )
    };
    Widget widget = new Button( shell, SWT.PUSH | SWT.BORDER );

    CssValue result = matcher.select( widget, values );

    assertEquals( BLUE, result );
  }

}
