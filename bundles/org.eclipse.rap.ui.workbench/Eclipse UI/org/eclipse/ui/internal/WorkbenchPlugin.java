/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.http.registry.HttpContextExtensionService;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.window.Window;
import org.eclipse.rap.ui.internal.branding.BrandingExtension;
import org.eclipse.rap.ui.internal.servlet.EntryPointExtension;
import org.eclipse.rap.ui.internal.servlet.HttpServiceTracker;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.SessionSingletonBase;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.StartupThreading.StartupRunnable;
import org.eclipse.ui.internal.decorators.DecoratorManager;
import org.eclipse.ui.internal.dialogs.WorkbenchPreferenceManager;
import org.eclipse.ui.internal.intro.IIntroRegistry;
import org.eclipse.ui.internal.intro.IntroRegistry;
import org.eclipse.ui.internal.misc.StatusUtil;
import org.eclipse.ui.internal.operations.WorkbenchOperationSupport;
import org.eclipse.ui.internal.progress.JobManagerAdapter;
import org.eclipse.ui.internal.progress.ProgressManager;
import org.eclipse.ui.internal.registry.ActionSetRegistry;
import org.eclipse.ui.internal.registry.EditorRegistry;
import org.eclipse.ui.internal.registry.IWorkbenchRegistryConstants;
import org.eclipse.ui.internal.registry.PerspectiveRegistry;
import org.eclipse.ui.internal.registry.PreferencePageRegistryReader;
import org.eclipse.ui.internal.registry.ViewRegistry;
import org.eclipse.ui.internal.registry.WorkingSetRegistry;
import org.eclipse.ui.internal.themes.IThemeRegistry;
import org.eclipse.ui.internal.themes.ThemeRegistry;
import org.eclipse.ui.internal.themes.ThemeRegistryReader;
import org.eclipse.ui.internal.util.BundleUtility;
import org.eclipse.ui.internal.util.SWTResourceUtil;
import org.eclipse.ui.internal.wizards.ExportWizardRegistry;
import org.eclipse.ui.internal.wizards.ImportWizardRegistry;
import org.eclipse.ui.internal.wizards.NewWizardRegistry;
import org.eclipse.ui.operations.IWorkbenchOperationSupport;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.presentations.AbstractPresentationFactory;
import org.eclipse.ui.views.IViewRegistry;
import org.eclipse.ui.wizards.IWizardRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.util.tracker.ServiceTracker;

import com.ibm.icu.text.MessageFormat;

/**
 * This class represents the TOP of the workbench UI world
 * A plugin class is effectively an application wrapper
 * for a plugin & its classes. This class should be thought
 * of as the workbench UI's application class.
 *
 * This class is responsible for tracking various registries
 * font, preference, graphics, dialog store.
 *
 * This class is explicitly referenced by the 
 * workbench plugin's  "plugin.xml" and places it
 * into the UI start extension point of the main
 * overall application harness
 *
 * When is this class started?
 *      When the Application
 *      calls createExecutableExtension to create an executable
 *      instance of our workbench class.
 *      
 */
public class WorkbenchPlugin extends AbstractUIPlugin {

// RAP [rh] SessionStore key to initicate whether the session-scoped 
//     PerspectiveRegistry is initialized   
  private static final String PERSP_REGISTRY_INITIALIZED
    = PerspectiveRegistry.class + "#initialized";

  // TODO [bm]: turn into real session scoped manager
	// RAP [rs]:
	private final static class DecoratorManagerStore extends
			SessionSingletonBase
	{
		private final DecoratorManager decoratorManager;

		private DecoratorManagerStore() {
			decoratorManager = new DecoratorManager();
		}

		public static DecoratorManagerStore getInstance() {
			Class clazz = DecoratorManagerStore.class;
			return (DecoratorManagerStore) getInstance(clazz);
		}

		public DecoratorManager getDecoratorManager() {
			return decoratorManager;
		}
	}
	// RAPEND]
	
// RAP [rh] session-singleton-wrapper for getThemeRegistry
	private final static class ThemeRegistryStore extends SessionSingletonBase {
	  private final ThemeRegistry themeRegistry;
	  
	  static ThemeRegistryStore getInstance() {
      Class clazz = ThemeRegistryStore.class;
      return ( ThemeRegistryStore )getInstance( clazz );
	  }
	  
	  public ThemeRegistryStore() {
	    // RAP [rh] ThemeRegistry initialization code, copied from getThemeRegistry()
      themeRegistry = new ThemeRegistry();
      ThemeRegistryReader reader = new ThemeRegistryReader();
      reader.readThemes( Platform.getExtensionRegistry(), themeRegistry );
	  }
	  
	  public IThemeRegistry getThemeRegistry() {
	    return themeRegistry;
	  }
	}

// RAP [rh] session-singleton-wrapper for getWorkingSetManager()	
  private final static class WorkingSetManagerStore extends SessionSingletonBase {
    private WorkingSetManager workingSetManager;
    
    static WorkingSetManagerStore getInstance() {
      Class clazz = WorkingSetManagerStore.class;
      return ( WorkingSetManagerStore )getInstance( clazz );
    }
    
    public IWorkingSetManager getWorkingSetManager( BundleContext context ) {
// RAP [rh] WorkingSetManager initialization code, copied from getWorkingSetManager()
      if( workingSetManager == null ) {
        workingSetManager = new WorkingSetManager( context );
        workingSetManager.restoreState();
      }
      return workingSetManager;
    }
  }
	
//RAP [rh] session-singleton-wrapper for getWorkingSetRegistry()  
  private final static class WorkingSetRegistryStore extends SessionSingletonBase {
    private WorkingSetRegistry workingSetRegistry;
    
    static WorkingSetRegistryStore getInstance() {
      Class clazz = WorkingSetRegistryStore.class;
      return ( WorkingSetRegistryStore )getInstance( clazz );
    }
    
