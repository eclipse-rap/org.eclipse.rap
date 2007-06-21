/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.swt.internal.theme;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.internal.theme.ThemeDefinitionReader.ThemeDef;
import org.eclipse.swt.internal.theme.ThemeDefinitionReader.ThemeDefHandler;
import org.eclipse.swt.resources.ResourceManager;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.List;
import org.xml.sax.SAXException;

import com.w4t.HtmlResponseWriter;
import com.w4t.IResourceManager.RegisterOptions;
import com.w4t.engine.service.ContextProvider;
import com.w4t.engine.service.IServiceStateInfo;

public class ThemeManager {
  
  public interface ResourceLoader {
    public abstract InputStream getResourceAsStream( String resourceName )
    throws IOException;
  }

  private static final String CHARSET = "UTF-8";

  private static final Pattern PATTERN_REPLACE
  = Pattern.compile( "THEME_VALUE\\(\\s*\"(.*?)\"\\s*\\)" );

  private static class ThemeWrapper {
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

  /**
   * This map contains all widget images needed by RAP and maps them to a
   * themeing key. If the key is null, the image is not yet themeable.
   * TODO [rst] Yes, I know this isn't nice. Find an alternative.
   */
  private static final Map WIDGET_RESOURCES_MAP = new HashMap();

  static {
    WIDGET_RESOURCES_MAP.put( "arrows/down.gif", null );
    WIDGET_RESOURCES_MAP.put( "arrows/down_small.gif", null );
    WIDGET_RESOURCES_MAP.put( "arrows/down_tiny.gif", null );
    WIDGET_RESOURCES_MAP.put( "arrows/first.png", null );
    WIDGET_RESOURCES_MAP.put( "arrows/forward.gif", null );
    WIDGET_RESOURCES_MAP.put( "arrows/last.png", null );
    WIDGET_RESOURCES_MAP.put( "arrows/left.png", null );
    WIDGET_RESOURCES_MAP.put( "arrows/minimize.gif", null );
    WIDGET_RESOURCES_MAP.put( "arrows/next.gif", null );
    WIDGET_RESOURCES_MAP.put( "arrows/previous.gif", null );
    WIDGET_RESOURCES_MAP.put( "arrows/rewind.gif", null );
    WIDGET_RESOURCES_MAP.put( "arrows/right.png", null );
    WIDGET_RESOURCES_MAP.put( "arrows/up.gif", null );
    WIDGET_RESOURCES_MAP.put( "arrows/up_small.gif", null );
    WIDGET_RESOURCES_MAP.put( "arrows/up_tiny.gif", null );
    WIDGET_RESOURCES_MAP.put( "colorselector/brightness-field.jpg", null );
    WIDGET_RESOURCES_MAP.put( "colorselector/brightness-handle.gif", null );
    WIDGET_RESOURCES_MAP.put( "colorselector/huesaturation-field.jpg", null );
    WIDGET_RESOURCES_MAP.put( "colorselector/huesaturation-handle.gif", null );
    WIDGET_RESOURCES_MAP.put( "cursors/alias.gif", null );
    WIDGET_RESOURCES_MAP.put( "cursors/copy.gif", null );
    WIDGET_RESOURCES_MAP.put( "cursors/move.gif", null );
    WIDGET_RESOURCES_MAP.put( "cursors/nodrop.gif", null );
    WIDGET_RESOURCES_MAP.put( "datechooser/lastMonth.png", null );
    WIDGET_RESOURCES_MAP.put( "datechooser/lastYear.png", null );
    WIDGET_RESOURCES_MAP.put( "datechooser/nextMonth.png", null );
    WIDGET_RESOURCES_MAP.put( "datechooser/nextYear.png", null );
    WIDGET_RESOURCES_MAP.put( "menu/checkbox.gif", null );
    WIDGET_RESOURCES_MAP.put( "menu/menu-blank.gif", null );
    WIDGET_RESOURCES_MAP.put( "menu/radiobutton.gif", null );
    WIDGET_RESOURCES_MAP.put( "splitpane/knob-horizontal.png", null );
    WIDGET_RESOURCES_MAP.put( "splitpane/knob-vertical.png", null );
    WIDGET_RESOURCES_MAP.put( "table/up.png", null );
    WIDGET_RESOURCES_MAP.put( "table/down.png", null );
    WIDGET_RESOURCES_MAP.put( "tree/cross.gif", null );
    WIDGET_RESOURCES_MAP.put( "tree/cross_minus.gif", null );
    WIDGET_RESOURCES_MAP.put( "tree/cross_plus.gif", null );
    WIDGET_RESOURCES_MAP.put( "tree/end.gif", null );
    WIDGET_RESOURCES_MAP.put( "tree/end_minus.gif", null );
    WIDGET_RESOURCES_MAP.put( "tree/end_plus.gif", null );
    WIDGET_RESOURCES_MAP.put( "tree/line.gif", null );
    WIDGET_RESOURCES_MAP.put( "tree/minus.gif", null );
    WIDGET_RESOURCES_MAP.put( "tree/only_minus.gif", null );
    WIDGET_RESOURCES_MAP.put( "tree/only_plus.gif", null );
    WIDGET_RESOURCES_MAP.put( "tree/plus.gif", null );
    WIDGET_RESOURCES_MAP.put( "tree/start_minus.gif", null );
    WIDGET_RESOURCES_MAP.put( "tree/start_plus.gif", null );

    // minimize button
    WIDGET_RESOURCES_MAP.put( "window/minimize.png",
                              "shell.minbutton.image" );
    WIDGET_RESOURCES_MAP.put( "window/minimize.over.png",
                              "shell.minbutton.over.image" );
    WIDGET_RESOURCES_MAP.put( "window/minimize.inactive.png",
                              "shell.minbutton.inactive.image" );
    WIDGET_RESOURCES_MAP.put( "window/minimize.inactive.over.png",
                              "shell.minbutton.inactive.over.image" );
    // maximize button
    WIDGET_RESOURCES_MAP.put( "window/maximize.png",
                              "shell.maxbutton.image" );
    WIDGET_RESOURCES_MAP.put( "window/maximize.over.png",
                              "shell.maxbutton.over.image" );
    WIDGET_RESOURCES_MAP.put( "window/maximize.inactive.png",
                              "shell.maxbutton.inactive.image" );
    WIDGET_RESOURCES_MAP.put( "window/maximize.inactive.over.png",
                              "shell.maxbutton.inactive.over.image" );
    // restore button
    WIDGET_RESOURCES_MAP.put( "window/restore.png",
                              "shell.restorebutton.image" );
    WIDGET_RESOURCES_MAP.put( "window/restore.over.png",
                              "shell.restorebutton.over.image" );
    WIDGET_RESOURCES_MAP.put( "window/restore.inactive.png",
                              "shell.restorebutton.inactive.image" );
    WIDGET_RESOURCES_MAP.put( "window/restore.inactive.over.png",
                              "shell.restorebutton.inactive.over.image" );
    // close button
    WIDGET_RESOURCES_MAP.put( "window/close.png",
                              "shell.closebutton.image" );
    WIDGET_RESOURCES_MAP.put( "window/close.over.png",
                              "shell.closebutton.over.image" );
    WIDGET_RESOURCES_MAP.put( "window/close.inactive.png",
                              "shell.closebutton.inactive.image" );
    WIDGET_RESOURCES_MAP.put( "window/close.inactive.over.png",
                              "shell.closebutton.inactive.over.image" );

    WIDGET_RESOURCES_MAP.put( "window/caption_active.gif",
                              "shell.title.active.bgimage" );
    WIDGET_RESOURCES_MAP.put( "window/caption_inactive.gif",
                              "shell.title.inactive.bgimage" );
    WIDGET_RESOURCES_MAP.put( "tree/folder_open.gif", null );
    WIDGET_RESOURCES_MAP.put( "tree/folder_closed.gif", null );
    WIDGET_RESOURCES_MAP.put( "display/bg.gif", null );
    WIDGET_RESOURCES_MAP.put( "table/check_white_on.gif", null );
    WIDGET_RESOURCES_MAP.put( "table/check_white_off.gif", null );
    WIDGET_RESOURCES_MAP.put( "table/check_gray_on.gif", null );
    WIDGET_RESOURCES_MAP.put( "table/check_gray_off.gif", null );
    WIDGET_RESOURCES_MAP.put( "progressbar/bar.gif",
                              "progressbar.fgimage" );
    WIDGET_RESOURCES_MAP.put( "progressbar/barbg.gif",
                              "progressbar.bgimage" );
  }

