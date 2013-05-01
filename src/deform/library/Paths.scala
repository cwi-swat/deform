package deform.library

import scala.math._
import deform._
import deform.library.querypaths.paths.factory.QueryPathFactory
import deform.library.querypaths.util.Vec
import deform.library.querypaths.paths.paths.QueryPath
import scala.collection.JavaConversions._

/** Functions for creating paths */

object Paths {
  
  /** Create a path from its parametric reprerensetation.
   * 
   */
    def path(f : Double => Point) = FPath(f,None)
      /** Create a path from its parametric reprerensetation and its bounding box.
   * 
   */
    def path(f : Double => Point, bbox : AABBox) = FPath(f,Some(bbox))
   /** String multiple a paths together.
   * 
   */
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
    
    private[deform] def toQueryPath(scale : Double, l : List[ConcreteSegment]) : deform.library.querypaths.paths.paths.QueryPath= {
      def toVec(p : Point) = new Vec(p.x,p.y)
      val all = l.map(x => x.map(p => p * scale)).map( x => x match {
        case Line(s,e) => QueryPathFactory.createLine(toVec(s), toVec(e))
        case Quad(s,c,e) => QueryPathFactory.createQuad(toVec(s), toVec(c),toVec(e))
        case Cubic(s,cl,cr,e) => QueryPathFactory.createCubic(toVec(s), toVec(cl),toVec(cr),toVec(e))
      }) : List[QueryPath]
      QueryPathFactory.createAppendsL(all.toList)
    }
    
    def circleArcClockwise(startAngle : Double, angleLength : Double) = 
      path(x => Point(cos(startAngle + x * angleLength),sin(startAngle + x * angleLength)))
}