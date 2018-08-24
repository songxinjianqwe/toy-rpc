package com.sinjinsong.benchmark.base.client;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.sinjinsong.benchmark.base.domain.User;
import com.sinjinsong.benchmark.base.service.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author sinjinsong
 * @date 2018/8/6
 */
@Slf4j
public abstract class AbstractClient {
    private int threads;
    private int requestsTotal;
    private int requestsPerThread;
    private ExecutorService executorService;
    private UserService userService;
    private int measurementIterations;

    protected abstract UserService getUserService();

//      ReferenceConfig<BenchmarkService> config = ReferenceConfig.createReferenceConfig(
//                BenchmarkService.class.getName(),
//                BenchmarkService.class,
//                false,
//                false,
//                false,
//                3000,
//                "",
//                1,
//                false,
//                null
//        );
//        AbstractRPCBeanPostProcessor.initConfig(ctx, config);
//        List<BenchmarkService> clients = new ArrayList<>(threads);
//        for (int i = 0; i < threads; i++) {
//            clients.add(config.getForBenchmark());
//        }
//        List<Runnable> tasks = new ArrayList<>();
//        TestObject testObject = new TestObject();
//        for (int i = 0; i < threads; i++) {
//            BenchmarkService client = clients.get(i);
//
//
//            tasks.add(r);
//        }

    public void run(int threads, int requestsTotal, int measurementIterations) {
        this.threads = threads;
        this.requestsTotal = requestsTotal;
        this.requestsPerThread = requestsTotal / threads;
        this.userService = getUserService();
        this.executorService = Executors.newFixedThreadPool(threads);
        this.measurementIterations = measurementIterations;
        createUser();
//        existUser();
//        getUser();
//        listUser();
    }

    @Data
    public class BenchmarkResult {
        @CsvBindByName(column = "Time(ms)")
        private String mills;
        @CsvBindByName(column = "QPS(ms)")
        private String qps;
        @CsvBindByName(column = "AVG_RT(ms)")
        private String avgRt;
        @CsvBindByName(column = "P90(ms)")
        private String p90;
        @CsvBindByName(column = "P99(ms)")
        private String p99;
        @CsvBindByName(column = "P999(ms)")
        private String p999;
        @CsvBindByName(column = "Type")
        private String index;
        
        private double _mills;
        private double _qps;
        private double _avgRt;
        private double _p90;
        private double _p99;
        private double _p999;

        public BenchmarkResult() {
        }

        public BenchmarkResult(int index,long nanos, List<Long> rts) {
            this.index = "NORMAL-" + index;
            double mills = 1.0 * nanos / 1000000;
            // 每毫秒的处理请求数
            double qps = 1.0 * requestsTotal * 1000000 / nanos;
            // 毫秒
            double avgRt = 1.0 * rts.stream().mapToLong(x -> x).sum() / 1000000 / requestsTotal;
            Collections.sort(rts);

            this._mills = mills;
            this._qps = qps;
            this._avgRt = avgRt;
            this._p90 = 1.0 * rts.get((int) (rts.size() * 0.9)) / 1000000;
            this._p99 = 1.0 * rts.get((int) (rts.size() * 0.99)) / 1000000;
            this._p999 = 1.0 * rts.get((int) (rts.size() * 0.999)) / 1000000;

            this.mills = String.format("%.3f", _mills).trim();
            this.qps = String.format("%.3f", _qps);
            this.avgRt = String.format("%.3f ", _avgRt);
            this.p90 = String.format("%.3f", _p90);
            this.p99 = String.format("%.3f", _p99);
            this.p999 = String.format("%.3f", _p999);
        }
    }

