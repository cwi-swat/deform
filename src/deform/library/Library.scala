package deform
import scala.Math._
import java.awt.Font
import java.awt.font.FontRenderContext
import java.awt.geom.AffineTransform
import deform.DeformFunctions._
import javax.swing.UIManager
import org.apache.commons.math3.util._
import javax.imageio.ImageIO
import java.io.File
import java.awt.BasicStroke
import deform.library.querypaths.paths.factory.QueryPathFactory
import deform.library.querypaths.util.Vec
import deform.library.querypaths.paths.paths.QueryPath
import scala.collection.JavaConversions._
import deform.library.querypaths.Sweep
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

object Library {
  def instance = this
  def render(area : AABBox, state : java.awt.Graphics, tsl : Drawing) : Unit = 
    RenderJava2D.render(area,state,tsl)
  
  def renderImage(path : String, width : Int, height : Int, tsl : Drawing) : Unit = {
    val bf = new BufferedImage(width,height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
    render(AABBox(Interval(0,width),Interval(0,height)), bf.getGraphics(),tsl)
    ImageIO.write(bf,"png",new File(path))
  }
    
    
  def drawing(ts : TexturedShape*) = ListDrawing(ts.toList.map(x => SingleDrawing(x)))
  def singleDraw(ts : TexturedShape) = SingleDrawing(ts)
  def nestedDraw(ts : Drawing*) = ListDrawing(ts.toList)
    
  object Colors {
    def color(r:Double,g:Double,b : Double,a : Double) = Color(r*a,g*a,b*a,a)
    def color(r:Double,g:Double,b : Double) = Color(r,g,b,1)
    def lerp(lhs : Color, i : Double, rhs : Color) = lhs.lerp(i,rhs)
    def lerpNoAlpha(lhs : Color, i : Double, rhs : Color) = lhs.lerpNoAlpha(i, rhs)
    def byteColor(r : Int, g : Int, b : Int, a : Int) : Color = color(r/255.0,g/255.0,b/255.0,a/255.0)
    def byteColor(r : Int, g : Int, b : Int) : Color= color(r/255.0,g/255.0,b/255.0)
    def aliceblue = byteColor(240,248, 255)
	def antiquewhite = byteColor(250,235, 215)
	def aqua = byteColor(0,255, 255)
	def aquamarine = byteColor(127,255, 212)
	def azure = byteColor(240,255, 255)
	def beige = byteColor(245,245, 220)
	def bisque = byteColor(255,228, 196)
	def black = byteColor(0,0, 0)
	def blanchedalmond = byteColor(255,235, 205)
	def blue = byteColor(0,0, 255)
	def blueviolet = byteColor(138,43, 226)
	def brown = byteColor(165,42, 42)
	def burlywood = byteColor(222,184, 135)
	def cadetblue = byteColor(95,158, 160)
	def chartreuse = byteColor(127,255, 0)
	def chocolate = byteColor(210,105, 30)
	def coral = byteColor(255,127, 80)
	def cornflowerblue = byteColor(100,149, 237)
	def cornsilk = byteColor(255,248, 220)
	def crimson = byteColor(220,20, 60)
	def cyan = byteColor(0,255, 255)
	def darkblue = byteColor(0,0, 139)
	def darkcyan = byteColor(0,139, 139)
	def darkgoldenrod = byteColor(184,134, 11)
	def darkgray = byteColor(169,169, 169)
	def darkgreen = byteColor(0,100, 0)
	def darkgrey = byteColor(169,169, 169)
	def darkkhaki = byteColor(189,183, 107)
	def darkmagenta = byteColor(139,0, 139)
	def darkolivegreen = byteColor(85,107, 47)
	def darkorange = byteColor(255,140, 0)
	def darkorchid = byteColor(153,50, 204)
	def darkred = byteColor(139,0, 0)
	def darksalmon = byteColor(233,150, 122)
	def darkseagreen = byteColor(143,188, 143)
	def darkslateblue = byteColor(72,61, 139)
	def darkslategray = byteColor(47,79, 79)
	def darkslategrey = byteColor(47,79, 79)
	def darkturquoise = byteColor(0,206, 209)
	def darkviolet = byteColor(148,0, 211)
	def deeppink = byteColor(255,20, 147)
	def deepskyblue = byteColor(0,191, 255)
	def dimgray = byteColor(105,105, 105)
	def dimgrey = byteColor(105,105, 105)
	def dodgerblue = byteColor(30,144, 255)
	def firebrick = byteColor(178,34, 34)
	def floralwhite = byteColor(255,250, 240)
	def forestgreen = byteColor(34,139, 34)
	def fuchsia = byteColor(255,0, 255)
	def gainsboro = byteColor(220,220, 220)
	def ghostwhite = byteColor(248,248, 255)
	def gold = byteColor(255,215, 0)
	def goldenrod = byteColor(218,165, 32)
	def gray = byteColor(128,128, 128)
	def grey = byteColor(128,128, 128)
	def green = byteColor(0,128, 0)
	def greenyellow = byteColor(173,255, 47)
	def honeydew = byteColor(240,255, 240)
	def hotpink = byteColor(255,105, 180)
	def indianred = byteColor(205,92, 92)
	def indigo = byteColor(75,0, 130)
	def ivory = byteColor(255,255, 240)
	def khaki = byteColor(240,230, 140)
	def lavender = byteColor(230,230, 250)
	def lavenderblush = byteColor(255,240, 245)
	def lawngreen = byteColor(124,252, 0)
	def lemonchiffon = byteColor(255,250, 205)
	def lightblue = byteColor(173,216, 230)
	def lightcoral = byteColor(240,128, 128)
	def lightcyan = byteColor(224,255, 255)
	def lightgoldenrodyellow = byteColor(250,250, 210)
	def lightgray = byteColor(211,211, 211)
	def lightgreen = byteColor(144,238, 144)
	def lightgrey = byteColor(211,211, 211)
	def lightpink = byteColor(255,182, 193)
	def lightsalmon = byteColor(255,160, 122)
	def lightseagreen = byteColor(32,178, 170)
	def lightskyblue = byteColor(135,206, 250)
	def lightslategray = byteColor(119,136, 153)
	def lightslategrey = byteColor(119,136, 153)
	def lightsteelblue = byteColor(176,196, 222)
	def lightyellow = byteColor(255,255, 224)
	def lime = byteColor(0,255, 0)
	def limegreen = byteColor(50,205, 50)
	def linen = byteColor(250,240, 230)
	def magenta = byteColor(255,0, 255)
	def maroon = byteColor(128,0, 0)
	def mediumaquamarine = byteColor(102,205, 170)
	def mediumblue = byteColor(0,0, 205)
	def mediumorchid = byteColor(186,85, 211)
	def mediumpurple = byteColor(147,112, 219)
	def mediumseagreen = byteColor(60,179, 113)
	def mediumslateblue = byteColor(123,104, 238)
	def mediumspringgreen = byteColor(0,250, 154)
	def mediumturquoise = byteColor(72,209, 204)
	def mediumvioletred = byteColor(199,21, 133)
	def midnightblue = byteColor(25,25, 112)
	def mintcream = byteColor(245,255, 250)
	def mistyrose = byteColor(255,228, 225)
	def moccasin = byteColor(255,228, 181)
	def navajowhite = byteColor(255,222, 173)
	def navy = byteColor(0,0, 128)
	def oldlace = byteColor(253,245, 230)
	def olive = byteColor(128,128, 0)
	def olivedrab = byteColor(107,142, 35)
	def orange = byteColor(255,165, 0)
	def orangered = byteColor(255,69, 0)
	def orchid = byteColor(218,112, 214)
	def palegoldenrod = byteColor(238,232, 170)
	def palegreen = byteColor(152,251, 152)
	def paleturquoise = byteColor(175,238, 238)
	def palevioletred = byteColor(219,112, 147)
	def papayawhip = byteColor(255,239, 213)
	def peachpuff = byteColor(255,218, 185)
	def peru = byteColor(205,133, 63)
	def pink = byteColor(255,192, 203)
	def plum = byteColor(221,160, 221)
	def powderblue = byteColor(176,224, 230)
	def purple = byteColor(128,0, 128)
	def red = byteColor(255,0, 0)
	def rosybrown = byteColor(188,143, 143)
	def royalblue = byteColor(65,105, 225)
	def saddlebrown = byteColor(139,69, 19)
	def salmon = byteColor(250,128, 114)
	def sandybrown = byteColor(244,164, 96)
	def seagreen = byteColor(46,139, 87)
	def seashell = byteColor(255,245, 238)
	def sienna = byteColor(160,82, 45)
	def silver = byteColor(192,192, 192)
	def skyblue = byteColor(135,206, 235)
	def slateblue = byteColor(106,90, 205)
	def slategray = byteColor(112,128, 144)
	def slategrey = byteColor(112,128, 144)
	def snow = byteColor(255,250, 250)
	def springgreen = byteColor(0,255, 127)
	def steelblue = byteColor(70,130, 180)
	def tan = byteColor(210,180, 140)
	def teal = byteColor(0,128, 128)
	def thistle = byteColor(216,191, 216)
	def treetrunkbrown = byteColor(83,53,10)
	def tomato = byteColor(255,99, 71)
	def turquoise = byteColor(64,224, 208)
	def violet = byteColor(238,130, 238)
	def wheat = byteColor(245,222, 179)
	def white = byteColor(255,255, 255)
	def whitesmoke = byteColor(245,245, 245)
	def yellow = byteColor(255,255, 0)
	def yellowgreen = byteColor(154,205, 50)
  }
  
  object Paths{
      def path(f : Double => Point) = FPath(f,None)
    def path(f : Double => Point, bbox : AABBox) = FPath(f,Some(bbox))
    def join(lp : List[Path]) : Path = 
      if(lp.tail.isEmpty) lp.head
      else {
        val s = lp.size
        val split = lp.splitAt(s/2)
        Join(join(split._1),join(split._2))
      }
    def line(a : Point, b : Point) = Line(a,b)
    def quad(start : Point, control : Point, end : Point) = Quad(start,control,end)
    def cubic(start : Point , controll : Point, controlr : Point, end : Point) = Cubic(start,controll,controlr,end)
    
    def toQueryPath(scale : Double, l : List[ConcreteSegment]) : deform.library.querypaths.paths.paths.QueryPath= {
      def toVec(p : Point) = new Vec(p.x,p.y)
      val all = l.map(x => x.map(p => p * scale)).map( x => x match {
        case Line(s,e) => QueryPathFactory.createLine(toVec(s), toVec(e))
        case Quad(s,c,e) => QueryPathFactory.createQuad(toVec(s), toVec(c),toVec(e))
        case Cubic(s,cl,cr,e) => QueryPathFactory.createCubic(toVec(s), toVec(cl),toVec(cr),toVec(e))
      }) : List[QueryPath]
      QueryPathFactory.createAppendsL(asList(all))
    }
  }
  
  object Shapes {
    def stroke(p : Path, width : Double) = {
      AnalyzePathShape(p, 
          (_,ls) => { 
            val pj = toJava2DShape(ls)
            fromJava2D(new BasicStroke(width.toFloat).createStrokedShape(pj))
         },
         p.bbox match {
           case Some(b) => Some(b.grow(width))
           case _ => None
         }
         )
    }
    
//      def circle = OpShape(Union(),List(FPath(t => Point(sin(t*2*Pi),cos(t*2*Pi)))))
    def getBBoxS(s: Shape) = s.bbox match {
      case Some(b) => b
      case _ => throw new Error("no BBOX!" )
    }
    def rect = OpShape(Union(),List(
        Join(
        Join(Line(Point(-1,-1),Point(1,-1)),Line(Point(1,-1), Point(1,1))),
        Join(Line(Point(1,1),Point(-1,1)),Line(Point(-1,1), Point(-1,-1))))))
    
    def shape(lp : List[Path]) = union(lp)
    def union(lp : List[Path]) = OpShape(Union(),lp)
    def symdiff(lp : List[Path]) = OpShape(Symdiff(),lp)
//    def union(a : Shape, b : Shape) = 
//      (normalize(a),normalize(b)) match {
//        case (OpShape(Union(),s),OpShape(Union(),l)) => OpShape(Union(),s ++ l)
//        case (na,nb) => 
//          val sa = new java.awt.geom.Area(RenderJava2D.getGeom(na))
//          val sb = new java.awt.geom.Area(RenderJava2D.getGeom(nb))
//          sa.add(sb)
//          fromJava2D(sa)
//      }
//    
     def symdiff(a : Shape, b : Shape) = 
      (a,b) match {
        case (OpShape(Symdiff(),s),OpShape(Symdiff(),l)) => OpShape(Symdiff(),s ++ l)
        case (na,nb) =>  
          AnalyzeShapeShape(na, (_,op,l) =>
            AnalyzeShapeShape(nb, (_,op2,l2) => {
            val ja = RenderJava2D.getJava2DShape(RenderJava2D.getWind(op), l)
            val jb = RenderJava2D.getJava2DShape(RenderJava2D.getWind(op), l2)
          val sa = new java.awt.geom.Area(ja)
          val sb = new java.awt.geom.Area(jb)
          sa.exclusiveOr(sb)
          fromJava2D(sa)
            },nb.bbox),(nb.bbox,na.bbox) match {
              case (Some(ba),Some(bb)) => Some(ba.union(bb))
              case _ => None
            }
          )
      }
     
    def subtract(a : Shape, b : Shape) = {
       AnalyzeShapeShape(a, (_,op,l) =>
            AnalyzeShapeShape(b, (_,op2,l2) => {
            val ja = RenderJava2D.getJava2DShape(RenderJava2D.getWind(op), l)
            val jb = RenderJava2D.getJava2DShape(RenderJava2D.getWind(op), l2)
          val sa = new java.awt.geom.Area(ja)
          val sb = new java.awt.geom.Area(jb)
          sa.subtract(sb)
          fromJava2D(sa)
            },b.bbox)
            ,(b.bbox,a.bbox) match {
              case (Some(ba),Some(bb)) => Some(ba.union(bb))
              case _ => None
            }
          )
    }
    
    def intersection(a : Shape, b : Shape) = {
      AnalyzeShapeShape(a, (_,op,l) =>
            AnalyzeShapeShape(b, (_,op2,l2) => {
            val ja = RenderJava2D.getJava2DShape(RenderJava2D.getWind(op), l)
            val jb = RenderJava2D.getJava2DShape(RenderJava2D.getWind(op), l2)
          val sa = new java.awt.geom.Area(ja)
          val sb = new java.awt.geom.Area(jb)
          sa.intersect(sb)
          fromJava2D(sa)
            },b.bbox)
            ,(b.bbox,a.bbox) match {
              case (Some(ba),Some(bb)) => Some(ba.intersect(bb))
              case _ => None
            }
          )
    }
    
  
    def fromJava2DPaths(s : java.awt.Shape) : List[Path] = {
      fromJava2D(s) match {
        case OpShape(_,l) => l
      }
    }
    def fromJava2D(s : java.awt.Shape) : Shape = {
      var it = s.getPathIterator(new AffineTransform())
      val op =
        if (it.getWindingRule() == java.awt.geom.PathIterator.WIND_EVEN_ODD) Symdiff 
        else Union 
      val tmp = new Array(6) : Array[Double]
      var startPoint = Point(0,0)
      var prevPoint = Point(0,0)
      var parts = List() : List[Path]
      var res = List() : List[Path]
      while(!it.isDone()){
        it.currentSegment(tmp) match {
          case java.awt.geom.PathIterator.SEG_MOVETO => {
        	  prevPoint = Point(tmp(0),tmp(1))
        	  startPoint = prevPoint
          }
          case java.awt.geom.PathIterator.SEG_LINETO => { 
            val np = Point(tmp(0),tmp(1))
            parts = Line(prevPoint,np) :: parts
            prevPoint = np
          }
          case java.awt.geom.PathIterator.SEG_QUADTO => { 
            val np = Point(tmp(0),tmp(1))
            val np2 = Point(tmp(2),tmp(3))
            parts = Quad(prevPoint,np,np2) :: parts
            prevPoint = np2
          }
          case java.awt.geom.PathIterator.SEG_CUBICTO => { 
            val np = Point(tmp(0),tmp(1))
            val np2 = Point(tmp(2),tmp(3))
            val np3 = Point(tmp(4),tmp(5))
            parts = Cubic(prevPoint,np,np2,np3) :: parts
            prevPoint = np3
          }
          case java.awt.geom.PathIterator.SEG_CLOSE => {
            if(!(prevPoint == startPoint)) {
              parts = Line(prevPoint,startPoint) :: parts
            }
            res = Paths.join(parts.reverse) :: res
            parts = List()
          }
        }
        it.next()
      }
      symdiff(res.reverse)
    }
    
    def toJava2DShape(s : List[ConcreteSegment]) : java.awt.Shape = 
      RenderJava2D.getJava2DPath(s)
    
    def toJava2DShape(wind : ShapeSetOperation, s : List[List[ConcreteSegment]]) =
      RenderJava2D.getJava2DShape(RenderJava2D.getWind(wind), s)
    
    def defaultFont = UIManager.getDefaults().getFont("Label.font").getName()
    def text(txt : String) :  Shape = text(defaultFont,txt,11)
    def text(txt : String, fontSize : Int) : Shape = text(defaultFont,txt,fontSize)
    def text(font : String, txt : String, fontSize : Int) :  Shape = {
      val f = new Font(font, Font.PLAIN, fontSize) : Font
	  val ctx = new FontRenderContext(null, true, true);
		val lm = f.getLineMetrics(txt, ctx);
		val height = lm.getHeight();
		val lines = txt.split("\\n") 
		var i = 0
		var result =List() : List[Path]

		for(line <- lines){
			if(!line.trim().isEmpty()){
				val v = f.createGlyphVector(ctx, line);
				val res = v.getOutline();
				result = fromJava2DPaths(res).map(x => Transforms.translate(0,i*height) **x) ++ result 
			}
			i=i+1;
		}
		OpShape(Symdiff(),result)
    }
  }
  
  object Textures {
    def imageTexture(path : String) = { 
    val f = ImageTexturedShape(ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream(path)))
      val b = TexturedShapes.getBBoxTS(f)
      val scale = 1/(min(b.width,b.height)/2)
      Transforms.scale(scale) ** Transforms.translate(-b.width/2,-b.height/2) ** ImageTexture(f.img,Point(0,0))
    }
	  def texture(f : Point => Color) = FTexture(f,None)
	  def parTexture(f: Point => Color, parSize : Int) = FTexture(f,Some(parSize))
	  def fillColor(c: Color) = FillColor(c)
	  def lineGradient(start : Point, end : Point, left : Color, right : Color) : Texture = 
	    lineGradient(start,end,List((0,left),(1,right)))
	  def lineGradient(start : Point, end : Point, fracColors : List[Tuple2[Double,Color]]) : Texture = 
	    lineGradient(start,end,fracColors,Reflect())
	  def lineGradient(start : Point, end : Point, fracColors : List[Tuple2[Double,Color]], cycle : CycleMethod)  : Texture= {
	    val l = fracColors.last
	    val fracColorss = if(l._1 != 1.0) fracColors ++ List((1.0,l._2)) else fracColors
	    LineLinearGradient(Line(start,end),fracColorss,cycle)
	  }
	  def circleGradient(center : Point, focus :Point, radius : Double, fracColors : List[Tuple2[Double,Color]], cycle : CycleMethod) = {
	    val l = fracColors.last
	    val fracColorss = if(l._1 != 1.0) fracColors ++ List((1.0,l._2)) else fracColors
	    LinearRadialGradient(center,focus,radius,fracColorss,cycle)
	  }
	  
	   def circleGradient(center : Point, focus :Point, radius : Double, fracColors : List[Tuple2[Double,Color]]) : Texture = 
			   circleGradient(center,focus,radius, fracColors,Reflect())
  }
  
