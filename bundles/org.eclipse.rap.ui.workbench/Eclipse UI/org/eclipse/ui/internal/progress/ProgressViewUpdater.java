/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.progress;

import java.util.*;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.SessionSingletonBase;
import org.eclipse.rwt.lifecycle.UICallBack;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.internal.util.PrefUtil;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * The ProgressViewUpdater is the singleton that updates viewers.
 */
// RAP [fappel]: ProgressViewUpdater needs to be a singleton per session
// class ProgressViewUpdater implements IJobProgressManagerListener {
//
//    private static ProgressViewUpdater singleton;
public class ProgressViewUpdater
  extends SessionSingletonBase
  implements IJobProgressManagerListener
{

    private IProgressUpdateCollector[] collectors;

    Job updateJob;

    UpdatesInfo currentInfo = new UpdatesInfo();

    Object updateLock = new Object();

    boolean debug;
    
    // RAP [fappel]:
    private Display display;
    
   
    /**
     * The UpdatesInfo is a private class for keeping track of the updates
     * required.
     */
    class UpdatesInfo {

        Collection additions = new HashSet();

        Collection deletions = new HashSet();

        Collection refreshes = new HashSet();

        boolean updateAll = false;

        private UpdatesInfo() {
            //Create a new instance of the info
        }

        /**
         * Add an add update
         * 
         * @param addition
         */
        void add(JobTreeElement addition) {
            additions.add(addition);
        }

        /**
         * Add a remove update
         * 
         * @param removal
         */
        void remove(JobTreeElement removal) {
            deletions.add(removal);
        }

        /**
         * Add a refresh update
         * 
         * @param refresh
         */
        void refresh(JobTreeElement refresh) {
            refreshes.add(refresh);
        }

        /**
         * Reset the caches after completion of an update.
         */
        void reset() {
            additions.clear();
            deletions.clear();
            refreshes.clear();
            updateAll = false;
        }

        void processForUpdate() {
            HashSet staleAdditions = new HashSet();

            Iterator additionsIterator = additions.iterator();
            while (additionsIterator.hasNext()) {
                JobTreeElement treeElement = (JobTreeElement) additionsIterator
                        .next();
                if (!treeElement.isActive()) {
                    if (deletions.contains(treeElement)) {
						staleAdditions.add(treeElement);
					}
                }
            }

            additions.removeAll(staleAdditions);

            HashSet obsoleteRefresh = new HashSet();
            Iterator refreshIterator = refreshes.iterator();
            while (refreshIterator.hasNext()) {
                JobTreeElement treeElement = (JobTreeElement) refreshIterator
                        .next();
                if (deletions.contains(treeElement)
                        || additions.contains(treeElement)) {
					obsoleteRefresh.add(treeElement);
				}
                
                //Also check for groups that are being added
               Object parent = treeElement.getParent();
               if(parent != null && (deletions.contains(parent)
                       || additions.contains(parent))){
            	   obsoleteRefresh.add(treeElement);
               }
               
                if (!treeElement.isActive()) {
                    //If it is done then delete it
                    obsoleteRefresh.add(treeElement);
                    deletions.add(treeElement);
                }
            }

            refreshes.removeAll(obsoleteRefresh);

        }
    }

    /**
     * Return a new instance of the receiver.
     * 
     * @return ProgressViewUpdater
     */
   static ProgressViewUpdater getSingleton() {
// RAP [fappel]: session aware implementation
//        if (singleton == null) {
//			singleton = new ProgressViewUpdater();
//		}
//        return singleton;
       ProgressViewUpdater instance
         = ( ProgressViewUpdater )getInstance( ProgressViewUpdater.class );
       if( instance.display == null ) {
         instance.display = Display.getCurrent();
       }
       return instance;
    }

    /**
     * Return whether or not there is a singleton for updates to avoid creating
     * extra listeners.
     * 
     * @return boolean <code>true</code> if there is already
     * a singleton
     */
    static boolean hasSingleton() {
// RAP [fappel]:
//        return singleton != null;
        return getSingleton() != null;
    }

    static void clearSingleton() {
// RAP [fappel]:
//        if (singleton != null) {
//			ProgressManager.getInstance().removeListener(singleton);
//		}
//        singleton = null;
        if (getSingleton() != null) {
			ProgressManager.getInstance().removeListener(getSingleton());
        }
    }

    /**
     * Create a new instance of the receiver.
     */
    private ProgressViewUpdater() {
        createUpdateJob();
        collectors = new IProgressUpdateCollector[0];
        ProgressManager.getInstance().addListener(this);
        debug = 
        	PrefUtil.getAPIPreferenceStore().
        		getBoolean(IWorkbenchPreferenceConstants.SHOW_SYSTEM_JOBS);
        // RAP [fappel]: Ensure that job is removed in case of session timeout.
        //               Note that this is still under investigation.
        ISessionStore session = RWT.getSessionStore();
        String watchDogKey = getClass().getName() + ".watchDog";
        if( session.getAttribute( watchDogKey ) == null ) {
          session.setAttribute( watchDogKey, new HttpSessionBindingListener() {
            public void valueBound( final HttpSessionBindingEvent event ) {
            }
            public void valueUnbound( final HttpSessionBindingEvent event ) {
              if( updateJob != null ) {
                updateJob.cancel();
                updateJob.addJobChangeListener( new JobCanceler() );
              }
            }
          } );
        }
    }

    /**
     * Add the new collector to the list of collectors.
     * 
     * @param newCollector
     */
    void addCollector(IProgressUpdateCollector newCollector) {
        IProgressUpdateCollector[] newCollectors = new IProgressUpdateCollector[collectors.length + 1];
        System.arraycopy(collectors, 0, newCollectors, 0, collectors.length);
        newCollectors[collectors.length] = newCollector;
        collectors = newCollectors;
    }

    /**
     * Remove the collector from the list of collectors.
     * 
     * @param provider
     */
    void removeCollector(IProgressUpdateCollector provider) {
        HashSet newCollectors = new HashSet();
        for (int i = 0; i < collectors.length; i++) {
            if (!collectors[i].equals(provider)) {
				newCollectors.add(collectors[i]);
			}
        }
        IProgressUpdateCollector[] newArray = new IProgressUpdateCollector[newCollectors
                .size()];
        newCollectors.toArray(newArray);
        collectors = newArray;
        //Remove ourselves if there is nothing to update
        if (collectors.length == 0) {
			clearSingleton();
		}
    }

    /**
     * Schedule an update.
     */
    void scheduleUpdate() {
// RAP [fappel]: session aware implementation
//        if (PlatformUI.isWorkbenchRunning()) {
//            //Add in a 100ms delay so as to keep priority low
//            updateJob.schedule(100);
//        }
        if (ProgressUtil.isWorkbenchRunning( display )) {
          //Add in a 100ms delay so as to keep priority low
          UICallBack.runNonUIThreadWithFakeContext( display, new Runnable() {
            public void run() {
              updateJob.schedule(100);
            }
          } );
        }
    }

    /**
     * Create the update job that handles the updatesInfo.
     */
    private void createUpdateJob() {
        updateJob = new WorkbenchJob(ProgressMessages.get().ProgressContentProvider_UpdateProgressJob) {
            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
             */
            public IStatus runInUIThread(IProgressMonitor monitor) {

                //Abort the job if there isn't anything
                if (collectors.length == 0) {
					return Status.CANCEL_STATUS;
				}

                if (currentInfo.updateAll) {
                    synchronized (updateLock) {
                        currentInfo.reset();
                    }
                    for (int i = 0; i < collectors.length; i++) {
                        collectors[i].refresh();
                    }

                } else {
                    //Lock while getting local copies of the caches.
                    Object[] updateItems;
                    Object[] additionItems;
                    Object[] deletionItems;
                    synchronized (updateLock) {
                        currentInfo.processForUpdate();

                        updateItems = currentInfo.refreshes.toArray();
                        additionItems = currentInfo.additions.toArray();
                        deletionItems = currentInfo.deletions.toArray();

                        currentInfo.reset();
                    }

                    for (int v = 0; v < collectors.length; v++) {
                        IProgressUpdateCollector collector = collectors[v];

                        if (updateItems.length > 0) {
							collector.refresh(updateItems);
						}
                        if (additionItems.length > 0) {
							collector.add(additionItems);
						}
                        if (deletionItems.length > 0) {
							collector.remove(deletionItems);
						}
                    }
                }

                return Status.OK_STATUS;
            }
        };
        updateJob.setSystem(true);
        updateJob.setPriority(Job.DECORATE);
        updateJob.setProperty(ProgressManagerUtil.INFRASTRUCTURE_PROPERTY, new Object());

    }

    /**
     * Get the updates info that we are using in the receiver.
     * 
     * @return Returns the currentInfo.
     */
    UpdatesInfo getCurrentInfo() {
        return currentInfo;
    }

    /**
     * Refresh the supplied JobInfo.
     * @param info
     */
    public void refresh(JobInfo info) {

        if (isUpdateJob(info.getJob())) {
			return;
		}

        synchronized (updateLock) {
            currentInfo.refresh(info);
            GroupInfo group = info.getGroupInfo();
            if (group != null) {
				currentInfo.refresh(group);
			}
        }
        //Add in a 100ms delay so as to keep priority low
        scheduleUpdate();

    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.internal.progress.IJobProgressManagerListener#refreshJobInfo(org.eclipse.ui.internal.progress.JobInfo)
     */
    public void refreshJobInfo(JobInfo info) {

        if (isUpdateJob(info.getJob())) {
			return;
		}

        synchronized (updateLock) {
            currentInfo.refresh(info);
        }
        //Add in a 100ms delay so as to keep priority low
        scheduleUpdate();

    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.internal.progress.IJobProgressManagerListener#refreshGroup(org.eclipse.ui.internal.progress.GroupInfo)
     */
    public void refreshGroup(GroupInfo info) {
        synchronized (updateLock) {
            currentInfo.refresh(info);
        }
        //Add in a 100ms delay so as to keep priority low
        scheduleUpdate();

    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.internal.progress.IJobProgressManagerListener#addGroup(org.eclipse.ui.internal.progress.GroupInfo)
     */
    public void addGroup(GroupInfo info) {

        synchronized (updateLock) {
            currentInfo.add(info);
        }
        scheduleUpdate();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.internal.progress.IJobProgressManagerListener#refreshAll()
     */
    public void refreshAll() {

        synchronized (updateLock) {
            currentInfo.updateAll = true;
        }

        //Add in a 100ms delay so as to keep priority low
        scheduleUpdate();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.internal.progress.IJobProgressManagerListener#add(org.eclipse.ui.internal.progress.JobInfo)
     */
    public void addJob(JobInfo info) {

        if (isUpdateJob(info.getJob())) {
			return;
		}

        synchronized (updateLock) {
            GroupInfo group = info.getGroupInfo();

            if (group == null) {
				currentInfo.add(info);
			} else {
                currentInfo.refresh(group);
            }
        }
        scheduleUpdate();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.internal.progress.IJobProgressManagerListener#removeJob(org.eclipse.ui.internal.progress.JobInfo)
     */
    public void removeJob(JobInfo info) {

        if (isUpdateJob(info.getJob())) {
			return;
		}

        synchronized (updateLock) {
            GroupInfo group = info.getGroupInfo();
            if (group == null) {
				currentInfo.remove(info);
			} else {
                currentInfo.refresh(group);
            }
        }
        scheduleUpdate();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.internal.progress.IJobProgressManagerListener#removeGroup(org.eclipse.ui.internal.progress.GroupInfo)
     */
    public void removeGroup(GroupInfo group) {
        synchronized (updateLock) {
            currentInfo.remove(group);
        }
        scheduleUpdate();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.internal.progress.IJobProgressManagerListener#showsDebug()
     */
    public boolean showsDebug() {
        return debug;
    }

    /**
     * Return whether or not this is the update job. This is used to determine
     * if a final refresh is required.
     * 
     * @param job
     * @return boolean <code>true</true> if this is the 
     * update job
     */
    boolean isUpdateJob(Job job) {
        return job.equals(updateJob);
    }
}
