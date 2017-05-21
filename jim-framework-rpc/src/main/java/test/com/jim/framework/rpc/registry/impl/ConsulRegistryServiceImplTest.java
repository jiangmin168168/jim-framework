package test.com.jim.framework.rpc.registry.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * ConsulRegistryService Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>���� 19, 2017</pre>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootConfiguration
@SpringBootTest(classes = {RegistryServiceConfig.class})
public class ConsulRegistryServiceImplTest {



    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: register(RpcURL url)
     */
//    @Test
//    public void testRegister() throws Exception {
////TODO: Test goes here...
//        ConsulRegistryService consulRegistryService=new ConsulRegistryService();
//        consulRegistryService.register(null);
//        List<RpcURL> urls=consulRegistryService.getUrls("192.168.21.128",8500);
//        Assert.assertTrue(null!=urls);
//    }

    /**
     * Method: unregister(RpcURL url)
     */
    @Test
    public void testUnregister() throws Exception {
//TODO: Test goes here... 
    }

    /**
     * Method: getUrls()
     */
    @Test
    public void testGetUrls() throws Exception {
//TODO: Test goes here... 
    }


} 
