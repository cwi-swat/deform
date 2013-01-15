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
import deform._
import deform.library.Drawings._

class WaveTriangle extends BaseScala {

  def triangle = {
    val a = Point(0,0)
    val b = Point(1,0.5)
    val c = Point(1,-0.5)
    shape(List(join(List(line(a, b), line(b, c), line(c, a)))))
  }

  def gradient(x : Double) = {
    val l = x - floor(x)
    if( l < 0.5) lerp(red,l*2,yellow)
    else lerp(yellow, 2*(l-0.5),red)
  }
  
  def tritex = texture(p => {
    val i = 2 * abs(p.y) /p.x
    lerp(gradient(p.x * 10),i*i,black )
  })
  
  def ftriangle = fill(triangle,tritex)
  
   def circ = shape(List(path(t => Point(sin(t*2*Pi),cos(t*2*Pi)))))
  def pacman = subtract(circ,triangle)
  
  def radgrad = texture(p=> lerp(red,p.normSquared,black))
  
  def wave = transformation(p  => Point(p.x + sin(p.y*10)/10.0,p.y ), p  => Point(p.x -sin(p.y*10)/10,p.y))
  
  def draw(): Unit = {  
    draw(scale(0.3) ** drawing(transform(translate(mouse),wave) ** fill(pacman,radgrad)))
    
  }


}


object MainWaveTriangle{
  def main(args: Array[String]): Unit = {
     new WaveTriangle()
   }
}