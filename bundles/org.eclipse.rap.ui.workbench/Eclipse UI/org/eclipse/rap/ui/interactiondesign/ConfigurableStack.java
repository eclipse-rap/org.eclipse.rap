/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.ui.interactiondesign;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.internal.provisional.action.IToolBarManager2;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.rap.ui.interactiondesign.internal.ConfigurableStackProxy;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.DefaultStackPresentationSite;
import org.eclipse.ui.internal.LayoutPart;
import org.eclipse.ui.internal.PartPane;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.internal.presentations.PresentablePart;
import org.eclipse.ui.internal.util.PrefUtil;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.presentations.IPartMenu;
import org.eclipse.ui.presentations.IPresentablePart;
import org.eclipse.ui.presentations.IStackPresentationSite;
import org.eclipse.ui.presentations.StackPresentation;

/**
 * This represents an object, which extends the original <code>
 * {@link StackPresentation}</code>. 
 * <p>
 * You can do everything with this as you can do with the original one. It 
 * just provides more methods for making a StackPresentation configurable.
 * </p>
 * 
 * @see StackPresentation
 * @since 1.2
 */
public abstract class ConfigurableStack extends StackPresentation {

  private static final String CONFIG_ACTION_NAME = "actionClass";
  
  private static IStackPresentationSite siteDummy 
    = new IStackPresentationSite() 
  {

    public void addSystemActions( final IMenuManager menuManager ) {
      
    }
    
    public void close( final IPresentablePart[] toClose ) {
      
    }

    public void dragStart( 
      final IPresentablePart beingDragged,
      final Point initialPosition,
      final boolean keyboard )
    {
      
    }

    public void dragStart( 
      final Point initialPosition, 
      final boolean keyboard ) 
    {
      
    }

    public void flushLayout() {
      
    }

    public IPresentablePart[] getPartList() {
      return null;
    }

    public String getProperty( final String id ) {
      return null;
    }

    public IPresentablePart getSelectedPart() {
      return null;
    }

    public int getState() {
      return 0;
    }

    public boolean isCloseable( final IPresentablePart toClose ) {
      return false;
    }

    public boolean isPartMoveable( final IPresentablePart toMove ) {
      return false;
    }

    public boolean isStackMoveable() {
      return false;
    }

    public void selectPart( final IPresentablePart toSelect ) {
      
    }

    public void setState( final int newState ) {
      
    }

    public boolean supportsState( final int state ) {
      return false;
    }
    
  };
  
  /**
   * Extension Point ID of the StackPresentation Extension point.
   */
  public static final String STACK_PRESENTATION_EXT_ID 
    = "org.eclipse.rap.ui.stackPresentations";
  /**
   * This method is used for getting the <code>{@link LayoutPart}</code> ID for 
   * a specific <code>IStackPresentationSite</code>. Their is no other 
   * oppertunity to get this id.
   * 
   * @param site an instance of <code>IStackPreentationSite</code>.
   * 
   * @return the unique <code>LayoutPart</code> ID defined in a Perspective or 
   * <code>null</code> if their is no id.
   * 
   * @see LayoutPart
   * @see IStackPresentationSite
   */
  public static final String getLayoutPartId( 
    final IStackPresentationSite site ) 
  {
    String result = null;
    if( site != null && site instanceof DefaultStackPresentationSite ) {
      DefaultStackPresentationSite defaultSite 
        = ( DefaultStackPresentationSite) site;
      result = defaultSite.getProperty( "id" );     
    }
    return result;
  }
  /**
   * Loads the saved <code>ConfigurableStack</code> id from the preferences
   * store using a specific <code>IStackPresentationSite</code>. This id is
   * needed to instantiate the <code>ConfigurableStack</code> for a specific
   * LayoutPart.
   * 
   * @param site an instance of <code>IStackPreentationSite</code>.
   * 
   * @return the saved <code>ConfigurableStack</code> id for the part 
   * represented by the <code>IStackPresentationSite</code> or <code>null</code>
   * if no id is saved.
   * 
   * @see LayoutPart
   * @see IStackPresentationSite
   */
  public static String getSavedStackId( final IStackPresentationSite site ) {
    String layoutPartId = null;
    String result = IPreferenceStore.STRING_DEFAULT_DEFAULT;
    
    layoutPartId = getLayoutPartId( site );
    
    if( layoutPartId != null ) {    
      ScopedPreferenceStore prefStore
        = ( ScopedPreferenceStore )PrefUtil.getAPIPreferenceStore();
      String stackPresentationId = ConfigurableStackProxy.STACK_PRESENTATION_ID;
      String stackPresentationKey = stackPresentationId + "/" + layoutPartId;
      result = prefStore.getString( stackPresentationKey );
    }
    
    return result;
  }
  
