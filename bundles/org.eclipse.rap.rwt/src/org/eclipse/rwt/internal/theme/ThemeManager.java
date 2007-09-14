/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.theme;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.rwt.internal.lifecycle.HtmlResponseWriter;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.rwt.internal.theme.ThemeDefinitionReader.ThemeDef;
import org.eclipse.rwt.internal.theme.ThemeDefinitionReader.ThemeDefHandler;
import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;
import org.eclipse.swt.widgets.Widget;
import org.xml.sax.SAXException;


public final class ThemeManager {

  public static interface ResourceLoader {
    abstract InputStream getResourceAsStream( String resourceName )
      throws IOException;
  }

  private static final class ThemeWrapper {
    final Theme theme;
    final ResourceLoader loader;
    final int count;
    public ThemeWrapper( final Theme theme,
                         final ResourceLoader loader,
                         final int count )
    {
      this.theme = theme;
      this.loader = loader;
      this.count = count;
    }
  }

  private static final String CHARSET = "UTF-8";

  private static final boolean DEBUG
    = "true".equals( System.getProperty( ThemeManager.class.getName() + ".log" ) );

  private static final Pattern PATTERN_REPLACE
    = Pattern.compile( "THEME_VALUE\\(\\s*\"(.*?)\"\\s*\\)" );

  /**
   * This array contains all widget images needed by RAP that are not themeable.
   * These images are registered by the ThemeManager once for every theme.
   */
  private static final String[] WIDGET_NOTHEME_RESOURCES = new String[]{
    "arrows/down.gif",
    "arrows/down_small.gif",
    "arrows/down_tiny.gif",
    "arrows/first.png",
    "arrows/forward.gif",
    "arrows/last.png",
    "arrows/left.png",
    "arrows/minimize.gif",
    "arrows/next.gif",
    "arrows/previous.gif",
    "arrows/rewind.gif",
    "arrows/right.png",
    "arrows/up.gif",
    "arrows/up_small.gif",
    "arrows/up_tiny.gif",
// "colorselector/brightness-field.jpg",
// "colorselector/brightness-handle.gif",
// "colorselector/huesaturation-field.jpg",
// "colorselector/huesaturation-handle.gif",
    "cursors/alias.gif",
    "cursors/copy.gif",
    "cursors/move.gif",
    "cursors/nodrop.gif",
// "datechooser/lastMonth.png",
// "datechooser/lastYear.png",
// "datechooser/nextMonth.png",
// "datechooser/nextYear.png",
    "menu/checkbox.gif",
    "menu/menu-blank.gif",
    "menu/radiobutton.gif",
    "splitpane/knob-horizontal.png",
    "splitpane/knob-vertical.png",
    "table/up.png",
    "table/down.png",
    "tree/cross.gif",
    "tree/cross_minus.gif",
    "tree/cross_plus.gif",
    "tree/end.gif",
    "tree/end_minus.gif",
    "tree/end_plus.gif",
    "tree/line.gif",
    "tree/minus.gif",
    "tree/only_minus.gif",
    "tree/only_plus.gif",
    "tree/plus.gif",
    "tree/start_minus.gif",
    "tree/start_plus.gif",
    "tree/folder_open.gif",
    "tree/folder_closed.gif",
    "display/bg.gif",
    "table/check_white_on.gif",
    "table/check_white_off.gif",
    "table/check_gray_on.gif",
    "table/check_gray_off.gif"
  };

  /** Where to load the default non-themeable images from */
  private static final String WIDGET_RESOURCES_SRC = "resource/widget/rap";

  private static final String BLANK_IMAGE_PATH
    = "resource/static/image/blank.gif";

  private static final String THEME_RESOURCE_DEST = "resource/themes/";

  private static final String THEME_PREFIX = "org.eclipse.swt.theme.";

  private static final String PREDEFINED_THEME_ID = THEME_PREFIX + "Default";

