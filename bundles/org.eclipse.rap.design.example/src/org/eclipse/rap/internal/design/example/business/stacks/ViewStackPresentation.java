/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.internal.design.example.business.stacks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rap.internal.design.example.business.builder.BusinessStackBuider;
import org.eclipse.rap.internal.design.example.business.layoutsets.StackInitializer;
import org.eclipse.rap.ui.interactiondesign.ConfigurableStack;
import org.eclipse.rap.ui.interactiondesign.ConfigurationAction;
import org.eclipse.rap.ui.interactiondesign.IConfigurationChangeListener;
import org.eclipse.rap.ui.interactiondesign.PresentationFactory;
import org.eclipse.rap.ui.interactiondesign.layout.ElementBuilder;
import org.eclipse.rwt.lifecycle.WidgetUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.PartPane;
import org.eclipse.ui.internal.presentations.PresentablePart;
import org.eclipse.ui.presentations.IPartMenu;
import org.eclipse.ui.presentations.IPresentablePart;
import org.eclipse.ui.presentations.IStackPresentationSite;


public class ViewStackPresentation extends ConfigurableStack {
  
  private static final String VARIANT_PART_INACTIVE = "partInactive";
  private static final String VARIANT_PART_ACTIVE = "partActive";
  private static final int BUTTON_SPACING = 6;
  private static final String ID_CLOSE = "close";
  private static final String BUTTON_ID = "buttonId";
  private static final int SIZE_SPACING = 65;
  private Control presentationControl;
  private IPresentablePart currentPart;
  private ElementBuilder stackBuilder;
  private Composite tabBg;
  private Composite confArea;
  private Button confButton;
  private Label confCorner;
  private Map partButtonMap = new HashMap();
  private List partList = new ArrayList();
  private List buttonList = new ArrayList();
  private Composite toolbarBg;
  private Shell toolBarLayer;
  private int state;
  protected boolean deactivated;
  private Button viewMenuButton;
  private Map dirtyListenerMap = new HashMap();
  private Button overflowButton;
  private List overflowButtons = new ArrayList();
  private Map buttonPartMap = new HashMap();
  
  private class DirtyListener implements IPropertyListener {
    
    private IPresentablePart part;
    
    public DirtyListener( final IPresentablePart part ) {
      this.part = part;
    }
    
    public void propertyChanged( Object source, int propId ) {
      if( propId == ISaveablePart.PROP_DIRTY ) {  
        Button partButton = getPartButton( part );
        if( partButton != null ) {
          String text = partButton.getText();
          char lastCharacter = getLastCharacter( text );
          if( part.isDirty() ) {
            // mark the part as dirty                                        
            if( lastCharacter != '*') {
              text = text + "*";
            }
          } else {
            // mark the part as clean
            if( lastCharacter == '*' ) {
              text = text.substring( 0, text.length() - 1 );
            }
          }
          partButton.setText( text );
        }
      }
    }
    
    private Button getPartButton( final IPresentablePart part ) {
      Button result = null;
      Object object = partButtonMap.get( part );
      if( object instanceof Composite ) {
        Control[] children = ( ( Composite ) object ).getChildren();
        if( children.length > 0 && children[ 0 ] instanceof Button ) {
          result = ( Button ) children[ 0 ];
        }
      }
      return result;
    }
    
    private char getLastCharacter( final String text ) {
      char[] starArray = new char[ 1 ];
      text.getChars( text.length()-1, text.length(), starArray, 0);
      return starArray[ 0 ];
    }
  };

  public ViewStackPresentation() {
    state = AS_INACTIVE;
    deactivated = false;
  }

  public void init() {
    ConfigurationAction action = getConfigAction();
    if( action != null ) {
      action.addConfigurationChangeListener( new IConfigurationChangeListener(){
      
        public void toolBarChanged() {
          ViewToolBarRegistry registry = ViewToolBarRegistry.getInstance();
          registry.fireToolBarChanged( );          
        }
      
        public void presentationChanged( final String newStackPresentationId ) {
          // do nothing atm
        }
      } );
    }    
    presentationControl = createStyledControl();
    ViewToolBarRegistry registry = ViewToolBarRegistry.getInstance();
    registry.addViewPartPresentation( this );
  }
  
  
  void catchToolbarChange() {
    layoutToolBar();
    setBounds( presentationControl.getBounds() );
  }


