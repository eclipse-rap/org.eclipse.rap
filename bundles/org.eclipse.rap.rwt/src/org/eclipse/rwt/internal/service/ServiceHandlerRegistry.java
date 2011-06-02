/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import javax.xml.parsers.*;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.internal.util.ClassUtil;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.service.IServiceHandler;
import org.w3c.dom.*;

class ServiceHandlerRegistry {
  private static final String SERVICEHANDLER_XML = "servicehandler.xml";

  private final Map<String,IServiceHandler> handlers;
  
  ServiceHandlerRegistry() {
    handlers = new HashMap<String,IServiceHandler>();
  }

  boolean isCustomHandler( String customHandlerId ) {
    synchronized( handlers ) {
      return handlers.containsKey( customHandlerId );
    }
  }
  
  void put( String id, IServiceHandler serviceHandler ) {
    synchronized( handlers ) {
      handlers.put( id, serviceHandler );
    }
  }

  void remove( String id ) {
    synchronized( handlers ) {
      handlers.remove( id );
    }
  }

  IServiceHandler get( String customHandlerId ) {
    synchronized( handlers ) {
      return handlers.get( customHandlerId );
    }
  }

  void activate() {
    registerHandlerInstances();
  }
  
  void deactivate() {
    handlers.clear();
  }
  
  /////////////////
  // helper methods

  
  private void registerHandlerInstances() {
    try {
      IResourceManager resourceManager = RWT.getResourceManager();
      if( resourceManager != null ) {
        registerHandlerInstances( resourceManager );
      }
    } catch( RuntimeException rte ) {
      throw rte;
    } catch( Exception exception ) {
      String msg = "Could not load custom service handlers.";
      throw new RuntimeException( msg, exception );
    }
  }

  private void registerHandlerInstances( IResourceManager manager ) throws Exception {
    Enumeration resources = manager.getResources( SERVICEHANDLER_XML );
    while( hasServiceHandlerDeclarations( resources ) ) {
      Document document = parseDocument( ( URL )resources.nextElement() );
      registerHandlerInstances( document );
    }
  }

  private boolean hasServiceHandlerDeclarations( Enumeration resources ) {
    return resources != null && resources.hasMoreElements();
  }

  private static Document parseDocument( URL url ) throws Exception, FactoryConfigurationError {
    DocumentBuilder builder = createDocumentBuilder();
    URLConnection connection = openConnection( url );
    return parseDocument( builder, connection );
  }

  private static Document parseDocument( DocumentBuilder builder, URLConnection connection )
    throws Exception
  {
    Document result;
    InputStream inputStream = connection.getInputStream();
    try {
      result = builder.parse( inputStream );
    } finally {
      inputStream.close();
    }
    return result;
  }

  private static URLConnection openConnection( URL url ) throws IOException {
    URLConnection result = url.openConnection();
    result.setUseCaches( false );
    return result;
  }

  private static DocumentBuilder createDocumentBuilder()
    throws FactoryConfigurationError, ParserConfigurationException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    return factory.newDocumentBuilder();
  }

  private void registerHandlerInstances( Document document ) {
    NodeList handlerList = getHandlerList( document );
    for( int i = 0; i < handlerList.getLength(); i++ ) {
      String name = getClassName( handlerList.item( i ) );
      String id = getHandlerId( handlerList.item( i ) );
      registerHandlerInstance( id, name );
    }
  }

  private void registerHandlerInstance( String id, String name ) {
    IServiceHandler handlerInstance = createServiceHandler( name );
    this.handlers.put( id, handlerInstance );
  }

  private IServiceHandler createServiceHandler( String name ) {
    return ( IServiceHandler )ClassUtil.newInstance( getClass().getClassLoader(), name );
  }

  private static NodeList getHandlerList( Document document ) {
    return document.getElementsByTagName( "handler" );
  }

  private static String getHandlerId( Node item ) {
    return getAttribute( item, "requestparameter" );
  }

  private static String getClassName( Node item ) {
    return getAttribute( item, "class" );
  }

  private static String getAttribute( Node item, String attrName ) {
    return item.getAttributes().getNamedItem( attrName ).getNodeValue();
  }
}