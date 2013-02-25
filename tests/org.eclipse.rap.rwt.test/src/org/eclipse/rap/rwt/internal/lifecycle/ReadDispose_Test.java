/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import static org.eclipse.rap.rwt.internal.service.ContextProvider.getApplicationContext;

import org.eclipse.rap.rwt.application.EntryPoint;
import org.eclipse.rap.rwt.internal.application.ApplicationContextImpl;
import org.eclipse.rap.rwt.internal.protocol.ClientMessageConst;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ReadDispose_Test {

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakeResponseWriter();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  // see bug 195735: Widget disposal causes NullPointerException
  // https://bugs.eclipse.org/bugs/show_bug.cgi?id=195735
  @Test
  public void testWidgetDisposal() throws Exception {
    // Run requests to initialize the 'system'
    Fixture.fakeNewRequest();
    ApplicationContextImpl applicationContext = getApplicationContext();
    applicationContext.getEntryPointManager().register( TestRequest.DEFAULT_SERVLET_PATH,
                                                        WidgetDisposalEntryPoint.class,
                                                        null );
    RWTLifeCycle lifeCycle = ( RWTLifeCycle )applicationContext.getLifeCycleFactory().getLifeCycle();
    lifeCycle.execute();
    Fixture.fakeNewRequest();
    lifeCycle.execute();
    Fixture.fakeNewRequest();
    String buttonId = WidgetDisposalEntryPoint.buttonId;
    Fixture.fakeNotifyOperation( buttonId, ClientMessageConst.EVENT_SELECTION, null );
    lifeCycle.execute();
  }

  private static class WidgetDisposalEntryPoint implements EntryPoint {
    private static String buttonId;

    public int createUI() {
      Display display = new Display();
      Shell shell = new Shell( display, SWT.NONE );
      final Text text = new Text( shell, SWT.MULTI );
      final Tree tree = new Tree( shell, SWT.SINGLE );
      for( int i = 0; i < 5; i++ ) {
        TreeItem item = new TreeItem( tree, SWT.NONE );
        item.setText( "foo" + i );
      }
      Button button = new Button( shell, SWT.PUSH );
      button.addSelectionListener( new SelectionAdapter() {
        @Override
        public void widgetSelected( SelectionEvent event ) {
          text.dispose();
          tree.dispose();
        }
      } );
      buttonId = WidgetUtil.getId( button );
      int count = 0;
      while( count  < 2 ) {
        if( !display.readAndDispatch() ) {
          display.sleep();
        }
        count++;
      }
      return 0;
    }
  }

}
