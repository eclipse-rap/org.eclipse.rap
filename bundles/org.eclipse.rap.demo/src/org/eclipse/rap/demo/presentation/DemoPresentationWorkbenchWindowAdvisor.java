package org.eclipse.rap.demo.presentation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.rap.demo.DemoActionBarAdvisor;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.application.*;

/**
 * Configures the initial size and appearance of a workbench window.
 */
public class DemoPresentationWorkbenchWindowAdvisor
  extends WorkbenchWindowAdvisor
{
  private static final int BANNER_HEIGTH = 88;
  private static final Color COLOR_BANNER_BG
    = Graphics.getColor( 27, 87, 144 );
  private static final Color COLOR_BANNER_FG
    = Graphics.getColor( 255, 255, 255 );
  private static final Color COLOR_SHELL_BG
    = Graphics.getColor( 255, 255, 255 );


  public DemoPresentationWorkbenchWindowAdvisor(
    final IWorkbenchWindowConfigurer configurer )
  {
    super( configurer );
  }

  public ActionBarAdvisor createActionBarAdvisor(
    final IActionBarConfigurer configurer )
  {
    return new DemoActionBarAdvisor( configurer );
  }

  public void preWindowOpen() {
    IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
    configurer.setShowCoolBar( true );
    configurer.setShowStatusLine( false );
    configurer.setTitle( "Presentation Prototype" );
    configurer.setShellStyle( SWT.NONE );
    Rectangle bounds = Display.getDefault().getBounds();
    configurer.setInitialSize( new Point( bounds.width, bounds.height ) );
  }

  public void postWindowOpen() {
    final IWorkbenchWindow window = getWindowConfigurer().getWindow();
    Shell shell = window.getShell();
    shell.setMaximized( true );
    shell.setBackground( COLOR_SHELL_BG );
  }
  
  public void createWindowContents( final Shell shell ) {
    shell.setLayout( new FormLayout() );
    createBanner( shell );
    createPageComposite( shell );    
  }

  private void createBanner( final Shell shell ) {
    Composite banner = new Composite( shell, SWT.NONE );
    banner.setBackgroundMode( SWT.INHERIT_DEFAULT );
    banner.setData( WidgetUtil.CUSTOM_VARIANT, "banner" );
    FormData fdBanner = new FormData();
    banner.setLayoutData( fdBanner );
    fdBanner.top = new FormAttachment( 0, 0 );
    fdBanner.left = new FormAttachment( 0, 50 );
    fdBanner.height = BANNER_HEIGTH;
    fdBanner.right = new FormAttachment( 100, -50 );
    banner.setLayout( new FormLayout() );
//    banner.setBackground( COLOR_BANNER_BG );
    banner.setBackgroundImage( Images.IMG_BANNER_BG );

    Label label = new Label( banner, SWT.NONE );
    label.setText( "RAP Demo" );
    label.setForeground( COLOR_BANNER_FG );
    label.setFont( Graphics.getFont( "Verdana", 38, SWT.BOLD ) );
    label.pack();
    FormData fdLabel = new FormData();
    label.setLayoutData( fdLabel );
    fdLabel.top = new FormAttachment( 0, 5 );
    fdLabel.left = new FormAttachment( 0, 10 );
    
    Label roundedCornerLeft = new Label( banner, SWT.NONE );
    roundedCornerLeft.setImage( Images.IMG_BANNER_ROUNDED_LEFT );
    roundedCornerLeft.pack();
    FormData fdRoundedCornerLeft = new FormData();
    roundedCornerLeft.setLayoutData( fdRoundedCornerLeft );
    fdRoundedCornerLeft.top = new FormAttachment( 100, -5 );
    fdRoundedCornerLeft.left = new FormAttachment( 0, 0 );
    roundedCornerLeft.moveAbove( banner );
    
    Label roundedCornerRight = new Label( banner, SWT.NONE );
    roundedCornerRight.setImage( Images.IMG_BANNER_ROUNDED_RIGHT );
    roundedCornerRight.pack();
    FormData fdRoundedCornerRight = new FormData();
    roundedCornerRight.setLayoutData( fdRoundedCornerRight );
    fdRoundedCornerRight.top = new FormAttachment( 100, -5 );
    fdRoundedCornerRight.left = new FormAttachment( 100, -5 );
    roundedCornerRight.moveAbove( banner );
    
    createMenuBar( banner );
    createCoolBar( banner, label );

//    fakeBannerButtons( banner );
//    createActionBar( banner );
//    createPerspectiveSwitcher( banner );
//    createSearch( banner );
  }

//  private void createSearch( final Composite banner ) {
//    Composite search = new Composite( banner, SWT.NONE );
//    search.setLayout( new FormLayout() );
//    final Text text = new Text( search, SWT.NONE );
//    FormData fdText = new FormData();
//    text.setLayoutData( fdText );
//    text.setText( TXT_SEARCH );
//    FontData fontData = text.getFont().getFontData()[ 0 ];
//    text.setForeground( Graphics.getColor( 128, 128, 128 ) );
//    text.addFocusListener( new FocusListener() {
//      public void focusGained( final FocusEvent event ) {
//        if( TXT_SEARCH.equals( ( text.getText() ) ) ) {
//          text.setText( "" );
//        }
//      }
//      public void focusLost( final FocusEvent event ) {
//        if( "".equals( ( text.getText() ) ) ) {
//          text.setText( TXT_SEARCH );
//        }
//      }
//    } );
//
//    Button button = new Button( search, SWT.PUSH | SWT.FLAT );
//    FormData fdButton = new FormData();
//    button.setLayoutData( fdButton );
//    button.setImage( IMAGE_SEARCH );
//    button.setData( WidgetUtil.CUSTOM_APPEARANCE, "banner-button" );
//    button.pack();
//    button.moveAbove( text );
//    
//    fdButton.top = new FormAttachment( 0, 0 );
//    fdButton.left = new FormAttachment( 0, 140 );
//    
//    fdText.top = new FormAttachment( 0, 3 );
//    fdText.left = new FormAttachment( 0, 0 );
//    fdText.width = 150;
//    fdText.height = button.getSize().y - 8;
//    
//    FormData fdSearch = new FormData();
//    search.setLayoutData( fdSearch );
//    fdSearch.top = new FormAttachment( 0, 10 );
//    fdSearch.left = new FormAttachment( 100, -175 );
//  }

//  private void createActionBar( final Composite banner ) {
//    
//    IAction[] actions = new IAction[] {
//      new Action( "In" ) {
//        public void run() {
//          System.out.println( "In pressed" );
//        }
//      },
//      new Action( "Out" ) {
//        public void run() {
//          System.out.println( "Out pressed" );
//        }        
//      },
//      new Action( "Over" ) {
//        public void run() {
//          System.out.println( "Over pressed" );
//        }
//      },
//      new Action( "Under" ) {
//        public void run() {
//          System.out.println( "Under pressed" );
//        }
//      },
//      new Action( "Through" ) {
//        public void run() {
//          System.out.println( "Through pressed" );
//        }        
//      }
//    };
//    
//    ActionBarButton actionBar = new ActionBarButton( banner, SWT.NONE, actions );
//    FormData fdActionBar = new FormData();
//    actionBar.setLayoutData( fdActionBar );
//    fdActionBar.top = new FormAttachment( 0, 44 );
//    fdActionBar.left = new FormAttachment( 0, 5 );
//    actionBar.pack();
//  }

//  private void createPerspectiveSwitcher( final Composite banner ) {
//    IAction[] actions = new IAction[] {
//      new Action( "Perspective 1" ) {
//        public void run() {
//          switchPerspective( 0 );
//        }
//
//      },
//      new Action( "Perspective 2" ) {
//        public void run() {
//          switchPerspective( 1 );
//        }
//      }
//    };
//    
//    ActionBarButton actionBar = new ActionBarButton( banner, SWT.NONE, actions );
//    actionBar.pack();
//    
//    FormData fdActionBar = new FormData();
//    actionBar.setLayoutData( fdActionBar );
//    fdActionBar.top = new FormAttachment( 0, 44 );
//    fdActionBar.left = new FormAttachment( 100, -actionBar.getSize().x );
//  }

  private void switchPerspective( final int perspectiveIndex ) {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IPerspectiveRegistry registry = workbench.getPerspectiveRegistry();
    final IPerspectiveDescriptor[] perspectives = registry.getPerspectives();
    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    final IWorkbenchPage page = window.getActivePage();
    page.setPerspective( perspectives[ perspectiveIndex ] );
  }
  
  private void createCoolBar( final Composite banner,
                              final Control leftControl )
  {
    IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
    Composite coolBar = ( Composite )configurer.createCoolBarControl( banner );
    coolBar.setBackgroundMode( SWT.INHERIT_FORCE );
    FormData fdCoolBar = new FormData();
    coolBar.setLayoutData( fdCoolBar );
    fdCoolBar.top = new FormAttachment( 0, 8 );
    fdCoolBar.left = new FormAttachment( leftControl, 35 );
    fdCoolBar.bottom = new FormAttachment( 0, 26 );
//    fdCoolBar.right = new FormAttachment( 100, -100 );
  }
  
  private void createMenuBar( final Composite banner ) {
    final Composite menuBar = new Composite( banner, SWT.NONE );
    menuBar.setBackgroundMode( SWT.INHERIT_FORCE );
    FormData fdMenuBar = new FormData();
    menuBar.setLayoutData( fdMenuBar );
    fdMenuBar.top = new FormAttachment( 100, -26 );
    fdMenuBar.left = new FormAttachment( 0, 10 );
    fdMenuBar.bottom = new FormAttachment( 100, -8 );

    final ApplicationWindow window
      = ( ApplicationWindow )getWindowConfigurer().getWindow();
    MenuManager menuBarManager = window.getMenuBarManager();
    IContributionItem[] menuBarItems = menuBarManager.getItems();
    List actions = new ArrayList();
    for( int i = 0; i < menuBarItems.length; i++ ) {
      final MenuManager menuManager = ( MenuManager )menuBarItems[ i ];
      actions.add( new Action() {
        public String getId() {
          return menuManager.getId();
        }
        public String getText() {
          return menuManager.getMenuText();
        }
        
        public void run() {
          final Shell shell = window.getShell();
          final PopupDialog popupDialog = new PopupDialog( shell,
                           SWT.RESIZE | SWT.ON_TOP,
                           false,
                           false,
                           false,
                           false,
                           null,
                           null )
          {
            protected Control createDialogArea( final Composite parent ) {
              final Composite popup = new Composite( parent, SWT.NONE );
              popup.setBackgroundMode( SWT.INHERIT_FORCE );
              popup.setLayout( new FormLayout() );
              popup.setBackground( Graphics.getColor( 9, 34, 60 ) );
              
              Label roundedCornerLeft = new Label( popup, SWT.NONE );
              roundedCornerLeft.setImage( Images.IMG_BANNER_ROUNDED_LEFT );
              roundedCornerLeft.pack();
              FormData fdRoundedCornerLeft = new FormData();
              roundedCornerLeft.setLayoutData( fdRoundedCornerLeft );
              fdRoundedCornerLeft.top = new FormAttachment( 100, -5 );
              fdRoundedCornerLeft.left = new FormAttachment( 0, 0 );
              
              Label roundedCornerRight = new Label( popup, SWT.NONE );
              roundedCornerRight.setImage( Images.IMG_BANNER_ROUNDED_RIGHT );
              roundedCornerRight.pack();
              FormData fdRoundedCornerRight = new FormData();
              roundedCornerRight.setLayoutData( fdRoundedCornerRight );
              fdRoundedCornerRight.top = new FormAttachment( 100, -5 );
              fdRoundedCornerRight.left = new FormAttachment( 100, -5 );
              
              final Composite content = new Composite( popup, SWT.NONE );
              FormData fdContent = new FormData();
              content.setLayoutData( fdContent );
              fdContent.top = new FormAttachment( 0, 5 );
              fdContent.left = new FormAttachment( 0, 14 );
              
              content.setLayout( new FillLayout( SWT.VERTICAL ) );
              IContributionItem[] menuItems = menuManager.getItems();
              for( int j = 0; j < menuItems.length; j++ ) {
                IContributionItem contributionItem = menuItems[ j ];
                if( contributionItem instanceof ActionContributionItem ) {
                  ActionContributionItem actionItem
                    = ( ActionContributionItem )contributionItem;
                  Action action = ( Action )actionItem.getAction();
                  new ActionBarButton( action, content ) {
                    public void run() {
                      close();
                      super.run();
                    }
                  };
                }
                
System.out.println( contributionItem );
              }
              content.pack();

              return popup;
            }
          };
          
          final Composite popup = new Composite( shell, SWT.NONE );
          popup.setBackgroundMode( SWT.INHERIT_FORCE );
          popup.setLayout( new FormLayout() );
          
          Label roundedCornerLeft = new Label( popup, SWT.NONE );
          roundedCornerLeft.setImage( Images.IMG_BANNER_ROUNDED_LEFT );
          roundedCornerLeft.pack();
          FormData fdRoundedCornerLeft = new FormData();
          roundedCornerLeft.setLayoutData( fdRoundedCornerLeft );
          fdRoundedCornerLeft.top = new FormAttachment( 100, -5 );
          fdRoundedCornerLeft.left = new FormAttachment( 0, 0 );
          roundedCornerLeft.moveAbove( banner );
          
          Label roundedCornerRight = new Label( popup, SWT.NONE );
          roundedCornerRight.setImage( Images.IMG_BANNER_ROUNDED_RIGHT );
          roundedCornerRight.pack();
          FormData fdRoundedCornerRight = new FormData();
          roundedCornerRight.setLayoutData( fdRoundedCornerRight );
          fdRoundedCornerRight.top = new FormAttachment( 100, -5 );
          fdRoundedCornerRight.left = new FormAttachment( 100, -5 );
          roundedCornerRight.moveAbove( banner );

          final Composite content = new Composite( popup, SWT.NONE );
          FormData fdContent = new FormData();
          content.setLayoutData( fdContent );
          fdContent.top = new FormAttachment( 0, 5 );
          fdContent.left = new FormAttachment( 0, 14 );
          
          content.setLayout( new FillLayout( SWT.VERTICAL ) );
          IContributionItem[] menuItems = menuManager.getItems();
          for( int j = 0; j < menuItems.length; j++ ) {
            IContributionItem contributionItem = menuItems[ j ];
            if( contributionItem instanceof ActionContributionItem ) {
              ActionContributionItem actionItem
                = ( ActionContributionItem )contributionItem;
              Action action = ( Action )actionItem.getAction();
              new ActionBarButton( action, content );
            }
            
System.out.println( contributionItem );
          }
          content.pack();
          
          popup.setBackground( Graphics.getColor( 9, 34, 60 ) );
          Rectangle popUpBounds = calculatePopUpBounds( banner,
                                                        menuBar,
                                                        content );
          popup.setBounds( popUpBounds );
          shell.addControlListener( new ControlAdapter() {
            public void controlResized( final ControlEvent e ) {
              Rectangle popUpBounds = calculatePopUpBounds( banner,
                                                            menuBar,
                                                            content );
              popup.setBounds( popUpBounds );
            }
          } );
          popup.moveAbove( null );
          
          popupDialog.open();
          Listener closeListener = new Listener() {
            public void handleEvent( Event event ) {
              if( popupDialog.getShell() != null ) {
                popupDialog.getShell().removeListener( SWT.Close, this );
                popupDialog.getShell().removeListener( SWT.Deactivate, this );
                popupDialog.getShell().removeListener( SWT.Dispose, this );
                popupDialog.close();
              }
              if( !popup.isDisposed() ) {
                popup.dispose();
              }
            }
          };
          popupDialog.getShell().addListener( SWT.Deactivate, closeListener );
          popupDialog.getShell().addListener( SWT.Close, closeListener );
          popupDialog.getShell().addListener( SWT.Dispose, closeListener );
//          content.addListener( SWT.Dispose, closeListener );
//          Shell controlShell = content.getShell();
//          controlShell.addListener( SWT.Move, closeListener );

          popupDialog.getShell().setAlpha( 0 );
          popupDialog.getShell().setActive();
          popupDialog.getShell().setBounds( popUpBounds );

//          shell.addMouseListener( new MouseAdapter() {
//            public void mouseUp( final MouseEvent e ) {
//              
//System.out.println( "mouseup" );
//              shell.removeMouseListener( this );
//              popup.dispose();
//            }
//          } );
          
        }
        
        private Rectangle calculatePopUpBounds( final Composite banner,
                                                final Composite menuBar,
                                                final Composite content )
        {
          Rectangle menuBarBounds = menuBar.getBounds();
          Rectangle bannerBounds = banner.getBounds();
          Display display = menuBar.getDisplay();
          Shell shell = menuBar.getShell();
          Point menuBarPosition
            = display.map( menuBar.getParent(), shell, menuBar.getLocation() );
          Point bannerPosition
            = display.map( banner.getParent(), shell, banner.getLocation() );
          
          return new Rectangle( bannerPosition.x,
                                bannerBounds.height - 5,
                                menuBarBounds.width + 10,
                                content.getSize().y + 10 );
        }
      } );
      System.out.println( "menuitems: " + menuManager.getMenuText() );
    }
    ActionBar.create( actions, menuBar );
  }

  private void createPageComposite( final Shell shell ) {
    Composite content = new Composite( shell, SWT.NONE );
    content.setBackground( COLOR_SHELL_BG );
    FormData fdContent = new FormData();
    content.setLayoutData( fdContent );
    fdContent.top = new FormAttachment( 0, BANNER_HEIGTH + 4 );
    fdContent.left = new FormAttachment( 0, 43 );
    fdContent.right = new FormAttachment( 100, -43 );
    fdContent.bottom = new FormAttachment( 100, 0 );
    FillLayout fillLayout = new FillLayout();
    fillLayout.marginWidth = 3;
    content.setLayout( fillLayout );
    IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
    configurer.createPageComposite( content );
  }
}