  private static final Class[] THEMEABLE_WIDGETS = new Class[]{
    org.eclipse.swt.widgets.Button.class,
    org.eclipse.swt.widgets.Combo.class,
    org.eclipse.swt.widgets.Control.class,
    org.eclipse.swt.widgets.CoolBar.class,
    org.eclipse.swt.custom.CTabFolder.class,
    org.eclipse.swt.widgets.Group.class,
    org.eclipse.swt.widgets.Label.class,
    org.eclipse.swt.widgets.Link.class,
    org.eclipse.swt.widgets.List.class,
    org.eclipse.swt.widgets.Menu.class,
    org.eclipse.swt.widgets.ProgressBar.class,
    org.eclipse.swt.widgets.Shell.class,
    org.eclipse.swt.widgets.Spinner.class,
    org.eclipse.swt.widgets.TabFolder.class,
    org.eclipse.swt.widgets.Table.class,
    org.eclipse.swt.widgets.Text.class,
    org.eclipse.swt.widgets.ToolBar.class,
    org.eclipse.swt.widgets.Tree.class,
    org.eclipse.swt.widgets.Widget.class
  };

  private static final String PREDEFINED_THEME_NAME = "RAP Default Theme";

  private static ThemeManager instance;

  private boolean initialized;
  private final List customWidgets;
  private final List addAppearances;
  private final Map themeDefs;
  private final Map themes;
  private final Map adapters;
  private final Map imageMapping;
  private Theme predefinedTheme;

  private int themeCount;

  private ThemeManager() {
    // prevent instantiation from outside
    initialized = false;
    customWidgets = new ArrayList();
    addAppearances = new ArrayList();
    themeDefs = new LinkedHashMap();
    themes = new HashMap();
    adapters = new HashMap();
    imageMapping = new HashMap();
  }

  /**
   * Returns the sole instance of the ThemeManager.
   */
  public static ThemeManager getInstance() {
    if( instance == null ) {
      instance = new ThemeManager();
    }
    return instance;
  }

  /**
   * Initializes the ThemeManager, i.e. loads theming-relevant files and
   * registers themes.
   */
  public void initialize() {
    log( "____ ThemeManager intialize" );
    if( !initialized ) {
      try {
        for( int i = 0; i < THEMEABLE_WIDGETS.length; i++ ) {
          processThemeableWidget( THEMEABLE_WIDGETS[ i ] );
        }
        Iterator widgetIter = customWidgets.iterator();
        while( widgetIter.hasNext() ) {
          processThemeableWidget( ( Class )widgetIter.next() );
        }
      } catch( final Exception e ) {
        throw new RuntimeException( "Initialization failed", e );
      }
      // initialize predefined theme
      predefinedTheme = new Theme( PREDEFINED_THEME_NAME );
      Iterator iterator = themeDefs.keySet().iterator();
      while( iterator.hasNext() ) {
        String key = ( String )iterator.next();
        ThemeDef def = ( ThemeDef )themeDefs.get( key );
        predefinedTheme.setValue( key, def.defValue );
      }
      themes.put( PREDEFINED_THEME_ID, new ThemeWrapper( predefinedTheme,
                                                         null,
                                                         themeCount++ ) );
      initialized = true;
      logRegisteredAdapters();
    }
  }

  public void deregisterAll() {
    if( initialized ) {
      customWidgets.clear();
      addAppearances.clear();
      themeDefs.clear();
      themes.clear();
      adapters.clear();
      predefinedTheme = null;
      initialized = false;
      log( "deregistered" );
    }
  }

  public void addThemeableWidget( final Class widget ) {
    if( initialized ) {
      throw new IllegalStateException( "ThemeManager is already initialized" );
    }
    customWidgets.add( widget );
  }