  private ConfigurationAction configAction;
  private ImageDescriptor menuIcon;
  
  
  private Composite parent;

  private ConfigurableStackProxy proxy;
  private IStackPresentationSite site;
    
  
  private String stackPresentationId;

  /**
   * Instantiate an object of this class by calling the super constructor with
   * a dummy <code>{@link IStackPresentationSite}</code>.
   * 
   * @see IStackPresentationSite
   */
  public ConfigurableStack() {
    super( siteDummy );
  }
  
  
  public Control createPartToolBar() {
    Control result = null;
    IToolBarManager manager = getPartToolBarManager();
    ConfigurationAction action = getConfigAction();
    IStackPresentationSite site = getSite();
    int actionCount = 0;
    if( action != null && manager != null ) {
      
      IContributionItem[] items = manager.getItems();
      String paneId = getPaneId( site );
      
      Composite toolBarParent = null;
      IPresentablePart selectedPart = site.getSelectedPart();
      if( selectedPart instanceof PresentablePart ) {
        PresentablePart part = ( PresentablePart ) selectedPart;
        Control nativeToolBar = part.getPane().getToolBar();
        if( nativeToolBar != null ) {
          nativeToolBar.setVisible( false );
          toolBarParent = nativeToolBar.getParent();
        }
        
      }
      if( toolBarParent == null ) {
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow activeWindow = workbench.getActiveWorkbenchWindow();
        IWorkbenchPage activePage = activeWindow.getActivePage();
        if( activePage instanceof WorkbenchPage ) {
          toolBarParent = ( ( WorkbenchPage ) activePage ).getClientComposite();
        }
      }

      for( int i = 0; i < items.length; i++ ) {
        items[ i ].setVisible( true );
      }
      
      if( toolBarParent != null ) {  
        IToolBarManager2 toolBarManager = null;
        if( items.length > 0 && ( manager instanceof IToolBarManager2 ) ) {
          toolBarManager = ( IToolBarManager2 ) manager;                        
          result = toolBarManager.createControl2( toolBarParent );
          toolBarManager.update( false );
        }
        
        for( int i = 0; i < items.length; i++ ) {
          if( items[ i ] instanceof ActionContributionItem ) {
            ActionContributionItem actionItem 
              = ( ActionContributionItem ) items[ i ];
            String actionId = actionItem.getAction().getId();
            boolean isVisible 
              = action.isViewActionVisibile( paneId, actionId );
   
            if( isVisible ) {     
              actionItem.setVisible( true );
              actionCount++;
            } else {              
              actionItem.setVisible( false );
            }
          } 
        }
        
        if( toolBarManager != null && result != null ) {
          toolBarManager.update( false );
        }      
                        
      } 
      if( actionCount <= 0 ) {
        result = null;
      } 
      if( result != null ) {
        result.pack();
        result.setVisible( true );
      } 
    }
    
    return result;
  }
  
  public IPartMenu createViewMenu() {
    
    IPartMenu result = null;
    if( isPartMenuVisisble() ) {    
      result = new IPartMenu() {
        public void showMenu(Point location) {
          IPresentablePart selectedPart = site.getSelectedPart();
          if( selectedPart instanceof PresentablePart ) {
            PresentablePart part = ( PresentablePart ) selectedPart;
            part.getPane().showViewMenu(location);
          }
        }
      };
    }
    return result;
  }
  
