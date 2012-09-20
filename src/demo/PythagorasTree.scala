package demo
import scala.math._
import deform.Library._
import deform.Library.Transforms._
import deform.Library.Paths._
import deform.Library.Shapes._
import deform.Library.Textures._
import deform.Library.TexturedShapes._
import deform.Library.Colors._
import deform._
import deform.AffineTransformation

class PythagorasTree  extends BaseScala{
  
  def triangle(p : Point) = {
    val a = Point(-1,0)
    val b = Point(1,0)
    val c = p
    translate(0,-1) **  shape(List(join(List(line(a,b),line(b,c),line(c,a)))))
  }
  
 def treeColor(curDepth : Int, maxDepth : Int) = {
   val f = curDepth.toDouble/maxDepth.toDouble
   lerpNoAlpha(treetrunkbrown,f,green)
}
 def rectang = rect
 def getTexture(curDepth : Int,maxDepth:Int) = {
		val bottom = treeColor(curDepth,maxDepth)
		val top = treeColor(curDepth+1,maxDepth)
		if(tex) texture(p => lerpNoAlpha(lerpNoAlpha(top,(p.y+1)/2,bottom),p.x * p.x,black ))
		else fillColor(bottom)
 }
 
 def triTex(curDepth : Int,maxDepth: Int) = {
   val top = treeColor(curDepth+1,maxDepth)
   if (tex)texture(p => {
   val x = abs(1 -abs(p.x))
   val y = abs(1 - abs(p.y))
   lerpNoAlpha(top,min(x*x,y*y),black)
 }) else fillColor(top)
 }

  
 def tree(maxDepth : Int, curDepth : Int , tri : Shape, triTexTrans : Transformation , lt : Transformation, rt : Transformation) : Drawing ={
		val c = getTexture(curDepth, maxDepth)
		if(curDepth >= maxDepth-1){
			drawing(fill(rectang,c))
		} else {
			val deeper = tree(maxDepth, curDepth+1,tri,triTexTrans,lt,rt) : Drawing
			val left = lt ** deeper
			val right = rt ** deeper
			val tex = triTexTrans ** triTex(curDepth,maxDepth)
			
			nestedDraw(left , right, drawing(fill(rectang,c), fill(tri,tex))) 
		}
	}
  def tree(maxDepth : Int, curDepth : Int, angle : Double)  : Drawing= {
	  	val sizeLeft = sin(angle) 
		val sizeRight = cos(angle)
		val rot =    transform(translate(-1,-1), rotate(0.5*Pi - angle) ** scale(sizeLeft) ) ** translate(0,-2)
		val rot2 =   transform(translate(1,-1),rotate(-angle) ** scale(sizeRight))  **  translate(0,-2)
		val triPoint = Point(sizeLeft*sizeLeft*2 - 1,-sizeRight*sizeLeft*2) 
		val tri = triangle(triPoint)
		val trip = triPoint + Point(0,-1)
		val textran =   translate(trip) ** rotate(-angle) ** scale(sizeRight,sizeLeft) 
		 tree(maxDepth,curDepth, tri, textran, rot,rot2)
  }
   var depth = 1
   var tex = false
  def tree(maxDepth : Int, angle :Double): Drawing = tree(maxDepth,0,angle)
  
  
   override def handleKeyStroke(key : Char) = {
    if(key == 'a') depth = depth +1
    else if(key == 't') tex = !tex 
    else if(depth > 0) depth = depth-1;
  }
  
  def draw(): Unit = { 
     val t =  tree(depth,( wheel/100 + 0.25) * Pi)
//     renderImage("/home/atze/tree.png",1800,1200,t)
     draw(translate(0,0.6) ** scale(1.0/5)  **  t)
  }
    def main(args: Array[String]): Unit = {
     new PythagorasTree().draw
   }


}