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

import org.eclipse.rwt.internal.lifecycle.HtmlResponseWriter;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.rwt.internal.theme.ThemeDefinitionReader.ThemeDef;
import org.eclipse.rwt.internal.theme.ThemeDefinitionReader.ThemeDefHandler;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;
import org.eclipse.swt.widgets.Widget;
import org.xml.sax.SAXException;


/**
 * The ThemeManager is responsible for reading custom themes and installing them
 * on the client.
 */
public final class ThemeManager {

  /**
   * Internal wrapper class to aggregate all information related to a registered
   * theme. Used as value type for themes map.
   */
  private static final class ThemeWrapper {
    final Theme theme;
    final ResourceLoader loader;
    final int count;
    ThemeWrapper( final Theme theme,
                  final ResourceLoader loader,
                  final int count )
    {
      this.theme = theme;
      this.loader = loader;
      this.count = count;
    }
  }

  /** Expected character set of JS files. */
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
    "ctabfolder/maximize.gif",
    "ctabfolder/minimize.gif",
    "ctabfolder/restore.gif",
    "ctabfolder/close.gif",
    "ctabfolder/close_hover.gif",
    "ctabfolder/chevron.gif",
    "cursors/alias.gif",
    "cursors/copy.gif",
    "cursors/move.gif",
    "cursors/nodrop.gif",
    "menu/checkbox.gif",
    "menu/menu-blank.gif",
    "menu/radiobutton.gif",
    "table/check_white_on.gif",
    "table/check_white_off.gif",
    "table/check_gray_on.gif",
    "table/check_gray_off.gif",
    "tree/cross.gif",
    "tree/cross_minus.gif",
    "tree/cross_plus.gif",
    "tree/end.gif",
    "tree/end_minus.gif",
    "tree/end_plus.gif",
    "tree/folder_open.gif",
    "tree/folder_closed.gif",
    "tree/line.gif",
    "tree/minus.gif",
    "tree/only_minus.gif",
    "tree/only_plus.gif",
    "tree/plus.gif",
    "tree/start_minus.gif",
    "tree/start_plus.gif",
  };

  /** Where to load the default non-themeable images from */
  private static final String WIDGET_RESOURCES_SRC = "resource/widget/rap";

  private static final String BLANK_IMAGE_PATH
    = "resource/static/image/blank.gif";

  /** Destination path for theme resources, contains trailing path separator. */
  private static final String THEME_RESOURCE_DEST = "resource/themes/";

  private static final String THEME_PREFIX = "org.eclipse.swt.theme.";
  
  private static final String PREDEFINED_THEME_ID = THEME_PREFIX + "Default";

  private static final String PREDEFINED_THEME_NAME = "RAP Default Theme";

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

  private static ThemeManager instance;

  private boolean initialized;

  private final List customWidgets;
  
  private final List addAppearances;
  
  private final Map themeDefs;
  
  private final Map themes;
  
  private final Map adapters;
  
  private final Map imageMapping;
  
  private Theme predefinedTheme;
  
  private final Set registeredThemeFiles;

  private int themeCount;

// TODO [rst] Evaluate timestamp approach to separate different versions of resources
//  private String timestamp;

  private ThemeManager() {
    // prevent instantiation from outside
    initialized = false;
    customWidgets = new ArrayList();
    addAppearances = new ArrayList();
    themeDefs = new LinkedHashMap();
    themes = new HashMap();
    adapters = new HashMap();
    imageMapping = new HashMap();
    registeredThemeFiles = new HashSet();
//    timestamp = createTimeStamp();
  }