  object Transforms {
    
    def transformation(forward : Point => Point, backward : Point => Point) =
      FTransformation(forward,backward,None,None)
    def inverse(t : Transformation) = t match {
      case FTransformation(f,b,_,_) => FTransformation(b,f,None,None)
      case AffineTransformation(f,b) => AffineTransformation(b,f)
      case _ => InverseTransformation(t,None,None)
    }
    def translate(p : Point) : AffineTransformation = translate(p.x,p.y)
    def translate(xs : Double, ys : Double) : AffineTransformation  = AffineTransformation(Matrix(1,0,xs,0,1,ys),Matrix(1,0,-xs,0,1,-ys))
    def scale(x : Double) : AffineTransformation = scale(x,x)
    def scale(xs : Double, ys : Double) : AffineTransformation  =  AffineTransformation(Matrix(xs,0,0,0,ys,0),Matrix(1/xs,0,0,0,1/ys,0))
	def rotate(angle : Double) = {
	    	val s = sin(angle);
			val c = cos(angle);
			AffineTransformation(Matrix(c, s, 0, -s, c, 0), Matrix(c, -s, 0, s, c, 0))
	}
    def shear(x : Double, y : Double) =
     AffineTransformation(Matrix(1, x, 0, 1, y, 0), Matrix(1, -x, 0, 1, -y, 0))
    def transform(t : Transformation, r : Transformation) = t ** r ** inverse(t)
    
