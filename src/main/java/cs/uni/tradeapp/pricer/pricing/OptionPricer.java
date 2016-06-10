package cs.uni.tradeapp.pricer.pricing;

import com.google.gson.reflect.TypeToken;
import cs.uni.tradeapp.utils.data.RiskCalculationTaskObject;
import cs.uni.tradeapp.utils.data.RiskCalculationTaskResult;
import cs.uni.tradeapp.utils.json.JsonBuilder;
import cs.uni.tradeapp.utils.zookeeper.MyDistributedQueue;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.NoSuchElementException;

/**
 * Created by Notechus on 06/10/2016.
 */
public class OptionPricer
{
	private final Logger log = LoggerFactory.getLogger(getClass());
	private static final String QUEUE_PATH = "/trade-application/tasks/worker1";
	private static final String RESULT_PATH = "/trade-application/results/worker1";
	private double volatility;
	private OptionCalculator optionCalculator;
	private JsonBuilder jsonBuilder = new JsonBuilder();

	private CuratorFramework curator;

	private MyDistributedQueue taskQueue;

	private MyDistributedQueue resultQueue;

	public OptionPricer(CuratorFramework curator, double volatility)
	{
		this.curator = curator;
		this.volatility = volatility;
		this.optionCalculator = new OptionCalculator(this.volatility);
		this.taskQueue = new MyDistributedQueue(this.curator, QUEUE_PATH);
		this.resultQueue = new MyDistributedQueue(this.curator, RESULT_PATH);
	}

	public void watchAndPrice() throws Exception
	{
		Type type = new TypeToken<Hashtable<RiskCalculationTaskObject, RiskCalculationTaskResult>>()
		{
		}.getType();
		try
		{
			Hashtable<RiskCalculationTaskObject, RiskCalculationTaskResult> task =
					(Hashtable<RiskCalculationTaskObject, RiskCalculationTaskResult>) jsonBuilder.deserialize(
							new String(this.taskQueue.remove()), type);
			log.info("Calculating results");
			for (RiskCalculationTaskObject o : task.keySet())
			{
				optionCalculator.Price(o, task.get(o));
			}
			resultQueue.put(jsonBuilder.serialize(task, type).getBytes());
		} catch (NullPointerException e)
		{
			//log.info("Queue empty");
		} catch (NoSuchElementException e)
		{

		}
	}
}
