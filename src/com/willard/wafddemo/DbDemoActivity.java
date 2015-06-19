/*
 * Copyright 2015 jhonween
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.willard.wafddemo;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.willard.waf.db.sqlite.Selector;
import com.willard.waf.db.table.DbModel;
import com.willard.waf.db.utils.DbUtils;
import com.willard.waf.exception.DbException;
import com.willard.wafddemo.entities.Child;
import com.willard.wafddemo.entities.Parent;

public class DbDemoActivity extends Activity {

	private Button test;
	private TextView mResultView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.db_fragment);
		test = (Button) findViewById(R.id.db_test_btn);
		mResultView = (TextView) findViewById(R.id.db_test_result);

		test.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dbTest();
			}
		});
	}

	private void dbTest() {
		StringBuilder strTemp = new StringBuilder();

		Parent parent = new Parent();
		parent.name = "测试" + System.currentTimeMillis();
		parent.setAdmin(true);
		parent.setEmail("wodekjwozz@163.com");

		try {

			// 可传入不同的参数新建DbUtils
			// DbUtils.create(context, dbName);
			// DbUtils.create(context, dbDir, dbName);
			DbUtils db = DbUtils.create(this);
			db.configAllowTransaction(true);
			db.configDebug(true);

			Child child = new Child();
			child.name = "child' name";
			// db.saveBindingId(parent);
			// child.parent = new ForeignLazyLoader<Parent>(Child.class,
			// "parentId", parent.getId());
			// child.parent = parent;

			Parent test = db.findFirst(Selector.from(Parent.class).where("id",
					"in", new int[] { 1, 3, 6 }));
			// Parent test =
			// db.findFirst(Selector.from(Parent.class).where("id",
			// "between", new String[] { "1", "5" }));
			if (test != null) {
				child.parent = test;
				strTemp.append("first parent:" + test + "\n");
				mResultView.setText(strTemp.toString());
			} else {
				child.parent = parent;
			}

			parent.setTime(new Date());
			parent.setDate(new java.sql.Date(new Date().getTime()));

			db.saveBindingId(child);// 保存实体,将其存入数据库,并获取数据库中该实体的id,设置其id值

			List<Child> children = db.findAll(Selector.from(Child.class));// .where(WhereBuilder.b("name",
																			// "=",
																			// "child' name")));
			strTemp.append("children size:" + children.size() + "\n");
			mResultView.setText(strTemp.toString());
			if (children.size() > 0) {
				strTemp.append("last children:"
						+ children.get(children.size() - 1) + "\n");
				mResultView.setText(strTemp.toString());
			}

			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, -1);
			calendar.add(Calendar.HOUR, 3);

			List<Parent> list = db.findAll(Selector.from(Parent.class)
					.where("id", "<", 54).and("time", ">", calendar.getTime())
					.orderBy("id").limit(10));
			strTemp.append("find parent size:" + list.size() + "\n");
			mResultView.setText(strTemp.toString());
			if (list.size() > 0) {
				strTemp.append("last parent:" + list.get(list.size() - 1)
						+ "\n");
				mResultView.setText(strTemp.toString());
				// list.get(0).children.getAllFromDb();
			}

			// parent.name = "hahaha123";
			// db.update(parent);

			Parent entity = db.findById(Parent.class, child.parent.getId());
			strTemp.append("find by id:" + entity.toString() + "\n");
			mResultView.setText(strTemp.toString());

			List<DbModel> dbModels = db.findDbModelAll(Selector
					.from(Parent.class).groupBy("name")
					.select("name", "count(name) as count"));
			strTemp.append("group by result:" + dbModels.get(0).getDataMap()
					+ "\n");
			mResultView.setText(strTemp.toString());

		} catch (DbException e) {
			strTemp.append("error :" + e.getMessage() + "\n");
			mResultView.setText(strTemp.toString());
		}
	}
}