    public WorkingSetRegistryStore() {
// RAP [rh] WorkingSetRegistry initialization code, copied from getWorkingSetRegistry()
      workingSetRegistry = new WorkingSetRegistry();
      workingSetRegistry.load();
    }
    
    public WorkingSetRegistry getWorkingSetRegistry() {
      return workingSetRegistry;
    }
  }
  
	// RAP [bm]:
// /**
// * Splash shell constant.
// */
//	private static final String DATA_SPLASH_SHELL = "org.eclipse.ui.workbench.splashShell"; //$NON-NLS-1$

	// RAP [bm]: 
//	/**
//	 * The OSGi splash property.
//	 * 
//	 * @sicne 3.4
//	 */
//	private static final String PROP_SPLASH_HANDLE = "org.eclipse.equinox.launcher.splash.handle"; //$NON-NLS-1$
	
	// RAP [bm]: 
//	private static final String LEFT_TO_RIGHT = "ltr"; //$NON-NLS-1$
//	private static final String RIGHT_TO_LEFT = "rtl";//$NON-NLS-1$
//	private static final String ORIENTATION_COMMAND_LINE = "-dir";//$NON-NLS-1$
//	private static final String ORIENTATION_PROPERTY = "eclipse.orientation";//$NON-NLS-1$
//	private static final String NL_USER_PROPERTY = "osgi.nl.user"; //$NON-NLS-1$
	// RAPEND: [bm] 
   
    // Default instance of the receiver
    private static WorkbenchPlugin inst;

    // Manager that maps resources to descriptors of editors to use
    
    // RAP [bm]: replaced with session scoped one
//    private EditorRegistry editorRegistry;
    // RAPEND: [bm] 

    // Manager for the DecoratorManager
    private DecoratorManager decoratorManager;

// RAP [rh] themeRegistry field unneeded, replaced by session-singleton    
    // Theme registry
//    private ThemeRegistry themeRegistry;

    // Manager for working sets (IWorkingSet)
    private WorkingSetManager workingSetManager;

// RAP [rh] workingSetRegistry field unneeded, replaced by session-singleton    
    // Working set registry, stores working set dialogs
//    private WorkingSetRegistry workingSetRegistry;

    // The context within which this plugin was started.
    private BundleContext bundleContext;

    // The set of currently starting bundles
    private Collection startingBundles = new HashSet();

    /**
     * Global workbench ui plugin flag. Only workbench implementation is allowed to use this flag
     * All other plugins, examples, or test cases must *not* use this flag.
     */
    public static boolean DEBUG = false;

    /**
     * The workbench plugin ID.
     * 
     * @issue we should just drop this constant and use PlatformUI.PLUGIN_ID instead
     */
    public static String PI_WORKBENCH = PlatformUI.PLUGIN_ID;

    /**
     * The character used to separate preference page category ids
     */
    public static char PREFERENCE_PAGE_CATEGORY_SEPARATOR = '/';

    // Other data.
    private WorkbenchPreferenceManager preferenceManager;

    // RAP [bm]: replaced with session scoped one
//    private ViewRegistry viewRegistry;
    // RAPEND: [bm] 

// RAP [rh] PerspectiveRegistry has session scope    
//    private PerspectiveRegistry perspRegistry;

    private ActionSetRegistry actionSetRegistry;

    private SharedImages sharedImages;

    /**
     * Information describing the product (formerly called "primary plugin"); lazily
     * initialized.
     */
    private ProductInfo productInfo = null;

    // RAP [bm]: not needed
//    private IntroRegistry introRegistry;
    
    private WorkbenchOperationSupport operationSupport;
	private BundleListener bundleListener;
	
	// RAP [bm]: 
	private HttpServiceTracker httpServiceTracker;
	// RAPEND: [bm] 
    
    /**
     * Create an instance of the WorkbenchPlugin. The workbench plugin is
     * effectively the "application" for the workbench UI. The entire UI
     * operates as a good plugin citizen.
     */
    public WorkbenchPlugin() {
        super();
        inst = this;
    }

    /**
     * Unload all members.  This can be used to run a second instance of a workbench.
     */
    void reset() {
    	// RAP [bm]: 
//        editorRegistry = null;
        // RAPEND: [bm] 

        if (decoratorManager != null) {
            decoratorManager.dispose();
            decoratorManager = null;
        }

        ProgressManager.shutdownProgressManager();

// RAP [rh] themeRegistry field unneeded, replaced by session-singleton    
//        themeRegistry = null;
        if (workingSetManager != null) {
        	workingSetManager.dispose();
        	workingSetManager = null;
        }
// RAP [rh] workingSetRegistry field unneeded, replaced by session-singleton    
//        workingSetRegistry = null;

        preferenceManager = null;

        // RAP [bm]: 
//        if (viewRegistry != null) {
//            viewRegistry.dispose();
//            viewRegistry = null;
//        }
        // RAPEND: [bm] 

// RAP [rh] perspRegistry field is unused, PerspectiveRegistry has session scope    
//        if (perspRegistry != null) {
//            perspRegistry.dispose();
//            perspRegistry = null;
//        }
        
        actionSetRegistry = null;
        sharedImages = null;

        productInfo = null;
        // RAP [bm]: not needed
//        introRegistry = null;
        
        if (operationSupport != null) {
        	operationSupport.dispose();
        	operationSupport = null;
        }

        DEBUG = false;
         
    }

    /**
     * Creates an extension.  If the extension plugin has not
     * been loaded a busy cursor will be activated during the duration of
     * the load.
     *
     * @param element the config element defining the extension
     * @param classAttribute the name of the attribute carrying the class
     * @return the extension object
     * @throws CoreException if the extension cannot be created
     */
    public static Object createExtension(final IConfigurationElement element,
            final String classAttribute) throws CoreException {
        try {
            // If plugin has been loaded create extension.
            // Otherwise, show busy cursor then create extension.
            if (BundleUtility.isActivated(element.getDeclaringExtension()
                    .getNamespace())) {
                return element.createExecutableExtension(classAttribute);
            }
            final Object[] ret = new Object[1];
            final CoreException[] exc = new CoreException[1];
            BusyIndicator.showWhile(null, new Runnable() {
                public void run() {
                    try {
                        ret[0] = element
                                .createExecutableExtension(classAttribute);
                    } catch (CoreException e) {
                        exc[0] = e;
                    }
                }
            });
            if (exc[0] != null) {
				throw exc[0];
			}
            return ret[0];

        } catch (CoreException core) {
            throw core;
        } catch (Exception e) {
            throw new CoreException(new Status(IStatus.ERROR, PI_WORKBENCH,
                    IStatus.ERROR, WorkbenchMessages.get().WorkbenchPlugin_extension,e));
        }
    }
    