  private void createToolBarBg() {
    Composite tabBar = getTabBar();
    toolbarBg = new Composite( tabBar.getParent(), SWT.NONE );
    toolbarBg.setLayout( new FormLayout() );
    Image bg = stackBuilder.getImage( StackInitializer.VIEW_TOOLBAR_BG );
    toolbarBg.setBackgroundImage( bg );
    FormData fdToolBar = new FormData();
    toolbarBg.setLayoutData( fdToolBar );
    fdToolBar.left = new FormAttachment( 0 );
    fdToolBar.right = new FormAttachment( 100 );
    fdToolBar.top = new FormAttachment( tabBar );
    fdToolBar.height = bg.getBounds().height; 
    toolbarBg.moveAbove( tabBar );
  }

  private Control createStyledControl() {
    getParent().setData( WidgetUtil.CUSTOM_VARIANT, "compGray" );
    final Composite parent = new Composite( getParent(), SWT.NONE );
    parent.addControlListener( new ControlAdapter() {
      public void controlResized( ControlEvent e ) {
        setBounds( parent.getBounds() );
      };
    } );    
    
    parent.setData( WidgetUtil.CUSTOM_VARIANT, "compGray" );
    stackBuilder = new BusinessStackBuider( parent, StackInitializer.SET_ID );
    stackBuilder.build();
    return parent;
  }
  
  private boolean isStandalone() {
    return getType().equals( PresentationFactory.KEY_STANDALONE_VIEW );
  }

  public void addPart( final IPresentablePart newPart, final Object cookie ) {
    checkTabBg();
    if( !isStandalone() ) {
      createPartButton( newPart );
      partList.add( newPart );
      Control partControl = newPart.getControl();
      if( partControl != null ) {
        partControl.getParent().setBackgroundMode( SWT.INHERIT_NONE );
        partControl.setData( WidgetUtil.CUSTOM_VARIANT, "partBorder" );
      }
    } else {
      decorateStandaloneView( newPart );
    }
    // add the lsitener for the dirty state
    IPropertyListener listener = new DirtyListener( newPart );
    dirtyListenerMap .put( newPart, listener );
    newPart.addPropertyListener( listener );
  }
  
  private void decorateStandaloneView( final IPresentablePart newPart ) {
    checkTabBg();
    if( getShowTitle() ) {
      getTabBar().setVisible( true );
      tabBg.setVisible( true );
      Label title = new Label( tabBg, SWT.NONE );
      title.setData( WidgetUtil.CUSTOM_VARIANT, "standaloneView" );
      title.setText( newPart.getName() );      
    } else {
      getTabBar().setVisible( false );      
    }    
  }

  private void layoutToolBar() {
    if( toolbarBg == null && tabBg != null ) {
      createToolBarBg();       
    }    
    if( currentPart != null ) {      
      Control toolBar = currentPart.getToolBar();
      final IPartMenu viewMenu = currentPart.getMenu();
      // viewmenu
      if( viewMenu != null ) {
        if( viewMenuButton == null ) {
          viewMenuButton = new Button( toolbarBg, SWT.PUSH );
          viewMenuButton.setData( WidgetUtil.CUSTOM_VARIANT, "clearButton" );
          Image icon = stackBuilder.getImage( StackInitializer.VIEW_MENU_ICON );
          viewMenuButton.setImage( icon );
          FormData fdViewMenuButton = new FormData();
          viewMenuButton.setLayoutData( fdViewMenuButton );
          fdViewMenuButton.right = new FormAttachment( 100, -3 );
          fdViewMenuButton.top = new FormAttachment( 0, 7 );
          viewMenuButton.addSelectionListener( new SelectionAdapter() {
            public void widgetSelected( final SelectionEvent e ) {
              Display display = viewMenuButton.getDisplay();
              int height = viewMenuButton.getSize().y;
              Point newLoc = display.map( viewMenuButton, null, 0, height );
              viewMenu.showMenu( newLoc );
            };
          } );
        }
      } else if( viewMenuButton != null ) {
        viewMenuButton.setVisible( false );
        viewMenuButton.dispose();
        viewMenuButton = null;
      }
      // toolbar
      Point size = toolbarBg.getSize();
      if( toolBar != null ) {
        Point point = currentPart.getControl().getLocation();        
        point.y -= ( size.y + 2 );
        point.x += ( size.x - toolBar.getSize().x );
        if( viewMenu != null ) {
          point.x -= 20;
        }
        toolBar.setLocation( point );
        
        toolBar.setVisible( true );
        
        toolbarBg.moveBelow( toolBar );
        presentationControl.moveBelow( toolBar );  
        currentPart.getControl().moveBelow( toolBar );
      } 
      // toolbarbg and layer
      if( toolBar != null || viewMenu != null ) {
        toolbarBg.setVisible( true );        
        // Toolbar Layer
        if( !deactivated ) {          
          getToolBarLayer();
          toolBarLayer.setVisible( false );
          if( state != AS_ACTIVE_FOCUS ) {
            Display display = toolBarLayer.getDisplay();
            Point newLocation = display.map( toolbarBg, null, 0, 0 );
            toolBarLayer.setBounds( newLocation.x, 
                                    newLocation.y, 
                                    size.x, 
                                    size.y - 1 
                                  );
            toolBarLayer.setVisible( true );
          }
        }
      } else {
        toolbarBg.setVisible( false );
      }
      toolbarBg.layout( true );       
    }    
  }

