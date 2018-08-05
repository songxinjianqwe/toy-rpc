package server;

import com.sinjinsong.toy.autoconfig.annotation.RPCService;
import service.BenchmarkService;
import service.TestObject;

/**
 * @author sinjinsong
 * @date 2018/8/6
 */
@RPCService
public class BenchmarkServiceImpl implements BenchmarkService {

    @Override
    public TestObject updateField(TestObject testObject) {
        testObject.setI(10);
        return testObject;
    }
}