  /**
   * Returns an instance of <code>{@link ConfigurationAction}</code> for this
   * <code>ConfigurableStack</code> object, which is declared over the same 
   * extension.
   * 
   * @return the <code>ConfigurationAction</code> or <code>null</code> if no 
   * action is declared for the id holding by this object.
   * 
   * @see ConfigurationAction
   */
  public ConfigurationAction getConfigAction() {
    ConfigurationAction result = null;
    if( configAction == null ) {
      if( stackPresentationId != null && !stackPresentationId.equals( "" ) ) {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        String stackId = STACK_PRESENTATION_EXT_ID;
        IExtensionPoint point = registry.getExtensionPoint( stackId );
        
        if( point != null ) {
          IConfigurationElement[] elements = point.getConfigurationElements();
          String defaultValue = IPreferenceStore.STRING_DEFAULT_DEFAULT;
          String actionClass = defaultValue;
          
          boolean breakValue = true;
          IConfigurationElement element = null;
          ImageDescriptor imageDesc = null;
          for( int i = 0;  breakValue && i < elements.length; i++ ) {
            String id = elements[ i ].getAttribute( "id" );
            
            
            if( id.equals( stackPresentationId ) ) {
              
              actionClass = elements[ i ].getAttribute( CONFIG_ACTION_NAME );
              if( actionClass != null && !actionClass.equals( defaultValue ) ) {
                breakValue = false;
                element = elements[ i ];
                String actionImage = element.getAttribute( "actionIcon" );
                String menuImage = element.getAttribute( "menuIcon" );
                String contributerId = element.getContributor().getName();
                if( actionImage != null ) {
                  imageDesc 
                    = AbstractUIPlugin.imageDescriptorFromPlugin( contributerId, 
                                                                  actionImage );
                }
                if( menuImage != null ) {
                  menuIcon 
                    = AbstractUIPlugin.imageDescriptorFromPlugin( contributerId, 
                                                                  menuImage );
                }
              }
            }
          }
          
          String defaultStore = defaultValue;
          if( actionClass != null && !actionClass.equals( defaultStore ) 
              && element != null ) 
          {
            try {
              Object obj 
                = element.createExecutableExtension( CONFIG_ACTION_NAME );
              if( obj instanceof ConfigurationAction ) {
                configAction = ( ConfigurationAction ) obj;
                configAction.init( getSite(), this );
                if( imageDesc != null ) {
                  configAction.setImageDescriptor( imageDesc );
                }
              }
              
            } catch( CoreException e ) {
              e.printStackTrace();
            }
          }        
        }      
      }      
    }
    result = configAction;
    
    return result;
  }
  
  /**
   * Represents the menuIcon which is declared in the extension for the 
   * extension point org.eclipse.rap.ui.stackPresentations. Return the 
   * ImageDescriptor for this image.
   */
  protected ImageDescriptor getMenuIcon() {
    return menuIcon;
  }
  
  /**
   * This method returns the <code>{@link PartPane}</code> id defined in the 
   * selected <code>{@link PresentablePart}</code> of a 
   * <code>IStackPresentationSite</code> instance.
   * 
   * @param site an instance of <code>IStackPresentationSite</code>
   * 
   * @return the id of the <code>PartPane</code> from the selected
   * <code>PresentablePart</code> or an empty String if no part is selected.
   * 
   * @see PartPane
   * @see PresentablePart
   * @see IStackPresentationSite
   */
  public final String getPaneId( final IStackPresentationSite site ) {
    String result = "";    
    IPresentablePart selectedPart = site.getSelectedPart();
    if( selectedPart instanceof PresentablePart ) {
      PresentablePart part = ( PresentablePart ) selectedPart;
      result = part.getPane().getID();
    }
    return result;
  }
  
