package deform 

import scala.math._

/**
 * Values should be in the range [0,1]
 */
case class Color(red: Double, green : Double , blue : Double, alpha : Double){
  def clamp(r : Double) = if (r < 0) 0 else (if(r > 1) 1 else r)
  def add(rhs : Color) = 
      if(alpha + rhs.alpha > 1.0){
        val nalpha = 1.0 - rhs.alpha
         Color(clamp(red * nalpha + rhs.red),clamp(green * nalpha + rhs.green),clamp(blue* nalpha  + rhs.blue),1.0)
      } else 
         Color(clamp(red + rhs.red),clamp(green + rhs.green),clamp(blue + rhs.blue),clamp(alpha + rhs.alpha))
   
  def *(op:Double) = {
    clamp(op)
    Color(red*op,green*op,blue*op, alpha*op)
  }
  
  def mulNoAlpha(op : Double) = {
     clamp(op)
    Color(red*op,green*op,blue*op, alpha)
  }
  def lerp(ct : Double, rhs : Color) = {
    val t = clamp(ct)
    val lt = 1.0 -t
    Color(red * lt + rhs.red * t, green * lt + rhs.green * t,blue * lt + rhs.blue * t,alpha * lt + rhs.alpha * t)
  }
  
  def lerpNoAlpha(t : Double, rhs : Color) = {
    val lt = 1.0 -t
    Color(red * lt + rhs.red * t, green * lt + rhs.green * t,blue * lt + rhs.blue * t,1.0)
  }
}


case class Point(x : Double, y : Double){
  def ==(op : Point) = x == op.x && y == op.y
  def +(op:Point) = Point(x+op.x,y+op.y)
  def -(op:Point) = Point(x-op.x,y-op.y)
  def *(op:Double) = Point(x*op,y*op)
  def /(op:Double) = Point(x/op,y/op)
  def dot(op : Point) = x * op.x + y * op.y 
  def normSquared = x*x + y * y
  def negate = Point(-x,-y)
  def norm = sqrt(normSquared);
  def normalized = this / norm
  def lerp(i :Double, rhs : Point) = {
    val li = 1 - i
    Point(li * x + i * rhs.x, li * y + i * rhs.y)
  }
  def distanceSquared(rhs : Point) = (rhs - this).normSquared
  def distance(rhs : Point) = (rhs - this).norm
}

/** Represents the interval [low,high] (inclusive,inclusive).
 * 
 */
case class Interval(low : Double, high : Double){
  def length = high - low
  def union(rhs : Interval) = new Interval(min(low,rhs.low),max(high,rhs.high))
  def intersect(rhs : Interval) = new Interval(max(low,rhs.low),min(high,rhs.high))
  def boundTo(x : Double) = max(low,min(x,high))
  def empty() = low > high
  def inside(x : Double) = x >= low && x <= high 
  def overlaps(rhs : Interval) = !(rhs.high < low || rhs.low > high)
  def middle = (low + high)/2
  def grow(d : Double) = Interval(low-d,high+d)
}

/** An Axis-Aligned Bounding Box.
 *  
 */
case class AABBox(xInterval : Interval, yInterval : Interval){
  def isInside(p : Point) = xInterval.inside(p.x) && yInterval.inside(p.y)
  def union(rhs : AABBox) = new AABBox(xInterval union rhs.xInterval, yInterval union rhs.yInterval)
  def intersect(rhs : AABBox) = new AABBox(xInterval intersect rhs.xInterval, yInterval intersect rhs.yInterval)
  def overlaps(rhs : AABBox) = (xInterval overlaps rhs.xInterval) && (yInterval overlaps rhs.yInterval)
  def width = xInterval.length
  def widthInt = width.toInt
  def height = yInterval.length
  def heightInt = height.toInt
  def left = Point(xInterval.low,yInterval.middle)
  def right = Point(xInterval.high,yInterval.middle)
  def up = Point(xInterval.middle, yInterval.low)
  def down = Point(xInterval.middle,yInterval.high)
  def leftUp = Point(xInterval.low, yInterval.low)
  def rightUp = Point(xInterval.high, yInterval.low)
  def leftDown = Point(xInterval.low, yInterval.high)
  def rightDown = Point(xInterval.high, yInterval.high)
  def grow(x : Double, y : Double) : AABBox= AABBox(xInterval.grow(x),yInterval.grow(y))
  def grow(x : Double) : AABBox= grow(x,x)
}



 object Util{
  private[deform] def modulo(i:Int,m:Int) = {
    val j = i % m
    if(j < 0) m - j else j 
  }
  def unitBBox = AABBox(unitInterval,unitInterval)
  def emptyInterval = Interval(1,0)
  def unitInterval = Interval(0,1)
  def emptyBBox = AABBox(emptyInterval,emptyInterval)
  def makeInterval(a : Double, b : Double) : Interval = 
    if(a <= b) Interval(a,b) else Interval(b,a)
  def makeInterval(a : Double, b : Double, c : Double) : Interval =
    makeInterval(a,b) union makeInterval(b,c)
   def makeInterval(a : Double, b : Double, c : Double,d : Double) : Interval =
     makeInterval(a,b) union makeInterval(c,d)
  def makeBBox(a : Point, b :Point) = AABBox(makeInterval(a.x,b.x),makeInterval(a.y,b.y))
  def makeBBox(a : Point, b :Point, c : Point) = AABBox(makeInterval(a.x,b.x,c.x),makeInterval(a.y,b.y,c.y))
  def makeBBox(a : Point, b :Point, c : Point, d : Point) = AABBox(makeInterval(a.x,b.x,c.x,d.x),makeInterval(a.y,b.y,c.y,d.y))
  
  private[deform] def newtonInvert(d : Double, func : Double => Double, funcDeriv : Double => Double, interval : Interval,initGuess : Double, error : Double) = {
      var guess = interval.boundTo(initGuess)
      var off = func(guess) - d
      var i = 0;
      while(abs(off) > error) {
        
        val deriv = funcDeriv(guess);
	    guess = interval.boundTo(guess - off / deriv);
		off = func(guess) -d;
		i+=1;
		if (i > 100) {
		  throw new Error("Not converging!" ++ error.toString() ++ " " ++ off.toString())
		}
      }
      guess
    } 
}
