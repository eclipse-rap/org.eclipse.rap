/*******************************************************************************
 * Copyright (c) 2007, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;

import org.eclipse.rwt.internal.lifecycle.HtmlResponseWriter;
import org.eclipse.rwt.internal.lifecycle.LifeCycleAdapterUtil;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.IServiceStateInfo;
import org.eclipse.rwt.internal.theme.css.*;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;
import org.eclipse.swt.widgets.Widget;
import org.w3c.css.sac.CSSException;

/**
 * The ThemeManager maintains information about the themeable widgets and the
 * installed themes.
 */
public final class ThemeManager {

  private static final ResourceLoader STANDARD_RESOURCE_LOADER
    = new ResourceLoader()
  {

      ClassLoader classLoader = getClass().getClassLoader();

      public InputStream getResourceAsStream( final String resourceName )
        throws IOException
      {
        return classLoader.getResourceAsStream( resourceName );
      }
    };

  /** Expected character set of JS files. */
  private static final String CHARSET = "UTF-8";

  private static final boolean DEBUG
    = "true".equals( System.getProperty( ThemeManager.class.getName() + ".log" ) );

  private static final String CLIENT_LIBRARY_VARIANT
    = "org.eclipse.rwt.clientLibraryVariant";

  private static final String DEBUG_CLIENT_LIBRARY_VARIANT = "DEBUG";

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
    "scale/h_line.gif",
    "scale/v_line.gif",
    "scale/h_thumb.gif",
    "scale/v_thumb.gif",
    "scale/h_marker_big.gif",
    "scale/v_marker_big.gif",
    "scale/h_marker_small.gif",
    "scale/v_marker_small.gif",
  };

  /** Where to load the default non-themeable images from */
  private static final String WIDGET_RESOURCES_SRC = "resource/widget/rap";

  /** Destination path for theme resources */
  private static final String THEME_RESOURCE_DEST = "resource/themes";

  private static final String THEME_PREFIX = "org.eclipse.swt.theme.";

  private static final String PREDEFINED_THEME_ID = THEME_PREFIX + "Default";

  private static final String PREDEFINED_THEME_NAME = "RAP Default Theme";

  private static final Class[] THEMEABLE_WIDGETS = new Class[]{
    org.eclipse.swt.widgets.Widget.class,
    org.eclipse.swt.widgets.Control.class,
    org.eclipse.swt.widgets.Button.class,
    org.eclipse.swt.widgets.Combo.class,
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
    org.eclipse.swt.widgets.Scale.class,
    org.eclipse.swt.widgets.DateTime.class,
    org.eclipse.swt.widgets.ExpandBar.class,
    org.eclipse.swt.widgets.Sash.class,
    org.eclipse.swt.widgets.Slider.class,
    org.eclipse.swt.custom.CCombo.class
  };

  private static ThemeManager instance;

  private final Set customAppearances;

  private final Map themes;

  private final Map adapters;

  private final Set registeredThemeFiles;

  private boolean initialized;

  private Theme predefinedTheme;

  private ThemeableWidgetHolder themeableWidgets;

  private StyleSheetBuilder defaultStyleSheetBuilder;

  private int themeCount;

  private CssElementHolder registeredCssElements;

  private ThemeManager() {
    // prevent instantiation from outside
    initialized = false;
    themeableWidgets = new ThemeableWidgetHolder();
    customAppearances = new HashSet();
    themes = new LinkedHashMap();
    adapters = new HashMap();
    registeredThemeFiles = new HashSet();
    registeredCssElements = new CssElementHolder();
    defaultStyleSheetBuilder = new StyleSheetBuilder();
  }

  /**
   * Returns the sole instance of the ThemeManager.
   *
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
    log( "Start ThemeManager initialization" );
    if( !initialized ) {
      addDefaultThemableWidgets();
      // initialize themeable widgets
      ThemeableWidget[] widgets = themeableWidgets.getAll();
      for( int i = 0; i < widgets.length; i++ ) {
        processThemeableWidget( widgets[ i ] );
      }
      // initialize predefined theme
      StyleSheet defaultStyleSheet = defaultStyleSheetBuilder.getStyleSheet();
      predefinedTheme = new Theme( PREDEFINED_THEME_NAME, defaultStyleSheet );
      predefinedTheme.initValuesMap( themeableWidgets.getAll() );
      predefinedTheme.setJsId( PREDEFINED_THEME_ID );
      themes.put( PREDEFINED_THEME_ID, predefinedTheme );
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
      themeableWidgets = new ThemeableWidgetHolder();
      customAppearances.clear();
      themes.clear();
      adapters.clear();
      registeredCssElements.clear();
      defaultStyleSheetBuilder = new StyleSheetBuilder();
      predefinedTheme = null;
      initialized = false;
      log( "ThemeManager reset" );
    }
  }

  /**
   * Adds a custom widget to the list of themeable widgets. Note that this
   * method must be called <em>before</em> initializing the ThemeManager.
   * 
   * @param widget the themeable widget to add, must not be <code>null</code>
   * @param loader the resource loader used to load theme resources like theme
   *          definitions etc. The resources to load follow a naming convention
   *          and must be resolved by the class loader. This argument must not
   *          be <code>null</code>.
   * @throws IllegalStateException if the ThemeManager is already initialized
   * @throws NullPointerException if a parameter is null
   * @throws IllegalArgumentException if the given widget is not a subtype of
   *           {@link Widget}
   */
  public void addThemeableWidget( final Class widget,
                                  final ResourceLoader loader )
  {
    if( initialized ) {
      throw new IllegalStateException( "ThemeManager is already initialized" );
    }
    if( widget == null ) {
      throw new NullPointerException( "widget" );
    }
    if( loader == null ) {
      throw new NullPointerException( "loader" );
    }
    if( !Widget.class.isAssignableFrom( widget ) ) {
      String message =   "Themeable widget is not a subtype of Widget: "
                       + widget.getName();
      throw new IllegalArgumentException( message );
    }
    themeableWidgets.add( new ThemeableWidget( widget, loader ) );
  }

  /**
   * Registers a theme from an input stream. Note that <code>initialize()</code>
   * must be called first.
   * 
   * @param id an id that identifies the theme in the Java code. Note that this
   *          id is not valid on the client-side. To get the id that is used on
   *          the client, see method <code>getJsThemeId</code>
   * @param name a name that describes the theme. Currently not used.
   * @param fileName the filename of the theme file
   * @param loader a resource loader that will load theme resources by path
   * @throws IllegalStateException if not initialized
   * @throws NullPointerException if one of the parameters id, fileName, or
   *           loader is <code>null</code>
   * @throws IllegalArgumentException if parameter id is empty
   * @throws IOException if an I/O error occurs while reading the theme file
   * @throws ThemeManagerException if the CSS file cannot be parsed
   */
  public void registerTheme( final String id,
                             final String name,
                             final String fileName,
                             final ResourceLoader loader )
    throws IOException
  {
    checkInitialized();
    log( "Register theme " + id + " from " + fileName );
    checkId( id );
    if( fileName == null ) {
      throw new NullPointerException( "fileName" );
    }
    if( loader == null ) {
      throw new NullPointerException( "loader" );
    }
    if( themes.containsKey( id ) ) {
      String pattern = "Theme with id ''{0}'' exists already";
      Object[] arguments = new Object[] { id };
      String msg = MessageFormat.format( pattern, arguments );
      throw new IllegalArgumentException( msg );
    }
    themeCount++;
    String jsId = THEME_PREFIX + "Custom_" + themeCount;
    InputStream inputStream = loader.getResourceAsStream( fileName );
    if( inputStream == null ) {
      throw new IllegalArgumentException(   "Could not open resource "
                                          + fileName );
    }
    try {
      Theme theme;
      CssFileReader reader = new CssFileReader();
      try {
        StyleSheet styleSheet = reader.parse( inputStream, fileName, loader );
        theme = new Theme( name != null ? name : jsId, styleSheet );
      } catch( CSSException e ) {
        throw new ThemeManagerException( "Failed parsing CSS file", e );
      }
      theme.initValuesMap( themeableWidgets.getAll() );
      theme.setJsId( jsId );
      themes.put( id, theme );
    } finally {
      inputStream.close();
    }
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
   * @return the id of the default theme, never <code>null</code>
   * @throws IllegalStateException if not initialized
   */
  public String getDefaultThemeId() {
    checkInitialized();
    return PREDEFINED_THEME_ID;
  }

  /**
   * Generates and registers JavaScript code that installs the registered themes
   * on the client.
   *
   * @throws IllegalStateException if not initialized
   */
  public void registerResources() {
    checkInitialized();
    log( "ThemeManager registers resources" );
    String libraryVariant = System.getProperty( CLIENT_LIBRARY_VARIANT );
    boolean compress = !DEBUG_CLIENT_LIBRARY_VARIANT.equals( libraryVariant );
    registerJsLibrary( "org/eclipse/swt/theme/AppearancesBase.js", compress );
    registerJsLibrary( "org/eclipse/swt/theme/BordersBase.js", compress );
    registerJsLibrary( "org/eclipse/swt/theme/ThemeStore.js", compress );
    registerJsLibrary( "org/eclipse/swt/theme/ThemeValues.js", compress );
    Iterator iterator = themes.keySet().iterator();
    while( iterator.hasNext() ) {
      String themeId = ( String )iterator.next();
      registerThemeFiles( themeId, compress );
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
    if( themes.containsKey( themeId ) ) {
      result = ( Theme )themes.get( themeId );
    }
    return result;
  }

  /**
   * Returns the theme adapter to use for controls of the specified type.
   * 
   * @param widgetClass
   * @return the theme adapter
   * @throws IllegalStateException if not initialized
   * @throws IllegalArgumentException if no theme adapter has been registered
   *           for the given widget
   */
  public IThemeAdapter getThemeAdapter( final Class widgetClass ) {
    checkInitialized();
    IThemeAdapter result = null;
    if( adapters.containsKey( widgetClass ) ) {
      result = ( IThemeAdapter )adapters.get( widgetClass );
    } else {
      Class clazz = widgetClass;
      while(    clazz != null
             && clazz != Widget.class
             && !adapters.containsKey( clazz ) )
      {
        clazz = clazz.getSuperclass();
      }
      if( adapters.containsKey( clazz ) ) {
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

  ThemeableWidget getThemeableWidget( final Class widget ) {
    return themeableWidgets.get( widget );
  }

  private void checkInitialized() {
    if( !initialized ) {
      throw new IllegalStateException( "ThemeManager not initialized" );
    }
  }

  private void addDefaultThemableWidgets() {
    for( int i = 0; i < THEMEABLE_WIDGETS.length; i++ ) {
      addThemeableWidget( THEMEABLE_WIDGETS[ i ], STANDARD_RESOURCE_LOADER );
    }
  }

  /**
   * Loads and processes all theme-relevant resources for a given widget.
   *
   * @param themeWidget the widget to process
   */
  private void processThemeableWidget( final ThemeableWidget themeWidget )
  {
    log( "Processing widget: " + themeWidget.widget.getName() );
    String packageName = themeWidget.widget.getPackage().getName();
    String[] variants = LifeCycleAdapterUtil.getPackageVariants( packageName );
    String className
      = LifeCycleAdapterUtil.getSimpleClassName( themeWidget.widget );
    boolean found = false;
    try {
      for( int i = 0; i < variants.length && !found ; i++ ) {
        String pkgName = variants[ i ] + "." + className.toLowerCase() + "kit";
        log( "  looking through package " + pkgName );
        found |= loadThemeDef( themeWidget, pkgName, className );
        found |= loadAppearanceJs( themeWidget, pkgName, className );
        found |= loadThemeAdapter( themeWidget, pkgName, className );
        found |= loadDefaultCss( themeWidget, pkgName, className );
      }
      if( themeWidget.elements == null ) {
        log( "WARNING: No elements defined for themeable widget: "
             + themeWidget.widget.getClass().getName() );
      }
      if( themeWidget.defaultStyleSheet != null ) {
        defaultStyleSheetBuilder.addStyleSheet( themeWidget.defaultStyleSheet );
      }
    } catch( final IOException e ) {
      String message = "Failed to initialize themeable widget "
                       + themeWidget.widget.getName();
      throw new ThemeManagerException( message, e );
    }
  }

  private boolean loadThemeDef( final ThemeableWidget themeWidget,
                                final String pkgName,
                                final String className ) throws IOException
  {
    boolean result = false;
    String resPkgName = pkgName.replace( '.', '/' );
    String fileName = resPkgName + "/" + className + ".theme.xml";
    InputStream inStream = themeWidget.loader.getResourceAsStream( fileName );
    if( inStream != null ) {
      log( "Found theme definition file: " +  fileName );
      result = true;
      try {
        ThemeDefinitionReader reader
          = new ThemeDefinitionReader( inStream, fileName );
        reader.read();
        themeWidget.elements = reader.getThemeCssElements();
        for( int i = 0; i < themeWidget.elements.length; i++ ) {
          registeredCssElements.addElement( themeWidget.elements[ i ] );
        }
      } catch( final Exception e ) {
        String message = "Failed to parse theme definition file " + fileName;
        throw new ThemeManagerException( message, e );
      } finally {
        inStream.close();
      }
    }
    return result;
  }

  private boolean loadAppearanceJs( final ThemeableWidget themeWidget,
                                    final String pkgName,
                                    final String className )
    throws IOException
  {
    boolean result = false;
    String resPkgName = pkgName.replace( '.', '/' );
    String fileName = resPkgName + "/" + className + ".appearances.js";
    InputStream inStream = themeWidget.loader.getResourceAsStream( fileName );
    if( inStream != null ) {
      log( "Found appearance js file: " +  fileName );
      try {
        String content = AppearancesUtil.readAppearanceFile( inStream );
        customAppearances.add( content );
        result = true;
      } finally {
        inStream.close();
      }
    }
    return result;
  }

  /**
   * Tries to load the theme adapter for a class from a given package.
   */
  private boolean loadThemeAdapter( final ThemeableWidget themeWidget,
                                    final String pkgName,
                                    final String className )
  {
    boolean result = false;
    IThemeAdapter themeAdapter = null;
    String adapterClassName = pkgName + '.' + className + "ThemeAdapter";
    try {
      ClassLoader classLoader = themeWidget.widget.getClassLoader();
      Class adapterClass = classLoader.loadClass( adapterClassName );
      themeAdapter = ( IThemeAdapter )adapterClass.newInstance();
      if( themeAdapter != null ) {
        log( "Found theme adapter class: " + themeAdapter.getClass().getName() );
        result = true;
        adapters.put( themeWidget.widget, themeAdapter );
      }
    } catch( final ClassNotFoundException e ) {
      // ignore and try to load from next package name variant
    } catch( final InstantiationException e ) {
      String message =   "Failed to instantiate theme adapter class "
                       + adapterClassName;
      throw new ThemeManagerException( message, e );
    } catch( final IllegalAccessException e ) {
      String message =   "Failed to instantiate theme adapter class "
                       + adapterClassName;
      throw new ThemeManagerException( message, e );
    }
    return result;
  }

  /**
   * Tries to load the theme adapter for a class from a given package.
   */
  private boolean loadDefaultCss( final ThemeableWidget themeWidget,
                                  final String pkgName,
                                  final String className ) throws IOException
  {
    boolean result = false;
    String resPkgName = pkgName.replace( '.', '/' );
    String fileName = resPkgName + "/" + className + ".default.css";
    ResourceLoader resLoader = themeWidget.loader;
    InputStream inStream = resLoader.getResourceAsStream( fileName );
    if( inStream != null ) {
      log( "Found default css file: " +  fileName );
      try {
        CssFileReader reader = new CssFileReader();
        // TODO [rst] Check for illegal element names in selector list
        themeWidget.defaultStyleSheet
          = reader.parse( inStream, fileName, resLoader );
        result = true;
      } finally {
        inStream.close();
      }
    }
    return result;
  }

  /**
   * Creates and registers all JavaScript theme files and images for a given
   * theme.
   *
   * @param themeId the theme id
   * @param compress to compress or not the js code
   */
  private void registerThemeFiles( final String themeId,
                                   final boolean compress )
  {
    synchronized( registeredThemeFiles ) {
      if( !registeredThemeFiles.contains( themeId ) ) {
        Theme theme = ( Theme )themes.get( themeId );
        String jsId = theme.getJsId();
        registerNonThemeableWidgetImages( themeId );
        registerThemeableWidgetImages( themeId );
        StringBuffer sb = new StringBuffer();
        sb.append( createQxThemes( theme ) );
        // TODO [rst] Optimize: create only one ThemeStoreWriter for all themes
        IThemeCssElement[] elements = registeredCssElements.getAllElements();
        ThemeStoreWriter storeWriter = new ThemeStoreWriter( elements );
        storeWriter.addTheme( theme, theme == predefinedTheme );
        sb.append( storeWriter.createJs() );
        String themeCode = sb.toString();
        log( "-- REGISTERED THEME CODE FOR " + themeId + " --" );
        log( themeCode );
        log( "-- END REGISTERED THEME CODE --" );
        String name = jsId.replace( '.', '/' ) + ".js";
        registerJsLibrary( name, themeCode, compress );
        registeredThemeFiles.add( themeId );
      }
    }
  }

  private void registerNonThemeableWidgetImages( final String themeId ) {
    Theme theme = ( Theme )themes.get( themeId );
    ClassLoader classLoader = ThemeManager.class.getClassLoader();
    // non-themeable images
    log( " == register non-themeable images for theme " + themeId );
    for( int i = 0; i < WIDGET_NOTHEME_RESOURCES.length; i++ ) {
      String imagePath = WIDGET_NOTHEME_RESOURCES[ i ];
      String res = WIDGET_RESOURCES_SRC + "/" + imagePath;
      InputStream inputStream = classLoader.getResourceAsStream( res );
      if( inputStream == null ) {
        String mesg = "Resource not found: " + res;
        throw new IllegalArgumentException( mesg );
      }
      try {
        String jsId = theme.getJsId();
        String registerPath = getWidgetDestPath( jsId ) + "/" + imagePath;
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
    Theme theme = ( Theme )themes.get( themeId );
    // themeable images
    log( " == register themeable images for theme " + themeId );
    QxType[] values = theme.getValues();
    for( int i = 0; i < values.length; i++ ) {
      QxType value = values[ i ];
      if( value instanceof QxImage ) {
        QxImage image = ( QxImage )value;
        String key = Theme.createCssKey( value );
        String path = image.path;
        log( " register theme image " + key + ", path=" + path );
        InputStream inputStream;
        if( !image.none ) {
          // TODO [rst] in case of a none image, overwrite potentially existing
          //            image in the resource directory by registering a blank
          try {
            inputStream = image.loader.getResourceAsStream( path );
          } catch( IOException e ) {
            String message = "Failed to load resource " + path;
            throw new ThemeManagerException( message, e );
          }
          if( inputStream == null ) {
            String pattern = "Resource ''{0}'' not found for theme ''{1}''";
            Object[] arguments = new Object[]{ path, theme.getName() };
            String mesg = MessageFormat.format( pattern, arguments  );
            throw new IllegalArgumentException( mesg );
          }
          try {
            String widgetDestPath = getImageDestPath();
            String registerPath = widgetDestPath + "/" + key;
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
  }

  private static void registerJsLibrary( final String name,
                                         final boolean compress )
  {
    registerJsLibrary( name, null, compress );
  }

  private static void registerJsLibrary( final String name,
                                         final String code,
                                         final boolean compress )
  {
    IResourceManager manager = ResourceManager.getInstance();
    RegisterOptions option = RegisterOptions.VERSION;
    if( compress ) {
      option = RegisterOptions.VERSION_AND_COMPRESS;
    }
    if( code != null ) {
      byte[] buffer;
      try {
        buffer = code.getBytes( CHARSET );
      } catch( final UnsupportedEncodingException shouldNotHappen ) {
        throw new RuntimeException( shouldNotHappen );
      }
      ByteArrayInputStream inputStream = new ByteArrayInputStream( buffer );
      manager.register( name, inputStream, CHARSET, option );
    } else {
      manager.register( name, CHARSET, option );
    }
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter responseWriter = stateInfo.getResponseWriter();
    responseWriter.useJSLibrary( name );
  }

  private String createQxThemes( final Theme theme ) {
    StringBuffer buffer = new StringBuffer();
    buffer.append( createQxTheme( theme, QxTheme.COLOR ) );
    buffer.append( createQxTheme( theme, QxTheme.BORDER ) );
    buffer.append( createQxTheme( theme, QxTheme.FONT ) );
    buffer.append( createQxTheme( theme, QxTheme.ICON ) );
    buffer.append( createQxTheme( theme, QxTheme.WIDGET ) );
    buffer.append( createQxTheme( theme, QxTheme.APPEARANCE ) );
    buffer.append( createQxTheme( theme, QxTheme.META ) );
    return buffer.toString();
  }

  private String createQxTheme( final Theme theme, final int type ) {
    String jsId = theme.getJsId();
    String base = null;
    if( type == QxTheme.BORDER ) {
      base = THEME_PREFIX + "BordersBase";
    } else if( type == QxTheme.APPEARANCE ) {
      base = "org.eclipse.swt.theme.AppearancesBase";
    }
    QxTheme qxTheme = new QxTheme( jsId, theme.getName(), type, base );
    if( type == QxTheme.WIDGET || type == QxTheme.ICON ) {
      qxTheme.appendUri( getWidgetDestPath( jsId ) );
    } else if( type == QxTheme.APPEARANCE ) {
      Iterator iterator = customAppearances.iterator();
      while( iterator.hasNext() ) {
        String appearance = ( String )iterator.next();
        qxTheme.appendValues( appearance );
      }
    } else if( type == QxTheme.META ) {
      qxTheme.appendTheme( "color", jsId  + "Colors" );
      qxTheme.appendTheme( "border", jsId + "Borders" );
      qxTheme.appendTheme( "font", jsId + "Fonts" );
      qxTheme.appendTheme( "icon", jsId + "Icons" );
      qxTheme.appendTheme( "widget", jsId + "Widgets" );
      qxTheme.appendTheme( "appearance", jsId + "Appearances" );
    }
    return qxTheme.getJsCode();
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
    return THEME_RESOURCE_DEST + "/" + jsThemeName + "/widgets";
  }

  private String getImageDestPath() {
    return THEME_RESOURCE_DEST + "/images";
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