    /**
	 * Answers whether the provided element either has an attribute with the
	 * given name or a child element with the given name with an attribute
	 * called class.
	 * 
	 * @param element
	 *            the element to test
	 * @param extensionName
	 *            the name of the extension to test for
	 * @return whether or not the extension is declared
	 */
	public static boolean hasExecutableExtension(IConfigurationElement element,
			String extensionName) {

		if (element.getAttribute(extensionName) != null)
			return true;
		String elementText = element.getValue();
		if (elementText != null && !elementText.equals("")) //$NON-NLS-1$
			return true;
		IConfigurationElement [] children = element.getChildren(extensionName);
		if (children.length == 1) {
			if (children[0].getAttribute(IWorkbenchRegistryConstants.ATT_CLASS) != null)
				return true;
		}
		return false;
	}
	
	/**
	 * Checks to see if the provided element has the syntax for an executable
	 * extension with a given name that resides in a bundle that is already
	 * active. Determining the bundle happens in one of two ways:<br/>
	 * <ul>
	 * <li>The element has an attribute with the specified name or element text
	 * in the form <code>bundle.id/class.name[:optional attributes]</code></li>
	 * <li>The element has a child element with the specified name that has a
	 * <code>plugin</code> attribute</li>
	 * </ul>
	 * 
	 * @param element
	 *            the element to test
	 * @param extensionName
	 *            the name of the extension to test for
	 * @return whether or not the bundle expressed by the above criteria is
	 *         active. If the bundle cannot be determined then the state of the
	 *         bundle that declared the element is returned.
	 */
	public static boolean isBundleLoadedForExecutableExtension(
			IConfigurationElement element, String extensionName) {
		Bundle bundle = getBundleForExecutableExtension(element, extensionName);

		if (bundle == null)
			return true;
		return bundle.getState() == Bundle.ACTIVE;
	}
	
	/**
	 * Returns the bundle that contains the class referenced by an executable
	 * extension. Determining the bundle happens in one of two ways:<br/>
	 * <ul>
	 * <li>The element has an attribute with the specified name or element text
	 * in the form <code>bundle.id/class.name[:optional attributes]</code></li>
	 * <li>The element has a child element with the specified name that has a
	 * <code>plugin</code> attribute</li>
	 * </ul>
	 * 
	 * @param element
	 *            the element to test
	 * @param extensionName
	 *            the name of the extension to test for
	 * @return the bundle referenced by the extension. If that bundle cannot be
	 *         determined the bundle that declared the element is returned. Note
	 *         that this may be <code>null</code>.
	 */
	public static Bundle getBundleForExecutableExtension(IConfigurationElement element, String extensionName) {
		// this code is derived heavily from
		// ConfigurationElement.createExecutableExtension.  
		String prop = null;
		String executable;
		String contributorName = null;
		int i;

		if (extensionName != null)
			prop = element.getAttribute(extensionName);
		else {
			// property not specified, try as element value
			prop = element.getValue();
			if (prop != null) {
				prop = prop.trim();
				if (prop.equals("")) //$NON-NLS-1$
					prop = null;
			}
		}

		if (prop == null) {
			// property not defined, try as a child element
			IConfigurationElement[] exec = element.getChildren(extensionName);
			if (exec.length != 0) 
				contributorName = exec[0].getAttribute("plugin"); //$NON-NLS-1$
		} else {
			// simple property or element value, parse it into its components
			i = prop.indexOf(':');
			if (i != -1) 
				executable = prop.substring(0, i).trim();
			else
				executable = prop;

			i = executable.indexOf('/');
			if (i != -1)
				contributorName = executable.substring(0, i).trim();
				
		}
		
		if (contributorName == null)
			contributorName = element.getContributor().getName();
		
		return Platform.getBundle(contributorName);
	}

    /**
	 * Returns the image registry for this plugin.
	 * 
	 * Where are the images? The images (typically gifs) are found in the same
	 * plugins directory.
	 * 
	 * @see ImageRegistry
	 * 
	 * Note: The workbench uses the standard JFace ImageRegistry to track its
	 * images. In addition the class WorkbenchGraphicResources provides
	 * convenience access to the graphics resources and fast field access for
	 * some of the commonly used graphical images.
	 */
    protected ImageRegistry createImageRegistry() {
        return WorkbenchImages.getImageRegistry();
    }

    /**
     * Returns the action set registry for the workbench.
     *
     * @return the workbench action set registry
     */
    public ActionSetRegistry getActionSetRegistry() {
        if (actionSetRegistry == null) {
            actionSetRegistry = new ActionSetRegistry();
        }
        return actionSetRegistry;
    }

    /**
     * Return the default instance of the receiver. This represents the runtime plugin.
     * @return WorkbenchPlugin
     * @see AbstractUIPlugin for the typical implementation pattern for plugin classes.
     */
    public static WorkbenchPlugin getDefault() {
        return inst;
    }

    /**
     * Answer the manager that maps resource types to a the 
     * description of the editor to use
     * @return IEditorRegistry the editor registry used
     * by this plug-in.
     */

    public IEditorRegistry getEditorRegistry() {
    	// RAP [bm]: 
//        if (editorRegistry == null) {
//            editorRegistry = new EditorRegistry();
//        }
//        return editorRegistry;
        return EditorRegistry.getInstance();
    	// RAPEND: [bm] 
    }