  private void createPartButton( final IPresentablePart part ) {
    Composite buttonArea = new Composite( tabBg, SWT.NONE );
    buttonArea.setData( WidgetUtil.CUSTOM_VARIANT, "compTrans" );
    Image bg = stackBuilder.getImage( StackInitializer.TAB_INACTIVE_BG_ACTIVE );
    buttonArea.setBackgroundImage( bg );
    buttonArea.setBackgroundMode( SWT.INHERIT_FORCE );
    buttonArea.setLayout( new FormLayout() );
    
    Button partButton = new Button( buttonArea, SWT.PUSH );
    partButton.setData( WidgetUtil.CUSTOM_VARIANT, VARIANT_PART_INACTIVE );
    partButton.setText( part.getName() );
    FormData fdPartButton = new FormData();
    partButton.setLayoutData( fdPartButton );
    fdPartButton.left = new FormAttachment( 0 );
    fdPartButton.top = new FormAttachment( 0, 4 );
    fdPartButton.bottom = new FormAttachment( 100 );
    partButton.addSelectionListener( new SelectionAdapter() {
      public void widgetSelected( SelectionEvent e ) {    
        if( !currentPart.equals( part ) ) {
          selectPart( part );
          layoutToolBar();
        }
        activatePart( part );
      };
    } );
    partButton.addListener( SWT.MouseDoubleClick, new Listener() {    
      public void handleEvent( Event event ) {
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        IWorkbenchPage page = window.getActivePage();
        page.toggleZoom( getReference( part ) );
      }
    } );
    
    Composite corner = new Composite( buttonArea, SWT.NONE );
    corner.setData( WidgetUtil.CUSTOM_VARIANT, "compTrans" );
    corner.setLayout( new FormLayout() );
    Image cornerImage 
      = stackBuilder.getImage( StackInitializer.TAB_INACTIVE_SEPARATOR_ACTIVE );
    corner.setBackgroundImage( cornerImage );
    FormData fdCorner = new FormData();
    corner.setLayoutData( fdCorner );
    fdCorner.right = new FormAttachment( 100 );
    fdCorner.bottom = new FormAttachment( 100 );
    fdCorner.width = cornerImage.getBounds().width;
    fdCorner.height = cornerImage.getBounds().height;
    fdPartButton.right = new FormAttachment( corner, -8 );
    partButtonMap.put( part, buttonArea );
    buttonPartMap.put( buttonArea, part );
    buttonList.add( buttonArea );
  }

  protected void activatePart( final IPresentablePart part ) {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    IWorkbenchPage activePage = window.getActivePage();
    IWorkbenchPart workbenchPart = getReference( part ).getPart( true );
    if( workbenchPart != null ) {
      activePage.activate( workbenchPart );
    }
  }
  
  private IWorkbenchPartReference getReference( final IPresentablePart part) {
    IWorkbenchPartReference result = null;    
    if( part instanceof PresentablePart ) {
      PresentablePart presentablePart = ( PresentablePart ) part;
      PartPane pane = presentablePart.getPane();
      result = pane.getPartReference();
    }
    return result;
  }