  /**
   * Registers a theme from an input stream.
   *
   * @param id an id that identifies the theme in the Java code. This id is not
   *            valid on the client-side. To get the id that is used on the
   *            client, see method <code>getJsThemeId</code>.
   * @param name a name that describes the theme. Currently not used.
   * @param instr an input stream to read the theme from.
   * @param loader a ResourceLoader instance that is able to load resources
   *            needed by this theme.
   */
  public void registerTheme( final String id,
                             final String name,
                             final InputStream instr,
                             final ResourceLoader loader )
    throws IOException
  {
    checkInitialized();
    log( "___ register theme " + id + ": " + instr );
    checkId( id );
    if( themes.containsKey( id ) ) {
      String pattern = "Theme with id ''{0}'' exists already";
      Object[] arguments = new Object[] { id };
      String msg = MessageFormat.format( pattern, arguments );
      throw new IllegalArgumentException( msg );
    }
    Theme theme = loadThemeFile( name, instr );
    themes.put( id, new ThemeWrapper( theme, loader, themeCount++  ) );
  }

  public void registerResources() throws IOException {
    checkInitialized();
    log( "____ ThemeManager register resources" );
    Iterator iterator = themes.keySet().iterator();
    while( iterator.hasNext() ) {
      String id = ( String )iterator.next();
      registerThemeFiles( id );
    }
  }

  public boolean hasTheme( final String themeId ) {
    checkInitialized();
    return themes.containsKey( themeId );
  }

  public Theme getTheme( final String themeId ) {
    checkInitialized();
    if( !hasTheme( themeId ) ) {
      throw new IllegalArgumentException( "No theme registered with id "
                                          + themeId );
    }
    ThemeWrapper wrapper = ( ThemeWrapper )themes.get( themeId );
    return wrapper.theme;
  }

  public String[] getRegisteredThemeIds() {
    checkInitialized();
    String[] result = new String[ themes.size() ];
    return ( String[] )themes.keySet().toArray( result );
  }

  public String getDefaultThemeId() {
    checkInitialized();
    return PREDEFINED_THEME_ID;
  }

  public boolean hasThemeAdapter( final Class controlClass ) {
    checkInitialized();
    return adapters.containsKey( controlClass );
  }

  public IThemeAdapter getThemeAdapter( final Class widgetClass ) {
    checkInitialized();
    IThemeAdapter result = null;
    Class clazz = widgetClass;
    while( clazz != null && clazz != Widget.class && !hasThemeAdapter( clazz ) ) {
      clazz = clazz.getSuperclass();
    }
    if( hasThemeAdapter( clazz ) ) {
      result = ( IThemeAdapter )adapters.get( clazz );
    } else {
      throw new IllegalArgumentException( "No theme adapter registered for class "
                                          + widgetClass.getName() );
    }
    return result;
  }

  /**
   * Returns the theme id to use on the client side.
   */
  public String getJsThemeId( final String id ) {
    checkInitialized();
    String result;
    if( PREDEFINED_THEME_ID.equals( id ) ) {
      result = PREDEFINED_THEME_ID;
    } else {
      ThemeWrapper wrapper = ( ThemeWrapper )themes.get( id );
      if( wrapper == null ) {
        String mesg = "No theme registered with id " + id;
        throw new IllegalArgumentException( mesg );
      }
      result = THEME_PREFIX + "Custom_" + wrapper.count;
    }
    return result;
  }

  /**
   * Writes a theme template file to the standard output.
   */
  public static void main( final String[] args ) {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    Iterator iterator = manager.themeDefs.keySet().iterator();
    StringBuffer sb = new StringBuffer();
    sb.append( "# Generated RAP theme file template\n" );
    sb.append( "#\n" );
    while( iterator.hasNext() ) {
      String key = ( String )iterator.next();
      ThemeDef def = ( ThemeDef )manager.themeDefs.get( key );
      String value = def.defValue.toDefaultString();
      sb.append( "\n" );
      if( def.description == null ) {
        throw new NullPointerException( "Description missing for " + key );
      }
      sb.append( "# " + def.description.replaceAll( "\\n\\s*", "\n# " ) + "\n" );
      sb.append( "# default: " + value + "\n" );
      sb.append( "#" + def.name + ": " + value  + "\n" );
    }
    System.out.println( sb.toString() );
  }

