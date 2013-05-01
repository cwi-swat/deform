package deform.library

import javax.imageio.ImageIO

import scala.math._
import deform._
/** Functions for creating textured shapes 
 */
object TexturedShapes {
      /** Fill a shape with a texture. */
    def fill(s : Shape,tex : Texture) =
      CTexturedShape(s,tex)
  
   	/** Convenvience function for obtaining a bounding box unconditionally */
    def getBBoxTS(s : TexturedShape) = 
      s.bbox match {
      case Some(b) => b
      case _ => throw new Error("No bbox!")
      }

      
   /** Create a textured shape from an Image. */
    def image(path : String) = { 
      val f = ImageTexturedShape(ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream(path)))
      val b = getBBoxTS(f)
      val scale = 1/(min(b.width,b.height)/2)
      Transforms.scale(scale) ** Transforms.translate(-b.width/2,-b.height/2) ** f
    }

}