package deform
import scala.math._
import java.io.File
import java.awt.geom.AffineTransform
import deform.library._

private[deform] case class Matrix(x1 : Double, x2 : Double, x3 : Double, y1 : Double, y2 : Double, y3 : Double) {
 def rmul(r:Matrix) = Matrix(r.x1 * x1 + r.y1 * x2, r.x2 * x1 + r.y2 * x2, r.x3* x1 + r.y3 * x2 + x3,
		 				r.x1 * y1 + r.y1 * y2, r.x2 * y1 + r.y2 * y2, r.x3 * y1 + r.y3 * y2+ y3)
 def *(r : Matrix) = r.rmul(this)
 def *(v : Point) = Point(x1 * v.x + x2 * v.y + x3 , y1 * v.x + y2* v.y + y3 )	
 def isNotSquashed = Point(x1,x2).normSquared == Point(y1,y2).normSquared
 def isTranslation = x1 == 1 && x2 == 0 && y1 == 0 && y2 == 1
}

abstract class Path{
  private[deform] def toFPath : FPath
  def bbox : Option[AABBox]
  def |(op : Transformation) = TransformPath(op,this)
}

private[deform] case class FPath(fun : Double => Point, bbox : Option[AABBox]) extends Path {
  def toFPath = this
}

/**
 * A path that is a Bezier curve. I.e. a path that can be analyzed.
 */
abstract class ConcreteSegment extends Path{
  def start : Point
  private[deform] def map(f : Point => Point) : ConcreteSegment
}

case class Line(start : Point, end : Point) extends ConcreteSegment{
    private[deform] def toFPath = FPath(t => start.lerp(t,end),bbox)
    private[deform] def map(f : Point => Point) = Line(f(start),f(end))
    def bbox = Some(Util.makeBBox(start,end))
}

/**
 * Quadratic Bezier curve.
 */

case class Quad(start : Point, control : Point, end : Point) extends ConcreteSegment{
    private[deform] def toFPath = FPath(t => {
      val ot = 1.0 -t;
	  val t2 = t * t;
	  val ot2 = ot * ot;
	  Point(start.x * ot2 + 2 * ot * t * control.x + t2 * end.x,
			start.y * ot2 + 2 * ot * t * control.y + t2 * end.y);
    },bbox)
    def map(f : Point => Point) = Quad(f(start),f(control),f(end))
    def bbox =  Some(Util.makeBBox(start,control,end))
}

/**
 * Cubic Bezier curve.
 */
case class Cubic(start : Point, controll : Point, controlr : Point, end : Point) extends ConcreteSegment{
   def toFPath = FPath(t => {
      val rt = 1.0 - t;
	  val rt2 = rt*rt;
	  val rt3 = rt2*rt;
	  val t2 = t*t;
	  val t3 = t*t2;
	  val c3rt2t = 3*rt2*t;
	  val c3rtt2 = 3*rt*t2;
		Point(rt3*start.x + c3rt2t*controll.x + c3rtt2*controlr.x + t3*end.x,
			  rt3*start.y + c3rt2t*controll.y + c3rtt2*controlr.y + t3*end.y);
    }, bbox)
    def map(f : Point => Point) = Cubic(f(start),f(controll),f(controlr),f(end))
    def bbox =  Some(Util.makeBBox(start,controll,controlr,end))
}
private[deform] case class Join(l : Path, r : Path) extends Path {
   def toFPath = {
     val fl = l.toFPath
     val fr = r.toFPath
     FPath(t => if(t <= 0.5) fl.fun(t * 2) else fr.fun((t - 0.5) *2), bbox)
   }
   def bbox = (l.bbox,r.bbox) match {
     case (Some(lb),Some(rb)) => Some(lb.union(rb))
     case _ => None
   }
}

private[deform] case class TransformPath(t : Transformation, p : Path) extends Path{
   def toFPath = throw new Error("not normalized!")
   def bbox = (p.bbox,t.transformBBox) match {
     case (Some(b),Some(tb)) => Some(tb(b))
     case _ => None
   }
}