    public BenchmarkResult avgBenchmarkResult(List<BenchmarkResult> benchmarkResults) {
        BenchmarkResult result = new BenchmarkResult();
        result.index = "AVG";
        result.mills = String.format("%.3f", benchmarkResults.stream().mapToDouble(BenchmarkResult::get_mills).average().getAsDouble()).trim();

        result.qps = String.format("%.3f", benchmarkResults.stream().mapToDouble(BenchmarkResult::get_qps).average().getAsDouble());

        result.avgRt = String.format("%.3f ", benchmarkResults.stream().mapToDouble(BenchmarkResult::get_avgRt).average().getAsDouble());
        result.p90 = String.format("%.3f", benchmarkResults.stream().mapToDouble(BenchmarkResult::get_p90).average().getAsDouble());
        result.p99 = String.format("%.3f", benchmarkResults.stream().mapToDouble(BenchmarkResult::get_p99).average().getAsDouble());

        result.p999 = String.format("%.3f", benchmarkResults.stream().mapToDouble(BenchmarkResult::get_p999).average().getAsDouble());
        return result;
    }

    
    private void createUser() {
        try {
            Path benchmark = Paths.get(System.getProperty("user.home"), "benchmark", "createUser.csv");
            final Path parent = benchmark.getParent();
            if (parent != null) // null will be returned if the path has no parent
                Files.createDirectories(parent);
            if (!Files.exists(benchmark)) {
                Files.createFile(benchmark);
            }
            BufferedWriter writer = Files.newBufferedWriter(benchmark, StandardOpenOption.WRITE);

            StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withEscapechar('\\').build();
            log.info("----------------------------------------------------------------------------");
            log.info("createUser started");
            List<BenchmarkResult> results = new ArrayList<>();
            for (int i = 0; i < measurementIterations; i++) {
                CountDownLatch countDownLatch = new CountDownLatch(threads);

                User user = new User();
                long id = 1L;
                user.setId(id);
                user.setName("Doug Lea" + id);
                user.setSex(1);
                user.setBirthday(LocalDate.of(1968, 12, 8));
                user.setEmail("dong.lea@gmail.com" + id);
                user.setMobile("18612345678" + id);
                user.setAddress("北京市 中关村 中关村大街1号 鼎好大厦 1605" + id);
                user.setIcon("https://www.baidu.com/img/bd_logo1.png" + id);
                user.setStatus(1);
                user.setCreateTime(LocalDateTime.now());
                user.setUpdateTime(user.getCreateTime());

                List<Integer> permissions = new ArrayList<Integer>(
                        Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 19, 88, 86, 89, 90, 91, 92));
                user.setPermissions(permissions);

                List<Long> rts = new Vector<>(requestsTotal);
                Runnable r = () -> {
                    for (int j = 0; j < requestsPerThread; j++) {
                        long begin = System.nanoTime();
                        try {
                            userService.createUser(user);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        rts.add(System.nanoTime() - begin);
                    }
                    countDownLatch.countDown();
                };


                for (int k = 0; k < threads; k++) {
                    executorService.submit(r);
                }
                long benchmarkStart = System.nanoTime();
                countDownLatch.await();
                long nanos = System.nanoTime() - benchmarkStart;
                results.add(new BenchmarkResult(i,nanos, rts));
            }
            results.add(avgBenchmarkResult(results));
            beanToCsv.write(results);
            writer.close();
            log.info("createUser end");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (CsvDataTypeMismatchException e) {
            e.printStackTrace();
        } catch (CsvRequiredFieldEmptyException e) {
            e.printStackTrace();
        }
    }

    private void existUser() {
        try {
            Path benchmark = Paths.get(System.getProperty("user.home"), "benchmark", "existUser.csv");
            final Path parent = benchmark.getParent();
            if (parent != null) // null will be returned if the path has no parent
                Files.createDirectories(parent);
            if (!Files.exists(benchmark)) {
                Files.createFile(benchmark);
            }
            BufferedWriter writer = Files.newBufferedWriter(benchmark, StandardOpenOption.WRITE);

            StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withEscapechar('\\').build();

            log.info("----------------------------------------------------------------------------");
            log.info("existUser started");
            List<BenchmarkResult> results = new ArrayList<>();
            for (int i = 0; i < measurementIterations; i++) {
                CountDownLatch countDownLatch = new CountDownLatch(threads);
                List<Long> rts = new Vector<>(requestsTotal);
                Runnable r = () -> {
                    for (int j = 0; j < requestsPerThread; j++) {
                        long begin = System.nanoTime();
                        try {
                            userService.existUser(j + "@gmail.com");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        rts.add(System.nanoTime() - begin);
                    }
                    countDownLatch.countDown();
                };
                for (int k = 0; k < threads; k++) {
                    executorService.submit(r);
                }
                long benchmarkStart = System.nanoTime();
                countDownLatch.await();
                long nanos = System.nanoTime() - benchmarkStart;
                results.add(new BenchmarkResult(i,nanos, rts));
            }
            results.add(avgBenchmarkResult(results));
            beanToCsv.write(results);
            writer.close();
            log.info("existUser end");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (CsvDataTypeMismatchException e) {
            e.printStackTrace();
        } catch (CsvRequiredFieldEmptyException e) {
            e.printStackTrace();
        }
    }

    private void getUser() {
        try {
            Path benchmark = Paths.get(System.getProperty("user.home"), "benchmark", "getUser.csv");
            final Path parent = benchmark.getParent();
            if (parent != null) // null will be returned if the path has no parent
                Files.createDirectories(parent);
            if (!Files.exists(benchmark)) {
                Files.createFile(benchmark);
            }
            BufferedWriter writer = Files.newBufferedWriter(benchmark, StandardOpenOption.WRITE);

            StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withEscapechar('\\').build();

            log.info("----------------------------------------------------------------------------");
            log.info("getUser started");
            List<BenchmarkResult> results = new ArrayList<>();
            for (int i = 0; i < measurementIterations; i++) {
                CountDownLatch countDownLatch = new CountDownLatch(threads);
                List<Long> rts = new Vector<>(requestsTotal);
                Runnable r = () -> {
                    for (int j = 0; j < requestsPerThread; j++) {
                        long begin = System.nanoTime();
                        try {
                            userService.getUser(j);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        rts.add(System.nanoTime() - begin);
                    }
                    countDownLatch.countDown();
                };

                for (int k = 0; k < threads; k++) {
                    executorService.submit(r);
                }
                long benchmarkStart = System.nanoTime();
                countDownLatch.await();
                long nanos = System.nanoTime() - benchmarkStart;
                results.add(new BenchmarkResult(i,nanos, rts));
            }
            results.add(avgBenchmarkResult(results));
            beanToCsv.write(results);
            writer.close();
            log.info("getUser end");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (CsvDataTypeMismatchException e) {
            e.printStackTrace();
        } catch (CsvRequiredFieldEmptyException e) {
            e.printStackTrace();
        }
    }

    private void listUser() {
        try {
            Path benchmark = Paths.get(System.getProperty("user.home"), "benchmark", "listUser.csv");
            final Path parent = benchmark.getParent();
            if (parent != null) // null will be returned if the path has no parent
                Files.createDirectories(parent);
            if (!Files.exists(benchmark)) {
                Files.createFile(benchmark);
            }
            BufferedWriter writer = Files.newBufferedWriter(benchmark, StandardOpenOption.WRITE);

            StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withEscapechar('\\').build();

            log.info("----------------------------------------------------------------------------");
            log.info("listUser started");
            List<BenchmarkResult> results = new ArrayList<>();
            for (int i = 0; i < measurementIterations; i++) {
                CountDownLatch countDownLatch = new CountDownLatch(threads);
                List<Long> rts = new Vector<>(requestsTotal);
                Runnable r = () -> {
                    for (int j = 0; j < requestsPerThread; j++) {
                        long begin = System.nanoTime();
                        try {
                            userService.listUser(j);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        rts.add(System.nanoTime() - begin);
                    }
                    countDownLatch.countDown();
                };

                for (int k = 0; k < threads; k++) {
                    executorService.submit(r);
                }
                long benchmarkStart = System.nanoTime();
                countDownLatch.await();
                long nanos = System.nanoTime() - benchmarkStart;
                results.add(new BenchmarkResult(i,nanos, rts));
            }
            results.add(avgBenchmarkResult(results));
            beanToCsv.write(results);
            writer.close();
            log.info("listUser end");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (CsvDataTypeMismatchException e) {
            e.printStackTrace();
        } catch (CsvRequiredFieldEmptyException e) {
            e.printStackTrace();
        }
    }
}
