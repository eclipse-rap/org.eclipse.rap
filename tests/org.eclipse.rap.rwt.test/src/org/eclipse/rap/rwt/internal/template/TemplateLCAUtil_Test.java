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
package org.eclipse.rap.rwt.internal.template;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.eclipse.rap.json.JsonArray;
import org.eclipse.rap.json.JsonValue;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectImpl;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectRegistry;
import org.eclipse.rap.rwt.internal.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.template.Template;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TemplateLCAUtil_Test {

  private Shell shell;
  private Template template;
  private RemoteObjectImpl remoteObject;

  @Before
  public void setUp() {
    Fixture.setUp();
    Display display = new Display();
    shell = new Shell( display );
    template = new Template();
    remoteObject = fakeRemoteObject();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testRenderRowTemplate() {
    shell.setData( RWT.ROW_TEMPLATE, template );

    TemplateLCAUtil.renderRowTemplate( shell );

    verify( remoteObject ).set( eq( "rowTemplate" ), eq( new JsonArray() ) );
  }

  @Test
  public void testRenderRowTemplate_omitsUnknownTypes() {
    shell.setData( RWT.ROW_TEMPLATE, new Object() );

    TemplateLCAUtil.renderRowTemplate( shell );

    verify( remoteObject, never() ).set( eq( "rowTemplate" ), any( JsonValue.class ) );
  }

  @Test
  public void testRenderRowTemplate_omitsWidgetsWithoutRowTemplate() {
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
