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
    if(key == 'a') depth = depth +1
    else if(key == 't') tex = !tex 
    else if(depth > 0) depth = depth-1;
  }
  
  def draw(): Unit = { 
          println(wheel/600 + 0.25)
     val t =  PythagorasFunc.tree(tex,depth,( wheel/600 + 0.25) * Pi)
     
     draw(translate(0,0.6) ** scale(1.0/5)  **  t)
  }
    def main(args: Array[String]): Unit = {

     new PythagorasTree().draw
   }


}

object PythagorasFunc{
    def triangle(p : Point) = {
    val a = Point(-1,0.04)
    val ap = a - Point(0,0.08)
    val b = Point(1,0.04)
    val bp = b - Point(0,0.08)
    val c = p*1.01 
    translate(0,-1) **  transform(translate(0,-0.5),scale(1.,1)) ** 
    shape(List(join(List(line(a,ap),line(ap,c),line(c,bp),line(bp,b),line(b,a)))))
  }
  
 def treeColor(curDepth : Int, maxDepth : Int) = {
   val f = curDepth.toDouble/maxDepth.toDouble
   lerpNoAlpha(treetrunkbrown.mulNoAlpha(2),f,green)
}
 def rectang = rect
 def getTexture(tex : Boolean, curDepth : Int,maxDepth:Int) = {
		val bottom = treeColor(curDepth,maxDepth)
		val top = treeColor(curDepth+1,maxDepth)
		if(tex) texture(p => lerpNoAlpha(lerpNoAlpha(top,(p.y+1)/2,bottom),p.x * p.x,black ))
		else fillColor(bottom)
 }
 
 def triTex(tex : Boolean, curDepth : Int,maxDepth: Int) = {
   val top = treeColor(curDepth+1,maxDepth)
   if (tex)texture(p => {
   val x = abs(1 -abs(p.x))
   val y = abs(1 - abs(p.y))
   lerpNoAlpha(top,min(x*x,y*y),black)
 }) else fillColor(top)
 }

  
 def tree(tex : Boolean,maxDepth : Int, curDepth : Int , tri : Shape, triTexTrans : Transformation , lt : Transformation, rt : Transformation) : Drawing ={
		val c = getTexture(tex,curDepth, maxDepth)
		if(curDepth >= maxDepth-1){
			drawing(fill(rectang,c))
		} else {
			val deeper = tree(tex,maxDepth, curDepth+1,tri,triTexTrans,lt,rt) : Drawing
			val left = lt ** deeper
			val right = rt ** deeper
			val texn = triTexTrans ** triTex(tex,curDepth,maxDepth)
			
			nestedDraw(left , right, drawing(fill(rectang,c), fill(tri,texn))) 
		}
	}
  def tree(tex : Boolean,maxDepth : Int, curDepth : Int, angle : Double)  : Drawing= {
	  	val sizeLeft = sin(angle) 
		val sizeRight = cos(angle)
		val rot =    transform(translate(-1,-1), rotate(0.5*Pi - angle) ** scale(sizeLeft) ) ** translate(0,-2)
		val rot2 =   transform(translate(1,-1),rotate(-angle) ** scale(sizeRight))  **  translate(0,-2)
		val triPoint = Point(sizeLeft*sizeLeft*2 - 1,-sizeRight*sizeLeft*2) 
		val tri = triangle(triPoint)
		val trip = triPoint + Point(0,-1)
		val textran =   translate(trip) ** rotate(-angle) ** scale(sizeRight,sizeLeft) 
		 tree(tex,maxDepth,curDepth, tri, textran, rot,rot2)
  }

  def tree(tex : Boolean,maxDepth : Int, angle :Double): Drawing = tree(tex,maxDepth,0,angle)
  
  
}

object MainPythagors{
  def main(args: Array[String]): Unit = {
//     new PythagorasTree()
         renderImage("/home/ploeg/tree.png",10000,5000,translate(0,0.8) ** scale(1.0/6) **PythagorasFunc.tree(true,21,0.275*Pi))
   }
}