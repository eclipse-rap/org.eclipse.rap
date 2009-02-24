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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.internal.provisional.action.ICoolBarManager2;
import org.eclipse.jface.internal.provisional.action.IToolBarContributionItem;
import org.eclipse.jface.internal.provisional.action.IToolBarManager2;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.rap.ui.interactiondesign.internal.ConfigurableStackProxy;
import org.eclipse.rap.ui.interactiondesign.layout.LayoutRegistry;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.branding.BrandingUtil;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.internal.provisional.presentations.IActionBarPresentationFactory;
import org.eclipse.ui.internal.util.PrefUtil;
import org.eclipse.ui.presentations.AbstractPresentationFactory;
import org.eclipse.ui.presentations.IStackPresentationSite;
import org.eclipse.ui.presentations.StackPresentation;

/**
 * <p>
 * The <code>PresentationFactory</code> is an enhancement of the original 
 * <code>{@link AbstractPresentationFactory}</code>. This factory provides
 * many additional possibilities, which the original one doesn't provide.
 * </p>
 * <p>
 * <code>{@link StackPresentation}</code> Objects shall not be created directly
 * by this factory. Instead their is a new Extension Point for this called 
 * <code>org.eclipse.rap.presentation.stackPresentations</code>. You can 
 * contribute <code>ConfigurableStack</code>s to this now. 
 * </p>
 * <p>
 * This Factory controls the appereance of:
 * <ul>
 *  <li>editors</li> 
 *  <li>views</li> 
 *  <li>standalone views</li>
 *  <li>view's toolbar</li>
 *  <li>coolbar</li>
 *  <li>menubar</li>
 *  <li>view's toolbar menu</li>
 *  <li>toolbar contribution items</li>
 * </ul> 
 * It represents one central point for customizing the workbench's window.
 * </p>
 * @since 1.2
 * 
 * @see ConfigurableStack
 * @see ConfigurationAction
 * @see IWindowComposer
 * @see AbstractPresentationFactory
 */
