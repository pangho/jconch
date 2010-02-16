package jconch.functor;

import com.google.common.base.Function;

/**
* Originally used to implement transforms in a typesafe manner.  Now {@link Function} is the preferred 
* way to do this.  This used to be an interface&mdash;if you previously <code>implements</code> this,
* instead <code>extends</code> this or <code>implements</code> {@link Function}.  If you used this as
* an anonymous inner class, your code should still compile (albeit with deprecation warnings).
*/
@Deprecated
public abstract class Transformer5<IN_T, OUT_T> implements Function<IN_T, OUT_T> {

  public abstract OUT_T transform(IN_T type);

  public final OUT_T apply(IN_T from) {
    return transform(from);
  }

}
