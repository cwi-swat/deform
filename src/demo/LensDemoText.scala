package demo

import deform.library._
import deform.library.Transforms.Lens._
import deform.library.Transforms._
import deform.library.Shapes._
import deform.library.Textures._
import deform.library.TexturedShapes._
import deform.library.Colors._
import deform.library.Colors.ColorNames._
import deform.library.Render._
import deform.library.Drawings._
import deform._


class LensDemoText extends BaseScala {
  var lorem = null : String 
  var atze = null : Shape
 
  var img = null : TexturedShape
  var txt = null : TexturedShape
  var justTxt = null : Drawing
  var all = null:Drawing
    var withImage = true
  var normIndex = 0
  var profileIndex = 0
  
  override def init() = {
    lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam sed turpis sed felis vestibulum pretium.\nPraesent at elit vitae odio eleifend tristique in sit amet sem. In cursus condimentum enim eget\nmalesuada. Donec iaculis, velit non tempor egestas, tellus lacus viverra orci, sed ultricies lorem magna"
    			
    atze =  translate((-size.x/2)/(rscale/2),0) ** Transforms.scale(1/(rscale)) ** text(lorem,60)
    img =  image("landscape.jpg")
    txt = fill(atze,fillColor(black))
    justTxt = drawing(txt)
    all = drawing(img,txt)
    profileIndex = 0
  }


  def profilese = List(linearProfile, quadraticProfile,sineProfile,gaussianProfile)
  def norms : List[Point => Double]= List(circleNorm, rectCircleNorm, circleRectNorm, rectNorm)
  
  override  def handleMouseClick(button : Int) = {
	  if(button == 1) {
	    normIndex = (normIndex + 1) % norms.length
	  } else {
	     profileIndex = (profileIndex + 1) % profilese.length
	  }
  }  
  
  override def handleKeyStroke(key : Char) = {
    withImage = !withImage
  }
  
  def draw(): Unit = {
    val inner = 100/(rscale/2)
    val outer = 200/(rscale/2)
    val zoom = wheel / 100 + 1
    val l = lens(norms(normIndex),profilese(profileIndex),mouse,inner,outer,zoom)
    val x = if(withImage) l ** all 
    	else l ** justTxt
    draw(x) 
   }

}

object Main{
  def main(args: Array[String]): Unit = {
     new LensDemoText()
   }
}