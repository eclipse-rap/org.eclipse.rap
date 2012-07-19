/*******************************************************************************
 * Copyright (c) 2010, 2011 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import java.lang.reflect.*;

import javax.servlet.http.*;

import org.eclipse.rap.rwt.internal.service.*;
import org.eclipse.rap.rwt.internal.util.ClassUtil;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.internal.widgets.IDisplayAdapter;
import org.eclipse.swt.widgets.Display;


public final class FakeContextUtil {
  
  private static final ClassLoader CLASS_LOADER = FakeContextUtil.class.getClassLoader();
  private static final HttpServletResponse RESPONSE_PROXY = newResponse();
  private static final Class<?> REQUEST_PROXY_CLASS = getRequestProxyClass();

  private FakeContextUtil() {
    // prevent instantiation
  }
  
  public static void runNonUIThreadWithFakeContext( Display display, Runnable runnable ) {
    // Don't replace local variables by method calls, since the context may
    // change during the methods execution.
    Display sessionDisplay = LifeCycleUtil.getSessionDisplay();
    boolean useDifferentContext =  ContextProvider.hasContext() && sessionDisplay != display;
    ServiceContext contextBuffer = null;
    // TODO [fappel]: The context handling's getting very awkward in case of
    //                having the context mapped instead of stored it in
    //                the ContextProvider's ThreadLocal (see ContextProvider).
    //                Because of this the wasMapped variable is used to
    //                use the correct way to restore the buffered context.
    //                See whether this can be done more elegantly and supplement
    //                the test cases...
    boolean wasMapped = false;
    if( useDifferentContext ) {
      contextBuffer = ContextProvider.getContext();
      wasMapped = ContextProvider.releaseContextHolder();
    }
    boolean useFakeContext = !ContextProvider.hasContext();
    if( useFakeContext ) {
      IDisplayAdapter adapter = display.getAdapter( IDisplayAdapter.class );
      ISessionStore session = adapter.getSessionStore();
      ContextProvider.setContext( createFakeContext( session ) );
    }
    try {
      runnable.run();
    } finally {
      if( useFakeContext ) {
        ContextProvider.disposeContext();
      }
      if( useDifferentContext ) {
        if( wasMapped ) {
          ContextProvider.setContext( contextBuffer, Thread.currentThread() );
        } else {
          ContextProvider.setContext( contextBuffer );
        }
      }
    }
  }

  public static ServiceContext createFakeContext( ISessionStore sessionStore ) {
    HttpServletRequest request = newRequest( sessionStore );
    ServiceContext result = new ServiceContext( request, RESPONSE_PROXY, sessionStore );
    result.setServiceStore( new ServiceStore() );
    return result;
  }

  private static HttpServletRequest newRequest( ISessionStore sessionStore ) {
    InvocationHandler invocationHandler = new RequestInvocationHandler( sessionStore );
    Class[] paramTypes = new Class[] { InvocationHandler.class };
    Object[] paramValues = new Object[] { invocationHandler };
    Object proxy = ClassUtil.newInstance( REQUEST_PROXY_CLASS, paramTypes, paramValues );
    return ( HttpServletRequest )proxy;
  }

  private static Class<?> getRequestProxyClass() {
    return Proxy.getProxyClass( CLASS_LOADER, new Class<?>[] { HttpServletRequest.class } );
  }

  private static HttpServletResponse newResponse() {
    Class[] interfaces = new Class[] { HttpServletResponse.class };
    ResponseInvocationHandler invocationHandler = new ResponseInvocationHandler();
    Object proxy = Proxy.newProxyInstance( CLASS_LOADER, interfaces , invocationHandler );
    return ( HttpServletResponse )proxy;
  }

  private static final class ResponseInvocationHandler implements InvocationHandler {
    public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable {
      throw new UnsupportedOperationException();
    }
  }

  private static class RequestInvocationHandler implements InvocationHandler {
    private final ISessionStore sessionStore;
  
    RequestInvocationHandler( ISessionStore sessionStore ) {
      this.sessionStore = sessionStore;
    }
  
    public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable {
      Object result;
      if( "getSession".equals( method.getName() ) ) {
        result = sessionStore.getHttpSession();
      } else if( "getLocale".equals( method.getName() ) ) {
        result = null;
      } else {
        throw new UnsupportedOperationException();
      }
      return result;
    }
  }
}