  /**
   * Returns the parent composite for this kind of <code>StackPresentation
   * </code>.
   * @return the parent composite
   */
  public Composite getParent() {
    return parent;
  }
  
  /**
   * This method returns the <code>{@link IToolBarManager}</code> for the 
   * selected <code>{@link ViewPart}</code>.
   * 
   * @return the <code>IToolBarManager</code> or <code>null</code> if their is 
   * no <code>ViewPart</code> selected.
   * 
   * @see IToolBarManager
   * @see ViewPart
   */
  public IToolBarManager getPartToolBarManager() {
    IToolBarManager result = null;
    IStackPresentationSite site = getSite();
    String paneId = getPaneId( site );
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
    IWorkbenchPage activePage = window.getActivePage();
    if( activePage != null ) {
      IViewPart viewPart = activePage.findView( paneId );
      
      IActionBars bars = null;
      if( viewPart != null ) {    
        IViewSite viewSite = ( IViewSite ) viewPart.getSite();
        bars = viewSite.getActionBars();
        result = bars.getToolBarManager();
      }
    }
    return result;
  }

  /**
   * If this Stack is from the type standaloneview, than it will have an 
   * attribute called showTitle. This is a parameter of the crate method in the 
   * original <code>AbstractPresentationFactory</code>. This is just a separate 
   * method, because the creation is now automated and using proxy objects. 
   * To match the old behaviour, this method was introduced.
   * @return the showTitle flag for the standalone view. If the view is not 
   * standalone, it will return allways <code>false</code>.
   */
  public boolean getShowTitle() {
    return proxy.getShowTitle();
  }
  

  
  /**
   * Returns the <code>{@link IStackPresentationSite}</code> holding by this 
   * instance.
   * 
   * @return the site used for communication between the presentation and
   * the workbench.
   * 
   * @see IStackPresentationSite
   */
  public IStackPresentationSite getSite() {
    return site;
  }
  
  public String getStackPresentationId() {
    return stackPresentationId;
  }
  
  /**
   * Returns the type of the Stack. 
   * @return the type. See <code>PresentationFactory</code> constants.
   */
  protected String getType() {
    return proxy.getType();
  }


  /**
   * This is called right after all necessary fields are initialized e.g. site,
   * stackPresentationId, parent and proxy. Subclasses can implement any 
   * initializaion behaviour using this mehtod.
   */
  public abstract void init();
  
  /**
   * This method is called right after the constructor is called. It's 
   * necessary for creating a <code>ConfigurableStack</code> object over an 
   * extension because the standard <code>StackPresentation</code> has no 
   * parameterless constructor.
   * <p>
   * This method just set the mandatory fields.
   * </p>
   * 
   * @param site the site used for communication between the presentation and
   * the workbench. 
   * @param stackId the StackPresentation ID, which is declared in the 
   * Extension. 
   * @param parent the parent composite to use for the presentation's controls. 
   * @param proxy the <code>{@link ConfigurableStackProxy}</code> that holds 
   * this instance.
   * 
   * @see ConfigurableStackProxy
   * @see StackPresentation
   */
  public void init( 
    final IStackPresentationSite site, 
    final String stackId, 
    final Composite parent,
    final ConfigurableStackProxy proxy ) 
  {
    this.site = site;
    this.stackPresentationId = stackId;
    this.parent = parent;
    this.proxy = proxy;
    init();
  }
  
  private boolean isPartMenuVisisble() {
    boolean result = false;
    if( configAction != null ) {
      result = configAction.isPartMenuVisible();
    }
    
    return result;
  }
  
  /**
   * Method to change the current StackPresentation of a part. This method just
   * calls the 
   * <code>{@link ConfigurableStackProxy#setCurrentStackPresentation(String)}
   * </code> method.
   * 
   * @param newStackId the id of the stack to change.
   * 
   * @see ConfigurableStackProxy
   */
  public void setCurrentStackPresentation( final String newStackId ) {
    if( proxy != null ) {
      proxy.setCurrentStackPresentation( newStackId );
    }
  }
  
}
