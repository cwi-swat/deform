package deform.library.querypaths.paths.results.intersections;

import java.util.Iterator;

import deform.library.querypaths.paths.paths.PathIndex;
import deform.library.querypaths.paths.results.transformers.PathIndexTupleTransformer;
import deform.library.querypaths.util.BBox;
import deform.library.querypaths.util.Vec;





public class Intersections implements IIntersections, Iterable<Intersection> {
	
	public static final Intersections NoIntersections = new Intersections((Intersection)null, null);
	
	private final Intersection first;
	private final Intersection last;
	
	public Intersections(PathIndex l, PathIndex r,Vec locl, Vec locr, Vec tanl, Vec tanr, IntersectionType typel, IntersectionType typer){
		this.first = new Intersection(l,r,locl, locr, tanl, tanr, typel, typer);
		this.last = first;
	}
	
	public Intersections(Intersection first, Intersection last) {
		this.first = first;
		this.last = last;
	}
	
	public Iterator<Intersection> iterator(){
		return new IntersectionsIterator(first);
	}
	
	
	@Override
	public IIntersections appendNorm(Intersections lhs) {
		if(lhs.first == null){
			return this;
		}
		if(first == null){
			return lhs;
		}
		lhs.last.next = first;
		return new Intersections(lhs.first, last);
	}
	
	@Override
	public IIntersections appendFlipped(FlippedIntersections lhs) {
		return appendNorm(lhs.normalise());
	}
	
	@Override
	public IIntersections flip() {
		if(first == null){
			return (this);
		} else {
			return new FlippedIntersections(this);
		}
	}
	
	
	private static class IntersectionsIterator
		implements Iterator<Intersection>{

		Intersection cur;
		
		IntersectionsIterator(Intersection first){
			cur = first;
		}
		@Override
		public boolean hasNext() {
			return cur != null;
		}

		@Override
		public Intersection next() {
			Intersection res = cur;
			cur = cur.next;
			return res;
		}

		@Override
		public void remove() {
			throw new Error("Not implemented!@");
		}
		
	}


	@Override
	public IIntersections transform(
			PathIndexTupleTransformer trans) {
		if(trans.doesNothing || first == null){
			return this;
		} else {
			Intersection firstNew = first.transform(trans);
			Intersection prev = firstNew;
			Intersection cur = first.next;
			while(cur != null){
				Intersection curn = cur.transform(trans);
				prev.next = curn;
				prev = curn;
				cur = cur.next;
			}
			return new Intersections(firstNew,prev);
		}
	}

	@Override
	public IIntersections append(IIntersections r) {
		return r.appendNorm(this);
	}
}