private[deform] case class AnalyzePathPath(p : Path, analysis :  (Double,List[ConcreteSegment]) => Path, bbox : Option[AABBox]) extends Path {
   def toFPath = throw new Error("not normalized!")
}

private[deform] case class AnalyzeShapePath(s : Shape, analysis :  (Double,ShapeSetOperation,List[List[ConcreteSegment]]) => Path, bbox : Option[AABBox]) extends Path {
   def toFPath = throw new Error("not normalized!")
}

private[deform] abstract class ShapeSetOperation
private[deform] case class Union extends ShapeSetOperation
private[deform] case class Symdiff extends ShapeSetOperation

 abstract class Shape {
  private[deform] def toOpShape : OpShape
  def |(op : Transformation) = TransformShape(op,this)
  def bbox : Option[AABBox]
}
private[deform] case class OpShape(op : ShapeSetOperation, cpaths : List[Path]) extends Shape{
  def toOpShape = this
  def bbox = {
    var list = cpaths.tail
    var res = cpaths.head.bbox
    while(!list.isEmpty && res != None) {
      res = (list.head.bbox,res) match {
        case (Some(b),Some(r)) => Some(r.union(b))
        case _ => None
      }
      list = list.tail
    }
    res
  }
}

private[deform] case class AnalyzePathShape(p : Path, analysis :  (Double,List[ConcreteSegment]) => Shape, bbox : Option[AABBox]) extends Shape {
   def toOpShape = throw new Error("not normalized!")
}

private[deform] case class AnalyzeShapeShape(s : Shape, analysis :  (Double,ShapeSetOperation,List[List[ConcreteSegment]]) => Shape, bbox : Option[AABBox]) extends Shape {
   def toOpShape = throw new Error("not normalized!")
}

private[deform] case class TransformShape(t : Transformation, s : Shape) extends Shape {
  def toOpShape = throw new Error("Not normalized!")
  def bbox =
    (t.transformBBox,s.bbox) match {
    case (Some(tb),Some(b)) => Some(tb(b))
    case _ => None
  }
}

private[deform] case class InverseAffTransformedShape(t : AffineTransformation, s : Shape) extends Shape{
    def toOpShape = throw new Error("Not normalized!")
    def bbox = (Transforms.inverse(t).transformBBox,s.bbox) match {
    case (Some(tb),Some(b)) => Some(tb(b))
    case _ => None
  }
}

abstract class Texture{
  def |(op : Transformation) = TransformTexture(op,this)
  private[deform] def toFTexture : FTexture
}
private[deform] case class FTexture(fun : Point => Color, par : Option[Int] ) extends Texture {
    def toFTexture = this
}
private[deform] case class TransformTexture(t : Transformation, tex : Texture) extends Texture{
   def toFTexture = throw new Error("not normalized!")
}

private[deform] case class AnalyzePathTexture(p : Path, analysis :  (Double,List[ConcreteSegment]) => Texture) extends Texture {
   def toFTexture = throw new Error("not normalized!")
}

private[deform] case class AnalyzeShapeTexture(s : Shape, analysis :  (Double,ShapeSetOperation,List[List[ConcreteSegment]]) => Texture, bbox : Option[AABBox]) extends Texture {
   def toFTexture = throw new Error("not normalized!")
}

private[deform] abstract class NativeTexture extends Texture 
private[deform] case class AffTransformedNativeTexture(t : AffineTransformation, ntex : NativeTexture) extends NativeTexture{
     def toFTexture = throw new Error("should not normalize!")
}

private[deform] case class FillColor(color : Color) extends NativeTexture {
       def toFTexture = throw new Error("should not normalize!")
}

