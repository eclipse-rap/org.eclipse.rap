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
package org.eclipse.swt.internal.widgets;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.rap.rwt.client.Client;
import org.eclipse.rap.rwt.internal.client.WidgetDataWhiteList;
import org.eclipse.rap.rwt.testfixture.Fixture;


public class WidgetDataUtil {

  public static void fakeWidgetDataWhiteList( String[] keys ) {
    WidgetDataWhiteList service = mock( WidgetDataWhiteList.class );
    when( service.getKeys() ).thenReturn( keys );
    Client client = mock( Client.class );
    when( client.getService( WidgetDataWhiteList.class ) ).thenReturn( service );
    Fixture.fakeClient( client );
  }

  private WidgetDataUtil() {
    // prevent instantiation
  }

}
