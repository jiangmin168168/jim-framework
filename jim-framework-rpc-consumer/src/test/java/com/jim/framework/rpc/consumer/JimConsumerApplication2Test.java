package com.jim.framework.rpc.consumer;

import com.jim.framework.rpc.api.model.Product;
import com.jim.framework.rpc.consumer.config.ConsumerConfiguration;
import com.jim.framework.rpc.consumer.service.ProductCommentService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootConfiguration
@SpringBootTest(classes = {ConsumerConfiguration.class})
public class JimConsumerApplication2Test {

	@Autowired
	private ProductCommentService productCommentService;

	@Test
	public void testProductService() throws Exception {
		Long productId=2L;
		Product result=this.productCommentService.getById(productId);
		Assert.assertTrue(null!=result&&result.getId().equals(productId));
	}

}
