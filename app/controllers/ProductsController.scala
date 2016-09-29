package controllers

import play.api.mvc.Controller
import javax.inject.Inject
import javax.inject.Singleton
import play.api.mvc.Action
import models.Product
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import play.api.mvc.Flash
import play.api.data.Form
import play.api.data.Forms.{ mapping, longNumber, nonEmptyText }
import play.api.i18n.Messages

@Singleton
class ProductsController @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport {
  def list = Action { implicit request =>
    val products = Product.findAll

    Ok(views.html.products(products))
  }

  def show(ean: Long) = Action { implicit request => 
    Product.findByEan(ean).map { product => Ok(views.html.details(product)) }.getOrElse(NotFound)
  }

  def save = Action { implicit request =>
    val newProductForm = productForm.bindFromRequest()

    newProductForm.fold(

      hasErrors = { form =>
        Redirect(routes.ProductsController.newProduct()).
          flashing(Flash(form.data) +
            ("error" -> Messages("validation.errors")))
      },

      success = { newProduct =>
        Product.add(newProduct)
        val message = Messages("products.new.success", newProduct.name)
        Redirect(routes.ProductsController.show(newProduct.ean)).
          flashing("success" -> message)
      })
  }
  

  def newProduct = Action { implicit request =>
    val form = if (request.flash.get("error").isDefined)
      productForm.bind(request.flash.data)
    else
      productForm
    Ok(views.html.editProduct(form))
  }
  
    private val productForm: Form[Product] = Form(
    mapping(
      "ean" -> longNumber.verifying(
        "validation.ean.duplicate", Product.findByEan(_).isEmpty),
      "name" -> nonEmptyText,
      "description" -> nonEmptyText)
      (Product.apply)(Product.unapply))
  
}