    /**
     * Answer the element factory for an id, or <code>null</code. if not found.
     * @param targetID
     * @return IElementFactory
     */
    public IElementFactory getElementFactory(String targetID) {

        // Get the extension point registry.
        IExtensionPoint extensionPoint;
        extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(
                PlatformUI.PLUGIN_EXTENSION_NAME_SPACE, IWorkbenchRegistryConstants.PL_ELEMENT_FACTORY);

        if (extensionPoint == null) {
            WorkbenchPlugin
                    .log("Unable to find element factory. Extension point: " + IWorkbenchRegistryConstants.PL_ELEMENT_FACTORY + " not found"); //$NON-NLS-2$ //$NON-NLS-1$
            return null;
        }

        // Loop through the config elements.
        IConfigurationElement targetElement = null;
        IConfigurationElement[] configElements = extensionPoint
                .getConfigurationElements();
        for (int j = 0; j < configElements.length; j++) {
            String strID = configElements[j].getAttribute("id"); //$NON-NLS-1$
            if (targetID.equals(strID)) {
                targetElement = configElements[j];
                break;
            }
        }
        if (targetElement == null) {
            // log it since we cannot safely display a dialog.
            WorkbenchPlugin.log("Unable to find element factory: " + targetID); //$NON-NLS-1$
            return null;
        }

        // Create the extension.
        IElementFactory factory = null;
        try {
            factory = (IElementFactory) createExtension(targetElement, "class"); //$NON-NLS-1$
        } catch (CoreException e) {
            // log it since we cannot safely display a dialog.
            WorkbenchPlugin.log(
                    "Unable to create element factory.", e.getStatus()); //$NON-NLS-1$
            factory = null;
        }
        return factory;
    }

    /**
     * Returns the presentation factory with the given id, or <code>null</code> if not found.
     * @param targetID The id of the presentation factory to use.
     * @return AbstractPresentationFactory or <code>null</code>
     * if not factory matches that id.
     */
    public AbstractPresentationFactory getPresentationFactory(String targetID) {
        Object o = createExtension(
		        IWorkbenchRegistryConstants.PL_PRESENTATION_FACTORIES,
		        "factory", targetID); //$NON-NLS-1$

        if (o instanceof AbstractPresentationFactory) {
            return (AbstractPresentationFactory) o;
        }
        WorkbenchPlugin
                .log("Error creating presentation factory: " + targetID + " -- class is not an AbstractPresentationFactory"); //$NON-NLS-1$ //$NON-NLS-2$
        return null;
    }

    /**
     * Looks up the configuration element with the given id on the given extension point
     * and instantiates the class specified by the class attributes.
     * 
     * @param extensionPointId the extension point id (simple id)
     * @param elementName the name of the configuration element, or <code>null</code>
     *   to match any element
     * @param targetID the target id
     * @return the instantiated extension object, or <code>null</code> if not found
     */
    private Object createExtension(String extensionPointId, String elementName,
            String targetID) {
    	// RAP [bm]: 
//        IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
//                .getExtensionPoint(PI_WORKBENCH, extensionPointId);
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
        .getExtensionPoint(PlatformUI.PLUGIN_EXTENSION_NAME_SPACE, extensionPointId);
        // RAPEND: [bm] 

        if (extensionPoint == null) {
            WorkbenchPlugin
                    .log("Unable to find extension. Extension point: " + extensionPointId + " not found"); //$NON-NLS-1$ //$NON-NLS-2$
            return null;
        }

        // Loop through the config elements.
        IConfigurationElement targetElement = null;
        IConfigurationElement[] elements = extensionPoint
                .getConfigurationElements();
        for (int j = 0; j < elements.length; j++) {
            IConfigurationElement element = elements[j];
            if (elementName == null || elementName.equals(element.getName())) {
                String strID = element.getAttribute("id"); //$NON-NLS-1$
                if (targetID.equals(strID)) {
                    targetElement = element;
                    break;
                }
            }
        }
        if (targetElement == null) {
            // log it since we cannot safely display a dialog.
            WorkbenchPlugin.log("Unable to find extension: " + targetID //$NON-NLS-1$
                    + " in extension point: " + extensionPointId); //$NON-NLS-1$ 
            return null;
        }

        // Create the extension.
        try {
            return createExtension(targetElement, "class"); //$NON-NLS-1$
        } catch (CoreException e) {
            // log it since we cannot safely display a dialog.
            WorkbenchPlugin.log("Unable to create extension: " + targetID //$NON-NLS-1$
                    + " in extension point: " + extensionPointId //$NON-NLS-1$
                    + ", status: ", e.getStatus()); //$NON-NLS-1$
        }
        return null;
    }

    /**
     * Return the perspective registry.
     * @return IPerspectiveRegistry. The registry for the receiver.
     */
    public IPerspectiveRegistry getPerspectiveRegistry() {
// RAP [rh]: PerspectiveRegistry has session scope
//        if (perspRegistry == null) {
//            perspRegistry = new PerspectiveRegistry();
//            // the load methods can touch on WorkbenchImages if an image is
//			// missing so we need to wrap the call in
//			// a startup block for the case where a custom descriptor exists on
//			// startup that does not have an image
//			// associated with it. See bug 196352.
//			StartupThreading.runWithoutExceptions(new StartupRunnable() {
//				public void runWithException() throws Throwable {
//					perspRegistry.load();
//				}
//			});
//            
//        }
//        return perspRegistry;
      final PerspectiveRegistry result
        = ( PerspectiveRegistry )SessionSingletonBase.getInstance( PerspectiveRegistry.class );
      ISessionStore sessionStore = RWT.getSessionStore();
      Boolean initialized
        = ( Boolean )sessionStore.getAttribute( PERSP_REGISTRY_INITIALIZED );
      if( initialized == null ) {
        sessionStore.setAttribute( PERSP_REGISTRY_INITIALIZED, Boolean.TRUE );        
        StartupThreading.runWithoutExceptions(new StartupRunnable() {
          public void runWithException() throws Throwable {
            result.load();
          }
        } );      
      }
    	return result;
    }

