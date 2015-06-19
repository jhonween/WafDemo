/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 * Modified by willard
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.willard.wafddemo.entities;

import java.util.Date;

import com.willard.waf.db.annotation.Column;
import com.willard.waf.db.annotation.Finder;
import com.willard.waf.db.annotation.Table;
import com.willard.waf.db.sqlite.FinderLazyLoader;

/**
 * Author: wyouflf
 * Modified by willard
 * Date: 13-7-25
 * Time: 下午7:06
 */
// 建议加上注解， 混淆后表名不受影响
@Table(name = "parent", execAfterTableCreated = "CREATE UNIQUE INDEX index_name ON parent(name,email)")
public class Parent extends EntityBase {

    @Column(column = "name") // 建议加上注解， 混淆后列名不受影响
    public String name;

    @Column(column = "email")
    private String email;

    @Column(column = "isAdmin")
    private boolean isAdmin;

    @Column(column = "time")
    private Date time;

    @Column(column = "date")
    private java.sql.Date date;

    @Finder(valueColumn = "id", targetColumn = "parentId")
    public FinderLazyLoader<Child> children; // 关联对象多时建议使用这种方式，延迟加载效率较高。

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public java.sql.Date getDate() {
        return date;
    }

    public void setDate(java.sql.Date date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Parent{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", isAdmin=" + isAdmin +
                ", time=" + time +
                ", date=" + date +
                '}';
    }
}
