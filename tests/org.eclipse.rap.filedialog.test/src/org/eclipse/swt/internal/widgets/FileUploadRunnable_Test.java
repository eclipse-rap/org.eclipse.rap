/*******************************************************************************
 * Copyright (c) 2013, 2019 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.rap.fileupload.DiskFileUploadReceiver;
import org.eclipse.rap.fileupload.FileDetails;
import org.eclipse.rap.fileupload.FileUploadEvent;
import org.eclipse.rap.fileupload.FileUploadHandler;
import org.eclipse.rap.fileupload.FileUploadListener;
import org.eclipse.rap.rwt.testfixture.TestContext;
import org.eclipse.swt.internal.widgets.FileUploadRunnable.State;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class FileUploadRunnable_Test {

  private Display display;
  private Shell shell;
  private FileUploadRunnable runnable;
  private UploadPanel uploadPanel;
  private ProgressCollector progressCollector;
  private Uploader uploader;
  private FileUploadHandler handler;

  @Rule public TestContext context = new TestContext();

  @Before
  public void setUp() {
    display = new Display();
    shell = new Shell( display );
    uploadPanel = mock( UploadPanel.class );
    when( uploadPanel.getDisplay() ).thenReturn( display );
    progressCollector = mock( ProgressCollector.class );
    DiskFileUploadReceiver diskFileUploadReceiver = mock( DiskFileUploadReceiver.class );
    when( diskFileUploadReceiver.getTargetFiles() ).thenReturn( new File[ 0 ] );
    uploader = mock( Uploader.class );
    handler = spy( new FileUploadHandler( diskFileUploadReceiver ) );
    runnable = new FileUploadRunnable( uploadPanel, progressCollector, uploader, handler );
  }

  @Test
  public void testCreate_addsUploadListener() {
    verify( handler ).addUploadListener( any( FileUploadListener.class ) );
  }

  @Test
  public void testCreate_updatesUploadPanelIcons() {
    verify( uploadPanel ).updateIcons( State.WAITING );
  }

  @Test
  public void testUploadPanelDispose_removesUploadListener() {
    uploadPanel = new UploadPanel( shell, new String[ 0 ] );
    runnable = new FileUploadRunnable( uploadPanel, progressCollector, uploader, handler );

    uploadPanel.dispose();

    verify( handler ).removeUploadListener( any( FileUploadListener.class ) );
  }

  @Test
  public void testUploadPanelDispose_disposesHandler() {
    uploadPanel = new UploadPanel( shell, new String[ 0 ] );
    runnable = new FileUploadRunnable( uploadPanel, progressCollector, uploader, handler );

    uploadPanel.dispose();

    verify( handler ).dispose();
  }

  @Test
  public void testGetState_initial() {
    assertEquals( State.WAITING, runnable.getState() );
  }

  @Test
  public void testFileUploadEvent_triggersHandleProgress() {
    new TestFileUploadEvent( handler ).dispatchProgress();
    runEventsLoop();

    assertEquals( State.UPLOADING, runnable.getState() );
  }

  @Test
  public void testFileUploadEvent_triggersHandleFinished() {
    new TestFileUploadEvent( handler ).dispatchFinished();
    runEventsLoop();

    assertEquals( State.FINISHED, runnable.getState() );
  }

  @Test
  public void testFileUploadEvent_triggersHandleFailed() {
    new TestFileUploadEvent( handler ).dispatchFailed();
    runEventsLoop();

    assertEquals( State.FAILED, runnable.getState() );
  }

  @Test
  public void testFileUploadEvent_doesNotTriggerHandleProgress_onDisposedDisplay() {
    new TestFileUploadEvent( handler ).dispatchProgress();
    display.dispose();

    assertEquals( State.WAITING, runnable.getState() );
  }

  @Test
  public void testFileUploadEvent_doesNotTriggerHandleFinished_onDisposedDisplay() {
    new TestFileUploadEvent( handler ).dispatchFinished();
    display.dispose();

    assertEquals( State.WAITING, runnable.getState() );
  }

  @Test
  public void testFileUploadEvent_doesNotTriggerHandleFailed_onDisposedDisplay() {
    new TestFileUploadEvent( handler ).dispatchFailed();
    display.dispose();

    assertEquals( State.WAITING, runnable.getState() );
  }

  @Test
  public void testHandleProgress_updatesIcons() {
    runnable.handleProgress( 100, 200 );

    verify( uploadPanel ).updateIcons( State.UPLOADING );
  }

  @Test
  public void testHandleProgress_twice_updatesIconsOnce() {
    runnable.handleProgress( 100, 200 );
    runnable.handleProgress( 150, 200 );

    verify( uploadPanel ).updateIcons( State.UPLOADING );
  }

  @Test
  public void testHandleProgress_updatesProgress() {
    runnable.handleProgress( 100, 200 );

    verify( progressCollector ).updateProgress( 50 );
  }

  @Test
  public void testHandleFinished_updatesIcons() {
    runnable.handleFinished( Collections.EMPTY_LIST );

    verify( uploadPanel ).updateIcons( State.FINISHED );
  }

  @Test
  public void testHandleFinished_updatesCompletedFiles() {
    List<String> completedFiles = new ArrayList<>();
    completedFiles.add( "foo" );
    completedFiles.add( "bar" );

    runnable.handleFinished( completedFiles );

    verify( progressCollector ).updateCompletedFiles( eq( completedFiles ) );
  }

  @Test
  public void testHandleFinished_resetsToolTip() {
    List<String> completedFiles = new ArrayList<>();
    completedFiles.add( "foo" );
    completedFiles.add( "bar" );

    runnable.handleFinished( completedFiles );

    verify( progressCollector ).resetToolTip();
  }

  @Test
  public void testHandleFailed_updatesIcons() {
    runnable.handleFailed( null );

    verify( uploadPanel ).updateIcons( State.FAILED );
  }

  @Test
  public void testHandleFailed_updatesToolTips() {
    Exception exception = mock( Exception.class );

    runnable.handleFailed( exception );

    verify( uploadPanel ).updateTexts( exception );
  }

  @Test
  public void testHandleFailed_resetsToolTip() {
    runnable.handleFailed(null);

    verify( progressCollector ).resetToolTip();
  }

  @Test
  public void testHandleFailed_addsExeption() {
    Exception exception = new Exception();

    runnable.handleFailed(exception);

    verify( progressCollector ).addException( exception );
  }

  @Test
  public void testRun_onDisposedDisplay() {
    display.dispose();

    runnable.run();
  }

  @Test
  public void testRun_callsUploaderSubmit() {
    sheduleFinishedEvent();

    runnable.run();
    runEventsLoop();

    verify( uploader ).submit( anyString() );
  }

  @Test
  public void testRun_disposesUploader() {
    sheduleFinishedEvent();

    runnable.run();
    runEventsLoop();

    verify( uploader ).dispose();
  }

  @Test
  public void testRun_disposeHandler() {
    sheduleFinishedEvent();

    runnable.run();
    runEventsLoop();

    verify( handler ).dispose();
  }

  @Test
  public void testRun_removesUploadListener() {
    sheduleFinishedEvent();

    runnable.run();
    runEventsLoop();

    verify( handler ).removeUploadListener( any( FileUploadListener.class ) );
  }

  private void sheduleFinishedEvent() {
    Thread thread = new Thread( new Runnable() {
      @Override
      public void run() {
        try {
          Thread.sleep( 200 );
        } catch( InterruptedException e ) {
          throw new RuntimeException( "Unexpected interrupt", e );
        }
        new TestFileUploadEvent( handler ).dispatchFinished();
      }
    } );
    thread.start();
  }

  private void runEventsLoop() {
    while( display.readAndDispatch() ) {
    }
  }

  public class TestFileUploadEvent extends FileUploadEvent {

    public TestFileUploadEvent( FileUploadHandler handler ) {
      super( handler );
    }

    private static final long serialVersionUID = 1L;

    @Override
    public FileDetails[] getFileDetails() {
      return new FileDetails[ 0 ];
    }

    @Override
    public long getContentLength() {
      return 0;
    }

    @Override
    public long getBytesRead() {
      return 0;
    }

    @Override
    public Exception getException() {
      return null;
    }

    @Override
    public void dispatchProgress() {
      super.dispatchProgress();
    }

    @Override
    public void dispatchFinished() {
      super.dispatchFinished();
    }

    @Override
    public void dispatchFailed() {
      super.dispatchFailed();
    }

  }

}
