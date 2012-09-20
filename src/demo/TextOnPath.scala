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

class TextOnPath extends BaseScala {

  def txt = text("Hallo",1)
  def rtext = scale(1/getBBoxS(txt).width) **translate(-getBBoxS(txt).left.x,0) ** txt
  def draw(): Unit = {  
    def p = quad(Point(-1,0), mouse, Point(1,0))
    draw(drawing(sweep(p) ** fill(rtext,fillColor(black)))) 
  }


}