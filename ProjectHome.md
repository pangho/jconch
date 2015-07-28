### Goal ###
The goal of the jconch project is to produce a reliable, safe set of implementations for common tasks in mulithreaded Java applications.  The goal is to be similar in spirit to the way that Spring scraps the boilerplate for common user tasks in database work.

### Current Functionality ###
  * **Logical Equivalence Locking** -- Both `synchronized` and [ReadWriteLock](http://java.sun.com/j2se/1.5.0/docs/api/java/util/concurrent/locks/ReadWriteLock.html)-based based on logical equivalence (#equals) instead of reference equality (==).
  * **Thread-safe Lazy Map** -- Good for caching look-ups in a multithreaded environment.  Use this to substantially improve performance in cases where repeated expensive look-ups are occurring.
  * **Fast, Thread-safe Multiple-Key Objects** -- For when storing one object as a key to a map just isn't enough.
  * **Easy Spring Integration** -- Architected with an eye towards making life easy for [Spring](http://www.springframework.org/) users.

### Might Someday Do List ###
(If you want this stuff, [vote for the issue](http://code.google.com/p/jconch/issues/list).)
  * **Generic Functors** -- Versions of [Commons-Collections functors](http://commons.apache.org/collections/api-release/index.html) that have type information.
  * **Naturally Multithreaded Producer/Consumer Chains** -- An easy way of representing distinct stages of a pipeline and configuring their threading model.
  * **Groovy-based Pipeline Builder** -- Because [Groovy's Builders](http://www.ibm.com/developerworks/java/library/j-pg04125/) are cool.

### Want to Keep Up-To-Date? ###
You can get notices when things happen with JConch by subscribing to the [JConch mailing list](http://groups.google.com/group/jconch).  Just send an e-mail to `jconch-subscribe@googlegroups.com`.  Messages will all be prepended with `[jconch]`, so it'll be easy to create a rule to handle them.
You can also subscribe to the [JConch RSS feed](http://feeds.feedburner.com/JConchGoogleGroup) if you don't feel like sharing your e-mail address.

### Requirements for Use ###
  * Java 1.5 or later
  * [Commons-Lang](http://commons.apache.org/lang/) and [Commons-Collections](http://commons.apache.org/collections/), which any respectable Java application should probably already have in play.  The specific versions used in development are provided in the "dep" archive (see [Downloads](http://code.google.com/p/jconch/downloads/list)).

### What's In a Name? ###
While the project started out as the Java CONCurrency Handler, it's taken on an additional meaning through an idiosyncracy.  The conch has been a symbol of deciding who gets a turn to speak in meetings, probably based on its use as such in 'Lord of the Flies'.  That seems very fitting for a concurrency system!

### What about `java.util.concurrent`? ###
This package requires a Java5 or greater JRE.  While Java5 added many low-level concurrency tools (which are used in this class), it does not provide 'grab-and-go' solutions for common concurrency problems.  For instance, while it provides [Semaphore](http://java.sun.com/j2se/1.5.0/docs/api/java/util/concurrent/Semaphore.html) and [CompletionService](http://java.sun.com/j2se/1.5.0/docs/api/java/util/concurrent/CompletionService.html), useful tools for development, it does not provide an actual producer/consumer framework.

### Who is Behind All This? ###
As of now, all development has been done by [Robert Fischer of Smokejumper Consulting](http://www.smokejumperit.com/).  If you've got tricky concurrency problems or just generally need some expertise on Java/Groovy development, drop him a line.