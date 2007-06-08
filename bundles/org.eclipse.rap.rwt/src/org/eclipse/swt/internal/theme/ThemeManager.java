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
import com.w4t.engine.service.ContextProvider;
import com.w4t.engine.service.IServiceStateInfo;

public class ThemeManager {

  public interface ResourceLoader {
    public abstract InputStream getResourceAsStream( String resourceName )
      throws IOException;
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
    WIDGET_RESOURCES_MAP.put( "window/close.gif",
                              "shell.closebutton.image" );
    WIDGET_RESOURCES_MAP.put( "window/maximize.gif",
                              "shell.maxbutton.image" );
    WIDGET_RESOURCES_MAP.put( "window/minimize.gif",
                              "shell.minbutton.image" );
    WIDGET_RESOURCES_MAP.put( "window/restore.gif",
                              "shell.restorebutton.image" );
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
  }
  
  /** Where to load the default images from */
  private static final String WIDGET_RESOURCES_SRC = "resource/widget/rap/";
  
  private static final String THEME_RESOURCE_DEST = "resource/themes/";
  
  private static final String PREDEFINED_THEME_ID
    = "org.eclipse.swt.theme.Default";
  
  private static final String PREDEFINED_THEME_NAME = "RAP Default Theme";

  private static ThemeManager instance;
  
  private final Map themeDefs;
  private final Map themes;
  private final Map loaders;
  private final Map adapters;
  private Theme predefinedTheme;
  private boolean initialized;
  private String defaultThemeId = PREDEFINED_THEME_ID;

  // TODO [rst] Remove sysout blocks when themeing has stabilized
  private static final boolean DEBUG = false;
  
