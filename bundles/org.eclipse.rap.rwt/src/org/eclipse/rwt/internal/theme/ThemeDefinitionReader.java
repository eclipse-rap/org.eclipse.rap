/*******************************************************************************
 * Copyright (c) 2007, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.theme;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;


/**
 * Reader for theme definition files. These are the "*.theme.xml" files
 * that define themeable properties of a certain widget.
 */
public class ThemeDefinitionReader {

  public static interface ThemeDefHandler {
    public abstract void readThemeProperty( ThemeProperty def );
  }

  private static final String NODE_ROOT = "theme";

  private static final String ATTR_NAME = "name";

  private static final String ATTR_DESCRIPTION = "description";

  private static final String ATTR_DEFAULT = "default";

  private static final String ATTR_INHERIT = "inherit";

  private static final String ATTR_TARGET_PATH = "targetPath";

  private static final String ATTR_TRANSPARENT_ALLOWED = "transparentAllowed";

  private static final String ATTR_CSS_ELEMENTS = "css-elements";

  private static final String ATTR_CSS_PROPERTY = "css-property";

  private static final String ATTR_CSS_SELECTORS = "css-selectors";

  private static final String TYPE_BOOLEAN = "boolean";

  private static final String TYPE_BORDER = "border";

  private static final String TYPE_DIMENSION = "dimension";

  private static final String TYPE_BOXDIMENSION = "boxdim";

  private static final String TYPE_COLOR = "color";

  private static final String TYPE_FONT = "font";

  private static final String TYPE_IMAGE = "image";

  private static final String THEME_DEF_SCHEMA = "themedef.xsd";

  private static final String JAXP_SCHEMA_LANGUAGE
    = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

  private static final String W3C_XML_SCHEMA
    = "http://www.w3.org/2001/XMLSchema";

  private final InputStream inputStream;

  private final ResourceLoader loader;

  private final String fileName;

  /**
   * An instance of this class reads theme definitions from an XML resource.
   *
   * @param inputStream input stream from a theme definition XML
   */
  public ThemeDefinitionReader( final InputStream inputStream,
                                final String fileName,
                                final ResourceLoader loader )
  {
    this.fileName = fileName;
    if( inputStream == null ) {
      throw new NullPointerException( "null argument" );
    }
    this.inputStream = inputStream;
    this.loader = loader;
  }

  /**
   * Reads a theme definition from the specified stream. The stream is kept open
   * after reading.
   *
   * @param callback an implementation of the ThemeDefHandler interface that
   *            handles parsing events
   * @throws IOException if a I/O error occurs
   * @throws SAXException if a parse error occurs
   */
  public void read( final ThemeDefHandler callback )
    throws SAXException, IOException
  {
    Document document;
    document = parseThemeDefinition( inputStream );
    Node root = document.getElementsByTagName( NODE_ROOT ).item( 0 );
    NodeList childNodes = root.getChildNodes();
    for( int i = 0; i < childNodes.getLength(); i++ ) {
      Node node = childNodes.item( i );
      if( node.getNodeType() == Node.ELEMENT_NODE ) {
        ThemeProperty property = readElement( node );
        if( property != null ) {
          callback.readThemeProperty( property );
        }
      }
    }
  }

