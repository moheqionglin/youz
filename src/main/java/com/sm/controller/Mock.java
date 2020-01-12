package com.sm.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author wanli.zhou
 * @description
 * @time 2019-12-23 23:30
 */
@RestController
public class Mock {


    @GetMapping(path = "/test")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Person getPerson(){
        return new Person("墨菏青林admin", new Date(), 30, new BigDecimal(0.988));
    }

    @GetMapping(path = "/test2")
    @PreAuthorize("hasAuthority('JIANHUO')")
    public Person getPerson2(){
        return new Person("墨菏青林JIANHUO", new Date(), 30, new BigDecimal(0.988));
    }

    @GetMapping(path = "/test3")
    @PreAuthorize("hasAuthority('BUYER')")
    public Person getPerson3(){
        return new Person("墨菏青林BUYER", new Date(), 30, new BigDecimal(0.988));
    }

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

}