private[deform] abstract class LinearGradient(fracColors : List[Tuple2[Double,Color]]) extends NativeTexture{
  def getColor(frac : Double) = 
    if(frac == 1.0) fracColors.last._2
    else {
    	val lsi = fracColors.indexWhere(p => p._1 > frac)
    	val ls = fracColors.drop(lsi-1)
    	val first = ls.head
    	val second = ls.tail.head
    	first._2.lerp((frac - first._1) / (second._1 - first._1),second._2) 
    }
    
}

/** The cycle method for gradients */
abstract class CycleMethod {
  private[deform] def getFrac(f : Double) : Double
}
case class NoCycle extends CycleMethod{
    def getFrac(f : Double)  = if(f > 1.0) 1.0 else( if(f < 0.0) 0.0 else f)
}

case class Reflect extends CycleMethod{
  def getFrac(f : Double)  =  if (f.toInt % 2 == 0) f - f.toInt else 1 - (f - f.toInt)
      
}

case class Repeat extends CycleMethod{
  def getFrac(f: Double) = f - f.toInt
}
private[deform] case class LineLinearGradient(line : Line, val fracColors : List[Tuple2[Double,Color]], cycle: CycleMethod) extends LinearGradient(fracColors){
    def toFTexture = {
      val dir = line.end - line.start
      val length = dir.norm
      val dirNormalized = dir / length
      FTexture(x => 
        getColor(cycle.getFrac((x - line.start).dot(dirNormalized)/length)), None)
    }
}

private[deform] case class LinearRadialGradient(center : Point, focusc : Point, radius : Double, fracColors : List[Tuple2[Double,Color]], cycle: CycleMethod)  extends LinearGradient(fracColors){
   def toFTexture = {
      val focus = focusc - center
      val c = focus.normSquared - radius*radius
      FTexture(pp => {
        val p = pp - center
        val a = p.normSquared
        val b = 2*focus.dot(p)
        val disc = sqrt(b*b - 4 * a * c)
        val sol = if(disc - b >= 0) (-b + disc)/(2*a) else (-b - disc)/(2*a)
        getColor(cycle.getFrac(1/sol))
      }, None)
    }
}

private[deform] case class ImageTexture(img : java.awt.image.BufferedImage, anchor : Point) extends NativeTexture{
  val imgBuf = img.getRaster().getDataBuffer();
  val width = img.getWidth()
  val height = img.getHeight()
  val bands = imgBuf.getNumBanks()
  val widthBands = width * bands
  def toFTexture = 
    FTexture(p => {
      val x = Util.modulo(p.x.toInt,width)
      val y = Util.modulo(p.y.toInt,height)
      val elem = y * widthBands + x * bands
      val r = elem + 3
      val g = elem + 2
      val b = elem + 1
      val a = elem
      Color(imgBuf.getElem(r)/255.0, imgBuf.getElem(g)/255.0,imgBuf.getElem(b)/255.0, imgBuf.getElem(a)/255.0)
    }, None)
}

/**
 * A shape with a texture.
 */
abstract class TexturedShape() {
  def |(op : Transformation) = TransformTexturedShape(op,this)
  def bbox : Option[AABBox]
}

private[deform] case class TransformTexturedShape(t : Transformation, i : TexturedShape) extends TexturedShape {
   def bbox =
    (t.transformBBox,i.bbox) match {
    case (Some(tb),Some(b)) => Some(tb(b))
    case _ => None
  }
}

private[deform] case class AnalyzePathTexturedShape(p : Path, analysis :  (Double,List[ConcreteSegment]) => TexturedShape, bbox : Option[AABBox]) extends TexturedShape

private[deform] case class AnalyzeShapeTexturedShape(s : Shape, analysis :  (Double,ShapeSetOperation,List[List[ConcreteSegment]]) => TexturedShape, bbox : Option[AABBox]) extends TexturedShape 

private[deform] case class CTexturedShape(shape : Shape, tex : Texture) extends TexturedShape {
  def bbox = shape.bbox
}

private[deform] case class AffTransformedNativeTexShape(t : AffineTransformation, ts : TexturedShape) extends TexturedShape{
    def bbox = (ts.bbox,t.transformBBox) match {
      case (Some(b),Some(t)) => Some(t(b))
      case _ => None
    }
}

