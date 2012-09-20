package deform.library.querypaths.paths.results.intersections;

import java.util.Iterator;

import deform.library.querypaths.paths.results.transformers.PathIndexTupleTransformer;



public class FlippedIntersections implements IIntersections {
	
	final Intersections real;

	public FlippedIntersections(Intersections real) {
		this.real = real;
	}

	Intersections normalise(){
		Iterator<Intersection> it = real.iterator();
		if(!it.hasNext()){
			return Intersections.NoIntersections;
		} else {
			Intersection cur = it.next();
			Intersection first = cur.flip();
			Intersection prev = first;
			while(it.hasNext()){
				cur = it.next();
				prev.next = cur.flip();
				prev = prev.next;
			}
			return new Intersections(first, prev);
		}
	}

	@Override
	public IIntersections appendFlipped(FlippedIntersections lhs) {
		return new FlippedIntersections((Intersections) real.appendNorm(lhs.real));
	}

	@Override
	public IIntersections appendNorm(Intersections r) {
		return normalise().append(r);
	}

	@Override
	public Iterator<Intersection> iterator() {
		return normalise().iterator();
	}

	

	@Override
	public IIntersections flip() {
		return real;
	}

	@Override
	public  IIntersections transform(
			PathIndexTupleTransformer trans) {
		if(trans.doesNothing){
			return this;
		} else {
			return normalise().transform(trans);
		}
	}

	@Override
	public IIntersections append(IIntersections r) {
		return r.appendFlipped(this);
	}



}
