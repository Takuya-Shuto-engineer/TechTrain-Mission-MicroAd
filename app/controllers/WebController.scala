package controllers

import javax.inject.Inject
import play.api.libs.json._
import play.api.mvc._

class WebController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def protein(): Action[AnyContent] = Action{
      Ok(views.html.protein())
  }

}