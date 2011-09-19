/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others.
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.internal.widgets.IDialogAdapter;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.widgets.DialogCallback;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;


public class Dialog_Test extends TestCase {

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

  private Display display;
  private Shell shell;

  protected void setUp() throws Exception {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
  }

  protected void tearDown() throws Exception {
    Fixture.tearDown();
  }
  
  public void testConstructorWithNullParent() {
    try {
      new TestDialog( null );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertEquals( "Argument cannot be null", expected.getMessage() );
    }
  }

  public void testDefaults() {
    Dialog dialog = new TestDialog( shell );
    assertSame( shell, dialog.getParent() );
    assertEquals( "", dialog.getText() );
    assertEquals( SWT.PRIMARY_MODAL, dialog.getStyle() );
  }
  
  public void testStyleApplicationModal() {
    Dialog dialog = new TestDialog( shell, SWT.APPLICATION_MODAL );
    assertTrue( ( dialog.getStyle() & SWT.APPLICATION_MODAL )!= 0 );
  }

  public void testStylePrimaryModal() {
    Dialog dialog = new TestDialog( shell, SWT.PRIMARY_MODAL );
    assertTrue( ( dialog.getStyle() & SWT.PRIMARY_MODAL )!= 0 );
  }
  
  public void testStyleSystemModal() {
    Dialog dialog = new TestDialog( shell, SWT.SYSTEM_MODAL );
    assertTrue( ( dialog.getStyle() & SWT.SYSTEM_MODAL )!= 0 );
  }
  
  public void testSetText() {
    Dialog dialog = new TestDialog( shell );
    dialog.setText( "Test" );
    assertEquals( "Test", dialog.getText() );
  }

  public void testSetTextWithNullArgument() {
    Dialog dialog = new TestDialog( shell );
    try {
      dialog.setText( null );
      fail();
    } catch( IllegalArgumentException expected ) {
      assertEquals( "Argument cannot be null", expected.getMessage() );
    }
  }
  
  public void testConvertHorizontalDLUsToPixels() {
    int pixels = Dialog.convertHorizontalDLUsToPixels( shell, 10 );
    assertTrue( pixels >= 10 );
  }

  public void testConvertHorizontalDLUsToPixelsWithDifferentFonts() {
    shell.setFont( new Font( display, "roman", 10, SWT.NORMAL ) );
    int smallPixels = Dialog.convertHorizontalDLUsToPixels( shell, 10 );
    shell.setFont( new Font( display, "roman", 22, SWT.NORMAL ) );
    int largePixels = Dialog.convertHorizontalDLUsToPixels( shell, 10 );
    assertTrue( smallPixels < largePixels );
  }
  
  public void testIsSerializable() throws Exception {
    String text = "text";
    TestDialog dialog = new TestDialog( shell );
    dialog.setText( text );
    IDialogAdapter adapter = ( IDialogAdapter )dialog.getAdapter( IDialogAdapter.class );
    adapter.openNonBlocking( mock( DialogCallback.class ) );
    
    TestDialog deserializedDialog = Fixture.serializeAndDeserialize( dialog );
    
    assertEquals( text, deserializedDialog.getText() );
  }
  
  public void testGetAdapter() {
    Dialog dialog = new TestDialog( shell );
    
    Object adapter = dialog.getAdapter( IDialogAdapter.class );
    
    assertTrue( adapter instanceof IDialogAdapter );
  }
  
  public void testNonBlockingDialogWithDefaultReturnCode() {
    Dialog dialog = new TestDialog( shell );
    IDialogAdapter adapter = ( IDialogAdapter )dialog.getAdapter( IDialogAdapter.class );
    DialogCallback dialogCallback = mock( DialogCallback.class );
    
    adapter.openNonBlocking( dialogCallback );
    dialog.shell.close();
    
    verify( dialogCallback ).dialogClosed( SWT.CANCEL );
  }

  public void testNonBlockingDialogWithCustomReturnCode() {
    Dialog dialog = new TestDialog( shell );
    IDialogAdapter adapter = ( IDialogAdapter )dialog.getAdapter( IDialogAdapter.class );
    DialogCallback dialogCallback = mock( DialogCallback.class );
    
    adapter.openNonBlocking( dialogCallback );
    dialog.returnCode = SWT.OK;
    dialog.shell.close();
    
    verify( dialogCallback ).dialogClosed( SWT.OK );
  }
}