private[deform] case class PartiallyTransformedTexturedShape(area : AABBox, t : Transformation, ts : TexturedShape) extends TexturedShape{
   def bbox = (ts.bbox,t.transformBBox) match {
      case (Some(b),Some(t)) => Some(t(b))
      case _ => None
    }
}

private[deform] case class ImageTexturedShape(img : java.awt.image.BufferedImage) extends TexturedShape {
  
  val imgBuf = img.getRaster().getDataBuffer();
  val width = img.getWidth()
  val height = img.getHeight()
  val bands = imgBuf.getSize() / (width * height)
  val widthBands = width * bands
  val a = Point(0,0)
  val b = Point(width,0)
  val c = Point(width,height)
  val d = Point(0, height)
  def shape = OpShape(Union(),List(Join(Join(Line(a,b),Line(b,c)),Join(Line(c,d),Line(d,a)))))
  
  def tex = 
    if(bands == 4) 
    FTexture(p => {
      val x = Util.modulo(p.x.toInt,width)
      val y = Util.modulo(p.y.toInt,height)
      val elem = y * widthBands + x * bands

      val r = elem + 3
      val g = elem + 2
      val b = elem + 1
      val a = elem
      Color(imgBuf.getElem(r)/255.0, imgBuf.getElem(g)/255.0,imgBuf.getElem(b)/255.0, imgBuf.getElem(a)/255.0 )
    }, None)
    else FTexture(p => {
      val x = Util.modulo(p.x.toInt,width)
      val y = Util.modulo(p.y.toInt,height)
      val elem = y * widthBands + x * bands

      val r = elem + 2
      val g = elem + 1
      val b = elem 
      Color(imgBuf.getElem(r)/255.0, imgBuf.getElem(g)/255.0,imgBuf.getElem(b)/255.0,  1.0)
    }, None)
   def bbox = shape.bbox
}

/** A set of textured shapes.
 * 
 */
abstract class Drawing


private[deform] case class SingleDrawing(ts : TexturedShape) extends Drawing
private[deform] case class ListDrawing(ts : List[Drawing]) extends Drawing
private[deform] case class TransformDrawing(t : Transformation, actual : Drawing) extends Drawing

abstract class Transformation{
  private[deform] def toFTransformation (d : Double): FTransformation
  
  def |(op : Transformation) = ComposeTransformation(op,this)
  def **(op : Point) : Point ={
    val f: Transformation = DeformFunctions.normalize(1,1,this)
    f.toFTransformation(1).forward(op)
  }
  def **(op : Transformation) = ComposeTransformation(this,op)
  def **(op : Path) = TransformPath(this,op)
  def **(op : Shape) = TransformShape(this,op)
  def **(op : Texture) = TransformTexture(this,op)
  def **(op : TexturedShape) = TransformTexturedShape(this,op)
  def **(op : Drawing) =TransformDrawing(this,op)
  /** (optional) A function that transforms a bounding box as it will be (approximalty) transformed by
   * this transformation.
   */
  def transformBBox : Option[AABBox => AABBox]
  /** (optional) The area that is transformed by this transformation,
   * for local transformations.
   */
  def affectedArea : Option[Tuple2[AABBox,Transformation]]
}

private[deform] case class FTransformation(forward : Point => Point, backwards : Point => Point, transformBBox : Option[AABBox => AABBox], affectedArea : Option[Tuple2[AABBox,Transformation]]) extends Transformation{
  private[deform]override def toFTransformation(d:Double)= this

}


private[deform] case class GetScaleTransformation(t : Double => (Point => Point,Point => Point ), transformBBox : Option[AABBox => AABBox], affectedArea : Option[Tuple2[AABBox,Transformation]]) extends Transformation{
    private[deform]override def toFTransformation(d:Double) = {
      val tr = t(d)
      FTransformation(tr._1, tr._2, transformBBox,affectedArea)
    }
}