  private void makePartButtonActive( final IPresentablePart part ) {
    Object object = partButtonMap.get( part );
    if( object instanceof Composite ) {
      Composite buttonArea = ( Composite ) object;
      checkHideSeparator( buttonArea );
      Image bg = stackBuilder.getImage( StackInitializer.CONF_BG_INACTIVE );
      buttonArea.setBackgroundImage( bg );
      Control[] children = buttonArea.getChildren();
      buttonArea.setLayout( new FormLayout() );
      
      for( int i = 0; i < children.length; i++ ) {
        Control child = children[ i ];
        if( child instanceof Button ) {
          // Partbutton
          Button partButton = ( Button ) child;
          partButton.setData( WidgetUtil.CUSTOM_VARIANT, VARIANT_PART_ACTIVE );
          FormData fdButton = ( FormData ) partButton.getLayoutData();
          fdButton.top = new FormAttachment( 0, 0 );
        } else if( child instanceof Composite ) {
          // Corner
          Composite corner = ( Composite ) child;
          corner.setVisible( true );
          String cornerDesc = StackInitializer.TAB_INACTIVE_CORNER_ACTIVE;
          Image cornerImage = stackBuilder.getImage( cornerDesc );
          corner.setBackgroundImage( cornerImage );
          FormData fdCorner = ( FormData ) corner.getLayoutData();
          fdCorner.top = new FormAttachment( 0 );
          fdCorner.width = cornerImage.getBounds().width;
          fdCorner.height = cornerImage.getBounds().height;
          if( part.isCloseable() ) {
            Button close = new Button( buttonArea, SWT.PUSH );
            close.setData( BUTTON_ID, ID_CLOSE );            
            close.setData( WidgetUtil.CUSTOM_VARIANT, "viewCloseInactive" );
            close.addSelectionListener( new SelectionAdapter() {
              public void widgetSelected( SelectionEvent e ) {
                getSite().close( new IPresentablePart[] { part } );
              };
            } );
            FormData fdClose = new FormData();
            close.setLayoutData( fdClose );
            fdClose.right = new FormAttachment( 100, -5 );
            fdClose.top = new FormAttachment( 0, 6 );
            fdClose.width = 8;
            fdClose.height = 8;
            close.setLayoutData( fdClose );
            close.moveAbove( corner );
          }
        }
      }
      buttonArea.getParent().layout();
    }
    
  }
  
  private void checkHideSeparator( final Composite buttonArea ) {
    int indexOf = buttonList.indexOf( buttonArea );
    for( int i = 0; i < buttonList.size(); i++ ) {
      Composite area = ( Composite ) buttonList.get( i );
      Control[] children = area.getChildren();
      for( int j = 0; j < children.length; j++ ) {
        if( children[ j ] instanceof Composite ) {
          
          if( i == indexOf || ( i == indexOf - 1 ) ) {
            ( ( Composite ) children[ j ] ).setVisible( false );
          } else {
            ( ( Composite ) children[ j ] ).setVisible( true );
          }
          
        }
      }     
    }
  }

  private void makePartButtonInactive( final IPresentablePart part ) {
    Object object = partButtonMap.get( part );
    if( object instanceof Composite ) {
      Composite buttonArea = ( Composite ) object;
      String bgDesc = StackInitializer.TAB_INACTIVE_BG_ACTIVE;
      Image bg = stackBuilder.getImage( bgDesc );
      buttonArea.setBackgroundImage( bg );
      Control[] children = buttonArea.getChildren();
      for( int i = 0; i < children.length; i++ ) {
        Control child = children[ i ];
        if( child instanceof Button ) {
          Button button = ( Button ) child;
          // Partbutton
          if( button.getData( BUTTON_ID ) != null ) {
            // close button            
            button.setVisible( false );
            button.dispose();
          } else {
            // Part button
            button.setData( WidgetUtil.CUSTOM_VARIANT, VARIANT_PART_INACTIVE );
            FormData fdButton = ( FormData ) button.getLayoutData();
            fdButton.top = new FormAttachment( 0, 4 );            
          }
        } else if( child instanceof Composite ) {
          // Corner
          Composite corner = ( Composite ) child;
          corner.setVisible( true );
          String sepConst = StackInitializer.TAB_INACTIVE_SEPARATOR_ACTIVE;
          Image cornerImage = stackBuilder.getImage( sepConst );
          corner.setBackgroundImage( cornerImage );
          FormData fdCorner = ( FormData ) corner.getLayoutData();
          fdCorner.width = cornerImage.getBounds().width;
          fdCorner.height = cornerImage.getBounds().height;
          fdCorner.top = new FormAttachment( 0, 6 );
        } 
      }
      buttonArea.getParent().layout();
    }
    
  }

