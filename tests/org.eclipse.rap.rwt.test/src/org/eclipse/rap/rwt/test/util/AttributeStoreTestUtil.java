/*******************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.test.util;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.service.ApplicationContext;
import org.eclipse.rap.rwt.service.UISession;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class AttributeStoreTestUtil {

  private AttributeStoreTestUtil() {
  }

  public static UISession mockUISessionWithAttributeStore() {
    UISession uiSession = mock( UISession.class );
    fakeAttributeStore( uiSession );
    return uiSession;
  }

  public static ApplicationContext mockApplicationContextWithAttributeStore() {
    ApplicationContext applicationContext = mock( ApplicationContext.class );
    fakeAttributeStore( applicationContext );
    return applicationContext;
  }

  public static void fakeAttributeStore( UISession mock ) {
    StubbedAttributeStore store = new StubbedAttributeStore();
    Mockito.doAnswer( store.setAttribute ).when( mock ).setAttribute( anyString(), any() );
    Mockito.doAnswer( store.getAttribute ).when( mock ).getAttribute( anyString() );
    Mockito.doAnswer( store.removeAttribute ).when( mock ).removeAttribute( anyString() );
  }

  public static void fakeAttributeStore( ApplicationContext mock ) {
    StubbedAttributeStore store = new StubbedAttributeStore();
    Mockito.doAnswer( store.setAttribute ).when( mock ).setAttribute( anyString(), any() );
    Mockito.doAnswer( store.getAttribute ).when( mock ).getAttribute( anyString() );
    Mockito.doAnswer( store.removeAttribute ).when( mock ).removeAttribute( anyString() );
  }

  private static class StubbedAttributeStore {

    private final Map<String, Object> map = new HashMap<String, Object>();

    private final Answer setAttribute = new Answer() {
      public Object answer( InvocationOnMock invocation ) throws Throwable {
        Object[] arguments = invocation.getArguments();
        map.put( ( String )arguments[ 0 ], arguments[ 1 ] );
        return Boolean.TRUE;
      }
    };

    private final Answer getAttribute = new Answer() {
      public Object answer( InvocationOnMock invocation ) throws Throwable {
        Object[] arguments = invocation.getArguments();
        return map.get( arguments[ 0 ] );
      }
    };

    private final Answer removeAttribute = new Answer() {
      public Object answer( InvocationOnMock invocation ) throws Throwable {
        Object[] arguments = invocation.getArguments();
        return map.remove( arguments[ 0 ] );
      }
    };

  }

}