    /**
     * Returns the working set manager
     * 
     * @return the working set manager
     */
    public IWorkingSetManager getWorkingSetManager() {
// RAP [rh] WorkingSetManager must be a session-singleton
//        if (workingSetManager == null) {
//            workingSetManager = new WorkingSetManager(bundleContext);
//            workingSetManager.restoreState();
//        }
//        return workingSetManager;
      return WorkingSetManagerStore.getInstance().getWorkingSetManager( bundleContext );
    }

    /**
     * Returns the working set registry
     * 
     * @return the working set registry
     */
    public WorkingSetRegistry getWorkingSetRegistry() {
// RAP [rh] WorkingSetRegistry must be a session-singleton      
//        if (workingSetRegistry == null) {
//            workingSetRegistry = new WorkingSetRegistry();
//            workingSetRegistry.load();
//        }
//        return workingSetRegistry;
      return WorkingSetRegistryStore.getInstance().getWorkingSetRegistry();
    }

    /**
     * Returns the introduction registry.
     *
     * @return the introduction registry.
     * @since 1.2
     */
    public IIntroRegistry getIntroRegistry() {
    	// RAP [bm]: IntroRegistry must be a session-singleton
//        if (introRegistry == null) {
//            introRegistry = new IntroRegistry();
//        }
//        return introRegistry;
    	return (IIntroRegistry) SessionSingletonBase.getInstance(IntroRegistry.class);
    }
    
    /**
	 * Returns the operation support.
	 * 
	 * @return the workbench operation support.
	 * @since 1.1
	 */
    public IWorkbenchOperationSupport getOperationSupport() {
        if (operationSupport == null) {
        	operationSupport = new WorkbenchOperationSupport();
        }
        return operationSupport;
    }
    

    /**
     * Get the preference manager.
     * @return PreferenceManager the preference manager for
     * the receiver.
     */
    public PreferenceManager getPreferenceManager() {
        if (preferenceManager == null) {
            preferenceManager = new WorkbenchPreferenceManager(
                    PREFERENCE_PAGE_CATEGORY_SEPARATOR);

            //Get the pages from the registry
            PreferencePageRegistryReader registryReader = new PreferencePageRegistryReader(
                    getWorkbench());
            registryReader
                    .loadFromRegistry(Platform.getExtensionRegistry());
            preferenceManager.addPages(registryReader.getTopLevelNodes());
           
        }
        return preferenceManager;
    }

    /**
     * Returns the shared images for the workbench.
     *
     * @return the shared image manager
     */
    public ISharedImages getSharedImages() {
        if (sharedImages == null) {
			sharedImages = new SharedImages();
		}
        return sharedImages;
    }

    /**
     * Returns the theme registry for the workbench.
     * 
     * @return the theme registry
     */
    public IThemeRegistry getThemeRegistry() {
// RAP [rh] ThemeRegistry must be a session-singleton       
//        if (themeRegistry == null) {
//            themeRegistry = new ThemeRegistry();
//            ThemeRegistryReader reader = new ThemeRegistryReader();
//            reader.readThemes(Platform.getExtensionRegistry(),
//                    themeRegistry);
//        }
//        return themeRegistry;
      return ThemeRegistryStore.getInstance().getThemeRegistry();
    }

    /**
     * Answer the view registry.
     * @return IViewRegistry the view registry for the
     * receiver.
     */
    public IViewRegistry getViewRegistry() {
    	// RAP [bm]: 
//        if (viewRegistry == null) {
//            viewRegistry = new ViewRegistry();
//        }
//        return viewRegistry;
        // RAPEND: [bm] 
    	return ViewRegistry.getInstance();
    }

    /**
     * Answer the workbench.
     * @deprecated Use <code>PlatformUI.getWorkbench()</code> instead.
     */
    public IWorkbench getWorkbench() {
        return PlatformUI.getWorkbench();
    }

    /** 
     * Set default preference values.
     * This method must be called whenever the preference store is initially loaded
     * because the default values are not stored in the preference store.
     */
    protected void initializeDefaultPreferences(IPreferenceStore store) {
        // Do nothing.  This should not be called.
        // Prefs are initialized in WorkbenchPreferenceInitializer.
    }

    /**
     * Logs the given message to the platform log.
     * 
     * If you have an exception in hand, call log(String, Throwable) instead.
     * 
     * If you have a status object in hand call log(String, IStatus) instead.
     * 
     * This convenience method is for internal use by the Workbench only and
     * must not be called outside the Workbench.
     * 
     * @param message
     *            A high level UI message describing when the problem happened.
     */
    public static void log(String message) {
        getDefault().getLog().log(
                StatusUtil.newStatus(IStatus.ERROR, message, null));    
    }
    
    /**
     * Log the throwable.
     * @param t
     */
    public static void log(Throwable t) {
		getDefault().getLog().log(getStatus(t));
	}

	/**
	 * Return the status from throwable
	 * @param t throwable
	 * @return IStatus
	 */
	public static IStatus getStatus(Throwable t) {
		String message = StatusUtil.getLocalizedMessage(t);

		return newError(message, t);
	}

	/**
	 * Create a new error from the message and the
	 * throwable.
	 * @param message
	 * @param t
	 * @return IStatus
	 */
	public static IStatus newError(String message, Throwable t) {
		String pluginId = "org.eclipse.ui.workbench"; //$NON-NLS-1$
		int errorCode = IStatus.OK;

		// If this was a CoreException, keep the original plugin ID and error
		// code
		if (t instanceof CoreException) {
			CoreException ce = (CoreException) t;
			pluginId = ce.getStatus().getPlugin();
			errorCode = ce.getStatus().getCode();
		}

		return new Status(IStatus.ERROR, pluginId, errorCode, message,
				StatusUtil.getCause(t));
	}
    