  private void checkInitialized() {
    if( !initialized ) {
      throw new IllegalStateException( "ThemeManager not initialized" );
    }
  }

  /**
   * Loads and processes all theme-relevant resources for a given class.
   * @throws InvalidThemeFormatException
   * @throws ParserConfigurationException
   * @throws FactoryConfigurationError
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  private void processThemeableWidget( final Class clazz )
    throws IOException, FactoryConfigurationError,
    ParserConfigurationException, InstantiationException,
    IllegalAccessException
  {
    log( "Processing widget: " + clazz.getName() );
    String[] variants = getPackageVariants( clazz.getPackage().getName() );
    String className = getSimpleClassName( clazz );
    ClassLoader loader = clazz.getClassLoader();
    boolean found = false;
    for( int i = 0; i < variants.length && !found ; i++ ) {
      String pkgName = variants[ i ] + "." + className.toLowerCase() + "kit";
      // TODO [rst] Is it possible to recognize whether a package exists?
      log( "looking through package " + pkgName );
      found |= loadThemeDef( loader, pkgName, className );
      found |= loadAppearanceJs( loader, pkgName, className );
      found |= loadThemeAdapter( loader, pkgName, className, clazz );
    }
  }

  private boolean loadThemeDef( final ClassLoader loader,
                                final String pkgName,
                                final String className )
    throws IOException, FactoryConfigurationError, ParserConfigurationException
  {
    boolean result = false;
    String resPkgName = pkgName.replace( '.', '/' );
    String fileName = resPkgName + "/" + className + ".theme.xml";
    InputStream inStream = loader.getResourceAsStream( fileName );
    if( inStream != null ) {
      log( "Found theme definition file: " +  fileName );
      result = true;
      try {
        ThemeDefinitionReader reader = new ThemeDefinitionReader( inStream );
        reader.read( new ThemeDefHandler() {
          public void readThemeDef( final ThemeDef def ) {
            if( themeDefs.containsKey( def.name ) ) {
              throw new IllegalArgumentException( "key defined twice: "
                                                  + def.name );
            }
            themeDefs.put( def.name, def );
            if( def.targetPath != null ) {
              imageMapping.put( def.name, def.targetPath );
            }
          }
        } );
      } catch( final SAXException e ) {
        throw new IllegalArgumentException( "Failed to parse file " + fileName );
      } finally {
        inStream.close();
      }
    }
    return result;
  }

  private boolean loadAppearanceJs( final ClassLoader loader,
                                    final String pkgName,
                                    final String className )
    throws IOException
  {
    boolean result = false;
    String resPkgName = pkgName.replace( '.', '/' );
    String fileName = resPkgName + "/" + className + ".appearances.js";
    InputStream inStream = loader.getResourceAsStream( fileName );
    if( inStream != null ) {
      log( "Found appearance js file: " +  fileName );
      result = true;
      try {
        StringBuffer sb = new StringBuffer();
        InputStreamReader reader = new InputStreamReader( inStream, "UTF-8" );
        BufferedReader br = new BufferedReader( reader );
        for( int i = 0; i < 100; i++ ) {
          int character = br.read();
          while( character != -1 ) {
            sb.append( ( char )character );
            character = br.read();
          }
        }
        addAppearances.add( stripTemplate( sb.toString() ) );
      } finally {
        inStream.close();
      }
    }
    return result;
  }

  /**
   * Tries to load the theme adapter for a class from a given package.
   * @return the theme adapter or <code>null</code> if not found.
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  private boolean loadThemeAdapter( final ClassLoader loader,
                                    final String pkgName,
                                    final String className,
                                    final Class clazz )
    throws InstantiationException, IllegalAccessException
  {
    boolean result = false;
    IThemeAdapter themeAdapter = null;
    String adapterClassName = pkgName + '.' + className + "ThemeAdapter";
    try {
      Class adapterClass = loader.loadClass( adapterClassName );
      themeAdapter = ( IThemeAdapter )adapterClass.newInstance();
      if( themeAdapter != null ) {
        log( "Found theme adapter class: " + themeAdapter.getClass().getName() );
        result = true;
        adapters.put( clazz, themeAdapter );
      }
    } catch( final ClassNotFoundException e ) {
      // ignore and try to load from next package name variant
    }
    return result;
  }

  private Theme loadThemeFile( final String name, final InputStream instr )
    throws IOException
  {
    if( instr == null ) {
      throw new IllegalArgumentException( "null argument" );
    }
    Theme newTheme = new Theme( name, predefinedTheme );
    Properties properties = new Properties();
    properties.load( instr );
    Iterator iterator = properties.keySet().iterator();
    while( iterator.hasNext() ) {
      String key = ( ( String )iterator.next() ).trim();
      QxType defValue = predefinedTheme.getValue( key );
      QxType newValue = null;
      if( defValue == null ) {
        throw new IllegalArgumentException( "Invalid key for themeing: " + key );
      }
      String value = ( String )properties.get( key );
      if( defValue instanceof QxBorder ) {
        newValue = new QxBorder( value );
      } else if( defValue instanceof QxBoxDimensions ) {
        newValue = new QxBoxDimensions( value );
      } else if( defValue instanceof QxFont ) {
        newValue = new QxFont( value );
      } else if( defValue instanceof QxColor ) {
        newValue = new QxColor( value );
      } else if( defValue instanceof QxDimension ) {
        newValue = new QxDimension( value );
      } else if( defValue instanceof QxImage ) {
        newValue = new QxImage( value );
      }
      newTheme.setValue( key, newValue );
    }
    // second loop for inheritance
    // TODO [rst] detect inheritance loops and handle multi-step inheritance
    iterator = themeDefs.keySet().iterator();
    while( iterator.hasNext() ) {
      String key = ( String )iterator.next();
      ThemeDef def = ( ThemeDef )themeDefs.get( key );
      if( def.inherit != null && !properties.containsKey( key ) ) {
        newTheme.setValue( key, newTheme.getValue( def.inherit ) );
      }
    }
    return newTheme;
  }

  /**
   * Creates and registers all JavaScript theme files and images for a given
   * theme.
   */
  private void registerThemeFiles( final String id )
    throws IOException
  {
    ThemeWrapper wrapper = ( ThemeWrapper )themes.get( id );
    String jsId = getJsThemeId( id );
    registerWidgetImages( id );
    StringBuffer sb = new StringBuffer();
    sb.append( createColorTheme( wrapper.theme, jsId ) );
    sb.append( createBorderTheme( wrapper.theme, jsId ) );
    sb.append( createFontTheme( wrapper.theme, jsId ) );
    sb.append( createIconTheme( wrapper.theme, jsId ) );
    sb.append( createWidgetTheme( wrapper.theme, jsId ) );
    sb.append( createAppearanceTheme( wrapper.theme, jsId ) );
    sb.append( createMetaTheme( wrapper.theme, jsId ) );
    String themeCode = sb.toString();
    log( "-- REGISTERED THEME CODE FOR " + id + " --" );
    log( themeCode );
    log( "-- END REGISTERED THEME CODE --" );
// TODO [rst] Load only the default theme at startup
//    boolean loadOnStartup = defaultThemeId.equals( id );
    boolean loadOnStartup = true;
    registerJsLibrary( themeCode, jsId + ".js", loadOnStartup );
  }

