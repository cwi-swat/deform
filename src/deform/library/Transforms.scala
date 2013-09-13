package deform.library

import scala.math._
import java.awt.BasicStroke
import deform.library.querypaths.paths.factory.QueryPathFactory
import deform.library.querypaths.util.Vec
import deform.library.querypaths.paths.paths.QueryPath
import deform.library.querypaths.Sweep
import deform._
/** Functions for creating Transformations 
 */
object Transforms {

  /** Create a transformation given the forward and backwards transformation functions. */
    def transformation(forward : Point => Point, backward : Point => Point) : Transformation =
      FTransformation(forward,backward,None,None)
    /** Invert a transformation */
    def inverse(t : Transformation) : Transformation = t match {
      case FTransformation(f,b,_,_) => FTransformation(b,f,None,None)
      case AffineTransformation(f,b) => AffineTransformation(b,f)
      case _ => InverseTransformation(t,None,None)
    }
    def translate(p : Point) : Transformation = translate(p.x,p.y)
    def translate(xs : Double, ys : Double) : Transformation  = AffineTransformation(Matrix(1,0,xs,0,1,ys),Matrix(1,0,-xs,0,1,-ys))
    def scale(x : Double) : Transformation = scale(x,x)
    def scale(xs : Double, ys : Double) : Transformation  =  AffineTransformation(Matrix(xs,0,0,0,ys,0),Matrix(1/xs,0,0,0,1/ys,0))
	def rotate(angle : Double): Transformation = {
	    	val s = sin(angle);
			val c = cos(angle);
			AffineTransformation(Matrix(c, s, 0, -s, c, 0), Matrix(c, -s, 0, s, c, 0))
	}
    def shear(x : Double, y : Double) : Transformation=
     AffineTransformation(Matrix(1, x, 0, 1, y, 0), Matrix(1, -x, 0, 1, -y, 0))
    def transform(t : Transformation, r : Transformation) = t ** r ** inverse(t)
    
   /** A simple non-affine transformation */
    def wave : Transformation = FTransformation(
        p => Point(p.x + sin(p.y), p.y),
        p => Point(p.x - sin(p.y),p.y), Some(b => b.grow(1,0)),None)
    
  
  /** Functions for creating focus+context lenses */   
    object Lens {
      
           /** Create a focus+context lens transformation.
        * 
        *  @param norm The norm to be used
        *  @param profile The profile to be used
        *  @param profileDeriv The derivative of the profile function
        *  @param center The center of the lens
        *  @param innerRadius The inner radius
        *  @param outerRadius The outer radius
        *  @param zoom The magnification factor
        *  */
	    def focusContextLens(norm : Point => Double, 
	        profile : Double => Double, profileDeriv : Double => Double, center : Point,  innerRadius : Double, outerRadius : Double, zoom : Double ) : Transformation = { 
	        val border = outerRadius - innerRadius
	        val area = Util.makeBBox(center - Point(outerRadius,outerRadius), center + Point(outerRadius,outerRadius))
	        def fromNorm (d : Double) = d/ ((1- profile((d-innerRadius)/border)) * (zoom - 1) + 1)
	        def fromNormDerivative (d : Double) = {
				val frac = (d-innerRadius)/border;
				val prof = profile(frac);
				val lu = 1 / ((zoom-1) * (1 - prof) + 1);
				val lu2 = lu * lu;
				val q = d * (zoom-1) * profileDeriv(frac)/border;
				lu + lu2*q;
	        	}
	        val borderTo = outerRadius - innerRadius/zoom
			val innerdivMag = innerRadius/zoom
	        def toNorm (d : Double,scale : Double) = {
	          val initGuess = ((d - innerdivMag) /borderTo) * border + innerRadius
	          val res = Util.newtonInvert(d, fromNorm,fromNormDerivative, new Interval(innerdivMag,outerRadius),initGuess, DeformFunctions.numericError/(scale * 3))
	          res
	        }
	        val innerZoom = innerRadius / zoom
	        val innerZoomSquared = innerZoom * innerZoom
	        val innerSquared = innerRadius * innerRadius
	        val outerSquared = outerRadius * outerRadius
	      GetScaleTransformation( scale => 
	        (
	          p => {if(area.isInside(p)){
	        	  	val pp = p - center
	        	    val d = norm(pp)
	        	    
	          		 if(d <= innerZoom) pp*zoom + center
	                else if(d < outerRadius) {
	                  pp * (toNorm(d,scale)/ d) + center
	                }
	                else p
	          } else p
	          },
	          p => {if(area.isInside(p)){
	        	  	val pp = p - center
	        	    val d =  norm(pp)
	                if(d <= innerRadius) (pp/zoom) + center
	                else if(d < outerRadius) {
	                  (pp * (fromNorm(d)/d)) + center }
	                else p
	          } else p
	          }),
	          Some((b : AABBox) => if(area.overlaps(b)) area.union(b) else b),
	          Some((area,IdentityTransformation()))
	      )
	    }
	    /** The LP(2) Norm */
	    def euclidianNorm(x : Point) = x.norm
	        /** Synonym for the euclidian norm*/
	    def circleNorm(x:Point) = euclidianNorm(x)
	    /** The LP(Infinity) Norm, gives a rectangular shaped lens */
	    def rectNorm(x : Point) = max(abs(x.x) ,abs(x.y))
	    /** The LP(4) Norm, gives a circular, rectangular shaped lens */
	    def circleRectNorm(v : Point) = {
	    	val x2 = v.x * v.x;
			val y2 = v.y * v.y;
				 sqrt(sqrt(x2 * x2 + y2 * y2))
	    }
	        /** The LP(3) Norm, gives a rectangular, circular shaped lens */
	    def rectCircleNorm(v : Point) = {
	      pow(v.x * v.x * abs(v.x) + v.y * v.y * abs(v.y) , 1.0/3.0)
	    }
	    