  /*
   * check if the tabBg exists. If not it will create it.
   */
  private void checkTabBg() {
    Composite tabBar = getTabBar();
    if( tabBg == null && tabBar != null ) {
      tabBg = new Composite( tabBar, SWT.NONE );
      tabBg.setData( WidgetUtil.CUSTOM_VARIANT, "compTrans" );
      FormData fdTabBg = new FormData();
      tabBg.setLayoutData( fdTabBg );
      fdTabBg.left = new FormAttachment( 0 );
      fdTabBg.top = new FormAttachment( 0 );
      fdTabBg.bottom = new FormAttachment( 100 );
      
      createConfArea( fdTabBg );

      RowLayout layout = new RowLayout( SWT.HORIZONTAL );
      layout.spacing = 0;
      layout.marginBottom = 0;
      if( !isStandalone() ) {
        layout.marginHeight = 0;
        layout.marginLeft = 0;
      } else {
        layout.marginHeight = 4;
        layout.marginLeft = BUTTON_SPACING;
      }
      layout.marginRight = 16;
      layout.marginTop = 0;
      layout.marginWidth = 0;
      layout.wrap = false;
      tabBg.setLayout( layout );
      // calculate overflow
      presentationControl.addControlListener( new ControlAdapter() {
        public void controlResized( final ControlEvent e ) {
          manageOverflow();        
        };
      } );
    }
  }

  private void manageOverflow() {
    int tabChildrenSize = getTabChildrenSize();
    if( tabChildrenSize > tabBg.getBounds().width 
        && moreThanOneChildVisible() ) 
    {
      hideLastVisibleButton();
      manageOverflow();
    } else {
      showLastChildIfNecessary();
    }
    handleOverflowButton();
  }

  private boolean moreThanOneChildVisible() {
    boolean result = false;
    Control[] children = tabBg.getChildren();
    int visibleChilds = 0;
    for( int i = 0; i < children.length && !result; i++ ) {
      if( children[ i ].isVisible() ) {
        visibleChilds++;
        if( visibleChilds > 1 ) {
          result = true;
        }
      }
    }
    return result;
  }

  private void handleOverflowButton() {
    if( overflowButton == null ) {
      overflowButton = new Button( tabBg.getParent(), SWT.PUSH );
      FormData fdOverflowButton = new FormData();
      overflowButton.setLayoutData( fdOverflowButton );
      fdOverflowButton.top = new FormAttachment( 0, 5 );
      fdOverflowButton.right = new FormAttachment( 100, -22 );      
      Image icon 
        = stackBuilder.getImage( StackInitializer.TAB_OVERFLOW_ACTIVE );
      fdOverflowButton.height = icon.getBounds().height;
      fdOverflowButton.width = icon.getBounds().width;
      String inactiveVariant = "tabOverflowInactive";
      overflowButton.setData( WidgetUtil.CUSTOM_VARIANT, inactiveVariant );
      overflowButton.moveAbove( tabBg );
      overflowButton.addSelectionListener( new SelectionAdapter() {
        public void widgetSelected( final SelectionEvent e ) {
          performOverflow();
        };
      } );
    }
    if( tabBgHasInvisibleButtons() ) {
      overflowButton.setVisible( true );
    } else {
      overflowButton.setVisible( false );
    }
  }

  private void performOverflow() {
    activatePart( currentPart );
    Menu overflowMenu = new Menu( overflowButton );
    for( int i = 0; i < overflowButtons.size(); i++ ) {
      Object obj = buttonPartMap.get( overflowButtons.get( i ) );
      final IPresentablePart part = ( IPresentablePart ) obj;
      MenuItem item = new MenuItem( overflowMenu, SWT.PUSH );
      item.setText( part.getName() );
      item.addSelectionListener( new SelectionAdapter() {
        public void widgetSelected( final SelectionEvent e ) {
          activatePart( part );
          showPartButton( part );
        };
      } );
    }
    // show popup
    overflowButton.setMenu( overflowMenu );
    overflowMenu.setVisible( true );
    Display display = overflowButton.getDisplay();
    Point newLocation = display.map( overflowButton, null, 0, 10 );
    overflowMenu.setLocation( newLocation );    
  }

  private void showPartButton( final IPresentablePart part ) {
    Control button = ( Control ) partButtonMap.get( part );
    Control hiddenButton = hideLastVisibleButton();
    button.setVisible( true );
    overflowButtons.remove( button );
    button.moveAbove( hiddenButton );
    tabBg.layout( true, true );
    manageOverflow();
  }

  private void showLastChildIfNecessary() {
    Control childToShow = getLastInvisibleButton();
    if( childToShow != null 
        && futureTabChildrenSize( childToShow ) < tabBg.getBounds().width &&
        tabBgHasInvisibleButtons() ) 
    {
      childToShow.setVisible( true );
      IPresentablePart part 
        = ( IPresentablePart ) buttonPartMap.get( childToShow );
      makePartButtonInactive( part );
      overflowButtons.remove( childToShow );
      showLastChildIfNecessary();
    }
  }