  private void registerWidgetImages( final String id )
    throws IOException
  {
    ThemeWrapper wrapper = ( ThemeWrapper )themes.get( id );
    Theme theme = wrapper.theme;
    ResourceLoader themeLoader = wrapper.loader;
    ClassLoader classLoader = ThemeManager.class.getClassLoader();

    // non-themeable images
    log( " == register non-themeable images for theme " + id );
    for( int i = 0; i < WIDGET_NOTHEME_RESOURCES.length; i++ ) {
      String imagePath = WIDGET_NOTHEME_RESOURCES[ i ];
      InputStream inputStream = null;
      String res = WIDGET_RESOURCES_SRC + "/" + imagePath;
      inputStream = classLoader.getResourceAsStream( res );
      if( inputStream  == null ) {
        String mesg = "Resource not found: " + res;
        throw new IllegalArgumentException( mesg );
      }
      try {
        String jsId = getJsThemeId( id );
        String registerPath = getWidgetDestPath( jsId  ) + "/" + imagePath;
        ResourceManager.getInstance().register( registerPath, inputStream );
        log( " notheme image registered @ " + registerPath );
      } finally {
        inputStream.close();
      }
    }

    // themeable images
    log( " == register themeable images for theme " + id + ", loader: " + themeLoader );
    String[] keys = theme.getKeys();
    for( int i = 0; i < keys.length; i++ ) {
      String key = keys[ i ];
      Object value = theme.getValue( key );
      if( value instanceof QxImage ) {
        QxImage image = ( QxImage )value;
        String path = image.getPath();
        log( " register theme image " + key + ", " + path );
        InputStream inputStream;
        if( image.isNone() || "".equals( path ) ) {
          path = BLANK_IMAGE_PATH;
          inputStream = classLoader.getResourceAsStream( path );
        } else if( themeLoader == null ) {
          inputStream = classLoader.getResourceAsStream( path );
        } else {
          inputStream = themeLoader.getResourceAsStream( path );
        }
        if( inputStream == null ) {
          String pattern = "Resource ''{0}'' not found for theme ''{1}''";
          Object[] arguments = new Object[]{ path, theme.getName() };
          String mesg = MessageFormat.format( pattern, arguments  );
          throw new IllegalArgumentException( mesg );
        }
        try {
          String jsId = getJsThemeId( id );
          // TODO [rst] implement proper path join
          String widgetDestPath = getWidgetDestPath( jsId  );
          String targetPath = ( String )imageMapping.get( key );
          String registerPath = widgetDestPath + "/" + targetPath;
          ResourceManager.getInstance().register( registerPath, inputStream );
          log( " theme image registered @ " + registerPath );
        } finally {
          inputStream.close();
        }
      }
    }
  }

