# Introduction #

You've got a version number.  What's that mean?


# Overview #
Incrementing the version numbers have a pretty strict meaning on the JConch project.
  * **All Versions Pre-1.0** -- Releases were pretty willy-nilly, with a lot of experimentation going on.
  * **Major Releases** -- Tested and ready to go, with no guaranty of any backward compatibility to the previous major release series.
  * **x.9 Minor Release** -- The release candidate for the next major release.
  * **Minor Releases < x.9** -- New API may be introduced, and warning-inducing changes to the code base (incl. deprecation) may be introduced.
  * **Point Releases** -- No API changes: bugfixes only.


# Changelog #
  * **1.1** -- Added concurrent testing utilities Assert#assertSynchronized and SerialExecutorService. Updated documentation.
  * **1.0** -- Moved pipelines over to the sandbox, and got the unit tests to where they should be.
  * **0.3** -- Implemented typing for the CacheMap, with the untyped version being relegated to ObjectCacheMap.  Also implemented the GroovyCacheMap for one-line groovytastic gold.  See more [at the EnfranchisedMind post](http://enfranchisedmind.com/blog/2008/03/14/jconch-03-released/)