  private boolean tabBgHasInvisibleButtons() {
    boolean result = false;
    Control[] children = tabBg.getChildren();
    for( int i = 0; i < children.length && !result; i++ ) {
      if( !children[ i ].isVisible() ) {
        result = true;
      }
    }
    return result;
  }

  private int futureTabChildrenSize( final Control childToShow ) {
    int result = 0;
    result = getTabChildrenSize();
    result += childToShow.getBounds().width;
    return result;
  }

  private Control getLastInvisibleButton() {
    Control result = null;
    Control[] children = tabBg.getChildren();
    boolean childShowedUp = false;
    for( int i = children.length - 1; i >= 0 && !childShowedUp; i-- ) {
      if( children[ i ].isVisible() ) {
        if( children.length >= ( i + 2 ) ) {
          result = children[ i + 1 ];
        } else {
          result = children[ i ];
        }
        childShowedUp = true;
      }
    }
    return result;
  }

  /*
   * Returns the control which was hide.
   */
  private Control hideLastVisibleButton() {
    Control result = null;
    Control[] children = tabBg.getChildren();
    boolean lastChildHidden = false;
    for( int i = children.length - 1; i >= 0 && !lastChildHidden; i-- ) {
      if( children[ i ].isVisible() ) {
        if( buttonIsActive( children[ i ] ) ) {
          if( i > 0 ) {
            children[ i - 1 ].setVisible( false );
            result = children[ i - 1 ];
            children[ i ].moveAbove( children[ i - 1 ] );
            overflowButtons.add( children[ i - 1 ] );
          }
        } else {
          children[ i ].setVisible( false );
          result = children[ i ];
          overflowButtons.add( children[ i ] );
        }
        lastChildHidden = true;
        tabBg.layout( true, true );        
      }
    }
    return result;
  }

  private boolean buttonIsActive( final Control control ) {
    boolean result = false;
    if( control instanceof Composite ) {
      Composite buttonArea = ( Composite ) control;
      Control[] children = buttonArea.getChildren();
      for( int i = 0; i < children.length && !result; i++ ) {
        if( children[ i ] instanceof Button ) {
          Object data = children[ i ].getData( WidgetUtil.CUSTOM_VARIANT );
          if( data.equals( VARIANT_PART_ACTIVE ) ) {
            result = true;
          }
        }
      }
    }    
    return result;
  }

  private int getTabChildrenSize() {
    int result = 0;
    Control[] children = tabBg.getChildren();
    for( int i = 0; i < children.length; i++ ) {
      if( children[ i ].isVisible() && !children[ i ].isDisposed() ) {
        result += ( children[ i ].getSize().x + BUTTON_SPACING );
      }
    }
    return result;
  }

  private void createConfArea( final FormData fdTabBg ) {
    final ConfigurationAction configAction = getConfigAction(); 
    
    if( configAction != null ) {
      confArea = new Composite( getTabBar(), SWT.NONE );
      Image confBg = stackBuilder.getImage( StackInitializer.CONF_BG_INACTIVE );
      confArea.setBackgroundImage( confBg );
      confArea.setLayout( new FormLayout() );
      FormData fdConfArea = new FormData();
      confArea.setLayoutData( fdConfArea );
      fdConfArea.top = new FormAttachment( 0 );
      fdConfArea.bottom = new FormAttachment( 100 );
      fdConfArea.right = new FormAttachment( 100 );    
      fdTabBg.right = new FormAttachment( confArea );
      
      confCorner = new Label( confArea, SWT.NONE );
      Image cornerImage 
        = stackBuilder.getImage( StackInitializer.INACTIVE_CORNER );
      confCorner.setImage( cornerImage );
      FormData fdCorner = new FormData();
      confCorner.setLayoutData( fdCorner );
      fdCorner.left = new FormAttachment( 0 );
      fdCorner.top = new FormAttachment( 0 );
      fdCorner.bottom = new FormAttachment( 100 );
      
      confButton = new Button( confArea, SWT.PUSH );
      Image confImage = stackBuilder.getImage( StackInitializer.CONF_INACTIVE );
      confButton.setImage( confImage );
      confButton.setData( WidgetUtil.CUSTOM_VARIANT, "clearButton" );
      FormData fdConfButton = new FormData();
      confButton.setLayoutData( fdConfButton );
      fdConfButton.left = new FormAttachment( confCorner );
      fdConfButton.top = new FormAttachment( 0, 1 );
      fdConfButton.right = new FormAttachment( 100, 0 );
      
      confButton.addSelectionListener( new SelectionAdapter(){
        public void widgetSelected( SelectionEvent e ) {
          activatePart( getSite().getSelectedPart() );
          configAction.run();
        };
      } );
      
    } else {
      // make tabarea full width if no confarea exist.
      fdTabBg.right = new FormAttachment( 100 );
    }    
  }

