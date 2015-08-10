/*******************************************************************************
 * Copyright (c) 2011, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.util;

import static org.eclipse.rap.rwt.testfixture.internal.Fixture.getProtocolMessage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assume.assumeTrue;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.lifecycle.DisplayUtil;
import org.eclipse.rap.rwt.internal.lifecycle.RemoteAdapter;
import org.eclipse.rap.rwt.internal.protocol.Operation.SetOperation;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.TestMessage;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


@RunWith( value = Parameterized.class )
public class ActiveKeysUtil_Test {

  private Display display;
  private Control control;
  private final boolean useDisplay;
  private final boolean useActiveKeys;
  private final String property;

  @Parameters( name = "{0} {1}" )
  public static Collection<Object[]> getParameters() {
    return Arrays.asList( new Object[][] {
      { "control", "activeKeys" },
      { "display", "activeKeys" },
      { "display", "cancelKeys" },
      { "control", "cancelKeys" } } );
  }

  public ActiveKeysUtil_Test( String target, String property ) {
    useDisplay = "display".equals( target );
    useActiveKeys = "activeKeys".equals( property );
    this.property = property;
  }

  @Rule
  public TestContext context = new TestContext();

  @Before
  public void setUp() {
    display = new Display();
    control = new Shell( display );
  }

  @Test
  public void testRender_initial() {
    render();

    assertNull( findSetProperty() );
  }

  @Test
  public void testRender_unchanged() {
    markInitialized();
    setData( new String[] { "CTRL+A" } );

    preserve();
    render();

    assertNull( findSetProperty() );
  }

  @Test
  public void testRender_changed() {
    markInitialized();

    preserve();
    setData( new String[] { "CTRL+A" } );
    render();

    assertEquals( new JsonArray().add( "CTRL+#65" ), findSetProperty() );
  }

  @Test
  public void testRender_translatesCorrectly() {
    setData( new String[] {
      "x",
      "ALT+x",
      "E",
      "CTRL+INSERT",
      "CTRL+E",
      "SHIFT+CTRL+ALT+1",
      "CTRL+ALT+E",
      "F1",
      "/",
      "SHIFT+~",
      "CTRL+ALT+#",
      ".",
      ","
    } );

    render();

    assertEquals( new JsonArray()
      .add( "#88" )
      .add( "ALT+#88" )
      .add( "#69" )
      .add( "CTRL+#45" )
      .add( "CTRL+#69" )
      .add( "ALT+CTRL+SHIFT+#49" )
      .add( "ALT+CTRL+#69" )
      .add( "#112" )
      .add( "/" )
      .add( "SHIFT+~" )
      .add( "ALT+CTRL+#" )
      .add( "." )
      .add( "," ), findSetProperty() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRender_unrecognizedKey() {
    setData( new String[] { "ALT+ABC" } );

    render();
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRender_modifiersOnly() {
    setData( new String[] { "ALT+CTRL+" } );

    render();
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRender_unrecognizedModifier() {
    setData( new String[] { "ALT+CONTROL+A" } );

    render();
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRender_emptyKeyBinding() {
    setData( new String[] { "CTRL+A", "", "ALT+INSERT" } );

    render();
  }

  @Test( expected = NullPointerException.class )
  public void testRender_nullKey() {
    setData( new String[] { "CTRL+A", null, "ALT+INSERT" } );

    render();
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRender_invalidActiveKeysListClass() {
    setData( new Integer( 123 ) );

    render();
  }

  @Test
  public void testRender_afterReset() {
    markInitialized();
    setData( new String[] { "CTRL+E" } );

    preserve();
    setData( null );
    render();

    assertEquals( new JsonArray(), findSetProperty() );
  }

  @Test
  public void testRender_changedToEmptyArray() {
    markInitialized();
    setData( new String[] { "CTRL+E" } );

    preserve();
    setData( new String[ 0 ] );
    render();

    assertEquals( new JsonArray(), findSetProperty() );
  }

  @Test
  public void testRender_plusKey() {
    setData( new String[] { "+" } );

    render();

    assertEquals( new JsonArray().add( "+" ), findSetProperty() );
  }

  /*
   * 438277: IllegalArgumentException when using CTRL + (ZoomIn) as key binding
   * https://bugs.eclipse.org/bugs/show_bug.cgi?id=438277
   */
  @Test
  public void testRender_plusKeyWithModifiers() {
    setData( new String[] { "CTRL++" } );

    render();

    assertEquals( new JsonArray().add( "CTRL++" ), findSetProperty() );
  }

  @Test
  public void testPreserveMnemonicActivator() {
    assumeTrue( useDisplay && useActiveKeys ); // prevent running this test for every parameter
    Fixture.markInitialized( display );
    RemoteAdapter adapter = DisplayUtil.getAdapter( display );

    display.setData( RWT.MNEMONIC_ACTIVATOR, "ALT+CTRL" );
    Fixture.preserveWidgets();

    String preserved = ( String )adapter.getPreserved( ActiveKeysUtil.PROP_MNEMONIC_ACTIVATOR );
    assertEquals( "ALT+CTRL+", preserved );
  }

  @Test
  public void testRenderMnemonicActivator() {
    assumeTrue( useDisplay && useActiveKeys ); // prevent running this test for every parameter
    display.setData( RWT.MNEMONIC_ACTIVATOR, "ALT+CTRL" );

    ActiveKeysUtil.renderMnemonicActivator( display );

    TestMessage message = Fixture.getProtocolMessage();
    assertEquals( "ALT+CTRL+", message.findSetProperty( "w1", "mnemonicActivator" ).asString() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRenderMnemonicActivator_notString() {
    assumeTrue( useDisplay && useActiveKeys ); // prevent running this test for every parameter
    display.setData( RWT.MNEMONIC_ACTIVATOR, Boolean.TRUE );

    ActiveKeysUtil.renderMnemonicActivator( display );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testRenderMnemonicActivator_notOnlyModifiers() {
    assumeTrue( useDisplay && useActiveKeys ); // prevent running this test for every parameter
    display.setData( RWT.MNEMONIC_ACTIVATOR, "ALT+CTRL+1" );

    ActiveKeysUtil.renderMnemonicActivator( display );
  }

  private void markInitialized() {
    if( useDisplay ) {
      Fixture.markInitialized( display );
    } else {
      Fixture.markInitialized( control );
    }
  }

  private void setData( Object value ) {
    String key = useActiveKeys ? RWT.ACTIVE_KEYS : RWT.CANCEL_KEYS;
    if( useDisplay ) {
      display.setData( key, value );
    } else {
      control.setData( key, value );
    }
  }

  private JsonValue findSetProperty() {
    TestMessage message = getProtocolMessage();
    SetOperation operation = useDisplay ? message.findSetOperation( "w1", property )
                                        : message.findSetOperation( control, property );
    return operation == null ? null : operation.getProperties().get( property );
  }

  private void preserve() {
    if( useDisplay ) {
      if( useActiveKeys ) {
        ActiveKeysUtil.preserveActiveKeys( display );
      } else {
        ActiveKeysUtil.preserveCancelKeys( display );
      }
    } else {
      Fixture.clearPreserved();
    }
  }

  private void render() {
    if( useDisplay ) {
      if( useActiveKeys ) {
        ActiveKeysUtil.renderActiveKeys( display );
      } else {
        ActiveKeysUtil.renderCancelKeys( display );
      }
    } else {
      if( useActiveKeys ) {
        ActiveKeysUtil.renderActiveKeys( control );
      } else {
        ActiveKeysUtil.renderCancelKeys( control );
      }
    }
  }

}
