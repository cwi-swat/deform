package demo

import scala.math._
import deform.library.Render._
import deform.library.Transforms._
import deform.library.Paths._
import deform.library.Shapes._
import deform.library.Textures._
import deform.library.TexturedShapes._
import deform.library.Colors._
import deform.library.Colors.ColorNames
import deform._
import deform.library.Drawings._

class TextOnPath extends BaseScala {

  def txt = text("Hallo",1)
  def rtext = scale(1/getBBoxS(txt).width) **translate(-getBBoxS(txt).left.x,0) ** txt
  def draw(): Unit = {  
    def p = quad(Point(-1,0), mouse, Point(1,0))
    draw(drawing(sweep(p) ** fill(rtext,fillColor(black)))) 
  }


}