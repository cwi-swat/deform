package deform.library
import scala.math._
import java.awt.Font
import java.awt.font.FontRenderContext
import java.awt.geom.AffineTransform
import deform._
import deform.DeformFunctions._
import javax.swing.UIManager
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
/** Rendering functions */
object Render {
  /**
   * Main rendering function.
   * 
   * @param area The area that should be rendered
   * @param state The Java2d Graphics object
   * @param tsl The drawing that should be rendered
   */
  def render(area : AABBox, state : java.awt.Graphics, tsl : Drawing) : Unit = 
    RenderJava2D.render(area,state,tsl)
  
    
  /** Convience function for rendering to image file.
   * 
   */
  def renderImage(path : String, width : Int, height : Int, tsl : Drawing) : Unit = {
    val bf = new BufferedImage(width,height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
    render(AABBox(Interval(0,width),Interval(0,height)), bf.getGraphics(),tsl)
    ImageIO.write(bf,"png",new File(path))
  }
    
}