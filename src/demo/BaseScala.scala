package demo

import deform.TexturedShape
import deform.Drawing
import deform.library.Render
import deform.Util._
import deform.AABBox
import deform.Interval

abstract class BaseScala extends DemoBase{
  
  def init() : Unit = ()
  
  def draw(tsl : Drawing) : Unit = 
    Render.render(AABBox(Interval(0,size.x),Interval(0,size.y)),g,tsl)

}