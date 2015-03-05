/*******************************************************************************
 * Copyright (c) 2013, 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

import org.eclipse.rap.rwt.client.ClientFile;
import org.eclipse.rap.rwt.dnd.ClientFileTransfer;
import org.eclipse.rap.rwt.internal.client.ClientFileImpl;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseId;
import org.eclipse.rap.rwt.internal.serverpush.ServerPushManager;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.rwt.widgets.DialogCallback;
import org.eclipse.rap.rwt.widgets.DialogUtil;
import org.eclipse.rap.rwt.widgets.FileUpload;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.internal.dnd.DNDEvent;
import org.eclipse.swt.internal.widgets.FileUploadRunnable;
import org.eclipse.swt.layout.GridData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings( {
  "restriction", "deprecation"
} )
public class FileDialog_Test {

  private Display display;
  private Shell shell;
  private FileDialog dialog;
  private DialogCallback callback;
  private ThreadPoolExecutor singleThreadExecutor;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    display = new Display();
    shell = new Shell( display );
    singleThreadExecutor = mock( ThreadPoolExecutor.class );
    dialog = new TestFileDialog( shell, SWT.MULTI );
    callback = mock( DialogCallback.class );
    DialogUtil.open( dialog, callback );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testStyle() {
    dialog = new TestFileDialog( shell );
    int style = dialog.getStyle();

    assertFalse( ( style & SWT.MULTI ) != 0 );
    assertTrue( ( style & SWT.TITLE ) != 0 );
    assertTrue( ( style & SWT.APPLICATION_MODAL ) != 0 );
    assertTrue( ( style & SWT.BORDER ) != 0 );
  }

  @Test
  public void testStyle_multi() {
    dialog = new TestFileDialog( shell, SWT.MULTI );
    int style = dialog.getStyle();

    assertTrue( ( style & SWT.MULTI ) != 0 );
    assertTrue( ( style & SWT.TITLE ) != 0 );
    assertTrue( ( style & SWT.APPLICATION_MODAL ) != 0 );
    assertTrue( ( style & SWT.BORDER ) != 0 );
  }

  @Test
  public void testReturnCode_afterClose() {
    dialog.shell.close();

    verify( callback ).dialogClosed( SWT.CANCEL );
  }

  @Test
  public void testReturnCode_afterOkPressed() {
    getOKButton().notifyListeners( SWT.Selection, null );

    verify( callback ).dialogClosed( SWT.OK );
  }

  @Test
  public void testReturnCode_afterCancelPressed() {
    getCancelButton().notifyListeners( SWT.Selection, null );

    verify( callback ).dialogClosed( SWT.CANCEL );
  }

  @Test
  public void testOpen_activatesServerPush() {
    assertTrue( ServerPushManager.getInstance().isServerPushActive() );
  }

  @Test
  public void testClose_deactivatesServerPush() {
    dialog.shell.close();

    assertFalse( ServerPushManager.getInstance().isServerPushActive() );
  }

  @Test
  public void testClose_shutdownSingleThreadExecutor() {
    dialog.shell.close();

    verify( singleThreadExecutor ).shutdownNow();
  }

  @Test
  public void testClose_deletesUploadedFiles() {
    dialog = spy( new TestFileDialog( shell ) );
    DialogUtil.open( dialog, callback );

    dialog.shell.close();

    verify( dialog ).deleteUploadedFiles( any( String[].class ) );
  }

  @Test
  public void testCancel_deletesUploadedFiles() {
    dialog = spy( new TestFileDialog( shell ) );
    DialogUtil.open( dialog, callback );

    getCancelButton().notifyListeners( SWT.Selection, null );

    verify( dialog ).deleteUploadedFiles( any( String[].class ) );
  }

  @Test
  public void testOK_doesNotDeleteUploadedFiles() {
    dialog = spy( new TestFileDialog( shell ) );
    DialogUtil.open( dialog, callback );

    getOKButton().notifyListeners( SWT.Selection, null );

    verify( dialog, never() ).deleteUploadedFiles( any( String[].class ) );
  }

  @Test
  public void testGetFileName_returnEmptyString() {
    dialog.shell.close();

    assertEquals( "", dialog.getFileName() );
  }

  @Test
  public void testGetFileNames_returnEmptyArray() {
    dialog.shell.close();

    assertEquals( 0, dialog.getFileNames().length );
  }

  @Test
  public void testFileUploadSelection_executesRunnable() {
    getFileUpload().notifyListeners( SWT.Selection, null );

    verify( singleThreadExecutor ).execute( any( FileUploadRunnable.class ) );
  }

  @Test
  public void testFileUploadSelection_hidesCurrentFileUpload_forMulti() {
    FileUpload fileUpload = getFileUpload();

    fileUpload.notifyListeners( SWT.Selection, null );

    assertFalse( fileUpload.isVisible() );
    assertTrue( ( ( GridData )fileUpload.getLayoutData() ).exclude );
  }

  @Test
  public void testFileUploadSelection_createsNewFileUpload_forMulti() {
    FileUpload fileUpload = getFileUpload();

    fileUpload.notifyListeners( SWT.Selection, null );

    assertNotSame( fileUpload, getFileUpload() );
  }

  @Test
  public void testFileUploadSelection_doesNotHideCurrentFileUpload_forSingle() {
    dialog = new TestFileDialog( shell );
    DialogUtil.open( dialog, callback );
    FileUpload fileUpload = getFileUpload();

    fileUpload.notifyListeners( SWT.Selection, null );

    assertTrue( fileUpload.isVisible() );
    assertFalse( ( ( GridData )fileUpload.getLayoutData() ).exclude );
  }

  @Test
  public void testFileUploadSelection_disablesCurrentFileUpload_forSingle() {
    dialog = new TestFileDialog( shell );
    DialogUtil.open( dialog, callback );
    FileUpload fileUpload = getFileUpload();

    fileUpload.notifyListeners( SWT.Selection, null );

    assertFalse( fileUpload.isEnabled() );
  }

  @Test
  public void testFileUploadSelection_doesNotCreateNewFileUpload_forSingle() {
    dialog = new TestFileDialog( shell );
    DialogUtil.open( dialog, callback );
    FileUpload fileUpload = getFileUpload();

    fileUpload.notifyListeners( SWT.Selection, null );

    assertSame( fileUpload, getFileUpload() );
  }

  @Test
  public void testFileDrop_acceptsClientFileTransfer() {
    DNDEvent event = new DNDEvent();
    event.dataType = ClientFileTransfer.getInstance().getSupportedTypes()[ 0 ];
    event.detail = DND.DROP_MOVE;

    getDropTarget().notifyListeners( DND.DropAccept, event );

    assertEquals( DND.DROP_MOVE, event.detail );
  }

  @Test
  public void testFileDrop_rejectsOtherTransfer() {
    DNDEvent event = new DNDEvent();
    event.dataType = FileTransfer.getInstance().getSupportedTypes()[ 0 ];
    event.detail = DND.DROP_MOVE;

    getDropTarget().notifyListeners( DND.DropAccept, event );

    assertEquals( DND.DROP_NONE, event.detail );
  }

  @Test
  public void testFileDrop_executesRunnable() {
    DNDEvent event = new DNDEvent();
    event.data = new ClientFile[] { new ClientFileImpl( "fileId", "", "", 0 ) };

    getDropTarget().notifyListeners( DND.Drop, event );

    verify( singleThreadExecutor ).execute( any( FileUploadRunnable.class ) );
  }

  @Test
  public void testDeleteUploadedFiles() throws IOException {
    File uploadedFile = File.createTempFile( "temp-", null );

    dialog.deleteUploadedFiles( new String[] { uploadedFile.getAbsolutePath() } );

    assertFalse( uploadedFile.exists() );
  }

  private DropTarget getDropTarget() {
    Composite dropControl = ( Composite )dialog.shell.getChildren()[ 0 ];
    return ( DropTarget )dropControl.getData( DND.DROP_TARGET_KEY );
  }

  private FileUpload getFileUpload() {
    Composite buttonsArea = ( Composite )dialog.shell.getChildren()[ 1 ];
    return ( FileUpload )buttonsArea.getChildren()[ 0 ];
  }

  private Button getOKButton() {
    Composite buttonsArea = ( Composite )dialog.shell.getChildren()[ 1 ];
    return ( Button )buttonsArea.getChildren()[ 2 ];
  }

  private Button getCancelButton() {
    Composite buttonsArea = ( Composite )dialog.shell.getChildren()[ 1 ];
    return ( Button )buttonsArea.getChildren()[ 3 ];
  }

  private class TestFileDialog extends FileDialog {

    public TestFileDialog( Shell shell ) {
      super( shell );
    }

    public TestFileDialog( Shell parent, int style ) {
      super( parent, style );
    }

    @Override
    ThreadPoolExecutor createSingleThreadExecutor() {
      return singleThreadExecutor;
    }

  }

}
