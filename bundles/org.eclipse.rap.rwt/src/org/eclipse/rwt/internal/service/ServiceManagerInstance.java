/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.internal.util.ClassUtil;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.service.IServiceHandler;
import org.w3c.dom.*;


public class ServiceManagerInstance {
  private static final String SERVICEHANDLER_XML = "servicehandler.xml";

  private final Map customHandlers;
  private final IServiceHandler handlerDispatcher;
  private IServiceHandler lifeCycleRequestHandler;
  private boolean initialized;

  private final class HandlerDispatcher implements IServiceHandler {

    public void service() throws ServletException, IOException {
      if( isCustomHandler() ) {
        IServiceHandler customHandler = getCustomHandler();
        customHandler.service();
      } else {
        getLifeCycleRequestHandler().service();
      }
    }
  }

  private ServiceManagerInstance() {
    handlerDispatcher = new HandlerDispatcher();
    customHandlers = new HashMap();
  }

  private void ensureInitialization() {
    synchronized( customHandlers ) {
      if( !initialized ) {
        init();
        initialized = true;
      }
    }
  }

  private void init() {
    try {
      IResourceManager manager = ResourceManager.getInstance();
      if( manager != null ) {
        Enumeration resources = manager.getResources( SERVICEHANDLER_XML );
        while( resources != null && resources.hasMoreElements() ) {
          URL url = ( URL )resources.nextElement();
          DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
          DocumentBuilder builder = factory.newDocumentBuilder();
          URLConnection con = url.openConnection();
          con.setUseCaches( false );
          Document document;
          InputStream is = con.getInputStream();
          try {
            document = builder.parse( is );
          } finally {
            is.close();
          }
          NodeList handlers = document.getElementsByTagName( "handler" );
          int count = handlers.getLength();
          for( int i = 0; i < count; i++ ) {
            Node handler = handlers.item( i );
            NamedNodeMap attributes = handler.getAttributes();
            String name = attributes.getNamedItem( "class" ).getNodeValue();
            String param = "requestparameter";
            String id = attributes.getNamedItem( param ).getNodeValue();
            Object handlerInstance = ClassUtil.newInstance( getClass().getClassLoader(), name );
            customHandlers.put( id, handlerInstance );
          }
        }
      }
    } catch( RuntimeException rte ) {
      throw rte;
    } catch( Exception exception ) {
      String msg = "Could not load custom service handlers.";
      throw new RuntimeException( msg, exception );
    }
  }

  void registerServiceHandler( final String id, final IServiceHandler handler )
  {
    ensureInitialization();
    synchronized( customHandlers ) {
      customHandlers.put( id, handler );
    }
  }

  void unregisterServiceHandler( final String id ) {
    ensureInitialization();
    synchronized( customHandlers ) {
      customHandlers.remove( id );
    }
  }

  IServiceHandler getHandler() {
    return handlerDispatcher;
  }

  boolean isCustomHandler() {
    ensureInitialization();
    synchronized( customHandlers ) {
      return customHandlers.containsKey( getCustomHandlerId() );
    }
  }

  IServiceHandler getCustomHandler() {
    ensureInitialization();
    synchronized( customHandlers ) {
      return ( IServiceHandler )customHandlers.get( getCustomHandlerId() );
    }
  }

  IServiceHandler getCustomHandler( final String id ) {
    ensureInitialization();
    synchronized( customHandlers ) {
      return ( IServiceHandler )customHandlers.get( id );
    }
  }

  //////////////////
  // helping methods

  private String getCustomHandlerId() {
    HttpServletRequest request = ContextProvider.getRequest();
    return request.getParameter( IServiceHandler.REQUEST_PARAM );
  }

  private IServiceHandler getLifeCycleRequestHandler() {
    if( lifeCycleRequestHandler == null ) {
      lifeCycleRequestHandler = new LifeCycleServiceHandler();
    }
    return lifeCycleRequestHandler;
  }
}