  private static void registerJsLibrary( final String code,
                                         final String name,
                                         final boolean loadOnStartup )
    throws IOException
  {
    ByteArrayInputStream resourceInputStream;
    byte[] buffer = code.getBytes( CHARSET );
    resourceInputStream = new ByteArrayInputStream( buffer );
    try {
      ResourceManager.getInstance().register( name,
                                              resourceInputStream,
                                              CHARSET,
                                              RegisterOptions.VERSION );
      IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
      HtmlResponseWriter responseWriter = stateInfo.getResponseWriter();
      if( loadOnStartup ) {
        responseWriter.useJSLibrary( name );
      }
    } finally {
      resourceInputStream.close();
    }
  }

  private static String createColorTheme( final Theme theme, final String id ) {
    ThemeWriter writer = new ThemeWriter( id,
                                          theme.getName(),
                                          ThemeWriter.COLOR );
    String[] keys = theme.getKeys();
    Arrays.sort( keys );
    for( int i = 0; i < keys.length; i++ ) {
      Object value = theme.getValue( keys[ i ] );
      if( value instanceof QxColor ) {
        QxColor color = ( QxColor )value;
        writer.writeColor( keys[ i ], color );
      }
    }
    return writer.getGeneratedCode();
  }

  private static String createBorderTheme( final Theme theme, final String id )
    throws IOException
  {
    ClassLoader classLoader = ThemeManager.class.getClassLoader();
    String resource = "org/eclipse/swt/theme/DefaultBorders.js";
    InputStream inStr = classLoader.getResourceAsStream( resource  );
    String content = readFromInputStream( inStr, "UTF-8" );
    String defaultBorders = stripTemplate( content );
    ThemeWriter writer = new ThemeWriter( id,
                                          theme.getName(),
                                          ThemeWriter.BORDER );
    writer.writeValues( defaultBorders.trim() );
    String[] keys = theme.getKeys();
    Arrays.sort( keys );
    for( int i = 0; i < keys.length; i++ ) {
      Object value = theme.getValue( keys[ i ] );
      if( value instanceof QxBorder ) {
        QxBorder border = ( QxBorder )value;
        writer.writeBorder( keys[ i ], border );
      }
    }
    return writer.getGeneratedCode();
  }

