package client;

import com.sinjinsong.toy.autoconfig.beanpostprocessor.AbstractRPCBeanPostProcessor;
import com.sinjinsong.toy.config.ReferenceConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import service.BenchmarkService;
import service.TestObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author sinjinsong
 * @date 2018/8/6
 */
@Component
@Slf4j
public class ConcurrentTest implements ApplicationContextAware {
    private ApplicationContext ctx;
    private long qps;
    private long rt;
    private long maxRT;
    private long benchmarkStart;
    private long benchmarkEnd;
    private List<InvokeResult> invokeResults;

    @Data
    private static class InvokeResult {
        private long begin;
        private long end;
        private boolean success;
    }

    public void run(int clientCount, int requestsTotal) throws InterruptedException {
        invokeResults = new ArrayList<>(requestsTotal);
        ExecutorService executorService = Executors.newFixedThreadPool(clientCount);
        CountDownLatch countDownLatch = new CountDownLatch(clientCount);
        int requestsPerClient = requestsTotal / clientCount;
        ReferenceConfig<BenchmarkService> config = ReferenceConfig.createReferenceConfig(
                BenchmarkService.class.getName(),
                BenchmarkService.class,
                false,
                false,
                false,
                3000,
                "",
                1,
                false,
                null
        );
        AbstractRPCBeanPostProcessor.initConfig(ctx, config);
        List<BenchmarkService> clients = new ArrayList<>(clientCount);
        for (int i = 0; i < clientCount; i++) {
            clients.add(config.getForBenchmark());
        }
        List<Runnable> tasks = new ArrayList<>();
        TestObject testObject = new TestObject();
        for (int i = 0; i < clientCount; i++) {
            BenchmarkService client = clients.get(i);

            Runnable r = () -> {
                for (int j = 0; j < requestsPerClient; j++) {
                    InvokeResult invokeResult = new InvokeResult();
                    invokeResult.setBegin(System.currentTimeMillis());
                    TestObject object = null;
                    try {
                        object = client.updateField(testObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                        invokeResult.setSuccess(Boolean.FALSE);
                    }
                    invokeResult.setEnd(System.currentTimeMillis());
                    if (object.getI() == 10) {
                        invokeResult.setSuccess(Boolean.TRUE);
                    } else {
                        invokeResult.setSuccess(Boolean.FALSE);
                    }
                    invokeResults.add(invokeResult);
                }
                countDownLatch.countDown();
            };
            tasks.add(r);
        }
        benchmarkStart = System.currentTimeMillis();
        for (int i = 0; i < clientCount; i++) {
            executorService.submit(tasks.get(i));
        }
        countDownLatch.await();
        benchmarkEnd = System.currentTimeMillis();
        qps = requestsTotal / ((benchmarkEnd - benchmarkStart) / 1000);
        long sum = invokeResults.stream().mapToLong(result -> result.getEnd() - result.getBegin()).sum();
        rt = sum / requestsTotal;
        maxRT = invokeResults.stream().mapToLong(result -> result.getEnd() - result.getBegin()).max().getAsLong();
        log.info("QPS:{},RT:{},MAXRT:{}",qps,rt,maxRT);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
