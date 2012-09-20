package deform.library.querypaths.paths.iterators;

import deform.library.querypaths.paths.paths.QueryPath;

import deform.library.querypaths.paths.paths.simple.Line;

public class LineIterator extends PathIterator<Line> {
	public static final PathSelect select = new PathSelect() {

		@Override
		public boolean select(QueryPath p) {
			return p instanceof Line;
		}
	};

	public LineIterator(QueryPath root) {
		super(select, root);
	}
}