  private ThemeProperty readElement( final Node node ) {
    String type = node.getNodeName();
    String name = getAttributeValue( node, ATTR_NAME );
    String description = getAttributeValue( node, ATTR_DESCRIPTION );
    String inherit = getAttributeValue( node, ATTR_INHERIT );
    String defaultStr = getAttributeValue( node, ATTR_DEFAULT );
    String cssElements = getAttributeValue( node, ATTR_CSS_ELEMENTS );
    if( cssElements == null ) {
      cssElements = getAttributeValue( node.getParentNode(), ATTR_CSS_ELEMENTS );
    }
    String cssProperty = getAttributeValue( node, ATTR_CSS_PROPERTY );
    String cssSelectors = getAttributeValue( node, ATTR_CSS_SELECTORS );
    QxType value;
    String targetPath = null;
    boolean transparentAllowed = false;
    ThemeProperty result = null;
    if( "property".equals( type ) || "element".equals( type ) ) {
      // new syntax, ignore for now
    } else {
      if( TYPE_FONT.equals( type ) ) {
        value = QxFont.valueOf( defaultStr );
      } else if( TYPE_COLOR.equals( type ) ) {
        String transpValue = getAttributeValue( node, ATTR_TRANSPARENT_ALLOWED );
        transparentAllowed = Boolean.valueOf( transpValue ).booleanValue();
        value = QxColor.valueOf( defaultStr );
      } else if( TYPE_BOOLEAN.equals( type ) ) {
        value = QxBoolean.valueOf( defaultStr );
      } else if( TYPE_BORDER.equals( type ) ) {
        value = QxBorder.valueOf( defaultStr );
      } else if( TYPE_BOXDIMENSION.equals( type ) ) {
        value = QxBoxDimensions.valueOf( defaultStr );
      } else if( TYPE_DIMENSION.equals( type ) ) {
        value = QxDimension.valueOf( defaultStr );
      } else if( TYPE_IMAGE.equals( type ) ) {
        targetPath = getAttributeValue( node, ATTR_TARGET_PATH );
        value = QxImage.valueOf( defaultStr, loader );
      } else {
        // TODO [rst] Remove when XML validation is active
        throw new IllegalArgumentException( "Illegal type: " + type );
      }
      result = new ThemeProperty( name, inherit, value, description );
      result.targetPath = targetPath;
      result.transparentAllowed = transparentAllowed;
      if( cssElements != null && cssProperty != null ) {
        result.cssElements = cssElements.split( "\\s+" );
        result.cssProperty = cssProperty;
        if( cssSelectors != null ) {
          result.cssSelectors = cssSelectors.split( "\\s+" );
        }
      }
    }
    return result;
  }

  private static String getAttributeValue( final Node node, final String name )
  {
    String result = null;
    NamedNodeMap attributes = node.getAttributes();
    if( attributes != null ) {
      Node namedItem = attributes.getNamedItem( name );
      if( namedItem != null ) {
        result = namedItem.getNodeValue();
      }
    }
    return result;
  }

  private Document parseThemeDefinition( final InputStream is )
    throws SAXException, IOException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware( true );
    ClassLoader loader = ThemeDefinitionReader.class.getClassLoader();
    final URL schema = loader.getResource( THEME_DEF_SCHEMA );
    factory.setValidating( schema != null );
    try {
      factory.setAttribute( JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA );
    } catch( final IllegalArgumentException iae ) {
      // XML-Processing does not support JAXP 1.2 or greater
      factory.setNamespaceAware( false );
      factory.setValidating( false );
    }
    DocumentBuilder builder;
    try {
      builder = factory.newDocumentBuilder();
    } catch( final ParserConfigurationException e ) {
      String message = "Failed to initialize parser for theme definition files";
      throw new RuntimeException( message, e );
    }
//    builder.setEntityResolver( new EntityResolver() {
//      public InputSource resolveEntity( final String publicID,
//                                        final String systemID )
//        throws IOException, SAXException
//      {
//        InputSource result = null;
//        if( schema != null && systemID.endsWith( THEME_DEF_SCHEMA ) ) {
//          URLConnection connection = schema.openConnection();
//          connection.setUseCaches( false );
//          result = new InputSource( connection.getInputStream() );
//        }
//        return result;
//      }
//    } );
    builder.setErrorHandler( new ThemeDefinitionErrorHandler() );
    return builder.parse( is );
  }

  // TODO: Logging instead of sysout
  private class ThemeDefinitionErrorHandler implements ErrorHandler {
    public void error( final SAXParseException spe ) throws SAXException {
      System.err.println( "Error parsing theme definition "
                          + getPosition( spe )
                          + ":" );
      System.err.println( spe.getMessage() );
    }
    
    public void fatalError( final SAXParseException spe )
    throws SAXException
    {
      System.err.println( "Fatal error parsing theme definition "
                          + getPosition( spe )
                          + ":" );
      System.err.println( spe.getMessage() );
    }
    
    public void warning( final SAXParseException spe )
    throws SAXException
    {
      System.err.println( "Warning parsing theme definition "
                          + getPosition( spe )
                          + ":" );
      System.err.println( spe.getMessage() );
    }
    
    private String getPosition( final SAXParseException spe ) {
      return "in file '"
      + fileName
      + "' at line "
      + spe.getLineNumber()
      + ", col "
      + spe.getColumnNumber();
    }
  }
}
