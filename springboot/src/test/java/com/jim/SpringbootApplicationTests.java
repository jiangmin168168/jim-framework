package com.jim;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootApplicationTests {
	private Logger logger = LoggerFactory.getLogger(getClass().getName());

	@Test
	public void contextLoads() {
	}

	@Test
	public void testDeadLock() throws Exception{
		Object lockA=new Object();
		Object lockB=new Object();

		MyThreadA myThreadA=new MyThreadA(lockA,lockB);
		MyThreadB myThreadB=new MyThreadB(lockA,lockB);
		Thread threadA=new Thread(myThreadA);
		Thread threadB=new Thread(myThreadB);
		threadA.start();
		Thread.sleep(1000);
		threadB.start();
		logger.info("testDeadLock end");
		Assert.assertTrue(true);

	}

	@Test
	public void testJoinThread() throws Exception{
		Object lock=new Object();
		int count=5;
		StarData starData=new StarData(count);
		Thread thread0=new Thread(new ThreadJoin(count,0,starData));

		Thread thread1=new Thread(new ThreadJoin(count,1,starData));

		Thread thread2=new Thread(new ThreadJoin(count,2,starData));

		Thread thread3=new Thread(new ThreadJoin(count,3,starData));

		Thread thread4=new Thread(new ThreadJoin(count,4,starData));
		thread0.start();
		thread1.start();
		thread2.start();
		thread3.start();
		thread4.start();
		logger.info("test end");
		Thread.sleep(3000);
		Assert.assertTrue(true);
	}

	@Test
	public void testConditionTask() throws Exception{
		ConditionTask conditionTask=new ConditionTask();
		conditionTask.run();
		Thread.sleep(3000);
		Assert.assertTrue(true);

	}


	class MyThreadA implements Runnable{
		private Object lockA;
		private Object lockB;
		public MyThreadA(Object lockA,Object lockB){
			this.lockA=lockA;
			this.lockB=lockB;
		}

		@Override
		public void run() {
			synchronized (this.lockA){
				System.out.print("myThreadA started");
				try {
					Thread.sleep(1000);
					synchronized (this.lockB){
						System.out.print("sync lockB");
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.print("myThreadA stopped");
			}
		}

		public void testDeadLockC() throws Exception{


			MyThreadC myThreadC=new MyThreadC();
			myThreadC.setFlag(0);
			Thread threadA=new Thread(myThreadC);

			threadA.start();
			Thread.sleep(1000);

			myThreadC.setFlag(1);
			Thread threadB=new Thread(myThreadC);
			threadB.start();

			System.out.print("end");


		}
	}
	class MyThreadB implements Runnable{
		private Object lockA;
		private Object lockB;
		public MyThreadB(Object lockA,Object lockB){
			this.lockA=lockA;
			this.lockB=lockB;
		}

		@Override
		public void run() {
			synchronized (this.lockB){
				System.out.print("myThreadB started");
				try {
					Thread.sleep(1000);
					synchronized (this.lockA){
						System.out.print("sync lockA");
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				System.out.print("myThreadB stopped");
			}
		}
	}

	class StarData{
		private volatile int flag=0;

		private int count;

		public StarData(int count){
			this.count=count;
		}

		public int getFlag() {
			return flag;
		}

		public void setFlag(int flag) {
			if(flag>=count){
				flag=0;
			}
			this.flag = flag;
		}


	}

	class ThreadJoin implements Runnable{

		private int count;

		private int flag;

		private StarData starData;

		public ThreadJoin(int count,int flag,StarData starData){
			this.count=count;
			this.flag=flag;
			this.starData=starData;

		}

		@Override
		public void run() {
			while (true) {
				if (this.flag != this.starData.getFlag()) {
					try {
						synchronized (this) {
							wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					System.out.print("value is:" + flag + Thread.currentThread().getId());
					this.starData.setFlag(++flag);
					synchronized (this) {
						this.notifyAll();
					}
				}
			}
		}
	}

	class ConditionTask{
		private Lock lock=new ReentrantLock();
		private Condition conditionA=lock.newCondition();
		private Condition conditionB=lock.newCondition();
		private Condition conditionC=lock.newCondition();
		private volatile int flag=1;
		public void run(){
			Thread threadA=new Thread(){
				public void run(){
					lock.lock();
					while (flag!=1){
						try {
							conditionA.await();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					System.out.print("AAAAA");
					flag=2;
					conditionB.signalAll();
					lock.unlock();
				}
			};
			Thread threadB=new Thread(){
				public void run(){
					lock.lock();
					while (flag!=2){
						try {
							conditionB.await();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					System.out.print("BBBBB");
					flag=3;
					conditionC.signalAll();
					lock.unlock();
				}
			};
			Thread threadC=new Thread(){
				public void run(){
					lock.lock();
					while (flag!=3){
						try {
							conditionC.await();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					System.out.print("CCCCC");
					flag=1;
					conditionA.signalAll();
					lock.unlock();
				}
			};
			int threadCount=5;
			Thread[] threadsA=new Thread[threadCount];
			Thread[] threadsB=new Thread[threadCount];
			Thread[] threadsC=new Thread[threadCount];
			for(int i=0;i<threadCount;i++){
				threadsA[i]=new Thread(threadA);
				threadsB[i]=new Thread(threadB);
				threadsC[i]=new Thread(threadC);
				threadsA[i].start();
				threadsB[i].start();
				threadsC[i].start();
			}
		}

	}


}
class MyThreadC implements Runnable{
	private Object lockA=new Object();
	private Object lockB=new Object();
	private int flag;

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public MyThreadC(){
	}

	@Override
	public void run() {
		if(this.flag==0) {
			synchronized (this.lockA) {
				System.out.print("myThreadA started");
				try {
					Thread.sleep(1000);
					synchronized (this.lockB) {
						System.out.print("sync lockB");
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				System.out.print("myThreadA stopped");
			}
		}
		if(this.flag==1) {
			synchronized (this.lockB) {
				System.out.print("myThreadB started");
				try {
					Thread.sleep(1000);
					synchronized (this.lockA) {
						System.out.print("sync lockA");
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				System.out.print("myThreadB stopped");
			}
		}
	}
}
