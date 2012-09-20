package deform.library.querypaths.paths.paths.compound;

import static deform.library.querypaths.paths.results.transformers.TupleTransformers.setRight;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import deform.library.querypaths.paths.paths.PathIndex;
import deform.library.querypaths.paths.paths.QueryPath;
import deform.library.querypaths.paths.paths.SplittablePath;
import deform.library.querypaths.paths.paths.simple.Line;
import deform.library.querypaths.paths.results.intersections.IIntersections;
import deform.library.querypaths.paths.results.intersections.Intersections;
import deform.library.querypaths.paths.results.project.BestProject;
import deform.library.querypaths.paths.results.project.BestProjectTup;
import deform.library.querypaths.paths.results.transformers.PITransformers;
import deform.library.querypaths.util.BBox;
import deform.library.querypaths.util.Tuple;
import deform.library.querypaths.util.Vec;



public class ShapeSet extends QueryPath {

	public final List<QueryPath> shapes;

	public ShapeSet(List<QueryPath> shapes) {
		this.shapes = shapes;
	}

	public ShapeSet(QueryPath[] shapes) {
		this(Arrays.asList(shapes));
	}

	@Override
	public BBox makeBBox() {
		BBox total = BBox.emptyBBox;
		for (QueryPath p : shapes) {
			total = total.union(p.getBBox());
		}
		return total;
	}

	@Override
	public Vec getAt(PathIndex t) {
		return shapes.get(((SetIndex) t).choice).getAt(t.next);
	}

	@Override
	public Vec getTangentAt(PathIndex t) {
		return shapes.get(((SetIndex) t).choice).getTangentAt(t.next);
	}

	@Override
	public IIntersections intersection(QueryPath other) {
		return other.intersectionLSet(this);
	}

	private IIntersections intersections(QueryPath lhs) {
		IIntersections res = Intersections.NoIntersections;
		if (lhs.getBBox().overlaps(getBBox())) {
			for (int i = 0; i < shapes.size(); i++) {
				QueryPath p = shapes.get(i);
				res = res.append(lhs.intersection(p).transform(setRight(i)));
			}
		}
		return res;
	}

	@Override
	public IIntersections intersectionLLine(Line lhs) {
		return intersections(lhs);
	}

	@Override
	public IIntersections intersectionLSet(ShapeSet lhs) {
		return intersections(lhs);
	}

	@Override
	public IIntersections intersectionLSplittable(SplittablePath lhs) {
		return intersections(lhs);
	}

	@Override
	public BestProject project(double best, Vec p) {
		if (getBBox().getNearestPoint(p).distanceSquared(p) > best) {
			return BestProject.noBestYet;
		}
		BestProject res = new BestProject(best);
		for (int i = 0; i < shapes.size(); i++) {
			QueryPath path = shapes.get(i);
			res = res.choose(path.project(res.distSquared, p).transform(
					PITransformers.setTrans(i)));
		}
		return res;
	}

	@Override
	public BestProjectTup project(double best, QueryPath other) {
		return other.projectLSet(best, this);
	}

	private BestProjectTup projects(double best, QueryPath lhs) {

		BestProjectTup res = new BestProjectTup(best);
		if (minDistTo(lhs.getBBox()) < best) {
			for (int i = 0; i < shapes.size(); i++) {
				QueryPath p = shapes.get(i);
				res = res.choose(lhs.project(best, p).transform(setRight(i)));
			}
		}
		return res;
	}

	@Override
	public BestProjectTup projectLLine(double best, Line lhs) {
		return projects(best, lhs);
	}

	@Override
	public BestProjectTup projectLSet(double best, ShapeSet lhs) {
		return projects(best, lhs);
	}

	@Override
	public BestProjectTup projectLSplittable(double best, SplittablePath lhs) {
		if (minDistTo(lhs.getBBox()) > best) {
			return BestProjectTup.noBestYet;
		}
		// each shape is bigger that half the split right (heurisitic),
		// lets iterate
		if (getBBox().area() / shapes.size() > lhs.getBBox().area() / 2.0) {
			return projects(best, lhs);
		} else {
			Tuple<QueryPath, QueryPath> sp = lhs.splitSimpler();
			if (sp.l.getBBox().avgDistSquared(getBBox().getMiddle()) < sp.l
					.getBBox().avgDistSquared(getBBox().getMiddle())) {
				BestProjectTup res = sp.l.project(best, this).transform(
						lhs.getLeftLeftTransformer());
				return res.choose(sp.r.project(res.distSquared, this)
						.transform(lhs.getLeftRightTransformer()));
			} else {
				BestProjectTup res = sp.r.project(best, this).transform(
						lhs.getLeftRightTransformer());
				return res.choose(sp.l.project(res.distSquared, this)
						.transform(lhs.getLeftLeftTransformer()));
			}
		}

	}

	public int nrChildren() {
		return shapes.size();
	}

	public QueryPath getChild(int i) {
		return shapes.get(i);
	}

	public boolean isClosed() {
		return false;
	}

	public String toString() {
		StringBuilder build = new StringBuilder();
		build.append("ShapeSet(");
		for (QueryPath p : shapes) {
			build.append(p.toString());
			build.append("\n, ");
		}
		build.append(")");
		return build.toString();
	}

	@Override
	public Tuple<QueryPath, Double> normaliseToLength(double prevLength) {
		throw new Error("Cannot length normalise set!");
	}

	@Override
	public PathIndex minPathIndex() {
		throw new Error("Shape does not have begin nor end!");
	}

	@Override
	public PathIndex maxPathIndex() {
		throw new Error("Shape does not have begin nor end!");
	}

}
