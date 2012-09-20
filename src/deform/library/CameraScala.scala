package deform.library

import deform.Library._
import deform.Library.Transforms._
import deform.Library.Paths._
import deform.Library.Shapes._
import deform.Library.Textures._
import deform.Library.TexturedShapes._
import deform.Library.Colors._
import deform._

class CameraScala {
  
  var trans = IdentityTransformation() : Transformation
  var zoomd = 1 : Double
  var shrink = 1 : Double
  var leftTop = Point(0,0)
  var ref = null : Point
  
  def zoom(zoomOnWorld : Point, zoomDelta :Double ) ={
    val factor = 1/zoomDelta
    val zoomLocal = zoomOnWorld * shrink
    leftTop = leftTop + zoomLocal * (1- factor) 
    shrink = shrink * factor
    zoomd = zoomd * zoomDelta
    trans = scale(zoomd) ** translate(leftTop.negate)
  }
  
  def move(delta : Point) = {
    leftTop = leftTop + delta * shrink
    trans = scale(zoomd) ** translate(leftTop.negate)
  }
  
  

}