  /** Where to load the default images from */
  private static final String WIDGET_RESOURCES_SRC = "resource/widget/rap/";

  private static final String THEME_RESOURCE_DEST = "resource/themes/";

  private static final String JS_THEME_PREFIX = "org.eclipse.swt.theme.";

  private static final String PREDEFINED_THEME_ID = JS_THEME_PREFIX + "Default";

  private static final String PREDEFINED_THEME_NAME = "RAP Default Theme";

  private static ThemeManager instance;

  private final Map themeDefs;
  private final Map themes;
  private final Map adapters;
  private Theme predefinedTheme;
  private boolean initialized;
  private String defaultThemeId = PREDEFINED_THEME_ID;
  private int themeCount;

  private static final boolean DEBUG
    = "true".equals( System.getProperty( ThemeManager.class.getName() + ".log" ) );

  // Note: The order of widgets also determines the order of the template file
  // generated by the main method.
  private static final Class[] THEMEABLE_WIDGETS = new Class[] {
//  Browser.class,
//  CBanner.class,
    Button.class,
    Combo.class,
//  Composite.class,
    Control.class,
    CoolBar.class,
    CTabFolder.class,
//  Group.class,
    Label.class,
    Link.class,
    List.class,
    Menu.class,
    ProgressBar.class,
//  Sash.class,
//  SashForm.class,
//  Scrollable.class,
//  Scrollbar.class,
//  ScrolledComposite.class,
    Shell.class,
    Spinner.class,
    TabFolder.class,
    Table.class,
    Text.class,
    ToolBar.class,
    Tree.class,
    Widget.class,
  };

