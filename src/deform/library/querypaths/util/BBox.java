package deform.library.querypaths.util;


public final class BBox {

	public static BBox emptyBBox = new BBox(Interval.emptyInterval,
			Interval.emptyInterval);
	public static BBox everything = new BBox(Interval.everything,Interval.everything);

	public final Interval xInterval, yInterval;

	public BBox(double x, double y, double xr, double yd) {
		this(new Interval(x, xr), new Interval(y, yd));
	}

	public BBox(Interval xInterval, Interval yInterval) {
		this.xInterval = xInterval;
		this.yInterval = yInterval;
	}

	public boolean overlaps(BBox other) {
		return xInterval.overlapsWith(other.xInterval)
				&& yInterval.overlapsWith(other.yInterval);
	}

	public Vec getFarthestPoint(Vec p) {
		return new Vec(xInterval.getFarthestPoint(p.x),
				yInterval.getFarthestPoint(p.y));
	}

	public Vec getNearestPoint(Vec p) {
		return new Vec(xInterval.getClosestPoint(p.x),
				yInterval.getClosestPoint(p.y));
	}

	public double width() {
		return xInterval.length();
	}

	public double height() {
		return yInterval.length();
	}

	public double area() {
		return xInterval.length() * yInterval.length();
	}

	public boolean sidesSmallerThan(double d) {
		return width() <= d && height() <= d;
	}

	public double diagonalLengthSquared() {
		return width() * width() + height() * height();
	}

	public BBox intersections(BBox other) {
		return new BBox(xInterval.intersection(other.xInterval),
				yInterval.intersection(other.yInterval));
	}

	public BBox union(BBox other) {
		return new BBox(xInterval.union(other.xInterval),
				yInterval.union(other.yInterval));
	}

	public double avgDistSquared(Vec p) {
		return getMiddle().distanceSquared(p);
	}

	public Vec getMiddle() {
		return new Vec(xInterval.middle(), yInterval.middle());
	}

	public Vec getLeftUp() {
		return new Vec(xInterval.low, yInterval.low);
	}

	public Vec getLeftDown() {
		return new Vec(xInterval.low, yInterval.high);
	}

	public Vec getRightUp() {
		return new Vec(xInterval.high, yInterval.low);
	}

	public Vec getRightDown() {
		return new Vec(xInterval.high, yInterval.high);
	}
	
	public boolean isInsideExRightBoder(Vec p){
		return xInterval.isInside(p.x) && yInterval.isInside(p.y) 
				&& xInterval.high != p.x && yInterval.high != p.y; 
	}

	public boolean isInside(Vec p) {
		return xInterval.isInside(p.x) && yInterval.isInside(p.y);
	}

	public static BBox from2Points(Vec a, Vec b) {
		return new BBox(new Interval(a.x, b.x), new Interval(a.y, b.y));
	}

	public static BBox from3Points(Vec a, Vec b, Vec c) {
		return new BBox(new Interval(a.x, b.x, c.x),
				new Interval(a.y, b.y, c.y));
	}

	public static BBox from4Points(Vec a, Vec b, Vec c, Vec d) {
		return new BBox(new Interval(a.x, b.x, c.x, d.x), new Interval(a.y,
				b.y, c.y, d.y));
	}

	public String toString() {
		return String.format("[x:%s y:%s]", xInterval, yInterval);
	}

	public BBox quadTreeLeftUp() {
		return new BBox(xInterval.intSplitLeft(), yInterval.intSplitLeft());
	}

	public BBox quadTreeRightUp() {
		return new BBox(xInterval.intSplitRight(), yInterval.intSplitLeft());
	}

	public BBox quadTreeRightDown() {
		return new BBox(xInterval.intSplitRight(), yInterval.intSplitRight());
	}

	public BBox quadTreeLeftDown() {
		return new BBox(xInterval.intSplitLeft(), yInterval.intSplitRight());
	}

	public boolean encloses(BBox bBox) {
		return xInterval.encloses(bBox.xInterval)
				&& yInterval.encloses(yInterval);
	}

	public BBox getIntBBox() {
		return new BBox(xInterval.intIterval(), yInterval.intIterval());
	}

	public int getXInt() {
		return (int) xInterval.low;
	}

	public int getYInt() {
		return (int) yInterval.low;
	}

	public int getWidthInt() {
		return (int) Math.ceil(xInterval.high) - (int) xInterval.low;
	}

	public int getHeightInt() {
		return (int) Math.ceil(yInterval.high) - (int) yInterval.low;
	}

	public BBox grow(double width) {
		return new BBox(xInterval.grow(width), yInterval.grow(width));
	}

}
