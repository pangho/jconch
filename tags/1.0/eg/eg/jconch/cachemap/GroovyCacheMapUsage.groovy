import jconch.cache.GroovyCacheMap

def cacheMap = new GroovyCacheMap() {a -> a * 2}
(1..5).each {i ->
    println cacheMap.get(i)
}
