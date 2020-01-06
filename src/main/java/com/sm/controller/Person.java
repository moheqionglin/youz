package com.sm.controller;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-06 18:53
 */
public class Person {
    private String name;
    private Date birthday;
    private int age;
    private BigDecimal money = new BigDecimal(0.998);

    public Person(String name, Date birthday, int age, BigDecimal money) {
        this.name = name;
        this.birthday = birthday;
        this.age = age;
        this.money = money;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }
}