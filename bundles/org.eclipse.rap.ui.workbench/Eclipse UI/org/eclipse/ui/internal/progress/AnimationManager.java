/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * The AnimationManager is the class that keeps track of the animation items to
 * update.
 */
//RAP [fappel]: AnimationManager needs to be session aware
public class AnimationManager extends SessionSingletonBase {
//    private static AnimationManager singleton;

    boolean animated = false;

    private IJobProgressManagerListener listener;

    IAnimationProcessor animationProcessor;

    WorkbenchJob animationUpdateJob;

    Display display;

    public static AnimationManager getInstance() {
// RAP [fappel]: AnimationManager needs to be session aware
//      if (singleton == null) {
//          singleton = new AnimationManager();
//      }
//      return singleton;
      AnimationManager instance
        = ( AnimationManager )getInstance( AnimationManager.class );
      if( instance.display == null ) {
        instance.display = Display.getCurrent();
      }
      return instance;
    }

    /**
     * Get the background color to be used.
     * 
     * @param control
     *            The source of the display.
     * @return Color
     */
    static Color getItemBackgroundColor(Control control) {
        return control.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND);
    }

    AnimationManager() {
         // RAP [fappel]: This is a helping flag used to avoid a memory leak
         //               due to thread management.
         //               Note that this is still under investigation.
         //               See comment in JobManagerAdapter
         final boolean[] done = new boolean[ 1 ];


        animationProcessor = new ProgressAnimationProcessor(this);

        animationUpdateJob = new WorkbenchJob(ProgressMessages.get().AnimationManager_AnimationStart) {

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
             */
            public IStatus runInUIThread(IProgressMonitor monitor) {

                if (animated) {
					animationProcessor.animationStarted();
				} else {
					animationProcessor.animationFinished();
				}
                return Status.OK_STATUS;
            }
            
            // RAP [fappel]: This is a helping mechanism used to avoid a memory
            //               leak due to thread management.
            //               Note that this is still under investigation.
            //               See comment in JobManagerAdapter
            public Object getAdapter( final Class adapter ) {
              Object result;
              if( adapter == IJobMarker.class ) {
                result = new IJobMarker() {
                  public boolean canBeRemoved() {
                    return done[ 0 ];
                  }
                };
              } else {
                result = super.getAdapter( adapter );
              }
              return result;
            }

        };
        animationUpdateJob.setSystem(true);
        
        listener = getProgressListener();
        ProgressManager.getInstance().addListener(listener);

        // RAP [fappel]: This is a helping mechanism used to avoid a memory leak
        //               due to thread management.
        //               Note that this is still under investigation.
        //               See comment in JobManagerAdapter
        ISessionStore session = RWT.getSessionStore();
        String watchDogKey = getClass().getName() + ".watchDog";
        if( session.getAttribute( watchDogKey ) == null ) {
          session.setAttribute( watchDogKey, new HttpSessionBindingListener() {
            public void valueBound( final HttpSessionBindingEvent event ) {
            }
            public void valueUnbound( final HttpSessionBindingEvent event ) {
              if( animationUpdateJob != null ) {
                animationUpdateJob.cancel();
                animationUpdateJob.addJobChangeListener( new JobCanceler() );
                done[ 0 ] = true;
              }
            }
          } );
        }
    }

    /**
     * Add an item to the list
     * 
     * @param item
     */
    void addItem(final AnimationItem item) {
        animationProcessor.addItem(item);
    }

    /**
     * Remove an item from the list
     * 
     * @param item
     */
    void removeItem(final AnimationItem item) {
        animationProcessor.removeItem(item);
    }

    /**
     * Return whether or not the current state is animated.
     * 
     * @return boolean
     */
    boolean isAnimated() {
        return animated;
    }

    /**
     * Set whether or not the receiver is animated.
     * 
     * @param bool
     */
// RAP [fappel]: map job to session
//    void setAnimated(final boolean bool) {
//      animated = bool;
//      animationUpdateJob.schedule(100);
//    }
    void setAnimated(final boolean bool) {
      animated = bool;
      Runnable scheduler = new Runnable() {
        public void run() {
          animationUpdateJob.schedule(100);
        }
      };
      UICallBack.runNonUIThreadWithFakeContext( display, scheduler );
    }

    /**
     * Dispose the images in the receiver.
     */
    void dispose() {
        setAnimated(false);
        ProgressManager.getInstance().removeListener(listener);
    }

    private IJobProgressManagerListener getProgressListener() {
        return new IJobProgressManagerListener() {
            Set jobs = Collections.synchronizedSet(new HashSet());

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.ui.internal.progress.IJobProgressManagerListener#addJob(org.eclipse.ui.internal.progress.JobInfo)
             */
            public void addJob(JobInfo info) {
                incrementJobCount(info);
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.ui.internal.progress.IJobProgressManagerListener#refreshJobInfo(org.eclipse.ui.internal.progress.JobInfo)
             */
            public void refreshJobInfo(JobInfo info) {
                int state = info.getJob().getState();
                if (state == Job.RUNNING) {
					addJob(info);
				} else {
					removeJob(info);
				}
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.ui.internal.progress.IJobProgressManagerListener#refreshAll()
             */
            public void refreshAll() {
                ProgressManager manager = ProgressManager.getInstance();
                jobs.clear();
                setAnimated(false);
                JobInfo[] currentInfos = manager.getJobInfos(showsDebug());
                for (int i = 0; i < currentInfos.length; i++) {
                    addJob(currentInfos[i]);
                }
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.ui.internal.progress.IJobProgressManagerListener#remove(org.eclipse.ui.internal.progress.JobInfo)
             */
            public void removeJob(JobInfo info) {
                decrementJobCount(info.getJob());
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.ui.internal.progress.IJobProgressManagerListener#showsDebug()
             */
            public boolean showsDebug() {
                return false;
            }

            private void incrementJobCount(JobInfo info) {
                //Don't count the animate job itself
                if (isNotTracked(info)) {
					return;
				}
                if (jobs.isEmpty()) {
					setAnimated(true);
				}
                jobs.add(info.getJob());
            }

            /*
             * Decrement the job count for the job
             */
            private void decrementJobCount(Job job) {
                jobs.remove(job);
                if (jobs.isEmpty()) {
					setAnimated(false);
				}
            }

            /**
             * If this is one of our jobs or not running then don't bother.
             */
            private boolean isNotTracked(JobInfo info) {
                //We always track errors
                Job job = info.getJob();
                return job.getState() != Job.RUNNING
                        || animationProcessor.isProcessorJob(job);
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.ui.internal.progress.IJobProgressManagerListener#addGroup(org.eclipse.ui.internal.progress.GroupInfo)
             */
            public void addGroup(GroupInfo info) {
                //Don't care about groups
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.ui.internal.progress.IJobProgressManagerListener#removeGroup(org.eclipse.ui.internal.progress.GroupInfo)
             */
            public void removeGroup(GroupInfo group) {
                //Don't care about groups
            }

            /*
             * (non-Javadoc)
             * 
             * @see org.eclipse.ui.internal.progress.IJobProgressManagerListener#refreshGroup(org.eclipse.ui.internal.progress.GroupInfo)
             */
            public void refreshGroup(GroupInfo info) {
                //Don't care about groups
            }
        };
    }

    /**
     * Get the preferred width for widgets displaying the animation.
     * 
     * @return int. Return 0 if there is no image data.
     */
    int getPreferredWidth() {
        return animationProcessor.getPreferredWidth();
    }

}
