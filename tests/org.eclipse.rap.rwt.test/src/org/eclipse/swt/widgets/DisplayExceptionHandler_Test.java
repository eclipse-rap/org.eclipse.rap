/*******************************************************************************
 * Copyright (c) 2013 Rüdiger Herrmann and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.widgets;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import junit.framework.TestCase;

import org.eclipse.rap.rwt.application.ExceptionHandler;
import org.eclipse.rap.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.ProcessActionRunner;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.events.EventList;
import org.junit.After;
import org.junit.Before;


public class DisplayExceptionHandler_Test extends TestCase {
  
  private ExceptionHandler exceptionHandler;
  private Display display;
  private Shell shell;
  
  public void testRuntimeExceptionInListenerWithExceptionHandler() {
    ApplicationContextUtil.getInstance().setExceptionHandler( exceptionHandler );
    RuntimeException exception = new RuntimeException();
    addMaliciousListener( SWT.Resize, exception );
    generateEvent( shell, SWT.Resize );
    
    display.readAndDispatch();
    
    verify( exceptionHandler ).handleException( exception );
  }

  public void testRuntimeExceptionInListenerWithoutExceptionHandler() {
    RuntimeException exception = new RuntimeException();
    addMaliciousListener( SWT.Resize, exception );
    generateEvent( shell, SWT.Resize );
    
    try {
      display.readAndDispatch();
      fail();
    } catch( RuntimeException expected ) {
      assertSame( exception, expected );
    }
  }
  
  public void testErrorInListenerWithExceptionHandler() {
    ApplicationContextUtil.getInstance().setExceptionHandler( exceptionHandler );
    Error error = new Error();
    addMaliciousListener( SWT.Resize, error );
    generateEvent( shell, SWT.Resize );
    
    try {
      display.readAndDispatch();
      fail();
    } catch( Error expected ) {
      assertSame( error, expected );
    }

    verify( exceptionHandler ).handleException( error );
  }

  public void testErrorInListenerWithoutExceptionHandler() {
    Error error = new Error();
    addMaliciousListener( SWT.Resize, error );
    generateEvent( shell, SWT.Resize );
    
    try {
      display.readAndDispatch();
      fail();
    } catch( Error expected ) {
      assertSame( error, expected );
    }
  }
  
  public void testExceptionInExceptionHandler() {
    ApplicationContextUtil.getInstance().setExceptionHandler( exceptionHandler );
    RuntimeException exceptionInHandler = new RuntimeException();
    doThrow( exceptionInHandler ).when( exceptionHandler ).handleException( any( Throwable.class ) );
    addMaliciousListener( SWT.Resize, exceptionInHandler );
    generateEvent( shell, SWT.Resize );
    
    try {
      display.readAndDispatch();
      fail();
    } catch( Exception expected ) {
      assertSame( exceptionInHandler, expected );
    }
  }
  
  public void testReSkinningIsRunWithinExceptionHandler() {
    ApplicationContextUtil.getInstance().setExceptionHandler( exceptionHandler );
    RuntimeException exception = new RuntimeException();
    Listener listener = mock( Listener.class );
    doThrow( exception ).when( listener ).handleEvent( any( Event.class ) );
    display.addListener( SWT.Skin, listener );
    display.addSkinnableWidget( shell );
    
    display.readAndDispatch();
    
    verify( exceptionHandler ).handleException( exception );
  }
  
  public void testDeferredLayoutIsRunWithinExceptionHandler() {
    shell = spy( shell );
    ApplicationContextUtil.getInstance().setExceptionHandler( exceptionHandler );
    RuntimeException exception = new RuntimeException();
    doThrow( exception ).when( shell ).setLayoutDeferred( anyBoolean() );
    display.addLayoutDeferred( shell );

    display.readAndDispatch();
    
    verify( exceptionHandler ).handleException( exception );
  }
  
  public void testProcessActionRunnableIsRunWithinExceptionHandler() {
    ApplicationContextUtil.getInstance().setExceptionHandler( exceptionHandler );
    RuntimeException exception = new RuntimeException();
    Runnable runnable = mock( Runnable.class );
    doThrow( exception ).when( runnable ).run();
    addProcessActionRunnable( runnable );

    display.readAndDispatch();
    
    verify( exceptionHandler ).handleException( exception );
  }

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
    exceptionHandler = mock( ExceptionHandler.class );
    display = new Display();
    shell = new Shell( display );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  private void addMaliciousListener( int eventType, Throwable throwable ) {
    Listener listener = mock( Listener.class );
    doThrow( throwable ).when( listener ).handleEvent( any( Event.class ) );
    shell.addListener( eventType, listener );
  }

  private void generateEvent( Widget widget, int eventType ) {
    Event event = new Event();
    event.type = eventType;
    event.widget = widget;
    EventList.getInstance().add( event );
  }

  private static void addProcessActionRunnable( Runnable runnable ) {
    Fixture.fakePhase( PhaseId.READ_DATA );
    ProcessActionRunner.add( runnable );
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

}
