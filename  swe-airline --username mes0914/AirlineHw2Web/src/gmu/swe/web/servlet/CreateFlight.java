/*
 * Created by: Matt Snyder
 */
package gmu.swe.web.servlet;

import gmu.swe.constant.Constants;
import gmu.swe.domain.Flight;
import gmu.swe.exception.DataAccessException;
import gmu.swe.exception.ValidationException;
import gmu.swe.service.ejb.HeadquartersEjbRemote;
import gmu.swe.util.NumberUtils;
import gmu.swe.util.ResourceUtil;
import gmu.swe.util.StringUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet providing functionality to create a flight in the system.
 */
public class CreateFlight extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateFlight() {
		super();
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
	 *      response) Handles the user input for information required to create
	 *      a flight in the system. This method will forward the user to the
	 *      PrepareCreateFlight page when complete (from either a successful
	 *      transaction or if an error occurs).
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		RequestDispatcher dispatch = request.getRequestDispatcher("/prepareCreateFlight");

		try {
			Flight flight = getFlight(request);

			int newFlightNum = createFlight(flight);
			flight.setId(newFlightNum);

			request.setAttribute("addedFlight", flight);

		} catch (ValidationException e) {

			String errorMessage = StringUtils.getFormattedMessages(e.getErrorMessages());
			request.setAttribute("error", errorMessage);

		} catch (ParseException e) {
			e.printStackTrace();

			String errorMessage = "Please provide a date in the format MM/dd/yyyy.";
			request.setAttribute("error", errorMessage);
		}

		dispatch.forward(request, response);
	}

	/**
	 * Communicate with the remote EJB service to create a flight in the system.
	 * 
	 * @param flight
	 *            Flight to create
	 * @return Flight # of the flight created.
	 * @throws ValidationException
	 *             Thrown if a validation error occurs or if there is a problem
	 *             with communicating with the remote EJB service.
	 */
	private int createFlight(Flight flight) throws ValidationException {
		try {
			HeadquartersEjbRemote ejbRef = (HeadquartersEjbRemote) ResourceUtil.getInitialContext().lookup(
					Constants.EAR_FILE_NAME + "/HeadquartersEjb/remote");
			return ejbRef.createFlight(flight);
		} catch (NamingException e) {
			e.printStackTrace();
			ValidationException ve = new ValidationException();
			ve.addErrorMessage("Server error occured during EJB lookup.");
			throw ve;
		} catch (ValidationException e) {
			throw e;
		} catch (DataAccessException e) {
			ValidationException ve = new ValidationException();
			ve.addErrorMessage("Server error occured while attempting to add airplane.");
			throw ve;
		}
	}

	/**
	 * Binds the request parameters to a Flight object, and returns that object.
	 * 
	 * @param request
	 *            Request containing the information to bind.
	 * @return Flight object bound with the user input for the flight
	 *         information.
	 * @throws ParseException
	 *             Thrown if the flight date in the request is in a bad/invalid
	 *             format.
	 * @throws ValidationException
	 */
	private Flight getFlight(HttpServletRequest request) throws ParseException, ValidationException {
		Flight flight = new Flight();
		flight.setDepartureAirportCode(request.getParameter("departureAirport"));
		flight.setDestinationAirportCode(request.getParameter("destinationAirport"));
		flight.setDepartureDate(getDateOfTrip(request));

		if (!NumberUtils.isValidCurrency(request.getParameter("cost"))) {
			ValidationException validationException = new ValidationException();
			validationException.addErrorMessage("Please enter a valid cost for the flight (ex: 150.00)");
			throw validationException;
		}

		flight.setCost(Integer.parseInt(request.getParameter("cost")));
		flight.setAirplaneId(Integer.parseInt(request.getParameter("airplaneId")));

		return flight;
	}

	/**
	 * Returns a Date object that was set in the 'flightDate' parameter in the
	 * request.
	 * 
	 * @param request
	 *            Request containing the parameter.
	 * @return Date object of the date in the 'flightDate' parameter.
	 * @throws ParseException
	 *             Thrown if the flight date in the request is in a bad/invalid
	 *             format.
	 */
	private Date getDateOfTrip(HttpServletRequest request) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		String sDateOfTrip = (String) request.getParameter("flightDate");
		Date dateOfTrip = null;
		try {
			if (sDateOfTrip != null && !sDateOfTrip.trim().equals("")) {
				dateOfTrip = sdf.parse(sDateOfTrip);
			}
			return dateOfTrip;
		} catch (ParseException e) {
			System.out.print("'" + sDateOfTrip + "' is an invalid date.");
			throw e;
		}
	}
}
