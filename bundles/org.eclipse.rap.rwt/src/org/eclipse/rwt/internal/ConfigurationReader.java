/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;


/** 
 * This is a helping class for the W4TApplication to read
 * the initialisations, which are stored in w4t.xml in the conf/
 * directory of the web applications root.
 */
public class ConfigurationReader {

  private static final String W4_TOOLKIT_SCHEMA = "w4t.xsd";
  private static final String JAXP_SCHEMA_LANGUAGE
    = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
  private static final String W3C_XML_SCHEMA
    = "http://www.w3.org/2001/XMLSchema";

  
  private static Document document = null;
  private static IConfiguration configuration = null;
  private static final Map values = new HashMap();
  private static File configurationFile = null;
  private static IEngineConfig engineConfig = null;
    
  private static final class ConfigurationImpl implements IConfiguration {

    private IInitialization initialization = null;
    private IFileUpload fileUpload = null;
    
    public IInitialization getInitialization() {
      if( initialization == null ) {
        initialization = new InitializationImpl();
      }
      return initialization;
    }

    public IFileUpload getFileUpload() {
      if( fileUpload == null ) {
        fileUpload = new FileUploadImpl();
      }
      return fileUpload;
    }
  }
  
  private static final class InitializationImpl implements IInitialization {

    public String getStartUpForm() {
      // do not use Startup.class.getName() since in name space mode
      // that class must be loaded in session scope
      return getConfigValue( "startUpForm", "com.w4t.administration.Startup" );
    }
    
    public String getLifeCycle() {
      String defaultValue = IInitialization.LIFE_CYCLE_DEFAULT;
      return getConfigValue( IInitialization.PARAM_LIFE_CYCLE, defaultValue );
    }

    public String getErrorPage() {
      // do not use DefaultErrorForm.class.getName() since in name space mode
      // that class must be loaded in session scope
      String defaultValue = "com.w4t.administration.DefaultErrorForm";
      return getConfigValue( "errorPage", defaultValue );
    }

    public String getAdministrationStartupForm() {
      // do not use Startup.class.getName() since in name space mode
      // that class must be loaded in session scope
      String defaultValue = "com.w4t.administration.Startup";
      return getConfigValue( "administrationStartupForm", defaultValue );
    }

    public String getMessagePage() {
      // do not use DefaultMessageForm.class.getName() since in name space mode
      // that class must be loaded in session scope
      String defaultValue = "com.w4t.administration.DefaultMessageForm";
      return getConfigValue( "messagePage", defaultValue );
    }

    public String getWorkDirectory() {
      return getConfigValue( "workDirectory", "WEB-INF/classes/" );
    }

    public long getClosingTimeout() {
      String value = getConfigValue( "closingTimeout", "3600000" );
      long result = Long.parseLong( value );
      return ( result < 60000 ) ? 60000 : result;
    }

    public long getSkimmerFrequenzy() {
      String value = getConfigValue( "skimmerFrequency", "60000" );
      return Long.parseLong( value );
    }

    public boolean isDirectMonitoringAccess() {
      String value = getConfigValue( "directMonitoringAccess", "true" );
      return Boolean.valueOf( value ).booleanValue();
    }

    public boolean isCompression() {
      String value = getConfigValue( "compression", "true" );
      return Boolean.valueOf( value ).booleanValue();
    }

    public boolean isProcessTime() {
      String value = getConfigValue( "processTime", "false" );
      return Boolean.valueOf( value ).booleanValue();
    }

    public String getNoscriptSubmitters() {
      String defaultValue = IInitialization.NOSCRIPT_SUBMITTERS_CREATE;
      return getConfigValue( "noscriptSubmitters", defaultValue );
    }

    public String getResources() {
      String defaultValue = IInitialization.RESOURCES_DELIVER_FROM_DISK;
      return getConfigValue( "resources", defaultValue );
    }

    public long getMaxSessionUnboundToForceGC() {
      String tagName = "maxSessionUnboundToForceGC";
      String value = getConfigValue( tagName, "0" );
      return Long.parseLong( value );
    }

    public String getHandleMissingI18NResource() {
      String defaultValue = IInitialization.HANDLE_MISSING_I18N_RESOURCE_EMPTY;
      return getConfigValue( "handleMissingI18NResource", defaultValue );
    }
  }

  private static final class FileUploadImpl implements IFileUpload {

    public long getMaxUploadSize() {
      String value = getConfigValue( "maxUploadSize", "4194304" );
      return Long.parseLong( value );
    }