    /**
	 * Logs the given message and throwable to the platform log.
	 * 
	 * If you have a status object in hand call log(String, IStatus) instead.
	 * 
	 * This convenience method is for internal use by the Workbench only and
	 * must not be called outside the Workbench.
	 * 
	 * @param message
	 *            A high level UI message describing when the problem happened.
	 * @param t
	 *            The throwable from where the problem actually occurred.
	 */
    public static void log(String message, Throwable t) {
        IStatus status = StatusUtil.newStatus(IStatus.ERROR, message, t);
        log(message, status);
    }
    
    /**
     * Logs the given throwable to the platform log, indicating the class and
     * method from where it is being logged (this is not necessarily where it
     * occurred).
     * 
     * This convenience method is for internal use by the Workbench only and
     * must not be called outside the Workbench.
     * 
     * @param clazz
     *            The calling class.
     * @param methodName
     *            The calling method name.
     * @param t
     *            The throwable from where the problem actually occurred.
     */
    public static void log(Class clazz, String methodName, Throwable t) {
        String msg = MessageFormat.format("Exception in {0}.{1}: {2}", //$NON-NLS-1$
                new Object[] { clazz.getName(), methodName, t });
        log(msg, t);
    }
    
    /**
     * Logs the given message and status to the platform log.
     * 
     * This convenience method is for internal use by the Workbench only and
     * must not be called outside the Workbench.
     * 
     * @param message
     *            A high level UI message describing when the problem happened.
     *            May be <code>null</code>.
     * @param status
     *            The status describing the problem. Must not be null.
     */
    public static void log(String message, IStatus status) {

        //1FTUHE0: ITPCORE:ALL - API - Status & logging - loss of semantic info

        if (message != null) {
            getDefault().getLog().log(
                    StatusUtil.newStatus(IStatus.ERROR, message, null));
        }

        getDefault().getLog().log(status);
    }

    /**
     * Log the status to the default log.
     * @param status
     */
    public static void log(IStatus status) {
        getDefault().getLog().log(status);
    }
    
    /**
     * Get the decorator manager for the receiver
     * @return DecoratorManager the decorator manager
     * for the receiver.
     */
    public DecoratorManager getDecoratorManager() {
    	// RAP [rs]: 
//        if (this.decoratorManager == null) {
//            this.decoratorManager = new DecoratorManager();
//        }
//        return decoratorManager;
    	return DecoratorManagerStore.getInstance().getDecoratorManager();
    	// RAPEND: [rs] 
    }

    // TODO [bm]: move to internal util
    // RAP [bm]: 
    /**
     * Get the http service tracker to register new servlets or resources (RAP only)
     * 
     * @return ServiceTracker the http server tracker 
     */
    public HttpServiceTracker getHttpServiceTracker() {
      return httpServiceTracker;
    }
    // RAPEND: [bm] 

    
    /*
     *  (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
    	context.addBundleListener(getBundleListener());
        super.start(context);
        bundleContext = context;
        
        Window.setDefaultOrientation(getDefaultOrientation());

        JFaceUtil.initializeJFace();
        
        // RAP [fappel]: initialize session aware job management
        JobManagerAdapter.getInstance();
        
        // RAP [fappel]: ensure that the rap http context was loaded before
        //               the mapping of servlets from branding takes place
        String serviceName = HttpContextExtensionService.class.getName();
        ServiceTracker httpContextExtensionServiceTracker
          = new ServiceTracker( context, serviceName, null )
        {
          public Object addingService( final ServiceReference reference ) {
            Object result = super.addingService( reference );
            httpServiceTracker = new HttpServiceTracker(context);
            try {
              BrandingExtension.read();
            } catch( final IOException ioe ) {
              WorkbenchPlugin.log( "Unable to read branding extension", ioe ); //$NON-NLS-1$
            }
            httpServiceTracker.open();
            return result;
          }
        };
        httpContextExtensionServiceTracker.open();

        
// RAP [fappel]: as workbench instances in RAP run in session scope the
//               UI bundle should be initialized in time
//        // The UI plugin needs to be initialized so that it can install the callback in PrefUtil,
//        // which needs to be done as early as possible, before the workbench
//        // accesses any API preferences.
//        Bundle uiBundle = Platform.getBundle(PlatformUI.PLUGIN_ID);
//        try {
//            // Attempt to load the activator of the ui bundle.  This will force lazy start
//            // of the ui bundle.  Using the bundle activator class here because it is a
//            // class that needs to be loaded anyway so it should not cause extra classes
//            // to be loaded.s
//        	if(uiBundle != null)
//        		uiBundle.start(Bundle.START_TRANSIENT);
//        } catch (BundleException e) {
//            WorkbenchPlugin.log("Unable to load UI activator", e); //$NON-NLS-1$
//        }
//
//		/*
//		 * DO NOT RUN ANY OTHER CODE AFTER THIS LINE.  If you do, then you are
//		 * likely to cause a deadlock in class loader code.  Please see Bug 86450
//		 * for more information.
//		 */
    }

	/**
     * Get the default orientation from the command line
     * arguments. If there are no arguments imply the 
     * orientation.
	 * @return int
	 * @see SWT#NONE
	 * @see SWT#RIGHT_TO_LEFT
	 * @see SWT#LEFT_TO_RIGHT
	 * @since 1.1
	 */
    private int getDefaultOrientation() {
		
		String[] commandLineArgs = Platform.getCommandLineArgs();
		
		int orientation = getCommandLineOrientation(commandLineArgs);
		
		if(orientation != SWT.NONE) {
			return orientation;
		}
		
		orientation = getSystemPropertyOrientation();
		
		if(orientation != SWT.NONE) {
			return orientation;
		}

		// RAP [bm]: 
//		return checkCommandLineLocale(); //Use the default value if there is nothing specified
		return SWT.NONE;
		// RAPEND: [bm] 

	}
	
    // RAP [bm]: 
