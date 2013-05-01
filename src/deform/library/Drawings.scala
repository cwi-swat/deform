package deform.library

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

/** Functions for creating drawings */

object Drawings {
  
  
  
  /** Create a drawing from some TexturedShapes.
   * 
   */
  def drawing(ts : TexturedShape*) : Drawing = ListDrawing(ts.toList.map(x => SingleDrawing(x)))
    /** Create a drawing from a TexturedShapes.
   * 
   */
  def singleDraw(ts : TexturedShape)  : Drawing= SingleDrawing(ts)
   /** Create a drawing from some Drawings.
   * 
   */
  def nestedDraw(ts : Drawing*)  : Drawing = ListDrawing(ts.toList)
     /** Create a drawing from a list of TexturedShapes.
   * 
   */
  def drawList(ts : List[TexturedShape])  : Drawing = ListDrawing(ts.toList.map(x => SingleDrawing(x)))
       /** Create a drawing from a list of Drawings.
   * 
   */
  def nDrawList(ts : List[Drawing])  : Drawing = ListDrawing(ts)

}