public abstract class PresentationFactory extends AbstractPresentationFactory 
  implements IActionBarPresentationFactory, IAdaptable
{
  
  private final class windowListener implements IWindowListener {

    public void windowActivated( IWorkbenchWindow window ) {
      
    }

    public void windowClosed( IWorkbenchWindow window ) {
      
    }

    public void windowDeactivated( IWorkbenchWindow window ) {
      
    }

    public void windowOpened( final IWorkbenchWindow window ) {
      IPreferenceStore preferenceStore 
        = PrefUtil.getAPIPreferenceStore();
      preferenceStore.addPropertyChangeListener( new IPropertyChangeListener() {
  
        public void propertyChange( PropertyChangeEvent event ) {
          String name = event.getProperty();
          String layoutKey = LayoutRegistry.SAVED_LAYOUT_KEY;
          if( name.equals( layoutKey ) ) {
            workbenchWindowLayoutChanged( window );
          }
          
        }

      });
      
    }
  }
  
  /**
   * The key for an editor <code>ConfigurableStack</code>.
   */
  public static final String KEY_EDITOR = "editor";
  
  /**
   * The key for a view <code>ConfigurableStack</code>.
   */
  public static final String KEY_VIEW = "view";
  
  /**
   * The key for a standalone view <code>ConfigurableStack</code>.
   */
  public static final String KEY_STANDALONE_VIEW = "standaloneview";
  
  private List proxyList = new ArrayList();
  
  /**
   * Constructs a new instance of this class and adds a listener to the 
   * <code>{@link WorkbenchWindow}</code> for changing the window design on the 
   * fly.
   */
  public PresentationFactory() {
    IWorkbench workbench = PlatformUI.getWorkbench();
    workbench.addWindowListener( new windowListener() ); 

    // load Layout from branding because its too early in the LayoutRegistry
    String layoutId = loadBrandingLayoutId();
    if( layoutId != null ) {
      LayoutRegistry.getInstance().setActiveLayout( layoutId, false );
    }
  }
  
  public abstract ICoolBarManager2 createCoolBarManager();
  
  /**
   * Creates an editor presentation proxy for presenting editors.
   * <p>
   * The presentation creates its controls on the given parent composite.
   * </p>
   * <p> 
   * The presentation itself can be contributed to the 
   * extension point 
   * <code>org.eclipse.ui.presentations.StackPresentations</code>.
   * </p>
   * 
   * @param parent
   *            the parent composite to use for the presentation's controls.
   * @param site
   *            the site used for communication between the presentation and
   *            the workbench.
   * @return a newly created part presentation proxy.
   */
  public final StackPresentation createEditorPresentation( 
    final Composite parent,
    final IStackPresentationSite site )
  {
    ConfigurableStackProxy result 
      = new ConfigurableStackProxy( parent, site, KEY_EDITOR );
    proxyList.add( result );
    return result;   
  }
  
  /**
   * Instantiate the <code>{@link MenuManager}</code> object for the window's
   * menubar.
   * 
   * @return the menubar manager
   * 
   * @see MenuManager
   */
  public abstract MenuManager createMenuBarManager();   
  
  /**
   * Instantiate the <code>{@link MenuManager}</code> object for the part's
   * menu.
   * 
   * @return the partmenu manager
   */
  public abstract MenuManager createPartMenuManager();
  
  /**
   * Creates a stack presentation proxy for presenting regular docked views.
   * <p>
   * The presentation creates its controls on the given parent composite.
   * </p>
   * <p> 
   * The presentation itself can be contributed to the 
   * extension point 
   * <code>org.eclipse.ui.presentations.StackPresentations</code>.
   * </p>
   * 
   * @param parent
   *            the parent composite to use for the presentation's controls.
   * @param site
   *            the site used for communication between the presentation and
   *            the workbench.
   * @param showTitle
   *            <code>true</code> to show the title for the view,
   *            <code>false</code> to hide it.
   * @return a newly created part presentation proxy
   */
  public final StackPresentation createStandaloneViewPresentation( 
    final Composite parent,
    final IStackPresentationSite site,
    final boolean showTitle )
  {
    ConfigurableStackProxy result 
      = new ConfigurableStackProxy( parent, site, KEY_STANDALONE_VIEW );
    if( showTitle ) {
      result.setShowTitle( showTitle );
    }
    proxyList.add( result );
    return result;     
  }

  public abstract IToolBarContributionItem createToolBarContributionItem( 
    final IToolBarManager toolBarManager, 
    final String id );

  public abstract IToolBarManager2 createToolBarManager(); 
  
  /**
   * Creates a standalone stack presentation proxy for presenting a standalone 
   * view. A standalone view cannot be docked together with other views. 
   * The title of a standalone view may be hidden.
   * <p>
   * The presentation creates its controls on the given parent composite.
   * </p>
   * <p> 
   * The presentation itself can be contributed to the 
   * extension point 
   * <code>org.eclipse.ui.presentations.StackPresentations</code>.
   * </p>
   * 
   * @param parent
   *            the parent composite to use for the presentation's controls.
   * @param site
   *            the site used for communication between the presentation and
   *            the workbench.
   * @return a newly created part presentation proxy.
   */
  public final StackPresentation createViewPresentation( 
    final Composite parent, final IStackPresentationSite site )
  {
    ConfigurableStackProxy result 
      = new ConfigurableStackProxy( parent, site, KEY_VIEW );
    proxyList.add( result );
    return result;    
  }

  
  public abstract IToolBarManager2 createViewToolBarManager();

  /**
   * This method is called within the <code>{@link WorkbenchWindow}</code> to
   * couple the presentation factory and the design of the 
   * <code>WorkbenchWindow</code>.
   * 
   * @return a newly created <code>{@link IWindowComposer}</code> object, which 
   * defines the design of a <code>WorkbenchWindow</code>.
   * 
   * @see IWindowComposer
   */
  public abstract IWindowComposer createWindowComposer( );

  public Object getAdapter( Class adapter ) {
    Object result = null;
    if( adapter.equals( StackPresentation.class ) ) {
      result = proxyList;
    }    
    return result;
  }

  private String loadBrandingDefaultLayoutId( 
    final IConfigurationElement element )
  {
    String result = null;
    IConfigurationElement[] factory 
      = element.getChildren( "presentationFactory" );
    if( factory.length > 0 ) {
      result = factory[ 0 ].getAttribute( "defaultLayoutId" );
    }
    return result;
  }
  
  private String loadBrandingLayoutId() {
    String result = null;
    AbstractBranding branding = BrandingUtil.findBranding();
    if( branding != null ) {
      String brandingId = branding.getId();
      IExtensionRegistry registry = Platform.getExtensionRegistry();
      String id = "org.eclipse.rap.ui.branding";
      IExtensionPoint brandingPoint = registry.getExtensionPoint( id );
      if( brandingPoint != null ) {
        IConfigurationElement[] elements 
          = brandingPoint.getConfigurationElements();
        boolean found = false;
        for( int i = 0; i < elements.length && !found; i++ ) {
          String tempId = elements[ i ].getAttribute( "id" );
          if( tempId.equals( brandingId ) ) {
            found = true;
            result = loadBrandingDefaultLayoutId( elements[ i ] );
          }
        }
      }
    }
    return result;
    
  }
  
  private void refreshStackPresentations( ) {
    for( int i = 0; i < proxyList.size(); i++ ) {
      ConfigurableStackProxy proxy 
        = ( ConfigurableStackProxy ) proxyList.get( i );
      proxy.refreshStack();
    }
  }
  
  private void workbenchWindowLayoutChanged( final IWorkbenchWindow window ) {

    Composite newStackComposite = null;
    if( window instanceof WorkbenchWindow ) {
      WorkbenchWindow wbWindow = ( WorkbenchWindow ) window;
      LayoutRegistry registry = LayoutRegistry.getInstance();
      registry.disposeBuilders();
      newStackComposite = wbWindow.changeWindowLayoutSet();
      
    }
    
    refreshStackPresentations();
    
    IWorkbenchPage activePage = window.getActivePage();
    if( activePage instanceof WorkbenchPage ) {
      WorkbenchPage page = ( WorkbenchPage ) activePage;
      Composite parent = page.getClientComposite().getParent();
      if( parent != null ) {
        Composite stackComposite = parent.getParent();
        if( stackComposite != null && newStackComposite != null ) {          
          stackComposite.setLayoutData( newStackComposite.getLayoutData() );
          newStackComposite.getParent().layout( true, true );
          newStackComposite.dispose();          
        }        
      }
    }
  }
  
}