  // Note: The order of widgets also determines the order of the template file
  // generated by the main method.
  private static final Class[] THEMEABLE_WIDGETS = new Class[] {
//    Browser.class,
//    CBanner.class,
    Button.class,
    Combo.class,
//    Composite.class,
    Control.class,
    CoolBar.class,
    CTabFolder.class,
//    Group.class,
    Label.class,
    Link.class,
    List.class,
    Menu.class,
//    ProgressBar.class,
//    Sash.class,
//    SashForm.class,
//    Scrollable.class,
//    Scrollbar.class,
//    ScrolledComposite.class,
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
    loaders = new HashMap();
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
    if( DEBUG ) {
      System.out.println( "____ ThemeManager intialize" );
    }
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
        predefinedTheme.setValue( key, def.value );
      }
      themes.put( PREDEFINED_THEME_ID, predefinedTheme );
      initialized = true;
      if( DEBUG  ) {
        System.out.println( "=== REGISTERED ADAPTERS ===" );
        Iterator iter = adapters.keySet().iterator();
        while( iter.hasNext() ) {
          Class key = ( Class )iter.next();
          Object adapter = adapters.get( key );
          System.out.println( key.getName() + ": " + adapter );        
        }
        System.out.println( "=== END REGISTERED ADAPTERS ===" );
      }
    }
  }
  
  public void deregisterAll() {
    if( initialized ) {
      themeDefs.clear();
      themes.clear();
      adapters.clear();
      predefinedTheme = null;
      initialized = false;
      if( DEBUG ) {
        System.out.println( "deregistered" );
      }
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
    if( DEBUG ) {
      System.out.println( "_____ register theme " + id + ": " + instr + " def: " + asDefault );
    }
    checkId( id );
    if( themes.containsKey( id ) ) {
      String pattern = "Theme with id ''{0}'' exists already";
      Object[] arguments = new Object[] { id };
      String msg = MessageFormat.format( pattern, arguments );
      throw new IllegalArgumentException( msg );
    }
    Theme theme = loadThemeFile( name, instr );
    themes.put( id, theme );
    loaders.put( theme, loader );
    if( asDefault ) {
      defaultThemeId = id;
    }
  }

  public void registerResources() throws IOException {
    checkInitialized();
    if( DEBUG ) {
      System.out.println( "____ ThemeManager register resources" );
    }
    Iterator iterator = themes.keySet().iterator();
    while( iterator.hasNext() ) {
      String id = ( String )iterator.next();
      Theme theme = ( Theme )themes.get( id );
      registerThemeFiles( theme, id );
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
    return ( Theme )themes.get( themeId );
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
      String value = def.value.toDefaultString();
      String description = def.description.replaceAll( "\\n\\s*", "\n# " );
      // TODO [rst] Hack to hide themeing properties that do not yet support
      //      user-defined values - Remove when themeing is stable.
      if( description.indexOf( "[hidden]" ) == -1 ) {
        sb.append( "\n" );
        sb.append( "# " + description + "\n" );
        sb.append( "# default: " + value + "\n" );
        sb.append( "#" + def.name + ": " + value  + "\n" );
      }
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
        if( DEBUG ) {
          System.out.println( "ThemeAdapter found: " + themeAdapter );
        }
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
      if( DEBUG ) {
        System.out.println( "Found theme definition file: " +  fileName );
      }
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
    if( DEBUG ) {
      System.out.println( "try to load '" + adapterClassName + "'" );
    }
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
    Properties properties = new Properties( );
    properties.load( instr );
    Iterator iterator = properties.keySet().iterator();
    while( iterator.hasNext() ) {
      String key = ( String )iterator.next();
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
      } else if( defValue instanceof QxColor ) {
        newValue = new QxColor( value );
      } else if( defValue instanceof QxDimension ) {
        newValue = new QxDimension( value );
      } else if( defValue instanceof QxImage ) {
        newValue = new QxImage( value );
      }
      newTheme.setValue( key, newValue );
    }
    return newTheme;
  }
  
  private void registerThemeFiles( final Theme theme, final String id )
    throws IOException
  {
    registerWidgetImages( theme, id );
    String colorThemeCode = createColorTheme( theme, id );
    String widgetThemeCode = createWidgetTheme( theme, id );
    String metaThemeCode = createMetaTheme( theme, id );
    if( DEBUG ) {
      System.out.println( "-- REGISTERED THEME --" );
      System.out.println( colorThemeCode );
      System.out.println( widgetThemeCode );
      System.out.println( metaThemeCode );
      System.out.println( "-- END REGISTERED THEME --" );
    }
    registerJsLibrary( colorThemeCode, id + "Colors.js" );
    registerJsLibrary( widgetThemeCode, id + "WidgetIcons.js" );
    registerJsLibrary( metaThemeCode, id + ".js" );
  }
  
  private void registerWidgetImages( final Theme theme, final String id ) 
    throws IOException 
  {
    Iterator iterator = WIDGET_RESOURCES_MAP.keySet().iterator();
    ResourceLoader rLoader = ( ResourceLoader )loaders.get( theme );
    while( iterator.hasNext() ) {
      String imagePath = ( String )iterator.next();
      String themeKey = ( String )WIDGET_RESOURCES_MAP.get( imagePath );
      InputStream inputStream = null;
      String res = "";
      if( themeKey != null ) {
        // TODO [rst] remove cast
        QxImage value = ( QxImage )theme.getValue( themeKey );
        if( value != null ) {
          res = value.getPath();
        }
      }
      if( "".equals( res ) ) {
        ClassLoader cLoader = ThemeManager.class.getClassLoader();
        inputStream = cLoader.getResourceAsStream(   WIDGET_RESOURCES_SRC
                                                   + imagePath );
      } else {
        if( rLoader == null ) {
          // TODO [rst]
          throw new NullPointerException();
        }
        inputStream = rLoader.getResourceAsStream( res );
      }
      if( inputStream == null ) {
        // TODO [rst]
        throw new NullPointerException();
      }
      try {
        String registerPath = getWidgetDestPath( id ) + imagePath;
        ResourceManager.getInstance().register( registerPath , inputStream );
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
  
  private static String createWidgetTheme( final Theme theme, final String id ) {
    ThemeWriter writer = new ThemeWriter( id,
                                          theme.getName(),
                                          ThemeWriter.WIDGET );
    writer.writeUri( getWidgetDestPath( id ) );
    return writer.getGeneratedCode();
  }

  private static String getWidgetDestPath( final String id ) {
    return THEME_RESOURCE_DEST + id + "/widgets/";
  }
  
  private static String createMetaTheme( final Theme theme, final String id ) {
    ThemeWriter writer = new ThemeWriter( id, theme.getName(), ThemeWriter.META );
    writer.writeTheme( "color", id + "Colors" );
    writer.writeTheme( "border", PREDEFINED_THEME_ID + "Borders" );
    writer.writeTheme( "font", PREDEFINED_THEME_ID + "Fonts" );
    writer.writeTheme( "widget", id + "Widgets" );
    writer.writeTheme( "appearance", PREDEFINED_THEME_ID + "Appearances" );
    writer.writeTheme( "icon", PREDEFINED_THEME_ID + "Icons" );
    return writer.getGeneratedCode();
  }
  
  private static void registerJsLibrary( final String code, final String name )
    throws IOException
  {
    ByteArrayInputStream resourceInputStream;
    byte[] buffer = code.getBytes( "UTF-8" );
    resourceInputStream = new ByteArrayInputStream( buffer );
    try {
      ResourceManager.getInstance().register( name, resourceInputStream );
      IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
      HtmlResponseWriter responseWriter = stateInfo.getResponseWriter();
      responseWriter.useJSLibrary( name );
    } finally {
      resourceInputStream.close();
    }
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
}
