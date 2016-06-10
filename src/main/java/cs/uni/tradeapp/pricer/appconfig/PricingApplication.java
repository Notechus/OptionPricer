package cs.uni.tradeapp.pricer.appconfig;

import cs.uni.tradeapp.pricer.pricing.OptionCalculator;
import cs.uni.tradeapp.pricer.pricing.OptionPricer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Notechus on 06/10/2016.
 */
@ComponentScan("cs.uni.tradeapp.pricer")
public class PricingApplication
{
	private static final Logger log = LoggerFactory.getLogger(PricingApplication.class);
	private static final Integer PERIOD = 1500;

	private static void periodicInvocation(final OptionPricer pricer)
	{
		new Timer().schedule(new TimerTask()
		{

			public void run()
			{
				try
				{
					pricer.watchAndPrice();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}, 0, PERIOD);
	}

	private static void joinTheSubThread()
	{
		try
		{
			Thread.currentThread().join();
		} catch (InterruptedException e)
		{
			log.warn("Interrupted", e);
		}
	}

	public static void main(String[] args)
	{
		ConfigurableApplicationContext context = new AnnotationConfigApplicationContext("cs.uni.tradeapp.pricer");
		periodicInvocation(context.getBean(OptionPricer.class));
		joinTheSubThread();
		context.close();
	}
}
