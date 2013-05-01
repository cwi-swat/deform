package deform.library

import scala.math._
import deform._
import java.awt.geom.AffineTransform
import java.awt.BasicStroke
import javax.swing.UIManager
import java.awt.Font
import java.awt.font.FontRenderContext

/** Functions for creating shapes 
 */

object Shapes {
  
      
    /** Shape from a list of closed paths (union) */
    def shape(lp : List[Path]) = union(lp)
  
  /** Convert a path to shape given the width of the "pen" */
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
    
 	/** Convenvience function for obtaining a bounding box unconditionally */
    def getBBoxS(s: Shape) = s.bbox match {
      case Some(b) => b
      case _ => throw new Error("no BBOX!" )
    }
      /** Unit rectangle */
    def rect = OpShape(Union(),List(
        Join(
        Join(Line(Point(-1,-1),Point(1,-1)),Line(Point(1,-1), Point(1,1))),
        Join(Line(Point(1,1),Point(-1,1)),Line(Point(-1,1), Point(-1,-1))))))
    /** Unit circle */
    def circle = shape(List(Paths.circleArcClockwise(0, 2 * Pi)))
    def ellipse(width : Double, height : Double) : Shape = Transforms.scale(width/2,height/2) ** circle
    def ellipse(center : Point, width : Double, height : Double) : Shape =
      Transforms.translate(center) ** ellipse(width,height)

    /** Shape from a list of closed paths (union) */
    def union(lp : List[Path]) = OpShape(Union(),lp)
        /** Shape from a list of closed paths (symmetric difference) */
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
     /** Symmetric difference between shapes */
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
    
    /** Subtract shape from another shape */
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
    
   /** Intersection between shapes */
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
    
  
    private[deform] def fromJava2DPaths(s : java.awt.Shape) : List[Path] = {
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
    
    private[deform] def toJava2DShape(wind : ShapeSetOperation, s : List[List[ConcreteSegment]]) =
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