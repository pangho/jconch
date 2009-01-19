
import jconch.testing.Assert
import java.util.concurrent.Callable

Assert.assertSynchronized(
  {
    def target = new Vector()   //fail if ArrayList 
    return [
      { (0..1000).each { target.add(new Object()) }  }  as Callable, 
      { (0..1000).each { target.add(new Object()) }  }  as Callable, 
    ]
  } as Callable
)

println "Success"