//	/**
//	 * Check to see if the command line parameter for -nl
//	 * has been set. If so imply the orientation from this 
//	 * specified Locale. If it is a bidirectional Locale
//	 * return SWT#RIGHT_TO_LEFT.
//	 * If it has not been set or has been set to 
//	 * a unidirectional Locale then return SWT#NONE.
//	 * 
//	 * Locale is determined differently by different JDKs 
//	 * and may not be consistent with the users expectations.
//	 * 
//
//	 * @return int
//	 * @see SWT#NONE
//	 * @see SWT#RIGHT_TO_LEFT
//	 */
//	private int checkCommandLineLocale() {
//		//Check if the user property is set. If not do not
//		//rely on the vm.
//		if(System.getProperty(NL_USER_PROPERTY) == null) {
//			return SWT.NONE;
//		}
//		
//		Locale locale = Locale.getDefault();
//		String lang = locale.getLanguage();
//
//		if ("iw".equals(lang) || "he".equals(lang) || "ar".equals(lang) ||  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//				"fa".equals(lang) || "ur".equals(lang)) { //$NON-NLS-1$ //$NON-NLS-2$ 
//			return SWT.RIGHT_TO_LEFT;
//		}
//
//		return SWT.NONE;
//	}
// RAPEND: [bm] 

	/**
	 * Check to see if the orientation was set in the
	 * system properties. If there is no orientation 
	 * specified return SWT#NONE.
	 * @return int
	 * @see SWT#NONE
	 * @see SWT#RIGHT_TO_LEFT
	 * @see SWT#LEFT_TO_RIGHT
	 * @since 1.0
	 */
	private int getSystemPropertyOrientation() {
		// RAP [bm]: 
//		String orientation = System.getProperty(ORIENTATION_PROPERTY);
//		if(RIGHT_TO_LEFT.equals(orientation)) {
//			return SWT.RIGHT_TO_LEFT;
//		}
//		if(LEFT_TO_RIGHT.equals(orientation)) {
//			return SWT.LEFT_TO_RIGHT;
//		}
		// RAPEND: [bm] 

		return SWT.NONE;
	}

	/**
	 * Find the orientation in the commandLineArgs. If there
	 * is no orientation specified return SWT#NONE.
	 * @param commandLineArgs
	 * @return int
	 * @see SWT#NONE
	 * @see SWT#RIGHT_TO_LEFT
	 * @see SWT#LEFT_TO_RIGHT
	 * @since 1.1
	 */
	private int getCommandLineOrientation(String[] commandLineArgs) {
		//Do not process the last one as it will never have a parameter
		// RAP [bm]: 
//		for (int i = 0; i < commandLineArgs.length - 1; i++) {
//			if(commandLineArgs[i].equalsIgnoreCase(ORIENTATION_COMMAND_LINE)){
//				String orientation = commandLineArgs[i+1];
//				if(orientation.equals(RIGHT_TO_LEFT)){
//					System.setProperty(ORIENTATION_PROPERTY,RIGHT_TO_LEFT);
//					return SWT.RIGHT_TO_LEFT;
//				}
//				if(orientation.equals(LEFT_TO_RIGHT)){
//					System.setProperty(ORIENTATION_PROPERTY,LEFT_TO_RIGHT);
//					return SWT.LEFT_TO_RIGHT;
//				}
//			}
//		}
		// RAPEND: [bm] 

		return SWT.NONE;
	}

	/**
     * Return an array of all bundles contained in this workbench.
     * 
     * @return an array of bundles in the workbench or an empty array if none
     */
    public Bundle[] getBundles() {
        return bundleContext == null ? new Bundle[0] : bundleContext
                .getBundles();
    }
    
    /**
     * Returns the bundle context associated with the workbench plug-in.
     * 
     * @return the bundle context
     */
    public BundleContext getBundleContext() {
    	return bundleContext;
    }

    // RAP [bm]: 
