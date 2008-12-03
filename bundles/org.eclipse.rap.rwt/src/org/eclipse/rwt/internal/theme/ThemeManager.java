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
    final int count;
    ThemeWrapper( final Theme theme, final int count ) {
      this.theme = theme;
      this.count = count;
    }
  }

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
    "menu/checkbox.gif",
    "menu/menu-blank.gif",
    "menu/radiobutton.gif",
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
    org.eclipse.swt.widgets.ExpandBar.class
  };

  private static ThemeManager instance;

  private final Set customAppearances;

  private final Map themeProperties;

  private final Map themes;

  private final Map adapters;

  private final Set registeredThemeFiles;

  private boolean initialized;

  private Theme predefinedTheme;

  private ThemeableWidgetHolder themeableWidgets;

  private StyleSheetBuilder defaultStyleSheetBuilder;

  private int themeCount;

  private CssElementHolder registeredCssElements;

// TODO [rst] Evaluate timestamp approach to separate different versions of
//            resources
//  private String timestamp;

  private ThemeManager() {
    // prevent instantiation from outside
    initialized = false;
    themeableWidgets = new ThemeableWidgetHolder();
    customAppearances = new HashSet();
    themeProperties = new LinkedHashMap();
    themes = new LinkedHashMap();
    adapters = new HashMap();
    registeredThemeFiles = new HashSet();
    registeredCssElements = new CssElementHolder();
    defaultStyleSheetBuilder = new StyleSheetBuilder();
//    timestamp = createTimeStamp();
  }

