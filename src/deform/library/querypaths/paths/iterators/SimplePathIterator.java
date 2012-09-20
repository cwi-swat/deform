package deform.library.querypaths.paths.iterators;

import deform.library.querypaths.paths.paths.QueryPath;

import deform.library.querypaths.paths.paths.simple.SimplePath;

public class SimplePathIterator extends PathIterator<SimplePath> {

	public static final PathSelect select = new PathSelect() {

		@Override
		public boolean select(QueryPath p) {
			return p instanceof SimplePath;
		}
	};

	public SimplePathIterator(QueryPath root) {
		super(select, root);
	}

}
