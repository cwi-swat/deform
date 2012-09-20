package deform.library.querypaths.paths.results.intersections;


import deform.library.querypaths.paths.paths.PathIndex;
import deform.library.querypaths.paths.results.transformers.PathIndexTupleTransformer;
import deform.library.querypaths.util.BBox;
import deform.library.querypaths.util.Vec;

public class Intersection{

	public final PathIndex left;
	public final PathIndex right;
	public final Vec locl;
	public final Vec locr;
	public final Vec tanl;
	public final Vec tanr;
	public final IntersectionType typel, typer;
	Intersection next;
	
	public Intersection(PathIndex left, PathIndex right, Vec locl, Vec locr, Vec tanl, Vec tanr, IntersectionType typel, IntersectionType typer){
		this(left,right,locl,locr,tanl,tanr,typel, typer, null);
	}
	
	public Intersection(PathIndex left, PathIndex right, Vec locl, Vec locr, Vec tanl, Vec tanr, IntersectionType typel, IntersectionType typer, Intersection next) {
		this.next = next;
		this.left = left;
		this.right = right;
		this.locl = locl;
		this.locr = locr;
		this.tanl = tanl;
		this.tanr = tanr;
		this.typel = typel;
		this.typer = typer;
	}

	public
		Intersection transform(PathIndexTupleTransformer trans){
		return new Intersection(trans.left.transform(left),
										  trans.right.transform(right),locl,locr,tanl,tanr,typel,typer);	
	}
	
	public Intersection flip(){
		return new Intersection(right, left,locr,locl,tanr,tanl,typer, typel);
	}
	
	public String toString(){
		return "Intersection(" + typel + " " + typer + " " + left.toString() + "\n\t" + right.toString() + ")";
	}



	
}
