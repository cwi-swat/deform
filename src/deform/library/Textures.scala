package deform.library

import scala.math._
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import deform._
/** Functions for creating textures 
 */
object Textures {
  /** Create a texture from an image.
 */
  def imageTexture(path : String) : Texture = { 
    val f = ImageTexturedShape(ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream(path)))
      val b = TexturedShapes.getBBoxTS(f) : AABBox
      val scale = 1/(min(b.width,b.height)/2)
      Transforms.scale(scale) ** Transforms.translate(-b.width/2,-b.height/2) ** ImageTexture(f.img,Point(0,0))
    }
    /** Create a texture from a function.
 */
	  def texture(f : Point => Color) : Texture = FTexture(f,None)
	/** Create a texture from a function and do the rendering in parallel for every parSize pixels
	 */
	  def parTexture(f: Point => Color, parSize : Int) : Texture = FTexture(f,Some(parSize))
	  def fillColor(c: Color) : Texture = FillColor(c)
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