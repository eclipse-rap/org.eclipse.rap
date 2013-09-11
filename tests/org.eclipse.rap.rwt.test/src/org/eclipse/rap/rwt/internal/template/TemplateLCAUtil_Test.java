/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.template;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectImpl;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TemplateLCAUtil_Test {

  private Shell shell;

  @Before
  public void setUp() {
    Fixture.setUp();
    Display display = new Display();
    shell = new Shell( display );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testRenderRowTemplate() {
    RowTemplate rowTemplate = new RowTemplate();
    shell.setData( RowTemplate.ROW_TEMPLATE, rowTemplate );
    JsonValue template = new TemplateSerializer( rowTemplate ).toJson();
    RemoteObjectImpl remoteObject = fakeRemoteObject();

    TemplateLCAUtil.renderRowTemplate( shell );

    verify( remoteObject ).set( eq( "rowTemplate" ), eq( template ) );
  }

  @Test
  public void testRenderRowTemplateOnlyIfItsARowTemplate() {
    shell.setData( RowTemplate.ROW_TEMPLATE, new Object() );
    RemoteObjectImpl remoteObject = fakeRemoteObject();

    TemplateLCAUtil.renderRowTemplate( shell );

    verify( remoteObject, never() ).set( eq( "rowTemplate" ), any( JsonValue.class ) );
  }

  @Test
  public void testRenderRowTemplateOnlyIfPresent() {
    RemoteObjectImpl remoteObject = fakeRemoteObject();

    TemplateLCAUtil.renderRowTemplate( shell );

    verify( remoteObject, never() ).set( eq( "rowTemplate" ), any( JsonValue.class ) );
  }

  private RemoteObjectImpl fakeRemoteObject() {
    RemoteObjectImpl remoteObject = mock( RemoteObjectImpl.class );
    when( remoteObject.getId() ).thenReturn( WidgetUtil.getId( shell ) );
    RemoteObjectRegistry.getInstance().register( remoteObject );
    return remoteObject;
  }
}
