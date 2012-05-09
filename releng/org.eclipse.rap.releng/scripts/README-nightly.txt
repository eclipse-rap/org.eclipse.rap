Publish Nightly builds
======================

Since the Hudson build is not allowed to publish files onto the download server [1], the publish script is run by cron every
night at 23:50 server time on behalf of user rsternber.

To publish manually:

$ export RUNTIME_DIR=/shared/rt/rap/build-runtimes/eclipse-3.6.1
$ /shared/rt/rap/scripts/1.5/publishNightlyBuild.sh

[1] https://bugs.eclipse.org/bugs/show_bug.cgi?id=353581, comment 3.
