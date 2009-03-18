/******************************************************************************* 
* Copyright (c) 2009 EclipseSource and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   EclipseSource - initial API and implementation
*******************************************************************************/ 
package org.eclipse.rap.ui.interactiondesign.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.rap.ui.interactiondesign.ConfigurableStack;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.branding.BrandingUtil;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.internal.presentations.NativeStackPresentation;
import org.eclipse.ui.internal.presentations.PresentablePart;
import org.eclipse.ui.presentations.IPartMenu;
import org.eclipse.ui.presentations.IPresentablePart;
import org.eclipse.ui.presentations.IPresentationSerializer;
import org.eclipse.ui.presentations.IStackPresentationSite;
import org.eclipse.ui.presentations.StackPresentation;

/**
 * This is the proxy object for <code>ConfigurableStack</code>. The <code>
 * PresentationFactory</code> creates proxies instead of real <code>
 * ConfigurableStack</code>s to be able to switch the presentation on the fly.
 * 
 * @since 1.2
 */
public class ConfigurableStackProxy extends StackPresentation {
  
  public static final String STACK_PRESENTATION_ID = "stackPresentationId"; 
  
  private IConfigurationElement brandingElement;
  
  private String currentId;

  private StackPresentation currentStackPresentation;
  private NativeStackPresentation nativeStackPresentation;
  private Composite parent;
  private boolean showTitle = false;
  private IStackPresentationSite site;

  
  private String type;
  
  public ConfigurableStackProxy( 
    final Composite parent, 
    final IStackPresentationSite stackSite, 
    final String type ) 
  {
    super( stackSite );
    this.parent = parent;
    this.site = stackSite;
    this.type = type;
    currentId = "";
    nativeStackPresentation = new NativeStackPresentation( parent, stackSite );
    
    currentStackPresentation 
      = loadStackPresentations( this.type, this.site, this.parent );
    if( currentStackPresentation == null ) {
      currentStackPresentation = nativeStackPresentation;
    }
  }

  public void addPart( final IPresentablePart newPart, final Object cookie ) {
    StackPresentation delegate = getDelegate();
    if( delegate instanceof ConfigurableStack ) {
      IAdaptable adaptable = getConfigAdaptable();
      if( newPart instanceof PresentablePart ) {
        PresentablePart part = ( PresentablePart ) newPart;
        part.setConfigurationAdaptable( adaptable );
      }
    }
    delegate.addPart( newPart, cookie );
  }
  
  private boolean brandingPresentationFactoryExists() {
    boolean result = false;
    String id = getBrandingPresentationFactoryId();
    if( id != null ) {
      result = true;
    }
    return result;
  }

  /**
   * @see StackPresentation
   */
  public Point computeMinimumSize() {
    return getDelegate().computeMinimumSize();
  }
  
  public int computePreferredSize( boolean width,
                                   int availableParallel,
                                   int availablePerpendicular,
                                   int preferredResult )
  {
    return getDelegate().computePreferredSize( width, 
                                               availableParallel, 
                                               availablePerpendicular, 
                                               preferredResult );
  }
  
  private ConfigurableStack createStackById( final String stackId ) {
    ConfigurableStack result = null;
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    String stackPresentationExtId = ConfigurableStack.STACK_PRESENTATION_EXT_ID;
    IExtensionPoint point = registry.getExtensionPoint( stackPresentationExtId );
    if( point != null ) {
      IConfigurationElement[] elements = point.getConfigurationElements();
      boolean found = false;
      for( int i = 0; i < elements.length && !found; i++ ) {
        String id = elements[ i ].getAttribute( "id" );
        //String presentationType = elements[ i ].getAttribute( "type" );
        if( id.equals( stackId ) ) {
          found = true;
          try {
          Object obj = elements[ i ].createExecutableExtension( "class" );
          result = ( ConfigurableStack ) obj; 
          result.init( site, id, parent, this );
          } catch( CoreException e ) {
            // nothing todo
          }
        }
      }
    }
    return result;
  }
  
  /**
   * @see StackPresentation
   */
  public void dispose() {
    getDelegate().dispose();
  }
  
