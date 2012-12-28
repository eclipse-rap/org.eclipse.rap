/*******************************************************************************
 * Copyright (c) 2010, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Ralf Zahn (ARS) - initial API and implementation
 *   EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.rwt.internal.widgets.IDialogAdapter;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.widgets.DialogCallback;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class Dialog_Test {

  private Display display;
  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testConstructorWithNullParent() {
    try {
      new TestDialog( null );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertEquals( "Argument cannot be null", expected.getMessage() );
    }
  }

  @Test
  public void testDefaults() {
    Dialog dialog = new TestDialog( shell );
    assertSame( shell, dialog.getParent() );
    assertEquals( "", dialog.getText() );
    assertEquals( SWT.PRIMARY_MODAL, dialog.getStyle() );
  }

  @Test
  public void testStyleApplicationModal() {
    Dialog dialog = new TestDialog( shell, SWT.APPLICATION_MODAL );
    assertTrue( ( dialog.getStyle() & SWT.APPLICATION_MODAL )!= 0 );
  }

  @Test
  public void testStylePrimaryModal() {
    Dialog dialog = new TestDialog( shell, SWT.PRIMARY_MODAL );
    assertTrue( ( dialog.getStyle() & SWT.PRIMARY_MODAL )!= 0 );
  }

  @Test
  public void testStyleSystemModal() {
    Dialog dialog = new TestDialog( shell, SWT.SYSTEM_MODAL );
    assertTrue( ( dialog.getStyle() & SWT.SYSTEM_MODAL )!= 0 );
  }

  @Test
  public void testSetText() {
    Dialog dialog = new TestDialog( shell );
    dialog.setText( "Test" );
    assertEquals( "Test", dialog.getText() );
  }

  @Test
  public void testSetTextWithNullArgument() {
    Dialog dialog = new TestDialog( shell );
    try {
      dialog.setText( null );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertEquals( "Argument cannot be null", expected.getMessage() );
    }
  }

  @Test
  public void testConvertHorizontalDLUsToPixels() {
    int pixels = Dialog.convertHorizontalDLUsToPixels( shell, 10 );
    assertTrue( pixels >= 10 );
  }

  @Test
  public void testConvertHorizontalDLUsToPixelsWithDifferentFonts() {
    shell.setFont( new Font( display, "roman", 10, SWT.NORMAL ) );
    int smallPixels = Dialog.convertHorizontalDLUsToPixels( shell, 10 );
    shell.setFont( new Font( display, "roman", 22, SWT.NORMAL ) );
    int largePixels = Dialog.convertHorizontalDLUsToPixels( shell, 10 );
    assertTrue( smallPixels < largePixels );
  }

  @Test
  public void testIsSerializable() throws Exception {
    String text = "text";
    TestDialog dialog = new TestDialog( shell );
    dialog.setText( text );
    IDialogAdapter adapter = dialog.getAdapter( IDialogAdapter.class );
    adapter.openNonBlocking( mock( DialogCallback.class ) );

    TestDialog deserializedDialog = Fixture.serializeAndDeserialize( dialog );

    assertEquals( text, deserializedDialog.getText() );
  }

  @Test
  public void testGetAdapter() {
    Dialog dialog = new TestDialog( shell );

    Object adapter = dialog.getAdapter( IDialogAdapter.class );

    assertTrue( adapter instanceof IDialogAdapter );
  }

  @Test
  public void testNonBlockingDialogWithDefaultReturnCode() {
    Dialog dialog = new TestDialog( shell );
    IDialogAdapter adapter = dialog.getAdapter( IDialogAdapter.class );
    DialogCallback dialogCallback = mock( DialogCallback.class );

    adapter.openNonBlocking( dialogCallback );
    dialog.shell.close();

    verify( dialogCallback ).dialogClosed( SWT.CANCEL );
  }

  @Test
  public void testNonBlockingDialogWithCustomReturnCode() {
    Dialog dialog = new TestDialog( shell );
    IDialogAdapter adapter = dialog.getAdapter( IDialogAdapter.class );
    DialogCallback dialogCallback = mock( DialogCallback.class );

    adapter.openNonBlocking( dialogCallback );
    dialog.returnCode = SWT.OK;
    dialog.shell.close();

    verify( dialogCallback ).dialogClosed( SWT.OK );
  }

  private static class TestDialog extends Dialog {
    private TestDialog( Shell parent ) {
      super( parent );
    }

    private TestDialog( Shell parent, int style ) {
      super( parent, style );
    }

    @Override
    protected void prepareOpen() {
      shell = new Shell( parent );
    }
  }

}
