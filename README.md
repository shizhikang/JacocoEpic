# JacocoEpic
Android UT 框架。基于Jacoco和Epic，实现android真机上的单元测试功能。

  ThreadUtils
  切换线程的工具类
  
  ServiceManagerUtils
  用于hook framwork相关service
  
  ActivityTest
  用于测试activity，继承之后，能够构造出activity对象（不同于ActivityTestRule，此方法是new出来的activity，不会出现45s问题）
  
  ServiceTest
  用于测试Service，继承之后，能够构造出service对象（优于ServiceTestCase，能够测试含有dialog和notification的service）
  
  PowerMockito
  在真机中mock任意函数(自身app和framework)
   
    whenThenReturn ：调用方法时强制返回值，方法真正的逻辑不会执行
      参考test_when /test_whenStatic/test_whenPrivate
    
    whenThrow： 调用方法时强制抛异常，方法真正的逻辑不会执行
      参考test_whenThrow
    
    doAnswer： 调用方法时回调方法参数，方法会正常执行，可以获取方法的参数
      参考test_doAnswer
    
    mock： 指定监听方法的调用，以获取其中的参数，方法会正常执行
      参考test_verify

    unhook: 解除所有mock相关操作，防止影响后续正常流程
  MockObject
    verify： 查看对应方法的调用次数
  