  private static String createFontTheme( final Theme theme, final String id ) {
    ThemeWriter writer = new ThemeWriter( id,
                                          theme.getName(),
                                          ThemeWriter.FONT );
    String[] keys = theme.getKeys();
    Arrays.sort( keys );
    for( int i = 0; i < keys.length; i++ ) {
      Object value = theme.getValue( keys[ i ] );
      if( value instanceof QxFont ) {
        QxFont font = ( QxFont )value;
        writer.writeFont( keys[ i ], font );
      }
    }
    return writer.getGeneratedCode();
  }

  private static String createWidgetTheme( final Theme theme, final String id ) {
    ThemeWriter writer = new ThemeWriter( id,
                                          theme.getName(),
                                          ThemeWriter.WIDGET );
    writer.writeUri( getWidgetDestPath( id ) );
    return writer.getGeneratedCode();
  }

  private static String createIconTheme( final Theme theme, final String id ) {
    ThemeWriter writer = new ThemeWriter( id,
                                          theme.getName(),
                                          ThemeWriter.ICON );
    writer.writeUri( getWidgetDestPath( id ) );
    return writer.getGeneratedCode();
  }

  private String createAppearanceTheme( final Theme theme,
                                        final String id )
    throws IOException
  {
    ClassLoader classLoader = ThemeManager.class.getClassLoader();
    String resource = "org/eclipse/swt/theme/DefaultAppearances.js";
    InputStream inStr = classLoader.getResourceAsStream( resource );
    String content = readFromInputStream( inStr, "UTF-8" );
    String template = stripTemplate( content );
    String appearances = substituteTemplate( template, theme );
    ThemeWriter writer = new ThemeWriter( id,
                                          theme.getName(),
                                          ThemeWriter.APPEARANCE );
    writer.writeValues( appearances );
    Iterator iterator = addAppearances.iterator();
    while( iterator.hasNext() ) {
      String addAppearance = ( String )iterator.next();
      writer.writeValues( substituteTemplate( addAppearance, theme ) );
    }
    return writer.getGeneratedCode();
  }

  private static String createMetaTheme( final Theme theme, final String id ) {
    ThemeWriter writer = new ThemeWriter( id, theme.getName(), ThemeWriter.META );
    writer.writeTheme( "color", id + "Colors" );
    writer.writeTheme( "border", id + "Borders" );
    writer.writeTheme( "font", id + "Fonts" );
    writer.writeTheme( "icon", id + "Icons" );
    writer.writeTheme( "widget", id + "Widgets" );
    writer.writeTheme( "appearance", id + "Appearances" );
    return writer.getGeneratedCode();
  }

  private static String getWidgetDestPath( final String id ) {
    int start = id.lastIndexOf( '.' ) + 1;
    int end = id.length();
    return THEME_RESOURCE_DEST + id.substring( start, end ) + "/widgets";
  }

  static String stripTemplate( final String input ) {
    Pattern startPattern = Pattern.compile( "BEGIN TEMPLATE.*(\\r|\\n)" );
    Pattern endPattern = Pattern.compile( "(\\r|\\n).*?END TEMPLATE" );
    int beginIndex = 0;
    int endIndex = input.length();
    Matcher matcher;
    matcher = startPattern.matcher( input );
    if( matcher.find() ) {
      beginIndex = matcher.end();
    }
    matcher = endPattern.matcher( input );
    if( matcher.find() ) {
      endIndex = matcher.start();
    }
    return input.substring( beginIndex, endIndex );
  }