	    private [deform] def id(x : Double) = x
	    private [deform] def one(x : Double) = 1
	    
	    def linearProfile : (Double=>Double,Double=>Double) = (x => x, x =>  1)
	    def quadraticProfile : (Double=>Double,Double=>Double) = (x => x * x, x => 2*x)
	    def sineProfile : (Double=>Double,Double=>Double) = {
	      val halfPi = 0.5 * Pi
	      (x => sin(x * halfPi), x => halfPi * cos(x* halfPi))
	    }
	    def gaussianProfile : (Double=>Double,Double=>Double) = {
	      val end = exp(-4);
	      val  interval = 1 - end;
		  ( d => 1 - (exp(-(d*d)*4) - end) /interval,
		    d => 8 * d / interval * exp(-4 * d *d)) 
	    }
           /** Create a focus+context lens transformation.
        * 
        *  @param norm The norm to be used
        *  @param profile A tuple of the profile function and its derivative 
        *  @param center The center of the lens
        *  @param innerRadius The inner radius
        *  @param outerRadius The outer radius
        *  @param zoom The magnification factor
        *  */
	    def lens(norm : Point => Double, profile : (Double=>Double,Double=>Double), center : Point, innerRadius : Double, outerRadius : Double, zoom : Double) : Transformation = {
	      focusContextLens(norm, profile._1, profile._2, center, innerRadius, outerRadius, zoom)
	    }
    } 
   
      /** A sweep transformation along the given path */   
    def sweep( p : Path) : Transformation = {
      var lastScale = 0.0
      var lastSweep = null : Sweep
      def trans(sweep : Sweep) = {
        FTransformation(
                pp => { 
                  val p = if(pp.x < 0) Point(0,pp.y) else if(pp.x > 1) Point(1,pp.y) else pp
                  val v = sweep.to(p)
                  new Point(v.x,v.y) /lastScale
                }
                , p => {
                  val v = sweep.from(p * lastScale)
                  new Point(v.x,v.y)
                }
                , p.bbox match {
                  case Some(l) => Some(b => l.grow(b.height)) 
                  case _ => None
                },
                None)
          }
      AnalyzePathTransformation(p, 
          (scale,l) => { 
            if(scale == lastScale) {
              trans(lastSweep)
            } else {
              lastScale = scale
              lastSweep = new Sweep(Paths.toQueryPath(scale,l))
              trans(lastSweep)
            }
          }
          ,p.bbox match {
             case Some(l) => Some(b => l.grow(b.height)) 
             case _ => None
          },
          None)
            
                  
    
    }
    
  /** A simple movable camera for creating google-maps like stuff */   
class CameraScala {
  
  /** The current transformation */
  var trans = IdentityTransformation() : Transformation
    /** The current magnification factor */
  var zoomd = 1 : Double
  private var shrink = 1 : Double
  private var leftTop = Point(0,0)
  private var ref = null : Point
  
  /** Zoom in on a point in the world 
   * 
   * @param zoomOnWorld The point on which to zoom
   * @param zoomDelta How much to zoom
   */
  def zoom(zoomOnWorld : Point, zoomDelta :Double ) ={
    val factor = 1/zoomDelta
    val zoomLocal = zoomOnWorld * shrink
    leftTop = leftTop + zoomLocal * (1- factor) 
    shrink = shrink * factor
    zoomd = zoomd * zoomDelta
    trans = scale(zoomd) ** translate(leftTop.negate)
  }
  /** Move the camera */
  def move(delta : Point) = {
    leftTop = leftTop + delta * shrink
    trans = scale(zoomd) ** translate(leftTop.negate)
  }
  
  

}
}