//    /**
//     * Returns the application name.
//     * <p>
//     * Note this is never shown to the user.
//     * It is used to initialize the SWT Display.
//     * On Motif, for example, this can be used
//     * to set the name used for resource lookup.
//     * </p>
//     *
//     * @return the application name, or <code>null</code>
//     * @see org.eclipse.swt.widgets.Display#setAppName
//     * @since 1.1
//     */
//    public String getAppName() {
//        return getProductInfo().getAppName();
//    }

    /**
     * Returns the name of the product.
     * 
     * @return the product name, or <code>null</code> if none
     */
    public String getProductName() {
        return getProductInfo().getProductName();
    }

    /**
     * Returns the image descriptors for the window image to use for this product.
     * 
     * @return an array of the image descriptors for the window image, or
     *         <code>null</code> if none
     */
    public ImageDescriptor[] getWindowImages() {
        return getProductInfo().getWindowImages();
    }

    /**
     * Returns an instance that describes this plugin's product (formerly "primary
     * plugin").
     * @return ProductInfo the product info for the receiver
     */
    private ProductInfo getProductInfo() {
        if (productInfo == null) {
			productInfo = new ProductInfo(Platform.getProduct());
		}
        return productInfo;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
    	if (bundleListener!=null) {
    		context.removeBundleListener(bundleListener);
    		bundleListener = null;
    	}
    	
    	// RAP [bm]: 
        httpServiceTracker.close();
        httpServiceTracker = null;
        EntryPointExtension.unbindAll();
        // RAPEND: [bm] 


    	// TODO normally super.stop(*) would be the last statement in this
    	// method
        super.stop(context);
        if (workingSetManager != null) {
        	workingSetManager.dispose();
        	workingSetManager= null;
        }       
        SWTResourceUtil.shutdown();
    } 
    
    /**
     * Return the new wizard registry.
     * 
     * @return the new wizard registry
     */
    public IWizardRegistry getNewWizardRegistry() {
    	return NewWizardRegistry.getInstance();
    }
    
    /**
     * Return the import wizard registry.
     * 
     * @return the import wizard registry
     */
    public IWizardRegistry getImportWizardRegistry() {
    	return ImportWizardRegistry.getInstance();
    }
    
    /**
     * Return the export wizard registry.
     * 
     * @return the export wizard registry
     */
    public IWizardRegistry getExportWizardRegistry() {
    	return ExportWizardRegistry.getInstance();
    }
    
    /**
     * FOR INTERNAL WORKBENCH USE ONLY. 
     * 
     * Returns the path to a location in the file system that can be used 
     * to persist/restore state between workbench invocations.
     * If the location did not exist prior to this call it will  be created.
     * Returns <code>null</code> if no such location is available.
     * 
     * @return path to a location in the file system where this plug-in can
     * persist data between sessions, or <code>null</code> if no such
     * location is available.
     */
    public IPath getDataLocation() {
        try {
            return getStateLocation();
        } catch (IllegalStateException e) {
            // This occurs if -data=@none is explicitly specified, so ignore this silently.
            // Is this OK? See bug 85071.
            return null;
        }
    }

	/* package */ void addBundleListener(BundleListener bundleListener) {
		bundleContext.addBundleListener(bundleListener);
	}    

	/* package */ void removeBundleListener(BundleListener bundleListener) {
		bundleContext.removeBundleListener(bundleListener);
	}    
	
	/* package */ int getBundleCount() {
		return bundleContext.getBundles().length;
	}
	
	/* package */ OutputStream getSplashStream() {
		// assumes the output stream is available as a service
		// see EclipseStarter.publishSplashScreen
		ServiceReference[] ref;
		try {
			ref = bundleContext.getServiceReferences(OutputStream.class.getName(), null);
		} catch (InvalidSyntaxException e) {
			return null;
		}
		if(ref==null) {
			return null;
		}
		for (int i = 0; i < ref.length; i++) {
			String name = (String) ref[i].getProperty("name"); //$NON-NLS-1$
			if (name != null && name.equals("splashstream")) {  //$NON-NLS-1$
				Object result = bundleContext.getService(ref[i]);
				bundleContext.ungetService(ref[i]);
				return (OutputStream) result;
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	private BundleListener getBundleListener() {
		if (bundleListener == null) {
			bundleListener = new SynchronousBundleListener() {
				public void bundleChanged(BundleEvent event) {
					WorkbenchPlugin.this.bundleChanged(event);
				}
			};
		}
		return bundleListener;
	}

	private void bundleChanged(BundleEvent event) {
		// a bundle in the STARTING state generates 2 events, LAZY_ACTIVATION
		// when it enters STARTING and STARTING when it exists STARTING :-)
		synchronized (startingBundles) {
			switch (event.getType()) {
				case BundleEvent.STARTING :
					startingBundles.add(event.getBundle());
					break;
				case BundleEvent.STARTED :
				case BundleEvent.STOPPED :
					startingBundles.remove(event.getBundle());
					break;
				default :
					break;
			}
		}
	}

	public boolean isStarting(Bundle bundle) {
		synchronized (startingBundles) {
			return startingBundles.contains(bundle);
		}
	}

	/**
	 * Return whether or not the OSGi framework has specified the handle of a splash shell.
	 * 
	 * @return whether or not the OSGi framework has specified the handle of a splash shell
	 * @since 1.1
	 */
	public static boolean isSplashHandleSpecified() {
		// RAP [bm]: 
//		return System.getProperty(PROP_SPLASH_HANDLE) != null;
		return false;
		// RAPEND: [bm] 
	}
	
	// RAP [bm]: 
//	/**
//	 * Get the splash shell for this workbench instance, if any. This will find
//	 * the splash created by the launcher (native) code and wrap it in a SWT
//	 * shell. This may have the side effect of setting data on the provided
//	 * {@link Display}.
//	 * 
//	 * @param display
//	 *            the display to parent the shell on
//	 * 
//	 * @return the splash shell or <code>null</code>
//	 * @throws InvocationTargetException
//	 * @throws IllegalAccessException
//	 * @throws IllegalArgumentException
//	 * @throws NumberFormatException
//	 * @see Display#setData(String, Object)
//	 * @since 3.4
//	 */
//	public static Shell getSplashShell(Display display)
//			throws NumberFormatException, IllegalArgumentException,
//			IllegalAccessException, InvocationTargetException {
//		Shell splashShell = (Shell) display.getData(DATA_SPLASH_SHELL); 
//		if (splashShell != null)
//			return splashShell;
//		
//		String splashHandle = System.getProperty(PROP_SPLASH_HANDLE);
//		if (splashHandle == null) {
//			return null;
//		}
//	
//		// look for the 32 bit internal_new shell method
//		try {
//			Method method = Shell.class.getMethod(
//					"internal_new", new Class[] { Display.class, int.class }); //$NON-NLS-1$
//			// we're on a 32 bit platform so invoke it with splash
//			// handle as an int
//			splashShell = (Shell) method.invoke(null, new Object[] { display,
//					new Integer(splashHandle) });
//		} catch (NoSuchMethodException e) {
//			// look for the 64 bit internal_new shell method
//			try {
//				Method method = Shell.class
//						.getMethod(
//								"internal_new", new Class[] { Display.class, long.class }); //$NON-NLS-1$
//
//				// we're on a 64 bit platform so invoke it with a long
//				splashShell = (Shell) method.invoke(null, new Object[] {
//						display, new Long(splashHandle) });
//			} catch (NoSuchMethodException e2) {
//				// cant find either method - don't do anything.
//			}
//		}
//
//		display.setData(DATA_SPLASH_SHELL, splashShell);
//		return splashShell;
//	}
	
	// RAP [bm]: 
//	/**
//	 * Removes any splash shell data set on the provided display and disposes
//	 * the shell if necessary.
//	 * 
//	 * @param display
//	 *            the display to parent the shell on
//	 * @since 3.4
//	 */
//	public static void unsetSplashShell(Display display) {
//		Shell splashShell = (Shell) display.getData(DATA_SPLASH_SHELL);
//		if (splashShell != null) {
//			if (!splashShell.isDisposed())
//				splashShell.dispose();
//			display.setData(DATA_SPLASH_SHELL, null);
//		}
//
//	}
}