//  private String createTimeStamp() {
//    return new SimpleDateFormat( "yyyyMMddHHmm" ).format( new Date() );
//  }

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
      ThemeProperty[] props = getThemeProperties();
      predefinedTheme = Theme.loadFromStyleSheet( PREDEFINED_THEME_NAME,
                                                  null,
                                                  defaultStyleSheet );
      predefinedTheme.fillOldPropertiesFromStyleSheet( props );
      for( int i = 0; i < props.length; i++ ) {
        ThemeProperty prop = props[ i ];
        // set key only if not already defined by default CSS
        if( !predefinedTheme.hasKey( prop.name ) ) {
          log( "WARNING: missing value for property in CSS: " + prop.name );
          predefinedTheme.setValue( prop.name, prop.defValue );
        }
      }
      predefinedTheme.setValuesMap( createCssValuesMap( predefinedTheme ) );
      themes.put( PREDEFINED_THEME_ID,
                  new ThemeWrapper( predefinedTheme, themeCount++ ) );
      predefinedTheme.setDefault( true );
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
      themeProperties.clear();
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
   * @throws IllegalStateException if the ThemeManager is already initialized
   */
  public void addThemeableWidget( final Class widget,
                                  final ResourceLoader loader )
  {
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
    themeableWidgets.add( new ThemeableWidget( widget, loader ) );
  }

  /**
   * Registers a theme from an input stream. Note that <code>initialize()</code>
   * must be called first.
   *
   * @param id an id that identifies the theme in the Java code. Note that this
   *            id is not valid on the client-side. To get the id that is used
   *            on the client, see method <code>getJsThemeId</code>
   * @param name a name that describes the theme. Currently not used.
   * @param fileName the filename of the theme file
   * @param loader a ResourceLoader instance that is able to load resources
   *            needed by this theme
   * @throws IOException if an I/O error occurs
   * @throws IllegalStateException if not initialized
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
      throw new NullPointerException( "null argument" );
    }
    if( themes.containsKey( id ) ) {
      String pattern = "Theme with id ''{0}'' exists already";
      Object[] arguments = new Object[] { id };
      String msg = MessageFormat.format( pattern, arguments );
      throw new IllegalArgumentException( msg );
    }
    InputStream inputStream = loader.getResourceAsStream( fileName );
    if( inputStream == null ) {
      throw new IllegalArgumentException( "Could not open resource "
                                          + fileName );
    }
    try {
      Theme theme;
      ThemeProperty[] props = getThemeProperties();
      if( fileName.toLowerCase().endsWith( ".css" ) ) {
        try {
          CssFileReader reader = new CssFileReader();
          StyleSheet styleSheet = reader.parse( inputStream, fileName, loader );
          theme = Theme.loadFromStyleSheet( name != null ? name : "",
                                            predefinedTheme,
                                            styleSheet );
          theme.fillOldPropertiesFromStyleSheet( props );
        } catch( CSSException e ) {
          throw new ThemeManagerException( "Failed parsing CSS file", e );
        }
      } else {
        theme = Theme.loadFromFile( name != null ? name : "",
                                    predefinedTheme,
                                    inputStream,
                                    loader );
        theme.createStyleSheetFromProperties( props );
      }
      theme.setValuesMap( createCssValuesMap( theme ) );
      themes.put( id, new ThemeWrapper( theme, themeCount++ ) );
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
      ThemeWrapper wrapper = ( ThemeWrapper )themes.get( themeId );
      result = wrapper.theme;
    }
    return result;
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

  ThemeableWidget getThemeableWidget( final Class widget ) {
    return themeableWidgets.get( widget );
  }

  private ThemeCssValuesMap createCssValuesMap( final Theme theme ) {
    ThemeCssValuesMap map = new ThemeCssValuesMap();
    ThemeableWidget[] widgets = themeableWidgets.getAll();
    StyleSheet styleSheet = theme.getStyleSheet();
    for( int i = 0; i < widgets.length; i++ ) {
      ThemeableWidget themeableWidget = widgets[ i ];
      IThemeCssElement[] elements = themeableWidget.elements;
      if( themeableWidget.elements != null ) {
        for( int j = 0; j < elements.length; j++ ) {
          IThemeCssElement themeCssElement = elements[ j ];
          map.init( themeCssElement, styleSheet );
        }
      } else {
        log( "WARNING: Missing theme.xml file for themeable widget: "
             + themeableWidget.widget.getName() );
      }
    }
    return map;
  }

  /**
   * Writes a theme template file to the standard output.
   *
   * @param args ignored
   */
  public static void main( final String[] args ) {
    ThemeManager manager = ThemeManager.getInstance();
    manager.initialize();
    StringBuffer sb = new StringBuffer();
    sb.append( "# Generated RAP theme file template\n" );
    sb.append( "#\n" );
    ThemeProperty[] properties = manager.getThemeProperties();
    for( int i = 0; i < properties.length; i++ ) {
      ThemeProperty prop = properties[ i ];
      sb.append( "\n" );
      if( prop.description == null ) {
        throw new NullPointerException( "Description missing for " + prop.name );
      }
      String value = prop.defValue.toDefaultString();
      String note = prop.transparentAllowed ? " (transparent allowed)" : "";
      sb.append( "# " + prop.description.replaceAll( "\\n\\s*", "\n# " ) + "\n" );
      sb.append( "# default: " + value + note + "\n" );
      sb.append( "#" + prop.name + ": " + value  + "\n" );
    }
    System.out.println( sb.toString() );
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
        // TODO [rst] Is it possible to recognize whether a package exists?
        log( "  looking through package " + pkgName );
        found |= loadThemeDef( themeWidget, pkgName, className );
        found |= loadAppearanceJs( themeWidget, pkgName, className );
        found |= loadThemeAdapter( themeWidget, pkgName, className );
        found |= loadDefaultCss( themeWidget, pkgName, className );
      }
      if( themeWidget.properties != null
          && themeWidget.defaultStyleSheet == null )
      {
        log( "creating default style sheet for " + themeWidget.widget.getName() );
        themeWidget.defaultStyleSheet
          = PropertySupport.createDefaultStyleSheet( themeWidget );
        log( themeWidget.defaultStyleSheet.toString() );
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
          = new ThemeDefinitionReader( inStream, fileName, themeWidget.loader );
        reader.read();
        themeWidget.properties = reader.getThemeProperties();
        for( int i = 0; i < themeWidget.properties.length; i++ ) {
          ThemeProperty prop = themeWidget.properties[ i ];
          if( themeProperties.containsKey( prop.name ) ) {
            throw new IllegalArgumentException( "key defined twice: "
                                                + prop.name );
          }
          themeProperties.put( prop.name, prop );
        }
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
        sb.append( createThemeStore( wrapper.theme, jsId ) );
        sb.append( createThemeStoreCss( wrapper.theme, jsId ) );
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
    // themeable images
    log( " == register themeable images for theme " + themeId );
    String[] keys = theme.getKeysWithVariants();
    for( int i = 0; i < keys.length; i++ ) {
      String key = keys[ i ];
      Object value = theme.getValue( key );
      if( value instanceof QxImage ) {
        QxImage image = ( QxImage )value;
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
            String jsId = getJsThemeId( themeId );
            String widgetDestPath;
            if( isCssKey( key ) ) {
              widgetDestPath = getImageDestPath();
            } else {
              widgetDestPath = getWidgetDestPath( jsId );
            }
            ThemeProperty prop
              = ( ThemeProperty )themeProperties.get( stripVariant( key ) );
            String targetPath
              = prop != null && prop.targetPath != null ? prop.targetPath : key;
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
      } catch( final UnsupportedEncodingException e ) {
        throw new RuntimeException( e );
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

  private static String createColorTheme( final Theme theme, final String id ) {
    QxTheme colorTheme = new QxTheme( id, theme.getName(), QxTheme.COLOR );
    String[] keys = theme.getKeysWithVariants();
    Arrays.sort( keys );
    for( int i = 0; i < keys.length; i++ ) {
      if( !isCssKey( keys[ i ] ) ) {
        Object value = theme.getValue( keys[ i ] );
        if( value instanceof QxColor ) {
          QxColor color = ( QxColor )value;
          colorTheme.appendColor( keys[ i ], color );
        }
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
    String[] keys = theme.getKeysWithVariants();
    Arrays.sort( keys );
    for( int i = 0; i < keys.length; i++ ) {
      if( !isCssKey( keys[ i ] ) ) {
        Object value = theme.getValue( keys[ i ] );
        if( value instanceof QxBorder ) {
          QxBorder border = ( QxBorder )value;
          borderTheme.appendBorder( keys[ i ], border );
        }
      }
    }
    return borderTheme.getJsCode();
  }

  private static String createFontTheme( final Theme theme, final String id ) {
    QxTheme fontTheme = new QxTheme( id, theme.getName(), QxTheme.FONT );
    String[] keys = theme.getKeysWithVariants();
    Arrays.sort( keys );
    for( int i = 0; i < keys.length; i++ ) {
      if( !isCssKey( keys[ i ] ) ) {
        Object value = theme.getValue( keys[ i ] );
        if( value instanceof QxFont ) {
          QxFont font = ( QxFont )value;
          fontTheme.appendFont( keys[ i ], font );
        }
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
    Iterator iterator = customAppearances.iterator();
    while( iterator.hasNext() ) {
      String addAppearance = ( String )iterator.next();
      String values = AppearancesUtil.substituteMacros( addAppearance, theme );
      appTheme.appendValues( values );
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

  private String createThemeStore( final Theme theme, final String jsId ) {
    StringBuffer sb = new StringBuffer();
    String[] keys = theme.getKeysWithVariants();
    Arrays.sort( keys );
    sb.append( "ts = org.eclipse.swt.theme.ThemeStore.getInstance();\n" );
    for( int i = 0; i < keys.length; i++ ) {
      Object value = theme.getValue( keys[ i ] );
      String key = keys[ i ];
      String type = null;
      JsonValue jsValue = null;
      if( value instanceof QxDimension ) {
        QxDimension dim = ( QxDimension )value;
        type = "dimension";
        jsValue = JsonValue.valueOf( dim.value );
      } else if( value instanceof QxBoxDimensions ) {
        QxBoxDimensions boxdim = ( QxBoxDimensions )value;
        JsonArray boxArray = new JsonArray();
        boxArray.append( boxdim.top );
        boxArray.append( boxdim.right );
        boxArray.append( boxdim.bottom );
        boxArray.append( boxdim.left );
        type = "boxdim";
        jsValue = boxArray;
      } else if( value instanceof QxBoolean ) {
        QxBoolean bool = ( QxBoolean )value;
        type = "boolean";
        jsValue = JsonValue.valueOf( bool.value );
      } else if( value instanceof QxImage ) {
        QxImage image = ( QxImage )value;
        type = "image";
        if( image.none ) {
          jsValue = JsonValue.NULL;
        } else {
          ThemeProperty prop
            = ( ThemeProperty )themeProperties.get( stripVariant( key ) );
          String targetPath
            = prop != null && prop.targetPath != null ? prop.targetPath : key;
          jsValue = JsonValue.valueOf( targetPath );
        }
      } else if( value instanceof QxColor ) {
        QxColor color = ( QxColor )value;
        if( isCssKey( key ) ) {
          type = "color";
          if( color.transparent ) {
            jsValue = JsonValue.valueOf( "undefined" );
          } else {
            jsValue = JsonValue.valueOf( QxColor.toHtmlString( color.red,
                                                               color.green,
                                                               color.blue ) );
          }
        } else if( color.transparent ) {
          ThemeProperty prop
            = ( ThemeProperty )themeProperties.get( stripVariant( key ) );
          if( prop != null && !prop.transparentAllowed ) {
            // TODO [rst] Move this check to Theme class
            String message = "Transparency not allowed for key " + key;
            throw new IllegalArgumentException( message );
          }
          type = "trcolor";
          jsValue = JsonValue.TRUE;
        }
      // === new values ===
      } else if( value instanceof QxFont && isCssKey( key ) ) {
        QxFont font = ( QxFont )value;
        JsonObject fontObject = new JsonObject();
        fontObject.append( "family", JsonArray.valueOf( font.family ) );
        fontObject.append( "size", font.size );
        fontObject.append( "bold", font.bold );
        fontObject.append( "italic", font.italic );
        type = "font";
        jsValue = fontObject;
      } else if( value instanceof QxBorder && isCssKey( key ) ) {
        QxBorder border = ( QxBorder )value;
        JsonObject borderObject = new JsonObject();
        borderObject.append( "width", border.width );
        String style = border.getQxStyle();
        if( style != null ) {
          borderObject.append( "style", style );
        }
        String colors = border.getQxColors();
        if( colors != null ) {
          JsonArray borderColors = QxBorderUtil.getColors( border, theme );
          if( borderColors != null ) {
            borderObject.append( "color", borderColors );
          }
        }
        String innerColors = border.getQxInnerColors();
        if( innerColors != null ) {
          JsonArray borderInnerColors
            = QxBorderUtil.getInnerColors( border, theme );
          if( borderInnerColors != null ) {
            borderObject.append( "innerColor", borderInnerColors );
          }
        }
        type = "border";
        jsValue = borderObject;
      }
      if( type != null ) {
        String pattern = "ts.setValue( \"{0}\", \"{1}\", {2}, \"{3}\" );\n";
        String tid = isCssKey( key ) ? "_" : jsId;
        Object[] arguments = new Object[] { type, key, jsValue.toString(), tid };
        sb.append( MessageFormat.format( pattern, arguments ) );
      }
    }
    sb.append( "ts.resolveBorderColors( \"_\" );\n" );
    sb.append( "delete ts;\n" );
    return sb.toString();
  }

  private String createThemeStoreCss( final Theme theme, final String jsId ) {
    ThemeCssValuesMap valuesMap = theme.getValuesMap();
    IThemeCssElement[] elements = registeredCssElements.getAllElements();
    JsonObject mainObject = new JsonObject();
    for( int i = 0; i < elements.length; i++ ) {
      IThemeCssElement element = elements[ i ];
      String elementName = element.getName();
      JsonObject elementObj = new JsonObject();
      IThemeCssProperty[] properties = element.getProperties();
      for( int j = 0; j < properties.length; j++ ) {
        IThemeCssProperty property = properties[ j ];
        JsonArray valuesArray = new JsonArray();
        String propertyName = property.getName();
        ConditionalValue[] values
          = valuesMap.getValues( elementName, propertyName );
        for( int k = 0; k < values.length; k++ ) {
          ConditionalValue conditionalValue = values[ k ];
          JsonArray array = new JsonArray();
          array.append( JsonArray.valueOf( conditionalValue.constraints ) );
          array.append( Theme.createCssPropertyName( conditionalValue.value ) );
          valuesArray.append( array );
        }
        elementObj.append( property.getName(), valuesArray );
      }
      mainObject.append( elementName, elementObj );
    }
    StringBuffer sb = new StringBuffer();
    sb.append( "ts = org.eclipse.swt.theme.ThemeStore.getInstance();\n" );
    sb.append( "ts.setThemeCssValues( " );
    sb.append( JsonValue.quoteString( jsId ) );
    sb.append( ", " );
    sb.append( mainObject.toString() );
    sb.append( ", " );
    sb.append( theme.isDefault() );
    sb.append( " );\n" );
    sb.append( "delete ts;\n" );
    return sb.toString();
  }

  /**
   * Returns theme properties as array.
   */
  ThemeProperty[] getThemeProperties() {
    ThemeProperty[] propArray = new ThemeProperty[ themeProperties.size() ];
    Iterator iterator = themeProperties.keySet().iterator();
    for( int i = 0; i < propArray.length; i++ ) {
      if( iterator.hasNext() ) {
        Object key = iterator.next();
        propArray[ i ] = ( ThemeProperty )themeProperties.get( key );
      }
    }
    return propArray;
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

  private static boolean isCssKey( final String key ) {
    return key.charAt( 0 ) == '_';
  }

  private void checkId( final String id ) {
    if( id == null ) {
      throw new NullPointerException( "id" );
    }
    if( id.length() == 0 ) {
      throw new IllegalArgumentException( "empty id" );
    }
  }

  private static String stripVariant( final String key ) {
    String result = key;
    int index = key.indexOf( '/' );
    if( index != -1 ) {
      result = key.substring( index + 1 );
    }
    return result;
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
