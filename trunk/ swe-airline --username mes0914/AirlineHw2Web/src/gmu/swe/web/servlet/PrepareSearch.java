/*
 * Created by: Matt Snyder
 */
package gmu.swe.web.servlet;

import gmu.swe.constant.Constants;
import gmu.swe.exception.DataAccessException;
import gmu.swe.exception.ValidationException;
import gmu.swe.service.ejb.TravelAgentEjbRemote;
import gmu.swe.util.ResourceUtil;
import gmu.swe.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that prepares the Request object for the search page.
 */
public class PrepareSearch extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PrepareSearch() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response) Adds all of the airports in the system to the Request
	 *      object so the user can see the exiting airports.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatch = request.getRequestDispatcher("jsp/search.jsp");

		Collection<String> airports = null;
		try {
			airports = getAllAirports();
			request.setAttribute("airports", airports);
			request.setAttribute("error", request.getAttribute("error"));

		} catch (ValidationException e) {
			String errorMessage = StringUtils.getFormattedMessages(e.getErrorMessages());
			request.setAttribute("error", errorMessage);
		}

		request.setAttribute("airports", airports);
		request.setAttribute("error", request.getAttribute("error"));
		dispatch.forward(request, response);
	}

	/**
	 * Returns all the airports in the system.
	 * 
	 * @return Collection of all the airport codes in the system.
	 * @throws ValidationException
	 *             Thrown if a problem occurs in communicating with the remote
	 *             EJB.
	 */
	private Collection<String> getAllAirports() throws ValidationException {
		try {
			TravelAgentEjbRemote ejbRef = (TravelAgentEjbRemote) ResourceUtil.getInitialContext().lookup(
					Constants.EAR_FILE_NAME + "/TravelAgentEjb/remote");

			return ejbRef.getAllAirports();
		} catch (NamingException e) {
			e.printStackTrace();
			ValidationException ve = new ValidationException();
			ve.addErrorMessage("Server error occured during EJB lookup.");
			throw ve;
		} catch (DataAccessException e) {
			ValidationException ve = new ValidationException();
			ve.addErrorMessage("Server error occured while looking up existing airports.");
			throw ve;
		}
	}
}