  private ThemeManager() {
    // prevent instantiation from outside
    themeDefs = new LinkedHashMap();
    themes = new HashMap();
    adapters = new HashMap();
    initialized = false;
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
   * Initializes the ThemeManager, i.e. loads themeing-relevant files and
   * registers themes.
   */
  public void initialize() {
    log( "____ ThemeManager intialize" );
    if( !initialized ) {
      predefinedTheme = new Theme( PREDEFINED_THEME_NAME );
      for( int i = 0; i < THEMEABLE_WIDGETS.length; i++ ) {
        try {
          processThemeableWidget( THEMEABLE_WIDGETS[ i ] );
        } catch( Exception e ) {
          throw new RuntimeException( "Initialization failed", e );
        }
      }
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
      log( "=== REGISTERED ADAPTERS ===" );
      Iterator iter = adapters.keySet().iterator();
      while( iter.hasNext() ) {
        Class key = ( Class )iter.next();
        Object adapter = adapters.get( key );
        log( key.getName() + ": " + adapter );        
      }
      log( "=== END REGISTERED ADAPTERS ===" );
    }
  }

  public void deregisterAll() {
    if( initialized ) {
      themeDefs.clear();
      themes.clear();
      adapters.clear();
      predefinedTheme = null;
      initialized = false;
      log( "deregistered" );
    }
  }

  public void registerTheme( final String id,
                             final String name,
                             final InputStream instr,
                             final ResourceLoader loader,
                             final boolean asDefault )
  throws IOException
  {
    checkInitialized();
    log( "_____ register theme " + id + ": " + instr + " def: " + asDefault );
    checkId( id );
    if( themes.containsKey( id ) ) {
      String pattern = "Theme with id ''{0}'' exists already";
      Object[] arguments = new Object[] { id };
      String msg = MessageFormat.format( pattern, arguments );
      throw new IllegalArgumentException( msg );
    }
    Theme theme = loadThemeFile( name, instr );

    themes.put( id, new ThemeWrapper( theme, loader, themeCount++  ) );
    if( asDefault ) {
      defaultThemeId = id;
    }
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

  public String[] getAvailableThemeIds() {
    String[] result = new String[ themes.size() ];
    return ( String[] )themes.keySet().toArray( result );
  }

  public String getDefaultThemeId() {
    checkInitialized();
    return defaultThemeId;
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

  public String getJsThemeId( final String themeId ) {
    String result;
    if( PREDEFINED_THEME_ID.equals( themeId ) ) {
      result = PREDEFINED_THEME_ID;
    } else {
      // TODO [rst] check if theme is registered
      ThemeWrapper wrapper = ( ThemeWrapper )themes.get( themeId );
      result = JS_THEME_PREFIX + "Custom_" + wrapper.count;
    }
    return result;
  }

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
      String description = def.description.replaceAll( "\\n\\s*", "\n# " );
      sb.append( "\n" );
      sb.append( "# " + description + "\n" );
      sb.append( "# default: " + value + "\n" );
      sb.append( "#" + def.name + ": " + value  + "\n" );
    }
    log( sb.toString() );
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
   */
  private void processThemeableWidget( final Class clazz )
  throws IOException, FactoryConfigurationError,
  ParserConfigurationException
  {
    String[] variants = getPackageVariants( clazz.getPackage().getName() );
    String className = getSimpleClassName( clazz );
    ClassLoader loader = clazz.getClassLoader();
    IThemeAdapter themeAdapter = null;
    boolean found = false;
    for( int i = 0; i < variants.length && !found ; i++ ) {
      String pkgName = variants[ i ] + "." + className.toLowerCase() + "kit";
      // TODO [rst] Is it possible to recognize whether a package exists?
      //      Information should not be collected from different package name
      //      variants.
      loadThemeDef( loader, pkgName, className );
      themeAdapter = loadThemeAdapter( loader, pkgName, className );
      if( themeAdapter != null ) {
        log( "ThemeAdapter found: " + themeAdapter );
        adapters.put( clazz, themeAdapter );
        found = true;
      }
    }
  }

  private void loadThemeDef( final ClassLoader loader,
                             final String pkgName,
                             final String className )
  throws IOException, FactoryConfigurationError,
  ParserConfigurationException
  {
    String resPkgName = pkgName.replace( '.', '/' );
    String fileName = resPkgName + "/" + className + ".theme.xml";
    InputStream inStream = loader.getResourceAsStream( fileName );
    if( inStream != null ) {
      log( "Found theme definition file: " +  fileName );
      try {
        ThemeDefinitionReader reader = new ThemeDefinitionReader( inStream );
        reader.read( new ThemeDefHandler() {
          public void readThemeDef( final ThemeDef def ) {
            if( themeDefs.containsKey( def.name ) ) {
              throw new IllegalArgumentException( "key defined twice: "
                                                  + def.name );
            }
            themeDefs.put( def.name, def );
          }
        } );
      } catch( final SAXException e ) {
        throw new IllegalArgumentException( "Failed to parse file " + fileName );
      } finally {
        inStream.close();
      }
    }
  }

  /**
   * Tries to load the theme adapter for a class from a given package.
   * @return the theme adapter or <code>null</code> if not found.
   */
  private IThemeAdapter loadThemeAdapter( final ClassLoader loader,
                                          final String pkgName,
                                          final String className )
  {
    IThemeAdapter result = null;
    String adapterClassName = pkgName + '.' + className + "ThemeAdapter";
    String msg = "Failed to load theme adapter for class ";
    log( "try to load '" + adapterClassName + "'" );
    try {
      Class adapterClass = loader.loadClass( adapterClassName );
      result = ( IThemeAdapter )adapterClass.newInstance();
    } catch( ClassNotFoundException e ) {
      // ignore and try to load from next package name variant
    } catch( InstantiationException e ) {
      throw new RuntimeException( msg + className, e );
    } catch( IllegalAccessException e ) {
      throw new RuntimeException( msg + className, e );
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
    // TODO [rst] handle loops and multi-step inheritance
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

  private void registerThemeFiles( final String id )
  throws IOException
  {
    ThemeWrapper wrapper = ( ThemeWrapper )themes.get( id );
    String jsId = getJsThemeId( id );
    registerWidgetImages( id );
    String colorThemeCode = createColorTheme( wrapper.theme, jsId );
    String borderThemeCode = createBorderTheme( wrapper.theme, jsId );
    String fontThemeCode = createFontTheme( wrapper.theme, jsId );
    String iconThemeCode = createIconTheme( wrapper.theme, jsId );
    String widgetThemeCode = createWidgetTheme( wrapper.theme, jsId );
    String appearanceThemeCode = createAppearanceTheme( wrapper.theme, jsId );
    String metaThemeCode = createMetaTheme( wrapper.theme, jsId );
    log( "-- REGISTERED THEME --" );
    log( colorThemeCode );
    log( borderThemeCode );
    log( fontThemeCode );
    log( iconThemeCode );
    log( widgetThemeCode );
    log( appearanceThemeCode );
    log( metaThemeCode );
    log( "-- END REGISTERED THEME --" );
    // TODO [rst] Check whether concatenating theme files improves loading times
    registerJsLibrary( colorThemeCode, jsId + "Colors.js" );
    registerJsLibrary( borderThemeCode, jsId + "Borders.js" );
    registerJsLibrary( fontThemeCode, jsId + "Fonts.js" );
    registerJsLibrary( iconThemeCode, jsId + "Icons.js" );
    registerJsLibrary( widgetThemeCode, jsId + "WidgetIcons.js" );
    registerJsLibrary( appearanceThemeCode, jsId + "Appearance.js" );
    registerJsLibrary( metaThemeCode, jsId + ".js" );
  }

  private void registerWidgetImages( final String id ) 
  throws IOException 
  {
    Iterator iterator = WIDGET_RESOURCES_MAP.keySet().iterator();
    ThemeWrapper wrapper = ( ThemeWrapper )themes.get( id );
    Theme theme = wrapper.theme;
    ResourceLoader themeLoader = wrapper.loader;

    while( iterator.hasNext() ) {
      String imagePath = ( String )iterator.next();
      String themeKey = ( String )WIDGET_RESOURCES_MAP.get( imagePath );
      InputStream inputStream = null;
      String res = "";
      if( themeKey != null ) {
        QxImage qxImage = theme.getImage( themeKey );
        if( qxImage != null ) {
          res = qxImage.getPath();
        }
      }
      if( "".equals( res ) ) {
        res = WIDGET_RESOURCES_SRC + imagePath;
        ClassLoader classLoader = ThemeManager.class.getClassLoader();
        inputStream = classLoader.getResourceAsStream( res );
      } else {
        inputStream = themeLoader.getResourceAsStream( res );
      }
      if( inputStream == null ) {
        String pattern = "Resource ''{0}'' not found for theme ''{1}''";
        Object[] arguments = new Object[]{ res, theme.getName() };
        String mesg = MessageFormat.format( pattern, arguments  );
        throw new IllegalArgumentException( mesg );
      }
      try {
        String jsId = getJsThemeId( id );
        String registerPath = getWidgetDestPath( jsId  ) + imagePath;
        ResourceManager.getInstance().register( registerPath, inputStream );
      } finally {
        inputStream.close();
      }
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

  private static String createAppearanceTheme( final Theme theme,
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

  private static void registerJsLibrary( final String code, final String name )
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
      responseWriter.useJSLibrary( name );
    } finally {
      resourceInputStream.close();
    }
  }

  private static String getWidgetDestPath( final String id ) {
    return THEME_RESOURCE_DEST + id + "/widgets/";
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
      if( result instanceof QxDimension ) {
        QxDimension dim = ( QxDimension )result;
        repl = String.valueOf( dim.getInt() );
      } else if( result instanceof QxBoxDimensions ) {
        QxBoxDimensions boxdim = ( QxBoxDimensions )result;
        repl = boxdim.toJsArray();
      } else {
        String mesg = "Only dimensions and box dimensions can be substituted in appearance templates";
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
      throw new NullPointerException( "null argument" );
    }
    if( id.length() == 0 ) {
      throw new IllegalArgumentException( "empty id" );
    }
  }

  // TODO [rst] Replace with Logger calls
  private static void log( final String mesg ) {
    if( DEBUG ) {
      System.out.println( mesg );
    }
  }
}