//  private String createTimeStamp() {
//    return new SimpleDateFormat( "yyyyMMddHHmm" ).format( new Date() );
//  }

  /**
   * Returns the sole instance of the ThemeManager.
   * @return the ThemeManager instance
   */
  public static ThemeManager getInstance() {
    if( instance == null ) {
      instance = new ThemeManager();
    }
    return instance;
  }

  /**
   * Initializes the ThemeManager. Theming-relevant files are loaded for all
   * themeable widgets, resources are registered. If the ThemeManager has
   * already been initialized, no action is taken.
   */
  public void initialize() {
    log( "____ ThemeManager intialize" );
    if( !initialized ) {
      for( int i = 0; i < THEMEABLE_WIDGETS.length; i++ ) {
        processThemeableWidget( THEMEABLE_WIDGETS[ i ] );
      }
      Iterator widgetIter = customWidgets.iterator();
      while( widgetIter.hasNext() ) {
        processThemeableWidget( ( Class )widgetIter.next() );
      }
      // initialize predefined theme
      predefinedTheme = new Theme( PREDEFINED_THEME_NAME );
      Iterator iterator = themeDefs.keySet().iterator();
      while( iterator.hasNext() ) {
        String key = ( String )iterator.next();
        ThemeDef def = ( ThemeDef )themeDefs.get( key );
        predefinedTheme.setValue( key, def.defValue );
      }
      themes.put( PREDEFINED_THEME_ID,
                  new ThemeWrapper( predefinedTheme, null, themeCount++ ) );
      initialized = true;
      logRegisteredThemeAdapters();
      
    }
  }
  
  /**
   * Resets the ThemeManager. All registered themes and themeable widgets are
   * de-registered. If the ThemeManager is not initialized, nothing happens.
   * After this method has been called the ThemeManager is no longer
   * initialized.
   */
  public void reset() {
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
  
  /**
   * Adds a custom widget to the list of themeable widgets. Note that this
   * method must be called <em>before</em> initializing the ThemeManager.
   * 
   * @param widget the themeable widget to add, must not be <code>null</code>
   * @throws IllegalStateException if the ThemeManager is already initialized
   */
  public void addThemeableWidget( final Class widget ) {
    if( initialized ) {
      throw new IllegalStateException( "ThemeManager is already initialized" );
    }
    if( widget == null ) {
      throw new NullPointerException( "null argument" );
    }
    if( !Widget.class.isAssignableFrom( widget ) ) {
      String message = "Themeable widget is not a subtype of Widget: "
                       + widget.getName();
      throw new IllegalArgumentException( message );
    }
    if( !customWidgets.contains( widget ) ) {
      customWidgets.add( widget );
    }
  }

  /**
   * Registers a theme from an input stream. Note that <code>initialize()</code>
   * must be called first.
   * 
   * @param id an id that identifies the theme in the Java code. Note that this
   *            id is not valid on the client-side. To get the id that is used
   *            on the client, see method <code>getJsThemeId</code>
   * @param name a name that describes the theme. Currently not used
   * @param instr an input stream to read the theme from
   * @param loader a ResourceLoader instance that is able to load resources
   *            needed by this theme
   * @throws IOException if an I/O error occurs
   * @throws IllegalStateException if not initialized
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
    Theme theme = loadThemeFile( name != null ? name : "", instr );
    themes.put( id, new ThemeWrapper( theme, loader, themeCount++  ) );
  }
  
  /**
   * Generates and registers JavaScript code that installs the registered themes
   * on the client.
   * 
   * @throws IllegalStateException if not initialized
   */
  public void registerResources() {
    checkInitialized();
    log( "____ ThemeManager register resources" );
    registerJsLibrary( "org/eclipse/swt/theme/BordersBase.js" );
    registerJsLibrary( "org/eclipse/swt/theme/AppearancesBase.js" );
    registerJsLibrary( "org/eclipse/swt/theme/Dimensions.js" );
    Iterator iterator = themes.keySet().iterator();
    while( iterator.hasNext() ) {
      String id = ( String )iterator.next();
      registerThemeFiles( id );
    }
  }
  
  /**
   * Determines whether a theme with the specified id has been registered.
   * 
   * @param themeId the id to check for
   * @return <code>true</code> if a theme has been registered with the given
   *         id
   * @throws IllegalStateException if not initialized
   */
  public boolean hasTheme( final String themeId ) {
    checkInitialized();
    return themes.containsKey( themeId );
  }

  /**
   * Returns the theme registered with the given id.
   * 
   * @param themeId the id of the theme to retrieve
   * @return the theme registered with the given id or <code>null</code> if
   *         there is no theme registered with this id
   * @throws IllegalStateException if not initialized
   */
  public Theme getTheme( final String themeId ) {
    checkInitialized();
    Theme result = null;
    if( hasTheme( themeId ) ) {
      ThemeWrapper wrapper = ( ThemeWrapper )themes.get( themeId );
      result = wrapper.theme;
    }
    return result;
  }

  /**
   * Returns a list of all registered themes.
   * 
   * @return an array that contains the ids of all registered themes, never
   *         <code>null</code>
   * @throws IllegalStateException if not initialized
   */
  public String[] getRegisteredThemeIds() {
    checkInitialized();
    String[] result = new String[ themes.size() ];
    return ( String[] )themes.keySet().toArray( result );
  }

  /**
   * Returns the id of the default theme.
   * 
   * @return the id of the default theme
   * @throws IllegalStateException if not initialized
   */
  public String getDefaultThemeId() {
    checkInitialized();
    return PREDEFINED_THEME_ID;
  }

  /**
   * Determines whether there is a theme adapter registered for controls of the
   * specified type.
   * 
   * @param controlClass the class to check for
   * @return <code>true</code> if a theme adapter has been registered for the
   *         given class
   * @throws IllegalStateException if not initialized
   */
  public boolean hasThemeAdapter( final Class controlClass ) {
    checkInitialized();
    return adapters.containsKey( controlClass );
  }

  /**
   * Returns the theme adapter to use for controls of the specified type.
   * 
   * @param widgetClass
   * @return the theme adapter
   * @throws IllegalStateException if not initialized
   */
  public IThemeAdapter getThemeAdapter( final Class widgetClass ) {
    checkInitialized();
    IThemeAdapter result = null;
    if( hasThemeAdapter( widgetClass ) ) {
      result = ( IThemeAdapter )adapters.get( widgetClass );
    } else {
      Class clazz = widgetClass;
      while(    clazz != null
             && clazz != Widget.class
             && !hasThemeAdapter( clazz ) )
      {
        clazz = clazz.getSuperclass();
      }
      if( hasThemeAdapter( clazz ) ) {
        result = ( IThemeAdapter )adapters.get( clazz );
        adapters.put( widgetClass, result );
      } else {
        String msg = "No theme adapter registered for class "
                                            + widgetClass.getName();
        throw new IllegalArgumentException( msg );
      }
    }
    return result;
  }

  /**
   * Returns the generated theme id to use on the client side which differs from
   * the theme id that is given in the extension.
   * 
   * @param id the theme id as specified in the theme extension
   * @return a generated id for use on the client
   * @throws IllegalStateException if not initialized
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
   * 
   * @param args ignored
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
   */
  private void processThemeableWidget( final Class widgetClass ) {
    log( "Processing widget: " + widgetClass.getName() );
    String[] variants = getPackageVariants( widgetClass.getPackage().getName() );
    String className = getSimpleClassName( widgetClass );
    ClassLoader loader = widgetClass.getClassLoader();
    boolean found = false;
    try {
      for( int i = 0; i < variants.length && !found ; i++ ) {
        String pkgName = variants[ i ] + "." + className.toLowerCase() + "kit";
        // TODO [rst] Is it possible to recognize whether a package exists?
        log( "looking through package " + pkgName );
        found |= loadThemeDef( loader, pkgName, className );
        found |= loadAppearanceJs( loader, pkgName, className );
        found |= loadThemeAdapter( loader, pkgName, className, widgetClass );
      }
    } catch( final IOException e ) {
      String message = "Failed to initialize themeable widget "
                       + widgetClass.getName();
      throw new ThemeManagerException( message, e );
    }
  }

  private boolean loadThemeDef( final ClassLoader loader,
                                final String pkgName,
                                final String className ) throws IOException
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
        String message = "Failed to parse theme definition file " + fileName;
        throw new IllegalArgumentException( message );
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
    } catch( final InstantiationException e ) {
      String message = "Failed to instantiate theme adapter class "
                       + adapterClassName;
      throw new ThemeManagerException( message, e );
    } catch( final IllegalAccessException e ) {
      String message = "Failed to instantiate theme adapter class "
                       + adapterClassName;
      throw new ThemeManagerException( message, e );
    }
    return result;
  }

  /**
   * Loads a theme from a <code>theme.properties</code> file.
   * 
   * @param name the name for the theme to create, must not be <code>null</code>
   * @param inStr the input stream of the theme file to read
   * @return the newly created theme
   */
  private Theme loadThemeFile( final String name, final InputStream inStr )
    throws IOException
  {
    if( inStr == null ) {
      throw new IllegalArgumentException( "null argument" );
    }
    Theme newTheme = new Theme( name, predefinedTheme );
    Properties properties = new Properties();
    properties.load( inStr );
    Iterator iterator = properties.keySet().iterator();
    while( iterator.hasNext() ) {
      String key = ( ( String )iterator.next() ).trim();
      QxType defValue = predefinedTheme.getValue( key );
      if( defValue == null ) {
        throw new IllegalArgumentException( "Invalid key for themeing: " + key );
      }
      String value = ( String )properties.get( key );
      if( value != null && value.trim().length() > 0 ) {
        QxType newValue;
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
        } else {
          // TODO [rst] How to handle programming errors?
          throw new RuntimeException( "unknown type" );
        }
        newTheme.setValue( key, newValue );
      }
    }
    return newTheme;
  }

  /**
   * Creates and registers all JavaScript theme files and images for a given
   * theme.
   * 
   * @param themeId the theme id
   */
  private void registerThemeFiles( final String themeId ) {
    synchronized( registeredThemeFiles ) {
      if( !registeredThemeFiles.contains( themeId ) ) {
        ThemeWrapper wrapper = ( ThemeWrapper )themes.get( themeId );
        String jsId = getJsThemeId( themeId );
        registerNonThemeableWidgetImages( themeId );
        registerThemeableWidgetImages( themeId );
        StringBuffer sb = new StringBuffer();
        sb.append( createColorTheme( wrapper.theme, jsId ) );
        sb.append( createBorderTheme( wrapper.theme, jsId ) );
        sb.append( createFontTheme( wrapper.theme, jsId ) );
        sb.append( createIconTheme( wrapper.theme, jsId ) );
        sb.append( createWidgetTheme( wrapper.theme, jsId ) );
        sb.append( createAppearanceTheme( wrapper.theme, jsId ) );
        sb.append( createMetaTheme( wrapper.theme, jsId ) );
        sb.append( createDimensionValues( wrapper.theme, jsId ) );
        String themeCode = sb.toString();
        log( "-- REGISTERED THEME CODE FOR " + themeId + " --" );
        log( themeCode );
        log( "-- END REGISTERED THEME CODE --" );
        registerJsLibrary( themeCode, jsId.replace( '.', '/' ) + ".js" );
        registeredThemeFiles.add( themeId );
      }
    }
  }
  
  private void registerNonThemeableWidgetImages( final String id ) {
    ClassLoader classLoader = ThemeManager.class.getClassLoader();
    // non-themeable images
    log( " == register non-themeable images for theme " + id );
    for( int i = 0; i < WIDGET_NOTHEME_RESOURCES.length; i++ ) {
      String imagePath = WIDGET_NOTHEME_RESOURCES[ i ];
      InputStream inputStream = null;
      String res = WIDGET_RESOURCES_SRC + "/" + imagePath;
      inputStream = classLoader.getResourceAsStream( res );
      if( inputStream == null ) {
        String mesg = "Resource not found: " + res;
        throw new IllegalArgumentException( mesg );
      }
      try {
        String jsId = getJsThemeId( id );
        String registerPath = getWidgetDestPath( jsId  ) + "/" + imagePath;
        IResourceManager resMgr = ResourceManager.getInstance();
        resMgr.register( registerPath, inputStream );
        String location = resMgr.getLocation( registerPath );
        log( " notheme image registered @ " + location );
      } finally {
        try {
          inputStream.close();
        } catch( final IOException e ) {
          throw new RuntimeException( e );
        }
      }
    }
  }
  
  private void registerThemeableWidgetImages( final String themeId ) {
    ThemeWrapper wrapper = ( ThemeWrapper )themes.get( themeId );
    Theme theme = wrapper.theme;
    ResourceLoader themeLoader = wrapper.loader;
    ClassLoader classLoader = ThemeManager.class.getClassLoader();
    // themeable images
    log( " == register themeable images for theme " + themeId + ", loader: " + themeLoader );
    String[] keys = theme.getKeys();
    for( int i = 0; i < keys.length; i++ ) {
      String key = keys[ i ];
      Object value = theme.getValue( key );
      if( value instanceof QxImage ) {
        QxImage image = ( QxImage )value;
        String path = image.getPath();
        log( " register theme image " + key + ", " + path );
        InputStream inputStream;
        if( image.isNone() ) {
          // use blank image
          path = BLANK_IMAGE_PATH;
          inputStream = classLoader.getResourceAsStream( path );
        } else if( !theme.definesKey( key ) || themeLoader == null ) {
          // use default image
          inputStream = classLoader.getResourceAsStream( path );
        } else {
          // use custom image
          try {
            inputStream = themeLoader.getResourceAsStream( path );
          } catch( final IOException e ) {
            String message = "Failed to load resource " + path;
            throw new ThemeManagerException( message, e );
          }
        }
        if( inputStream == null ) {
          String pattern = "Resource ''{0}'' not found for theme ''{1}''";
          Object[] arguments = new Object[]{ path, theme.getName() };
          String mesg = MessageFormat.format( pattern, arguments  );
          throw new IllegalArgumentException( mesg );
        }
        try {
          String jsId = getJsThemeId( themeId );
          // TODO [rst] implement proper path join
          String widgetDestPath = getWidgetDestPath( jsId  );
          String targetPath = ( String )imageMapping.get( key );
          if( targetPath == null ) {
            targetPath = key;
          }
          String registerPath = widgetDestPath + "/" + targetPath;
          IResourceManager resMgr = ResourceManager.getInstance();
          resMgr.register( registerPath, inputStream );
          String location = resMgr.getLocation( registerPath );
          log( " theme image registered @ " + location );
        } finally {
          try {
            inputStream.close();
          } catch( final IOException e ) {
            throw new RuntimeException( e );
          }
        }
      }
    }
  }

  private static void registerJsLibrary( final String name ) {
    ResourceManager.getInstance().register( name,
                                            CHARSET,
                                            RegisterOptions.VERSION );
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter responseWriter = stateInfo.getResponseWriter();
    responseWriter.useJSLibrary( name );
  }

  private static void registerJsLibrary( final String code, final String name )
  {
    ByteArrayInputStream resourceInputStream;
    byte[] buffer;
    try {
      buffer = code.getBytes( CHARSET );
    } catch( final UnsupportedEncodingException e ) {
      throw new RuntimeException( e );
    }
    resourceInputStream = new ByteArrayInputStream( buffer );
    try {
      ResourceManager.getInstance().register( name,
                                              resourceInputStream,
                                              CHARSET,
                                              RegisterOptions.VERSION );
      IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
      HtmlResponseWriter responseWriter = stateInfo.getResponseWriter();
      responseWriter.useJSLibrary( name );
    } finally {
      try {
        resourceInputStream.close();
      } catch( final IOException e ) {
        throw new RuntimeException( e );
      }
    }
  }

  private static String createColorTheme( final Theme theme, final String id ) {
    QxTheme colorTheme = new QxTheme( id, theme.getName(), QxTheme.COLOR );
    String[] keys = theme.getKeys();
    Arrays.sort( keys );
    for( int i = 0; i < keys.length; i++ ) {
      Object value = theme.getValue( keys[ i ] );
      if( value instanceof QxColor ) {
        QxColor color = ( QxColor )value;
        colorTheme.appendColor( keys[ i ], color );
      }
    }
    return colorTheme.getJsCode();
  }

  private static String createBorderTheme( final Theme theme, final String id )
  {
    String base = THEME_PREFIX + "BordersBase";
    QxTheme borderTheme = new QxTheme( id,
                                       theme.getName(),
                                       QxTheme.BORDER,
                                       base );
    String[] keys = theme.getKeys();
    Arrays.sort( keys );
    for( int i = 0; i < keys.length; i++ ) {
      Object value = theme.getValue( keys[ i ] );
      if( value instanceof QxBorder ) {
        QxBorder border = ( QxBorder )value;
        borderTheme.appendBorder( keys[ i ], border );
      }
    }
    return borderTheme.getJsCode();
  }

  private static String createFontTheme( final Theme theme, final String id ) {
    QxTheme fontTheme = new QxTheme( id, theme.getName(), QxTheme.FONT );
    String[] keys = theme.getKeys();
    Arrays.sort( keys );
    for( int i = 0; i < keys.length; i++ ) {
      Object value = theme.getValue( keys[ i ] );
      if( value instanceof QxFont ) {
        QxFont font = ( QxFont )value;
        fontTheme.appendFont( keys[ i ], font );
      }
    }
    return fontTheme.getJsCode();
  }

  private String createWidgetTheme( final Theme theme, final String id ) {
    QxTheme widgetTheme = new QxTheme( id, theme.getName(), QxTheme.WIDGET );
    widgetTheme.appendUri( getWidgetDestPath( id ) );
    return widgetTheme.getJsCode();
  }

  private String createIconTheme( final Theme theme, final String id ) {
    QxTheme iconTheme = new QxTheme( id, theme.getName(), QxTheme.ICON );
    iconTheme.appendUri( getWidgetDestPath( id ) );
    return iconTheme.getJsCode();
  }

  private String createAppearanceTheme( final Theme theme, final String id ) {
    QxTheme appTheme = new QxTheme( id,
                                    theme.getName(),
                                    QxTheme.APPEARANCE,
                                    "org.eclipse.swt.theme.AppearancesBase" );
    Iterator iterator = addAppearances.iterator();
    while( iterator.hasNext() ) {
      String addAppearance = ( String )iterator.next();
      appTheme.appendValues( substituteMacros( addAppearance, theme ) );
    }
    return appTheme.getJsCode();
  }

  private static String createMetaTheme( final Theme theme, final String id ) {
    QxTheme metaTheme = new QxTheme( id, theme.getName(), QxTheme.META );
    metaTheme.appendTheme( "color", id + "Colors" );
    metaTheme.appendTheme( "border", id + "Borders" );
    metaTheme.appendTheme( "font", id + "Fonts" );
    metaTheme.appendTheme( "icon", id + "Icons" );
    metaTheme.appendTheme( "widget", id + "Widgets" );
    metaTheme.appendTheme( "appearance", id + "Appearances" );
    return metaTheme.getJsCode();
  }
  
  private static String createDimensionValues( final Theme theme,
                                               final String jsId )
  {
    StringBuffer sb = new StringBuffer();
    String[] keys = theme.getKeys();
    Arrays.sort( keys );
    sb.append( "dim = org.eclipse.swt.theme.Dimensions.getInstance();\n" );
    for( int i = 0; i < keys.length; i++ ) {
      Object value = theme.getValue( keys[ i ] );
      String key = keys[ i ];
      if( value instanceof QxDimension ) {
        QxDimension dim = ( QxDimension )value;
        String val = String.valueOf( dim.value );
        sb.append( "dim.set( \"" + key + "\", \"" + jsId + "\", " + val + " );\n" );
      } else if( value instanceof QxBoxDimensions ) {
        QxBoxDimensions boxdim = ( QxBoxDimensions )value;
        String val = String.valueOf( boxdim.toJsArray() );
        sb.append( "dim.set( \"" + key + "\", \"" + jsId + "\", " + val + " );\n" );
      }
    }
    sb.append( "delete dim;\n" );
    return sb.toString();
  }

  /**
   * Returns the client side widget path, i.e. the path to which qooxdoo icon
   * resources starting with "widget/" are mapped.
   * 
   * @param jsThemeId the theme id
   */
  private String getWidgetDestPath( final String jsThemeId ) {
    int start = jsThemeId.lastIndexOf( '.' ) + 1;
    int end = jsThemeId.length();
    String jsThemeName = jsThemeId.substring( start, end );
//    return THEME_RESOURCE_DEST + jsThemeName + "/widgets-" + timestamp;
    return THEME_RESOURCE_DEST + jsThemeName + "/widgets";
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

  /**
   * Replaces all THEME_VALUE() macros in a given template with the actual
   * values from the specified theme.
   * 
   * @param template the template string that contains the macros to replace
   * @param theme the theme to get the values from
   * @return the template string with all macros replaced
   */
  static String substituteMacros( final String template,
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

  private void logRegisteredThemeAdapters() {
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