private[deform] case class ComposeTransformation(a : Transformation, b : Transformation) extends Transformation{
    private[deform] override def toFTransformation(d:Double) = throw new Error("Not normalized!")
    def transformBBox = (a.transformBBox,b.transformBBox) match {
      case (Some(a),Some(b)) => Some((bb : AABBox) => a(b(bb)))
      case _ => None
    }
    def affectedArea = (a.affectedArea,b.affectedArea) match {
      case (Some( (a,f) ),Some( (b,f2) )) => Some( (a.union(b),ComposeTransformation(f,f2)))
      case _ => None
    }
} 

private[deform] case class AnalyzePathTransformation(p : Path, analysis : (Double,List[ConcreteSegment]) => Transformation, transformBBox : Option[AABBox=>AABBox], affectedArea : Option[Tuple2[AABBox,Transformation]]) extends Transformation{
    private[deform] override def toFTransformation(d:Double) = throw new Error("Not normalized!")
}

private[deform] case class AnalyzeShapeTransformation(s : Shape, analysis :  (Double,ShapeSetOperation,List[List[ConcreteSegment]]) => Transformation, transformBBox : Option[AABBox=>AABBox],affectedArea : Option[Tuple2[AABBox,Transformation]]) extends Transformation {
      private[deform] override def toFTransformation(d:Double) = throw new Error("Not normalized!")
}

private[deform] case class AffineTransformation(forward : Matrix, backwards : Matrix) extends Transformation{
  private[deform] override def toFTransformation(d:Double) = FTransformation(p => forward * p, p => backwards * p,transformBBox, None)
  def transformBBox = Some(b => Util.makeBBox(forward * b.leftUp, forward * b.rightUp, forward * b.rightDown, forward * b.leftDown ) )
      def affectedArea = None
}

private[deform] case class InverseTransformation(t : Transformation, transformBBox : Option[AABBox=>AABBox],affectedArea : Option[Tuple2[AABBox,Transformation]]) extends Transformation{
        private[deform] override def toFTransformation(d:Double) = throw new Error("Not normalized!")
}

private[deform] case class IdentityTransformation extends Transformation{
    private[deform] override def toFTransformation(d:Double) = throw new Error("Not normalized!")
      def transformBBox = Some(b => b)
    def affectedArea = None
}



