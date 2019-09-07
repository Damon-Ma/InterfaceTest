# InterfaceTest

使用说明：
    
    1.创建名为autotest的数据库
    2.导入根目录下autotest.sql文件
    3.在config.properties文件中配置数据库地址、用户名密码
    
数据库说明：
    
    1.该项目使用的是mysql数据库
    
    2.interface表，用于保存接口的基本信息：
        name：接口名称
        method：请求方法，目前只支持get和post
        header：请求头
        url：接口地址
        param：参数格式（暂未使用）
        
    3.test_datra表，用于保存接口参数，fid指向interface表的id，其他参数：

        param：接口参数
        depend：依赖，如果所依赖的接口执行失败，那么这个接口就会跳过执行
        expected：断言内容，如果为json格式的可以写成json.data.xxx=xxx格式，正则表达式以#开头
        assert_type：断言方法，目前支持json断言（json），响应体断言（完全匹配）(body)，正则表达式断言(#正则表达式)
        save_data_key：需要保存的数据键值，比如userid，在其他接口使用中可以直接使用userid={userid}的方式引入
        result：接口请求结果，接口执行完成后自动插入，用于接口间依赖使用
        description：参数描述，会在测试报告中体现
        env：运行环境，暂未使用
        
    4.test_case表,用于接口分组，或者归纳用例，让报告看起来更加清晰
    
        business：业务编号，填写test_data表的id，将会按照顺序执行
        description：描述，在报告中体现
        isrun：控制用例是否运行
        
    5.user_data,用于保存用户默认参数，如果接口有依赖参数，但是所依赖参数的接口执行失败，为了不影响使用该参数的接口运行，可以增加默认参数。
        
        user_key：key值
        user_value：value值