  public void dispose() {
    ViewToolBarRegistry registry = ViewToolBarRegistry.getInstance();
    registry.removeViewPartPresentation( this );
    if( toolBarLayer != null ) {
      toolBarLayer.dispose();
    }
    presentationControl.dispose();
  }

  public Control getControl() {
    return presentationControl;
  }

  public Control[] getTabList( final IPresentablePart part ) {
    ArrayList list = new ArrayList();
    if (getControl() != null) {
        list.add(getControl());
    }
    if (part.getToolBar() != null) {
        list.add(part.getToolBar());
    }
    if (part.getControl() != null) {
        list.add(part.getControl());
    }
    return (Control[]) list.toArray(new Control[list.size()]);
  }

  public void removePart( final IPresentablePart oldPart ) {
    Object object = partButtonMap.get( oldPart );
    buttonPartMap.remove( object );
    if( toolBarLayer != null ) {
      toolBarLayer.setVisible( false );
    }
    // remove the dirtyListener
    Object listener = dirtyListenerMap.get( oldPart );
    if( listener != null && listener instanceof IPropertyListener ) {
      oldPart.removePropertyListener( ( IPropertyListener ) listener ); 
    }
    partButtonMap.remove( oldPart );
    buttonList.remove( object );
    ( ( Composite ) object ).dispose(); 
    partList.remove( oldPart );
    oldPart.setVisible( false );
    tabBg.layout();
  }

  public void selectPart( final IPresentablePart toSelect ) {    
    if( toSelect != null ) {
      toSelect.setVisible( true );
    }
    if( currentPart != null  ) {
      if( currentPart instanceof PresentablePart 
          && ( (PresentablePart) currentPart ).getPane() != null ) {
        currentPart.setVisible( false ); 
      }
    }
    makePartButtonInactive( currentPart );
    currentPart = toSelect;
    makePartButtonActive( currentPart );
    layoutToolBar();
  }

  public void setActive( final int newState ) {
    state = newState;
    Image confBg = null;
    Image cornerImage = null;
    Image confImage = null;
    String tabOverflow = "tabOverflowInactive";
    // create the necessary images
    if( newState == AS_ACTIVE_FOCUS ) {
      if( !isStandalone() ) {
        changeSelectedActiveButton( true );
      }
      confBg = stackBuilder.getImage( StackInitializer.CONF_BG_ACTIVE );
      cornerImage 
        = stackBuilder.getImage( StackInitializer.TAB_INACTIVE_RIGHT_ACTIVE );
      confImage = stackBuilder.getImage( StackInitializer.CONF_ACTIVE );
      tabOverflow = "tabOverflowActive";
    } else {
      if( !isStandalone() ) {
        changeSelectedActiveButton( false );
      }
      confBg = stackBuilder.getImage( StackInitializer.CONF_BG_INACTIVE );
      cornerImage 
        = stackBuilder.getImage( StackInitializer.INACTIVE_CORNER );
      confImage = stackBuilder.getImage( StackInitializer.CONF_INACTIVE );
    }
    
    // set the images
    if( confArea != null ) {
      confArea.setBackgroundImage( confBg );
      if( confCorner != null ) {
        confCorner.setImage( cornerImage );
      }
      if( confButton != null ) {
        confButton.setImage( confImage );
      }
      confArea.getParent().layout( true );
      if( currentPart != null ) {
        currentPart.setVisible( true );
      }
      confArea.layout( true );
    }
    if( overflowButton != null ) {
      overflowButton.setData( WidgetUtil.CUSTOM_VARIANT, tabOverflow );
    }
    setBounds( presentationControl.getBounds() );
  }