private[deform] object DeformFunctions{
  
  val numericError = 1.2
  val numericErrorSquared = numericError*numericError
  val minParameterDist = 0.1
  
  
  def computeDependentTrans(userScale : Double, scale : Double,  a : Transformation) : Transformation = {
    a match {
    case AnalyzePathTransformation(p,fb,_,_) => computeDependentTrans(userScale,scale,fb(scale,toBezierUser(userScale,p)))
    case AnalyzeShapeTransformation(s,fp,_,_) =>  
      normalize(userScale,scale,s) match {
		        case OpShape(op,l) => normalize(userScale,scale,fp(scale,op,l.map(x => toBezierUser(userScale,x))))
		        case _ => throw new Error("Should not happen")
		      }
    case ComposeTransformation(c,d) => ComposeTransformation(computeDependentTrans(userScale,scale,c),computeDependentTrans(userScale,scale,d))
    case InverseTransformation(t,c,d) => InverseTransformation(computeDependentTrans(userScale,scale,t),c,d) 
    case _ => a
  }
  }
  
  def compose(userScale : Double,scale : Double,  a : Transformation, b : Transformation) : Transformation =
	composeReal(userScale,scale,a, normalize(userScale,scale,b))
  
  def composeReal(userScale : Double, scale : Double, af: Transformation, bf: Transformation) = {
    val a = removeCompose(userScale,scale,af)
    val b = removeCompose(userScale,scale,bf)
    (a,b) match {
    case (IdentityTransformation(), _) => b
    case (_,IdentityTransformation()) => a
     case (AffineTransformation(f,b), AffineTransformation(f2,b2)) => AffineTransformation(f2*f, b * b2)
    case _ => {
      val fa = a.toFTransformation(scale)
      val fb = b.toFTransformation(scale)
      FTransformation(x => fa.forward(fb.forward(x)), x => fb.backwards(fa.backwards(x)), 
          (fa.transformBBox,fb.transformBBox) match {
	        case (Some(la),Some(lb)) => Some(la.compose(lb))
	        case _ => None
	      }, (fa.affectedArea,fb.affectedArea) match {
	         case (Some((la,f)),Some((lb,f2))) =>
	           Some( (la,
	               normalize(userScale,scale,ComposeTransformation(f,b)) ))
	        case (None,Some((la,f))) => Some((la,ComposeTransformation(a,f)))
	        case (Some((la,f)),None) => Some((la,ComposeTransformation(f,b)))
	        case _ => None
	      })
    }
  }
  }
  
  
    def removeCompose(userScale : Double, scale: Double, a : Transformation): Transformation = 
      a match {
      case ComposeTransformation(c,d) => composeReal(userScale,scale,c,d)
      case _ => a
    }
  
    def removeInverse(userScale : Double , scale: Double,a : Transformation) : Transformation = 
        a match {
      case ComposeTransformation(b,c) => ComposeTransformation(removeInverse(userScale,scale,b),removeInverse(userScale,scale,c))
      case InverseTransformation(b,tb,af) => normalize(userScale,scale,b) match {
        case AffineTransformation(f,w) => AffineTransformation(w,f)
        case _ => b.toFTransformation(scale) match {
          case FTransformation(q,w,_,_) => FTransformation(w,q,tb,af)  
        }
      }
      case _ => a
    }
    
  def normalize(userScale : Double,scale : Double,  a: Transformation) : Transformation = {
   removeCompose(userScale,scale,removeInverse(userScale,scale,computeDependentTrans(userScale,scale,a)))
  }
  
  def normalize(userScale : Double,scale : Double, s : Path) : Path = normalize(IdentityTransformation(),userScale,scale,s)
  def normalize (t : Transformation , userScale : Double, scale : Double,  p : Path) : Path = 
    (t.affectedArea,t.transformBBox, p.bbox) match {
    case (Some((a,f)),Some(tb),Some(b)) if(!a.overlaps(tb(b))) =>  {
      normalize(normalize(userScale,scale,f),userScale,scale,p)
    }
    case _ => 
	    (t,p) match {
	      case (_, AnalyzePathPath(p,fp,_)) => normalize(t,userScale,scale,fp(scale,toBezierUser(userScale,normalize(userScale,scale,p))))
	      case (_,AnalyzeShapePath(s,fp,_)) =>
		      normalize(userScale,scale,s) match {
		        case OpShape(op,l) => normalize(t,userScale,scale,fp(scale,op,l.map(x => toBezierUser(userScale,x))))
		        case _ => throw new Error("Should not happen")
		      }
	      case (_, TransformPath(t2,p)) => normalize(compose(userScale,scale,t,t2),userScale,scale,p)
	      case (AffineTransformation(t,b), s : ConcreteSegment) => s.map(p => t * p)
	      case (_,Join(l,r)) => Join(normalize(t,userScale,scale,l),normalize(t,userScale,scale,r))
	      case (IdentityTransformation(),p) => p
	      case _ => {
	        val tf = t.toFTransformation(scale)
	        val pf = p.toFPath
	        val bbox = (tf.transformBBox,pf.bbox) match {
	          case (Some(tb),Some(b)) => Some(tb(b))
	          case _ => None
	        }
	        FPath(x => tf.forward(pf.fun(x)),bbox)
	     }
	   }
    }
 
  def normalize(userScale : Double,scale : Double, s : Shape) : Shape = normalize(IdentityTransformation(),userScale,scale,s)
  def normalize(t : Transformation , userScale : Double, scale : Double, s : Shape) : Shape =  {
     (t.affectedArea,t.transformBBox, s.bbox)  match {
    case (Some((a,f)),Some(tb),Some(b)) if(!a.overlaps(tb(b)))=> {
//            println("No overlap Shape! " ++ a.toString ++ b.toString)
      normalize(normalize(userScale,scale,f),userScale,scale,s)
    }
    case _ => 
		    s match {
		    case AnalyzePathShape(p,fp,_) => normalize(t,userScale,scale,fp(scale,toBezierUser(userScale,normalize(userScale,scale,p))))
		    case AnalyzeShapeShape(s,fp,_) =>
		      normalize(userScale,scale,s) match {
		        case OpShape(op,l) => normalize(t,userScale,scale,fp(scale,op,l.map(x => toBezierUser(userScale,x))))
		        case _ => throw new Error("Should not happen")
		      }
		    case OpShape(op,cl) =>  OpShape(op,cl.map(p => normalize(t,userScale,scale,p)))
		    case TransformShape(t2,s2) => normalize(compose(userScale,scale,t,t2),userScale,scale,s2)
		    }
    }
  }
   
  def normalize(userScale : Double,scale : Double, s : Texture) : Texture = normalize(IdentityTransformation(),userScale,scale,s)
  def normalize(t : Transformation, userScale : Double,scale : Double,  tex :Texture) : Texture = {

     (t,tex) match {
      case (_,FillColor(c)) => tex
      case (_, AnalyzePathTexture(p,fp)) => normalize(t,userScale,scale,fp(scale,toBezierUser(userScale,normalize(userScale,scale,p))))
      case (_,AnalyzeShapeTexture(s,fp,_)) =>
		      normalize(userScale,scale,s) match {
		        case OpShape(op,l) => normalize(t,userScale,scale,fp(scale,op,l.map(x => toBezierUser(userScale,x))))
		        case _ => throw new Error("Should not happen")
		      }
      case (_, TransformTexture(t2,tex2)) => normalize(compose(userScale,scale,t,t2),userScale,scale,tex2)
      case (af : AffineTransformation,ntex : NativeTexture) => AffTransformedNativeTexture(af,ntex)
      case (IdentityTransformation(),tex2) => tex2
      case (_,_) => {
        val ftex = tex.toFTexture
        val ftrans = t.toFTransformation(scale)
        FTexture(x => ftex.fun(ftrans.backwards(x)),ftex.par)
      }
    }
  }
  
  def normalize(userScale : Double, scale : Double, s : TexturedShape) : TexturedShape = normalize(false,IdentityTransformation(),userScale,scale,s)
    def normalize(t : Transformation, userScale : Double,scale : Double,  ts : TexturedShape) : TexturedShape = 
      normalize(false,t,userScale,scale,ts)
  def normalize(part : Boolean,t : Transformation, userScale : Double, scale : Double,  ts : TexturedShape) : TexturedShape = {
    (t.affectedArea,t.transformBBox,ts.bbox,part) match {
    case (Some((a,f)),Some(tb),Some(b),false) => if(!a.overlaps(tb(b))) {
    					    normalize(normalize(userScale,scale,f),userScale,scale,ts)
    					               
    						}else {
    					     PartiallyTransformedTexturedShape(a,t,ts)
    					   }
    case _ => 
	    (t,ts) match {
	    case (_, TransformTexturedShape(t2,ts2)) => normalize(compose(userScale,scale,t,t2),userScale,scale,ts2)
	    case (_, AnalyzePathTexturedShape(p,fp,_)) => normalize(t,userScale,scale,fp(scale,toBezierUser(userScale,normalize(userScale,scale,p))))
	    case (_,AnalyzeShapeTexturedShape(s,fp,_)) =>
		      normalize(userScale,scale,s) match {
		        case OpShape(op,l) => normalize(t,userScale,scale,fp(scale,op,l.map(x => toBezierUser(userScale,x))))
		        case _ => throw new Error("Should not happen")
		      }
	    case (_, CTexturedShape(s,tex)) => {
	      normalize(t,userScale,scale,tex) match {
	    	  case FillColor(r) => {
	    	    CTexturedShape(normalize(t,userScale,scale,s),FillColor(r))
	    	  }
	    	  case AffTransformedNativeTexture(af,ntex) =>  
	    	   AffTransformedNativeTexShape(af,
	    	        CTexturedShape(InverseAffTransformedShape(af,normalize(t,userScale,scale,s)),ntex))
	    	  case ntex  => 
	    	    CTexturedShape(normalize(t,userScale,scale,s),ntex)
	    	}
	    }
	      case (IdentityTransformation(),ImageTexturedShape(img)) => ImageTexturedShape(img)
	      case (af : AffineTransformation,ImageTexturedShape(img)) =>  AffTransformedNativeTexShape(af,ImageTexturedShape(img))
	      case (_,ImageTexturedShape(img)) => CTexturedShape(normalize(t,userScale,scale,ImageTexturedShape(img).shape),
	          normalize(t,userScale,scale,ImageTexturedShape(img).tex))
	    }
	    
    }
  }
   def normalize(t : Transformation, userScale : Double, scale : Double, ts : Drawing) : List[TexturedShape] =
     ts match {
     case SingleDrawing(ts) => List(normalize(t,userScale,scale,ts))
     case ListDrawing(ls) => ls.flatMap(x => normalize(t,userScale,scale,x))
     case TransformDrawing(t2,ts) => normalize(compose(userScale,scale,t,t2),userScale,scale,ts)
   }
     
  
  def toBezierReal(userScale: Double,p : Path) : List[ConcreteSegment] = {
    p match {
      case FPath(f,_) => toBezierFun(userScale,f)
      case s : ConcreteSegment => List(s)
      case Join(l,r) => toBezierReal(userScale,l) ++ toBezierReal(userScale,r)
      case _ => throw new Error("Unkown toBezier case" + p)
    }
  }
  
//  def toBezier(s : Shape) : List[List[ConcreteSegment]] = 
//      normalize(IdentityTransformation(),s) match {
//        case s2 @ OpShape(_,_) => toBezierReal(s2)
//        case _ => throw new Error("Not normalized!")
//      }
  
  def toBezierUser(userScale : Double, p : Path) : List[ConcreteSegment] = toBezierReal(userScale, p)
  
  def toBezierReal(userScale: Double,s : List[Path]) : List[List[ConcreteSegment]] = s.map(x => toBezierReal(userScale,x))
  
  def toBezierFun(userScale: Double, f : Double => Point) : List[ConcreteSegment] ={
     def getLines(start : Double, end : Double, sp: Point, ep : Point) : List[ConcreteSegment] = {
      if(end - start <= minParameterDist && sp.distanceSquared(ep) <= userScale){
        List(Line(sp,ep))
      } else {
        val middle = (start + end)/2.0
        if (middle == start || middle == end) {
          System.err.println("Numeric resolution to small!");
          List(Line(sp,ep))
        } else {
        val mp = f(middle)
        getLines(start,middle,sp,mp) ++ getLines(middle,end,mp,ep)
        }
      }
    }
    getLines(0,1,f(0),f(1))
  }
  def getAABB(cc : List[List[ConcreteSegment]]) : AABBox = cc.foldLeft(Util.emptyBBox)((b,c) => b union getAABBL(c))
  def getAABBL(cc : List[ConcreteSegment]) : AABBox = cc.foldLeft(Util.emptyBBox)((b,c) => b union getAABBSegment(c))
  def getAABBSegment(c : ConcreteSegment) : AABBox = 
		 c match {
      case Line(s,e) => Util.makeBBox(s,e)
      case Quad(s,c,e) => Util.makeBBox(s,c,e)
      case Cubic(s,cl,cr,e) => Util.makeBBox(s,cl,cr,e)
      case _ => throw new Error("Unkown segment case" + c)
    }

}

