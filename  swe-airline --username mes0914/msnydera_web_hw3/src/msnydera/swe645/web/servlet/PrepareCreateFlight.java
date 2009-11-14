/*
 * Created by: Matt Snyder
 */
package msnydera.swe645.web.servlet;

import java.io.IOException;
import java.util.Collection;

import javax.naming.NamingException;
import javax.security.auth.login.LoginException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import msnydera.swe645.constant.Constants;
import msnydera.swe645.domain.AirlineUser;
import msnydera.swe645.domain.Airplane;
import msnydera.swe645.domain.Flight;
import msnydera.swe645.exception.DataAccessException;
import msnydera.swe645.exception.ValidationException;
import msnydera.swe645.service.ejb.HeadquartersEjbRemote;
import msnydera.swe645.util.ResourceUtil;
import msnydera.swe645.util.StringUtils;

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

		AirlineUser user = ResourceUtil.getLoggedInUser(request.getSession());

		if (user == null) {
			dispatch = request.getRequestDispatcher("jsp/login.jsp");
			request.setAttribute("error", "Please login before accessing the system.");

			dispatch.forward(request, response);

			return;
		}

		try {
			Collection<Airplane> airplanes = this.airplaneServlet.getExistingAirplanes(user);
			Collection<String> airports = this.airportServlet.getExistingAirports(user);
			Collection<Flight> flights = getExistingFlights(user);

			request.setAttribute("airplanes", airplanes);
			request.setAttribute("airports", airports);
			request.setAttribute("flights", flights);
			request.setAttribute("addedFlight", request.getAttribute("addedFlight"));
			request.setAttribute("error", request.getAttribute("error"));
		} catch (ValidationException e) {
			String errorMessage = StringUtils.getFormattedMessages(e.getErrorMessages());
			request.setAttribute("error", errorMessage);
		} catch (LoginException e) {
			dispatch = request.getRequestDispatcher("jsp/headquartersMenu.jsp");

			request.setAttribute("error", "Your role does not allow you to perform this action.");

		} catch (Exception e) {
			dispatch = request.getRequestDispatcher("jsp/headquartersMenu.jsp");

			request.setAttribute("error", "Your role does not allow you to perform this action.");
		}

		dispatch.forward(request, response);
	}

	/**
	 * Returns all the flights in the system.
	 * 
	 * @param user
	 *            AirlineUser to use
	 * 
	 * @return Collection of all the flights in the system.
	 * @throws ValidationException
	 *             Thrown if there is a problem in communicating with the remote
	 *             EJB.
	 * @throws ValidationException
	 *             Thrown to help with messages
	 * @throws LoginException
	 *             Thrown if a problem occurs when logging the user in.
	 * @throws Exception
	 *             Thrown if an error occurs with the connection to the DB with
	 *             the user.
	 */
	private Collection<Flight> getExistingFlights(AirlineUser user) throws ValidationException, LoginException,
			Exception {
		try {
			// HeadquartersEjbRemote ejbRef = (HeadquartersEjbRemote)
			// ResourceUtil.getInitialContext().lookup(
			// Constants.EAR_FILE_NAME + "/HeadquartersEjb/remote");
			HeadquartersEjbRemote ejbRef = (HeadquartersEjbRemote) ResourceUtil.getLoggedInContext(user).lookup(
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