  private void changeSelectedActiveButton( final boolean selected ) {
    Image buttonAreaBg = null;
    Image corner = null;
    String close = "";
    if( selected ) {
      buttonAreaBg 
        = stackBuilder.getImage( StackInitializer.TAB_ACTIVE_BG_ACTIVE );
      corner = 
        stackBuilder.getImage( StackInitializer.TAB_ACTIVE_RIGHT_ACTIVE );
      close = "viewClose";
      
    } else {
      buttonAreaBg = stackBuilder.getImage( StackInitializer.CONF_BG_INACTIVE );
      corner 
        = stackBuilder.getImage( StackInitializer.TAB_INACTIVE_CORNER_ACTIVE );
      close = "viewCloseInactive";
    }
    Object object = partButtonMap.get( currentPart );
    if( object != null && object instanceof Composite ) {
      Composite buttonArea = ( Composite ) object;
      buttonArea.setBackgroundImage( buttonAreaBg );
      Control[] children = buttonArea.getChildren();
      for( int i = 0; i < children.length; i++ ) {
        Control child = children[ i ];
        if( child instanceof Composite ) {
          // Corner
          Composite cornerComp = ( Composite ) child;
          cornerComp.setBackgroundImage( corner );          
        } else if( child instanceof Button ) {
          Button button = ( Button ) child;
          if( button.getData( BUTTON_ID ) != null ) {
            button.setData( WidgetUtil.CUSTOM_VARIANT, close );
          }
        }
      }
    }    
  }

  public void setBounds( final Rectangle bounds ) {
    presentationControl.setBounds( bounds );
    Composite tabBar = getTabBar();
    if( currentPart != null && tabBar != null ) {
      int newHeight = bounds.height - 16;
      int partBoundsY = bounds.y + 8;
      if( getTabBar().isVisible() ) {
        newHeight -= ( tabBar.getBounds().height );
        partBoundsY += tabBar.getBounds().height;
      }

      Control toolBar = currentPart.getToolBar();
      if( toolbarBg != null && ( toolbarBg.isVisible() || toolBar != null ) ) {
        int toolbarHeight = toolbarBg.getBounds().height;
        newHeight -= toolbarHeight;
        partBoundsY += toolbarHeight;
      } 
      
      Rectangle partBounds = new Rectangle( bounds.x + 8, 
                                            partBoundsY, 
                                            bounds.width - 16, 
                                            newHeight );
      currentPart.setBounds( partBounds );
    }
    layoutToolBar();
  }

  private Shell getToolBarLayer() {
    if( toolBarLayer == null && toolbarBg != null ) {
      Display display = toolbarBg.getDisplay();
      toolBarLayer = new Shell( display, SWT.NO_TRIM | SWT.ON_TOP );
      toolBarLayer.setData( WidgetUtil.CUSTOM_VARIANT, "toolbarLayer" );
      toolBarLayer.setAlpha( 200 );  
      toolBarLayer.open();
      toolBarLayer.addListener( SWT.MouseDown, new Listener() {      
        public void handleEvent( final Event event ) {
          activatePart( currentPart );
        }
      } );
    }
    return toolBarLayer;
  }


  private Composite getTabBar() {
    Composite result = null;
    Object adapter = stackBuilder.getAdapter( this.getClass() );
    if( adapter != null && adapter instanceof Composite ) {
      result = ( Composite ) adapter;
    }
    return result;
  }

  public void setState( final int state ) {
  }

  public void setVisible( final boolean isVisible ) {
    if( currentPart != null ) {
      currentPart.setVisible( isVisible );
      // Toolbar Layer
      deactivated = !isVisible;
      layoutToolBar();      
      if( toolBarLayer != null ) {
        if( !isVisible ) {          
          toolBarLayer.setVisible( false );
        }
        
      }
      setBounds( presentationControl.getBounds() );
    }
  }

  public void showPaneMenu() {
  }

  public void showSystemMenu() {
  }
  
  public int computePreferredSize( 
    final boolean width,
    final int availableParallel,
    final int availablePerpendicular,
    final int preferredResult )
  {
    int minSize = calculateMinimumSize();
    int result = preferredResult;
    if (getSite().getState() == IStackPresentationSite.STATE_MINIMIZED 
        || preferredResult < minSize) 
    {
      result = minSize;
    }    
    return result;
  }

  /*
   * Calculates the size of the biggest child
   */
  private int calculateMinimumSize() {
    int result = 0;
    if( tabBg != null ) {
      Control[] children = tabBg.getChildren();
      for( int i = 0; i < children.length; i++ ) {
        if( children[ i ].getSize().x >= result ) {
          result = children[ i ].getSize().x;
        }
      }    
    }
    return result + SIZE_SPACING;
  }
}
