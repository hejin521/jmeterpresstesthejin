package com.gotokeep.jmeter;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.rpc.RpcContext;
import com.keep.medivh.api.SmartWorkoutRecommend;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

public class RPCGetUserReplaceExercisePressTest<JmeterResponseDTO> extends AbstractJavaSamplerClient {
    SmartWorkoutRecommend smartWorkoutRecommend;

    private static long start = 0;
    private static long end = 0;

    //设置GUI页面显示的变量名称
    private static final String USERID = "userId";
    private String resultData;
    private JmeterResponseDTO jmeterResponseDTO;

    /**
     * 执行runTest()方法前会调用此方法,可放一些初始化代码
     */
    public void setupTest(JavaSamplerContext arg0) {
        // 开始时间
        start = System.currentTimeMillis();
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"classpath:applicationContext.xml"});
        context.start();
        smartWorkoutRecommend = (SmartWorkoutRecommend) context.getBean("smartWorkoutRecommend");
    }


    /**
     * 执行runTest()方法后会调用此方法.
     */
    public void teardownTest(JavaSamplerContext arg0) {

        // 结束时间
        end = System.currentTimeMillis();
        // 总体耗时
        System.err.println("cost time:" + (end - start) / 1000);
    }

    @Override
    public Arguments getDefaultParameters() {
        Arguments args = new Arguments();
        args.addArgument(USERID, "${userId}");
        return args;
    }

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {

        SampleResult sampleResult = new SampleResult();
        try {

            Map<String, String> argumentsAsMap = getDefaultParameters().getArgumentsAsMap();
            //添加泳道标签
            RpcContext.getContext().setAttachment(Constants.TAG_KEY, "smartsuitupgrade");
            sampleResult.sampleStart();
            //打印日志
            System.out.println(argumentsAsMap.get(USERID));
            smartWorkoutRecommend.getUserReplaceExercise(argumentsAsMap.get(USERID), "62943cb469ef0e000132be0e");
            sampleResult.sampleEnd();
            //删除泳道标签
            RpcContext.getContext().removeAttachment(Constants.TAG_KEY);

        } catch (Exception e) {
            e.printStackTrace();
            sampleResult.setSuccessful(false);
        } finally {
            // End
            //sr.sampleEnd();
        }
        sampleResult.setSuccessful(true);

        return sampleResult;

    }
}