  private IConfigurationElement getBrandingElement() {
    if( brandingElement == null ) {
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
              brandingElement = elements[ i ];
            }
          }            
        }
      }
    }
    return brandingElement;
  }

  // Delegate methods

  private String getBrandingPresentationFactoryId() {
    return loadBrandingPresentationFactoryId( getBrandingElement() );
  }

  private IAdaptable getConfigAdaptable() {
    IAdaptable adaptable = new IAdaptable() {

      public Object getAdapter( Class adapter ) {
        Object result = null;
        if( currentStackPresentation instanceof ConfigurableStack ) { 
          ConfigurableStack stackPresentation  
            = ( ConfigurableStack ) currentStackPresentation;
          if( adapter == ToolBar.class ) {
            result = stackPresentation.createPartToolBar( );
          } else if( adapter == IPartMenu.class ) {
            result = stackPresentation.createViewMenu();
          }
        }
        return result;
      }
      
    };
    return adaptable;
  }

  /**
   * @see StackPresentation
   */
  public Control getControl() {
    return getDelegate().getControl();
  }

  public ConfigurableStack getCurrentStackPresentation() {
    ConfigurableStack result = null;
    if( currentStackPresentation instanceof ConfigurableStack ) {
      result = ( ConfigurableStack ) currentStackPresentation;
    }
    return result;
  }

  private  StackPresentation getDelegate() {
    StackPresentation result;
    if( currentStackPresentation != null ) {
      result = currentStackPresentation;
    } else {
      result = nativeStackPresentation;
    }
    return result;
  }

  /**
   * @see StackPresentation
   */
  public boolean getShowTitle() {
    return showTitle;
  }

  /**
   * @see StackPresentation
   */
  public int getSizeFlags( final boolean width ) {
    return getDelegate().getSizeFlags( width );
  }
  
  /**
   * @see StackPresentation
   */
  public Control[] getTabList( final IPresentablePart part ) {
    return getDelegate().getTabList( part );
  }

  /**
   * Returns the type of the Stack. 
   * @return the type. See <code>PresentationFactory</code> constants.
   */
  public String getType() {
    return type;
  }

  private String loadBrandingPresentationFactoryId( 
    final IConfigurationElement element )
  {
    String result = null;
    if( element != null ) {
      IConfigurationElement[] factory 
        = element.getChildren( "presentationFactory" );
      if( factory.length > 0 ) {
        result = factory[ 0 ].getAttribute( "id" );
      }
    }
    return result;
  }

  private ConfigurableStack loadDefaultPartStack() {
    ConfigurableStack result = null;
    IConfigurationElement element = getBrandingElement();
    IConfigurationElement[] factory 
      = element.getChildren( "presentationFactory" );
    if( factory.length > 0 ) {
      IConfigurationElement[] stacks 
        = factory[ 0 ].getChildren( "defaultStackPresentation" );
      if( stacks.length > 0 ) {
        String id = stacks[ 0 ].getAttribute( "id" );
        result = createStackById( id );
      }
    }
    return result;
  }

  private ConfigurableStack loadLayoutPartStack() {
    ConfigurableStack result = null;
    IConfigurationElement element = getBrandingElement();
    IConfigurationElement[] factory 
      = element.getChildren( "presentationFactory" );
    if( factory.length > 0 ) {
      IConfigurationElement[] stacks 
        = factory[ 0 ].getChildren( "stackPresentation" );
      if( stacks.length > 0 ) {
        String layoutPartId = ConfigurableStack.getLayoutPartId( site );
        boolean found = false;
        for( int i = 0; i < stacks.length && !found; i++ ) {
          String partId = stacks[ i ].getAttribute( "partId" );
          if( partId.equals( layoutPartId ) ) {
            String stackId = stacks[ i ].getAttribute( "id" );
            result = createStackById( stackId );
            found = true;
          }
        }
      }
    }
    return result;
  }

  private ConfigurableStack loadStackFromBranding() {
    ConfigurableStack result = loadLayoutPartStack();
    if( result == null ) {
      result = loadDefaultPartStack();
    } 
    return result;
  }
  
  /*
   * load the saved or default StackPresentation from extension registry
   */
  private ConfigurableStack loadStackPresentations( 
    final String type, 
    final IStackPresentationSite site,
    final Composite parent ) 
  {
    this.parent = parent;
    ConfigurableStack result = null;
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    String stackPresentationExtId = ConfigurableStack.STACK_PRESENTATION_EXT_ID;
    IExtensionPoint point = registry.getExtensionPoint( stackPresentationExtId );
    if( point != null ) {
      IConfigurationElement[] elements = point.getConfigurationElements();
      
      String savedStackId  = ConfigurableStack.getSavedStackId( site );
      
      boolean error = false;
      boolean found = false;
      for( int i = 0; !error && !found && i < elements.length; i++ ) {
        
        String id = elements[ i ].getAttribute( "id" );
        String presentationType = elements[ i ].getAttribute( "type" );

        if( savedStackId.equals( IPreferenceStore.STRING_DEFAULT_DEFAULT ) ) {
          // No id is saved, check branding Presentationfactory
          if( brandingPresentationFactoryExists() ) {
            ConfigurableStack tempStack = loadStackFromBranding();
            if( tempStack != null ) {
              result = tempStack;
              currentId = id;
            } else {
              result = null;
            }
            found = true;
          } else {
            result = null;
            found = true;
          }
          
        } else {
          // their is a saved id, loading the stackPresentation if the id 
          // matches the saved id
          if( id.equals( savedStackId ) && presentationType.equals( type ) ) {
            result = createStackById( id );
            currentId = id;
            found = true;
          }
        }

      }
    }
    return result;
  }

  /**
   * @see StackPresentation
   */
  public void movePart( final IPresentablePart toMove, final Object cookie ) {
    getDelegate().movePart( toMove, cookie );
  }

  /**
   * @see StackPresentation
   */
  public void refreshStack() {
    if( !currentId.equals( "" ) ) {
      setCurrentStackPresentation( currentId );
    }
    
  }

  /**
   * @see StackPresentation
   */
  public void removePart( final IPresentablePart oldPart ) {
    getDelegate().removePart( oldPart );
  }

  /**
   * @see StackPresentation
   */
  public void restoreState( 
    final IPresentationSerializer context, 
    final IMemento memento )
  {
    getDelegate().restoreState( context, memento );
  }

  /**
   * @see StackPresentation
   */
  public void saveState( 
    final IPresentationSerializer context, 
    final IMemento memento ) 
  {
    getDelegate().saveState( context, memento );
  }

  /**
   * @see StackPresentation
   */
  public void selectPart( final IPresentablePart toSelect ) {
    getDelegate().selectPart( toSelect );
  }

  /**
   * @see StackPresentation
   */
  public void setActive( final int newState ) {
    getDelegate().setActive( newState );
  }
  
  /**
   * @see StackPresentation
   */
  public void setBounds( final Rectangle bounds ) {
    getDelegate().setBounds( bounds );
  }

  public void setCurrentStackPresentation( final String id ) {
    
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    String stackPresentationExtId = ConfigurableStack.STACK_PRESENTATION_EXT_ID;
    IExtensionPoint point 
      = registry.getExtensionPoint( stackPresentationExtId );
    
    IConfigurationElement elements[] = point.getConfigurationElements();
    
    Object stackObj = null;
    for( int i = 0; stackObj == null && i < elements.length; i++ ) {
      String attributeId = elements[ i ].getAttribute( "id" );
      if( attributeId.equals( id ) ) {
        currentId = id;
        try {
          Rectangle bounds = currentStackPresentation.getControl().getBounds();
          
          stackObj = elements[ i ].createExecutableExtension( "class" );
          if( stackObj instanceof ConfigurableStack ) {
            
            ConfigurableStack newStackPresentation 
              = ( ConfigurableStack ) stackObj;
            newStackPresentation.init( site, id, parent, this );
            
            
            Control control = currentStackPresentation.getControl();
            control.dispose();
            
            IPresentablePart[] parts = site.getPartList();
            for( int j = 0; j < parts.length; j++ ) {
              newStackPresentation.addPart( parts[ j ], null );
              currentStackPresentation.removePart( parts[ j ] );
            }  
            newStackPresentation.selectPart( site.getSelectedPart() );
            
            currentStackPresentation.getControl().dispose();
            currentStackPresentation.dispose();
            currentStackPresentation = newStackPresentation;
            currentStackPresentation.setBounds( bounds );
            
          }
        } catch( CoreException e ) {
          e.printStackTrace();
        }
        
      }
    }   
    
  }

  /**
   * @see StackPresentation
   */
  public void setShowTitle( boolean showTitle ) {
    this.showTitle = showTitle;
  }

  /**
   * @see StackPresentation
   */
  public void setState( final int state ) {
    getDelegate().setState( state );
  }

  /**
   * @see StackPresentation
   */
  public void setVisible( final boolean isVisible ) {
    getDelegate().setVisible( isVisible );
  }

  /**
   * @see StackPresentation
   */
  public void showPaneMenu() {
    getDelegate().showPaneMenu();
  }

  /**
   * @see StackPresentation
   */
  public void showPartList() {
    getDelegate().showPartList();
  }

  /**
   * @see StackPresentation
   */
  public void showSystemMenu() {
    getDelegate().showSystemMenu();
  }
    
}
