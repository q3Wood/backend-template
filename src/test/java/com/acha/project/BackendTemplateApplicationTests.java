package com.acha.project;

import com.acha.project.mapper.UserMapper;
import com.acha.project.model.entity.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.List;

@SpringBootTest
class BackendTemplateApplicationTests {

	// 1. 注入我们刚才写的 Mapper
	@Resource
	private UserMapper userMapper;

	@Test
	void testAddUser() {
		System.out.println("----- 开始测试插入数据 -----");

		// 2. 创建一个虚拟用户
		User user = new User();
		user.setUserAccount("test_admin");
		user.setUserPassword("12345678");
		user.setUserName("测试管理员");
		user.setUserAvatar("https://github.com/q3Wood.png");
		user.setUserRole("admin");

		// 3. 调用 MyBatis Plus 的 insert 方法
		int result = userMapper.insert(user);

		System.out.println("影响行数: " + result);
		System.out.println("生成的ID: " + user.getId()); // MyBatis 会自动把生成的 ID 回填到对象里

		// 4. 断言检查（确保真的插进去了）
		Assert.isTrue(result > 0, "插入失败！");
	}

	@Test
	void testSelect() {
		System.out.println("----- 开始测试查询数据 -----");
		List<User> userList = userMapper.selectList(null); // null 表示查询所有
		// 打印所有用户
		userList.forEach(System.out::println);
	}

}