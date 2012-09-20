package deform.library.querypaths;

import java.util.ArrayList;
import java.util.List;

import deform.library.querypaths.paths.iterators.SimplePathIterator;
import deform.library.querypaths.paths.paths.PathIndex;
import deform.library.querypaths.paths.paths.QueryPath;
import deform.library.querypaths.paths.results.project.BestProject;
import deform.library.querypaths.util.*;
import deform.library.querypaths.paths.paths.simple.Line;
import deform.library.querypaths.paths.paths.simple.SimplePath;
import deform.library.querypaths.util.Tuple;

class SweepTo{

	final List<LineInfo> lines;
	final List<Double> lengths;

	static class LineInfo {
		Line line;
		Vec normal;
		TriangleCoordinateSystem coord;

		public LineInfo(Line line) {
			this.line = line;
			this.normal = line.dir.tanToNormal().normalize();
		}

		void makeCoord(Vec prevNorm, Vec nextNorm) {
			this.coord = TriangleCoordinateSystem.create(line, prevNorm,
					normal, nextNorm);
		}
	}

	SweepTo(List<LineInfo> lines, List<Double> lengths) {
		this.lines = lines;
		this.lengths = lengths;
	}

	SweepTo(QueryPath p) {
		this.lines = makeLines(p);
		lengths = makeLengths();
		makeCoords(p);

	}

	private List<Double> makeLengths() {
		List<Double> lengths = new ArrayList<Double>(lines.size());
		lengths.add(0.0);
		for (LineInfo l : lines) {
			lengths.add(l.line.tInterval.high);
		}
		return lengths;
	}

	private void makeCoords(QueryPath p) {
		if (lines.size() == 1) {
			lines.get(0).makeCoord(lines.get(0).normal, lines.get(0).normal);
			return;
		}
		if (p.isClosed()) {
			lines.get(0).makeCoord(lines.get(lines.size() - 1).normal,
					lines.get(1).normal);
		} else {
			lines.get(0).makeCoord(lines.get(0).normal, lines.get(1).normal);
		}
		for (int i = 1; i < lines.size() - 1; i++) {
			lines.get(i).makeCoord(lines.get(i - 1).normal,
					lines.get(i + 1).normal);

		}
		if (p.isClosed()) {
			lines.get(lines.size() - 1).makeCoord(
					lines.get(lines.size() - 2).normal, lines.get(0).normal);
		} else {
			lines.get(lines.size() - 1).makeCoord(
					lines.get(lines.size() - 2).normal,
					lines.get(lines.size() - 1).normal);
		}
	}

	private List<LineInfo> makeLines(QueryPath p) {
		List<LineInfo> lines = new ArrayList<LineInfo>();
		SimplePathIterator it = new SimplePathIterator(p);
		while (it.hasNext()) {
			makeAndExpandLines(lines, it.next());
		}
		return lines;
	}
	
	Vec to(Vec d) {
		int start = BinarySearches.floorBinarySearch(lengths, d.x);
		if(start < 0){
			start = 0;
		} 
		if(start >= lines.size()){
			start = lines.size()-1;
		}
		return lines.get(start).coord.getAt(d);
		
	}

	private void makeAndExpandLines(List<LineInfo> lines, SimplePath next) {
		if (next instanceof Line) {
			lines.add(new LineInfo((Line) next));
		} else {
			Tuple<QueryPath, QueryPath> simp = next.splitSimpler();
			makeAndExpandLines(lines, (SimplePath) simp.l);
			makeAndExpandLines(lines, (SimplePath) simp.r);
		}

	}
}
