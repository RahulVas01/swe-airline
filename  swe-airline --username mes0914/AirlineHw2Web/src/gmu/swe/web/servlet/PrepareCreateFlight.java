/*
 * Created by: Matt Snyder
 */
package gmu.swe.web.servlet;

import gmu.swe.constant.Constants;
import gmu.swe.domain.Airplane;
import gmu.swe.domain.Flight;
import gmu.swe.exception.DataAccessException;
import gmu.swe.exception.ValidationException;
import gmu.swe.service.ejb.HeadquartersEjbRemote;
import gmu.swe.util.ResourceUtil;
import gmu.swe.util.StringUtils;

import java.io.IOException;
import java.util.Collection;

import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet used to prepare the Request object for the create flight page.
 */
public class PrepareCreateFlight extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private PrepareAddAirplane airplaneServlet;
	private PrepareAddAirport airportServlet;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PrepareCreateFlight() {
		super();

		this.airplaneServlet = new PrepareAddAirplane();
		this.airportServlet = new PrepareAddAirport();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response) Adds all the airplanes, airports, and flights currently in
	 *      the system to the Request object to display them to the user.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		RequestDispatcher dispatch = request.getRequestDispatcher("jsp/createFlight.jsp");

		try {
			Collection<Airplane> airplanes = this.airplaneServlet.getExistingAirplanes();
			Collection<String> airports = this.airportServlet.getExistingAirports();
			Collection<Flight> flights = getExistingFlights();

			request.setAttribute("airplanes", airplanes);
			request.setAttribute("airports", airports);
			request.setAttribute("flights", flights);
			request.setAttribute("addedFlight", request.getAttribute("addedFlight"));
			request.setAttribute("error", request.getAttribute("error"));
		} catch (ValidationException e) {
			String errorMessage = StringUtils.getFormattedMessages(e.getErrorMessages());
			request.setAttribute("error", errorMessage);
		}

		dispatch.forward(request, response);
	}

	/**
	 * Returns all the flights in the system.
	 * 
	 * @return Collection of all the flights in the system.
	 * @throws ValidationException
	 *             Thrown if there is a problem in communicating with the remote
	 *             EJB.
	 */
	private Collection<Flight> getExistingFlights() throws ValidationException {
		try {
			HeadquartersEjbRemote ejbRef = (HeadquartersEjbRemote) ResourceUtil.getInitialContext().lookup(
					Constants.EAR_FILE_NAME + "/HeadquartersEjb/remote");
			return ejbRef.getAllFlights();
		} catch (NamingException e) {
			e.printStackTrace();
			ValidationException ve = new ValidationException();
			ve.addErrorMessage("Server error occured during EJB lookup.");
			throw ve;
		} catch (DataAccessException e) {
			ValidationException ve = new ValidationException();
			ve.addErrorMessage("Server error occured while retrieving all the flights.");
			throw ve;
		}
	}
}
