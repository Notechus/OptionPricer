package cs.uni.tradeapp.pricer.pricing;

import cs.uni.tradeapp.utils.data.RiskCalculationTaskObject;
import cs.uni.tradeapp.utils.data.RiskCalculationTaskResult;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Objects;

/**
 * Created by Notechus on 06/10/2016.
 */
public class OptionCalculator
{
	private double volatility;
	private final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssX";
	private SimpleDateFormat simpleDateFormat;
	private final long MS_YEAR = 31556952000L;

	/**
	 * @param volatility Volatility value
	 */
	public OptionCalculator(double volatility)
	{
		this.volatility = volatility;
		this.simpleDateFormat = new SimpleDateFormat(ISO_DATE_FORMAT);

	}

	/**
	 * Calculates option value in cash units and delta
	 *
	 * @param task   Option which will be priced
	 * @param result result object with delta and price
	 */
	public void Price(RiskCalculationTaskObject task, RiskCalculationTaskResult result) throws ParseException
	{
		double s0 = task.getNewPrice();
		double k = task.getOption().getStrike();
		Date d = simpleDateFormat.parse(task.getOption().getMaturity());
		double tmp = (d.getTime() - new Date().getTime());
		double t = tmp / MS_YEAR;
		double d1 = (StrictMath.log(s0 / k) + volatility * volatility * t / 2) / (volatility * StrictMath.sqrt(t));
		double d2 = (StrictMath.log(s0 / k) - volatility * volatility * t / 2) / (volatility * StrictMath.sqrt(t));
		result.setDelta(this.calculateDelta(d1, task.getOption().getDirection()));
		result.setPresentValue(this.calculateValue(k, s0, d1, d2, task.getOption().getDirection()));
	}

	/**
	 * Calculates delta geek in BS model
	 *
	 * @param d1   1st parameter for normal cdf
	 * @param type trade type
	 * @return calculated delta geek value
	 */
	public double calculateDelta(double d1, String type)
	{
		NormalDistribution norm = new NormalDistribution();
		if (Objects.equals(type, "CALL"))
		{
			return norm.cumulativeProbability(d1);
		} else if (Objects.equals(type, "PUT"))
		{
			return norm.cumulativeProbability(-d1);
		} else
		{
			throw new ArithmeticException("An error occurred while calculating delta: Option type should be PUT/CALL.");
		}
	}

	/**
	 * Calculates value in BS model
	 *
	 * @param strike option strike
	 * @param s0     stock price
	 * @param d1     parameter for normal cdf
	 * @param d2     2nd parameter for normal cdf
	 * @param type   trade type
	 * @return calculated trade value
	 */
	public double calculateValue(double strike, double s0, double d1, double d2, String type)
	{
		NormalDistribution norm = new NormalDistribution();
		if (Objects.equals(type, "CALL"))
		{
			double a = s0 * norm.cumulativeProbability(d1);
			double b = strike * norm.cumulativeProbability(d2);
			return a - b;
		} else if (Objects.equals(type, "PUT"))
		{
			double a = s0 * norm.cumulativeProbability(-d1);
			double b = strike * norm.cumulativeProbability(-d2);
			return b - a;
		} else
		{
			throw new ArithmeticException("An error occurred while calculating value: Option type should be PUT/CALL.");
		}
	}

}
