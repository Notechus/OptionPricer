package cs.uni.tradeapp.pricer.appconfig;

import cs.uni.tradeapp.utils.spring.RestServiceDetails;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by Notechus on 05/29/2016.
 */
@Component
public class ServiceDiscoveryRegister
{
	@Autowired
	private ServiceDiscovery<RestServiceDetails> discovery;

	@Autowired
	private ServiceInstance<RestServiceDetails> serviceInstance;

	@PostConstruct
	public void init() throws Exception
	{
		discovery.registerService(serviceInstance);
	}
}