    def wave = FTransformation(
        p => Point(p.x + FastMath.sin(p.y), p.y),
        p => Point(p.x - FastMath.sin(p.y),p.y), Some(b => b.grow(1,0)),None)
    
    def focusContextLens(norm : Point => Double, 
        profile : Double => Double, profileDeriv : Double => Double, center : Point,  innerRadius : Double, outerRadius : Double, zoom : Double ) = { 
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
          val res = Util.newtonInvert(d, fromNorm,fromNormDerivative, initGuess, DeformFunctions.numericError/(scale * 3))
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
    
    def euclidianNorm(x : Point) = x.norm
    def circleNorm(x:Point) = euclidianNorm(x)
    def rectNorm(x : Point) = max(abs(x.x) ,abs(x.y))
    def circleRectNorm(v : Point) = {
    	val x2 = v.x * v.x;
		val y2 = v.y * v.y;
			 sqrt(sqrt(x2 * x2 + y2 * y2))
    }
    def rectCircleNorm(v : Point) = {
      pow(v.x * v.x * abs(v.x) + v.y * v.y * abs(v.y) , 1.0/3.0)
    }
    def id(x : Double) = x
    def one(x : Double) = 1
    
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

    def lens(norm : Point => Double, profile : (Double=>Double,Double=>Double), center : Point, innerRadius : Double, outerRadius : Double, zoom : Double) = {
      focusContextLens(norm, profile._1, profile._2, center, innerRadius, outerRadius, zoom)
    }
    
    		
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
      
  }
 
  object TexturedShapes {
    def getBBoxTS(s : TexturedShape) = 
      s.bbox match {
      case Some(b) => b
      case _ => throw new Error("No bbox!")
      }
    def fill(s : Shape,tex : Texture) =
      CTexturedShape(s,tex)
      
    def image(path : String) = { 
      val f = ImageTexturedShape(ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream(path)))
      val b = getBBoxTS(f)
      val scale = 1/(min(b.width,b.height)/2)
      Transforms.scale(scale) ** Transforms.translate(-b.width/2,-b.height/2) ** f
    }
      
  }
}