    public int getMaxMemorySize() {
      String value = getConfigValue( "maxMemorySize", "524288" );
      return Integer.parseInt( value );
    }
  }
  
  public static IConfiguration getConfiguration() {
    if( configuration == null ) {
      configuration = new ConfigurationImpl();
    }
    return configuration;
  }
  
  public static void setConfigurationFile( final File configurationFile )
    throws FactoryConfigurationError, 
           ParserConfigurationException, 
           SAXException, 
           IOException
  {
    reset();
    if( configurationFile != null ) {
      if( !configurationFile.exists() ) {
        String msg =   "Parameter 'configurationFile ["
                     + configurationFile.toString()
                     + "]' does not exist.";
        throw new IllegalArgumentException( msg );
      }
      if( configurationFile.isDirectory() ) {
        String msg = "Parameter 'configurationFile' must not be a directory.";
        throw new IllegalArgumentException( msg );
      }
      ConfigurationReader.configurationFile  = configurationFile;
    }
    if( configurationFile != null ) {
      parseConfiguration();
    }
  }

  public static IEngineConfig getEngineConfig() {
    return engineConfig;
  }
  
  public static void setEngineConfig( final IEngineConfig engineConfig )
    throws FactoryConfigurationError,
           ParserConfigurationException,
           SAXException,
           IOException
  {
    ConfigurationReader.engineConfig = engineConfig;
    setConfigurationFile( engineConfig.getConfigFile() );
  }
  
  
  //////////////////
  // helping methods

  private static void reset() {
    values.clear();
    document = null;
    configuration = null;
    configurationFile = null;
  }
  
  private static void parseConfiguration()
    throws FactoryConfigurationError,
           ParserConfigurationException, 
           SAXException, 
           IOException 
  {
    if( configurationFile != null ) {
      InputStream is = new FileInputStream( configurationFile );
      try {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware( true );
        ClassLoader loader = ConfigurationReader.class.getClassLoader();
        final URL schema = loader.getResource( W4_TOOLKIT_SCHEMA );
        factory.setValidating( schema != null );
        try {
          factory.setAttribute( JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA );
        } catch( final IllegalArgumentException iae ) {
          // XML-Processing does not support JAXP 1.2 or greater
          factory.setNamespaceAware( false );
          factory.setValidating( false );
        }
        DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setEntityResolver( new EntityResolver() {
          public InputSource resolveEntity( final String publicID, 
                                            final String systemID )
            throws IOException, SAXException
          {
            InputSource result = null;
            if( schema != null && systemID.endsWith( W4_TOOLKIT_SCHEMA ) ) {
              URLConnection connection = schema.openConnection();
              connection.setUseCaches( false );
              result = new InputSource( connection.getInputStream() );
            }
            return result;
          }
        } );
        
        // TODO: more sophisticated ErrorHandler implementation...
        builder.setErrorHandler( new ErrorHandler() {
          
          public void error( final SAXParseException spe )
            throws SAXException
          {
            System.out.println( "Error parsing W4 Toolkit configuration:" );
            System.out.println( configurationFile.toString() );
            System.out.println( spe.getMessage() );
          }
          
          public void fatalError( final SAXParseException spe )
            throws SAXException
          {
            String msg = "Fatal error parsing W4 Toolkit configuration:";
            System.out.println( msg );
            System.out.println( configurationFile.toString() );
            System.out.println( spe.getMessage() );
          }
          
          public void warning( final SAXParseException spe )
            throws SAXException
          {
            System.out.println( "Warning parsing W4 Toolkit configuration:" );
            System.out.println( configurationFile.toString() );
            System.out.println( spe.getMessage() );
          } 
          
        } );
        document = builder.parse( is );
      } finally {
        is.close();
      }
    }
  }
  
  private static String getConfigValue( final String tagName,
                                        final String defaultValue )
  {
    if( !values.containsKey( tagName ) ) {
      String result = "";
      if( System.getProperty( tagName ) != null ) {
        result = System.getProperty( tagName );
      } else if( document != null ) {
        NodeList nodeList = document.getElementsByTagName( tagName );
        Node item = nodeList.item( 0 );
        if( item != null ) {
          Node firstChild = item.getFirstChild();
          if( firstChild != null ) {
            String nodeValue = firstChild.getNodeValue();
            result = nodeValue.trim();
          }
        } else {
          result = defaultValue;
        }
      } else {
        result = defaultValue;
      }
      values.put( tagName, result );
    }
    return ( String )values.get( tagName );
  }
}