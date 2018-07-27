package multithreaded;

import java.util.*;
import java.util.concurrent.locks.*;


/**
 * A bank with a number of bank accounts.
 * @author lenovo
 *
 */

public class Bank {
	
	private final double[] accounts;
//	private Condition sufficientFunds;//条件对象
//	private Lock bankLock;//锁
	
	public Bank(int n, double initialBalance) {
		this.accounts = new double[n];
		Arrays.fill(this.accounts, initialBalance);
//		this.sufficientFunds = bankLock.newCondition();
//		this.bankLock = new ReentrantLock();
	}
	
	/**
	 * 使用Lock和Condition对象实现锁
	 * @param from
	 * @param to
	 * @param amount
	 * @throws InterruptedException
	 */
	/*public void transfer(int from, int to, double amount) throws InterruptedException {
		this.bankLock.lock();
		try {
			while (accounts[from] < amount) {
				//条件对象，如果该临界区无法则行，则把此线程进入等待集。
				this.sufficientFunds.await();
			}
			//currentThread()返回在最近执行的线程的对象的引用
			System.out.println(Thread.currentThread());
			this.accounts[from] -= amount;
			System.out.printf(" %10.2f from %d to %d", amount, from, to);
			this.accounts[to] += amount;
			System.out.printf(" Total Balance:%10.2f%n", getTotalBalance());
			//通知所有正在等待的线程，进行竞争。
			this.sufficientFunds.signalAll();
		}
		finally {
			this.bankLock.unlock();
		}
	}*/
	
	/**
	 * 使用synchronized关键字实现锁
	 * @param from
	 * @param to
	 * @param amount
	 * @throws InterruptedException
	 */
	public synchronized void transfer(int from, int to, double amount) throws InterruptedException {
		while (accounts[from] < amount) {
			//条件对象，如果该临界区无法则行，则把此线程进入等待集。
			this.wait();
		}
		//currentThread()返回在最近执行的线程的对象的引用
		System.out.println(Thread.currentThread());
		this.accounts[from] -= amount;
		System.out.printf(" %10.2f from %d to %d", amount, from, to);
		this.accounts[to] += amount;
		System.out.printf(" Total Balance:%10.2f%n", getTotalBalance());
		//通知所有正在等待的线程，进行竞争。
		this.notifyAll();
	}
	
	/**
	 * 使用Lock和Condition类实现锁
	 * @return
	 */
	/*public double getTotalBalance() {
		bankLock.lock();
		try {
			double sum = 0;
			for (double a : this.accounts) {
				sum += a;
			}
			return sum;
		}
		finally {
			bankLock.unlock();
		}
	}*/
	
	/**
	 * 使用synchronized关键字实现锁
	 * @return
	 */
	public synchronized double getTotalBalance() {
		double sum = 0;
		for (double a : this.accounts) {
			sum += a;
		}
		return sum;
	}
	
	
	public int size() {
		return this.accounts.length;
	}
	
	
}
