package com.jpbnetsoftware.homebudget.service

import javax.servlet.annotation.WebFilter
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import javax.servlet.{FilterChain, ServletRequest, ServletResponse}

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.filter.GenericFilterBean

import scala.beans.BeanProperty

/**
  * Created by pburzynski on 22/03/2016.
  */
@WebFilter(urlPatterns = Array[String]("/*"))
class AppSecurityFilter extends GenericFilterBean {

  @BeanProperty
  @Autowired
  var authenticationManager: AuthenticationManager = _

  def AuthorizationHeader = "Authorization"

  override def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain): Unit = {

    val req = request.asInstanceOf[HttpServletRequest]
    val resp = response.asInstanceOf[HttpServletResponse]

    val headers = req.getHeaders("authorization")

    if (!headers.hasMoreElements) {
      resp.setStatus(HttpServletResponse.SC_FORBIDDEN)
      return
    }

    if (!authenticationManager.authenticate(headers.nextElement())) {
      resp.setStatus(HttpServletResponse.SC_FORBIDDEN)
      return
    }

    chain.doFilter(request, response)
  }
}
