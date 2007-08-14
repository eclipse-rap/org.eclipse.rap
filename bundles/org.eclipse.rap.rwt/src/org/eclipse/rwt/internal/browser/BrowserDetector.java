/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.browser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import javax.xml.parsers.*;

import org.eclipse.rwt.internal.resources.ResourceManagerImpl;
import org.eclipse.rwt.resources.IResourceManager;
import org.w3c.dom.*;
import org.xml.sax.SAXException;


/**
 * <p>The singleton class that parses browser vendor, version info and 
 * and os info of a given userAgent http header string. 
 * </p>
 * <p>This class is not inteded to be used by clients.</p>
 */
public class BrowserDetector {
  
  private static final String BROWSERDETECTION_XML = "browserdetection.xml";
  private static final String CLASSNAME_ELEMENT = "classname";
  private static final String BROWSER_ELEMENT = "browser";
  
  private static BrowserDetector _instance;

  private final List browserDetectors;

  private BrowserDetector() {
    browserDetectors = new ArrayList();
    loadBrowserDetectors();
  }
  
  /**
   * <p>Returns the singleton instance of the <code>BrowserDetector</code>.</p>
   */
  public static synchronized BrowserDetector getInstance() {
    if( _instance == null ) {
      _instance = new BrowserDetector();
    }
    return _instance;
  }
  
  /**
   * <p>Detects browser by parsing the userAgent string and returns the 
   * class name of the matching <code>Browser</code> class.</p>
   */
  public String getBrowserClassName( final String userAgent ) {
    return detectBrowser( userAgent );
  }

  public String getBrowserOSName( final String userAgent ) {
    return DetectorBase.getBrowserOS( userAgent );
  }
  
  private String detectBrowser( final String theUserAgent ) {
    String result = Default.class.getName();
    if( theUserAgent != null ) {    
      // all comparison is done in lower case
      String userAgent = theUserAgent.toLowerCase();
      // set default in case no browser is found
      Object[] detectors = browserDetectors.toArray();
      boolean detected = false;
      for( int i = 0; !detected && i < detectors.length; i++ ) {
        DetectorBase browserDetector = ( DetectorBase )detectors[ i ];
        if( browserDetector.knowsBrowserString( userAgent ) ) {
          result = browserDetector.getBrowserClassName( userAgent );
          detected = true;
        }
      }
    }
    return result;
  } 
  
  private void loadBrowserDetectors() {
    try {
      // create a special URLLoader to avoid problems with other classloaders
      // that do not support getResources.
      IResourceManager manager = ResourceManagerImpl.getInstance();
      Enumeration browserDetectionFiles;
      if( manager == null ) {
        ClassLoader loader = getClass().getClassLoader();
        browserDetectionFiles = loader.getResources( BROWSERDETECTION_XML );
      } else {
        browserDetectionFiles = manager.getResources( BROWSERDETECTION_XML );
      }
      
      while( browserDetectionFiles.hasMoreElements() ) {
        int nextItem = 0;
        URL browserFile = ( URL )browserDetectionFiles.nextElement();
        
        Document document = parseDocument( browserFile );
        
        // extract list of browser definition (from current file) 
        NodeList nodeList = document.getElementsByTagName( BROWSER_ELEMENT );
        Node item = nodeList.item( nextItem );
        
        // iterate over all browser definitions (in current file)
        while( item != null ) {
          // extract classname from current browser definition
          String detectorClassName = findElementValue( item, CLASSNAME_ELEMENT );
          // create instance of the detector and store it in 
          // list of browser detectors.
          Object detectorInstance 
            = Class.forName( detectorClassName ).newInstance();
          DetectorBase browserDetector = ( DetectorBase )detectorInstance;
          browserDetectors.add( browserDetector );
          nextItem++;
          item = nodeList.item( nextItem );
        } // while (item != null )
      } // while (browserDetectionFiles.hasMoreElements() )
    } catch( final Throwable thr ) {
      // too many different exceptions may occur to handle each, 
      // so if something goes wrong just throw an illegal state exception
      StringBuffer msg = new StringBuffer();
      msg.append( new Date() );
      msg.append( " BROWSERDETECTOR error: " );
      msg.append( "Unable to load browserdetection classes (caused by " );
      msg.append( thr.getClass().getName() );
      msg.append( " with message: " );
      msg.append( thr.getMessage() );
      msg.append( ")." );
      System.out.println( msg );
      thr.printStackTrace();
      // TODO [rh] An exception thrown from here never gets through (I think
      // due to the fact that it is called during class initialization)
      //      REWORK THIS!
    }
  }
  
  /////////////////////////////////
  // Helper methods for xml parsing 
  
  private static String findElementValue( final Node rootNode, 
                                          final String elementName ) 
  {
    String result = null;
    if( rootNode instanceof Element ) {
      Element rootElement = ( Element )rootNode;
      NodeList foundElements = rootElement.getElementsByTagName( elementName );
      Node firstNode = foundElements.item( 0 );
      if( firstNode != null && firstNode instanceof Element ) {
        Element foundElement = ( Element )firstNode;
        result = foundElement.getFirstChild().getNodeValue().trim();
      }
    }
    return result;
  }
  
  private static Document parseDocument( final URL browserFile ) 
    throws IOException, ParserConfigurationException, SAXException
  {
    URLConnection con = browserFile.openConnection();
    con.setUseCaches( false );
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document result;
    InputStream is = con.getInputStream();
    try {
      result = builder.parse( is );
    } finally {
      is.close();
    }
    return result;
  }
}
