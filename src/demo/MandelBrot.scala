package demo

import scala.math._
import deform.library.Render._
import deform.library.Transforms._
import deform.library.Paths._
import deform.library.Shapes._
import deform.library.Drawings._
import deform.library.Textures._
import deform.library.TexturedShapes._
import deform.library.Colors._
import deform._
import deform.library.Transforms.CameraScala
import deform.library.Colors.ColorNames._
import deform.library.Transforms.Lens._

class MandelBrot extends BaseScala {

	var MaxIters = 0
  
	
	def mandelBrot = { 

		val log2 = log(2)
		val colors = Vector(black,yellow,red,black)
		val amp = 1.0/(colors.length-1)
	  parTexture( point => {

		  	val tq = point.x - 0.25
		  	val py2 = point.y * point.y
			val q = tq * tq + py2
			if( q * (q + (tq)) < 0.25 * py2){
				 black
			} else {
				var x = point.x;
				var y = point.y;
				var i = 0;
				var x2 = x*x;
				var y2 = y*y;
				var mod = x2 + y2;
				while(i < MaxIters && mod < 4){
					val xtemp = x2 - y2 + point.x;
					y = 2 * x * y + point.y;
					x = xtemp;
					x2 = x*x;
					y2 = y*y;
					mod = x2 + y2;
					i = i + 1
				}
				
				val mu = (i - log(log(sqrt(mod))/log2))/MaxIters;
				val start = (mu / amp).toInt;
				if(start < 0 || start >= colors.length-1) black
				else colors(start).lerpNoAlpha((mu - start*amp)/amp, colors(start+1))
			}
		},30000)
	}
	
	def mandelRect = scale(4) ** rect
    
	var prevMouse = null : Point
	  var camera = null : CameraScala
	  
	  
	override def handleKeyStroke(key : Char) = {
	  if(key == 'a'){
			MaxIters= MaxIters +1;
		}
		if(key == 'z'){
			if(MaxIters > 1){
				MaxIters= MaxIters -1;
			}
		}
		if(key == 'l') {
		      withLens = !withLens
		}

	}
	override def handleMouseWheel(unitsToScroll : Int) =
	  camera.zoom(mouse,if(unitsToScroll  > 0)  0.8 else 1.2)
  
	  
  var withImage = true
  var normIndex = 0
  var profileIndex = 0
  var withLens = false
  
  def profilese = List(linearProfile, quadraticProfile,sineProfile,gaussianProfile)
  def norms : List[Point => Double]= List(circleNorm, rectCircleNorm, circleRectNorm, rectNorm)
  

	  
	override  def handleMouseClick(button : Int) = {
	  prevMouse = mouse;
	   if(button == 1) {
	    normIndex = (normIndex + 1) % norms.length
	  } else {
	     profileIndex = (profileIndex + 1) % profilese.length
	  }
	}  
	
	override  def handleMouseRelease(button : Int) = {
	  prevMouse = null
	}
	
	 

	
 override def init() : Unit = {
   MaxIters = 30
   camera = new CameraScala()
 }
	  
  def draw(): Unit = {  
    if(prevMouse!=null){
		camera.move(prevMouse -mouse);
		prevMouse = mouse;
	}
     val l = lens(norms(normIndex),profilese(profileIndex),mouse,0.2,0.4,4)
    val tran = if(withLens) l ** camera.trans else camera.trans
	  draw(drawing(fill(mandelRect,tran** mandelBrot)))
	}

}


object MandelBrotMain{
  def main(args: Array[String]): Unit = {
     new MandelBrot()
   }
}