  static String substituteTemplate( final String template,
                                    final Theme theme )
  {
    Matcher matcher = PATTERN_REPLACE.matcher( template );
    StringBuffer sb = new StringBuffer();
    while( matcher.find() ) {
      String key = matcher.group( 1 );
      QxType result = theme.getValue( key );
      String repl;
      if( result instanceof QxBoolean ) {
        QxBoolean bool = ( QxBoolean )result;
        repl = String.valueOf( bool.value );
      } else if( result instanceof QxDimension ) {
        QxDimension dim = ( QxDimension )result;
        repl = String.valueOf( dim.value );
      } else if( result instanceof QxBoxDimensions ) {
        QxBoxDimensions boxdim = ( QxBoxDimensions )result;
        repl = boxdim.toJsArray();
      } else {
        String mesg = "Only boolean values, dimensions, and box dimensions"
                      + " can be substituted in appearance templates";
        throw new IllegalArgumentException( mesg );
      }
      matcher.appendReplacement( sb, repl );
    }
    matcher.appendTail( sb );
    return sb.toString();
  }

  static String readFromInputStream( final InputStream is,
                                     final String charset )
    throws IOException
  {
    String result = null;
    if( is != null ) {
      try {
        StringBuffer sb = new StringBuffer();
        InputStreamReader reader = new InputStreamReader( is, charset );
        BufferedReader br = new BufferedReader( reader );
        for( int i = 0; i < 100; i++ ) {
          int character = br.read();
          while( character != -1 ) {
            sb.append( ( char )character );
            character = br.read();
          }
        }
        result = sb.toString();
      } finally {
        is.close();
      }
    }
    return result;
  }

  /**
   * Inserts the package path segment <code>internal</code> at every possible
   * position in a given package name.
   */
  // TODO [rh] seems to be a copy of LifeCycleAdapterFactory, unite if possible
  private static String[] getPackageVariants( final String packageName ) {
    String[] result;
    if( packageName == null || "".equals( packageName ) ) {
      result = new String[] { "internal" };
    } else {
      String[] segments = packageName.split( "\\." );
      result = new String[ segments.length + 1 ];
      for( int i = 0; i < result.length; i++ ) {
        StringBuffer buffer = new StringBuffer();
        for( int j = 0; j < segments.length; j++ ) {
          if( j == i ) {
            buffer.append( "internal." );
          }
          buffer.append( segments[ j ] );
          if( j < segments.length - 1 ) {
            buffer.append( "." );
          }
        }
        if( i == segments.length ) {
          buffer.append( ".internal" );
        }
        result[ i ] = buffer.toString();
      }
    }
    return result;
  }

  /**
   * For a given full class name, this method returns the class name without
   * package prefix.
   */
  // TODO [rst] Copy of LifeCycleAdapterFactory, move to a utility class?
  private static String getSimpleClassName( final Class clazz ) {
    String className = clazz.getName();
    int idx = className.lastIndexOf( '.' );
    return className.substring( idx + 1 );
  }

  private void checkId( final String id ) {
    if( id == null ) {
      throw new NullPointerException( "id" );
    }
    if( id.length() == 0 ) {
      throw new IllegalArgumentException( "empty id" );
    }
  }

  private void logRegisteredAdapters() {
    log( "=== REGISTERED ADAPTERS ===" );
    Iterator iter = adapters.keySet().iterator();
    while( iter.hasNext() ) {
      Class key = ( Class )iter.next();
      Object adapter = adapters.get( key );
      log( key.getName() + ": " + adapter );
    }
    log( "=== END REGISTERED ADAPTERS ===" );
  }

  // TODO [rst] Replace with Logger calls
  private static void log( final String mesg ) {
    if( DEBUG ) {
      System.out.println( mesg );
    }
  }
}
