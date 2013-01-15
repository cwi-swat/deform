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

class PaperExample extends BaseScala {

  def spiral = path(t => {
    val s = 6 * Pi * (1+t)
    val f = 1.0/50.0 * exp(s/10)
    Point(f*cos(s),f*sin(s))
  })
  
  def circ = shape(List(path(t => Point(sin(t*2*Pi),cos(t*2*Pi)))))

  def triangle = {
    val a = Point(0,0)
    val b = Point(1,0.5)
    val c = Point(1,-0.5)
    shape(List(join(List(line(a, b), line(b, c), line(c, a)))))
  }
  def pacman = subtract(circ,triangle)
  
  def radgrad = texture(p=> lerp(red,p.normSquared,black))

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
  
  def wave = transformation(p  => Point(p.x + sin(p.y),p.y ), p  => Point(p.x -sin(p.y),p.y))
  def scaledWave = transform(scale(1.0/30.0),wave)
  
  def fspir = sweep(spiral) ** scale(1,1.0/40.0) ** ftriangle
  def all = List( 
      drawing(fill(circ,fillColor(black))),
      drawing(fill(pacman,fillColor(black))),
      drawing(fill(triangle,fillColor(black))),
      drawing(fill(stroke(spiral,1/200.0),fillColor(black))),
      drawing(fill(pacman,radgrad)),
      drawing(fill(triangle,tritex)),
      drawing(scaledWave ** fill(triangle,tritex)),
      drawing(fspir),
      drawing(scaledWave ** fspir)
      )
  
  var cur = 0
    
  override def handleMouseClick(button: Int) = {
    cur = (cur + 1) % all.length
  }

  
  def draw(): Unit = {
    
    draw(scale(0.7) ** all(cur))
    
  }

}

object PaperExampleMain{
  def main(args: Array[String]): Unit = {
     new PaperExample()
   }
}