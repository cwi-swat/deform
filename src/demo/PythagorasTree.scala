package demo
import scala.math._
import deform.library.Render._
import deform.library.Transforms._
import deform.library.Paths._
import deform.library.Shapes._
import deform.library.Textures._
import deform.library.TexturedShapes._
import deform.library.Colors._
import deform.library.Colors.ColorNames._
import deform.library.Drawings._
import deform._
import deform.AffineTransformation

class PythagorasTree  extends BaseScala{
  

   var depth = 1
   var tex = false
   override def handleKeyStroke(key : Char) = {
    if(key == 'a') {
      depth = depth +1
      System.out.println(depth)
    }
    else if(key == 't') tex = !tex 
    else if(depth > 0) depth = depth-1;
  }
  
  def draw(): Unit = { 
     val t =  PythagorasFunc.tree(tex,depth,( wheel/600 + 0.25) * Pi)
     val st = System.currentTimeMillis()
     draw(translate(0,0.6) ** scale(1.0/5)  **  t)
     System.out.println(((System.currentTimeMillis() - st) / 1000.0).toString)
  }
    def main(args: Array[String]): Unit = {

     new PythagorasTree().draw
   }


}

object PythagorasFunc{
    def triangle(p : Point) = {
    val a = Point(-1,0.0)
    val b = Point(1,0.0)
    val c = p
    translate(0,-1) **  
    shape(List(join(List(line(a,b),line(b,c),line(c,a)))))
  }
  
 def treeColor(curDepth : Int, maxDepth : Int) = {
   val f = curDepth.toDouble/maxDepth.toDouble
   lerp(treetrunkbrown.mulNoAlpha(2),f,green)
}
 
  def treeColorEnd(curDepth : Int, maxDepth : Int) = {
   val f = curDepth.toDouble/maxDepth.toDouble
   lerp(treetrunkbrown.mulNoAlpha(0.6),f,green.mulNoAlpha(0.3))
}
 def rectang = rect

  def tree(tex: Boolean, maxDepth: Int, curDepth: Int, angle: Double): Drawing = {
    val sizeLeft = sin(angle)
    val sizeRight = cos(angle)
    val rot = transform(translate(-1, -1), rotate(0.5 * Pi - angle) ** scale(sizeLeft)) ** translate(0, -2)
    val rot2 = transform(translate(1, -1), rotate(-angle) ** scale(sizeRight)) ** translate(0, -2)
    val triPoint = Point(sizeLeft * sizeLeft * 2 - 1, -sizeRight * sizeLeft * 2)
    val tri = triangle(triPoint)
    val trip = triPoint + Point(0, -1)
    val textran = translate(trip) ** rotate(-angle)

    def getTexture(tex: Boolean, curDepth: Int, maxDepth: Int) = {
      val bottom = treeColor(curDepth, maxDepth)
      val top = treeColor(curDepth + 1, maxDepth)
      val bottome = treeColorEnd(curDepth, maxDepth)
      val tope = treeColorEnd(curDepth + 1, maxDepth)
     
      if (tex) texture(p => {
        val vercol = lerp(top,(p.y+1)/2,bottom)
         val vercole = lerp(tope,(p.y+1)/2,bottome)
        val px = -p.x + 1 
        val yz = max(0,min(1,(p.y + 1) /2))
        val y = yz * yz
        val sr = (1 -y) * sizeRight + y
        val sl = (1 -y) * sizeLeft + y
        val d = if(px < sr)
             max (0, 1 - px / sr)
           else max (0, 1 - (p.x + 1 ) / sl)
             val lp = (1 - y) * d + y * abs(p.x)
          lerp(vercol, lp * lp, vercole)
        
      }
      )
      else fillColor(bottom)
    }

    def triTex(tex: Boolean, curDepth: Int, maxDepth: Int) = {
      val top = treeColor(curDepth + 1, maxDepth)
      val bpt = treeColorEnd(curDepth + 1, maxDepth)
      if (tex) texture(p => {
        val z = 1 - Point(p.x / sizeRight, p.y / sizeLeft).distance(Point(0,0))
        if(z > 0)lerp(top, z * z, bpt)
        else {
          val z= 1 - p.distance(Point(0,2 * sizeLeft)) / sizeLeft
           if(z > 0) lerp(top, z * z, bpt)
           else {
              val z= 1 - p.distance(Point(2 * sizeRight,0)) / sizeRight
              if(z >0)lerp(top, z * z, bpt)
              else top
           }
        }
      })
      else fillColor(top)
    }
    def tree(tex: Boolean, maxDepth: Int, curDepth: Int, tri: Shape, triTexTrans: Transformation, lt: Transformation, rt: Transformation): Drawing = {
      val c = getTexture(tex, curDepth, maxDepth)
      if (curDepth >= maxDepth - 1) {
        drawing(fill(rectang, c))
      } else {
        val deeper = tree(tex, maxDepth, curDepth + 1, tri, triTexTrans, lt, rt): Drawing
        val left = lt ** deeper
        val right = rt ** deeper
        val texn = triTexTrans ** triTex(tex, curDepth, maxDepth)

        nestedDraw(left, right, drawing(fill(rectang, c), fill(tri, texn)))
      }
    }

    tree(tex, maxDepth, curDepth, tri, textran, rot, rot2)
  }

  def tree(tex : Boolean,maxDepth : Int, angle :Double): Drawing = {
    nestedDraw(drawing(fill(scale(10) ** rect, fillColor(white))),tree(tex,maxDepth,0,angle))
  }
  
  
}

object MainPythagors{
  def main(args: Array[String]): Unit = {
     //new PythagorasTree()
    val st = System.currentTimeMillis()
     renderImage("/export/scratch1/ploeg/tree.png",8000,4000,translate(0,0.8) ** scale(1.0/5) **PythagorasFunc.tree(true,21,0.275*Pi))
     System.out.println(((System.currentTimeMillis() - st) / 1000.0).toString)
        
   }
}